package ind.web.nhp.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlProperties extends Properties {

	private static final long serialVersionUID = 1L;
	private int rendering = 0;
	private final static Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)\\}"); // ${}

	public SqlProperties() {}

	public SqlProperties(Properties initProperties) {
		setProperties(initProperties);
	}

	public void setProperties(Properties properties) {
		this.clear();
		putAll(properties);
	}

	@Override
	synchronized public Object put(Object key, Object value) {
		if (key instanceof String && value instanceof String) {
			Object result = super.put(key, value);
			if (rendering == 0) {
				renderProperty((String) key, (String) value);
			}
			return result;
		}
		return null;
	}

	@Override
	synchronized public void putAll(Map<? extends Object, ? extends Object> t) {
		if (t == null) {
			return;
		}
		try {
			rendering++;
			super.putAll(t);
		} finally {
			rendering--;
		}
		renderProperties();
	}

	/**
	 * Renders all properties.
	 */
	synchronized protected void renderProperties() {
		try {
			rendering++;
			for (Iterator<Object> i = this.keySet().iterator(); i.hasNext();) {
				Object key = i.next();
				if (key instanceof String) {
					renderProperty((String) key);
				}
			}
		} finally {
			rendering--;
		}
	}

	/**
	 * Renders a specified property.
	 */
	synchronized protected void renderProperty(String key) {
		String value = getProperty(key);
		renderProperty(key, value);
	}

	/**
	 * A property just only render once time.
	 */
	private Map<String, Object> isRendering = new HashMap<String, Object>();

	/**
	 * Replace strings match PATTERN by value of properties.
	 * 
	 * @param key
	 * @param value
	 */
	synchronized protected void renderProperty(String key, String value) {
		if (key == null || value == null)
			return;

		if (isRendering.containsKey(key))
			throw new IllegalStateException("Circular referencing detected!");

		try {
			rendering++;
			Matcher m = PATTERN.matcher(value);
			StringBuffer sb = new StringBuffer();
			boolean render = false;
			while (m.find()) {
				if (!render) {
					isRendering.put(key, value);
					render = true;
				}
				String replacement = replace(m.group(1));
				m.appendReplacement(sb, RegexUtils.regexReplacementEscape(replacement));
			}
			m.appendTail(sb);
			super.put(key, sb.toString().trim());
		} finally {
			rendering--;
			isRendering.remove(key);
		}
	}

	private String replace(String pattern) {
		if (this.containsKey(pattern)) {
			this.renderProperty(pattern);
			return this.getProperty(pattern);
		}

		String value = System.getProperty(pattern);
		if (value != null)
			return value;

		return "";
	}

}
