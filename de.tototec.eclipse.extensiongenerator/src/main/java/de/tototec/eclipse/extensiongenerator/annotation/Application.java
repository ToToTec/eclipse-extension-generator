package de.tototec.eclipse.extensiongenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Definition of an extension to extension point
 * org.eclipse.core.runtime.applications. The annotated class must implement
 * org.eclipse.equinox.app.IApplication.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Application {

	/** Defaults to the full class name. */
	String id() default "";

	String name() default "";

	boolean visible() default true;

	Thread thread() default Thread.MAIN;

	Cardinality cardinality() default Cardinality.SINGLETON_GLOBAL;

	String icon() default "";

	/** Parameters made available to the application instance. */
	Parameter[] parameters() default {};

}
