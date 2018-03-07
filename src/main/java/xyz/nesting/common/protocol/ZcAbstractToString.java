/**
 * 
 */
package xyz.nesting.common.protocol;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * 
 * @Description: 统一重写ToString方法
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年5月22日-上午10:26:55
 *
 */
@SuppressWarnings("serial")
public abstract class ZcAbstractToString implements Serializable{


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
