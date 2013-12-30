package vng.wmb.service;

public class ReverbEffect {

	public native void init();

	public native void initProcess(double mRoomSize, double mDelay,
			double mReverberance, double mHfDamping, double mToneLow,
			double mToneHigh, double mWetGain, double mDryGain);

	public native void destroy();

}
