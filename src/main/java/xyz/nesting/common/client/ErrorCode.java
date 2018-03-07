/**
 * 
 */
package xyz.nesting.common.client;

/**
 * 
 * @Description: TODO(添加描述)
 * @Author zhengyue
 * @Version: 0.0.1
 * @CreateAt 2017年6月6日-上午10:24:20
 *
 */
public interface ErrorCode {
	int SUCCESS = 0;
	int FAILED = 2;


	int ACCESS_TOKEN_ERROR = 3001;

	/**
	 * 支付时，支付中心请求失败
	 */
	int PAY_CENTER_ERROR = 4507;
}
