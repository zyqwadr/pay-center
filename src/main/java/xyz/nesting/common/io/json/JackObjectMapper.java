/**
 * 
 */
package xyz.nesting.common.io.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 * 
 * @Description: 自定义JSON输出,定义非驼峰命名字段自动映射至驼峰命名字段，输出统一为非驼峰形式
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年5月22日-下午12:31:46
 *
 */
public class JackObjectMapper extends ObjectMapper {
	public JackObjectMapper() {
		super();
		// 让jackson支持jaxb注解的配置
		this.registerModule(new JaxbAnnotationModule());
		// 为null的属性值不映射
		this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 驼峰属性处理
		this.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		// 排序
		// this.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
		// 有属性不能映射的时候不报错
		this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
}
