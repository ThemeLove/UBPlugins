package com.ubsdk.meizu.plugin;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBSettingPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class MeiZuSettingPlugin implements IUBSettingPlugin{
	private final String TAG=MeiZuSettingPlugin.class.getSimpleName();
	private Activity mActivity;
	private MeiZuSettingPlugin(Activity activity){
		this.mActivity=activity;
	}

	@Override
	public void gamePause() {
		UBLogUtil.logI(TAG,"gamePause");
		MeiZuSDK.getInstance().gamePause();
	}

	@Override
	public void exit() {
		UBLogUtil.logI(TAG,"exit");
		MeiZuSDK.getInstance().exit();
	}

	@Override
	public int getPlatformID() {
		UBLogUtil.logI(TAG,"getPlatformID");
		return 0;
	}
	
	@Override
	public String getPlatformName() {
		UBLogUtil.logI(TAG+"----->getPlatformName");
		
		String platformName="demo";
		try {
			platformName = UBSDKConfig.getInstance().getParamsMap().get(UBSDKConfig.UB_PlatformName);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return platformName;
		}
		return platformName;
	}

	@Override
	public int getSubPlatformID() {
		UBLogUtil.logI(TAG,"getSubPlatformID");
		return 0;
	}

	@Override
	public String getExtrasConfig(String extras) {
		UBLogUtil.logI(TAG,"getExtrasConfig");
		return null;
	}

	@Override
	public boolean isFunctionSupported(int functionName) {
		UBLogUtil.logI(TAG,"isFunctionSupported");
		return false;
	}

	@Override
	public String callFunction(int functionName) {
		UBLogUtil.logI(TAG,"callFunction");
		return null;
	}

}
