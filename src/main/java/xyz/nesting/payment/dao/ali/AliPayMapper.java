/**
 * 
 */
package xyz.nesting.payment.dao.ali;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import xyz.nesting.common.db.MyBatisDao;
import xyz.nesting.payment.model.ali.AliPayNotifySync;
import xyz.nesting.payment.model.ali.AliPayRequest;

/**
 * 
 * @Description:智峰卡数据层操作
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年5月23日-上午9:49:56
 *
 */
@MyBatisDao
public interface AliPayMapper {
	
	/**
	 * 记录支付宝支付请求信息
	 * 
	 * @param params
	 * @return
	 */
	int insertAliPayRequest(AliPayRequest AliPayRequest);

	/**
	 * 记录支付宝回调信息
	 * 
	 * @param params
	 * @return
	 */
	int inserAliNotifyInfo(AliPayNotifySync notifySync);

	/**
	 * 获取appid
	 * 
	 * @param outTradeNo
	 *            订单号
	 * 
	 * @return cardId
	 */
	Integer selectAppId(@Param("out_trade_no") String outTradeNo);

	/**
	 * 回调地址
	 * 
	 * @param outTradeNo
	 *            订单号
	 * 
	 * @return cbURL
	 */
	String selectCallBackUrl(@Param("out_trade_no") String outTradeNo);

	/**
	 * @param outTradeNo
	 * @param outRequestNo
	 * @param outRequestNo2
	 * @param refundAmount
	 * @param i
	 */
	int insertAliRefundRequest(@Param("outTradeNo") String outTradeNo, @Param("outRequestNo") String outRequestNo, @Param("totalFee") String totalFee, @Param("refundFee") String refundFee, @Param("status") int status);

	/**
	 * @param outTradeNo
	 * @return
	 */
	int selectCountAliRefundByOutTradeNo(@Param("outTradeNo") String outTradeNo);

	/**
	 * @param refundRequest
	 * @return
	 */
	List<String> selectRefundStatus(int refundRequest);

	/**
	 * @param outTradeNo
	 * @param refundSuccess
	 */
	void updateAliPayRefundStatus(@Param("outTradeNo") String outTradeNo, @Param("status") int status);

	/**
	 * @param outTradeNo
	 * @param refundSuccess
	 */
	List<AliPayNotifySync> selectPaySuccessOrder(@Param("outTradeNo") String outTradeNo);
	
	/**
	 * @param gtCreateTime
	 * @param ltCreateTime
	 */
	BigDecimal selectAliSettleMoney(@Param("gtCreateTime") Date gtCreateTime, @Param("ltCreateTime") Date ltCreateTime);
	
	/**
	 * @param gtCreateTime
	 * @param ltCreateTime
	 */
	BigDecimal selectAliSettleRefundMoney(@Param("gtCreateTime") Date gtCreateTime, @Param("ltCreateTime") Date ltCreateTime);

}
