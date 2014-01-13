package vng.wmb.service;

import android.content.res.AssetManager;

public class BackgroundEffect {

	public native void init(AssetManager mgr);

	/**
	 * 
	 * @param mDelay
	 *            (ms)
	 * @param mDecay
	 */
	public native void initProcess(String pathFileBackground, double gain,
			boolean fileFromApp);

	public native void destroy();
}
