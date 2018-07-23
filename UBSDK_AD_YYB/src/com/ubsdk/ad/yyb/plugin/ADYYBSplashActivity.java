package com.ubsdk.ad.yyb.plugin;

import java.util.ArrayList;
import java.util.List;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.ResUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 应用宝闪屏广告
 * @author qingshanliao
 *
 */
public class ADYYBSplashActivity extends Activity{
	
	private final String TAG=ADYYBSplashActivity.class.getSimpleName();
	private String mYYBAppID;
	private String mYYBSplashID;
	private UBADCallback mUBADCallback;
	private SplashADListener mSplashADListener;
	private SplashAD mSplashAD;
	private TextView mSkipView;
	private ImageView mSplashHolder;
	private ImageView mAppLogo;
	private FrameLayout mSplashContainer;

	private static final String SKIP_TEXT = "点击跳过 %d";
	private final int REQUEST_PERMISSION=1024;
	private long loadSplashADStartTime;
	
	/**
	 * 闪屏最小展示时间
	 */
	private final long minSplashTimeWhenNoAD = 4000;
	private Handler mMainHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);

        // 必须设定固定屏幕方向
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置屏幕的横方向
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置屏幕的竖方向

		setContentView(ResUtil.getLayoutId(this,"activity_ad_yyb_splash"));
		mYYBAppID = UBSDKConfig.getInstance().getParamMap().get("AD_YYB_APP_ID");
		mYYBSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_YYB_Splash_ID");
		
		mUBADCallback = UBAD.getInstance().getUBADCallback();
		
		initViewAndListener();
		
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {//系统版本>=23的时候，需要处理动态权限问题 
			checkAndRequestPermission();
		}else{//系统版本<23直接，拉去闪屏广告
			showSplashAD();
		}
	}

	/**
	 * 初始化视图和监听
	 */
	private void initViewAndListener() {
		mSkipView = (TextView) findViewById(ResUtil.getViewID(this,"ad_yyb_splash_skip_view"));
		mSplashHolder = (ImageView) findViewById(ResUtil.getViewID(this,"ad_yyb_splash_holder"));
		mAppLogo = (ImageView) findViewById(ResUtil.getViewID(this,"ad_yyb_splash_app_logo"));
		mSplashContainer = (FrameLayout) findViewById(ResUtil.getViewID(this,"ad_yyb_splash_container"));
		
		mMainHandler = new Handler(Looper.getMainLooper());
		
		mSplashADListener = new SplashADListener(){

			@Override
			public void onADClicked() {
				UBLogUtil.logI(TAG+"----->Splash AD click!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_SPLASH,"Splash AD click!");
				}
			}

			@Override
			public void onADDismissed() {
				UBLogUtil.logI(TAG+"----->Splash AD closed!");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_SPLASH,"Splash AD closed!");
				}
				
				finish();
			}

			@Override
			public void onADExposure() {//广告展示成功
				UBLogUtil.logI(TAG+"----->Splash AD show!");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_SPLASH, "Splash AD show!");
				}
			}

			@Override
			public void onADPresent() {//广告请求成功
				UBLogUtil.logI(TAG+"----->Splash AD present!");
				if (mUBADCallback!=null) {
//					mUBADCallback.onShow(ADType.AD_TYPE_SPLASH,"Splash AD present!");
				}
				
				mSplashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
			}

			@Override
			public void onADTick(long countDownTime) {
				UBLogUtil.logI(TAG+"----->Splash AD countDownTime="+countDownTime);
				mSkipView.setText(String.format(SKIP_TEXT, Math.round(countDownTime / 1000f)));
			}

			@Override
			public void onNoAD(AdError adError) {
				UBLogUtil.logI(TAG+"----->Splash AD error!");
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_SPLASH, "Splash AD failed:msg="+adError.getErrorMsg());
				}
				
				
				long alreadyDelayTime = System.currentTimeMillis()-loadSplashADStartTime;
				long shouldDelayTime=alreadyDelayTime>minSplashTimeWhenNoAD?0:minSplashTimeWhenNoAD-alreadyDelayTime;
				
				mMainHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						finish();
					}
				}, shouldDelayTime);
			}};
	}

	
	/** 
	 * 加载闪屏广告
	 */
	private void showSplashAD(){
		loadSplashADStartTime = System.currentTimeMillis();
		if (mSplashAD==null) {
			mSplashAD = new SplashAD(this,
					mSplashContainer,	//闪屏广告容器
					mSkipView, // 跳过按钮
					mYYBAppID, //     应用id
					mYYBSplashID, //  广告位id
					mSplashADListener,// 监听
					0);
		}
	}
		
	 /**
	  * 检查并请求权限
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
	
	    if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
	      lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
	    }
	
	    // 权限都已经有了，那么直接调用SDK
	    if (lackedPermission.size() == 0) {
	      showSplashAD();
	    } else {
	      // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
	      String[] requestPermissions = new String[lackedPermission.size()];
	      lackedPermission.toArray(requestPermissions);
	      requestPermissions(requestPermissions, REQUEST_PERMISSION);
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
	
	@Override
	protected void onDestroy() {
		mMainHandler.removeCallbacksAndMessages(null);
		mSplashAD=null;
		super.onDestroy();
	}

	/**
	 * 判断是否请求权限成功
	 * @param grantResults
	 * @return
	 */
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
	    if (requestCode == REQUEST_PERMISSION && hasAllPermissionsGranted(grantResults)) {
	      showSplashAD();
	    } else {
	      // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
	      Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
	      Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
	      intent.setData(Uri.parse("package:" + getPackageName()));
	      startActivity(intent);
	      finish();
	    }
	 }
	  
}
