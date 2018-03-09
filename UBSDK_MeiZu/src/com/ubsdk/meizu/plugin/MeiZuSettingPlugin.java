package com.ubsdk.meizu.plugin;

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
		UBLogUtil.logI(TAG,"callFunction");
		return null;
	}

}
