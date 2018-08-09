package com.ubsdk.muzhiwan.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.muzhiwan.sdk.core.MzwSdkController;
import com.muzhiwan.sdk.core.callback.MzwInitCallback;
import com.muzhiwan.sdk.core.callback.MzwLoignCallback;
import com.muzhiwan.sdk.core.callback.MzwPayCallback;
import com.muzhiwan.sdk.service.IMzwExitGameCallBack;
import com.muzhiwan.sdk.service.MzwOrder;
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
import com.umbrella.game.ubsdk.utils.ToastUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;

public class MuZhiWanSDK {
	private final String TAG=MuZhiWanSDK.class.getSimpleName();
	private static MuZhiWanSDK instance=null;
	private Activity mActivity;

	private MuZhiWanSDK(){ }
	
	public static MuZhiWanSDK getInstance(){
		if (instance==null) {
			if (instance==null) {
				instance=new MuZhiWanSDK();
			}
		}
		return instance;
	}
	
	public void init(){
		UBLogUtil.logI(TAG+"----->init");
		try {
			loadParams();
			setActivityListener();
//			动态权限处理
			if (android.os.Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
				 checkAndRequestPermission();
		    }else{
		         initSDK();   
		    }
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			UBLogUtil.logI(TAG+"----->init finally");
		}
	}

	private void loadParams() {
		UBLogUtil.logI(TAG+"----->loadParams");
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		mGameOrientation = UBSDKConfig.getInstance().getUBGame().getOrientation();
		
	}
	

	private void setActivityListener() {
		UBLogUtil.logI(TAG+"----->setActivityListener");
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onCreate(Bundle savedInstanceState) {
				UBLogUtil.logI(TAG+"----->onCreate");
			}

			@Override
			public void onPause() {
				UBLogUtil.logI(TAG+"----->onPause");
			}

			@Override
			public void onResume() {
				UBLogUtil.logI(TAG+"----->onResume");
			}

			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestory");
				MzwSdkController.getInstance().destory();
			}

			@Override
			public void onBackPressed() {
				UBLogUtil.logI(TAG+"----->onBackPressed");
			}

			@Override
			public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
				UBLogUtil.logI(TAG+"----->onRequestPermissionResult");
				if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
			    	UBLogUtil.logI(TAG+"----->have got the request permissioins");
//			    	这里如果获得了权限，就去调用一次初始化
			    	initSDK();
			      } else {
			        // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
			    	ToastUtil.showToast(mActivity, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。");
			        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
			        mActivity.startActivity(intent);
			     }
			}
			
		});
		
	}
	
	private void initSDK() {
		UBLogUtil.logI(TAG+"----->initSDK");
		MzwSdkController.getInstance().init(mActivity,
				 TextUtil.equalsIgnoreCase("portrait",mGameOrientation)?MzwSdkController.ORIENTATION_VERTICAL:MzwSdkController.ORIENTATION_HORIZONTAL, 
				 new MzwInitCallback() {
					@Override
					public void onResult(final int code,final String msg) {
						UBSDK.getInstance().runOnUIThread(new Runnable() {
							
							@Override
							public void run() {
								UBLogUtil.logI(TAG+"----->init----->result:code="+code+",msg="+msg);
								if (code==1) {//初始化成功
									UBSDK.getInstance().getUBInitCallback().onSuccess();
								}else{//初始化失败
									UBSDK.getInstance().getUBInitCallback().onFailed(msg, null);
								}
							}
						});
					}
         });
	}
	
	private final int PERMISSION_REQUEST_CODE=1024;
	private String mGameOrientation;

	 @TargetApi(Build.VERSION_CODES.M)
	 private void checkAndRequestPermission() {
	    List<String> lackedPermission = new ArrayList<String>();
	    if (!(mActivity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
	      lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
	    }
	    if (!(mActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
		      lackedPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
		}
	    if (!(mActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
	      lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
	    }

	    if (!(mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
	      lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
	    }
	    
	    if (!(mActivity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
		      lackedPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
		}
	    if (!(mActivity.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)) {
		      lackedPermission.add(Manifest.permission.READ_SMS);
		}
	    if (!(mActivity.checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)) {
		      lackedPermission.add(Manifest.permission.RECEIVE_SMS);
		}

	    // 权限都已经有了，那么直接调用SDK初始化
	    if (lackedPermission.size() == 0) {
	    	initSDK();
	    } else {
	      // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
	      String[] requestPermissions = new String[lackedPermission.size()];
	      lackedPermission.toArray(requestPermissions);
	      mActivity.requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE);
	    }
	  }
	 
	 /**
	  * 权限是否获取成功
	  * @param grantResults
	  * @return
	  */
	 private boolean hasAllPermissionsGranted(int[] grantResults) {
		    for (int grantResult : grantResults) {
		      if (grantResult == PackageManager.PERMISSION_DENIED) {
		        return false;
		      }
		    }
		    return true;
	 }
	
	
/***********************************************************login***********************************************************/
	private String mLoginToken="";
	public void login(){
		UBLogUtil.logI(TAG+"---->login");
		
		MzwSdkController.getInstance().doLogin(new MzwLoignCallback() {
			
			@Override
			public void onResult(int code, String msg) {
				UBLogUtil.logI(TAG+"----->mzw login----->result:code="+code+",msg="+msg);
				if (code==1) {//登录成功，msg为登录成功的token,有效期为1分钟
					UBLogUtil.logI(TAG+"------>mzw login-----success:msg="+msg);
					mLoginToken = msg;
//					
//					MzwSdkController.getInstance().setPopVisible(true);
				}else if(code==4){//登录取消
					UBLogUtil.logI(TAG+"----->mzw login----->cancel:msg="+msg);
					mLoginToken="";
				}else if(code==6){//注销
					UBLogUtil.logI(TAG+"----->mzw login----->logout:msg="+msg);
					mLoginToken="";
				}else if(code==0){//登录失败
					UBLogUtil.logI(TAG+"----->mzw login----->failed:msg="+msg);
					mLoginToken="";
				}else {//
					UBLogUtil.logI(TAG+"----->mzw login----->failed:msg="+msg);
					mLoginToken="";
				}
			}
		});
		
		UBUserInfo ubUserInfo = new UBUserInfo();
		ubUserInfo.setUid("123456");
		ubUserInfo.setUserName("ubsdktest");
		ubUserInfo.setToken("123456ABCDEFG");
		ubUserInfo.setExtra("extra");
		
		UBSDK.getInstance().getUBLoginCallback().onSuccess(ubUserInfo);
		
	}
	
	public void logout(){
		UBLogUtil.logI(TAG+"----->logout");
		MzwSdkController.getInstance().doLogout();
//		MzwSdkController.getInstance().setPopVisible(false);
		
//		直接同步给出注销回调
		mLoginToken="";
		UBSDK.getInstance().getUBLogoutCallback().onSuccess();
	}
	
	public void exit(){
		UBLogUtil.logI(TAG+"----->exit");
		MzwSdkController.getInstance().exitGame(new IMzwExitGameCallBack() {
			
			@Override
			public IBinder asBinder() {
				return null;
			}
			
			@Override
			public void onResult(int code, String msg) throws RemoteException {
				
			}
		});
		
	}
	public void gamePause(){
		UBLogUtil.logI(TAG+"----->gamePause");
	}
	
	/***********************************************************pay***********************************************************/
	private PayConfig mPayConfig;
	public void pay(UBRoleInfo ubRoleInfo, UBOrderInfo ubOrderInfo) {
		UBLogUtil.logI(TAG+"----->pay");
		UBLogUtil.logI(TAG+"----->pay----->ubRoleInfo="+ubRoleInfo+",ubOrderInfo="+ubOrderInfo);
		
		HashMap<String, PayConfig> mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml");
		if (mPayConfigMap!=null&&!TextUtils.isEmpty(ubOrderInfo.getGoodsID())) {
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
		}
		if (mPayConfig==null) {
			throw new RuntimeException("MuZhiWan store pay config error!!");
		}
		
		if (TextUtil.isEmpty(mLoginToken)) {
			login();
			return;
		}
		
		final String tm=System.currentTimeMillis()+"";
		
		if (mPayConfig.getPayType()==PayType.PAY_TYPE_NORMAL) {
			MzwOrder mzwOrder = new MzwOrder();
			mzwOrder.setMoney(mPayConfig.getOrderInfo().getAmount());
			mzwOrder.setProductname(mPayConfig.getOrderInfo().getGoodsName());
			mzwOrder.setProductdesc(mPayConfig.getOrderInfo().getGoodsDesc());
			mzwOrder.setExtern(mPayConfig.getOrderInfo().getExtrasParams());
			
			mzwOrder.setProductid(mPayConfig.getProductID());
			mzwOrder.setOrderid(tm);
			
/*			mzwOrder.setAccount(account);
			mzwOrder.setAccountname(accountname);
			mzwOrder.setProcessing(processing);*/
			
			MzwSdkController.getInstance().doPay(mzwOrder, new MzwPayCallback() {
				
				@Override
				public void onResult(final int code,final MzwOrder mzwOrder) {
					UBSDK.getInstance().runOnUIThread(new Runnable() {
						@Override
						public void run() {
							UBLogUtil.logI(TAG+"----->mzw pay----->result:code="+code+",mzwOrder="+mzwOrder.toString());
							if (code==1) {//支付成功
								UBSDK.getInstance().getUBPayCallback().onSuccess(tm,tm, mPayConfig.getProductID(), mPayConfig.getOrderInfo().getGoodsName(), mPayConfig.getOrderInfo().getAmount()+"", mPayConfig.getOrderInfo().getExtrasParams());
							}else if(code==0){//支付失败
								UBSDK.getInstance().getUBPayCallback().onFailed(tm,"mzw pay failed", null);
							}else if(code==-1){//支付发起状态
								UBLogUtil.logI(TAG+"----->mzw pay----->code==-1 pay waiting...");
							}else if(code==5){//支付完成状态
								UBLogUtil.logI(TAG+"----->mzw pay----->code==5 pay finish");
							}else{//其他情况
								UBLogUtil.logI(TAG+"----->mzw pay----->unknown!");
								UBSDK.getInstance().getUBPayCallback().onFailed(tm, "unKnown",null);
							}
						}
					});
					
				}
			});
			
		}
		
	}
	
}
