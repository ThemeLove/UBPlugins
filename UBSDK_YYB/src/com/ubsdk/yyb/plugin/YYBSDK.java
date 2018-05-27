package com.ubsdk.yyb.plugin;

import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

public class YYBSDK {
	private final String TAG=YYBSDK.class.getSimpleName();
	private static YYBSDK instance;
	private YYBSDK(){}
	public static YYBSDK getInstance(){
		if (instance==null) {
			if (instance==null) {
				instance=new YYBSDK();
			}
		}
		return instance;
	}

	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
	}
}
