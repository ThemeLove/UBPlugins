package com.ubsdk.baidu.plugin;

import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;
import com.umbrella.game.ubsdk.iplugin.IUBPayPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class BaiDuPayPlugin implements IUBPayPlugin {
	private  final String TAG=BaiDuPayPlugin.class.getSimpleName();

	private Activity mActivity;
	private BaiDuPayPlugin(Activity activity){
		mActivity=activity;
	}
	@Override
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		BaiDuSDK.getInstance().pay(ubRoleInfo,ubOrderInfo);
	}

}
