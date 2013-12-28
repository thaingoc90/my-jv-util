package vng.wmb.activity;

import vng.wmb.service.AudioService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
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
	CountDownTimer cdt;
	private static final String LOG_TAG = "StartActivity";
	private static final String message = "Recording ....";

	// FOR SOUND WAVE
	private WaveDrawer.DrawThread mDrawThread;
	private WaveDrawer mdrawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		audioServices = new AudioService(this);
		audioServices.init();

		mHandler = new Handler();

		// button record
		recordBtn = (Button) findViewById(R.id.btn_record);
		recordBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startRecording();
			}
		});

		// button stop
		stopBtn = (Button) findViewById(R.id.btn_stop_record);
		stopBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopRecording();
			}
		});

		// message to count time.
		messageRecord = (TextView) findViewById(R.id.text_recording);

		// for soundwave
		mdrawer = (WaveDrawer) findViewById(R.id.drawer);
	}

	@Override
	protected void onResume() {
		Log.i(LOG_TAG, "onResume");
		checkInterface();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(LOG_TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		Log.i(LOG_TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		audioServices.destroy();
		super.onDestroy();
	}

	/**
	 * Start recording & stop when press button 'stop' or after TIME_RECORD.
	 */
	private void startRecording() {
		audioServices.startRecord();
		mDrawThread = mdrawer.getThread();
		startCountTimer();
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

	/**
	 * Stop recording & start EffectActivity.
	 */
	private synchronized void stopRecording() {
		if (isRecording) {
			audioServices.stopRecord();
			isRecording = false;
			Intent intent = new Intent(getApplicationContext(),
					EffectActivity.class);
			startActivity(intent);
			if (cdt != null) {
				cdt.cancel();
			}
			if (stopThreads != null) {
				mHandler.removeCallbacks(stopThreads);
				stopThreads = null;
			}
			mDrawThread = mdrawer.getThread();
			mDrawThread.setRun(false);
			mDrawThread.SetSleeping(true);
		}
	}

	/**
	 * CHECK & SHOW INTERFACE OF START ACTIVITY.
	 */
	private void checkInterface() {
		if (isRecording) {
			messageRecord.setVisibility(View.VISIBLE);
			stopBtn.setVisibility(View.VISIBLE);
			recordBtn.setVisibility(View.INVISIBLE);
			mdrawer.setVisibility(View.VISIBLE);
		} else {
			messageRecord.setVisibility(View.INVISIBLE);
			stopBtn.setVisibility(View.INVISIBLE);
			recordBtn.setVisibility(View.VISIBLE);
			mdrawer.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Count time of record.
	 */
	private void startCountTimer() {
		cdt = new CountDownTimer(TIME_RECORD, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				messageRecord.setText(message
						+ ((TIME_RECORD - millisUntilFinished) / 1000 + 1)
						+ "s");
			}

			@Override
			public void onFinish() {
				messageRecord.setText(message + TIME_RECORD / 1000 + "s");
			}
		};
		cdt.start();
	}

	/**
	 * Recives the buffer from the sampler
	 * 
	 * @param buffer
	 */
	public void setBuffer(short[] paramArrayOfShort) {
		mDrawThread = mdrawer.getThread();
		mDrawThread.setBuffer(paramArrayOfShort);
	}

}
