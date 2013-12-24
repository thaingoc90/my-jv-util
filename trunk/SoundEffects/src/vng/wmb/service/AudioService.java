package vng.wmb.service;

public class AudioService {

	public native int init();

	public native void startRecord();

	public native void stopRecord();

	public native int initPlayer();

	public native void playMusic();

	public native void stopMusic();

	public native void destroy();
	
	public native void playWaveFile(String path);
}
