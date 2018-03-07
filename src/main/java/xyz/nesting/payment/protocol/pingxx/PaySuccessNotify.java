package xyz.nesting.payment.protocol.pingxx;

import xyz.nesting.common.protocol.ZcAbstractToString;
import xyz.nesting.common.util.EncryptionUtil;

public class PaySuccessNotify extends ZcAbstractToString {
	
	private String orderNo;
	
	private Integer amount;
	
	private String chargeId;
	
	private String channel;
	
	private String timestamp;
	
	private String sign;
	
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getChargeId() {
		return chargeId;
	}

	public void setChargeId(String chargeId) {
		this.chargeId = chargeId;
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

	public static PaySuccessNotify build (String orderNo, String chargeId, Integer amount, String channel, String salt) {
		PaySuccessNotify notify = new PaySuccessNotify();
		notify.setOrderNo(orderNo);
		notify.setChargeId(chargeId);
		notify.setAmount(amount);
		notify.setChannel(channel);
		notify.setTimestamp(String.valueOf(System.currentTimeMillis()));
		notify.setSign(EncryptionUtil.getSign(notify, salt));
		return notify;
	}
	
	

}
