LOCAL_PATH := $(call my-dir)
JNI_PATH := $(LOCAL_PATH)

include $(CLEAR_VARS)
LOCAL_MODULE := lameMp3
MY_FILE_LIST := $(wildcard $(LOCAL_PATH)/LameMp3/*.c)
LOCAL_SRC_FILES := $(MY_FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_CFLAGS = -DSTDC_HEADERS -DHAVE_MPGLIB
include $(BUILD_STATIC_LIBRARY)

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
LOCAL_MODULE := echoAudacity
MY_FILE_LIST := $(wildcard $(LOCAL_PATH)/EchoEffect/*.cpp)
LOCAL_SRC_FILES := $(MY_FILE_LIST:$(LOCAL_PATH)/%=%)
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := soundEffect
MY_FILE_LIST := $(wildcard $(LOCAL_PATH)/*.cpp)
LOCAL_SRC_FILES := $(MY_FILE_LIST:$(LOCAL_PATH)/%=%)
#LOCAL_SRC_FILES := AudioService.cpp SoundTouchWrapper.cpp Log.cpp WavFile.cpp 
LOCAL_LDLIBS    := -landroid -llog -lOpenSLES

LOCAL_STATIC_LIBRARIES :=  soundTouch reverbAudacity echoAudacity #opencore_amrnben lameMp3
include $(BUILD_SHARED_LIBRARY)

LOCAL_PATH := $(JNI_PATH)
include $(JNI_PATH)/opencore/Android.mk

