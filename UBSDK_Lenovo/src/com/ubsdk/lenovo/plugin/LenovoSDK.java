package com.ubsdk.lenovo.plugin;

import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

public class LenovoSDK {
	private final String TAG=LenovoSDK.class.getSimpleName();
	private static LenovoSDK instance=null;
	private LenovoSDK(){}
	private static LenovoSDK getInstance(){
		if (instance==null) {
			if (instance==null) {
				instance=new LenovoSDK();
			}
		}
		return instance;
	}
	
	public void init(){
		UBLogUtil.logI(TAG+"----->init");
	}
	
	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
	}
}
