package vng.wmb.activity;

import vng.wmb.service.AudioService;
import vng.wmb.service.SoundTouchEffect;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class EffectActivity extends Activity {

	private Button catBtn, cowBtn, birdBtn, fastBtn;
	private static String inFilePath, outFilePath, storagePath;
	public SoundTouchEffect soundTouchService;
	public static AudioService audioServices = StartActivity.audioServices;
	private static int WRITE_EFFECT_TO_FILE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);

		if (audioServices == null) {
			audioServices = new AudioService();
			audioServices.init();
		}
		audioServices.initPlayer();
		audioServices.playMusic();

		soundTouchService = new SoundTouchEffect();
		storagePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		inFilePath = storagePath + "/voice.wav";
		outFilePath = storagePath + "/effect.wav";
		soundTouchService.init(inFilePath, outFilePath);

		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Cat Effect");
				soundTouchService.changeTempo(0);
				soundTouchService.changePitch(6);
				soundTouchService.changeRate(15);
				soundTouchService.process(WRITE_EFFECT_TO_FILE);
			}
		});

		cowBtn = (Button) findViewById(R.id.btn_effect_cow);
		cowBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Cow Effect");
				soundTouchService.changeTempo(0);
				soundTouchService.changePitch(0);
				soundTouchService.changeRate(-4);
				soundTouchService.process(WRITE_EFFECT_TO_FILE);
			}
		});

		birdBtn = (Button) findViewById(R.id.btn_effect_bird);
		birdBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Bird Effect");
				soundTouchService.changeTempo(0);
				soundTouchService.changePitch(7);
				soundTouchService.changeRate(25);
				soundTouchService.process(WRITE_EFFECT_TO_FILE);
			}
		});

		fastBtn = (Button) findViewById(R.id.btn_effect_fast);
		fastBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Fast Effect");
				soundTouchService.changeTempo(0);
				soundTouchService.changePitch(7);
				soundTouchService.changeRate(25);
				soundTouchService.process(WRITE_EFFECT_TO_FILE);
			}
		});

	}

	@Override
	protected void onDestroy() {
		soundTouchService.destroy();
		super.onDestroy();
	}

}
