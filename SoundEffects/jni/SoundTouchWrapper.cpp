#include <SoundTouchWrapper.h>
#include <stdexcept>
#include <stdio.h>
#include <string.h>
#include "SoundTouch/SoundTouch.h"
#include "Utils.h"
#include "Log.h"

using namespace soundtouch;
using namespace std;

SoundTouch *pSoundTouch;

jint Java_vng_wmb_service_SoundTouchEffect_init(JNIEnv* pEnv, jobject pThis) {
	pSoundTouch = new SoundTouch();
	return 0;
}

void Java_vng_wmb_service_SoundTouchEffect_destroy(JNIEnv* pEnv,
		jobject pThis) {
	if (pSoundTouch != NULL) {
		delete pSoundTouch;
		pSoundTouch = NULL;
	}
}

void Java_vng_wmb_service_SoundTouchEffect_createSoundTouch(JNIEnv* pEnv,
		jobject pThis, jdouble tempo, jdouble pitch, jdouble rate) {

	Log::info("createSoundTouch");

	pSoundTouch->reset();
	int sampleRate, channels;

	pSoundTouch->setSetting(SETTING_USE_QUICKSEEK, 0);
	pSoundTouch->setSetting(SETTING_USE_AA_FILTER, 1);

	//param for speech
	pSoundTouch->setSetting(SETTING_SEQUENCE_MS, 40);
	pSoundTouch->setSetting(SETTING_SEEKWINDOW_MS, 15);
	pSoundTouch->setSetting(SETTING_OVERLAP_MS, 8);

	sampleRate = SAMPLE_RATE;
	channels = 1;
	pSoundTouch->setSampleRate(sampleRate);
	pSoundTouch->setChannels(channels);

	pSoundTouch->setTempoChange((float) tempo);
	pSoundTouch->setPitchSemiTones((float) pitch);
	pSoundTouch->setRateChange(rate);

}

void Java_vng_wmb_service_SoundTouchEffect_changeTempo(JNIEnv* pEnv,
		jobject pThis, jdouble tempo) {
	pSoundTouch->setTempoChange(tempo);
}

void Java_vng_wmb_service_SoundTouchEffect_changePitch(JNIEnv* pEnv,
		jobject pThis, jdouble pitch) {
	pSoundTouch->setPitchSemiTones((float) pitch);
}

void Java_vng_wmb_service_SoundTouchEffect_changeRate(JNIEnv* pEnv,
		jobject pThis, jdouble rate) {
	pSoundTouch->setRateChange(rate);
}

int processBlockForSoundTouch(short*& playerBuffer, int size) {
	int nSamples, sizePlayerBuffer = 0;
	SAMPLETYPE* sampleBuffer;
	short* tempPlayerBuffer = NULL;

	if (sizeof(SAMPLETYPE) == sizeof(float)) {
		sampleBuffer = (SAMPLETYPE*) convertToFloat(playerBuffer, size);
	} else {
		sampleBuffer = (SAMPLETYPE*) convertToShortBuffer(playerBuffer, size);
	}

	pSoundTouch->putSamples(sampleBuffer, size);
	do {
		nSamples = pSoundTouch->receiveSamples(sampleBuffer, size);
		short *buffer = convertToShortBuffer(sampleBuffer, nSamples);

		if (tempPlayerBuffer == NULL) {
			tempPlayerBuffer = buffer;
		} else {
			short * temp = new short[sizePlayerBuffer + nSamples];
			memcpy(temp, tempPlayerBuffer, sizePlayerBuffer * 2);
			memcpy(temp + sizePlayerBuffer, buffer, nSamples * 2);
			delete buffer;
			delete tempPlayerBuffer;
			tempPlayerBuffer = temp;
		}
		sizePlayerBuffer += nSamples;

	} while (nSamples != 0);

	delete sampleBuffer;
	delete playerBuffer;
	playerBuffer = tempPlayerBuffer;
	return sizePlayerBuffer;
}
