package vng.wmb.activity;

import vng.wmb.service.AudioService;
import vng.wmb.service.SoundTouchEffect;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

	static {
		System.loadLibrary("soundEffect");
	}

	private Button recordBtn, normalBtn, speedBtn;
	public static boolean recordedFlag = false;
	public static AudioService audioServices = new AudioService();
	public SoundTouchEffect soundTouchService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		audioServices.init();
		audioServices.initPlayer();
		soundTouchService = new SoundTouchEffect();
		// soundTouchService.init();

		normalBtn = (Button) findViewById(R.id.btn_effect_normal);
		normalBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// audioServices.playMusic();
			}
		});

		speedBtn = (Button) findViewById(R.id.btn_effect_speed);
		speedBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// audioServices.playMusic();
			}
		});

		recordBtn = (Button) findViewById(R.id.btn_record);
		recordBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						RecordingActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onStart() {
		if (recordedFlag) {
			normalBtn.setVisibility(View.VISIBLE);
			speedBtn.setVisibility(View.VISIBLE);
			recordBtn.setText(R.string.btn_record_again);
		} else {
			normalBtn.setVisibility(View.INVISIBLE);
			speedBtn.setVisibility(View.INVISIBLE);
			recordBtn.setText(R.string.btn_record);
		}
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		audioServices.destroy();
		// soundTouchService.destroy();
		super.onDestroy();
	}

}
