package com.ubsdk.plugin;

import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;
import com.umbrella.game.ubsdk.iplugin.IUBPayPlugin;

import android.app.Activity;

public class BaiDuPayPlugin implements IUBPayPlugin {

	private Activity mActivity;
	private BaiDuPayPlugin(Activity activity){
		mActivity=activity;
	}
	@Override
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		BaiDuSDK.getInstance().pay(ubRoleInfo,ubOrderInfo);
	}

}
