package com.ubsdk.ad.baidu;

import com.duoku.alone.ssp.util.ToastUtil;
import com.duoku.code.analytics.ReportAPI;
import com.umbrella.game.ubsdk.UBSDK;
import com.umbrella.game.ubsdk.callback.UBADCallback;
import com.umbrella.game.ubsdk.callback.UBInitCallback;
import com.umbrella.game.ubsdk.callback.UBSwitchAccountCallback;
import com.umbrella.game.ubsdk.pluginimpl.UBAD;
import com.umbrella.game.ubsdk.plugintype.ad.ADType;
import com.umbrella.game.ubsdk.plugintype.user.UBUserInfo;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class UBSDK_AD_BaiDu_MainActivity extends Activity implements OnClickListener{
	private final String TAG=UBSDK_AD_BaiDu_MainActivity.class.getSimpleName();

    private EditText mADTypeOrMethod;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 必须设定固定屏幕方向
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 设置屏幕的横方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置屏幕的竖方向

        setContentView(R.layout.activity_main_ad_demo);
        //设置Banner 容器
        setSDKListener();
        
        UBSDK.getInstance().init(this,new UBInitCallback() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailed(String message, String trace) {
				// TODO Auto-generated method stub
				
			}
		});
        
        mADTypeOrMethod = (EditText) findViewById(R.id.et_ADTypeOrMethodInput);
        
        findViewById(R.id.but_splashscreen_img).setOnClickListener(this);
        findViewById(R.id.but_banner_top_img).setOnClickListener(this);
        findViewById(R.id.but_banner_hide).setOnClickListener(this);
        findViewById(R.id.but_banner_buttom_img).setOnClickListener(this);
        findViewById(R.id.but_block_center_img).setOnClickListener(this);
        findViewById(R.id.but_fullscreen_video).setOnClickListener(this);
        findViewById(R.id.but_isSupportADType).setOnClickListener(this);
        findViewById(R.id.but_isSupportMethod).setOnClickListener(this);
        
        findViewById(R.id.but_test_crash).setOnClickListener(this);
        findViewById(R.id.but_test).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(UBSDK_AD_BaiDu_MainActivity.this,TestActivity.class));
//                LogUtils.log_release("DEMO",CipherTextUtils.decrypt("c5d8eebafb4f7a74910cf5c6c97bb38ca9b365b504667cfef764452212a0f76bae344aede59638c23a0bd051afe069afb71efd8efbc17f1e89668acc36f0146809d8498c87032d92130bc07d2b6655b27629f8f080480a9ffb1db36ffbacaef029d2217d00dcad42b69b43d97bbc16a2ac0ba3032206637c83007df78c90fdd5950ed776cd734042f96ff4fb1baa03b6fab150a2cd620a4c48e1921ebec92cb5da2c5d563c9267453f91da0e53299ce0e661fba09d189732cf5e9453c3a7e47536fa06c56fb0d7cd39be4ecf2630116be3f3e50f3136831310ca4f82eed98a80bb1d980eff1f844d4db4f6cfde15a38ecf8cfcbe27728a26c0d2051bc0b999e036c34c7ef86e1cafa5cc98da2bcb3137d0a436f2fe9c174660b994a09f5604e253ec4a1874f4294c7b526247543c6e668e27593e95075dd60ac6964dfdb8cf9011b68e521b092fb18e713a518463911a6393ce46a9f38e8a86f2ce93bc825639ffc35fee2ce7b0b487cd9e31401b8bd13bfdb77758d477d6d88d08de7814d6be0fda3cc4cdc9076b9db4d1cbfc93b3e7399540f92752b578a20c10a005199222"));
//                LogUtils.log_release("DEMO",CipherTextUtils.decrypt("{\"code\":\"be87bd5f2ccd51def33c580db01c707f204bc23b7461786c299af01e79b1df1e9baa3abd55a73fd02fcec351f1418df66875128b6d1e374f3fb637f9c931d55dc9d22996adf9589ce8b03014c79a989e7f0ea8c33141ef56c24eb5e1ed92ee7b687b7557dff218e48c4bdb30b38da0be67e0a05c81dc0206075c471a2cac037a25014026e3cc0915eaec0e032033290843a45ba8c622e53db7f77eca71d0385af68e7611bc4eb3a087cdeadc62c534d4cc3863902655dc643cd83e0504a564ff198f9e8e98f0b3c8a99f2fb323f047add0cb205a5062e202ebd65e59070e6c96a4e631607c7602bdef8dd10a2ba85e0dd77c1c07bc9baa94092cf83414b95ce565255725cd2943158de371f49a3d258c79af7658f5873cf8d5a87346ccf2faaa449b65713bfd2ef7b4bba7108b2cc81b3cfb4d8c9ab0c748f2d418285c081cebbb08ff913c34d76712e2388b93484a29762977871fa1e2632a5f9a957200c9605cb4018a4f173e451dcf117956da71d00c5d239824b4b259f6c46e6db7d4306d21d6aaba878dc89c8ee0ec159c5f141649bc024f210e7bc11bf7e8351432325cce12d808a2bbdba07e67949d8c98a2c0cc5b25bda9b7314844a479c977f20fd299429d63d01247f05d633c6133499b4e1e7c719a39a2c78cd6ae6229a760d056170c619665dba236a0914440226a24ac4eff0142cb51014308c37adc2e7318270c4e4a5d27d5c2274bcb5a75209080709f40239afbb4c1937b84bb462993c9a59c915699d5d087590716a1f55af609f0048208f6511aaf60fff4aa9d23b67e28c6ae6242d678fb7f42ee8e6ee2b135a4568ca42ec087753c776b7215e791580518bce548023bb9741a008ce308e19fec2004f354d44cea02f4ef2b1c9a572739\"}"));
            }
        });

    }

    private void setSDKListener() {
//    	设置切换账号监听
    	UBSDK.getInstance().setUBSwitchAccountCallback(new UBSwitchAccountCallback() {
			
			@Override
			public void onSuccess(UBUserInfo ubUserInfo) {
				UBLogUtil.logI(TAG+"----->switchAccount----->onSuccess");
			}
			
			@Override
			public void onFailed(String message, String trace) {
				UBLogUtil.logI(TAG+"----->switchAccount----->onFailed");
			}
			
			@Override
			public void onCancel() {
				UBLogUtil.logI(TAG+"----->switchAccount----->onCancel");
			}
		});
    	
//    	设置广告监听
    	UBAD.getInstance().setUBADCallback(new UBADCallback(){

			@Override
			public void onInit(boolean isInitSuccess, String msg) {
				UBLogUtil.logI(TAG+"----->UBAD----->onInit");
				if (isInitSuccess) {
					UBLogUtil.logI(TAG+"----->UBAD----->init success!");
				}else{
					UBLogUtil.logI(TAG+"----->UBAD----->init fail!");
				}
			}

			@Override
			public void onClick(int adType, String msg) {
				UBLogUtil.logI(TAG+"----->UBAD----->onClick");
				switch (adType) {
				case ADType.AD_TYPE_BANNER:
					UBLogUtil.logI(TAG+"----->UBAD----->onClick banner AD");
					break;
				case ADType.AD_TYPE_FULLSCREEN:
					UBLogUtil.logI(TAG+"----->UBAD----->onClick fullscreen AD");
					break;
				case ADType.AD_TYPE_REWARDEDVIDEO:
					UBLogUtil.logI(TAG+"----->UBAD----->onClick rewardVideo AD");
					break;
				case ADType.AD_TYPE_SPLASH:
					UBLogUtil.logI(TAG+"----->UBAD----->onClick splash AD");
					break;

				default:
					break;
				}
			}

			@Override
			public void onComplete(int adType, String msg) {
				switch (adType) {
				case ADType.AD_TYPE_BANNER:
					UBLogUtil.logI(TAG+"----->UBAD----->onComplete banner AD");
					break;
				case ADType.AD_TYPE_FULLSCREEN:
					UBLogUtil.logI(TAG+"----->UBAD----->onComplete fullscreen AD");
					break;
				case ADType.AD_TYPE_REWARDEDVIDEO:
					UBLogUtil.logI(TAG+"----->UBAD----->onComplete rewardVideo AD");
					break;
				case ADType.AD_TYPE_SPLASH:
					UBLogUtil.logI(TAG+"----->UBAD----->onComplete splash AD");
					break;

				default:
					break;
				}
			}

			@Override
			public void onShow(int adType, String msg) {
				switch (adType) {
				case ADType.AD_TYPE_BANNER:
					UBLogUtil.logI(TAG+"----->UBAD----->onShow banner AD");
					break;
				case ADType.AD_TYPE_FULLSCREEN:
					UBLogUtil.logI(TAG+"----->UBAD----->onShow fullscreen AD");
					break;
				case ADType.AD_TYPE_REWARDEDVIDEO:
					UBLogUtil.logI(TAG+"----->UBAD----->onShow rewardVideo AD");
					break;
				case ADType.AD_TYPE_SPLASH:
					UBLogUtil.logI(TAG+"----->UBAD----->onShow splash AD");
					break;

				default:
					break;
				}
			}

			@Override
			public void onClosed(int adType, String msg) {
				switch (adType) {
				case ADType.AD_TYPE_BANNER:
					UBLogUtil.logI(TAG+"----->UBAD----->onClosed banner AD");
					break;
				case ADType.AD_TYPE_FULLSCREEN:
					UBLogUtil.logI(TAG+"----->UBAD----->onClosed fullscreen AD");
					break;
				case ADType.AD_TYPE_REWARDEDVIDEO:
					UBLogUtil.logI(TAG+"----->UBAD----->onClosed rewardVideo AD");
					break;
				case ADType.AD_TYPE_SPLASH:
					UBLogUtil.logI(TAG+"----->UBAD----->onClosed splash AD");
					break;

				default:
					break;
				}
			}

			@Override
			public void onFailed(int adType, String msg) {
				switch (adType) {
				case ADType.AD_TYPE_BANNER:
					UBLogUtil.logI(TAG+"----->UBAD----->onFailed banner AD");
					break;
				case ADType.AD_TYPE_FULLSCREEN:
					UBLogUtil.logI(TAG+"----->UBAD----->onFailed fullscreen AD");
					break;
				case ADType.AD_TYPE_REWARDEDVIDEO:
					UBLogUtil.logI(TAG+"----->UBAD----->onFailed rewardVideo AD");
					break;
				case ADType.AD_TYPE_SPLASH:
					UBLogUtil.logI(TAG+"----->UBAD----->onFailed splash AD");
					break;

				default:
					break;
				}
			}});
		
	}

	public void onClick(View arg0) {
        switch (arg0.getId()) {
            // 闪屏广告
            case R.id.but_splashscreen_img:
            	UBAD.getInstance().showADWithADType(ADType.AD_TYPE_SPLASH);
                break;
            // 关闭横幅
            case R.id.but_banner_hide:
                UBAD.getInstance().hideADWithADType(ADType.AD_TYPE_BANNER);
                break;
            // 顶部-横幅-图片
            case R.id.but_banner_top_img:
            	UBAD.getInstance().showADWithADType(ADType.AD_TYPE_BANNER);
                break;
            // 底部-横幅-图片
            case R.id.but_banner_buttom_img:
            	UBAD.getInstance().showADWithADType(ADType.AD_TYPE_BANNER);
                break;
            // 中间-插屏
            case R.id.but_block_center_img:
            	UBAD.getInstance().showADWithADType(ADType.AD_TYPE_FULLSCREEN);
                break;
            // 视频
            case R.id.but_fullscreen_video:
            	UBAD.getInstance().showADWithADType(ADType.AD_TYPE_REWARDEDVIDEO);
                break;
            case R.id.but_isSupportADType:
            	String ADTypeStr = mADTypeOrMethod.getText().toString().trim();
            	boolean supportADType = UBAD.getInstance().isSupportADType(Integer.parseInt(ADTypeStr));
            	String supportADTypeStr=supportADType==true?"支持":"不支持";
            	ToastUtil.showToast(UBSDK_AD_BaiDu_MainActivity.this, supportADTypeStr);
            	
            	break;
            case R.id.but_isSupportMethod:
            	String MethodStr = mADTypeOrMethod.getText().toString().trim();
            	boolean supportMethod = UBAD.getInstance().isSupportMethod(MethodStr,null);
            	String supportMethodStr=supportMethod==true?"支持":"不支持";
            	ToastUtil.showToast(UBSDK_AD_BaiDu_MainActivity.this, supportMethodStr);
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

    @Override
    public void onBackPressed() {
    	UBSDK.getInstance().onBackPressed();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
    	UBSDK.getInstance().onDestroy();
        super.onDestroy();
    }

}
