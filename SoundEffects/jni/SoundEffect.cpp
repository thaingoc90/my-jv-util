#include <jni.h>
#include <pthread.h>
#include "SoundEffect.h"
#include "AudioService.h"
#include "BackgroundWrapper.h"
#include "SoundTouchWrapper.h"
#include "EchoWrapper.h"
#include "ReverbWrapper.h"
#include "Log.h"
#include "WavFile.h"
#include "Utils.h"
#include "opencore/interf_enc.h"

JavaVM * jvm;
jobject jSoundEffectObject;
jobject jSoundEffectClass;

pthread_mutex_t isProcessingBlock;
WavInFile* inFileTemp;
#define BUFF_SIZE 10 * 1024
short sampleBuffer[BUFF_SIZE];

bool mHasEcho = false;
bool mHasSoundTouch = false;
bool mHasBackGround = false;
bool mHasReverb = false;

/**
 * JNI_OnLoad. Save JVM.
 */
jint JNI_OnLoad(JavaVM* aVm, void* aReserved) {
	Log::info("JNI_ONLOAD");
	jvm = aVm;
	return JNI_VERSION_1_6;
}

void setFlagUseOfEffect(bool, bool, bool, bool);

/**
 *Init AudioLib
 */
jint Java_vng_wmb_service_SoundEffect_init(JNIEnv* pEnv, jobject pThis) {
	int res = AudioService_init();
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}

	res = AudioService_initPlayer();
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}

	{
		//SAVE ENVIROMENT
		jclass tempClass = pEnv->FindClass("vng/wmb/service/SoundEffect");
		if (tempClass == NULL) {
			Log::info("ERROR - cant get class");
		}
		jSoundEffectClass = pEnv->NewGlobalRef(tempClass);
		jSoundEffectObject = pEnv->NewGlobalRef(pThis);
	}

	return STATUS_OK;
}

/**
 * Destroy AudioLib
 */
jint Java_vng_wmb_service_SoundEffect_destroy(JNIEnv* pEnv, jobject pThis) {

	{
		// Delete global variable.
		pEnv->DeleteGlobalRef(jSoundEffectClass);
		pEnv->DeleteGlobalRef(jSoundEffectObject);
	}

	AudioService_destroyPlayer();
	AudioService_destroy();
	return STATUS_OK;
}

/**
 * Start record.
 */
jint Java_vng_wmb_service_SoundEffect_startRecord(JNIEnv* pEnv, jobject pThis) {
	int resStatus = AudioService_initRecorder();
	if (resStatus != STATUS_OK) {
		return resStatus;
	}
	resStatus = AudioService_startRecord();
	if (resStatus != STATUS_OK) {
		return resStatus;
	}
	return STATUS_OK;
}

/**
 * Stop record.
 */
jint Java_vng_wmb_service_SoundEffect_stopRecord(JNIEnv* pEnv, jobject pThis) {
	int resStatus = AudioService_stopRecord();
	if (resStatus != STATUS_OK) {
		return resStatus;
	}
	AudioService_destroyRecorder();
	return STATUS_OK;
}

/**
 * Start player. Open tempFile (sdcard/voice.wav) to ready to play.
 */
jint Java_vng_wmb_service_SoundEffect_startPlayer(JNIEnv* pEnv, jobject pThis) {
	inFileTemp = new WavInFile(pathWavFileTemp);
	return AudioService_startPlayer();
}

/**
 * Stop player.
 */
jint Java_vng_wmb_service_SoundEffect_stopPlayer(JNIEnv* pEnv, jobject pThis) {
	if (inFileTemp != NULL) {
		delete inFileTemp;
		inFileTemp = NULL;
	}

	return AudioService_stopPlayer();
}

/**
 * Init lib of effects
 */
jint Java_vng_wmb_service_SoundEffect_initEffectLib(JNIEnv* pEnv, jobject pThis,
		jobject assetManager) {
	SoundTouchEffect_init();
	ReverbEffect_init();
	EchoEffect_init();
	AAssetManager* mgr = AAssetManager_fromJava(pEnv, assetManager);
	BackgroundEffect_init(mgr);
	return STATUS_OK;
}

/**
 * Destroy lib of effects.
 */
jint Java_vng_wmb_service_SoundEffect_destroyEffectLib(JNIEnv* pEnv,
		jobject pThis) {
	SoundTouchEffect_destroy();
	ReverbEffect_destroy();
	EchoEffect_destroy();
	BackgroundEffect_destroy();
	return STATUS_OK;
}

/**
 * Effect = 1 : bird
 *        = 2 : cat
 *        = 3 : cow
 *        = 4 : fast
 *        = 5 : robot
 *        = 6 : dub vader
 *        = 7 : microphone
 *        = 8 : stage
 *        = 9 : romance
 *        = 10 : techtronic
 *        = 11 : dub
 *        = else : origninal
 */
void selectAndInitEffect(int effect) {
	bool setIfSoundTouch = false, setIfEcho = false;
	bool setIfReverb = false, setIfBackground = false;
	switch (effect) {
	case EFFECT_BIRD: {
		SoundTouchEffect_initProcess(0, 7, 25);
		setIfSoundTouch = true;
		break;
	}
	case EFFECT_CAT: {
		SoundTouchEffect_initProcess(0, 6, 15);
		setIfSoundTouch = true;
		break;
	}
	case EFFECT_COW: {
		SoundTouchEffect_initProcess(0, -4, 0);
		setIfSoundTouch = true;
		break;
	}
	case EFFECT_FAST: {
		SoundTouchEffect_initProcess(60, 0, 0);
		setIfSoundTouch = true;
		break;
	}
	case EFFECT_ROBOT: {
		EchoEffect_initProcess(8, 0.85);
		setIfEcho = true;
		break;
	}
	case EFFECT_DUB_VADER: {
		SoundTouchEffect_initProcess(-2, -6, -2);
		setIfSoundTouch = true;
		EchoEffect_initProcess(200, 0.3);
		setIfEcho = true;
		break;
	}
	case EFFECT_MIC: {
		ReverbEffect_initProcess(30, 10, 30, 50, 50, 100, 4, 4);
		setIfReverb = true;
		break;
	}
	case EFFECT_STAGE: {
		ReverbEffect_initProcess(70, 30, 80, 60, 100, 80, 0, 0);
		setIfReverb = true;
		break;
	}
	case EFFECT_ROMANCE: {
		const char* path = "bg_romance.wav";
		BackgroundEffect_initProcess(path, 0.7, true, true);
		setIfBackground = true;
		break;
	}
	case EFFECT_TECHTRONIC: {
		const char* path = "bg_techtronic.wav";
		BackgroundEffect_initProcess(path, 0.7, true, true);
		setIfBackground = true;
		break;
	}
	case EFFECT_DUB: {
		const char* path = "bg_dub.wav";
		BackgroundEffect_initProcess(path, 0.7, true, true);
		setIfBackground = true;
		break;
	}
	}
	setFlagUseOfEffect(setIfSoundTouch, setIfEcho, setIfBackground,
			setIfReverb);
}

/**
 * Play a effect which is defined.
 */
jint Java_vng_wmb_service_SoundEffect_playEffect(JNIEnv* pEnv, jobject pThis,
		jint effect) {
	selectAndInitEffect(effect);

	pthread_mutex_lock(&isProcessingBlock);
	inFileTemp->rewind();
	pthread_mutex_unlock(&isProcessingBlock);

	return AudioService_playEffect();
}

/**
 * Process data & write data to amr file.
 */
jint Java_vng_wmb_service_SoundEffect_processAndWriteToAmr(JNIEnv* pEnv,
		jobject pThis, jint effect) {
	if (SAMPLE_RATE != 8000) {
		return STATUS_NOT_SUPPORT;
	}

	selectAndInitEffect(effect);
	FILE* outAmrFile = fopen(pathAmrFileTemp, "wb");
	void* amrObj = Encoder_Interface_init(0);
	WavInFile* inWavFile = new WavInFile(pathWavFileTemp);
	if (inWavFile == NULL || outAmrFile == NULL) {
		return STATUS_FAIL;
	}

	enum ModeAMR modeAMR = AMRNB122;
	int WAV_FRAME = 160;
	int numShort = 0, numEncode = 0, sizeInBuffer = 0;
	short* buffer;
	short inBuffer[WAV_FRAME];
	unsigned char outBuffer[200];

	fwrite("#!AMR\n", 1, 6, outAmrFile);
	while (inWavFile != NULL && inWavFile->eof() == 0) {
		numShort = inWavFile->read(sampleBuffer, BUFF_SIZE);
		buffer = duplicateShortPtr(sampleBuffer, numShort);
		if (numShort > 0 && mHasSoundTouch) {
			numShort = SoundTouchEffect_processBlock(&buffer, numShort);
		}

		if (numShort > 0 && mHasEcho) {
			numShort = EchoEffect_processBlock(&buffer, numShort);
		}

		if (numShort > 0 && mHasReverb) {
			numShort = ReverbEffect_processBlock(&buffer, numShort);
		}

		if (numShort > 0 && mHasBackGround) {
			numShort = BackgroundEffect_processBlock(&buffer, numShort);
		}

		int pos = WAV_FRAME - sizeInBuffer;
		while (pos <= numShort) {
			for (int i = pos - WAV_FRAME + sizeInBuffer; i < pos; i++) {
				inBuffer[sizeInBuffer] = buffer[i];
				sizeInBuffer = (sizeInBuffer + 1) % WAV_FRAME;
			}
			pos += WAV_FRAME;

			numEncode = Encoder_Interface_Encode(amrObj, modeAMR, inBuffer,
					outBuffer, 0);
			fwrite(outBuffer, 1, numEncode, outAmrFile);
		}
		if (pos > numShort) {
			sizeInBuffer = 0;
			for (int i = pos - WAV_FRAME; i < numShort; i++) {
				inBuffer[sizeInBuffer++] = buffer[i];
			}
		}
		sizeInBuffer = sizeInBuffer % WAV_FRAME;
		delete[] buffer;
	}
	if (sizeInBuffer > 0) {
		short RedundantBuffer[sizeInBuffer];
		memcpy(RedundantBuffer, inBuffer, sizeInBuffer);
		numEncode = Encoder_Interface_Encode(amrObj, modeAMR, RedundantBuffer,
				outBuffer, 0);
		fwrite(outBuffer, 1, numEncode, outAmrFile);
	}

	if (inWavFile != NULL) {
		delete inWavFile;
		inWavFile = NULL;
	}
	fclose(outAmrFile);
	Encoder_Interface_exit(amrObj);
	return STATUS_OK;
}

// ------------------Start custom functions----------------------------------------
/**
 * Play custom effect. Note: use this func after inited an any effect.
 */
void Java_vng_wmb_service_SoundEffect_playCustomEffect(JNIEnv* pEnv,
		jobject pThis, jboolean setIfSoundTouch, jboolean setIfEcho,
		jboolean setIfBackground, jboolean setIfReverb) {

	setFlagUseOfEffect(setIfSoundTouch, setIfEcho, setIfBackground,
			setIfReverb);
	pthread_mutex_lock(&isProcessingBlock);
	inFileTemp->rewind();
	pthread_mutex_unlock(&isProcessingBlock);
	AudioService_playEffect();
}

/**
 * Prepare for soundTouch Lib
 */
void Java_vng_wmb_service_SoundEffect_prepareSoundTouchEffect(JNIEnv* pEnv,
		jobject pThis, jdouble tempo, jdouble pitch, jdouble rate) {
	SoundTouchEffect_initProcess(tempo, pitch, rate);
}

/**
 * Prepare for Echo Lib
 */
void Java_vng_wmb_service_SoundEffect_prepareEchoEffect(JNIEnv* pEnv,
		jobject pThis, jdouble mDelay, jdouble mDecay) {
	EchoEffect_initProcess(mDelay, mDecay);
}

/**
 * Prepare for Background Lib
 */
void Java_vng_wmb_service_SoundEffect_prepareBackgroundEffect(JNIEnv* pEnv,
		jobject pThis, jstring bgPath, jdouble gain, jboolean inAssetFolder,
		jboolean isWavFile) {
	const char* bgFilePath = pEnv->GetStringUTFChars(bgPath, NULL);
	BackgroundEffect_initProcess(bgFilePath, gain, inAssetFolder, isWavFile);
	pEnv->ReleaseStringUTFChars(bgPath, bgFilePath);
}

/**
 * Prepare for Reverb Lib
 */
void Java_vng_wmb_service_SoundEffect_prepareReverbEffect(JNIEnv* pEnv,
		jobject pThis, jdouble mRoomSize, jdouble mDelay, jdouble mReverberance,
		jdouble mHfDamping, jdouble mToneLow, jdouble mToneHigh,
		jdouble mWetGain, jdouble mDryGain) {
	ReverbEffect_initProcess(mRoomSize, mDelay, mReverberance, mHfDamping,
			mToneLow, mToneHigh, mWetGain, mDryGain);
}

// ------------------End----------------------------------------

/**
 * Set effects which will be used.
 */
void setFlagUseOfEffect(bool setIfSoundTouch, bool setIfEcho,
		bool setIfBackground, bool setIfReverb) {
	mHasSoundTouch = setIfSoundTouch;
	mHasEcho = setIfEcho;
	mHasBackGround = setIfBackground;
	mHasReverb = setIfReverb;
}

/**
 * Callback to function writeBuffer in obj SoundEffect. Empty if not use
 */
void callback_to_writeBuffer(short* temp, int size) {
	JNIEnv* pEnv;
	jvm->AttachCurrentThread(&pEnv, 0);

	jshortArray buffer = pEnv->NewShortArray(size);
	pEnv->SetShortArrayRegion(buffer, 0, size, temp);

	jmethodID method = pEnv->GetMethodID((jclass) jSoundEffectClass,
			"setBuffer", "([S)V");
	pEnv->CallVoidMethod(jSoundEffectObject, method, buffer);
	jvm->DetachCurrentThread();
}

/**
 * Processing a block. Read data from tempFile & process through effects & play it.
 */
int processBlock(short** playerBuffer) {
	int numRead = 0;
	pthread_mutex_lock(&isProcessingBlock);
	if (inFileTemp != NULL && inFileTemp->eof() == 0) {
		numRead = inFileTemp->read(sampleBuffer, BUFF_SIZE);
		short *buffer = duplicateShortPtr(sampleBuffer, numRead);
		if ((*playerBuffer) != NULL) {
			delete[] (*playerBuffer);
		}
		(*playerBuffer) = buffer;
	}

	if (numRead > 0 && mHasSoundTouch) {
		numRead = SoundTouchEffect_processBlock(playerBuffer, numRead);
	}

	if (numRead > 0 && mHasEcho) {
		numRead = EchoEffect_processBlock(playerBuffer, numRead);
	}

	if (numRead > 0 && mHasReverb) {
		numRead = ReverbEffect_processBlock(playerBuffer, numRead);
	}

	if (numRead > 0 && mHasBackGround) {
		numRead = BackgroundEffect_processBlock(playerBuffer, numRead);
	}

	pthread_mutex_unlock(&isProcessingBlock);

	return numRead;
}
