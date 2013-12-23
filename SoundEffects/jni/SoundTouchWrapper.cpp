#include <SoundTouchWrapper.h>
#include <stdexcept>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <Log.h>
#include "SoundTouch/WavFile.h"
#include "SoundTouch/SoundTouch.h"
#include "SoundTouch/BPMDetect.h"
#include "AudioService.h"

using namespace soundtouch;
using namespace std;

// Processing chunk size
#define BUFF_SIZE           2048

WavInFile *inFile;
WavOutFile *outFile;
static SoundTouch *pSoundTouch;

void openFiles(WavInFile **, WavOutFile **, const char*, const char*);
void setupSoundTouch();

jint Java_vng_wmb_service_SoundTouchEffect_init(JNIEnv* pEnv, jobject pThis,
		jstring inPath, jstring outPath) {
	Log::info("SoundTouch_init");
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

// Processes the sound
void Java_vng_wmb_service_SoundTouchEffect_process(JNIEnv* pEnv, jobject pThis,
		jint writeFile) {
	int nSamples;
	int nChannels;
	int buffSizeSamples;
	SAMPLETYPE sampleBuffer[BUFF_SIZE];

	if ((inFile == NULL) || (outFile == NULL))
		return;  // nothing to do.

	nChannels = (int) inFile->getNumChannels();
	assert(nChannels > 0);
	buffSizeSamples = BUFF_SIZE / nChannels;

	// Process samples read from the input file
	int inSample = 0, outSample = 0;
	while (inFile->eof() == 0) {
		int num;

		int i = 1;
		num = inFile->read(sampleBuffer, BUFF_SIZE);
		nSamples = num / (int) inFile->getNumChannels();
		inSample += nSamples;

		pSoundTouch->putSamples(sampleBuffer, nSamples);

		// Read ready samples from SoundTouch processor & write them output file.
		// NOTES:
		// - 'receiveSamples' doesn't necessarily return any samples at all
		//   during some rounds!
		// - On the other hand, during some round 'receiveSamples' may have more
		//   ready samples than would fit into 'sampleBuffer', and for this reason
		//   the 'receiveSamples' call is iterated for as many times as it
		//   outputs samples.
		do {
			nSamples = pSoundTouch->receiveSamples(sampleBuffer,
					buffSizeSamples);
			outSample += nSamples;
			if (writeFile == 1) {
				outFile->write(sampleBuffer, nSamples * nChannels);
			} else {
				writePlayerBuffer(sampleBuffer, nSamples * sizeof(int16_t));
			}
		} while (nSamples != 0);
	}

	// Now the input file is processed, yet 'flush' few last samples that are
	// hiding in the SoundTouch's internal processing pipeline.
	pSoundTouch->flush();
	do {
		nSamples = pSoundTouch->receiveSamples(sampleBuffer, buffSizeSamples);
		outSample += nSamples;
		if (writeFile == 1) {
			outFile->write(sampleBuffer, nSamples * nChannels);
		} else {
			writePlayerBuffer(sampleBuffer, nSamples * sizeof(int16_t));
		}
		outFile->write(sampleBuffer, nSamples * nChannels);
	} while (nSamples != 0);

	Log::info("In %d - Out: %d", inSample, outSample);

	Log::info("SoundTouch - Process finish");
}
