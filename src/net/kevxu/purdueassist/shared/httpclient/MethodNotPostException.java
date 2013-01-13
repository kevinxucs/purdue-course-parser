package net.kevxu.purdueassist.shared.httpclient;

public class MethodNotPostException extends Exception {

	private static final long serialVersionUID = 1L;

	public MethodNotPostException() {

	}

	public MethodNotPostException(String message) {
		super(message);
	}

	public MethodNotPostException(Throwable cause) {
		super(cause);
	}

	public MethodNotPostException(String message, Throwable cause) {
		super(message, cause);
	}

}
