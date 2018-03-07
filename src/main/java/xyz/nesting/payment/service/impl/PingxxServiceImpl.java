package xyz.nesting.payment.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.InvalidRequestException;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Identification;
import com.pingplusplus.model.Refund;
import com.pingplusplus.model.Webhooks;
import com.tencent.common.Configure;

import xyz.nesting.common.exceptions.BusinessException;
import xyz.nesting.common.io.json.ZcJsonKit;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.model.pingxx.MgAuth;
import xyz.nesting.payment.model.pingxx.MgCreateCharge;
import xyz.nesting.payment.model.pingxx.MgCreateRefund;
import xyz.nesting.payment.model.pingxx.MgWebhookChargeSucceeded;
import xyz.nesting.payment.model.pingxx.MgWebhookRefundSucceeded;
import xyz.nesting.payment.protocol.ApiResponse;
import xyz.nesting.payment.protocol.pingxx.PaySuccessNotify;
import xyz.nesting.payment.protocol.pingxx.PingxxAuthReq;
import xyz.nesting.payment.protocol.pingxx.PingxxPayReq;
import xyz.nesting.payment.protocol.pingxx.PingxxRefundReq;
import xyz.nesting.payment.protocol.pingxx.RefundSuccessNotify;
import xyz.nesting.payment.service.PingxxService;
import xyz.nesting.payment.util.HttpRequest;

@Service
public class PingxxServiceImpl implements PingxxService {
	private Logger logger = LoggerFactory.getLogger(PingxxServiceImpl.class);

	@Value("${spring.profiles.active}")
	private String RUN_ENV;
	
	@Value("${pingxx.apiKey}")
	private String apiKey;
	@Value("${pingxx.appid}")
	private String appId;
	@Value("${pingxx.privateKeyFilePath}")
	private String privateKeyFilePath;
	@Value("${pingxx.publicKeyFilePath}")
	private String publicKeyFilePath;
	@Value("${encryption.salt}")
	private String salt;
	
	

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@PostConstruct
	public void init() {
		Pingpp.apiKey = apiKey;
		String path = Configure.class.getClassLoader().getResource("").getPath();
		Pingpp.privateKeyPath = path + privateKeyFilePath;
		publicKeyFilePath = path + publicKeyFilePath;
	}

	@Override
	public Charge createCharge(String channel, PingxxPayReq reqdata) {
		Map<String, Object> chargeMap = new HashMap<String, Object>();
		chargeMap.put("amount", reqdata.getAmount());// 订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
		chargeMap.put("currency", "cny");
		chargeMap.put("subject", reqdata.getSubject());
		chargeMap.put("body", reqdata.getBody());
		chargeMap.put("order_no", reqdata.getOrderNo());// 推荐使用 8-20 位，要求数字或字母，不允许其他字符
		chargeMap.put("channel", channel);// 支付使用的第三方支付渠道取值，请参考：https://www.pingxx.com/api#api-c-new
		chargeMap.put("client_ip", reqdata.getClientIP()); // 发起支付请求客户端的 IP 地址，格式为 IPV4，如: 127.0.0.1
		Map<String, String> app = new HashMap<String, String>();
		app.put("id", appId);
		chargeMap.put("app", app);

		try {
			// 发起交易请求
			Charge charge = Charge.create(chargeMap);
			// 传到客户端请先转成字符串 .toString(), 调该方法，会自动转成正确的 JSON 字符串
			logger.debug("createCharge done:{}", charge.toString());
			mongoTemplate.insert(MgCreateCharge.build(reqdata, charge));
			return charge;
		} catch (Exception e) {
			logger.error("createCharge fail", e);
			BusinessException.error(ErrorCode.FAILED, "请求支付失败");
		}
		return null;
	}
	
	@Override
	public Refund refund(PingxxRefundReq reqdata) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("description", reqdata.getDescription());
        params.put("amount", reqdata.getAmount());// 退款的金额, 单位为对应币种的最小货币单位，例如：人民币为分（如退款金额为 1 元，此处请填 100）。必须小于等于可退款金额，默认为全额退款

        Query query = new Query().addCriteria(Criteria.where("charge.id").is(reqdata.getChargeId()).and("charge.orderNo").is(reqdata.getOrderNo()));
        MgWebhookChargeSucceeded mcs = mongoTemplate.findOne(query, MgWebhookChargeSucceeded.class);
        
        if (null == mcs) {
        		BusinessException.error(ErrorCode.FAILED, "无法获取支付成功信息,请求退款失败");
        }
        
        String chargeId = mcs.getCharge().getId();
        
        try {
        		Refund refund = Refund.create(chargeId, params);
        		mongoTemplate.insert(MgCreateRefund.build(reqdata, refund));
        		return refund;
        } catch (Exception e) {
        		logger.error("createCharge fail", e);
			BusinessException.error(ErrorCode.FAILED, "请求退款失败");
        }
        return null;
	}

	@Override
	public boolean verifyData(String dataString, String signatureString) {
		try {
			byte[] signatureBytes = Base64.decodeBase64(signatureString);
			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(getPubKey());
			signature.update(dataString.getBytes("UTF-8"));
			return signature.verify(signatureBytes);
		} catch (Exception e) {
			logger.error("verifyDate fail", e);
		}
		return false;
	}

	private PublicKey getPubKey() throws Exception {
		String pubKeyString = getStringFromFile(publicKeyFilePath);
		pubKeyString = pubKeyString.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
		byte[] keyBytes = Base64.decodeBase64(pubKeyString);

		// generate public key
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(spec);
		return publicKey;
	}

	private String getStringFromFile(String filePath) throws Exception {
		FileInputStream in = new FileInputStream(filePath);
		InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
		BufferedReader bf = new BufferedReader(inReader);
		StringBuilder sb = new StringBuilder();
		String line;
		do {
			line = bf.readLine();
			if (line != null) {
				if (sb.length() != 0) {
					sb.append("\n");
				}
				sb.append(line);
			}
		} while (line != null);

		return sb.toString();
	}
	
	@Override
	public void processEvent(String dataString) throws Exception {
		Event event = Webhooks.eventParse(dataString);
		Object obj = event.getData().getObject();
		String type = event.getType();
		
		if ("charge.succeeded".equals(type)) {
			Charge charge = (Charge) obj;
            logger.debug("processEvent charge:{}", charge.toString());
            //处理支付成功事件
            processChargeSucceeded(charge);
		} else if ("refund.succeeded".equals(type)) {
			Refund refund = (Refund) obj;
	    		logger.debug("processEvent refund:{}", refund.toString());
	    		//处理退款成功事件
	    		processRefundSucceeded(refund);
		} else {
			logger.debug("processEvent unknown:{}", dataString);
		}
		
	}
	
	private void processChargeSucceeded(Charge charge) throws Exception {
		mongoTemplate.insert(MgWebhookChargeSucceeded.build(charge));
		
		Query query = new Query().addCriteria(Criteria
				.where("charge._id").is(charge.getId())
				.and("charge.orderNo").is(charge.getOrderNo())
				.and("charge.amount").is(charge.getAmount())
				.and("charge.channel").is(charge.getChannel()));
		MgCreateCharge mcc = mongoTemplate.findOne(query, MgCreateCharge.class);
		
		if (null == mcc) {
			BusinessException.error(ErrorCode.FAILED, "无法获取付款信息");
		}
		
		String callBackUrl = mcc.getPayReq().getCallBackUrl();
		//加密获取sign
		PaySuccessNotify notify = PaySuccessNotify.build(charge.getOrderNo(), charge.getId(), charge.getAmount(), charge.getChannel(), salt);
		logger.debug("processChargeSucceeded callBackUrl:{}, notify:{}", callBackUrl, notify);
		
		String result = new HttpRequest().sendPostObj(callBackUrl, notify);
		ApiResponse resp = ZcJsonKit.nonNullCaseMapper().fromJson(result, ApiResponse.class);
		if (resp == null || resp.getCode() != 0) {
			logger.error("processChargeSucceeded notify fail, orderNo:{}", charge.getOrderNo());
			throw new BusinessException(ErrorCode.PAY_SUCCESS_NOTIFY_FAIL, "订单支付成功回调失败");
		}
		logger.debug("processChargeSucceeded notify done, orderNo:{}", charge.getOrderNo());
	}

	private void processRefundSucceeded(Refund refund) throws Exception {
		mongoTemplate.insert(MgWebhookRefundSucceeded.build(refund));
		
		Query query = new Query().addCriteria(Criteria
				.where("refund._id").is(refund.getId())
				.and("refund.orderNo").is(refund.getOrderNo())
				.and("refund.amount").is(refund.getAmount()));
		MgCreateRefund mr = mongoTemplate.findOne(query, MgCreateRefund.class);
		if (null == mr) {
			BusinessException.error(ErrorCode.FAILED, "无法获取请求退款信息");
		}
		String callBackUrl = mr.getRefundReq().getCallBackUrl();
		//加密获取sign
		RefundSuccessNotify notifySign = RefundSuccessNotify.build(refund.getOrderNo(), refund.getId(), refund.getAmount(), salt);
		logger.debug("processRefundSucceeded callBackUrl:{}, notifySign:{}", callBackUrl, notifySign);
		
		String result = new HttpRequest().sendPostObj(callBackUrl, notifySign);
		ApiResponse resp = ZcJsonKit.nonNullCaseMapper().fromJson(result, ApiResponse.class);
		if (resp == null || resp.getCode() != 0) {
			logger.error("processRefundSucceeded notify fail, orderNo:{}", refund.getOrderNo());
			throw new BusinessException(ErrorCode.REFUND_SUCCESS_NOTIFY_FAIL, "订单退款成功回调失败");
		}
		logger.debug("processRefundSucceeded notify done, orderNo:{}", refund.getOrderNo());
	}

	@Override
	public Identification auth(PingxxAuthReq reqdata) {
		try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("app", appId);
            params.put("type", "bank_card"); // 身份证信息或者银行卡信息串，取值范围: "id_card"（身份证信息串）；"bank_card"（银行卡信息串）。
            Map<String, String> data = new HashMap<String, String>();
            data.put("id_name", reqdata.getIdName());
            data.put("id_number", reqdata.getIdNumber());
            data.put("card_number", reqdata.getCardNumber());
            params.put("data", data);
            
            Identification result = Identification.identify(params);
            logger.debug("auth done:{}", result.toString());
            mongoTemplate.insert(MgAuth.build(reqdata, result));
            if (result.getResultCode() == 0) {
            		return result;
            } else {
            		logger.warn("auth fail resultCode:{} message:{}", result.getResultCode(), result.getMessage());
                BusinessException.error(ErrorCode.FAILED, result.getMessage());
            }
        } catch (BusinessException e) {
        		throw e;
        } catch (Exception e) {
        		if (e instanceof InvalidRequestException) {
        			InvalidRequestException ire = (InvalidRequestException) e;
        			if (ire.getMessage().contains("livemode") && ("test".equals(RUN_ENV)||"dev".equals(RUN_ENV))) {
        				logger.warn("fake auth pass for test, req:{}", reqdata);
        				return null;
        			}
        		}
        		logger.error("auth fail", e);
			BusinessException.error(ErrorCode.FAILED, "认证失败");
        }
		return null;
	}


}
