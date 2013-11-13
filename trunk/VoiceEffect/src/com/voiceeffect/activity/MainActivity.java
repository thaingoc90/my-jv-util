package com.voiceeffect.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	RecordButton recordBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recordBtn = (RecordButton) findViewById(R.id.btn_record);
	}

	class RecordButton extends Button {

		boolean mStartRecording = true;

		OnClickListener clicker = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mStartRecording) {
					setText("StopRecord");
				} else {
					setText("StartRecord");
				}
				mStartRecording = !mStartRecording;
			}
		};

		public RecordButton(Context context) {
			super(context);
			setText("StartRecord");
			setOnClickListener(clicker);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Call when click 'record' button. Start recordActivity.
	 * 
	 * @param view
	 */
	public void recordVoice(View view) {
		Intent recordIntent = new Intent(getApplicationContext(),
				RecordVoiceActivity.class);
		startActivityForResult(recordIntent, 0);
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
