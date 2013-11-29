#ifndef _RECORDSERVICE_H_
#define _RECORDSERVICE_H_

#include "Types.h"
#include "android_native_app_glue.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <SLES/OpenSLES_AndroidConfiguration.h>
#include <ctime>

class RecordService {
public:
	RecordService();
	~RecordService();
	status initEngineObj();
	void recordSound();
	void stopRecord();
	void destroyEngineObj();

	status initPlayer();
	void startPlayer();
	void stopPlayer();

private:
	SLObjectItf mEngineObj;
	SLEngineItf mEngine;
	SLDataFormat_PCM lDataFormat;

	// RECORDER
	SLObjectItf mRecorderObj;
	SLRecordItf mRecorder;
	SLAndroidSimpleBufferQueueItf mRecorderQueue;
	int32_t startTime;

	// PLAYER
	SLObjectItf mOutputMixObj;
	SLObjectItf mPlayerObj;
	SLPlayItf mPlayer;
	SLAndroidSimpleBufferQueueItf mPlayerQueue;

};

#endif
