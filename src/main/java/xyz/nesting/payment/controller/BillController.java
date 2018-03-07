package xyz.nesting.payment.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xyz.nesting.common.io.json.ZcJsonKit;
import xyz.nesting.payment.constant.ErrorCode;
import xyz.nesting.payment.protocol.ApiResponse;
import xyz.nesting.payment.service.impl.AliPayServiceImpl;
import xyz.nesting.payment.service.impl.WxPayServiceImpl;

@RestController
@RequestMapping(BillController.BASE_MAPPER)
public class BillController extends AbstractController {
	protected static final String BASE_MAPPER = "/public/pay/bill";
	@Autowired
	private WxPayServiceImpl wxPayService;
	@Autowired
	private AliPayServiceImpl aliPayService;
	
	@RequestMapping(value = "/generate", method = { RequestMethod.GET })
	public ApiResponse billGenerate(String billDate, String billType) {
		wxPayService.generateBillFile();
		aliPayService.generateBillFile();
		return success("");
	}
	
	@RequestMapping(value = "/download/{type}", method = RequestMethod.GET)
	public void downloadExternalBill(HttpServletResponse response, @PathVariable String type, String date) {
		if (StringUtils.isBlank(date)) {
			jsonResponse(response, "date could not be null");
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			Date parse = simpleDateFormat.parse(date);
		} catch (ParseException e1) {
			jsonResponse(response, "date format : yyyyMMdd");
		}
		if ("wx".equals(type) && "ali".equals(type)) {
			jsonResponse(response, "type should be wx or ali");
		}
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			String billFilePath =  wxPayService.getBillFilePath(date);
			if ("ali".equals(type)) {
				billFilePath = aliPayService.getBillFilePath(date);
			}
			File file = new File(billFilePath);
			if (!file.exists()) {
				jsonResponse(response, "bill not exist");
			}
			FileInputStream is = new FileInputStream(file);
			// 设置response参数，可以打开下载页面
			response.reset();
			response.setContentType("application/vnd.ms-excel;charset=utf-8");
			String fileName = billFilePath.substring(billFilePath.lastIndexOf("/")+1);
			response.setHeader("Content-Disposition", "attachment;filename=\"" + new String(fileName.getBytes(), "iso-8859-1") + "\"");
			ServletOutputStream out = response.getOutputStream();
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		}  catch (Exception e) {
			logger.error("billDownload fail", e);
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				logger.error("billDownload fail", e);
			}
		}
	}

	private void jsonResponse(HttpServletResponse response, String reason) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType(OAuth.ContentType.JSON); 
		response.setStatus(200);
		
		PrintWriter writer;
		try {
			writer = response.getWriter();
			ApiResponse resp = ApiResponse.error(ErrorCode.FAILED, reason, null);
			writer.write(ZcJsonKit.nonNullCaseMapper().toJson(resp));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
