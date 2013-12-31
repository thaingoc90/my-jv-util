#include "EchoWrapper.h"
#include "EchoEffect/Echo.h"
#include "AudioService.h"
#include "Utils.h"
#include "Log.h"

EffectEcho *pEffectEcho;

JNIEXPORT void JNICALL Java_vng_wmb_service_EchoEffect_init(JNIEnv *pEnv,
		jobject pThis) {
	Log::info("Init Echo");
	pEffectEcho = new EffectEcho(SAMPLE_RATE);
}

JNIEXPORT void JNICALL Java_vng_wmb_service_EchoEffect_initProcess(JNIEnv *pEnv,
		jobject pThis, jdouble mDelay, jdouble mDecay) {
	Log::info("Init Process Echo");
	pEffectEcho->init(mDelay / 1000, mDecay);
}

JNIEXPORT void JNICALL Java_vng_wmb_service_EchoEffect_destroy(JNIEnv * pEnv,
		jobject pThis) {
	Log::info("Destroy Echo");
	if (pEffectEcho != NULL) {
		delete pEffectEcho;
		pEffectEcho = NULL;
	}
}

int processBlockForEcho(short*& playerBuffer, int size) {
	float* echoBuffer = convertToFloat(playerBuffer, size);
	int result = pEffectEcho->ProcessOneBlock(echoBuffer, size);
	short *tempPlayerBuffer = convertToShortBuffer(echoBuffer, result);
	delete echoBuffer;
	if (playerBuffer != NULL) {
		delete playerBuffer;
	}
	playerBuffer = tempPlayerBuffer;
	return result;
}
