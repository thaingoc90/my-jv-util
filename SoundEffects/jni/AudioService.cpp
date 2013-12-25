#include "AudioService.h"
#include "Log.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <stdio.h>
#include <assert.h>
#include <ctime>
#include <pthread.h>
#include "WavFile.h"
#include "SoundTouchWrapper.h"

const int STATUS_OK = 0;
const int STATUS_KO = -1;

SLObjectItf mEngineObj = NULL;
SLEngineItf mEngine;
SLDataFormat_PCM lDataFormat;

// RECORDER
SLObjectItf mRecorderObj = NULL;
SLRecordItf mRecorder;
SLAndroidSimpleBufferQueueItf mRecorderQueue;
std::clock_t startTime;

// PLAYER
SLObjectItf mOutputMixObj;
SLObjectItf mPlayerObj = NULL;
SLPlayItf mPlayer;
SLAndroidSimpleBufferQueueItf mPlayerQueue;

bool isRecording = false;

const int MAX_TIME_RECORD = 11;
const int SAMPE_RATE = 44100;
int32_t mRecordSize = SAMPE_RATE * MAX_TIME_RECORD;
int16_t* mRecordBuffer1 = new int16_t[mRecordSize];
int16_t* mRecordBuffer2 = new int16_t[mRecordSize];
int16_t* mActiveRecordBuffer = mRecordBuffer1;
int16_t* mPlayerBuffer1;
int16_t* mPlayerBuffer2;
int16_t* mActivePlayerBuffer;
int32_t tempSize = 0;

const char* pathFileTemp = "/sdcard/voice.wav";
WavOutFile* outFileTemp;

void callback_recorder(SLAndroidSimpleBufferQueueItf, void*);
void callback_player(SLAndroidSimpleBufferQueueItf, void*);
int checkError(SLresult);
int numRecord = 0;

jint Java_vng_wmb_service_AudioService_init(JNIEnv* pEnv, jobject pThis) {
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

	const SLuint32 lEngineRecorderIIDCount = 2;
	const SLInterfaceID lEngineRecorderIIDs[] = { SL_IID_RECORD,
			SL_IID_ANDROIDSIMPLEBUFFERQUEUE };
	const SLboolean lEngineRecorderReqs[] = { SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE };

	SLDataLocator_AndroidSimpleBufferQueue lDataLocatorOut;
	lDataLocatorOut.locatorType = SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE;
	lDataLocatorOut.numBuffers = 2;
	lDataFormat.channelMask = SL_SPEAKER_FRONT_CENTER;
	lDataFormat.samplesPerSec = SL_SAMPLINGRATE_44_1;
	lDataFormat.bitsPerSample = SL_PCMSAMPLEFORMAT_FIXED_16;
	lDataFormat.containerSize = SL_PCMSAMPLEFORMAT_FIXED_16;
	lDataFormat.formatType = SL_DATAFORMAT_PCM;
	lDataFormat.numChannels = SL_AUDIOCHANMODE_MP3_MONO;
	lDataFormat.endianness = SL_BYTEORDER_LITTLEENDIAN;
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

	res = (*mRecorder)->SetCallbackEventsMask(mRecorder,
			SL_RECORDEVENT_BUFFER_FULL );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	return STATUS_OK;
}

void Java_vng_wmb_service_AudioService_startRecord(JNIEnv* pEnv,
		jobject pThis) {
	Log::info("Start record");
	isRecording = true;
	SLresult res;
	res = (*mRecorderQueue)->Clear(mRecorderQueue);
	memset(mRecordBuffer1, 0, mRecordSize * sizeof(short));
	memset(mRecordBuffer2, 0, mRecordSize * sizeof(short));
	if (checkError(res) != STATUS_OK)
		return;
	if (numRecord == 0) {
		res = (*mRecorderQueue)->Enqueue(mRecorderQueue, mActiveRecordBuffer,
				mRecordSize * sizeof(int16_t));
		if (checkError(res) != STATUS_OK)
			return;
		numRecord++;
	}
	res = (*mRecorder)->SetRecordState(mRecorder, SL_RECORDSTATE_RECORDING );
	if (checkError(res) != STATUS_OK)
		return;
//	time_t nowTime = time(0);
//	tm* now = localtime(&nowTime);
	startTime = std::clock();
	outFileTemp = new WavOutFile(pathFileTemp, SAMPE_RATE, 16, 1);
}

void Java_vng_wmb_service_AudioService_stopRecord(JNIEnv* pEnv, jobject pThis) {
	Log::info("Stop Record");
	if (isRecording) {
		isRecording = false;
		time_t nowTime = time(0);
		tm* now = localtime(&nowTime);
		SLint32 duration = (std::clock() - startTime) / 1000;
//		duration = duration < 0 ? 60 + duration : duration;
		Log::info("startTime %d", startTime);
		Log::info("Duration %d", duration);
		(*mRecorder)->SetRecordState(mRecorder, SL_RECORDSTATE_STOPPED );
		// Write to file temp
		int32_t size = (duration * SAMPE_RATE) / 1000;
		outFileTemp->write(mActiveRecordBuffer, size);
		delete outFileTemp;
		(*mRecorderQueue)->Clear(mRecorderQueue);
	}
}

void callback_recorder(SLAndroidSimpleBufferQueueItf slBuffer, void *pContext) {
	Log::info("callback record");
	time_t nowTime = time(0);
	tm* now = localtime(&nowTime);
	std::clock_t endTime = std::clock();
	long callbackTime = (endTime - startTime) / 1000;
	Log::info("callbackTime %d", callbackTime);
	Log::info("endTime %d", endTime);
//	callbackTime = callbackTime < 0 ? 60 + callbackTime : callbackTime;
	startTime = endTime;

	if (mActiveRecordBuffer == mRecordBuffer1) {
		mActiveRecordBuffer = mRecordBuffer2;
	} else {
		mActiveRecordBuffer = mRecordBuffer1;
	}
	(*mRecorderQueue)->Enqueue(mRecorderQueue, mActiveRecordBuffer,
			mRecordSize * sizeof(int16_t));

	int32_t size = (callbackTime * SAMPE_RATE) / 1000;
	int16_t * temp = new int16_t[size];
	if (mActiveRecordBuffer == mRecordBuffer1) {
		memcpy(temp, mRecordBuffer2 + (MAX_TIME_RECORD - size), size);
	} else {
		memcpy(temp, mRecordBuffer1 + (MAX_TIME_RECORD - size), size);
	}
	outFileTemp->write(temp, size);
	delete temp;
//	Java_vng_wmb_service_AudioService_stopRecord(NULL, NULL);
}

jint Java_vng_wmb_service_AudioService_initPlayer(JNIEnv* pEnv, jobject pThis) {
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

// Set-up sound audio source.
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
	const SLuint32 lSoundPlayerIIDCount = 2;
	const SLInterfaceID lSoundPlayerIIDs[] = { SL_IID_PLAY, SL_IID_BUFFERQUEUE };
	const SLboolean lSoundPlayerReqs[] = { SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE };

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

	res = (*mPlayerObj)->GetInterface(mPlayerObj,
			SL_IID_ANDROIDSIMPLEBUFFERQUEUE, &mPlayerQueue);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

// Registers a callback called when sound is finished.
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

void Java_vng_wmb_service_AudioService_playEffect(JNIEnv* pEnv, jobject pThis) {
	Log::info("playEffect");
	SLresult lRes;
	SLuint32 lPlayerState;
	(*mPlayerObj)->GetState(mPlayerObj, &lPlayerState);
	if (lPlayerState == SL_OBJECT_STATE_REALIZED ) {
		lRes = (*mPlayerQueue)->Clear(mPlayerQueue);
		if (checkError(lRes) != STATUS_OK)
			return;
		if (mPlayerBuffer1 != NULL) {
			delete mPlayerBuffer1;
			mPlayerBuffer1 = NULL;
			mPlayerBuffer1 = new short[1];
		}
		if (mPlayerBuffer2 != NULL) {
			delete mPlayerBuffer2;
			mPlayerBuffer2 = NULL;
			mPlayerBuffer2 = new short[1];
		}
		int size = processBlock(&mPlayerBuffer1);
		mActivePlayerBuffer = mPlayerBuffer1;
		(*mPlayerQueue)->Enqueue(mPlayerQueue, mActivePlayerBuffer,
				size * sizeof(short));
		tempSize = processBlock(&mPlayerBuffer2);
	}
	return;
}

void Java_vng_wmb_service_AudioService_startPlayer(JNIEnv* pEnv,
		jobject pThis) {
	SLresult lRes;
	SLuint32 lPlayerState;
	(*mPlayerObj)->GetState(mPlayerObj, &lPlayerState);
	if (lPlayerState == SL_OBJECT_STATE_REALIZED ) {
		lRes = (*mPlayerQueue)->Clear(mPlayerQueue);
		if (checkError(lRes) != STATUS_OK)
			return;
		Log::info("Start Player");
		lRes = (*mPlayer)->SetPlayState(mPlayer, SL_PLAYSTATE_PLAYING );
		if (checkError(lRes) != STATUS_OK)
			return;
	}
	return;
}

void callback_player(SLAndroidSimpleBufferQueueItf slBuffer, void *pContext) {
	if (mActivePlayerBuffer == mPlayerBuffer1) {
		mActivePlayerBuffer = mPlayerBuffer2;
	} else {
		mActivePlayerBuffer = mPlayerBuffer1;
	}
	if (tempSize != 0) {
		(*mPlayerQueue)->Enqueue(mPlayerQueue, mActivePlayerBuffer,
				tempSize * sizeof(short));
		if (mActivePlayerBuffer == mPlayerBuffer1) {
			tempSize = processBlock(&mPlayerBuffer2);
		} else {
			tempSize = processBlock(&mPlayerBuffer1);
		}
	}

}

void Java_vng_wmb_service_AudioService_stopPlayer(JNIEnv* pEnv, jobject pThis) {
	Log::info("Stop Player");
	(*mPlayer)->SetPlayState(mPlayer, SL_PLAYSTATE_STOPPED );
	SLuint32 lPlayerState;
	(*mPlayerObj)->GetState(mPlayerObj, &lPlayerState);
	if (lPlayerState == SL_OBJECT_STATE_REALIZED ) {
		(*mPlayerQueue)->Clear(mPlayerQueue);
	}
}

void Java_vng_wmb_service_AudioService_destroy(JNIEnv* pEnv, jobject pThis) {
	Log::info("Destroy Audio Service");
	delete mRecordBuffer1;
	if (mRecorderObj != NULL) {
		(*mRecorderObj)->Destroy(mRecorderObj);
		mRecorderObj = NULL;
		mRecorder = NULL;
		mRecorderQueue = NULL;
	}
	if (mPlayerObj != NULL) {
		(*mPlayerObj)->Destroy(mPlayerObj);
		mPlayerObj = NULL;
		mPlayer = NULL;
		mPlayerQueue = NULL;
	}
	if (mEngineObj != NULL) {
		(*mEngineObj)->Destroy(mEngineObj);
		mEngineObj = NULL;
		mEngine = NULL;
	}
}

int checkError(SLresult res) {
	if (res != SL_RESULT_SUCCESS ) {
		Log::error("Error when processing data");
		return STATUS_KO;
	}
	return STATUS_OK;
}

