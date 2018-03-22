LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := show
LOCAL_SRC_FILES := show.cpp
LOCAL_SHARED_LIBRARIES := boost_serialization_shared

include $(BUILD_SHARED_LIBRARY)

$(call import-module,boost/1.59.0)