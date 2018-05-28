package com.ubsdk.xiaomi.plugin;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.IChannelProxyApplication;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.xiaomi.hy.dj.HyDJ;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class XiaoMiApplication implements IChannelProxyApplication{
	private final String TAG=XiaoMiApplication.class.getSimpleName();
	
	@Override
	public void onProxyAttachBaseContext(Application application, Context base) {
		UBLogUtil.logI(TAG+"----->onProxyAttachBaseContext");
	}

	@Override
	public void onProxyCreate(Application application) {
		UBLogUtil.logI(TAG+"----->onProxyCreate");
		String xiaoMiAppID = UBSDKConfig.getInstance().getParamMap().get("XiaoMi_AppID");
		String xiaoMiAppKey = UBSDKConfig.getInstance().getParamMap().get("XiaoMi_AppKey");
		HyDJ.init(application, xiaoMiAppID, xiaoMiAppKey);
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
