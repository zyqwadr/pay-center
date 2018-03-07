package xyz.nesting.payment.controller;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Identification;
import com.pingplusplus.model.Refund;

import xyz.nesting.common.util.EncryptionUtil;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.constant.PingxxChannel;
import xyz.nesting.payment.protocol.ApiResponse;
import xyz.nesting.payment.protocol.pingxx.PingxxAuthReq;
import xyz.nesting.payment.protocol.pingxx.PingxxPayReq;
import xyz.nesting.payment.protocol.pingxx.PingxxRefundReq;
import xyz.nesting.payment.service.PingxxService;

/**
 * pingxx 支付,退款,回调,认证接口
 * @author nesting
 *
 */
@RestController
@RequestMapping(PingxxController.BASE_MAPPER)
@Validated
public class PingxxController extends AbstractController {

	protected static final String BASE_MAPPER = "/public/pingxx";

	@Autowired
	private PingxxService pingxxService;

	/**
	 * 创建支付charge
	 * 
	 * @param reqdata
	 * @return
	 */
	@RequestMapping(value = "/charge/{channel}", method = { RequestMethod.POST })
	public ApiResponse createCharge(@PathVariable String channel, @Valid @RequestBody PingxxPayReq reqdata) {
		logger.debug("crateCharge channel:{}, req:{}", channel, reqdata);
		PingxxChannel.valid(channel);
		if(!EncryptionUtil.validateSign(reqdata, encryptionSalt)){
			logger.error("签名错误,请求支付失败");
			return error(ErrorCode.FAILED, "签名错误,请求支付失败", null);
		}
		Charge charge = pingxxService.createCharge(channel, reqdata);
		return success(charge);
	}
	
	/**
	 * 请求退款接口
	 * @param orderNo
	 * @param reqdata
	 * @return
	 */
	@RequestMapping(value = "/refund", method = { RequestMethod.POST })
	public ApiResponse refund(@Valid @RequestBody PingxxRefundReq reqdata) {
		logger.debug("refund req:{}", reqdata);
		if(!EncryptionUtil.validateSign(reqdata, encryptionSalt)){
			logger.error("签名错误,请求退款失败");
			return error(ErrorCode.FAILED, "签名错误,请求退款失败", null);
		}
		Refund refund = pingxxService.refund(reqdata);
		return success(refund);
	}

	/**
	 * 监听pingxx事件
	 * 
	 * @param dataString
	 * @return
	 */
	@RequestMapping(value = "/webhook", method = { RequestMethod.POST })
	public void webhook(@RequestBody String dataString) {
		try {
			logger.debug("webhook dataString:{}", dataString);
			String signatureString = request.getHeader("x-pingplusplus-signature");
			if (StringUtils.isNotBlank(signatureString)) {
				logger.debug("webhook signatureString:{}", signatureString);
				boolean verifyResult = pingxxService.verifyData(dataString, signatureString);
				logger.debug("webhook verifyResult:{}", verifyResult);
				if (verifyResult) {
					pingxxService.processEvent(dataString);
				}
			}
		} catch (Exception e) {
			logger.warn("webhook process fail", e);
			response.setStatus(500);
			return;
		}
		response.setStatus(200);
	}
	
	@RequestMapping(value = "/auth", method = { RequestMethod.POST })
	public ApiResponse auth(@Valid @RequestBody PingxxAuthReq reqdata) {
		logger.debug("auth req:{}", reqdata);
		if(!EncryptionUtil.validateSign(reqdata, encryptionSalt)){
			logger.error("签名错误,请求认证失败");
			return error(ErrorCode.FAILED, "签名错误,请求认证失败", null);
		}
		Identification result = pingxxService.auth(reqdata);
		return success(result);
	}

}
