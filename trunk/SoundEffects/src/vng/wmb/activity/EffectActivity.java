package vng.wmb.activity;

import vng.wmb.service.AudioService;
import vng.wmb.service.BackgroundEffect;
import vng.wmb.service.EchoEffect;
import vng.wmb.service.ReverbEffect;
import vng.wmb.service.SoundTouchEffect;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class EffectActivity extends Activity {

	private Button catBtn, cowBtn, birdBtn, fastBtn, robotBtn, stageBtn,
			dubVaderBtn, romanceBtn, micBtn, originalBtn, dubBtn,
			techtronicBtn;
	public SoundTouchEffect soundTouchService;
	public EchoEffect echoService;
	public BackgroundEffect backgroundService;
	public ReverbEffect reverbService;
	public static AudioService audioServices = StartActivity.audioServices;
	private AssetManager mgr;
	private static final String LOG_TAG = "EffectActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);

		audioServices.initPlayer();

		soundTouchService = new SoundTouchEffect();
		soundTouchService.init();

		echoService = new EchoEffect();
		echoService.init();

		reverbService = new ReverbEffect();
		reverbService.init();

		backgroundService = new BackgroundEffect();
		mgr = getResources().getAssets();
		backgroundService.init(mgr);

		/* CAT EFFECT */
		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "Cat Effect");
						soundTouchService.createSoundTouch(0, 6, 15);
						audioServices.playEffect(true, false, false, false);
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
						Log.i(LOG_TAG, "Cow Effect");
						soundTouchService.createSoundTouch(0, -4, 0);
						audioServices.playEffect(true, false, false, false);
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
						Log.i(LOG_TAG, "Bird Effect");
						soundTouchService.createSoundTouch(0, 7, 25);
						audioServices.playEffect(true, false, false, false);
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
						Log.i(LOG_TAG, "Fast Effect");
						soundTouchService.createSoundTouch(60, 0, 0);
						audioServices.playEffect(true, false, false, false);
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
						Log.i(LOG_TAG, "Robot Effect");
						echoService.initProcess(8, 0.85);
						audioServices.playEffect(false, true, false, false);
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
						Log.i(LOG_TAG, "Stage Effect");
						echoService.initProcess(80, 0.6);
						audioServices.playEffect(false, true, false, false);
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
						Log.i(LOG_TAG, "Dub Vader Effect");
						soundTouchService.createSoundTouch(-2, -6, -2);
						echoService.initProcess(200, 0.3);
						audioServices.playEffect(true, true, false, false);
					}
				}).start();
			}
		});

		/* MUSIC EFFECT */
		romanceBtn = (Button) findViewById(R.id.btn_effect_romance);
		romanceBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "MUSIC Effect");
						backgroundService.initProcess("bg_romance.wav", 0.7,
								true);
						audioServices.playEffect(false, false, true, false);
					}
				}).start();
			}
		});

		/* MICROPHONE EFFECT */
		micBtn = (Button) findViewById(R.id.btn_effect_mic);
		micBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "MICROPHONE Effect");
						reverbService
								.initProcess(30, 10, 50, 50, 50, 100, 0, 0);
						audioServices.playEffect(false, false, false, true);
					}
				}).start();
			}
		});

		/* DUB EFFECT */
		dubBtn = (Button) findViewById(R.id.btn_effect_dub);
		dubBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "DUB Effect");
						backgroundService.initProcess("bg_dub.wav", 0.7, true);
						audioServices.playEffect(false, false, true, false);
					}
				}).start();
			}
		});

		/* TECHTRONIC EFFECT */
		techtronicBtn = (Button) findViewById(R.id.btn_effect_techtronic);
		techtronicBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "TECHTRONIC Effect");
						backgroundService.initProcess("bg_techtronic.wav", 0.7,
								true);
						audioServices.playEffect(false, false, true, false);
					}
				}).start();
			}
		});

		/* ORIGINAL EFFECT */
		originalBtn = (Button) findViewById(R.id.btn_effect_original);
		originalBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "ORIGINAL Effect");
						audioServices.playEffect(false, false, false, false);
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
		reverbService.destroy();
		backgroundService.destroy();
		audioServices.destroyPlayer();
		super.onDestroy();
	}

}
