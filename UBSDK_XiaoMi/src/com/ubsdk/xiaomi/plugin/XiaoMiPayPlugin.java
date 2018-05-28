package com.ubsdk.xiaomi.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBPayPlugin;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class XiaoMiPayPlugin implements IUBPayPlugin{
	private final String TAG=XiaoMiPayPlugin.class.getSimpleName();

	private Activity mActivity;
	private XiaoMiPayPlugin(Activity activity){
		this.mActivity=activity;
		XiaoMiSDK.getInstance().init();
	}
	
	@Override
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		XiaoMiSDK.getInstance().pay(ubRoleInfo, ubOrderInfo);
	}

	@Override
	public boolean isSupportMethod(String methodName, Object[] args) {
		UBLogUtil.logI(TAG+"----->isSupportMethod");
		return false;
	}

	@Override
	public Object callMethod(String methodName, Object[] args) {
		UBLogUtil.logI(TAG+"----->isSupportMethod");
		return null;
	}
}
