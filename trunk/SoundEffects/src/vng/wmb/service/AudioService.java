package vng.wmb.service;

import vng.wmb.activity.StartActivity;

public class AudioService {

	public AudioService(StartActivity activity) {
		mActivity = activity;
	}

	private StartActivity mActivity;

	public void setBuffer(short[] paramArrayOfShort) {
		mActivity.setBuffer(paramArrayOfShort);
	}

	public native int init();

	public native void startRecord();

	public native void stopRecord();

	public native int initPlayer();

	public native void playEffect();

	public native void startPlayer();

	public native void stopPlayer();

	public native void destroy();

}
