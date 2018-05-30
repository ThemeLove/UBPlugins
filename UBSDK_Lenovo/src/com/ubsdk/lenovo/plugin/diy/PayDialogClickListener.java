package com.ubsdk.lenovo.plugin.diy;
/**
 * 支付方式点击监听
 * @author qingshanliao
 */
public interface PayDialogClickListener {
	/**
	 * 支付按钮
	 * @param payItem
	 */
	void onPay(PayMethodItem payMethodItem);
	
	/**
	 * 关闭按钮
	 */
	void onClose();
}
