/*
 * Copyright (C) 2008-2012 The Android Open Source Project
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

package android.net;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.NetworkStateTracker;
import android.os.Handler;
import android.os.IBinder;
import android.util.Config;
import android.util.Log;
import java.util.Set;

/**
 * {@hide}
 */
public class BluetoothStateTracker extends NetworkStateTracker {
    private static final boolean LOCAL_LOGD = Config.LOGD || false;
    
    private static final String TAG = "BluetoothStateTracker";

    private BroadcastReceiver mPanReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                boolean connected = intent.getBooleanExtra("connected", false);
                
                if (LOCAL_LOGD) Log.d(TAG, "PAN connected: " + connected);
                setDetailedState(connected ? DetailedState.CONNECTED : DetailedState.DISCONNECTED);
            }
        };
    
    private BluetoothAdapter mBluetoothAdapter = null;
    
    public BluetoothStateTracker(Context context, Handler target) {
        super(context, target, ConnectivityManager.TYPE_BLUETOOTH, 0, "BLUETOOTH_TETHER", "");       
        if (LOCAL_LOGD) Log.d(TAG, "BluetoothStateTracker");
    }
        
    /**
     * Return the system properties name associated with the tcp buffer sizes
     * for this network.
     */
    public String getTcpBufferSizesPropName() {
        return "net.tcp.buffersize.wifi";
    }
    
    public void startMonitoring() {
        if (LOCAL_LOGD) Log.d(TAG, "startMonitoring");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        mContext.registerReceiver(mPanReceiver, new IntentFilter("pan.connected"));
    }

    /**
     * Disable connectivity to a network
     * @return {@code true} if a teardown occurred, {@code false} if the
     * teardown did not occur.
     */
    public boolean teardown() {
        if (LOCAL_LOGD) Log.d(TAG, "teardown");
        return true;
    }

    /**
     * Reenable connectivity to a network after a {@link #teardown()}.
     */
    public boolean reconnect() {
        if (LOCAL_LOGD) Log.d(TAG, "reconnect");
        return true;
    }

    /**
     * Turn the wireless radio off for a network.
     * @param turnOn {@code true} to turn the radio on, {@code false}
     */
    public boolean setRadio(boolean turnOn) {
        Log.d(TAG, "setRadio");
        return turnOn;
    }

    /**
     * Returns an indication of whether this network is available for
     * connections. A value of {@code false} means that some quasi-permanent
     * condition prevents connectivity to this network.
     */
    public boolean isAvailable() {
        if (LOCAL_LOGD) Log.d(TAG, "isAvailable");
        
        if (!mBluetoothAdapter.isEnabled()) {
            if (LOCAL_LOGD) Log.d(TAG, "bluetooth adapter is not enabled");
            return false;
        }
        
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        
        if (devices.isEmpty()) {
            if (LOCAL_LOGD) Log.d(TAG, "no paired devices");
            return false;
        }
        
        if (LOCAL_LOGD) Log.d(TAG, "is available");
        return true;
    }

    /**
     * Tells the underlying networking system that the caller wants to
     * begin using the named feature. The interpretation of {@code feature}
     * is completely up to each networking implementation.
     * @param feature the name of the feature to be used
     * @param callingPid the process ID of the process that is issuing this request
     * @param callingUid the user ID of the process that is issuing this request
     * @return an integer value representing the outcome of the request.
     * The interpretation of this value is specific to each networking
     * implementation+feature combination, except that the value {@code -1}
     * always indicates failure.
     */
    public int startUsingNetworkFeature(String feature, int callingPid, int callingUid) {
        if (LOCAL_LOGD) Log.d(TAG, "startUsingNetworkFeature");
        return 0;
    }

    /**
     * Tells the underlying networking system that the caller is finished
     * using the named feature. The interpretation of {@code feature}
     * is completely up to each networking implementation.
     * @param feature the name of the feature that is no longer needed.
     * @param callingPid the process ID of the process that is issuing this request
     * @param callingUid the user ID of the process that is issuing this request
     * @return an integer value representing the outcome of the request.
     * The interpretation of this value is specific to each networking
     * implementation+feature combination, except that the value {@code -1}
     * always indicates failure.
     */
    public int stopUsingNetworkFeature(String feature, int callingPid, int callingUid) {
        if (LOCAL_LOGD) Log.d(TAG, "stopUsingNetworkFeature");
        return 0;
    }

    @Override
    public String[] getNameServers() {
        return new String[] { "8.8.8.8" };
    }
}
