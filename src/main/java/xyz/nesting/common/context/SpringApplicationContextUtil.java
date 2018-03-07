/**
 * 
 */
package xyz.nesting.common.context;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSourceResolvable;

/**
 * 
 * 
 * @Description: 通过注入的方式获取spring的上下文,注意必须在在xml里添加
 * 
 *               <pre>
 *               bean = xyz.intbee.common.context.SpringApplicationContextUtil
 *               </pre>
 * 
 *               且不能使用延迟初始化
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年6月28日-下午5:12:12
 *
 *
 */
public class SpringApplicationContextUtil implements ApplicationContextAware {
	private static Logger logger = LoggerFactory.getLogger(SpringApplicationContextUtil.class);
	private static ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
		logger.info("初始化应用上下文 .");
	}

	public static ApplicationContext getCtx() {
		return ctx;
	}

	private static void checkCtx() {
		if (null == ctx) {
			throw new IllegalArgumentException("无法获取程序的上下文信息，ApplicationContext为空！");
		}
	}

	public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		checkCtx();
		return ctx.getMessage(code, args, defaultMessage, locale);
	}

	public static String getMessage(String code, Object[] args, Locale locale) {
		checkCtx();
		return ctx.getMessage(code, args, locale);
	}

	public static String getMessage(MessageSourceResolvable resolvable, Locale locale) {
		checkCtx();
		return ctx.getMessage(resolvable, locale);
	}
	
	public static <T> T getBean(Class<T> clazz) {
		checkCtx();
		return ctx.getBean(clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		checkCtx();
		return (T) ctx.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		checkCtx();
		return ctx.getBean(name, requiredType);
	}
}
