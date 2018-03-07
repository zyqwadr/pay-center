package xyz.nesting.payment.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import xyz.nesting.common.io.json.ZcJsonKit;

/**
 * User: rizenguo Date: 2014/10/29 Time: 14:36
 */
public class HttpRequest{

	public interface ResultListener {

		public void onConnectionPoolTimeoutError();

	}

	private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

	// 表示请求器是否已经做了初始化工作
	private boolean hasInit = false;

	// 连接超时时间，默认10秒
	private int socketTimeout = 10000;

	// 传输超时时间，默认30秒
	private int connectTimeout = 30000;

	// 请求器的配置
	private RequestConfig requestConfig;

	// HTTP请求器
	private CloseableHttpClient httpClient;

	public HttpRequest() {
		init();
	}

	private void init() {
		httpClient = HttpClients.custom().build();
		// 根据默认超时限制初始化requestConfig
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
		hasInit = true;
	}

	/**
	 * 通过Https往API post xml数据
	 *
	 * @param url
	 *            API地址
	 * @param xmlObj
	 *            要提交的XML数据对象
	 * @return API回包的实际数据
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */

	public String sendPost(String url, Object xmlObj) throws IOException {

		if (!hasInit) {
			init();
		}

		String result = null;

		HttpPost httpPost = new HttpPost(url);

		// 解决XStream对出现双下划线的bug
		XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));

		// 将要提交给API的数据对象转换成XML格式数据Post给API
		String postDataXML = xStreamForRequestPostData.toXML(xmlObj);

		logger.info("API，POST过去的数据是：");
		logger.info(postDataXML);

		// 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
		StringEntity postEntity = new StringEntity(postDataXML, "UTF-8");
		httpPost.addHeader("Content-Type", "text/xml");
		httpPost.setEntity(postEntity);

		// 设置请求器的配置
		httpPost.setConfig(requestConfig);

		logger.info("executing request" + httpPost.getRequestLine());

		try {
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity, "UTF-8");

		} catch (ConnectionPoolTimeoutException e) {
			logger.error("http get throw ConnectionPoolTimeoutException(wait time out)");

		} catch (ConnectTimeoutException e) {
			logger.error("http get throw ConnectTimeoutException");

		} catch (SocketTimeoutException e) {
			logger.error("http get throw SocketTimeoutException");

		} catch (Exception e) {
			logger.error("http get throw Exception");

		} finally {
			httpPost.abort();
		}

		return result;
	}

	/**
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public String sendPostForm(String url, Map<String, String> param) throws IOException {

		if (!hasInit) {
			init();
		}

		String result = null;
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> entry : param.entrySet()) {
			formParams.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
			builder.append(entry.getKey()).append("=").append(String.valueOf(entry.getValue())).append("&");
		}
		logger.info("API，POST过去的数据是："+builder.toString());
		// 设置请求器的配置
		httpPost.setConfig(requestConfig);
		logger.info("executing request" + httpPost.getRequestLine());
		try {
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
			httpPost.setEntity(uefEntity); 
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
			logger.info("API，POST返回的数据是：" + result);
		} catch (ConnectionPoolTimeoutException e) {
			logger.error("http get throw ConnectionPoolTimeoutException(wait time out)");

		} catch (ConnectTimeoutException e) {
			logger.error("http get throw ConnectTimeoutException");

		} catch (SocketTimeoutException e) {
			logger.error("http get throw SocketTimeoutException");

		} catch (Exception e) {
			logger.error("http get throw Exception");

		} finally {
			httpPost.abort();
		}

		return result;
	}
	
	/**
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public String sendPostJson(String url, Map<String, String> param) throws IOException {

		if (!hasInit) {
			init();
		}
		
		String result = null;

		HttpPost httpPost = new HttpPost(url);
		
	    String paramJson = ZcJsonKit.nonNullCaseMapper().toJson(param);
		
		
	    logger.info("API，POST过去的数据是：");
		logger.info(paramJson);
		
		// 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
		StringEntity postEntity = new StringEntity(paramJson, "UTF-8");
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.setEntity(postEntity);

		// 设置请求器的配置
		httpPost.setConfig(requestConfig);

		logger.info("executing request" + httpPost.getRequestLine());

		try {
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity, "UTF-8");

		} catch (ConnectionPoolTimeoutException e) {
			logger.error("http get throw ConnectionPoolTimeoutException(wait time out)");

		} catch (ConnectTimeoutException e) {
			logger.error("http get throw ConnectTimeoutException");

		} catch (SocketTimeoutException e) {
			logger.error("http get throw SocketTimeoutException");

		} catch (Exception e) {
			logger.error("http get throw Exception");

		} finally {
			httpPost.abort();
		}

		return result;
	}
	
	public String sendPostObj(String url, Object obj) throws IOException {

		if (!hasInit) {
			init();
		}
		
		String result = null;

		HttpPost httpPost = new HttpPost(url);
		
	    String paramJson = ZcJsonKit.nonNullCaseMapper().toJson(obj);
		
		
	    logger.info("API，POST过去的数据是：");
		logger.info(paramJson);
		
		// 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
		StringEntity postEntity = new StringEntity(paramJson, "UTF-8");
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.setEntity(postEntity);

		// 设置请求器的配置
		httpPost.setConfig(requestConfig);

		logger.info("executing request" + httpPost.getRequestLine());

		try {
			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity, "UTF-8");

		} catch (ConnectionPoolTimeoutException e) {
			logger.error("http get throw ConnectionPoolTimeoutException(wait time out)");

		} catch (ConnectTimeoutException e) {
			logger.error("http get throw ConnectTimeoutException");

		} catch (SocketTimeoutException e) {
			logger.error("http get throw SocketTimeoutException");

		} catch (Exception e) {
			logger.error("http get throw Exception");

		} finally {
			httpPost.abort();
		}

		return result;
	}

	public String doGet(String url) throws IOException {
		if (!hasInit) {
			init();
		}
		String result = null;

		HttpGet httpGet = new HttpGet(url);
		// 设置请求器的配置
		httpGet.setConfig(requestConfig);
		logger.info("executing request" + httpGet.getRequestLine());
		try {
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "UTF-8");
		} catch (ConnectionPoolTimeoutException e) {
			logger.error("http get throw ConnectionPoolTimeoutException(wait time out)");
		} catch (ConnectTimeoutException e) {
			logger.error("http get throw ConnectTimeoutException");
		} catch (SocketTimeoutException e) {
			logger.error("http get throw SocketTimeoutException");
		} catch (Exception e) {
			logger.error("http get throw Exception");
		} finally {
			httpGet.abort();
		}

		return result;
	}

	/**
	 * 设置连接超时时间
	 *
	 * @param socketTimeout
	 *            连接时长，默认10秒
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		resetRequestConfig();
	}

	/**
	 * 设置传输超时时间
	 *
	 * @param connectTimeout
	 *            传输时长，默认30秒
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		resetRequestConfig();
	}

	private void resetRequestConfig() {
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
	}

	/**
	 * 允许商户自己做更高级更复杂的请求器配置
	 *
	 * @param requestConfig
	 *            设置HttpsRequest的请求器配置
	 */
	public void setRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
	}
}
