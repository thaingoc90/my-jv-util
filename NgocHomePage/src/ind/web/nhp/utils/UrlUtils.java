package ind.web.nhp.utils;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * 
 * @author ngoctdn
 * 
 */
public class UrlUtils implements ServletContextAware {

	public final static int DEFAULT_HTTP_PORT = 80;
	public final static int DEFAULT_HTTPS_PORT = 443;

	private HttpServletResponse httpResponse;
	private HttpServletRequest httpRequest;
	private ServletContext servletContext;
	private String urlSuffix;

	public String createUrl(String[] actions, Map<String, Object> urlParams) {
		return createUrl(httpRequest, httpResponse, actions, urlParams, urlSuffix);
	}

	/**
	 * Creates a URL with default settings.
	 * 
	 * @param actions
	 * @param params
	 * @return
	 */
	public String createUrl(HttpServletRequest request, HttpServletResponse response,
	        String[] pathParams, Map<String, Object> urlParams, String urlSuffix) {

		StringBuilder url = new StringBuilder();
		if (pathParams != null) {
			for (String param : pathParams) {
				url.append("/").append(param);
			}
		}
		if (!StringUtils.isBlank(urlSuffix)) {
			url.append(urlSuffix);
		}
		if (urlParams != null && urlParams.size() > 0) {
			url.append("?");
			for (Entry<String, Object> entry : urlParams.entrySet()) {
				url.append(entry.getKey());
				url.append("=");
				url.append(entry.getValue());
				url.append("&");
			}
			url.setLength(url.length() - 1);
		}

		if (request != null) {
			String baseUrl = getBaseUrl(request);
			url.insert(0, baseUrl);
		} else {
			url.insert(0, servletContext.getContextPath());
		}

		return response != null ? response.encodeURL(url.toString()) : url.toString();
	}

	/**
	 * gets base URL.
	 * 
	 * @param request
	 * @return
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		if ((request.getServerPort() == DEFAULT_HTTP_PORT)
		        || (request.getServerPort() == DEFAULT_HTTPS_PORT)) {
			return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		} else {
			return request.getScheme() + "://" + request.getServerName() + ":"
			        + request.getServerPort() + request.getContextPath();
		}
	}

	public HttpServletResponse getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(HttpServletResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	@Override
	public void setServletContext(ServletContext serContext) {
		this.servletContext = serContext;
	}

	protected ServletContext getServletContext() {
		return servletContext;
	}

	public String getUrlSuffix() {
		return urlSuffix;
	}

	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}

}
