package com.ubsdk.lenovo.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBPayPlugin;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class LenovoPayPlugin implements IUBPayPlugin{
	private final String TAG=LenovoPayPlugin.class.getSimpleName();
	private Activity mActivity;
	private LenovoPayPlugin(Activity activity){
		mActivity=activity;
	}

	@Override
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		LenovoSDK.getInstance().pay(ubRoleInfo, ubOrderInfo);
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
