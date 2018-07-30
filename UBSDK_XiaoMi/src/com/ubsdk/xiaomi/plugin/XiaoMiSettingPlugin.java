package com.ubsdk.xiaomi.plugin;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBSettingPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class XiaoMiSettingPlugin implements IUBSettingPlugin{
	private final String TAG=XiaoMiSettingPlugin.class.getSimpleName();
	private Activity mActivity;
	
	private XiaoMiSettingPlugin(Activity activity){
		this.mActivity=activity;
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
		XiaoMiSDK.getInstance().gamePause();
	}

	@Override
	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");
		XiaoMiSDK.getInstance().exit();
	}

	@Override
	public int getPlatformID() {
		UBLogUtil.logI(TAG+"----->getPlatformID");
		return 0;
	}

	@Override
	public String getPlatformName() {
		UBLogUtil.logI(TAG+"----->getPlatformName");
		String platformName="xiaomi";
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
