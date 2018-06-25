package com.ubsdk.ad.vivo.plugin;
import java.util.ArrayList;
import java.util.List;

import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;
import com.vivo.ad.model.AdError;
import com.vivo.ad.splash.SplashADListener;
import com.vivo.mobilead.splash.SplashAdParams;
import com.vivo.mobilead.splash.VivoSplashAd;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ADVIVOSplashActivity extends Activity implements SplashADListener {
	 	private  final String TAG = ADVIVOSplashActivity.class.getSimpleName();
	 	
	    private String mVIVOSplashID = "";
		private String mAppName="";
		private String mAppDesc;
		private UBADCallback mUBADCallback;
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        
	        // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion <
	        // 23），那么只需要在这里直接调用fetchSplashAD接口。
	        
	        mVIVOSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Splash_ID");
//	        mAppName=ResUtil.getStringFormResouse(this,"app_name");
	        mAppName = UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Splash_App_Name");
	        mAppDesc=UBSDKConfig.getInstance().getParamMap().get("AD_VIVO_Splash_App_Desc");
	        
	        mUBADCallback = UBAD.getInstance().getUBADCallback();
	        
	        if (Build.VERSION.SDK_INT >= 23) {
	            checkAndRequestPermission();
	        } else {
	            fetchSplashAD(this, mVIVOSplashID, this);
	        }
	    }
	    
	    /**
	     *
	     * ----------非常重要----------
	     *
	     * Android6.0以上的权限适配简单示例：
	     *
	     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广告SDK，否则不会有广告返回。
	     *
	     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
	     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
	     */
	    @TargetApi(Build.VERSION_CODES.M)
	    private void checkAndRequestPermission() {
	        
	        List<String> lackedPermission = new ArrayList<String>();
	        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
	            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
	        }
	        
	        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
	            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
	        }
	        
	        // 权限都已经有了，那么直接调用SDK
	        if (lackedPermission.size() == 0) {
	            fetchSplashAD(this, mVIVOSplashID, this);
	        } else {
	            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
	            String[] requestPermissions = new String[lackedPermission.size()];
	            lackedPermission.toArray(requestPermissions);
	            requestPermissions(requestPermissions, 1024);
	        }
	    }
	    
	    private boolean hasAllPermissionsGranted(int[] grantResults) {
	        
	        for (int grantResult : grantResults) {
	            if (grantResult == PackageManager.PERMISSION_DENIED) {
	                return false;
	            }
	        }
	        return true;
	    }
	    
	    @Override
	    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	        
	        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
	            fetchSplashAD(this, mVIVOSplashID, this);
	        } else {
	            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
	            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
	            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
	            intent.setData(Uri.parse("package:" + getPackageName()));
	            startActivity(intent);
	            finish();
	        }
	    }
	    
	    /**
	     * 获取广告
	     * 
	     * @param activity
	     * @param posId
	     * @param listener
	     */
	    private void fetchSplashAD(Activity activity, String posId, SplashADListener listener) {
	        
	        try {
	            SplashAdParams.Builder builder = new SplashAdParams.Builder();
	            // 拉取广告的超时时长：即开屏广告从请求到展示所花的最大时长（并不是指广告曝光时长）取值范围[3000, 5000]
	            builder.setFetchTimeout(3000);
	            // 广告下面半屏的应用标题+应用描述:应用标题和应用描述是必传字段，不传将抛出异常
	            // 标题最长5个中文字符 描述最长8个中文字符
	            builder.setTitle(mAppName);
	            builder.setDesc(mAppDesc);
	            
	            new VivoSplashAd(activity, posId, listener, builder.build());
	        } catch (Exception e) {
	        	UBLogUtil.logI(TAG+"----->fetchSplashAD----->exception");
	            e.printStackTrace();
	            finish();
	        }
	    }
	    
	    @Override
	    public void onADDismissed() {
	        UBLogUtil.logI(TAG+"----->showSplashAD----->onADDismissed");
	        if (mUBADCallback!=null) {
				mUBADCallback.onClosed(ADType.AD_TYPE_SPLASH,"splash AD closed!");
			}
	        finish();
	    }
	    
	    @Override
	    public void onNoAD(AdError error) {
	        UBLogUtil.logI(TAG+"----->showSplashAD----->onError");
	        if (mUBADCallback!=null) {
				mUBADCallback.onFailed(ADType.AD_TYPE_SPLASH,"splash AD show failed:msg="+error.getErrorMsg());
			}
	    }
	    
	    @Override
	    public void onADPresent() {
	        UBLogUtil.logI(TAG+"----->showSplashAD----->onShow");
	        if (mUBADCallback!=null) {
				mUBADCallback.onShow(ADType.AD_TYPE_SPLASH,"splash AD show success");
			}
	    }
	    
	    @Override
	    public void onADClicked() {
	        UBLogUtil.logI(TAG+"----->showSplashAD----->onClick");
	        if (mUBADCallback!=null) {
				mUBADCallback.onClick(ADType.AD_TYPE_SPLASH,"splash AD clicked");
			}
	    }
	    
	    /** 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费 */
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        
	        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
	            return true;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
}
