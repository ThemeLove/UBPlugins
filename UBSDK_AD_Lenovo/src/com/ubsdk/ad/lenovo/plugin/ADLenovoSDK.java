package com.ubsdk.ad.lenovo.plugin;

import java.lang.reflect.Method;

import com.lestore.ad.sdk.Banner;
import com.lestore.ad.sdk.LestoreAD;
import com.lestore.ad.sdk.listener.BannerListener;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADLenovoSDK implements IUBADPlugin{
	private final String TAG=ADLenovoSDK.class.getSimpleName();
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL,ADType.AD_TYPE_SPLASH,ADType.AD_TYPE_REWARDVIDEO};
	private WindowManager mWM;
	private Activity mActivity;
	private FrameLayout mBannerContainer;
	private BannerListener mBannerADListener;
	private UBADCallback mUBADCallback;
	private String mBannerID;
	private int mBannerPosition;
	private String mInterstitialID;
	private String mSplashID;
	private String mRewardVideoID;
	private Banner mBannerAD;
	private ADLenovoSDK(Activity activity){
		mActivity=activity;
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		mWM=(WindowManager) activity.getSystemService(Activity.WINDOW_SERVICE);
		
		try {
			setActivityListener();
//			加载百度广告参数
			loadADParams();
//			初始化广告插件
			initAD();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setActivityListener(){
		
	}
	
	private void loadADParams(){
		UBLogUtil.logI(TAG+"----->loadADParams");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_Banner_ID");
		mBannerPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_Banner_Position"));
		mInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_Interstitial_ID");
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_Splash_ID");
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_RewardVideo_ID");
	}
	
	private void initAD(){
		UBLogUtil.logI(TAG+"----->initAD");
		mBannerContainer = new FrameLayout(mActivity);
		android.widget.FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerContainer.setLayoutParams(layoutParams);
		
		mBannerADListener = new BannerListener() {
			
			@Override
			public void onBannerShowSuccess(String msg) {
				UBLogUtil.logI(TAG+"----->onBannerADListener----->success!");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_BANNER,msg);
				}
			}
			
			@Override
			public void onBannerRequestSuccess() {
				UBLogUtil.logI(TAG+"----->onBannerADListener----->onRequestSuccess!");
			}
			
			@Override
			public void onBannerRequestFailed(String msg) {
				UBLogUtil.logI(TAG+"----->onBannerADListener----->failed!");
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_BANNER, msg);
				}
			}
		};
		
		LestoreAD.init(mActivity);// 执行初始化，否则获取不到
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


	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		if (mBannerAD==null) {
			mBannerAD = new Banner(mActivity, mBannerContainer, mBannerID,mBannerADListener);
		}
		mBannerContainer.setVisibility(View.VISIBLE);
	}

	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");
		
	}

	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		
	}

	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		
	}

	@Override
	public void hideADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->hideADWithADType");
		
		switch (adType) {
		case ADType.AD_TYPE_BANNER:
			hideBannerAD();
			break;
		case ADType.AD_TYPE_INTERSTITIAL:
			hideInterstitialAD();
			break;
		case ADType.AD_TYPE_REWARDVIDEO:
			hideRewardVideoAD();
			break;
		case ADType.AD_TYPE_SPLASH:
			hideSplashAD();
			break;
		default:
			break;
		}
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
	
	/**
	 * 隐藏Banner广告
	 */
	private void hideBannerAD(){
		UBLogUtil.logI(TAG+"----->hideBannerAD");
		mBannerContainer.setVisibility(View.GONE);
	}
	
}
