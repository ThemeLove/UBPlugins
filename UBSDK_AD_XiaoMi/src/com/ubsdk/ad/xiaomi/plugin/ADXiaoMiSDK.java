package com.ubsdk.ad.xiaomi.plugin;

import java.lang.reflect.Method;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class ADXiaoMiSDK implements IUBADPlugin{
	private final String TAG=ADXiaoMiSDK.class.getSimpleName();
	private final int[] supportedADTypeArray=new int[]{ADType.AD_TYPE_BANNER,ADType.AD_TYPE_BANNER,ADType.AD_TYPE_REWARDEDVIDEO,ADType.AD_TYPE_SPLASH};
	
	
	private Activity mActivity;
	
	private String mBannerID;//bannerID
	private String mFullScreenID;//FullScreenID
	private String mSplashID;//SplashID
	private String mRewardVideoID;//RewardVideoID
	private ADXiaoMiSDK(Activity activity){
		this.mActivity=activity;
		try {
			
			loadADParams();
			
		} catch (Exception e) {
			
		}
	}

	/**
	 * 加载广告参数
	 */
	private void loadADParams() {
		mBannerID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_BannerID");
		mFullScreenID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_FullScreenID");
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_SplashID");
		mRewardVideoID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_RewardVideoID");
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
	

	private void showSplashAD() {
		UBLogUtil.logI(TAG+"----->showSplashAD");
//		显示广告之前先设置容器可见
	}

	private void showVideoAD() {
		UBLogUtil.logI(TAG+"----->showVideoAD");
	}

	private void showFullScreenAD() {
		UBLogUtil.logI(TAG+"----->showFullScreenAD");	
	}

	private void showBannerAD() {
		UBLogUtil.logI(TAG+"----->showBannerAD");
	}
 
	@Override
	public void hideADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->hideADWithADType");
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
