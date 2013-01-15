package de.tototec.eclipse.extensiongenerator.annotation;

/**
 * Cardinality for org.eclipse.core.runtime.applications. This specifies how
 * many applications that may be running at the same time.
 */
public enum Cardinality {
	SINGLETON_GLOBAL("singleton-global"), //
	SINGLETON_SCOPED("singleton-scoped"), //
	ONE("1"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), //
	ANY("*");

	private final String title;

	private Cardinality(String title) {
		this.title = title;
	}

	public String title() {
		return title;
	}
}