package ind.web.nhp.base;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

public abstract class BaseBo {

	public abstract Map<String, Object[]> getFieldMap();

	public void populate(Map<String, Object> data) throws SQLException {
		Map<String, Object[]> fieldMap = getFieldMap();
		Class<?> myClass = getClass();
		for (Entry<String, Object[]> entry : fieldMap.entrySet()) {
			String dbCol = entry.getKey();
			Object[] boInfo = entry.getValue();
			String boAttr = boInfo[0].toString();
			Class<?> boAttrType = (Class<?>) boInfo[1];
			Object boValue = data.get(dbCol);
			if (boAttrType == boolean.class || boAttrType == Boolean.class) {
				boValue = toBoolean(boValue);
			}
			Object value = changeType(boValue, boAttrType);
			String methodName = "set" + WordUtils.capitalize(boAttr);
			try {
				Method method = myClass.getDeclaredMethod(methodName,
						boAttrType);
				if (method != null) {
					method.setAccessible(true);
					method.invoke(this, value);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected Boolean toBoolean(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			return ((Number) value).doubleValue() != 0.0;
		}
		if (value instanceof Character) {
			char c = ((Character) value).charValue();
			return c == 'Y' || c == 'y' || c == 'T' || c == 't' || c == '1';
		}
		if (value instanceof String) {
			return "YES".equalsIgnoreCase(value.toString())
					|| "Y".equalsIgnoreCase(value.toString())
					|| "TRUE".equalsIgnoreCase(value.toString())
					|| "T".equalsIgnoreCase(value.toString())
					|| "1".equalsIgnoreCase(value.toString());
		}
		return Boolean.parseBoolean(value.toString());
	}

	@SuppressWarnings("unchecked")
	public <T> T changeType(Object value, Class<T> clazz) {
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
					value = toBoolean(value);
				}
			}
			return (T) value;
		}
		return null;
	}
}
