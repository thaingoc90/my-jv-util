package my.library.utils;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * jackson-mapper-lgpl-1.8.2.jar, jackson-core-lgpl-1.8.2.jar, jackson-databind-2.0.4.jar
 * 
 * @author ngoctdn
 * 
 */
public class JsonUtils {
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Converts a Java object to JSON string.
	 * 
	 * @param obj
	 *            Object
	 * @return String
	 * @throws IllegalArgumentException
	 *             if the object can not be converted
	 */
	public static String toJson(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Converts a JSON string to a Java object
	 * 
	 * @param json
	 *            String
	 * @return Object
	 * @throws IllegalArgumentException
	 *             if the JSON string can not be converted
	 */
	public static Object fromJson(String json) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		try {
			return mapper.readValue(json, Object.class);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Converts a JSON to a Java object with specified type.
	 * 
	 * @param <T>
	 * @param json
	 *            String
	 * @param clazz
	 *            Class<T>
	 * @return T
	 * @throws IllegalArgumentException
	 *             if the JSON string can not be converted as the given type
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		if (clazz == null)
			throw new IllegalArgumentException("The specified type is null");
		try {
			return mapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
