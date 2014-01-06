#include "BackgroundWrapper.h"
#include "Log.h"
#include "Utils.h"
#include "pthread.h"
#include <stdio.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

float mGain;
pthread_mutex_t isProcessingFile;
AAssetManager* mgr;
AAsset* asset = NULL;

void Java_vng_wmb_service_BackgroundEffect_init(JNIEnv* pEnv, jobject pThis,
		jobject assetManager) {
	asset = NULL;
	mGain = float(1.0);
	mgr = AAssetManager_fromJava(pEnv, assetManager);
}

void Java_vng_wmb_service_BackgroundEffect_initProcess(JNIEnv* pEnv,
		jobject pThis, jstring bgPath, jdouble gain) {
	pthread_mutex_lock(&isProcessingFile);
	if (asset != NULL) {
		AAsset_close(asset);
		asset = NULL;
	}
	pthread_mutex_unlock(&isProcessingFile);
	mGain = gain;
	const char* bgFilePath = pEnv->GetStringUTFChars(bgPath, NULL);

	asset = AAssetManager_open(mgr, bgFilePath, AASSET_MODE_UNKNOWN);
	if (NULL == asset) {
		Log::info("Can not open file in asset folder");
		return;
	}
	AAsset_seek(asset, 44, SEEK_SET);

	pEnv->ReleaseStringUTFChars(bgPath, bgFilePath);
}

void Java_vng_wmb_service_BackgroundEffect_destroy(JNIEnv* pEnv,
		jobject pThis) {
	if (asset != NULL) {
		AAsset_close(asset);
		asset = NULL;
	}
}

int processBlockForBackground(short** playerBuffer, int size) {
	float* buffer = convertToFloat((*playerBuffer), size);
	short tempBuffer[size];
	int numRead = 0, c = size;
	float* floatTempBuffer;

	pthread_mutex_lock(&isProcessingFile);
	while (c > 0) {
		numRead = 0;
		if (asset != NULL) {
			numRead = AAsset_read(asset, tempBuffer, c * sizeof(short));
		}
		if (numRead > 0) {
			numRead = numRead / 2;
			floatTempBuffer = convertToFloat(tempBuffer, numRead);
			for (int i = 0; i < numRead; i++) {
				buffer[i] += floatTempBuffer[i] * mGain;
			}
			delete floatTempBuffer;
			c = c - numRead;
			if (c > 0) {
				AAsset_seek(asset, 44, SEEK_SET);
			}
		} else {
			break;
		}
	}
	pthread_mutex_unlock(&isProcessingFile);

	delete (*playerBuffer);
	(*playerBuffer) = convertToShortBuffer(buffer, size);
	delete buffer;
	return size;
}
