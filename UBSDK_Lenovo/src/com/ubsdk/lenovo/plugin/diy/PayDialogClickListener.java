package com.ubsdk.lenovo.plugin.diy;

import com.umbrella.game.ubsdk.plugintype.pay.diy.PayMethodItem;

/**
 * 支付方式点击监听
 * @author qingshanliao
 */
public interface PayDialogClickListener {
	/**
	 * 支付按钮
	 * @param payItemO
	 */
	void onPay(PayMethodItem payMethodItem);
	
	/**
	 * 关闭按钮
	 */
	void onClose();
}
