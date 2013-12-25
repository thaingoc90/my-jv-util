#include "Log.h"
#include <android/log.h>

void Log::info(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_INFO, "JNI_SE", pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_INFO, "JNI_SE", "\n");
	va_end(lVarArgs);
}
void Log::error(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_ERROR, "JNI_SE", pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_ERROR, "JNI_SE", "\n");
	va_end(lVarArgs);
}

void Log::warn(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_WARN, "JNI_SE", pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_WARN, "JNI_SE", "\n");
	va_end(lVarArgs);
}

void Log::debug(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_DEBUG, "JNI_SE", pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_DEBUG, "JNI_SE", "\n");
	va_end(lVarArgs);
}
