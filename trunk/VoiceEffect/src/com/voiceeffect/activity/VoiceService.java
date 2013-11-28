package com.voiceeffect.activity;

public class VoiceService {

	static {
		System.loadLibrary("voiceEffect");
	}

	public native void init();

	public native void startRecord();

	public native void stopRecord();

	public native void playMusic();

	public native void stopMusic();

	public native void destroy();

}
