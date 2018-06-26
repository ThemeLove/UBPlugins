package com.ubsdk.ad.vivo.plugin;

import java.lang.reflect.Method;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.vivo.mobilead.banner.VivoBannerAd;
import com.vivo.mobilead.interstitial.VivoInterstialAd;
import com.vivo.mobilead.listener.IAdListener;
import com.vivo.mobilead.manager.VivoAdManager;
import com.vivo.mobilead.model.VivoAdError;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;	

public class ADVIVOSDK implements IUBADPlugin{
	private final String TAG=ADVIVOSDK.class.getSimpleName();
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL,ADType.AD_TYPE_SPLASH};
	private Activity mActivity;
	private String mVIVOAppID;
	private String mVIVOBannerID;
	private String mVIVOInterstitialID;
	private String mVIVONativeID;
	private IAdListener mBannerADListener;
	private VivoBannerAd mBannerAD;
	private View mBannerADView;
	private WindowManager mWM;
	private FrameLayout mBannerContainer;
	private int mBannerADPosition;
	private UBADCallback mUBADCallback;
	private IAdListener mInterstitialADListener;
	private VivoInterstialAd mInterstitialAD;
	private ADVIVOSDK(Activity activity){
		this.mActivity=activity;
		mWM = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
		try {
			setActivityListener();
			loadADParams();
			initAD();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			UBLogUtil.logI(TAG+"----->VIVO AD init success！");
		}
	}
	
	private void setActivityListener() {
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){
			@Override
			public void onDestroy() {
				mBannerContainer.removeAllViews();
				mBannerContainer=null;
				mBannerADView=null;
				mBannerAD.destroy();
				mBannerAD=null;
				
				mInterstitialAD=null;
				
				super.onDestroy();
			}
		});
	}

	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mVIVOAppID = UBSDKConfig.getInstance().getParamMap().get("VIVO_AppID");
		mVIVOBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Banner_ID");
		mVIVOInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Interstitial_ID");
		mVIVONativeID = UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Native_ID");
		
		mBannerADPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Banner_Position"));
	}
	
	private void initAD() {
		UBLogUtil.logI(TAG+"----->initAD");
		
//		BannerAD
		mBannerContainer = new FrameLayout(mActivity);
		android.widget.FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerContainer.setLayoutParams(layoutParams);
		
		mBannerADListener = new IAdListener() {
			@Override
			public void onAdShow() {
				UBLogUtil.logI(TAG+"----->showBanner----->onADShow");
				if (mUBADCallback!=null) {
					mUBADCallback.onComplete(ADType.AD_TYPE_BANNER,"banner ad show success");
				}
			}
			
			@Override
			public void onAdReady() {
				UBLogUtil.logI(TAG+"----->showBanner----->onAdReady");
				
			}
			
			@Override
			public void onAdFailed(VivoAdError adError) {
				UBLogUtil.logI(TAG+"----->showBanner----->onAdFailed");
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_BANNER,"banner ad show failed:msg="+adError.getErrorMsg());
				}
			}
			
			@Override
			public void onAdClosed() {
				UBLogUtil.logI(TAG+"----->showBanner----->onAdClosed");
				mBannerContainer.setVisibility(View.GONE);
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_BANNER,"banner ad closed");
				}
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->showBanner----->onAdClick");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_BANNER,"banner ad click");
				}
			}
		};
		
//		InterstitialAD
		mInterstitialADListener = new IAdListener() {
			
			@Override
			public void onAdShow() {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onAdShow");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad show success!");
				}
			}
			
			@Override
			public void onAdReady() {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onAdReady");
				if (mInterstitialAD!=null) {
					mInterstitialAD.showAd();
				}
			}
			
			@Override
			public void onAdFailed(VivoAdError adError) {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onAdFailed");
				
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad show failed:msg="+adError.getErrorMsg());
				}
			}
			
			@Override
			public void onAdClosed() {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onAdClosed");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad closed!");
				}
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onAdClick");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad click!");
				}
			}
		};
		
//		初始化VIVO广告sdk
		VivoAdManager.getInstance().init(mActivity, mVIVOAppID);
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
				case ADType.AD_TYPE_REWARDVIDEO:
					showVideoAD();
					break;
				case ADType.AD_TYPE_SPLASH:
					showSplashAD();
					break;
				default:
					break;
				}
			}
		});
		
	}
	
	protected void showBannerAD() {	
		UBLogUtil.logI(TAG+"----->showBannerAD");
		if (mBannerADView==null) {
			mBannerAD = new VivoBannerAd(mActivity,mVIVOBannerID,mBannerADListener);
//			设置Banner显示关闭按钮
			mBannerAD.setShowClose(false);
//			设置刷新频率
			mBannerAD.setRefresh(30);
//			获取Banner的视图view
			mBannerADView = mBannerAD.getAdView();
			
			mBannerContainer.addView(mBannerADView);
			ADHelper.addBannerView(mWM,mBannerContainer,mBannerADPosition);
		}
//		mBannerContainer.removeAllViews();
//		mBannerContainer.addView(mBannerADView);
		mBannerContainer.setVisibility(View.VISIBLE);
	}
	/**
	 * 展示闪屏广告
	 */
	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		Intent intent = new Intent(mActivity, ADVIVOSplashActivity.class);
		mActivity.startActivity(intent);
	}

	/**
	 *  展示激励视频广告
	 */
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
	}

	/**
	 * 展示插屏广告
	 */
	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");
		if (mInterstitialAD==null) {
			mInterstitialAD = new VivoInterstialAd(mActivity,mVIVOInterstitialID,mInterstitialADListener);
		}
		mInterstitialAD.load();
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
		mBannerContainer.setVisibility(View.GONE);
	}

	private void hideInterstitialAD() {
		UBLogUtil.logI(TAG+"----->hideInterstitialAD");
	}

	private void hideSplashAD() {
		UBLogUtil.logI(TAG+"----->hideSplashAD");
	}

	private void hideRewardVideoAD() {
		UBLogUtil.logI(TAG+"----->hideRewardVideo");
	}
}
