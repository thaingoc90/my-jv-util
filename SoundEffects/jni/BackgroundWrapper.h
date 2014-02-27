#ifndef _BACKGROUND_EFFECT_H_
#define _BACKGROUND_EFFECT_H_

#ifdef _ANDROID_FLAG_

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

void BackgroundEffect_init(AAssetManager*);

#else

void BackgroundEffect_init();

#endif

void BackgroundEffect_initProcess(const char*, double, bool, bool);

void BackgroundEffect_destroy();

int BackgroundEffect_processBlock(short** playerBuffer, int size);

#endif
