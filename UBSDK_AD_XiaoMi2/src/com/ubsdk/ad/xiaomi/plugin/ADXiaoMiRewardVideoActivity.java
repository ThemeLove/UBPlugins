package com.ubsdk.ad.xiaomi.plugin;

import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IVideoAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoVideoListener;
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
import android.widget.RelativeLayout.LayoutParams;

public class ADXiaoMiRewardVideoActivity extends Activity{
	private final String TAG=ADXiaoMiRewardVideoActivity.class.getSimpleName();
	
	private FrameLayout mVideoADContainer;
	private UBADCallback mUBADCallback;
	private MimoVideoListener mVideoADListener;
	private IVideoAdWorker mVideoAD;
	private String mRewardVideoID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    RelativeLayout rootView = new RelativeLayout(this);
	    rootView.setBackgroundColor(0xff000000);
	    
	    mVideoADContainer = new FrameLayout(this);
	    FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    mVideoADContainer.setLayoutParams(frameLayoutParams);
	    
	    LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
	    rootView.addView(mVideoADContainer,relativeLayoutParams);
	    
	    
		setContentView(rootView);
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_RewardVideo_ID");
		initADandListener();
	}
	
	private void initADandListener() {
		mVideoADListener = new MimoVideoListener() {
			
			@Override
			public void onAdPresent() {
				UBLogUtil.logI(TAG+"----->on RewardVideo onAdPresent");
			}
			
			@Override
			public void onAdLoaded() {
				UBLogUtil.logI(TAG+"----->on RewardVideo onAdLoaded");
				try {
					if (mVideoAD.isReady()) {
						mVideoADContainer.removeAllViews();
						mVideoAD.show(mVideoADContainer);
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								try {
									mVideoAD.play();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->on RewardVideo onAdFailed:msg="+msg);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO, msg);
				}
				finish();
			}
			
			@Override
			public void onAdDismissed() {
				UBLogUtil.logI(TAG+"----->on RewardVideo onAdDismissed");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD closed!");
				}
				finish();
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->on RewardVideo onAdClick");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD click!");
				}
			}
			
			@Override
			public void onVideoStart() {
				UBLogUtil.logI(TAG+"----->on RewardVideo onVideoStart");
				
			}
			
			@Override
			public void onVideoPause() {
				UBLogUtil.logI(TAG+"----->on RewardVideo onVideoPause");
			}
			
			@Override
			public void onVideoComplete() {
				UBLogUtil.logI(TAG+"----->on RewardVideo onVideoComplete");
				if (mUBADCallback!=null) {
					mUBADCallback.onComplete(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD complete");
				}
				finish();
			}
		};
		
		UBLogUtil.logI(TAG+"----->showVideoAD");
		try {
			if (mVideoAD==null) {
				mVideoAD = AdWorkerFactory.getVideoAdWorker(this, mRewardVideoID, AdType.AD_PLASTER_VIDEO);
				mVideoAD.setListener(mVideoADListener);
			}
			mVideoAD.recycle();
			mVideoAD.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		UBLogUtil.logI(TAG+"----->mVideoAD.getStatus()="+mVideoAD.getStatus());
		if (keyCode==KeyEvent.KEYCODE_BACK||keyCode==KeyEvent.KEYCODE_HOME) {
			if (mVideoAD.getStatus()!=IVideoAdWorker.FLOW_VIDEO_COMPLETE) {
//				return true;
			}
		}
		try {
			if (mVideoAD!=null) {
				mVideoAD.pause();
				mVideoAD.recycle();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onKeyDown(keyCode, event);
	}
}
