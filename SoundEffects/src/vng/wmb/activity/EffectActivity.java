package vng.wmb.activity;

import vng.wmb.service.AudioService;
import vng.wmb.service.BackgroundEffect;
import vng.wmb.service.EchoEffect;
import vng.wmb.service.SoundTouchEffect;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class EffectActivity extends Activity {

	private Button catBtn, cowBtn, birdBtn, fastBtn, robotBtn, stageBtn,
			dubVaderBtn, musicBtn;
	public SoundTouchEffect soundTouchService;
	public EchoEffect echoService;
	public BackgroundEffect backgroundService;
	public static AudioService audioServices = StartActivity.audioServices;
	private AssetManager mgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);
		mgr = getResources().getAssets();

		audioServices.initPlayer();

		soundTouchService = new SoundTouchEffect();
		soundTouchService.init();

		echoService = new EchoEffect();
		echoService.init();

		backgroundService = new BackgroundEffect();
		backgroundService.init(mgr);

		/* CAT EFFECT */
		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i("EFFECT", "Cat Effect");
						soundTouchService.createSoundTouch(0, 6, 15);
						audioServices.playEffect(true, false, false);
					}
				}).start();
			}
		});

		/* COW EFFECT */
		cowBtn = (Button) findViewById(R.id.btn_effect_cow);
		cowBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i("EFFECT", "Cow Effect");
						soundTouchService.createSoundTouch(0, -4, 0);
						audioServices.playEffect(true, false, false);
					}
				}).start();
			}
		});

		/* BIRD EFFECT */
		birdBtn = (Button) findViewById(R.id.btn_effect_bird);
		birdBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i("EFFECT", "Bird Effect");
						soundTouchService.createSoundTouch(0, 7, 25);
						audioServices.playEffect(true, false, false);
					}
				}).start();
			}
		});

		/* FAST EFFECT */
		fastBtn = (Button) findViewById(R.id.btn_effect_fast);
		fastBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i("EFFECT", "Fast Effect");
						soundTouchService.createSoundTouch(60, 0, 0);
						audioServices.playEffect(true, false, false);
					}
				}).start();
			}
		});

		/* ROBOT EFFECT */
		robotBtn = (Button) findViewById(R.id.btn_effect_robot);
		robotBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i("EFFECT", "Robot Effect");
						echoService.initProcess(8, 0.85);
						audioServices.playEffect(false, true, false);
					}
				}).start();
			}
		});

		/* STAGE EFFECT */
		stageBtn = (Button) findViewById(R.id.btn_effect_stage);
		stageBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i("EFFECT", "Stage Effect");
						echoService.initProcess(68, 0.5);
						audioServices.playEffect(false, true, false);
					}
				}).start();
			}
		});

		/* DUB VADER EFFECT */
		dubVaderBtn = (Button) findViewById(R.id.btn_effect_dub_vader);
		dubVaderBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i("EFFECT", "Dub Vader Effect");
						soundTouchService.createSoundTouch(-2, -6, -2);
						echoService.initProcess(200, 0.3);
						audioServices.playEffect(true, true, false);
					}
				}).start();
			}
		});

		/* MUSIC EFFECT */
		musicBtn = (Button) findViewById(R.id.btn_effect_music);
		musicBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i("EFFECT", "MUSIC Effect");
						backgroundService.initProcess("bg_rain.wav", 0.7);
						audioServices.playEffect(false, false, true);
					}
				}).start();
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
		backgroundService.destroy();
		audioServices.destroyPlayer();
		super.onDestroy();
	}

}
