package com.hnjhn.playerdemo;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hnjhn.player.HnPlayer;
import com.hnjhn.player.IHnPlayEvent;
import com.hnjhn.player.IHnPlayer;
import com.hnjhn.player.PlayEvent;
import com.hnjhn.player.ui.VideoView;
import com.hnjhn.util.CommUtils;
import com.hnjhn.util.SystemBarTintManager;

/*RTSP Player*/
public class PlayActivity extends Activity implements IHnPlayer, IHnPlayEvent {
	private static final String TAG = "PlayActivity";
	
	private VideoView mVideoView;
	
	private PlayEvent mEvent;
	private HnPlayer mPlayer;
	
	private int mPlayFlag;	
	private String URL;
	private String DATA;
	
	private TextView item_desc;	
	private Button btn_photograph;
	
	private SystemBarTintManager tintManager;
    /*Load the .so*/
	static{
		System.loadLibrary("myutil");
		System.loadLibrary("hnplayer");
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_player);
        
        getWindow().setFormat(PixelFormat.TRANSLUCENT);          
        //setTitle("player");    

		Intent intent = this.getIntent();
		
        mPlayFlag = intent.getIntExtra("TYPE", HnPlayer.PLAY_FLAG_FILE);
		//mPlayFlag = HnPlayer.PLAY_FLAG_FILE;		
		//Get URL
		String url = SetActivity.instance().getUrl();//intent.getStringExtra("URL");
		if(url==null || url.isEmpty()){
			gotoSettings(true);		//goto Set Activity
		} else {
			if(url.indexOf("://")<0){
				URL = String.format("%1$s/%2$s", PlayerApp.SDCard(), url);
			} else {
				URL = url;
			}
		}

		//Get Parms		
		String data = intent.getStringExtra("DATA");
		if(data==null || data.isEmpty()){
			DATA = "0.0.0.0,0,0.0.0.0";
		} else {
			DATA = data;
		}		
        
        mVideoView = (VideoView) findViewById(R.id.myVideoView);
        
        mPlayer = HnPlayer.createNew(this, mVideoView);
		mEvent = mPlayer.getEvent();
		
        initView();
        
        mEvent.sendMessage(1001, 5000);	
        
        Log.d(TAG, "Activity -> onCreate.");
    }
    
    private void initView(){
    	//Actvity Title
        ((TextView)findViewById(R.id.custom_title)).setText(R.string.app_title);
        
        //Close button
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exit(true);
			}
		});    
        
        mVideoView.setOnFullScreen(new VideoView.OnFullScreen() {		
			@Override
			public void onFullScreen() {
				// TODO Auto-generated method stub
				toggleFullScreen();
			}
		});
//        
//        mVideoView.setOnClickEvent(new VideoView.OnClickEvent() {			
//			public void onSingleClick(View view) {
//				// TODO Auto-generated method stub
//			}
//			
//			public void onDoubleClick(View view) {
//				// TODO Auto-generated method stub
//			}
//		});
        
        btn_photograph = (Button)findViewById(R.id.btn_photograph);
        btn_photograph.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.setEnabled(false);
				//playSound();
				mPlayer.photograph();
			}
		});
        //btn_photograph.setEnabled(false);
		
		item_desc = (TextView) findViewById(R.id.video_item_desc);
		item_desc.setText(R.string.app_copyright);
		item_desc.setTextSize(12);	
		item_desc.setTextColor(0xFFCCCCCC);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			CommUtils.setTranslucentStatus(this, true);
		}		
		tintManager = new SystemBarTintManager(this);		
		tintManager.setStatusBarTintEnabled(true); 		
		tintManager.setTintColor(Color.parseColor("#FF0BB68B"));
    }

	private void toggleFullScreen(){
		View title_panel = this.findViewById(R.id.title_panel);
		View btn_panel = findViewById(R.id.btn_panel);
		
		Log.d(TAG, "Activity -> toggleFullScreen, ORIENTATION:" + getRequestedOrientation());
		
		if(mVideoView.isFullScreen()){ //to Full	
			//1
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			tintManager.setStatusBarTintEnabled(false);					
			
			btn_panel.setVisibility(View.GONE);
			title_panel.setVisibility(View.GONE);
			item_desc.setVisibility(View.GONE);
			
			//isFullScreen = true;		
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		else
		{		
			//1
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			tintManager.setStatusBarTintEnabled(true);
			
			btn_panel.setVisibility(View.VISIBLE);
			title_panel.setVisibility(View.VISIBLE);		
			item_desc.setVisibility(View.VISIBLE);
			
			//isFullScreen = false;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		//toggle end
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		Log.d(TAG, "Activity -> onConfigurationChanged, ORIENTATION:" + getRequestedOrientation());// +", newConfig:" + newConfig.orientation

		//SDL redraw
		mPlayer.redraw();		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			if(mVideoView.isFullScreen()){
				mVideoView.toggleFullScreen();
			}
			
			return false;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			//popupMenu();
			//return false;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG, "Activity -> onPause");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
		super.onResume();
		Log.v(TAG, "Activity -> onResume");		
	}	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "Activity -> onDestroy");
	}
	
//	@Override
//	public void onBackPressed() {			
//		Log.d(TAG, "Activity -> onBackPressed");
//		mPlayer.exit();
//		
//		super.onBackPressed();			
//		return;
//	}
    
	public void exit(boolean cls){		
		//Log.d(TAG, "Status: " + mPlayer.getPlayStatus() + ", isfinish: " + this.isFinishing());		
		mPlayer.exit(cls);
		finish();
	}
	
	private void gotoSettings(boolean isRestart){		
		Intent intent = new Intent();
		intent.putExtra(SetActivity.PLAY_RESTART_FLAG, isRestart);		
		intent.setClass(PlayActivity.this, SetActivity.class);
		startActivity(intent);
		//startActivityForResult(intent, 100);
		
		if(isRestart){
			finish();
		}
	}
	
	private void tips(int msgId){
		Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
	}
    

	@Override
	public boolean getNextFile()
	{		
		return true;
	}
	
	@Override
	public String current(){
		return URL;
	}	

	@Override
	public String parameters(){
		/*type#loop#extended*/
		return String.format(Locale.ENGLISH, "%1$d#%2$d#%3$s", mPlayFlag, SetActivity.instance().getLoop(), DATA);
	}
	
	@Override
	public HnPlayer getPlayer(){
		return mPlayer;
	}

	@Override
	public void onUserEvent(int what, Message msg) {
		// TODO Auto-generated method stub
		if(what == 1001){
			item_desc.setText(URL);
		}
		else if(what == 1002){
		}
	}

	@Override
	public void onPlayError(int code) {
		// TODO Auto-generated method stub
		Log.e(TAG, "info::MSG_OPEN_ERROR:" + code);
		
		tips(R.string.list_media_no);
		
		gotoSettings(true);
	}

	@Override
	public void onPlaying() {
		// TODO Auto-generated method stub
		Log.v(TAG, "FFmpeg load file finish.");
		//start...
		btn_photograph.setEnabled(true);
	}

	@Override
	public void onPlayStop() {
		// TODO Auto-generated method stub
		Log.v(TAG, "info::PLAY_EVENT_STOPPED");
	}

	@Override
	public void onPlayExit() {
		// TODO Auto-generated method stub
		Log.e(TAG, "info::PLAY_EVENT_EXIT");
		finish();
	}

	@Override
	public void onPhotograph(boolean success) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Photograph FINISH.");
		
		tips(success ? R.string.camera_photograph_ok : R.string.camera_photograph_fail);
		
		btn_photograph.setEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		switch(id)
		{
		case R.id.ic_settings:
			gotoSettings(false);			
			break;
			
		case R.id.ic_exit:	
		default:
			exit(true);
			break;
		}		
		return super.onOptionsItemSelected(item);
	}
}
