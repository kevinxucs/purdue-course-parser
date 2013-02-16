package net.kevxu.purdueassist.test;

import java.io.*;
import java.util.Scanner;

import net.kevxu.purdueassist.course.CatalogDetail;
import net.kevxu.purdueassist.course.CatalogDetail.CatalogDetailEntry;
import net.kevxu.purdueassist.course.CatalogDetail.OnCatalogDetailFinishedListener;
import net.kevxu.purdueassist.course.elements.Predefined.Subject;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HttpParseException;

public class TestUnit {
	public static void main(String[] args) {
		System.out.println("Subject:");
		Scanner getInput = new Scanner(System.in);
		Subject tem_subject = null;
		try {
			tem_subject = Subject.valueOf(getInput.next().toUpperCase());
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
		for (int mcnbr = 10000; mcnbr < 50000; mcnbr += 100) {
			final int cnbr = mcnbr;
			CatalogDetail detail = new CatalogDetail(subject, cnbr, new OnCatalogDetailFinishedListener() {
				@Override
				public void onCatalogDetailFinished(CatalogDetailEntry entry) {
					// TODO Auto-generated method stub
					System.out.println(entry.toString());
					System.out.println("Course Found");
					System.out.println("----------------------");
				}

				@Override
				public void onCatalogDetailFinished(IOException e) {
					// TODO Auto-generated method stub
					System.out.println("INPUT: " + cnbr + " " + subject.toString());
					System.out.println("IO Error!");
					System.out.println("----------------------");
				}

				@Override
				public void onCatalogDetailFinished(HttpParseException e) {
					// TODO Auto-generated method stub
					System.out.println("INPUT: " + cnbr + " " + subject.toString());
					System.out.println("Parse Error!");
					System.out.println("----------------------");
				}

				@Override
				public void onCatalogDetailFinished(CourseNotFoundException e) {
					// TODO Auto-generated method stub
					// System.out.println("INPUT: " + cnbr + " " +
					// subject.toString());
					// System.out.println("Course Not Found!");
					// System.out.println("----------------------");
				}

			});
			try {
				detail.getResult();
			} catch (StringIndexOutOfBoundsException e) {

			}
		}
	}
}
