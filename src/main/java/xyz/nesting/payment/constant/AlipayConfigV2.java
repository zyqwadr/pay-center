package xyz.nesting.payment.constant;

import xyz.nesting.common.context.SpringAppConfing;

public class AlipayConfigV2 {

	// 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
		public static String ali_app_id = "2016052401436400";

		// 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
		public static String seller_id = "2088221861486600";

		// 商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
		public static String private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJzAkjQpzJ34jaCSwhuIVWL+1cHBmtv8L5e/3ABufLB+23QqGHqXwpOYM25TAeHzzcjsFZi8s2sIcxADbZZrBZS+30S/DxzTEJlufoVYg4jPnfjwedKhcvfvr79iNSEFUTOxP04ESxOHoBIW+Ob57x01e/+la++CS22T2rmvPksvAgMBAAECgYAET181w/h2V4E9awufSi5e3HbwLuydI+ADIilgcalICRTrqsSlikq1WDgBzpJ8rgF4htVJL9cXGFJhnFaFDkhG9bxY2IZJmx9uVyJVOzc9kDpaYhsNiBe1RHfrst5wOs29p3T3tlt5z5ERSAK6FPzcA7YVzEIuH9U+0KY+Dk0P8QJBAMoT+KlORI71ckjhghLcQCs0UyWzSlcpTGzhah3CtmJGau0Uqc2banCh+xjFSYibB8Y/cEjnnEAu6miH3a0Uhf0CQQDGlGCz2h0b5Ydi47V8I58Xx+Q8ORZ52GS84jLGjvUdgXwqXVWbGVUfrP4e4BFoYwALNYbJ6No3sCrR9P8pyEebAkApB/MWlYDQMkA5UiiyIayZ2S8E2am/lvAgr4h3cIDN5ZRyfR6ZU/RB2HH+rphmt2vVsi3fb7/HPH92KBg3u5IVAkBSv0+Qgknw7vM8fOCKKxnHQg9UHQHFoitQHf3EiZuQQP2pdZ0PdT3oZiC6Yuo+EkrZYOOazUHpRKqDNNEC6ycTAkEAqCd9GrBrW7K5eJ9+kReD2Tad7jDEI49sAwrTrpQccYwFdYIsBD57KTSanjn1BsuOBxTXOu0Lw4EkaJjq0+NTOQ==";

		// 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
		public static String alipay_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";

		// 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
		public static String notify_url = SpringAppConfing.get("alipay.notify.url.v2");//"http://120.24.43.211:8081/public/pay/alipay/notify";

		// 签名方式
		public static String sign_type = "RSA";

		// 字符编码格式 目前支持utf-8
		public static String input_charset = "UTF-8";
		
		public static String request_format = "json";
		
		// 服务请求地址
		public static String service = "https://openapi.alipay.com/gateway.do";
}
