package xyz.nesting.payment.constant;

public interface IntbeePaymentConstant {
	/*
	 * 退款请求发送成功
	 */
	int REFUND_REQUEST = 0;
	/*
	 * 退款成功 
	 */
	int REFUND_SUCCESS = 1;
	
	/*
	 * 清分撤销（对应撤销6056接口）
	 */
	String PINGAN_FUNC_FLAG_CANCEL_SETTLEMENT = "1";
	/*
	 * 清分冻结撤销（对应撤销6007接口
	 */
	String PINGAN_FUNC_FLAG_CANCEL_FREEZE = "2";
	/*
	 * 清分撤销（对应撤销6056接口）
	 */
	String PINGAN_FUNC_FLAG_CANCEL_SETTLEMENT_NOTE = "平安清分撤销";
	/*
	 * 清分冻结撤销（对应撤销6007接口
	 */
	String PINGAN_FUNC_FLAG_CANCEL_FREEZE_NOTE = "平安冻结撤销";
	
}
