#include "VoiceUtilityJNI.h"
#include "VoiceEffectLib.h"
#include "Log.h"

/**
 *Init AudioLib
 */
jint Java_ntdn_voiceutil_service_CVoiceService_init(JNIEnv* pEnv, jobject pThis,
		jstring rootStorage) {
	const char* rootStoragePath = pEnv->GetStringUTFChars(rootStorage, 0);
#ifdef _ANDROID_FLAG_
	int res = VoiceEffect_init(pEnv, pThis, (char*) rootStoragePath);
#else
	int res = VoiceEffect_init((char*) rootStoragePath);
#endif
	pEnv->ReleaseStringUTFChars(rootStorage, rootStoragePath);
	return res;
}

/**
 * Destroy AudioLib
 */
jint Java_ntdn_voiceutil_service_CVoiceService_destroy(JNIEnv* pEnv,
		jobject pThis) {
#ifdef _ANDROID_FLAG_
	return VoiceEffect_destroy(pEnv, pThis);
#else
	return VoiceEffect_destroy();
#endif
}

void Java_ntdn_voiceutil_service_CVoiceService_setSampleRate(JNIEnv* pEnv,
		jobject pThis, jint sampleRate) {
	VoiceEffect_setSampleRate(sampleRate);
}

void Java_ntdn_voiceutil_service_CVoiceService_setChannel(JNIEnv* pEnv,
		jobject pThis, jint channel) {
	VoiceEffect_setChannel(channel);
}

/**
 * Start record.
 */
jint Java_ntdn_voiceutil_service_CVoiceService_startRecord(JNIEnv* pEnv,
		jobject pThis) {
	return VoiceEffect_startRecord();
}

/**
 * Stop record.
 */
jint Java_ntdn_voiceutil_service_CVoiceService_stopRecord(JNIEnv* pEnv,
		jobject pThis) {
	return VoiceEffect_stopRecord();
}

/**
 * Start player. Open tempFile (sdcard/voice.wav) to ready to play.
 */
jint Java_ntdn_voiceutil_service_CVoiceService_startPlayer(JNIEnv* pEnv,
		jobject pThis) {
	return VoiceEffect_startPlayer();
}

/**
 * Stop player.
 */
jint Java_ntdn_voiceutil_service_CVoiceService_stopPlayer(JNIEnv* pEnv,
		jobject pThis) {
	return VoiceEffect_stopPlayer();
}

/**
 * Init lib of effects
 */
jint Java_ntdn_voiceutil_service_CVoiceService_initEffectLib(JNIEnv* pEnv,
		jobject pThis, jobject assetManager) {
#ifdef _ANDROID_FLAG_
	return VoiceEffect_initEffectLib(pEnv, pThis, assetManager);
#else
	return VoiceEffect_initEffectLib();
#endif
}

/**
 * Destroy lib of effects.
 */
jint Java_ntdn_voiceutil_service_CVoiceService_destroyEffectLib(JNIEnv* pEnv,
		jobject pThis) {
	return VoiceEffect_destroyEffectLib();
}

/**
 * Play a effect which is defined.
 */
jint Java_ntdn_voiceutil_service_CVoiceService_playEffect(JNIEnv* pEnv,
		jobject pThis, jint effect) {
	return VoiceEffect_playEffect(effect);
}

/**
 * Process data & write data to amr file.
 */
jint Java_ntdn_voiceutil_service_CVoiceService_processAndWriteToAmr(
		JNIEnv* pEnv, jobject pThis, jint effect) {
	return VoiceEffect_processAndWriteToAmr(effect);
}

/**
 * Process data & write data to mp3 file.
 */
jint Java_ntdn_voiceutil_service_CVoiceService_processAndWriteToMp3(
		JNIEnv* pEnv, jobject pThis, jint effect) {
	return VoiceEffect_processAndWriteToMp3(effect);
}

// ------------------Start custom functions----------------------------------------
/**
 * Play custom effect. Note: use this func after inited an any effect.
 */
void Java_ntdn_voiceutil_service_CVoiceService_playCustomEffect(JNIEnv* pEnv,
		jobject pThis, jboolean setIfSoundTouch, jboolean setIfEcho,
		jboolean setIfBackground, jboolean setIfReverb) {

	VoiceEffect_playCustomEffect(setIfSoundTouch, setIfEcho, setIfBackground,
			setIfReverb);
}

/**
 * Prepare for soundTouch Lib
 */
void Java_ntdn_voiceutil_service_CVoiceService_prepareSoundTouchEffect(
		JNIEnv* pEnv, jobject pThis, jdouble tempo, jdouble pitch,
		jdouble rate) {
	VoiceEffect_prepareSoundTouchEffect(tempo, pitch, rate);
}

/**
 * Prepare for Echo Lib
 */
void Java_ntdn_voiceutil_service_CVoiceService_prepareEchoEffect(JNIEnv* pEnv,
		jobject pThis, jdouble mDelay, jdouble mDecay) {
	VoiceEffect_prepareEchoEffect(mDelay, mDecay);
}

/**
 * Prepare for Background Lib
 */
void Java_ntdn_voiceutil_service_CVoiceService_prepareBackgroundEffect(
		JNIEnv* pEnv, jobject pThis, jstring bgPath, jdouble gain,
		jboolean inAssetFolder, jboolean isWavFile) {
	const char* bgFilePath = pEnv->GetStringUTFChars(bgPath, 0);
	VoiceEffect_prepareBackgroundEffect(bgFilePath, gain, inAssetFolder,
			isWavFile);
	pEnv->ReleaseStringUTFChars(bgPath, bgFilePath);
}

/**
 * Prepare for Reverb Lib
 */
void Java_ntdn_voiceutil_service_CVoiceService_prepareReverbEffect(JNIEnv* pEnv,
		jobject pThis, jdouble mRoomSize, jdouble mDelay, jdouble mReverberance,
		jdouble mHfDamping, jdouble mToneLow, jdouble mToneHigh,
		jdouble mWetGain, jdouble mDryGain) {
	VoiceEffect_prepareReverbEffect(mRoomSize, mDelay, mReverberance,
			mHfDamping, mToneLow, mToneHigh, mWetGain, mDryGain);
}

// ------------------End----------------------------------------

