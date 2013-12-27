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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);

		audioServices.initPlayer();
		audioServices.startPlayer();

		soundTouchService = new SoundTouchEffect();
		storagePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		inFilePath = storagePath + "/voice.wav";
		outFilePath = storagePath + "/effect.wav";
		soundTouchService.init(inFilePath, outFilePath);

		/* CAT EFFECT */
		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Cat Effect");
				soundTouchService.createSoundTouch(0, 6, 15);
				audioServices.playEffect();
			}
		});

		/* COW EFFECT */
		cowBtn = (Button) findViewById(R.id.btn_effect_cow);
		cowBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Cow Effect");
				soundTouchService.createSoundTouch(0, -4, 0);
				audioServices.playEffect();
			}
		});

		/* BIRD EFFECT */
		birdBtn = (Button) findViewById(R.id.btn_effect_bird);
		birdBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Bird Effect");
				soundTouchService.createSoundTouch(0, 7, 25);
				audioServices.playEffect();
			}
		});

		/* FAST EFFECT */
		fastBtn = (Button) findViewById(R.id.btn_effect_fast);
		fastBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Fast Effect");
				soundTouchService.createSoundTouch(60, 0, 0);
				audioServices.playEffect();
			}
		});

	}

	@Override
	protected void onStop() {
		soundTouchService.destroy();
		audioServices.stopPlayer();
		super.onStop();
	}

}
