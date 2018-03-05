package com.ubsdk.plugin;

import com.umbrella.game.ubsdk.bean.DataType;
import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;

public class BaiDuSDK {
	private static  BaiDuSDK instance=null; 
	private BaiDuSDK(){ }
	
	public static BaiDuSDK getInstance(){
		if (instance==null) {
			synchronized (BaiDuSDK.class) {
				if (instance == null) {
					instance = new BaiDuSDK();
				}
			}
		}
		return instance;
	}
	
	public void init(){
		
	}

	public void login() {
		// TODO Auto-generated method stub
		
	}

	public void logout() {
		// TODO Auto-generated method stub
		
	}

	public void setGameDataInfo(Object obj, DataType dataType) {
		// TODO Auto-generated method stub
		
	}

	public void exit() {
		
		
	}

	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		
	}
}
