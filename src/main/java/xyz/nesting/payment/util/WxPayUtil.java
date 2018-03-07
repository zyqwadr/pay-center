package xyz.nesting.payment.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class WxPayUtil {
	public static void main(String[] args) {
		HttpRequest req = new HttpRequest();
		try {
			String xmlString = req
					.doGet(getUrl("2016010521001004590064132461", null, null, 1));
			System.out.println(xmlString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static String getUrl(String out_trade_no,String body,String attach,int total_fee){
		Map<String, String> sParaTemp = new HashMap<String, String>();
		if (null != out_trade_no) {
			sParaTemp.put("out_trade_no", out_trade_no);
		}
		if (null != body) {
			sParaTemp.put("body", body);
		}
		if (null != attach) {
			sParaTemp.put("attach", attach);
		}
		sParaTemp.put("total_fee", DoubleHelper.formatNumber(total_fee, 2));
	        List<String> keys = new ArrayList<String>(sParaTemp.keySet());
	        StringBuffer sbUrl = new StringBuffer();
	        sbUrl.append("http://localhost:8080/public/wxpay/");
	        System.out.println("=============");
	        for (int i = 0; i < keys.size(); i++) {
	            String name = (String) keys.get(i);
	            String value = (String) sParaTemp.get(name);
	            sbUrl.append(name);
	            sbUrl.append("=");
	            sbUrl.append(value);
	            sbUrl.append("&");
	            System.out.println(name + "="+ value);
	        }
	    sbUrl.delete(sbUrl.length() -1, sbUrl.length());
		return sbUrl.toString();
	}
}
