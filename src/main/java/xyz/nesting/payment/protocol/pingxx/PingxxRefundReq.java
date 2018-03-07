package xyz.nesting.payment.protocol.pingxx;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import xyz.nesting.common.protocol.ZcAbstractToString;

public class PingxxRefundReq extends ZcAbstractToString{
	
	@NotEmpty(message="chargeId is empty")
	private String chargeId;
	
	@NotEmpty(message="orderNo is empty")
	private String orderNo;

	@Min(value=1,message="amount must bigger than 0")
	private Integer amount;
	
	@Size(min=1,max=255,message="description's length between 1 and 128 charater")
	private String description;
	
	@NotEmpty(message="callBackUrl is empty")
	private String callBackUrl;
	
	private String timestamp;
	
	private String sign;
	
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

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
