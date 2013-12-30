#include "ReverbWrapper.h"
#include "ReverbAuda/Reverb.h"
#include "AudioService.h"
#include "Utils.h"
#include "Log.h"

EffectReverb *pEffectReverb;

JNIEXPORT void JNICALL Java_vng_wmb_service_ReverbEffect_init(JNIEnv *pEnv,
		jobject pThis) {
	pEffectReverb = new EffectReverb();
}

JNIEXPORT void JNICALL Java_vng_wmb_service_ReverbEffect_initProcess(
		JNIEnv *pEnv, jobject pThis, jdouble mRoomSize, jdouble mDelay,
		jdouble mReverberance, jdouble mHfDamping, jdouble mToneLow,
		jdouble mToneHigh, jdouble mWetGain, jdouble mDryGain) {
	pEffectReverb->setParam(mRoomSize, mDelay, mReverberance, mHfDamping,
			mToneLow, mToneHigh, mWetGain, mDryGain);
	pEffectReverb->InitProcess(SAMPE_RATE, false);
}

JNIEXPORT void JNICALL Java_vng_wmb_service_ReverbEffect_destroy(JNIEnv * pEnv,
		jobject pThis) {
	if (pEffectReverb != NULL) {
		pEffectReverb->Delete();
		delete pEffectReverb;
		pEffectReverb = NULL;
	}
}

int processBlockForReverb(short** playerBuffer, int size) {
	float* reverbBuffer[2];
	reverbBuffer[0] = convertToFloat((*playerBuffer), size);
	reverbBuffer[1] = 0;
	pEffectReverb->ProcessOneBlock(size, reverbBuffer);
	short *tempPlayerBuffer = convertToShortBuffer(reverbBuffer[0], size);
	if (*playerBuffer != NULL) {
		delete (*playerBuffer);
	}
	(*playerBuffer) = tempPlayerBuffer;
	return size;
}
