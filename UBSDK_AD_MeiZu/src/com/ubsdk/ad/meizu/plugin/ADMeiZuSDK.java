package com.ubsdk.ad.meizu.plugin;

import java.lang.reflect.Method;

import com.shenqi.video.AdBanner;
import com.shenqi.video.AdBannerListener;
import com.shenqi.video.FullScreenAd;
import com.shenqi.video.FullScreenAdListener;
import com.shenqi.video.InterstitialAd;
import com.shenqi.video.InterstitialAdListener;
import com.shenqi.video.ShenQiVideo;
import com.shenqi.video.VideoAdListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.plugintype.ad.BannerPosition;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADMeiZuSDK implements IUBADPlugin{
	private final String TAG=ADMeiZuSDK.class.getSimpleName();
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
	private FullScreenAdListener mSplashScreenADListener;
	
	private String mRewardVideoID;//魅族RewardVideo广告id
	private boolean isCanReward=false;//激励视频是否可以给用户发奖励
	
	
	public ADMeiZuSDK (Activity activity){
		mActivity=activity;
		mWM = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		mContainer = (ViewGroup) ((ViewGroup)mActivity.findViewById(android.R.id.content)).getChildAt(0);
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

				if (mBannerAD!=null) {
					mBannerAD.destory();
//					避免内存泄漏
					mWM.removeViewImmediate(mBannerADContainer);
					mBannerADContainer=null;
				}
				
				mContainer=null;
				mSplashADContainer=null;
				super.onDestroy();
			}
		});
	}

	/**
	 * 初始化广告
	 */
	private void initAD() {
		UBLogUtil.logI(TAG+"----->initAD");
		mSplashADContainer = new FrameLayout(mActivity);
//		mSplashADContainer.setBackgroundColor(0x00ff0000);
		mSplashADContainer.setVisibility(View.GONE);//默认不显示
		LayoutParams splashLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		mContainer.addView(mSplashADContainer,splashLayoutParams);//添加到第一个，确保显示的时候可见
		
		mBannerADContainer = new FrameLayout(mActivity);
		FrameLayout.LayoutParams bannerLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerADContainer.setLayoutParams(bannerLayoutParams);
		
//		Banner广告
		mBannerADListener = new AdBannerListener() {
			
			@Override
			public void onAdShow(Object adViews) {
				UBLogUtil.logI(TAG+"----->onAdShow");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_BANNER, "Banner AD show success!");
				}
			}
			
			@Override
			public void onAdError(String msg) {
				UBLogUtil.logI(TAG+"----->onAdError----->msg"+msg);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_BANNER, msg);
				}
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->onAdClick");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_BANNER, "Banner AD click!");
				}
			}
		};
		
		mInterstitialAdListener = new InterstitialAdListener() {
					
			@Override
			public void onInterstitialAdShow() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdShow");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD show succcess!");
				}
			}
			
			@Override
			public void onInterstitialAdReady() {
				UBLogUtil.logI(TAG+"----->onInterstitialADReady");
				if (mInterstitialAD.isInterstitialAdReady()) {
					mInterstitialAD.showInterstitialAd(mActivity);
				}
			}
			
			@Override
			public void onInterstitialAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onInterstitialADFailed----->msg:"+msg);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL, msg);
				}
			}
			
			@Override
			public void onInterstitialAdClose() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdClose");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD closed!");
				}
			}
			
			@Override
			public void onInterstitialAdClick() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdClick");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD click!");
				}
			}
		};

//		Splash广告
		mSplashScreenADListener = new FullScreenAdListener() {
			
			@Override
			public void onFullScreenAdShow() {
				UBLogUtil.logI(TAG+"----->onSplashADShow");
					if (mUBADCallback!=null) {
						mUBADCallback.onShow(ADType.AD_TYPE_SPLASH,"Splash AD show success!");
					}
				}
			
			@Override
			public void onFullScreenAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onSplashADFailed----->msg:"+msg);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_SPLASH, msg);
				}
			}
			
			@Override
			public void onFullScreenAdDismiss() {
				UBLogUtil.logI(TAG+"----->onSplashADDismiss");
				mSplashADContainer.setVisibility(View.GONE);
				if (mUBADCallback!=null) {//同时给出2个回调
					mUBADCallback.onComplete(ADType.AD_TYPE_SPLASH, "Splash AD complete!");
					mUBADCallback.onClosed(ADType.AD_TYPE_SPLASH, "Splash AD dismiss!");
				}
			}
		};
		
//		RewardVideo广告监听
		mVideoAdListener = new VideoAdListener() {
			
			@Override
			public void onVideoAdReady() {
				UBLogUtil.logI(TAG+"----->onVideoAdReady");
				if (ShenQiVideo.getInstance().isVideoReady()) {
					ShenQiVideo.getInstance().playVideoAd();
				}
			}
			
			@Override
			public void onVideoAdPlayProgress(int current, int max) {
				UBLogUtil.logI(TAG+"----->onVideoAdPlayProgress----->current:"+current+",max:"+max);
			}
			
			@Override
			public void onVideoAdPlayFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onVideoAdPlayFailed----->msg:"+msg);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO, msg);
				}
			}
			
			@Override
			public void onVideoAdPlayComplete() {
//				魅族视频广告倒计时结束的时候会回调该方法
				UBLogUtil.logI(TAG+"----->onVideoAdPlayComplete");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_REWARDVIDEO, "rewardVideo Countdown completed");
				}
				isCanReward=true;
			}
			
			@Override
			public void onVideoAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onVideoAdFailed----->msg:"+msg);
					if (mUBADCallback!=null) {
						mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO,msg);
					}
				}
			
			@Override
			public void onVideoAdClose() {
//			魅族激励视频播放时页面上没有关闭按钮，同时屏蔽了物理返回键，激励视频倒计时播放完毕后页面上会出现一个关闭按钮，
//			单机关闭按钮才能关闭视频广告，回调此方法，所以在该回调了给用户发奖励，给出onComplete回调
				UBLogUtil.logI(TAG+"----->onVideoAdClose");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD close!");
					if (isCanReward) {
						mUBADCallback.onComplete(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD complete!");
					}
				}
			}
		};
	}

	/**
	 * 加载广告参数
	 */
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_Banner_ID");
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
	public void showADWithADType(final int adType) {
		UBLogUtil.logI(TAG+"----->showADWithADType");
		UBSDK.getInstance().runOnUIThread(new Runnable() {
			@Override
			public void run() {
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
		});
	}

	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
//		显示广告之前先设置容器可见
		mSplashADContainer.setVisibility(View.VISIBLE);
		new FullScreenAd(mActivity, mSplashADContainer, mSplashID, mSplashScreenADListener);
	}

//	private boolean isVideoInit=false;//视频是否初始化
	private VideoAdListener mVideoAdListener;
	private ShenQiVideo mShenQiVideo;
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		if (mShenQiVideo==null) {
			mShenQiVideo = ShenQiVideo.getInstance().init(mActivity, mRewardVideoID, mVideoAdListener);
			mShenQiVideo.fetchedVideoAd();
		}else{
			mShenQiVideo.fetchedVideoAd();
		}
	}
	
//	private boolean isInterstitialInit=false;//插屏广告是否初始化，默认为false
	private InterstitialAdListener mInterstitialAdListener;

	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");	
		if (mInterstitialAD==null) {
			mInterstitialAD = new InterstitialAd(mActivity, mInterstitialID);
			mInterstitialAD.setInterstitialAdListener(mInterstitialAdListener);
			mInterstitialAD.loadInterstitialAd();
		}else{
			mInterstitialAD.loadInterstitialAd();
		}
	}
	

	private AdBanner mBannerAD;
	private UBADCallback mUBADCallback;

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		
/*		if (mBannerADContainer.isAttachedToWindow()) {//新api，低版本机型会报错
			mWM.removeView(mBannerADContainer);
		}*/
		
		if (mBannerAD==null) {
			mBannerAD = new AdBanner(mActivity, mBannerID);
			mBannerAD.setAdBannerListener(mBannerADListener);
			mBannerADContainer.removeAllViews();
			mBannerADContainer.addView(mBannerAD);
//			把mBannerADContainer添加到WindowManager上，只添加一次
			ADHelper.addBannerView(mWM, mBannerADContainer,mBannerPosition);
		}
//		mBannerADContainer.addView(mBannerAD,mBannerLayoutParams);
		mBannerADContainer.setVisibility(View.VISIBLE);
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
		mBannerADContainer.setVisibility(View.GONE);
	}
	
	private void hideInterstitialAD() {
		UBLogUtil.logI(TAG+"----->hideInterstitialAD");
	}
	
	private void hideSplashAD() {
		UBLogUtil.logI(TAG+"----->hideSplashAD");
		mSplashADContainer.setVisibility(View.GONE);
	}
	
	private void hideRewardVideoAD() {
		UBLogUtil.logI(TAG+"----->hideRewardVideoAD");
		isCanReward=false;
	}
}
