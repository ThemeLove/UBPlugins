package com.ubsdk.huawei.plugin;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.CheckUpdateHandler;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.game.handler.LoginHandler;
import com.huawei.android.hms.agent.pay.PaySignUtil;
import com.huawei.android.hms.agent.pay.handler.GetProductDetailsHandler;
import com.huawei.android.hms.agent.pay.handler.PayHandler;
import com.huawei.hms.support.api.entity.game.GameUserData;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.entity.pay.ProductDetailRequest;
import com.huawei.hms.support.api.pay.PayResultInfo;
import com.huawei.hms.support.api.pay.ProductDetailResult;
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

import android.app.Activity;

public class HuaWeiSDK {
	private final String TAG=HuaWeiSDK.class.getSimpleName();
	
	private static HuaWeiSDK instance=null;

	private Activity mActivity;

	private HashMap<String,PayConfig> mPayConfigMap;

	private PayConfig mPayConfig;

	private String mHuaWeiAppID;

	private String mHuaWeiCPID;

	private String mHuaWeiPayPrivateKey;

	private String mHuaWeiDevelopName;

	private String mHuaWeiPayPublicKey;
	
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
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		mHuaWeiAppID = UBSDKConfig.getInstance().getParamMap().get("HuaWei_AppID");
		mHuaWeiCPID = UBSDKConfig.getInstance().getParamMap().get("HuaWei_CPID");
		mHuaWeiPayPublicKey = UBSDKConfig.getInstance().getParamMap().get("HuaWei_PayPublicKey");
		mHuaWeiPayPrivateKey = UBSDKConfig.getInstance().getParamMap().get("HuaWei_PayPrivateKey");
		mHuaWeiDevelopName = UBSDKConfig.getInstance().getParamMap().get("HuaWei_DevelopName");
		
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){
			@Override
			public void onResume() {
				UBLogUtil.logI(TAG+"----->onResume");
				HMSAgent.Game.showFloatWindow(mActivity);
			}
			@Override
			public void onPause() {
				UBLogUtil.logI(TAG+"----->onPause");
				HMSAgent.Game.hideFloatWindow(mActivity);
			}
		});
		
		HMSAgent.connect(mActivity, new ConnectHandler() {
		    @Override
		    public void onConnect(int rst) {
		    	UBLogUtil.logI(TAG+"---->connect-rst="+rst);
		    	
		        HMSAgent.checkUpdate(mActivity, new CheckUpdateHandler() {
		            @Override
		            public void onResult(int rst) {
		            	UBLogUtil.logI(TAG+"---->checkUpdate-rst="+rst);
		            }
		        });
		    }
		});    
		
	}
	
	public void login(){
		UBLogUtil.logI(TAG+"----->login");
		
		UBUserInfo ubUserInfo = new UBUserInfo();
		ubUserInfo.setUid("123456");
		ubUserInfo.setUserName("ubsdktest");
		ubUserInfo.setToken("123456ABCDEFG");
		ubUserInfo.setExtra("extra");
		UBSDK.getInstance().getUBLoginCallback().onSuccess(ubUserInfo);
		
		HMSAgent.Game.login(new LoginHandler() {
			
			@Override
			public void onResult(int code, GameUserData userData) {
				UBLogUtil.logI(TAG+"----->huawei-login----->onResult:code="+code+",userData="+userData);
				if (code==HMSAgent.AgentResultCode.HMSAGENT_SUCCESS && userData != null){
					String huaweiUserID = userData.getPlayerId();
					String huaweiUserName = userData.getDisplayName();
					String token = userData.getGameAuthSign();
					
					if (userData.getIsAuth()==1) {//登录成功的第二次回调,可以做登录验签
						
					}
				}else{//登录失败
					
				}
			}
			
			@Override
			public void onChange() {//此处帐号登录发生变化，需要重新登录
				login();
			}
		}, 1);
	}
	
	public void logout(){
		UBLogUtil.logI(TAG+"----->logout");
	}
	
	public void exit(){
		UBLogUtil.logI(TAG+"----->exit");
	}

	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml",false);
		if (mPayConfigMap!=null&&!TextUtil.isEmpty(ubOrderInfo.getGoodsID())) {
			UBLogUtil.logI(TAG+"----->mPayConfigMap="+mPayConfigMap.toString());
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
			UBLogUtil.logI(TAG+"----->payConfig="+mPayConfig.toString());
		}
		
		if (mPayConfig==null) {
			throw new RuntimeException("OPPO store pay config error!!");
		}
		
		if (PayType.PAY_TYPE_NORMAL==mPayConfig.getPayType()) {
			
		    /**
		     * 生成requestId,订单唯一标示
		     */
		    DateFormat format = new java.text.SimpleDateFormat("yyyyMMddhhmmssSSS");
		    int random= new SecureRandom().nextInt() % 100000;
		    random = random < 0 ? -random : random;
		    String requestId = format.format(new Date());
		    final String orderID= String.format("%s%05d", requestId, random);
			
			PayReq createPayReq = HuaWeiPayUtil.createPayReq(orderID,(float)mPayConfig.getAmount(), mPayConfig.getProductName(), mPayConfig.getOrderInfo().getGoodsDesc(), mHuaWeiCPID, mHuaWeiAppID, mHuaWeiDevelopName, mPayConfig.getOrderInfo().getExtrasParams(), mHuaWeiPayPrivateKey);
			HMSAgent.Pay.pay(createPayReq, new PayHandler() {

				@Override
				public void onResult(int code, PayResultInfo payResult) {
					UBLogUtil.logI(TAG+"----->code");
					if (code==HMSAgent.AgentResultCode.HMSAGENT_SUCCESS && payResult != null) {//支付成功
						boolean checkSign = PaySignUtil.checkSign(payResult, mHuaWeiPayPublicKey);
						if (checkSign) {//支付成功且验签成功，可以发货
							UBSDK.getInstance().getUBPayCallback().onSuccess(orderID, orderID, mPayConfig.getProductID(), mPayConfig.getProductName(),mPayConfig.getAmount()+"", mPayConfig.getOrderInfo().getExtrasParams());
						}else{//支付成功，但是验签失败，要调用订单查询接口
							queryRequest(mHuaWeiAppID, mHuaWeiCPID, orderID,mPayConfig.getProductID(),mPayConfig.getProductName(),mPayConfig.getAmount()+"",mPayConfig.getOrderInfo().getExtrasParams());
						}
					}else if(code==HMSAgent.AgentResultCode.ON_ACTIVITY_RESULT_ERROR
			                || code == PayStatusCodes.PAY_STATE_TIME_OUT
			                || code == PayStatusCodes.PAY_STATE_NET_ERROR){
						queryRequest(mHuaWeiAppID, mHuaWeiCPID, orderID,mPayConfig.getProductID(),mPayConfig.getProductName(),mPayConfig.getAmount()+"",mPayConfig.getOrderInfo().getExtrasParams());
//						订单查询
					}else{//其他错误码
						UBSDK.getInstance().getUBPayCallback().onFailed(orderID,payResult.getErrMsg(),null);
					}
				}
			});
			
		}
	}
	
	/**
	 * 查询订单接口
	 * @param huaWeiAppID
	 * @param huaWeiCPID
	 * @param orderID
	 * @param productID
	 * @param productName
	 * @param productPrice
	 * @param extrasParams
	 */
	private void queryRequest(String huaWeiAppID,String huaWeiCPID,final String orderID,final String productID,final String productName,final String productPrice,final String extrasParams){
		ProductDetailRequest queryRequest = HuaWeiPayUtil.createQueryRequest(huaWeiAppID, huaWeiCPID, orderID,productID);
		HMSAgent.Pay.getProductDetails(queryRequest, new GetProductDetailsHandler() {
			
			@Override
			public void onResult(int code, ProductDetailResult result) {
				if (code==PayStatusCodes.PAY_STATE_SUCCESS&&result!=null) {//支付成功,给出支付成功回调
//					TODO
					UBSDK.getInstance().getUBPayCallback().onSuccess(orderID, orderID, productID, productName, productPrice, extrasParams);
				}else{//补单失败
					UBSDK.getInstance().getUBPayCallback().onFailed(orderID,"query fail",null);
				}
			}
		} );
	}

	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePauser");
		
	}
	
}
