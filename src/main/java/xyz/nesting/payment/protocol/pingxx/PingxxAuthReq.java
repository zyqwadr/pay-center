package xyz.nesting.payment.protocol.pingxx;

import org.hibernate.validator.constraints.NotEmpty;

import xyz.nesting.common.protocol.ZcAbstractToString;

public class PingxxAuthReq extends ZcAbstractToString{

	@NotEmpty(message="idName is empty")
	private String idName;
	
	@NotEmpty(message="idNumber is empty")
	private String idNumber;
	
	@NotEmpty(message="cardNumber is empty")
	private String cardNumber;
	
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

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	
}
