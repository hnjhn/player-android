package com.hnjhn.playerdemo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hnjhn.player.PlayUtil;
import com.hnjhn.player.ui.SwitchButton;
import com.hnjhn.util.CommUtils;

public class SetActivity extends Activity {
	private static final String TAG = "SetActivity";
	
	private final SharedPreferences sp;
	
	private EditText mEditText;
	
	private String  mURL;
	private boolean isRepeatFile;
	private boolean isExit;
	
	private static SetActivity sSetActivity=null;	
	
	public SetActivity(){		
		sp = PreferenceManager.getDefaultSharedPreferences(PlayerApp.getContext());
		
		mURL = sp.getString("URL", "");	
		isRepeatFile = sp.getBoolean("repeat", false);
	}
	
	public String getUrl(){
		return mURL;
	}	
	
	public boolean isRepeatFile(){
		return isRepeatFile;
	}
	
	public int getLoop(){
		return isRepeatFile ? 1 : 0;
	}
	
	public static final String PLAY_RESTART_FLAG = "ISRESTART";
	
	public static SetActivity instance(){
		if(sSetActivity==null)
			sSetActivity = new SetActivity();
		
		return sSetActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
		//setTitle(R.string.settings_cfg);
    	//Actvity Title
        ((TextView)findViewById(R.id.custom_title)).setText(R.string.settings_play);        
        
        //Close Activity
		findViewById(R.id.btn_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SetActivity.this.finish();
					}
				});
		//Confirm
		findViewById(R.id.btn_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String u = mEditText.getText().toString();
						
						if (u.length()!=0 && u.length()<5) {
							tips("Data setting error!");
							return;
						}						
						//tips("Setting URL success.");
						
						sp.edit().putString("URL", u).commit();
						mURL=u;
						setResult(Activity.RESULT_OK, null); 
						finish();
					}
				});
		
		//Restart Player
		isExit = getIntent().getBooleanExtra(PLAY_RESTART_FLAG, true);
		
		sSetActivity = this;
		
		mEditText = (EditText) findViewById(R.id.editText1);
		mEditText.setText(mURL);
		mEditText.setHint(R.string.rtsp_hint);
		mEditText.setSelection(mURL.length());
		
		SwitchButton sw = (SwitchButton) findViewById(R.id.btn_file_repeat);
		sw.setChecked(!isRepeatFile);
		sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				sp.edit().putBoolean("repeat", !isChecked).commit();
				isRepeatFile=!isChecked;
			}
		});

		Log.d(TAG, "URL::" + mURL);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			CommUtils.setTranslucentStatus(this, Color.parseColor("#FF0BB68B"));
		}
		
		((TextView)findViewById(R.id.app_copyright)).setText(PlayUtil.getCopyrightInfo());
	}
	
	private void tips(String msgId){
		Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();		
		if(isExit)
		{
			Log.e(TAG, "###### Please reboot the player. ######");
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
}
