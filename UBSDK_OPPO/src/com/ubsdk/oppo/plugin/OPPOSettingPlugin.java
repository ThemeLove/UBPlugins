package com.ubsdk.oppo.plugin;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBSettingPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class OPPOSettingPlugin implements IUBSettingPlugin{
	private final String TAG=OPPOSettingPlugin.class.getSimpleName();
	
	private Activity mActivity;
	private OPPOSettingPlugin(Activity activity){
		this.mActivity=activity;
	}
	
	@Override
	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");
		OPPOSDK.getInstance().exit();
	}

	@Override
	public boolean isSupportMethod(String methodName, Object[] args) {
		UBLogUtil.logI(TAG+"----->isSupportMethod");
		return false;
	}

	@Override
	public Object callMethod(String methodName, Object[] args) {
		UBLogUtil.logI(TAG+"----->callMethod");
		return null;
	}

	@Override
	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePause");
		OPPOSDK.getInstance().gamePause();
	}

	@Override
	public int getPlatformID() {
		UBLogUtil.logI(TAG+"----->getPlatformID");
		return 0;
	}

	@Override
	public String getPlatformName() {
		UBLogUtil.logI(TAG+"----->getPlatformName");
		String platformName="oppo";
		try {
			platformName = UBSDKConfig.getInstance().getUBChannel().getUbPlatformName();
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

}
