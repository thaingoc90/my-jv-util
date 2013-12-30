/**********************************************************************

 Audacity: A Digital Audio Editor

 Echo.cpp

 Dominic Mazzoni
 Vaughan Johnson (dialog)

 *******************************************************************//**

 \class EffectEcho
 \brief An Effect that causes an echo, variable delay and volume.

 *//****************************************************************//**

 \class EchoDialog
 \brief EchoDialog used with EffectEcho

 *//*******************************************************************/

#include <math.h>
#include <stdlib.h>
#include "Echo.h"

EffectEcho::EffectEcho(int sampleRate) {
	delay = float(1.0);
	decay = float(0.5);
	sample_rate = sampleRate;
}

void EffectEcho::init(float delay, float decay) {
	this->delay = delay;
	this->decay = decay;
}

int EffectEcho::ProcessOneBlock(float* data, int len) {
	int s = 0;
	int blockSize = (int) (delay * sample_rate);
	float *ptr0 = new float[blockSize];
	float *ptr1 = new float[blockSize];
	bool first = true;

	while (s < len) {
		int block = blockSize;
		if (s + block > len) {
			block = len - s;
		}
		memcpy(ptr0, data + s, block * sizeof(float));
		if (!first) {
			for (int i = 0; i < block; i++) {
				ptr0[i] += ptr1[i] * decay;
			}
		}
		float *ptrtemp = ptr0;
		ptr0 = ptr1;
		ptr1 = ptrtemp;
		memcpy(data + s, ptr1, block * sizeof(float));

		first = false;
		s += block;
	}

	delete[] ptr0;
	delete[] ptr1;

	return len;
}
