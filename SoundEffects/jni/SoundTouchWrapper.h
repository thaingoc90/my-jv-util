/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class vng_wmb_service_SoundTouchEffect */

#ifndef _SOUNDTOUCHWRAPPER_H_
#define _SOUNDTOUCHWRAPPER_H_
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     vng_wmb_service_SoundTouchEffect
 * Method:    init
 * Signature: ()V
 */JNIEXPORT jint JNICALL Java_vng_wmb_service_SoundTouchEffect_init(JNIEnv *,
		jobject);

/*
 * Class:     vng_wmb_service_SoundTouchEffect
 * Method:    changeTempo
 * Signature: (I)V
 */

JNIEXPORT void JNICALL Java_vng_wmb_service_SoundTouchEffect_changeTempo(
		JNIEnv *, jobject, jdouble);

/*
 * Class:     vng_wmb_service_SoundTouchEffect
 * Method:    changePitch
 * Signature: (I)V
 */

JNIEXPORT void JNICALL Java_vng_wmb_service_SoundTouchEffect_changePitch(
		JNIEnv *, jobject, jdouble);

/*
 * Class:     vng_wmb_service_SoundTouchEffect
 * Method:    changeRate
 * Signature: (I)V
 */

JNIEXPORT void JNICALL Java_vng_wmb_service_SoundTouchEffect_changeRate(
		JNIEnv *, jobject, jdouble);

/*
 * Class:     vng_wmb_service_SoundTouchEffect
 * Method:    destroy
 * Signature: ()V
 */

JNIEXPORT void JNICALL Java_vng_wmb_service_SoundTouchEffect_destroy(JNIEnv *,
		jobject);

/*
 * Class:     vng_wmb_service_SoundTouchEffect
 * Method:    createSoundTouch
 * Signature: ()V
 */

JNIEXPORT void JNICALL Java_vng_wmb_service_SoundTouchEffect_createSoundTouch(
		JNIEnv *, jobject, jdouble, jdouble, jdouble);

int processBlockForSoundTouch(short*& playerBuffer, int size);

#ifdef __cplusplus
}
#endif
#endif
