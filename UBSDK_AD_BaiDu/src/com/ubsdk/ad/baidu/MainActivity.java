package com.ubsdk.ad.baidu;

import com.duoku.alone.ssp.DuoKuAdSDK;
import com.duoku.alone.ssp.ErrorCode;
import com.duoku.alone.ssp.FastenEntity;
import com.duoku.alone.ssp.entity.ViewEntity;
import com.duoku.alone.ssp.listener.CallBackListener;
import com.duoku.alone.ssp.listener.InitListener;
import com.duoku.alone.ssp.listener.ViewClickListener;
import com.duoku.alone.ssp.util.ToastUtil;
import com.duoku.code.analytics.ReportAPI;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 广告SDK demo
 */

public class MainActivity extends Activity implements OnClickListener {

    // 上下文对象
    // private Activity context = MainActivity.this;
    public static TextView sLogTextView;
    public static ScrollView sScrollView;
    private FrameLayout adBannerTop;
    private FrameLayout adBannerBottom;
    private MainActivity act;
    private FrameLayout currentBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        act = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 必须设定固定屏幕方向
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置屏幕的横方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置屏幕的竖方向

        setContentView(R.layout.activity_main);
        //设置Banner 容器
        adBannerTop = ((FrameLayout) findViewById(R.id.ad_banner_top));//顶部Banner 展示位置
        adBannerBottom = ((FrameLayout) findViewById(R.id.ad_banner_bottom));//底部Banner 展示位置
        DuoKuAdSDK.getInstance().init(this, new InitListener() {
            @Override
            public void onBack(int code, String desc) {
                //初始化回调
                if (code == ErrorCode.SUCCESS_CODE) {
                    ToastUtil.showToast(MainActivity.this,"初始化成功");
                }else {
                    ToastUtil.showToast(MainActivity.this,"初始化失败"+code+" desc:"+desc);
                }
            }
        });

        findViewById(R.id.but_splashscreen_img).setOnClickListener(this);
        findViewById(R.id.but_banner_top_img).setOnClickListener(this);
        findViewById(R.id.but_banner_hide).setOnClickListener(this);
        findViewById(R.id.but_banner_buttom_img).setOnClickListener(this);
        findViewById(R.id.but_block_center_img).setOnClickListener(this);
        findViewById(R.id.but_fullscreen_video).setOnClickListener(this);
        findViewById(R.id.but_test_crash).setOnClickListener(this);
        findViewById(R.id.but_test).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TestActivity.class));
//                LogUtils.log_release("DEMO",CipherTextUtils.decrypt("c5d8eebafb4f7a74910cf5c6c97bb38ca9b365b504667cfef764452212a0f76bae344aede59638c23a0bd051afe069afb71efd8efbc17f1e89668acc36f0146809d8498c87032d92130bc07d2b6655b27629f8f080480a9ffb1db36ffbacaef029d2217d00dcad42b69b43d97bbc16a2ac0ba3032206637c83007df78c90fdd5950ed776cd734042f96ff4fb1baa03b6fab150a2cd620a4c48e1921ebec92cb5da2c5d563c9267453f91da0e53299ce0e661fba09d189732cf5e9453c3a7e47536fa06c56fb0d7cd39be4ecf2630116be3f3e50f3136831310ca4f82eed98a80bb1d980eff1f844d4db4f6cfde15a38ecf8cfcbe27728a26c0d2051bc0b999e036c34c7ef86e1cafa5cc98da2bcb3137d0a436f2fe9c174660b994a09f5604e253ec4a1874f4294c7b526247543c6e668e27593e95075dd60ac6964dfdb8cf9011b68e521b092fb18e713a518463911a6393ce46a9f38e8a86f2ce93bc825639ffc35fee2ce7b0b487cd9e31401b8bd13bfdb77758d477d6d88d08de7814d6be0fda3cc4cdc9076b9db4d1cbfc93b3e7399540f92752b578a20c10a005199222"));
//                LogUtils.log_release("DEMO",CipherTextUtils.decrypt("{\"code\":\"be87bd5f2ccd51def33c580db01c707f204bc23b7461786c299af01e79b1df1e9baa3abd55a73fd02fcec351f1418df66875128b6d1e374f3fb637f9c931d55dc9d22996adf9589ce8b03014c79a989e7f0ea8c33141ef56c24eb5e1ed92ee7b687b7557dff218e48c4bdb30b38da0be67e0a05c81dc0206075c471a2cac037a25014026e3cc0915eaec0e032033290843a45ba8c622e53db7f77eca71d0385af68e7611bc4eb3a087cdeadc62c534d4cc3863902655dc643cd83e0504a564ff198f9e8e98f0b3c8a99f2fb323f047add0cb205a5062e202ebd65e59070e6c96a4e631607c7602bdef8dd10a2ba85e0dd77c1c07bc9baa94092cf83414b95ce565255725cd2943158de371f49a3d258c79af7658f5873cf8d5a87346ccf2faaa449b65713bfd2ef7b4bba7108b2cc81b3cfb4d8c9ab0c748f2d418285c081cebbb08ff913c34d76712e2388b93484a29762977871fa1e2632a5f9a957200c9605cb4018a4f173e451dcf117956da71d00c5d239824b4b259f6c46e6db7d4306d21d6aaba878dc89c8ee0ec159c5f141649bc024f210e7bc11bf7e8351432325cce12d808a2bbdba07e67949d8c98a2c0cc5b25bda9b7314844a479c977f20fd299429d63d01247f05d633c6133499b4e1e7c719a39a2c78cd6ae6229a760d056170c619665dba236a0914440226a24ac4eff0142cb51014308c37adc2e7318270c4e4a5d27d5c2274bcb5a75209080709f40239afbb4c1937b84bb462993c9a59c915699d5d087590716a1f55af609f0048208f6511aaf60fff4aa9d23b67e28c6ae6242d678fb7f42ee8e6ee2b135a4568ca42ec087753c776b7215e791580518bce548023bb9741a008ce308e19fec2004f354d44cea02f4ef2b1c9a572739\"}"));
            }
        });

    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            // 闪屏广告
            case R.id.but_splashscreen_img:
//                getSplashScreenView();
                this.startActivity(new Intent(this,SplashActivity.class));
                break;
            // 关闭横幅
            case R.id.but_banner_hide:
                hideBanner();
                break;
            // 顶部-横幅-图片
            case R.id.but_banner_top_img:
                hideBanner();
                 getADBanner(FastenEntity.POSTION_TOP);
                break;
            // 底部-横幅-图片
            case R.id.but_banner_buttom_img:
                hideBanner();
                getADBanner(FastenEntity.POSTION_BOTTOM);
                break;
            // 中间-插屏
            case R.id.but_block_center_img:
                 getBlockView();
                break;
            // 视频
            case R.id.but_fullscreen_video:
                getVideoView();
                break;
            case R.id.but_test_crash:
                // 1、ANR
//                CrashReport.testANRCrash();
                // 2、JavaCrash
                ReportAPI.testJavaCrash();
                // 3、NativeCrash
                // CrashReport.testNativeCrash();
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏banner 此处隐藏会导致 banner 不在轮播显示
     */
    private void hideBanner() {
       boolean flag =  DuoKuAdSDK.getInstance().hideBannerView(this,currentBanner);
       if(flag){
           ToastUtil.showToast(this,"横幅关闭成功");
       }else {
           ToastUtil.showToast(this,"没有横幅");
       }
    }

    /**
     * 请求banner广告
     *
     * @param postion
     */
    public void getADBanner(int postion) {
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setType(FastenEntity.VIEW_BANNER); // 展示的类型
        viewEntity.setPostion(postion); // banner展示的位置
        viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL); // 展示方向
        // viewEntity.setDirection(FastenEntity.VIEW_VERTICAL);
        viewEntity.setSeatId(1000000); // 广告位id
        currentBanner = postion==FastenEntity.POSTION_TOP?adBannerTop:adBannerBottom;
        DuoKuAdSDK.getInstance().showBannerView(this, viewEntity,currentBanner, new ViewClickListener() {
            @Override
            public void onSuccess(String bid) {
                ToastUtil.showToast(MainActivity.this,bid+"广告位展示id");
            }

            @Override
            public void onFailed(int ErrorCode) {
                ToastUtil.showToast(MainActivity.this,"广告位展示失败");
            }

            @Override
            public void onClick(int type) {
                if (type == 1) {
                    //调用hideBanner();会取消banner 的轮播显示
                    hideBanner();
                    ToastUtil.showToast(MainActivity.this,"关闭");
                } else if (type == 2) {
                    ToastUtil.showToast(MainActivity.this,"点击广告");
                }
            }
        });
    }

    /**
     * 请求插屏广告
     */
    public void getBlockView() {
        ViewEntity viewEntity = new ViewEntity();
        viewEntity.setType(FastenEntity.VIEW_BLOCK); // 广告类型
        viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL); // 展示方向竖或横
        // viewEntity.setDirection(FastenEntity.VIEW_VERTICAL);
        viewEntity.setSeatId(1000001); // 广告位id
        DuoKuAdSDK.getInstance().showBlockView(this, viewEntity, new ViewClickListener() {
            @Override
            public void onSuccess(String bid) {
                ToastUtil.showToast(MainActivity.this,bid+"广告位展示id");
            }

            @Override
            public void onFailed(int ErrorCode) {
                ToastUtil.showToast(MainActivity.this,"广告位展示失败");
            }

            @Override
            public void onClick(int type) {
                if (type == 1) {
                    ToastUtil.showToast(MainActivity.this,"关闭");
                } else if (type == 2) {
                    ToastUtil.showToast(MainActivity.this,"点击广告");
                }
            }
        });
    }

    /**
     * 请求激励视频
     */
    public void getVideoView() {
        final ViewEntity viewEntity = new ViewEntity();
        viewEntity.setType(FastenEntity.VIEW_VIDEO);
//        viewEntity.setDirection(FastenEntity.VIEW_HORIZONTAL); // 展示方向竖或横
         viewEntity.setDirection(FastenEntity.VIEW_VERTICAL); // 展示方向竖或横
        viewEntity.setSeatId(1000003); // 广告位id
        //请求视频缓存
        DuoKuAdSDK.getInstance().cacheVideo(this, viewEntity, new CallBackListener() {
            @Override
            public void onReady() {
                Log.i("AD","缓存成功");
                //bofang
                DuoKuAdSDK.getInstance().showVideoImmediate(act,viewEntity);
            }

            @Override
            public void onComplete() {
                Log.i("AD","视频播放完成,可以获取奖励");
            }

            @Override
            public void onClick(int type) {
                Log.i("AD",type==FastenEntity.VIEW_CLOSE?"关闭":"点击广告");
                ToastUtil.showToast(act,type==FastenEntity.VIEW_CLOSE?"关闭":"点击广告");
            }

            @Override
            public void onFailMsg(String msg) {
                ToastUtil.showToast(act,"Error:"+msg);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
    }

    @Override
    protected void onDestroy() {
        DuoKuAdSDK.getInstance().onDestoryBanner();
        DuoKuAdSDK.getInstance().onDestoryBlock();
        DuoKuAdSDK.getInstance().onDestoryVideo();
        super.onDestroy();
    }

}
