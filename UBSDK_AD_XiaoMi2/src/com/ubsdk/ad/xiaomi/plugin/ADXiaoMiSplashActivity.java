package com.ubsdk.ad.xiaomi.plugin;

import java.io.Serializable;

import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.xiaomi.ad.common.pojo.AdType;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;

public class ADXiaoMiSplashActivity extends Activity{
	private final String TAG=ADXiaoMiSplashActivity.class.getSimpleName();
	private UBADCallback mUBADCallback;
	private String mSplashID;
	private FrameLayout mSplashADContainer;
	private IAdWorker mSplashAD;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    RelativeLayout rootView = new RelativeLayout(this);
	    rootView.setBackgroundColor(0xffffffff);
	    
	    mSplashADContainer = new FrameLayout(this);
	    FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
	    mSplashADContainer.setLayoutParams(frameLayoutParams);
	    
	    RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
	    rootView.addView(mSplashADContainer,relativeLayoutParams);
		setContentView(rootView);
		
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_Splash_ID");
		if (savedInstanceState!=null) {
			mSplashAD = (IAdWorker) savedInstanceState.getSerializable("splashAD");
			UBLogUtil.logI(TAG+"----->onCreate");
		}
		
		initADandListener();
	}

	private void initADandListener() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		MimoAdListener mSplashADListener=new MimoAdListener(){

			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->Splash AD click!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_SPLASH,"Splash AD click!");
				}
			}

			@Override
			public void onAdDismissed() {
				UBLogUtil.logI(TAG+"----->Splash AD closed!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_SPLASH,"Splash AD closed!");
				}
				finish();
			}

			@Override
			public void onAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->Splash AD show failed!----->msg="+msg);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_SPLASH, msg);
				}
				
				finish();
			}

			@Override
			public void onAdLoaded() {
				UBLogUtil.logI(TAG+"----->Splash AD onLoaded");
			}

			@Override
			public void onAdPresent() {
				UBLogUtil.logI(TAG+"----->Splash AD show success!");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_SPLASH, "Splash AD show success!");
				}
			}};
			
		try {
			if (mSplashAD==null) {
				UBLogUtil.logI(TAG+"----->mSplashAD create");
				mSplashAD = AdWorkerFactory.getAdWorker(this, mSplashADContainer, mSplashADListener,AdType.AD_SPLASH);
			}
			mSplashAD.loadAndShow(mSplashID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState!=null) {
			mSplashAD = (IAdWorker) savedInstanceState.getSerializable("splashAD");
			UBLogUtil.logI(TAG+"----->onRestoreInstanceState");
		}
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		UBLogUtil.logI(TAG+"----->onSaveInstanceState");
		outState.putSerializable("splashAD", (Serializable) mSplashAD);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
