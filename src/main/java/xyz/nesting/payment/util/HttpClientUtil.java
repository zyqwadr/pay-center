package xyz.nesting.payment.util;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import xyz.nesting.common.web.ZcHttpKit;

public class HttpClientUtil {
	
	
	protected static ZcHttpKit httpKit = ZcHttpKit.getInstance();
	
	public static String get(String uri) {
		HttpGet httpGet = new HttpGet(uri);
		return call_http(httpGet);
	}
	
	public static String call_http(HttpRequestBase requestBase) {
		String resp = httpKit._do(requestBase);
		return resp;
	}

}
