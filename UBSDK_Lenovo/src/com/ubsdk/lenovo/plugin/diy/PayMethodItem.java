package com.ubsdk.lenovo.plugin.diy;

/**
 * 支付方式视图封装javaBean
 * @author qingshanliao
 */
public class PayMethodItem {
	/**
	 * 支付方式ID,对应PayType
	 */
	private int ID;
	/**
	 * 支付方式名称
	 */
	private String name;
	/**
	 * 支付方式描述
	 */
	private String desc;
	/**
	 * 支付方式logo
	 */
	private int    logo;
	private boolean isSelect=false;//标示payTypeItemView是否被选中
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getLogo() {
		return logo;
	}
	public void setLogo(int logo) {
		this.logo = logo;
	}
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	@Override
	public String toString() {
		return "PayMethodItem [ID=" + ID + ", name=" + name + ", desc=" + desc + ", logo=" + logo + ", isSelect="
				+ isSelect + "]";
	}

}
