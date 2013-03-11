/*
 * SceduleSearch.java
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
import java.util.ArrayList;
import java.util.List;

import net.kevxu.purdueassist.course.elements.Predefined.Subject;
import net.kevxu.purdueassist.course.elements.Predefined.Term;
import net.kevxu.purdueassist.course.elements.Predefined.Type;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HtmlParseException;
import net.kevxu.purdueassist.course.shared.RequestNotFinishedException;
import net.kevxu.purdueassist.shared.httpclient.BasicHttpClientAsync;
import net.kevxu.purdueassist.shared.httpclient.BasicHttpClientAsync.HttpRequestListener;
import net.kevxu.purdueassist.shared.httpclient.HttpClientAsync.HttpMethod;
import net.kevxu.purdueassist.shared.httpclient.MethodNotPostException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

public class ScheduleSearch implements HttpRequestListener {

	private static final String URL_HEAD = "https://selfservice.mypurdue.purdue.edu/prod/"
			+ "bzwsrch.p_search_schedule";

	private ScheduleSearchListener mListener;
	private BasicHttpClientAsync mHttpClient;

	private boolean requestFinished;

	public class ScheduleSearchConfig {
		public Term term = null;
		public Subject subject = null;
		public String cnbr = null;
		public String title = null;
		// TODO: find a better type for startTime, endTime, termPart, etc.
		public String startTime = null;
		public String endTime = null;
		public String days = null;
		public String termPart = null;
		public String campus = null;
		public Type scheduleType = null;
		public String session = null;
		public String instructor = null;
		public String attributes = null;
		public String level = null;
		public Double creditFrom = null;
		public Double creditTo = null;
		public Integer crn = null;

		private List<NameValuePair> getParameters() {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			if (term != null)
				parameters.add(new BasicNameValuePair("term", term
						.getLinkName()));
			// TODO: add more

			return parameters;
		}
	}

	public interface ScheduleSearchListener {
		public void onScheduleSearchFinished(ScheduleSearchEntry entry);

		public void onScheduleSearchFinished(IOException e);

		public void onScheduleSearchFinished(HtmlParseException e);

		public void onScheduleSearchFinished(CourseNotFoundException e);

		public void onScheduleSearchFinished(Exception e);
	}

	public ScheduleSearch(ScheduleSearchListener listener) {
		this.mListener = listener;
		this.requestFinished = true;
	}

	public void getResult(ScheduleSearchConfig config)
			throws RequestNotFinishedException {
		if (!this.requestFinished)
			throw new RequestNotFinishedException();

		this.requestFinished = false;

		mHttpClient = new BasicHttpClientAsync(URL_HEAD, HttpMethod.POST, this);
		try {
			mHttpClient.setParameters(config.getParameters());
			mHttpClient.getResponse();
		} catch (MethodNotPostException e) {
			e.printStackTrace();
		} finally {
			this.requestFinished = true;
		}
	}

	/**
	 * Check whether previous request has been finished.
	 * 
	 * @return Return true if previous request has already finished.
	 */
	public boolean isRequestFinished() {
		return this.requestFinished;
	}

	@Override
	public void onRequestFinished(HttpResponse httpResponse) {
		try {

		} finally {
			this.requestFinished = true;
		}
	}

	@Override
	public void onRequestFinished(ClientProtocolException e) {
		this.requestFinished = true;
	}

	@Override
	public void onRequestFinished(IOException e) {
		mListener.onScheduleSearchFinished(e);
		this.requestFinished = true;
	}

	public class ScheduleSearchEntry {

	}

}
