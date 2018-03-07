package xyz.nesting.payment.model.ali;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;


public class AliPayRequest {
	
	/**
	 * 订单号
	 */
	@NotEmpty(message = "outTradeNo不能为空")
	private String outTradeNo;
	
	/**
	 * 订单说明
	 */
	@NotEmpty(message = "subject不能为空")
	private String subject;
	
	/**
	 * 付款金额
	 */
	@NotNull(message ="totalFee不能为空")
	private Double totalFee;
	
	/**
	 * 收银台页面上，商品展示的超链接
	 */
	@NotEmpty(message = "showUrl不能为空")
	private String showUrl;
	
	/**
	 * 商品描述
	 */
	private String body;
	
	/**
	 * 收银台页面上，商品展示的超链接
	 */
	@NotEmpty(message = "appId不能为空")
	private String appId;
	
	/**
	 * 子系统回调地址
	 */
	@NotEmpty(message = "callBackUrl不能为空")
	private String callBackUrl;
	
	
	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(Double totalFee) {
		this.totalFee = totalFee;
	}

	public String getShowUrl() {
		return showUrl;
	}

	public void setShowUrl(String showUrl) {
		this.showUrl = showUrl;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}
	
	
}
