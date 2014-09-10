package ntdn.voiceutil.activity;

import ntdn.voiceutil.manager.CSoundManager;
import ntdn.voiceutil.utils.Constants;
import ntdn.voiceutil.utils.Log;
import ntdn.voiceutil.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EffectActivity extends Activity {

	private Button catBtn, cowBtn, birdBtn, fastBtn, robotBtn, stageBtn,
			dubVaderBtn, romanceBtn, micBtn, originalBtn, dubBtn,
			techtronicBtn, sendBtn;

	private AssetManager mgr;
	private static final String LOG_TAG = "EffectActivity";
	private int effectType = Constants.EFFECT_ORIGINAL;
	public static CSoundManager soundServices = VoiceEffectActivity.voiceServices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_effect);

		mgr = getResources().getAssets();
		soundServices.initEffectLib(mgr);
		effectType = Constants.EFFECT_ORIGINAL;

		View.OnClickListener btnListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String logStr = "EFFECT_ORIGINAL";
				switch (v.getId()) {
				case R.id.btn_effect_cat:
					effectType = Constants.EFFECT_CAT;
					logStr = "EFFECT_CAT";
					break;
				case R.id.btn_effect_bird:
					effectType = Constants.EFFECT_BIRD;
					logStr = "EFFECT_BIRD";
					break;
				case R.id.btn_effect_cow:
					effectType = Constants.EFFECT_COW;
					logStr = "EFFECT_COW";
					break;
				case R.id.btn_effect_dub:
					effectType = Constants.EFFECT_DUB;
					logStr = "EFFECT_DUB";
					break;
				case R.id.btn_effect_dub_vader:
					effectType = Constants.EFFECT_DUB_VADER;
					logStr = "EFFECT_DUB_VADER";
					break;
				case R.id.btn_effect_fast:
					effectType = Constants.EFFECT_FAST;
					logStr = "EFFECT_FAST";
					break;
				case R.id.btn_effect_mic:
					effectType = Constants.EFFECT_MIC;
					logStr = "EFFECT_MIC";
					break;
				case R.id.btn_effect_robot:
					effectType = Constants.EFFECT_ROBOT;
					logStr = "EFFECT_ROBOT";
					break;
				case R.id.btn_effect_romance:
					effectType = Constants.EFFECT_ROMANCE;
					logStr = "EFFECT_ROMANCE";
					break;
				case R.id.btn_effect_stage:
					effectType = Constants.EFFECT_STAGE;
					logStr = "EFFECT_STAGE";
					break;
				case R.id.btn_effect_techtronic:
					effectType = Constants.EFFECT_TECHTRONIC;
					logStr = "EFFECT_TECHTRONIC";
					break;
				default:
					effectType = Constants.EFFECT_ORIGINAL;
					break;
				}

				Log.i(LOG_TAG, logStr);
				new Thread(new Runnable() {
					public void run() {
						soundServices.playEffect(effectType);
					}
				}).start();

				// Another way
				// soundServices.prepareSoundTouchEffect(0, 6, 15);
				// soundServices.playCustomEffect(true, false, false,
				// false);
			}
		};

		/* SEND */
		sendBtn = (Button) findViewById(R.id.btn_action_send);
		sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOG_TAG, "SAVE ACTION");
				final ProgressDialog waitingDialog = ProgressDialog.show(
						EffectActivity.this, "Please wait ...",
						"Saving Image ...", true, true);

				new Thread(new Runnable() {
					@Override
					public void run() {
						String filePath = Utils
								.getFileName(Constants.FILE_TYPE_WAV);
						if (Utils.strIsEmpty(filePath)) {
							waitingDialog.dismiss();
							Utils.showMsgSync(EffectActivity.this,
									"Cannot get app's folder");
							return;
						}

						int status = soundServices.saveFile(filePath,
								effectType, Constants.FILE_TYPE_WAV);

						waitingDialog.dismiss();

						String msg = "";
						if (status == Constants.STATUS_OK) {
							msg = "Save successfully";
						} else if (status == Constants.STATUS_NOT_SUPPORT) {
							msg = "Not support";
						} else {
							msg = "Failed";
						}
						Utils.showMsgSync(EffectActivity.this, msg);
					}
				}).start();
			}
		});

		/* CAT EFFECT */
		catBtn = (Button) findViewById(R.id.btn_effect_cat);
		catBtn.setOnClickListener(btnListener);

		/* COW EFFECT */
		cowBtn = (Button) findViewById(R.id.btn_effect_cow);
		cowBtn.setOnClickListener(btnListener);

		/* BIRD EFFECT */
		birdBtn = (Button) findViewById(R.id.btn_effect_bird);
		birdBtn.setOnClickListener(btnListener);

		/* FAST EFFECT */
		fastBtn = (Button) findViewById(R.id.btn_effect_fast);
		fastBtn.setOnClickListener(btnListener);

		/* ROBOT EFFECT */
		robotBtn = (Button) findViewById(R.id.btn_effect_robot);
		robotBtn.setOnClickListener(btnListener);

		/* STAGE EFFECT */
		stageBtn = (Button) findViewById(R.id.btn_effect_stage);
		stageBtn.setOnClickListener(btnListener);

		/* DUB VADER EFFECT */
		dubVaderBtn = (Button) findViewById(R.id.btn_effect_dub_vader);
		dubVaderBtn.setOnClickListener(btnListener);

		/* ROMANCE EFFECT */
		romanceBtn = (Button) findViewById(R.id.btn_effect_romance);
		romanceBtn.setOnClickListener(btnListener);

		/* MICROPHONE EFFECT */
		micBtn = (Button) findViewById(R.id.btn_effect_mic);
		micBtn.setOnClickListener(btnListener);

		/* DUB EFFECT */
		dubBtn = (Button) findViewById(R.id.btn_effect_dub);
		dubBtn.setOnClickListener(btnListener);

		/* TECHTRONIC EFFECT */
		techtronicBtn = (Button) findViewById(R.id.btn_effect_techtronic);
		techtronicBtn.setOnClickListener(btnListener);

		/* ORIGINAL EFFECT */
		originalBtn = (Button) findViewById(R.id.btn_effect_original);
		originalBtn.setOnClickListener(btnListener);

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
