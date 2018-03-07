/**
 * 
 */
package xyz.nesting.payment.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipaySubmit;

import xyz.nesting.payment.protocol.ali.AlipayAcquireRefundResponse;

/**
 * @author staff
 *
 */
public class AlipayUtil {

	private static final Logger logger = LoggerFactory.getLogger(AlipayUtil.class);
	final static String service_alipay_acquire_refund = "alipay.acquire.refund";

	/**
	 * @param trade_no
	 *            支付宝交易号
	 * @param out_trade_no
	 *            商户订单号
	 * @param out_request_no
	 *            退款请求单号（非全额退款必须提供）
	 * @param refund_amount
	 *            退款金额（元）
	 * @param refund_reason
	 *            (退款原因)
	 * @return
	 */
	private static String getAcquireRefundUrl(String trade_no, String out_trade_no, String out_request_no,
			double refund_amount, String refund_reason) throws UnsupportedEncodingException{
		Map<String, String> sParaTemp = new HashMap<String, String>();
		// 商户订单号
		if (null != out_trade_no) {
			sParaTemp.put("out_trade_no", out_trade_no);
		}
		// 支付宝交易号
		if (null != trade_no) {
			sParaTemp.put("trade_no", trade_no);
		}
		// 退款请求单号
		if (null != out_request_no) {
			sParaTemp.put("out_request_no", out_request_no);
		}
		// 退款金额，单位为元
		sParaTemp.put("refund_amount", DoubleHelper.formatNumber(refund_amount, 2));
		sParaTemp.put("refund_reason", refund_reason);
		sParaTemp.put("service", service_alipay_acquire_refund);
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		return AlipaySubmit.buildRequestUrl(sParaTemp);
	}

	/**
	 * 发起退款请求
	 * 
	 * @param trade_no
	 *            支付宝交易号
	 * @param out_trade_no
	 *            商户订单号
	 * @param out_request_no
	 *            退款请求单号（非全额退款必须提供）
	 * @param refund_amount
	 *            退款金额（元）
	 * @param refund_reason
	 *            (退款原因)
	 * @return
	 */
	public static AlipayAcquireRefundResponse getAcquireRefund(String trade_no, String out_trade_no,
			String out_request_no, double refund_amount, String refund_reason) {
		try {
			HttpRequest req = new HttpRequest();
			String xmlString = req
					.doGet(getAcquireRefundUrl(trade_no, out_trade_no, out_request_no, refund_amount, refund_reason));
			Document doc = DocumentHelper.parseText(xmlString);
			Element root_alipay = doc.getRootElement();
			// T-成功，F-失败
			String is_success = root_alipay.elementTextTrim("is_success");
			String error = root_alipay.elementTextTrim("error");
			Element response_ele = root_alipay.element("response");
			Element alipay_ele = response_ele.element("alipay");
			String display_message = alipay_ele.elementTextTrim("display_message");
			// 本次退款是否发生资金变动
			String fund_change = alipay_ele.elementTextTrim("fund_change");
			String refund_fee = alipay_ele.elementTextTrim("refund_fee");
			// 退款成功SUCCESS、退款失败FAIL、UNKNOWN结果未知
			String result_code = alipay_ele.elementTextTrim("result_code");
			String _out_trade_no = alipay_ele.elementTextTrim("out_trade_no");
			String _trade_no = alipay_ele.elementTextTrim("trade_no");

			AlipayAcquireRefundResponse acquireRefundResponse = new AlipayAcquireRefundResponse();
			acquireRefundResponse.setIs_success(is_success);
			acquireRefundResponse.setError(error);
			acquireRefundResponse.setDisplay_message(display_message);
			acquireRefundResponse.setFund_change(fund_change);
			acquireRefundResponse.setRefund_fee(refund_fee);
			acquireRefundResponse.setResult_code(result_code);
			acquireRefundResponse.setOut_trade_no(_out_trade_no);
			acquireRefundResponse.setTrade_no(_trade_no);
			return acquireRefundResponse;
		} catch (IOException | DocumentException e) {
			logger.error("退款异常:" + e.getMessage(), e);
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			HttpRequest req = new HttpRequest();
			String xmlString = req
					.doGet(getAcquireRefundUrl("2016010521001004590064132461", null, null, 1.0, "交易失败退款"));
			Document doc = DocumentHelper.parseText(xmlString);
			Element root_alipay = doc.getRootElement();
			// T-成功，F-失败
			String is_success = root_alipay.elementTextTrim("is_success");
			String error = root_alipay.elementTextTrim("error");

			Element response_ele = root_alipay.element("response");
			Element alipay_ele = response_ele.element("alipay");
			String display_message = alipay_ele.elementTextTrim("display_message");
			String fund_change = alipay_ele.elementTextTrim("fund_change");
			String refund_fee = alipay_ele.elementTextTrim("refund_fee");
			String result_code = alipay_ele.elementTextTrim("result_code");
			String out_trade_no = alipay_ele.elementTextTrim("out_trade_no");
			String trade_no = alipay_ele.elementTextTrim("trade_no");
			//
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("is_success=").append(is_success);
			stringBuilder.append(",error=").append(error);
			stringBuilder.append(",display_message=").append(display_message);
			// 本次退款是否发生资金变动
			stringBuilder.append(",fund_change=").append(fund_change);
			stringBuilder.append(",refund_fee=").append(refund_fee);
			// 退款成功SUCCESS、退款失败FAIL、UNKNOWN结果未知
			stringBuilder.append(",result_code=").append(result_code);
			stringBuilder.append(",trade_no=").append(trade_no);
			stringBuilder.append(",out_trade_no=").append(out_trade_no);
			// 部分退款时才有
			Element refund_detail_ele = alipay_ele.element("refund_detail_item_list");
			List<Element> tradeFundBillList = refund_detail_ele.elements("TradeFundBill");
			for (Element tradeFundBill : tradeFundBillList) {
				String amount = tradeFundBill.elementTextTrim("amount");
				String fund_channel = tradeFundBill.elementTextTrim("fund_channel");
				stringBuilder.append(",[amount=").append(amount);
				stringBuilder.append(",fund_channel=").append(fund_channel);
				stringBuilder.append("]");
			}
			AlipayAcquireRefundResponse acquireRefundResponse = new AlipayAcquireRefundResponse();
			acquireRefundResponse.setIs_success(is_success);
			acquireRefundResponse.setError(error);
			acquireRefundResponse.setDisplay_message(display_message);
			acquireRefundResponse.setFund_change(fund_change);
			acquireRefundResponse.setRefund_fee(refund_fee);
			acquireRefundResponse.setResult_code(result_code);
			acquireRefundResponse.setOut_trade_no(out_trade_no);
			acquireRefundResponse.setTrade_no(trade_no);

			System.out.println(stringBuilder.toString());
		} catch (IOException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
