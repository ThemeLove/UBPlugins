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
		BaiDuSDK.getInstance().gamePause();
		UBLogUtil.logI(TAG,"gamePause");
	}

	@Override
	public void exit() {
		BaiDuSDK.getInstance().exit();
		UBLogUtil.logI(TAG,"exit");
	}

	@Override
	public int getPlatformId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSubPlatformId() {
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



}
