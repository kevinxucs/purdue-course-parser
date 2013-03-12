/*
 * ScheduleDetailTest.java
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

package net.kevxu.purdueassist.test;

import java.io.IOException;

import net.kevxu.purdueassist.course.ScheduleDetail;
import net.kevxu.purdueassist.course.ScheduleDetail.ScheduleDetailEntry;
import net.kevxu.purdueassist.course.elements.Predefined.Term;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HtmlParseException;
import net.kevxu.purdueassist.course.shared.RequestNotFinishedException;
import net.kevxu.purdueassist.course.shared.ResultNotMatchException;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ScheduleDetailTest {

	public static final String VERSION = "0.2.0-SNAPSHOT";

	private static final HelpFormatter formatter = new HelpFormatter();
	private static final Options options = new Options();

	private static boolean silent;
	private static boolean smallSilent;

	public static void main(String[] args) throws InterruptedException {
		options.addOption("t", "term", true, "full name (without space) for school term. i.e. fall2012 (optional)");
		options.addOption("s", "small-silent", false, "Do not print input information.");
		options.addOption("S", "slient", false, "Do not print anything.");

		CommandLineParser parser = new GnuParser();
		org.apache.commons.cli.CommandLine cmd;

		Term term = null;
		String[] crns = {};

		try {
			if (args.length <= 0) {
				printHelp(formatter, options);

				System.exit(10);
			} else {
				cmd = parser.parse(options, args);

				String termString;
				if (!cmd.hasOption("t")) {
					termString = "CURRENT";
				} else {
					termString = cmd.getOptionValue("t");
				}
				term = parseTerm(termString);
				silent = cmd.hasOption("S");
				smallSilent = cmd.hasOption("s");
				crns = cmd.getArgs();
			}
		} catch (ParseException e) {
			System.err.println("Command line arguments parsing failed. Reason: "
					+ e.getMessage());
			printHelp(formatter, options);
		} catch (IllegalArgumentException e) {
			System.err.println("No such school term.");
			printHelp(formatter, options);
		}

		// Not parallel
		ScheduleDetail detail = new ScheduleDetail();
		for (final String crnString : crns) {
			int crn = Integer.valueOf(crnString);

			try {
				ScheduleDetailEntry entry;
				entry = detail.getResult(term, crn);
				if (!silent) {
					if (!smallSilent)
						System.err.println("INPUT: " + crn + " " + term);
					System.out.println(entry);
				}
			} catch (RequestNotFinishedException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				if (!silent) {
					if (!smallSilent)
						System.err.println("INPUT: " + crn + " " + term);
					System.err.println("IO Error: " + e.getMessage() + "\n");
				}
			} catch (HtmlParseException e) {
				if (!silent) {
					if (!smallSilent)
						System.err.println("INPUT: " + crn + " " + term);
					System.err.println("Parse Error: " + e.getMessage() + "\n");
				}
			} catch (CourseNotFoundException e) {
				if (!silent) {
					if (!smallSilent)
						System.err.println("INPUT: " + crn + " " + term);
					System.out.println("CRN: " + crn + " " + "Term: " + term
							+ " Not Found: " + e.getMessage() + "\n");
				}
			} catch (ResultNotMatchException e) {
				if (!silent) {
					if (!smallSilent)
						System.err.println("INPUT: " + crn + " " + term);
					e.printStackTrace();
					System.err.println();
				}
			}
		}
	}

	private static void printHelp(HelpFormatter formatter, Options options) {
		formatter.printHelp("java -jar RemainSeats.jar [options] [crn1 [crn2 [crn3] ...]]", options);
	}

	private static Term parseTerm(String termString) {
		return Term.valueOf(termString.toUpperCase());
	}
}