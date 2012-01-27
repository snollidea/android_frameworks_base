/*
 * Copyright (C) 2006 The Android Open Source Project
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

package com.android.server.am;

import com.android.internal.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WimmErrorDialog extends Dialog {
    private static final int MSG_ENABLE_DIALOG = 0;
    private static final int MSG_DISMISS_DIALOG = 1;
        
    private TextView mTitleView;
    private TextView mMessageView;
    private LinearLayout mButtonLayout;
    
    private Button mButtonPositive;
    private Button mButtonNegative;
    private Button mButtonNeutral;

    private Message mButtonPositiveMessage;
    private Message mButtonNegativeMessage;
    private Message mButtonNeutralMessage;
    
    public WimmErrorDialog(Context context) {
        super(context);
        
        final Window win = getWindow();
        
        win.setType(LayoutParams.TYPE_SYSTEM_ALERT);
        win.setFlags(LayoutParams.FLAG_ALT_FOCUSABLE_IM, LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        win.setBackgroundDrawable(new ColorDrawable(0x00000000));

        LayoutInflater inf = LayoutInflater.from(context);
        View view = inf.inflate(com.android.internal.R.layout.wimm_error_dialog, null);
	    setContentView(view);
	    
        mTitleView = ((TextView)findViewById(com.android.internal.R.id.title_text));
        mMessageView = ((TextView)findViewById(com.android.internal.R.id.message_text));
		
		mButtonLayout = (LinearLayout)findViewById(R.id.button_layout);
        mButtonNeutral = (Button)findViewById(R.id.button1);
        mButtonNegative = (Button)findViewById(R.id.button2);
        mButtonPositive = (Button)findViewById(R.id.button3);
    }

    public void onStart() {
        super.onStart();
        setEnabled(false);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_ENABLE_DIALOG), 1000);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mConsuming) {
            //Log.i(TAG, "Consuming: " + event);
            return true;
        }
        //Log.i(TAG, "Dispatching: " + event);
        return super.dispatchKeyEvent(event);
    }

    private void setEnabled(boolean enabled) {
        mButtonPositive.setEnabled(enabled);
        mButtonNegative.setEnabled(enabled);
        mButtonNeutral.setEnabled(enabled);
    }

    private boolean mConsuming = true;
    
    public void setButton(CharSequence text, Message msg) {
        setButton(DialogInterface.BUTTON_POSITIVE, text, msg);
    }
    
    public void setButton(int whichButton, CharSequence text, Message msg) {
        Button b = null;
        
        switch(whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                b = mButtonPositive;
                mButtonPositiveMessage = msg;
                break;
                
            case DialogInterface.BUTTON_NEGATIVE:
                b = mButtonNegative;
                mButtonNegativeMessage = msg;
                break;
                
            case DialogInterface.BUTTON_NEUTRAL:
                b = mButtonNeutral;
                mButtonNeutralMessage = msg;
                break;
        }
        if ( b==null ) return;
        
        // update button
        b.setText(text);
        b.setVisibility(View.VISIBLE);
        b.setOnClickListener( mButtonHandler );
        
        // orient buttons vertically if we're showing all 3
        if ( ( mButtonPositive.getVisibility() == View.VISIBLE ) &&
             ( mButtonNegative.getVisibility() == View.VISIBLE ) && 
             ( mButtonNeutral.getVisibility() == View.VISIBLE ) ) {
            mButtonLayout.setOrientation(LinearLayout.VERTICAL);
        } else {
            mButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
        }             
    }
    
    public void setMessage(CharSequence message) {
    	mMessageView.setText(message);
    }
    
    public void setTitle(CharSequence title) {
    	mTitleView.setText(title);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ENABLE_DIALOG:
                    mConsuming = false;
                    setEnabled(true);
                    break;
                    
                case MSG_DISMISS_DIALOG:
                    dismiss();
                    break;
            }
        }
    };

    private View.OnClickListener mButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Message msg = null;
            
            if (v == mButtonPositive && mButtonPositiveMessage != null) {
                msg = mButtonPositiveMessage;
            } else if (v == mButtonNegative && mButtonNegativeMessage != null) {
                msg = mButtonNegativeMessage;
            } else if (v == mButtonNeutral && mButtonNeutralMessage != null) {
                msg = mButtonNeutralMessage;
            }

            if ( msg != null ) Message.obtain(msg).sendToTarget();
                
            // Post a message so we dismiss after the above handlers are executed
            mHandler.obtainMessage(MSG_DISMISS_DIALOG).sendToTarget();
        }
    };
}
