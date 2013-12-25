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

	// IF WRITE_EFFECT_TO_FILE = 1, processing of effect will write to file
	// effect.wav. Else, it will play this effect.
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

		/* CAT EFFECT */
		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Cat Effect");
				soundTouchService.init(inFilePath, outFilePath);
				soundTouchService.changeTempo(0);
				soundTouchService.changePitch(6);
				soundTouchService.changeRate(15);
				soundTouchService.process(WRITE_EFFECT_TO_FILE);
				soundTouchService.destroy();
			}
		});

		/* COW EFFECT */
		cowBtn = (Button) findViewById(R.id.btn_effect_cow);
		cowBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Cow Effect");
				soundTouchService.init(inFilePath, outFilePath);
				soundTouchService.changeTempo(0);
				soundTouchService.changePitch(0);
				soundTouchService.changeRate(-4);
				soundTouchService.process(WRITE_EFFECT_TO_FILE);
				soundTouchService.destroy();
			}
		});

		/* BIRD EFFECT */
		birdBtn = (Button) findViewById(R.id.btn_effect_bird);
		birdBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Bird Effect");
				soundTouchService.init(inFilePath, outFilePath);
				soundTouchService.changeTempo(0);
				soundTouchService.changePitch(7);
				soundTouchService.changeRate(25);
				soundTouchService.process(WRITE_EFFECT_TO_FILE);
				soundTouchService.destroy();
			}
		});

		/* FAST EFFECT */
		fastBtn = (Button) findViewById(R.id.btn_effect_fast);
		fastBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Fast Effect");
				soundTouchService.init(inFilePath, outFilePath);
				soundTouchService.changeTempo(60);
				soundTouchService.changePitch(0);
				soundTouchService.changeRate(0);
				soundTouchService.process(WRITE_EFFECT_TO_FILE);
				soundTouchService.destroy();
			}
		});

	}

	@Override
	protected void onStop() {
		audioServices.stopMusic();
		super.onStop();
	}

}
