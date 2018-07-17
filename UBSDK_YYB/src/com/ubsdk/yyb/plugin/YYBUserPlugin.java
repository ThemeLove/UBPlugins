package com.ubsdk.yyb.plugin;

import java.lang.reflect.Method;

import com.umbrella.game.ubsdk.iplugin.IUBUserPlugin;
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;

public class YYBUserPlugin implements IUBUserPlugin{
	private final String TAG=YYBUserPlugin.class.getSimpleName();
	private Activity mActivity;
	private YYBUserPlugin(Activity activity){
		this.mActivity=activity;
		YYBSDK.getInstance().init();
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
	public void login() {
		UBLogUtil.logI(TAG+"----->login");
		YYBSDK.getInstance().login();
	}

	@Override
	public void logout() {
		UBLogUtil.logI(TAG+"----->logout");
		YYBSDK.getInstance().logout();
		
	}

	@Override
	public UBUserInfo getUserInfo() {
		UBLogUtil.logI(TAG+"----->getUserInfo");
		return null;
	}

	@Override
	public void setGameDataInfo(Object obj, int dataType) {
		UBLogUtil.logI(TAG+"----->setGameDataInfo");
	}

}
