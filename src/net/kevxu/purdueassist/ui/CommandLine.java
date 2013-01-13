package net.kevxu.purdueassist.ui;

import java.io.IOException;
import java.util.Formatter;

import net.kevxu.purdueassist.course.ScheduleDetail;
import net.kevxu.purdueassist.course.ScheduleDetail.OnScheduleDetailFinishedListener;
import net.kevxu.purdueassist.course.ScheduleDetail.ScheduleDetailEntry;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HttpParseException;
import net.kevxu.purdueassist.course.shared.Predefined.Term;

public class CommandLine {

	public static final String VERSION = "0.1.2";

	public static void main(String[] args) {
		if (args.length <= 1) {
			printHelp();
		} else {
			Term term = parseTerm(args[0]);
			for (int i = 1; i < args.length; i++) {
				ScheduleDetail scheduleDetail = new ScheduleDetail(term,
						Integer.valueOf(args[i]),
						new OnScheduleDetailFinishedListener() {

							@Override
							public void onScheduleDetailFinished(
									ScheduleDetailEntry entry) {
								System.out.println(entry);
							}

							@Override
							public void onScheduleDetailFinished(IOException e) {
								System.err.println("IO Error.");
							}

							@Override
							public void onScheduleDetailFinished(
									HttpParseException e) {
								System.err.println("Parse Error: "
										+ e.getMessage());
							}

							@Override
							public void onScheduleDetailFinished(
									CourseNotFoundException e) {
								System.err.println("Course Not Found.");
							}

						});
				scheduleDetail.getResult();
			}
		}
	}

	private static void printHelp() {
		final String help = "Purdue Course Parser %s\n"
				+ "Usage: java RemainSeats.jar [term] [crn1, crn2, ...]\n"
				+ "term - full name (without space) for school term. i.e. fall2012\n"
				+ "crn - crn number of class\n";
		Formatter formatter = new Formatter();
		System.err.print(formatter.format(help, VERSION).toString());
		formatter.close();
	}

	private static Term parseTerm(String termString) {
		return Term.valueOf(termString.toUpperCase());
	}

}