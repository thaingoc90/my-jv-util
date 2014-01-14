package vng.wmb.activity;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

import vng.wmb.service.AudioService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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
	private static final int TIME_RECORD = 300000; // ms
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
				stopRecording(true);
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
		isRecording = true;
		mDrawThread = mdrawer.getThread();
		checkInterface();
		startCountTimer();
		stopThreads = new Runnable() {
			@Override
			public void run() {
				stopRecording(true);
			}
		};
		mHandler.postDelayed(stopThreads, TIME_RECORD);
	}

	/**
	 * Stop recording. Start EffectActivity if startNewAct is true.
	 */
	private synchronized void stopRecording(boolean startNewAct) {
		if (isRecording) {
			audioServices.stopRecord();
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
		Log.i(LOG_TAG, "checkInterface");
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

	// ---------------------------------------------------------
	// --------------------DIALOG - NOT USE YET-----------------------------
	// ---------------------------------------------------------

	private File mFile = Environment.getExternalStorageDirectory();
	private static final String FTYPE = "mp3";
	private List<String> mListFile;
	private String mChosenPath;

	/**
	 * Load all folders which contain file match with condition.
	 * 
	 * @param file
	 * @param listPathFiles
	 */
	private void loadFolders(File file, List<String> listPathFiles) {
		if (!file.exists()) {
			return;
		}
		boolean flagContainFile = false;
		File[] listFiles = file.listFiles();

		// If subFile is folder, recursion.
		// Else, check if filename contains FTYPE.
		// Do not process hidden files.
		for (File subFile : listFiles) {
			if (subFile.isDirectory() && !subFile.isHidden()) {
				loadFolders(subFile, listPathFiles);
			} else if (!flagContainFile && subFile.isFile()
					&& !subFile.isHidden()) {
				String fileName = subFile.getName();
				if (fileName.contains("." + FTYPE)) {
					listPathFiles.add(file.getAbsolutePath());
					flagContainFile = true;
				}
			}
		}
	}

	/**
	 * Load all files which match condition.
	 * 
	 * @param path
	 * @param listPathFiles
	 */
	private void loadFiles(String path, List<String> listPathFiles) {
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return;
		}
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.contains(FTYPE);
			}
		};
		File[] listFiles = file.listFiles(filter);
		if (listFiles != null) {
			for (File subFile : listFiles) {
				listPathFiles.add(subFile.getName());
			}
		}
	}

	/**
	 * Create dialog. loadFolder is true, dialog to load folders.
	 * 
	 * @param loadFolder
	 * @return
	 */
	protected Dialog onCreateDialog(boolean loadFolder) {
		Dialog dialog = null;
		String dialogTitle;
		DialogInterface.OnClickListener listener;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mListFile = new LinkedList<String>();

		if (loadFolder) {
			loadFolders(mFile, mListFile);
			dialogTitle = "Select " + FTYPE + " file";
			listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mChosenPath = mListFile.get(which);
					onCreateDialog(false);
				}
			};
		} else {
			loadFiles(mChosenPath, mListFile);
			dialogTitle = mChosenPath;
			listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mChosenPath = mChosenPath + "/" + mListFile.get(which);
					// Process here.
				}
			};
		}

		builder.setTitle(dialogTitle);
		builder.setItems(mListFile.toArray(new String[0]), listener);
		dialog = builder.show();
		return dialog;
	}
}
