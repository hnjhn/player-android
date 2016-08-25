package com.hnjhn.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class CommUtils {
	// parameters for the encoder 
	//private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding 
	//private static final int FRAME_RATE = 15;               // 15fps 
	//private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames 
	
	public CommUtils(Context context){
		//urls = new String[3];
		//mContext = context;
		
		//app = null;
		Log.i("Configure", "Configure::construction");
	}	
	
//	public void testEncodeDecodeVideoFromBufferToBufferQCIF() throws Exception { 
//	 setParameters(176, 144, 1000000); 
//	} 
//	public void testEncodeDecodeVideoFromBufferToBufferQVGA() throws Exception { 
//	setParameters(320, 240, 2000000); 
//	} 
//	public void testEncodeDecodeVideoFromBufferToBuffer720p() throws Exception { 
//	setParameters(1280, 720, 6000000); 
//	}	
	
    @TargetApi(19)
    public static void setTranslucentStatus(final Activity act, boolean on) {
        Window win = act.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

	public static void setTranslucentStatus(final Activity act, int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        	setTranslucentStatus(act, true);
            
    		SystemBarTintManager tintManager = new SystemBarTintManager(act);		
    		tintManager.setStatusBarTintEnabled(true);  
    		tintManager.setNavigationBarTintEnabled(true);
    		
    		tintManager.setTintColor(/*Color.parseColor("#FF0BB68B")*/color);
        }	
	}
}
