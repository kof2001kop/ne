LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := show
LOCAL_SRC_FILES := show.cpp

include $(BUILD_SHARED_LIBRARY)
