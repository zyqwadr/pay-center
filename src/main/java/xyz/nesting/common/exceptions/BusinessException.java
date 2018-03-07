/**
 * 
 */
package xyz.nesting.common.exceptions;

/**
 * 
 * @Description: 抛出业务异常Controller统一处理
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年5月24日-下午7:33:15
 *
 */
public class BusinessException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6306718938332210530L;
	/**
	 * 状态码。0为正确；非0为异常
	 */
	private int errorCode;
	private String errorMessage;
	private String errorStack;

	/**
	 * @param code
	 */
	public BusinessException(int errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the {@link #errorCode}
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return the {@link #errorStack}
	 */
	public String getErrorStack() {
		return errorStack;
	}

	/**
	 * @param errorStack
	 *            the {@link #errorStack} to set
	 */
	public void setErrorStack(String errorStack) {
		this.errorStack = errorStack;
	}

	/**
	 * 抛出业务异常Controller统一处理
	 * 
	 * @param code
	 * @param message
	 */
	public static void error(int errorCode, String errorMessage) {
		throw new BusinessException(errorCode, errorMessage);
	}

	/**
	 * 
	 * @param errorCode
	 * @param errorMessage
	 * @param errorStack
	 */
	public static void error(int errorCode, String errorMessage, String errorStack) {
		BusinessException instance = new BusinessException(errorCode, errorMessage);
		instance.setErrorStack(errorStack);
		throw instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return String.format("ApiBusinessError:[code:%d,message:%s,errorStack:%s]", errorCode, errorMessage,
				errorStack);
	}
}
