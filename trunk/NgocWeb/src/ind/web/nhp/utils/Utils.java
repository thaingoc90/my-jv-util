package ind.web.nhp.utils;

import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Utils {

	public static final long MASK_TIMESTAMP_32 = 0xFFFFFFFFL; // 32bit
	public static final long MASK_TIMESTAMP_16 = 0xFFFFL; // 16bit
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

	/**
	 * Validates email pattern
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email) {
		return EmailValidator.getInstance().isValid(email);
	}

	/**
	 * Converts obj to int-type. If not, return 0.
	 * 
	 * @param obj
	 * @return
	 */
	public static int toInt(Object obj) {
		return toInt(obj, 0);
	}

	/**
	 * Converts obj to int-type. If not, return default value.
	 * 
	 * @param obj
	 * @param defaultVal
	 * @return
	 */
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

	/**
	 * Convert Object to String, if obj is null, return default value.
	 * 
	 * @param obj
	 * @param defaultVal
	 * @return
	 */
	public static String toString(Object obj, String defaultVal) {
		return obj != null ? obj.toString() : defaultVal;
	}

	/**
	 * Convert Object to String, if obj is empty, return default value.
	 * 
	 * @param obj
	 * @param defaultVal
	 * @return
	 */
	public static String toStringNotEmpty(Object obj, String defaultVal) {
		return obj != null && !StringUtils.isEmpty(obj.toString()) ? obj.toString() : defaultVal;
	}

	/**
	 * Gets value from map, generic version.
	 * 
	 * @param props
	 * @param lookupKey
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Map<?, ?> props, String key, Class<T> clazz) {
		Object value = props.get(key);
		if (value != null) {
			if (!clazz.isAssignableFrom(value.getClass())) {
				if (clazz == String.class) {
					value = value.toString();
				} else if (clazz == Short.class || clazz == short.class || clazz == Integer.class
						|| clazz == int.class) {
					try {
						value = Integer.parseInt(value.toString().trim());
					} catch (NumberFormatException e) {
						value = 0;
					}
				} else if (clazz == Long.class || clazz == long.class) {
					try {
						value = Long.parseLong(value.toString().trim());
					} catch (NumberFormatException e) {
						value = 0;
					}
				} else if (clazz == Float.class || clazz == float.class || clazz == Double.class
						|| clazz == double.class) {
					try {
						value = Double.parseDouble(value.toString().trim());
					} catch (NumberFormatException e) {
						value = 0.0;
					}
				} else if (clazz == Boolean.class || clazz == boolean.class) {
					value = strToBoolean(value.toString().trim());
				} else {
					value = null;
				}
			}
			return (T) value;
		}
		return null;
	}

	/**
	 * Get value (int type) from map. If not, return 0
	 * 
	 * @param props
	 * @param key
	 * @return
	 */
	public static int getInt(Map<?, ?> props, String key) {
		Integer value = getValue(props, key, Integer.class);
		return value == null ? 0 : value;
	}

	/**
	 * Get value (String type) from map.
	 * 
	 * @param props
	 * @param key
	 * @return
	 */
	public static String getString(Map<?, ?> props, String key) {
		Object value = props.get(key);
		return value != null ? value.toString() : null;
	}

	/**
	 * Converts a string to boolean value.
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	private static boolean strToBoolean(String str) {
		if (str == null) {
			return false;
		}
		if (str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("true")
				|| str.equalsIgnoreCase("y") || str.equalsIgnoreCase("t")
				|| str.equalsIgnoreCase("1")) {
			return true;
		}
		try {
			if (Double.parseDouble(str) != 0.0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Generates an unique 64bit-keys (type String)
	 * 
	 * @return
	 */
	public static String generateId64() {
		return Long.toHexString(generateLongId64());
	}

	/**
	 * Generates an unique 64bit-keys (type long)
	 * 
	 * @return
	 */
	public static long generateLongId64() {
		long timestamp = System.currentTimeMillis() - ORIGINAL_TIMESTAMP;
		timestamp = timestamp & MASK_TIMESTAMP_32;
		timestamp = timestamp << 32L;
		long randomInt = rd.nextInt();
		randomInt = randomInt & MASK_TIMESTAMP_32;
		long result = timestamp | randomInt;
		return result;
	}

	public static long generateLongId48() {
		long timestamp = System.currentTimeMillis() - ORIGINAL_TIMESTAMP;
		timestamp = timestamp & MASK_TIMESTAMP_32;
		timestamp = timestamp << 16L;
		long randomInt = rd.nextInt(Short.MAX_VALUE * 2);
		randomInt = randomInt & MASK_TIMESTAMP_16;
		long result = timestamp | randomInt;
		return result;
	}
	
}
