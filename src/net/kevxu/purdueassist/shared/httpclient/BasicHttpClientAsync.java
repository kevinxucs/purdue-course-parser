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

public class BasicHttpClientAsync implements HttpClientAsync {

	private String mUrl;
	private HttpMethod mMethod;
	private OnRequestFinishedListener mListener;
	private FutureTask<?> mFutureTask;
	private ExecutorService mExecutor;

	private List<NameValuePair> mParameters = null;

	public interface OnRequestFinishedListener {
		public void onRequestFinished(HttpResponse httpResponse);

		public void onRequestFinished(ClientProtocolException e);

		public void onRequestFinished(IOException e);
	}

	public BasicHttpClientAsync(String url, HttpMethod httpMethod,
			OnRequestFinishedListener onRequestFinishedListener) {
		this.mUrl = url;
		this.mMethod = httpMethod;
		this.mListener = onRequestFinishedListener;
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
