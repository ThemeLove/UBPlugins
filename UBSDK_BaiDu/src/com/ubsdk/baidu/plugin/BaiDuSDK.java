package com.ubsdk.baidu.plugin;

import java.util.HashMap;

import org.json.JSONObject;

import com.duoku.platform.single.DKPlatform;
import com.duoku.platform.single.DKPlatformSettings.SdkMode;
import com.duoku.platform.single.DkErrorCode;
import com.duoku.platform.single.DkProtocolKeys;
import com.duoku.platform.single.callback.IDKSDKCallBack;
import com.duoku.platform.single.item.GamePropsInfo;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;
import com.umbrella.game.ubsdk.utils.TextUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class BaiDuSDK {
	private final String TAG = BaiDuSDK.class.getSimpleName();
	private static BaiDuSDK instance = null;
	private Activity mActivity;
	private boolean baiDu_Game_isLandscape = true;// 游戏是否是横屏
	private HashMap<String,HashMap<String,BaiDuBilling>> baiDuBillingWithTypeMap;//百度计费点数据
	private BaiDuSDK() {
	}

	public static BaiDuSDK getInstance() {
		if (instance == null) {
			synchronized (BaiDuSDK.class) {
				if (instance == null) {
					instance = new BaiDuSDK();
				}
			}
		}
		return instance;
	}

	public void init() {
		UBLogUtil.logI(TAG+"----->init");
		try {
			mActivity = UBSDKConfig.getInstance().getGameActivity();
			if (mActivity == null) {
				UBLogUtil.logW("the mAcitivity is null");
				UBSDK.getInstance().getUBInitCallback().onFailed("gameActivity is null", null);
				return;
			}
			String orientation = UBSDKConfig.getInstance().getParamMap().get("BaiDu_Game_Orientation");
			if (TextUtil.equalsIgnoreCase("portrait", orientation)) {
				baiDu_Game_isLandscape = false;
			}

			UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl() {

				@Override
				public void onCreate(Bundle savedInstanceState) {
					super.onCreate(savedInstanceState);
				}

				@Override
				public void onPause() {
					super.onPause();
					// DKPlatform.getInstance().stopSuspenstionService(mActivity);//品宣对应
					DKPlatform.getInstance().pauseBaiduMobileStatistic(mActivity);// 统计接口
				}

				@Override
				public void onResume() {
					super.onResume();
					// initPingXuan();//初始化品选
					// gamePause();//游戏暂停
					DKPlatform.getInstance().resumeBaiduMobileStatistic(mActivity);// 统计接口
				}

				@Override
				public void onBackPressed() {
					super.onBackPressed();
				}
			});

			UBSDK.getInstance().runOnUIThread(new Runnable() {

				@Override
				public void run() {
					try {

						UBLogUtil.logI(TAG, "thread:" + Thread.currentThread().getName());

						// SDK初始化
						DKPlatform.getInstance().init(mActivity, baiDu_Game_isLandscape, SdkMode.SDK_PAY, // 接入模式，支付版
								null, // DKCMMMData,移动MM初始化参数
								null, // DKCMGBData,移动基地初始化参数
								null, // DKCpWoStoreDaa,Cp版沃商店初始化数据
								new IDKSDKCallBack() {

									@Override
									public void onResponse(String paramString) {
										UBLogUtil.logI(TAG, "init:onResponse----->success");
										try {
											JSONObject jsonObject = new JSONObject(paramString);
											// 返回的操作状态码
											int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
											// 初始化完成
											if (mFunctionCode == DkErrorCode.BDG_CROSSRECOMMEND_INIT_FINSIH) {
												UBSDK.getInstance().getUBInitCallback().onSuccess();

												initPingXuan();// 初始化品宣
												callSupplement();// 补单接口
											}
										} catch (Exception e) {
											e.printStackTrace();
										}

									}
								});
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		baiDuBillingWithTypeMap = BaiDuBillingConfigXMLParser.getBaiDuBillingWithTypeMap();
	}

	/**
	 * SDK品宣接口
	 */
	private void initPingXuan() {
		UBLogUtil.logI(TAG+"----->initPingXuan");
		DKPlatform.getInstance().bdgameInit(mActivity, new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				Log.d("GameMainActivity", "bggameInit success");
				UBLogUtil.logI(TAG, "initPingXuan:success----->bggameInit");
			}
		});
	}

	/**
	 * 游戏暂停
	 */
	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePause");
		DKPlatform.getInstance().bdgamePause(mActivity, new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				UBLogUtil.logI(TAG, "GamePause:success----->bdgamePause");
				UBSDK.getInstance().getUBGamePauseCallback().onGamePause();
			}
		});
	}

	public void login() {
		UBLogUtil.logI(TAG+"----->login:success----->simulation empty implementation");
		UBSDK.getInstance().getUBLoginCallback().onSuccess(new UBUserInfo());
	}

	public void logout() {
		UBLogUtil.logI(TAG+"----->logout:success----->simulation success");
		UBSDK.getInstance().getUBLogoutCallback().onSuccess();
	}

	public void setGameDataInfo(Object obj, int dataType) {

		UBLogUtil.logI(TAG+"----->setGameDataInfo----->simulation empty implementation");
	}

	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");
		DKPlatform.getInstance().bdgameExit(mActivity, new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				UBLogUtil.logI(TAG+"----->exit:onResponse----->"+paramString);
				UBSDK.getInstance().getUBExitCallback().onExit();
			}
		});
	}

	
	
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {

		UBLogUtil.logI(TAG+"----->pay");
		UBLogUtil.logI(TAG+"----->productID="+ubOrderInfo.getGoodsID());
		
		UBLogUtil.logI(TAG+"----->baiduBillingWithTypeMap="+baiDuBillingWithTypeMap);
		HashMap<String, BaiDuBilling> billingMap = baiDuBillingWithTypeMap.get("baidu");
		BaiDuBilling baiDuBilling=null;
		if (billingMap!=null) {
			baiDuBilling = billingMap.get(ubOrderInfo.getGoodsID());
		}
		
		if (baiDuBilling==null) {
			UBLogUtil.logE(TAG+"----->error----->baidu 计费点配置出错!!!");
			return;
		}
		
		GamePropsInfo gamePropsInfo = new GamePropsInfo(baiDuBilling.getBillingID(), // 百度计费点id
				baiDuBilling.getBillingPrice(), // 计费点价格
				baiDuBilling.getBillingName(), // 计费点名称
				ubOrderInfo.getExtrasParams());// 透传字段
		
		gamePropsInfo.setThirdPay("qpfangshua");// 只接入微信支付宝,固定值
		
		DKPlatform.getInstance().invokePayCenterActivity(mActivity, gamePropsInfo, null, null, null, null, null,
				RechargeCallback);
	}

	// 支付回调函数
	IDKSDKCallBack RechargeCallback = new IDKSDKCallBack() {
		@Override
		public void onResponse(String paramString) {

			UBLogUtil.logI(TAG+"----->pay:onResponse----->"+paramString);
			try {
				JSONObject jsonObject = new JSONObject(paramString);
				// 支付状态码
				int mStatusCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_STATUS_CODE);
				if (mStatusCode == DkErrorCode.BDG_RECHARGE_SUCCESS) {
					// 返回支付成功的状态码，开发者可以在此处理相应的逻辑
					// 订单ID
					String mOrderId = null;
					// 订单状态
					String mOrderStatus = null;
					// 道具ID
					String mOrderProductId = null;
					// 道具价格
					String mOrderPrice = null;
					// 支付通道
					String mOrderPayChannel = null;
					if (jsonObject.has(DkProtocolKeys.BD_ORDER_ID)) {
						mOrderId = jsonObject.getString(DkProtocolKeys.BD_ORDER_ID);
					}
					if (jsonObject.has(DkProtocolKeys.BD_ORDER_STATUS)) {
						mOrderStatus = jsonObject.getString(DkProtocolKeys.BD_ORDER_STATUS);
					}
					if (jsonObject.has(DkProtocolKeys.BD_ORDER_PRODUCT_ID)) {
						mOrderProductId = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRODUCT_ID);
					}
					if (jsonObject.has(DkProtocolKeys.BD_ORDER_PRICE)) { // 用户实际支付的价格
						mOrderPrice = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRICE);
					}
					if (jsonObject.has(DkProtocolKeys.BD_ORDER_PAY_CHANNEL)) {
						mOrderPayChannel = jsonObject.getString(DkProtocolKeys.BD_ORDER_PAY_CHANNEL);
					}

					String mOrderPriceOriginal = "";
					if (jsonObject.has(DkProtocolKeys.BD_ORDER_PAY_ORIGINAL)) { // 道具的原始价格，只有有打折信息该字段才有值
						mOrderPriceOriginal = jsonObject.getString(DkProtocolKeys.BD_ORDER_PAY_ORIGINAL);
					}

					int mNum = 0;
					if ("".equals(mOrderPriceOriginal) || null == mOrderPriceOriginal) {
						mNum = Integer.valueOf(mOrderPrice) * 10;
					} else {
						mNum = Integer.valueOf(mOrderPriceOriginal) * 10;
					}

					UBLogUtil.logI(TAG, "pay:paySuccess");
					UBSDK.getInstance().getUBPayCallback().onSuccess(mOrderId, mOrderId, mOrderProductId, "",
							mOrderProductId, "");
				} else if (mStatusCode == DkErrorCode.BDG_RECHARGE_USRERDATA_ERROR) {

					UBLogUtil.logI(TAG, "pay:fail----->extras params illegal");
					UBSDK.getInstance().getUBPayCallback().onFailed("", "extras params illegal", null);
				} else if (mStatusCode == DkErrorCode.BDG_RECHARGE_ACTIVITY_CLOSED) {

					// 返回玩家手动关闭支付中心的状态码，开发者可以在此处理相应的逻辑
					UBLogUtil.logI(TAG, "pay:fail----->user close the payment center");
					UBSDK.getInstance().getUBPayCallback().onFailed("", "user close the payment center", null);
				} else if (mStatusCode == DkErrorCode.BDG_RECHARGE_FAIL) {

					// 返回支付失败的状态码，开发者可以在此处理相应的逻辑
					UBLogUtil.logI(TAG, "pay:fail----->Failed purchase");
					UBSDK.getInstance().getUBPayCallback().onFailed("", "Failed purchase", null);
				} else if (mStatusCode == DkErrorCode.BDG_RECHARGE_EXCEPTION) {

					// 返回支付出现异常的状态码，开发者可以在此处理相应的逻辑
					UBLogUtil.logI(TAG, "pay:fail----->purchase exception");
					UBSDK.getInstance().getUBPayCallback().onFailed("", "purchase exception", null);
				} else if (mStatusCode == DkErrorCode.BDG_RECHARGE_CANCEL) {

					// 返回取消支付的状态码，开发者可以在此处理相应的逻辑
					UBLogUtil.logI(TAG, "pay:fail----->user cancel");
					UBSDK.getInstance().getUBPayCallback().onFailed("", "user cancel", null);
				} else {

					UBLogUtil.logI(TAG, "pay:fail----->Unknown situation");
					UBSDK.getInstance().getUBPayCallback().onFailed("", "Unknown situation", null);
				}

			} catch (Exception e) {
				e.printStackTrace();
				UBLogUtil.logI(TAG, "pay:fail----->Unknown situation");
				UBSDK.getInstance().getUBPayCallback().onFailed("", "Unknown situation", null);
			}
		}
	};

	/**
	 * 百度补单接口
	 */
	private void callSupplement(){
		UBLogUtil.logI(TAG+"----->callSupplement");
		//回调函数
		IDKSDKCallBack supplementlistener = new IDKSDKCallBack(){
			@Override
			public void onResponse(String paramString) {
				UBLogUtil.logI(TAG, paramString);
				try {
					JSONObject jsonObject = new JSONObject(paramString);
					// 返回的操作状态码
					int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);

					if (mFunctionCode == DkErrorCode.BDG_REORDER_CHECK) {
						// 订单ID 返回补单成功的状态码，可以补发道具
						String mOrderId = jsonObject.getString(DkProtocolKeys.BD_ORDER_ID);
						// 道具ID
						String mOrderProductId = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRODUCT_ID);
						// 道具价格
						String mOrderPrice = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRICE);

						// int mNum = Integer.valueOf(mOrderPrice) * 10;

						UBLogUtil.logI(TAG+"----->callSupplement:success----->orderId=" + mOrderId);
						UBSDK.getInstance().getUBPayCallback().onSuccess("", mOrderId, mOrderProductId, "", mOrderPrice,
								"");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		DKPlatform.getInstance().invokeSupplementDKOrderStatus(mActivity, supplementlistener);
	}
}
