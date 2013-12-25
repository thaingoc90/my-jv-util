package vng.wmb.service;

public class AudioService {

	public native int init();

	public native void startRecord();

	public native void stopRecord();

	public native int initPlayer();

	public native void playEffect();

	public native void startPlayer();

	public native void stopPlayer();

	public native void destroy();

}
