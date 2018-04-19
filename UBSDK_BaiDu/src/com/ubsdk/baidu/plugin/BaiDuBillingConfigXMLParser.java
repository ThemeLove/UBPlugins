package com.ubsdk.baidu.plugin;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.umbrella.game.ubsdk.config.UBSDKConfig;
import com.umbrella.game.ubsdk.utils.AssetUtil;
import com.umbrella.game.ubsdk.utils.TextUtil;

import android.util.Xml;

public class BaiDuBillingConfigXMLParser {
	/**
	 * 从Asset/baidu_pay.xml中解析计费点
	 * @return
	 */
	public static HashMap<String, HashMap<String, BaiDuBilling>> getBaiDuBillingWithTypeMap() {
		HashMap<String, HashMap<String, BaiDuBilling>> billingWithTypeMap = new HashMap<String, HashMap<String, BaiDuBilling>>();
		HashMap<String, BaiDuBilling> billingMap = null;
		String billingStr = AssetUtil.getAssetConfigStr(UBSDKConfig.getInstance().getApplicationContext(),"baidu_pay.xml");
		XmlPullParser parse = Xml.newPullParser();
		try {
			parse.setInput(new StringReader(billingStr));
			int eventType = parse.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String payType = null;
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String tag = parse.getName();
					if (TextUtil.equals("pay", tag)) {
						payType="";
						billingMap=null;
						payType = parse.getAttributeValue(null, "type");
						billingMap = new HashMap<String, BaiDuBilling>();
					}
					if (TextUtil.equals("good", tag)) {
						String productID = parse.getAttributeValue(null, "productID");
						String billingID = parse.getAttributeValue(null, "billingID");
						String billingName = parse.getAttributeValue(null, "billingName");
						String billingPrice = parse.getAttributeValue(null, "billingPrice");

						BaiDuBilling baiDuBilling = new BaiDuBilling();
						baiDuBilling.setProductID(productID);
						baiDuBilling.setBillingID(billingID);
						baiDuBilling.setBillingName(billingName);
						baiDuBilling.setBillingPrice(billingPrice);

						billingMap.put(productID, baiDuBilling);
					}
					billingWithTypeMap.put(payType, billingMap);
					break;
				default:
					break;
				}
				eventType = parse.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return billingWithTypeMap;
	}

}