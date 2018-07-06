package com.ubsdk.ad.oppo.plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
	
    private List<String> mNeedRequestPMSList = new ArrayList<>();
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    
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
//					解决内存泄漏，一定要用这个api
					mWM.removeViewImmediate(mBannerContainer);
//					mWM.removeView(mBannerContainer);
					mBannerContainer=null;
				}
				if (mInterstitialAD!=null) {
					mInterstitialAD.destroyAd();
					mInterstitialAD=null;
				}
				MobAdManager.getInstance().exit(mActivity);
			
				super.onDestroy();
			}

			@Override
			public void onCreate(Bundle savedInstanceState) {
		        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
		            /**
		             * 如果你的targetSDKVersion >= 23，就要主动申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAd方法。
		             *
		             */
		            checkAndRequestPermissions();
		        } 
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
					switch (requestCode) {
		            /**
		             *处理SDK申请权限的结果。
		             */
		            case REQUEST_PERMISSIONS_CODE:
		                if (hasNecessaryPMSGranted()) {
		                	UBLogUtil.logI(TAG+"----->request Permission success");
		                } else {
		                	UBLogUtil.logI(TAG+"----->request Permission failed");
		                }
		                break;
		            default:
		                break;
		        }
				super.onRequestPermissionResult(requestCode, permissions, grantResults);
			}
			
		});
	}
	
    /**
     * 判断应用是否已经获得SDK运行必须的READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE两个权限。
     * @return
     */
    private boolean hasNecessaryPMSGranted() {
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE)) {
            if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true;
            }
        }
        return false;
    }
	
	   /**
     * 申请SDK运行需要的权限
     * 注意：READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE 两个权限是必须权限，没有这两个权限SDK无法正常获得广告。
     * WRITE_CALENDAR、ACCESS_FINE_LOCATION 是两个可选权限；没有不影响SDK获取广告；但是如果应用申请到该权限，会显著提升应用的广告收益。
     */
    private void checkAndRequestPermissions() {
        /**
         * READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE 两个权限是必须权限，没有这两个权限SDK无法正常获得广告。
         */
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE)) {
            mNeedRequestPMSList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        /**
         * WRITE_CALENDAR、ACCESS_FINE_LOCATION 是两个可选权限；没有不影响SDK获取广告；但是如果应用申请到该权限，会显著提升应用的广告收益。
         */
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CALENDAR)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_CALENDAR);
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mNeedRequestPMSList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //
        if (0 != mNeedRequestPMSList.size()) {
            /**
             * 有权限需要申请，主动申请。
             */
            String[] temp = new String[mNeedRequestPMSList.size()];
            mNeedRequestPMSList.toArray(temp);
            ActivityCompat.requestPermissions(mActivity, temp, REQUEST_PERMISSIONS_CODE);
        }
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
				UBLogUtil.logI(TAG+"----->--------------------->mBannerADView");
//				放在这里，保证只添加一次，多次添加会报错，有些机型在拒绝存储权限后，mBannerADView会为null
				ADHelper.addBannerView(mWM, mBannerContainer,mBannerADPosition);
			}
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
