package com.ubsdk.meizu.plugin;

import com.meizu.gamesdk.offline.core.MzGameCenterPlatform;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.IChannelProxyApplication;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class MeiZuApplication implements IChannelProxyApplication{
	
	private final String TAG=MeiZuApplication.class.getSimpleName();

	@Override
	public void onProxyAttachBaseContext(Application application, Context base) {
		UBLogUtil.logI(TAG+"----->onProxyAttachBaseContext");
	}

	@Override
	public void onProxyCreate(Application application) {
		UBLogUtil.logI(TAG+"----->onProxyCreate");
		String meiZuAppID = UBSDKConfig.getInstance().getParamMap().get("MeiZu_AppID");
		String meiZuAppKey = UBSDKConfig.getInstance().getParamMap().get("MeiZu_AppKey");
		
		MzGameCenterPlatform.init(application, meiZuAppID, meiZuAppKey);
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
