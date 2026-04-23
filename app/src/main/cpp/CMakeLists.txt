cmake_minimum_required(VERSION 3.18)
project(llama-android-text CXX C)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# QNN SDK is optional and disabled for text model focus
# set(QNN_SDK_ROOT /data/qairt/2.39.0.250926)
# Comment out all QNN file copies - not needed for text models

if(WIN32)
    set(PLATFORM_NAME "windows")
    set(HOST_TAG "windows-x86_64")
else()
    set(PLATFORM_NAME "linux")
    set(HOST_TAG "linux-x86_64")
endif()

set(OUTPUT_SUFFIX ${CMAKE_ANDROID_ARCH_ABI})
set(PLATFORM_LIBS log android dl m c)
# GoogleBuild options
# set(ANDROID_TOOLCHAIN_ROOT ${CMAKE_ANDROID_NDK}/toolchains/llvm/prebuilt/${HOST_TAG} CACHE PATH "Android NDK root path")

add_compile_options(
    -O3
    -Wall
    -fno-rtti
    -fPIC
    # -Werror
    -fvisibility=hidden
)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wno-error=for-loop-analysis")

# ZSTD
set(ZSTD_ROOT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/zstd)

set(ZSTD_BUILD_PROGRAMS OFF CACHE BOOL "Build zstd programs" FORCE)
set(ZSTD_BUILD_STATIC ON CACHE BOOL "Build static zstd library" FORCE)
set(ZSTD_MULTITHREAD_SUPPORT OFF CACHE BOOL "Enable multithreading support" FORCE)
set(ZSTD_BUILD_TESTS OFF CACHE BOOL "Build zstd tests" FORCE)

add_subdirectory(${ZSTD_ROOT_DIR}/build/cmake zstd EXCLUDE_FROM_ALL)

set(ZSTD_INCLUDE_DIR ${ZSTD_ROOT_DIR}/lib)

# ============================================
# MNN (Optional - Disabled for Text Modle Focus)
# ============================================
# MNN Library is only needed for image upscaling
# Uncomment if you need image model support
# ============================================
# set(MNN_ROOT_DIR 3rdparty/MNN)
# set(MNN_BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
# set(MNN_BUILD_TOOLS OFF CACHE BOOL "" FORCE)
# set(MNN_LOW_MEMORY ON CACHE BOOL "" FORCE)
# set(MNN_CPU_WEIGHT_DEQUANT_GEMM ON CACHE BOOL "" FORCE)
# set(MNN_BUILD_FOR_ANDROID ON CACHE BOOL "" FORCE)
# set(MNN_BUILD_FOR_ANDROID_COMMAND ON CACHE BOOL "" FORCE)
# set(MNN_USE_LOGCAT OFF CACHE BOOL "" FORCE)
# set(MNN_OPENCL ON CACHE BOOL "" FORCE)
# set(MNN_SUPPORT_TRANSFORMER_FUSE ON CACHE BOOL "" FORCE)
# add_subdirectory(3rdparty/MNN)

set(XTENSOR_USE_XSIMD ON)

# ============================================
# Include Directories (Minimal for text models)
# ============================================
set(PACKAGE_INCLUDES
    ${CMAKE_CURRENT_SOURCE_DIR}/include/
    ${CMAKE_CURRENT_SOURCE_DIR}/src
    ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/cpp-httplib
    ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/json/include/nlohmann
    ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/json/include
    ${ZSTD_INCLUDE_DIR}
)

# ============================================
# tokenizers-cpp (Optional - Disabled for now)
# ============================================
# TokenizersText is optional - only use if needed for preprocessing
# Uncomment if you need tokenizer support
# set(TOKENZIER_CPP_PATH ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/tokenizers-cpp)
# if(NOT DEFINED CMAKE_ANDROID_ARCH_ABI)
#     set(CMAKE_ANDROID_ARCH_ABI "arm64-v8a" CACHE STRING "Android ABI" FORCE)
# endif()
# if(NOT DEFINED ANDROID_ABI)
#     set(ANDROID_ABI ${CMAKE_ANDROID_ARCH_ABI} CACHE STRING "Android ABI" FORCE)
# endif()
# add_subdirectory(${TOKENZIER_CPP_PATH} tokenizers EXCLUDE_FROM_ALL)

# ============================================
# Set ABI configuration
# ============================================
if(NOT DEFINED CMAKE_ANDROID_ARCH_ABI)
    set(CMAKE_ANDROID_ARCH_ABI "arm64-v8a" CACHE STRING "Android ABI" FORCE)
endif()
if(NOT DEFINED ANDROID_ABI)
    set(ANDROID_ABI ${CMAKE_ANDROID_ARCH_ABI} CACHE STRING "Android ABI" FORCE)
endif()

# ============================================
# llama.cpp Integration - Main Build Target
# ============================================
add_subdirectory(3rdparty/llama.cpp)

add_library(llama-android SHARED llama-android.cpp)

target_include_directories(llama-android PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/llama.cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/3rdparty/llama.cpp/include
    ${ZSTD_INCLUDE_DIR}
)

target_link_libraries(llama-android
    PRIVATE
    ${PLATFORM_LIBS}
    llama
)

if(ZSTD_FOUND OR EXISTS ${ZSTD_ROOT_DIR})
    target_link_libraries(llama-android PRIVATE libzstd)
endif()

set_target_properties(llama-android PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib/${OUTPUT_SUFFIX}"
)

message(STATUS "============================================")
message(STATUS "llama-android Text Model Build Configuration")
message(STATUS "============================================")
message(STATUS "Platform: ${PLATFORM_NAME}")
message(STATUS "Android ABI: ${CMAKE_ANDROID_ARCH_ABI}")
message(STATUS "Build type: ${CMAKE_BUILD_TYPE}")
message(STATUS "Enabled: llama.cpp (GGUF text models)")
message(STATUS "Disabled: QNN, MNN, tokenizers-cpp (for text model focus)")
message(STATUS "============================================")

