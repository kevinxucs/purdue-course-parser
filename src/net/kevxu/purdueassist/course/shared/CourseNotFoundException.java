package net.kevxu.purdueassist.course.shared;

public class CourseNotFoundException extends Exception {

	private static final long serialVersionUID = 5380861147324206149L;

	public CourseNotFoundException() {
	}

	public CourseNotFoundException(String message) {
		super(message);
	}

	public CourseNotFoundException(Throwable cause) {
		super(cause);
	}

	public CourseNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
