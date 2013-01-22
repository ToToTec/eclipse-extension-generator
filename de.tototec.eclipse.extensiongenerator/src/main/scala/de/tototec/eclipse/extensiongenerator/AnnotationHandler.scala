package de.tototec.eclipse.extensiongenerator

import javassist.bytecode.annotation.Annotation
import javassist.bytecode.ClassFile

trait AnnotationHandler {

  def annotationName: String

  def generateXmlFragement(classFile: ClassFile, annotation: Annotation): String

}

