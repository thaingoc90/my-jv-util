#include <jni.h>

#ifndef _AUDIOSERVICE_H_
#define _AUDIOSERVICE_H_

#include <pthread.h>

#ifdef __cplusplus
extern "C" {
#endif

jint Java_vng_wmb_service_AudioService_init(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_startRecord(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_stopRecord(JNIEnv *, jobject);

jint Java_vng_wmb_service_AudioService_initPlayer(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_destroyPlayer(JNIEnv*, jobject);

void Java_vng_wmb_service_AudioService_playEffect(JNIEnv *, jobject, jboolean,
		jboolean, jboolean, jboolean);

void Java_vng_wmb_service_AudioService_startPlayer(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_stopPlayer(JNIEnv *, jobject);

void Java_vng_wmb_service_AudioService_destroy(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif

#endif
