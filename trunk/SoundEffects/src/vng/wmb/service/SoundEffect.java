package vng.wmb.service;

import android.content.res.AssetManager;
import vng.wmb.activity.StartActivity;

public class SoundEffect {

	private StartActivity mActivity;

	public SoundEffect(StartActivity activity) {
		mActivity = activity;
	}

	public native int init();

	public native int destroy();

	public native int startRecord();

	public native int stopRecord();

	public native int startPlayer();

	public native int stopPlayer();

	public native int initEffectLib(AssetManager mgr);

	public native int destroyEffectLib();

	public native int playEffect(int effect);

	public native int processAndWriteToAmr(int effect);
	
	public native int processAndWriteToMp3(int effect);

	// FUNCTION USE FOR CUSTOM EFFECT IF NECESSARY

	public native void playCustomEffect(boolean setIfSoundTouch,
			boolean setIfEcho, boolean setIfBackground, boolean setIfReverb);

	public native void prepareSoundTouchEffect(double tempo, double pitch,
			double rate);

	public native void prepareBackgroundEffect(String pathFileBackground,
			double gain, boolean inAssetFolder, boolean isWavFile);

	public native void prepareReverbEffect(double mRoomSize, double mDelay,
			double mReverberance, double mHfDamping, double mToneLow,
			double mToneHigh, double mWetGain, double mDryGain);

	public native void prepareEchoReverb(double mDelay, double mDecay);

	public void setBuffer(short[] paramArrayOfShort) {
		mActivity.setBuffer(paramArrayOfShort);
	}
}
