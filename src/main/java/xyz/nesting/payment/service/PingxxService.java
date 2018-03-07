package xyz.nesting.payment.service;

import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Identification;
import com.pingplusplus.model.Refund;

import xyz.nesting.payment.protocol.pingxx.PingxxAuthReq;
import xyz.nesting.payment.protocol.pingxx.PingxxPayReq;
import xyz.nesting.payment.protocol.pingxx.PingxxRefundReq;

public interface PingxxService {

	/**
	 * 创建支付charge
	 * @param channel
	 * @param reqdata
	 * @return
	 */
	Charge createCharge(String channel, PingxxPayReq reqdata);
	
	/**
	 * 退款
	 * @param reqdata
	 * @return
	 */
	Refund refund(PingxxRefundReq reqdata);
	
	/**
	 * 验证 Webhooks 签名
	 * @param dataString
	 * @param signatureString
	 * @return
	 */
	boolean verifyData(String dataString, String signatureString);

	/**
	 * 处理 pingxx 事件
	 * @param dataString
	 */
	void processEvent(String dataString) throws Exception;

	/**
	 * 认证接口
	 * @param reqdata
	 * @return
	 */
	Identification auth(PingxxAuthReq reqdata);

}
