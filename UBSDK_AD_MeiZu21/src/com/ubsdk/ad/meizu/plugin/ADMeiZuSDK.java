package com.ubsdk.ad.meizu.plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.shenqi.video.AdBanner;
import com.shenqi.video.AdBannerListener;
import com.shenqi.video.InterstitialAd;
import com.shenqi.video.InterstitialAdListener;
import com.shenqi.video.ShenQiVideo;
import com.shenqi.video.VideoAdListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.plugintype.ad.BannerPosition;
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
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADMeiZuSDK implements IUBADPlugin{
	private final String TAG=ADMeiZuSDK.class.getSimpleName();
	private Activity mActivity;
	private WindowManager mWM;
	
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL,ADType.AD_TYPE_SPLASH,ADType.AD_TYPE_REWARDVIDEO};
	
	/**
	 * 游戏主Activity的根视图，不是DecorView
	 */
//	private ViewGroup mContainer;
	
	private String mBannerID;//魅族banner广告id
	private AdBannerListener mBannerADListener;
	private FrameLayout mBannerADContainer;
	private int mBannerPosition=BannerPosition.TOP;
	
	private String mInterstitialID;//魅族Interstitial广告id
	private InterstitialAd mInterstitialAD;
	
	private String mRewardVideoID;//魅族RewardVideo广告id
	private boolean isCanReward=false;//激励视频是否可以给用户发奖励
	
	
	public ADMeiZuSDK (Activity activity){
		mActivity=activity;
		mWM = (WindowManager) mActivity.getSystemService(Activity.WINDOW_SERVICE);
		mUBADCallback = UBAD.getInstance().getUBADCallback();
//		mContainer = (ViewGroup) ((ViewGroup)mActivity.findViewById(android.R.id.content)).getChildAt(0);
		try {
			if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {//23以上要动态获取权限
				checkAndRequestPermission();
			}
			setActivityListener();
			loadADParams();
			initAD();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			UBLogUtil.logI(TAG+"----->MeiZu AD init success!");
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
			public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
				UBLogUtil.logI(TAG+"----->onRequestPermissionResult");
			    if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
			    	UBLogUtil.logI(TAG+"----->have got the request permissioins");
			      } else {
			        // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
			    	ToastUtil.showToast(mActivity, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。");
			        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
			        mActivity.startActivity(intent);
			     }
			}
			
			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestroy");

				if (mBannerAD!=null) {
					mBannerAD.destory();
//					避免内存泄漏
					if (mBannerADContainer!=null) {
						mWM.removeViewImmediate(mBannerADContainer);
					}
					mBannerADContainer=null;
				}
				
				if (mInterstitialAD!=null) {
					mInterstitialAD.destory();
				}
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
		mBannerADContainer.setBackgroundColor(0xa0ffffff);
//		mBannerADContainer.setBackgroundDrawable(new ColorDrawable(0x000000ff));
		FrameLayout.LayoutParams bannerLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerADContainer.setLayoutParams(bannerLayoutParams);
		
//		各种广告回调不一定在主线程，所以一定要切换到主线程，sdk有的回调是在Thread=JavaBridge线程
		
//		Banner广告
		mBannerADListener = new AdBannerListener() {
			
			@Override
			public void onAdShow(Object adViews) {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						
						UBLogUtil.logI(TAG+"----->Banner AD show success!");
						if (mUBADCallback!=null) {
							mUBADCallback.onShow(ADType.AD_TYPE_BANNER, "Banner AD show success!");
						}
						isInitBannerADSuccess=true;
					}
				});
				
			}
			
			@Override
			public void onAdError(final String msg) {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->Banner AD falied!----->msg="+msg);
						if (mUBADCallback!=null) {
							mUBADCallback.onFailed(ADType.AD_TYPE_BANNER, msg);
						}
						mBannerADContainer.setVisibility(View.GONE);
						isInitBannerADSuccess=false;
					}
				});
			}
			
			@Override
			public void onAdClick() {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->Banner AD click!");
						if (mUBADCallback!=null) {
							mUBADCallback.onClick(ADType.AD_TYPE_BANNER, "Banner AD click!");
						}
						isInitBannerADSuccess=true;
					}
				});
				
			}

			@Override
			public void onAdClose() {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->Banner AD closed!");
						if (mUBADCallback!=null) {
							mUBADCallback.onClosed(ADType.AD_TYPE_BANNER,"Banner AD closed!");
						}
//						手动隐藏一下Banner,不然可能会出现banner广告黑屏
						mBannerADContainer.setVisibility(View.GONE);
						isInitBannerADSuccess=false;
					}
				});
			}
			
		};
		
		
//		InterstitialAD 
		mInterstitialAdListener = new InterstitialAdListener() {
			@Override
			public void onInterstitialAdShow() {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->Interstitial AD show success!");
						if (mUBADCallback!=null) {
							mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD show succcess!");
						}
					}
				});

			}
			
			@Override
			public void onInterstitialAdReady() {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->Interstitial AD ready!");
						if (mInterstitialAD.isInterstitialAdReady()) {
							mInterstitialAD.showInterstitialAd(mActivity);
						}
					}
				});
				
			}
			
			@Override
			public void onInterstitialAdFailed(final String msg) {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->Interstital AD Failed!----->msg="+msg);
						if (mUBADCallback!=null) {
							mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL, msg);
						}
					}
				});
				
			}
			
			@Override
			public void onInterstitialAdClose() {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->Interstitial AD closed!");
						if (mUBADCallback!=null) {
							mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD closed!");
						}
					}
				});
			}
			
			@Override
			public void onInterstitialAdClick() {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->Interstitial AD click!");
						if (mUBADCallback!=null) {
							mUBADCallback.onClick(ADType.AD_TYPE_INTERSTITIAL,"Interstitial AD click!");
						}
					}
				});
			}
		};
		
//		RewardVideo广告监听
		mVideoAdListener = new VideoAdListener() {
			
			@Override
			public void onVideoAdReady() {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->RewardVideo AD ready");
						if (ShenQiVideo.getInstance().isVideoReady()) {
							ShenQiVideo.getInstance().playVideoAd();
						}
					}
				});
				
			}
			
			@Override
			public void onVideoAdPlayProgress(final int current,final int max) {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->RewardVideo AD playPorcess----->current:"+current+",max:"+max);
					}
				});
				
			}
			
			@Override
			public void onVideoAdPlayFailed(final String msg) {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->RewardVideo AD Failed!----->msg="+msg);
						if (mUBADCallback!=null) {
							mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO, msg);
						}
					}
				});

			}
			
			@Override
			public void onVideoAdPlayComplete() {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					
					@Override
					public void run() {
//						魅族视频广告倒计时结束的时候会回调该方法
						UBLogUtil.logI(TAG+"----->RewardVideo AD Countdown completed!");
						if (mUBADCallback!=null) {
							mUBADCallback.onShow(ADType.AD_TYPE_REWARDVIDEO, "rewardVideo Countdown completed");
						}
						isCanReward=true;
					}
				});

			}
			
			@Override
			public void onVideoAdFailed(final String msg) {
				
				UBSDK.getInstance().runOnUIThread(new Runnable() {
					@Override
					public void run() {
						UBLogUtil.logI(TAG+"----->RewardVideo AD Failed!----->msg="+msg);
						if (mUBADCallback!=null) {
							mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO,msg);
						}
					}
				});
				
			}
			
			@Override
			public void onVideoAdClose() {
				
			UBSDK.getInstance().runOnUIThread(new Runnable() {
				@Override
				public void run() {
	//					魅族激励视频播放时页面上没有关闭按钮，同时屏蔽了物理返回键，激励视频倒计时播放完毕后页面上会出现一个关闭按钮，
	//					单机关闭按钮才能关闭视频广告，回调此方法，所以在该回调了给用户发奖励，给出onComplete回调
						UBLogUtil.logI(TAG+"----->RewardVideo AD Closed!");
						if (mUBADCallback!=null) {
							mUBADCallback.onClosed(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD close!");
							if (isCanReward) {
								mUBADCallback.onComplete(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD complete!");
							}
						}
					}
				});
			}
			
		};
	}

	/**
	 * 加载广告参数
	 */
	private void loadADParams() {
		UBLogUtil.logI(TAG+"----->loadADParams");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_Banner_ID");
		mBannerPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_Banner_Position"));
		
		mInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_Interstitial_ID");
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_MeiZu_RewardVideo_ID");
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
//				显示广告之前检查权限
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {//23以上要动态获取权限
					checkAndRequestPermission();
				}
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

	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		Intent intent = new Intent(mActivity,ADMeiZuSplashActivity.class);
		mActivity.startActivity(intent);
	}

//	private boolean isVideoInit=false;//视频是否初始化
	private VideoAdListener mVideoAdListener;
	private ShenQiVideo mShenQiVideo;
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		if (mShenQiVideo==null) {
			mShenQiVideo = ShenQiVideo.getInstance().init(mActivity, mRewardVideoID, mVideoAdListener);
			mShenQiVideo.fetchedVideoAd();
		}else{
			mShenQiVideo.fetchedVideoAd();
		}
	}
	
	private InterstitialAdListener mInterstitialAdListener;
	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");	
		if (mInterstitialAD==null) {
			mInterstitialAD = new InterstitialAd(mActivity, mInterstitialID);
			mInterstitialAD.setInterstitialAdListener(mInterstitialAdListener);
			mInterstitialAD.loadInterstitialAd();
		}else{
			mInterstitialAD.loadInterstitialAd();
		}
	}

	private AdBanner mBannerAD;
	private UBADCallback mUBADCallback;
	private boolean isFirstShowBannerAD=true;//是否是第一次调用显示Banner广告
	private boolean isInitBannerADSuccess=false;//是否初始化Banner成功
	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
		
		if (isFirstShowBannerAD) {
			ADHelper.addBannerView(mWM, mBannerADContainer, mBannerPosition);//只添加一次
			isFirstShowBannerAD=false;
		}
		
		mBannerAD = new AdBanner(mActivity, mBannerID);
		if (mBannerAD!=null) {
			UBLogUtil.logI(TAG+"----->mBannerAD");
			mBannerAD.setAdBannerListener(mBannerADListener);
			mBannerADContainer.removeAllViews();
			mBannerADContainer.addView(mBannerAD);
		}
		
/*		if (!isInitBannerADSuccess) {//初始化没有成功
			mBannerAD = new AdBanner(mActivity, mBannerID);
			if (mBannerAD!=null) {
				UBLogUtil.logI(TAG+"----->mBannerAD");
				mBannerAD.setAdBannerListener(mBannerADListener);
				mBannerADContainer.removeAllViews();
				mBannerADContainer.addView(mBannerAD);
			}
		}*/
		
		mBannerADContainer.setVisibility(View.VISIBLE);
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
		isCanReward=false;
	}
	
	
	/****************************************************动态权限相关****************************************************/
	private final int PERMISSION_REQUEST_CODE=1024;//权限请求的code
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
}
