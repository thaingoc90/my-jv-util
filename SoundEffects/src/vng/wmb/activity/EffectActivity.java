package vng.wmb.activity;

import vng.wmb.service.AudioService;
import vng.wmb.service.EchoEffect;
import vng.wmb.service.SoundTouchEffect;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class EffectActivity extends Activity {

	private Button catBtn, cowBtn, birdBtn, fastBtn, robotBtn, stageBtn,
			dubVaderBtn;
	public SoundTouchEffect soundTouchService;
	public EchoEffect echoService;
	public static AudioService audioServices = StartActivity.audioServices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);

		audioServices.initPlayer();

		soundTouchService = new SoundTouchEffect();
		soundTouchService.init();

		echoService = new EchoEffect();
		echoService.init();

		/* CAT EFFECT */
		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Cat Effect");
				soundTouchService.createSoundTouch(0, 6, 15);
				audioServices.playEffect(true, false);
			}
		});

		/* COW EFFECT */
		cowBtn = (Button) findViewById(R.id.btn_effect_cow);
		cowBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Cow Effect");
				soundTouchService.createSoundTouch(0, -4, 0);
				audioServices.playEffect(true, false);
			}
		});

		/* BIRD EFFECT */
		birdBtn = (Button) findViewById(R.id.btn_effect_bird);
		birdBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Bird Effect");
				soundTouchService.createSoundTouch(0, 7, 25);
				audioServices.playEffect(true, false);
			}
		});

		/* FAST EFFECT */
		fastBtn = (Button) findViewById(R.id.btn_effect_fast);
		fastBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Fast Effect");
				soundTouchService.createSoundTouch(60, 0, 0);
				audioServices.playEffect(true, false);
			}
		});

		/* ROBOT EFFECT */
		robotBtn = (Button) findViewById(R.id.btn_effect_robot);
		robotBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Robot Effect");
				echoService.initProcess(8, 0.85);
				audioServices.playEffect(false, true);
			}
		});

		/* STAGE EFFECT */
		stageBtn = (Button) findViewById(R.id.btn_effect_stage);
		stageBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Stage Effect");
				echoService.initProcess(68, 0.5);
				audioServices.playEffect(false, true);
			}
		});

		/* DUB VADER EFFECT */
		dubVaderBtn = (Button) findViewById(R.id.btn_effect_dub_vader);
		dubVaderBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("EFFECT", "Dub Vader Effect");
				soundTouchService.createSoundTouch(-2, -6, -2);
				echoService.initProcess(200, 0.3);
				audioServices.playEffect(true, true);
			}
		});

	}

	@Override
	protected void onStart() {
		audioServices.startPlayer();
		super.onStart();
	}

	@Override
	protected void onStop() {
		audioServices.stopPlayer();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		soundTouchService.destroy();
		echoService.destroy();
		audioServices.destroyPlayer();
		super.onDestroy();
	}

}
