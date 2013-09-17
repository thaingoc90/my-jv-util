package ind.web.nhp.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.jdbc.JDBCAppender;
import org.apache.log4j.spi.LoggingEvent;

public class MyJDBCAppender extends JDBCAppender {

	@Override
	protected String getLogStatement(LoggingEvent event) {

		LoggingEvent clone = new LoggingEvent(event.fqnOfCategoryClass, LogManager.getLogger(event
				.getLoggerName()), event.getLevel(), sqlEscape(event.getMessage().toString()),
				event.getThrowableInformation() != null ? event.getThrowableInformation()
						.getThrowable() : null);

		return getLayout().format(clone);
	}

	private static String sqlEscape(String sql) {
		if (sql == null) {
			return sql;
		}
		return sql.replace("'", "\\'");
	}

	public static void main(String[] args) {
		System.out.println(sqlEscape("abc'fds'"));
	}
}
