package com.ubsdk.yyb.plugin;

import java.lang.reflect.Method;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.iplugin.IUBSettingPlugin;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class YYBSettingPlugin implements IUBSettingPlugin {
	private final String TAG=YYBSettingPlugin.class.getSimpleName();
	
	private Activity mActivity;
	private YYBSettingPlugin(Activity activity){
		this.mActivity=activity;
	}

	@Override
	public boolean isSupportMethod(String methodName,Object[] args) {
        UBLogUtil.logI(TAG+"----->isSupportMethod");
        Class<?> [] parameterTypes=null;
        if (args!=null&&args.length>0) {
        	parameterTypes=new Class<?>[args.length];
			for(int i=0;i<args.length;i++){
				parameterTypes[i]=args[i].getClass();
			}
		}
        
        try {
			Method method = getClass().getDeclaredMethod(methodName, parameterTypes);
			return method==null?false:true;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Object callMethod(String methodName, Object[] args) {
		UBLogUtil.logI(TAG+"----->callMethod");
		Class<?>[] parameterTypes=null;
		if (args!=null&&args.length>0) {
			parameterTypes=new Class<?>[args.length];
			for (int i=0;i<args.length;i++) {
				parameterTypes[i]=args[i].getClass();
			}
		}
		
		try {
			Method method = getClass().getDeclaredMethod(methodName, parameterTypes);
			return method.invoke(this, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void gamePause() {
		UBLogUtil.logI(TAG+"----->gamePause");
		
	}

	@Override
	public void exit() {
		UBLogUtil.logI(TAG+"----->exit");
		YYBSDK.getInstance().exit();
	}

	@Override
	public int getPlatformID() {
		UBLogUtil.logI(TAG+"----->getPlatformID");
		return 0;
	}

	@Override
	public String getPlatformName() {
		UBLogUtil.logI(TAG+"----->getPlatformName");
		String platformName="yyb";
		try {
			platformName = UBSDKConfig.getInstance().getParamMap().get(UBSDKConfig.UB_PlatformName);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return platformName;
		}
		return platformName;
	}

	@Override
	public int getSubPlatformID() {
		UBLogUtil.logI(TAG+"----->getSubPlatformID");
		return 0;
	}

	@Override
	public String getExtrasConfig(String extras) {
		UBLogUtil.logI(TAG+"----->getExtrasConfig");
		return null;
	}

}
