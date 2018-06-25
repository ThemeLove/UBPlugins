package com.ubsdk.ad.xiaomi.plugin;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.IChannelProxyApplication;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.xiaomi.ad.AdSdk;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class ADXiaoMiApplication implements IChannelProxyApplication{
	private final String TAG=ADXiaoMiApplication.class.getSimpleName();
	private String mADXiaoMiAppID;

	@Override
	public void onProxyAttachBaseContext(Application application, Context base) {
		UBLogUtil.logI(TAG+"----->onProxyAttachBaseContext");
		mADXiaoMiAppID = UBSDKConfig.getInstance().getParamMap().get("AD_XiaoMi_APP_ID");
	}

	@Override
	public void onProxyCreate(Application application) {
		UBLogUtil.logI(TAG+"----->onProxyCreate");
		UBLogUtil.logI(TAG+"----->onProxyCreate----->mADXiaoMiAppID="+mADXiaoMiAppID);
//		TODO
//		 AdSdk.setMockOn(); //打开模拟模式，仅在初次接入广告时使用（测试用，在开发者站提交应用时请删除本行代码，否则不会有收益）
	     AdSdk.setDebugOn(); // 打开调试，输出调试信息（测试用，在开发者站提交应用时请删除本行代码，否则不会有收益）
	     AdSdk.initialize(application, mADXiaoMiAppID);//appId是你在小米开发者网站上注册的应用ID。
	}

	@Override
	public void onProxyConfigurationChanged(Application application, Configuration config) {
		UBLogUtil.logI(TAG+"----->onProxyConfigurationChanged");
	}

	@Override
	public void onTerminate() {
		UBLogUtil.logI(TAG+"----->onTerminate");
		
	}

}
