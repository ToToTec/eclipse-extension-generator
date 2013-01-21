package de.tototec.eclipse.extensiongenerator

import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

import scala.Array.canBuildFrom
import scala.util.matching.Regex

import de.tototec.eclipse.extensiongenerator.annotation.Application
import de.tototec.eclipse.extensiongenerator.annotation.Cardinality
import de.tototec.eclipse.extensiongenerator.annotation.Perspective
import de.tototec.eclipse.extensiongenerator.annotation.Thread
import de.tototec.eclipse.extensiongenerator.annotation.View
import javassist.bytecode.AnnotationsAttribute
import javassist.bytecode.ClassFile
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.AnnotationMemberValue
import javassist.bytecode.annotation.ArrayMemberValue
import javassist.bytecode.annotation.BooleanMemberValue
import javassist.bytecode.annotation.DoubleMemberValue
import javassist.bytecode.annotation.EnumMemberValue
import javassist.bytecode.annotation.MemberValue
import javassist.bytecode.annotation.StringMemberValue

class PluginXmlBuilder(
  scanDirs: Seq[File] = Seq(),
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

    def recursiveListFiles(dir: File, regex: Regex): Array[File] = {
      dir.listFiles match {
        case allFiles: Array[File] =>
          allFiles.filter(f => f.isFile && regex.findFirstIn(f.getName).isDefined) ++
            allFiles.filter(_.isDirectory).flatMap(recursiveListFiles(_, regex))
        case null => Array()
      }
    }

    val classFiles = scanDirs.flatMap { path => recursiveListFiles(path, """.*\.class""".r) }

    var apps: Map[String, Annotation] = Map()
    var perspectives: Map[String, Annotation] = Map()
    var views: Map[String, Annotation] = Map()

    classFiles.foreach { file =>
      val is = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))
      try {
        val classFile = new ClassFile(is)

        val invisibleAnnos =
          Option(classFile.getAttribute(AnnotationsAttribute.invisibleTag).asInstanceOf[AnnotationsAttribute]).map {
            _.getAnnotations()
          }.getOrElse(Array())

        // This is needed because of the bug in the scala compiler: SI-4788
        val visibleAnnos =
          Option(classFile.getAttribute(AnnotationsAttribute.visibleTag).asInstanceOf[AnnotationsAttribute]).map {
            _.getAnnotations()
          }.getOrElse(Array())

        val annos = invisibleAnnos ++ visibleAnnos

        // Applications
        annos.find { anno =>
          anno.getTypeName == classOf[Application].getName || 
            anno.getTypeName == "de.tototec.eclipse.extensiongenerator.annotation.scala.Application"
        }.map { appAnno =>
          apps += (classFile.getName -> appAnno)
        }

        // Perspectives
        annos.find { anno =>
          anno.getTypeName == classOf[Perspective].getName
        }.map { pAnno =>
          perspectives += (classFile.getName -> pAnno)
        }

        // Views
        annos.find { anno =>
          anno.getTypeName == classOf[View].getName
        }.map { vAnno =>
          views += (classFile.getName -> vAnno)
        }

      } finally {
        is.close
      }

    }

    val appXml = apps.map {
      case (className, anno) =>
        val id = anno.getMemberValue("id") match {
          case value: StringMemberValue => value.getValue
          case _ => className
        }
        val name = anno.getMemberValue("name") match {
          case value: StringMemberValue => value.getValue
          case _ => ""
        }
        val visible = anno.getMemberValue("visible") match {
          case value: BooleanMemberValue => value.getValue
          case _ => true
        }
        val thread = anno.getMemberValue("thread") match {
          case value: EnumMemberValue => Thread.valueOf(value.getValue).title
          case _ => Thread.MAIN.title
        }
        val cardinality = anno.getMemberValue("cardinality") match {
          case x: EnumMemberValue => Cardinality.valueOf(x.getValue).title
          case _ => Cardinality.SINGLETON_GLOBAL.title
        }
        val icon = anno.getMemberValue("icon") match {
          case x: StringMemberValue => x.getValue
          case _ => ""
        }
        val params = (anno.getMemberValue("parameters") match {
          case x: ArrayMemberValue => x.getValue
          case _ => Array[MemberValue]()
        }).map {
          case x: AnnotationMemberValue =>
            val paramAnno = x.getValue
            val paramName = paramAnno.getMemberValue("name").asInstanceOf[StringMemberValue].getValue
            val paramValue = paramAnno.getMemberValue("value").asInstanceOf[StringMemberValue].getValue
            (paramName -> paramValue)
        }

        s"""|  <extension
            |      point="org.eclipse.core.runtime.applications"
            |      id="${id}"
            |      name="${name}">
            |    <application
            |        visible="${if (visible) "true" else "false"}"
            |        cardinality="${cardinality}"
            |        thread="${thread}"
            |        icon="${icon}">
            |      <run class="${className}">
            |""".stripMargin + params.map {
          case (name, value) =>
            s"""        <parameter name="${name}" value="${value}"/>"""
        }.mkString("\n") +
          s"""|      </run>
              |    </application>
              |  </extension>""".stripMargin
    }.mkString("\n")

    val perspectiveXml = perspectives.map {
      case (className, anno) =>
        val id = anno.getMemberValue("id") match {
          case value: StringMemberValue => value.getValue
          case _ => className
        }
        val name = anno.getMemberValue("name") match {
          case value: StringMemberValue => value.getValue
          case _ => ""
        }
        val icon = anno.getMemberValue("icon") match {
          case x: StringMemberValue => x.getValue
          case _ => ""
        }
        val fixed = anno.getMemberValue("fixed") match {
          case value: BooleanMemberValue => value.getValue
          case _ => false
        }

        s"""|  <extension
            |      point="org.eclipse.ui.perspectives">
            |    <perspective
            |        id="${id}"
            |        name="${name}"
            |        class="${className}"
            |        icon="${icon}"
            |        fixed="${fixed}">
            |    </perspective>
            |  </extension>""".stripMargin
    }.mkString("\n")

    val viewXml = views.map {
      case (className, anno) =>
        val id = anno.getMemberValue("id") match {
          case value: StringMemberValue => value.getValue
          case _ => className
        }
        val name = anno.getMemberValue("name") match {
          case value: StringMemberValue => value.getValue
          case _ => ""
        }
        val category = anno.getMemberValue("category") match {
          case value: StringMemberValue => value.getValue
          case _ => ""
        }
        val icon = anno.getMemberValue("icon") match {
          case x: StringMemberValue => x.getValue
          case _ => ""
        }
        val fastViewWidthRatio = anno.getMemberValue("fastViewWidthRatio") match {
          case value: DoubleMemberValue => value.getValue
          case _ => -1
        }
        val allowMultiple = anno.getMemberValue("allowMultiple") match {
          case x: BooleanMemberValue => x.getValue
          case _ => false
        }
        val restorable = anno.getMemberValue("restorable") match {
          case x: BooleanMemberValue => x.getValue
          case _ => true
        }
        val description = anno.getMemberValue("description") match {
          case x: StringMemberValue => x.getValue
          case _ => ""
        }

        s"""|  <extension
            |      point="org.eclipse.ui.views">
            |    <view
            |        id="${id}"
            |        name="${name}"
            |        class="${className}"
            |        icon="${icon}"
            |        ${
          if (fastViewWidthRatio >= 0.05 && fastViewWidthRatio <= 0.95)
            s"""fastViewWidthRatio="${fastViewWidthRatio}"""
          else ""
        }
            |        allowMultiple="${if (allowMultiple) "true" else "false"}"
            |        restorable="${if (restorable) "true" else "false"}">
            |        ${
          if (description != "")
            s"      <description><![CDATA[${fastViewWidthRatio}]]></description>"
          else ""
        }
            |    </view>
            |  </extension>""".stripMargin
    }.mkString("\n")

    """|<?xml version="1.0" encoding="UTF-8"?>
       |<?eclipse version="3.4"?>
       |<plugin>""".stripMargin +
      Seq(appXml, perspectiveXml, viewXml).filter(_.trim != "").mkString("\n", "\n", "\n") +
      "</plugin>"
  }

}

