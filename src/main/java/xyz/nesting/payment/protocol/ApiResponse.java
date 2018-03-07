package xyz.nesting.payment.protocol;

import org.apache.commons.lang3.builder.ToStringBuilder;

import xyz.nesting.common.io.json.ZcJsonKit;

public class ApiResponse {
	
	/**
	 * 状态码。0为正确；非0为异常
	 */
	private int code;
	/**
	 * 空为正确；非空为错误原因
	 */
	private String message;
	/**
	 * 空为正确；非空为错误栈
	 */
	private String stackTrace;
	/**
	 * 响应结果集
	 */
	private Object result;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	public String toJson() {
		return ZcJsonKit.nonNullMapper().toJson(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * @param failed
	 * @param reason
	 * @param object
	 * @return
	 */
	public static ApiResponse error(int code, String reason, Object object) {
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setCode(code);
		apiResponse.setMessage(reason);
		apiResponse.setResult(object);
		return apiResponse;
	}
}
