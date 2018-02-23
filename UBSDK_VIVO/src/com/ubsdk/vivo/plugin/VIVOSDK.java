package com.ubsdk.vivo.plugin;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.bean.DataType;
import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoPayCallback;
import com.vivo.unionsdk.open.VivoPayInfo;
import com.vivo.unionsdk.open.VivoUnionSDK;

import android.app.Activity;
import android.os.Bundle;

public class VIVOSDK {
	private static VIVOSDK instance;
	private VIVOSDK(){}
	public static VIVOSDK getInstance(){
		if (instance==null) {
			synchronized (VIVOSDK.class) {
				if (instance == null) {
					instance = new VIVOSDK();
				}
			}
		}
		return instance;
	}
	
	private Activity mActivity;
	public void init(){
		mActivity=UBSDKConfig.getInstance().getGameActivity();
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onCreate(Bundle savedInstanceState) {
				// TODO Auto-generated method stub
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onPause() {
				// TODO Auto-generated method stub
				super.onPause();
			}

			@Override
			public void onResume() {
				// TODO Auto-generated method stub
				super.onResume();
			}

			@Override
			public void onStop() {
				// TODO Auto-generated method stub
				super.onStop();
			}

			@Override
			public void onDestroy() {
				// TODO Auto-generated method stub
				super.onDestroy();
			}

			@Override
			public void onBackPressed() {
				super.onBackPressed();
				exit();
			}
			
		});
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
		VivoUnionSDK.exit(mActivity, new VivoExitCallback() {
			
			@Override
			public void onExitConfirm() {
//				同步给出退出回调
				UBSDK.getInstance().getUBExitCallback().onExit();
			}
			
			@Override
			public void onExitCancel() {
//				同步给出取消回调
				UBSDK.getInstance().getUBExitCallback().onCancel("user cancel", null);
			}
		});
		
	}
	
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		VivoPayInfo vivoPayInfo = new VivoPayInfo("商品名称","商品描述", "6.00","验签", "appid", "订单号", null);
		VivoUnionSDK.pay(mActivity, vivoPayInfo, new VivoPayCallback() {
			
			@Override
			public void onVivoPayResult(String transNo, boolean isSuccess, String errorCode) {
				
			}
		});
	}
}
