package vng.wmb.service;

public class SoundTouchEffect {

	public native void init();

	public native void changeTempo(int tempo);

	public native void changePitch(int pitch);

	public native void changeRate(int rate);

	public native void process();

	public native void destroy();
}
