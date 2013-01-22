package de.tototec.eclipse.extensiongenerator.annotation;

/**
 * Definition of an extension to extension point org.eclipse.ui.editors.
 * 
 * The annotated class must either implement {@link org.eclipse.ui.IEditorPart}
 * or {@link org.eclipse.ui.IEditorLauncher}.
 */
public @interface Editor {

	/**
	 * A unique name that will be used to identify this editor.
	 */
	String id() default "";

	/**
	 * A translatable name that will be used in the UI for this editor.
	 */
	String name() default "";

	/**
	 * A relative name of the icon that will be used for all resources that
	 * match the specified extensions. Editors should provide an icon to make it
	 * easy for users to distinguish between different editor types. If you
	 * specify a command rather than a class, an icon is not needed. In that
	 * case, the workbench will use the icon provided by the operating system.
	 */
	String icon() default "";

	/**
	 * An optional field containing the list of file types understood by the
	 * editor. This is a array of strings containing file extensions. For
	 * instance, an editor which understands hypertext documents may register
	 * for { "htm" , "html" }.
	 */
	String[] extensions() default {};

	/**
	 * The name of a class that implements
	 * {@link org.eclipse.ui.IEditorActionBarContributor}. This attribute should
	 * only be defined if the annotation class is an instance of
	 * {@link org.eclipse.ui.IEditorPart}. This class is used to add new actions
	 * to the workbench menu and tool bar which reflect the features of the
	 * editor type.
	 */
	String contributorClass() default "";

	/**
	 * if <code>true</code>, this editor will be used as the default editor for
	 * the type. This is only relevant in a case where more than one editor is
	 * registered for the same type. If an editor is not the default for the
	 * type, it can still be launched using "Open with..." submenu for the
	 * selected resource.
	 * 
	 * Please note that this attribute is only honored for filename and
	 * extension associations at this time. It will not be honored for content
	 * type bindings. Content type-based resolution will occur on a first come,
	 * first serve basis and is not explicitly specified.
	 * 
	 */
	boolean isDefault() default false;

	/**
	 * An optional field containing the list of file names understood by the
	 * editor. For instance, an editor which understands specific hypertext
	 * documents may register for {"ejb.htm", "ejb.html"}.
	 */
	String[] fileNames() default {};

	/**
	 * The symbolic name of a font. The symbolic font name must be the id of a
	 * defined font (see org.eclipse.ui.fontDefinitions). If this attribute is
	 * missing or invalid then the font name is the value of
	 * "org.eclipse.jface.textfont" in the editor's preferences store. If there
	 * is no preference store or the key is not defined then the JFace text font
	 * will be used. The editor implementation decides if it uses this symbolic
	 * font name to set the font.
	 */
	String symbolicFontName() default "";

	/**
	 * The name of a class that implements
	 * {@link org.eclipse.ui.IEditorMatchingStrategy}. This attribute should
	 * only be defined if the annotated class implement
	 * {@link org.eclipse.ui.IEditorPart}. This allows the editor extension to
	 * provide its own algorithm for matching the input of one of its editors to
	 * a given editor input.
	 */
	String matchingStrategy() default "";

	/**
	 * Advertises that the containing editor understands the given content type
	 * and is suitable for editing files of that type. The content type
	 * identifier. This is an ID defined by the
	 * 'org.eclipse.core.contenttype.contentTypes' extension point.
	 */
	String[] contentTypeBindings() default {};
}
