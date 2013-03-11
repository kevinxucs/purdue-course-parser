/*
 * BasicHttpClientAsync.java
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

package net.kevxu.purdueassist.shared.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Simplest implementation of HttpClientAsync.
 * 
 * @author Kaiwen Xu (kevin)
 */
public class BasicHttpClientAsync implements HttpClientAsync {

	private String mUrl;
	private HttpMethod mMethod;
	private HttpRequestListener mListener;
	private FutureTask<?> mFutureTask;
	private ExecutorService mExecutor;

	private List<NameValuePair> mParameters = null;

	public interface HttpRequestListener {
		public void onRequestFinished(HttpResponse httpResponse);

		public void onRequestFinished(ClientProtocolException e);

		public void onRequestFinished(IOException e);
	}

	public BasicHttpClientAsync(String url, HttpMethod httpMethod,
			HttpRequestListener httpRequestListener) {
		this.mUrl = url;
		this.mMethod = httpMethod;
		this.mListener = httpRequestListener;
		this.mFutureTask = new FutureTask<Object>(httpTask, null);
		this.mExecutor = Executors.newSingleThreadExecutor();
	}

	private Runnable httpTask = new Runnable() {

		@Override
		public void run() {
			HttpClient httpClient = new DefaultHttpClient();
			switch (mMethod) {
			case GET: {
				HttpGet httpGet = new HttpGet(mUrl);
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					mListener.onRequestFinished(httpResponse);
				} catch (ClientProtocolException e) {
					mListener.onRequestFinished(e);
				} catch (IOException e) {
					mListener.onRequestFinished(e);
				} finally {
					mExecutor.shutdown();
				}
				break;
			}
			case POST: {
				HttpPost httpPost = new HttpPost(mUrl);
				if (mParameters != null) {
					try {
						httpPost.setEntity(new UrlEncodedFormEntity(mParameters));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				try {
					HttpResponse httpResponse = httpClient.execute(httpPost);
					mListener.onRequestFinished(httpResponse);
				} catch (ClientProtocolException e) {
					mListener.onRequestFinished(e);
				} catch (IOException e) {
					mListener.onRequestFinished(e);
				} finally {
					mExecutor.shutdown();
				}
				break;
			}
			}

		}
	};

	public void setParameters(List<NameValuePair> parameters)
			throws MethodNotPostException {
		if (mMethod != HttpMethod.POST) {
			throw new MethodNotPostException();
		} else {
			this.mParameters = parameters;
		}
	}

	@Override
	public void getResponse() {
		mExecutor.execute(mFutureTask);
	}

}
