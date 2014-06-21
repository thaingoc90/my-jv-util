package ntdn.voiceutil.activity;

import ntdn.voiceutil.service.CVoiceService;
import ntdn.voiceutil.utils.Constants;
import ntdn.voiceutil.utils.Utils;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class EffectActivity extends Activity {

	private Button catBtn, cowBtn, birdBtn, fastBtn, robotBtn, stageBtn,
			dubVaderBtn, romanceBtn, micBtn, originalBtn, dubBtn,
			techtronicBtn, sendBtn;

	private AssetManager mgr;
	private static final String LOG_TAG = "EffectActivity";
	private int effectType = Constants.EFFECT_ORIGINAL;
	public static CVoiceService soundServices = VoiceEffectActivity.voiceServices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);

		mgr = getResources().getAssets();
		soundServices.initEffectLib(mgr);
		effectType = Constants.EFFECT_ORIGINAL;

		/* SEND */
		sendBtn = (Button) findViewById(R.id.btn_action_send);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOG_TAG, "SEND ACTION");
				// int status = soundServices.processAndWriteToAmr(effectType);
				int status = soundServices.processAndWriteToMp3(effectType);

				if (status == Constants.STATUS_OK) {
					Utils.showMsg(EffectActivity.this, "Send succeffully");
				} else if (status == Constants.STATUS_NOT_SUPPORT) {
					Utils.showMsg(EffectActivity.this,
							"Not support this sample rate");
				} else {
					Utils.showMsg(EffectActivity.this, "Failed");
				}
			}
		});

		/* CAT EFFECT */
		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					public void run() {
						Log.i(LOG_TAG, "CAT EFFECT");
						effectType = Constants.EFFECT_CAT;
						soundServices.playEffect(effectType);

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
						Log.i(LOG_TAG, "COW EFFECT");
						effectType = Constants.EFFECT_COW;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "BIRD EFFECT");
						effectType = Constants.EFFECT_BIRD;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "FAST EFFECT");
						effectType = Constants.EFFECT_FAST;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "ROBOT EFFECT");
						effectType = Constants.EFFECT_ROBOT;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "STAGE EFFECT");
						effectType = Constants.EFFECT_STAGE;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "DUB VADER EFFECT");
						effectType = Constants.EFFECT_DUB_VADER;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "ROMANCE EFFECT");
						effectType = Constants.EFFECT_ROMANCE;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "MICROPHONE EFFECT");
						effectType = Constants.EFFECT_MIC;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "DUB EFFECT");
						effectType = Constants.EFFECT_DUB;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "TECHTRONIC EFFECT");
						effectType = Constants.EFFECT_TECHTRONIC;
						soundServices.playEffect(effectType);
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
						Log.i(LOG_TAG, "ORIGINAL EFFECT");
						effectType = Constants.EFFECT_ORIGINAL;
						soundServices.playEffect(effectType);
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
		soundServices.destroyEffectLib();
		super.onDestroy();
	}

}
