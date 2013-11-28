#include <jni.h>

#ifndef _RECORDINTERFACE_H_
#define _RECORDINTERFACE_H_

#ifdef __cplusplus
extern "C" {
#endif

void Java_com_voiceeffect_activity_VoiceService_init(JNIEnv *, jobject);

void Java_com_voiceeffect_activity_VoiceService_startRecord(JNIEnv *, jobject);

void Java_com_voiceeffect_activity_VoiceService_stopRecord(JNIEnv *, jobject);

void Java_com_voiceeffect_activity_VoiceService_playMusic(JNIEnv *, jobject);

void Java_com_voiceeffect_activity_VoiceService_stopMusic(JNIEnv *, jobject);

void Java_com_voiceeffect_activity_VoiceService_destroy(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif

#endif
