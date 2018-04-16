package com.ubsdk.ad.baidu.plugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import com.duoku.alone.ssp.DuoKuAdSDK;
import com.duoku.alone.ssp.ErrorCode;
import com.duoku.alone.ssp.FastenEntity;
import com.duoku.alone.ssp.entity.ViewEntity;
import com.duoku.alone.ssp.listener.CallBackListener;
import com.duoku.alone.ssp.listener.InitListener;
import com.duoku.alone.ssp.listener.ViewClickListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.plugintype.ad.ADHelper;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.plugintype.ad.BannerPosition;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class ADBaiDuSDK implements IUBADPlugin{
	private static final String TAG=ADBaiDuSDK.class.getSimpleName();
	
	private int [] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_FULLSCREEN,ADType.AD_TYPE_REWARDEDVIDEO,ADType.AD_TYPE_SPLASH};
	private Activity mActivity;
	private WindowManager mWM;

	private FrameLayout mBannerContainer;
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
		}
	}
	
	private void initAD(){
		DuoKuAdSDK.getInstance().init(mActivity, new InitListener() {
			@Override
			public void onBack(int code, String desc) {
				if (ErrorCode.SUCCESS_CODE==code) {//初始化成功
					UBLogUtil.logI(TAG+"----->初始化BaidDu AD成功!");
				}else{//初始化失败
					UBLogUtil.logI(TAG+"----->初始化BaiDu AD失败!");
				}
			}
		});
	}
	
	/**
	 * 加载百度广告参数
	 */
	private void loadADParams(){
		//		TODO
	}
	
	private void setActivityListener(){
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){
			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestory");
		        DuoKuAdSDK.getInstance().onDestoryBanner();
		        DuoKuAdSDK.getInstance().onDestoryBlock();
		        DuoKuAdSDK.getInstance().onDestoryVideo();
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
	public void showADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->showADWithADType");
		hideADWithADType(adType);//显示之前先隐藏广告
		switch (adType) {
		case ADType.AD_TYPE_BANNER:
			showBannerAD();
			break;
		case ADType.AD_TYPE_FULLSCREEN:
			showFullScreenAD();
			break;
		case ADType.AD_TYPE_REWARDEDVIDEO:
			showVideoAD();
			break;
		case ADType.AD_TYPE_SPLASH:
			showSplashAD();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 展示闪屏广告
	 */
	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
		mActivity.startActivity(new Intent(mActivity,BaiDuADSplashActivity.class));
	}

	/**
	 *  展示激励视频广告
	 */
	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
		final ViewEntity viewEntity = new ViewEntity();
		viewEntity.setType(FastenEntity.VIEW_VIDEO);
		viewEntity.setDirection(FastenEntity.VIEW_VERTICAL);
		viewEntity.setSeatId(1000003);
		
		DuoKuAdSDK.getInstance().cacheVideo(mActivity, viewEntity, new CallBackListener() {
			
			@Override
			public void onReady() {
				UBLogUtil.logI(TAG+"----->showVideoAD----->onReady");
				 DuoKuAdSDK.getInstance().showVideoImmediate(mActivity,viewEntity);
			}
			
			@Override
			public void onFailMsg(String msg) {
				UBLogUtil.logI(TAG+"----->showVideoAD----->onFailMsg----->msg="+msg);
			}
			
			@Override
			public void onComplete() {
				UBLogUtil.logI(TAG+"----->showVideoAD----->onComplete");
			}
			
			@Override
			public void onClick(int type) {
				if (type==1) {
					UBLogUtil.logI(TAG+"----->showVideoAD----->onClick----->type=1,user close");
				}else if(type==2){
					UBLogUtil.logI(TAG+"----->showVideoAD----->onClick----->type=2,user click");
				}
			}
		});
	}

	/**
	 * 展示插屏广告
	 */
	private void showFullScreenAD() {
		UBLogUtil.logI(TAG+"----->showFullScreenAD");
		ViewEntity viewEntity = new ViewEntity();
		viewEntity.setType(FastenEntity.VIEW_BLOCK);
		viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL);
		viewEntity.setSeatId(1000001);
		DuoKuAdSDK.getInstance().showBlockView(mActivity, viewEntity, new ViewClickListener() {
			
			@Override
			public void onSuccess(String adID) {
				UBLogUtil.logI(TAG+"----->showFullScreen----->onSuccess----->adID="+adID);
			}
			
			@Override
			public void onFailed(int errorCode) {
				UBLogUtil.logI(TAG+"----->showFullScreen----->errorCode="+errorCode);
			}
			
			@Override
			public void onClick(int type) {
				if (type==1) {//用户关闭
					UBLogUtil.logI(TAG+"----->onClick----->type=1,user close");
				}else if(type==2){//用户点击
					UBLogUtil.logI(TAG+"----->onClick----->type=2,user click");
				}
			}
		});
	}

	/**
	 * 显示Banner广告
	 */
	private void showBannerAD(){
		UBLogUtil.logI(TAG+"----->showBannerAD");
		
		mBannerContainer = new FrameLayout(mActivity);
		android.widget.FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBannerContainer.setLayoutParams(layoutParams);
		
		ADHelper.addBannerView(mWM, mBannerContainer, BannerPosition.TOP);
		
		ViewEntity viewEntity = new ViewEntity();
		viewEntity.setType(FastenEntity.VIEW_BANNER);//banner 类型
		viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL);//展示方向
		viewEntity.setPostion(FastenEntity.POSTION_TOP);//展示位置
		viewEntity.setSeatId(1000000);//广告位id
		
		DuoKuAdSDK.getInstance().showBannerView(mActivity, viewEntity, mBannerContainer, new ViewClickListener() {
			
			@Override
			public void onSuccess(String adID) {
				UBLogUtil.logI(TAG+"----->showBannerView----->onSuccess----->adID="+adID);
			}
			
			@Override
			public void onFailed(int errorCode) {
				UBLogUtil.logI(TAG+"----->showBannerView----->onFailed----->errorCode:"+errorCode);
			}
			
			@Override
			public void onClick(int type) {
				if (type==1) {//关闭广告
					UBLogUtil.logI(TAG+"----->onClick----->type=1,user close");
				}else if(type==2){//点击广告
					UBLogUtil.logI(TAG+"----->onClick----->type=2,user click");
				}
			}
		});
	}

	@Override
	public void hideADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->hideADWithADType");
		
		switch (adType) {
		case ADType.AD_TYPE_BANNER:
			hideBannerAD();
			break;
		case ADType.AD_TYPE_FULLSCREEN:
			
			break;
		case ADType.AD_TYPE_REWARDEDVIDEO:
			
			break;
		case ADType.AD_TYPE_SPLASH:
			
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
		
		DuoKuAdSDK.getInstance().hideBannerView(mActivity, mBannerContainer);
		
        if (null != mBannerContainer) {
        	mBannerContainer.removeAllViews();
            if (null != mWM) {
                mWM.removeView(mBannerContainer);
            }
            mBannerContainer.destroyDrawingCache();
            mBannerContainer = null;
        }
	}
}
