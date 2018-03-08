package com.ubsdk.baidu.plugin;

import com.umbrella.game.ubsdk.bean.DataType;
import com.umbrella.game.ubsdk.bean.UBUserInfo;
import com.umbrella.game.ubsdk.iplugin.IUBUserPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class BaiDuUserPlugin implements IUBUserPlugin{
	private final String TAG=BaiDuUserPlugin.class.getSimpleName();
	private Activity mActivity;
	private BaiDuUserPlugin(Activity activity){
		this.mActivity=activity;
		BaiDuSDK.getInstance().init();
		UBLogUtil.logI(TAG,"init");
	}

	@Override
	public void login() {
		BaiDuSDK.getInstance().login();
		UBLogUtil.logI(TAG,"login");
	}

	@Override
	public void logout() {
		BaiDuSDK.getInstance().logout();
		UBLogUtil.logI(TAG,"logout");
	}

	@Override
	public UBUserInfo getUserInfo() {
		return null;
	}

	@Override
	public void setGameDataInfo(Object obj, int dataType) {
		BaiDuSDK.getInstance().setGameDataInfo(obj,dataType);
		UBLogUtil.logI(TAG,"setGameDataInfo");
	}

}
