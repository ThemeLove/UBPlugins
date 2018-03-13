package com.ubsdk.baidu.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBInitPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class BaiDuInitPlugin implements IUBInitPlugin{
	private final String TAG=BaiDuInitPlugin.class.getSimpleName();
	private Activity mActivity;
	
	private BaiDuInitPlugin(Activity activity){
		this.mActivity=activity;
	}

	@Override
	public void initChannel() {
		UBLogUtil.logI(TAG+"----->initChannel");
		BaiDuSDK.getInstance().init();
	}

}
