package ntdn.voiceutil.utils;

public class Log {

	public static final int LOG_TYPE_LOGCAT = 1;
	public static final int LOG_TYPE_FILE = 2; // not yet implemented
	public static final int LOG_TYPE_BOTH = 3;

	private static int logLevel = AppSetting.logLevel;
	private static int logType = AppSetting.logType;

	public static int getLogLevel() {
		return logLevel;
	}

	public static void setLogLevel(int logLevel) {
		Log.logLevel = logLevel;
	}

	public static void v(String tag, String msg) {
		if (!AppSetting.isEnableLog || logLevel < 5) {
			return;
		}

		if (logType == LOG_TYPE_LOGCAT || logType == LOG_TYPE_BOTH) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (!AppSetting.isEnableLog || logLevel < 4) {
			return;
		}

		if (logType == LOG_TYPE_LOGCAT || logType == LOG_TYPE_BOTH) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {
		if (!AppSetting.isEnableLog || logLevel < 3) {
			return;
		}

		if (logType == LOG_TYPE_LOGCAT || logType == LOG_TYPE_BOTH) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (!AppSetting.isEnableLog || logLevel < 2) {
			return;
		}

		if (logType == LOG_TYPE_LOGCAT || logType == LOG_TYPE_BOTH) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (!AppSetting.isEnableLog || logLevel < 1) {
			return;
		}

		if (logType == LOG_TYPE_LOGCAT || logType == LOG_TYPE_BOTH) {
			android.util.Log.e(tag, msg);
		}
	}
}
