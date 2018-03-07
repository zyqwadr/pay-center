package xyz.nesting.payment.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;

import xyz.nesting.common.exceptions.BusinessException;
import xyz.nesting.payment.model.ali.AliPayNotifySync;
import xyz.nesting.payment.model.ali.AliPayRequest;
import xyz.nesting.payment.model.ali.AliRefundRequest;

/**
 * 
 * 
 * @Description: 支付宝服务
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年6月3日-上午11:21:52
 *
 */

public interface AliPayService {
	
	/**
	 * 手机网站支付接口
	 * 
	 * @param requestMap
	 * @return
	 */
	public String payForForm(AliPayRequest AliPayRequest);
	
	/**
	 * 手机网站支付接口Version 2
	 * 
	 * @param requestMap
	 * @return
	 */
	public String payForFormV2(AliPayRequest AliPayRequest) throws AlipayApiException ;
	
	/**
	 * App支付接口
	 * 
	 * @param requestMap
	 * @return
	 */
	public String payForApp(AliPayRequest AliPayRequest) throws UnsupportedEncodingException;
	
	/**
	 * App支付接口Version 2
	 * 
	 * @param requestMap
	 * @return
	 */
	public String payForAppV2(AliPayRequest aliPayRequest) throws AlipayApiException;
	
	/**
	 * 扫码支付接口
	 * 
	 * @param requestMap
	 * @return
	 */
	public String payForScan(AliPayRequest aliPayRequest) throws AlipayApiException;
	
	/**
	 * 支付接口
	 * 
	 * @param requestMap
	 * @return
	 */
	public String payForUrl(AliPayRequest AliPayRequest) throws UnsupportedEncodingException;
	
	
	/**
	 * 支付宝回调接口
	 * 
	 * @param request
	 * @return
	 */
	public void notify(HttpServletRequest request) throws UnsupportedEncodingException, IOException, BusinessException;
	
	/**
	 * 支付宝回调接口
	 * 
	 * @param request
	 * @return
	 * @throws AlipayApiException 
	 */
	public boolean notifyV2(HttpServletRequest request) throws UnsupportedEncodingException, IOException, BusinessException, AlipayApiException;
	
	/**
	 * 支付宝退款
	 * 
	 * @param request
	 * @return
	 */
	public String refund(AliRefundRequest aliRefundRequest) throws UnsupportedEncodingException, IOException, BusinessException, AlipayApiException;

	/**
	 * @param 退款查询
	 * @throws AlipayApiException 
	 */
	public AlipayTradeFastpayRefundQueryResponse refundQuery(String outTradeNo) throws AlipayApiException;

	/**
	 * @param outTradeNo
	 * @return
	 */
	public List<AliPayNotifySync> getPaySuccessOrder(String outTradeNo);

	/**
	 * @param outTradeNo
	 */
	public int selectCountAliRefundByOutTradeNo(String outTradeNo);
	
	/**
	 * @param outTradeNo
	 */
	public BigDecimal getAliSettleMoney(Date beginTime, Date endTime);
	
	/**
	 * @param outTradeNo
	 */
	public BigDecimal getAliSettleRefundMoney(Date beginTime, Date endTime);

	/**
	 * 
	 */
	public void checkAliRefundStatus();

	/**
	 * @param billType
	 * @param billDate
	 */
	public String getBillUrl(String billType, String billDate);

	String getAliNotifyUrl(String outTradeNo) throws BusinessException;

}
