#include "Config.h"
#include "BackgroundWrapper.h"
#include "Log.h"
#include "Utils.h"
#include "pthread.h"
#include "WavFile.h"
#include "Mp3Utils.h"
#include <stdio.h>

float mGain;
pthread_mutex_t isProcessingFile;
bool fileInAsset = true;
bool isWavFile = true;
WavInFile* bgInFile = NULL;
const char* wavBgPath = "/sdcard/bg_user.wav";

#ifdef _ANDROID_FLAG_

AAssetManager* mgr;
AAsset* asset = NULL;

void BackgroundEffect_init(AAssetManager* pMgr) {
	asset = NULL;
	mGain = float(1.0);
	fileInAsset = true;
	mgr = pMgr;
}

#else

void BackgroundEffect_init() {
	mGain = float(1.0);
	fileInAsset = true;
}

#endif


void BackgroundEffect_initProcess(const char* bgFilePath, double gain,
		bool inAsset, bool wavFile) {
	pthread_mutex_lock(&isProcessingFile);
	fileInAsset = inAsset;
	isWavFile = wavFile;

#ifdef _ANDROID_FLAG_
	if (asset != NULL) {
		AAsset_close(asset);
		asset = NULL;
	}
#endif

	if (bgInFile != NULL) {
		delete bgInFile;
		bgInFile = NULL;
	}
	pthread_mutex_unlock(&isProcessingFile);
	mGain = gain;

	if (fileInAsset) {
#ifdef _ANDROID_FLAG_
		asset = AAssetManager_open(mgr, bgFilePath, AASSET_MODE_UNKNOWN);
		if (NULL == asset) {
			Log::info("Can not open file in asset folder");
			return;
		}
		AAsset_seek(asset, 44, SEEK_SET);
#endif
	} else {
		if (isWavFile) {
			bgInFile = new WavInFile(bgFilePath);
		} else {
			/*----------------------------------------------*/
			/* CONVERT MP3 --> WAV BEFORE PROCESSING EFFECT */
			/*----------------------------------------------*/
			int res = convertMp3ToWav((char*) bgFilePath, (char*) wavBgPath);
			if (res == STATUS_OK) {
				bgInFile = new WavInFile(wavBgPath);
			}

			/*----------------------------------------------*/
			/*------ CONVERT & PROCESS SIMULTANEOUSLY ------*/
			/*----------------------------------------------*/
			//		destroyConvertMp3ToWav();
			//		initConvertMp3ToWav((char*) bgFilePath);
		}

	}
}

void BackgroundEffect_destroy() {
	Log::info("BackgroundEffect_destroy");
#ifdef _ANDROID_FLAG_
	if (asset != NULL) {
		AAsset_close(asset);
		asset = NULL;
	}
#endif
	if (bgInFile != NULL) {
		delete bgInFile;
		bgInFile = NULL;
	}
}

int sizeReadBuffer = 0, numByteRead;
float* readBuffer = NULL;
float* tempReadBuffer = NULL;

int BackgroundEffect_processBlock(short** playerBuffer, int size) {
	float* buffer = convertShortPtrToFloatPtr((*playerBuffer), size);
	short tempBuffer[size];
	int numRead = 0, c = size;
	float* floatTempBuffer;

	pthread_mutex_lock(&isProcessingFile);
	if (fileInAsset) {
#ifdef _ANDROID_FLAG_
		// If file's in asset folder, it must be wav file. Don't process .mp3 file.
		if (isWavFile) {
			while (c > 0) {
				numRead = 0;
				if (asset != NULL) {
					numRead = AAsset_read(asset, tempBuffer, c * sizeof(short));
				}
				if (numRead > 0) {
					numRead = numRead / 2;
					floatTempBuffer = convertShortPtrToFloatPtr(tempBuffer,
							numRead);
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
		}
#endif
	} else {
		if (isWavFile) {
			while (c > 0) {
				numRead = 0;
				if (bgInFile != NULL && bgInFile->eof() == 0) {
					numRead = bgInFile->read(tempBuffer, c);
				}
				if (numRead > 0) {
					floatTempBuffer = convertShortPtrToFloatPtr(tempBuffer,
							numRead);
					for (int i = 0; i < numRead; i++) {
						buffer[i] += floatTempBuffer[i] * mGain;
					}
					delete[] floatTempBuffer;
					c = c - numRead;
					if (c > 0) {
						bgInFile->rewind();
					}
				} else {
					break;
				}
			}
		} else {

			/*----------------------------------------------*/
			/*------ CONVERT & PROCESS SIMULTANEOUSLY ------*/
			/*----------------------------------------------*/

			//		short tempBuffer1[1152];
			//		for (int i = 0; i < size; i++) {
			//			if (sizeReadBuffer == numByteRead) {
			//				sizeReadBuffer = convertBlockMp3ToWav(tempBuffer1);
			//				numByteRead = 0;
			//				if (readBuffer != NULL) {
			//					delete[] readBuffer;
			//					readBuffer = NULL;
			//				}
			//				if (sizeReadBuffer == 0) {
			//					rewindMp3InputFile();
			//					break;
			//				}
			//				readBuffer = convertToFloat(tempBuffer1, sizeReadBuffer);
			//			}
			//			buffer[i] = buffer[i] + readBuffer[numByteRead++] * mGain;
			//		}
			/*----------------------------------------------*/
			/* CONVERT MP3 --> WAV BEFORE PROCESSING EFFECT */
			/*----------------------------------------------*/

			while (c > 0) {
				numRead = 0;
				if (bgInFile != NULL && bgInFile->eof() == 0) {

					if (bgInFile->getNumChannels() == 2) {
						// FOR STEREO CHANNEL
						short tempStereoBuffer[c * 2];
						numRead = bgInFile->read(tempStereoBuffer, c * 2);
						numRead = numRead / 2;
						for (int i = 0; i < numRead; i++) {
							tempBuffer[i] = (tempStereoBuffer[2 * i]
									+ tempStereoBuffer[2 * i + 1]) / 2;
						}
					} else {
						// FOR MONO CHANNEL
						numRead = bgInFile->read(tempBuffer, c);
					}
				}
				if (numRead > 0) {
					floatTempBuffer = convertShortPtrToFloatPtr(tempBuffer,
							numRead);
					for (int i = 0; i < numRead; i++) {
						buffer[i] += floatTempBuffer[i] * mGain;
					}
					delete[] floatTempBuffer;
					c = c - numRead;
					if (c > 0) {
						bgInFile->rewind();
					}
				} else {
					break;
				}
			}
		}

	}

	pthread_mutex_unlock(&isProcessingFile);

	delete[] (*playerBuffer);
	(*playerBuffer) = convertFloatPtrToShortPtr(buffer, size);
	delete[] buffer;
	return size;
}
