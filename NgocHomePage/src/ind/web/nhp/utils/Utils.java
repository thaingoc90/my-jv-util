package ind.web.nhp.utils;

import org.apache.commons.validator.routines.EmailValidator;

public class Utils {

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
