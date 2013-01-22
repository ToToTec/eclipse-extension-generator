package de.tototec.eclipse.extensiongenerator

import de.tototec.eclipse.extensiongenerator.annotation.View
import javassist.bytecode.ClassFile
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.BooleanMemberValue
import javassist.bytecode.annotation.DoubleMemberValue
import javassist.bytecode.annotation.StringMemberValue
import javassist.bytecode.annotation.ArrayMemberValue
import javassist.bytecode.annotation.MemberValue

class EditorAnnotationHandler extends AnnotationHandler {

  override def annotationName = classOf[View].getName

  override def generateXmlFragement(classFile: ClassFile, anno: Annotation): String = {
    val className = classFile.getName

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
    val extensions = (anno.getMemberValue("extensions") match {
      case x: ArrayMemberValue => x.getValue
      case _ => Array[MemberValue]()
    }).map {
      case x: StringMemberValue => x.getValue
      case _ => ""
    }
    val contributorClass = anno.getMemberValue("contibutorClass") match {
      case x: StringMemberValue => x.getValue
      case _ => ""
    }
    val isDefault = anno.getMemberValue("isDefault") match {
      case x: BooleanMemberValue => x.getValue
      case _ => false
    }
    val fileNames = (anno.getMemberValue("fileNames") match {
      case x: ArrayMemberValue => x.getValue
      case _ => Array[MemberValue]()
    }).map {
      case x: StringMemberValue => x.getValue
      case _ => ""
    }
    val symbolicFontName = anno.getMemberValue("symbolicFontName") match {
      case x: StringMemberValue => x.getValue
      case _ => ""
    }
    val matchingStrategy = anno.getMemberValue("matchingStrategy") match {
      case x: StringMemberValue => x.getValue
      case _ => ""
    }
    val contentTypeBindings = (anno.getMemberValue("contentTypeBindings") match {
      case x: ArrayMemberValue => x.getValue
      case _ => Array[MemberValue]()
    }).map {
      case x: StringMemberValue => x.getValue
      case _ => ""
    }

    // TODO: class or launcher?
    val classAttr = className
    val launcherAttr = ""

    def attribNotEmpty(attrib: String, value: String) = if (value != null && value != "") {
      s"""${attrib}="${value}""""
    } else ""

    s"""|  <extension
            |      point="org.eclipse.ui.editors">
            |    <editor
            |        id="${id}"
            |        ${attribNotEmpty("name", name)}
            |        ${attribNotEmpty("icon", icon)}
            |        ${attribNotEmpty("extensions", extensions.mkString(","))}
            |        ${attribNotEmpty("class", classAttr)}
            |        ${attribNotEmpty("contributorClass", contributorClass)}
            |        ${attribNotEmpty("launcher", launcherAttr)}
            |        ${attribNotEmpty("filenames", fileNames.mkString(","))}
            |        ${attribNotEmpty("symbolicFontName", symbolicFontName)}
            |        ${attribNotEmpty("matchingStrategy", matchingStrategy)}
            |        default="${if (isDefault) "true" else "false"}">
            |${
      if (!contentTypeBindings.isEmpty) contentTypeBindings.map { bindingId =>
        s"""      <contentTypeBindings contentTypeId="${bindingId}"/>"""
      }.mkString("\n")
      else ""
    }
            |    </editor>
            |  </extension>""".stripMargin
  }

}
