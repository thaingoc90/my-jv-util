package ind.web.nhp.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Utils {

	/**
	 * Gets the current http request.
	 * 
	 * @return
	 */
	public static HttpServletRequest getHttpRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
		        .getRequest();
	}

	/**
	 * Gets the http session instance.
	 * 
	 * @param forceCreate
	 * @return
	 */
	public static HttpSession getHttpSession(boolean forceCreate) {
		return getHttpRequest().getSession(forceCreate);
	}

	/**
	 * Gets a session a attribute.
	 * 
	 * @param session
	 * @param name
	 * @param value
	 */
	public static Object getSessionAttribute(boolean sessionForceCreate, String name) {
		HttpSession session = getHttpSession(sessionForceCreate);
		if (session == null) {
			return null;
		}
		return session.getAttribute(name);
	}

	public static boolean isValidEmail(String email) {
		return EmailValidator.getInstance().isValid(email);
	}

	public static int toInt(Object obj) {
		return toInt(obj, 0);
	}

	public static Integer toInt(Object obj, Integer defaultVal) {
		if (obj == null) {
			return defaultVal;
		}
		Integer result = defaultVal;
		try {
			result = Integer.parseInt(obj.toString().trim());
		} catch (NumberFormatException e) {
		}
		return result;
	}
}
