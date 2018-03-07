package xyz.nesting.payment.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_protocol.RefundResData;

import xyz.nesting.common.util.EncryptionUtil;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.model.ali.AliPayNotifySync;
import xyz.nesting.payment.model.ali.AliRefundRequest;
import xyz.nesting.payment.protocol.ApiResponse;
import xyz.nesting.payment.service.AliPayService;
import xyz.nesting.payment.service.WxPayService;

/**
 * 
 * 
 * @Description: 
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年6月3日-上午10:59:58
 *
 */
@RestController
@RequestMapping(RefundController.BASE_MAPPER)
public class RefundController extends AbstractController {
	protected static final String BASE_MAPPER = "/public/pay";

	@Autowired
	private AliPayService aliPayService;
	
	@Autowired
	private WxPayService wxPayService;
	
	@RequestMapping(value = "/refund", method = { RequestMethod.POST })
	public ApiResponse refundAll(@RequestBody Map<String, Object> reqdata, HttpServletRequest request) {
		
		if(!EncryptionUtil.validateSign(reqdata, encryptionSalt)){
			logger.warn("订单退款非法sign:[orderNo:{},IP:{}]", reqdata.get("out_trade_no"), request.getRemoteAddr());
			return error(ErrorCode.FAILED, "refund fail!", null);
		}
		
		String outTradeNo = (String) reqdata.get("out_trade_no");
		String refundAmount = (String) reqdata.get("refund_amount");
		String result = null;
		List<AliPayNotifySync> payResults = aliPayService.getPaySuccessOrder(outTradeNo);
		try {
			if (CollectionUtils.isNotEmpty(payResults)) {
				AliRefundRequest aliRefundRequest = new AliRefundRequest();
				aliRefundRequest.setOutTradeNo(outTradeNo);
				aliRefundRequest.setRefundAmount(refundAmount);
				result = aliPayService.refund(aliRefundRequest);
			} else {
				List<Map<String, Object>> wxPayResults = wxPayService.getPaySuccessOrder(outTradeNo);
				if (CollectionUtils.isNotEmpty(wxPayResults)) {
					RefundReqData refundReqData = new RefundReqData();
					refundReqData.setOut_trade_no(outTradeNo);
					int refundFee = new BigDecimal(refundAmount).multiply(new BigDecimal("100")).intValue();
					refundReqData.setTotal_fee(refundFee);
					refundReqData.setRefund_fee(refundFee);
					RefundResData refund = wxPayService.refund(refundReqData);
					result = refund.toString();
				} else {
					logger.warn("订单未付款成功，无法退款  orderNo:{} refundAmount:{}", outTradeNo, refundAmount);
					return error(ErrorCode.FAILED, "order not pay success", null);
				}
			}
		} catch (Exception e) {
			int count = 0;
			if (CollectionUtils.isNotEmpty(payResults)) {
				count = aliPayService.selectCountAliRefundByOutTradeNo(outTradeNo);
			} else {
				count = wxPayService.selectCountWxRefundByOutTradeNo(outTradeNo);
			}
			if (count > 0) {
				return error(ErrorCode.ORDER_IS_REFUNDED, "订单已退款", e.getMessage());
			}
			logger.error("Refund Failed", e);
			return error(ErrorCode.FAILED, "Refund Failure", e.getMessage());
		}
		return success(result);
	}
	

	@RequestMapping(value = "/refund/status/check", method = { RequestMethod.GET })
	public ApiResponse refundStatusCheck() {
		//任务调度由 intbee-timer 完成
		logger.debug("refundStatusCheck start");
		wxPayService.checkWxRefundStatus();
		aliPayService.checkAliRefundStatus();
		logger.debug("refundStatusCheck end");
		return success(null);
	}

}