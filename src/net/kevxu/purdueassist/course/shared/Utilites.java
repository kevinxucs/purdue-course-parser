package net.kevxu.purdueassist.course.shared;

public class Utilites {

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

	private static int intNumberOfDigits(int number) {
		return (number == 0) ? 1 : (int) Math.log10(number) + 1;
	}

}
