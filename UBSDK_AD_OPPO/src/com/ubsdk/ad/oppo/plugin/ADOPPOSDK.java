package com.ubsdk.ad.oppo.plugin;

import java.lang.reflect.Method;

import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobad.api.ad.BannerAd;
import com.oppo.mobad.api.ad.InterstitialAd;
import com.oppo.mobad.api.listener.IBannerAdListener;
import com.oppo.mobad.api.listener.IInterstitialAdListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADOPPOSDK implements IUBADPlugin{
	private final String TAG=ADOPPOSDK.class.getSimpleName();
	private Activity mActivity;
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL,ADType.AD_TYPE_SPLASH};
	private WindowManager mWM;
	private UBADCallback mUBADCallback;
	private FrameLayout mBannerContainer;
	private String mOPPOBannerID;
	private String mOPPOInterstitialID;
	private String mOPPORewardVideoID;
	private int mBannerADPosition;
	private IBannerAdListener mBannerADListener;
	private View mBannerADView;
	private BannerAd mBannerAD;
	private IInterstitialAdListener mInterstitialADListener;
	private InterstitialAd mInterstitialAD;
	private String mOPPOID;
	private ADOPPOSDK(Activity activity){
		this.mActivity=activity;
		mWM = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		try {
			setActivityListener();
			loadADParams();
			initAD();
		} finally {
			UBLogUtil.logI(TAG+"----->oppo ad init success");
		}
	}
	
	private void setActivityListener() {
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onDestroy() {
				if (mBannerAD!=null) {
					mBannerAD.destroyAd();
					mBannerAD=null;
				}
				if (mInterstitialAD!=null) {
					mInterstitialAD.destroyAd();
					mInterstitialAD=null;
				}
				MobAdManager.getInstance().exit(mActivity);
				super.onDestroy();
			}
		});
	}
	
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mOPPOID = UBSDKConfig.getInstance().getParamMap().get("OPPO_AppID");
		mOPPOBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_OPPO_Banner_ID");
		mOPPOInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_OPPO_Interstitial_ID");
		mOPPORewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_OPPO_RewardVideo_ID");
		
		mBannerADPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_OPPO_Banner_Position"));
	}
	
	private void initAD() {
		UBLogUtil.logI(TAG+"----->initAD");
		
//		BannerAD
		mBannerContainer = new FrameLayout(mActivity);
		android.widget.FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerContainer.setLayoutParams(layoutParams);
		
		mBannerADListener = new IBannerAdListener() {
			
			@Override
			public void onAdShow() {
				UBLogUtil.logI(TAG+"----->showBanner----->onAdShow");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_BANNER,"banner ad show success!");
				}
			}
			
			@Override
			public void onAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->showBanner----->onADFailed");
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_BANNER,"banner ad show failed:msg="+msg);
				}
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->showBanner----->onADClick");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_BANNER,"banner ad click!");
				}
			}
			
			@Override
			public void onAdReady() {
				UBLogUtil.logI(TAG+"----->showBanner----->onADReady");
			}
			
			@Override
			public void onAdClose() {
				UBLogUtil.logI(TAG+"----->showBanner----->onADClose");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_BANNER,"banner ad close!");
				}
				mBannerContainer.setVisibility(View.GONE);
			}
		};
		
//		InterstitialAD
		mInterstitialADListener = new IInterstitialAdListener() {
			
			@Override
			public void onAdShow() {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onADShow");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad show success");
				}
			}
			
			@Override
			public void onAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onADFailed");
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad show failed:msg="+msg);
				}
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onADClick");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad click");
				}
			}
			
			@Override
			public void onAdReady() {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onADReady");
				mInterstitialAD.showAd();
			}
			
			@Override
			public void onAdClose() {
				UBLogUtil.logI(TAG+"----->showInterstitial----->onADClose");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad close");
				}
			}
		};
		
//		初始化广告sdk
        InitParams initParams = new InitParams.Builder()
                .setDebug(true)//true打开SDK日志，当应用发布Release版本时，必须注释掉这行代码的调用，或者设为false
                .build();
        MobAdManager.getInstance().init(mActivity,mOPPOID,initParams);
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
			mBannerAD = new BannerAd(mActivity, mOPPOBannerID);
			mBannerAD.setAdListener(mBannerADListener);
			mBannerADView = mBannerAD.getAdView();
			if (mBannerADView!=null) {
				mBannerContainer.addView(mBannerADView);
			}
			ADHelper.addBannerView(mWM, mBannerContainer,mBannerADPosition);
		}
		mBannerAD.loadAd();
		mBannerContainer.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 展示闪屏广告
	 */
	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		Intent intent = new Intent(mActivity, ADOPPOSplashActivity.class);
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
			mInterstitialAD = new InterstitialAd(mActivity,mOPPOInterstitialID);
	        mInterstitialAD.setAdListener(mInterstitialADListener);
		}
        mInterstitialAD.loadAd();
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
