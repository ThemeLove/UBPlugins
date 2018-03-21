package com.ubsdk.ad.baidu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.duoku.alone.ssp.DuoKuAdSDK;
import com.duoku.alone.ssp.FastenEntity;
import com.duoku.alone.ssp.entity.ViewEntity;
import com.duoku.alone.ssp.listener.ViewClickListener;
import com.duoku.alone.ssp.util.ToastUtil;

public class SplashActivity extends Activity {

    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout parent = new LinearLayout(this);
        parent.setOrientation(LinearLayout.VERTICAL);

        // 去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(parent);
        {
            container = new FrameLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
            params.weight = 1;
            parent.addView(container, params);
        }

        myhander.sendEmptyMessageDelayed(0, 1000);

    }

    private Handler myhander = new Handler() {

        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            getSplashScreenView();
            return super.sendMessageAtTime(msg, uptimeMillis);
        }


    };

    /**
     * 请求闪屏广告
     */
    public void getSplashScreenView() {
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setType(FastenEntity.VIEW_SPLASHSCREEN); // 广告类型 闪屏、全屏广告
        viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL); // 展示方向竖或横
        // viewEntity.setDirection(FastenEntity.VIEW_VERTICAL);
        viewEntity.setSeatId(1000002); // 广告位id
        DuoKuAdSDK.getInstance().showSplashScreenView(this, viewEntity, container, new ViewClickListener() {
            @Override
            public void onSuccess(String bid) {
                ToastUtil.showToast(SplashActivity.this, bid + "广告展示id");
                // SplashActivity.this.finish();
            }

            @Override
            public void onFailed(int ErrorCode) {
                try {
                    ToastUtil.showToast(SplashActivity.this, ErrorCode + "广告展示失败");
                    SplashActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClick(int type) {
                try {
                    if (type == 1) {
                        ToastUtil.showToast(SplashActivity.this, "关闭");
                        // SplashActivity.this.finish();
                    } else if (type == 2) {
                        ToastUtil.showToast(SplashActivity.this, "点击广告");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}