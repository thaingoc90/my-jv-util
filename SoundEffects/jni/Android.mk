LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := soundTouch
MY_FILE_LIST := $(wildcard $(LOCAL_PATH)/SoundTouch/*.cpp)
LOCAL_SRC_FILES := $(MY_FILE_LIST:$(LOCAL_PATH)/%=%)
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := reverbAudacity
MY_FILE_LIST := $(wildcard $(LOCAL_PATH)/ReverbAuda/*.cpp)
LOCAL_SRC_FILES := $(MY_FILE_LIST:$(LOCAL_PATH)/%=%)
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := soundEffect
MY_FILE_LIST := $(wildcard $(LOCAL_PATH)/*.cpp)
LOCAL_SRC_FILES := $(MY_FILE_LIST:$(LOCAL_PATH)/%=%)
#LOCAL_SRC_FILES := AudioService.cpp SoundTouchWrapper.cpp Log.cpp WavFile.cpp
LOCAL_LDLIBS    := -landroid -llog -lOpenSLES

LOCAL_STATIC_LIBRARIES := soundTouch reverbAudacity
include $(BUILD_SHARED_LIBRARY)
