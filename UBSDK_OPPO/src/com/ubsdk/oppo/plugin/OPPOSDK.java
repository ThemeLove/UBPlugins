package com.ubsdk.oppo.plugin;

import java.util.HashMap;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.model.UBPayConfigModel;
import com.umbrella.game.ubsdk.plugintype.pay.PayConfig;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class OPPOSDK {
	private final String TAG=OPPOSDK.class.getSimpleName();
	private static OPPOSDK instance=null;
	private Activity mActivity;
	private OPPOSDK(){}
	
	public static OPPOSDK getInstance(){
		if (instance==null) {
			synchronized (OPPOSDK.class) {
				if (instance == null) {
					instance = new OPPOSDK();
				}
			}
		}
		return instance;
	}
	
	public void init(){
		UBLogUtil.logI(TAG+"----->init");
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		String mOPPOPayRate = UBSDKConfig.getInstance().getParamMap().get("OPPO_Pay_Rate");
		String string2 = UBSDKConfig.getInstance().getParamMap().get("OPPO_");
		String string = UBSDKConfig.getInstance().getParamMap().get("");
		HashMap<String, PayConfig> payConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml");
		
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onDestroy() {
				super.onDestroy();
			}

			@Override
			public void onBackPressed() {
				super.onBackPressed();
			}
		});
		
		UBSDK.getInstance().getUBInitCallback().onSuccess();
	}
	
	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
	
	}

	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");
		GameCenterSDK.getInstance().onExit(mActivity, new GameExitCallback() {
			@Override
			public void exitGame() {
				UBSDK.getInstance().getUBExitCallback().onExit();
			}
		});
	}
}
