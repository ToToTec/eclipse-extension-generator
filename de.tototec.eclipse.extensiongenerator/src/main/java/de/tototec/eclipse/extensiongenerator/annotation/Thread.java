package de.tototec.eclipse.extensiongenerator.annotation;

/**
 * Specifies the thread in which the application
 * (org.eclipse.core.runtime.applications) has to run.
 */
public enum Thread {
	MAIN("main"), ANY("any");

	private final String title;

	private Thread(String title) {
		this.title = title;
	}

	public String title() {
		return title;
	}
}