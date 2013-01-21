package net.kevxu.purdueassist.shared.httpclient;

public interface HttpClientAsync {

	public enum HttpMethod {
		GET, POST
	};

	public void getResponse();

}
