package com.dzf.service.pub.report;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.dzf.model.branch.reportmanage.CorpDataVO;
import com.dzf.model.channel.report.DataAnalysisVO;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDouble;

/**
 * 利用开源组件POI3.0.2动态导出EXCEL文档 如果有特殊格式导出，禁止在修改本类的方法，请复制新的方法修改。
 * 
 * @author dzf
 * @version v1.0
 * @param <T>
 *            应用泛型，代表任意一个符合javabean风格的类
 *            注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
 *            byte[]表jpg格式的图片数据
 */
public class ExportExcel<T> {

	private Logger log = Logger.getLogger(this.getClass());

	public void exportExcel(Collection<T> dataset, OutputStream out) {
		exportExcel("测试POI导出EXCEL文档", null, null, dataset, out, "yyyy-MM-dd");
	}

	public byte[] exportExcel(String sheetName, String[] headers, String[] fields, Collection<T> dataset,
			OutputStream out) {
		return exportExcel(sheetName, headers, fields, dataset, out, "yyyy-MM-dd");
	}

	public void exportExcel(String[] headers, String[] fields, Collection<T> dataset, OutputStream out,
			String pattern) {
		exportExcel("测试POI导出EXCEL文档", headers, fields, dataset, out, pattern);
	}

	/**
	 * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
	 * 
	 * @param title
	 *            表格标题名
	 * @param headers
	 *            表格属性列名数组
	 * @param dataset
	 *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的
	 *            javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param pattern
	 *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"
	 */
	@SuppressWarnings("unchecked")
	public byte[] exportExcel(String title, String[] headers, String[] fields, Collection<T> dataset, OutputStream out,
			String pattern) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 15);
		// 生成一个样式
		HSSFCellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 生成一个字体
		HSSFFont font = workbook.createFont();
		font.setColor(HSSFColor.VIOLET.index);
		font.setFontHeightInPoints((short) 12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 把字体应用到当前的样式
		style.setFont(font);
		// 生成并设置另一个样式
		HSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 生成另一个字体
		HSSFFont font2 = workbook.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);

		HSSFCellStyle style3 = workbook.createCellStyle();
		style3.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		style3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style3.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style3.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style3.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style3.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style3.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// 把字体应用到当前的样式
		style3.setFont(font2);

		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置,详见文档
		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("dzf");

		// 产生表格标题行
		HSSFRow row = sheet.createRow(0);
		for (short i = 0; i < headers.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(style);
			HSSFRichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}

		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			// Field[] fields = t.getClass().getDeclaredFields();
			for (short i = 0; i < fields.length; i++) {
				HSSFCell cell = row.createCell(i);
				// Field field = fields[i];
				String fieldName = fields[i];
				try {
					Class tCls = t.getClass();
					Object value = null;
					if (!StringUtil.isEmpty(fieldName)) {
						String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
						Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
						value = getMethod.invoke(t, new Object[] {});
					}
					if (value instanceof DZFDouble) {
						cell.setCellStyle(style3);
					} else {
						cell.setCellStyle(style2);
					}
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					if (value instanceof DZFBoolean) {
						boolean bValue = ((DZFBoolean) value).booleanValue();
						textValue = "是";
						if (!bValue) {
							textValue = "否";
						}
					} else if (value instanceof DZFDouble) {
						DZFDouble bValue = (DZFDouble) value;
						bValue.setScale(2, DZFDouble.ROUND_HALF_UP);
						textValue = bValue.toString();
					} else if (value instanceof Date) {
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat(pattern);
						textValue = sdf.format(date);
					}
					// else if (value instanceof byte[]){
					// // 有图片时，设置行高为60px;
					// row.setHeightInPoints(60);
					// // 设置图片所在列宽度为80px,注意这里单位的一个换算
					// sheet.setColumnWidth(i, (short) (35.7 * 80));
					// // sheet.autoSizeColumn(i);
					// byte[] bsValue = (byte[]) value;
					// HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0,
					// 1023, 255, (short) 6, index, (short) 6, index);
					// anchor.setAnchorType(2);
					// patriarch.createPicture(anchor, workbook.addPicture(
					// bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
					// }
					else {
						// 其它数据类型都当作字符串简单处理
						textValue = value == null ? null : value.toString();
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
							// cell.setCellValue(textValue);
						} else {
							HSSFRichTextString richString = new HSSFRichTextString(textValue);
							HSSFFont font3 = workbook.createFont();
							font3.setColor(HSSFColor.BLUE.index);
							richString.applyFont(font3);
							cell.setCellValue(richString);
						}
					}
				} catch (SecurityException e) {
					log.error("Excel导出错误", e);
				} catch (NoSuchMethodException e) {
					log.error("Excel导出错误", e);
				} catch (IllegalArgumentException e) {
					log.error("Excel导出错误", e);
				} catch (IllegalAccessException e) {
					log.error("Excel导出错误", e);
				} catch (InvocationTargetException e) {
					log.error("Excel导出错误", e);
				} finally {
					// 清理资源
				}
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			log.error("Excel导出错误", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 合并单元格设置边框
	 * 
	 * @param sheet
	 * @param region
	 * @param cs
	 */
	private void setRegionStyle(XSSFSheet sheet, CellRangeAddress region, XSSFCellStyle cs) {
		for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
			XSSFRow row = (XSSFRow) CellUtil.getRow(i, sheet);
			for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
				XSSFCell cell = (XSSFCell) CellUtil.getCell(row, (short) j);
				cell.setCellStyle(cs);
			}
		}
	}

	/**
	 * 合并单元格设置边框
	 * 
	 * @param sheet
	 * @param region
	 * @param cs
	 */
	@SuppressWarnings("deprecation")
	private void setRegionStyle(HSSFSheet sheet, Region region, HSSFCellStyle cs) {
		for (int i = region.getRowFrom(); i <= region.getRowTo(); i++) {
			HSSFRow row = HSSFCellUtil.getRow(i, sheet);
			for (int j = region.getColumnFrom(); j <= region.getColumnTo(); j++) {
				HSSFCell cell = HSSFCellUtil.getCell(row, (short) j);
				cell.setCellStyle(cs);
			}
		}
	}

	/**
	 * 扣款统计表导出
	 * 
	 * @param title
	 * @param exptitls
	 * @param hbltitls
	 * @param fields
	 * @param array
	 * @param out
	 * @param pattern
	 * @return
	 */
	public void expDeductExcel(String title, List<String> exptitls, List<String> expfieids, List<String> hbltitls,
			List<Integer> hblindexs, List<String> hbhtitls, Integer[] hbhindexs, JSONArray array, OutputStream out,
			String pattern, List<String> strslist, List<String> mnylist) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		try {
			int index = 4;
			XSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);

			// 数字类型样式
			XSSFCellStyle numsty = workbook.createCellStyle();
			numsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			numsty.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			// 字符、日期类型样式
			XSSFCellStyle strsty = workbook.createCellStyle();
			strsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			strsty.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			// 通用样式
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(HSSFColor.WHITE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			// 生成一个字体
			XSSFFont font = workbook.createFont();
			// font.setColor(HSSFColor.VIOLET.index);
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			// 把字体应用到当前的样式
			style.setFont(font);

			XSSFCellStyle headstyle = workbook.createCellStyle();
			XSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 14);// 字号
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			headstyle.setFont(f);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 内容左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 内容上下居中
			headstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

			int headerlength = exptitls.size();
			int fieldlength = expfieids.size();

			// 大标题
			XSSFRow rowtitle = sheet.createRow(0);
			XSSFCell titlecell = rowtitle.createCell(0);
			titlecell.setCellValue(title);
			titlecell.setCellStyle(headstyle);
			// 起始行号，终止行号， 起始列号，终止列号
			CellRangeAddress region = new CellRangeAddress(0, (short) 2, 0, (short) (fieldlength - 1));// 合并前三行
			sheet.addMergedRegion(region);// 合并标题

			// 合并行标题赋值 begin##############################################
			XSSFRow rowthree = sheet.createRow(3);
			if (hbhtitls != null && hbhtitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbhtitls.size(); i++) {
					begindex = hbhindexs[i];
					XSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new XSSFRichTextString(String.valueOf(hbhtitls.get(i))));
					cell.setCellStyle(style);
					// 起始行号，终止行号， 起始列号，终止列号
					CellRangeAddress reg = new CellRangeAddress(3, 4, (short) (begindex), (short) (begindex));
					sheet.addMergedRegion(reg);
				}
			}
			// 合并行标题赋值 end################################################

			// 合并列 begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			XSSFRow rowfour = sheet.createRow(4);
			CellRangeAddress reg = null;
			if (hbltitls != null && hbltitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbltitls.size(); i++) {
					begindex = hblindexs.get(i);
					XSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new XSSFRichTextString(String.valueOf(hbltitls.get(i))));
					cell.setCellStyle(style);
					// 起始行号，终止行号， 起始列号，终止列号
					reg = new CellRangeAddress(3, 3, (short) (begindex), (short) (begindex + 1));
					sheet.addMergedRegion(reg);
				}
			}

			// 合并列 end&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			// 添加导出行信息
			XSSFRow row2 = null;
			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				XSSFCell cell1 = rowfour.createCell(i);
				cell1.setCellValue(new XSSFRichTextString(String.valueOf(exptitls.get(i))));
				cell1.setCellStyle(style);
			}

			// 合并单元格设置边框
			setRegionStyle(sheet, region, headstyle);
			setRegionStyle(sheet, reg, style);

			for (int i = 0; i < array.size(); i++) {
				XSSFRow row1 = sheet.createRow(i + index + 1);
				Map<String, Object> map = (Map<String, Object>) array.get(i);
				int count = 0;
				for (String key : expfieids) {
					try {
						XSSFRichTextString richString;
						XSSFCell cell = row1.createCell(count);
						if (map.get(key) != null) {
							if (mnylist != null && mnylist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString(), 2);
								cell.setCellValue(doublevalue.toString());
							} else if (strslist != null && strslist.contains(key)) {
								richString = new XSSFRichTextString(map.get(key).toString());
								cell.setCellValue(richString);
							}
						} else {
							richString = new XSSFRichTextString("");
							cell.setCellValue(richString);
						}
						if (mnylist != null && mnylist.contains(key)) {
							cell.setCellStyle(numsty);
						} else if (strslist != null && strslist.contains(key)) {
							cell.setCellStyle(strsty);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("文件打印", e);
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
			log.error("文件打印", e);
		}
	}

	/**
	 * 加盟商扣款查询导出
	 * 
	 * @param title
	 * @param headers
	 * @param headers2
	 * @param fields
	 * @param array
	 * @param out
	 * @param pattern
	 * @param gs
	 * @param qj
	 * @param fieldlist
	 * @return
	 */
	public byte[] exportDebitExcel(String title, List<String> headers, List<String> headers1, List<String> fields,
			JSONArray array, OutputStream out, String pattern, List<String> fieldlist) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			int num = 8;

			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth(15);
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
			font.setFontHeightInPoints((short) 12);// 字号
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

			int headerlength = headers.size();
			int fieldlength = fields.size();
			// 合并标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell celltitle = rowtitle.createCell(0);
			celltitle.setCellValue(title);
			celltitle.setCellStyle(style1);
			sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, (fieldlength - 1)));// 合并标题

			HSSFRow row = sheet.createRow(index);
			HSSFRow row2 = null;

			// 第一行标题行
			HSSFRow rowtitle1m = sheet.createRow(3);
			for (int i = 0; i < fieldlength; i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(i);
				celltitle1m.setCellStyle(style);
			}

			for (int i = 0; i * 2 < fieldlength - num; i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(num + 2 * i);
				celltitle1m.setCellValue(new HSSFRichTextString(headers1.get(i)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 3, num + 2 * i, num + 1 + 2 * i));
			}
			HSSFCellStyle stylegsm = workbook.createCellStyle();// 表头样式
			stylegsm.cloneStyleFrom(style);
			stylegsm.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			for (int i = 0; i < num; i++) {
				int j = i;
				if (i >= num) {
					j = i + 4;
				}
				HSSFCell celltitle1m = rowtitle1m.createCell(j);
				celltitle1m.setCellValue(new HSSFRichTextString(headers.get(j)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 4, j, j));
			}

			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = row.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
				cell1.setCellStyle(style);
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
							if (!fieldlist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString());
								doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								cell.setCellValue(doublevalue.toString());
							} else {
								String value = map.get(key).toString();
								if (key.equals("chtype")) {
									if (!StringUtil.isEmpty(value)) {
										if (value.equals("1")) {
											value = "普通加盟商";
										} else {
											value = "金牌加盟商";
										}
									}
								}
								richString = new HSSFRichTextString(value);
								cell.setCellValue(richString);
							}
						} else {
							cell.setCellValue("");
						}
						if (!fieldlist.contains(key)) {
							cell.setCellStyle(st);
						} else {
							cell.setCellStyle(st1);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("文件打印", e);
					} finally {
						// 清理资源
					}
				}
				if (i == array.size()) {
					sheet.addMergedRegion(new CellRangeAddress(i + index + 1, i + index + 1, 0, 1));
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new WiseRunException(e);
			}
		} catch (Exception e) {
			log.error("文件打印", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 渠道运营管理报表导出功能
	 * 
	 * @param title
	 * @param headers
	 * @param headers1
	 * @param fields
	 * @param array
	 * @param out
	 * @param pattern
	 * @param fieldlist
	 * @param num
	 * @return
	 */
	public byte[] exportManExcel(String title, List<String> headers, List<String> headers1, List<String> fields,
			JSONArray array, OutputStream out, String pattern, List<String> fieldlist, Integer num) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth(15);
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
			font.setFontHeightInPoints((short) 12);// 字号
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

			HSSFRow row = sheet.createRow(index);
			HSSFRow row2 = null;

			// 第一行标题行
			HSSFRow rowtitle1m = sheet.createRow(3);
			for (int i = 0; i < fieldlength; i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(i);
				celltitle1m.setCellStyle(style);
			}
			for (int i = 0; i < 2; i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(num + 2 * i);
				celltitle1m.setCellValue(new HSSFRichTextString(headers1.get(i)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 3, num + 2 * i, num + 1 + 2 * i));
			}
			HSSFCellStyle stylegsm = workbook.createCellStyle();// 表头样式
			stylegsm.cloneStyleFrom(style);
			stylegsm.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			for (int i = 0; i < num + 4; i++) {
				int j = i;
				if (i >= num) {
					j = i + 4;
				}
				HSSFCell celltitle1m = rowtitle1m.createCell(j);
				celltitle1m.setCellValue(new HSSFRichTextString(headers.get(j)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 4, j, j));
			}

			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = row.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
				cell1.setCellStyle(style);
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
							if (!fieldlist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString());
								doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								cell.setCellValue(doublevalue.toString());
							} else {
								String value = map.get(key).toString();
								richString = new HSSFRichTextString(value);
								cell.setCellValue(richString);
							}
						} else if (key.equals("retmny")) {
							cell.setCellValue("0.00");
						} else {
							cell.setCellValue("");
						}
						if (!fieldlist.contains(key)) {
							cell.setCellStyle(st);
						} else {
							cell.setCellStyle(st1);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("渠道运营管理报表导出", e);
					} finally {
						// 清理资源
					}
				}
				if (i == array.size()) {
					sheet.addMergedRegion(new CellRangeAddress(i + index + 1, i + index + 1, 0, 1));
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new WiseRunException(e);
			}
		} catch (Exception e) {
			log.error("渠道运营管理报表导出", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 业绩统计表导出
	 * 
	 * @param title
	 * @param headers
	 * @param headers1
	 * @param fields
	 * @param array
	 * @param out
	 * @param pattern
	 * @param fieldlist
	 * @param num
	 * @return
	 */
	public byte[] exportYjtjExcel(String title, List<String> headers, List<String> headers1, List<String> fields,
			JSONArray array, OutputStream out, String pattern, List<String> fieldlist, Integer num) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth(15);
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
			font.setFontHeightInPoints((short) 12);// 字号
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

			HSSFRow row = sheet.createRow(index);
			HSSFRow row2 = null;

			// 第一行标题行
			HSSFRow rowtitle1m = sheet.createRow(3);
			for (int i = 0; i < fieldlength; i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(i);
				celltitle1m.setCellStyle(style);
			}
			for (int i = 0; i < headers1.size(); i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(num + 2 * i);
				celltitle1m.setCellValue(new HSSFRichTextString(headers1.get(i)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 3, num + 2 * i, num + 1 + 2 * i));
			}
			HSSFCellStyle stylegsm = workbook.createCellStyle();// 表头样式
			stylegsm.cloneStyleFrom(style);
			stylegsm.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			for (int i = 0; i < num; i++) {
				int j = i;
				if (i >= num) {
					j = i + 4;
				}
				HSSFCell celltitle1m = rowtitle1m.createCell(j);
				celltitle1m.setCellValue(new HSSFRichTextString(headers.get(j)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 4, j, j));
			}

			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = row.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
				cell1.setCellStyle(style);
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
							if (key.contains("trate")) {
								if (new DZFDouble(map.get(key).toString()).equals(new DZFDouble().ZERO_DBL)) {
									cell.setCellValue("--");
								} else {
									DZFDouble doublevalue = new DZFDouble(map.get(key).toString());
									doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
									cell.setCellValue(doublevalue.toString());
								}
							} else if (!fieldlist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString());
								doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								cell.setCellValue(doublevalue.toString());
							} else {
								String value = map.get(key).toString();
								richString = new HSSFRichTextString(value);
								cell.setCellValue(richString);
							}
						} else {
							cell.setCellValue("");
						}
						if (!fieldlist.contains(key)) {
							cell.setCellStyle(st);
						} else {
							cell.setCellStyle(st1);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("业绩统计表导出", e);
					} finally {
						// 清理资源
					}
				}
				if (i == array.size()) {
					sheet.addMergedRegion(new CellRangeAddress(i + index + 1, i + index + 1, 0, 1));
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new WiseRunException(e);
			}
		} catch (Exception e) {
			log.error("业绩统计表导出", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 出入库明细表导出
	 * 
	 * @param title
	 * @param headers
	 * @param headers1
	 * @param fields
	 * @param array
	 * @param out
	 * @param pattern
	 * @param fieldlist
	 * @param num
	 * @return
	 */
	public byte[] exportOutInExcel(String title, String qj, List<String> headers, List<String> headers1,
			List<String> fields, JSONArray array, OutputStream out, String pattern, List<String> fieldlist,
			Integer num) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth(15);
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
			font.setFontHeightInPoints((short) 12);// 字号
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
			int fieldlength = fieldlist.size();
			// 合并标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell celltitle = rowtitle.createCell(0);
			celltitle.setCellValue(title);
			celltitle.setCellStyle(style1);
			sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, (fieldlength - 1)));// 合并标题

			HSSFRow row = sheet.createRow(index);
			HSSFRow row2 = null;

			// 第一行标题行
			HSSFRow rowtitle1m = sheet.createRow(3);
			for (int i = 0; i < fieldlength; i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(i);
				celltitle1m.setCellStyle(style);
			}
			for (int i = 0; i < headers1.size(); i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(num + 3 * i);
				celltitle1m.setCellValue(new HSSFRichTextString(headers1.get(i)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 3, num + 3 * i, num + 2 + 3 * i));
			}
			HSSFCellStyle stylegsm = workbook.createCellStyle();// 表头样式
			stylegsm.cloneStyleFrom(style);
			stylegsm.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			for (int i = 0; i < num; i++) {
				int j = i;
				if (i >= num) {
					j = i + 4;
				}
				HSSFCell celltitle1m = rowtitle1m.createCell(j);
				celltitle1m.setCellValue(new HSSFRichTextString(headers.get(j)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 4, j, j));
			}

			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = row.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
				cell1.setCellStyle(style);
			}
			for (int i = 0; i < array.size(); i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				Map<String, Object> map = (Map<String, Object>) array.get(i);
				int count = 0;
				for (String key : fieldlist) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if (map.get(key) != null) {
							if (key.equals("itype")) {
								if (map.get(key).equals(0)) {
									cell.setCellValue("期初余额");
								} else if (map.get(key).equals(1)) {
									cell.setCellValue("商品入库");
								} else if (map.get(key).equals(2)) {
									cell.setCellValue("销售出库");
								} else {
									cell.setCellValue("其他出库");
								}
							} else if (key.equals("contime")) {
								if (map.get(key).equals("1999-01-01 00:00:00")) {
									cell.setCellValue(qj.substring(0, 10));
								} else {
									cell.setCellValue(((String) map.get(key)).substring(0, 10));
								}
							} else if (key.contains("price") || key.contains("money")) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString());
								doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								cell.setCellValue(doublevalue.toString());
								if (!map.get(key).equals(0)) {
									DZFDouble doublevalue2 = new DZFDouble(map.get(key).toString());
									doublevalue2 = doublevalue2.setScale(4, DZFDouble.ROUND_HALF_UP);
									cell.setCellValue(doublevalue2.toString());
								} else if (map.get(key).equals(0) && (key.equals("moneyb") || key.equals("priceb"))) {
									cell.setCellValue("0.0000");
								} else {
									cell.setCellValue("");
								}
							} else if (key.contains("in") || key.contains("out")) {
								if (!map.get(key).equals(0)) {
									String value = map.get(key).toString();
									richString = new HSSFRichTextString(value);
									cell.setCellValue(richString);
								} else {
									cell.setCellValue("");
								}
							} else {
								String value = map.get(key).toString();
								richString = new HSSFRichTextString(value);
								cell.setCellValue(richString);
							}
						} else {
							cell.setCellValue("");
						}
						if (!fieldlist.contains(key)) {
							cell.setCellStyle(st);
						} else {
							cell.setCellStyle(st1);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("出入库明细表导出", e);
					} finally {
						// 清理资源
					}
				}
				if (i == array.size()) {
					sheet.addMergedRegion(new CellRangeAddress(i + index + 1, i + index + 1, 0, 1));
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new WiseRunException(e);
			}
		} catch (Exception e) {
			log.error("出入库明细表导出", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 物料申请表导出
	 * 
	 * @param string
	 * @param exptitlist
	 * @param expfieidlist
	 * @param hbltitlist
	 * @param hblindexlist
	 * @param hbhtitlist
	 * @param hbhindexs
	 * @param array
	 * @param toClient
	 * @param string2
	 * @param strslist
	 * @param mnylist
	 * @return
	 */
	public byte[] expMatApply(String title, List<String> exptitls, List<String> expfieids, List<String> hbltitls,
			List<Integer> hblindexs, List<String> hbhtitls, List<Integer> hbhindexs, JSONArray array, OutputStream out,
			String pattern, List<String> strslist, List<String> mnylist) {

		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);

			// 数字类型样式
			HSSFCellStyle numsty = workbook.createCellStyle();
			numsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			numsty.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			// 字符、日期类型样式
			HSSFCellStyle strsty = workbook.createCellStyle();
			strsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			strsty.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			// 通用样式
			HSSFCellStyle style = workbook.createCellStyle();
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
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			// 把字体应用到当前的样式
			style.setFont(font);

			HSSFCellStyle headstyle = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20);// 字号
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			headstyle.setFont(f);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 内容左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 内容上下居中
																			// 、
			headstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

			int headerlength = exptitls.size();
			int fieldlength = expfieids.size();

			// 大标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell titlecell = rowtitle.createCell(0);
			titlecell.setCellValue(title);
			titlecell.setCellStyle(headstyle);
			// Region(int left, int top, int right, int bottom)
			Region region = new Region(0, (short) 0, 2, (short) (fieldlength - 1));// 合并前三行
			sheet.addMergedRegion(region);// 合并标题

			// 合并行标题赋值 begin##############################################
			HSSFRow rowthree = sheet.createRow(3);
			if (hbhtitls != null && hbhtitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbhtitls.size(); i++) {
					begindex = hbhindexs.get(i);
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(hbhtitls.get(i))));
					cell.setCellStyle(style);
					Region reg = new Region(3, (short) (begindex), 4, (short) (begindex));
					sheet.addMergedRegion(reg);
				}
			}
			// 合并行标题赋值 end################################################

			// 合并列 begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			HSSFRow rowfour = sheet.createRow(4);
			Region reg = null;
			if (hbltitls != null && hbltitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbltitls.size(); i++) {
					begindex = hblindexs.get(i);
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(hbltitls.get(i))));
					cell.setCellStyle(style);
					if (i == hbltitls.size() - 1) {
						reg = new Region(3, (short) (begindex), 3, (short) (begindex + 3));
					} else if (i == hbltitls.size() - 2) {
						reg = new Region(3, (short) (begindex), 3, (short) (begindex + 2));
					} else {
						reg = new Region(3, (short) (begindex), 3, (short) (begindex + 1));
					}
					sheet.addMergedRegion(reg);
				}
			}

			// 合并列 end&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			// 添加导出行信息
			HSSFRow row2 = null;
			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = rowfour.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(exptitls.get(i))));
				cell1.setCellStyle(style);
			}

			// 合并单元格设置边框
			setRegionStyle(sheet, region, headstyle);
			setRegionStyle(sheet, reg, style);
			// setRegionStyle(sheet, reg3_1, rightstyle);

			String showname = "";
			for (int i = 0; i < array.size(); i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				Map<String, Object> map = (Map<String, Object>) array.get(i);
				int count = 0;
				for (String key : expfieids) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if (map.get(key) != null && !map.get(key).toString().equals("0")) {
							if (mnylist != null && mnylist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString(), 2);
								// doublevalue = doublevalue.setScale(0,
								// DZFDouble.ROUND_HALF_UP);//四舍五入
								cell.setCellValue(doublevalue.toString());
							} else if (strslist != null && strslist.contains(key)) {
								if ("status".equals(key)) {
									// 合同状态 1-待审核、2-待发货、3-已发货、4-已驳回
									if ("1".equals(map.get(key).toString())) {
										showname = "待审核";
									} else if ("2".equals(map.get(key).toString())) {
										showname = "待发货";
									} else if ("3".equals(map.get(key).toString())) {
										showname = "已发货";
									} else if ("4".equals(map.get(key).toString())) {
										showname = "已驳回";
									}
									richString = new HSSFRichTextString(showname);
								} else {
									richString = new HSSFRichTextString(map.get(key).toString());
								}
								cell.setCellValue(richString);
							}
						} else {
							richString = new HSSFRichTextString("");
							cell.setCellValue(richString);
						}
						if (mnylist != null && mnylist.contains(key)) {
							cell.setCellStyle(numsty);
						} else if (strslist != null && strslist.contains(key)) {
							cell.setCellStyle(strsty);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("文件打印", e);
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
			log.error("文件打印", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 导出快递统计表
	 * 
	 * @param title
	 * @param exptitls
	 * @param expfieids
	 * @param hbltitls
	 * @param hblindexs
	 * @param hbhtitls
	 * @param hbhindexs
	 * @param array
	 * @param out
	 * @param pattern
	 * @param strslist
	 * @param mnylist
	 * @return
	 */
	public byte[] exportLogistic(String title, List<String> exptitls, List<String> expfieids, List<String> hbltitls,
			List<Integer> hblindexs, List<String> hbhtitls, List<Integer> hbhindexs, JSONArray array, OutputStream out,
			String pattern, List<String> strslist, List<String> mnylist) {

		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);

			// 数字类型样式
			HSSFCellStyle numsty = workbook.createCellStyle();
			numsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			numsty.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			// 字符、日期类型样式
			HSSFCellStyle strsty = workbook.createCellStyle();
			strsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			strsty.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			// 通用样式
			HSSFCellStyle style = workbook.createCellStyle();
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
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			// 把字体应用到当前的样式
			style.setFont(font);

			HSSFCellStyle headstyle = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20);// 字号
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			headstyle.setFont(f);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 内容左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 内容上下居中
																			// 、
			headstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

			int headerlength = exptitls.size();
			int fieldlength = expfieids.size();

			// 大标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell titlecell = rowtitle.createCell(0);
			titlecell.setCellValue(title);
			titlecell.setCellStyle(headstyle);
			// Region(int left, int top, int right, int bottom)
			Region region = new Region(0, (short) 0, 2, (short) (fieldlength - 1));// 合并前三行
			sheet.addMergedRegion(region);// 合并标题

			// 合并行标题赋值 begin##############################################
			HSSFRow rowthree = sheet.createRow(3);
			if (hbhtitls != null && hbhtitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbhtitls.size(); i++) {
					begindex = hbhindexs.get(i);
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(hbhtitls.get(i))));
					cell.setCellStyle(style);
					Region reg = new Region(3, (short) (begindex), 4, (short) (begindex));
					sheet.addMergedRegion(reg);
				}
			}
			// 合并行标题赋值 end################################################

			// 合并列 begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			HSSFRow rowfour = sheet.createRow(4);
			Region reg = null;
			if (hbltitls != null && hbltitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbltitls.size(); i++) {
					begindex = hblindexs.get(i);
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(hbltitls.get(i))));
					cell.setCellStyle(style);
					if (i == hbltitls.size() - 1) {
						reg = new Region(3, (short) (begindex), 3, (short) (begindex + 3));
					} else if (i == hbltitls.size() - 2) {
						reg = new Region(3, (short) (begindex), 3, (short) (begindex + 2));
					} else {
						reg = new Region(3, (short) (begindex), 3, (short) (begindex + 1));
					}
					sheet.addMergedRegion(reg);
				}
			}

			// 合并列 end&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			// 添加导出行信息
			HSSFRow row2 = null;
			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = rowfour.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(exptitls.get(i))));
				cell1.setCellStyle(style);
			}

			// 合并单元格设置边框
			setRegionStyle(sheet, region, headstyle);
			setRegionStyle(sheet, reg, style);

			String showname = "";
			for (int i = 0; i < array.size(); i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				Map<String, Object> map = (Map<String, Object>) array.get(i);
				int count = 0;
				for (String key : expfieids) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if (map.get(key) != null && !map.get(key).toString().equals("0")) {
							if (mnylist != null && mnylist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString(), 2);
								cell.setCellValue(doublevalue.toString());
							} else if (strslist != null && strslist.contains(key)) {
								richString = new HSSFRichTextString(map.get(key).toString());
								cell.setCellValue(richString);
							}
						} else {
							richString = new HSSFRichTextString("");
							cell.setCellValue(richString);
						}
						if (mnylist != null && mnylist.contains(key)) {
							cell.setCellStyle(numsty);
						} else if (strslist != null && strslist.contains(key)) {
							cell.setCellStyle(strsty);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("文件打印", e);
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
			log.error("文件打印", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 渠道业绩统计表
	 * 
	 * @param string
	 * @param conameList
	 * @param codeList
	 * @param array
	 * @param mnylist
	 * @param strslist
	 * @param toClient
	 * @param string2
	 * @return
	 */
	public byte[] expChannelStatis(String title, List<String> conameList, List<String> codeList, JSONArray array,
			OutputStream out, List<String> mnylist, List<String> strslist) {
		List<Integer> codeindexList = new ArrayList<Integer>();
		codeindexList.add(0);
		codeindexList.add(1);
		codeindexList.add(2);
		codeindexList.add(3);
		codeindexList.add(4);
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);

			// 数字类型样式
			HSSFCellStyle numsty = workbook.createCellStyle();
			numsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			numsty.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			// 字符、日期类型样式
			HSSFCellStyle strsty = workbook.createCellStyle();
			strsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			strsty.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			// 通用样式
			HSSFCellStyle style = workbook.createCellStyle();
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
			font.setFontHeightInPoints((short) 12);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			// 把字体应用到当前的样式
			style.setFont(font);

			HSSFCellStyle headstyle = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20);// 字号
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			headstyle.setFont(f);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 内容左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 内容上下居中

			headstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

			int headerlength = conameList.size();
			int fieldlength = codeList.size();

			// 大标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell titlecell = rowtitle.createCell(0);
			titlecell.setCellValue(title);
			titlecell.setCellStyle(headstyle);
			Region region = new Region(0, (short) 0, 2, (short) (fieldlength - 1));// 合并前三行
			sheet.addMergedRegion(region);// 合并标题

			// 添加导出行信息
			HSSFRow rowfour = sheet.createRow(3);
			Region reg = null;
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = rowfour.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(conameList.get(i))));
				cell1.setCellStyle(style);
			}

			// 大标题合并行设置边框
			setRegionStyle(sheet, region, headstyle);

			for (int i = 0; i < array.size(); i++) {
				HSSFRow row1 = sheet.createRow(i + index);
				Map<String, Object> map = (Map<String, Object>) array.get(i);

				int count = 0;
				for (String key : codeList) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if (map.get(key) != null && !map.get(key).toString().equals("0")) {
							if (mnylist != null && mnylist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString(), 2);
								cell.setCellValue(doublevalue.toString());

							} else if (strslist != null && strslist.contains(key)) {
								richString = new HSSFRichTextString(map.get(key).toString());
								cell.setCellValue(richString);
							}
						} else {
							richString = new HSSFRichTextString("");
							cell.setCellValue(richString);
						}
						if (mnylist != null && mnylist.contains(key)) {
							cell.setCellStyle(numsty);
							if (map.get(key).toString().equals("0")) {
								cell.setCellValue("0.00");
							}
						} else if (strslist != null && strslist.contains(key)) {
							cell.setCellStyle(strsty);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("文件打印", e);
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
			log.error("文件打印", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 加盟商数据分析导出
	 * 
	 * @param title
	 * @param exptitls
	 * @param expfieids
	 * @param hbltitls
	 * @param hblindexs
	 * @param hbhtitls
	 * @param hbhindexs
	 * @param expVOs
	 * @param out
	 * @param pattern
	 * @param strslist
	 * @param mnylist
	 * @return
	 */
	public byte[] expSjfxExcel(String title, List<String> exptitls, List<String> expfieids, List<String> hbltitls,
			List<Integer> hblindexs, List<String> hbhtitls, Integer[] hbhindexs, DataAnalysisVO[] expVOs,
			OutputStream out, String pattern, List<String> strslist, List<String> mnylist, List<String> strnmylist) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);

			// 数字类型样式
			HSSFCellStyle numsty = workbook.createCellStyle();
			numsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			numsty.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			// 字符、日期类型样式
			HSSFCellStyle strsty = workbook.createCellStyle();
			strsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			strsty.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			// 通用样式
			HSSFCellStyle style = workbook.createCellStyle();
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

			HSSFCellStyle headstyle = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20);// 字号
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			headstyle.setFont(f);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 内容左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 内容上下居中
																			// 、
			headstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

			int headerlength = exptitls.size();
			int fieldlength = expfieids.size();

			// 大标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell titlecell = rowtitle.createCell(0);
			titlecell.setCellValue(title);
			titlecell.setCellStyle(headstyle);
			// Region(int left, int top, int right, int bottom)
			Region region = new Region(0, (short) 0, 2, (short) (fieldlength - 1));// 合并前三行
			sheet.addMergedRegion(region);// 合并标题

			// 合并行标题赋值 begin##############################################
			HSSFRow rowthree = sheet.createRow(3);
			if (hbhtitls != null && hbhtitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbhtitls.size(); i++) {
					begindex = hbhindexs[i];
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(hbhtitls.get(i))));
					cell.setCellStyle(style);
					Region reg = new Region(3, (short) (begindex), 4, (short) (begindex));
					sheet.addMergedRegion(reg);
				}
			}
			// 合并行标题赋值 end################################################

			// 合并列 begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			HSSFRow rowfour = sheet.createRow(4);
			Region reg = null;
			if (hbltitls != null && hbltitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbltitls.size(); i++) {
					begindex = hblindexs.get(i);
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(hbltitls.get(i))));
					cell.setCellStyle(style);
					reg = new Region(3, (short) (begindex), 3, (short) (begindex + 1));
					sheet.addMergedRegion(reg);
				}
			}

			// 合并列 end&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			// 添加导出行信息
			HSSFRow row2 = null;
			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = rowfour.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(exptitls.get(i))));
				cell1.setCellStyle(style);
			}

			// 合并单元格设置边框
			setRegionStyle(sheet, region, headstyle);
			setRegionStyle(sheet, reg, style);
			// setRegionStyle(sheet, reg3_1, rightstyle);

			
			for (int i = 0; i < expVOs.length; i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				int count = 0;
				for (String key : expfieids) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if (expVOs[i].getAttributeValue(key) != null) {
							if (mnylist != null && mnylist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(expVOs[i].getAttributeValue(key).toString(), 2);
								cell.setCellValue(doublevalue.toString());
							} else if (strslist != null && strslist.contains(key)) {
								richString = new HSSFRichTextString(expVOs[i].getAttributeValue(key).toString());
								cell.setCellValue(richString);
							}
						} else {
							richString = new HSSFRichTextString("");
							cell.setCellValue(richString);
						}
						if (mnylist != null && mnylist.contains(key)) {
							cell.setCellStyle(numsty);
						} else if (strslist != null && strslist.contains(key)) {
							if (strnmylist != null && strnmylist.contains(key)) {
								cell.setCellStyle(numsty);
							} else {
								cell.setCellStyle(strsty);
							}
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("文件打印", e);
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
			log.error("文件打印", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 分部管理-导出客户数据
	 * 
	 * @param title
	 * @param exptitls
	 * @param expfieids
	 * @param hbltitls
	 * @param hblindexs
	 * @param hbhtitls
	 * @param hbhindexs
	 * @param expVOs
	 * @param out
	 * @param pattern
	 * @param strslist
	 * @param mnylist
	 * @param strnmylist
	 * @return
	 */
	public byte[] expCorpDataExcel(String title, List<String> exptitls, List<String> expfieids, List<String> hbltitls,
			List<Integer> hblindexs, List<String> hbhtitls, Integer[] hbhindexs, CorpDataVO[] expVOs, OutputStream out,
			String pattern, List<String> strslist, List<String> mnylist, List<String> strnmylist) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);

			// 数字类型样式
			HSSFCellStyle numsty = workbook.createCellStyle();
			numsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			numsty.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			// 字符、日期类型样式
			HSSFCellStyle strsty = workbook.createCellStyle();
			strsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			strsty.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			// 通用样式
			HSSFCellStyle style = workbook.createCellStyle();
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

			HSSFCellStyle headstyle = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20);// 字号
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			headstyle.setFont(f);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 内容左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 内容上下居中
			headstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);

			int headerlength = exptitls.size();
			int fieldlength = expfieids.size();

			// 大标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell titlecell = rowtitle.createCell(0);
			titlecell.setCellValue(title);
			titlecell.setCellStyle(headstyle);
			// Region(int left, int top, int right, int bottom)
			Region region = new Region(0, (short) 0, 2, (short) (fieldlength - 1));// 合并前三行
			sheet.addMergedRegion(region);// 合并标题

			// 合并行标题赋值 begin##############################################
			HSSFRow rowthree = sheet.createRow(3);
			if (hbhtitls != null && hbhtitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbhtitls.size(); i++) {
					begindex = hbhindexs[i];
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(hbhtitls.get(i))));
					cell.setCellStyle(style);
					Region reg = new Region(3, (short) (begindex), 4, (short) (begindex));
					sheet.addMergedRegion(reg);
				}
			}
			// 合并行标题赋值 end################################################

			// 合并列 begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			HSSFRow rowfour = sheet.createRow(4);
			Region reg = null;
			if (hbltitls != null && hbltitls.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < hbltitls.size(); i++) {
					begindex = hblindexs.get(i);
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(hbltitls.get(i))));
					cell.setCellStyle(style);
					reg = new Region(3, (short) (begindex), 3, (short) (begindex + 4));
					sheet.addMergedRegion(reg);
				}
			}

			// 合并列 end&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			// 添加导出行信息
			HSSFRow row2 = null;
			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = rowfour.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(exptitls.get(i))));
				cell1.setCellStyle(style);
			}

			// 合并单元格设置边框
			setRegionStyle(sheet, region, headstyle);
			setRegionStyle(sheet, reg, style);
			// setRegionStyle(sheet, reg3_1, rightstyle);

			for (int i = 0; i < expVOs.length; i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				int count = 0;
				for (String key : expfieids) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if (expVOs[i].getAttributeValue(key) != null) {
							if (mnylist != null && mnylist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(expVOs[i].getAttributeValue(key).toString(), 2);
								cell.setCellValue(doublevalue.toString());
							} else if (strslist != null && strslist.contains(key)) {
								richString = new HSSFRichTextString(expVOs[i].getAttributeValue(key).toString());
								cell.setCellValue(richString);
							}
						} else {
							richString = new HSSFRichTextString("");
							cell.setCellValue(richString);
						}
						if (mnylist != null && mnylist.contains(key)) {
							cell.setCellStyle(numsty);
						} else if (strslist != null && strslist.contains(key)) {
							if (strnmylist != null && strnmylist.contains(key)) {
								cell.setCellStyle(numsty);
							} else {
								cell.setCellStyle(strsty);
							}
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("文件打印", e);
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
			log.error("文件打印", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 公司数据统计/销售业绩统计 导出
	 * 
	 * @param title
	 * @param qj
	 * @param headers
	 * @param headers1
	 * @param fields
	 * @param array
	 * @param out
	 * @param pattern
	 * @param fieldlist
	 * @param num
	 * @return
	 */
	public byte[] exportCompanyDataExcel(String title, List<String> headers, List<String> headers1, List<String> fields,
			JSONArray array, OutputStream out, String pattern, List<String> fieldlist, Integer num) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth(15);
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
			font.setFontHeightInPoints((short) 12);// 字号
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

			HSSFRow row = sheet.createRow(index);
			HSSFRow row2 = null;

			// 第一行标题行
			HSSFRow rowtitle1m = sheet.createRow(3);
			for (int i = 0; i < fieldlength; i++) {
				HSSFCell celltitle1m = rowtitle1m.createCell(i);
				celltitle1m.setCellStyle(style);
			}

			HSSFCell celltitle1m = rowtitle1m.createCell(num);
			celltitle1m.setCellValue(new HSSFRichTextString(headers1.get(0)));
			celltitle1m.setCellStyle(style); // 居中
			sheet.addMergedRegion(new CellRangeAddress(3, 3, num, num + 1));

			celltitle1m = rowtitle1m.createCell(num + 2);
			celltitle1m.setCellValue(new HSSFRichTextString(headers1.get(1)));
			celltitle1m.setCellStyle(style); // 居中
			sheet.addMergedRegion(new CellRangeAddress(3, 3, num + 2, num + 2 + 5));

			HSSFCellStyle stylegsm = workbook.createCellStyle();// 表头样式
			stylegsm.cloneStyleFrom(style);
			stylegsm.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			for (int i = 0; i < num; i++) {
				celltitle1m = rowtitle1m.createCell(i);
				celltitle1m.setCellValue(new HSSFRichTextString(headers.get(i)));
				celltitle1m.setCellStyle(style); // 居中
				sheet.addMergedRegion(new CellRangeAddress(3, 4, i, i));
			}

			if (headerlength != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < headerlength; i++) {
				HSSFCell cell1 = row.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(headers.get(i)));
				cell1.setCellStyle(style);
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
							if (key.contains("trate")) {
								if (new DZFDouble(map.get(key).toString()).equals(new DZFDouble().ZERO_DBL)) {
									cell.setCellValue("--");
								} else {
									DZFDouble doublevalue = new DZFDouble(map.get(key).toString());
									doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
									cell.setCellValue(doublevalue.toString());
								}
							} else if (!fieldlist.contains(key)) {
								DZFDouble doublevalue = new DZFDouble(map.get(key).toString());
								doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
								cell.setCellValue(doublevalue.toString());
							} else {
								String value = map.get(key).toString();
								richString = new HSSFRichTextString(value);
								cell.setCellValue(richString);
							}
						} else {
							cell.setCellValue("");
						}
						if (!fieldlist.contains(key)) {
							cell.setCellStyle(st);
						} else {
							cell.setCellStyle(st1);
						}
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("公司数据统计/销售业绩统计 导出", e);
					} finally {
						// 清理资源
					}
				}
				if (i == array.size()) {
					sheet.addMergedRegion(new CellRangeAddress(i + index + 1, i + index + 1, 0, 1));
				}
			}
			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new WiseRunException(e);
			}
		} catch (Exception e) {
			log.error("公司数据统计/销售业绩统计 导出", e);
		}
		return workbook.getBytes();
	}

	/**
	 * 加盟商人员统计表导出
	 * 
	 * @param string
	 * @param heads
	 * @param headindexs
	 * @param heads1
	 * @param head1indexs
	 * @param heads2
	 * @param fieldslist
	 * @param exparray
	 * @param toClient
	 * @param string2
	 * @param fieldlist
	 * @param strnmylist 
	 * @param num
	 * @return
	 */
	public byte[] exportPersonStatisExcel(String title, List<String> heads, Integer[] headindexs, List<String> heads1,
			List<Integer> head1indexs, List<String> heads2, List<String> fieldslist, JSONArray exparray,
			OutputStream out, String string2, List<String> fieldlist, int num) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			int index = 4;
			HSSFSheet sheet = workbook.createSheet(title);
			// 行宽
			sheet.setDefaultColumnWidth((short) 15);

			// 数字类型样式
			HSSFCellStyle numsty = workbook.createCellStyle();
			numsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			numsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			numsty.setAlignment(HSSFCellStyle.ALIGN_RIGHT);

			// 字符、日期类型样式
			HSSFCellStyle strsty = workbook.createCellStyle();
			strsty.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderRight(HSSFCellStyle.BORDER_THIN);
			strsty.setBorderTop(HSSFCellStyle.BORDER_THIN);
			strsty.setAlignment(HSSFCellStyle.ALIGN_LEFT);

			// 通用样式
			HSSFCellStyle style = workbook.createCellStyle();
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

			HSSFCellStyle headstyle = workbook.createCellStyle();
			HSSFFont f = workbook.createFont();
			f.setFontHeightInPoints((short) 20);// 字号
			f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			headstyle.setFont(f);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 内容左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 内容上下居中
																		
			headstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
			headstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
																		
			int header2length = heads2.size();
			int fieldlength = fieldlist.size();

			// 大标题
			HSSFRow rowtitle = sheet.createRow(0);
			HSSFCell titlecell = rowtitle.createCell(0);
			titlecell.setCellValue(title);
			titlecell.setCellStyle(headstyle);
			Region region = new Region(0, (short) 0, 2, (short) (fieldlength - 1));// 合并前三行
			sheet.addMergedRegion(region);// 合并标题

			// 合并行标题赋值 begin##############################################
			HSSFRow rowthree = sheet.createRow(3);
			if (heads != null && heads.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < heads.size(); i++) {
					begindex = headindexs[i];
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(heads.get(i))));
					cell.setCellStyle(style);
					Region reg = new Region(3, (short) (begindex), 4, (short) (begindex));
					sheet.addMergedRegion(reg);
				}
			}
			// 合并行标题赋值 end################################################

			// 合并列 begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			HSSFRow rowfour = sheet.createRow(4);
			Region reg = null;
			if (heads1 != null && heads1.size() > 0) {
				int begindex = 0;
				for (int i = 0; i < heads1.size(); i++) {
					begindex = head1indexs.get(i);
					HSSFCell cell = rowthree.createCell(begindex);
					cell.setCellValue(new HSSFRichTextString(String.valueOf(heads1.get(i))));
					cell.setCellStyle(style);
					reg = i == 0 ? new Region(3, (short) (begindex), 3, (short) (begindex + 7))
							: new Region(3, (short) (begindex), 3, (short) (begindex + 3));
					sheet.addMergedRegion(reg);
				}
			}

			// 合并列 end&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			// 添加导出行信息
			HSSFRow row2 = null;
			if (header2length != fieldlength) {
				index++;
				row2 = sheet.createRow(index);
			}
			for (int i = 0; i < header2length; i++) {
				HSSFCell cell1 = rowfour.createCell(i);
				cell1.setCellValue(new HSSFRichTextString(String.valueOf(heads2.get(i))));
				cell1.setCellStyle(style);
			}

			// 合并单元格设置边框
			setRegionStyle(sheet, region, headstyle);
			setRegionStyle(sheet, reg, style);
			
			for (int i = 0; i < exparray.size(); i++) {
				HSSFRow row1 = sheet.createRow(i + index + 1);
				Map<String, Object> map = (Map<String, Object>) exparray.get(i);
				int count = 0;
				for (String key : fieldlist) {
					try {
						HSSFRichTextString richString;
						HSSFCell cell = row1.createCell(count);
						if (map.get(key)!= null) {
							if(key.equals("ktotal")||key.equals("lznum")||key.equals("xtotal")){
								DZFDouble value = new DZFDouble(map.get(key).toString());
								value = value.setScale(2, DZFDouble.ROUND_HALF_UP);
								cell.setCellValue(value.toString());
							}else{
								String value = map.get(key).toString();
								richString = new HSSFRichTextString(value);
								cell.setCellValue(richString);	
							}
						} else {
							richString = new HSSFRichTextString("");
							cell.setCellValue(richString);
						}
						cell.setCellStyle(strsty);
						count++;
					} catch (SecurityException e) {
						throw new WiseRunException(e);
					} catch (IllegalArgumentException e) {
						throw new WiseRunException(e);
					} catch (Exception e) {
						log.error("文件打印", e);
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
			log.error("加盟商人员统计表导出", e);
		}
		return workbook.getBytes();

	}

}