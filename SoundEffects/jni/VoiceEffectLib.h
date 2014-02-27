#include "GlobalConstant.h"

#ifndef _VOICE_EFFECT_H_
#define _VOICE_EFFECT_H_

/*
 * Define type effects.
 */
const int EFFECT_ORGINAL = 0;
const int EFFECT_BIRD = 1;
const int EFFECT_CAT = 2;
const int EFFECT_COW = 3;
const int EFFECT_FAST = 4;
const int EFFECT_ROBOT = 5;
const int EFFECT_DUB_VADER = 6;
const int EFFECT_MIC = 7;
const int EFFECT_STAGE = 8;
const int EFFECT_ROMANCE = 9;
const int EFFECT_TECHTRONIC = 10;
const int EFFECT_DUB = 11;

#ifdef _ANDROID_FLAG_

#include <jni.h>

#endif

#ifdef _ANDROID_FLAG_

int VoiceEffect_init(JNIEnv* pEnv, jobject pThis);

int VoiceEffect_destroy(JNIEnv* pEnv, jobject pThis);

#else

int VoiceEffect_init();

int VoiceEffect_destroy();

#endif

int VoiceEffect_startRecord();

int VoiceEffect_stopRecord();

int VoiceEffect_startPlayer();

int VoiceEffect_stopPlayer();

#ifdef _ANDROID_FLAG_

int VoiceEffect_initEffectLib(JNIEnv*, jobject, jobject);

#else

int VoiceEffect_initEffectLib();

#endif

int VoiceEffect_destroyEffectLib();

int VoiceEffect_playEffect(int);

int VoiceEffect_processAndWriteToAmr(int);

int VoiceEffect_processAndWriteToMp3(int);

void VoiceEffect_playCustomEffect(bool, bool, bool, bool);

void VoiceEffect_prepareSoundTouchEffect(double, double, double);

void VoiceEffect_prepareBackgroundEffect(const char*, double, bool, bool);

void VoiceEffect_prepareReverbEffect(double, double, double, double, double,
		double, double, double);

void VoiceEffect_prepareEchoEffect(double, double);

void VoiceEffect_cbDrawSoundWave(short* temp, int size);

int VoiceEffect_readWavFileAndFilterEffect(short** playerBuffer);

int VoiceEffect_filterEffect(short** blockData, int size);

void VoiceEffect_selectAndInitEffect(int effect);

#endif
