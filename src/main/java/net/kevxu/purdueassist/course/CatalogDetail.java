/*
 * CatalogDetail.java
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

package net.kevxu.purdueassist.course;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.kevxu.purdueassist.course.elements.Predefined.Subject;
import net.kevxu.purdueassist.course.elements.Predefined.Term;
import net.kevxu.purdueassist.course.elements.Predefined.Type;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HtmlParseException;
import net.kevxu.purdueassist.course.shared.RequestNotFinishedException;
import net.kevxu.purdueassist.course.shared.ResultNotMatchException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This is the class implementing "Catalog Detail" search described in the
 * document.
 * <p>
 * Input: subject cnbr <br />
 * Input (optional): term
 * <p>
 * Output: <br />
 * subject cnbr name description levels type offeredBy department campuses
 * restrictions prerequisites
 * 
 * @author Rendong Chen (ryan), Kaiwen Xu (kevin)
 */
public class CatalogDetail {

	private static final String URL_HEAD = "https://selfservice.mypurdue.purdue.edu/prod/"
			+ "bzwsrch.p_catalog_detail";

	private Term mTerm;
	private Subject mSubject;
	private int mCnbr;

	private HttpClient mHttpClient;

	private AtomicBoolean mRequestFinished;

	public CatalogDetail() {
		mHttpClient = new DefaultHttpClient();
		mRequestFinished = new AtomicBoolean(true);
	}

	public CatalogDetailEntry getResult(Subject subject, int cnbr) throws RequestNotFinishedException, IOException, HtmlParseException, CourseNotFoundException, ResultNotMatchException {
		return getResult(Term.CURRENT, subject, cnbr);
	}

	public CatalogDetailEntry getResult(Term term, Subject subject, int cnbr) throws RequestNotFinishedException, IOException, HtmlParseException, CourseNotFoundException, ResultNotMatchException {
		if (!isRequestFinished())
			throw new RequestNotFinishedException();

		requestStart();

		if (term == null)
			mTerm = Term.CURRENT;
		else
			mTerm = term;

		mSubject = subject;
		mCnbr = cnbr;

		CatalogDetailEntry entry = null;

		try {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.add(new BasicNameValuePair("term", mTerm.getLinkName()));
			parameters.add(new BasicNameValuePair("subject", mSubject.name()));
			parameters.add(new BasicNameValuePair("cnbr", Integer.toString(mCnbr)));

			HttpPost httpPost = new HttpPost(URL_HEAD);
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));

			HttpResponse httpResponse = mHttpClient.execute(httpPost);
			InputStream stream = httpResponse.getEntity().getContent();
			Header encoding = httpResponse.getEntity().getContentEncoding();
			Document document;
			if (encoding == null) {
				document = Jsoup.parse(stream, null, URL_HEAD);
			} else {
				document = Jsoup.parse(stream, encoding.getValue(), URL_HEAD);
			}
			stream.close();
			entry = parseDocument(document);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} finally {
			requestEnd();
		}

		return entry;
	}

	private void requestStart() {
		this.mRequestFinished.set(false);
	}

	private void requestEnd() {
		this.mRequestFinished.set(true);
	}

	/**
	 * Check whether previous request has been finished.
	 * 
	 * @return Return true if previous request has already finished.
	 */
	public boolean isRequestFinished() {
		return mRequestFinished.get();
	}

	private CatalogDetailEntry parseDocument(Document document) throws HtmlParseException, CourseNotFoundException, IOException, ResultNotMatchException {
		CatalogDetailEntry entry = new CatalogDetailEntry(this.mTerm, this.mSubject, this.mCnbr);
		Elements tableElements = document.getElementsByAttributeValue("summary", "This table lists the course detail for the selected term.");
		if (!tableElements.isEmpty()) {
			String[] temp;

			// get name
			Element body = tableElements.first().select("tbody").first();
			String nameBlock = body.select("tr td.nttitle").first().text();
			temp = new String[] {
					nameBlock.substring(0, nameBlock.indexOf('-')).trim(),
					nameBlock.substring(nameBlock.indexOf('-') + 1).trim() };
			entry.setSubject(Subject.valueOf(temp[0].split(" ")[0].toUpperCase()));
			if (!entry.getSubject().equals(entry.getSearchSubject())) {
				throw new ResultNotMatchException("Result not match with search subject.");
			}
			entry.setCnbr(Integer.valueOf(temp[0].split(" ")[1]));
			if (entry.getCnbr() != entry.getSearchCnbr()) {
				throw new ResultNotMatchException("Result not match with search cnbr.");
			}
			entry.setName(temp[1]);

			// get description
			body = body.select(".ntdefault").first();
			String text = body.text();
			int split = text.indexOf("Levels:");
			String description = text.substring(0, split);
			description = description.substring(20);
			entry.setDescription(description);

			// get levels
			int begin = split;
			int end = text.indexOf("Schedule Types:");
			String levels = text.substring(begin + 8, end);
			temp = levels.split("[ ,]");
			List<String> lvs = new ArrayList<String>();
			for (String s : temp)
				if (!s.equals("")) {
					lvs.add(s);
				}
			entry.setLevels(lvs);

			// get type and prerequisites
			List<Type> types = new ArrayList<Type>();
			List<String> preq = new ArrayList<String>();
			Elements parsing_A = body.select("a");
			for (Element e : parsing_A) {
				if (e.attr("href").contains("schd_in")
						&& !(e.attr("href").contains("%"))) {

					try {
						types.add(Type.valueOf(e.text().replace(" ", "")));
					} catch (Exception exception) {
						throw new HtmlParseException();
					}
				} else if (e.attr("href").contains("sel_attr=")) {
					preq.add(e.text());
				}
			}
			if (types.size() > 0)
				entry.setTypes(types);
			if (preq.size() > 0)
				entry.setPrerequisites(preq);

			// get offered by
			begin = text.indexOf("Offered By:");
			end = text.indexOf("Department:");
			if (end < 0)
				end = text.indexOf("Course Attributes:");
			if (end > 0) {
				entry.setOfferedBy(text.substring(begin + 12, end - 1));
			}

			// get department
			begin = text.indexOf("Department:");
			if (begin > 0) {
				end = text.indexOf("Course Attributes:");
				entry.setDepartment((text.substring(begin + 12, end - 1)));
			}

			// get campus
			begin = text.indexOf("May be offered at any of the following campuses:");
			String campuses;
			end = text.indexOf("Repeatable for Additional Credit:");
			if (end < 0)
				end = text.indexOf("Learning Objectives:");
			if (end < 0)
				end = text.indexOf("Restrictions:");
			if (end < 0)
				end = text.indexOf("Corequisites:");
			if (end < 0)
				end = text.indexOf("Prerequisites:");
			if (end < 0) {
				campuses = text.substring(begin
						+ "May be offered at any of the following campuses:".length()
						+ 5);
			} else {
				campuses = text.substring(begin
						+ "May be offered at any of the following campuses:".length()
						+ 5, end - 1);
			}
			
			String ttt=campuses.substring(0,7);
			temp = campuses.replace(ttt, "#").split("#");
			List<String> camps = new ArrayList<String>();
			for (String s : temp) {
				if (s.length() > 1) {
					camps.add(s);
				}

			}
			entry.setCampuses(camps);

			// get restrictions
			begin = text.indexOf("Restrictions:");
			end = text.indexOf("Corequisites:");
			if (end < 0)
				end = text.indexOf("Prerequisites:");
			if (begin > 0 && end < 0) {
				entry.setRestrictions(text.substring(begin
						+ "Restrictions:".length()).replace("            ", "\n"));
			} else if (begin > 0) {
				entry.setRestrictions(text.substring(begin
						+ "Restrictions:".length(), end).replace("            ", "\n"));
			}
		} else {
			// test empty
			Elements informationElements = document.getElementsByClass("pagebodydiv");
			if (!informationElements.isEmpty()
					&& informationElements.text().contains("No course to display")) {
				throw new CourseNotFoundException(informationElements.text());
			} else {
				throw new HtmlParseException("Course table not found, but page does not contain message stating no course found.");
			}
		}

		return entry;
	}

	/**
	 * This class contains information return by CatalogDetail.
	 * 
	 * @author Rendong Chen (ryan), Kaiwen Xu (kevin)
	 */
	@SuppressWarnings("unused")
	public class CatalogDetailEntry {

		private Term term;
		private Subject subject;
		private int cnbr;
		private String name;
		private String description;
		private List<String> levels;
		private List<Type> types;
		private String offeredBy;
		private String department;
		private List<String> campuses;
		private String restrictions;
		private List<String> prerequisites;

		private Term searchTerm;
		private Subject searchSubject;
		private int searchCnbr;

		public CatalogDetailEntry(Term term, Subject subject, int cnbr) {
			this.searchTerm = term;
			this.searchSubject = subject;
			this.searchCnbr = cnbr;
		}

		private Term getSearchTerm() {
			return this.searchTerm;
		}

		private Subject getSearchSubject() {
			return searchSubject;
		}

		private int getSearchCnbr() {
			return searchCnbr;
		}

		public Term getTerm() {
			return this.term;
		}

		public Subject getSubject() {
			return this.subject;
		}

		public int getCnbr() {
			return this.cnbr;
		}

		public String getName() {
			return this.name;
		}

		public String getDescription() {
			return this.description;
		}

		public List<String> getLevels() {
			return this.levels;
		}

		public List<Type> getTypes() {
			return this.types;
		}

		public String getOfferedBy() {
			return this.offeredBy;
		}

		public String getDepartment() {
			return this.department;
		}

		public List<String> getCampuses() {
			return this.campuses;
		}

		public String getRestrictions() {
			return this.restrictions;
		}

		public List<String> getPrerequisites() {
			return this.prerequisites;
		}

		private void setTerm(Term term) {
			this.term = term;
		}

		private void setSubject(Subject subject) {
			this.subject = subject;
		}

		private void setCnbr(int cnbr) {
			this.cnbr = cnbr;
		}

		private void setName(String name) {
			this.name = name;
		}

		private void setDescription(String description) {
			this.description = description;
		}

		private void setLevels(List<String> levels) {
			this.levels = levels;
		}

		private void setTypes(List<Type> types) {
			this.types = types;
		}

		private void setOfferedBy(String offeredBy) {
			this.offeredBy = offeredBy;
		}

		private void setDepartment(String department) {
			this.department = department;
		}

		private void setCampuses(List<String> campuses) {
			this.campuses = campuses;
		}

		private void setRestrictions(String restrictions) {
			this.restrictions = restrictions;
		}

		private void setPrerequisites(List<String> prerequisites) {
			this.prerequisites = prerequisites;
		}

		@Override
		public String toString() {
			return "Term: " + term + "\n" + "Subject: " + subject + "\n"
					+ "CNBR: " + cnbr + "\n" + "Course Name: " + name + "\n"
					+ "Description: " + description + "\n" + "Levels: "
					+ levels + "\n" + "Types: " + types + "\n" + "Offered by: "
					+ offeredBy + "\n" + "Department: " + department + "\n"
					+ "Campuses: " + campuses + "\n" + "Restrictions: "
					+ restrictions + "\n" + "Prerequisites: " + prerequisites
					+ "\n";
		}

	}

}
