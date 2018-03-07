package xyz.nesting.payment.constant;

public interface StatusCode {
	int TRADE_WAIT_BUYER_PAY_ID = 0;//
	int TRADE_CLOSED_ID  = 1;//
	int TRADE_SUCCESS_ID  = 2;//
	int TRADE_PENDING_ID  = 3;//
	int TRADE_FINISHED_ID  = 4;//

	String TRADE_WAIT_BUYER_PAY_CODE = "WAIT_BUYER_PAY";//
	String TRADE_CLOSED_CODE = "TRADE_CLOSED";//
	String TRADE_SUCCESS_CODE = "TRADE_SUCCESS";//
	String TRADE_PENDING_CODE = "TRADE_PENDING";//
	String TRADE_FINISHED_CODE = "TRADE_FINISHED";//
	
	int WITHDRAW_STATUS_FAILED = 0;
	int WITHDRAW_STATUS_SUCCESS = 1;
	int WITHDRAW_STATUS_HANDLING = 2;
	int WITHDRAW_STATUS_FAILED_HANDLED = 3;	
			
}
