/*
 * CatalogDetail.java
 * 
 * Written by Kaiwen Xu (kevin).
 * Released under Apache License 2.0.
 */

package net.kevxu.purdueassist.course;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.kevxu.purdueassist.course.elements.Predefined.Subject;
import net.kevxu.purdueassist.course.elements.Predefined.Term;
import net.kevxu.purdueassist.course.shared.CourseNotFoundException;
import net.kevxu.purdueassist.course.shared.HttpParseException;
import net.kevxu.purdueassist.shared.httpclient.BasicHttpClientAsync;
import net.kevxu.purdueassist.shared.httpclient.BasicHttpClientAsync.OnRequestFinishedListener;
import net.kevxu.purdueassist.shared.httpclient.HttpClientAsync.HttpMethod;
import net.kevxu.purdueassist.shared.httpclient.MethodNotPostException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CatalogDetail implements OnRequestFinishedListener {

	private static final String URL_HEAD = "https://selfservice.mypurdue.purdue.edu/prod/"
			+ "bzwsrch.p_catalog_detail";

	private Term term;
	private Subject subject;
	private int cnbr;

	private OnCatalogDetailFinishedListener mListener;
	private BasicHttpClientAsync httpClient;

	public interface OnCatalogDetailFinishedListener {
		public void onCatalogDetailFinished(CatalogDetailEntry entry);

		public void onCatalogDetailFinished(IOException e);

		public void onCatalogDetailFinished(HttpParseException e);

		public void onCatalogDetailFinished(CourseNotFoundException e);
	}

	public CatalogDetail(Subject subject, int cnbr,
			OnCatalogDetailFinishedListener onCatalogDetailFinishedListener) {
		this(Term.CURRENT, subject, cnbr, onCatalogDetailFinishedListener);
	}

	public CatalogDetail(Term term, Subject subject, int cnbr,
			OnCatalogDetailFinishedListener onCatalogDetailFinishedListener) {
		if (term != null)
			this.term = term;
		else
			this.term = Term.CURRENT;

		this.subject = subject;
		this.cnbr = cnbr;
		this.mListener = onCatalogDetailFinishedListener;
	}

	public void getResult() {
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
			CatalogDetailEntry entry = parseDocument(document);
			mListener.onCatalogDetailFinished(entry);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			mListener.onCatalogDetailFinished(e);
		} catch (HttpParseException e) {
			mListener.onCatalogDetailFinished(e);
		} catch (CourseNotFoundException e) {
			mListener.onCatalogDetailFinished(e);
		}
	}

	@Override
	public void onRequestFinished(ClientProtocolException e) {
		e.printStackTrace();
	}

	@Override
	public void onRequestFinished(IOException e) {
		mListener.onCatalogDetailFinished(e);
	}

	private CatalogDetailEntry parseDocument(Document document)
			throws HttpParseException, CourseNotFoundException {
		CatalogDetailEntry entry = new CatalogDetailEntry(subject, cnbr);

		return entry;
	}

	public class CatalogDetailEntry {
		private Subject searchSubject;
		private int searchCnbr;

		public CatalogDetailEntry(Subject subject, int cnbr) {
			this.searchSubject = subject;
			this.searchCnbr = cnbr;
		}

		private Subject subject;
		private int cnbr;
		private String name;
		private String description;
		private List<String> levels;

	}

}
