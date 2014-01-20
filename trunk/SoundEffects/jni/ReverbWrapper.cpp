#include "ReverbWrapper.h"
#include "ReverbAuda/Reverb.h"
#include "Utils.h"
#include "Log.h"

EffectReverb *pEffectReverb;

void ReverbEffect_init() {
	pEffectReverb = new EffectReverb();
}

void ReverbEffect_initProcess(double mRoomSize, double mDelay,
		double mReverberance, double mHfDamping, double mToneLow,
		double mToneHigh, double mWetGain, double mDryGain) {
	pEffectReverb->setParam(mRoomSize, mDelay, mReverberance, mHfDamping,
			mToneLow, mToneHigh, mWetGain, mDryGain);
	pEffectReverb->InitProcess(SAMPLE_RATE, false);
}

void ReverbEffect_destroy() {
	Log::info("ReverbEffect_destroy");
	if (pEffectReverb != NULL) {
		pEffectReverb->Delete();
		delete pEffectReverb;
		pEffectReverb = NULL;
	}
}

int ReverbEffect_processBlock(short** playerBuffer, int size) {
	float* reverbBuffer[2];
	reverbBuffer[0] = convertToFloat((*playerBuffer), size);
	reverbBuffer[1] = 0;
	pEffectReverb->ProcessOneBlock(size, reverbBuffer);
	short *tempPlayerBuffer = convertToShortBuffer(reverbBuffer[0], size);
	if (*playerBuffer != NULL) {
		delete[] (*playerBuffer);
	}
	(*playerBuffer) = tempPlayerBuffer;
	return size;
}
