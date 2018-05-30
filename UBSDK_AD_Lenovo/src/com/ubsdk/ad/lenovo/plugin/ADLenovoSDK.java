package com.ubsdk.ad.lenovo.plugin;

import com.umbrella.game.ubsdk.iplugin.IUBADPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class ADLenovoSDK implements IUBADPlugin{
	private final String TAG=ADLenovoSDK.class.getSimpleName();
	private Activity mActivity;
	private ADLenovoSDK(Activity activity){
		mActivity=activity;
	}
	
	@Override
	public boolean isSupportADType(int adType) {
		UBLogUtil.logI(TAG+"----->isSupportADType");
		return false;
	}

	@Override
	public void showADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->showADWithADType");
		
	}

	@Override
	public void hideADWithADType(int adType) {
		UBLogUtil.logI(TAG+"----->hideADWithADType");
		
	}

	@Override
	public boolean isSupportMethod(String methodName, Object[] args) {
		UBLogUtil.logI(TAG+"----->isSupportMethod");
		return false;
	}

	@Override
	public Object callMethod(String methodName, Object[] args) {
		UBLogUtil.logI(TAG+"----->callMethod");
		return null;
	}
}
