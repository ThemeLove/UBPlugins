package com.ubsdk.ad.baidu.plugin;

import com.duoku.alone.ssp.DuoKuAdSDK;
import com.duoku.alone.ssp.FastenEntity;
import com.duoku.alone.ssp.entity.ViewEntity;
import com.duoku.alone.ssp.listener.ViewClickListener;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.utils.TextUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ADBaiDuSplashActivity extends Activity {
	private static final String TAG=ADBaiDuSplashActivity.class.getSimpleName();
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
		mSplashID = UBSDKConfig.getInstance().getParamMap().get("AD_BaiDu_Splash_ID");
		mGameOrientation = UBSDKConfig.getInstance().getParamMap().get("BaiDu_Game_Orientation");
		mUBADCallback = UBAD.getInstance().getUBADCallback();
	}

	private Handler myhander = new Handler() {
        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        	showSplashAD();
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    };
    
	private String mSplashID;
	private String mGameOrientation;
	private UBADCallback mUBADCallback;

    /**
     * 请求闪屏广告
     */
    public void showSplashAD() {
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setType(FastenEntity.VIEW_SPLASHSCREEN); // 广告类型 闪屏、全屏广告
        if (TextUtil.equals(mGameOrientation, "horizontal")) {
        	viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL); // 展示方向横
		}else{
			viewEntity.setDirection(FastenEntity.VIEW_VERTICAL); // 展示方向竖
		}
        viewEntity.setSeatId(Integer.parseInt(mSplashID)); // 广告位id
        
        DuoKuAdSDK.getInstance().showSplashScreenView(this, viewEntity, container, new ViewClickListener() {
            @Override
            public void onSuccess(String adID) {
            	UBLogUtil.logI(TAG+"----->showSplashAD----->onSuccess----->adID="+adID);
            	if (mUBADCallback!=null) {
            		mUBADCallback.onShow(ADType.AD_TYPE_SPLASH, "Splash AD show success!");
				}
            }

            @Override
            public void onFailed(int errorCode) {
            	UBLogUtil.logI(TAG+"----->showSplashAD----->onFailed----->errorCode="+errorCode);
                try {
                	if (mUBADCallback!=null) {
                		mUBADCallback.onFailed(ADType.AD_TYPE_SPLASH, "Splash AD show Failed:errorCode="+errorCode);
					}
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClick(int type) {
                try {
                    if (type == 1) {//用户关闭
                    	UBLogUtil.logI(TAG+"----->showSplashAD----->onClick-----type=1,close");
                    	if (mUBADCallback!=null) {
                    		mUBADCallback.onClosed(ADType.AD_TYPE_SPLASH, "Splash AD closed!");
        				}
                        finish();
                    } else if (type == 2) {//用户点击
                    	UBLogUtil.logI(TAG+"----->showSplashAD----->onClick-----type=2,user click");
                    	if (mUBADCallback!=null) {
                    		mUBADCallback.onClick(ADType.AD_TYPE_SPLASH, "Splash AD user click!");
						}
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
//    	闪屏广告倒计时结束自己结束，这里屏蔽返回键
//    	return;
    	super.onBackPressed();
    }
}