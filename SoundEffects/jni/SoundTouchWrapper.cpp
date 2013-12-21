////////////////////////////////////////////////////////////////////////////////
///
/// SoundStretch main routine.
///
/// Author        : Copyright (c) Olli Parviainen
/// Author e-mail : oparviai 'at' iki.fi
/// SoundTouch WWW: http://www.surina.net/soundtouch
///
////////////////////////////////////////////////////////////////////////////////
//
// Last changed  : $Date: 2012-04-04 22:47:28 +0300 (Wed, 04 Apr 2012) $
// File revision : $Revision: 4 $
//
// $Id: main.cpp 141 2012-04-04 19:47:28Z oparviai $
//
////////////////////////////////////////////////////////////////////////////////
//
// License :
//
//  SoundTouch audio processing library
//  Copyright (c) Olli Parviainen
//
//  This library is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public
//  License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//  Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public
//  License along with this library; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
////////////////////////////////////////////////////////////////////////////////

#include <stdexcept>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include "SoundTouch/WavFile.h"
#include "SoundTouch/SoundTouch.h"
#include "SoundTouch/BPMDetect.h"

using namespace soundtouch;
using namespace std;

// Processing chunk size
#define BUFF_SIZE           2048
#define SET_STREAM_TO_BIN_MODE(f) {}

static void openFiles(WavInFile **inFile, WavOutFile **outFile,
		const char* inFilePath, const char* outFilePath) {
	int bits, samplerate, channels;

	*inFile = new WavInFile(inFilePath);

	// ... open output file with same sound parameters
	bits = (int) (*inFile)->getNumBits();
	samplerate = (int) (*inFile)->getSampleRate();
	channels = (int) (*inFile)->getNumChannels();

	if (outFilePath) {
		*outFile = new WavOutFile(outFilePath, samplerate, bits, channels);
	} else {
		*outFile = NULL;
	}
}

// Sets the 'SoundTouch' object up according to input file sound format &
// command line parameters
static void setup(SoundTouch *pSoundTouch, const WavInFile *inFile) {
	int sampleRate;
	int channels;

	sampleRate = (int) inFile->getSampleRate();
	channels = (int) inFile->getNumChannels();
	pSoundTouch->setSampleRate(sampleRate);
	pSoundTouch->setChannels(channels);

	/*pSoundTouch->setTempoChange(params->tempoDelta);
	 pSoundTouch->setPitchSemiTones(params->pitchDelta);
	 pSoundTouch->setRateChange(params->rateDelta);*/

	pSoundTouch->setSetting(SETTING_USE_QUICKSEEK, 0);
	pSoundTouch->setSetting(SETTING_USE_AA_FILTER, 1);

	//param for speech
	pSoundTouch->setSetting(SETTING_SEQUENCE_MS, 40);
	pSoundTouch->setSetting(SETTING_SEEKWINDOW_MS, 15);
	pSoundTouch->setSetting(SETTING_OVERLAP_MS, 8);

}

void changeTempo(SoundTouch *pSoundTouch, float tempo) {
	pSoundTouch->setTempo(tempo);
}

void changePitch(SoundTouch *pSoundTouch, float pitch) {
	pSoundTouch->setPitch(pitch);
}

void changeRate(SoundTouch *pSoundTouch, float rate) {
	pSoundTouch->setRate(rate);
}

// Processes the sound
static void process(SoundTouch *pSoundTouch, WavInFile *inFile,
		WavOutFile *outFile) {
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
	while (inFile->eof() == 0) {
		int num;

		// Read a chunk of samples from the input file
		num = inFile->read(sampleBuffer, BUFF_SIZE);
		nSamples = num / (int) inFile->getNumChannels();

		// Feed the samples into SoundTouch processor
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
			outFile->write(sampleBuffer, nSamples * nChannels);
		} while (nSamples != 0);
	}

	// Now the input file is processed, yet 'flush' few last samples that are
	// hiding in the SoundTouch's internal processing pipeline.
	pSoundTouch->flush();
	do {
		nSamples = pSoundTouch->receiveSamples(sampleBuffer, buffSizeSamples);
		outFile->write(sampleBuffer, nSamples * nChannels);
	} while (nSamples != 0);
}

int mainABC(const int nParams, const char * const paramStr[], char * inFilePath,
		char* outFilePath) {
	WavInFile *inFile;
	WavOutFile *outFile;
	SoundTouch soundTouch;

	// Open input & output files
	openFiles(&inFile, &outFile, inFilePath, outFilePath);

	// Setup the 'SoundTouch' object for processing the sound
	setup(&soundTouch, inFile);

	// clock_t cs = clock();    // for benchmarking processing duration
	// Process the sound
	process(&soundTouch, inFile, outFile);
	// clock_t ce = clock();    // for benchmarking processing duration
	// printf("duration: %lf\n", (double)(ce-cs)/CLOCKS_PER_SEC);

	// Close WAV file handles & dispose of the objects
	delete inFile;
	delete outFile;
	return 0;
}
