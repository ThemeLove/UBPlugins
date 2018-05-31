package com.ubsdk.ad.lenovo.plugin;


import com.lestore.ad.sdk.Splash;
import com.lestore.ad.sdk.listener.SplashListener;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ADLenovoSplashActivity extends Activity {
	private static final String TAG=ADLenovoSplashActivity.class.getSimpleName();
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);
        
        container = new FrameLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        parent.addView(container, params);
        
        setContentView(parent);
        
//      加载广告参数
        loadADParams();
        
        showSplashAD();
    }

    /**
     * 加载广告参数
     */
    private void loadADParams() {
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_Lenovo_Splash_ID");
		mUBADCallback = UBAD.getInstance().getUBADCallback();
	}

    
	private String mSplashID;

	public boolean canJump = false;
	private Splash mSplashAD;
	private UBADCallback mUBADCallback;
    /**
     * 请求闪屏广告
     */
    public void showSplashAD() {
    	mSplashAD = new Splash(this, container, mSplashID, new SplashListener() {
			
			@Override
			public void onSplashShowSuccess() {
				UBLogUtil.logI(TAG+"----->splashListener----->success!");
				if (mUBADCallback!=null) {
					mUBADCallback.onShow(ADType.AD_TYPE_REWARDVIDEO, "splash AD show!");
				}
			}
			
			@Override
			public void onSplashRequestFailed(String code) {
//				/** 如果加载广告失败，则直接跳转 */
//				startActivity(new Intent(ADLenovoSplashActivity.this, SplashIntentActivity.class));
				UBLogUtil.logI(TAG+"----->splashListener----->Failed:code="+code);
				if (mUBADCallback!=null) {
					mUBADCallback.onFailed(ADType.AD_TYPE_SPLASH, code);
				}
				finish();
			}
			
			@Override
			public void onSplashDismiss() {
				UBLogUtil.logI(TAG+"----->splashListener----->splashDismiss");
				if (mUBADCallback!=null) {
					mUBADCallback.onClosed(ADType.AD_TYPE_REWARDVIDEO, "dismiss");
				}
				finish();
//				next();
			}
			
			@Override
			public void onSplashClick() {
				UBLogUtil.logI(TAG+"----->splashListener----->splashClick");
				if (mUBADCallback!=null) {
					mUBADCallback.onClick(ADType.AD_TYPE_REWARDVIDEO,"click");
				}
			}
		});
    }
    
/*	private void next() {
		if (canJump) {
			this.startActivity(new Intent(this, SplashIntentActivity.class));
			this.finish();
		} else {
			canJump = true;
		}
	}*/

/*	@Override
	protected void onPause() {
		super.onPause();
		canJump = false;
	}*/

/*	@Override
	protected void onResume() {
		super.onResume();
		if (canJump) {
			next();
		}
		canJump = true;
	}
*/
	@Override
	protected void onDestroy() {
		mSplashAD.destroy();
		super.onDestroy();
	}
}