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

    val annoHandlers: Seq[AnnotationHandler] = Seq(
      new ApplicationAnnotationHandler(),
      new PerspectiveAnnotationHandler(),
      new ViewAnnotationHandler()
    )

    var handleAnnoRequest: Seq[(AnnotationHandler, String, Annotation)] = Seq()

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

        val handledAnnos = for (
          handler <- annoHandlers;
          anno <- annos if handler.annotationName == anno.getTypeName
        ) {
          handleAnnoRequest ++= Seq((handler, classFile.getName, anno))
        }

      } finally {
        is.close
      }

    }

    val xmlFragments = handleAnnoRequest.map {
      case (handler, className, anno) => handler.generateXmlFragement(className, anno)
    }

    val pluginXml = xmlFragments.mkString(
      """|<?xml version="1.0" encoding="UTF-8"?>
       |<?eclipse version="3.4"?>
       |<plugin>
       |""".stripMargin,
      "\n",
      "\n</plugin>"
    )

    pluginXml
  }

}

