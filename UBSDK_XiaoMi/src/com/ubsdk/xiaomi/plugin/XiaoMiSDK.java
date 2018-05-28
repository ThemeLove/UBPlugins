package com.ubsdk.xiaomi.plugin;

import java.util.HashMap;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.model.UBPayConfigModel;
import com.umbrella.game.ubsdk.plugintype.pay.PayConfig;
import com.umbrella.game.ubsdk.plugintype.pay.PayMethod;
import com.umbrella.game.ubsdk.plugintype.pay.PayType;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.pay.diy.PayDialog;
import com.umbrella.game.ubsdk.plugintype.pay.diy.PayDialogClickListener;
import com.umbrella.game.ubsdk.plugintype.pay.diy.PayMethodItem;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.xiaomi.hy.dj.HyDJ;
import com.xiaomi.hy.dj.PayResultCallback;
import com.xiaomi.hy.dj.purchase.FeePurchase;

import android.app.Activity;

public class XiaoMiSDK {
	private final String TAG=XiaoMiSDK.class.getSimpleName();
	private static XiaoMiSDK instance=null;
	private HashMap<String, PayConfig> mPayConfigMap;
	private Activity mActivity;
	private PayConfig mPayConfig;//当前购买的支付配置
	private XiaoMiSDK (){}
	public static XiaoMiSDK getInstance(){
		if (instance==null) {
			if (instance==null) {
				instance=new XiaoMiSDK();
			}
		}
		return instance;
	}
	
	public void init(){
		UBLogUtil.logI(TAG+"----->init");
		
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml");
		
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
		UBLogUtil.logI(TAG+"----->pay");
		if (mPayConfigMap!=null) {
			UBLogUtil.logI(TAG+"----->mPayConfigMap="+mPayConfigMap.toString());
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
			UBLogUtil.logI(TAG+"----->payConfig="+mPayConfig.toString());
		}
		if (mPayConfig==null) {
			throw new RuntimeException("xiaomi store pay config error!!");
		}
		
//		
		final String systemTime=System.currentTimeMillis()+"";
		if (mPayConfig.getPayType()==PayType.PAY_TYPE_DIY) {
			final PayDialog payDialog = new PayDialog(mActivity);
			payDialog.setPayMethodItemList(mPayConfig.getPayMethodItemList());
			payDialog.updatePayInfoStatus("XiaoMi", "",mPayConfig.getProductName(),mPayConfig.getAmount());
			payDialog.setPayDialogClickListener(new PayDialogClickListener() {
				
				@Override
				public void onPay(PayMethodItem payMethodItem) {
					UBLogUtil.logI(TAG+"----->diy dialog onPay----->payMethod="+payMethodItem.getName());
					payDialog.dismiss();
					
					FeePurchase feePurchase = new FeePurchase();
					feePurchase.setCpOrderId(systemTime);
					feePurchase.setFeeValue(mPayConfig.getAmount()+"");
					feePurchase.setDisplayName(mPayConfig.getProductName());
					
					PayResultCallback payResultCallback = new PayResultCallback() {
						
						@Override
						public void onSuccess(String cpOrderID) {
							UBLogUtil.logI(TAG+"----->paySuccess:cpOrderID="+cpOrderID);
							UBSDK.getInstance().getUBPayCallback().onSuccess(cpOrderID, cpOrderID, mPayConfig.getProductID(), mPayConfig.getProductName(), mPayConfig.getAmount()+"", "ext");
						}
						
						@Override
						public void onError(int code, String msg) {
							UBLogUtil.logI(TAG+"----->payFail:code="+code+",msg="+msg);
							UBSDK.getInstance().getUBPayCallback().onFailed(systemTime, msg, null);
						}
					};
					
					if (payMethodItem.getID()==PayMethod.ALIPAY) {
						HyDJ.getInstance().aliPay(mActivity, feePurchase, payResultCallback);
					}else if(payMethodItem.getID()==PayMethod.WEIXING){
						HyDJ.getInstance().wxPay(mActivity, feePurchase, payResultCallback);
					}else if(payMethodItem.getID()==PayMethod.QQ){
						HyDJ.getInstance().qqPay(mActivity, feePurchase, payResultCallback);
					}
				}
				
				@Override
				public void onClose() {
					UBLogUtil.logI(TAG+"----->diy dialog close click!");
					payDialog.dismiss();
					UBSDK.getInstance().getUBPayCallback().onCancel(systemTime);
				}
			});
		}
		
	}
}
