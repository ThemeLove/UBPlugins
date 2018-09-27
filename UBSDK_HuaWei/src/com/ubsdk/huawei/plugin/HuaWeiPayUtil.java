package com.ubsdk.huawei.plugin;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.util.Date;

import com.huawei.android.hms.agent.pay.PaySignUtil;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.ProductDetailRequest;


public class HuaWeiPayUtil {
	/**
	 * 构造华为支付请求对象
	 * @param orderID		订单唯一标示
	 * @param totalAmount	支付金额
	 * @param productName	商品名称
	 * @param prodectDesc	商品描述
	 * @param cpID			商户ID，来源于开发者联盟的“支付ID”
	 * @param appID			应用ID，来源于开发者联盟
	 * @param developName	商户名称，必填，不参与签名。开发者注册的公司名称
	 * @param ext			透传参数
	 * @param payPriKey		支付私钥
	 * @return	华为支付请求对象
	 */
	public static  PayReq createPayReq(String orderID,float totalAmount,String productName,String productDesc,String cpID,String appID,String developName,String ext,String payPriKey) {
	    PayReq payReq = new PayReq();

	    /**
	     * 生成总金额
	     */
	    String amount = String.format("%.2f", totalAmount);

	    //商品名称
	    payReq.productName = productName;
	    //商品描述
	    payReq.productDesc = productDesc;
	    // 商户ID，来源于开发者联盟的“支付ID”
	    payReq.merchantId = cpID;
	    // 应用ID，来源于开发者联盟
	    payReq.applicationID = appID;
	    // 支付金额
	    payReq.amount = amount;
	    // 商户订单号：开发者在支付前生成，用来唯一标识一次支付请求
	    payReq.requestId = orderID;
	    // 国家码
	    payReq.country = "CN";
	    //币种
	    payReq.currency = "CNY";
	    // 渠道号
	    payReq.sdkChannel = 1;
	    // 回调接口版本号
	    payReq.urlVer = "2";

	    // 商户名称，必填，不参与签名。开发者注册的公司名称
	    payReq.merchantName =developName;
	    //分类，必填，不参与签名。该字段会影响风控策略
	    // X4：主题,X5：应用商店,  X6：游戏,X7：天际通,X8：云空间,X9：电子书,X10：华为学习,X11：音乐,X12 视频,
	    // X31 话费充值,X32 机票/酒店,X33 电影票,X34 团购,X35 手机预购,X36 公共缴费,X39 流量充值
	    payReq.serviceCatalog = "X6"; // 应用设置为"X5"，游戏设置为"X6"
	    //商户保留信息，选填不参与签名，支付成功后会华为支付平台会原样 回调CP服务端
	    payReq.extReserved = ext;

	    //对单机应用可以直接调用此方法对请求信息签名，非单机应用一定要在服务器端储存签名私钥，并在服务器端进行签名操作。| For stand-alone applications, this method can be called directly to the request information signature, not stand-alone application must store the signature private key on the server side, and sign operation on the server side.
	    // 在服务端进行签名的cp可以将getStringForSign返回的待签名字符串传给服务端进行签名 | The CP, signed on the server side, can pass the pending signature string returned by Getstringforsign to the service side for signature
	    payReq.sign = PaySignUtil.rsaSign(PaySignUtil.getStringForSign(payReq), payPriKey);    
	    
	    return payReq;
	}
	
	/**
	 * 构造订单查询请求对象
	 * @param appID
	 * @param cpID
	 * @param orderID
	 * @param productID
	 * @return
	 */
	public static ProductDetailRequest createQueryRequest(String appID,String cpID,String orderID,String productID){
		ProductDetailRequest queryRequest = new ProductDetailRequest();
		queryRequest.applicationID=appID;
		queryRequest.merchantId=cpID;
		queryRequest.requestId=orderID;
		queryRequest.productNos=productID;
		return queryRequest;
	}
}
