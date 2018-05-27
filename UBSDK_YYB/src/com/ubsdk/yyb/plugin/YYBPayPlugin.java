package com.ubsdk.yyb.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBPayPlugin;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class YYBPayPlugin implements IUBPayPlugin{
	
	private final String TAG=YYBPayPlugin.class.getSimpleName();
	private static YYBPayPlugin instance=null;
	private Activity mActivity;
	private YYBPayPlugin (Activity activity){
		mActivity=activity;
	}
	
	@Override
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		YYBSDK.getInstance().pay(ubRoleInfo,ubOrderInfo);
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
