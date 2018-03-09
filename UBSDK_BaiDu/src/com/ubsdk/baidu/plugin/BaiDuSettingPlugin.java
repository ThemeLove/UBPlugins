package com.ubsdk.baidu.plugin;

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
		UBLogUtil.logI(TAG,"gamePause");
		BaiDuSDK.getInstance().gamePause();
	}

	@Override
	public void exit() {
		UBLogUtil.logI(TAG,"exit");
		BaiDuSDK.getInstance().exit();
	}

	@Override
	public int getPlatformId() {
		UBLogUtil.logI(TAG,"getPlatformId");
		return 0;
	}

	@Override
	public int getSubPlatformId() {
		UBLogUtil.logI(TAG,"getSubPlatformId");
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
		// TODO Auto-generated method stub
		return null;
	}



}
