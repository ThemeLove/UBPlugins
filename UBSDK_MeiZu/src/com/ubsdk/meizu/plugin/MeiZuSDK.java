package com.ubsdk.meizu.plugin;

import java.util.Locale;
import java.util.TreeMap;

import com.meizu.gamesdk.model.callback.MzPayListener;
import com.meizu.gamesdk.model.model.MzPayParams;
import com.meizu.gamesdk.model.model.PayResultCode;
import com.meizu.gamesdk.offline.core.MzGameCenterPlatform;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;
import com.umbrella.game.ubsdk.utils.TextUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.umbrella.game.ubsdk.utils.UBMD5Util;

import android.app.Activity;
import android.os.Bundle;

public class MeiZuSDK {
	private final String TAG=MeiZuSDK.class.getSimpleName();
	private static MeiZuSDK instance=null;
	private MeiZuSDK(){}
	public static  MeiZuSDK getInstance(){
		if (instance==null) {
			if (instance==null) {
				instance=new MeiZuSDK();
			}
		}
		return instance;
	}
	private Activity mActivity=null;
	private String mMeiZuAppID;
	private String mMeiZuAppSecret;
	
	public void init(){
		UBLogUtil.logI(TAG+"----->init");
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		mMeiZuAppID = UBSDKConfig.getInstance().getParamMap().get("MeiZu_AppID");
		mMeiZuAppSecret = UBSDKConfig.getInstance().getParamMap().get("MeiZu_AppSecret");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onPause() {
				super.onPause();
			}

			@Override
			public void onResume() {
				super.onResume();
			}

			@Override
			public void onAttachedToWindow() {
				super.onAttachedToWindow();
				UBLogUtil.logI(TAG+"----->onAttachedToWindow");
				MzGameCenterPlatform.orderQueryConfirm(mActivity,mzPayListener);
			}

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
	
	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePause");
	}
	
	public void login(){
		UBLogUtil.logI(TAG+"----->login:success----->empty implemention");
		UBSDK.getInstance().getUBLoginCallback().onSuccess(new UBUserInfo());
	}
	
	private String mCpOrderID="";
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		
		String systemTime=System.currentTimeMillis()+"";
		String appID=TextUtil.isEmpty(mMeiZuAppID)?"":mMeiZuAppID;
		mCpOrderID = TextUtil.isEmpty(ubOrderInfo.getCpOrderID())?"":ubOrderInfo.getCpOrderID();
		String cpOrderCreateTime=TextUtil.isEmpty(ubOrderInfo.getCpOrderCreateTime())?systemTime:ubOrderInfo.getCpOrderCreateTime();
		String payType="0";
		String productBody=TextUtil.isEmpty(ubOrderInfo.getGoodsName())?"":ubOrderInfo.getGoodsName();
		String productID=TextUtil.isEmpty(ubOrderInfo.getGoodsID())?"":ubOrderInfo.getGoodsID();
		String productSubject=TextUtil.isEmpty(ubOrderInfo.getGoodsDesc())?"":ubOrderInfo.getGoodsDesc();
		String totalPrice=TextUtil.isEmpty(ubOrderInfo.getAmount()+"")?"":ubOrderInfo.getAmount()+"";
		String userInfo=TextUtil.isEmpty(ubOrderInfo.getExtrasParams())?"":ubOrderInfo.getExtrasParams();
		
		TreeMap<String,String> treeMap = new TreeMap<String,String>();
		treeMap.put("app_id",appID);
		treeMap.put("cp_order_id", mCpOrderID);
		treeMap.put("create_time", cpOrderCreateTime);
		treeMap.put("pay_type", payType);//0 表示定额支付
		treeMap.put("product_body",productBody);
		treeMap.put("product_id", productID);
		treeMap.put("product_subject", productSubject);
		treeMap.put("total_price", totalPrice);
		treeMap.put("user_info", userInfo);
		
		String sign = UBMD5Util.MD5EncryptString(treeMap,":"+mMeiZuAppSecret).toLowerCase(Locale.getDefault());//魅族要求转成小写
		
		Bundle payInfo = new Bundle();
		payInfo.putString(MzPayParams.ORDER_KEY_ORDER_APPID, appID);//游戏id
		payInfo.putString(MzPayParams.ORDER_KEY_ORDER_ID,mCpOrderID);//订单id
		payInfo.putLong(MzPayParams.ORDER_KEY_CREATE_TIME,Long.parseLong(cpOrderCreateTime));//订单创建时间
		payInfo.putString(MzPayParams.ORDER_KEY_PAY_TYPE,payType);//支付方式，默认为0,定额支付
		payInfo.putString(MzPayParams.ORDER_KEY_PRODUCT_BODY,productBody);//商品名称
		payInfo.putString(MzPayParams.ORDER_KEY_PRODUCT_ID,productID);//商品id
		payInfo.putString(MzPayParams.ORDER_KEY_PRODUCT_SUBJECT,productSubject);//商品描述
		payInfo.putString(MzPayParams.ORDER_KEY_AMOUNT,totalPrice);//金额
		payInfo.putString(MzPayParams.ORDER_KEY_CP_INFO,userInfo);//cp自定义信息
		payInfo.putString(MzPayParams.ORDER_KEY_SIGN,sign);//签名
		
		payInfo.putString(MzPayParams.ORDER_KEY_SIGN_TYPE, "md5");//签名类型
		payInfo.putBoolean(MzPayParams.ORDER_KEY_DISABLE_PAY_TYPE_SMS,false);//是否关闭短信支付，默认是开启状态
//		payInfo.putString(MzPayParams.ORDER_KEY_PRE_SELECTED_PAYWAY, MzPreSelectedPayWay.PAY_BY_UNIONPAY);指定支付方式
		
		MzGameCenterPlatform.singlePay(mActivity, payInfo,mzPayListener);
	}
	
	MzPayListener mzPayListener=new MzPayListener() {
		
		@Override
		public void onPayResult(int code, Bundle info, String errorMsg) {
			String orderID = "";
			String goodsID = "";
			String goodsName= "";
			String goodsPrice ="";
			String extrasParams= "";
			
			if (info != null) {
				orderID = info.getString(MzPayParams.ORDER_KEY_ORDER_ID);
				goodsID = info.getString(MzPayParams.ORDER_KEY_PRODUCT_ID);
				goodsName=info.getString(MzPayParams.ORDER_KEY_PRODUCT_BODY);
				goodsPrice=info.getString(MzPayParams.ORDER_KEY_PER_PRICE);
				extrasParams=info.getString(MzPayParams.ORDER_KEY_CP_INFO);
			}
			if (code == PayResultCode.PAY_SUCCESS) {
				UBLogUtil.logI(TAG+"----->pay:success");
				UBSDK.getInstance().getUBPayCallback().onSuccess(mCpOrderID, orderID, goodsID, goodsName, goodsPrice, extrasParams);
				
			} else if (code == PayResultCode.PAY_USE_SMS) {
				
				UBLogUtil.logI(TAG+"----->pay:pay_user_sms");
				UBSDK.getInstance().getUBPayCallback().onFailed(mCpOrderID, "pay_user_sms", null);
			} else if (code == PayResultCode.PAY_ERROR_CANCEL) {
				UBLogUtil.logI(TAG+"----->pay:fail----->user cancel");
				UBSDK.getInstance().getUBPayCallback().onFailed(mCpOrderID, "user cancel", null);
				
			} else if (code == PayResultCode.PAY_ERROR_CODE_DUPLICATE_PAY) {
				UBLogUtil.logI(TAG+"----->pay:fail----->duplicate pay");
				UBSDK.getInstance().getUBPayCallback().onFailed(mCpOrderID, "duplicate pay",null);
				
			} else if (code == PayResultCode.PAY_ERROR_GAME_VERIFY_ERROR) {
				UBLogUtil.logI(TAG+"----->pay:fail----->game verify error");
				UBSDK.getInstance().getUBPayCallback().onFailed(mCpOrderID, "game verify error", null);
				
			} else {
				UBLogUtil.logI(TAG+"----->pay:fail----->unknown");
				UBSDK.getInstance().getUBPayCallback().onFailed(mCpOrderID, "unknown", null);
			}
		}
	};

	
	public void exit() {
		UBLogUtil.logI(TAG,"exit----->store noImplement");
		UBSDK.getInstance().getUBExitCallback().noImplement();
	}
	
	public void logout() {
		UBLogUtil.logI("logout:success----->simulation success");
		UBSDK.getInstance().getUBLogoutCallback().onSuccess();
	}
	
	public void setGameDataInfo(Object obj, int dataType) {
		UBLogUtil.logI(TAG,"setGameDataInfo----->simulation empty implementation");
	}
	
}
