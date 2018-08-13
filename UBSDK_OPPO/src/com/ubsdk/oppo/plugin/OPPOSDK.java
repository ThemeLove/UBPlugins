package com.ubsdk.oppo.plugin;

import java.util.HashMap;

import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.callback.SinglePayCallback;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import com.nearme.platform.opensdk.pay.PayResponse;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBGamePauseCallback;
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

public class OPPOSDK {
	private final String TAG=OPPOSDK.class.getSimpleName();
	private static OPPOSDK instance=null;
	private Activity mActivity;
	private int mOPPOPayRate;//oppo支付比例
	private boolean mIsShowCpmsChannel=false;//是否显示运营商短代支付方式
	private boolean mIsUseCachedChannel=true;//是否记住上次支付方式
	private HashMap<String, PayConfig> mPayConfigMap;
	private PayConfig mPayConfig;//本次支付的支付配置
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
		mOPPOPayRate =Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("OPPO_Pay_Rate"));
		mIsShowCpmsChannel = Boolean.parseBoolean(UBSDKConfig.getInstance().getParamMap().get("OPPO_IsShowCpSmsChannel"));
		mIsUseCachedChannel = Boolean.parseBoolean(UBSDKConfig.getInstance().getParamMap().get("OPPO_IsUseCachedChannel"));
		
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
	
	public void login() {
		UBLogUtil.logI(TAG+"----->login");
		UBUserInfo ubUserInfo = new UBUserInfo();
		ubUserInfo.setUid("123456");
		ubUserInfo.setUserName("ubsdktest");
		ubUserInfo.setToken("123456ABCDEFG");
		ubUserInfo.setExtra("extra");
//		成功回调
		UBSDK.getInstance().getUBLoginCallback().onSuccess(ubUserInfo);
	}

	public void logout() {
		UBLogUtil.logI(TAG+"----->logout");
		UBSDK.getInstance().getUBLogoutCallback().onSuccess();
	}
	
	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
		mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml",true);
		if (mPayConfigMap!=null&&!TextUtil.isEmpty(ubOrderInfo.getGoodsID())) {
			UBLogUtil.logI(TAG+"----->mPayConfigMap="+mPayConfigMap.toString());
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
			UBLogUtil.logI(TAG+"----->payConfig="+mPayConfig.toString());
		}
		
		if (mPayConfig==null) {
			throw new RuntimeException("OPPO store pay config error!!");
		}
		
		final String tm=System.currentTimeMillis()+"";
		
		if (PayType.PAY_TYPE_NORMAL==mPayConfig.getPayType()) {
//			三个参数分别为订单号，扩展参数，金额
			int oppoAmount=(int) (mPayConfig.getAmount()*mOPPOPayRate);
			PayInfo payInfo = new PayInfo(tm,mPayConfig.getProductName(),Integer.parseInt(oppoAmount+""));
			payInfo.setProductName(mPayConfig.getProductName());
			payInfo.setProductDesc(mPayConfig.getOrderInfo().getGoodsDesc());
			payInfo.setShowCpSmsChannel(mIsShowCpmsChannel);
			payInfo.setUseCachedChannel(mIsUseCachedChannel);
//			payInfo.setCallbackUrl("");不通过服务器发货不用填写
			GameCenterSDK.getInstance().doSinglePay(mActivity, payInfo, new SinglePayCallback() {
				
				@Override
				public void onSuccess(String msg) {
					UBLogUtil.logI(TAG+"----->pay:onSuccess----->msg="+msg);
					UBSDK.getInstance().getUBPayCallback().onSuccess(tm, tm, mPayConfig.getProductID(), mPayConfig.getProductName(), mPayConfig.getAmount()+"", mPayConfig.getProductName());
				}
				
				@Override
				public void onFailure(String msg, int code) {
					if (PayResponse.CODE_CANCEL== code) {
						UBLogUtil.logI(TAG+"----->pay:cancel----->msg="+msg);
						UBSDK.getInstance().getUBPayCallback().onCancel(tm);
					}else{
						UBLogUtil.logI(TAG+"----->pay:failed----->msg="+msg);
						UBSDK.getInstance().getUBPayCallback().onFailed(tm, msg, null);
					}
				}
				
				@Override
				public void onCallCarrierPay(PayInfo payInfo, boolean bySelectSMSPay) {
					UBLogUtil.logI(TAG+"----->onCallCarrierPay");
//					TODO运营商支付逻辑
				}
			});
		}
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

	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePause");
		UBGamePauseCallback ubGamePauseCallback = UBSDK.getInstance().getUBGamePauseCallback();
		if (ubGamePauseCallback!=null) {
			ubGamePauseCallback.onGamePause();
		}
	}
}
