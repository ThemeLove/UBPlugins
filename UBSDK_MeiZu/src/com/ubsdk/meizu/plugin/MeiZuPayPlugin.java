package com.ubsdk.meizu.plugin;

import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;
import com.umbrella.game.ubsdk.iplugin.IUBPayPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class MeiZuPayPlugin implements IUBPayPlugin{
	private final String TAG=MeiZuPayPlugin.class.getSimpleName();
	private Activity mActivity;
	private MeiZuPayPlugin(Activity activity){
		this.mActivity=activity;
	}

	@Override
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG,"pay");
		MeiZuSDK.getInstance().pay(ubRoleInfo,ubOrderInfo);
	}
}
