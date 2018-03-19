package com.ubsdk.baidu.plugin;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBSettingPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class BaiDuSettingPlugin implements IUBSettingPlugin{
	private final String TAG=BaiDuSettingPlugin.class.getSimpleName();
	private Activity mActivity;
	public BaiDuSettingPlugin(Activity activity){
		this.mActivity=activity;
	}
	
	@Override
	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePause");
		BaiDuSDK.getInstance().gamePause();
	}

	@Override
	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");
		BaiDuSDK.getInstance().exit();
	}

	@Override
	public int getPlatformID() {
		UBLogUtil.logI(TAG+"----->getPlatformID");
		return 0;
	}
	
	@Override
	public String getPlatformName() {
		UBLogUtil.logI(TAG+"----->getPlatformName");
		
		String platformName="baidu";
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
		UBLogUtil.logI(TAG+"----->getSubPlatformID");
		return 0;
	}

	@Override
	public String getExtrasConfig(String extras) {
		UBLogUtil.logI(TAG+"----->getExtrasConfig");
		return null;
	}

	@Override
	public boolean isFunctionSupported(int functionName) {
		UBLogUtil.logI(TAG+"----->isFunctionSupported");
		return false;
	}

	@Override
	public String callFunction(int functionName) {
		UBLogUtil.logI(TAG+"----->callFunction");
		return null;
	}

}
