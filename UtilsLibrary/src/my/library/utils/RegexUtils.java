package my.library.utils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
	/**
	 * Escapes a regular expression replacement string.
	 * 
	 * @param str
	 *            String
	 * @return String
	 */
	public static String regexpReplacementEscape(String str, String regex, String replacement) {
		return regexpReplacementEscape(str, regex, replacement, false);
	}

	/**
	 * Escapes all regular expression replacement string.
	 * 
	 * @param str
	 *            String
	 * @param preserveNull
	 *            boolean indicates that returned value can be null or not
	 * @return String
	 */
	public static String regexpReplacementEscape(String str, String regex, String replacement,
			boolean preserveNull) {
		return str != null ? str.replaceAll(regex, replacement) : (preserveNull ? null : "");
	}

	/**
	 * Extracts all parameters from a string and preserves theirs index.
	 * 
	 * @param src
	 * @return
	 */
	public static String[] extractParams(String src, String patternFormat) {
		List<String> result = new LinkedList<String>();
		Pattern pattern = Pattern.compile(patternFormat);
		Matcher matcher = pattern.matcher(src);
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result.toArray(new String[0]);
	}

	/**
	 * Extracts parameters from a string and replaces theirs index with value.
	 * 
	 * @param src
	 * @return
	 */
	public static String replaceRegexToValue(String src, String patternFormat,
			Map<String, Object> params) throws IllegalArgumentException {
		Pattern pattern = Pattern.compile(patternFormat);
		Matcher matcher = pattern.matcher(src);
		while (matcher.find()) {
			String param = matcher.group(1);
			if (params.containsKey(param)) {
				src = matcher.replaceFirst(params.get(param).toString());
				matcher = pattern.matcher(src);
			} else {
				throw new IllegalArgumentException("Missing value for param " + param);
			}
		}
		return src;
	}

	/**
	 * Formats an object with method.(method must be static)
	 * 
	 * @param value
	 *            T
	 * @param formatMethodName
	 *            String
	 * @param classNameContainsMethod
	 *            Class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T, C> T formatObjectWithMethod(T value, String formatMethodName,
			Class<C> classNameContainsMethod) {
		try {
			Method method = classNameContainsMethod.getMethod(formatMethodName, value.getClass());
			if (method != null) {
				method.setAccessible(true);
				Object resultMethod = method.invoke(classNameContainsMethod, value);
				value = (T) resultMethod;
			}
			return value;
		} catch (Exception e) {
			return value;
		}
	}

	/**
	 * Gets value of key Map, and format it with method.(method must be static)
	 * 
	 * @param results
	 *            Map
	 * @param key
	 *            String
	 * @param formatMethodName
	 *            String
	 * @param classNameContainsMethod
	 *            Class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> void formatValueOfMap(Map<String, ?> results, String key,
			String formatMethodName, Class<T> classNameContainsMethod) {
		if (results == null) {
			return;
		}
		Object value = results.get(key);
		value = formatObjectWithMethod(value, formatMethodName, classNameContainsMethod);
		((Map<String, Object>) results).put(key, value);
	}

	/**
	 * Gets value of key Map, and format it with method.(method must be static)
	 * 
	 * @param results
	 *            Map
	 * @param key
	 *            String
	 * @param formatMethodName
	 *            String
	 * @param classNameContainsMethod
	 *            Class
	 * @return
	 */
	public static <T, C> void formatValueOfArray(T[] results, String formatMethodName,
			Class<C> classNameContainsMethod) {
		if (results == null) {
			return;
		}
		for (int i = 0; i < results.length; i++) {
			T value = results[i];
			results[i] = formatObjectWithMethod(value, formatMethodName, classNameContainsMethod);
		}
	}
}
