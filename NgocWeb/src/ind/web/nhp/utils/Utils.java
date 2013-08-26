package ind.web.nhp.utils;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Utils {

	public static final long MASK_TIMESTAMP_32 = 0xFFFFFFFFL; // 32bit
	private static final long ORIGINAL_TIMESTAMP = 1356973200000L;
	private static Random rd = new Random(System.currentTimeMillis());

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
		} catch (NumberFormatException e) {}
		return result;
	}

	public static String toString(Object obj, String defaultVal) {
		return obj != null ? obj.toString() : defaultVal;
	}

	public static String toStringNotEmpty(Object obj, String defaultVal) {
		return obj != null && !StringUtils.isEmpty(obj.toString()) ? obj.toString() : defaultVal;
	}

	public static String generateId64() {
		long timestamp = System.currentTimeMillis() - ORIGINAL_TIMESTAMP;
		timestamp = timestamp & MASK_TIMESTAMP_32;
		timestamp = timestamp << 32L;
		long randomInt = rd.nextInt();
		randomInt = randomInt & MASK_TIMESTAMP_32;
		long result = timestamp | randomInt;
		return Long.toHexString(result);
	}

}
