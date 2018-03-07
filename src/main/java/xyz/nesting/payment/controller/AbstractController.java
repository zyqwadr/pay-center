/**
 * 
 */
package xyz.nesting.payment.controller;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import xyz.nesting.common.exceptions.BusinessException;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.protocol.ApiResponse;

/**
 * 
 * @Description: API基础Controller
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年5月20日-下午9:11:17
 *
 */
public abstract class AbstractController {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${encryption.salt}")
	protected String encryptionSalt;
	
	@Autowired
	protected HttpServletRequest request;
	
	@Autowired
	protected HttpServletResponse response;
	
	/**
	 * 构建正确的响应，默认状态码为{@link ErrorCode#SUEECSS}
	 * 
	 * @param result
	 *            响应数据
	 * @return
	 */
	protected ApiResponse success(Object result) {
		ApiResponse resp = new ApiResponse();
		resp.setCode(ErrorCode.SUEECSS);
		resp.setMessage("success");
		resp.setStackTrace("");
		resp.setResult(result);
		return resp;
	}

	protected ApiResponse success(int code, String message, String stackTrace, Object result) {
		ApiResponse resp = new ApiResponse();
		resp.setCode(code);
		resp.setMessage(message);
		resp.setStackTrace(stackTrace);
		resp.setResult(result);
		return resp;
	}

	protected ApiResponse error(int code, String message, String stackTrace) {
		ApiResponse resp = new ApiResponse();
		resp.setCode(code);
		resp.setMessage(message);
		resp.setStackTrace(stackTrace);
		resp.setResult(null);
		return resp;
	}
	
	@ExceptionHandler({ BusinessException.class })
	@ResponseBody
	public ApiResponse apiBusinessErrorHandler(BusinessException businessException) {
		logger.error("API请求业务出错:[{}]", businessException.toString());
		return error(businessException.getErrorCode(), businessException.getErrorMessage(), "");
	}
	
	/**
	 * 捕获参数验证异常
	 * @param ce
	 * @return
	 */
	@ExceptionHandler({ ConstraintViolationException.class, MethodArgumentNotValidException.class, BindException.class })
	@ResponseBody
	public ApiResponse apiParamExceptionHandler(Exception ce) {
		String message = "";
		try {
			if (ce instanceof MethodArgumentNotValidException) {
				message = ((MethodArgumentNotValidException)ce).getBindingResult().getAllErrors().get(0).getDefaultMessage();
				
			} else if (ce instanceof BindException) {
				message = ((BindException)ce).getAllErrors().get(0).getDefaultMessage();
				
			} else if (ce instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException)ce;
				Set<ConstraintViolation<?>> cs = cve.getConstraintViolations();
				if (cs.iterator().hasNext()) {
					message = cs.iterator().next().getMessage();
				}
			}
		}catch (Exception e) {
			message = "apiParamExceptionHandler error";
		}
		logger.warn("API请求参数错误:[{}]", message);
		return error(ErrorCode.FAILED, message, "");
	}

}
