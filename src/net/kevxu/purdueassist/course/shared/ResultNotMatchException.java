package net.kevxu.purdueassist.course.shared;

public class ResultNotMatchException extends Exception {

	private static final long serialVersionUID = -662553829655560093L;

	public ResultNotMatchException() {
	}

	public ResultNotMatchException(String message) {
		super(message);
	}

	public ResultNotMatchException(Throwable cause) {
		super(cause);
	}

	public ResultNotMatchException(String message, Throwable cause) {
		super(message, cause);
	}

}
