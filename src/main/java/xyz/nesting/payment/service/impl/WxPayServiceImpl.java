package xyz.nesting.payment.service.impl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tencent.WXPay;
import com.tencent.business.DownloadBillBusiness;
import com.tencent.common.Configure;
import com.tencent.common.RandomStringGenerator;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.common.XMLParser;
import com.tencent.protocol.downloadbill_protocol.DownloadBillReqData;
import com.tencent.protocol.downloadbill_protocol.DownloadBillResData;
import com.tencent.protocol.pay_protocol.GetOpenidResData;
import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.ScanPayResData;
import com.tencent.protocol.pay_protocol.WxAppPayReqData;
import com.tencent.protocol.pay_protocol.WxBrowserPayReqData;
import com.tencent.protocol.refund_protocol.RefundReqData;
import com.tencent.protocol.refund_protocol.RefundResData;
import com.tencent.protocol.refund_query_protocol.RefundQueryReqData;

import xyz.nesting.common.context.SpringAppConfing;
import xyz.nesting.common.exceptions.BusinessException;
import xyz.nesting.common.hash.MD5;
import xyz.nesting.common.io.json.ZcJsonKit;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.constant.IntbeePaymentConstant;
import xyz.nesting.payment.dao.wx.WxPayMapper;
import xyz.nesting.payment.protocol.ApiResponse;
import xyz.nesting.payment.service.WxPayService;
import xyz.nesting.payment.util.ExcelExportUtil;
import xyz.nesting.payment.util.HttpClientUtil;
import xyz.nesting.payment.util.HttpRequest;

@Service
public class WxPayServiceImpl implements WxPayService {
	private Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);
	
	final static String SUCCESS = "SUCCESS";
	
	@Autowired
	private WxPayMapper mWxPayMapper;
	
	@Override
	public Map<String, Object> payForForm(ScanPayReqData PayRequest) throws Exception, IllegalAccessException {
		ScanPayReqData PayRequestClone = PayRequest.clone();
		mWxPayMapper.insertWxPayRequest(PayRequest);
		PayRequest.setApp_id(null);
		PayRequest.setCall_back_url(null);
		WxRequestFilter(PayRequest);
		String result =  WXPay.requestScanPayService(PayRequest);
		logger.debug("wxpay result : {}", result);
		Map <String,Object> mapResult = XMLParser.getMapFromXML(result);
		if(SUCCESS.equals(mapResult.get("return_code")) &&SUCCESS.equals(mapResult.get("result_code"))){
			Map <String,Object> returnData = new HashMap<String, Object>();
			returnData.put("code_url",mapResult.get("code_url"));
			logger.debug("wxpay returnData : {}", returnData);
			return returnData;
		} else if ("201 商户订单号重复".equals(mapResult.get("err_code_des"))){
			//解决微信浏览器调起支付后未支付，换浏览器后重新调起二维码支付时，无法支付的问题
			String suffix = "R" + RandomStringGenerator.getRandomStringByLength(3);
			String newOutTradeNo = PayRequestClone.getOut_trade_no() + suffix;
			PayRequestClone.setOut_trade_no(newOutTradeNo);
			return payForForm(PayRequestClone);
		}
		logger.error("wxpayForForm failure = [{}]", mapResult);
		
		throw new IllegalAccessException(mapResult.get("return_msg").toString());
	}
	
	@Override
	public Map<String, Object> wxBrowserPayForForm(WxBrowserPayReqData PayRequest) throws Exception, IllegalAccessException {
		// TODO Auto-generated method stub
		mWxPayMapper.insertWxbrowserPayRequest(PayRequest);
		PayRequest.setApp_id(null);
		PayRequest.setCall_back_url(null);
		WxRequestFilter(PayRequest);
		String result =  WXPay.requestWxBrowserPayService(PayRequest);
		logger.debug("wxBrowsPay  result : {}", result);
		Map <String,Object> mapResult = XMLParser.getMapFromXML(result);
		if(SUCCESS.equals(mapResult.get("return_code")) && SUCCESS.equals(mapResult.get("result_code"))){
			Map <String,Object> returnData = new HashMap<String, Object>();
			returnData.put("appId", Configure.getAppid());
			returnData.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
			returnData.put("nonceStr", UUID.randomUUID().toString());
			returnData.put("package", "prepay_id="+mapResult.get("prepay_id"));
			returnData.put("signType", "MD5");
			returnData.put("paySign", Signature.getSign(returnData));
			logger.debug("wxpayForForm returnData : {}", returnData);
			return returnData;
		}
		logger.error("wxpayForForm failure = [{}]", mapResult);
		throw new IllegalAccessException(mapResult.get("return_msg").toString());
	}
	
	public Map<String, Object> wxAppPayForForm(WxAppPayReqData PayRequest) throws Exception, IllegalAccessException {
		mWxPayMapper.insertWxAppPayRequest(PayRequest);
		PayRequest.setApp_id(null);
		PayRequest.setCall_back_url(null);
		PayRequest.setAuth_code(null);
		PayRequest.setProduct_id(null);
		WxRequestFilter(PayRequest);
		String result =  WXPay.requestWxAppPayService(PayRequest);
		logger.debug("wxAppPayForForm  result : {}", result);
		Map <String,Object> mapResult = XMLParser.getMapFromXML(result);
		if(SUCCESS.equals(mapResult.get("return_code")) && SUCCESS.equals(mapResult.get("result_code"))){
			Map <String,Object> returnData = new HashMap<String, Object>();
			returnData.put("appid", Configure.getAppID_App());
			returnData.put("partnerid", Configure.getMchID_App());
			returnData.put("prepayid", mapResult.get("prepay_id"));
			returnData.put("package", "Sign=WXPay");
			returnData.put("noncestr", RandomStringGenerator.getRandomStringByLength(32));
			returnData.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
			returnData.put("sign", Signature.getSign(returnData));
			logger.debug("wxAppPayForForm returnData : {}", returnData);
			return returnData;
		}
		logger.error("wxAppPayForForm failure = [{}]", mapResult);
		throw new IllegalAccessException(mapResult.get("return_msg").toString());
		
	}
	
	public RefundResData refund(RefundReqData refundReqData) throws Exception {
		String tradeType = WxRequestFilter(refundReqData);
		String result =  WXPay.requestRefundService(refundReqData, tradeType);
		RefundResData refundResData = (RefundResData) Util.getObjectFromXML(result, RefundResData.class);
		logger.debug("wx refund refundReqData : {}", refundReqData);
		if(SUCCESS.equals(refundResData.getReturn_code()) && SUCCESS.equals(refundResData.getResult_code())){
			mWxPayMapper.insertWxPayRefundRequest(refundResData);
			return refundResData;
		}
		logger.error("wxpayForForm failure = [{}]", refundResData);
		throw new IllegalAccessException(refundResData.getReturn_msg());
	}
	
	public Map<String, Object> refundQuery(RefundQueryReqData refundQureyReqData) throws Exception {
		WxRequestFilter(refundQureyReqData);
		String result =  WXPay.requestRefundQueryService(refundQureyReqData);
		Map <String,Object> mapResult = XMLParser.getMapFromXML(result);
		if("FAIL".equals(mapResult.get("return_code"))){
			logger.error("wxpayForForm failure = [{}]", mapResult);
			throw new IllegalAccessException(mapResult.get("return_msg").toString());
		}
		//退款状态：SUCCESS—退款成功 FAIL—退款失败 PROCESSING—退款处理中 CHANGE—转入代发
		if (SUCCESS.equals(mapResult.get("refund_status_0"))) {
			mWxPayMapper.updateWxPayRefundStatus(refundQureyReqData.getOut_trade_no(), IntbeePaymentConstant.REFUND_SUCCESS);
		}
		return mapResult;
	}
	
	private void WxRequestFilter(RefundQueryReqData refundReqData) throws IllegalAccessException {
		
		Map<String, String> tradeType = mWxPayMapper.selectTradeTypeFromResult(refundReqData.getOut_trade_no());
		if (MapUtils.isEmpty(tradeType)) {
			logger.error("refund query fail, order not refunding");
			throw new IllegalAccessException("refund query fail, order not refunding");
		}
		if (Configure.getApp_trade_type().equals(tradeType.get("trade_type"))) {
			refundReqData.setAppid(Configure.getAppID_App());
			refundReqData.setMch_id(Configure.getMchID_App());
		} else {
			refundReqData.setAppid(Configure.getAppid());
			refundReqData.setMch_id(Configure.getMchid());
		}
		
		refundReqData.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
		refundReqData.setSign(Signature.getSign(refundReqData.toMap()));
	}

	private void WxRequestFilter(WxAppPayReqData PayRequest) {
		String body = PayRequest.getBody();
		int bodyLength = body.length();
		PayRequest.setBody(bodyLength>40 ? body.substring(0, 40) : body);
		PayRequest.setAppid(Configure.getAppID_App());
		PayRequest.setMch_id(Configure.getMchID_App());
		PayRequest.setTrade_type(Configure.getApp_trade_type());
		PayRequest.setNotify_url(Configure.getNotifyUrl());
		PayRequest.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
		PayRequest.setSign(Signature.getSign(PayRequest.toMap()));
	}

	private void WxRequestFilter(ScanPayReqData PayRequest) throws IllegalAccessException {
		String body = PayRequest.getBody();
		int bodyLength = body.length();
		PayRequest.setBody(bodyLength>40 ? body.substring(0, 40) : body);
		PayRequest.setAppid(Configure.getAppid());
		PayRequest.setMch_id(Configure.getMchid());
		PayRequest.setTrade_type(Configure.getTradeType());
		PayRequest.setNotify_url(Configure.getNotifyUrl());
		PayRequest.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
		PayRequest.setSign(Signature.getSign(PayRequest.toMap()));
	}
	
	private void WxRequestFilter(WxBrowserPayReqData PayRequest) throws IllegalAccessException {
		String body = PayRequest.getBody();
		int bodyLength = body.length();
		PayRequest.setBody(bodyLength>40 ? body.substring(0, 40) : body);
		PayRequest.setAppid(Configure.getAppid());
		PayRequest.setMch_id(Configure.getMchid());
		PayRequest.setTrade_type(Configure.getJsapiTradeType());
		PayRequest.setNotify_url(Configure.getNotifyUrl());
		PayRequest.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
		PayRequest.setSign(Signature.getSign(PayRequest.toMap()));
	}
	
	private String WxRequestFilter(RefundReqData refundReqData) throws IllegalAccessException {
		Map<String, String> tradeType = mWxPayMapper.selectTradeTypeByOutTradeNo(refundReqData.getOut_trade_no() + "%");
		if (MapUtils.isEmpty(tradeType)) {
			logger.error("refund fail,order not pay success");
			throw new IllegalAccessException("refund fail,order not pay success");
		}
		if (Configure.getApp_trade_type().equals(tradeType.get("trade_type"))) {
			refundReqData.setAppid(Configure.getAppID_App());
			refundReqData.setMch_id(Configure.getMchID_App());
		} else {
			refundReqData.setAppid(Configure.getAppid());
			refundReqData.setMch_id(Configure.getMchid());
		}
		refundReqData.setOut_trade_no(tradeType.get("out_trade_no"));
		refundReqData.setOp_user_id(refundReqData.getMch_id());
		refundReqData.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
		refundReqData.setOut_refund_no("refund" + refundReqData.getOut_trade_no());
		//fix 修复隔日退款未结算金额不足问题，改为可用(已结算金额退款)
		refundReqData.setRefund_account("REFUND_SOURCE_RECHARGE_FUNDS");//可用余额退款
		refundReqData.setSign(Signature.getSign(refundReqData.toMap()));
		return tradeType.get("trade_type");
	}
	
	@Override
	public void notify(HttpServletRequest request)
			throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException, BusinessException {
		// TODO Auto-generated method stub
		InputStream stream = request.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(stream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        // 释放资源
        bufferedReader.close();
        inputStreamReader.close();
        stream.close();
        stream = null;
        String xmldata = buffer.toString();
        Map<String,Object> resobject = XMLParser.getMapFromXML(xmldata);
        boolean isSign = Signature.checkIsSignValidFromResponseString(xmldata);
        if (!isSign){
        	logger.error("订单验证不通过！");
        	throw new IOException("订单验证不通过！");
        	
        }
        ScanPayResData resdata = ScanPayResData.toObject(resobject);
        mWxPayMapper.inserWxNotifyInfo(resdata);
        
        if(SUCCESS.equals(resdata.getReturn_code()) && SUCCESS.equals(resdata.getResult_code())){
		    String out_trade_no = resdata.getOut_trade_no();
		    String cbUrl = getWxNotifyUrl(out_trade_no); //
		    
		    ////解决微信浏览器调起支付后未支付，换浏览器后重新调起二维码支付时，无法支付的问题
		    int rindex = out_trade_no.length()-4;
		    if ('R' == out_trade_no.charAt(rindex)) {
		    	out_trade_no = out_trade_no.substring(0, rindex);
		    }
		    //加密获取sign
			String timestamp = String.valueOf(System.currentTimeMillis());
			String salt = SpringAppConfing.get("encryption.salt");
			String sign = MD5.MD5Encode(out_trade_no + timestamp + salt);
			
			Map<String, String> param = new HashMap<String, String>();
			param.put("timestamp", timestamp);
			param.put("sign", sign);
			param.put("out_trade_no", out_trade_no);
			
			logger.debug("WxPay pay success notify start, orderNo:{}", out_trade_no);
			String result = new HttpRequest().sendPostJson(cbUrl, param);
			ApiResponse resp = ZcJsonKit.nonNullCaseMapper().fromJson(result, ApiResponse.class);
			if (resp == null || resp.getCode() != 0) {
				logger.error("WxPay pay success notify fail, orderNo:{}", out_trade_no);
				throw new BusinessException(ErrorCode.PAY_SUCCESS_NOTIFY_FAIL, "订单回调失败");
			}
			logger.debug("WxPay pay success notify done, orderNo:{}", out_trade_no);
        }
	}
	
	@Override
	public String getWxNotifyUrl(String outTradeNo) throws BusinessException{
		String cbURL = mWxPayMapper.selectCallBackUrl(outTradeNo);
		if(StringUtils.isBlank(cbURL)){
			logger.error("Sub system Call back can not be found:{[]}", outTradeNo);
			throw new BusinessException(ErrorCode.OUT_TRADE_NO_NOT_FOUND, "订单号查询失败");
		}

		return cbURL;
	}
	
	public GetOpenidResData getOpenId(String code) {
		String uri = Configure.GET_OPENID_API + "?appid=" + Configure.getAppid() + "&secret=" + Configure.getAppSecret() + "&code=" + code + "&grant_type=authorization_code";
		String resp = HttpClientUtil.get(uri);
		GetOpenidResData fromJson = ZcJsonKit.nonNullCaseMapper().fromJson(resp, GetOpenidResData.class);
		return fromJson;
	}

	@Override
	public List<Map<String, Object>> getPaySuccessOrder(String outTradeNo) {
		return mWxPayMapper.selectPaySuccessOrder(outTradeNo + "%");
	}

	@Override
	public int selectCountWxRefundByOutTradeNo(String outTradeNo) {
		return mWxPayMapper.selectCountWxRefundByOutTradeNo(outTradeNo+"%");
	}

	@Override
	public BigDecimal getWxSettleMoney(Date beginTime, Date endTime) {
		BigDecimal settleMoney = mWxPayMapper.selectWxSettleMoney(beginTime, endTime);
		
		return settleMoney == null ? BigDecimal.ZERO : settleMoney;
	}
	
	@Override
	public BigDecimal getWxSettleRefundMoney(Date beginTime, Date endTime) {
		BigDecimal settleMoney = mWxPayMapper.selectWxSettleRefundMoney(beginTime, endTime);
		
		return settleMoney == null ? BigDecimal.ZERO : settleMoney;
	}

	@Override
	public void checkWxRefundStatus() {
		List<String> outTradeNoList = mWxPayMapper.selectRefundStatus(IntbeePaymentConstant.REFUND_REQUEST);
		if (outTradeNoList == null) return;
		for (String outTradeNo : outTradeNoList) {
			RefundQueryReqData refundQueryReqData = new RefundQueryReqData();
			refundQueryReqData.setOut_trade_no(outTradeNo);
			try {
				refundQuery(refundQueryReqData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public List<List<String>> getBillContent(String deviceInfo,String billDate,String billType) {
		DownloadBillReqData downloadBillReqData = new DownloadBillReqData(deviceInfo, billDate, billType);
		final List<List<String>> items = Lists.newArrayList();
		try {
			//账单数据
			WXPay.doDownloadBillBusiness(downloadBillReqData,  getBillResultListener(items));
		} catch (Exception e) {
			logger.error("getBill fail", e);
		}
		return items;
	}

    private DownloadBillBusiness.ResultListener getBillResultListener(final List<List<String>> items) {
		return new DownloadBillBusiness.ResultListener() {
			@Override
			public void onFailByReturnCodeError(DownloadBillResData downloadBillResData) {
				logger.error("onFailByReturnCodeError");
			}
			@Override
			public void onFailByReturnCodeFail(DownloadBillResData downloadBillResData) {
				logger.error("onFailByReturnCodeFail");
			}
			@Override
			public void onDownloadBillFail(String response) {
				logger.error("onDownloadBillFail");
			}
			@Override
			public void onDownloadBillSuccess(String response) {
				logger.error("onDownloadBillSuccess");
				String[] lines = response.split("\n");
				for (int i=0; i<lines.length; i++) {
					String[] cells = lines[i].substring(0, lines[i].length() - 1).split(",");
					List<String> cellList = Lists.newArrayListWithCapacity(cells.length);
					for (String cell : cells) {
						if (i == 0 || i == lines.length-2) {
							cellList.add(cell);
						} else {
							cellList.add(cell.substring(1, cell.length()));
						}
					}
					items.add(cellList);
				}
			}
		
		};
	}
	
	public String getBillFilePath(String dateStr) {
		String saveWxBillFilePath = "download/bill/wxpay";
		File saveWxBillDir = new File(saveWxBillFilePath);
		if ( !saveWxBillDir.exists()) {
			saveWxBillDir.mkdirs(); 
			logger.debug("create saveWxBillDir :{}", saveWxBillDir.getAbsolutePath());
		}
		return saveWxBillFilePath + "/wxbill_" + dateStr + ".xls";
	}
	
	public void generateBillFile() {
		Date lastDate = DateTime.now().plusDays(-1).toDate();
		String dateStr = new SimpleDateFormat("yyyyMMdd").format(lastDate);
		List<List<String>> billContent = getBillContent("", dateStr, "ALL");
		if (billContent.isEmpty()) {
			logger.error("generateBillFile error, billContent is empty");
			return;
		}
		HSSFWorkbook wb = ExcelExportUtil.exportExcel("wxbill_" + dateStr, billContent);
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(getBillFilePath(dateStr));
			wb.write(fileOut);
			logger.debug("generateBillFile done");
		} catch (Exception e) {
			logger.error("generateBillFile error", e);
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (IOException e) {
					logger.error("generateBillFile error", e);
				}
			}
		}
	}
	
	private void convertDataToJsonFormat(final List<Map<String, String>> items, String response) {
		String[] line = response.split("\n");
		//第一行标题
		String[] title = line[0].substring(0, line[0].length() - 1).split(",");
		//详细数据
		for (int i=1; i<line.length -2; i++) {
			String[] item = line[i].substring(0, line[i].length() - 1).split(",");
			Map<String, String> itemMap = Maps.newHashMapWithExpectedSize(title.length);
			for (int j=0;j<title.length;j++) {
				String key = title[j];
				String val = item[j].substring(1, item[j].length());
				itemMap.put(key, val);
			}
			items.add(itemMap);
		}
		//倒数第二行标题
		String[] sumTitle = line[line.length-2].substring(0, line[line.length-2].length() - 1).split(",");
		//统计数据
		String[] sumValue = line[line.length-1].substring(0, line[line.length-1].length() - 1).split(",");
		Map<String, String> item = Maps.newHashMapWithExpectedSize(sumTitle.length);
		for (int i=0; i<sumTitle.length; i++) {
			String sv = sumValue[i];
			item.put(sumTitle[i], sv.substring(1, sv.length()));
		}
		items.add(item);
	}

}
