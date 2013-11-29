#include "RecordInterface.h"
#include "RecordService.h"

RecordService recordService;

void Java_com_voiceeffect_activity_VoiceService_init(JNIEnv* pEnv,
		jobject pThis) {
	recordService.initEngineObj();
	recordService.initPlayer();
}

void Java_com_voiceeffect_activity_VoiceService_startRecord(JNIEnv* pEnv,
		jobject pThis) {
	recordService.recordSound();
}

void Java_com_voiceeffect_activity_VoiceService_stopRecord(JNIEnv* pEnv,
		jobject pThis) {
	recordService.stopRecord();
}

void Java_com_voiceeffect_activity_VoiceService_playMusic(JNIEnv* pEnv,
		jobject pThis) {
	recordService.startPlayer();
}

void Java_com_voiceeffect_activity_VoiceService_stopMusic(JNIEnv* pEnv,
		jobject pThis) {

}

void Java_com_voiceeffect_activity_VoiceService_destroy(JNIEnv* pEnv,
		jobject pThis) {
	recordService.destroyEngineObj();
}

void signalToUI(JNIEnv* pEnv, jobject pThis) {
	jclass infoClass = pEnv->FindClass(
			"com/voiceeffect/activity/RecordActivity");
	jmethodID methodSet = pEnv->GetMethodID(infoClass, "setPlaying", "(Z)V");
	pEnv->CallVoidMethod(pThis, methodSet, JNI_FALSE);
}
