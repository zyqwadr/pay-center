package xyz.nesting.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import xyz.nesting.common.io.json.JackObjectMapper;

/**
 * 定义MVC配置
 * @author zhengyue
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	
	/**
	 * 配置输入输出json格式,字段下划线格式
	 * @return
	 */
	@Bean
	public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
	    final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	    converter.setObjectMapper(new JackObjectMapper());
	    return converter;
	}

}
