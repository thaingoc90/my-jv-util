package ind.web.nhp.interceptor;

import ind.web.nhp.base.Constants;
import ind.web.nhp.controller.be.BaseBackendController;
import ind.web.nhp.utils.UrlUtils;
import ind.web.nhp.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoginAuthentication extends HandlerInterceptorAdapter {

	@Autowired
	private UrlUtils urlUtils;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
	        Object handlerObj) throws Exception {
		Map<String, Object> varParams = new HashMap<String, Object>();
		varParams.put("url", request.getServletPath());
		String[] pathParams = new String[] { "admin", "login" };
		String urlLogin = urlUtils.createUrl(request, response, pathParams, varParams, null);
		HandlerMethod handler = (HandlerMethod) handlerObj;
		if (handler.getBean() instanceof BaseBackendController) {
			BaseBackendController controller = (BaseBackendController) handler.getBean();
			if (controller.isLoginAuthentication()) {
				HttpSession session = request.getSession(false);
				if (session == null || session.getAttribute(Constants.NHP_USER_ID) == null) {
					Cookie[] listCookies = request.getCookies();
					if (checkCookie(listCookies, Constants.NHP_USER_ID)) {
						String cookVal = getValueFromCookie(listCookies, Constants.NHP_USER_ID);
						if (!StringUtils.isEmpty(cookVal)) {
							session = session == null ? request.getSession(true) : session;
							session.setAttribute(Constants.NHP_USER_ID, Utils.toInt(cookVal));
							return super.preHandle(request, response, handler);
						}
					}
					response.sendRedirect(urlLogin);
					return false;
				}
			}
		}
		return super.preHandle(request, response, handler);
	}

	private boolean checkCookie(Cookie[] listCookies, String key) {
		if (listCookies != null) {
			for (Cookie cookie : listCookies) {
				if (key.equals(cookie.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private String getValueFromCookie(Cookie[] listCookies, String key) {
		if (listCookies != null) {
			for (Cookie cookie : listCookies) {
				if (key.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
