package xyz.nesting.payment.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.tencent.protocol.pay_protocol.GetOpenidResData;
import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.WxBrowserPayReqData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_protocol.RefundResData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;

import xyz.nesting.common.exceptions.BusinessException;

public interface WxPayService {
	public Map payForForm(ScanPayReqData PayRequest) throws Exception ;
	public Map wxBrowserPayForForm(WxBrowserPayReqData PayRequest) throws Exception;
	public void notify(HttpServletRequest request) throws UnsupportedEncodingException, IOException,ParserConfigurationException, SAXException,BusinessException; 
	public GetOpenidResData getOpenId(String code) throws Exception;
	public RefundResData refund(RefundReqData refundReqData) throws Exception;
	public Map<String, Object> refundQuery(RefundQueryReqData refundQureyReqData) throws Exception;
	public List<Map<String, Object>> getPaySuccessOrder(String outTradeNo);
	public int selectCountWxRefundByOutTradeNo(String outTradeNo);
	
	public BigDecimal getWxSettleMoney(Date beginTime, Date endTime);
	
	public BigDecimal getWxSettleRefundMoney(Date beginTime, Date endTime);
	public void checkWxRefundStatus();
	List<List<String>> getBillContent(String deviceInfo,String billDate,String billType);

	String getWxNotifyUrl(String outTradeNo) throws BusinessException;
}
