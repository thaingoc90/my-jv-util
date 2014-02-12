LOCAL_PATH := $(call my-dir)
PV_TOP := $(JNI_PATH)/opencore
PV_CFLAGS := $(JNI_PATH)/opencore
include $(CLEAR_VARS)

include $(PV_TOP)/amr_nb_enc.mk
include $(PV_TOP)/amr_nb_common.mk
LOCAL_SRC_FILES += AMRWrapper.cpp


LOCAL_MODULE := opencore_amrnben

LOCAL_CFLAGS :=   $(PV_CFLAGS)
LOCAL_ARM_MODE := arm

LOCAL_STATIC_LIBRARIES := 

LOCAL_SHARED_LIBRARIES := 

LOCAL_C_INCLUDES := \
	$(PV_TOP)/codecs_v2/audio/gsm_amr/amr_nb/enc/src \
 	$(PV_TOP)/codecs_v2/audio/gsm_amr/amr_nb/enc/include \
 	$(PV_TOP)/codecs_v2/audio/gsm_amr/amr_nb/common/include \
	$(PV_TOP)/codecs_v2/audio/gsm_amr/amr_nb/common/src \
 	$(PV_TOP)/oscl \
 	$(PV_INCLUDES)

LOCAL_COPY_HEADERS_TO := $(PV_COPY_HEADERS_TO)

LOCAL_COPY_HEADERS := \
 	$(PV_TOP)/codecs_v2/audio/gsm_amr/amr_nb/enc/include/gsmamr_encoder_wrapper.h

include $(BUILD_STATIC_LIBRARY)
