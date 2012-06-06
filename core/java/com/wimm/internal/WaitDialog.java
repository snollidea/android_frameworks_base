/*
 * Copyright (C) 2012 WIMM Labs Incorporated
 */
 
package com.wimm.internal.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

/**
 * Quick control to replace some android spinners with something that matches
 * the rest of the system. Only works as a non-cancelable dialog. Should
 * eventually reskin android's progress dialog but this will bridge the few
 * newly exposed places from the 2.3 upgrade and get us past 1.1.
 */
public class WaitDialog extends Dialog {
    private TextView mTitleView = null;

    public WaitDialog(Context context) {
        super(context);

        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));

        LayoutInflater inf = LayoutInflater.from(context);
        FrameLayout mainFrame = (FrameLayout)inf.inflate(com.android.internal.R.layout.wimm_dialog_frame, null);
        mTitleView = (TextView)mainFrame.findViewById(com.android.internal.R.id.title_text);
        mainFrame.findViewById(com.android.internal.R.id.cancel_button).setVisibility(View.GONE);

        FrameLayout contentFrame = (FrameLayout)mainFrame.findViewById(com.android.internal.R.id.content_view);
        contentFrame.addView(new WaitSpinner(context), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        super.setContentView(mainFrame);
    }

    @Override
    public void setTitle(int titleId) {
        if (mTitleView != null) mTitleView.setText(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (mTitleView != null) mTitleView.setText(title);
    }

    private class WaitSpinner extends ImageView {
        public WaitSpinner(Context context) {
            super(context);
            setImageResource(com.android.internal.R.drawable.wimm_waitspinner);
        }

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            Animation a = new RotateAnimation(0, 3600, w/2, h/2);
            a.setInterpolator(new LinearInterpolator());
            a.setDuration(30000);
            a.setRepeatCount(Animation.INFINITE);
            startAnimation(a);
        }
    };
}
