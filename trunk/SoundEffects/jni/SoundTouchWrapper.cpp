#include "SoundTouchWrapper.h"
#include <stdexcept>
#include <stdio.h>
#include <string.h>
#include "SoundTouch/SoundTouch.h"
#include "Utils.h"
#include "Log.h"
#include <pthread.h>

using namespace soundtouch;
using namespace std;

SoundTouch *pSoundTouch;
pthread_mutex_t isProcessingSoundTouch;

void SoundTouchEffect_init() {
	pSoundTouch = new SoundTouch();
}

void SoundTouchEffect_destroy() {
	Log::info("SoundTouchEffect_destroy");
	pthread_mutex_lock(&isProcessingSoundTouch);
	if (pSoundTouch != NULL) {
		delete pSoundTouch;
		pSoundTouch = NULL;
	}
	pthread_mutex_unlock(&isProcessingSoundTouch);
}

void SoundTouchEffect_initProcess(double tempo, double pitch, double rate) {

	pthread_mutex_lock(&isProcessingSoundTouch);
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
	pthread_mutex_unlock(&isProcessingSoundTouch);
}

int SoundTouchEffect_processBlock(short** playerBuffer, int size) {
	int nSamples, sizePlayerBuffer = 0;
	SAMPLETYPE* sampleBuffer;
	short* tempPlayerBuffer = NULL;

	if (sizeof(SAMPLETYPE) == sizeof(float)) {
		sampleBuffer = (SAMPLETYPE*) convertShortPtrToFloatPtr((*playerBuffer), size);
	} else {
		sampleBuffer = (SAMPLETYPE*) duplicateShortPtr((*playerBuffer), size);
	}

	pthread_mutex_lock(&isProcessingSoundTouch);
	pSoundTouch->putSamples(sampleBuffer, size);
	do {
		nSamples = pSoundTouch->receiveSamples(sampleBuffer, size);
		short* outShortBuffer;
		if (sizeof(SAMPLETYPE) == sizeof(float)) {
			outShortBuffer = convertFloatPtrToShortPtr((float*) sampleBuffer, nSamples);
		} else {
			outShortBuffer = duplicateShortPtr((short*) sampleBuffer, nSamples);
		}

		if (tempPlayerBuffer == NULL) {
			tempPlayerBuffer = outShortBuffer;
		} else if (nSamples > 0) {
			short * resBuffer = new short[sizePlayerBuffer + nSamples];
			memcpy(resBuffer, tempPlayerBuffer, sizePlayerBuffer * 2);
			memcpy(resBuffer + sizePlayerBuffer, outShortBuffer, nSamples * 2);
			delete[] outShortBuffer;
			delete[] tempPlayerBuffer;
			tempPlayerBuffer = resBuffer;
		}
		sizePlayerBuffer += nSamples;

	} while (nSamples != 0);
	pthread_mutex_unlock(&isProcessingSoundTouch);

	delete[] sampleBuffer;
	delete[] (*playerBuffer);
	(*playerBuffer) = tempPlayerBuffer;
	return sizePlayerBuffer;
}
