package my.library.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.struts2.ServletActionContext;

/**
 * servlet-api-2.5.jar, commons-httpclient-3.0.1.jar, struts2-core-2.3.1.2.jar
 * 
 * @author ngoctdn
 *
 */
public class UrlUtils {

	public static void main(String[] args) {
		String url = "http://linux.die.net/man/8/dmidecode";
		String input = "";
		try {
			System.out.println(UrlUtils.getResult(url, input, "get"));
		} catch (RuntimeException re) {
			System.out.println(re.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Unknown Exception");
		}
	}

	/**
	 * Creates a URL with default settings.
	 * 
	 * @param actions
	 * @param params
	 * @return
	 */
	private static String suffix = ".action";

	public static String createUrl(String[] actions, Map<String, ?> params) {
		ServletContext servletContext = ServletActionContext.getServletContext();
		StringBuilder url = new StringBuilder(servletContext.getContextPath());
		for (String action : actions) {
			url.append("/").append(action);
		}
		url.append(suffix);

		if (params != null && params.size() > 0) {
			url.append("?");
			for (Entry<String, ?> entry : params.entrySet()) {
				url.append(entry.getKey());
				url.append("=");
				url.append(entry.getValue());
				url.append("&");
			}
			url.setLength(url.length() - 1);
		}

		HttpServletResponse servletResponse = ServletActionContext.getResponse();
		return servletResponse.encodeURL(url.toString());
	}

	/**
	 * gets current URL.
	 * 
	 * @param request
	 * @return
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
			return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		else
			return request.getScheme() + "://" + request.getServerName() + ":"
					+ request.getServerPort() + request.getContextPath();
	}

	/**
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	public static String executeByPostMethod(String url, Map<String, String> data) throws Exception {
		HttpClient client = new HttpClient();
		try {
			client.getHttpConnectionManager().getParams().setConnectionTimeout(8000);
			client.getHttpConnectionManager().getParams().setSoTimeout(8000);
			PostMethod method = new PostMethod(url);
			method.setRequestEntity(new StringRequestEntity(JsonUtils.toJson(data)));
			if (client.executeMethod(method) == HttpStatus.SC_OK) {
				String body = method.getResponseBodyAsString();
				return body;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * @param method
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> executeByGetMethod(String url, Map<String, String> data) {
		HttpClient client = new HttpClient();
		try {
			client.getHttpConnectionManager().getParams().setConnectionTimeout(8000);
			client.getHttpConnectionManager().getParams().setSoTimeout(8000);
			GetMethod method = new GetMethod(url);
			if (data != null && data.size() > 0) {
				NameValuePair[] params = new NameValuePair[data.size()];
				for (int i = 0; i < data.size(); i++) {
					String key = (String) data.keySet().toArray()[i];
					String value = data.get(key);
					params[i] = new NameValuePair(key, value);
				}
				method.setQueryString(params);
			}
			if (client.executeMethod(method) == HttpStatus.SC_OK) {
				String body = method.getResponseBodyAsString();
				return (Map<Object, Object>) JsonUtils.fromJson(body);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getResult(String urlString, String input, String type) throws Exception {
		int resCode, c;
		String result = "";
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type", "application/json");
		if (type.equalsIgnoreCase("post")) {
			conn.setRequestMethod("POST");
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
		} else {
			conn.setRequestMethod("GET");
		}
		try {
			resCode = conn.getResponseCode();
		} catch (IOException e) {
			throw new RuntimeException("Failed connection");
		}
		if (resCode != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		}
		Reader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		while ((c = br.read()) != -1) {
			result += (char) c;
		}
		conn.disconnect();
		return result;
	}
}
