package com.ubsdk.ad.baidu.plugin;

import com.duoku.alone.ssp.DuoKuAdSDK;
import com.umbrella.game.ubsdk.listener.IChannelProxyApplication;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class ADBaiDuApplication implements IChannelProxyApplication{
	private static final String TAG=ADBaiDuApplication.class.getSimpleName();

	@Override
	public void onProxyAttachBaseContext(Application application, Context base) {
		UBLogUtil.logI(TAG+"----->onProxyAttachBaseContext");
	}

	@Override
	public void onProxyCreate(Application application) {
		UBLogUtil.logI(TAG+"----->onProxyCreate");
		
		DuoKuAdSDK.getInstance().initApplication(application);
	    // 设置是否是线上地址，默认或者true为线上地址，false为测试地址
	    // 必须在init之前设置
	    DuoKuAdSDK.getInstance().setOnline(true,application);
	    // 开启调试日志 默认false
	    DuoKuAdSDK.getInstance().setDebug(true);
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
