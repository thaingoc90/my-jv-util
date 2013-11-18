package com.voiceeffect.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String LOG_TAG = "VoiceEffect";
	private static String mFileName = "", mTempFileName;
	Button recordBtn, playBtn;
	boolean isRecording = false;
	boolean mStartPlaying = true;
	private MediaPlayer mPlayer = null;
	private AudioRecord mRecorder = null;
	private Handler mHandler = null;
	private final static int TIME_RECORD = 5; // second
	private Thread threadRecord = null;

	int RECORDER_SAMPLERATE = 44100;
	private final int RECORDER_BPP = 16;
	int channelConfig = AudioFormat.CHANNEL_IN_MONO;
	int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	int bufferSizeInByte = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
			channelConfig, audioFormat);
	byte[] bufferData = new byte[bufferSizeInByte];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mTempFileName = mFileName + "/TempAudio.raw";
		mFileName += "/TestAudio.wav";

		mHandler = new Handler();

		recordBtn = (Button) findViewById(R.id.btn_record);
		initRecordingVariable();
		recordBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isRecording) {
					startRecording();
					recordBtn.setText("StopRecord");
				} else {
					stopRecording();
					recordBtn.setText("StartRecord");
				}
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
		isRecording = false;
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
		try {
			isRecording = true;
			mRecorder = new AudioRecord(AudioSource.MIC, RECORDER_SAMPLERATE,
					channelConfig, audioFormat, bufferSizeInByte);
			mRecorder.startRecording();
			threadRecord = new Thread(new Runnable() {
				@Override
				public void run() {
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(mTempFileName);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (fos != null) {
						int read = 0;
						while (isRecording) {
							read = mRecorder.read(bufferData, 0,
									bufferData.length);
							if (read != AudioRecord.ERROR_INVALID_OPERATION) {
								try {
									fos.write(bufferData);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			threadRecord.start();
			// mHandler.postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// stopRecording();
			// initRecordingVariable();
			// }
			// }, TIME_RECORD * 1000);
		} catch (Exception e) {
			Log.e(LOG_TAG, "Recording failed");
			isRecording = false;
		}
	}

	/**
	 * Stop recording
	 */
	public void stopRecording() {
		Log.d(LOG_TAG, "stop recording");
		isRecording = false;
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			threadRecord = null;
		}
		Log.d(LOG_TAG, "copy File");
		copyWaveFile(mTempFileName, mFileName);
		Log.d(LOG_TAG, "delete File");
		deleteTempFile();
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

	private void deleteTempFile() {
		File file = new File(mTempFileName);

		file.delete();
	}

	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 2;
		long byteRate = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

		byte[] data = new byte[8];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			Log.d(LOG_TAG, "" + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}
			
			Log.d(LOG_TAG, "3");

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			Log.d(LOG_TAG, e.getMessage());
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(LOG_TAG, e.getMessage());
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = RECORDER_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}
}
