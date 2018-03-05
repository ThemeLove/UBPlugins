package com.ubsdk.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBSettingPlugin;

import android.app.Activity;

public class BaiDuSettingPlugin implements IUBSettingPlugin{
	private Activity mActivity;
	public BaiDuSettingPlugin(Activity activity){
		this.mActivity=activity;
	}

	@Override
	public void exit() {
		BaiDuSDK.getInstance().exit();
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
