package com.ubsdk.vivo;
import java.util.UUID;

import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.bean.DataType;
import com.umbrella.game.ubsdk.bean.UBOrderInfo;
import com.umbrella.game.ubsdk.bean.UBRoleInfo;
import com.umbrella.game.ubsdk.bean.UBUserInfo;
import com.umbrella.game.ubsdk.callback.UBExitCallback;
import com.umbrella.game.ubsdk.callback.UBInitCallback;
import com.umbrella.game.ubsdk.callback.UBLoginCallback;
import com.umbrella.game.ubsdk.callback.UBLogoutCallback;
import com.umbrella.game.ubsdk.callback.UBPayCallback;
import com.umbrella.game.ubsdk.callback.UBSwitchAccountCallback;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UBSDKGameMainActivity extends Activity
{

    private Activity mActivity;

    private Button mLoginBtn;

    private Button mLogoutBtn;

    private Button mPayBtn;

    private Button mExitBtn;

    private Button mCreatRoleBtn;

    private Button mCommitRoleInfoBtn;

    private TextView minfoTv;
    
    public final String TAG =getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActivity = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getResId("activity_main", "layout"));
        initView();
        setSDKlistener();
        
//      设置UBSDK监听在 init 之前，init在onCrete之前
        
        setListener();
        UBSDK.getInstance().init(mActivity);
        UBSDK.getInstance().onCreate(savedInstanceState);
        
    }

    private void initView()
    {
        mLoginBtn = (Button) findViewById(getResId("btn_login", "id"));
        mLogoutBtn = (Button) findViewById(getResId("btn_logout", "id"));
        mPayBtn = (Button) findViewById(getResId("btn_pay", "id"));
        mExitBtn = (Button) findViewById(getResId("btn_exit", "id"));
        mCreatRoleBtn = (Button) findViewById(getResId("btn_createRole", "id"));
        mCommitRoleInfoBtn = (Button) findViewById(getResId("btn_commitRoleInfo", "id"));
        minfoTv = (TextView) findViewById(getResId("tv_info", "id"));
    }

    private void setSDKlistener()
    {
        // 设置初始化通知(必接)
    	UBSDK.getInstance().setUBInitCallback(new UBInitCallback()
        {

            @Override
            public void onSuccess()
            {
                Log.i(TAG, "init onSuccess");
                Toast.makeText(mActivity, "初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(String message, String trace)
            {
                Log.i(TAG, "init onFailed : message = " + message + ",trace = " + trace);
                Toast.makeText(mActivity, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
        // 设置登录通知(必接)
    	UBSDK.getInstance().setUBLoginCallback(new UBLoginCallback()
        {

            @Override
            public void onSuccess(UBUserInfo paramUserInfo)
            {
                minfoTv.setText("登录成功" + "\n\r" + "UserID:  " + paramUserInfo.getUid() + "\n\r" + "UserName:  "
                        + paramUserInfo.getUserName() + "\n\r" + "Token:  " + paramUserInfo.getToken() + ",extra:" + paramUserInfo.getExtra());
                Log.i(TAG, "登录成功" + "\n\r" + "UserID:  " + paramUserInfo.getUid() + "\n\r" + "UserName:  "
                        + paramUserInfo.getUserName() + "\n\r" + "Token:  " + paramUserInfo.getToken());
                
                int platfromId = UBSDK.getInstance().getPlatformId();
                int subPlatformId = UBSDK.getInstance().getSubPlatformId();
                
                Log.i(TAG, "platfromId : " + platfromId);
                Log.i(TAG, "subPlatformId : " + subPlatformId);
                
//              TODO 去2次登录验签
                
            }

            @Override
            public void onFailed(String paramString1, String paramString2)
            {
                minfoTv.setText("登录失败" + "\n\r" + "paramString1 :  " + paramString1 + "\n\r" + "paramString2:  "
                        + paramString2);
            }

            @Override
            public void onCancel()
            {
                minfoTv.setText("登录取消");
            }
        });
        // 设置注销通知(必接)
        UBSDK.getInstance().setUBLogoutCallback(new UBLogoutCallback()
        {

            @Override
            public void onSuccess()
            {
                //注意：收到注销回调需要回到游戏首页，重新登录
                minfoTv.setText("注销成功");
            }

            @Override
            public void onFailed(String message, String trace)
            {
                minfoTv.setText("注销失败 ： " + "\n\r" + "message = " + message + ",trace = " + trace);
            }
        });
        // 设置支付通知(必接)
        UBSDK.getInstance().setUBPayCallback(new UBPayCallback()
        {

            @Override
            public void onSuccess(String sdkOrderID, String cpOrderID, String extrasParams)
            {
                minfoTv.setText("支付成功：" + "\n\r" + "sdkOrderID = " + sdkOrderID + "\n\r" + "cpOrderID = " + cpOrderID
                        + "\n\r" + "extrasParams = " + extrasParams);
            }

            @Override
            public void onFailed(String cpOrderID, String message, String trace)
            {
                minfoTv.setText("支付失败：" + "\n\r" + "cpOrderID = " + cpOrderID + "\n\r" + "message = " + message
                        + "\n\r" + "trace = " + trace);
            }

            @Override
            public void onCancel(String cpOrderID)
            {
                minfoTv.setText("支付取消：" + "\n\r" + "cpOrderID = " + cpOrderID);
            }
        });
        // 设置切换账号通知(必接)
        UBSDK.getInstance().setUBSwitchAccountCallback(new UBSwitchAccountCallback()
        {

            @Override
            public void onSuccess(UBUserInfo ubUserInfo)
            {
                //注意:收到切换账号回调需要回到游戏首页，重新走登录验证流程（切换账号成功后会返回uid，游戏不需要再次调用登录方法）
                minfoTv.setText("切换账号成功" + "\n\r" + "UserID:  " + ubUserInfo.getUid() + "\n\r" + "UserName:  "
                        + ubUserInfo.getUserName() + "\n\r" + "Token:  " + ubUserInfo.getToken());
            }

            @Override
            public void onFailed(String message, String trace)
            {
                minfoTv.setText("切换账号失败" + "\n\r" + "message :  " + message + "\n\r" + "trace:  " + trace);
            }

            @Override
            public void onCancel()
            {
                minfoTv.setText("切换账号取消");
            }
        });
        // 设置退出通知(必接)
        UBSDK.getInstance().setUBExitCallback(new UBExitCallback()
        {

			@Override
			public void onExit() {
                // 游戏本身的退出操作
                mActivity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
			}

			@Override
			public void onCancel(String message, String trace) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void noImplement() {
				// TODO 对接的该渠道没有实现退出弹出框，cp自己去实现
				UBSDK.getInstance().showExitDialog();
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
                UBSDK.getInstance().login();
            }
        });
        mLogoutBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
            	UBSDK.getInstance().logout();
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


    
    /**
     * 向渠道提交用户信息。 在创建游戏角色、进入游戏和角色升级3个地方调用此接口
     * 当创建角色时最后一个参数值为DataType.CREATE_ROLE
     * 当进入游戏时最后一个参数值RoleType.ENTER_GAME
     * 当角色升级时最后一个参数值DataType.LEVEL_UP
     * VasRoleInfo所有字段均不能传null，游戏没有的字段请传一个默认值
     * @param roleType
     */
    public void setGameDataInfo(DataType roleType){
        UBRoleInfo roleInfo = new UBRoleInfo();
        roleInfo.setServerId("1");// 服务器ID
        roleInfo.setServerName("服务器1");// 服务器名称
        roleInfo.setRoleName("冰上上的王者");// 角色名称
        roleInfo.setRoleId("2666255");// 角色ID
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
        roleInfo.setServerId("1");// 服务器ID，其值必须为数字字符串
        roleInfo.setServerName("serverName");// 服务器名称
        roleInfo.setRoleName("roleName");// 角色名称
        roleInfo.setRoleId("6855625");// 角色ID
        roleInfo.setRoleLevel("8");// 等级
        roleInfo.setVipLevel("Vip1");// VIP等级
        roleInfo.setGameBalance("300");// 角色现有金额
        roleInfo.setPartyName("partName");// 公会名字

        UBOrderInfo orderInfo = new UBOrderInfo();
        orderInfo.setCpOrderId(UUID.randomUUID().toString().replace("-", ""));// 游戏订单号
        orderInfo.setGoodsName("钻石");// 产品名称
        orderInfo.setCount(1);// 购买数量，默认为1
        orderInfo.setAmount(6); // 总金额（单位为元）
        orderInfo.setGoodsId("101"); // 产品ID，用来识别购买的产品
        orderInfo.setGoodsDesc("商品描述");//必传
        orderInfo.setExtrasParams("extra"); // 透传参数，游戏自定义的参数
        orderInfo.setCallbackUrl("http://TAGx/notify");//客户端可以不传，通知回调(需要在我们后台配置)
        UBSDK.getInstance().pay(roleInfo,orderInfo);
    }

    /**
     * 退出
     */
    private void exit()
    {
        UBSDK.getInstance().exit();
    }

    /**
     * 返回资源id
     */
    public int getResId(String name, String defType)
    {
        return mActivity.getResources().getIdentifier(name, defType, mActivity.getPackageName());
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
    public void onBackPressed()
    {
        UBSDK.getInstance().onBackPressed();
    }
}
