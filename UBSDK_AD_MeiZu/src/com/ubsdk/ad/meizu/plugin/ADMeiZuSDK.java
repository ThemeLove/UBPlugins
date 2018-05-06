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
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class ADMeiZuSDK implements IUBADPlugin{
	private final String TAG=ADMeiZuSDK.class.getSimpleName();
	private Activity mActivity;
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_FULLSCREEN,ADType.AD_TYPE_REWARDEDVIDEO,ADType.AD_TYPE_SPLASH};
	
	/**
	 * 游戏主Activity的根视图，不是DecorView
	 */
	private ViewGroup mContainer;
	
	private String mBannerID;//魅族banner广告id
	private AdBannerListener mADBannerListener;
	
	private String mRewardVideoID;//魅族RewardVideo广告id
	
	private String mSplashID;//魅族Splash广告id
	private FrameLayout mSplashADContainner;//魅族Splash广告的容器
	private FullScreenAdListener mSplashScreenAdListener;
	
	private String mFullScreenID;//魅族FullScreen广告id
	private InterstitialAd mInterstitialAD;
	
	public ADMeiZuSDK (Activity activity){
		mActivity=activity;
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
				mContainer=null;
				mSplashADContainner=null;
				super.onDestroy();
			}
		});
	}

	/**
	 * 初始化广告
	 */
	private void initAD() {
		UBLogUtil.logI(TAG+"----->initAD");
		mSplashADContainner = new FrameLayout(mActivity);
		mSplashADContainner.setVisibility(View.GONE);//默认不显示
		LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mContainer.addView(mSplashADContainner,layoutParams);//添加到第一个，确保显示的时候可见
//		Splash广告
		mSplashScreenAdListener = new FullScreenAdListener() {
			
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
				mSplashADContainner.setVisibility(View.GONE);
				UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_SPLASH, "Splash AD dismiss!");
			}
		};
		
//		Banner广告
		mADBannerListener = new AdBannerListener() {
			
			@Override
			public void onAdShow(Object adViews) {
				UBLogUtil.logI(TAG+"----->onAdShow");
				UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_BANNER, "Banner AD showSuccess!");
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
		
//		Video广告
		ShenQiVideo.getInstance().init(mActivity, mRewardVideoID, new VideoAdListener() {
			
			@Override
			public void onVideoAdReady() {
				UBLogUtil.logI(TAG+"----->onVideoAdReady");
//				if (!isVideoInit) {
//					if (ShenQiVideo.getInstance().isVideoReady()) {
//						ShenQiVideo.getInstance().playVideoAd();
//					}
//				}
				
//				UBAD.getInstance().getUBADCallback().on
			}
			
			@Override
			public void onVideoAdPlayProgress(int current, int max) {
				UBLogUtil.logI(TAG+"----->onVideoAdPlayProgress----->current:"+current+",max:"+max);
			}
			
			@Override
			public void onVideoAdPlayFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onVideoAdPlayFailed----->msg:"+msg);
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_REWARDEDVIDEO, msg);
			}
			
			@Override
			public void onVideoAdPlayComplete() {
				UBLogUtil.logI(TAG+"----->onVideoAdPlayComplete");
				UBAD.getInstance().getUBADCallback().onComplete(ADType.AD_TYPE_REWARDEDVIDEO, "RewardVideo AD complete!");
			}
			
			@Override
			public void onVideoAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onVideoAdFailed----->msg:"+msg);
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_REWARDEDVIDEO,msg);
			}
			
			@Override
			public void onVideoAdClose() {
				UBLogUtil.logI(TAG+"----->onVideoAdClose");
				UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_REWARDEDVIDEO, "RewardVideo AD close!");
			}
		});
		ShenQiVideo.getInstance().fetchedVideoAd();
		
//		插屏广告
		mInterstitialAD = new InterstitialAd(mActivity, mFullScreenID);
		mInterstitialAD.setInterstitialAdListener(new InterstitialAdListener() {
			
			@Override
			public void onInterstitialAdShow() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdShow");
				UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_FULLSCREEN, "FullScreen AD show succcess!");
			}
			
			@Override
			public void onInterstitialAdReady() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdReady");
			}
			
			@Override
			public void onInterstitialAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onInterstitialAdFailed----->msg:"+msg);
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_FULLSCREEN, msg);
			}
			
			@Override
			public void onInterstitialAdClose() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdClose");
				UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_FULLSCREEN, "FullScreen AD closed!");
				
			}
			
			@Override
			public void onInterstitialAdClick() {
				UBLogUtil.logI(TAG+"----->onInterstitialAdClick");
				UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_FULLSCREEN,"FullScreen AD click!");
				
			}
		});
		mInterstitialAD.loadInterstitialAd();
	}

	/**
	 * 加载广告参数
	 */
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_BannerID");
		mFullScreenID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_FullScreenID");
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_RewardVideoID");
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_SplashID");
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
		case ADType.AD_TYPE_FULLSCREEN:
			showFullScreenAD();
			break;
		case ADType.AD_TYPE_REWARDEDVIDEO:
			showVideoAD();
			break;
		case ADType.AD_TYPE_SPLASH:
			showSplashAD();
			break;
		default:
			break;
		}
	}
	

	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
//		显示广告之前先设置容器可见
		mSplashADContainner.setVisibility(View.VISIBLE);
		new FullScreenAd(mActivity, mSplashADContainner, mSplashID, mSplashScreenAdListener);
	}

	private boolean isVideoInit=true;
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		isVideoInit=false;
		if (ShenQiVideo.getInstance().isVideoReady()) {
			ShenQiVideo.getInstance().playVideoAd();
		}else{
			ShenQiVideo.getInstance().fetchedVideoAd();
		}
	}

	private void showFullScreenAD() {
		UBLogUtil.logI(TAG+"----->showFullScreenAD");	
		if (mInterstitialAD==null) {
			UBLogUtil.logI(TAG+"-------null");
			return;
		}
		if (mInterstitialAD.isInterstitialAdReady()) {
			mInterstitialAD.showInterstitialAd(mActivity);
		}else{
			mInterstitialAD.loadInterstitialAd();
		}
	}

	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		AdBanner adBanner = new AdBanner(mActivity, mBannerID);
		adBanner.setAdBannerListener(mADBannerListener);
		mSplashADContainner.setVisibility(View.VISIBLE);
		mSplashADContainner.removeAllViews();
		mSplashADContainner.addView(adBanner);
	}
 
	@Override
	public void hideADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->hideADWithADType");
	}
	
}
