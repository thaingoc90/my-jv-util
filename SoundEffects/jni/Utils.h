#ifndef _UTILS_H_
#define _UTILS_H_

#include <stdlib.h>

static const int SAMPLE_RATE = 44100;

// TODO: Rename
static int saturate(float fvalue, float minval, float maxval) {
	if (fvalue > maxval) {
		fvalue = maxval;
	} else if (fvalue < minval) {
		fvalue = minval;
	}
	return (int) fvalue;
}

// Use for bit_per_sample = 16bit. Convert float buffer to short buffer.
static short* convertToShortBuffer(float* buffer, int numSample) {
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
static short* convertToShortBuffer(short* buffer, int numSample) {
	short* shortBuffer = (short*) new char[numSample * 2];
	memcpy(shortBuffer, buffer, numSample * 2);
	return shortBuffer;
}

static float* convertToFloat(short * buffer, int size) {
	float* floatBuffer = new float[size];
	double conv = 1.0 / 32768.0;
	for (int i = 0; i < size; i++) {
		short value = buffer[i];
		floatBuffer[i] = (float) (value * conv);
	}
	return floatBuffer;
}

#endif
