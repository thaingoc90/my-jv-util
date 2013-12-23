package vng.wmb.service;

public class SoundTouchEffect {

	public native int init(String inFilePath, String outFilePath);

	public native void changeTempo(double tempo);

	public native void changePitch(double pitch);

	public native void changeRate(double rate);

	public native void process(int writeFile);

	public native void destroy();
}
