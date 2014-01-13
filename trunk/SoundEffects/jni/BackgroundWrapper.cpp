#include "BackgroundWrapper.h"
#include "Log.h"
#include "Utils.h"
#include "pthread.h"
#include "WavFile.h"
#include "Mp3Utils.h"
#include <stdio.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

float mGain;
pthread_mutex_t isProcessingFile;
AAssetManager* mgr;
AAsset* asset = NULL;
bool fileFromApp = true;
WavInFile* bgInFile = NULL;
const char* wavBgPath = "/sdcard/bg_user.wav";

void Java_vng_wmb_service_BackgroundEffect_init(JNIEnv* pEnv, jobject pThis,
		jobject assetManager) {
	asset = NULL;
	mGain = float(1.0);
	fileFromApp = true;
	mgr = AAssetManager_fromJava(pEnv, assetManager);
}

void Java_vng_wmb_service_BackgroundEffect_initProcess(JNIEnv* pEnv,
		jobject pThis, jstring bgPath, jdouble gain, jboolean fromApp) {
	pthread_mutex_lock(&isProcessingFile);
	fileFromApp = fromApp;
	if (asset != NULL) {
		AAsset_close(asset);
		asset = NULL;
	}
	if (bgInFile != NULL) {
		delete bgInFile;
		bgInFile = NULL;
	}
	pthread_mutex_unlock(&isProcessingFile);
	mGain = gain;
	const char* bgFilePath = pEnv->GetStringUTFChars(bgPath, NULL);

	if (fileFromApp) {
		asset = AAssetManager_open(mgr, bgFilePath, AASSET_MODE_UNKNOWN);
		if (NULL == asset) {
			Log::info("Can not open file in asset folder");
			return;
		}
		AAsset_seek(asset, 44, SEEK_SET);
	} else {

		/*----------------------------------------------*/
		/* CONVERT MP3 --> WAV BEFORE PROCESSING EFFECT */
		/*----------------------------------------------*/
//		convertMp3ToWav((char*) bgFilePath, (char*) wavBgPath);
//		bgInFile = new WavInFile(wavBgPath);
		/*----------------------------------------------*/
		/*------ CONVERT & PROCESS SIMULTANEOUSLY ------*/
		/*----------------------------------------------*/
		destroyConvertMp3ToWav();
		initConvertMp3ToWav((char*) bgFilePath);
	}

	pEnv->ReleaseStringUTFChars(bgPath, bgFilePath);
}

void Java_vng_wmb_service_BackgroundEffect_destroy(JNIEnv* pEnv,
		jobject pThis) {
	if (asset != NULL) {
		AAsset_close(asset);
		asset = NULL;
	}
	if (bgInFile != NULL) {
		delete bgInFile;
		bgInFile = NULL;
	}
}

int sizeReadBuffer = 0, numByteRead;
float* readBuffer = NULL;
float* tempReadBuffer = NULL;

int processBlockForBackground(short** playerBuffer, int size) {
	float* buffer = convertToFloat((*playerBuffer), size);
	short tempBuffer[size];
	int numRead = 0, c = size;
	float* floatTempBuffer;

	pthread_mutex_lock(&isProcessingFile);
	if (fileFromApp) {
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
				delete[] floatTempBuffer;
				c = c - numRead;
				if (c > 0) {
					AAsset_seek(asset, 44, SEEK_SET);
				}
			} else {
				break;
			}
		}
	} else {

		/*----------------------------------------------*/
		/*------ CONVERT & PROCESS SIMULTANEOUSLY ------*/
		/*----------------------------------------------*/

		short tempBuffer1[1152];
		for (int i = 0; i < size; i++) {
			if (sizeReadBuffer == numByteRead) {
				sizeReadBuffer = convertBlockMp3ToWav(tempBuffer1);
				numByteRead = 0;
				if (readBuffer != NULL) {
					delete[] readBuffer;
					readBuffer = NULL;
				}
				if (sizeReadBuffer == 0) {
					rewindMp3InputFile();
					break;
				}
				readBuffer = convertToFloat(tempBuffer1, sizeReadBuffer);
			}
			buffer[i] = buffer[i] + readBuffer[numByteRead++] * mGain;
		}

		/*----------------------------------------------*/
		/* CONVERT MP3 --> WAV BEFORE PROCESSING EFFECT */
		/*----------------------------------------------*/

//		while (c > 0) {
//			numRead = 0;
//			if (bgInFile != NULL && bgInFile->eof() == 0) {
//
//				if (bgInFile->getNumChannels() == 2) {
//					// FOR STEREO CHANNEL
//					short tempStereoBuffer[c * 2];
//					numRead = bgInFile->read(tempStereoBuffer, c * 2);
//					numRead = numRead / 2;
//					for (int i = 0; i < numRead; i++) {
//						tempBuffer[i] = (tempStereoBuffer[2 * i]
//								+ tempStereoBuffer[2 * i + 1]) / 2;
//					}
//				} else {
//					// FOR MONO CHANNEL
//					numRead = bgInFile->read(tempBuffer, c);
//				}
//			}
//			if (numRead > 0) {
//				floatTempBuffer = convertToFloat(tempBuffer, numRead);
//				for (int i = 0; i < numRead; i++) {
//					buffer[i] += floatTempBuffer[i] * mGain;
//				}
//				delete[] floatTempBuffer;
//				c = c - numRead;
//				if (c > 0) {
//					bgInFile->rewind();
//				}
//			} else {
//				break;
//			}
//		}
	}

	pthread_mutex_unlock(&isProcessingFile);

	delete[] (*playerBuffer);
	(*playerBuffer) = convertToShortBuffer(buffer, size);
	delete[] buffer;
	return size;
}
