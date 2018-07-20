package com.ubsdk.ad.yyb.plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.comm.util.AdError;
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

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADYYBSDK implements IUBADPlugin{
	private final String TAG=ADYYBSDK.class.getSimpleName();
	private final int[] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL,ADType.AD_TYPE_SPLASH,ADType.AD_TYPE_REWARDVIDEO};
	private Activity mActivity;
	private ADYYBSDK(Activity activity){
		this.mActivity=activity;
		mWM = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
		try {
			setActivityListener();
			loadADParams();
			initAD();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			UBLogUtil.logI(TAG+"---->YYB AD init Success!");
		}
	}

	private void setActivityListener() {
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onCreate(Bundle savedInstanceState) {
				UBLogUtil.logI(TAG+"----->onCreate");
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {//23以上要动态获取权限
					checkAndRequestPermission();
				}
			}

			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestroy");
			}

			@Override
			public void onBackPressed() {
				UBLogUtil.logI(TAG+"----->onBackPressed");
				if (mBannerView!=null) {
					mBannerADContainer.removeAllViews();
//					防止窗体泄漏
					mWM.removeViewImmediate(mBannerADContainer);
					mBannerADContainer=null;
					mBannerView.destroy();
					mBannerView=null;
				}
				
				if (mInterstitialAD!=null) {
					mInterstitialAD.closePopupWindow();
					mInterstitialAD.destroy();
				}
			}

			@Override
			public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
				UBLogUtil.logI(TAG+"----->onBackPressed");
			    if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
			    	UBLogUtil.logI(TAG+"----->have got the request permissioins");
			      } else {
			        // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
			    	ToastUtil.showToast(mActivity, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。");
			        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
			        mActivity.startActivity(intent);
			      }
			}
		});
	}
	 
	private final int PERMISSION_REQUEST_CODE=1024;//权限请求的code
	private String mADYYBAppID;
	private String mBannerID;
	private String mInterstitialID;
	private String mRewardVideo;
	private UBADCallback mUBADCallback;
	private BannerView mBannerView;
	private AbstractBannerADListener mBannerADListener;
	private WindowManager mWM;
	private FrameLayout mBannerADContainer;
	private int mBannerPosition;
	private InterstitialAD mInterstitialAD;
	private AbstractInterstitialADListener mInterstitialListener;
	 
	 /**
	  * 检测并请求sdk必要权限
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

	    if (!(mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
	      lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
	    }

	    // 权限都已经有了，那么直接调用SDK
	    if (lackedPermission.size() == 0) {
	    	UBLogUtil.logI(TAG+"----->have got the request permissioins");
	    } else {
	      // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
	      String[] requestPermissions = new String[lackedPermission.size()];
	      lackedPermission.toArray(requestPermissions);
	      mActivity.requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE);
	    }
	  }

	 /**
	  * 权限是否获取成功
	  * @param grantResults
	  * @return
	  */
	 private boolean hasAllPermissionsGranted(int[] grantResults) {
		    for (int grantResult : grantResults) {
		      if (grantResult == PackageManager.PERMISSION_DENIED) {
		        return false;
		      }
		    }
		    return true;
	 }
	  
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mADYYBAppID = UBSDKConfig.getInstance().getParamMap().get("AD_YYB_APP_ID");
		mBannerPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_YYB_Banner_Position"));
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_YYB_Banner_ID");
		mInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_YYB_Interstitial_ID");
		mRewardVideo = UBSDKConfig.getInstance().getParamMap().get("AD_YYB_RewardVideo_ID");
	}

	private void initAD() {
		UBLogUtil.logI(TAG+"----->initAD");
		
//		BannerAD
		mBannerADContainer = new FrameLayout(mActivity);
		FrameLayout.LayoutParams bannerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mBannerADContainer.setLayoutParams(bannerLayoutParams);
		
		
		mBannerADListener = new AbstractBannerADListener() {
			
			@Override
			public void onNoAD(AdError adError) {
				UBLogUtil.logI(TAG+"----->Banner AD show failed!----->errorCode="+adError.getErrorCode()+",errorMsg="+adError.getErrorMsg());
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_BANNER,adError.getErrorMsg());
				}
			}
			
			@Override
			public void onADClicked() {
				UBLogUtil.logI(TAG+"----->Banner AD click!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_BANNER,"Banner AD click!");
				}
			}

			@Override
			public void onADClosed() {
				UBLogUtil.logI(TAG+"----->Banner AD closed!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_BANNER,"Banner AD closed");
				}
			}

			@Override
			public void onADExposure() {
				UBLogUtil.logI(TAG+"----->Banner AD show Success!");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_BANNER,"Banner AD show success!");
				}
			}

			@Override
			public void onADReceiv() {
				UBLogUtil.logI(TAG+"----->Banner AD show receiv");
			}
		};
		
//		InterstitialAD
		mInterstitialListener = new AbstractInterstitialADListener() {

		    @Override
		    public void onNoAD(AdError adError) {
		    	  UBLogUtil.logI(TAG+"----->Interstitial AD show Failed:errorCode="+adError.getErrorCode()+",msg="+adError.getErrorMsg());
		    	  if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL, adError.getErrorMsg());
		    	  }
		    }
		      
		    @Override
			public void onADClicked() {
		    	UBLogUtil.logI(TAG+"----->Interstitial AD click!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_INTERSTITIAL,"Interstitial Ad click！");
				}
			}

			@Override
			public void onADClosed() {
				UBLogUtil.logI(TAG+"----->Interstitial AD closed");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL,"Interstitial Ad closed！");
				}
			}

			@Override
			public void onADExposure() {
				UBLogUtil.logI(TAG+"----->Interstitial AD show success！");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD show success");
				}
			}


			@Override
		      public void onADReceive() {
		    	  UBLogUtil.logI(TAG+"----->Interstitial AD show Receive");
		    	  if (mInterstitialAD!=null) {
					  mInterstitialAD.show();
//					  mInterstitialAD.showAsPopupWindow();
		    	  }
		      }
		    };
	}

	@Override
	public void showADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->showADWithADType");
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		hideADWithADType(adType);
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

	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		mBannerADContainer.setVisibility(View.VISIBLE);
		if (mBannerView==null) {
			mBannerView = new BannerView(mActivity, ADSize.BANNER,mADYYBAppID,mBannerID);
			if (mBannerView!=null) {
				ADHelper.addBannerView(mWM, mBannerADContainer, mBannerPosition);//只添加一次
				mBannerADContainer.addView(mBannerView);
				mBannerView.setRefresh(30);
				mBannerView.setADListener(mBannerADListener);	
			}
		}
		if (mBannerView!=null) {
			mBannerView.loadAD();
		}
	}

	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");
		if (mInterstitialAD==null) {
			mInterstitialAD = new InterstitialAD(mActivity, mADYYBAppID, mInterstitialID);
			mInterstitialAD.setADListener(mInterstitialListener);
		}
		if (mInterstitialAD!=null) {
			mInterstitialAD.loadAD();
		}
	}

	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		Intent intent = new Intent(mActivity,ADYYBSplashActivity.class);
		mActivity.startActivity(intent);
	}

	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
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
	
	private void hideBannerAD() {
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
