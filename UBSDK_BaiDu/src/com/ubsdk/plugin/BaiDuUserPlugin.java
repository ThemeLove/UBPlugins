package com.ubsdk.plugin;

import com.umbrella.game.ubsdk.bean.DataType;
import com.umbrella.game.ubsdk.bean.UBUserInfo;
import com.umbrella.game.ubsdk.iplugin.IUBUserPlugin;

import android.app.Activity;

public class BaiDuUserPlugin implements IUBUserPlugin{
	private Activity mActivity;
	private BaiDuUserPlugin(Activity activity){
		this.mActivity=activity;
		BaiDuSDK.getInstance().init();
	}

	@Override
	public void login() {
		BaiDuSDK.getInstance().login();
	}

	@Override
	public void logout() {
		BaiDuSDK.getInstance().logout();
	}

	@Override
	public UBUserInfo getUserInfo() {
		return null;
	}

	@Override
	public void setGameDataInfo(Object obj, DataType dataType) {
		BaiDuSDK.getInstance().setGameDataInfo(obj,dataType);
	}

}
