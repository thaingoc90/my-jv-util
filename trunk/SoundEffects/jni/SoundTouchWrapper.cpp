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

#define BUFF_SIZE 4096
WavInFile *inFile;
WavOutFile *outFile;
SoundTouch *pSoundTouch;
short* tempPlayerBuffer = NULL;
int sizePlayerBuffer = 0;

void openFiles(WavInFile **, WavOutFile **, const char*, const char*);
void setupSoundTouch();

jint Java_vng_wmb_service_SoundTouchEffect_init(JNIEnv* pEnv, jobject pThis,
		jstring inPath, jstring outPath) {
	const char* inFilePath = pEnv->GetStringUTFChars(inPath, NULL);
	const char* outFilePath = pEnv->GetStringUTFChars(outPath, NULL);
	openFiles(&inFile, &outFile, inFilePath, outFilePath);
	pSoundTouch = new SoundTouch();
	setupSoundTouch();

	pEnv->ReleaseStringUTFChars(inPath, inFilePath);
	pEnv->ReleaseStringUTFChars(inPath, outFilePath);
	return 0;
}

void Java_vng_wmb_service_SoundTouchEffect_destroy(JNIEnv* pEnv,
		jobject pThis) {
	delete inFile;
	delete outFile;
	delete pSoundTouch;
}

void openFiles(WavInFile **inFile, WavOutFile **outFile, const char* inFilePath,
		const char* outFilePath) {
	int bits, samplerate, channels;

	*inFile = new WavInFile(inFilePath);
	bits = (int) (*inFile)->getNumBits();
	samplerate = (int) (*inFile)->getSampleRate();
	channels = (int) (*inFile)->getNumChannels();

	if (outFilePath) {
		*outFile = new WavOutFile(outFilePath, samplerate, bits, channels);
	} else {
		*outFile = NULL;
	}
}

void setupSoundTouch() {
	int sampleRate;
	int channels;

	sampleRate = (int) inFile->getSampleRate();
	channels = (int) inFile->getNumChannels();
	pSoundTouch->setSampleRate(sampleRate);
	pSoundTouch->setChannels(channels);

	pSoundTouch->setSetting(SETTING_USE_QUICKSEEK, 0);
	pSoundTouch->setSetting(SETTING_USE_AA_FILTER, 1);

	//param for speech
	pSoundTouch->setSetting(SETTING_SEQUENCE_MS, 40);
	pSoundTouch->setSetting(SETTING_SEEKWINDOW_MS, 15);
	pSoundTouch->setSetting(SETTING_OVERLAP_MS, 8);

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

//Buffer the output of effect. When finish processing of effect, send this buffer to PlayerAudio.
void writeToTempPlayerBuffer(short * buffer, int numSample) {
	if (tempPlayerBuffer == NULL) {
		tempPlayerBuffer = buffer;
	} else {
		short * temp = new short[sizePlayerBuffer + numSample];
		memcpy(temp, tempPlayerBuffer, sizePlayerBuffer * 2);
		memcpy(temp + sizePlayerBuffer, buffer, numSample * 2);
		delete buffer;
		delete tempPlayerBuffer;
		tempPlayerBuffer = temp;
	}
	sizePlayerBuffer += numSample;
}

// Processes the sound. If writeFile = 1, write to file effect. Else processing normal.
void Java_vng_wmb_service_SoundTouchEffect_process(JNIEnv* pEnv, jobject pThis,
		jint writeFile) {
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
			if (writeFile == 0) {
				short *temp = convertToShortBuffer(sampleBuffer, nSamples);
				writeToTempPlayerBuffer(temp, nSamples);
			} else {
				outFile->write(sampleBuffer, nSamples * nChannels);
			}
		} while (nSamples != 0);
	}

	pSoundTouch->flush();
	do {
		nSamples = pSoundTouch->receiveSamples(sampleBuffer, BUFF_SIZE);
		outSample += nSamples;
		if (writeFile == 1) {
			outFile->write(sampleBuffer, nSamples * nChannels);
		} else {
			short *temp = convertToShortBuffer(sampleBuffer, nSamples);
			writeToTempPlayerBuffer(temp, nSamples);
		}
	} while (nSamples != 0);

	// Write to buffer of AudioPlayer when finish.
	if (writeFile == 0) {
		writePlayerBuffer(tempPlayerBuffer, sizePlayerBuffer * sizeof(short));
		delete tempPlayerBuffer;
		tempPlayerBuffer = NULL;
		sizePlayerBuffer = 0;
	}

	Log::info("Sample In %d - Sample Out: %d", inSample, outSample);
	Log::info("SoundTouch - Process finish");
}
