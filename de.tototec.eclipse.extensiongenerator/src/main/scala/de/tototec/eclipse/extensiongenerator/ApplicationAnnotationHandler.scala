package de.tototec.eclipse.extensiongenerator

import scala.Array.canBuildFrom
import de.tototec.eclipse.extensiongenerator.annotation.Application
import de.tototec.eclipse.extensiongenerator.annotation.Cardinality
import de.tototec.eclipse.extensiongenerator.annotation.Thread
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.AnnotationMemberValue
import javassist.bytecode.annotation.ArrayMemberValue
import javassist.bytecode.annotation.BooleanMemberValue
import javassist.bytecode.annotation.EnumMemberValue
import javassist.bytecode.annotation.MemberValue
import javassist.bytecode.annotation.StringMemberValue
import javassist.bytecode.ClassFile

class ApplicationAnnotationHandler extends AnnotationHandler {

  override def annotationName = classOf[Application].getName

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

    def attribNotEmpty(attrib: String, value: String) = if (value != null && value != "") {
      s"""${attrib}="${value}""""
    } else ""

    s"""|  <extension 
        |      point="org.eclipse.core.runtime.applications"
        |      id="${id}"
        |      ${attribNotEmpty("name", name)}>
        |    <application
        |        visible="${if (visible) "true" else "false"}"
        |        ${attribNotEmpty("icon", icon)}
        |        cardinality="${cardinality}"
        |        thread="${thread}">
        |      <run class="${className}">
        |""".stripMargin + params.map {
      case (name, value) =>
        s"""        <parameter name="${name}" value="${value}"/>"""
    }.mkString("\n") +
      s"""|      </run>
          |    </application>
          |  </extension>""".stripMargin
  }

}