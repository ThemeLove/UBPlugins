package com.ubsdk.baidu.plugin;

/**
 * 百度支付计费点
 * @author qingshanliao
 */
public class BaiDuBilling {
	/**
	 * 计费点对应的商品id ,映射关系一对一
	 */
	private String productID;
	
	/**
	 * 百度平台申请的计费点id
	 */
	private String billingID;
	/**
	 * 百度平台申请的计费点name
	 */
	private String billingName;
	/**
	 * 百度平台申请的计费点价格
	 */
	private String billingPrice;

	public String getBillingID() {
		return billingID;
	}
	public void setBillingID(String billingID) {
		this.billingID = billingID;
	}
	public String getBillingName() {
		return billingName;
	}
	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}
	public String getBillingPrice() {
		return billingPrice;
	}
	public void setBillingPrice(String billingPrice) {
		this.billingPrice = billingPrice;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
}
