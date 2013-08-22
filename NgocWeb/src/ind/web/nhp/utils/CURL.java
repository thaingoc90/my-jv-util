package ind.web.nhp.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class CURL {

	public static final int DEFAULT_MAX_CONNECTION_PER_ROUTE = 40;
	public static final int DEFAULT_MAX_CONNECTION = 10000;
	public static final int DEFAULT_CONNECTION_TIMEOUT = 20;
	public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;
	public static final int DEFAULT_RETRY_TIMES = 3;

	/**
	 * Sends POST request & get result.
	 * 
	 * @param uri
	 * @param entity
	 * @param connTimeout
	 * @param timeUnit
	 * @return
	 * @throws IOException
	 */
	public static String post(String uri, HttpEntity entity, long connTimeout, TimeUnit timeUnit)
	        throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		int timeout = (int) timeUnit.toMillis(connTimeout);
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpPost request = new HttpPost(uri);
		request.setEntity(entity);
		HttpResponse response = client.execute(request);
		HttpEntity responseEntity = response.getEntity();
		return EntityUtils.toString(responseEntity);
	}

	public static String post(String url, String data) throws IOException {
		return post(url, new StringEntity(data), DEFAULT_CONNECTION_TIMEOUT, DEFAULT_TIMEUNIT);
	}

	/**
	 * Sends GET request & get result.
	 * 
	 * @param uri
	 * @param entity
	 * @param connTimeout
	 * @param timeUnit
	 * @return
	 * @throws IOException
	 */
	public static String get(String uri, long connTimeout, TimeUnit timeUnit) throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		int timeout = (int) timeUnit.toMillis(connTimeout);
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		HttpEntity responseEntity = response.getEntity();
		return EntityUtils.toString(responseEntity);
	}

	public static String get(String url) throws IOException {
		return get(url, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_TIMEUNIT);
	}

	public static String get(String url, Map<String, Object> params) throws IOException {
		StringBuilder str = new StringBuilder(url);
		if (params != null && params.size() > 0) {
			str.append(str.indexOf("?") == -1 ? "?" : "&");
			for (Entry<String, Object> entry : params.entrySet()) {
				str.append(entry.getKey());
				str.append("=");
				str.append(entry.getValue());
				str.append("&");
			}
			str.setLength(str.length() - 1);
		}
		return get(url, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_TIMEUNIT);
	}
}
