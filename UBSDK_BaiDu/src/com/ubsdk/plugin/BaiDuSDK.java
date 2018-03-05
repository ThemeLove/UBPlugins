package com.ubsdk.plugin;

import org.json.JSONObject;

import com.duoku.platform.single.DKPlatform;
import com.duoku.platform.single.DKPlatformSettings.SdkMode;
import com.duoku.platform.single.DkErrorCode;
import com.duoku.platform.single.DkProtocolKeys;
import com.duoku.platform.single.callback.IDKSDKCallBack;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.bean.DataType;
import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.utils.TextUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class BaiDuSDK {
	private static  BaiDuSDK instance=null;
	private Activity mActivity; 
	private boolean baiDu_Game_isLandscape=true;
	
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
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		String orientation = UBSDKConfig.getInstance().getParamsMap().get("BaiDu_Game_Orientation");
		if (TextUtil.equalsIgnoreCase("portrait", orientation)) {
			baiDu_Game_isLandscape=false;
		}
		
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
			}

			@Override
			public void onPause() {
				super.onPause();
				DKPlatform.getInstance().stopSuspenstionService(mActivity);
				DKPlatform.getInstance().pauseBaiduMobileStatistic(mActivity); 
			}

			@Override
			public void onResume() {
				super.onResume();
				initPingXuan();
				GamePause();
				DKPlatform.getInstance().resumeBaiduMobileStatistic(mActivity);
			}

			@Override
			public void onBackPressed() {
				super.onBackPressed();
				exit();
			}
			
		});
		
//		SDK初始化
		DKPlatform.getInstance().init(mActivity,
				baiDu_Game_isLandscape, 
				SdkMode.SDK_PAY,//接入模式，支付版
				null,//DKCMMMData,移动MM初始化参数
				null,//DKCMGBData,移动基地初始化参数
				null,//DKCpWoStoreDaa,Cp版沃商店初始化数据
				new IDKSDKCallBack() {
					
					@Override
					public void onResponse(String paramString) {
						Log.d("GameMainActivity", paramString);
						try {
							JSONObject jsonObject = new JSONObject(paramString);
							// 返回的操作状态码
							int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
							
							//初始化完成
							if(mFunctionCode == DkErrorCode.BDG_CROSSRECOMMEND_INIT_FINSIH){
								initPingXuan();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				});
	}
	
	/**
	 * SDK品宣接口
	 */
	private void initPingXuan(){
		DKPlatform.getInstance().bdgameInit(mActivity, new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				Log.d("GameMainActivity","bggameInit success");
			}
		});
	}
	
	/**
	 * 游戏暂停
	 */
	private void GamePause(){
		DKPlatform.getInstance().bdgamePause(mActivity, new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				Log.d("GameMainActivity","bggamePause success");	}
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
		DKPlatform.getInstance().bdgameExit(mActivity, new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				UBSDK.getInstance().getUBExitCallback().onExit();
				
				}
			});
		
	}

	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		
	}
}
