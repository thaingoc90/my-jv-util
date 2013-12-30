#include <SoundTouchWrapper.h>
#include <stdexcept>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <Log.h>
#include "WavFile.h"
#include "SoundTouch/SoundTouch.h"
#include "SoundTouch/BPMDetect.h"
#include "AudioService.h"
#include "Utils.h"

using namespace soundtouch;
using namespace std;

#define BUFF_SIZE 10000
WavInFile *inFile;
SoundTouch *pSoundTouch;
const char* inFilePath;
bool playFirst = true;
pthread_mutex_t isProcessingBlock;

jint Java_vng_wmb_service_SoundTouchEffect_init(JNIEnv* pEnv, jobject pThis,
		jstring inPath) {
	inFilePath = pEnv->GetStringUTFChars(inPath, NULL);
	playFirst = true;
	inFile = new WavInFile(inFilePath);
	return 0;
}

void Java_vng_wmb_service_SoundTouchEffect_destroy(JNIEnv* pEnv,
		jobject pThis) {
	if (inFile != NULL) {
		delete inFile;
		inFile = NULL;
	}
	if (pSoundTouch != NULL) {
		delete pSoundTouch;
		pSoundTouch = NULL;
	}
}

void Java_vng_wmb_service_SoundTouchEffect_createSoundTouch(JNIEnv* pEnv,
		jobject pThis, jdouble tempo, jdouble pitch, jdouble rate) {

	Log::info("createSoundTouch");
	if (!thread_done) {
		pthread_kill(playerThread, SIGUSR1);
	}

	pthread_mutex_lock(&isProcessingBlock);
	inFile->rewind();
	pthread_mutex_unlock(&isProcessingBlock);
	if (playFirst || pSoundTouch == NULL) {
		pSoundTouch = new SoundTouch();
		playFirst = false;
	} else {
		pSoundTouch->reset();
	}

	int sampleRate, channels;

	pSoundTouch->setSetting(SETTING_USE_QUICKSEEK, 0);
	pSoundTouch->setSetting(SETTING_USE_AA_FILTER, 1);

	//param for speech
	pSoundTouch->setSetting(SETTING_SEQUENCE_MS, 40);
	pSoundTouch->setSetting(SETTING_SEEKWINDOW_MS, 15);
	pSoundTouch->setSetting(SETTING_OVERLAP_MS, 8);

	sampleRate = SAMPE_RATE;
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

int processBlockForSoundTouch(short** playerBuffer) {
	int nSamples, result = 0;
	pthread_mutex_lock(&isProcessingBlock);
	if (inFile != NULL && inFile->eof() == 0) {
		SAMPLETYPE sampleBuffer[BUFF_SIZE];
		nSamples = inFile->read(sampleBuffer, BUFF_SIZE);
		pSoundTouch->putSamples(sampleBuffer, nSamples);
		short* tempPlayerBuffer = NULL;
		int sizePlayerBuffer = 0;

		do {
			nSamples = pSoundTouch->receiveSamples(sampleBuffer, BUFF_SIZE);
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

		if (*playerBuffer != NULL) {
			delete (*playerBuffer);
		}
		(*playerBuffer) = tempPlayerBuffer;
		result = sizePlayerBuffer;
	}
	pthread_mutex_unlock(&isProcessingBlock);
	return result;
}
