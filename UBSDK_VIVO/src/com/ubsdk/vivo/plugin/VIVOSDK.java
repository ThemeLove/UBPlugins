package com.ubsdk.vivo.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.http.UBHttpManager;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.ui.UBLoadingDialog;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
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
	private String mVIVO_StoreId;
	private String mVIVO_AppId;
	private UBLoadingDialog mUBLoadingDialog;
	public void init(){
		mVIVO_StoreId = UBSDKConfig.getInstance().getParamMap().get("VIVO_StoreID");
		mVIVO_AppId = UBSDKConfig.getInstance().getParamMap().get("VIVO_AppID");
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
	}
	public void logout() {
		UBLogUtil.logI(TAG+"----->logout");
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
		String VIVOOrderUrl="https://pay.vivo.com.cn/vivoPay/getVivoOrderNum";
		int VIVORequestWhat=1;
		Request<String> VIVOOrderRequest = NoHttp.createStringRequest(VIVOOrderUrl,RequestMethod.POST);
        //订单推送接口请在服务器端访问
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("notifyUrl",ubOrderInfo.getCallbackUrl());//回调地址
        params.put("orderAmount",ubOrderInfo.getAmount()+"");  //注意：精确到小数点后两位；
        params.put("orderDesc",ubOrderInfo.getGoodsDesc());
        params.put("orderTitle",ubOrderInfo.getGoodsName());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        params.put("orderTime",format.format(new Date()));//订单创建时间
        params.put("storeId",mVIVO_StoreId);//商户ID
        params.put("appId", mVIVO_AppId);                  //APPID
//        params.put("storeOrder", UUID.randomUUID().toString().replaceAll("-", ""));//商户订单号
        params.put("storeOrder",ubOrderInfo.getOrderID());//商户订单号
        params.put("version", "1.0");
        String str = VivoSignUtils.getVivoSign(params,mVIVO_StoreId);//signkey
        params.put("signature", str);
        params.put("signMethod", "MD5");
		VIVOOrderRequest.add(params);
		
		UBHttpManager.getInstance().addRequest(VIVORequestWhat, VIVOOrderRequest, new OnResponseListener<String>() {

			@Override
			public void onFailed(int what, String arg1, Object arg2, Exception e, int arg4, long arg5) {
				UBSDK.getInstance().getUBPayCallback().onFailed(ubOrderInfo.getCpOrderID(), "创建订单失败！", null);
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
						JSONObject jsob = new JSONObject(response.get());
						String orderAmount = jsob.optString("orderAmount");
						String vivoSignature = jsob.optString("vivoSignature");
						String vivoOrder = jsob.optString("vivoOrder");
						ubOrderInfo.setOrderID(vivoOrder);
						VivoPayInfo vivoPayInfo = new VivoPayInfo(ubOrderInfo.getGoodsName(),ubOrderInfo.getGoodsDesc(),orderAmount,vivoSignature, mVIVO_AppId,vivoOrder, null);
						VivoUnionSDK.pay(mActivity, vivoPayInfo, new VivoPayCallback() {
							@Override
							public void onVivoPayResult(String transNo, boolean isSuccess, String errorCode) {
								if (isSuccess) {
									UBSDK.getInstance().getUBPayCallback().onSuccess(ubOrderInfo.getCpOrderID(),ubOrderInfo.getOrderID(),"", "","",ubOrderInfo.getExtrasParams());
								}else{
									UBSDK.getInstance().getUBPayCallback().onFailed(ubOrderInfo.getCpOrderID(), errorCode, null);
								}
							}
						});
					}
				} catch (Exception e) {
					
				}
			}
		});
		
	}
}
