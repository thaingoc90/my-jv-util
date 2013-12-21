package vng.wmb.util;

import android.app.Activity;
import android.widget.Toast;

public class Utils {

	public static void showMsg(Activity atv, String msg) {
		Toast.makeText(atv.getApplicationContext(), msg, Toast.LENGTH_LONG)
				.show();
	}

}
