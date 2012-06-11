package com.android.internal.policy.impl;

import com.android.internal.app.ShutdownThread;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

// NOTE: This is drastically different from the default android GlobalActions.
//       I believe some of the reasons for this are 1) at some point we were trying
//       to not change much of android 2) it utilizes some UI which was contained
//       in a separate framework. 1 is no longer valid, and I believe most of the
//       resources required by 2 were pushed down into the base framework. Would
//       be great to revisit this design and fall more in line with the standard.
//       android code base. The fact that this is currently spawning a service
//       elsewhere to display a dialog rather than building and presenting one
//       itself is unfortunate.
class GlobalActions {
    private final Context mContext;

    // These strings need to stay in sync with those in the wimm framework.
    // We can't reference the framework from here unfortunately.
    private static final String ACTION_SHOW_GLOBAL_DIALOG = "com.wimm.action.SHOW_GLOBAL_DIALOG";
    private static final String ACTION_HIDE_GLOBAL_DIALOG = "com.wimm.action.HIDE_GLOBAL_DIALOG";
    
    private static final String ACTION_INITIATE_SHUTDOWN = "com.wimm.action.INITIATE_SHUTDOWN";

    public GlobalActions(Context context) {
        mContext = context;

        context.registerReceiver(
            mShutdownReceiver, 
            new IntentFilter(ACTION_INITIATE_SHUTDOWN),
            android.Manifest.permission.REBOOT,
            null
        );

        context.registerReceiver(
            mDialogReceiver, 
            new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        );
    }

    public void showDialog() {
        mContext.startService(new Intent(ACTION_SHOW_GLOBAL_DIALOG));
    }

    public void hideDialog() {
        mContext.startService(new Intent(ACTION_HIDE_GLOBAL_DIALOG));
    }
    
    private BroadcastReceiver mShutdownReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ShutdownThread.shutdown(context, false);
        }
    };
    
    private BroadcastReceiver mDialogReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String reason = intent.getStringExtra(PhoneWindowManager.SYSTEM_DIALOG_REASON_KEY);
            if (!PhoneWindowManager.SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS.equals(reason)) {
                hideDialog();
            }
        }
    };
}
