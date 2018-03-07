/**
 * 
 */
package xyz.nesting.payment.protocol.ali;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * 
 * @Description: TODO(添加描述)
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年6月3日-上午10:40:45
 *
 */
public class AlipayAcquireRefundResponse {
	/**
	 * 请求是否成功
	 * 
	 * @return
	 */
	public boolean isOkRequest() {
		return "T".equals(is_success);
	}

	/**
	 * 退款是否成功
	 * 
	 * @return
	 */
	public boolean isOkResult() {
		return "SUCCESS".equals(result_code);
	}

	/**
	 * 是否有资金变动
	 * 
	 * @return
	 */
	public boolean isFundChange() {
		return "Y".equals(fund_change);
	}

	private String is_success;
	private String error;
	private String display_message;
	private String fund_change;
	private String refund_fee;
	private String result_code;
	private String out_trade_no;
	private String trade_no;

	public String getIs_success() {
		return is_success;
	}

	public void setIs_success(String is_success) {
		this.is_success = is_success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getDisplay_message() {
		return display_message;
	}

	public void setDisplay_message(String display_message) {
		this.display_message = display_message;
	}

	public String getFund_change() {
		return fund_change;
	}

	public void setFund_change(String fund_change) {
		this.fund_change = fund_change;
	}

	public String getRefund_fee() {
		return refund_fee;
	}

	public void setRefund_fee(String refund_fee) {
		this.refund_fee = refund_fee;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
