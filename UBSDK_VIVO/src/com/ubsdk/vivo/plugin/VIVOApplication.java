package com.ubsdk.vivo.plugin;

import com.umbrella.game.ubsdk.listener.IChannelProxyApplication;
import com.vivo.unionsdk.open.VivoUnionSDK;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class VIVOApplication implements IChannelProxyApplication{

	@Override
	public void onProxyCreate(Application application) {
//		初始化sdk
		VivoUnionSDK.initSdk(application, "", false);
	}

	@Override
	public void onProxyAttachBaseContext(Application application, Context base) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProxyConfigurationChanged(Application application, Configuration config) {
		// TODO Auto-generated method stub
		
	}

}
