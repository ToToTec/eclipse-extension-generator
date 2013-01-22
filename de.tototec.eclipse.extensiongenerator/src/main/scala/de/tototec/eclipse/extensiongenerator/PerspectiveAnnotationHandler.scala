package de.tototec.eclipse.extensiongenerator

import de.tototec.eclipse.extensiongenerator.annotation.Perspective
import javassist.bytecode.ClassFile
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.BooleanMemberValue
import javassist.bytecode.annotation.StringMemberValue

class PerspectiveAnnotationHandler extends AnnotationHandler {

  override def annotationName = classOf[Perspective].getName

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
    val fixed = anno.getMemberValue("fixed") match {
      case value: BooleanMemberValue => value.getValue
      case _ => false
    }

    def attribNotEmpty(attrib: String, value: String) = if (value != null && value != "") {
      s"""${attrib}="${value}""""
    } else ""

    s"""|  <extension point="org.eclipse.ui.perspectives">
        |    <perspective
        |        id="${id}"
        |        ${attribNotEmpty("name", name)}
        |        class="${className}"
        |        ${attribNotEmpty("icon", icon)}
        |        fixed="${fixed}">
        |    </perspective>
        |  </extension>""".stripMargin

  }

}
