/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.beeper;

import android.util.Log;

/**
 * Class that provides access to some of the beerper management functions.
 *
 * @hide
 */
public class Beeper {
    
    public static final String TAG = "Beeper";
    public static final int MAX_COUNT = 255;
    public static final int MIN_COUNT = 40; //Melody change 32 t0 40
    public static final int MAX_DUTY  = 127;
    public static final int MIN_DUTY  = 0;
    
    /*
     * set the beeper's duty.
     * {@hide}
     */
    public static native int native_set_duty(int duty);

    /* 
     * set the beeper's count.
     * {@hide}
     */
    public static native int native_set_count(int count);

    /* 
     * open the beeper on.
     * {@hide}
     */
    public static native int native_beeper_on(int time);
    
    /* 
     * close the beeper.
     */
    public static void beeperOff() {
    
        native_beeper_on(0);

    }

    public static void beeperOn(int count, int duty, int time) {
    
        // wuhua 20100930 modify begin: add commmets and change if case
        if (count > MAX_COUNT || count < MIN_COUNT || duty > MAX_DUTY || duty < MIN_DUTY) {
            throw new IllegalArgumentException("inlegal arguments!! count should > " + MIN_COUNT + 
                    " and < " + MAX_COUNT +  "; duty should be > " + MIN_DUTY + " and < " + MAX_DUTY);
        }
        if (native_set_count(count) != 0) {
            Log.e(TAG, "beeper set count error");
        }

        if (native_set_duty(duty) != 0) {
            Log.e(TAG, "beeper set duty error");
        }
        
        if (native_beeper_on(time) != 0) {
            Log.e(TAG, "beeper on error");
        }
        // wuhua 20100930 modify end: add commmets and change if case
    
    }
    
}
