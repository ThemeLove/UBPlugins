package com.ubsdk.lenovo.plugin;

import java.util.ArrayList;
import java.util.HashMap;

import com.iapppay.alpha.interfaces.callback.IPayResultCallback;
import com.iapppay.alpha.sdk.main.IAppPay;
import com.iapppay.alpha.sdk.main.IAppPayOrderUtils;
import com.ubsdk.lenovo.plugin.diy.PayDialog;
import com.ubsdk.lenovo.plugin.diy.PayDialogClickListener;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.model.UBPayConfigModel;
import com.umbrella.game.ubsdk.plugintype.pay.PayConfig;
import com.umbrella.game.ubsdk.plugintype.pay.PayMethod;
import com.umbrella.game.ubsdk.plugintype.pay.PayType;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.pay.diy.PayMethodItem;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.TextUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.text.TextUtils;

public class LenovoSDK {
	private final String TAG=LenovoSDK.class.getSimpleName();
	private static LenovoSDK instance=null;
	private Activity mActivity;
	private String mLenovoAppID;
	private String mPrivateKey;//应用私钥
	private String mPublicKey;//平台公钥
	private PayConfig mPayConfig;//本次支付的支付配置
	private int mProductID;//商品ID
	private LenovoSDK(){}
	public static LenovoSDK getInstance(){
		if (instance==null) {
			if (instance==null) {
				instance=new LenovoSDK();
			}
		}
		return instance;
	}
	
	public void init(){
		UBLogUtil.logI(TAG+"----->init");
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		mLenovoAppID = UBSDKConfig.getInstance().getParamMap().get("Lenovo_AppID");
		mPrivateKey = UBSDKConfig.getInstance().getParamMap().get("Lenovo_PrivateKey");
		mPublicKey = UBSDKConfig.getInstance().getParamMap().get("Lenovo_PublicKey");
		mProductID = Integer.parseInt(UBSDKConfig.getInstance().getParamMap().get("Lenovo_Pay_ProductID"));
		
		String mLenovoOrientation = UBSDKConfig.getInstance().getParamMap().get("Lenovo_Orientation");
		int screenType=TextUtil.equalsIgnoreCase(mLenovoOrientation,"portrait")?IAppPay.PORTRAIT:IAppPay.LANDSCAPE;
		IAppPay.init(mActivity,screenType, mLenovoAppID);
		
//		给出同步初始化成功回调
		UBSDK.getInstance().getUBInitCallback().onSuccess();
	}
	

	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
		
		HashMap<String, PayConfig> mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml");
		if (mPayConfigMap!=null&&!TextUtils.isEmpty(ubOrderInfo.getGoodsID())) {
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
		}
		if (mPayConfig==null) {
			throw new RuntimeException("Lenovo store pay config error!!");
		}
		
		final String tm=System.currentTimeMillis()+"";
		
		final IPayResultCallback mPayResultCallback=new IPayResultCallback() {
				
				@Override
				public void onPayResult(int resultCode, String signvalue, String resultInfo) {
					UBLogUtil.logI(TAG+"----->onPayResult----->resultCode="+resultCode+",signvalue="+signvalue+",resultInfo="+resultInfo);
					switch (resultCode) {
		            case IAppPay.PAY_SUCCESS:
		                boolean isPaySuccess = IAppPayOrderUtils.checkPayResult(signvalue,mPublicKey);
		                if (isPaySuccess) {
		                   UBSDK.getInstance().getUBPayCallback().onSuccess(tm, tm, tm, mPayConfig.getProductName(), mPayConfig.getAmount()+"", mPayConfig.getProductName());
		                } else {
		                   UBSDK.getInstance().getUBPayCallback().onFailed(tm, resultInfo, null);
		                }
		                break;
		            case IAppPay.PAY_CANCEL:
		            	UBSDK.getInstance().getUBPayCallback().onCancel(tm);
		            	break;
		            case IAppPay.PAY_ERROR:
		            	UBSDK.getInstance().getUBPayCallback().onFailed(tm, resultInfo, null);
		            	break;
		            default:
		            	UBSDK.getInstance().getUBPayCallback().onFailed(tm, resultInfo, null);
		                break;
					}
				}
		};
			
		if (PayType.PAY_TYPE_DIY==mPayConfig.getPayType()) {
			ArrayList<PayMethodItem> payMethodItemList = mPayConfig.getPayMethodItemList();
			
			final PayDialog payDialog = new PayDialog(mActivity);
			payDialog.setPayMethodItemList(payMethodItemList);
			payDialog.updatePayInfoStatus(mPayConfig.getProductName(),mPayConfig.getAmount());
			payDialog.setPayDialogClickListener(new PayDialogClickListener() {
				
				@Override
				public void onPay(PayMethodItem payMethodItem) {
					
			        //调用 IAppPayOrderUtils getTransdata() 获取支付参数
			        IAppPayOrderUtils orderUtils = new IAppPayOrderUtils();
			        orderUtils.setAppid(mLenovoAppID);
			        orderUtils.setWaresid(mProductID);//爱贝后台申请的商品编号,可能对应计费点
			        orderUtils.setWaresname(mPayConfig.getProductName());//商品名称，可选(用户可自定义，如果不传以后台配置为准)
			        orderUtils.setCporderid(tm);//商户订单号
			        orderUtils.setAppuserid("");//用户名
			        orderUtils.setPrice((float)mPayConfig.getAmount());//单位 元,可选
			        orderUtils.setCpprivateinfo(mPayConfig.getProductName());//透传，可选
//			        orderUtils.setNotifyurl(PayConfig.notifyurl);//回调地址，可选
			        
			        String transdata = orderUtils.getTransdata(mPrivateKey);
			        
			        if (PayMethod.ALIPAY==payMethodItem.getID()) {
			        	IAppPay.startPay (mActivity,transdata,mPayResultCallback,IAppPay.PAY_METHOD_ALIPAY);
					}else if(PayMethod.WEIXING==payMethodItem.getID()){
						IAppPay.startPay (mActivity,transdata,mPayResultCallback,IAppPay.PAY_METHOD_WECHATPAY);
					}else if(PayMethod.UNION==payMethodItem.getID()){
						IAppPay.startPay (mActivity,transdata,mPayResultCallback,IAppPay.PAY_METHOD_UPPAY);
					}
				}
				
				@Override
				public void onClose() {
					payDialog.dismiss();
					UBSDK.getInstance().getUBPayCallback().onCancel(tm);
				}
			});
		}
		
	}

}
