package xyz.nesting.payment.dao.wx;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.ScanPayResData;
import com.tencent.protocol.pay_protocol.WxAppPayReqData;
import com.tencent.protocol.pay_protocol.WxBrowserPayReqData;
import com.tencent.protocol.refund_protocol.RefundResData;

import xyz.nesting.common.db.MyBatisDao;

@MyBatisDao
public interface WxPayMapper {
	int insertWxPayRequest(ScanPayReqData PayRequest);

	int inserWxNotifyInfo(ScanPayResData notifySync);
	
	String selectCallBackUrl(@Param("out_trade_no") String outTradeNo);

	int insertWxbrowserPayRequest(WxBrowserPayReqData payRequest);

	int insertWxAppPayRequest(WxAppPayReqData reqdata);

	int insertWxPayRefundRequest(RefundResData refundResData);
	
	int selectCountWxRefundByOutTradeNo(@Param("outTradeNo") String outTradeNo);
	
	int updateWxPayRefundStatus(@Param("out_trade_no") String out_trade_no, @Param("status") int status);

	List<String> selectRefundStatus(@Param("status") int status);

	Map<String, String> selectTradeTypeByOutTradeNo(@Param("out_trade_no") String out_trade_no);
	
	Map<String, String> selectTradeTypeFromResult(@Param("out_trade_no") String out_trade_no);

	List<Map<String, Object>> selectPaySuccessOrder(@Param("out_trade_no") String outTradeNo);
	
	BigDecimal selectWxSettleMoney(@Param("gtCreateTime") Date gtCreateTime, @Param("ltCreateTime") Date ltCreateTime);
	
	BigDecimal selectWxSettleRefundMoney(@Param("gtCreateTime") Date gtCreateTime, @Param("ltCreateTime") Date ltCreateTime);

}
