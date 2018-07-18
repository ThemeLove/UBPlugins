package com.ubsdk.yyb.plugin;

import java.util.HashMap;

import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.bugly.BuglyListener;
import com.tencent.ysdk.module.pay.PayItem;
import com.tencent.ysdk.module.pay.PayListener;
import com.tencent.ysdk.module.pay.PayRet;
import com.tencent.ysdk.module.user.UserListener;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.tencent.ysdk.module.user.UserRelationRet;
import com.tencent.ysdk.module.user.WakeupRet;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.UBActivityListenerImpl;
import com.umbrella.game.ubsdk.model.UBPayConfigModel;
import com.umbrella.game.ubsdk.plugintype.pay.PayConfig;
import com.umbrella.game.ubsdk.plugintype.pay.PayType;
import com.umbrella.game.ubsdk.plugintype.pay.UBOrderInfo;
import com.umbrella.game.ubsdk.plugintype.user.UBRoleInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class YYBSDK {
	private final String TAG=YYBSDK.class.getSimpleName();
	private static YYBSDK instance;
	private Activity mActivity;
	private String mMidasAppKey;//米大师appKey
	private PayConfig mPayConfig;//本次支付的支付配置
	private YYBSDK(){}
	public static YYBSDK getInstance(){
		if (instance==null) {
			if (instance==null) {
				instance=new YYBSDK();
			}
		}
		return instance;
	}

	public void init() {
		mActivity = UBSDKConfig.getInstance().getGameActivity();
		mMidasAppKey = UBSDKConfig.getInstance().getParamMap().get("Midas_AppKey");
		
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
				UBLogUtil.logI(TAG+"----->login----->OnWakeupNotify");
			}
			
			@Override
			public void OnRelationNotify(UserRelationRet arg0) {
				UBLogUtil.logI(TAG+"----->login----->OnRelationNotify");
				
			}
			
			@Override
			public void OnLoginNotify(UserLoginRet ret) {
				UBLogUtil.logI(TAG+"----->login----->OnLoginNotify----->msg="+ret.msg);
				 switch (ret.flag) {
		            case eFlag.Succ://登录成功
		            	UBLogUtil.logI(TAG+"----->login----->success!");
		                break;
		            // 游戏逻辑，对登录失败情况分别进行处理
		            case eFlag.QQ_UserCancel://QQ登录，用户取消
		            	UBLogUtil.logI(TAG+"----->login----->failed:QQ user cancel msg="+ret.msg);
		                break;
		            case eFlag.QQ_LoginFail://QQ登录失败
		            	UBLogUtil.logI(TAG+"----->login----->failed:QQ msg="+ret.msg);
		                break;
		            case eFlag.QQ_NetworkErr://QQ登录异常
		            	UBLogUtil.logI(TAG+"----->login----->failed:QQ msg="+ret.msg);
		                break;
		            case eFlag.QQ_NotInstall://手机未安装QQ
		            	UBLogUtil.logI(TAG+"----->login----->failed:QQ msg="+ret.msg);
		                break;
		            case eFlag.QQ_NotSupportApi://QQ版本太低
		            	UBLogUtil.logI(TAG+"----->login----->failed:QQ msg="+ret.msg);
		                break;
		            case eFlag.WX_NotInstall://手机未安装WX
		            	UBLogUtil.logI(TAG+"----->login----->failed:WX msg="+ret.msg);
		                break;
		            case eFlag.WX_NotSupportApi://WX版本太低
		            	UBLogUtil.logI(TAG+"----->login----->failed:WX msg="+ret.msg);
		                break;
		            case eFlag.WX_UserCancel://WX登录，用户取消
		            	UBLogUtil.logI(TAG+"----->login----->failed:WX user cancel msg="+ret.msg);
		                break;
		            case eFlag.WX_UserDeny://WX登录，用户拒绝了授权
		            	UBLogUtil.logI(TAG+"----->login----->failed:WX msg="+ret.msg);
		                break;
		            case eFlag.WX_LoginFail://WX登录失败
		            	UBLogUtil.logI(TAG+"----->login----->failed:WX msg="+ret.msg);
		                break;
		            case eFlag.Login_TokenInvalid://您尚未登录或者之前的登录已过期，请重试
		            	UBLogUtil.logI(TAG+"----->login----->failed:msg="+ret.msg);
		                break;
		            case eFlag.Login_NotRegisterRealName://您的账号没有进行实名认证，请实名认证后重试
		            	UBLogUtil.logI(TAG+"----->login----->failed:msg="+ret.msg);
		                // 显示登录界面
		                break;
		            case eFlag.GUEST_LoginFail://游客登录失败
		            	UBLogUtil.logI(TAG+"----->login----->failed:Guest msg="+ret.toString());
		            	break;
		            default:
		            	UBLogUtil.logI(TAG+"----->login----->failed:msg="+ret.msg);
		                break;
		            }
			}
		});
		
		YSDKApi.setBuglyListener(new BuglyListener() {
			
			@Override
			public String OnCrashExtMessageNotify() {
				return null;
			}
			
			@Override
			public byte[] OnCrashExtDataNotify() {
				return null;
			}
		});
		
//		同步给出初始化成功回调
		UBSDK.getInstance().getUBInitCallback().onSuccess();
	}
	
	public void login() {
		UBLogUtil.logI(TAG+"----->login");
		ePlatform platform = getPlatform();
		if (platform!=platform.None) {//有登录态
//			TODO
		}else{
			UBLogUtil.logI(TAG+"----->guest login");
			YSDKApi.login(ePlatform.Guest);
		}
	}
	
	// 获取当前登录平台
    public ePlatform getPlatform() {
        UserLoginRet ret = new UserLoginRet();
        YSDKApi.getLoginRecord(ret);
        if (ret.flag == eFlag.Succ) {
            return ePlatform.getEnum(ret.platform);
        }
        return ePlatform.None;
    }  
	
	public void logout() {
		UBLogUtil.logI(TAG+"----->logout");
		YSDKApi.logout();
	}

	public void pay(UBRoleInfo ubRoleInfo,UBOrderInfo ubOrderInfo){
		UBLogUtil.logI(TAG+"----->pay");
		UBLogUtil.logI(TAG+"----->pay----->ubRoleInfo="+ubRoleInfo+",ubOrderInfo="+ubOrderInfo);
		HashMap<String, PayConfig> mPayConfigMap = UBPayConfigModel.getInstance().loadStorePayConfig("payConfig.xml");
		if (mPayConfigMap!=null&&!TextUtils.isEmpty(ubOrderInfo.getGoodsID())) {
			mPayConfig = mPayConfigMap.get(ubOrderInfo.getGoodsID());
		}
		if (mPayConfig==null) {
			throw new RuntimeException("Lenovo store pay config error!!");
		}
		
		final String tm=System.currentTimeMillis()+"";
		
		PayListener payListener = new PayListener() {
			
			@Override
			public void OnPayNotify(PayRet ret) {
				 if(PayRet.RET_SUCC == ret.ret){
			            //支付流程成功
			            switch (ret.payState){
			                //支付成功
			                case PayRet.PAYSTATE_PAYSUCC:
			                	UBLogUtil.logI(TAG+"----->pay----->success");
			                	int realSaveNum = ret.realSaveNum;//支付金额
			                	int payChannel = ret.payChannel;//使用渠道
			                	int provideState = ret.provideState;//发货状态
			                	String extendInfo = ret.extendInfo;//业务类型
			                	
			                    break;
			                //取消支付
			                case PayRet.PAYSTATE_PAYCANCEL://用户支付取消
			                	UBLogUtil.logI(TAG+"----->pay----->failed=user cancel");
			                    break;
			                //支付结果未知
			                case PayRet.PAYSTATE_PAYUNKOWN://用户支付结果未知，建议查询余额
			                	UBLogUtil.logI(TAG+"----->pay----->failed=unknown");
			                    break;
			                //支付失败
			                case PayRet.PAYSTATE_PAYERROR://支付异常
			                	UBLogUtil.logI(TAG+"----->pay----->failed=Payment exception!");
			                    break;
			            }
			        }else{//支付失败
			            switch (ret.flag){
			                case eFlag.Login_TokenInvalid://登录态过期
			                	UBLogUtil.logI(TAG+"----->pay----->failed=login_tokenInvalid:登录态过期");
			                    break;
			                case eFlag.Pay_User_Cancle://用户支付取消
			                	UBLogUtil.logI(TAG+"----->pay----->failed=user cancel");
			                    break;
			                case eFlag.Pay_Param_Error://支付失败，参数错误
			                	UBLogUtil.logI(TAG+"----->pay----->failed=Parameter error!");
			                    break;
			                case eFlag.Error://支付异常
			                	UBLogUtil.logI(TAG+"----->pay----->failed=Payment exception!");
			                	break;
			                default:
			                	UBLogUtil.logI(TAG+"----->pay----->failed=Payment exception!");
			                    break;
			            }
			        }
			}
		};
		
		if (PayType.PAY_TYPE_NORMAL==mPayConfig.getPayType()) {
			PayItem payItem = new PayItem();
			payItem.id=mPayConfig.getProductID();
			payItem.name=mPayConfig.getProductName();
			payItem.desc=mPayConfig.getOrderInfo().getGoodsDesc();
//			商品价格，米大师的单位为角
			payItem.price=(int)mPayConfig.getAmount();
			payItem.num=1;//这里固定为1
			
			YSDKApi.buyGoods(false,//是否可以修改订单金额
					ubRoleInfo.getServerID(),//大区id，服务器id
					payItem,//payItem(道具id,道具名称，道具描述，道具价格，道具数量)
					mMidasAppKey,//midas_appKey
					null,//byte[] 商品图片
					"midasExt",//米大师透传
					"ysdkExt",//YSDK透传参数
					payListener);//PayListener
		}
	}
	
	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");

		
	}
	

}
