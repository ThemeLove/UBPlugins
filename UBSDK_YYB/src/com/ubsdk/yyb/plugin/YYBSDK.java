package com.ubsdk.yyb.plugin;

import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.module.bugly.BuglyListener;
import com.tencent.ysdk.module.user.UserListener;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.tencent.ysdk.module.user.UserRelationRet;
import com.tencent.ysdk.module.user.WakeupRet;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class YYBSDK {
	private final String TAG=YYBSDK.class.getSimpleName();
	private static YYBSDK instance;
	private Activity mActivity;
	private YYBSDK(){}
	public static YYBSDK getInstance(){
		if (instance==null) {
			if (instance==null) {
				instance=new YYBSDK();
			}
		}
		return instance;
	}

	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
	}
	
	public void init() {
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		UBSDK.getInstance().setUBActivityListener(new UBActivityListenerImpl(){

			@Override
			public void onRestart() {
				UBLogUtil.logI(TAG+"----->onRestart");
				YSDKApi.onRestart(mActivity);
			}
			
			@Override
			public void onResume() {
				UBLogUtil.logI(TAG+"----->onResume");
				YSDKApi.onResume(mActivity);
			}

			@Override
			public void onPause() {
				UBLogUtil.logI(TAG+"----->onPause");
				YSDKApi.onPause(mActivity);
			}

			@Override
			public void onStop() {
				UBLogUtil.logI(TAG+"----->onStop");
				YSDKApi.onStop(mActivity);
			}

			@Override
			public void onDestroy() {
				UBLogUtil.logI(TAG+"----->onDestory");
				YSDKApi.onDestroy(mActivity);
			}

			@Override
			public void onNewIntent(Intent newIntent) {
				UBLogUtil.logI(TAG+"----->onNewIntent");
				YSDKApi.handleIntent(newIntent);
			}

			@Override
			public void onActivityResult(int requestCode, int resultCode, Intent data) {
				UBLogUtil.logI(TAG+"----->onActivityResult");
				YSDKApi.onActivityResult(requestCode, resultCode, data);
			}
		});
		
//		YSDK初始化
		YSDKApi.onCreate(mActivity);
		
		YSDKApi.setUserListener(new UserListener() {
			
			@Override
			public void OnWakeupNotify(WakeupRet arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnRelationNotify(UserRelationRet arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnLoginNotify(UserLoginRet ret) {
				 switch (ret.flag) {
		            case eFlag.Succ://登录成功
		                break;
		            // 游戏逻辑，对登录失败情况分别进行处理
		            case eFlag.QQ_UserCancel://QQ登录，用户取消
		                break;
		            case eFlag.QQ_LoginFail://QQ登录失败
		                break;
		            case eFlag.QQ_NetworkErr://QQ登录异常
		                break;
		            case eFlag.QQ_NotInstall://手机未安装QQ
		                break;
		            case eFlag.QQ_NotSupportApi://QQ版本太低
		                break;
		            case eFlag.WX_NotInstall://手机未安装WX
		                break;
		            case eFlag.WX_NotSupportApi://WX版本太低
		                break;
		            case eFlag.WX_UserCancel://WX登录，用户取消
		                break;
		            case eFlag.WX_UserDeny://WX登录，用户拒绝了授权
		                break;
		            case eFlag.WX_LoginFail://WX登录失败
		                break;
		            case eFlag.Login_TokenInvalid://您尚未登录或者之前的登录已过期，请重试
		                break;
		            case eFlag.Login_NotRegisterRealName://您的账号没有进行实名认证，请实名认证后重试
		                // 显示登录界面
		                break;
		            default:
		                break;
		            }
			}
		});
		
		YSDKApi.setBuglyListener(new BuglyListener() {
			
			@Override
			public String OnCrashExtMessageNotify() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public byte[] OnCrashExtDataNotify() {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}
}
