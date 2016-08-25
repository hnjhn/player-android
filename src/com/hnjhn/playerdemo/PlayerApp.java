package com.hnjhn.playerdemo;

import android.app.Application;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

public class PlayerApp extends Application {
	private static final String TAG = "PlayerApp";

	private static PlayerApp mInstance;
	
	static {
		mInstance = null;
	}

	public PlayerApp() {
		super();
		Log.i(TAG, "PlayerApp::construction");
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		mInstance = this;
	}

	public static PlayerApp getContext() {
		return mInstance;
	}
	
	public static Resources getResource(){
		return mInstance.getResources();
	}
	
	public static String SDCard(){
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
}
