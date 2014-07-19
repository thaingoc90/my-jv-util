#include "Config.h"

#ifndef _VOICE_EFFECT_H_
#define _VOICE_EFFECT_H_

#ifdef _ANDROID_FLAG_

#include <jni.h>

#endif

#ifdef _ANDROID_FLAG_

int VoiceEffect_init(JNIEnv* pEnv, jobject pThis, char*);

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

int VoiceEffect_saveFile(char*, int, int);

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

void VoiceEffect_setSampleRate(int sampleRate);

void VoiceEffect_setChannel(int channel);

#endif
