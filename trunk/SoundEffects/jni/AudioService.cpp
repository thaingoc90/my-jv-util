#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <stdio.h>
#include <assert.h>
#include <ctime>
#include <pthread.h>
#include "AudioService.h"
#include "SoundEffect.h"
#include "Log.h"
#include "WavFile.h"
#include "Utils.h"

/*
 * VARIABLE OF OPENSLES
 */
SLObjectItf mEngineObj = NULL;
SLEngineItf mEngine;
SLDataFormat_PCM lDataFormat;
// RECORDER
SLObjectItf mRecorderObj = NULL;
SLRecordItf mRecorder;
SLAndroidSimpleBufferQueueItf mRecorderQueue;
// PLAYER
SLObjectItf mOutputMixObj;
SLObjectItf mPlayerObj = NULL;
SLPlayItf mPlayer;
SLAndroidSimpleBufferQueueItf mPlayerQueue;
SLVolumeItf mPlayerVolume;

const int32_t mRecordSize = SAMPLE_RATE * MAX_TIME_BUFFER_RECORD / 1000;
int16_t mRecordBuffer1[mRecordSize];
int16_t mRecordBuffer2[mRecordSize];
int16_t* mActiveRecordBuffer = mRecordBuffer1;
int16_t* mPlayerBuffer1;
int16_t* mPlayerBuffer2;
int16_t* mActivePlayerBuffer;
int32_t tempSize = 0;
bool isRecording = false;
int numRecord = 0;
std::clock_t startTime;
SLint32 stopTime = 0;

WavOutFile* outFileTemp;
bool thread_done = true;
pthread_t playerThread;

// Use to write File after 1000ms (access IO more performance). Now, not use.
// ---------------Start----------------
const int32_t mBufferWriteFileSize = 1000 * SAMPLE_RATE / 1000;
int16_t mBufferWriteFile[mBufferWriteFileSize];
int32_t currentBufferSize = 0;
// ---------------End----------------

// INTERFACE OF METHOD
void callback_recorder(SLAndroidSimpleBufferQueueItf, void*);
void callback_player(SLAndroidSimpleBufferQueueItf, void*);
void writeFile(short* temp, int size, bool isStopping);
void* playbackFile(void * param);
int checkError(SLresult);

/****************************************************************/

int AudioService_init() {
	Log::info("Init Audio Service");
	SLresult res;
	const SLuint32 lEngineIIDCount = 1;
	const SLInterfaceID lEngineIIDs[] = { SL_IID_ENGINE };
	const SLboolean lEngineReqs[] = { SL_BOOLEAN_TRUE };

	res = slCreateEngine(&mEngineObj, 0, NULL, lEngineIIDCount, lEngineIIDs,
			lEngineReqs);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mEngineObj)->Realize(mEngineObj, SL_BOOLEAN_FALSE );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mEngineObj)->GetInterface(mEngineObj, SL_IID_ENGINE, &mEngine);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	lDataFormat.channelMask = SL_SPEAKER_FRONT_CENTER;
	lDataFormat.samplesPerSec = SL_SAMPLINGRATE_44_1;
	lDataFormat.bitsPerSample = SL_PCMSAMPLEFORMAT_FIXED_16;
	lDataFormat.containerSize = SL_PCMSAMPLEFORMAT_FIXED_16;
	lDataFormat.formatType = SL_DATAFORMAT_PCM;
	lDataFormat.numChannels = SL_AUDIOCHANMODE_MP3_MONO;
	lDataFormat.endianness = SL_BYTEORDER_LITTLEENDIAN;

	return STATUS_OK;
}

void AudioService_destroy() {
	Log::info("Destroy Audio Service");

	if (mRecorderObj != NULL) {
		(*mRecorderObj)->Destroy(mRecorderObj);
		mRecorderObj = NULL;
		mRecorder = NULL;
		mRecorderQueue = NULL;
	}
	if (mEngineObj != NULL) {
		(*mEngineObj)->Destroy(mEngineObj);
		mEngineObj = NULL;
		mEngine = NULL;
	}
}

/**
 *Init Recorder
 */
int AudioService_initRecorder() {
	Log::info("Init Recorder");
	SLresult res;
	const SLuint32 lEngineRecorderIIDCount = 2;
	const SLInterfaceID lEngineRecorderIIDs[] = { SL_IID_RECORD,
			SL_IID_ANDROIDSIMPLEBUFFERQUEUE };
	const SLboolean lEngineRecorderReqs[] = { SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE };

	SLDataLocator_AndroidSimpleBufferQueue lDataLocatorOut;
	lDataLocatorOut.locatorType = SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE;
	lDataLocatorOut.numBuffers = 2;
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

	res = (*mEngine)->CreateAudioRecorder(mEngine, &mRecorderObj, &lDataSource,
			&lDataSink, lEngineRecorderIIDCount, lEngineRecorderIIDs,
			lEngineRecorderReqs);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mRecorderObj)->Realize(mRecorderObj, SL_BOOLEAN_FALSE );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mRecorderObj)->GetInterface(mRecorderObj, SL_IID_RECORD,
			&mRecorder);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mRecorderObj)->GetInterface(mRecorderObj,
			SL_IID_ANDROIDSIMPLEBUFFERQUEUE, &mRecorderQueue);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mRecorderQueue)->RegisterCallback(mRecorderQueue, callback_recorder,
			NULL);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	// SET VARIABLE
	stopTime = 0;
	numRecord = 0;
	return STATUS_OK;
}

/**
 * Destroy Recorder
 */
void AudioService_destroyRecorder() {
	Log::info("Destroy Recorder");
	if (mRecorderObj != NULL) {
		(*mRecorderObj)->Destroy(mRecorderObj);
		mRecorderObj = NULL;
		mRecorder = NULL;
		mRecorderQueue = NULL;
	}
}

/**
 * Start record. Use 2 buffers.
 */
void AudioService_startRecord() {
	Log::info("Start record");

	outFileTemp = new WavOutFile(pathFileTemp, SAMPLE_RATE, 16, 1);
	currentBufferSize = 0;
	SLresult res;
	SLuint32 state;

	(*mRecorderObj)->GetState(mRecorderObj, &state);
	if (state == SL_OBJECT_STATE_REALIZED ) {
		res = (*mRecorderQueue)->Clear(mRecorderQueue);
		if (checkError(res) != STATUS_OK)
			return;
		memset(mRecordBuffer1, 0, mRecordSize * sizeof(short));
		memset(mRecordBuffer2, 0, mRecordSize * sizeof(short));
		memset(mBufferWriteFile, 0, mBufferWriteFileSize * sizeof(int16_t));

		if (numRecord == 0) {
			res = (*mRecorderQueue)->Enqueue(mRecorderQueue,
					mActiveRecordBuffer, mRecordSize * sizeof(int16_t));
//			numRecord++;
		}
		res = (*mRecorder)->SetRecordState(mRecorder,
				SL_RECORDSTATE_RECORDING );
		if (checkError(res) != STATUS_OK)
			return;
		isRecording = true;
		startTime = std::clock();
	}
}

void AudioService_stopRecord() {
	Log::info("Stop Record");
	if (isRecording) {
		isRecording = false;
		SLint32 duration = (std::clock() - startTime) / 1000;
		if (duration < 0) {
			duration = 0;
		}
		if (duration >= MAX_TIME_BUFFER_RECORD) {
			duration = duration % MAX_TIME_BUFFER_RECORD;
		}
		(*mRecorder)->SetRecordState(mRecorder, SL_RECORDSTATE_STOPPED );
		// Write to file temp
		int32_t size = (duration * SAMPLE_RATE) / 1000;
		if (stopTime == 0) {
//			outFileTemp->write(mActiveRecordBuffer, size);
			writeFile(mActiveRecordBuffer, size, true);
		} else {
			int16_t * temp = new int16_t[size];
			if (mActiveRecordBuffer == mRecordBuffer1) {
				memcpy(temp, mRecordBuffer1 + stopTime * SAMPLE_RATE / 1000,
						size * 2);
			} else {
				memcpy(temp, mRecordBuffer2 + stopTime * SAMPLE_RATE / 1000,
						size * 2);
			}

//			outFileTemp->write(temp, size);
			writeFile(temp, size, true);
			delete[] temp;
		}
		delete outFileTemp;
		stopTime = (stopTime == 0) ? duration : stopTime + duration;
		(*mRecorderQueue)->Clear(mRecorderQueue);
	}
}
/**
 * Callback is called when buffer is full.
 * It will enqueue the other buffer & write data in this buffer to file.
 */
void callback_recorder(SLAndroidSimpleBufferQueueItf slBuffer, void *pContext) {
	long callbackTime;

	startTime = std::clock();
	callbackTime = MAX_TIME_BUFFER_RECORD - stopTime;
	stopTime = 0;

	if (mActiveRecordBuffer == mRecordBuffer1) {
		mActiveRecordBuffer = mRecordBuffer2;
	} else {
		mActiveRecordBuffer = mRecordBuffer1;
	}
	(*mRecorderQueue)->Enqueue(mRecorderQueue, mActiveRecordBuffer,
			mRecordSize * sizeof(int16_t));

	int32_t size = (callbackTime * SAMPLE_RATE) / 1000;
	int16_t * temp = new int16_t[size];
	if (mActiveRecordBuffer == mRecordBuffer1) {
		memcpy(temp, mRecordBuffer2 + (mRecordSize - size), size * 2);
	} else {
		memcpy(temp, mRecordBuffer1 + (mRecordSize - size), size * 2);
	}
	callback_to_writeBuffer(temp, size);
//	outFileTemp->write(temp, size);
	writeFile(temp, size, false);
	delete[] temp;
}

// Use to write buffer to file when bufferWriteFile full or isStopping is true.
void writeFile(short* temp, int size, bool isStopping) {
	if (currentBufferSize + size > mBufferWriteFileSize) {
		outFileTemp->write(mBufferWriteFile, currentBufferSize);
		currentBufferSize = 0;
	}
	memcpy(mBufferWriteFile + currentBufferSize, temp, size * 2);
	currentBufferSize += size;
	if (isStopping) {
		outFileTemp->write(mBufferWriteFile, currentBufferSize);
	}
}

// ----------------------------------------------------------------
// ----------------------FOR PLAYER -------------------------------
// ----------------------------------------------------------------

int AudioService_initPlayer() {
	Log::info("Init Player");
	SLresult res;

	const SLuint32 lOutputMixIIDCount = 0;
	const SLInterfaceID lOutputMixIIDs[] = { };
	const SLboolean lOutputMixReqs[] = { };
	res = (*mEngine)->CreateOutputMix(mEngine, &mOutputMixObj,
			lOutputMixIIDCount, lOutputMixIIDs, lOutputMixReqs);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mOutputMixObj)->Realize(mOutputMixObj, SL_BOOLEAN_FALSE );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	SLDataLocator_AndroidSimpleBufferQueue lDataLocatorIn;
	lDataLocatorIn.locatorType = SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE;
	lDataLocatorIn.numBuffers = 2;
	SLDataSource lDataSource;
	lDataSource.pLocator = &lDataLocatorIn;
	lDataSource.pFormat = &lDataFormat;

	SLDataLocator_OutputMix lDataLocatorOut;
	lDataLocatorOut.locatorType = SL_DATALOCATOR_OUTPUTMIX;
	lDataLocatorOut.outputMix = mOutputMixObj;
	SLDataSink lDataSink;
	lDataSink.pLocator = &lDataLocatorOut;
	lDataSink.pFormat = NULL;
	const SLuint32 lSoundPlayerIIDCount = 3;
	const SLInterfaceID lSoundPlayerIIDs[] = { SL_IID_PLAY, SL_IID_VOLUME,
			SL_IID_BUFFERQUEUE };
	const SLboolean lSoundPlayerReqs[] = { SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE,
			SL_BOOLEAN_TRUE };

	res = (*mEngine)->CreateAudioPlayer(mEngine, &mPlayerObj, &lDataSource,
			&lDataSink, lSoundPlayerIIDCount, lSoundPlayerIIDs,
			lSoundPlayerReqs);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mPlayerObj)->Realize(mPlayerObj, SL_BOOLEAN_FALSE );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mPlayerObj)->GetInterface(mPlayerObj, SL_IID_PLAY, &mPlayer);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mPlayerObj)->GetInterface(mPlayerObj, SL_IID_VOLUME,
			&mPlayerVolume);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	SLmillibel maxVol;
	res = (*mPlayerVolume)->GetMaxVolumeLevel(mPlayerVolume, &maxVol);
	if (checkError(res) == STATUS_OK) {
		res = (*mPlayerVolume)->SetVolumeLevel(mPlayerVolume, maxVol);
	}

	res = (*mPlayerObj)->GetInterface(mPlayerObj,
			SL_IID_ANDROIDSIMPLEBUFFERQUEUE, &mPlayerQueue);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mPlayerQueue)->RegisterCallback(mPlayerQueue, callback_player,
			NULL);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mPlayer)->SetCallbackEventsMask(mPlayer, SL_PLAYEVENT_HEADATEND );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mPlayerQueue)->Clear(mPlayerQueue);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	return STATUS_OK;
}

void AudioService_destroyPlayer() {
	Log::info("Destroy Player");

	if (mPlayerObj != NULL) {
		(*mPlayerObj)->Destroy(mPlayerObj);
		mPlayerObj = NULL;
		mPlayer = NULL;
		mPlayerQueue = NULL;
		mPlayerVolume = NULL;
	}
	if (mOutputMixObj != NULL) {
		(*mOutputMixObj)->Destroy(mOutputMixObj);
		mOutputMixObj = NULL;
	}

}

/**
 * Start player. Set state of PlayerObj to SL_PLAYSTATE_PLAYING.
 */
void AudioService_startPlayer() {
	Log::info("Start Player");

	SLresult lRes;
	SLuint32 lPlayerState;
	(*mPlayerObj)->GetState(mPlayerObj, &lPlayerState);
	if (lPlayerState == SL_OBJECT_STATE_REALIZED ) {
		lRes = (*mPlayerQueue)->Clear(mPlayerQueue);
		if (checkError(lRes) != STATUS_OK)
			return;
		lRes = (*mPlayer)->SetPlayState(mPlayer, SL_PLAYSTATE_PLAYING );
		if (checkError(lRes) != STATUS_OK)
			return;
	}
	return;
}

/**
 * Stop player
 */
void AudioService_stopPlayer() {
	Log::info("Stop Player");
	(*mPlayer)->SetPlayState(mPlayer, SL_PLAYSTATE_STOPPED );
	(*mPlayerQueue)->Clear(mPlayerQueue);
}

/**
 * Play tempFile. Read data from function "processBlock" & play it.
 */
void AudioService_playEffect() {
	if (!thread_done) {
		pthread_kill(playerThread, SIGUSR1);
	}

	(*mPlayerQueue)->Clear(mPlayerQueue);
	int size = processBlock(&mPlayerBuffer1);
	mActivePlayerBuffer = mPlayerBuffer1;
	(*mPlayerQueue)->Enqueue(mPlayerQueue, mActivePlayerBuffer,
			size * sizeof(short));
	tempSize = processBlock(&mPlayerBuffer2);
	return;
}

/**
 * Callback when finish playing on a buffer. Enqueue the other buffer & write data to this buffer.
 */
void callback_player(SLAndroidSimpleBufferQueueItf slBuffer, void *pContext) {
	if (mActivePlayerBuffer == mPlayerBuffer1) {
		mActivePlayerBuffer = mPlayerBuffer2;
	} else {
		mActivePlayerBuffer = mPlayerBuffer1;
	}
	if (!thread_done) {
		pthread_kill(playerThread, SIGUSR1);
	}
	pthread_create(&playerThread, NULL, playbackFile, NULL);
}

/**
 * Body of thread to process mission of callback_player.
 */
void* playbackFile(void * param) {
	thread_done = false;
	if (tempSize != 0) {
		(*mPlayerQueue)->Enqueue(mPlayerQueue, mActivePlayerBuffer,
				tempSize * sizeof(short));
		if (mActivePlayerBuffer == mPlayerBuffer1) {
			tempSize = processBlock(&mPlayerBuffer2);
		} else {
			tempSize = processBlock(&mPlayerBuffer1);
		}
	}
	thread_done = true;
	return NULL;
}

int checkError(SLresult res) {
	if (res != SL_RESULT_SUCCESS ) {
		Log::error("Error when processing data");
		return STATUS_KO;
	}
	return STATUS_OK;
}

