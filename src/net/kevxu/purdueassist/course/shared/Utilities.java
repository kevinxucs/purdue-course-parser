package net.kevxu.purdueassist.course.shared;

@SuppressWarnings("unused")
public class Utilities {

	/**
	 * Verify CRN number.
	 * 
	 * @param crn
	 *            crn number.
	 * @return true if crn number is valid, false if invalid.
	 */
	public static boolean verifyCrn(int crn) {
		// To be implemented
		return true;
	}

	/**
	 * Remove all the HTML tags in String.
	 * 
	 * @param rawString
	 *            input String.
	 * @return String without HTML tags.
	 */
	public static String removeHtmlTags(String rawString) {
		return rawString.replaceAll("\\<[^>]*>", "");
	}

	/**
	 * Shrink content in parentheses. i.e. "( ABC )" will be shrunk to "(ABC)"
	 * 
	 * @param rawString
	 *            input String.
	 * @return Shrunk String.
	 */
	public static String shrinkContentInParentheses(String rawString) {
		return rawString.replaceAll("\\(\\s+", "(").replaceAll("\\s+\\)",")");
	}

	private static int intNumberOfDigits(int number) {
		return (number == 0) ? 1 : (int) Math.log10(number) + 1;
	}

}
