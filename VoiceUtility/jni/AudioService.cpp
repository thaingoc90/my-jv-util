#include "Config.h"
#include <stdio.h>
#include <assert.h>
#include <ctime>
#include "AudioService.h"
#include "VoiceEffectLib.h"
#include "Log.h"
#include "WavFile.h"
#include "Utils.h"
#include "BoostAudioWrapper.h"
#include "NSWrapper.h"

#ifdef _AUDIO_SERVICE_USE_OPENSLES_

#include <pthread.h>
#include <SLES/OpenSLES.h>

#ifdef _ANDROID_FLAG_

#include <SLES/OpenSLES_Android.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>

#define W_SLBufferQueueItf SLAndroidSimpleBufferQueueItf
#define W_SL_IID_BUFFERQUEUE SL_IID_ANDROIDSIMPLEBUFFERQUEUE
#define W_SLBufferQueueState SLAndroidSimpleBufferQueueState

#else

#define W_SLBufferQueueItf SLBufferQueueItf
#define W_SL_IID_BUFFERQUEUE SL_IID_BUFFERQUEUE
#define W_SLBufferQueueState SLBufferQueueState
#endif

/*
 * VARIABLE OF OPENSLES
 */
SLObjectItf engineObj = NULL;
SLEngineItf engineItf;
SLDataFormat_PCM lDataFormat;
// RECORDER
SLObjectItf recorderObj = NULL;
SLRecordItf recorderItf;
W_SLBufferQueueItf recorderQueue;
// PLAYER
SLObjectItf outputMixObj;
SLObjectItf playerObj = NULL;
SLPlayItf playerItf;
W_SLBufferQueueItf playerQueue;
SLVolumeItf playerVolume;
SLAndroidConfigurationItf playerConfig;
SLEnvironmentalReverbSettings reverbSettings =
		SL_I3DL2_ENVIRONMENT_PRESET_BATHROOM;
SLEnvironmentalReverbItf outputMixEnvReverb = NULL;

static int32_t recordSize = SAMPLE_RATE * MAX_TIME_BUFFER_RECORD / 1000;
int16_t* recordBuffer1;
int16_t* recordBuffer2;
int16_t* activeRecordBuffer;
int16_t* playerBuffer1;
int16_t* playerBuffer2;
int16_t* activePlayerBuffer;
bool isRecordingFlag = false;
int enqueuedFlag = 0;
std::clock_t startTimeRecord;
SLint32 stopTimeRecord = 0;

WavOutFile* outFileTemp;
bool threadDoneFlag = true;
pthread_t playbackFileThread;

// Use to write File after 1000ms (access IO more performance). Now, not use.
// ---------------Start----------------
static int32_t sizeOfWriteFileBuffer = 1000 * SAMPLE_RATE / 1000;
int16_t* writeFileBuffer;
int32_t currentSizeBuffer = 0;
// ---------------End----------------

// INTERFACE OF METHOD
void recorderCallback(W_SLBufferQueueItf, void*);
void playerCallback(W_SLBufferQueueItf, void*);
void writeFile(short* temp, int size, bool isStopping);
void* playbackFileFunc(void * param);
int checkError(SLresult);

/****************************************************************/

int AudioService_init() {
	Log::info("Init Audio Service");
	SLresult res;
	const SLuint32 lEngineIIDCount = 1;
	const SLInterfaceID lEngineIIDs[] = { SL_IID_ENGINE };
	const SLboolean lEngineReqs[] = { SL_BOOLEAN_TRUE };

	res = slCreateEngine(&engineObj, 0, NULL, lEngineIIDCount, lEngineIIDs,
			lEngineReqs);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*engineObj)->Realize(engineObj, SL_BOOLEAN_FALSE );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*engineObj)->GetInterface(engineObj, SL_IID_ENGINE, &engineItf);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	if (CHANNEL == 2) {
		lDataFormat.channelMask = SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT;
	} else {
		lDataFormat.channelMask = SL_SPEAKER_FRONT_CENTER;
	}
	switch (SAMPLE_RATE) {
	case 44100:
		lDataFormat.samplesPerSec = SL_SAMPLINGRATE_44_1;
		break;
	case 32000:
		lDataFormat.samplesPerSec = SL_SAMPLINGRATE_32;
		break;
	case 16000:
		lDataFormat.samplesPerSec = SL_SAMPLINGRATE_16;
		break;
	case 8000:
		lDataFormat.samplesPerSec = SL_SAMPLINGRATE_8;
		break;
	default:
		lDataFormat.samplesPerSec = SL_SAMPLINGRATE_44_1;
		break;
	}
	lDataFormat.bitsPerSample = SL_PCMSAMPLEFORMAT_FIXED_16;
	lDataFormat.containerSize = SL_PCMSAMPLEFORMAT_FIXED_16;
	lDataFormat.formatType = SL_DATAFORMAT_PCM;
	lDataFormat.numChannels = SL_AUDIOCHANMODE_MP3_MONO;
	lDataFormat.endianness = SL_BYTEORDER_LITTLEENDIAN;

	recordSize = SAMPLE_RATE * MAX_TIME_BUFFER_RECORD / 1000;
	sizeOfWriteFileBuffer = 1000 * SAMPLE_RATE / 1000;

	recordBuffer1 = new int16_t[recordSize];
	recordBuffer2 = new int16_t[recordSize];
	activeRecordBuffer = recordBuffer1;
	writeFileBuffer = new int16_t[sizeOfWriteFileBuffer];

	return STATUS_OK;
}

void AudioService_destroy() {
	Log::info("Destroy Audio Service");

	if (recorderObj != NULL) {
		(*recorderObj)->Destroy(recorderObj);
		recorderObj = NULL;
		recorderItf = NULL;
		recorderQueue = NULL;
	}
	if (engineObj != NULL) {
		(*engineObj)->Destroy(engineObj);
		engineObj = NULL;
		engineItf = NULL;
	}

	delete[] recordBuffer1;
	delete[] recordBuffer2;
	delete[] writeFileBuffer;
}

/**
 *Init Recorder
 */
int AudioService_initRecorder() {
	Log::info("Init Recorder");
	SLresult res;
	const SLuint32 lEngineRecorderIIDCount = 2;
	const SLInterfaceID lEngineRecorderIIDs[] = { SL_IID_RECORD,
			W_SL_IID_BUFFERQUEUE };
	const SLboolean lEngineRecorderReqs[] = { SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE };

#ifdef _ANDROID_FLAG_
	SLDataLocator_AndroidSimpleBufferQueue lDataLocatorOut = {
			SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2 };
#else
	SLDataLocator_BufferQueue lDataLocatorOut = {SL_DATALOCATOR_BUFFERQUEUE, 2};
#endif

	SLDataLocator_IODevice lDataLocatorIn;
	lDataLocatorIn.locatorType = SL_DATALOCATOR_IODEVICE;
	lDataLocatorIn.deviceType = SL_IODEVICE_AUDIOINPUT;
	lDataLocatorIn.deviceID = SL_DEFAULTDEVICEID_AUDIOINPUT;
	lDataLocatorIn.device = NULL;

	SLDataSink lDataSink;
	lDataSink.pLocator = &lDataLocatorOut;
	lDataSink.pFormat = &lDataFormat;
	SLDataSource lDataSource;
	lDataSource.pLocator = &lDataLocatorIn;
	lDataSource.pFormat = NULL;

	res = (*engineItf)->CreateAudioRecorder(engineItf, &recorderObj,
			&lDataSource, &lDataSink, lEngineRecorderIIDCount,
			lEngineRecorderIIDs, lEngineRecorderReqs);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*recorderObj)->Realize(recorderObj, SL_BOOLEAN_FALSE );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*recorderObj)->GetInterface(recorderObj, SL_IID_RECORD,
			&recorderItf);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*recorderObj)->GetInterface(recorderObj, W_SL_IID_BUFFERQUEUE,
			&recorderQueue);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*recorderQueue)->RegisterCallback(recorderQueue, recorderCallback,
			NULL);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	// SET VARIABLE
	stopTimeRecord = 0;
	enqueuedFlag = 0;
	return STATUS_OK;
}

/**
 * Destroy Recorder
 */
void AudioService_destroyRecorder() {
	Log::info("Destroy Recorder");
	if (recorderObj != NULL) {
		(*recorderObj)->Destroy(recorderObj);
		recorderObj = NULL;
		recorderItf = NULL;
		recorderQueue = NULL;
	}
}

/**
 * Start record. Use 2 buffers.
 */
int AudioService_startRecord(char* pathWavFileTemp) {
	Log::info("Start record");

	outFileTemp = new WavOutFile(pathWavFileTemp, SAMPLE_RATE, 16, 1);
	currentSizeBuffer = 0;
	SLresult res;
	SLuint32 state;

	(*recorderObj)->GetState(recorderObj, &state);
	if (state == SL_OBJECT_STATE_REALIZED ) {
		NS_init(3, SAMPLE_RATE);

		res = (*recorderQueue)->Clear(recorderQueue);
		if (checkError(res) != STATUS_OK)
			return STATUS_FAIL;
		memset(recordBuffer1, 0, recordSize * sizeof(short));
		memset(recordBuffer2, 0, recordSize * sizeof(short));
		memset(writeFileBuffer, 0, sizeOfWriteFileBuffer * sizeof(int16_t));

		if (enqueuedFlag == 0) {
			res = (*recorderQueue)->Enqueue(recorderQueue, activeRecordBuffer,
					recordSize * sizeof(int16_t));
			enqueuedFlag++;
		}
		res = (*recorderItf)->SetRecordState(recorderItf,
				SL_RECORDSTATE_RECORDING );
		if (checkError(res) != STATUS_OK)
			return STATUS_FAIL;
		isRecordingFlag = true;
		startTimeRecord = std::clock();
	}
	return STATUS_OK;
}

int AudioService_stopRecord() {
	Log::info("Stop Record");
	SLresult res;
	if (isRecordingFlag) {
		isRecordingFlag = false;
		SLint32 duration = (std::clock() - startTimeRecord) / 1000;
		if (duration < 0) {
			duration = 0;
		}
		if (duration >= MAX_TIME_BUFFER_RECORD) {
			duration = duration % MAX_TIME_BUFFER_RECORD;
		}
		res = (*recorderItf)->SetRecordState(recorderItf,
				SL_RECORDSTATE_STOPPED);
		if (checkError(res) != STATUS_OK)
			return STATUS_FAIL;
		// Write to file temp
		int32_t size = (duration * SAMPLE_RATE) / 1000;
		if (stopTimeRecord == 0) {
			writeFile(activeRecordBuffer, size, true);
		} else {
			int16_t * temp = new int16_t[size];
			if (activeRecordBuffer == recordBuffer1) {
				memcpy(temp,
						recordBuffer1 + stopTimeRecord * SAMPLE_RATE / 1000,
						size * 2);
			} else {
				memcpy(temp,
						recordBuffer2 + stopTimeRecord * SAMPLE_RATE / 1000,
						size * 2);
			}

			writeFile(temp, size, true);
			delete[] temp;
		}
		delete outFileTemp;
		stopTimeRecord =
				(stopTimeRecord == 0) ? duration : stopTimeRecord + duration;
		(*recorderQueue)->Clear(recorderQueue);
	}
	NS_destroy();
	return STATUS_OK;
}
/**
 * Callback is called when buffer is full.
 * It will enqueue the other buffer & write data in this buffer to file.
 */
void recorderCallback(W_SLBufferQueueItf slBuffer, void *pContext) {
	long callbackTime;

	startTimeRecord = std::clock();
	callbackTime = MAX_TIME_BUFFER_RECORD - stopTimeRecord;
	stopTimeRecord = 0;

	if (activeRecordBuffer == recordBuffer1) {
		activeRecordBuffer = recordBuffer2;
	} else {
		activeRecordBuffer = recordBuffer1;
	}
	(*recorderQueue)->Enqueue(recorderQueue, activeRecordBuffer,
			recordSize * sizeof(int16_t));

	int32_t size = (callbackTime * SAMPLE_RATE) / 1000;
	int16_t * temp = new int16_t[size];
	if (activeRecordBuffer == recordBuffer1) {
		memcpy(temp, recordBuffer2 + (recordSize - size), size * 2);
	} else {
		memcpy(temp, recordBuffer1 + (recordSize - size), size * 2);
	}
	VoiceEffect_cbDrawSoundWave(temp, size);
	writeFile(temp, size, false);
	delete[] temp;
}

// Write to file 'outFileTemp'
void writeFile(short* temp, int size, bool isStopping) {
	// Write directly buffer to file
	//	outFileTemp->write(temp, size);

	NS_Process(temp, size);
	BoostAudioWrapper_processBlockByDb(temp, size, 5);

	// Use a temp buffer. Just write file when bufferWriteFile full or isStopping is true
	if (currentSizeBuffer + size >= sizeOfWriteFileBuffer) {
		int remainSize = sizeOfWriteFileBuffer - currentSizeBuffer;
		memcpy(writeFileBuffer + currentSizeBuffer, temp, remainSize * 2);
//		NS_Process(writeFileBuffer, sizeOfWriteFileBuffer);
		outFileTemp->write(writeFileBuffer, sizeOfWriteFileBuffer);
		currentSizeBuffer = 0;
		if (remainSize < size) {
			memcpy(writeFileBuffer, temp + remainSize, (size - remainSize) * 2);
			currentSizeBuffer = size - remainSize;
		}
	} else {
		memcpy(writeFileBuffer + currentSizeBuffer, temp, size * 2);
		currentSizeBuffer += size;
	}
	if (isStopping) {
//		NS_Process(writeFileBuffer, currentSizeBuffer);
		outFileTemp->write(writeFileBuffer, currentSizeBuffer);
	}
}

// ----------------------------------------------------------------
// ----------------------FOR PLAYER -------------------------------
// ----------------------------------------------------------------

int AudioService_initPlayer() {
	Log::info("Init Player");
	SLresult res;

	const SLuint32 lOutputMixIIDCount = 1;
	const SLInterfaceID lOutputMixIIDs[] = {SL_IID_ENVIRONMENTALREVERB };
	const SLboolean lOutputMixReqs[] = {SL_BOOLEAN_FALSE };
	res = (*engineItf)->CreateOutputMix(engineItf, &outputMixObj,
			lOutputMixIIDCount, lOutputMixIIDs, lOutputMixReqs);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*outputMixObj)->Realize(outputMixObj, SL_BOOLEAN_FALSE);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

//	res = (*outputMixObj)->GetInterface(outputMixObj, SL_IID_ENVIRONMENTALREVERB,
//	            &outputMixEnvReverb);
//	if (checkError(res) != STATUS_OK)
//			return checkError(res);

#ifdef _ANDROID_FLAG_
	SLDataLocator_AndroidSimpleBufferQueue lDataLocatorIn = {
			SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2 };
#else
	SLDataLocator_BufferQueue lDataLocatorIn = {SL_DATALOCATOR_BUFFERQUEUE, 2};
#endif

	SLDataSource lDataSource;
	lDataSource.pLocator = &lDataLocatorIn;
	lDataSource.pFormat = &lDataFormat;

	SLDataLocator_OutputMix lDataLocatorOut;
	lDataLocatorOut.locatorType = SL_DATALOCATOR_OUTPUTMIX;
	lDataLocatorOut.outputMix = outputMixObj;
	SLDataSink lDataSink;
	lDataSink.pLocator = &lDataLocatorOut;
	lDataSink.pFormat = NULL;
	const SLuint32 lSoundPlayerIIDCount = 4;
	const SLInterfaceID lSoundPlayerIIDs[] = { SL_IID_PLAY, SL_IID_VOLUME,
			SL_IID_BUFFERQUEUE, SL_IID_ANDROIDCONFIGURATION };
	const SLboolean lSoundPlayerReqs[] = { SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE,
			SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE };

	res = (*engineItf)->CreateAudioPlayer(engineItf, &playerObj, &lDataSource,
			&lDataSink, lSoundPlayerIIDCount, lSoundPlayerIIDs,
			lSoundPlayerReqs);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

//	SLint32 streamType = SL_ANDROID_STREAM_VOICE;
	SLint32 streamType = SL_ANDROID_STREAM_MEDIA;

	res = (*playerObj)->GetInterface(playerObj, SL_IID_ANDROIDCONFIGURATION,
			&playerConfig);
	if (checkError(res) != STATUS_OK) {
		return checkError(res);
	}
	res = (*playerConfig)->SetConfiguration(playerConfig,
			SL_ANDROID_KEY_STREAM_TYPE, &streamType, sizeof(SLint32));
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*playerObj)->Realize(playerObj, SL_BOOLEAN_FALSE );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*playerObj)->GetInterface(playerObj, SL_IID_PLAY, &playerItf);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*playerObj)->GetInterface(playerObj, SL_IID_VOLUME, &playerVolume);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	SLmillibel maxVol;
	res = (*playerVolume)->GetMaxVolumeLevel(playerVolume, &maxVol);
	if (checkError(res) == STATUS_OK) {
		res = (*playerVolume)->SetVolumeLevel(playerVolume, maxVol);
	}

	res = (*playerObj)->GetInterface(playerObj, W_SL_IID_BUFFERQUEUE,
			&playerQueue);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*playerQueue)->RegisterCallback(playerQueue, playerCallback, NULL);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*playerItf)->SetCallbackEventsMask(playerItf,
			SL_PLAYEVENT_HEADATEND);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*playerQueue)->Clear(playerQueue);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	return STATUS_OK;
}

void AudioService_destroyPlayer() {
	Log::info("Destroy Player");

	if (playerObj != NULL) {
		(*playerObj)->Destroy(playerObj);
		playerObj = NULL;
		playerItf = NULL;
		playerQueue = NULL;
		playerVolume = NULL;
	}
	if (outputMixObj != NULL) {
		(*outputMixObj)->Destroy(outputMixObj);
		outputMixObj = NULL;
	}

}

/**
 * Start player. Set state of PlayerObj to SL_PLAYSTATE_PLAYING.
 */
int AudioService_startPlayer() {
	Log::info("Start Player");

	SLresult lRes;
	SLuint32 lPlayerState;
	(*playerObj)->GetState(playerObj, &lPlayerState);
	if (lPlayerState == SL_OBJECT_STATE_REALIZED) {
		lRes = (*playerQueue)->Clear(playerQueue);
		if (checkError(lRes) != STATUS_OK)
			return STATUS_FAIL;
		lRes = (*playerItf)->SetPlayState(playerItf, SL_PLAYSTATE_PLAYING);
		if (checkError(lRes) != STATUS_OK)
			return STATUS_FAIL;
	}
	return STATUS_OK;
}

/**
 * Stop player
 */
int AudioService_stopPlayer() {
	Log::info("Stop Player");
	SLresult lRes;
	(*playerItf)->SetPlayState(playerItf, SL_PLAYSTATE_STOPPED);
	lRes = (*playerQueue)->Clear(playerQueue);
	if (checkError(lRes) != STATUS_OK)
		return STATUS_FAIL;
	(*playerQueue)->Clear(playerQueue);
	return STATUS_OK;
}

/**
 * Play tempFile. Read data from function "processBlock" & play it.
 */
int AudioService_playEffect() {
	if (!threadDoneFlag) {
		pthread_kill(playbackFileThread, SIGUSR1);
	}
	SLresult lRes;
	lRes = (*playerQueue)->Clear(playerQueue);
	if (checkError(lRes) != STATUS_OK)
		return STATUS_FAIL;
	activePlayerBuffer = playerBuffer1;
	int size = VoiceEffect_readWavFileAndFilterEffect(&playerBuffer1);
	if (size > 0) {
		lRes = (*playerQueue)->Enqueue(playerQueue, playerBuffer1,
				size * sizeof(short));
	}
	size = VoiceEffect_readWavFileAndFilterEffect(&playerBuffer2);
	if (size > 0) {
		lRes = (*playerQueue)->Enqueue(playerQueue, playerBuffer2,
				size * sizeof(short));
	}
	return STATUS_OK;
}

/**
 *	Clear bufferQueue of player
 */
int AudioService_clearPlayer() {
	if (!threadDoneFlag) {
		pthread_kill(playbackFileThread, SIGUSR1);
	}
	SLresult lRes;
	lRes = (*playerQueue)->Clear(playerQueue);
	if (checkError(lRes) != STATUS_OK)
		return STATUS_FAIL;
	return STATUS_OK;
}

/**
 * Callback when finish playing on a buffer. Enqueue the other buffer & write data to this buffer.
 */
void playerCallback(W_SLBufferQueueItf slBuffer, void *pContext) {
	if (activePlayerBuffer == playerBuffer1) {
		activePlayerBuffer = playerBuffer2;
	} else {
		activePlayerBuffer = playerBuffer1;
	}
	pthread_create(&playbackFileThread, NULL, playbackFileFunc, NULL);
}

/**
 * Body of thread to process mission of playerCallback.
 */
void* playbackFileFunc(void * param) {
	threadDoneFlag = false;
	int size = 0;
	if (activePlayerBuffer == playerBuffer1) {
		size = VoiceEffect_readWavFileAndFilterEffect(&playerBuffer2);
		if (size > 0) {
			(*playerQueue)->Enqueue(playerQueue, playerBuffer2,
					size * sizeof(short));
		}
	} else {
		size = VoiceEffect_readWavFileAndFilterEffect(&playerBuffer1);
		if (size > 0) {
			(*playerQueue)->Enqueue(playerQueue, playerBuffer1,
					size * sizeof(short));
		}
	}
	threadDoneFlag = true;
	return NULL;
}

int checkError(SLresult res) {
	if (res != SL_RESULT_SUCCESS) {
		Log::error("Error when processing data");
		return STATUS_FAIL;
	}
	return STATUS_OK;
}

#else

int AudioService_init() {
	return STATUS_NOT_SUPPORT;
}

int AudioService_initRecorder() {
	return STATUS_NOT_SUPPORT;
}

void AudioService_destroyRecorder() {
}

int AudioService_startRecord() {
	return STATUS_NOT_SUPPORT;
}

int AudioService_stopRecord() {
	return STATUS_NOT_SUPPORT;
}

int AudioService_initPlayer() {
	return STATUS_NOT_SUPPORT;
}

void AudioService_destroyPlayer() {
}

int AudioService_playEffect() {
	return STATUS_NOT_SUPPORT;
}

int AudioService_startPlayer() {
	return STATUS_NOT_SUPPORT;
}

int AudioService_stopPlayer() {
	return STATUS_NOT_SUPPORT;
}

void AudioService_destroy() {
}

#endif
