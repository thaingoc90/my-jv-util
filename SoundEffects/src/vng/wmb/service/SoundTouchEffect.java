package vng.wmb.service;

public class SoundTouchEffect {

	public native int init();

	public native void changeTempo(double tempo);

	public native void changePitch(double pitch);

	public native void changeRate(double rate);

	public native void writeToFile();

	public native void destroy();

	public native void createSoundTouch(double tempo, double pitch, double rate);
}
