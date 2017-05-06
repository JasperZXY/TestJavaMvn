package com.jasper.mvntest.apache.http;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * 对应引入的包commons-httpclient.commons-httpclient
 * 
 * 
 * @author zxy
 *
 */
public class CommonsHttpUtils {
	public static String httpGet(String url) {
		return httpGet(url, null);
	}
	
	public static String httpGet(String url, Map<String, Object> param) {
		HttpClient httpClient = new HttpClient();
		GetMethod method = new GetMethod(url);
		
		if (param != null && param.size() > 0) {
			NameValuePair[] pairs = new NameValuePair[param.size()];
			int i = 0;
			for (Map.Entry<String, Object> entry : param.entrySet()) {
				pairs[i] = new NameValuePair();
				pairs[i].setName(entry.getKey());
				pairs[i].setValue(entry.getValue().toString());
				i ++;
			}
			method.setQueryString(pairs);
		}
		
		try {
			int status = httpClient.executeMethod(method);
			if (status == 200) {
				return method.getResponseBodyAsString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		
		return null;
	}

}
