package vng.wmb.activity;

import vng.wmb.service.AudioService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class RecordingActivity extends Activity {

	private Button stopRecordBtn;
	private AudioService audioServices = StartActivity.audioServices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);

		stopRecordBtn = (Button) findViewById(R.id.btn_stop_record);
		stopRecordBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				audioServices.stopRecord();
			}
		});
	}

	public void stopActivity() {
		Intent intent = new Intent(getApplicationContext(), StartActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onStart() {
		StartActivity.recordedFlag = true;
		// audioServices.startRecord();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recording, menu);
		return true;
	}

}
