package com.ubsdk.ad.yyb.plugin;

import com.umbrella.game.ubsdk.utils.ResUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * 这里接入的应用宝（广点通）激励视频广告是广点通的原生视频（自渲染）视频广告
 * @author qingshanliao
 */
public class ADYYBRewardVideoActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(ResUtil.getLayoutId(this,""));
		initViewAndListener();
	}

	private void initViewAndListener() {
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
