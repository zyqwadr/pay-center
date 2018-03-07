package xyz.nesting.payment.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.config.AlipayConfig;
import com.alipay.util.AlipayCore;
import com.alipay.util.AlipayNotify;
import com.alipay.util.AlipaySubmit;

import xyz.nesting.common.context.SpringAppConfing;
import xyz.nesting.common.exceptions.BusinessException;
import xyz.nesting.common.hash.MD5;
import xyz.nesting.common.io.json.ZcJsonKit;
import xyz.nesting.payment.constant.AlipayConfigV2;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.constant.IntbeePaymentConstant;
import xyz.nesting.payment.dao.ali.AliPayMapper;
import xyz.nesting.payment.model.ali.AliPayNotifySync;
import xyz.nesting.payment.model.ali.AliPayRequest;
import xyz.nesting.payment.model.ali.AliRefundRequest;
import xyz.nesting.payment.protocol.ApiResponse;
import xyz.nesting.payment.service.AliPayService;
import xyz.nesting.payment.util.DownloadUtil;
import xyz.nesting.payment.util.HttpRequest;

/**
 * 
 * 
 * @Description: 支付宝服务
 * @Author qizai
 * @CreateAt 2016年6月3日-上午11:21:52
 *
 */
@Service
public class AliPayServiceImpl implements AliPayService {
	private Logger logger = LoggerFactory.getLogger(AliPayServiceImpl.class);

	public static final String CHATSET = "UTF-8";

	@Autowired
	private AliPayMapper mAliPayMapper;

	/**
	 * 构造表单form形式的html支付参数
	 */
	@Override
	public String payForForm(AliPayRequest aliPayRequest) {
		// 保存请求数据
		logger.debug("Alipay order out trade no, [{}]", aliPayRequest.getOutTradeNo());
		mAliPayMapper.insertAliPayRequest(aliPayRequest);
		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequestForm(buildPayRequestParamForWap(aliPayRequest), "get", "确认");
		return sHtmlText;

	}

	/**
	 * 构造表单form形式的html支付参数,V2版本
	 * 
	 * @throws AlipayApiException
	 */
	@Override
	public String payForFormV2(AliPayRequest aliPayRequest) throws AlipayApiException {
		// 保存请求数据
		logger.debug("V2 Alipay order out trade no, [{}]", aliPayRequest.getOutTradeNo());
		mAliPayMapper.insertAliPayRequest(aliPayRequest);

		AlipayClient alipayClient = getAlipayClient();

		AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();// 创建API对应的request
		alipayRequest.setReturnUrl(aliPayRequest.getShowUrl());
		alipayRequest.setNotifyUrl(AlipayConfigV2.notify_url);// 在公共参数中设置回跳和通知地址

		alipayRequest.setBizContent("{" + "    \"out_trade_no\":\"" + aliPayRequest.getOutTradeNo() + "\","
				+ "    \"total_amount\":" + aliPayRequest.getTotalFee() + "," + "    \"subject\":\""
				+ aliPayRequest.getSubject() + "\"," + "    \"seller_id\":\"" + AlipayConfigV2.seller_id + "\","
				+ "    \"product_code\":\"INTBEE_WAP_PAY\"" + "  }");// 填充业务参数

		String sHtmlText = alipayClient.pageExecute(alipayRequest).getBody();

		logger.debug(sHtmlText);
		//System.out.print(sHtmlText);
		return sHtmlText;
	}
	
	/**
	 * 构造App支付参数
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public String payForApp(AliPayRequest aliPayRequest) throws UnsupportedEncodingException {
		// 保存请求数据
		mAliPayMapper.insertAliPayRequest(aliPayRequest);
		// 建立请求
		String sPara = AlipaySubmit.buildRequestApp(buildPayRequestParamForApp(aliPayRequest));
		logger.error("App pay param:[{}]", sPara);
		
		return sPara;
	}

	
	/**
	 * 构造App支付参数,V2版本
	 * 
	 * @throws AlipayApiException
	 */
	@Override
	public String payForAppV2(AliPayRequest aliPayRequest) throws AlipayApiException {
		// 保存请求数据
		logger.debug("App Alipay order out trade no, [{}]", aliPayRequest.getOutTradeNo());
		mAliPayMapper.insertAliPayRequest(aliPayRequest);

		AlipayClient alipayClient = getAlipayClient();

		AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();// 创建API对应的request
		alipayRequest.setApiVersion("1.0");
		alipayRequest.setReturnUrl(aliPayRequest.getShowUrl());
		alipayRequest.setNotifyUrl(AlipayConfigV2.notify_url);// 在公共参数中设置回跳和通知地址

		alipayRequest.setBizContent("{" + "    \"out_trade_no\":\"" + aliPayRequest.getOutTradeNo() + "\","
				+ "    \"total_amount\":" + aliPayRequest.getTotalFee() + "," + "    \"subject\":\""
				+ aliPayRequest.getSubject() + "\"," + "    \"seller_id\":\"" + AlipayConfigV2.seller_id + "\","
				+ "    \"product_code\":\"INTBEE_WAP_PAY\"" + "  }");// 填充业务参数

		//String sdkBody = alipayClient.pageExecute(alipayRequest).getBody();
		String sdkBody = alipayClient.sdkExecute(alipayRequest).getBody();
		logger.debug(sdkBody);
		
		return sdkBody;
	}

	/**
	 * 构造扫码支付参数
	 * 
	 * @throws AlipayApiException
	 */
	@Override
	public String payForScan(AliPayRequest aliPayRequest) throws AlipayApiException {
		// 保存请求数据
		logger.debug("Scan Alipay order out trade no, [{}]", aliPayRequest.getOutTradeNo());
		mAliPayMapper.insertAliPayRequest(aliPayRequest);
		
		AlipayClient alipayClient = getAlipayClient();
		
		AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
		request.setBizContent("{" +
		 "    \"out_trade_no\":\"" + aliPayRequest.getOutTradeNo() + "\"," +
		 "    \"total_amount\":"+ aliPayRequest.getTotalFee() +"," +
		 "    \"subject\":\"" + aliPayRequest.getSubject() + "\"," +
		 "    \"store_id\":\""+ AlipayConfigV2.seller_id +"\"," +
		 "    \"timeout_express\":\"90m\"}");//设置业务参数
		AlipayTradePrecreateResponse response = alipayClient.execute(request);
		
		return response.getQrCode();
	}
	
	
	/**
	 * 构造url形式的支付参数
	 */
	@Override
	public String payForUrl(AliPayRequest aliPayRequest) throws UnsupportedEncodingException{
		// 保存请求数据
		mAliPayMapper.insertAliPayRequest(aliPayRequest);
		// // 建立请求
		String sUrl = AlipaySubmit.buildRequestUrl(buildPayRequestParamForWap(aliPayRequest));
		return sUrl;

	}

	/**
	 * 异步回调通知处理接口
	 * 
	 * @throws BusinessException
	 */
	@Override
	public void notify(HttpServletRequest request) throws UnsupportedEncodingException, IOException, BusinessException {
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = buildPayRequestParam(request);
		String notifyText = AlipayCore.createLinkString(params);
		logger.info("支付宝异步通知内容:{}", notifyText);
		if (!AlipayNotify.verify(params)) {
			// 验证失败
			logger.error("支付宝回调验证失败！");
			return;
		}
		// 保存异步通知消息内容
		AliPayNotifySync notifySync = buildPayNotifySyncParams(request);
		mAliPayMapper.inserAliNotifyInfo(notifySync);
		// 通知子系统更新支付状态
		if ("TRADE_SUCCESS".equals(notifySync.getTradeStatus())) {
			String out_trade_no = notifySync.getOutTradeNo();
			String cbUrl = getAliNotifyUrl(out_trade_no);
		
			// 加密获取sign
			String timestamp = String.valueOf(System.currentTimeMillis());
			String salt = SpringAppConfing.get("encryption.salt");
			String sign = MD5.MD5Encode(out_trade_no + timestamp + salt);
		
			params.put("timestamp", timestamp);
			params.put("sign", sign);
			params.put("out_trade_no", out_trade_no);
			params.put("tc_order_paystatus", "SUCCESS");
			
			logger.debug("AliPay pay success notify start, orderNo:{}", out_trade_no);
			String result = new HttpRequest().sendPostJson(cbUrl, params);
			ApiResponse resp = ZcJsonKit.nonNullCaseMapper().fromJson(result, ApiResponse.class);
			if (resp == null || resp.getCode() != 0) {
				logger.error("AliPay pay success notify fail, orderNo:{}", out_trade_no);
				throw new BusinessException(ErrorCode.PAY_SUCCESS_NOTIFY_FAIL, "订单回调失败");
			}
			logger.debug("AliPay pay success notify done, orderNo:{}", out_trade_no);
		}
	}

	@Override
	public boolean notifyV2(HttpServletRequest request)
			throws UnsupportedEncodingException, IOException, BusinessException, AlipayApiException {
		logger.debug("Alipay order out trade no, [{}]", request.getParameter("out_trade_no"));
		// 支付宝回调，不做任何check
		Map<String, String> paramsMap = buildPayRequestParam(request);
		boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, AlipayConfigV2.alipay_public_key, AlipayConfigV2.input_charset); // 调用SDK验证签名
		AliPayNotifySync notifySync = buildPayNotifySyncParams(request);

		if(signVerified){
			// 保存异步通知消息内容
			mAliPayMapper.inserAliNotifyInfo(notifySync);
			
			if ("TRADE_SUCCESS".equals(notifySync.getTradeStatus())) {
		
				String out_trade_no = notifySync.getOutTradeNo();
				
				String cbUrl = getAliNotifyUrl(out_trade_no);
		
				// 加密获取sign
				String timestamp = String.valueOf(System.currentTimeMillis());
				String salt = SpringAppConfing.get("encryption.salt");
				String sign = MD5.MD5Encode(out_trade_no + timestamp + salt);
		
				Map<String, String> params = new HashMap<String, String>();
				params.put("timestamp", timestamp);
				params.put("sign", sign);
				params.put("out_trade_no", out_trade_no);
				params.put("tc_order_paystatus", "SUCCESS");
			
				logger.debug("AliPay V2 pay success notify start, orderNo:{}", out_trade_no);
				String result = new HttpRequest().sendPostJson(cbUrl, params);
				ApiResponse resp = ZcJsonKit.nonNullCaseMapper().fromJson(result, ApiResponse.class);
				if (resp == null || resp.getCode() != 0) {
					logger.error("AliPay V2 pay success notify fail, orderNo:{}", out_trade_no);
					throw new BusinessException(ErrorCode.PAY_SUCCESS_NOTIFY_FAIL, "订单回调失败");
				}
				logger.debug("AliPay V2 pay success notify done, orderNo:{}", out_trade_no);
			}
	
		}else{
			System.out.println("alipay error");
			logger.warn("Alipay 支付校验失败：[{}]", notifySync.getOutTradeNo());
		}

		return signVerified;
	}
	
	@Override
	public String getAliNotifyUrl(String outTradeNo) throws BusinessException{
		String cbURL = mAliPayMapper.selectCallBackUrl(outTradeNo);
		if(StringUtils.isBlank(cbURL)){
			logger.error("Sub system Call back can not be found:{[]}", outTradeNo);
			throw new BusinessException(ErrorCode.OUT_TRADE_NO_NOT_FOUND, "订单号查询失败");
		}
		
		return cbURL;
	}

	// /**
	// * 签名 生成请求支付宝的参数
	// *
	// * @param orders
	// * @return
	// * @throws UnsupportedEncodingException
	// */
	// @Override
	// public String buildAlipayIframe(Map orders) throws
	// UnsupportedEncodingException {
	// Map<String, String> param = getPayInfo(orders);
	// String params = AlipayCore.createLinkString(param);
	// String sign = com.alipay.sign.RSA.sign(params, privateKeyPKCS8_alipay,
	// CHATSET);
	// sign = URLEncoder.encode(sign, CHATSET);
	// params = params + "&sign=" + sign + "&sign_type=RSA";
	// return params;
	// }

	/**
	 * 获取支付信息
	 */
	private Map<String, String> buildPayRequestParamForWap(AliPayRequest AliPayRequest) {
		// ------------必填----------------/
		// 订单名称，
		String out_trade_no = AliPayRequest.getOutTradeNo();
		// 订单名称
		String subject = AliPayRequest.getSubject();
		// 付款金额
		String total_fee = String.valueOf(AliPayRequest.getTotalFee());
		// 收银台页面上，商品展示的超链接
		String show_url = AliPayRequest.getShowUrl();
		// ------------必填END----------------/
		// 商品描述，可空
		String body = AliPayRequest.getBody();

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", AlipayConfig.service_wap);
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("seller_id", AlipayConfig.seller_id);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", AlipayConfig.payment_type);
		sParaTemp.put("notify_url", AlipayConfig.notify_url);
		sParaTemp.put("return_url", show_url);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("show_url", show_url);
		sParaTemp.put("body", body);

		return sParaTemp;
	}
	

	/**
	 * 获取支付信息
	 */
	private Map<String, String> buildPayRequestParamForApp(AliPayRequest AliPayRequest) {
		// ------------必填----------------/
		// 订单名称，
		String out_trade_no = AliPayRequest.getOutTradeNo();
		// 订单名称
		String subject = AliPayRequest.getSubject();
		// 付款金额
		String total_fee = String.valueOf(AliPayRequest.getTotalFee());
		// 收银台页面上，商品展示的超链接
		String show_url = AliPayRequest.getShowUrl();
		// ------------必填END----------------/
		// 商品描述，可空
		String body = AliPayRequest.getBody();

		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", AlipayConfig.service_app);
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("seller_id", AlipayConfig.seller_id);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("payment_type", AlipayConfig.payment_type);
		sParaTemp.put("notify_url", AlipayConfig.notify_url);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("it_b_pay", "30m");
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("show_url", show_url);
		sParaTemp.put("return_url", AlipayConfig.notify_url);
		sParaTemp.put("body", body);

		return sParaTemp;
	}
	
	private Map<String, String> buildPayRequestParam(HttpServletRequest request){
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Entry<String, String[]> entry : requestParams.entrySet()) {
			String name = entry.getKey();
			String[] values = entry.getValue();
			StringBuilder b = new StringBuilder();
			boolean first = false;
			for (String val : values) {
				if (first) {
					b.append(",");
				}
				b.append(val);
				first = true;
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(name, b.toString());
		}
		
		return params;
	}

	public String getParam(final HttpServletRequest request, final String key) throws UnsupportedEncodingException {
		String value = request.getParameter(key);
		if (null == value) {
			return null;
		}
		String param = new String(value.getBytes("ISO-8859-1"), CHATSET);
		return param;
	}

	/**
	 * 获取支付信息
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private AliPayNotifySync buildPayNotifySyncParams(HttpServletRequest request) throws UnsupportedEncodingException {
		AliPayNotifySync notifySync = new AliPayNotifySync();
		notifySync.setOutTradeNo(getParam(request, "out_trade_no"));
		notifySync.setTradeStatus(getParam(request, "trade_status"));
		notifySync.setTradeNo(getParam(request, "trade_no"));
		notifySync.setTotalFee(NumberUtils.toDouble(getParam(request, "total_amount")));
		return notifySync;
	}

	@Override
	public String refund(AliRefundRequest aliRefundRequest) throws UnsupportedEncodingException, IOException, BusinessException, AlipayApiException {
		logger.debug("Ali Refund Request : {}", aliRefundRequest);
		String outRequestNo = "refund" + UUID.randomUUID().toString().replaceAll("-", "");
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();//创建API对应的request类
		request.setBizContent("{" +
				"    \"out_trade_no\":\""+aliRefundRequest.getOutTradeNo()+"\"," +
				"    \"out_request_no\":\""+outRequestNo+"\"," +
				"    \"refund_amount\":\""+aliRefundRequest.getRefundAmount()+"\"" +
				"  }");//设置业务参数
		AlipayTradeRefundResponse response = getAlipayClient().execute(request);//通过alipayClient调用API，获得对应的response类
		logger.debug("Ali Refund Result : {}", response.getBody());
		if (!response.isSuccess()) throw new BusinessException(ErrorCode.FAILED, "支付宝退款失败");
		mAliPayMapper.insertAliRefundRequest(aliRefundRequest.getOutTradeNo(), outRequestNo, aliRefundRequest.getRefundAmount(), aliRefundRequest.getRefundAmount(), IntbeePaymentConstant.REFUND_REQUEST);
		return response.getMsg();
	}

	private AlipayClient getAlipayClient() {
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfigV2.service, AlipayConfigV2.ali_app_id,
				AlipayConfigV2.private_key, AlipayConfigV2.request_format, AlipayConfigV2.input_charset,
				AlipayConfigV2.alipay_public_key);
		return alipayClient;
	}

	@Override
	public AlipayTradeFastpayRefundQueryResponse refundQuery(String outTradeNo) throws AlipayApiException {
		logger.debug("Ali Refund Query Request : {}", outTradeNo);
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfigV2.service, AlipayConfigV2.ali_app_id,
				AlipayConfigV2.private_key, AlipayConfigV2.request_format, "GBK",
				AlipayConfigV2.alipay_public_key);
		String outRequestNo = "refund" + outTradeNo;
		AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
		request.setBizContent("{" +
				"\"out_trade_no\":\""+outTradeNo+"\"," +
				"\"out_request_no\":\""+outRequestNo+"\"" +
		"}");
		AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
		logger.debug("Ali Refund Query Result : {}", response.getBody());
		if(response.isSuccess()) {
			mAliPayMapper.updateAliPayRefundStatus(outTradeNo, IntbeePaymentConstant.REFUND_SUCCESS);
		}
		return response;
	}

	@Override
	public List<AliPayNotifySync> getPaySuccessOrder(String outTradeNo) {
		return mAliPayMapper.selectPaySuccessOrder(outTradeNo);
	}

	@Override
	public int selectCountAliRefundByOutTradeNo(String outTradeNo) {
		return mAliPayMapper.selectCountAliRefundByOutTradeNo(outTradeNo);
	}

	@Override
	public BigDecimal getAliSettleMoney(Date beginTime, Date endTime) {
		BigDecimal settleMoney = mAliPayMapper.selectAliSettleMoney(beginTime, endTime);
		
		return settleMoney == null ? BigDecimal.ZERO : settleMoney;
		
	}
	
	@Override
	public BigDecimal getAliSettleRefundMoney(Date beginTime, Date endTime) {
		BigDecimal settleMoney = mAliPayMapper.selectAliSettleRefundMoney(beginTime, endTime);
		
		return settleMoney == null ? BigDecimal.ZERO : settleMoney;
		
	}

	@Override
	public void checkAliRefundStatus() {
		List<String> outTradeNoList = mAliPayMapper.selectRefundStatus(IntbeePaymentConstant.REFUND_REQUEST);
		if (outTradeNoList == null) return;
		for (String outTradeNo : outTradeNoList) {
			try {
				refundQuery(outTradeNo);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("",e);
			}
		}
	}
	
	@Override
	public String getBillUrl(String billType, String billDate) {
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfigV2.service, AlipayConfigV2.ali_app_id, AlipayConfigV2.private_key, AlipayConfigV2.request_format, "GBK", AlipayConfigV2.alipay_public_key, AlipayConfigV2.sign_type);
		AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
		request.setBizContent("{" +"\"bill_type\":\""+billType+"\"," +"\"bill_date\":\""+billDate+"\"" +"}");
		AlipayDataDataserviceBillDownloadurlQueryResponse response = null;
		try {
			response = alipayClient.execute(request);
			if(response.isSuccess()) {
				logger.debug("getBill succsee");
				return response.getBillDownloadUrl();
			} else {
				logger.error("getBill fail");
			}
		} catch (AlipayApiException e) {
			logger.error("getBill fail", e);
		}
		return null;
	}
	
	public String getBillFilePath(String dateStr) {
		String saveAliBillFilePath = "download/bill/alipay";
		File saveAliBillDir = new File(saveAliBillFilePath);
		if ( !saveAliBillDir.exists()) {
			saveAliBillDir.mkdirs(); 
			logger.debug("create saveAliBillDir :{}", saveAliBillDir.getAbsolutePath());
		}
		return saveAliBillFilePath + "/alibill_" + dateStr + ".csv.zip";
	}
	
	public void generateBillFile() {
		Date lastDate = DateTime.now().plusDays(-1).toDate();
		String downloadUrl = getBillUrl("trade", new SimpleDateFormat("yyyy-MM-dd").format(lastDate));
		if (StringUtils.isBlank(downloadUrl)) {
			logger.error("generateBillFile error, downloadUrl is null");
			return;
		}
		DownloadUtil.downloadFile(downloadUrl, getBillFilePath(new SimpleDateFormat("yyyyMMdd").format(lastDate)));
		logger.debug("generateBillFile done");
	}

}
