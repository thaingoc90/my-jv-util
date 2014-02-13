#include "Log.h"
#include <android/log.h>

const char* PRO_LOG_TAG = "JNI_SE";
void Log::info(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_INFO, PRO_LOG_TAG, pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_INFO, PRO_LOG_TAG, "\n");
	va_end(lVarArgs);
}
void Log::error(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_ERROR, PRO_LOG_TAG, pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_ERROR, PRO_LOG_TAG, "\n");
	va_end(lVarArgs);
}

void Log::warn(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_WARN, PRO_LOG_TAG, pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_WARN, PRO_LOG_TAG, "\n");
	va_end(lVarArgs);
}

void Log::debug(const char* pMessage, ...) {
	va_list lVarArgs;
	va_start(lVarArgs, pMessage);
	__android_log_vprint(ANDROID_LOG_DEBUG, PRO_LOG_TAG, pMessage, lVarArgs);
	__android_log_print(ANDROID_LOG_DEBUG, PRO_LOG_TAG, "\n");
	va_end(lVarArgs);
}
