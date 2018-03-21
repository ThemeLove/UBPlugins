package com.ubsdk.ad.baidu;

import android.app.Application;

import com.duoku.alone.ssp.DuoKuAdSDK;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DuoKuAdSDK.getInstance().initApplication(this);

        // 设置是否是线上地址，默认或者true为线上地址，false为测试地址
        // 必须在init之前设置
        DuoKuAdSDK.getInstance().setOnline(false,this);
        // 开启调试日志 默认false
        DuoKuAdSDK.getInstance().setDebug(true);

    }
}