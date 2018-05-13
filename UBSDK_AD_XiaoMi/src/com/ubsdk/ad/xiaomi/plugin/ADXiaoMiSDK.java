package com.ubsdk.ad.xiaomi.plugin;

import java.lang.reflect.Method;
import java.util.List;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.SplashAdListener;
import com.xiaomi.ad.adView.BannerAd;
import com.xiaomi.ad.adView.BannerAd.BannerListener;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.adView.SplashAd;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

import android.R;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADXiaoMiSDK implements IUBADPlugin{
	private final String TAG=ADXiaoMiSDK.class.getSimpleName();
	private final int[] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_BANNER,ADType.AD_TYPE_REWARDVIDEO,ADType.AD_TYPE_SPLASH};
	
	
	private Activity mActivity;
	private WindowManager mWM;
	private ViewGroup mContainer;
	
	private String mBannerID;//bannerID
	private String mInterstitialID;//InterstitialID
	private String mSplashID;//SplashID
	private String mRewardVideoID;//RewardVideoID
	private FrameLayout mBannerADContainer;
	private BannerListener mBannerADListener;
	private BannerAd mBannerAD;
	private AdListener mInterstitialADListener;
	private InterstitialAd mInterstitialAD;
	private SplashAdListener mSplashADListener;
	private FrameLayout mSplashADContainer;
	private SplashAd mSplashAD;
	private StandardNewsFeedAd mRewardVideoAD;
	private NativeAdListener mRewardVideoListener;
	private FrameLayout mRewardVideoADContainer;
	private ADXiaoMiSDK(Activity activity){
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
			UBLogUtil.logI(TAG+"----->XiaoMi AD init Success!");
		}
	}
	
	private void setActivityListener(){
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onDestroy() {
				super.onDestroy();
			}
		});
	}

	/**
	 * 初始化广告
	 */
	private void initAD() {
		UBLogUtil.logI(TAG+"----->initAD");
		mBannerADContainer = new FrameLayout(mActivity);
		FrameLayout.LayoutParams bannerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mBannerADContainer.setLayoutParams(bannerLayoutParams);
		
		mSplashADContainer = new FrameLayout(mActivity);
		FrameLayout.LayoutParams splashLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		mSplashADContainer.setLayoutParams(splashLayoutParams);
		mContainer.addView(mSplashADContainer,splashLayoutParams);
		
		mRewardVideoADContainer = new FrameLayout(mActivity);
		FrameLayout.LayoutParams rewardVideoLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
		mRewardVideoADContainer.setLayoutParams(rewardVideoLayoutParams);
		mContainer.addView(mRewardVideoADContainer,rewardVideoLayoutParams);
		
		mBannerADListener = new BannerAd.BannerListener() {
			@Override
			public void onAdEvent(AdEvent adEvent) {
				if (adEvent.mType==AdEvent.TYPE_CLICK) {
					UBLogUtil.logI(TAG+"----->on Banner Click!");
					UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_BANNER, "Banner AD click!");
				}else if(adEvent.mType==AdEvent.TYPE_SKIP){
					UBLogUtil.logI(TAG+"----->on Banner Closed!");
					UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_BANNER,"Banner AD closed!");
				}else if (adEvent.mType==AdEvent.TYPE_VIEW) {
					UBLogUtil.logI(TAG+"----->on Banner Show!");
					UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_BANNER, "Banner AD show success!");
				}
			}
		};
		
		mInterstitialADListener = new AdListener() {
			
			@Override
			public void onViewCreated(View view) {
				UBLogUtil.logI(TAG+"----->on Interstitial onViewCreated");
				
			}
			
			@Override
			public void onAdLoaded() {
				UBLogUtil.logI(TAG+"----->on Interstitial onAdLoaded");
				UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD show success!");
				mInterstitialAD.show();
			}
			
			@Override
			public void onAdEvent(AdEvent adEvent) {
				UBLogUtil.logI(TAG+"----->on Interstitial onAdEvent");
				if (AdEvent.TYPE_SKIP==adEvent.mType) {
					UBLogUtil.logI(TAG+"----->on Interstitial close!");
					UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD close!");
					
				}else if(AdEvent.TYPE_CLICK==adEvent.mType){
					UBLogUtil.logI(TAG+"----->on Interstitial click!");
					UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD click!");
				}
			}
			
			@Override
			public void onAdError(AdError adError) {
				UBLogUtil.logI(TAG+"----->on Interstitial onAdError");
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD failed!");
			}
		};
		
		mSplashADListener = new SplashAdListener() {
			
			@Override
			public void onAdPresent() {
				UBLogUtil.logI(TAG+"----->Splash AD show success!");
				UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_SPLASH, "Splash AD show success!");
			}
			
			@Override
			public void onAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->Splash AD show failed!");
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_SPLASH, msg);
			}
			
			@Override
			public void onAdDismissed() {
				UBLogUtil.logI(TAG+"----->Splash AD show closed!");
				UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_SPLASH,"Splash AD closed!");
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->Splash AD click!");
				UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_SPLASH,"Splash AD click!");
			}
		};
		
		mRewardVideoListener = new NativeAdListener() {
			@Override
			public void onNativeInfoSuccess(List<NativeAdInfoIndex> list) {
				UBLogUtil.logI(TAG+"----->onNativeInfoSuccess!");
				if (list!=null&&list.size()>0) {
					NativeAdInfoIndex response = list.get(0);
					mRewardVideoAD.buildViewAsync(response, 1, new AdListener() {
						
						@Override
						public void onViewCreated(View view) {
							UBLogUtil.logI(TAG+"----->RewardVideo onViewCreated!");
							mRewardVideoADContainer.removeAllViews();
							mRewardVideoADContainer.addView(view);
						}
						
						@Override
						public void onAdLoaded() {
							UBLogUtil.logI(TAG+"----->RewardVideo onAdLoaded!");
						}
						
						@Override
						public void onAdEvent(AdEvent adEvent) {
							if (AdEvent.TYPE_SKIP==adEvent.mType) {
								UBLogUtil.logI(TAG+"----->RewardVideo AD closed!");
								UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD closed!");
							}else if(AdEvent.TYPE_CLICK==adEvent.mType){
								UBLogUtil.logI(TAG+"----->RewardVideo AD click!");
								UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD click!");
							}else if(AdEvent.TYPE_VIEW==adEvent.mType){
								UBLogUtil.logI(TAG+"----->RewardVideo AD show success!");
								UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD show success!");
							}
						}
						
						@Override
						public void onAdError(AdError adError) {
							UBLogUtil.logI(TAG+"----->RewardVideo AD failed!");
							UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD failed!");
							mRewardVideoADContainer.removeAllViews();
						}
					});
				}
			}
			
			@Override
			public void onNativeInfoFail(AdError adError) {
				UBLogUtil.logI(TAG+"----->Video AD show Failed!");
				UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_REWARDVIDEO,"Video AD show Failed!");
				mRewardVideoADContainer.removeAllViews();
			}
		};
	}

	/**
	 * 加载广告参数
	 */
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_Banner_ID");
		mInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_Interstitial_ID");
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_Splash_ID");
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_RewardVideo_ID");
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
		if (mSplashAD==null) {
			mSplashAD = new SplashAd(mActivity, mSplashADContainer, R.drawable.alert_dark_frame, mSplashADListener);
		}
		mSplashAD.requestAd(mSplashID);
	}

	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		if (mRewardVideoAD==null) {
			mRewardVideoAD = new StandardNewsFeedAd(mActivity);
		}
		mRewardVideoAD.requestAd(mRewardVideoID, 1, mRewardVideoListener);
	}

	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");	
		
		if (mInterstitialAD==null) {
			mInterstitialAD = new InterstitialAd(mActivity.getApplicationContext(),mActivity);
		}
		mInterstitialAD.requestAd(mInterstitialID,mInterstitialADListener);
	}

	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		if (mBannerAD==null) {
			mBannerAD = new BannerAd(mActivity, mBannerADContainer, mBannerADListener);
		}
		mBannerAD.show(mBannerID);
	}
 
	@Override
	public void hideADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->hideADWithADType");
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

}
