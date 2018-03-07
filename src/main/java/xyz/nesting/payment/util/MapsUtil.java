/**
 * 
 */
package xyz.nesting.payment.util;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @Description: TODO(添加描述)
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年5月28日-下午6:05:50
 *
 */
public class MapsUtil {
	/**
	 * @return the {@link #offset}
	 */
	public static int getOffset(Map<String, Object> requestMap) {
		return getInt(requestMap, "offset", 0);
	}

	/**
	 * @return the {@link #limit}
	 */
	public static int getLimit(Map<String, Object> requestMap) {
		return getInt(requestMap, "limit", 5);
	}

	/**
	 * @return the {@link #order}
	 */
	public static String getOrder(Map<String, Object> requestMap) {
		return getString(requestMap, "order");
	}

	/**
	 * @return the {@link #sortby}
	 */
	public static String getSortby(Map<String, Object> requestMap) {
		return getString(requestMap, "sortby");
	}

	/**
	 * 
	 * @param key
	 * @return ""
	 */
	@JsonIgnore
	public static String getString(Map<String, Object> requestMap, String key) {
		Object value = requestMap.get(key);
		return null == value ? "" : value.toString();
	}

	/**
	 * 
	 * @param key
	 * @return 默认0
	 */
	@JsonIgnore
	public static int getInt(Map<String, Object> requestMap, String key) {
		return getInt(requestMap, key, 0);
	}

	@JsonIgnore
	public static int getInt(Map<String, Object> requestMap, String key, int defaultValue) {
		return NumberUtils.toInt(getString(requestMap, key), defaultValue);
	}

	/**
	 * 检查参数是否存在，不存在则设置为默认值
	 * 
	 * @param requestMap
	 * @param key
	 * @param defaultValue
	 */
	@JsonIgnore
	public static void checkInt(Map<String, Object> requestMap, String key, int defaultValue) {
		requestMap.put(key, getInt(requestMap, key, defaultValue));
	}

	/**
	 * 检查参数是否存在，不存在则设置为默认值
	 * 
	 * @param requestMap
	 * @param key
	 * @param defaultValue
	 */
	public static void checkString(Map<String, Object> requestMap, String key, String defaultValue) {
		String val = getString(requestMap, key);
		requestMap.put(key, "".equals(val) ? defaultValue : val);
	}

	/**
	 * 检查参数是否存在，不存在则设置为默认值
	 * 
	 * @param requestMap
	 * @param key
	 * @param defaultValue
	 */
	public static void checkDouble(Map<String, Object> requestMap, String key, double defaultValue) {
		requestMap.put(key, getDouble(requestMap, key, defaultValue));
	}
	/**
	 * 
	 * @param key
	 * @return 非数字返回-1
	 */
	@JsonIgnore
	public static long getLong(Map<String, Object> requestMap, String key) {
		return getLong(requestMap, key, -1);
	}

	@JsonIgnore
	public static long getLong(Map<String, Object> requestMap, String key, long defaultValue) {
		return NumberUtils.toLong(getString(requestMap, key), defaultValue);
	}

	/**
	 * 
	 * @param key
	 * @return 非数字返回-1
	 */
	@JsonIgnore
	public static double getDouble(Map<String, Object> requestMap, String key) {
		return getDouble(requestMap, key, -1);
	}

	@JsonIgnore
	public static double getDouble(Map<String, Object> requestMap, String key, double defaultValue) {
		return NumberUtils.toDouble(getString(requestMap, key), defaultValue);
	}
}
