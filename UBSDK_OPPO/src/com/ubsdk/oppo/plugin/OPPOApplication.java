package com.ubsdk.oppo.plugin;

import com.nearme.game.sdk.GameCenterSDK;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.listener.IChannelProxyApplication;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class OPPOApplication implements IChannelProxyApplication{
	private final String TAG=OPPOApplication.class.getSimpleName();

	@Override
	public void onProxyAttachBaseContext(Application application, Context base) {
		UBLogUtil.logI(TAG+"----->onProxyAttachBaseContext");
	}

	@Override
	public void onProxyCreate(Application application) {
		UBLogUtil.logI(TAG+"----->onProxyCreate");
		String OPPOAppSecret = UBSDKConfig.getInstance().getParamMap().get("OPPO_AppSecret");
		GameCenterSDK.init(OPPOAppSecret, application);
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
