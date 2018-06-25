package com.ubsdk.oppo.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBUserPlugin;
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class OPPOUserPlugin implements IUBUserPlugin{
	private final String TAG=OPPOUserPlugin.class.getSimpleName();
	
	private Activity mActivity;
	
	private OPPOUserPlugin(Activity activity){
		mActivity=activity;
		OPPOSDK.getInstance().init();
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
	public void login() {
		UBLogUtil.logI(TAG+"----->login");
		OPPOSDK.getInstance().login();
	}

	@Override
	public void logout() {
		UBLogUtil.logI(TAG+"----->login");
		OPPOSDK.getInstance().logout();
	}

	@Override
	public UBUserInfo getUserInfo() {
		UBLogUtil.logI(TAG+"----->login");
		return null;
	}

	@Override
	public void setGameDataInfo(Object obj, int dataType) {
		UBLogUtil.logI(TAG+"----->login");
	}

}
