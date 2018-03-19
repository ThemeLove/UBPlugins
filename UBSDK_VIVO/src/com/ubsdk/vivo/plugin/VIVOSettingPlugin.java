package com.ubsdk.vivo.plugin;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBSettingPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class VIVOSettingPlugin implements IUBSettingPlugin {
	private final String TAG=VIVOSettingPlugin.class.getSimpleName();
	
	private Activity mActivity;
	private VIVOSettingPlugin (Activity activity){
		this.mActivity=activity;
	}

	@Override
	public void exit() {
		VIVOSDK.getInstance().exit();
	}

	@Override
	public int getPlatformID() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String getPlatformName() {
		UBLogUtil.logI(TAG+"----->getPlatformName");
		
		String platformName="vivo";
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getExtrasConfig(String extras) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isFunctionSupported(int functionName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String callFunction(int functionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void gamePause() {
		
	}

}
