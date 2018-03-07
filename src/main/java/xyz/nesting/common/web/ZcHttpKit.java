package xyz.nesting.common.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 
 * 
 * @Description: Http链接工具类
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年5月17日-下午5:27:44
 *
 */
public class ZcHttpKit {
	private static Logger log = LoggerFactory.getLogger(ZcHttpKit.class);

	
	public static final String DEFAULT_ENCODE = "UTF-8";

	// 表示请求器是否已经做了初始化工作
	private AtomicBoolean hasInit = new AtomicBoolean(false);

	// 连接超时时间，默认100秒
	private int socketTimeout = 100000;

	// 传输超时时间，默认300秒
	private int connectTimeout = 300000;

	// 请求器的配置
	private RequestConfig requestConfig;

	// HTTP请求器
	private CloseableHttpClient httpClient;

	private ZcHttpKit() {
		init();
	}

	private static ZcHttpKit instance = null;

	public static ZcHttpKit getInstance() {
		synchronized (ZcHttpKit.class) {
			if (null != instance) {
				return instance;
			}
			instance = new ZcHttpKit();
			return instance;
		}
	}

	private void init() {
		httpClient = HttpClients.custom().build();
		// 根据默认超时限制初始化requestConfig
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout)
				.build();
		hasInit.set(true);
	}

	/**
	 * 执行POST请求返回String类型数据，默认使用UTF-8编码
	 *
	 * @param url
	 *            API地址
	 * @param jsonData
	 *            要提交的数据
	 * @return API回包的实际数据
	 */

	public String doPost(String url, String jsonData) {
		return post(url, jsonData);
	}

	public String post(String url, String jsonData) {
		HttpPost httpPost = new HttpPost(url);
		log.debug("HTTP [POST] Data：");
		log.debug(jsonData);
		if (null != jsonData) {
			StringEntity postEntity = new StringEntity(jsonData, DEFAULT_ENCODE);
			httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.setEntity(postEntity);
		}
		String result = _do(httpPost);
		return result;
	}

	public String put(String url, String jsonData) {
		HttpPut httpPost = new HttpPut(url);
		log.debug("HTTP [PUT] Data：");
		log.debug(jsonData);
		if (null != jsonData) {
			StringEntity postEntity = new StringEntity(jsonData, DEFAULT_ENCODE);
			httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.setEntity(postEntity);
		}
		String result = _do(httpPost);
		return result;
	}

	public String delete(String url) {
		return _do(new HttpDelete(url));
	}

	public String get(String url) {
		return _do(new HttpGet(url));
	}

	public int get(String url, String destFileName) {
		return _do(new HttpGet(url), destFileName);
	}

	/**
	 * 执行Get请求返回String类型数据，默认使用UTF-8编码
	 * 
	 * @param url
	 * @return
	 */
	public String doGet(String url) {
		return _do(new HttpGet(url));
	}

	/**
	 * 执行请求返回String类型数据，默认使用UTF-8编码
	 * 
	 * @param request
	 * @return
	 */
	public String _do(HttpRequestBase request) {

		if (!hasInit.get()) {
			init();
		}
		String result = null;
		// 设置请求器的配置
		request.setConfig(requestConfig);
		log.debug("HTTP URI :{}", request.getRequestLine());
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
			try {
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, DEFAULT_ENCODE);
			} catch (Exception e) {
				log.debug(e.getMessage());
				log.error("http get throw Exception", e);
			} finally {
				if (null != response) {
					response.close();
				}
			}

		} catch (ConnectionPoolTimeoutException e) {
			log.debug(e.getMessage());
			log.error("http ConnectionPoolTimeoutException(wait time out)");
		} catch (ConnectTimeoutException e) {
			log.debug(e.getMessage());
			log.error("http ConnectTimeoutException");
		} catch (SocketTimeoutException e) {
			log.debug(e.getMessage());
			log.error("http SocketTimeoutException");
		} catch (Exception e) {
			log.debug(e.getMessage());
			log.error("http Exception");
		}
		log.debug("HTTP Response Data ：{}", result);
		return result;
	}

	/**
	 * 执行请求返回String类型数据，默认使用UTF-8编码
	 *
	 * @param request
	 * @param destFileName
	 * @return
	 */
	public int _do(HttpRequestBase request, String destFileName) {

		if (!hasInit.get()) {
			init();
		}

		int stateCode = 0;
		// 设置请求器的配置
		request.setConfig(requestConfig);
		log.debug("HTTP URI :{}", request.getRequestLine());
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request);
			stateCode = response.getStatusLine().getStatusCode();
			try {
				HttpEntity entity = response.getEntity();

				InputStream in = entity.getContent();
				File file = new File(destFileName);

				FileOutputStream fout = new FileOutputStream(file);

				try {

					int l = -1;
					byte[] tmp = new byte[1024];
					while ((l = in.read(tmp)) != -1) {
						fout.write(tmp, 0, l);
						// 注意这里如果用OutputStream.write(buff)的话，图片会失真
					}
					fout.flush();
					fout.close();
				}catch (Exception e){
					log.debug(e.getMessage());
					log.error("Write File throw Exception", e);
				} finally {
					if (null != in){
						in.close();
					}
				}

			} catch (Exception e) {
				log.debug(e.getMessage());
				log.error("http get throw Exception", e);
			} finally {
				if (null != response) {
					response.close();
				}
			}

		} catch (ConnectionPoolTimeoutException e) {
			log.debug(e.getMessage());
			log.error("http ConnectionPoolTimeoutException(wait time out)");
		} catch (ConnectTimeoutException e) {
			log.debug(e.getMessage());
			log.error("http ConnectTimeoutException");
		} catch (SocketTimeoutException e) {
			log.debug(e.getMessage());
			log.error("http SocketTimeoutException");
		} catch (Exception e) {
			log.debug(e.getMessage());
			log.error("http Exception");
		}

		log.debug("HTTP Response Status Code ：{}", stateCode);

		return stateCode;
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
		requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout)
				.build();
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
