package vng.wmb.activity;

import vng.wmb.service.AudioService;
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
	private SoundSampler sampler;

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
		// int i = 0;
		// while (true) {
		// if ((sampler.GetDead2()) && (mdrawer.GetDead2())) {
		// Log.i(LOG_TAG, sampler.GetDead2() + ", " + mdrawer.GetDead2());
		// sampler.Restart();
		// if (!m_bStart) {
		// mdrawer.Restart(true);
		// }
		// sampler.SetSleeping(false);
		// mDrawThread.SetSleeping(false);
		// m_bStart = false;
		// super.onResume();
		// return;
		// }
		// try {
		// Thread.sleep(500L);
		// Log.i(LOG_TAG, "Hang on..");
		// i++;
		// if (!sampler.GetDead2())
		// Log.i(LOG_TAG, "sampler not DEAD!!!");
		// if (!mdrawer.GetDead2()) {
		// Log.i(LOG_TAG, "mDrawer not DeAD!!");
		// mdrawer.setRun(false);
		// }
		// if (i <= 4)
		// continue;
		// mDrawThread.SetDead2(true);
		// } catch (InterruptedException localInterruptedException) {
		// localInterruptedException.printStackTrace();
		// }
		// }
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
		// runSoundWave();
		// Log.i(LOG_TAG, "mDrawThread NOT NULL");
		// Log.i(LOG_TAG, "recorder NOT NULL");
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
		Log.i(LOG_TAG, "Buffer");
		mDrawThread = mdrawer.getThread();
		mDrawThread.setBuffer(paramArrayOfShort);
	}

	/**
	 * Called by OnCreate to get everything up and running
	 */
	public void runSoundWave() {
		try {
			if (mDrawThread == null) {
				mDrawThread = mdrawer.getThread();
			}
			if (sampler == null) {
				sampler = new SoundSampler(this);
			}
			try {
				sampler.Init();
			} catch (Exception e) {
				Utils.showMsg(this,
						"Could not instance the sampler. Could not access the device audio-drivers");
				return;
			}
			sampler.StartRecording();
			sampler.StartSampling();
		} catch (NullPointerException e) {
			Log.e(LOG_TAG, "NullPointer: " + e.getMessage());
		}
	}
}
