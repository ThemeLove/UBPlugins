package com.ubsdk.ad.vivo.plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.ToastUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.vivo.mobilead.banner.VivoBannerAd;
import com.vivo.mobilead.interstitial.VivoInterstialAd;
import com.vivo.mobilead.listener.IAdListener;
import com.vivo.mobilead.manager.VivoAdManager;
import com.vivo.mobilead.model.VivoAdError;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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
			checkAndRequestPermission();
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
			public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
				UBLogUtil.logI(TAG+"----->onRequestPermissionResult");
				
				 if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
					 	UBLogUtil.logI(TAG+"----->have got the request permissions");
			        } else {
			            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
			        	ToastUtil.showToast(mActivity,"应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。");
			            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			            intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
			            mActivity.startActivity(intent);
			        }
			}

			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestory");
				if (mBannerAD!=null&&mBannerADView!=null) {
					mBannerAD.destroy();
					mBannerContainer.removeAllViews();
					mWM.removeViewImmediate(mBannerContainer);
					mBannerAD=null;
					mBannerADView=null;
					mBannerContainer=null;
				}
				mInterstitialAD=null;
			}
		});
	}

	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		
		mVIVOAppID = UBSDKConfig.getInstance().getParamMap().get("VIVO_AppID");
		
		mBannerADPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Banner_Position"));
		mVIVOBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Banner_ID");
		mVIVOInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Interstitial_ID");
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
				UBLogUtil.logI(TAG+"----->Banner AD show success!");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_BANNER,"banner ad show success");
					mUBADCallback.onComplete(ADType.AD_TYPE_BANNER,"banner ad show success");
				}
				isInitBannerADSuccess=true;
			}
			
			@Override
			public void onAdReady() {
				UBLogUtil.logI(TAG+"----->Banner AD ready");
				isInitBannerADSuccess=true;
			}
			
			@Override
			public void onAdFailed(VivoAdError adError) {
				UBLogUtil.logI(TAG+"----->Banner AD show Failed!----->msg="+adError.getErrorMsg());
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_BANNER,"banner ad show failed:msg="+adError.getErrorMsg());
				}
				isInitBannerADSuccess=false;
			}
			
			@Override
			public void onAdClosed() {
				UBLogUtil.logI(TAG+"----->Banner AD closed!");
				mBannerContainer.setVisibility(View.GONE);
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_BANNER,"banner ad closed");
				}
				isInitBannerADSuccess=false;
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->Banner AD click!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_BANNER,"banner ad click");
				}
				isInitBannerADSuccess=true;
			}
		};
		
//		InterstitialAD
		mInterstitialADListener = new IAdListener() {
			@Override
			public void onAdShow() {
				UBLogUtil.logI(TAG+"----->Interstitial AD show Success!");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad show success!");
				}
			}
			
			@Override
			public void onAdReady() {
				UBLogUtil.logI(TAG+"----->Interstitial AD ready");
				if (mInterstitialAD!=null) {
					mInterstitialAD.showAd();
				}
			}
			
			@Override
			public void onAdFailed(VivoAdError adError) {
				UBLogUtil.logI(TAG+"----->Interstitial AD show Failed!----->msg="+adError.getErrorMsg());
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad show failed:msg="+adError.getErrorMsg());
				}
			}
			
			@Override
			public void onAdClosed() {
				UBLogUtil.logI(TAG+"----->Interstitial AD closed!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL,"interstitial ad closed!");
				}
			}
			
			@Override
			public void onAdClick() {
				UBLogUtil.logI(TAG+"----->Interstitial AD click!");
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
	
	private boolean isFirstShowBannerAD=true;
	private boolean isInitBannerADSuccess=false;
	protected void showBannerAD() {	
		UBLogUtil.logI(TAG+"----->showBannerAD");
		
		if (isFirstShowBannerAD) {
			ADHelper.addBannerView(mWM,mBannerContainer,mBannerADPosition);
			isFirstShowBannerAD=false;
		}
		
		if (!isInitBannerADSuccess) {
			mBannerAD=new VivoBannerAd(mActivity,mVIVOBannerID,mBannerADListener);
			UBLogUtil.logI(TAG+"----->new mBannerAD");
			if (mBannerAD!=null) {
				//设置刷新时间间隔				
				mBannerAD.setRefresh(20);
				//是否显示关闭按钮
				mBannerAD.setShowClose(true);
				mBannerADView=mBannerAD.getAdView();
			}
			
			if (mBannerADView!=null) {
				mBannerContainer.removeAllViews();
				mBannerContainer.addView(mBannerADView);
			}
		}
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
	
    /************************************************************动态权限相关************************************************************/
	private final int PERMISSION_REQUEST_CODE=1024;//权限请求的code
    /**
     * ----------非常重要----------
     * Android6.0以上的权限适配简单示例：
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广告SDK，否则不会有广告返回。
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(mActivity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!(mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            UBLogUtil.logI(TAG+"----->have got the request permissions");
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            mActivity.requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE);
        }
    }
    
    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
