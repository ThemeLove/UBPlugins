package com.ubsdk.ad.baidu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
//
///**
// * ========================================
// * Created by zhaokai on 2018/1/5.
// * Email zhaokai1033@126.com
// * des:
// * SDK功能自测
// * ========================================
// */
//
public class TestActivity extends Activity {

    private EditText content;
    private TestActivity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test);
        act = this;
//        initView();
    }

//    private void initView() {
//
//        content = ((EditText) findViewById(R.id.content));
//        content.setHint("当前请求地址："+ TestUtil.getDateUrl(TestActivity.this)+"\n:如需修改请输入：");
//
//        //测试玉米
//        testYM();
//        //测试玩转
//        testWZ();
//        //测试蓝莓
//        testLM();
//        //测试和传媒
//        testHC();
//        //测试地址
//        testData();
//    }
//
//    private void testYM() {
//        findViewById(R.id.but_ym_4).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                YumiAdHelper.getInstance().cacheVideo(act, new CallBackListener() {
//                    @Override
//                    public void onReady() {
//                        Log.i("AD","准备");
//                        ToastUtil.showToast(act,"视频准备完成");
//                        YumiAdHelper.getInstance().showVideo(act);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.i("AD","视频播放完成");
//                        ToastUtil.showToast(act,"视频播放完成");
//                    }
//
//                    @Override
//                    public void onClick(int type) {
//                        Log.i("AD",(type==FastenEntity.VIEW_CLOSE?"关闭":"广告点击"));
//                        ToastUtil.showToast(act,type==FastenEntity.VIEW_CLOSE?"关闭":"广告点击");
//                    }
//
//                    @Override
//                    public void onFailMsg(String msg) {
//                        Log.i("AD","视频加载失败："+msg);
//                        ToastUtil.showToast(act,"视频加载失败："+msg);
//                    }
//                });
//            }
//        });
//    }
//
//    private void testWZ() {
//        //视频
//        findViewById(R.id.but_wz_4).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                WzAdHelper.getInstance().cacheVideo(act, new CallBackListener() {
//                    @Override
//                    public void onReady() {
//                        ToastUtil.showToast(act,"视频准备完成");
//                        WzAdHelper.getInstance().showVideo(act);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        ToastUtil.showToast(act,"视频播放完成");
//                    }
//
//                    @Override
//                    public void onClick(int type) {
//                        ToastUtil.showToast(act,type==FastenEntity.VIEW_CLOSE?"关闭":"广告点击");
//                    }
//
//                    @Override
//                    public void onFailMsg(String msg) {
//                        ToastUtil.showToast(act,"视频加载失败："+msg);
//                    }
//                });
//            }
//        });
//    }
//
//    private void testData() {
//        ((CheckBox) findViewById(R.id.cb_dev)).setOnCheckedChangeListener(cbListener);
//        ((CheckBox) findViewById(R.id.cb_test)).setOnCheckedChangeListener(cbListener);
//        ((CheckBox) findViewById(R.id.cb_online)).setOnCheckedChangeListener(cbListener);
//        ((CheckBox) findViewById(R.id.cb_own)).setOnCheckedChangeListener(cbListener);
//    }
//
//    private void testHC() {
//        findViewById(R.id.but_hc_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HcAdHelper.getInstance().showBanner(TestActivity.this,null,null);
//            }
//        });
//
//        findViewById(R.id.but_hc_2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HcAdHelper.getInstance().showInterval(TestActivity.this,null,null);
//            }
//        });
//
//        findViewById(R.id.but_hc_3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HcAdHelper.getInstance().showSplash(TestActivity.this,null,new ViewClickListener() {
//                    @Override
//                    public void onSuccess(String bid) {
//
//                    }
//
//                    @Override
//                    public void onFailed(int ErrorCode) {
//
//                    }
//
//                    @Override
//                    public void onClick(int type) {
//                        if(type== FastenEntity.VIEW_CLOSE){
//                            setContentView(R.layout.activity_test);
//                            initView();
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    private void testLM() {
//        findViewById(R.id.but_lm_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                final boolean isFull = ((CheckBox) findViewById(R.id.is_full)).isChecked();
//                final boolean isLandScape = ((CheckBox) findViewById(R.id.is_Land)).isChecked();
//                LmAdHelper.getInstance().cacheVideo(act, new CallBackListener() {
//                    @Override
//                    public void onFailMsg(String msg) {
//                        ToastUtil.showToast(act,"播放失败："+msg);
//                    }
//                    @Override
//                    public void onClick(int type) {
//                        ToastUtil.showToast(act,FastenEntity.VIEW_CLOSE==type?"关闭":"点击广告");
//                    }
//
//                    @Override
//                    public void onReady() {
//                        ToastUtil.showToast(act,"准备完成可以播放");
//                        LmAdHelper.getInstance().show(act,isLandScape,isFull);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        ToastUtil.showToast(act,"完整播放");
//                    }
//                });
//            }
//        });
//    }
//
//    private CompoundButton.OnCheckedChangeListener cbListener = new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if(isChecked){
//                String title = buttonView.getText().toString();
//                if(!TextUtils.isEmpty(title)){
//                    String[] ss = title.split("_");
//                    if(ss.length>1){
//                        TestUtil.setDateUrl(TestActivity.this,"",ss[1]);
//                    }else {
//                        ToastUtil.showToast(TestActivity.this,"请在上方输入");
//                    }
//
//                }
//            }
//        }
//    };
//
}
