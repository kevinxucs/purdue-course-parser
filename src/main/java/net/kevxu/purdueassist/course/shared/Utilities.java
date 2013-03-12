/*
 * Utilities.java
 * 
 * The MIT License
 *
 * Copyright (c) 2013 Kaiwen Xu and Rendong Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal 
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is furnished 
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */

package net.kevxu.purdueassist.course.shared;

public class Utilities {

	/**
	 * Verify CRN number.
	 * 
	 * @param crn
	 *            crn number.
	 * @return true if crn number is valid, false if invalid.
	 */
	public static boolean verifyCrn(int crn) {
		//TODO: To be implemented
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
	 * @return shrunk String.
	 */
	public static String shrinkContentInParentheses(String rawString) {
		return rawString.replaceAll("\\(\\s+", "(").replaceAll("\\s+\\)", ")");
	}

	/**
	 * Count number of digits contained in an Integer.
	 * 
	 * @param number
	 *            the number.
	 * @return number of digits.
	 */
	public static int intNumberOfDigits(int number) {
		return (number == 0) ? 1 : (int) Math.log10(number) + 1;
	}

}
