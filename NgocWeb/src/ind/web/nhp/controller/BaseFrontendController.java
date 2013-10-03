package ind.web.nhp.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import ind.web.nhp.base.BaseController;
import ind.web.nhp.base.Constants;
import ind.web.nhp.us.IUsManager;
import ind.web.nhp.us.IUser;
import ind.web.nhp.utils.Utils;

public class BaseFrontendController extends BaseController {

	private boolean loginAuthentication = false;

	@Autowired
	protected IUsManager usManager;

	protected IUser getCurrentUser() {
		IUser user = null;
		Object userIdObj = Utils.getSessionAttribute(false, Constants.NHP_FE_USER_ID);
		if (userIdObj == null) {
			HttpServletRequest request = Utils.getHttpRequest();
			Cookie[] listCookies = request.getCookies();
			String userIdCookie = getValueFromCookie(listCookies, Constants.NHP_FE_USER_ID);
			if (!StringUtils.isEmpty(userIdCookie)) {
				HttpSession session = Utils.getHttpSession(false);
				if (session == null) {
					session = Utils.getHttpSession(true);
				}
				session.setAttribute(Constants.NHP_FE_USER_ID, Utils.toInt(userIdCookie));
				userIdObj = userIdCookie;
			}
		}
		int userId = Utils.toInt(userIdObj);
		if (userId != 0) {
			user = usManager.getUser(userId);
		}
		return user;
	}

	protected String getCurrentUsername() {
		IUser user = getCurrentUser();
		return (user != null) ? user.getLoginName() : null;
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

	public boolean isLoginAuthentication() {
		return loginAuthentication;
	}

	public void setLoginAuthentication(boolean loginAuthentication) {
		this.loginAuthentication = loginAuthentication;
	}

}
