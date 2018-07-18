package com.ubsdk.ad.xiaomi.plugin;

import java.lang.reflect.Method;

import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.ad.IVideoAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.xiaomi.ad.common.pojo.AdType;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADXiaoMiSDK implements IUBADPlugin{
	private final String TAG=ADXiaoMiSDK.class.getSimpleName();
	//小米广告建议直接入banner和插屏 这2中广告，预算相对充足，外加系统开屏在广告后台开启即可
	private final int[] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL};
	
	
	private Activity mActivity;
	private WindowManager mWM;
	private ViewGroup mContainer;
	
	private String mBannerID;//bannerID
	private String mInterstitialID;//InterstitialID
	private FrameLayout mBannerADContainer;
	private FrameLayout mSplashADContainer;
	private FrameLayout mRewardVideoADContainer;
	private UBADCallback mUBADCallback;
	private IAdWorker mSplashAD;
	private MimoAdListener mInterstitialADListener;
	private IAdWorker mInterstitialAD;
	private IVideoAdWorker mVideoAD;
	private MimoAdListener mBannerADListener;
	private IAdWorker mBannerAD;
	private int mBannerADPosition=1;//默认为顶部
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
			public void onCreate(Bundle savedInstanceState) {
				 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//version>23时
					 if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
			             || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
			             || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
						 
						 ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
			                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.INTERNET}, 1001);
					}
				}
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
				super.onRequestPermissionResult(requestCode, permissions, grantResults);
				if (requestCode == 1001) {
		            if (grantResults.length >= 2) {
		                if (grantResults[0]==PackageManager.PERMISSION_GRANTED) {
							UBLogUtil.logI(TAG+"----->获得读写权限");
						}else{
							UBLogUtil.logI(TAG+"----->申请读写权限失败");
						}
		                if (grantResults[1]==PackageManager.PERMISSION_GRANTED) {
							UBLogUtil.logI(TAG+"----->获得电话权限");
						}else{
							UBLogUtil.logI(TAG+"----->申请电话权限失败");
						}
		                if (grantResults[2]==PackageManager.PERMISSION_GRANTED) {
							UBLogUtil.logI(TAG+"----->获得联网权限");
						}else{
							UBLogUtil.logI(TAG+"----->申请联网权限失败");
						}
		            }
		        }
			}

			@Override
			public void onDestroy() {
				try {
					if (mBannerAD!=null) {
//						避免窗体泄漏
						mWM.removeViewImmediate(mBannerADContainer);
						mBannerAD.recycle();
						mBannerADContainer=null;
					}
					if (mSplashAD!=null) {
						mSplashAD.recycle();
					}
					if (mInterstitialAD!=null) {
						mInterstitialAD.recycle();
					}
					if (mVideoAD!=null) {
						mVideoAD.recycle();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onDestroy();
			}

			@Override
			public void onBackPressed() {
				if (mSplashADContainer.getVisibility()==View.VISIBLE) {//闪屏广告和插屏广告显示的时候屏蔽返回键，这里屏蔽不了返回键，因为退出是在外层activity里执行的
					return;
				}
				super.onBackPressed();
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
		
//		BannerAD
		mBannerADListener = new MimoAdListener(){

			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->Banner AD click!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_BANNER,"Banner AD click!");
				}
			}

			@Override
			public void onAdDismissed() {
				UBLogUtil.logI(TAG+"----->Banner AD show closed!");
				mBannerADContainer.setVisibility(View.GONE);
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_BANNER,"Banner AD closed!");
				}
			}
			
			@Override
			public void onAdFailed(String msg) {
				UBLogUtil.logI(TAG+"----->Banner AD show failed!----->msg="+msg);
				mBannerADContainer.setVisibility(View.GONE);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_BANNER, msg);
				}
			}

			@Override
			public void onAdLoaded() {
				UBLogUtil.logI(TAG+"----->Banner AD onLoaded");
				try {
					if (mBannerAD.isReady()) {
						mBannerAD.show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onAdPresent() {
				UBLogUtil.logI(TAG+"----->Banner AD show success!");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_BANNER, "Banners AD show success!");
				}
			}};
			
//			InterstitialAD
			mInterstitialADListener = new MimoAdListener(){

				@Override
				public void onAdClick() {
					UBLogUtil.logI(TAG+"----->on Interstitial click!");
					if (mUBADCallback!=null) {
						mUBADCallback.onClick(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD click!");
					}
				}

				@Override
				public void onAdDismissed() {
					UBLogUtil.logI(TAG+"----->on Interstitial close!");
					if (mUBADCallback!=null) {
						mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD close!");
					}
					try {
						if (mInterstitialAD != null) {
							mInterstitialAD.recycle();
						} 
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onAdFailed(String msg) {
					UBLogUtil.logI(TAG+"----->on Interstitial onAdError----->msg="+msg);
					if (mUBADCallback!=null) {
						mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD failed!");
					}
				}

				@Override
				public void onAdLoaded() {
					UBLogUtil.logI(TAG+"----->on Interstitial onAdLoaded");
					try {
						if (mInterstitialAD.isReady()) {
							mInterstitialAD.show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onAdPresent() {
					UBLogUtil.logI(TAG+"----->on Interstitial onAdPresent");
					if (mUBADCallback!=null) {
						mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD show success!");
					}
				}};
	}

	/**
	 * 加载广告参数
	 */
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_Banner_ID");
		mInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_Interstitial_ID");
		mBannerADPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_Banner_Position"));
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
		
		Intent intent = new Intent(mActivity,ADXiaoMiSplashActivity.class);
		mActivity.startActivity(intent);
		
/*		mSplashADContainer.setVisibility(View.VISIBLE);
		try {
			if (mSplashAD==null) {
				mSplashAD = AdWorkerFactory.getAdWorker(mActivity, mSplashADContainer, mSplashADListener,AdType.AD_SPLASH);
			}
			mSplashAD.loadAndShow(mSplashID);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		
		Intent intent = new Intent(mActivity,ADXiaoMiRewardVideoActivity.class);
		mActivity.startActivity(intent);
/*		
		mRewardVideoADContainer.setVisibility(View.VISIBLE);
		try {
			if (mVideoAD==null) {
				mVideoAD = AdWorkerFactory.getVideoAdWorker(mActivity, mRewardVideoID, AdType.AD_PLASTER_VIDEO);
				mVideoAD.setListener(mVideoADListener);
			}
			mVideoAD.recycle();
			mVideoAD.load();;
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");	
//		开启插屏的时候把banner隐藏
		mBannerADContainer.setVisibility(View.GONE);
		try {
			if (mInterstitialAD==null) {
				mInterstitialAD = AdWorkerFactory.getAdWorker(mActivity, mContainer, mInterstitialADListener,AdType.AD_INTERSTITIAL);
			}
			mInterstitialAD.load(mInterstitialID);
//			mInterstitialAD.loadAndShow(mInterstitialID);
/*			if (mInterstitialAD.isReady()) {
				mInterstitialAD.show();
			}else{
				
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		mBannerADContainer.setVisibility(View.VISIBLE);
		try {
			if (mBannerAD==null) {
				ADHelper.addBannerView(mWM, mBannerADContainer,mBannerADPosition);
				mBannerAD = AdWorkerFactory.getAdWorker(mActivity, mBannerADContainer, mBannerADListener,AdType.AD_BANNER);
			}
			mBannerAD.loadAndShow(mBannerID);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		mSplashADContainer.setVisibility(View.GONE);
	}
	
	private void hideRewardVideoAD() {
		UBLogUtil.logI(TAG+"----->hideRewardVideoAD");
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
