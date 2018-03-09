package com.ubsdk.meizu.plugin;

import com.umbrella.game.ubsdk.bean.UBUserInfo;
import com.umbrella.game.ubsdk.iplugin.IUBUserPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class MeiZuUserPlugin implements IUBUserPlugin{
	private final String TAG=MeiZuUserPlugin.class.getSimpleName();

	private Activity mActivity;
	private MeiZuUserPlugin(Activity activity){
		this.mActivity=activity;
		MeiZuSDK.getInstance().init();
	}
	
	@Override
	public void login() {
		UBLogUtil.logI(TAG,"login");
		MeiZuSDK.getInstance().login();
	}

	@Override
	public void logout() {
		UBLogUtil.logI(TAG,"logout");
		MeiZuSDK.getInstance().logout();
	}

	@Override
	public UBUserInfo getUserInfo() {
		UBLogUtil.logI(TAG,"getUserInfo");
		return null;
	}

	@Override
	public void setGameDataInfo(Object obj, int dataType) {
		UBLogUtil.logI(TAG,"setGameDataInfo");
		MeiZuSDK.getInstance().setGameDataInfo(obj,dataType);
	}

}
