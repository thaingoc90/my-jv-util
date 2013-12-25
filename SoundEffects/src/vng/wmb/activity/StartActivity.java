package vng.wmb.activity;

import vng.wmb.service.AudioService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends Activity {

	static {
		System.loadLibrary("soundEffect");
	}

	private Button recordBtn, stopBtn;
	private TextView messageRecord;
	public static boolean isRecording = false;
	public static AudioService audioServices;
	private Handler mHandler;
	private static final int TIME_RECORD = 20000; // ms
	private Runnable stopThreads = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		audioServices = new AudioService();
		audioServices.init();

		mHandler = new Handler();

		recordBtn = (Button) findViewById(R.id.btn_record);
		recordBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				audioServices.startRecord();
				isRecording = true;
				stopThreads = new Runnable() {
					@Override
					public void run() {
						stopRecording();
					}
				};
				mHandler.postDelayed(stopThreads, TIME_RECORD);
				checkInterface();
			}
		});

		stopBtn = (Button) findViewById(R.id.btn_stop_record);
		stopBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				stopRecording();
			}
		});

		messageRecord = (TextView) findViewById(R.id.text_recording);

	}

	private synchronized void stopRecording() {
		if (isRecording) {
			audioServices.stopRecord();
			isRecording = false;
			Intent intent = new Intent(getApplicationContext(),
					EffectActivity.class);
			startActivity(intent);
			if (stopThreads != null) {
				mHandler.removeCallbacks(stopThreads);
				stopThreads = null;
			}
		}
	}

	private void checkInterface() {
		if (isRecording) {
			messageRecord.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.VISIBLE);
			recordBtn.setVisibility(View.INVISIBLE);
		} else {
			messageRecord.setVisibility(View.INVISIBLE);
			stopBtn.setVisibility(View.INVISIBLE);
			recordBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		checkInterface();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		audioServices.destroy();
		super.onDestroy();
	}

}
