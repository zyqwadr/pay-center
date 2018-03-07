package com.tencent.common;

import xyz.nesting.common.context.SpringAppConfing;

/**
 * User: rizenguo Date: 2014/10/29 Time: 14:40 这里放置各种配置数据
 */
public class Configure {
	// 这个就是自己要保管好的私有Key了（切记只能放在自己的后台代码里，不能放在任何可能被看到源代码的客户端程序中）
	// 每次自己Post数据给API的时候都要用这个key来对所有字段进行签名，生成的签名会放在Sign这个字段，API收到Post数据的时候也会用同样的签名算法对Post过来的数据进行签名和验证
	// 收到API的返回的时候也要用这个key来对返回的数据算下签名，跟API的Sign数据进行比较，如果值不一致，有可能数据被第三方给篡改

	private static String key = SpringAppConfing.get("weixin.property.key");

	// 微信分配的公众号ID（开通公众号之后可以获取到）
	private static String appID = SpringAppConfing.get("weixin.property.appID");
	
	//app端支付appId(开通app支付获取)
	private static String appID_App = SpringAppConfing.get("weixin.property.appID_App");

	private static String appSecret = SpringAppConfing.get("weixin.property.appSecret");
	// 微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	private static String mchID = SpringAppConfing.get("weixin.property.mchID");
	
	//app端支付mchID(开通app支付获取)
	private static String mchID_App = SpringAppConfing.get("weixin.property.mchID_App");

	// 受理模式下给子商户分配的子商户号
	private static String subMchID = "";

	// HTTPS证书的本地路径
	private static String certLocalPath = "conf/properties/prod/cert/wx/apiclient_cert.p12";

	// HTTPS证书密码，默认密码等于商户号MCHID
	private static String certPassword = SpringAppConfing.get("weixin.property.mchID");
	
	private static String certPassword_App = SpringAppConfing.get("weixin.property.mchID_App");
	
	// 是否使用异步线程的方式来上报API测速，默认为异步模式
	private static boolean useThreadToDoReport = true;

	// private static String notify_url =
	// "http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php";
	private static String notify_url = SpringAppConfing.get("wxpay.notify.url");
//	"http://120.24.43.211:8081/public/pay/wxpay/notify";
	
	private static String trade_type = "NATIVE";
	private static String jsapi_trade_type = "JSAPI";
	private static String app_trade_type = "APP";
	//private static String trade_type = "MWEB";
	// 机器IP
	private static String ip = "";
	
	// APP名称
	private static String app_name =  SpringAppConfing.get("weixin.property.app_name");//"智蜂-";
	
	// 以下是几个API的路径：
	// 1）被扫支付API
	public static String PAY_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	// 2）被扫支付查询API
	public static String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

	// 3）退款API
	public static String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	// 4）退款查询API
	public static String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

	// 5）撤销API
	public static String REVERSE_API = "https://api.mch.weixin.qq.com/secapi/pay/reverse";

	// 6）下载对账单API
	public static String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";

	// 7) 统计上报API
	public static String REPORT_API = "https://api.mch.weixin.qq.com/payitil/report";

	public static String GET_OPENID_API = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public static String getNotifyUrl() {
		return notify_url;
	}

	public static void setNotifyUrl(String notify_url) {
		Configure.notify_url = notify_url;
	}

	public static String getTradeType() {
		return trade_type;
	}

	public static void setTradeType(String trade_type) {
		Configure.trade_type = trade_type;
	}

	public static String getJsapiTradeType() {
		return jsapi_trade_type;
	}

	public static void setJsapiTradeType(String jsapi_trade_type) {
		Configure.jsapi_trade_type = jsapi_trade_type;
	}

	public static boolean isUseThreadToDoReport() {
		return useThreadToDoReport;
	}

	public static void setUseThreadToDoReport(boolean useThreadToDoReport) {
		Configure.useThreadToDoReport = useThreadToDoReport;
	}

	public static String HttpsRequestClassName = "com.tencent.common.HttpsRequest";

	public static void setKey(String key) {
		Configure.key = key;
	}

	public static void setAppID(String appID) {
		Configure.appID = appID;
	}

	public static String getAppSecret() {
		return appSecret;
	}

	public static void setAppSecret(String appSecret) {
		Configure.appSecret = appSecret;
	}

	public static void setMchID(String mchID) {
		Configure.mchID = mchID;
	}

	public static void setSubMchID(String subMchID) {
		Configure.subMchID = subMchID;
	}

	public static void setCertLocalPath(String certLocalPath) {
		Configure.certLocalPath = certLocalPath;
	}

	public static void setCertPassword(String certPassword) {
		Configure.certPassword = certPassword;
	}

	public static void setIp(String ip) {
		Configure.ip = ip;
	}

	public static String getKey() {
		return key;
	}

	public static String getAppid() {
		return appID;
	}

	public static String getMchid() {
		return mchID;
	}

	public static String getSubMchid() {
		return subMchID;
	}
	
	public static String getCertPassword_App() {
		return certPassword_App;
	}

	public static void setCertPassword_App(String certPassword_App) {
		Configure.certPassword_App = certPassword_App;
	}

	public static String getCertLocalPath() {
		//www/web/intbee/pay.intbee.com/webapps/ROOT/WEB-INF/classes/
		String path = Configure.class.getClassLoader().getResource("").getPath();
		String absolutePath = path + SpringAppConfing.get("weixin.cert");
		return absolutePath;
	}
	
	public static String getCertLocalPathApp() {
		String path = Configure.class.getClassLoader().getResource("").getPath();
		String absolutePath = path + SpringAppConfing.get("weixin.cert.app");
		return absolutePath;
	}

	public static String getCertPassword() {
		return certPassword;
	}

	public static String getIP() {
		return ip;
	}

	public static void setHttpsRequestClassName(String name) {
		HttpsRequestClassName = name;
	}

	public static String getAppID_App() {
		return appID_App;
	}

	public static void setAppID_App(String appID_App) {
		Configure.appID_App = appID_App;
	}

	public static String getApp_trade_type() {
		return app_trade_type;
	}

	public static void setApp_trade_type(String app_trade_type) {
		Configure.app_trade_type = app_trade_type;
	}

	public static String getMchID_App() {
		return mchID_App;
	}

	public static void setMchID_App(String mchID_App) {
		Configure.mchID_App = mchID_App;
	}

	public static String getApp_name() {
		return app_name;
	}

	public static void setApp_name(String app_name) {
		Configure.app_name = app_name;
	}
	
	
}
