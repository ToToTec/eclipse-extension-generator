package de.tototec.eclipse.extensiongenerator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface Parameter {

	String name();

	String value();

}
