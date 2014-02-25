#ifndef _UTILS_H_
#define _UTILS_H_

#include <string.h>
#include <stdlib.h>

static const int SAMPLE_RATE = 44100;

const static char* pathWavFileTemp = "/sdcard/voice.wav";
const static char* pathAmrFileTemp = "/sdcard/voice.amr";
const static char* pathMp3FileTemp = "/sdcard/voice.mp3";

const static int STATUS_OK = 1;
const static int STATUS_FAIL = 0;
const static int STATUS_NOT_SUPPORT = 2;

const static int MAX_TIME_BUFFER_RECORD = 50; // ms

static int floatToInt(float fvalue, float minval, float maxval) {
	if (fvalue > maxval) {
		fvalue = maxval;
	} else if (fvalue < minval) {
		fvalue = minval;
	}
	return (int) fvalue;
}

// Use for bit_per_sample = 16bit. Convert float buffer to short buffer.
static short* convertFloatPtrToShortPtr(float* buffer, int numSample) {
	short* shortBuffer = (short*) new char[numSample * 2];
	short* temp2 = (short*) shortBuffer;
	for (int i = 0; i < numSample; i++) {
		short value = (short) floatToInt(buffer[i] * 32768.0f, -32768.0f,
				32767.0f);
		temp2[i] = value;
	}
	return shortBuffer;
}

// Use for bit_per_sample = 16bit. Copy short buffer to new buffer.
static short* duplicateShortPtr(short* buffer, int numSample) {
	short* shortBuffer = (short*) new char[numSample * 2];
	memcpy(shortBuffer, buffer, numSample * 2);
	return shortBuffer;
}

static float* convertShortPtrToFloatPtr(short* buffer, int size) {
	float* floatBuffer = new float[size];
	double conv = 1.0 / 32768.0;
	for (int i = 0; i < size; i++) {
		short value = buffer[i];
		floatBuffer[i] = (float) (value * conv);
	}
	return floatBuffer;
}

#endif
