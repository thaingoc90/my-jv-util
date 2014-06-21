#include <pthread.h>
#include "VoiceEffectLib.h"
#include "AudioService.h"
#include "BackgroundWrapper.h"
#include "SoundTouchWrapper.h"
#include "EchoWrapper.h"
#include "ReverbWrapper.h"
#include "Log.h"
#include "WavFile.h"
#include "Utils.h"

#include "Mp3Utils.h"
#ifdef _USE_LAME_MP3_
#include "LameMp3/lame.h"
#endif

#ifdef _USE_OPENCORE_AMR_
#include "opencore/interf_enc.h"
#endif

#ifdef _ANDROID_FLAG_
#include <android/asset_manager_jni.h>
JavaVM * jvm;
jobject jSoundEffectObject;
jobject jSoundEffectClass;
#endif

pthread_mutex_t isProcessingBlock;
WavInFile* inFileTemp;
#define BUFF_SIZE 5120
short sampleBuffer[BUFF_SIZE];

bool hasEchoFlag = false;
bool hasSoundTouchFlag = false;
bool hasBackGroundFlag = false;
bool hasReverbFlag = false;

static char* pathWavFileTemp;
static char* pathAmrFileTemp;
static char* pathMp3FileTemp;

void setFlagEffectsInFilter(bool, bool, bool, bool);

#ifdef _ANDROID_FLAG_

/**
 * JNI_OnLoad. Save JVM.
 */
int JNI_OnLoad(JavaVM* aVm, void* aReserved) {
	Log::info("JNI_ONLOAD");
	jvm = aVm;
	return JNI_VERSION_1_6;
}

/**
 *Init AudioLib
 */
int VoiceEffect_init(JNIEnv* pEnv, jobject pThis, char* rootStorage) {

	int size = strlen(rootStorage) + strlen("/voice.wav") + 1;
	pathWavFileTemp = new char[size];
	pathMp3FileTemp = new char[size];
	pathAmrFileTemp = new char[size];
	strcpy(pathWavFileTemp, rootStorage);
	strcat(pathWavFileTemp, "/voice.wav");
	strcpy(pathMp3FileTemp, rootStorage);
	strcat(pathMp3FileTemp, "/voice.mp3");
	strcpy(pathAmrFileTemp, rootStorage);
	strcat(pathAmrFileTemp, "/voice.amr");

	int res = AudioService_init();
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}

	res = AudioService_initRecorder();
	if (res != STATUS_OK) {
		return res;
	}

	res = AudioService_initPlayer();
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}

	{
		//SAVE ENVIROMENT
		jclass tempClass = pEnv->FindClass("ntdn/voiceutil/service/CVoiceEffect");
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
int VoiceEffect_destroy(JNIEnv* pEnv, jobject pThis) {

	{
		// Delete global variable.
		pEnv->DeleteGlobalRef(jSoundEffectClass);
		pEnv->DeleteGlobalRef(jSoundEffectObject);
	}

	AudioService_destroyPlayer();
	AudioService_destroyRecorder();
	AudioService_destroy();

	delete[] pathWavFileTemp;
	delete[] pathMp3FileTemp;
	delete[] pathAmrFileTemp;

	return STATUS_OK;
}

#else

/**
 *Init AudioLib
 */
int VoiceEffect_init(char* rootStorage) {
	int size = strlen(rootStorage) + strlen("/voice.wav") + 1;
	pathWavFileTemp = new char[size];
	pathMp3FileTemp = new char[size];
	pathAmrFileTemp = new char[size];
	strcpy(pathWavFileTemp, rootStorage);
	strcat(pathWavFileTemp, "/voice.wav");
	strcpy(pathMp3FileTemp, rootStorage);
	strcat(pathMp3FileTemp, "/voice.mp3");
	strcpy(pathAmrFileTemp, rootStorage);
	strcat(pathAmrFileTemp, "/voice.amr");

	int res = AudioService_init();
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}

	res = AudioService_initPlayer();
	if (res != STATUS_OK) {
		return STATUS_FAIL;
	}

	res = AudioService_initRecorder();
	if (res != STATUS_OK) {
		return res;
	}

	return STATUS_OK;
}

/**
 * Destroy AudioLib
 */
int VoiceEffect_destroy() {
	AudioService_destroyPlayer();
	AudioService_destroyRecorder();
	AudioService_destroy();

	delete[] pathWavFileTemp;
	delete[] pathMp3FileTemp;
	delete[] pathAmrFileTemp;
	return STATUS_OK;
}

#endif

/**
 * Start record.
 */
int VoiceEffect_startRecord() {
	int resStatus = AudioService_startRecord(pathWavFileTemp);
	if (resStatus != STATUS_OK) {
		return resStatus;
	}
	return STATUS_OK;
}

/**
 * Stop record.
 */
int VoiceEffect_stopRecord() {
	int resStatus = AudioService_stopRecord();
	if (resStatus != STATUS_OK) {
		return resStatus;
	}
	return STATUS_OK;
}

/**
 * Start player. Open tempFile (sdcard/voice.wav) to ready to play.
 */
int VoiceEffect_startPlayer() {
	inFileTemp = new WavInFile(pathWavFileTemp);
	return AudioService_startPlayer();
}

/**
 * Stop player.
 */
int VoiceEffect_stopPlayer() {
	if (inFileTemp != NULL) {
		delete inFileTemp;
		inFileTemp = NULL;
	}

	return AudioService_stopPlayer();
}

#ifdef _ANDROID_FLAG_

/**
 * Init lib of effects
 */
int VoiceEffect_initEffectLib(JNIEnv* pEnv, jobject pThis,
		jobject assetManager) {
	SoundTouchEffect_init();
	ReverbEffect_init();
	EchoEffect_init();
	AAssetManager* mgr = AAssetManager_fromJava(pEnv, assetManager);
	BackgroundEffect_init(mgr);
	return STATUS_OK;
}

#else

/**
 * Init lib of effects
 */
int VoiceEffect_initEffectLib() {
	SoundTouchEffect_init();
	ReverbEffect_init();
	EchoEffect_init();
	BackgroundEffect_init();
	return STATUS_OK;
}

#endif

/**
 * Destroy lib of effects.
 */
int VoiceEffect_destroyEffectLib() {
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
void VoiceEffect_selectAndInitEffect(int effect) {
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
	setFlagEffectsInFilter(setIfSoundTouch, setIfEcho, setIfBackground,
			setIfReverb);
}

/**
 * Play a effect which is defined.
 */
int VoiceEffect_playEffect(int effect) {
	VoiceEffect_selectAndInitEffect(effect);

	pthread_mutex_lock(&isProcessingBlock);
	inFileTemp->rewind();
	pthread_mutex_unlock(&isProcessingBlock);

	return AudioService_playEffect();
}

/**
 * Process data & write data to amr file.
 */
#ifdef _USE_OPENCORE_AMR_
int VoiceEffect_processAndWriteToAmr(int effect) {
	if (SAMPLE_RATE != 8000) {
		return STATUS_NOT_SUPPORT;
	}

	VoiceEffect_selectAndInitEffect(effect);
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
		numShort = VoiceEffect_filterEffect(&buffer, numShort);

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
#else
int VoiceEffect_processAndWriteToAmr(int effect) {
	return STATUS_NOT_SUPPORT;
}
#endif

/**
 * Process data & write data to mp3 file.
 */
#ifdef _USE_LAME_MP3_
int VoiceEffect_processAndWriteToMp3(int effect) {
	VoiceEffect_selectAndInitEffect(effect);
	FILE* outMp3File = fopen(pathMp3FileTemp, "wb");
	WavInFile* inWavFile = new WavInFile(pathWavFileTemp);
	if (inWavFile == NULL || outMp3File == NULL) {
		return STATUS_FAIL;
	}

	lame_t lame = lame_init();
	lame_set_num_channels(lame, 1);
	lame_set_in_samplerate(lame, SAMPLE_RATE);
	lame_set_out_samplerate(lame, SAMPLE_RATE);
	lame_set_brate(lame, 128);
	lame_set_VBR(lame, vbr_default);
	if ((lame_init_params(lame)) < 0) {
		delete inWavFile;
		inWavFile = NULL;
		fclose(outMp3File);
		return STATUS_FAIL;
	}

	int WAV_FRAME = 2048 * 2;
	int numShort = 0, numEncode = 0, sizeInBuffer = 0;
	short* buffer;
	short inBuffer[WAV_FRAME];
	unsigned char outBuffer[WAV_FRAME / 2];

	while (inWavFile != NULL && inWavFile->eof() == 0) {
		numShort = inWavFile->read(sampleBuffer, BUFF_SIZE);
		buffer = duplicateShortPtr(sampleBuffer, numShort);
		numShort = VoiceEffect_filterEffect(&buffer, numShort);

		int pos = WAV_FRAME - sizeInBuffer;
		while (pos <= numShort) {
			for (int i = pos - WAV_FRAME + sizeInBuffer; i < pos; i++) {
				inBuffer[sizeInBuffer] = buffer[i];
				sizeInBuffer = (sizeInBuffer + 1) % WAV_FRAME;
			}
			pos += WAV_FRAME;
			numEncode = lame_encode_buffer(lame, inBuffer, inBuffer, WAV_FRAME,
					outBuffer, WAV_FRAME / 2);

			fwrite(outBuffer, numEncode, 1, outMp3File);
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
		numEncode = lame_encode_buffer(lame, inBuffer, inBuffer, sizeInBuffer,
				outBuffer, WAV_FRAME);
		fwrite(outBuffer, 1, numEncode, outMp3File);
	}

	lame_encode_flush_nogap(lame, outBuffer, WAV_FRAME);

	if (inWavFile != NULL) {
		delete inWavFile;
		inWavFile = NULL;
	}
	fclose(outMp3File);
	lame_close(lame);
	return STATUS_OK;
}
#else
int VoiceEffect_processAndWriteToMp3(int effect) {
	return STATUS_NOT_SUPPORT;
}
#endif

// ------------------Start custom functions----------------------------------------
/**
 * Play custom effect. Note: use this func after inited an any effect.
 */
void VoiceEffect_playCustomEffect(bool setIfSoundTouch, bool setIfEcho,
		bool setIfBackground, bool setIfReverb) {

	setFlagEffectsInFilter(setIfSoundTouch, setIfEcho, setIfBackground,
			setIfReverb);
	pthread_mutex_lock(&isProcessingBlock);
	inFileTemp->rewind();
	pthread_mutex_unlock(&isProcessingBlock);
	AudioService_playEffect();
}

/**
 * Prepare for soundTouch Lib
 */
void VoiceEffect_prepareSoundTouchEffect(double tempo, double pitch,
		double rate) {
	SoundTouchEffect_initProcess(tempo, pitch, rate);
}

/**
 * Prepare for Echo Lib
 */
void VoiceEffect_prepareEchoEffect(double mDelay, double mDecay) {
	EchoEffect_initProcess(mDelay, mDecay);
}

/**
 * Prepare for Background Lib
 */
void VoiceEffect_prepareBackgroundEffect(const char* bgPath, double gain,
		bool inAssetFolder, bool isWavFile) {
	BackgroundEffect_initProcess(bgPath, gain, inAssetFolder, isWavFile);
}

/**
 * Prepare for Reverb Lib
 */
void VoiceEffect_prepareReverbEffect(double mRoomSize, double mDelay,
		double mReverberance, double mHfDamping, double mToneLow,
		double mToneHigh, double mWetGain, double mDryGain) {
	ReverbEffect_initProcess(mRoomSize, mDelay, mReverberance, mHfDamping,
			mToneLow, mToneHigh, mWetGain, mDryGain);
}

// ------------------End----------------------------------------

/**
 * Set effects which will be used.
 */
void setFlagEffectsInFilter(bool setIfSoundTouch, bool setIfEcho,
		bool setIfBackground, bool setIfReverb) {
	hasSoundTouchFlag = setIfSoundTouch;
	hasEchoFlag = setIfEcho;
	hasBackGroundFlag = setIfBackground;
	hasReverbFlag = setIfReverb;
}

/**
 * Callback to function writeBuffer in obj SoundEffect. Empty if not use
 */
void VoiceEffect_cbDrawSoundWave(short* temp, int size) {

#if defined(_CALLBACK_TO_DRAW_SOUNDWAVE_) && defined(_ANDROID_FLAG_)
	JNIEnv* pEnv;
	jvm->AttachCurrentThread(&pEnv, 0);

	jshortArray buffer = pEnv->NewShortArray(size);
	pEnv->SetShortArrayRegion(buffer, 0, size, temp);

	jmethodID method = pEnv->GetMethodID((jclass) jSoundEffectClass,
			"setBuffer", "([S)V");
	pEnv->CallVoidMethod(jSoundEffectObject, method, buffer);
	jvm->DetachCurrentThread();
#endif

}

/**
 * Processing a block. Read data from tempFile & process through effects & play it.
 */
int VoiceEffect_readWavFileAndFilterEffect(short** playerBuffer) {
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

	numRead = VoiceEffect_filterEffect(playerBuffer, numRead);
	pthread_mutex_unlock(&isProcessingBlock);

	return numRead;
}

int VoiceEffect_filterEffect(short** blockData, int size) {
	int numRead = size;
	if (numRead > 0 && hasSoundTouchFlag) {
		numRead = SoundTouchEffect_processBlock(blockData, numRead);
	}

	if (numRead > 0 && hasEchoFlag) {
		numRead = EchoEffect_processBlock(blockData, numRead);
	}

	if (numRead > 0 && hasReverbFlag) {
		numRead = ReverbEffect_processBlock(blockData, numRead);
	}

	if (numRead > 0 && hasBackGroundFlag) {
		numRead = BackgroundEffect_processBlock(blockData, numRead);
	}
	return numRead;
}

void VoiceEffect_setSampleRate(int sampleRate) {
	SAMPLE_RATE = sampleRate;
}

void VoiceEffect_setChannel(int channel) {
	CHANNEL = channel;
}
