package com.ubsdk.ad.baidu.plugin;

import java.lang.reflect.Method;

import com.duoku.alone.ssp.DuoKuAdSDK;
import com.duoku.alone.ssp.ErrorCode;
import com.duoku.alone.ssp.FastenEntity;
import com.duoku.alone.ssp.entity.ViewEntity;
import com.duoku.alone.ssp.listener.CallBackListener;
import com.duoku.alone.ssp.listener.InitListener;
import com.duoku.alone.ssp.listener.ViewClickListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.plugintype.ad.BannerPosition;
import com.umbrella.game.ubsdk.utils.TextUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADBaiDuSDK implements IUBADPlugin{
	private static final String TAG=ADBaiDuSDK.class.getSimpleName();
	
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_INTERSTITIAL,ADType.AD_TYPE_SPLASH,ADType.AD_TYPE_REWARDVIDEO};
	private Activity mActivity;
	private WindowManager mWM;

	private FrameLayout mBannerContainer;
	private String mBannerID;
	private int mBannerPosition=BannerPosition.TOP;//banner广告的位置

	private String mInterstitialID;
	private String mRewardVideoID;

	private ViewClickListener mBannerADListener;
	
	private UBADCallback mUBADCallback;

	private ADBaiDuSDK(Activity activity){
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
			UBLogUtil.logI(TAG+"----->BaiDu AD init success!");
		}
	}
	
	private void initAD(){
		UBLogUtil.logI(TAG+"----->initAD");
//		bannerAD
		mBannerContainer = new FrameLayout(mActivity);
		android.widget.FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerContainer.setLayoutParams(layoutParams);

		mBannerADListener = new ViewClickListener() {
				
				@Override
				public void onSuccess(String adID) {
					UBLogUtil.logI(TAG+"----->showBannerView----->onSuccess----->adID="+adID);
					if (mUBADCallback!=null) {
						mUBADCallback.onShow(ADType.AD_TYPE_BANNER, "Banner AD show success!");
					}
				}
				
				@Override
				public void onFailed(int errorCode) {
					UBLogUtil.logI(TAG+"----->showBannerView----->onFailed----->errorCode:"+errorCode);
					if (mUBADCallback!=null) {
						mUBADCallback.onFailed(ADType.AD_TYPE_BANNER,"Banner AD show failed:errorCode="+errorCode);
					}
				}
				
				@Override
				public void onClick(int type) {
					if (type==1) {//关闭广告
						UBLogUtil.logI(TAG+"----->onColsed----->type=1,banner AD close");
						if (mUBADCallback!=null) {
							mUBADCallback.onClosed(ADType.AD_TYPE_BANNER, "Banner AD closed!");
						}
						hideBannerAD();
					}else if(type==2){//点击广告
						UBLogUtil.logI(TAG+"----->onClick----->type=2,banner AD click");
						if (mUBADCallback!=null) {
							mUBADCallback.onClick(ADType.AD_TYPE_BANNER, "Banner AD click!");
						}
					}
				}
			};
		
//		InterstitialAD
		mInterstitialAdListener = new ViewClickListener() {
						
				@Override
				public void onSuccess(String adID) {
					UBLogUtil.logI(TAG+"----->showInterstitial----->onSuccess----->adID="+adID);
					if (mUBADCallback!=null) {
						mUBADCallback.onShow(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD show success!");
					}
				}
				
				@Override
				public void onFailed(int errorCode) {
					UBLogUtil.logI(TAG+"----->showFullScreen----->errorCode="+errorCode);
					if (mUBADCallback!=null) {
						mUBADCallback.onFailed(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD show failed:errorCode="+errorCode);
					}
				}
				
				@Override
				public void onClick(int type) {
					if (type==1) {//用户关闭
						UBLogUtil.logI(TAG+"----->onClosed----->type=1,interstitial AD close");
						if (mUBADCallback!=null) {
							mUBADCallback.onClosed(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD　 closed!");
						}
					}else if(type==2){//用户点击
						UBLogUtil.logI(TAG+"----->onClick----->type=2,interstitial AD click");
						if (mUBADCallback!=null) {
							mUBADCallback.onClick(ADType.AD_TYPE_INTERSTITIAL, "Interstitial AD  click!");
						}
					}
				}
			};
			
//			RewardVideoAD
			mRewardVideoADListener = new CallBackListener() {
				
				@Override
				public void onReady() {
					UBLogUtil.logI(TAG+"----->showVideoAD----->onReady");
					
					DuoKuAdSDK.getInstance().showVideoImmediate(mActivity,mRewardVideoEntity);
				}
				
				@Override
				public void onFailMsg(String msg) {
					UBLogUtil.logI(TAG+"----->showVideoAD----->onFailMsg----->msg="+msg);
					if (mUBADCallback!=null) {
						mUBADCallback.onFailed(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD show failed:msg="+msg);
					}
				}
				
				@Override
				public void onComplete() {
					UBLogUtil.logI(TAG+"----->showVideoAD----->onComplete");
					if (mUBADCallback!=null) {
						mUBADCallback.onComplete(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD complete");
					}
				}
				
				@Override
				public void onClick(int type) {
					if (type==1) {
						UBLogUtil.logI(TAG+"----->showVideoAD----->onClose----->type=1,rewardVideo AD close");
						if (mUBADCallback!=null) {
							mUBADCallback.onClosed(ADType.AD_TYPE_REWARDVIDEO,"RewardVideo AD closed!");
						}
					}else if(type==2){
						UBLogUtil.logI(TAG+"----->showVideoAD----->onClick----->type=2,rewardVideo AD click");
						if (mUBADCallback!=null) {
							mUBADCallback.onClick(ADType.AD_TYPE_REWARDVIDEO, "RewardVideo AD click!");
						}
					}
				}
			};
				
//		初始化百度广告
		DuoKuAdSDK.getInstance().init(mActivity, new InitListener() {
			@Override
			public void onBack(int code, String desc) {
				UBLogUtil.logI(TAG+"----->adbaidu init----->code="+code+",desc="+desc);
				if (ErrorCode.SUCCESS_CODE==code) {//初始化成功
					UBLogUtil.logI(TAG+"----->BaiDu AD init success!");
				}else{//初始化失败
					UBLogUtil.logI(TAG+"----->BaiDu AD init failed!");
				}
			}
		});
	}
	
	/**
	 * 加载百度广告参数
	 */
	private void loadADParams(){
		UBLogUtil.logI(TAG+"----->loadADParams");
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_BaiDu_Banner_ID");
		mInterstitialID = UBSDKConfig.getInstance().getParamMap().get("AD_BaiDu_Interstitial_ID");
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_BaiDu_RewardVideo_ID");
		mBannerPosition = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("AD_BaiDu_Banner_Position"));
		mGameOrientation = UBSDKConfig.getInstance().getUBGame().getOrientation();
	}
	
	private void setActivityListener(){
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){
			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestory");
		        DuoKuAdSDK.getInstance().onDestoryBanner();
		        DuoKuAdSDK.getInstance().onDestoryBlock();
		        DuoKuAdSDK.getInstance().onDestoryVideo();
		        
		        if (mBannerViewEntity!=null) {//如果有Banner广告，退出的时候从WindowManager移除
					mWM.removeViewImmediate(mBannerContainer);
					mWM=null;
			        mBannerContainer=null;
				}
				super.onDestroy();
			}
		});
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
	
	/**
	 * 展示闪屏广告
	 */
	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		mActivity.startActivity(new Intent(mActivity,ADBaiDuSplashActivity.class));
	}

	/**
	 *  展示激励视频广告
	 */
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		if (mRewardVideoEntity==null) {
			mRewardVideoEntity = new ViewEntity();
			mRewardVideoEntity.setType(FastenEntity.VIEW_VIDEO);
			if (TextUtil.equals("horizontal",mGameOrientation)) {//展示方向
				mBannerViewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL);
			}else{
				mBannerViewEntity.setDirection(FastenEntity.VIEW_VERTICAL);
			}
			mRewardVideoEntity.setSeatId(Integer.parseInt(mRewardVideoID));
		}
		DuoKuAdSDK.getInstance().cacheVideo(mActivity, mRewardVideoEntity, mRewardVideoADListener);
	}

	/**
	 * 展示插屏广告
	 */
	private void showInterstitialAD() {
		UBLogUtil.logI(TAG+"----->showInterstitialAD");
		ViewEntity viewEntity = new ViewEntity();
		viewEntity.setType(FastenEntity.VIEW_BLOCK);
		if (TextUtil.equals("horizontal",mGameOrientation)) {//展示方向
			mBannerViewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL);
		}else{
			mBannerViewEntity.setDirection(FastenEntity.VIEW_VERTICAL);
		}
		viewEntity.setSeatId(Integer.parseInt(mInterstitialID));
		DuoKuAdSDK.getInstance().showBlockView(mActivity, viewEntity, mInterstitialAdListener);
	}

	private ViewClickListener mInterstitialAdListener;

	private CallBackListener mRewardVideoADListener;

	private ViewEntity mRewardVideoEntity;

	private ViewEntity mBannerViewEntity;

	private String mGameOrientation="portrait";//默认竖屏
	/**
	 * 显示Banner广告
	 */
	private void showBannerAD(){
		UBLogUtil.logI(TAG+"----->showBannerAD");
		
		if (mBannerViewEntity==null) {
			ADHelper.addBannerView(mWM, mBannerContainer,mBannerPosition);//只添加一次
			mBannerViewEntity = new ViewEntity();
			mBannerViewEntity.setType(FastenEntity.VIEW_BANNER);//banner 类型
			if (TextUtil.equals("horizontal",mGameOrientation)) {//展示方向
				mBannerViewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL);
			}else{
				mBannerViewEntity.setDirection(FastenEntity.VIEW_VERTICAL);
			}
			
			if (BannerPosition.TOP==mBannerPosition) {
				mBannerViewEntity.setPostion(FastenEntity.POSTION_TOP);//展示位置
			}else if(BannerPosition.BOTTOM==mBannerPosition){
				mBannerViewEntity.setPostion(FastenEntity.POSTION_BOTTOM);//展示位置
			}else{
				mBannerViewEntity.setPostion(FastenEntity.POSTION_TOP);//展示位置
			}
			mBannerViewEntity.setSeatId(Integer.parseInt(mBannerID));//广告位id
		}
		mBannerContainer.setVisibility(View.VISIBLE);
		DuoKuAdSDK.getInstance().showBannerView(mActivity, mBannerViewEntity, mBannerContainer,mBannerADListener);
		
/*		ADHelper.addBannerView(mWM, mBannerContainer,mBannerPosition);//只添加一次
		ViewEntity viewEntity = new ViewEntity();
		viewEntity.setType(FastenEntity.VIEW_BANNER);//banner 类型
		viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL);//展示方向
		
		if (BannerPosition.TOP==mBannerPosition) {
			viewEntity.setPostion(FastenEntity.POSTION_TOP);//展示位置
		}else if(BannerPosition.BOTTOM==mBannerPosition){
			viewEntity.setPostion(FastenEntity.POSTION_BOTTOM);//展示位置
		}else{
			viewEntity.setPostion(FastenEntity.POSTION_TOP);//展示位置
		}
		
		viewEntity.setSeatId(Integer.parseInt(mBannerID));//广告位id
		
		DuoKuAdSDK.getInstance().showBannerView(mActivity, viewEntity, mBannerContainer,mBannerADListener);*/
	}

	@Override
	public void hideADWithADType(final int adType) {
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
//		mBannerContainer.setVisibility(View.GONE);
//		mBannerContainer.clearAnimation();
//		mBannerContainer.setVisibility(View.INVISIBLE);
//		boolean isHideBannerSuccess = DuoKuAdSDK.getInstance().hideBannerView(mActivity, mBannerContainer);
		
		DuoKuAdSDK.getInstance().hideBannerView(mActivity, mBannerContainer);
		
/*        if (null != mBannerContainer) {
//        	mBannerContainer.removeAllViews();
            if (null != mWM) {
                mWM.removeView(mBannerContainer);
            }
//            mBannerContainer.destroyDrawingCache();
        }*/
	}
}
