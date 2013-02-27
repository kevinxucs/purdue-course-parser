package net.kevxu.purdueassist.course.shared;

public class HttpParseException extends Exception {

	private static final long serialVersionUID = -1004516908012487206L;

	public HttpParseException() {

	}

	public HttpParseException(String message) {
		super(message);
	}

	public HttpParseException(Throwable cause) {
		super(cause);
	}

	public HttpParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
