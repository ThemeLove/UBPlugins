package com.ubsdk.lenovo.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBUserPlugin;
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class LenovoUserPlugin implements IUBUserPlugin{
	private final String TAG=LenovoUserPlugin.class.getSimpleName();

	private Activity mActivity;
	private LenovoUserPlugin(Activity activity){
		this.mActivity=activity;
//		LenovoSDK.getInstance().init();
	}
	
	@Override
	public void login() {
		UBLogUtil.logI(TAG+"----->login");
	}

	@Override
	public void logout() {
		UBLogUtil.logI(TAG+"----->logout");
	}

	@Override
	public UBUserInfo getUserInfo() {
		UBLogUtil.logI(TAG+"----->getUserInfo");
		return null;
	}

	@Override
	public void setGameDataInfo(Object obj, int dataType) {
		UBLogUtil.logI(TAG+"----->setGameDataInfo");
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

}
