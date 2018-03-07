package xyz.nesting.payment.protocol.pingxx;

import xyz.nesting.common.protocol.ZcAbstractToString;
import xyz.nesting.common.util.EncryptionUtil;

public class RefundSuccessNotify extends ZcAbstractToString {
	
	private String orderNo;
	
	private String refundId;
	
	private Integer amount;
	
	private String timestamp;
	
	private String sign;
	
	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public static RefundSuccessNotify build (String orderNo, String refundId, Integer amount, String salt) {
		RefundSuccessNotify notifySign = new RefundSuccessNotify();
		notifySign.setOrderNo(orderNo);
		notifySign.setRefundId(refundId);
		notifySign.setAmount(amount);
		notifySign.setTimestamp(String.valueOf(System.currentTimeMillis()));
		notifySign.setSign(EncryptionUtil.getSign(notifySign, salt));
		return notifySign;
	}
	
	

}
