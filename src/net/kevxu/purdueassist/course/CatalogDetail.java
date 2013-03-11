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
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This is the class implementing "Catalog Detail" search described in the
 * document. It utilizes asynchronous function call for non-blocking calling
 * style. You have to provide callback method by implementing
 * CatalogDetailListener.
 * <p>
 * Input: subject cnbr <br />
 * Input (optional): term
 * <p>
 * Output: <br />
 * subject cnbr name description levels type offeredBy department campuses
 * restrictions prerequisites
 * 
 * @author Rendong Chen (ryan), Kaiwen Xu (kevin)
 * @see CatalogDetailListener
 */
public class CatalogDetail {

	private static final String URL_HEAD = "https://selfservice.mypurdue.purdue.edu/prod/"
			+ "bzwsrch.p_catalog_detail";

	private Term term;
	private Subject subject;
	private int cnbr;

	private CatalogDetailListener mListener;
	private HttpClient httpClient;

	private AtomicBoolean requestFinished;

	public interface CatalogDetailListener {
		public void onCatalogDetailFinished(CatalogDetailEntry entry, Term term, Subject subject, int cnbr);

		public void onCatalogDetailFinished(IOException e, Term term, Subject subject, int cnbr);

		public void onCatalogDetailFinished(HtmlParseException e, Term term, Subject subject, int cnbr);

		public void onCatalogDetailFinished(CourseNotFoundException e, Term term, Subject subject, int cnbr);

		public void onCatalogDetailFinished(Exception e, Term term, Subject subject, int cnbr);
	}

	public CatalogDetail(CatalogDetailListener catalogDetailListener) {
		this.mListener = catalogDetailListener;
		this.requestFinished.set(true);
	}

	public void getResult(Subject subject, int cnbr) throws RequestNotFinishedException {
		getResult(Term.CURRENT, subject, cnbr);
	}

	public void getResult(Term term, Subject subject, int cnbr) throws RequestNotFinishedException {
		if (!isRequestFinished())
			throw new RequestNotFinishedException();

		requestStart();

		if (term == null)
			term = Term.CURRENT;

		this.term = term;
		this.subject = subject;
		this.cnbr = cnbr;

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("term", term.getLinkName()));
		parameters.add(new BasicNameValuePair("subject", subject.name()));
		parameters.add(new BasicNameValuePair("cnbr", Integer.toString(cnbr)));

		httpClient = new BasicHttpClientAsync(URL_HEAD, HttpMethod.POST, this);
		try {
			httpClient.setParameters(parameters);
			httpClient.getResponse();
		} catch (MethodNotPostException e) {
			e.printStackTrace();
			requestEnd();
		}
	}

	private synchronized void requestStart() {
		this.requestFinished.set(false);
	}

	private synchronized void requestEnd() {
		this.requestFinished.set(false);
	}

	/**
	 * Check whether previous request has been finished.
	 * 
	 * @return Return true if previous request has already finished.
	 */
	public boolean isRequestFinished() {
		return requestFinished.get();
	}

	@Override
	public void onRequestFinished(HttpResponse httpResponse) {
		try {
			InputStream stream = httpResponse.getEntity().getContent();
			Header encoding = httpResponse.getEntity().getContentEncoding();
			Document document;
			if (encoding == null) {
				document = Jsoup.parse(stream, null, URL_HEAD);
			} else {
				document = Jsoup.parse(stream, encoding.getValue(), URL_HEAD);
			}
			stream.close();
			CatalogDetailEntry entry = parseDocument(document);
			mListener.onCatalogDetailFinished(entry, this.term, this.subject, this.cnbr);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			mListener.onCatalogDetailFinished(e, this.term, this.subject, this.cnbr);
		} catch (HtmlParseException e) {
			mListener.onCatalogDetailFinished(e, this.term, this.subject, this.cnbr);
		} catch (CourseNotFoundException e) {
			mListener.onCatalogDetailFinished(e, this.term, this.subject, this.cnbr);
		} catch (Exception e) {
			mListener.onCatalogDetailFinished(e, this.term, this.subject, this.cnbr);
		} finally {
			requestEnd();
		}
	}

	private CatalogDetailEntry parseDocument(Document document) throws HtmlParseException, CourseNotFoundException, IOException, ResultNotMatchException {
		CatalogDetailEntry entry = new CatalogDetailEntry(this.term, this.subject, this.cnbr);
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
				entry.setType(types);
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
			temp = campuses.replace("       ", "#").split("#");
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
		private List<Type> type;
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

		public List<Type> getType() {
			return this.type;
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

		private void setType(List<Type> type) {
			this.type = type;
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
			StringBuilder myStr = new StringBuilder();

			if (subject != null) {
				myStr.append("Subject: " + subject.toString() + "\n");
			}

			myStr.append("CNBR: " + cnbr + "\n");

			if (name != null) {
				myStr.append("Name: " + name + "\n");
			}

			if (description != null) {
				myStr.append("Description: " + description + "\n");
			}

			if (levels != null) {
				myStr.append("Level: ");
				myStr.append(levels);
				myStr.append("\n");
			}

			if (type != null) {
				myStr.append("Type: ");
				myStr.append(type);
				myStr.append("\n");
			}

			if (offeredBy != null) {
				myStr.append("OfferedBy: " + offeredBy + "\n");
			}

			if (department != null) {
				myStr.append("Department: " + department + "\n");
			}

			if (campuses != null) {
				myStr.append("Campuses: ");
				myStr.append(campuses);
				myStr.append("\n");
			}

			if (restrictions != null) {
				myStr.append("Restrictions: " + restrictions + "\n");
			}

			if (prerequisites != null) {
				myStr.append("Prerequisites: ");
				myStr.append(prerequisites);
				myStr.append("\n");
			}

			return myStr.toString();
		}

	}

}
