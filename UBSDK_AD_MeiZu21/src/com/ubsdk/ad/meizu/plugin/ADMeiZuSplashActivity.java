package com.ubsdk.ad.meizu.plugin;

import com.shenqi.video.FullScreenAd;
import com.shenqi.video.FullScreenAdListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class ADMeiZuSplashActivity extends Activity{
	private final String TAG=ADMeiZuSplashActivity.class.getSimpleName();
	private UBADCallback mUBADCallback;
	private FrameLayout mSplashADContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		mSplashADContainer = new FrameLayout(this);
		LayoutParams splashParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
		mSplashADContainer.setLayoutParams(splashParams);
		
		setContentView(mSplashADContainer);
		
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		String splashID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_Splash_ID");
		
		new FullScreenAd(this, mSplashADContainer, splashID, mSplashScreenADListener,2000L);
	}
	
//	Splash广告
	private FullScreenAdListener mSplashScreenADListener = new FullScreenAdListener() {
		
		@Override
		public void onFullScreenAdShow() {
			
			UBSDK.getInstance().runOnUIThread(new Runnable() {
				
				@Override
				public void run() {
					UBLogUtil.logI(TAG+"----->Splash AD show success!");
					if (mUBADCallback!=null) {
						mUBADCallback.onShow(ADType.AD_TYPE_SPLASH,"Splash AD show success!");
					}
				}
			});
			
		}
		
		@Override
		public void onFullScreenAdFailed(final String msg) {
			
			UBSDK.getInstance().runOnUIThread(new Runnable() {
				
				@Override
				public void run() {
					UBLogUtil.logI(TAG+"----->Splash AD Failed!----->msg="+msg);
					if (mUBADCallback!=null) {
						mUBADCallback.onFailed(ADType.AD_TYPE_SPLASH, msg);
					}
					
					finish();
				}
			});
			
		}
		
		@Override
		public void onFullScreenAdDismiss() {
			
			UBSDK.getInstance().runOnUIThread(new Runnable() {
				@Override
				public void run() {
					UBLogUtil.logI(TAG+"----->Splash AD Closed!");
					if (mUBADCallback!=null) {//同时给出2个回调
						mUBADCallback.onComplete(ADType.AD_TYPE_SPLASH, "Splash AD complete!");
						mUBADCallback.onClosed(ADType.AD_TYPE_SPLASH, "Splash AD dismiss!");
					}
					
					finish();
				}
			});
			
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		mUBADCallback=null;
		mSplashADContainer=null;
		super.onDestroy();
	}
}
