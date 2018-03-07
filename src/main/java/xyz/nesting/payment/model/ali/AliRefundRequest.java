/**
 * 
 */
package xyz.nesting.payment.model.ali;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @Description: TODO(添加描述)
 * @Author zhengyue
 * @Version: 0.0.1
 * @CreateAt 2017年2月28日-下午4:38:07
 *
 */
public class AliRefundRequest {
	
	@NotEmpty(message = "outTradeNo不能为空")
	private String outTradeNo;
	
	private String refundAmount;
	
	/**
	 * @return the {@link #outTradeNo}
	 */
	public String getOutTradeNo() {
		return outTradeNo;
	}

	/**
	 * @param outTradeNo the {@link #outTradeNo} to set
	 */
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	/**
	 * @return the {@link #refundAmount}
	 */
	public String getRefundAmount() {
		return refundAmount;
	}

	/**
	 * @param refundAmount the {@link #refundAmount} to set
	 */
	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AliRefundRequest [outTradeNo=" + outTradeNo + ", refundAmount=" + refundAmount + "]";
	}
	
}
