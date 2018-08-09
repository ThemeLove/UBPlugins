package com.ubsdk.huawei.plugin;

import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

public class HuaWeiSDK {
	private final String TAG=HuaWeiSDK.class.getSimpleName();
	
	private static HuaWeiSDK instance=null;
	
	private HuaWeiSDK(){ }
	
	public static HuaWeiSDK getInstance(){
		if (instance==null) {
			synchronized (HuaWeiSDK.class) {
				if (instance == null) {
					instance = new HuaWeiSDK();
				}
			}
		}
		return instance;
	}
	
	public void init(){
		UBLogUtil.logI(TAG+"----->init");
	}
	
	public void login(){
		
	}
	
	public void logout(){
		
	}
	
	public void exit(){
		
	}

	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		
	}

	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePauser");
		
	}
	
}
