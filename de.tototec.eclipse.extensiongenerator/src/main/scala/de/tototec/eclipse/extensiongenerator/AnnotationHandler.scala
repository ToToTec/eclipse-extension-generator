package de.tototec.eclipse.extensiongenerator

import javassist.bytecode.annotation.Annotation


trait AnnotationHandler {

  def annotationName: String

  def generateXmlFragement(className: String, annotation: Annotation): String
  
}

