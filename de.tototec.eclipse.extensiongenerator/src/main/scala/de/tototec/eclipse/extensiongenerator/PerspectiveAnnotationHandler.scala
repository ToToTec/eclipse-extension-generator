package de.tototec.eclipse.extensiongenerator

import de.tototec.eclipse.extensiongenerator.annotation.Perspective
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.BooleanMemberValue
import javassist.bytecode.annotation.StringMemberValue

class PerspectiveAnnotationHandler extends AnnotationHandler {

  override def annotationName = classOf[Perspective].getName

  override def generateXmlFragement(className: String, anno: Annotation): String = {
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

  }

}
