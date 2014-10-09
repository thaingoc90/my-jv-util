package ntdn.voiceutil.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

public class Utils {

	public static void deleteFile(String fileName) {
		File file = new File(fileName);
		file.delete();
	}

	public static void showMsg(Activity atv, String msg) {
		Toast.makeText(atv.getApplicationContext(), msg, Toast.LENGTH_LONG)
				.show();
	}

	public static void showMsgSync(Activity atv, String msg) {
		atv.runOnUiThread(new ToastRunnable(atv, msg));
	}

	public static boolean strIsEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Get folder of app
	 * 
	 * @return
	 */
	public static String getAppFolder() {
		String folder = "";
		String rootStorage = Environment.getExternalStorageDirectory()
				.getPath();
		folder = rootStorage + File.separator + Constants.APP_FOLDER;
		File dir = new File(folder);
		if (!dir.exists()) {
			boolean status = dir.mkdir();
			if (!status) {
				folder = "";
			}
		}
		return folder;
	}

	/**
	 * Create fileName when saving
	 * 
	 * @param fileType
	 * @return
	 */
	public static String getFileName(int fileType) {
		String fileName = getAppFolder();
		if (strIsEmpty(fileName)) {
			return "";
		}
		Date cur = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		String timeStr = sdf.format(cur);
		fileName += File.separator + timeStr;
		switch (fileType) {
		case Constants.FILE_TYPE_WAV:
			fileName += Constants.WAV_EXT;
			break;
		case Constants.FILE_TYPE_MP3:
			fileName += Constants.MP3_EXT;
			break;
		case Constants.FILE_TYPE_AMR:
			fileName += Constants.AMR_EXT;
			break;
		default:
			return "";
		}

		return fileName;
	}

	/**
	 * Get temp file when recording
	 * 
	 * @return
	 */
	public static final String getFileTemp() {
		String fileName = getAppFolder();
		if (strIsEmpty(fileName)) {
			return "";
		}
		fileName += File.separator + Constants.RECORD_FILE_TEMP
				+ Constants.WAV_EXT;
		return fileName;
	}

	public static final String getLogFolder() {
		String logFolder = getAppFolder() + File.separator
				+ Constants.APP_LOG_FOLDER;
		File dir = new File(logFolder);
		if (!dir.exists()) {
			boolean status = dir.mkdir();
			if (!status) {
				logFolder = "";
			}
		}
		return logFolder;
	}

	static class ToastRunnable implements Runnable {
		private Activity mAtv;
		private String mMsg;

		public ToastRunnable(Activity atv, String msg) {
			this.mAtv = atv;
			this.mMsg = msg;
		}

		@Override
		public void run() {
			Toast.makeText(mAtv.getApplicationContext(), mMsg,
					Toast.LENGTH_LONG).show();
		}

	}
}
