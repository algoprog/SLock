package com.nodeflop.slock.lockutil;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.nodeflop.slock.R;

import java.lang.reflect.Method;


public class HomeKeyLocker {
	
    private OverlayDialog mOverlayDialog;
    

    public void lock(Activity activity) {
    	
        if (mOverlayDialog == null) {
        	
            mOverlayDialog = new OverlayDialog(activity);

            mOverlayDialog.show();

            mOverlayDialog.show();
        }
    }

    public void unlock() {
    	
        if (mOverlayDialog != null) {
            mOverlayDialog.dismiss();
            mOverlayDialog = null;
        }
    }

    private static class OverlayDialog extends AlertDialog {

        public OverlayDialog(Activity activity) {
        	
            super(activity, R.style.Dialog_Fullscreen);
            
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.type = TYPE_SYSTEM_ALERT;
            params.dimAmount = 0.0F; 
            params.width = 0;
            params.height = 0;
            params.gravity = Gravity.BOTTOM;
            getWindow().setAttributes(params);
            getWindow().setFlags(FLAG_SHOW_WHEN_LOCKED | FLAG_NOT_TOUCH_MODAL, 0xffffff);
            setOwnerActivity(activity);
            setCancelable(false);
            
            
        }

        public final boolean dispatchTouchEvent(MotionEvent motionevent) { return true; }

        protected final void onCreate(Bundle bundle) {
        	
            super.onCreate(bundle);
            
            FrameLayout framelayout = new FrameLayout(getContext());
            framelayout.setBackgroundColor(0);
            setContentView(framelayout);
            
        }

        
        
    }
}
