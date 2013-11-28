package com.voiceeffect.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class RecordActivity extends Activity {

	private static final String LOG_PREFIX = "RecordActivity";
	VoiceService voiceSer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_main);

		voiceSer = new VoiceService();
		voiceSer.init();
	}

	public void startRecord(View view) {
		Log.w(LOG_PREFIX, "Record");
		voiceSer.startRecord();
	}

	public void startPlay(View view) {
		Log.w(LOG_PREFIX, "Play");
		voiceSer.stopRecord();
	}

	@Override
	protected void onDestroy() {
		voiceSer.destroy();
		voiceSer = null;
		super.onDestroy();
	}

}
