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
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.plugintype.ad.BannerPosition;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
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
	
	
	public ADMeiZuSDK (Activity activity){
		mActivity=activity;
		mWM = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
		mContainer = (ViewGroup) ((ViewGroup)mActivity.findViewById(android.R.id.content)).getChildAt(0);
		try {
			setActivityListener();
			loadADParams();
			initAD();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{//最终给出初始化成功的回调
			UBAD.getInstance().getUBADCallback().onInit(true, "AD MeiZu init Success!");
		}
	}

	private void setActivityListener() {
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){
			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestroy");
				mWM.removeView(mBannerADContainer);
				mContainer=null;
				mSplashADContainer=null;
				mBannerADContainer=null;
				if (mBannerAD!=null) {
					mBannerAD.destory();
				}
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
		LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		mContainer.addView(mSplashADContainer,layoutParams);//添加到第一个，确保显示的时候可见
		
		mBannerADContainer = new FrameLayout(mActivity);
		FrameLayout.LayoutParams bannerLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerADContainer.setLayoutParams(bannerLayoutParams);
		
//		Banner广告
		mBannerADListener = new AdBannerListener() {
			
			@Override
			public void onAdShow(Object adViews) {
				UBLogUtil.logI(TAG+"----->onAdShow");
				UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_BANNER, "Banner AD show success!");
			}
			
			@Override
			public void onAdError(String msg) {
				UBLogUtil.logI(TAG+"----->onAdError----->msg"+msg);
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_BANNER, msg);
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->onAdClick");
				UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_BANNER, "Banner AD click!");
			}
		};
		
//		插屏广告
		mInterstitialAD = new InterstitialAd(mActivity, mInterstitialID);
		mInterstitialAD.setInterstitialAdListener(new InterstitialAdListener() {
			
			@Override
			public void onInterstitialAdShow() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdShow");
				UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD show succcess!");
			}
			
			@Override
			public void onInterstitialAdReady() {
				UBLogUtil.logI(TAG+"----->onInterstitialADReady");
				if (!isInterstitialInit&&mInterstitialAD.isInterstitialAdReady()) {
					mInterstitialAD.showInterstitialAd(mActivity);
				}
			}
			
			@Override
			public void onInterstitialAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onInterstitialADFailed----->msg:"+msg);
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_INTERSTITIAL, msg);
			}
			
			@Override
			public void onInterstitialAdClose() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdClose");
				UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD closed!");
			}
			
			@Override
			public void onInterstitialAdClick() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdClick");
				UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD click!");
			}
		});

		
//		Splash广告
		mSplashScreenADListener = new FullScreenAdListener() {
			
			@Override
			public void onFullScreenAdShow() {
				UBLogUtil.logI(TAG+"----->onSplashADShow");
				UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_SPLASH,"Splash AD show success!");
			}
			
			@Override
			public void onFullScreenAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onSplashADFailed----->msg:"+msg);
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_SPLASH, msg);
			}
			
			@Override
			public void onFullScreenAdDismiss() {
				UBLogUtil.logI(TAG+"----->onSplashADDismiss");
				mSplashADContainer.setVisibility(View.GONE);
				UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_SPLASH, "Splash AD dismiss!");
			}
		};
		

		
//		Video广告
		ShenQiVideo.getInstance().init(mActivity, mRewardVideoID, new VideoAdListener() {
			
			@Override
			public void onVideoAdReady() {
				UBLogUtil.logI(TAG+"----->onVideoAdReady");
				if (!isVideoInit&&ShenQiVideo.getInstance().isVideoReady()) {
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
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_REWARDVIDEO, msg);
			}
			
			@Override
			public void onVideoAdPlayComplete() {
				UBLogUtil.logI(TAG+"----->onVideoAdPlayComplete");
				UBAD.getInstance().getUBADCallback().onComplete(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD complete!");
			}
			
			@Override
			public void onVideoAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onVideoAdFailed----->msg:"+msg);
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_REWARDVIDEO,msg);
			}
			
			@Override
			public void onVideoAdClose() {
				UBLogUtil.logI(TAG+"----->onVideoAdClose");
				UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD close!");
			}
		});
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
	public void showADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->showADWithADType");
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
//		显示广告之前先设置容器可见
		mSplashADContainer.setVisibility(View.VISIBLE);
		new FullScreenAd(mActivity, mSplashADContainer, mSplashID, mSplashScreenADListener);
	}

	private boolean isVideoInit=true;
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		isVideoInit=false;
		ShenQiVideo.getInstance().fetchedVideoAd();
	}

	private boolean isInterstitialInit=true;
	private AdBanner mBannerAD;
	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");	
		if (mInterstitialAD==null) {
			UBLogUtil.logI(TAG+"-------null");
			return;
		}
		isInterstitialInit=false;
		mInterstitialAD.loadInterstitialAd();
	}

	@SuppressLint("NewApi")
	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		mBannerADContainer.setVisibility(View.VISIBLE);
		
		if (mBannerADContainer.isAttachedToWindow()) {
			mWM.removeView(mBannerADContainer);
		}
		
		if (mBannerAD==null) {
			mBannerAD = new AdBanner(mActivity, mBannerID);
		}
		mBannerAD.setAdBannerListener(mBannerADListener);
		mBannerADContainer.removeAllViews();
		mBannerADContainer.addView(mBannerAD);
//		mBannerADContainer.addView(mBannerAD,mBannerLayoutParams);
		
		ADHelper.addBannerView(mWM, mBannerADContainer,mBannerPosition);
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
	}
}
