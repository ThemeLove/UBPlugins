package com.ubsdk.lenovo.plugin;

import java.util.ArrayList;
import java.util.HashMap;

import com.iapppay.alpha.sdk.main.IAppPay;
import com.iapppay.alpha.sdk.main.IAppPayOrderUtils;
import com.ubsdk.lenovo.plugin.diy.PayDialog;
import com.ubsdk.lenovo.plugin.diy.PayMethodItem;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.model.UBPayConfigModel;
import com.umbrella.game.ubsdk.plugintype.pay.PayConfig;
import com.umbrella.game.ubsdk.plugintype.pay.PayType;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
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
		
		String mLenovoOrientation = UBSDKConfig.getInstance().getParamMap().get("Lenovo_Orientation");
		int screenType=TextUtil.equalsIgnoreCase(mLenovoOrientation,"portrait")?IAppPay.PORTRAIT:IAppPay.LANDSCAPE;
		IAppPay.init(mActivity,screenType, mLenovoAppID);
		
//		给出同步初始化成功回调
		UBSDK.getInstance().getUBInitCallback().onSuccess();
	}
	
	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
		
		HashMap<String, PayConfig> mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml");
		if (mPayConfigMap!=null&&TextUtils.isEmpty(ubOrderInfo.getGoodsID())) {
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
		}
		if (mPayConfig==null) {
			throw new RuntimeException("Lenovo store pay config error!!");
		}
		
		if (PayType.PAY_TYPE_DIY==mPayConfig.getPayType()) {
			ArrayList<PayMethodItem> payMethodItemList = mPayConfig.getPayMethodItemList();
			
			PayDialog payDialog = new PayDialog(mActivity);
			payDialog.setPayMethodItemList(payMethodItemList);
		}
		
		
		
		
		
		
		
//		IAppPay.startPay (mActivity, String params, IPayResultCallback callback , int payMethod);
		
	}
	

    /**
     * 获取收银台参数
     */
    private String getTransdata(String appuserid, String cpprivateinfo, int waresid, float price, String cporderid) {
        //调用 IAppPayOrderUtils getTransdata() 获取支付参数
        IAppPayOrderUtils orderUtils = new IAppPayOrderUtils();
        orderUtils.setAppid(mLenovoAppID);
        orderUtils.setWaresid(waresid);
        orderUtils.setCporderid(cporderid);
        orderUtils.setAppuserid(appuserid);
        orderUtils.setPrice(price);//单位 元
        orderUtils.setWaresname("自定义名称");//开放价格名称(用户可自定义，如果不传以后台配置为准)
        orderUtils.setCpprivateinfo(cpprivateinfo);
        orderUtils.setNotifyurl(PayConfig.notifyurl);
        return orderUtils.getTransdata(PayConfig.privateKey);
    }
}
