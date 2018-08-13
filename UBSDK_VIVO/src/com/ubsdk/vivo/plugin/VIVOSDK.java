package com.ubsdk.vivo.plugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.http.UBHttpManager;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.listener.UBHttpListener;
import com.umbrella.game.ubsdk.model.UBPayConfigModel;
import com.umbrella.game.ubsdk.plugintype.pay.PayConfig;
import com.umbrella.game.ubsdk.plugintype.pay.PayMethod;
import com.umbrella.game.ubsdk.plugintype.pay.PayType;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.pay.diy.PayDialog;
import com.umbrella.game.ubsdk.plugintype.pay.diy.PayDialogClickListener;
import com.umbrella.game.ubsdk.plugintype.pay.diy.PayMethodItem;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;
import com.umbrella.game.ubsdk.ui.UBLoadingDialog;
import com.umbrella.game.ubsdk.utils.TextUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.vivo.unionsdk.open.VivoConstants;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoPayCallback;
import com.vivo.unionsdk.open.VivoPayInfo;
import com.vivo.unionsdk.open.VivoUnionSDK;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import android.app.Activity;

public class VIVOSDK {
	private final String TAG=VIVOSDK.class.getSimpleName();
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
	private String mVIVO_CPID;
	private String mVIVO_AppID;
	private UBLoadingDialog mUBLoadingDialog;
	private PayConfig mPayConfig;//本次支付的支付配置
	public void init(){
		mVIVO_CPID = UBSDKConfig.getInstance().getParamMap().get("VIVO_CPID");
		mVIVO_AppID = UBSDKConfig.getInstance().getParamMap().get("VIVO_AppID");
		mVIVO_AppKey = UBSDKConfig.getInstance().getParamMap().get("VIVO_AppKey");
		mVIVOPayVersion = UBSDKConfig.getInstance().getParamMap().get("VIVO_Pay_Version");
		mActivity=UBSDKConfig.getInstance().getGameActivity();
		mUBLoadingDialog = new UBLoadingDialog(mActivity, "订单创建中...");
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
		
		UBSDK.getInstance().getUBInitCallback().onSuccess();//给出同步成功回调
	}
	
	public void login() {
		UBLogUtil.logI(TAG+"----->login");
//		模拟登录成功！
		UBUserInfo ubUserInfo = new UBUserInfo();
		ubUserInfo.setUid("123456");
		ubUserInfo.setUserName("ubsdktest");
		ubUserInfo.setToken("123456ABCDEFG");
		ubUserInfo.setExtra("extra");
		UBSDK.getInstance().getUBLoginCallback().onSuccess(ubUserInfo);
	}
	
	public void logout() {
		UBLogUtil.logI(TAG+"----->logout");
		UBSDK.getInstance().getUBLogoutCallback().onSuccess();
	}
	
	public void setGameDataInfo(Object obj, int dataType) {
		UBLogUtil.logI(TAG+"----->setGameDataInfo");
	}
	
	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");
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
	
	public void pay(UBRoleInfo ubRoleInfo, final UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		HashMap<String, PayConfig> mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml",true);
		if (mPayConfigMap!=null&&!TextUtil.isEmpty(ubOrderInfo.getGoodsID())) {
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
		}
		if (mPayConfig==null) {
			throw new RuntimeException("vivo store pay config error!");
		}
		final String tm=System.currentTimeMillis()+"";//系统时间用作商户订单号
		if (PayType.PAY_TYPE_DIY==mPayConfig.getPayType()) {//vivo是自定义支付
			ArrayList<PayMethodItem> payMethodItemList = mPayConfig.getPayMethodItemList();
			payDialog = new PayDialog(mActivity);
			payDialog.setPayMethodItemList(payMethodItemList);
			payDialog.updatePayInfoStatus("VIVO", "",mPayConfig.getProductName(),mPayConfig.getAmount());
			payDialog.setPayDialogClickListener(new PayDialogClickListener() {
				
				@Override
				public void onPay(final PayMethodItem payMethodItem) {
					
					getVIVOOrder(mPayConfig,tm,new UBHttpListener<VivoPayInfo>() {

						@Override
						public void onSuccess(VivoPayInfo vivoPayInfo) {
							UBLogUtil.logI(TAG+"----->vivoPayInfo="+vivoPayInfo);
							if (PayMethod.WEIXING==payMethodItem.getID()) {
								VivoUnionSDK.payNow(mActivity, vivoPayInfo, vivoPayCallback, VivoConstants.SINGLE_FRONT_PAY_WEIXIN);
							}else if(PayMethod.ALIPAY==payMethodItem.getID()){
								VivoUnionSDK.payNow(mActivity, vivoPayInfo, vivoPayCallback, VivoConstants.SINGLE_FRONT_PAY_ALI);
							}
						}

						@Override
						public void onFailed(String errorMsg) {
							UBSDK.getInstance().getUBPayCallback().onFailed(tm, "get vivo orderInfo error!",null);
							payDialog.dismiss();
						}
					});
				}
				
				@Override
				public void onClose() {
					UBSDK.getInstance().getUBPayCallback().onCancel(tm);
					payDialog.dismiss();
				}
			});
		}
	}
	
	VivoPayCallback vivoPayCallback = new VivoPayCallback() {
		
		@Override
		public void onVivoPayResult(final String transNo,final boolean isSuccess,final String errorCode) {
			UBLogUtil.logI(TAG+"----->vivoPay----->transNo="+transNo+",isSuccess="+isSuccess+",errorCode="+errorCode);
			UBSDK.getInstance().runOnUIThread(new Runnable() {
				@Override
				public void run() {
					payDialog.dismiss();
					if (isSuccess) {
						UBSDK.getInstance().getUBPayCallback().onSuccess(transNo, transNo, mPayConfig.getProductID(), mPayConfig.getProductName(), mPayConfig.getAmount()+"", mPayConfig.getProductName());
					}else{
						UBSDK.getInstance().getUBPayCallback().onFailed(transNo, errorCode, null);
					}
				}
			});
		}
	};
	private String mVIVO_AppKey;
	private String mVIVOPayVersion;
	private PayDialog payDialog;
	/**
	 * 从VIVO服务器交互获取订单信息
	 * @param payConfig
	 * @param tm
	 * @return
	 */
	private void  getVIVOOrder(PayConfig payConfig,final String tm,final UBHttpListener<VivoPayInfo> ubHttpListener){
		String VIVOOrderUrl="https://pay.vivo.com.cn/vivoPay/getVivoOrderNum";
		int VIVORequestWhat=1;
		Request<String> VIVOOrderRequest = NoHttp.createStringRequest(VIVOOrderUrl,RequestMethod.POST);
	    //订单推送接口请在服务器端访问
	    final HashMap<String, String> params = new HashMap<String, String>();
	    params.put("notifyUrl","");//回调地址
	    params.put("orderAmount",Float.parseFloat(mPayConfig.getAmount()+"")+"");  //注意：精确到小数点后两位；
	    params.put("orderDesc",mPayConfig.getProductName());
	    params.put("orderTitle",mPayConfig.getProductName());
	    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	    params.put("orderTime",format.format(new Date()));//订单创建时间
	    params.put("storeId",mVIVO_CPID);//商户ID
	    params.put("appId", mVIVO_AppID);                  //APPID
//	    params.put("storeOrder", UUID.randomUUID().toString().replaceAll("-", ""));//商户订单号
	    params.put("storeOrder",tm);//商户订单号
	    params.put("version", mVIVOPayVersion);
	    final String signStr = VivoSignUtils.getVivoSign(params,mVIVO_AppKey);//signkey
	    params.put("signature", signStr);
	    params.put("signMethod", "MD5");
	    UBLogUtil.logI(TAG+"----->params="+params);
		VIVOOrderRequest.add(params);
//		UBLogUtil.logI(TAG+"----->signStr="+signStr);
//		boolean verifySignature = VivoSignUtils.verifySignature(params, mVIVO_AppKey);
//		UBLogUtil.logI(TAG+"----->验证签名="+verifySignature);
		
		UBHttpManager.getInstance().addRequest(VIVORequestWhat, VIVOOrderRequest, new OnResponseListener<String>() {

			@Override
			public void onFailed(int what, String arg1, Object arg2, Exception e, int arg4, long arg5) {
				UBSDK.getInstance().getUBPayCallback().onFailed(tm, "创建订单失败！", null);
				if (ubHttpListener!=null) {
					ubHttpListener.onFailed("create vivo order failed!");
				}
			}

			@Override
			public void onFinish(int what) {
				mUBLoadingDialog.dismiss();
			}

			@Override
			public void onStart(int what) {
				mUBLoadingDialog.show();
			}

			@Override
			public void onSucceed(int what, Response<String> response) {
				try {
					if (response.getHeaders().getResponseCode()==200) {
						UBLogUtil.logI(TAG+"----->response.get()="+response.get());
						JSONObject jsob = new JSONObject(response.get());
						String orderAmount = jsob.optString("orderAmount");
						String vivoSignature = jsob.optString("vivoSignature");
//						String signature = jsob.optString("signature");
						String vivoOrder = jsob.optString("vivoOrder");
						VivoPayInfo vivoPayInfo = new VivoPayInfo(mPayConfig.getProductName(),mPayConfig.getProductName(),orderAmount,vivoSignature, mVIVO_AppID,vivoOrder, null);
						if (ubHttpListener!=null&&ubHttpListener!=null) {
							ubHttpListener.onSuccess(vivoPayInfo);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (ubHttpListener!=null) {
						ubHttpListener.onFailed("create vivo order :parse error!");
					}
				}
			}
		});
	}
}
