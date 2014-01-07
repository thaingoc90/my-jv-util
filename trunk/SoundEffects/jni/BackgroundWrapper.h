/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class vng_wmb_service_BackgroundEffect */

#ifndef _BACKGROUND_EFFECT_H_
#define _BACKGROUND_EFFECT_H_
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     vng_wmb_service_BackgroundEffect
 * Method:    init
 * Signature: ()V
 */

JNIEXPORT void JNICALL Java_vng_wmb_service_BackgroundEffect_init(JNIEnv *,
		jobject, jobject);

/*
 * Class:     vng_wmb_service_BackgroundEffect
 * Method:    initProcess
 * Signature: (Ljava/lang/String;F)V
 */

JNIEXPORT void JNICALL Java_vng_wmb_service_BackgroundEffect_initProcess(
		JNIEnv *, jobject, jstring, jdouble);

/*
 * Class:     vng_wmb_service_BackgroundEffect
 * Method:    destroy
 * Signature: ()V
 */

JNIEXPORT void JNICALL Java_vng_wmb_service_BackgroundEffect_destroy(JNIEnv *,
		jobject);

int processBlockForBackground(short** playerBuffer, int size);

#ifdef __cplusplus
}
#endif
#endif