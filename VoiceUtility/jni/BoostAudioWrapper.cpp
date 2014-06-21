#include "BoostAudioWrapper.h"
#include "math.h"

int BoostAudioWrapper_processBlockByGain(short* buffer, int size,
		double gain) {
	short sample = 0;
	double dsample = 0;
	for (int i = 0; i < size; i++) {
		sample = buffer[i];
		dsample = (double) sample * gain;
		if (dsample > 32767.0) {
			dsample = 32767.0;
		} else if (dsample < -32768.0) {
			dsample = -32768.0;
		}
		buffer[i] = (short) dsample;
	}
	return size;
}

int BoostAudioWrapper_processBlockByDb(short* buffer, int size,
		double decibel) {
	short sample = 0;
	double dsample = 0;
	for (int i = 0; i < size; i++) {
		sample = buffer[i];
		dsample = (double) sample * pow(10.0f, decibel * 0.05f);
		if (dsample > 32767.0) {
			dsample = 32767.0;
		} else if (dsample < -32768.0) {
			dsample = -32768.0;
		}
		buffer[i] = (short) dsample;
	}
	return size;
}
