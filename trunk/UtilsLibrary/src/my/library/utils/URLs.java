package my.library.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * httpcore-4.1.4.jar, httpclient-4.1.3.jar, httpmime-4.1.3.jar
 * 
 * @author ngoctdn
 * 
 */
public class URLs {

	public static final int DEFAULT_MAX_CONNECTION_PER_ROUTE = 40;
	public static final int DEFAULT_MAX_CONNECTION = 10000;
	public static final int DEFAULT_CONNECTION_TIMEOUT = 20; // in seconds
	public static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.SECONDS;
	public static final int DEFAULT_RETRY_TIMES = 3;

	public static final ThreadSafeClientConnManager DEFAULT_CONN_MANAGER;
	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		DEFAULT_CONN_MANAGER = new ThreadSafeClientConnManager(schemeRegistry);
		DEFAULT_CONN_MANAGER.setMaxTotal(DEFAULT_MAX_CONNECTION);
		DEFAULT_CONN_MANAGER.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTION_PER_ROUTE);
	}

	/**
	 * Send GET request to given URI and return the response's body as String.
	 * 
	 * @param uri
	 *            the destination HTTP URI to send request
	 * @return response's body
	 * @throws IOException
	 */
	public static String get(String uri) throws IOException {
		return URLs.get(uri, DEFAULT_CONN_MANAGER, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_TIMEUNIT);
	}

	public static String get(String scheme, String host, int port, String path,
			List<NameValuePair> params, ClientConnectionManager connManager, long connTimeout,
			TimeUnit timeUnit) throws URISyntaxException, IOException {
		String query = URLEncodedUtils.format(params, "UTF-8");
		URI uri = URIUtils.createURI(scheme, host, port, path, query, null);
		return URLs.get(uri.toString(), connManager, connTimeout, timeUnit);
	}

	public static String get(String uri, ClientConnectionManager connManager, long connTimeout,
			TimeUnit timeUnit) throws IOException {
		HttpClient client = new DefaultHttpClient(connManager);
		HttpParams params = client.getParams();
		int timeout = (int) timeUnit.toMillis(connTimeout);
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);

		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity);
	}

	/**
	 * Send POST request to given URI with given data and return the response's body as String.
	 * 
	 * @param uri
	 *            the destination HTTP URI to send request
	 * @param data
	 *            the request body
	 * @return response's body
	 * @throws IOException
	 */
	public static String post(String uri, String data) throws IOException {
		return URLs.post(uri, new StringEntity(data), DEFAULT_CONN_MANAGER,
				DEFAULT_CONNECTION_TIMEOUT, DEFAULT_TIMEUNIT);
	}

	public static String post(String uri, Map<String, String> params) throws IOException {
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> param : params.entrySet()) {
			postParams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);
		return URLs.post(uri, entity, DEFAULT_CONN_MANAGER, DEFAULT_CONNECTION_TIMEOUT,
				DEFAULT_TIMEUNIT);
	}

	public static String post(String uri, HttpEntity entity, ClientConnectionManager connManager,
			long connTimeout, TimeUnit timeUnit) throws IOException {
		HttpClient client = new DefaultHttpClient(connManager);
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

	/**
	 * Send GET request to given host:port/path and return the raw response from server.
	 * 
	 * @param host
	 * @param port
	 * @param path
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static String getRawResponse(String host, int port, String path)
			throws UnknownHostException, IOException {
		Socket socket = null;
		PrintWriter writer = null;
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			socket = new Socket(host, port);
			writer = new PrintWriter(socket.getOutputStream());
			writer.println("GET " + path + " HTTP/1.1");
			writer.println("Host: " + host);
			writer.println("Accept: */*");
			writer.println("User-Agent: Java");
			writer.println("");
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			for (String line; (line = reader.readLine()) != null;) {
				sb.append(line);
			}
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException logOrIgnore) {
				}
			if (writer != null) {
				writer.close();
			}
			if (socket != null)
				try {
					socket.close();
				} catch (IOException logOrIgnore) {
				}
		}
		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		String url = "http://download.vuimanga.vn/images/thumb/manga/200x200/image_1[1]_1359016632.jpg";
		System.out.println(getEncodeUrl(url));
		System.out.println(get(getEncodeUrl(url)));
	}

	public static String getEncodeUrl(String url) {
		if (url == null || url.length() == 0)
			return "";

		int doubleslash = url.indexOf("//");
		doubleslash = (doubleslash == -1) ? 0 : doubleslash + 2;
		String protocol = url.substring(0, doubleslash);
		url = url.substring(doubleslash);

		int slash = url.indexOf('/', doubleslash);
		slash = (slash == -1) ? url.length() : slash + 1;
		String host = protocol + url.substring(0, slash);
		String params = url.substring(slash);
		try {
			return host + URLEncoder.encode(params, "UTF-8");
		} catch (Exception e) {
			return protocol + url;
		}
	}
}
