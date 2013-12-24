#include <jni.h>

#ifndef _AUDIOSERVICE_H_
#define _AUDIOSERVICE_H_

#ifdef __cplusplus
extern "C" {
#endif

jint Java_vng_wmb_service_AudioService_init(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_startRecord(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_stopRecord(JNIEnv *, jobject);

jint Java_vng_wmb_service_AudioService_initPlayer(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_playMusic(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_stopMusic(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_destroy(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_playWaveFile(JNIEnv *, jobject, jstring);

void writePlayerBuffer(const void *pBuffer, jint size);

#ifdef __cplusplus
}
#endif

#endif
