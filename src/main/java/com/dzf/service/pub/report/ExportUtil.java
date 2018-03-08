package com.dzf.service.pub.report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import com.alibaba.fastjson.JSONArray;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDouble;

/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档 转载时请保留以下信息，注明出处！
 * 
 * @author leno
 * @version v1.0
 * @param <T>
 *            应用泛型，代表任意一个符合javabean风格的类
 *            注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 *            byte[]表jpg格式的图片数据
 */

public class ExportUtil<T> {

	private Logger log = Logger.getLogger(this.getClass());
	
	public byte[] exportExcel(String title, List<String> headers, List<String> fields, JSONArray array,
			OutputStream out, String pattern, List<String> stringlist, Map<String,String> submap) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 列宽
			sheet.setDefaultColumnWidth(26);
			// 生成一个样式
			HSSFCellStyle style = workbook.createCellStyle();
			HSSFCellStyle st = workbook.createCellStyle();
			st.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			st.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			st.setBorderRight(HSSFCellStyle.BORDER_THIN);
			st.setBorderTop(HSSFCellStyle.BORDER_THIN);
			st.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			HSSFCellStyle st1 = workbook.createCellStyle();
			st1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			st1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			st1.setBorderRight(HSSFCellStyle.BORDER_THIN);
			st1.setBorderTop(HSSFCellStyle.BORDER_THIN);
			st1.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			// 设置这些样式
			style.setFillForegroundColor(HSSFColor.WHITE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			// 生成一个字体
			HSSFFont font = workbook.createFont();
			// font.setColor(HSSFColor.VIOLET.index);
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			// 把字体应用到当前的样式
			style.setFont(font);

			HSSFCellStyle style1 = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20);// 字号
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			style1.setFont(f);
			style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 内容左右居中
			style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 内容上下居中
																		// 、

			int headerlength = headers.size();
			int fieldlength = fields.size();
			// 合并标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell celltitle = rowtitle.createCell(0);
			celltitle.setCellValue(title);
			celltitle.setCellStyle(style1);
			sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, (fieldlength - 1)));// 合并标题

			// 合并期间、公司行
			if(submap != null && !submap.isEmpty()){
				if (title.equals("付款单余额明细") || title.equals("合同金额明细") ) {
					HSSFRow rowtitle1 = sheet.createRow(3);
					HSSFCell celltitle1 = rowtitle1.createCell(0);
					HSSFCell celltitle2 = rowtitle1.createCell(2);
					HSSFCell celltitle3 = rowtitle1.createCell(4);
					celltitle1.setCellValue("查询：" + submap.get("查询"));
					celltitle2.setCellValue("加盟商：" + submap.get("加盟商"));
					if(title.equals("付款单余额明细") ){
						celltitle3.setCellValue("付款类型：" + submap.get("付款类型"));
					}
					
					HSSFCellStyle style3 = workbook.createCellStyle();
					style3.setFont(font);
					style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					celltitle1.setCellStyle(style3);
					sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 1));
					
					celltitle2.setCellStyle(style3);
					sheet.addMergedRegion(new CellRangeAddress(3, 3, 2, 3));
					
					HSSFCellStyle style4 = workbook.createCellStyle();
					style4.setFont(font);
					style4.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
					style4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					celltitle3.setCellStyle(style4);
					sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 4));
				} 
			}
			// end 合并期间、公司行

			HSSFRow row = sheet.createRow(index);
			HSSFRow row2 = null;
			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				{
					HSSFCell cell1 = row.createCell(i);
					cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
					cell1.setCellStyle(style);
				}
			}
			for (int i = 0; i < array.size(); i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				Map<String, Object> map = (Map<String, Object>) array.get(i);
				int count = 0;
				for (String key : fields) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if (map.get(key) != null) {
							if (stringlist.contains(key)) {
								richString = new HSSFRichTextString(map.get(key).toString());
								cell.setCellValue(richString);
							} else if (!map.get(key).toString().equals("0")) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString());
								doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								cell.setCellValue(doublevalue.toString());
							} else {
								cell.setCellValue("");
							}
						}else{
							
						}
						if (stringlist.contains(key)) {
							cell.setCellStyle(st1);
						} else {
							cell.setCellStyle(st);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error(e);
					} finally {
						// 清理资源
					}
				}
			}

			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new WiseRunException(e);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return workbook.getBytes();

	}
}
