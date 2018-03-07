package xyz.nesting.payment.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;

import xyz.nesting.common.exceptions.BusinessException;
import xyz.nesting.common.util.EncryptionUtil;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.model.ali.AliPayRequest;
import xyz.nesting.payment.model.ali.AliRefundRequest;
import xyz.nesting.payment.protocol.ApiResponse;
import xyz.nesting.payment.service.AliPayService;

/**
 * 
 * 
 * @Description: 支付宝通知回调
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年6月3日-上午10:59:58
 *
 */
@RestController
@RequestMapping(AlipayController.BASE_MAPPER)
@Validated
public class AlipayController extends AbstractController {
	protected static final String BASE_MAPPER = "/public/pay/alipay";

	@Autowired
	private AliPayService mAliPayService;

	@RequestMapping(value = "", method = { RequestMethod.POST })
	public ApiResponse payForm(@Valid @RequestBody AliPayRequest aliPayRequest) {

		aliRequestFilter(aliPayRequest);

		String result;
		try {
			result = mAliPayService.payForForm(aliPayRequest);
		} catch (Exception e) {
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}

		return success(result);
	}

	@RequestMapping(value = "v2", method = { RequestMethod.POST })
	public ApiResponse payFormV2(@Valid @RequestBody AliPayRequest aliPayRequest) {

		aliRequestFilter(aliPayRequest);

		String result;

		try {
			result = mAliPayService.payForFormV2(aliPayRequest);
		} catch (AlipayApiException e) {
			logger.error("Alipay pay Failed, ", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}

		return success(result);
	}
	
	@RequestMapping(value = "app", method = { RequestMethod.POST })
	public ApiResponse payApp(@Valid @RequestBody AliPayRequest aliPayRequest) {

		aliRequestFilter(aliPayRequest);

		String result;

		try {
			result = mAliPayService.payForApp(aliPayRequest);
		} catch (Exception e) {
			logger.error("Alipay pay app Failed, ", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}

		return success(result);
	}
	
	@RequestMapping(value = "app/v2", method = { RequestMethod.POST })
	public ApiResponse payAppV2(@Valid @RequestBody AliPayRequest aliPayRequest) {

		aliRequestFilter(aliPayRequest);

		String result;

		try {
			result = mAliPayService.payForAppV2(aliPayRequest);
		} catch (AlipayApiException e) {
			logger.error("Alipay pay app Failed, V2", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}

		return success(result);
	}
	
	@RequestMapping(value = "refund", method = { RequestMethod.POST })
	public ApiResponse refund(@RequestBody Map<String, Object> reqdata, HttpServletRequest request) {
		
		if(!EncryptionUtil.validateSign(reqdata, encryptionSalt)){
			logger.error("订单退款非法sign:[orderNo:{},IP:{}]", reqdata.get("out_trade_no"), request.getRemoteAddr());
			return error(ErrorCode.FAILED, "ali refund fail!", null);
		}
		AliRefundRequest aliRefundRequest = new AliRefundRequest(); 
		aliRefundRequest.setOutTradeNo((String)reqdata.get("out_trade_no"));
		aliRefundRequest.setRefundAmount(String.valueOf((double)reqdata.get("refund_amount")));
		if (StringUtils.isEmpty(aliRefundRequest.getOutTradeNo())) {
			return error(ErrorCode.FAILED, "out_trade_no is null", "");
		}
		if (StringUtils.isEmpty(aliRefundRequest.getRefundAmount())) {
			return error(ErrorCode.FAILED, "refund_amount is null", "");
		}
		
		
		try {
			mAliPayService.refund(aliRefundRequest);
		} catch (Exception e) {
			logger.error("Alipay refund Failed, ", e);
			return error(ErrorCode.FAILED, "Alipay refund Failure", e.getMessage());
		}
		return success("");
	}
	
	@RequestMapping(value = "refund/query", method = { RequestMethod.POST })
	public ApiResponse refundQuery(@Valid @RequestBody AliRefundRequest aliRefundRequest) {
		String result;
		try {
			AlipayTradeFastpayRefundQueryResponse response = mAliPayService.refundQuery(aliRefundRequest.getOutTradeNo());
			result = response.getBody();
		} catch (Exception e) {
			logger.error("Alipay refund Failed, ", e);
			return error(ErrorCode.FAILED, "Alipay refund Failure", e.getMessage());
		}
		return success(result);
	}
	
	@RequestMapping(value = "scan", method = { RequestMethod.POST })
	public ApiResponse payScan(@Valid @RequestBody AliPayRequest aliPayRequest) {

		aliRequestFilter(aliPayRequest);

		String result;

		try {
			result = mAliPayService.payForScan(aliPayRequest);
		} catch (Exception e) {
			logger.error("Alipay pay scan Failed, ", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}

		return success(result);
	}

	@RequestMapping("notify")
	public void notify(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		// 支付宝回调，不做任何check
		try {
			mAliPayService.notify(request);

			response.getWriter().print("success");

		} catch (IOException e) {
			logger.error("支付宝回调错误:" + e.getMessage());

		} catch (BusinessException e) {
			logger.error("支付宝回调错误:" + e.getMessage());
		}
	}

	@RequestMapping("notify/v2")
	public void notifyV2(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		try {
			mAliPayService.notifyV2(request);
			
			response.getWriter().print("success");

		} catch (Exception e) {
			logger.error("支付宝回调错误:" + e.getMessage());
			System.out.println( e.getMessage());
			// TODO 验签失败则记录异常日志，并在response中返回failure.
			try {
				response.getWriter().print("failure");
			} catch (IOException e1) {
				logger.error("支付宝回调错误,IO 出错:" + e1.getMessage());
			}
		}

	}

	private void aliRequestFilter(AliPayRequest aliPayRequest) {
		String subject = aliPayRequest.getSubject();
		subject = StringUtils.substring(subject, 0, 128);
		aliPayRequest.setSubject(subject);
	}
	
}