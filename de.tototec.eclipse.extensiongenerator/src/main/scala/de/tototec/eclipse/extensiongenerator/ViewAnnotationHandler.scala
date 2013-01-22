package de.tototec.eclipse.extensiongenerator

import de.tototec.eclipse.extensiongenerator.annotation.View
import javassist.bytecode.ClassFile
import javassist.bytecode.annotation.Annotation
import javassist.bytecode.annotation.BooleanMemberValue
import javassist.bytecode.annotation.DoubleMemberValue
import javassist.bytecode.annotation.StringMemberValue

class ViewAnnotationHandler extends AnnotationHandler {

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

    def attribNotEmpty(attrib: String, value: String) = if (value != null && value != "") {
      s"""${attrib}="${value}""""
    } else ""

    s"""|  <extension point="org.eclipse.ui.views">
        |    <view
        |        id="${id}"
        |        ${attribNotEmpty("name", name)}
        |        class="${className}"
        |        ${attribNotEmpty("icon", icon)}
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
