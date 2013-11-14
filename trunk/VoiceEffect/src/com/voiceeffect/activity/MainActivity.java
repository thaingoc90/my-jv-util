package com.voiceeffect.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.media.MediaPlayer.OnCompletionListener;

public class MainActivity extends Activity {

	private static final String LOG_TAG = "VoiceEffect";
	private static String mFileName = "";
	Button recordBtn, playBtn;
	boolean mStartRecording = true;
	boolean mStartPlaying = true;
	private MediaPlayer mPlayer = null;
	private MediaRecorder mRecorder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initRecordingVariable();
		initPlayingVariable();

		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/TestAudio.mp3";

		recordBtn = (Button) findViewById(R.id.btn_record);
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
		playBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mStartPlaying) {
					startPlaying();
					playBtn.setText("Stop");
				} else {
					stopPlaying();
					playBtn.setText("Play");
				}
				mStartPlaying = !mStartPlaying;

			}
		});
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
				Log.i(LOG_TAG, "File not exist");
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
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, "Playing failed");
			initPlayingVariable();
		}
	}

	/**
	 * Stop playing
	 */
	public void stopPlaying() {
		Log.d(LOG_TAG, "stop playing");
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	public void startAlert(View view) {
		EditText text = (EditText) findViewById(R.id.time);
		int i = Integer.parseInt(text.getText().toString());
		Intent intent = new Intent(this, MyBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				this.getApplicationContext(), 12345, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (i * 1000), pendingIntent);
		Toast.makeText(this, "Alarm set in " + i + " seconds",
				Toast.LENGTH_LONG).show();
	}
}
