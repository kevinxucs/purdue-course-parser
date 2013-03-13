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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.kevxu.purdueassist.course.ScheduleDetail;
import net.kevxu.purdueassist.course.ScheduleDetail.ScheduleDetailEntry;
import net.kevxu.purdueassist.course.elements.Predefined.Term;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HtmlParseException;
import net.kevxu.purdueassist.course.shared.RequestNotFinishedException;
import net.kevxu.purdueassist.course.shared.ResultNotMatchException;
import net.kevxu.purdueassist.test.utility.FileWriterQueue;
import net.kevxu.purdueassist.test.utility.FileWriterQueue.FileWriterEntry;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ScheduleDetailTest {

	private static final HelpFormatter formatter = new HelpFormatter();
	private static final Options options = new Options();

	private static Term term;
	private static boolean silent;
	private static boolean smallSilent;
	private static List<Integer> crns;
	private static boolean parallel;
	private static int threads;
	private static String folder;

	public static void main(String[] args) {
		options.addOption("t", "term", true, "full name (without space) for school term. i.e. fall2012 (optional)");
		options.addOption("s", "small-silent", false, "Do not print input information.");
		options.addOption("S", "slient", false, "Do not print anything.");
		options.addOption("p", "parallel", true, "Parallel process (with thread number).");
		options.addOption("l", "list", true, "Use crn list file (all crn typed will be ignore).");
		options.addOption("f", "output-folder", true, "Output results into a folder.");

		CommandLineParser parser = new GnuParser();
		org.apache.commons.cli.CommandLine cmd;

		// Parsing command line
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

				if (cmd.hasOption("p")) {
					parallel = true;
					threads = Integer.valueOf(cmd.getOptionValue("p"));
				} else {
					parallel = false;
				}

				if (cmd.hasOption("f")) {
					folder = cmd.getOptionValue("f");
				}

				crns = new ArrayList<Integer>();
				if (cmd.hasOption("l")) {
					String listName = cmd.getOptionValue("l");
					Scanner scanner = new Scanner(new BufferedReader(new FileReader(listName)));

					while (scanner.hasNext()) {
						crns.add(scanner.nextInt());
					}

					scanner.close();
				} else {
					for (String crnString : cmd.getArgs()) {
						crns.add(Integer.valueOf(crnString));
					}
				}

				final FileWriterQueue writer = new FileWriterQueue();
				writer.start();

				if (!parallel) {
					// Not parallel
					for (int crn : crns) {
						new ScheduleDetailTestRunnable(term, crn, silent, smallSilent, folder, writer).run();
					}
				} else {
					// Parallel
					// TODO: bug fix - some request lost during execution
					ExecutorService executor = Executors.newFixedThreadPool(threads);
					for (int crn : crns) {
						executor.submit(new ScheduleDetailTestRunnable(term, crn, silent, smallSilent, folder, writer));
					}

					executor.shutdown();

					while (!executor.awaitTermination(100, TimeUnit.MILLISECONDS)) {

					}
				}

				while (true) {
					if (writer.queueSize() == 0)
						break;

					Thread.sleep(100);
				}

				writer.stop();
			}
		} catch (ParseException e) {
			System.err.println("Command line arguments parsing failed. Reason: "
					+ e.getMessage());
			printHelp(formatter, options);
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e.getMessage());
			printHelp(formatter, options);
		} catch (RuntimeException e) {
			System.err.println("Runtime exception: " + e.getMessage());
			printHelp(formatter, options);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private static void printHelp(HelpFormatter formatter, Options options) {
		formatter.printHelp("java -jar RemainSeats.jar [options] [crn1 [crn2 [crn3] ...]]", options);
	}

	private static Term parseTerm(String termString) {
		return Term.valueOf(termString.toUpperCase());
	}
}

class ScheduleDetailTestRunnable implements Runnable {

	private Term term;
	private int crn;
	private boolean silent;
	private boolean smallSilent;
	private String folder;
	private FileWriterQueue writer;

	public ScheduleDetailTestRunnable(Term term, int crn, boolean silent, boolean smallSilent, String folder, FileWriterQueue writer) {
		this.term = term;
		this.crn = crn;
		this.silent = silent;
		this.smallSilent = smallSilent;
		this.folder = folder;
		this.writer = writer;
	}

	@Override
	public void run() {
		ScheduleDetail detail = new ScheduleDetail();
		try {
			ScheduleDetailEntry entry;
			entry = detail.getResult(term, crn);
			if (folder == null) {
				if (!silent) {
					if (!smallSilent)
						System.err.println("INPUT: " + crn + " " + term);
					System.out.println(entry);
				}
			} else {
				// writer.append(new FileWriterEntry(folder + File.separator +
				// crn, entry.toString()));
				File dir = new File(folder);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(folder + File.separator + crn);
				if (!file.exists()) {
					file.createNewFile();
				}
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write(entry.toString());
				writer.flush();
				writer.close();
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
			// if (!silent) {
			// if (!smallSilent)
			// System.err.println("INPUT: " + crn + " " + term);
			// System.out.println("CRN: " + crn + " " + "Term: " + term
			// + " Not Found: " + e.getMessage() + "\n");
			// }
		} catch (ResultNotMatchException e) {
			if (!silent) {
				if (!smallSilent)
					System.err.println("INPUT: " + crn + " " + term);
				e.printStackTrace();
				System.err.println();
			}
		} catch (RuntimeException e) {
			if (!silent) {
				if (!smallSilent)
					System.err.println("INPUT: " + crn + " " + term);
				e.printStackTrace();
				System.err.println();
			}
		}

	}
}