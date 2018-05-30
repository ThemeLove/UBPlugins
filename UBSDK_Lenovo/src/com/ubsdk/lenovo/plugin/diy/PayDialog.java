package com.ubsdk.lenovo.plugin.diy;

import java.util.ArrayList;

import com.umbrella.game.ubsdk.utils.DisplayUtil;
import com.umbrella.game.ubsdk.utils.ResUtil;
import com.umbrella.game.ubsdk.utils.TextUtil;
import com.umbrella.game.ubsdk.utils.UBLogUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class PayDialog extends Dialog{
	
	private Context mContext; 
	private LayoutInflater mInflater;
	private View mContainer;
	private ImageView mCloseBtn;
	private TextView mPayOrderInfo;
	private LinearLayout mPayMethodItemContainer;
	private TextView mLogo;

	public PayDialog(Context context) {
		super(context,ResUtil.getStyleId(context,"LenovoPayDialog"));
		this.mContext=context;
		
		mInflater = LayoutInflater.from(context);
		
		initView();
		setListener();
	}

	private void initView() {
		mContainer = mInflater.inflate(ResUtil.getLayoutId(mContext, "lenovo_pay_dialog_pay"), null);
		mCloseBtn = (ImageView) mContainer.findViewById(ResUtil.getViewID(mContext,"img_close"));
		TextView title = (TextView) mContainer.findViewById(ResUtil.getViewID(mContext,"tv_payTitle"));
		mPayOrderInfo = (TextView) mContainer.findViewById(ResUtil.getViewID(mContext,"tv_payOrderInfo"));
		mPayMethodItemContainer = (LinearLayout) mContainer.findViewById(ResUtil.getViewID(mContext,"ll_payMethodItemContainer"));
		mLogo = (TextView) mContainer.findViewById(ResUtil.getViewID(mContext,"tv_logo"));
		
		title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));  
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(mContainer);
        show();
        
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        
        Window w = getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        int screenWidth = DisplayUtil.screenWidth;
        int screenHeight = DisplayUtil.screenHeight;
        boolean isHorizontal = DisplayUtil.isHorizontal();
        if (isHorizontal) {
			lp.width=screenHeight*4/5;
//			lp.height=screenHeight*4/5;
		}else{
			lp.width=screenWidth*4/5;
//			lp.height=screenHeight*3/5;s
		}
        
        lp.gravity = Gravity.CENTER;
//        onWindowAttributesChanged(lp);
        w.setAttributes(lp);

        UBLogUtil.logI("screenWidth="+screenWidth);
        UBLogUtil.logI("screenHeight="+screenHeight);
        UBLogUtil.logI("lp.width="+lp.width);
        UBLogUtil.logI("lp.height="+lp.height);
	}
	
	private void setListener() {
		mCloseBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mPayDialogClickListener!=null) {
					mPayDialogClickListener.onClose();
				}
			}
		});
	}

	private PayDialogClickListener mPayDialogClickListener;
	
	public void setPayDialogClickListener(PayDialogClickListener payDialogClickListener) {
		this.mPayDialogClickListener=payDialogClickListener;
	}
	
	/**
	 * 当前选中的PayType
	 */
	private PayMethodItem mCurrentPayMethodItem;
	
	public PayMethodItem getCurrentPayMethodItem(){
		return mCurrentPayMethodItem;
	}
	
	private ArrayList<PayMethodItem> mPayMethodItemList;
	public void setPayMethodItemList(ArrayList<PayMethodItem> payMethodItemList){
		mPayMethodItemList=payMethodItemList;
		if (mPayMethodItemList!=null) {
			mPayMethodItemContainer.removeAllViews();
			for (PayMethodItem payMethodItem : payMethodItemList) {
				final PayMethodItemView payMethodItemView = new PayMethodItemView(mContext);
				payMethodItemView.setPayMethodItem(payMethodItem);
				
				LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(80), DisplayUtil.dip2px(100));
				layoutParams.rightMargin=DisplayUtil.dip2px(3);
				
				mPayMethodItemContainer.addView(payMethodItemView,layoutParams);
				
				payMethodItemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mCurrentPayMethodItem = payMethodItemView.getPayMethodItem();
//						重新构造数据更新视图
						updatePayMethodItemList(mCurrentPayMethodItem);
						setPayMethodItemList(mPayMethodItemList);
//						if (mPayDialogClickListener!=null) {
//							mPayDialogClickListener.OnPayMethodItemClick(mCurrentPayMethodItem);
//						}
					}
				});
			}
		}
		
//		更新支付按钮状态
		for (PayMethodItem payMethodItem : payMethodItemList) {
			if (payMethodItem.isSelect()) {
				mCurrentPayMethodItem = payMethodItem;
				updatePayBtnStatus(payMethodItem);
			}
		}
	}
	
	private void updatePayMethodItemList(PayMethodItem selectMethodItem){
		if (mPayMethodItemList==null||mPayMethodItemList.size()<=0) {
			return;
		}
		for (PayMethodItem payMethodItem : mPayMethodItemList) {
			if (payMethodItem.getID()==selectMethodItem.getID()) {
				payMethodItem.setSelect(true);
			}else{
				payMethodItem.setSelect(false);
			}
		}
	}
	
	/**
	 * 更新支付信息
	 * @param ubUserInfo
	 * @param orderInfo
	 */
	public void updatePayInfoStatus(String sdkLogoStr,String userName,String productName,double amount){
		mLogo.setText(sdkLogoStr);
		if (TextUtil.isEmpty(userName)) {
			mPayUserName.setVisibility(View.GONE);
		}else{
			mPayUserName.setVisibility(View.VISIBLE);
			SpannableStringBuilder sb = new SpannableStringBuilder();
			sb.append(userName);
			sb.setSpan(new ForegroundColorSpan(0xFFFF6600), 0, userName.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
			mPayUserName.setText(sb);
		}
		
		if (TextUtil.isEmpty(productName)&&amount<=0) {
			mPayOrderInfo.setVisibility(View.GONE);
		}else{
			mPayOrderInfo.setVisibility(View.VISIBLE);
			SpannableStringBuilder sb = new SpannableStringBuilder();
			sb.append(productName+"（￥"+amount+"元）");
			sb.setSpan(new ForegroundColorSpan(0xFFFF6600), 0,productName.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
			mPayOrderInfo.setText(sb);
		}
	}
}
