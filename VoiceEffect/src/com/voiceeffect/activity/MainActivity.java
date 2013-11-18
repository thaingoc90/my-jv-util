package com.voiceeffect.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String LOG_TAG = "VoiceEffect";
	private static String mFileName = "";
	Button recordBtn, playBtn;
	boolean mStartRecording = true;
	boolean mStartPlaying = true;
	private MediaPlayer mPlayer = null;
	private MediaRecorder mRecorder = null;
	private Handler mHandler = null;
	private final static int TIME_RECORD = 5; // second

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/TestAudio.wav";
		mHandler = new Handler();

		recordBtn = (Button) findViewById(R.id.btn_record);
		initRecordingVariable();
		recordBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mStartRecording) {
					startRecording();
					recordBtn.setText("StopRecord");
				} else {
					stopRecording();
					recordBtn.setText("StartRecord");
				}
				mStartRecording = !mStartRecording;
			}
		});

		playBtn = (Button) findViewById(R.id.btn_normal_voice);
		initPlayingVariable();
		playBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mStartPlaying) {
					startPlaying();
				} else {
					stopPlaying();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		stopPlaying();
		stopRecording();
		super.onDestroy();
	}

	public void initRecordingVariable() {
		mStartRecording = true;
		recordBtn.setText("StartRecord");
	}

	public void initPlayingVariable() {
		mStartPlaying = true;
		playBtn.setText("Play");
	}

	/**
	 * Start recording.
	 */
	public void startRecording() {
		Log.d(LOG_TAG, "start recording");
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
			mRecorder.start();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					stopRecording();
					initRecordingVariable();
				}
			}, TIME_RECORD * 1000);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Recording failed");
		}
	}

	/**
	 * Stop recording
	 */
	public void stopRecording() {
		Log.d(LOG_TAG, "stop recording");
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	/**
	 * Start playing
	 */
	public void startPlaying() {
		mPlayer = new MediaPlayer();
		Log.d(LOG_TAG, "start playing");
		try {
			File file = new File(mFileName);
			if (!file.exists()) {
				Toast.makeText(this, "Please record your voice.",
						Toast.LENGTH_LONG).show();
			} else {
				mPlayer.setDataSource(mFileName);
				mPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						stopPlaying();
						initPlayingVariable();
					}
				});
				mPlayer.prepare();
				mPlayer.start();
				mStartPlaying = false;
				playBtn.setText("Stop");
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "Playing failed");
			stopPlaying();
		}
	}

	/**
	 * Stop playing
	 */
	public void stopPlaying() {
		initPlayingVariable();
		Log.d(LOG_TAG, "stop playing");
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	/* -----------------------TEST ALERT------------------------ */
	/**
	 * 
	 * @param view
	 */
	// public void startAlert(View view) {
	// EditText text = (EditText) findViewById(R.id.time);
	// int i;
	// try {
	// i = Integer.parseInt(text.getText().toString());
	// } catch (Exception e) {
	// i = 0;
	// }
	// Intent intent = new Intent(this, MyBroadcastReceiver.class);
	// PendingIntent pendingIntent = PendingIntent.getBroadcast(
	// this.getApplicationContext(), 12345, intent, 0);
	// AlarmManager alarmManager = (AlarmManager)
	// getSystemService(ALARM_SERVICE);
	// alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
	// + (i * 1000), pendingIntent);
	// Toast.makeText(this, "Alarm set in " + i + " seconds",
	// Toast.LENGTH_LONG).show();
	// }
}
