package com.yinweilong.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

public class AcceptHeaderHttpRequestInterceptor implements ClientHttpRequestInterceptor {
	private final String headerValue;

	public AcceptHeaderHttpRequestInterceptor(String headerValue) {
		this.headerValue = headerValue;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

		HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
		List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.valueOf(headerValue));
		requestWrapper.getHeaders().setAccept(acceptableMediaTypes);
		return execution.execute(requestWrapper, body);
	}
}
