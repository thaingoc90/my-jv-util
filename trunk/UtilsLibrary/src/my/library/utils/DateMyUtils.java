package my.library.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class DateMyUtils extends DateUtils {

	public static String toString(Date date, String pattern) {
		DateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static int intervalOfTwoDates(Date fromDate, Date toDate) {
		int result;
		Long distance = toDate.getTime() - fromDate.getTime();
		if (distance == 0) {
			result = 0;
		} else {
			result = (int) (distance / (24 * 60 * 60 * 1000));
		}
		return result;
	}
	
}
