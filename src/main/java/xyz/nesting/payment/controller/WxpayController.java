package xyz.nesting.payment.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.tencent.protocol.pay_protocol.GetOpenidResData;
import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.WxAppPayReqData;
import com.tencent.protocol.pay_protocol.WxBrowserPayReqData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_protocol.RefundResData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;

import xyz.nesting.common.context.SpringAppConfing;
import xyz.nesting.common.exceptions.BusinessException;
import xyz.nesting.common.util.EncryptionUtil;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.protocol.ApiResponse;
import xyz.nesting.payment.service.impl.WxPayServiceImpl;

@RestController
@RequestMapping(WxpayController.BASE_MAPPER)
@Validated
public class WxpayController extends AbstractController {
	protected static final String BASE_MAPPER = "/public/pay/wxpay";
	@Autowired
	private WxPayServiceImpl mPayService;

	@RequestMapping(value = "", method = { RequestMethod.POST })
	public ApiResponse wxpay(@Valid @RequestBody ScanPayReqData reqdata) {
		Map<String,Object> result;
		try {
			result = mPayService.payForForm(reqdata);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, e.getMessage(), "");
		} catch (Exception e) {
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}
		return success(result);
	}
	
	@RequestMapping(value = "wxbrowser", method = { RequestMethod.POST })
	public ApiResponse wxbrowserpay(@Valid @RequestBody WxBrowserPayReqData reqdata) {

		Map<String,Object> result;
		try {
			result = mPayService.wxBrowserPayForForm(reqdata);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, e.getMessage(), "");
		} catch (Exception e) {
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}
		return success(result);
	}
	
	@RequestMapping(value = "app", method = { RequestMethod.POST })
	public ApiResponse wxAppPay(@Valid @RequestBody WxAppPayReqData reqdata) {
		Map<String,Object> result;
		try {
			result = mPayService.wxAppPayForForm(reqdata);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, e.getMessage(), "");
		} catch (Exception e) {
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}
		return success(result);
	}
	
	/**
	 * 通过code获取openid
	 * @param reqdata
	 * @return
	 */
	@RequestMapping(value = "/getopenid", method = { RequestMethod.GET })
	public ApiResponse getOpenid(String code) {
		GetOpenidResData result;
		try {
			result = mPayService.getOpenId(code);
		} catch (Exception e) {
			logger.error("wxpay get openid error = [{}]", e);
			return error(ErrorCode.FAILED, "Get openid Failure", e.getMessage());
		}
		return success(result);
	}

	@RequestMapping("notify")
	public void notify(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		try {
			mPayService.notify(request);
			response.getWriter().print("success");
		} catch (IOException e) {
			logger.error("微信支付回调错误:" + e.getMessage());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			logger.error("微信支付回调错误1:" + e.getMessage());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			logger.error("微信支付回调错误2:" + e.getMessage());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			logger.error("微信支付回调错误2:" + e.getMessage());
		}
	}
	
	@RequestMapping(value = "/refund", method = { RequestMethod.POST })
	public ApiResponse wxpayRefund(@RequestBody Map<String, Object> reqdata, HttpServletRequest request) {
		if(!EncryptionUtil.validateSign(reqdata, encryptionSalt)){
			logger.error("订单退款非法sign:[orderNo:{},IP:{}]", reqdata.get("out_trade_no"), request.getRemoteAddr());
			return error(ErrorCode.FAILED, "wx refund fail!", null);
		}
		
		RefundReqData refundReqData = new RefundReqData();
		refundReqData.setOut_trade_no((String) reqdata.get("out_trade_no"));
		refundReqData.setTotal_fee((int) reqdata.get("total_fee"));
		refundReqData.setRefund_fee((int) reqdata.get("refund_fee"));
		
		if (StringUtils.isEmpty(refundReqData.getOut_trade_no())) {
			return error(ErrorCode.FAILED, "out trade no is null", "");
		}
		
		RefundResData result;
		try {
			result = mPayService.refund(refundReqData);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, e.getMessage(), "");
		}  catch (Exception e) {
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}
		return success(result);
	}
	
	@RequestMapping(value = "/refund/query", method = { RequestMethod.POST })
	public ApiResponse wxpayRefundQuery(@Valid @RequestBody RefundQueryReqData reqdata) {

		Map<String,Object> result;
		try {
			result = mPayService.refundQuery(reqdata);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, e.getMessage(), "");
		} catch (Exception e) {
			logger.error("wxpay failure = [{}]", e);
			return error(ErrorCode.FAILED, "Pay Failure", e.getMessage());
		}
		return success(result);
	}
	
	@RequestMapping(value = "/appid", method = { RequestMethod.GET })
	public ApiResponse appid() {
		return success(SpringAppConfing.get("weixin.property.appID", "wxf712db1e1042a294"));
	}
	
}
