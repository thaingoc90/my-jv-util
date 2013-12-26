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

using namespace soundtouch;
using namespace std;

#define BUFF_SIZE 10000
WavInFile *inFile;
WavOutFile *outFile;
SoundTouch *pSoundTouch;
const char* inFilePath;
const char* outFilePath;
bool playFirst = true;
pthread_mutex_t isProcessingBlock;

jint Java_vng_wmb_service_SoundTouchEffect_init(JNIEnv* pEnv, jobject pThis,
		jstring inPath, jstring outPath) {
	inFilePath = pEnv->GetStringUTFChars(inPath, NULL);
	outFilePath = pEnv->GetStringUTFChars(outPath, NULL);
	playFirst = true;
	int bits, samplerate, channels;

	inFile = new WavInFile(inFilePath);
	bits = (int) (inFile)->getNumBits();
	samplerate = (int) (inFile)->getSampleRate();
	channels = (int) (inFile)->getNumChannels();

	if (outFilePath) {
		outFile = new WavOutFile(outFilePath, samplerate, bits, channels);
	} else {
		outFile = NULL;
	}
	return 0;
}

void Java_vng_wmb_service_SoundTouchEffect_destroy(JNIEnv* pEnv,
		jobject pThis) {
	if (inFile != NULL) {
		delete inFile;
		inFile = NULL;
	}
	if (outFile != NULL) {
		delete outFile;
		outFile = NULL;
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

int saturate(float fvalue, float minval, float maxval) {
	if (fvalue > maxval) {
		fvalue = maxval;
	} else if (fvalue < minval) {
		fvalue = minval;
	}
	return (int) fvalue;
}

// Use for bit_per_sample = 16bit. Convert float buffer to short buffer.
short* convertToShortBuffer(float* buffer, int numSample) {
	short* shortBuffer = (short*) new char[numSample * 2];
	short *temp2 = (short *) shortBuffer;
	for (int i = 0; i < numSample; i++) {
		short value = (short) saturate(buffer[i] * 32768.0f, -32768.0f,
				32767.0f);
		temp2[i] = value;
	}
	return shortBuffer;
}

// Use for bit_per_sample = 16bit. Copy short buffer to new buffer.
short* convertToShortBuffer(short* buffer, int numSample) {
	short* shortBuffer = (short*) new char[numSample * 2];
	memcpy(shortBuffer, buffer, numSample * 2);
	return shortBuffer;
}

// Processes the sound. If writeFile = 1, write to file effect. Else processing normal.
void Java_vng_wmb_service_SoundTouchEffect_writeToFile(JNIEnv* pEnv,
		jobject pThis) {
	int nSamples;
	int nChannels;
	// Note: SAMPLE is float if PC, else is short for mobile.
	SAMPLETYPE sampleBuffer[BUFF_SIZE];

	if ((inFile == NULL) || (outFile == NULL))
		return;

	int inSample = 0, outSample = 0;

	while (inFile->eof() == 0) {
		nSamples = inFile->read(sampleBuffer, BUFF_SIZE);
		inSample += nSamples;
		pSoundTouch->putSamples(sampleBuffer, nSamples);
		do {
			nSamples = pSoundTouch->receiveSamples(sampleBuffer, BUFF_SIZE);
			outSample += nSamples;
			outFile->write(sampleBuffer, nSamples * nChannels);
		} while (nSamples != 0);
	}

	pSoundTouch->flush();
	do {
		nSamples = pSoundTouch->receiveSamples(sampleBuffer, BUFF_SIZE);
		outSample += nSamples;
		outFile->write(sampleBuffer, nSamples * nChannels);
	} while (nSamples != 0);

	Log::info("Sample In %d - Sample Out: %d", inSample, outSample);
	Log::info("SoundTouch - Process finish");
}

int processBlock(short** playerBuffer) {
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
