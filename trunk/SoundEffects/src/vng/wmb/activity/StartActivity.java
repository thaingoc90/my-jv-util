package vng.wmb.activity;

import vng.wmb.service.AudioService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends Activity {

	static {
		System.loadLibrary("soundTouch");
		System.loadLibrary("soundEffect");
	}

	private Button recordBtn, stopBtn;
	private TextView messageRecord;
	public static boolean isRecording = false;
	public static AudioService audioServices;
	private Handler mHandler;
	private static final int TIME_RECORD = 10000; // ms

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
				Intent intent = new Intent(getApplicationContext(),
						EffectActivity.class);
				startActivity(intent);
//				audioServices.startRecord();
//				isRecording = true;
//				mHandler.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						stopRecording();
//					}
//				}, TIME_RECORD);
//				checkInterface();
			}
		});

		stopBtn = (Button) findViewById(R.id.btn_stop_record);
		stopBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("JAVA_JNI_SE", "StopActivity");
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
		Log.i("JNI_VE", "destroy");
		audioServices.destroy();
		super.onDestroy();
	}

}
