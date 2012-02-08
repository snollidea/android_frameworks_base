/*
**
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

#include "JNIHelp.h"
#include "jni.h"
#include "android_runtime/AndroidRuntime.h"
#include <utils/misc.h>
#include <fcntl.h>
#include <errno.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/time.h>
#include <time.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <utils/Log.h>

#define  LOG_DEBUG  1
#if LOG_DEBUG
#  define  D(...)   LOGE(__VA_ARGS__)
#else
#  define  D(...)   ((void)0)
#endif

namespace android
{

#define BEEPER_SYS_FILE_ENABLE "/sys/devices/platform/timed-piezo/enable"
#define BEEPER_SYS_FILE_COUNT "/sys/devices/platform/timed-piezo/count"
#define BEEPER_SYS_FILE_DUTY "/sys/devices/platform/timed-piezo/duty"

static int beeperOn(JNIEnv *env, jobject clazz, jint time) {

    D("BeeperOn IN time = %d", time);
    char *filepath = BEEPER_SYS_FILE_ENABLE;
    int fd = open(filepath, O_RDWR);
    if( fd >= 0) {
        char buf[32];
        ssize_t len;
        len = sprintf(buf, "%d", ((int)(time)));
        len = write(fd, buf, len);
        close(fd);
    } else {
        return errno;
    }

    D("BeeperOn OUT");
    return 0;

}

static int setDuty(JNIEnv *env, jobject clazz, jint duty) {
    
    D("setDuty IN duty = %d", duty);
    char *filepath = BEEPER_SYS_FILE_DUTY;
    int fd = open(filepath, O_RDWR);
    if( fd >= 0) {
        char buf[32];
        ssize_t len;
        len = sprintf(buf, "%d", ((int)(duty)));
        len = write(fd, buf, len);
        close(fd);
    } else {
        return errno;
    }
    D("setDuty OUT");
    return 0;
    
}

static int setCount(JNIEnv *env, jobject clazz, jint count) {

    D("setCount IN count = %d", count);
    char *filepath = BEEPER_SYS_FILE_COUNT;
    int fd = open(filepath, O_RDWR);
    if( fd >= 0) {
        char buf[32];
        ssize_t len;
        len = sprintf(buf, "%d", ((int)(count)));
        len = write(fd, buf, len);
        close(fd);
    } else {
        return errno;
    }
    D("setDuty OUT");
    return 0;

}

static JNINativeMethod method_table[] = {
    { "native_beeper_on", "(I)I", (void*)beeperOn },
    { "native_set_duty", "(I)I", (void*)setDuty },
    { "native_set_count", "(I)I", (void*)setCount }
};

int register_android_beeper_Beeper(JNIEnv *env)
{
    return AndroidRuntime::registerNativeMethods(
        env, "android/beeper/Beeper",
        method_table, NELEM(method_table));
}

}