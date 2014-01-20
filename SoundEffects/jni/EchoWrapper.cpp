#include "EchoWrapper.h"
#include "EchoEffect/Echo.h"
#include "Utils.h"
#include "Log.h"

EffectEcho *pEffectEcho;

void EchoEffect_init() {
	pEffectEcho = new EffectEcho(SAMPLE_RATE);
}

void EchoEffect_initProcess(double mDelay, double mDecay) {
	pEffectEcho->init(mDelay / 1000, mDecay);
}

void EchoEffect_destroy() {
	Log::info("EchoEffect_destroy");
	if (pEffectEcho != NULL) {
		delete pEffectEcho;
		pEffectEcho = NULL;
	}
}

int EchoEffect_processBlock(short** playerBuffer, int size) {
	float* echoBuffer = convertToFloat((*playerBuffer), size);
	int result = pEffectEcho->ProcessOneBlock(echoBuffer, size);
	short *tempPlayerBuffer = convertToShortBuffer(echoBuffer, result);
	delete[] echoBuffer;
	if ((*playerBuffer) != NULL) {
		delete[] (*playerBuffer);
	}
	(*playerBuffer) = tempPlayerBuffer;
	return result;
}
