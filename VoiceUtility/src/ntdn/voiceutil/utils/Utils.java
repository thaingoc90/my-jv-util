package ntdn.voiceutil.utils;

import java.io.File;

import android.app.Activity;
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
}
