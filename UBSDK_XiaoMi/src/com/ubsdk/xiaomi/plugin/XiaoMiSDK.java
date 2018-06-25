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
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.xiaomi.hy.dj.HyDJ;
import com.xiaomi.hy.dj.PayResultCallback;
import com.xiaomi.hy.dj.purchase.RepeatPurchase;
import com.xiaomi.hy.dj.purchase.UnrepeatPurchase;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

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
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){
			@Override
			public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			            //当前系统大于等于6.0
			            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			                UBLogUtil.logI(TAG+"----->具有读写权限");
			            } else {
			                //不具有读写权限，需要进行权限申请
			                ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
			            }
			        } else {
			        	UBLogUtil.logI(TAG+"----->具有读写权限");
			        }
			}
			
			@Override
			public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
				super.onRequestPermissionResult(requestCode, permissions, grantResults);
				if (requestCode == 1001) {
		            if (grantResults.length >= 1) {
		                int sdcardResult = grantResults[0];//读写sdcard权限
		                boolean sdCardGranted = sdcardResult == PackageManager.PERMISSION_GRANTED;
		                if (sdCardGranted) {
		                	UBLogUtil.logI(TAG+"----->具有读写权限");
		                } else {
		                	UBLogUtil.logI(TAG+"----->申请读写权限失败！");
		                }
		            }
		        }
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
	
	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
		mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml");
		if (mPayConfigMap!=null) {
			UBLogUtil.logI(TAG+"----->mPayConfigMap="+mPayConfigMap.toString());
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
			UBLogUtil.logI(TAG+"----->payConfig="+mPayConfig.toString());
		}
		if (mPayConfig==null) {
			throw new RuntimeException("xiaomi store pay config error!!");
		}
		
		final String tm=System.currentTimeMillis()+"";
		if (mPayConfig.getPayType()==PayType.PAY_TYPE_DIY) {
			final PayDialog payDialog = new PayDialog(mActivity);
			payDialog.setPayMethodItemList(mPayConfig.getPayMethodItemList());
			payDialog.updatePayInfoStatus("XiaoMi", "",mPayConfig.getProductName(),mPayConfig.getAmount());
			payDialog.setPayDialogClickListener(new PayDialogClickListener() {
				
				@Override
				public void onPay(PayMethodItem payMethodItem) {
					UBLogUtil.logI(TAG+"----->diy dialog onPay----->payMethod="+payMethodItem.getName());
					
			        UnrepeatPurchase purchase =new UnrepeatPurchase();
			        purchase.setCpOrderId(tm);
			        purchase.setChargeCode(mPayConfig.getBilling().getBillingID());//设置计费点
			        
/*			        RepeatPurchase purchase =new RepeatPurchase();
			        purchase.setCpOrderId(tm);
			        purchase.setChargeCode(mPayConfig.getBilling().getBillingID());//设置计费点
*/					
					PayResultCallback payResultCallback = new PayResultCallback() {
						
						@Override
						public void onSuccess(final String xiaomiOrderID) {
							
							UBSDK.getInstance().runOnUIThread(new Runnable() {
								
								@Override
								public void run() {
									payDialog.dismiss();
									UBLogUtil.logI(TAG+"----->paySuccess:cpOrderID="+xiaomiOrderID);
									UBSDK.getInstance().getUBPayCallback().onSuccess(tm, xiaomiOrderID, mPayConfig.getProductID(), mPayConfig.getProductName(), mPayConfig.getAmount()+"", "ext");
								}
							});
						}
						
						@Override
						public void onError(final int code, final String msg) {
							
							UBSDK.getInstance().runOnUIThread(new Runnable() {
								
								@Override
								public void run() {
									payDialog.dismiss();
									UBLogUtil.logI(TAG+"----->payFail:code="+code+",msg="+msg);
									UBSDK.getInstance().getUBPayCallback().onFailed(tm, msg, null);
								}
							});
						}
					};
					
					
					if (payMethodItem.getID()==PayMethod.ALIPAY) {
						HyDJ.getInstance().aliPay(mActivity, purchase, payResultCallback);
					}else if(payMethodItem.getID()==PayMethod.WEIXING){
						HyDJ.getInstance().wxPay(mActivity, purchase, payResultCallback);
					}else if(payMethodItem.getID()==PayMethod.QQ){
						HyDJ.getInstance().qqPay(mActivity, purchase, payResultCallback);
					}
				}
				
				@Override
				public void onClose() {
					UBLogUtil.logI(TAG+"----->diy dialog close click!");
					payDialog.dismiss();
					UBSDK.getInstance().getUBPayCallback().onCancel(tm);
				}
			});
		}
	}
	
	public void login() {
		UBLogUtil.logI(TAG+"----->login");
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
	
	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePause");
		UBSDK.getInstance().getUBGamePauseCallback().onGamePause();
	}
	
	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");
		UBSDK.getInstance().getUBExitCallback().noImplement();
	}
}
