#include "RevertWrapper.h"
#include "ReverbAuda/Reverb.h"
#include "AudioService.h"

EffectReverb *pEffectReverb;

JNIEXPORT void JNICALL Java_vng_wmb_service_ReverbEffect_init(JNIEnv *pEnv,
		jobject pThis, jdouble mRoomSize, jdouble mDelay, jdouble mReverberance,
		jdouble mHfDamping, jdouble mToneLow, jdouble mToneHigh,
		jdouble mWetGain, jdouble mDryGain) {
	pEffectReverb = new EffectReverb();
	pEffectReverb->setParam(mRoomSize, mDelay, mReverberance, mHfDamping,
			mToneLow, mToneHigh, mWetGain, mDryGain);
	pEffectReverb->Create(SAMPE_RATE, false);
}

JNIEXPORT void JNICALL Java_vng_wmb_service_ReverbEffect_destroy(JNIEnv * pEnv,
		jobject pThis) {
	if (pEffectReverb != NULL) {
		pEffectReverb->Delete();
		delete pEffectReverb;
		pEffectReverb = NULL;
	}
}

// TODO: Rename
int saturate1(float fvalue, float minval, float maxval) {
	if (fvalue > maxval) {
		fvalue = maxval;
	} else if (fvalue < minval) {
		fvalue = minval;
	}
	return (int) fvalue;
}

// Use for bit_per_sample = 16bit. Convert float buffer to short buffer.
short* convertToShortBuffer1(float* buffer, int numSample) {
	short* shortBuffer = (short*) new char[numSample * 2];
	short *temp2 = (short *) shortBuffer;
	for (int i = 0; i < numSample; i++) {
		short value = (short) saturate1(buffer[i] * 32768.0f, -32768.0f,
				32767.0f);
		temp2[i] = value;
	}
	return shortBuffer;
}

float* convertToFloat(short * buffer, int size) {
	float* floatBuffer = new float[size];
	double conv = 1.0 / 32768.0;
	for (int i = 0; i < size; i++) {
		short value = buffer[i];
		buffer[i] = (float) (value * conv);
	}
	return floatBuffer;
}

int processBlockForReverb(short** playerBuffer, int size) {
	float* reverbBuffer[2];
	reverbBuffer[0] = convertToFloat((*playerBuffer), size);
	reverbBuffer[1] = 0;
	pEffectReverb->ProcessOneBlock(size, reverbBuffer);
	short *tempPlayerBuffer = convertToShortBuffer1(reverbBuffer[0], size);
	if (*playerBuffer != NULL) {
		delete (*playerBuffer);
	}
	if (*playerBuffer != NULL) {
		delete (*playerBuffer);
	}
	(*playerBuffer) = tempPlayerBuffer;
	return size;
}
