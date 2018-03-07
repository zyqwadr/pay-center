package xyz.nesting.payment.constant;

public interface ErrorCode {
	int SUEECSS = 0;
	
	int OUT_TRADE_NO_NOT_FOUND =1001;
	int FAILED =9999;
	
	//RspCode:ERR145,RspMsg:原有申请在待验证时间内，请稍后再发起
	int PINGAN_PENNY_AUTH_ERR_145 = 145;
	//RspCode:ERR135,RspMsg:短信动态码验证中
	int PINGAN_UNIONPAY_AUTH_ERR_135 = 135;
	
	int PAY_SUCCESS_NOTIFY_FAIL = 200;
	int REFUND_SUCCESS_NOTIFY_FAIL = 201;
	
	int ORDER_IS_REFUNDED = 300;
	
	int REQUEST_PARAM_ERROR = 2002;
}
