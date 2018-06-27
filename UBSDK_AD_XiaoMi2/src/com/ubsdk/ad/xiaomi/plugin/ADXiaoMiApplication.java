package com.ubsdk.ad.xiaomi.plugin;

import com.miui.zeus.mimo.sdk.MimoSdk;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.IChannelProxyApplication;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

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
//		MimoSdk.setDebugOn();
//		MimoSdk.setStageOn();
		MimoSdk.init(application, mADXiaoMiAppID,"fake_app_key","fake_app_token");
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
