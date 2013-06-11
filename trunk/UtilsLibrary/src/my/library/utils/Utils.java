package my.library.utils;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class Utils {
	/**
	 * Looks up a value from a map, with fall-back support.
	 * 
	 * @param props
	 *            Map<?, ?>
	 * @param lookupKeys
	 * @return Object
	 */
	private static Object getValue(Map<?, ?> props, String[] lookupKeys) {
		for (String key : lookupKeys) {
			Object value = props.get(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Looks up a value from a map, generic version.
	 * 
	 * @param <T>
	 * @param props
	 *            Map
	 * @param key
	 *            String
	 * @param clazz
	 *            Class
	 * @return T
	 */
	public static <T> T getValue(Map<?, ?> props, String key, Class<T> clazz) {
		return getValue(props, new String[] { key }, clazz);
	}
	
	/**
	 * Looks up a value from a map, with fall-back support, generic version.
	 * 
	 * @param <T>
	 * @param props
	 *            Map
	 * @param lookupKeys
	 *            String[]
	 * @param clazz
	 *            Class
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Map<?, ?> props, String[] lookupKeys,
			Class<T> clazz) {
		Object value = getValue(props, lookupKeys);
		if (value != null) {
			if (!clazz.isAssignableFrom(value.getClass())) {
				if (clazz == String.class) {
					value = value.toString();
				} else if (clazz == Short.class || clazz == short.class
						|| clazz == Integer.class || clazz == int.class) {
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
				} else if (clazz == Float.class || clazz == float.class
						|| clazz == Double.class || clazz == double.class) {
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
		// no thing I can do, return null!
		return null;
	}
	
	/**
	 * Converts a string to boolean value.
	 * 
	 * @param str
	 *            String
	 * @return boolean
	 */
	public static boolean strToBoolean(String str) {
		if (str == null) {
			return false;
		}
		if (str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("true")
				|| str.equalsIgnoreCase("y") || str.equalsIgnoreCase("t")) {
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
	 * Create a Document from literal XML string.
	 * 
	 * @param xml
	 *            the literal XML formated string
	 * @return Document represent given XML string
	 */
	public static Document loadXmlFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml.getBytes()));
	}

	/**
	 * Join a list of Object with specified separator.
	 * 
	 * @param separator
	 *            the separator to insert between each element
	 * @param list
	 *            list of object to join
	 * @return a string contain all string representation of objects in given
	 *         list in given order, separated by given separator
	 */
	public static String stringJoin(Object separator, List<?> list) {
		if (list.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		sb.append(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			sb.append(separator);
			sb.append(list.get(i));
		}
		return sb.toString();
	}
}
