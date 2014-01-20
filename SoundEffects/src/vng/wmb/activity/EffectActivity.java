package vng.wmb.activity;

import vng.wmb.service.SoundEffect;
import vng.wmb.util.EffectConstants;
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

	private AssetManager mgr;
	private static final String LOG_TAG = "EffectActivity";
	public static SoundEffect soundServices = StartActivity.soundServices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);

		mgr = getResources().getAssets();
		soundServices.initEffect(mgr);

		/* CAT EFFECT */
		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "Cat Effect");
						soundServices.playEffect(EffectConstants.EFFECT_CAT);

						// Another way
						// soundServices.prepareSoundTouchEffect(0, 6, 15);
						// soundServices.playCustomEffect(true, false, false,
						// false);
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
						soundServices.playEffect(EffectConstants.EFFECT_COW);
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
						soundServices.playEffect(EffectConstants.EFFECT_BIRD);
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
						soundServices.playEffect(EffectConstants.EFFECT_FAST);
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
						soundServices.playEffect(EffectConstants.EFFECT_ROBOT);
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
						soundServices.playEffect(EffectConstants.EFFECT_STAGE);
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
						soundServices
								.playEffect(EffectConstants.EFFECT_DUB_VADER);
					}
				}).start();
			}
		});

		/* ROMANCE EFFECT */
		romanceBtn = (Button) findViewById(R.id.btn_effect_romance);
		romanceBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "ROMANCE Effect");
						soundServices
								.playEffect(EffectConstants.EFFECT_ROMANCE);
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
						soundServices.playEffect(EffectConstants.EFFECT_MIC);
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
						soundServices.playEffect(EffectConstants.EFFECT_DUB);
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
						soundServices
								.playEffect(EffectConstants.EFFECT_TECHTRONIC);
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
						soundServices
								.playEffect(EffectConstants.EFFECT_ORIGINAL);
					}
				}).start();
			}
		});

	}

	@Override
	protected void onStart() {
		Log.i(LOG_TAG, "onStart");
		soundServices.startPlayer();
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(LOG_TAG, "onStop");
		soundServices.stopPlayer();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(LOG_TAG, "onDestroy");
		soundServices.destroyEffect();
		super.onDestroy();
	}

}
