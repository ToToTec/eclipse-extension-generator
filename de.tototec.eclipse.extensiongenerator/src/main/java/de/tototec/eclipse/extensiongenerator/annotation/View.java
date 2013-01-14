package de.tototec.eclipse.extensiongenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Definition of an extension to extension point org.eclipse.ui.views. The
 * annotated class must implement org.eclipse.ui.IViewPart.
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface View {

	/** Unique name to identify this view. Defaults to annotated class name. */
	String id() default "";

	/** Name of this view. */
	String name() default "";

	String categoryId() default "";

	String icon() default "";

	double fastViewWidthRatio() default -1d;

	boolean allowMultiple() default false;

	boolean restorable() default true;

	String description() default "";
}
