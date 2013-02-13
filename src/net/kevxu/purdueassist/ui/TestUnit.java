package net.kevxu.purdueassist.ui;

import java.io.IOException;
import java.util.Scanner;

import net.kevxu.purdueassist.course.CatalogDetail;
import net.kevxu.purdueassist.course.CatalogDetail.CatalogDetailEntry;
import net.kevxu.purdueassist.course.CatalogDetail.OnCatalogDetailFinishedListener;
import net.kevxu.purdueassist.course.elements.Predefined.Subject;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HttpParseException;

public class TestUnit {
	public static void main(String []args){

		System.out.println("Subject:");
		Scanner getInput=new Scanner(System.in);
		Subject tem_subject = null;
		try {
			tem_subject=Subject.valueOf(getInput.next().toUpperCase());
		}catch(IllegalArgumentException e){
			System.err.println("No such subject.");
			System.exit(-1);
		}
		final Subject subject=tem_subject;
		System.out.println("Cnbr:");
		final int cnbr=getInput.nextInt();

		CatalogDetail detail=new CatalogDetail(subject, cnbr, new OnCatalogDetailFinishedListener(){
			@Override
			public void onCatalogDetailFinished(CatalogDetailEntry entry) {
				// TODO Auto-generated method stub
				System.out.println("Course Found");
			}

			@Override
			public void onCatalogDetailFinished(IOException e) {
				// TODO Auto-generated method stub
				System.out.println("INPUT: " + cnbr + " " + subject.toString());
				System.out.println("IO Error!");
			}

			@Override
			public void onCatalogDetailFinished(HttpParseException e) {
				// TODO Auto-generated method stub
				System.out.println("INPUT: " + cnbr + " " + subject.toString());
				System.out.println("Parse Error!");
			}

			@Override
			public void onCatalogDetailFinished(CourseNotFoundException e) {
				// TODO Auto-generated method stub
				System.out.println("INPUT: " + cnbr + " " + subject.toString());
				System.out.println("Course Not Found!");
			}

		});
		detail.getResult();

	}
}
