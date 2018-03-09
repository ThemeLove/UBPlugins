package com.ubsdk.vivo.plugin;

import com.umbrella.game.ubsdk.bean.UBUserInfo;
import com.umbrella.game.ubsdk.iplugin.IUBUserPlugin;

import android.app.Activity;

public class VIVOUserPlugin implements IUBUserPlugin{
	private Activity mActivity;
	private VIVOUserPlugin(Activity activity){
		this.mActivity=activity;
		VIVOSDK.getInstance().init();
	}

	@Override
	public void login() {
		VIVOSDK.getInstance().login();
	}

	@Override
	public void logout() {
		VIVOSDK.getInstance().logout();
		
	}

	@Override
	public UBUserInfo getUserInfo() {
		return null;
	}

	@Override
	public void setGameDataInfo(Object obj, int dataType) {
		VIVOSDK.getInstance().setGameDataInfo(obj,dataType);
	}

}
