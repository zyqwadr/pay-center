/**
 * 
 */
package xyz.nesting.payment.protocol;

import java.math.BigDecimal;

/**
 * 
 * @Description: TODO(添加描述)
 * @Author zhengyue
 * @Version: 0.0.1
 * @CreateAt 2017年3月1日-下午3:32:16
 *
 */
public class RefundRequestDTO {
	
	private String outTradeNo;
	
	private BigDecimal sellIncome;
	
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

	public BigDecimal getSellIncome() {
		return sellIncome;
	}

	public void setSellIncome(BigDecimal sellIncome) {
		this.sellIncome = sellIncome;
	}
	
	

}
