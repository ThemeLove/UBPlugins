package com.ubsdk.ad.baidu.plugin;

import com.duoku.alone.ssp.DuoKuAdSDK;
import com.duoku.alone.ssp.FastenEntity;
import com.duoku.alone.ssp.entity.ViewEntity;
import com.duoku.alone.ssp.listener.ViewClickListener;
import com.duoku.alone.ssp.util.ToastUtil;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class BaiDuADSplashActivity extends Activity {
	private static final String TAG=BaiDuADSplashActivity.class.getSimpleName();
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
        
        myhander.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * 加载广告参数
     */
    private void loadADParams() {
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_BaiDu_SplashID");
	}

	private Handler myhander = new Handler() {
        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        	showSplashAD();
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    };
    
	private String mSplashID;

    /**
     * 请求闪屏广告
     */
    public void showSplashAD() {
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setType(FastenEntity.VIEW_SPLASHSCREEN); // 广告类型 闪屏、全屏广告
        viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL); // 展示方向竖或横
        // viewEntity.setDirection(FastenEntity.VIEW_VERTICAL);
        viewEntity.setSeatId(Integer.parseInt(mSplashID)); // 广告位id
        DuoKuAdSDK.getInstance().showSplashScreenView(this, viewEntity, container, new ViewClickListener() {
            @Override
            public void onSuccess(String adID) {
            	UBLogUtil.logI(TAG+"----->showSplashAD----->onSuccess----->adID="+adID);
            	UBAD.getInstance().getUBADCallback().onShow(ADType.AD_TYPE_SPLASH, "Splash AD show success!");
            }

            @Override
            public void onFailed(int errorCode) {
            	UBLogUtil.logI(TAG+"----->showSplashAD----->onFailed----->errorCode="+errorCode);
                try {
                	UBAD.getInstance().getUBADCallback().onFailed(ADType.AD_TYPE_SPLASH, "Splash AD show Failed:errorCode="+errorCode);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClick(int type) {
                try {
                    if (type == 1) {//用户关闭
                    	UBLogUtil.logI(TAG+"----->showSplashAD----->onClick-----type=1,user close");
                    	UBAD.getInstance().getUBADCallback().onClosed(ADType.AD_TYPE_SPLASH, "Splash AD　user closed!");
                        finish();
                    } else if (type == 2) {//用户点击
                    	UBLogUtil.logI(TAG+"----->showSplashAD----->onClick-----type=2,user click");
                        ToastUtil.showToast(BaiDuADSplashActivity.this, "点击广告");
                        UBAD.getInstance().getUBADCallback().onClick(ADType.AD_TYPE_SPLASH, "Splash AD user click!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        DuoKuAdSDK.getInstance().onDestorySplash();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	finish();
    }
}