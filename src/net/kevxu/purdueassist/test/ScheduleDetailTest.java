package net.kevxu.purdueassist.test;

import java.io.IOException;

import net.kevxu.purdueassist.course.ScheduleDetail;
import net.kevxu.purdueassist.course.ScheduleDetail.ScheduleDetailEntry;
import net.kevxu.purdueassist.course.ScheduleDetail.ScheduleDetailListener;
import net.kevxu.purdueassist.course.elements.Predefined.Term;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HttpParseException;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ScheduleDetailTest {

	public static final String VERSION = "0.1.2-SNAPSHOT";

	private static final HelpFormatter formatter = new HelpFormatter();
	private static final Options options = new Options();

	public static void main(String[] args) {
		options.addOption("t", "term", true,
				"full name (without space) for school term. i.e. fall2012 (required)");
		options.addOption("s", "small-silent", false,
				"Do not print input information.");
		options.addOption("S", "slient", false, "Do not print anything.");

		CommandLineParser parser = new GnuParser();
		org.apache.commons.cli.CommandLine cmd;
		try {
			if (args.length <= 0) {
				printHelp(formatter, options);

				System.exit(10);
			} else {
				cmd = parser.parse(options, args);

				if (!cmd.hasOption("t")) {
					System.err.println("Please specify school term.");
					printHelp(formatter, options);
					System.exit(-1);
				}

				String termString = cmd.getOptionValue("t");
				final Term term = parseTerm(termString);
				final boolean silent = cmd.hasOption("S");
				final boolean smallSilent = cmd.hasOption("s");
				final String[] crns = cmd.getArgs();

				for (final String crnString : crns) {
					final int crn = Integer.valueOf(crnString);
					ScheduleDetail detail = new ScheduleDetail(term, crn,
							new ScheduleDetailListener() {

								@Override
								public void onScheduleDetailFinished(
										CourseNotFoundException e) {
									if (!silent) {
										if (!smallSilent)
											System.out.println("INPUT: "
													+ crnString + " " + term);
										System.out.println("Course Not Found!");
									}
								}

								@Override
								public void onScheduleDetailFinished(
										HttpParseException e) {
									if (!silent) {
										if (!smallSilent)
											System.out.println("INPUT: "
													+ crnString + " " + term);
										System.out.println("Parse Error!");
									}
								}

								@Override
								public void onScheduleDetailFinished(
										IOException e) {
									if (!silent) {
										if (!smallSilent)
											System.out.println("INPUT: "
													+ crnString + " " + term);
										System.out.println("IO Error!");
									}
								}

								@Override
								public void onScheduleDetailFinished(
										ScheduleDetailEntry entry) {
									if (!silent) {
										if (!smallSilent)
											System.out.println("INPUT: "
													+ crnString + " " + term);
										System.out.println(entry);
									}
								}
							});
					detail.getResult();
				}
			}

		} catch (ParseException e) {
			System.err
					.println("Command line arguments parsing failed. Reason: "
							+ e.getMessage());
			printHelp(formatter, options);
		} catch (IllegalArgumentException e) {
			System.err.println("No such school term.");
			printHelp(formatter, options);
		}
	}

	private static void printHelp(HelpFormatter formatter, Options options) {
		formatter.printHelp(
				"java -jar RemainSeats.jar [options] [crn1 [crn2 [crn3] ...]]",
				options);
	}

	private static Term parseTerm(String termString) {
		return Term.valueOf(termString.toUpperCase());
	}
}