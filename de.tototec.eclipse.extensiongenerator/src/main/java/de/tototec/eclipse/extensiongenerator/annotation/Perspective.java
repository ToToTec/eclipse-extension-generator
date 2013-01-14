package de.tototec.eclipse.extensiongenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Definition of an extension to extension point org.eclipse.ui.perspective
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Perspective {

	/** Defaults to the annotated class name. */
	String id() default "";

	String name() default "";

	String icon() default "";

	boolean fixed() default false;

}
