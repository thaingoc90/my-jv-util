LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := voiceEffect
LOCAL_SRC_FILES := Log.cpp RecordService.cpp RecordInterface.cpp
LOCAL_LDLIBS    := -landroid -llog -lOpenSLES
LOCAL_STATIC_LIBRARIES := android_native_app_glue

include $(BUILD_SHARED_LIBRARY)
$(call import-module,android/native_app_glue)
