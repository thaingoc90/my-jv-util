package vng.wmb.service;

public class EchoEffect {

	public native void init();

	/**
	 * 
	 * @param mDelay
	 *            (ms)
	 * @param mDecay
	 */
	public native void initProcess(double mDelay, double mDecay);

	public native void destroy();
}
