/**
 * 
 */
package xyz.nesting.payment.controller;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tencent.protocol.pay_protocol.ScanPayReqData;

import xyz.nesting.payment.protocol.ApiResponse;


/**
 * 
 * @Description: TODO(添加描述)
 * @Author qizai
 * @Version: 0.0.1
 * @CreateAt 2016年5月27日-下午7:39:21
 *
 */
@RestController
@RequestMapping("/public/debug")
@Validated
public class DebugController extends AbstractController{
	
	@RequestMapping(value = "/{id}", method = { RequestMethod.POST })
	public ApiResponse wxpay(@Valid @RequestBody ScanPayReqData reqdata) {

		return success(reqdata);
	}
}
