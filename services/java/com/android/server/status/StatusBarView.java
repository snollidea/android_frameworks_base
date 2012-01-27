package com.android.server.status;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import android.os.Handler;  //add dangku
import android.os.ServiceManager;   //add dangku
import android.os.SystemClock;   //add dangku
import android.view.IWindowManager;   //add dangku
import android.os.Message;
import android.os.RemoteException;

import com.android.internal.R;

public class StatusBarView extends FrameLayout {
    private static final String TAG = "StatusBarView";

    StatusBarService mService;
    boolean mTracking;
    int mStartX, mStartY;
    ViewGroup mNotificationIcons;
    ViewGroup mStatusIcons;
    View mDate;
    FixedSizeDrawable mBackground;
    
    /*add dankgu*/
    //android.widget.LinearLayout keysLayout;
    android.widget.ImageView btnHome;
    android.widget.ImageView btnBack;
    android.widget.ImageView btnMenu;
    private static final int KEY_HOME = 1000;
    private static final int KEY_BACK = 1001;
    private static final int KEY_MENU = 1002;

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNotificationIcons = (ViewGroup)findViewById(R.id.notificationIcons);
        mStatusIcons = (ViewGroup)findViewById(R.id.statusIcons);
        mDate = findViewById(R.id.date);

        mBackground = new FixedSizeDrawable(mDate.getBackground());
        mBackground.setFixedBounds(0, 0, 0, 0);
        mDate.setBackgroundDrawable(mBackground);
        
        /* Begin : Added by dangku */
        //keysLayout = (android.widget.LinearLayout)findViewById(R.id.keys);
        btnHome = (android.widget.ImageView)findViewById(R.id.status_home);
        btnBack = (android.widget.ImageView)findViewById(R.id.status_back);
        btnMenu = (android.widget.ImageView)findViewById(R.id.status_menu);
        
        btnHome.setOnClickListener(mKeysListener);
        btnBack.setOnClickListener(mKeysListener);
        btnMenu.setOnClickListener(mKeysListener);
        /* End : Added by dangku */
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mService.onBarViewAttached();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mService.updateExpandedViewPos(StatusBarService.EXPANDED_LEAVE_ALONE);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        // put the date date view quantized to the icons
        int oldDateRight = mDate.getRight();
        int newDateRight;

        newDateRight = getDateSize(mNotificationIcons, oldDateRight,
                getViewOffset(mNotificationIcons));
        if (newDateRight < 0) {
            int offset = getViewOffset(mStatusIcons);
            if (oldDateRight < offset) {
                newDateRight = oldDateRight;
            } else {
                newDateRight = getDateSize(mStatusIcons, oldDateRight, offset);
                if (newDateRight < 0) {
                    newDateRight = r;
                }
            }
        }
        int max = r - getPaddingRight();
        if (newDateRight > max) {
            newDateRight = max;
        }

        mDate.layout(mDate.getLeft(), mDate.getTop(), newDateRight, mDate.getBottom());
        mBackground.setFixedBounds(-mDate.getLeft(), -mDate.getTop(), (r-l), (b-t));
    }

    /**
     * Gets the left position of v in this view.  Throws if v is not
     * a child of this.
     */
    private int getViewOffset(View v) {
        int offset = 0;
        while (v != this) {
            offset += v.getLeft();
            ViewParent p = v.getParent();
            if (v instanceof View) {
                v = (View)p;
            } else {
                throw new RuntimeException(v + " is not a child of " + this);
            }
        }
        return offset;
    }

    private int getDateSize(ViewGroup g, int w, int offset) {
        final int N = g.getChildCount();
        for (int i=0; i<N; i++) {
            View v = g.getChildAt(i);
            int l = v.getLeft() + offset;
            int r = v.getRight() + offset;
            if (w >= l && w <= r) {
                return r;
            }
        }
        return -1;
    }

    /**
     * Ensure that, if there is no target under us to receive the touch,
     * that we process it ourself.  This makes sure that onInterceptTouchEvent()
     * is always called for the entire gesture.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
            mService.interceptTouchEvent(event);
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	if(  (event.getX() > btnHome.getRight())
		  &&  (event.getX() < btnMenu.getLeft())){
        	return mService.interceptTouchEvent(event)
                ? true : super.onInterceptTouchEvent(event);
     	}

     	return false;
   }
    
    android.view.View.OnClickListener mKeysListener = new android.view.View.OnClickListener(){
    	public void onClick(View v) {
    	  switch (v.getId()) {
    		case R.id.status_home:
    			mKeysHandler.sendEmptyMessage(KEY_HOME);
    			break;
    		case R.id.status_back:
    			mKeysHandler.sendEmptyMessage(KEY_BACK);
    			break;
    		case R.id.status_menu:
    			mKeysHandler.sendEmptyMessage(KEY_MENU);
    			break;
    		default:
    			break;
    	  }
    	}
    };
    
    /* Begin : ADDED by dangku 20100831 */
   // private static final int KEY_HOME = 1000;
   // private static final int KEY_BACK = 1001;
   // private static final int KEY_MENU = 1002;
    private Handler mKeysHandler = new Handler(){
    public void handleMessage(Message msg) {
       switch (msg.what) {
    case KEY_HOME:
       sendKey(KeyEvent.KEYCODE_HOME);
       break;
    case KEY_BACK:
       sendKey(KeyEvent.KEYCODE_BACK);
       break;
    case KEY_MENU:
       sendKey(KeyEvent.KEYCODE_MENU);
       break;
    default:
       break;
    }
    }

    private void sendKey(int keyCode) {
    long now = SystemClock.uptimeMillis();
    long n = System.currentTimeMillis();
    Log.d("Tiger", "Intent.ACTION_SOFT_"+keyCode+"_PRESSED   0="+n);
    try {
       KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0);
       KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0);
       Log.d("Tiger", "Intent.ACTION_SOFT_"+keyCode+"_PRESSED   1="+(System.currentTimeMillis()/*-n*/));
       IWindowManager wm = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
       Log.d("Tiger", "Intent.ACTION_SOFT_"+keyCode+"_PRESSED   2="+(System.currentTimeMillis()/*-n*/));
       wm.injectKeyEvent(down, false);
       Log.d("Tiger", "Intent.ACTION_SOFT_"+keyCode+"_PRESSED   3="+(System.currentTimeMillis()/*-n*/));
       wm.injectKeyEvent(up, false);
       Log.d("Tiger", "Intent.ACTION_SOFT_"+keyCode+"_PRESSED   4="+(System.currentTimeMillis()/*-n*/));
    } catch (RemoteException e) {
       Log.i("Input", "DeadOjbectException");
    }
    }
    };
    /* End : ADDED by TigerPan 20100831 */
}

