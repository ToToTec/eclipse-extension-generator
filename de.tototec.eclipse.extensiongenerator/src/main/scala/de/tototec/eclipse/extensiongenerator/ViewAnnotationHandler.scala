package de.tototec.eclipse.extensiongenerator

import de.tototec.eclipse.extensiongenerator.annotation.View
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.BooleanMemberValue
import javassist.bytecode.annotation.DoubleMemberValue
import javassist.bytecode.annotation.StringMemberValue

class ViewAnnotationHandler extends AnnotationHandler {

  override def annotationName = classOf[View].getName

  override def generateXmlFragement(className: String, anno: Annotation): String = {
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
  }

}
