package com.ubsdk.yyb.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBUserPlugin;
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;

import android.app.Activity;

public class YYBUserPlugin implements IUBUserPlugin{
	private final String TAG=YYBUserPlugin.class.getSimpleName();
	private Activity mActivity;
	private YYBUserPlugin(Activity activity){
		this.mActivity=activity;
		YYBSDK.getInstance().init();
	}
	
	@Override
	public boolean isSupportMethod(String methodName, Object[] args) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object callMethod(String methodName, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void login() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UBUserInfo getUserInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGameDataInfo(Object obj, int dataType) {
		// TODO Auto-generated method stub
		
	}

}
