package net.kevxu.purdueassist.course.shared;

public class RequestNotFinishedException extends Exception {

	private static final long serialVersionUID = -2042384911352208656L;

	public RequestNotFinishedException() {

	}

	public RequestNotFinishedException(String message) {
		super(message);

	}

	public RequestNotFinishedException(Throwable cause) {
		super(cause);

	}

	public RequestNotFinishedException(String message, Throwable cause) {
		super(message, cause);

	}

}
