package de.tototec.eclipse.extensiongenerator.annotation;

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