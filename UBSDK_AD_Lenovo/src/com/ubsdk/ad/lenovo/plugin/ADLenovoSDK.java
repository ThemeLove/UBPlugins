package com.ubsdk.ad.lenovo.plugin;

import java.lang.reflect.Method;

import com.lestore.ad.sdk.Banner;
import com.lestore.ad.sdk.Interstitial;
import com.lestore.ad.sdk.LestoreAD;
import com.lestore.ad.sdk.VideoActivity;
import com.lestore.ad.sdk.VideoAdvert;
import com.lestore.ad.sdk.listener.BannerListener;
import com.lestore.ad.sdk.listener.InterstitialListener;
import com.lestore.ad.sdk.listener.VideoAdvertListener;
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

public class ADLenovoSDK implements IUBADPlugin{
	private final String TAG=ADLenovoSDK.class.getSimpleName();
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL,ADType.AD_TYPE_SPLASH,ADType.AD_TYPE_REWARDVIDEO};
	private WindowManager mWM;
	private Activity mActivity;
	private FrameLayout mBannerADContainer;
	private BannerListener mBannerADListener;
	private UBADCallback mUBADCallback;
	private String mBannerID;
	private int mBannerPosition;
	private String mInterstitialID;
	private String mRewardVideoID;
	private Banner mBannerAD;
	private InterstitialListener mInterstitialADListener;
	private Interstitial mInterstitialAD;
	private VideoAdvertListener mRewardVideoListener;
	private VideoAdvert mRewardVideoAD;
	private ADLenovoSDK(Activity activity){
		mActivity=activity;
		mWM=(WindowManager) activity.getSystemService(Activity.WINDOW_SERVICE);
		try {
			setActivityListener();
//			加载百度广告参数
			loadADParams();
//			初始化广告插件
			initAD();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			UBLogUtil.logI(TAG+"----->Lenovosdk AD init success!");
		}
	}
	
	private void setActivityListener(){
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onPause() {
				if(mBannerAD != null){
				   mBannerAD.PauseBanner();
				}
				super.onPause();
			}

			@Override
			public void onResume() {
				if(mBannerAD!= null){
				   mBannerAD.ResumeBanner();
				}
				super.onResume();
			}

			@Override
			public void onDestroy() {
				if(mBannerAD!= null){
					mBannerAD.DestroyBanner();
					mBannerAD=null;
				}
				
				if(mInterstitialAD!= null){
				   mInterstitialAD.destroyIntersititial();
				   mInterstitialAD=null;
				}
				
				if (mRewardVideoAD!= null) {
					mRewardVideoAD.destroy();
					mRewardVideoAD=null;
				}
				super.onDestroy();
				
			}
		});
	}
	
	private void loadADParams(){
		UBLogUtil.logI(TAG+"----->loadADParams");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_Banner_ID");
		mBannerPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_Banner_Position"));
		mInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_Interstitial_ID");
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_RewardVideo_ID");
	}
	
	private void initAD(){
		UBLogUtil.logI(TAG+"----->initAD");
		mBannerADContainer = new FrameLayout(mActivity);
		android.widget.FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerADContainer.setLayoutParams(layoutParams);
		
		mBannerADListener = new BannerListener() {
			
			@Override
			public void onBannerShowSuccess(String msg) {
				UBLogUtil.logI(TAG+"----->onBannerADListener----->showSuccess!");
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
				UBLogUtil.logI(TAG+"----->onBannerADListener----->requestFailed!");
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_BANNER, msg);
				}
			}
		};
		
//		插屏广告监听
		mInterstitialADListener = new InterstitialListener() {
			
			@Override
			public void onInterstitialShowSuccess(String msg) {
				UBLogUtil.logI(TAG+"----->interstitialListener----->showSuccess");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL, msg);
				}
			}
			
			@Override
			public void onInterstitialRequestFailed(String msg) {
				UBLogUtil.logI(TAG+"----->interstitialListener----->requestFail:msg="+msg);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL, msg);
				}
			}
			
			@Override
			public void onInterstitialDismiss() {
				UBLogUtil.logI(TAG+"----->interstitialListener----->dismiss");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL,"dismiss");
				}
			}
		};
		
//		RewardVideo监听
		mRewardVideoListener = new VideoAdvertListener() {
			
			@Override
			public void onVideoShowSuccess() {
				UBLogUtil.logI(TAG+"----->videoListener----->showSuccess!");
				if (mUBADCallback!=null) {
					mUBADCallback.onComplete(ADType.AD_TYPE_REWARDVIDEO,"video complete!");
				}
			}
			
			@Override
			public void onVideoRequestSuccess() {
				UBLogUtil.logI(TAG+"----->videoListener----->requestSuccess!");
			}
			
			@Override
			public void onVideoRequestFailed(String msg) {
				UBLogUtil.logI(TAG+"----->videoListener----->requestFailed!");
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO, msg);
				}
			}
			
			@Override
			public void onVideoDismiss() {
				UBLogUtil.logI(TAG+"----->videoListener----->dismiss");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_REWARDVIDEO, "dismiss");
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

	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		if (mBannerAD==null) {
			mBannerAD = new Banner(mActivity, mBannerADContainer, mBannerID,mBannerADListener);
//			把mBannerADContainer添加到WindowManager上，只添加一次
			ADHelper.addBannerView(mWM, mBannerADContainer,mBannerPosition);
		}
		mBannerADContainer.setVisibility(View.VISIBLE);
	}

	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");
		mInterstitialAD = new Interstitial(mActivity, mInterstitialID, mInterstitialADListener);
	}

	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		Intent intent = new Intent(mActivity, ADLenovoSplashActivity.class);
//		intent.putExtra("mSplashOutTime", mSplashTime);
		mActivity.startActivity(intent);
	}
	
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
/*		if (mRewardVideoAD==null) {
			mRewardVideoAD = new VideoAdvert(mActivity, mRewardVideoID, mRewardVideoListener);
			mRewardVideoAD.loadVideoAd(); // 加载视频
		}
		mRewardVideoAD.showVideoAd(); // 加载视频
*/		
		new VideoActivity(mActivity,mRewardVideoID, mRewardVideoListener);
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
			
		case ADType.AD_TYPE_SPLASH:
			hideSplashAD();
			break;
			
		case ADType.AD_TYPE_REWARDVIDEO:
			hideRewardVideoAD();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 隐藏Banner广告
	 */
	private void hideBannerAD(){
		UBLogUtil.logI(TAG+"----->hideBannerAD");
		mBannerADContainer.setVisibility(View.GONE);
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
