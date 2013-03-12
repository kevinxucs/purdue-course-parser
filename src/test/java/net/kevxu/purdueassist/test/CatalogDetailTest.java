/*
 * CatalogDetailTest.java
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
import java.util.Scanner;

import net.kevxu.purdueassist.course.CatalogDetail;
import net.kevxu.purdueassist.course.CatalogDetail.CatalogDetailEntry;
import net.kevxu.purdueassist.course.elements.Predefined.Subject;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HtmlParseException;
import net.kevxu.purdueassist.course.shared.RequestNotFinishedException;
import net.kevxu.purdueassist.course.shared.ResultNotMatchException;

public class CatalogDetailTest {
	public static void main(String[] args) {
		Scanner getInput = new Scanner(System.in);

		System.out.println("Subject:");
		Subject tem_subject = null;
		try {
			tem_subject = Subject.valueOf(getInput.nextLine().toUpperCase());
		} catch (IllegalArgumentException e) {
			System.err.println("No such subject.");
			System.exit(-1);
		}
		final Subject subject = tem_subject;
		// System.out.println("Cnbr:");
		// int tmp_cnbr=getInput.nextInt();
		// if(tmp_cnbr<1000){
		// tmp_cnbr*=100;
		// }
		// final int cnbr=tmp_cnbr;

		System.out.println("CNBR: ");
		int cnbr = 0;
		boolean all = false;
		try {
			String strcnbr = getInput.nextLine();
			if (strcnbr.equals("ALL") || strcnbr.equals("")) {
				all = true;
			} else {
				cnbr = Integer.valueOf(strcnbr);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		if (all) {
			for (cnbr = 10000; cnbr < 50000; cnbr += 100) {
				CatalogDetail detail = new CatalogDetail();
				try {
					CatalogDetailEntry entry = detail.getResult(subject, cnbr);
					System.out.println(entry);
					System.out.println("Course Found");
					System.out.println("----------------------");
				} catch (RequestNotFinishedException e) {
					System.err.println("INPUT: " + cnbr + " " + subject);
					e.printStackTrace();
					System.err.println("----------------------");
				} catch (IOException e) {
					System.err.println("INPUT: " + cnbr + " " + subject);
					System.err.println("IO Error!");
					System.err.println("----------------------");
				} catch (HtmlParseException e) {
					System.err.println("INPUT: " + cnbr + " " + subject);
					System.err.println("Parse Error!");
					System.err.println(e.getMessage());
					System.err.println("----------------------");
				} catch (CourseNotFoundException e) {
					System.err.println("INPUT: " + cnbr + " " + subject);
					System.err.println("Course Not Found!");
					System.err.println("----------------------");
				} catch (ResultNotMatchException e) {
					System.err.println("INPUT: " + cnbr + " " + subject);
					System.err.println("Parse Error!");
					System.err.println(e.getMessage());
					System.err.println("----------------------");
				} catch (RuntimeException e) {
					System.err.println("INPUT: " + cnbr + " " + subject);
					e.printStackTrace();
					System.err.println("----------------------");
				}
			}
		} else {
			CatalogDetail detail = new CatalogDetail();
			try {
				CatalogDetailEntry entry = detail.getResult(subject, cnbr);
				System.out.println(entry);
				System.out.println("Course Found");
				System.out.println("----------------------");
			} catch (RequestNotFinishedException e) {
				System.err.println("INPUT: " + cnbr + " " + subject);
				e.printStackTrace();
				System.err.println("----------------------");
			} catch (IOException e) {
				System.err.println("INPUT: " + cnbr + " " + subject);
				System.err.println("IO Error!");
				System.err.println("----------------------");
			} catch (HtmlParseException e) {
				System.err.println("INPUT: " + cnbr + " " + subject);
				System.err.println("Parse Error!");
				System.err.println(e.getMessage());
				System.err.println("----------------------");
			} catch (CourseNotFoundException e) {
				System.err.println("INPUT: " + cnbr + " " + subject);
				System.err.println("Course Not Found!");
				System.err.println("----------------------");
			} catch (ResultNotMatchException e) {
				System.err.println("INPUT: " + cnbr + " " + subject);
				System.err.println("Parse Error!");
				System.err.println(e.getMessage());
				System.err.println("----------------------");
			} catch (RuntimeException e) {
				System.err.println("INPUT: " + cnbr + " " + subject);
				e.printStackTrace();
				System.err.println("----------------------");
			}
		}
	}
}
