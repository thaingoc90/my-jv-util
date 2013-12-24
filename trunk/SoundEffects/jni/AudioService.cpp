#include "AudioService.h"
#include "Log.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <stdio.h>
#include <assert.h>
#include <ctime>
#include <pthread.h>

const int STATUS_OK = 0;
const int STATUS_KO = -1;

static SLObjectItf mEngineObj = NULL;
SLEngineItf mEngine;
SLDataFormat_PCM lDataFormat;

// RECORDER
static SLObjectItf mRecorderObj = NULL;
SLRecordItf mRecorder;
static SLAndroidSimpleBufferQueueItf mRecorderQueue;
int32_t startTime;

// PLAYER
SLObjectItf mOutputMixObj;
SLObjectItf mPlayerObj = NULL;
SLPlayItf mPlayer;
static SLAndroidSimpleBufferQueueItf mPlayerQueue;

static bool isRecording = false;

const int MAX_TIME_RECORD = 10;
const int SAMPE_RATE = 8000;
int32_t mRecordSize = SAMPE_RATE * MAX_TIME_RECORD;
int16_t* mRecordBuffer = new int16_t[mRecordSize];

void write_wav(const char *, unsigned int, unsigned int, unsigned int,
		unsigned int, int16_t*);
void callback_recorder(SLAndroidSimpleBufferQueueItf, void*);
void callback_player(SLAndroidSimpleBufferQueueItf, void *);
int checkError(SLresult);

jint Java_vng_wmb_service_AudioService_init(JNIEnv* pEnv, jobject pThis) {
	Log::info("Init");
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
	lDataFormat.samplesPerSec = SL_SAMPLINGRATE_8;
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
	SLuint32 state;
	(*mRecorderObj)->GetState(mRecorderObj, &state);
	if (state == SL_OBJECT_STATE_REALIZED ) {
		(*mRecorder)->SetRecordState(mRecorder, SL_RECORDSTATE_STOPPED );
		(*mRecorderQueue)->Clear(mRecorderQueue);

		res = (*mRecorderQueue)->Enqueue(mRecorderQueue, mRecordBuffer,
				mRecordSize * sizeof(int16_t));
		if (checkError(res) != STATUS_OK)
			return;
		(*mRecorder)->SetRecordState(mRecorder, SL_RECORDSTATE_RECORDING );
		time_t nowTime = time(0);
		tm* now = localtime(&nowTime);
		startTime = now->tm_sec;
	}
}

void Java_vng_wmb_service_AudioService_stopRecord(JNIEnv* pEnv, jobject pThis) {
	Log::info("Stop Record");
	if (isRecording) {
		isRecording = false;
		time_t nowTime = time(0);
		tm* now = localtime(&nowTime);
		SLint32 duration = now->tm_sec - startTime;
		Log::info("Duration %d", duration);
		int32_t mRecordSize = SAMPE_RATE * MAX_TIME_RECORD;
		(*mRecorder)->SetRecordState(mRecorder, SL_RECORDSTATE_STOPPED );
		write_wav("/sdcard/voice.wav", SAMPE_RATE, 16, 1, duration,
				mRecordBuffer);
	}
}

void callback_recorder(SLAndroidSimpleBufferQueueItf slBuffer, void *pContext) {
	Log::info("callback record");
	Java_vng_wmb_service_AudioService_stopRecord(NULL, NULL);
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
	lDataLocatorIn.numBuffers = 1;
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
//	res = (*mPlayerQueue)->RegisterCallback(mPlayerQueue, callback_player,
//			NULL);
//	if (checkError(res) != STATUS_OK)
//		return checkError(res);
//
//	res = (*mPlayer)->SetCallbackEventsMask(mPlayer, SL_PLAYEVENT_HEADATEND );
//	if (checkError(res) != STATUS_OK)
//		return checkError(res);

	res = (*mPlayer)->SetPlayState(mPlayer, SL_PLAYSTATE_PLAYING );
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	res = (*mPlayerQueue)->Clear(mPlayerQueue);
	if (checkError(res) != STATUS_OK)
		return checkError(res);

	return STATUS_OK;
}

void Java_vng_wmb_service_AudioService_playMusic(JNIEnv* pEnv, jobject pThis) {
	SLresult lRes;
	SLuint32 lPlayerState;
	(*mPlayerObj)->GetState(mPlayerObj, &lPlayerState);
	if (lPlayerState == SL_OBJECT_STATE_REALIZED ) {
		lRes = (*mPlayerQueue)->Clear(mPlayerQueue);
		if (checkError(lRes) != STATUS_OK)
			return;
	}
	return;
}

void Java_vng_wmb_service_AudioService_stopMusic(JNIEnv* pEnv, jobject pThis) {
	Log::info("Stop Player");
}

void writePlayerBuffer(const void *pBuffer, jint size) {
	SLuint32 lPlayerState;
	if (mPlayerObj != NULL && mPlayerQueue != NULL) {
		(*mPlayerObj)->GetState(mPlayerObj, &lPlayerState);
		if (lPlayerState == SL_OBJECT_STATE_REALIZED ) {
			(*mPlayerQueue)->Enqueue(mPlayerQueue, pBuffer, size);
		}
	}
}

void Java_vng_wmb_service_AudioService_pauseMusic(JNIEnv* pEnv, jobject pThis) {

}

void Java_vng_wmb_service_AudioService_destroy(JNIEnv* pEnv, jobject pThis) {
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

void callback_player(SLAndroidSimpleBufferQueueItf slBuffer, void *pContext) {
	Log::info("callback player");
	JNIEnv* pEnv = (JNIEnv *) pContext;
	Java_vng_wmb_service_AudioService_stopMusic(pEnv, NULL);
}

int checkError(SLresult res) {
	if (res != SL_RESULT_SUCCESS ) {
		Log::error("Error when processing data");
		return STATUS_KO;
	}
	return STATUS_OK;
}

void write_little_endian(unsigned int word, int num_bytes, FILE *wav_file) {
	unsigned buf;
	while (num_bytes > 0) {
		buf = word & 0xff;
		fwrite(&buf, 1, 1, wav_file);
		num_bytes--;
		word >>= 8;
	}
}

void write_wav(const char * filename, unsigned int num_samples,
		unsigned int bits_per_sample, unsigned int num_channels,
		unsigned int time_record, int16_t* data) {
	unsigned int bytes_per_sample = bits_per_sample / 8;
	unsigned int byte_rate = num_samples * num_channels * bytes_per_sample;
	unsigned long i; /* counter for samples */
	FILE* wav_file;

	wav_file = fopen(filename, "w");
	assert(wav_file);

	/* write RIFF header */
	fwrite("RIFF", 1, 4, wav_file);
	write_little_endian(
			36 + bytes_per_sample * num_samples * num_channels * time_record, 4,
			wav_file);
	fwrite("WAVE", 1, 4, wav_file);

	/* write fmt subchunk */
	fwrite("fmt ", 1, 4, wav_file);
	write_little_endian(16, 4, wav_file); /* SubChunk1Size is 16 */
	write_little_endian(1, 2, wav_file); /* PCM is format 1 */
	write_little_endian(num_channels, 2, wav_file);
	write_little_endian(num_samples, 4, wav_file);
	write_little_endian(byte_rate, 4, wav_file);
	write_little_endian(num_channels * bytes_per_sample, 2, wav_file); /* block align */
	write_little_endian(8 * bytes_per_sample, 2, wav_file); /* bits/sample */

	/* write data subchunk */
	fwrite("data", 1, 4, wav_file);
	write_little_endian(byte_rate * time_record, 4, wav_file);
	fwrite(data, byte_rate * time_record, 1, wav_file);

	fflush(wav_file);
	fclose(wav_file);
	Log::info("Write file OK");
}
