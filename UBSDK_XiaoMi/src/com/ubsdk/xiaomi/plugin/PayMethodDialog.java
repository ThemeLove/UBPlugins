package com.ubsdk.xiaomi.plugin;

import com.umbrella.game.ubsdk.utils.ResUtil;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class PayMethodDialog extends Dialog{
	private final String TAG=PayMethodDialog.class.getSimpleName();
	private Context mContext;
	private LayoutInflater mInflater;
	public PayMethodDialog(Context context) {
		super(context,ResUtil.getStyleId(context,"UBSDKPayDialog"));
		this.mContext=context;
		mInflater = LayoutInflater.from(context);
		
		initView();
		setListener();
	}

	private void initView() {
		View mContainer = mInflater.inflate(ResUtil.getStyleId(mContext,""),null);
		
	}
	
	private void setListener() {
		// TODO Auto-generated method stub
		
	}
}
