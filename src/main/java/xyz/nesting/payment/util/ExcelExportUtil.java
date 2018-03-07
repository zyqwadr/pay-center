package xyz.nesting.payment.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class ExcelExportUtil {

	public static HSSFWorkbook exportExcel(String tableName, List<List<String>> list) {
		// 第一步，创建一个webbook，对应一个Excel文件
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet(tableName);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth(15);
		// 把字体应用到当前的样式
		// 生成并设置另一个样式
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFillForegroundColor(HSSFColor.WHITE.index);
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		// 生成另一个字体
		HSSFFont font2 = wb.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);

		// 把字体应用到当前的样式
		style2.setFont(font2);
		
		if (CollectionUtils.isEmpty(list)) {
			return wb;
		}
		
		for (int i=0; i<list.size(); i++) {
			HSSFRow row = sheet.createRow(i);
			List<String> item = list.get(i);
			for (int j=0; j<item.size(); j++) {
				HSSFCell cell = row.createCell(j);
				cell.setCellStyle(style2);
				cell.setCellValue(item.get(j));
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			}
		}

		return wb;
	}


}
