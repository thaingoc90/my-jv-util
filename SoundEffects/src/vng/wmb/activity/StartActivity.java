package vng.wmb.activity;

import vng.wmb.service.SoundEffect;
import vng.wmb.util.Utils;
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
	private Handler mHandler;
	private Runnable stopThreads = null;
	private boolean isRecording = false;
	private static final String LOG_TAG = "StartActivity";
	private static final String message = "Recording ....";
	private CountDownTimer cdt;

	public static final int TIME_RECORD = 300000; // ms
	public static SoundEffect soundServices;
	private boolean initFail = false;

	// FOR SOUND WAVE
	private WaveDrawer.DrawThread mDrawThread;
	private WaveDrawer mdrawer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		soundServices = new SoundEffect(this);
		int res = soundServices.init();
		if (res == 0) {
			Utils.showMsg(this,
					"Init fail, maybe your device doesn't support this plugin");
			initFail = true;
		}

		mHandler = new Handler();

		// button record
		recordBtn = (Button) findViewById(R.id.btn_record);
		if (!initFail) {
			recordBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startRecording();
				}
			});
		}

		// button stop
		stopBtn = (Button) findViewById(R.id.btn_stop_record);
		if (!initFail) {
			stopBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					stopRecording(true);
				}
			});
		}

		// message to count time.
		messageRecord = (TextView) findViewById(R.id.text_recording);

		// for soundwave
		mdrawer = (WaveDrawer) findViewById(R.id.drawer);

		stopThreads = new Runnable() {
			@Override
			public void run() {
				stopRecording(true);
			}
		};
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
		mDrawThread = mdrawer.getThread();
		mDrawThread.setRun(false);
		mDrawThread.SetSleeping(true);
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(LOG_TAG, "onStop");
		stopRecording(false);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(LOG_TAG, "onDestroy");
		if (!initFail) {
			soundServices.destroy();
		}
		super.onDestroy();
	}

	/**
	 * Start recording & stop when press button 'stop' or after TIME_RECORD.
	 */
	private synchronized void startRecording() {
		if (!isRecording) {
			soundServices.startRecord();
			isRecording = true;
			mDrawThread = mdrawer.getThread();
			checkInterface();
			startCountTimer();
			mHandler.postDelayed(stopThreads, TIME_RECORD);
		}
	}

	/**
	 * Stop recording. Start EffectActivity if startNewAct is true.
	 */
	private synchronized void stopRecording(boolean startNewAct) {
		if (isRecording) {
			soundServices.stopRecord();
			isRecording = false;
			if (startNewAct) {
				Intent intent = new Intent(getApplicationContext(),
						EffectActivity.class);
				startActivity(intent);
			}
			if (cdt != null) {
				cdt.cancel();
			}
			if (stopThreads != null) {
				mHandler.removeCallbacks(stopThreads);
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
