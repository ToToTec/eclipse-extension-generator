package de.tototec.eclipse.extensiongenerator

import java.io.File
import com.impetus.annovention.Discoverer
import com.impetus.annovention.Filter
import com.impetus.annovention.listener.ClassAnnotationDiscoveryListener
import de.tototec.eclipse.extensiongenerator.annotation.Application
import java.net.URLClassLoader
import de.tototec.eclipse.extensiongenerator.annotation.Perspective
import de.tototec.eclipse.extensiongenerator.annotation.Cardinality
import de.tototec.eclipse.extensiongenerator.annotation.Cardinality
import de.tototec.eclipse.extensiongenerator.annotation.View

class PluginXmlBuilder(
  scanPackages: Seq[String] = Seq(),
  classpath: Seq[File] = Seq(),
  debug: Boolean = false) {

  /** Logger, which you might want overload. */
  protected lazy val log = new {
    def debug(msg: => String, throwable: Throwable = null) =
      if (PluginXmlBuilder.this.debug)
        println(msg + (if (throwable == null) "" else "\n" + throwable.printStackTrace))
  }

  /**
   * Build and return the content of the "plugin.xml".
   */
  def build: String = {

    val discoverer = new Discoverer() {

      override def findResources: Array[java.net.URL] = classpath.map(file => file.toURI.toURL).toArray

      override def getFilter: Filter = new Filter() {
        override def accepts(fileName: String): Boolean = fileName.endsWith(".class")
      }

    }

    val classLoader = new URLClassLoader(classpath.map { file => file.toURI.toURL }.toArray, classOf[PluginXmlBuilder].getClassLoader)

    var applications: Seq[(String, Application)] = Seq()
    var perspectives: Seq[(String, Perspective)] = Seq()
    var views: Seq[(String, View)] = Seq()

    //    var annotations: Map[String, Any] = Map()

    discoverer.addAnnotationListener(new ClassAnnotationDiscoveryListener() {
      override def supportedAnnotations: Array[String] = Array(
        classOf[Application],
        classOf[Perspective],
        classOf[View]
      ).map(_.getName)
      override def discovered(className: String, annotationName: String) {
        if (scanPackages.find { pack => className.startsWith(pack + ".") }.isDefined) {
          // when package matches
          val clazz = classLoader.loadClass(className)
          annotationName match {
            case x if x == classOf[Application].getName =>
              val anno = clazz.getAnnotation(classOf[Application])
              log.debug(s"Detected annotation ${anno} in class ${className}")
              applications ++= Seq(className -> anno)
            case x if x == classOf[Perspective].getName =>
              val anno = clazz.getAnnotation(classOf[Perspective])
              log.debug(s"Detected annotation ${anno} in class ${className}")
              perspectives ++= Seq(className -> anno)
            case x if x == classOf[View].getName =>
              val anno = clazz.getAnnotation(classOf[View])
              log.debug(s"Detected annotation ${anno} in class ${className}")
              views ++= Seq(className -> anno)
          }
        }
      }
    })

    // discover visible classes
    discoverer.discover(true, false, false, true, false)

    val appXml = applications.map {
      case (className, anno) =>
        s"""|  <extension
            |      point="org.eclipse.core.runtime.applications"
            |      id="${if (anno.id == "") className else anno.id}"
            |      name="${anno.name}">
            |    <application
            |        visible="${if (anno.visible) "true" else "false"}"
            |        cardinality="${anno.cardinality.title}"
            |        thread="${anno.thread.title}"
            |        icon="${anno.icon}">
            |      <run class="${className}">
            |""".stripMargin + anno.parameters.map { param =>
          s"""        <parameter name="${param.name}" value="${param.value}"/>"""
        }.mkString("\n") +
          s"""|      </run>
              |    </application>
              |  </extension>""".stripMargin
    }.mkString("\n")

    val perspectiveXml = perspectives.map {
      case (className, anno) =>
        s"""|  <extension
            |      point="org.eclipse.ui.perspectives">
            |    <perspective
            |        id="${if (anno.id == "") className else anno.id}"
            |        name="${anno.name}"
            |        class="${className}"
            |        icon="${anno.icon}"
            |        fixed="${anno.fixed}">
            |    </perspective>
            |  </extension>""".stripMargin
    }.mkString("\n")

    val viewXml = views.map {
      case (className, anno) =>
        s"""|  <extension
            |      point="org.eclipse.ui.views">
            |    <perspective
            |        id="${if (anno.id == "") className else anno.id}"
            |        name="${anno.name}"
            |        class="${className}"
            |        icon="${anno.icon}"
            |        ${
          if (anno.fastViewWidthRatio >= 0.05 && anno.fastViewWidthRatio <= 0.95)
            s"""fastViewWidthRatio="${anno.fastViewWidthRatio}"""
          else ""
        }
            |        allowMultiple="${if (anno.allowMultiple) "true" else "false"}"
            |        restorable="${if (anno.restorable) "true" else "false"}"
            |    </perspective>
            |  </extension>""".stripMargin
    }.mkString("\n")

    """|<?xml version="1.0" encoding="UTF-8"?>
       |<?eclipse version="3.4"?>
       |<plugin>""".stripMargin +
      Seq(appXml, perspectiveXml, viewXml).filter(_.trim != "").mkString("\n", "\n", "\n") +
      "</plugin>"
  }

}

