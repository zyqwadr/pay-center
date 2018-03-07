package xyz.nesting.payment.protocol.pingxx;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import xyz.nesting.common.protocol.ZcAbstractToString;

public class PingxxPayReq extends ZcAbstractToString{

	@Min(value=1,message="amount must bigger than 0")
	private Integer amount;
	
	@NotEmpty(message="order_no is empty")
	private String orderNo;
	
	@Size(min=1,max=32,message="subject's length between 1 and 32 charater")
	private String subject;
	
	@Size(min=1,max=128,message="body's length between 1 and 128 charater")
	private String body;
	
	@NotEmpty(message="clientIP is empty")
	private String clientIP;

	@NotEmpty(message="callBackUrl is empty")
	private String callBackUrl;
	
	private String timestamp;
	
	private String sign;
	
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

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

	
}
