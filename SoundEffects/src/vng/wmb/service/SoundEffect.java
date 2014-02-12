package vng.wmb.service;

import android.content.res.AssetManager;
import vng.wmb.activity.StartActivity;

public class SoundEffect {

	private StartActivity mActivity;

	public SoundEffect(StartActivity activity) {
		mActivity = activity;
	}

	public native int init();

	public native void destroy();

	public native void startRecord();

	public native void stopRecord();

	public native void startPlayer();

	public native void stopPlayer();

	public native int initEffectLib(AssetManager mgr);

	public native void destroyEffectLib();

	public native void playEffect(int effect);

	public native int processAndWriteToAmr(int effect);

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
