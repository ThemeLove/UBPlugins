package com.ubsdk.vivo.plugin;

import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;
import com.umbrella.game.ubsdk.iplugin.IUBPayPlugin;

import android.app.Activity;

public class VIVOPayPlugin implements IUBPayPlugin{
	
	private Activity mActivity;
	private VIVOPayPlugin(Activity activity){
		this.mActivity=activity;
	}

	@Override
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		VIVOSDK.getInstance().pay(ubRoleInfo,ubOrderInfo);
		
	}

}
