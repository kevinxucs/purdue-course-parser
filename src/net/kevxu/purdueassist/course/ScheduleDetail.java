/*
 * ScheduleDetail.java
 * 
 * Written by Kaiwen Xu (kevin).
 * Released under Apache License 2.0.
 */

package net.kevxu.purdueassist.course;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kevxu.purdueassist.course.elements.Seats;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HttpParseException;
import net.kevxu.purdueassist.course.shared.Predefined.Subject;
import net.kevxu.purdueassist.course.shared.Predefined.Term;
import net.kevxu.purdueassist.course.shared.Predefined.Type;
import net.kevxu.purdueassist.course.shared.ResultNotMatchException;
import net.kevxu.purdueassist.shared.httpclient.BasicHttpClientAsync;
import net.kevxu.purdueassist.shared.httpclient.BasicHttpClientAsync.HttpMethod;
import net.kevxu.purdueassist.shared.httpclient.BasicHttpClientAsync.OnRequestFinishedListener;
import net.kevxu.purdueassist.shared.httpclient.MethodNotPostException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This is the class implementing "Schedule Detail Search" described in
 * document. It utilizes asynchronous function call for non-blocking calling
 * style. You have to provide callback method by implementing
 * OnScheduleDetailFinishedListener.
 * <p>
 * Input: crn term (optional)
 * <p>
 * Output: name crn subject cnbr section term level campus type credits seats
 * waitlist seats prerequisites restrictions
 * 
 * @author Kaiwen Xu (kevin)
 * @see OnScheduleDetailFinishedListener
 */
public class ScheduleDetail implements OnRequestFinishedListener {

	private static final String URL_HEAD = "https://selfservice.mypurdue.purdue.edu/prod/"
			+ "bzwsrch.p_schedule_detail";

	public Term term = Term.CURRENT;
	public int crn;

	private OnScheduleDetailFinishedListener mListener;
	private BasicHttpClientAsync httpClient;

	/**
	 * Callback methods you have to implement.
	 * 
	 * @author Kaiwen Xu (kevin)
	 */
	public interface OnScheduleDetailFinishedListener {
		public void onScheduleDetailFinished(ScheduleDetailEntry entry);

		public void onScheduleDetailFinished(IOException e);

		public void onScheduleDetailFinished(HttpParseException e);

		public void onScheduleDetailFinished(CourseNotFoundException e);
	}

	/**
	 * Constructor for specific crn. Term will be set to CURRENT.
	 * 
	 * @param crn
	 *            CRN number for course.
	 * @param onScheduleDetailFinishedListener
	 *            callback you have to implement.
	 */
	public ScheduleDetail(int crn,
			OnScheduleDetailFinishedListener onScheduleDetailFinishedListener) {
		this(Term.CURRENT, crn, onScheduleDetailFinishedListener);
	}

	public ScheduleDetail(Term term, int crn,
			OnScheduleDetailFinishedListener onScheduleDetailFinishedListener) {
		if (term != null)
			this.term = term;
		else
			this.term = Term.CURRENT;

		this.crn = crn;
		this.mListener = onScheduleDetailFinishedListener;
	}

	/**
	 * Call this method to start retrieving and parsing data.
	 */
	public void getResult() {
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("term", term.getLinkName()));
		parameters.add(new BasicNameValuePair("crn", Integer.toString(crn)));

		httpClient = new BasicHttpClientAsync(URL_HEAD, HttpMethod.POST, this);
		try {
			httpClient.setParameters(parameters);
			httpClient.getResponse();
		} catch (MethodNotPostException e) {
			e.printStackTrace();
		}
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
			ScheduleDetailEntry entry = parseDocument(document);
			mListener.onScheduleDetailFinished(entry);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			mListener.onScheduleDetailFinished(e);
		} catch (HttpParseException e) {
			mListener.onScheduleDetailFinished(e);
		} catch (CourseNotFoundException e) {
			mListener.onScheduleDetailFinished(e);
		} catch (ResultNotMatchException e) {
			mListener.onScheduleDetailFinished(new HttpParseException(e
					.getMessage()));
		}
	}

	@Override
	public void onRequestFinished(ClientProtocolException e) {
		e.printStackTrace();
	}

	@Override
	public void onRequestFinished(IOException e) {
		mListener.onScheduleDetailFinished(e);
	}

	private ScheduleDetailEntry parseDocument(Document document)
			throws HttpParseException, CourseNotFoundException,
			ResultNotMatchException {
		ScheduleDetailEntry entry = new ScheduleDetailEntry(crn);
		Elements tableElements = document
				.getElementsByAttributeValue("summary",
						"This table is used to present the detailed class information.");

		if (tableElements.isEmpty() != true) {
			for (Element tableElement : tableElements) {
				// get basic info for selected course
				Element tableBasicInfoElement = tableElement
						.getElementsByClass("ddlabel").first();
				if (tableBasicInfoElement != null) {
					entry.setBasicInfo(tableBasicInfoElement.text());
				} else {
					throw new HttpParseException();
				}

				// get detailed course info
				Element tableDetailedInfoElement = tableElement
						.getElementsByClass("dddefault").first();

				if (tableDetailedInfoElement != null) {
					// process seat info
					Elements tableSeatDetailElements = tableDetailedInfoElement
							.getElementsByAttributeValue("summary",
									"This layout table is used to present the seating numbers.");
					if (tableSeatDetailElements.size() == 1) {
						Element tableSeatDetailElement = tableSeatDetailElements
								.first();
						Elements tableSeatDetailEntryElements = tableSeatDetailElement
								.getElementsByTag("tbody").first().children();
						if (tableSeatDetailEntryElements.size() == 3) {
							entry.setSeats(tableSeatDetailEntryElements.get(1)
									.text());
							entry.setWaitlistSeats(tableSeatDetailEntryElements
									.get(2).text());
						} else {
							throw new HttpParseException();
						}
					} else {
						throw new HttpParseException();
					}
					// remove the seat info from detailed info
					tableSeatDetailElements.remove();

					// remaining information
					entry.setRemainingInfo(tableDetailedInfoElement.html());

				} else {
					throw new HttpParseException();
				}

			}
		} else {
			throw new CourseNotFoundException();
		}

		return entry;
	}

	/**
	 * This class contains information return by ScheduleDetail.
	 * 
	 * @author Kaiwen Xu (kevin)
	 */
	public class ScheduleDetailEntry {

		private int searchCrn;

		public ScheduleDetailEntry(int crn) {
			this.searchCrn = crn;
		}

		private String name;
		private int crn;
		private Subject subject;
		private int cnbr;
		private String section;
		private Term term;
		private List<String> levels;
		private String campus;
		private Type type;
		private double credits;
		private Seats seats;
		private Seats waitlistSeats;
		private String prerequisites;
		private String restrictions;

		public String getName() {
			return name;
		}

		public int getCrn() {
			return crn;
		}

		public Subject getSubject() {
			return subject;
		}

		public int getCnbr() {
			return cnbr;
		}

		public String getSection() {
			return section;
		}

		public Term getTerm() {
			return term;
		}

		public List<String> getLevels() {
			return levels;
		}

		public String getCampus() {
			return campus;
		}

		public Type getType() {
			return type;
		}

		public double getCredits() {
			return credits;
		}

		public Seats getSeats() {
			return seats;
		}

		public Seats getWaitlistSeats() {
			return waitlistSeats;
		}

		public String getPrerequisites() {
			return prerequisites;
		}

		public String getRestrictions() {
			return restrictions;
		}

		@Override
		public String toString() {
			return "Course Name: " + name + "\n" + "CRN: " + crn + "\n"
					+ "Subject: " + subject + "\n" + "CNBR: " + cnbr + "\n"
					+ "Section: " + section + "\n" + "Term: " + term + "\n"
					+ "Levels: " + levels + "\n" + "Campus: " + campus + "\n"
					+ "Type: " + type + "\n" + "Credits: " + credits + "\n"
					+ "Seats: " + seats + "\n" + "Waitlist Seats: "
					+ waitlistSeats + "\n" + "Prerequisites: " + prerequisites
					+ "\n" + "Restrictions: " + restrictions + "\n";
		}

		/**
		 * Set course name, crn, subject - cnbr and section number based on the
		 * string passed to this method.
		 * 
		 * @param basicInfo
		 *            String contains course name, crn, subject - cnbr and
		 *            section number.
		 * @throws HttpParseException
		 * @throws ResultNotMatchException
		 */
		private void setBasicInfo(String basicInfo) throws HttpParseException,
				ResultNotMatchException {
			String[] basicInfoes = basicInfo.split(" - ");
			if (basicInfoes.length == 4) {
				this.name = basicInfoes[0];
				this.crn = Integer.valueOf(basicInfoes[1]);
				if (this.crn != this.searchCrn)
					throw new ResultNotMatchException(
							"Result not match with search option.");
				this.section = basicInfoes[3];

				String[] subjectCnbr = basicInfoes[2].split(" ");
				if (subjectCnbr.length == 2) {
					this.subject = Subject.valueOf(subjectCnbr[0]);
					this.cnbr = Integer.valueOf(subjectCnbr[1]);
				} else {
					throw new HttpParseException();
				}
			} else {
				throw new HttpParseException();
			}
		}

		/**
		 * Set seats information, which contains capacity, actual and remaining.
		 * 
		 * @param seatsInfo
		 *            String contains capacity, actual and remaining.
		 * @throws HttpParseException
		 */
		private void setSeats(String seatsInfo) throws HttpParseException {
			String[] seatsInfoes = seatsInfo.split(" ");
			if (seatsInfoes.length == 4) {
				this.seats = new Seats(Integer.valueOf(seatsInfoes[1]),
						Integer.valueOf(seatsInfoes[2]),
						Integer.valueOf(seatsInfoes[3]));
			} else {
				throw new HttpParseException();
			}
		}

		/**
		 * Same as setSeats().
		 * 
		 * @param waitlistSeatsInfo
		 * @throws HttpParseException
		 */
		private void setWaitlistSeats(String waitlistSeatsInfo)
				throws HttpParseException {
			String[] waitlistSeatsInfoes = waitlistSeatsInfo.split(" ");
			if (waitlistSeatsInfoes.length == 5) {
				this.waitlistSeats = new Seats(
						Integer.valueOf(waitlistSeatsInfoes[2]),
						Integer.valueOf(waitlistSeatsInfoes[3]),
						Integer.valueOf(waitlistSeatsInfoes[4]));
			} else {
				throw new HttpParseException();
			}
		}

		/**
		 * Set term, levels, campus and etc. based on the html passed to this
		 * method.
		 * 
		 * @param remainingInfoHtml
		 *            Html String contains information about the term, levels,
		 *            campus.
		 */
		private void setRemainingInfo(String remainingInfoHtml) {
			final int NOT_RECORD = 0;
			final int PREREQUISTES = 1;
			final int RESTRICTIONS = 2;

			int recordType = NOT_RECORD;

			String prerequisitesString = null;
			String restrictionsString = null;

			String[] remainingInfoes = remainingInfoHtml.split("<br />");
			for (String info : remainingInfoes) {
				info = info.trim();

				if (recordType == PREREQUISTES) {
					if (prerequisitesString == null) {
						prerequisitesString = "";
					}
					prerequisitesString += " "
							+ info.replaceAll("\\<[^>]*>", "");
				}

				if (recordType == RESTRICTIONS) {
					if (restrictionsString == null) {
						restrictionsString = "";
					}
					restrictionsString += " "
							+ info.replace("&nbsp;", "").trim();
				}

				if (info.contains("Associated Term: ")) {
					String termString = info.substring(info.indexOf("</span>")
							+ "</span>".length());
					this.term = Term.valueOf(termString.replace(" ", "")
							.toUpperCase());
					continue;
				} else if (info.contains("Levels: ")) {
					String levelsString = info.substring(info
							.indexOf("</span>") + "</span>".length());
					this.levels = new ArrayList<String>(
							Arrays.asList(levelsString.split(", ")));
					continue;
				} else if (info.contains("Campus")) {
					String campusString = info.substring(0,
							info.indexOf("Campus")).trim();
					this.campus = campusString;
					continue;
				} else if (info.contains("Schedule Type")) {
					String typeString = info.substring(0,
							info.indexOf("Schedule Type")).trim();
					this.type = Type.valueOf(typeString.replace(" ", ""));
					continue;
				} else if (info.contains("Credits")) {
					String creditsString = info.substring(0,
							info.indexOf("Credits")).trim();
					this.credits = Double.valueOf(creditsString);
					continue;
				} else if (info.contains("Prerequisites:")) {
					recordType = PREREQUISTES;
					continue;
				} else if (info.contains("Restrictions:")) {
					recordType = RESTRICTIONS;
					continue;
				}
			}

			if (prerequisitesString != null) {
				this.prerequisites = prerequisitesString.trim();
			}

			if (restrictionsString != null) {
				this.restrictions = restrictionsString.trim();
			}
		}
	}
}
