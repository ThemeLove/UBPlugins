package com.ubsdk.meizu.plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

import com.meizu.gamesdk.model.callback.MzPayListener;
import com.meizu.gamesdk.model.model.MzPayParams;
import com.meizu.gamesdk.model.model.PayResultCode;
import com.meizu.gamesdk.offline.core.MzGameCenterPlatform;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.model.UBPayConfigModel;
import com.umbrella.game.ubsdk.plugintype.pay.PayConfig;
import com.umbrella.game.ubsdk.plugintype.pay.PayType;
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
	private HashMap<String,PayConfig> mPayConfigMap;
	private PayConfig mPayConfig;//魅族支付配置
	
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
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo orderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml",true);
		if (mPayConfigMap!=null) {
			UBLogUtil.logI(TAG+"----->mPayConfigMap="+mPayConfigMap.toString());
			mPayConfig = mPayConfigMap.get(orderInfo.getGoodsID());
			UBLogUtil.logI(TAG+"----->payConfig="+mPayConfig.toString());
		}
		
//		UBLogUtil.logI(TAG+"----->pay----->param----->mMeiZuAppID="+mMeiZuAppID+",cpOrderID="+ubOrderInfo.getCpOrderID());
		
//		魅族渠道支付时三个参数不能为空 app_id,cp_order_id,sign_type
		String systemTime=System.currentTimeMillis()+"";
//		校验参数
		if (TextUtil.isEmpty(mMeiZuAppID)) {
			UBLogUtil.logE(TAG+"----->error----->MeiZuAppID必要参数为空");
			return;
		}
		
		if (mPayConfig==null) {
			throw new RuntimeException("meizu store pay config error!!");
		}
		
		if (mPayConfig.getPayType()==PayType.PAY_TYPE_NORMAL) {//正常支付
			orderInfo = mPayConfig.getOrderInfo();
			mCpOrderID=TextUtil.replaceBlank(orderInfo.getCpOrderID());
			if (TextUtil.isEmpty(orderInfo.getCpOrderID())) {
				UBLogUtil.logI(TAG+"----->warning----->cpOrderID 为空，使用系统时间代替！");
				mCpOrderID=systemTime;
			}
			
			String cpOrderCreateTime=TextUtil.isEmpty(orderInfo.getCpOrderCreateTime())?systemTime:orderInfo.getCpOrderCreateTime();
			String productBody=TextUtil.isEmpty(orderInfo.getGoodsName())?"":orderInfo.getGoodsName();
			String productID=TextUtil.isEmpty(orderInfo.getGoodsID())?"0":orderInfo.getGoodsID();
			String productSubject=TextUtil.isEmpty(orderInfo.getGoodsDesc())?"":orderInfo.getGoodsDesc();
			String totalPrice=TextUtil.isEmpty(orderInfo.getAmount()+"")?"":orderInfo.getAmount()+"";
			String userInfo=TextUtil.isEmpty(orderInfo.getExtrasParams())?"":orderInfo.getExtrasParams();

			cpOrderCreateTime = TextUtil.replaceBlank(cpOrderCreateTime);
			productBody = TextUtil.replaceBlank(productBody);
			productID = TextUtil.replaceBlank(productID);
			productSubject = TextUtil.replaceBlank(productSubject);
			totalPrice = TextUtil.replaceBlank(totalPrice);
			userInfo = TextUtil.replaceBlank(userInfo);
			
			String payType="0";
			TreeMap<String,String> treeMap = new TreeMap<String,String>();
			treeMap.put("app_id",mMeiZuAppID);
			treeMap.put("cp_order_id", mCpOrderID);
			treeMap.put("create_time", cpOrderCreateTime);
			treeMap.put("pay_type", payType);//0 表示定额支付
			treeMap.put("product_body",productBody);//默认为""
			treeMap.put("product_id", productID);//默认为"0"
			treeMap.put("product_subject", productSubject);//订单描述
			treeMap.put("total_price", totalPrice);
			treeMap.put("user_info", userInfo);
			
			String sign = UBMD5Util.MD5EncryptString(treeMap,":"+mMeiZuAppSecret).toLowerCase(Locale.getDefault());//魅族要求转成小写
			
			Bundle payInfo = new Bundle();
			payInfo.putString(MzPayParams.ORDER_KEY_ORDER_APPID, mMeiZuAppID);//游戏id,不能为空
			payInfo.putString(MzPayParams.ORDER_KEY_ORDER_ID,mCpOrderID);//订单id，不能为空
			payInfo.putLong(MzPayParams.ORDER_KEY_CREATE_TIME,Long.parseLong(cpOrderCreateTime));//订单创建时间
			payInfo.putString(MzPayParams.ORDER_KEY_PAY_TYPE,payType);//支付方式，默认为0,定额支付
			payInfo.putString(MzPayParams.ORDER_KEY_PRODUCT_BODY,productBody);//商品名称
			payInfo.putString(MzPayParams.ORDER_KEY_PRODUCT_ID,productID);//商品id
			payInfo.putString(MzPayParams.ORDER_KEY_PRODUCT_SUBJECT,productSubject);//商品描述
			payInfo.putString(MzPayParams.ORDER_KEY_AMOUNT,totalPrice);//金额
			payInfo.putString(MzPayParams.ORDER_KEY_CP_INFO,userInfo);//cp自定义信息
			payInfo.putString(MzPayParams.ORDER_KEY_SIGN,sign);//签名
			
			payInfo.putString(MzPayParams.ORDER_KEY_SIGN_TYPE, "md5");//签名类型，不能为空，不参与签名
			payInfo.putBoolean(MzPayParams.ORDER_KEY_DISABLE_PAY_TYPE_SMS,false);//是否关闭短信支付，默认是开启状态
//			payInfo.putString(MzPayParams.ORDER_KEY_PRE_SELECTED_PAYWAY, MzPreSelectedPayWay.PAY_BY_UNIONPAY);指定支付方式
			
			MzGameCenterPlatform.singlePay(mActivity, payInfo,mzPayListener);
			
			
		}else if(mPayConfig.getPayType()==PayType.PAY_TYPE_BILLING){//计费点支付
			
		}else if(mPayConfig.getPayType()==PayType.PAY_TYPE_DIY){//自定义支付
			
		}
		
		
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
