LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PREBUILT_LIBS:= lib_Down_Sampler_ARM_GA_Library.so

include $(BUILD_MULTI_PREBUILT)

LOCAL_SHARED_LIBRARIES := lib_Down_Sampler_ARM_GA_Library
