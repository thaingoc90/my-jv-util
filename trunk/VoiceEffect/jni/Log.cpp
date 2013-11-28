#include "Log.h"
#include <android/log.h>

void Log::info(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_INFO, "JNI_VE", pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_INFO, "JNI_VE", "\n");
	va_end(lVarArgs);
}
void Log::error(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_ERROR, "JNI_VE", pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_ERROR, "JNI_VE", "\n");
	va_end(lVarArgs);
}

void Log::warn(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_WARN, "JNI_VE", pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_WARN, "JNI_VE", "\n");
	va_end(lVarArgs);
}

void Log::debug(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_DEBUG, "JNI_VE", pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_DEBUG, "JNI_VE", "\n");
	va_end(lVarArgs);
}
