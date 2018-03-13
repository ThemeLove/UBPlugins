package com.ubsdk.demo;
import java.util.UUID;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.bean.DataType;
import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;
import com.umbrella.game.ubsdk.bean.UBUserInfo;
import com.umbrella.game.ubsdk.callback.UBExitCallback;
import com.umbrella.game.ubsdk.callback.UBGamePauseCallback;
import com.umbrella.game.ubsdk.callback.UBInitCallback;
import com.umbrella.game.ubsdk.callback.UBLoginCallback;
import com.umbrella.game.ubsdk.callback.UBLogoutCallback;
import com.umbrella.game.ubsdk.callback.UBPayCallback;
import com.umbrella.game.ubsdk.callback.UBSwitchAccountCallback;
import com.umbrella.game.ubsdk.utils.ResUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.unity3d.player.UnityPlayerActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UBSDK_Demo_MainActivity extends UnityPlayerActivity
{

    private Activity mActivity;

    private Button mLoginBtn;

    private Button mLogoutBtn;

    private Button mPayBtn;
    
    private Button mGamePauseBtn;

    private Button mExitBtn;

    private Button mCreatRoleBtn;

    private Button mCommitRoleInfoBtn;

    private TextView mInfoTv;
    
    public final String TAG =UBSDK_Demo_MainActivity.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mActivity = this;
    	UBLogUtil.logI(TAG+"----->"+"onCreate");
        
//      设置UBSDK监听在 init 之前，init在onCrete之前
        setSDKlistener();
        UBSDK.getInstance().init(mActivity,new UBInitCallback()
        {

            @Override
            public void onSuccess()
            {
                setContentView(ResUtil.getLayoutId(mActivity, "activity_main"));
                initView();
                setListener();
            	
            	String successStr="init success";
            	UBLogUtil.logI(TAG+"----->"+successStr);
                Toast.makeText(mActivity,successStr, Toast.LENGTH_SHORT).show();
                mInfoTv.setText(successStr);
            }

            @Override
            public void onFailed(String msg, String trace)
            {
                setContentView(ResUtil.getLayoutId(mActivity, "activity_main"));
                initView();
                setListener();
            	
            	String failStr="init onFailed : msg = " + msg + ",trace = " + trace;
            	UBLogUtil.logI(TAG+"----->"+failStr);
                Toast.makeText(mActivity,failStr, Toast.LENGTH_SHORT).show();
                mInfoTv.setText(failStr);
            }
        });
        UBSDK.getInstance().onCreate(savedInstanceState);
        
    }

    private void initView()
    {
        mLoginBtn = (Button) findViewById(ResUtil.getViewID(this, "btn_login"));
        mLogoutBtn = (Button) findViewById(ResUtil.getViewID(this, "btn_logout"));
        mPayBtn = (Button) findViewById(ResUtil.getViewID(this, "btn_pay"));
        
        mGamePauseBtn = (Button) findViewById(ResUtil.getViewID(this, "btn_gamePause"));
        mExitBtn = (Button) findViewById(ResUtil.getViewID(this, "btn_exit"));
        mCreatRoleBtn = (Button) findViewById(ResUtil.getViewID(this, "btn_createRole"));
        mCommitRoleInfoBtn = (Button) findViewById(ResUtil.getViewID(this, "btn_commitRoleInfo"));
        mInfoTv = (TextView) findViewById(ResUtil.getViewID(this, "tv_info"));
    }

    private void setSDKlistener()
    {
        // 设置切换账号通知(必接)
        UBSDK.getInstance().setUBSwitchAccountCallback(new UBSwitchAccountCallback()
        {

            @Override
            public void onSuccess(UBUserInfo ubUserInfo)
            {
                //注意:收到切换账号回调需要回到游戏首页，重新走登录验证流程（切换账号成功后会返回uid，游戏不需要再次调用登录方法）
            	String switchAccountSuccessStr="switchAccount success:" + "\n\r" + "UserID:  " + ubUserInfo.getUid() + "\n\r" + "UserName:  "
                        + ubUserInfo.getUserName() + "\n\r" + "Token:  " + ubUserInfo.getToken();
                mInfoTv.setText(switchAccountSuccessStr);
                UBLogUtil.logI(TAG+"----->"+switchAccountSuccessStr);
            }

            @Override
            public void onFailed(String msg, String trace)
            {
            	String switchAccountFailStr="switchAccount fail:" + "\n\r" + "msg :  " + msg + "\n\r" + "trace:  " + trace;
                mInfoTv.setText(switchAccountFailStr);
                UBLogUtil.logI(TAG+"----->"+switchAccountFailStr);
            }

            @Override
            public void onCancel()
            {
                mInfoTv.setText("switchAccount cancel");
                UBLogUtil.logI(TAG+"----->"+"switchAccount cancel");
            }
        });

    }

    private void setListener()
    {
        mLoginBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                UBSDK.getInstance().login(new UBLoginCallback()
                {

                    @Override
                    public void onSuccess(UBUserInfo ubUserInfo)
                    {
                    	String loginSuccessStr="login success:" + "\n\r" + "UserID:  " + ubUserInfo.getUid() + "\n\r" + "UserName:  "
                                + ubUserInfo.getUserName() + "\n\r" + "Token:  " + ubUserInfo.getToken() + "\n\r"+"extra:" + ubUserInfo.getExtra();
                    	UBLogUtil.logI(TAG+"----->"+loginSuccessStr);
                        mInfoTv.setText(loginSuccessStr);
                        
                        int platfromId = UBSDK.getInstance().getPlatformId();
                        int subPlatformId = UBSDK.getInstance().getSubPlatformId();
                    	UBLogUtil.logI(TAG+"----->"+"platfromId : " + platfromId);
                    	UBLogUtil.logI(TAG+"----->"+"subPlatformId : " + subPlatformId);
                        
//                      TODO 去2次登录验签
                        
                    }

                    @Override
                    public void onFailed(String msg, String trace)
                    {
                    	String loginFailStr="login fail:" + "\n\r" + "msg :  " + msg + "\n\r" + "trace:  "
                                + trace;
                    	UBLogUtil.logI(TAG+"----->"+loginFailStr);
                        mInfoTv.setText(loginFailStr);
                    }

                    @Override
                    public void onCancel()
                    {
                        mInfoTv.setText("cancel by user");
                    	UBLogUtil.logI(TAG+"----->"+"cancel by user");
                    }
                });
            }
        });
        
        mLogoutBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
            	UBSDK.getInstance().logout(new UBLogoutCallback()
                {

                    @Override
                    public void onSuccess()
                    {
                        //注意：收到注销回调需要回到游戏首页，重新登录
                        mInfoTv.setText("logout success");
                        UBLogUtil.logI(TAG+"----->"+"logout success");
                    }

                    @Override
                    public void onFailed(String msg, String trace)
                    {
                    	String loginFailStr="logout fail ： " + "\n\r" + "msg : " + msg +"\n\r"+  "trace : " + trace;
                        mInfoTv.setText(loginFailStr);
                        UBLogUtil.logI(TAG+"----->"+loginFailStr);
                    }
                });
            }
        });
        
        mPayBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                pay();
            }
        });
        
        mGamePauseBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gamePause();
			}
		});
        
        mExitBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                exit();
            }
        });
        
        mCreatRoleBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                setGameDataInfo(DataType.CREATE_ROLE);
            }
        });
        
        mCommitRoleInfoBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                //进入游戏的时候
            	setGameDataInfo(DataType.ENTER_GAME);
                //角色升级的时候
            	setGameDataInfo(DataType.LEVEL_UP);
            }
        });
        
    }


    
    protected void gamePause() {
		UBSDK.getInstance().gamePause(new UBGamePauseCallback() {
			
			@Override
			public void onGamePause() {
				UBLogUtil.logI(TAG+"----->"+"gamePause");
				 mInfoTv.setText("gamePause");
			}
			@Override
			public void onFail(String msg) {
				UBLogUtil.logI(TAG+"----->gamePauseFail");
				mInfoTv.setText("gamePauseFail");
			}
		});
		
	}

	/**
     * 向渠道提交用户信息。 在创建游戏角色、进入游戏和角色升级3个地方调用此接口
     * 当创建角色时最后一个参数值为DataType.CREATE_ROLE
     * 当进入游戏时最后一个参数值DataType.ENTER_GAME
     * 当角色升级时最后一个参数值DataType.LEVEL_UP
     * UBRoleInfo所有字段均不能传null，游戏没有的字段请传一个默认值
     * @param roleType
     */
    public void setGameDataInfo(int roleType){
        UBRoleInfo roleInfo = new UBRoleInfo();
        roleInfo.setServerID("1");// 服务器ID
        roleInfo.setServerName("服务器1");// 服务器名称
        roleInfo.setRoleName("冰上上的王者");// 角色名称
        roleInfo.setRoleID("2666255");// 角色ID
        roleInfo.setRoleLevel("8");// 等级
        roleInfo.setVipLevel("Vip1");// VIP等级
        roleInfo.setGameBalance("300");// 角色现有金额
        roleInfo.setPartyName("partyName"); // 公会名字
        UBSDK.getInstance().setGameDataInfo(roleInfo, roleType);
    }
    

    /**
     * 支付
     */
    private void pay()
    {
        UBRoleInfo roleInfo = new UBRoleInfo();
        roleInfo.setServerID("1");// 服务器ID，其值必须为数字字符串
        roleInfo.setServerName("serverName");// 服务器名称
        roleInfo.setRoleName("roleName");// 角色名称
        roleInfo.setRoleID("6855625");// 角色ID
        roleInfo.setRoleLevel("8");// 等级
        roleInfo.setVipLevel("Vip1");// VIP等级
        roleInfo.setGameBalance("300");// 角色现有金额
        roleInfo.setPartyName("partName");// 公会名字

        UBOrderInfo orderInfo = new UBOrderInfo();
        orderInfo.setCpOrderID(UUID.randomUUID().toString().replace("-", ""));// 游戏订单号
        orderInfo.setGoodsName("钻石");// 产品名称
        orderInfo.setCount(1);// 购买数量，默认为1
        orderInfo.setAmount(6); // 总金额（单位为元）
        orderInfo.setGoodsID("101"); // 产品ID，用来识别购买的产品
        orderInfo.setGoodsDesc("商品描述");//必传
        orderInfo.setExtrasParams("extra"); // 透传参数，游戏自定义的参数
        orderInfo.setCallbackUrl("http://TAGx/notify");//客户端可以不传，通知回调(需要在我们后台配置)
        UBSDK.getInstance().pay(roleInfo,orderInfo,new UBPayCallback()
        {

            @Override
            public void onSuccess(String cpOrderId, String orderID,String goodsId,String goodsName,String goodsPrice, String extrasParams)
            {
            	String paySuccessStr="pay success：" + "\n\r" + "cpOrderID : " + cpOrderId + "\n\r" 
            			+ "orderID : " + orderID+ "\n\r" 
            			+ "goodsId:"+goodsId+"\n\r"
            			+ "goodsName:"+goodsName+"\n\r"
            			+ "goodsPrice:"+goodsPrice+"\n\r"
            			+ "extrasParams : " + extrasParams;
            	
                mInfoTv.setText(paySuccessStr);
                UBLogUtil.logI(TAG+"----->"+paySuccessStr);
                
//                TODO 
            }

            @Override
            public void onFailed(String cpOrderID, String msg, String trace)
            {
            	String payFailStr="pay fail：" + "\n\r" + "cpOrderID : " + cpOrderID + "\n\r" + "msg : " + msg
                        + "\n\r" + "trace : " + trace;
                mInfoTv.setText(payFailStr);
                UBLogUtil.logI(TAG+"----->"+payFailStr);
            }

            @Override
            public void onCancel(String cpOrderID)
            {
            	String payCancelStr="pay cancel：" + "\n\r" + "cpOrderID : " + cpOrderID;
                mInfoTv.setText(payCancelStr);
                UBLogUtil.logI(TAG+"----->"+payCancelStr);
            }
        });
    }

    /**
     * 退出
     */
    private void exit()
    {
        UBSDK.getInstance().exit(new UBExitCallback()
        {

			@Override
			public void onExit() {
                mInfoTv.setText("exit :exit");
                UBLogUtil.logI(TAG+"----->"+"exit :exit");
				
                // 游戏本身的退出操作
                mActivity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
			}

			@Override
			public void onCancel(String message, String trace) {
                mInfoTv.setText("exit:cancel by user");
                UBLogUtil.logI(TAG+"----->"+"exit:cancel by user");
			}

			@Override
			public void noImplement() {
				// TODO 对接的该渠道没有实现退出弹出框，cp自己去实现
				UBSDK.getInstance().showExitDialog();
                mInfoTv.setText("exit:channel noImplement");
                UBLogUtil.logI(TAG+"----->"+"exit:channel noImplement");
			}
        });
    }

 

    @Override
    protected void onStart()
    {
        super.onStart();
        UBSDK.getInstance().onStart();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        UBSDK.getInstance().onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        UBSDK.getInstance().onResume();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        UBSDK.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        UBSDK.getInstance().onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        UBSDK.getInstance().onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        UBSDK.getInstance().onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        UBSDK.getInstance().onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        UBSDK.getInstance().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
    	if (keyCode==KeyEvent.KEYCODE_BACK) {
    		UBSDK.getInstance().onBackPressed();
    		exit();
		}
    	return super.onKeyDown(keyCode, keyEvent);
    }
    
    @Override
    public void onBackPressed()
    {
        UBSDK.getInstance().onBackPressed();
        exit();
    }
}
