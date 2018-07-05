package com.ubsdk.ad.taptap.plugin;

import java.lang.reflect.Method;
import java.util.Map;

import com.shenqi.video.AdBannerListener;
import com.shenqi.video.InterstitialAd;
import com.shenqi.video.VideoAdListener;
import com.soulgame.sgsdk.tgsdklib.TGSDK;
import com.soulgame.sgsdk.tgsdklib.TGSDKServiceResultCallBack;
import com.soulgame.sgsdk.tgsdklib.ad.ITGADListener;
import com.soulgame.sgsdk.tgsdklib.ad.ITGPreloadListener;
import com.soulgame.sgsdk.tgsdklib.ad.ITGRewardVideoADListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.plugintype.ad.BannerPosition;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADTapTapSDK implements IUBADPlugin{
	private final String TAG=ADTapTapSDK.class.getSimpleName();
	private Activity mActivity;
	private WindowManager mWM;
	
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL,ADType.AD_TYPE_SPLASH,ADType.AD_TYPE_REWARDVIDEO};
	
	/**
	 * 游戏主Activity的根视图，不是DecorView
	 */
	private ViewGroup mContainer;
	
	private String mBannerID;//魅族banner广告id
	private AdBannerListener mBannerADListener;
	private FrameLayout mBannerADContainer;
	private int mBannerPosition=BannerPosition.TOP;
	
	private String mInterstitialID;//魅族Interstitial广告id
	private InterstitialAd mInterstitialAD;
	
	private String mSplashID;//魅族Splash广告id
	private FrameLayout mSplashADContainer;//魅族Splash广告的容器
	
	private String mRewardVideoID;//魅族RewardVideo广告id
	
	
	public ADTapTapSDK (Activity activity){
		mActivity=activity;
		mWM = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		try {
			setActivityListener();
			loadADParams();
			initAD();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			UBLogUtil.logI(TAG+"----->MeiZu AD init success!");
		}
	}

	private void setActivityListener() {
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){
			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestroy");
				super.onDestroy();
			}

			@Override
			public void onStart() {
				TGSDK.onStart(mActivity);
				super.onStart();
			}

			@Override
			public void onPause() {
				TGSDK.onPause(mActivity);
				super.onPause();
			}

			@Override
			public void onResume() {
				TGSDK.onResume(mActivity);
				super.onResume();
			}

			@Override
			public void onStop() {
				TGSDK.onStop(mActivity);
				super.onStop();
			}

			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				TGSDK.onActivityResult(mActivity, requestCode, resultCode, data);
				super.onActivityResult(requestCode, resultCode, data);
			}

			@Override
			public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
				TGSDK.onRequestPermissionsResult(mActivity, requestCode, permissions, grantResults);
				super.onRequestPermissionResult(requestCode, permissions, grantResults);
			}
			
		});
	}

	/**
	 * 初始化广告
	 */
	private void initAD() {
		UBLogUtil.logI(TAG+"----->initAD");
		
//		设置激励视频广告监听
		TGSDK.setRewardVideoADListener(new ITGRewardVideoADListener() {

		    public void onADAwardSuccess(String result) {
		        // 奖励广告条件达成，可以向用户发放奖励
		    	UBLogUtil.logI(TAG+"----->RewardVideoListener----->onADAwardSuccess");
		    	if (mUBADCallback!=null) {
					mUBADCallback.onComplete(ADType.AD_TYPE_REWARDVIDEO,result);
				}
		    }

		    public void onADAwardFailed(String result, String error) {
		        // 奖励广告条件未达成，无法向用户发放奖励
		    	UBLogUtil.logI(TAG+"----->RewardVideoListener----->onADAwardFailed");
		    	if (mUBADCallback!=null) {
		    		mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO, error);
				}
		    }

		});
		
		TGSDK.setADListener(new ITGADListener() {

		    public void onShowSuccess(String result) {
		        // 广告开始播放
		    	UBLogUtil.logI(TAG+"----->RewardVideoListener----->onShowSuccess");
		    	if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_REWARDVIDEO,"start show");
				}
		    }

		    public void onShowFailed(String result, String error) {
		        // 广告播放失败
		    	UBLogUtil.logI(TAG+"----->RewardVideoListener----->onShowFailed");
		    	if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO,"on show Failed");
				}
		    }

		    public void onADComplete(String result) {
		        // 视频广告的视频部分播放结束
		    	UBLogUtil.logI(TAG+"----->RewardVideoListener----->onAdComplete");
		    }

		    public void onADClick(String result) {
		        // 用户点击了广告，正在跳转到其他页面
		    	UBLogUtil.logI(TAG+"----->RewardVideoListener----->onADClick");
		    	if (mUBADCallback!=null) {
		    		mUBADCallback.onClick(ADType.AD_TYPE_REWARDVIDEO,result);
				}
		    }

		    public void onADClose(String result) {
		        // 广告关闭
		    	UBLogUtil.logI(TAG+"------>RewardVideoListener----->onADClose");
		    	if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_REWARDVIDEO,result);
				}
		    }
		});

//		设置debug模式，在初始化之前执行
		TGSDK.setDebugModel(true);
		TGSDK.initialize(mActivity, mTGSDKAppID,mTGSDKChannelID,new TGSDKServiceResultCallBack() {
			
			@Override
			public void onSuccess(Object arg0, Map<String, String> map) {
//				初始化成功后预加载广告
				TGSDK.preloadAd(mActivity,new ITGPreloadListener() {

				    @Override
				    public void onPreloadSuccess(String result) {
				    	UBLogUtil.logI(TAG+"----->onPreloadSuccess");
				    }

				    @Override
				    public void onPreloadFailed(String scene, String error) {
				    	UBLogUtil.logI(TAG+"----->onPreloadFailed");
				    }

				    @Override
				    public void onCPADLoaded(String result) {
				    	UBLogUtil.logI(TAG+"----->onCPADLoaded");
				    }

				    @Override
				    public void onVideoADLoaded(String result) {
				    	UBLogUtil.logI(TAG+"----->onVideoADLoaded");
				    }
				});
			}
			
			@Override
			public void onFailure(Object arg0, String obj) {
//				TODO
			}
		});

	}

	/**
	 * 加载广告参数
	 */
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mTGSDKAppID = UBSDKConfig.getInstance().getParamMap().get("AD_TGSDK_APP_ID");
		mTGSDKChannelID = UBSDKConfig.getInstance().getParamMap().get("AD_TGSDK_Channel_ID");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_TapTap_Banner_ID");
		mBannerPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_Banner_Position"));
		
		mInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_Interstitial_ID");
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_RewardVideo_ID");
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_Splash_ID");
	}

	@Override
	public boolean isSupportMethod(String methodName,Object[] args) {
        UBLogUtil.logI(TAG+"----->isSupportMethod");
        Class<?> [] parameterTypes=null;
        if (args!=null&&args.length>0) {
        	parameterTypes=new Class<?>[args.length];
			for(int i=0;i<args.length;i++){
				parameterTypes[i]=args[i].getClass();
			}
		}
        
        try {
			Method method = getClass().getDeclaredMethod(methodName, parameterTypes);
			return method==null?false:true;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Object callMethod(String methodName, Object[] args) {
		UBLogUtil.logI(TAG+"----->callMethod");
		Class<?>[] paramterTypes=null;
		if (args!=null&&args.length>0) {
			paramterTypes=new Class<?>[args.length];
			for (int i=0;i<args.length;i++) {
				paramterTypes[i]=args[i].getClass();
			}
		}
		
		try {
			Method method = getClass().getDeclaredMethod(methodName, paramterTypes);
			return method.invoke(this, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isSupportADType(int adType) {
		UBLogUtil.logI(TAG+"----->isSupportADType");
		if (supportedADTypeArray!=null&&supportedADTypeArray.length>0) {
			for (int i : supportedADTypeArray) {
				if (i==adType) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void showADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->showADWithADType");
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		hideADWithADType(adType);//显示之前先隐藏广告
		switch (adType) {
		case ADType.AD_TYPE_BANNER:
			showBannerAD();
			break;
		case ADType.AD_TYPE_INTERSTITIAL:
			showInterstitialAD();
			break;
		case ADType.AD_TYPE_SPLASH:
			showSplashAD();
			break;
		case ADType.AD_TYPE_REWARDVIDEO:
			showVideoAD();
			break;
		default:
			break;
		}
	}

	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		if (TGSDK.couldShowAd(mSplashID)) {
//			TGSDK.showAd(mActivity, mSplashID);
			TGSDK.showTestView(mActivity, mSplashID);
		}
	}

	private VideoAdListener mVideoAdListener;
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		if (TGSDK.couldShowAd(mRewardVideoID)) {
//			TGSDK.showAd(mActivity, mRewardVideoID);
			TGSDK.showTestView(mActivity, mRewardVideoID);
		}
	}

	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");	
		if (TGSDK.couldShowAd(mInterstitialID)) {
//			TGSDK.showAd(mActivity, mInterstitialID);
			TGSDK.showTestView(mActivity, mInterstitialID);
		}
	}

	private UBADCallback mUBADCallback;
	private String mTGSDKChannelID;//聚合广告渠道ids,这里我们上在TapTap渠道上，对应的渠道号为10053
	private String mTGSDKAppID;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		
		if (TGSDK.couldShowAd(mBannerID)) {
//			TGSDK.showAd(mActivity, mBannerID);
			TGSDK.showTestView(mActivity, mBannerID);
		}
	}
 
	@Override
	public void hideADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->hideADWithADType");
		switch(adType){
		case ADType.AD_TYPE_BANNER:
			hideBannerAD();
			break;
		case ADType.AD_TYPE_INTERSTITIAL:
			hideInterstitialAD();
			break;
		case ADType.AD_TYPE_SPLASH:
			hideSplashAD();
			break;
		case ADType.AD_TYPE_REWARDVIDEO:
			hideRewardVideoAD();
			break;
		}
	}
	private void hideBannerAD() {
		UBLogUtil.logI(TAG+"----->hideBannerAD");
	}
	
	private void hideInterstitialAD() {
		UBLogUtil.logI(TAG+"----->hideInterstitialAD");
	}
	
	private void hideSplashAD() {
		UBLogUtil.logI(TAG+"----->hideSplashAD");
	}
	
	private void hideRewardVideoAD() {
		UBLogUtil.logI(TAG+"----->hideRewardVideoAD");
	}
}
