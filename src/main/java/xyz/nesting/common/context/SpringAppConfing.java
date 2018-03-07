/**
 * 
 */
package xyz.nesting.common.context;

import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.NumberUtils;

/**
 * 
 * @Description: 配置文件读取工具类，beanid=appConfigs
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年6月7日-下午5:35:35
 *
 */
public final class SpringAppConfing {
	private final static SpringAppConfing instance = new SpringAppConfing();
	private Properties properties;

	/**
	 * 
	 * @param popKey
	 *            配置key
	 * @return 默认返回""
	 */
	public static String get(String popKey) {
		return get(popKey, "");
	}

	public static String get(String popKey, String def) {
		if (null == instance.properties) {
			instance.properties = SpringApplicationContextUtil.getBean("appConfigs", Properties.class);
		}
		if (null == instance.properties) {
			return "";
		}
		return instance.properties.getProperty(popKey, def);
	}

	public static boolean getBool(String popKey) {
		return BooleanUtils.toBoolean(get(popKey));
	}

}
