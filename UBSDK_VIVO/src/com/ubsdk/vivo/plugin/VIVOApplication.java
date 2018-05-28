package com.ubsdk.vivo.plugin;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.IChannelProxyApplication;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.vivo.unionsdk.open.VivoUnionSDK;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class VIVOApplication implements IChannelProxyApplication{
	private final String TAG=VIVOApplication.class.getSimpleName();

	@Override
	public void onProxyCreate(Application application) {
		UBLogUtil.logI(TAG+"----->onProxyCreate");
//		初始化sdk
		String VIVOAppID = UBSDKConfig.getInstance().getParamMap().get("VIVO_AppID");
		VivoUnionSDK.initSdk(application,VIVOAppID, false);//debug,正式版本设为false
	}

	@Override
	public void onProxyAttachBaseContext(Application application, Context base) {
		UBLogUtil.logI(TAG+"----->onProxyAttachBaseContext");
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
