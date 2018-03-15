package com.dzf.service.pub.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;

import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.pub.ColumnCellAttr;
import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.SuperVO;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.ResourceUtils;
import com.dzf.pub.util.ResourceUtils.ICloseAction;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 普通打印
 * 
 * @author zongy
 * 
 */

public class PrintUtil<T> extends BaseAction<T> {
	
	private static final long serialVersionUID = 1L;
	
	private DZFBoolean iscross= DZFBoolean.FALSE;//是否横向
	
	public DZFBoolean getIscross() {
		return iscross;
	}

	public void setIscross(DZFBoolean iscross) {
		this.iscross = iscross;
	}
	
	/**
	 * 打印
	 * @param array 前台获取JSON数组
	 * @param titlename 
	 * @param columns  字段名称
	 * @param fields 字段编码
	 * @param widths
	 * @param pagecount
	 * @param list  字符类型的值
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void printMultiColumn(JSONArray array ,  String titlename, List<String> columns,String[] fields,
			int[] widths, Integer pagecount, List<String> list, Map<String, String> pmap) throws DocumentException, IOException {
		BaseFont bf = BaseFont.createFont(IGlobalConstants.FONTPATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// C:/windows/fonts/simfang.ttf
		BaseFont bf_Bold = BaseFont.createFont(IGlobalConstants.FONTPATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);//IGlobalConstants.FONTPATH
		Font titleFonts = new Font(bf_Bold, 20, Font.BOLD);
//		int font =Integer.parseInt(pmap.get("font"));
		int font= 9;
		Font tableBodyFounts = new Font(bf,font, Font.NORMAL);
		Font tableHeadFounts = new Font(bf,font, Font.BOLD);
		float totalAmountHight = 13f;
		float totalMnyHight ;
			totalMnyHight = 22f;//设置双倍行距解决科目显示不完整问题
		int separatorLine = 1;
		int pageVchNum = 1;
//		float leftsize =(float) (Float.parseFloat(pmap.get("left"))*2.83);
//		float topsize =(float) (Float.parseFloat(pmap.get("top"))*2.83);
		float leftsize =(float) (15);
		float topsize =(float) (20);
		Document document = null;
		Rectangle pageSize=null;
		if(iscross!=null && iscross.booleanValue()){
			pageSize = new Rectangle((PageSize.A4.getHeight()), (PageSize.A4.getWidth()));
			document = new Document(pageSize,leftsize,15,topsize,10);
		}else{
			pageSize=PageSize.A4;
			document = new Document(pageSize,leftsize, 15,topsize, 5);
		}
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, buffer);
		document.open();
		ServletOutputStream out = null;
		int tableCount = 1;
		try {
				Map<String, Object> para = new TreeMap<String, Object>();
				para.put("titleFonts", titleFonts);
				para.put("tableHeadFounts", tableHeadFounts);
				para.put("tableBodyFounts", tableBodyFounts);
				para.put("totalAmountHight", totalAmountHight);
				para.put("totalMnyHight", totalMnyHight);
				Paragraph title = new Paragraph();
				title.add(new Chunk(titlename, titleFonts));
				title.setAlignment(Element.ALIGN_CENTER);
				document.add(title);
				//副标题  注释
				if(pmap != null && !pmap.isEmpty()){
					Paragraph head = new Paragraph();
					if (titlename.equals("付款单余额明细")) {
						head.add(new Chunk(getSpace(3) + "查询：" + pmap.get("qrydate") + getSpace(6), tableHeadFounts));
						head.add(new Chunk(getSpace(3) + "加盟商：" + pmap.get("corpnm") + getSpace(6), tableHeadFounts));
						head.add(new Chunk(getSpace(3) + "付款类型：" + pmap.get("ptypenm") + getSpace(6), tableHeadFounts));
					}else if(titlename.equals("合同金额明细")){
						head.add(new Chunk(getSpace(3) + "查询：" + pmap.get("qrydate") + getSpace(6), tableHeadFounts));
						head.add(new Chunk(getSpace(3) + "加盟商：" + pmap.get("corpnm") + getSpace(6), tableHeadFounts));
					}
					head.setAlignment(Element.ALIGN_LEFT);
					document.add(head);
				}
				int linenum=0;
				if(iscross!=null && iscross.booleanValue()){
					if(columns.size()!=fields.length){
						linenum=21;
					}else{
						linenum=22;
					}
				}else{
					if(columns.size()!=fields.length){
						linenum=32;
					}else{
						linenum=33;
					}
				}
				JSONArray array1=new JSONArray();
				for (int i = 0; i < array.size(); i++) {
					array1.add(array.get(i));
 					if((i+1) % linenum== 0 && i != 0) {
						PdfPTable table =getTableBody(tableBodyFounts, array1, totalMnyHight, columns, fields, titlename ,widths, para, list);
						document.add(table);
						array1.clear();
						if(i<array.size()-1){
							document.newPage();
							document.add(title); //输入标题
						}
					}
				}
				PdfPTable table= getTableBody(tableBodyFounts, array1, totalMnyHight, columns,fields, titlename, widths, para, list);
				document.add(table);
				
				document.close();
				getResponse().setContentType("application/pdf");
				getResponse().setCharacterEncoding("utf-8");
				getResponse().setContentLength(buffer.size());
				ResourceUtils.doSOStreamAExec(getResponse(), new ICloseAction<ServletOutputStream>(){
					@Override
					public Object doAction(ServletOutputStream out) throws Exception {
						buffer.writeTo(out);
						return null;
					}
					
				});
//				out = getResponse().getOutputStream();
//				buffer.writeTo(out);
			}catch (Exception e) {
				throw new WiseRunException(e);
			} finally {
				if(out != null){
					out.flush();
					out.close();
				}
				if(document != null){
					document.close();					
				}
			}
	}
	
	/**
	 * 追加空格
	 * @param num
	 * @return
	 */
	public String getSpace(int num) {
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sf.append(" ");
		}
		return sf.toString();
	}
	
	/**
	 * 获取打印的值
	 * @param fonts
	 * @param array
	 * @param totalMnyHight
	 * @param columns
	 * @param fields
	 * @param tilename
	 * @param widths
	 * @param para
	 * @param list
	 * @return
	 */
	public PdfPTable getTableBody(Font fonts,JSONArray array, float totalMnyHight, List<String> columns,String[] fields,String tilename,
			int[] widths, Map<String, Object> para, List<String> list){
		List<ColumnCellAttr> listattr = new ArrayList<ColumnCellAttr>();
		int columnslength=columns.size();
		int fieldslength=fields.length;
		PdfPTable table = new PdfPTable(fieldslength);
		if(columnslength!= fieldslength){
			table.setHeaderRows(2);
		}else{
			table.setHeaderRows(1);
		}
		table.setSpacingBefore(10);
		table.setWidthPercentage(100);
		try {
			table.setWidths(widths);
		} catch (DocumentException e1) {
			throw new WiseRunException(e1);
		}
		for(int i =0;i<columnslength;i++){
			ColumnCellAttr attr = new ColumnCellAttr();
			attr.setColumname(columns.get(i));
			if(columnslength>fieldslength){
				if(i<=6 ){
					attr.setRowspan(2);
				}else if(i == 7){
					attr.setColspan(fieldslength-7);
				}
			}
			listattr.add(attr);
		}
		addTabHead((Font)para.get("tableHeadFounts"), table,totalMnyHight, listattr);
		try {
			PdfPCell cell = null;
			for (int i = 0; i < array.size(); i++) {
				Map<String, Object> map=new HashMap<String, Object>();
				map = (Map<String, Object>) array.get(i);
				for (String key : fields) {
//					if(map.get(key)!=null&&!map.get(key).toString().equals("0")){
					if(map.get(key)!=null){
						if(!list.contains(key)){
							DZFDouble doublevalue =new DZFDouble(map.get(key).toString());
							doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
							cell = new PdfPCell(new Phrase(doublevalue.toString(), fonts));
						}else{
							cell = new PdfPCell(new Phrase(String.valueOf(map.get(key)), fonts));
						}
					}else{
						cell = new PdfPCell(new Phrase("", fonts));
					}
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					if(list.contains(key)){
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					}else{
						cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					}
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);	
				}
			}
		}
		catch (Exception e) {
			throw new WiseRunException(e);
		}
		return table;
	}	
	
	private void addTabHead(Font fonts10_bold, PdfPTable table,float totalMnyHight, List<ColumnCellAttr> listattr) {
		PdfPCell cell;
		int count =0;
		for (ColumnCellAttr columnsname : listattr) {
			cell = new PdfPCell(new Paragraph(columnsname.getColumname(), fonts10_bold));
			if(listattr.get(count).getColspan()!=null && listattr.get(count).getColspan().intValue()>0){//合并
				cell.setFixedHeight(totalMnyHight);
				cell.setColspan(listattr.get(count).getColspan());
			}
			if(listattr.get(count).getRowspan()!=null && listattr.get(count).getRowspan().intValue()>0){//空行
				cell.setPadding(0);
				cell.setFixedHeight(totalMnyHight*listattr.get(count).getRowspan());
				cell.setRowspan(listattr.get(count).getRowspan());
			}
			cell.setFixedHeight(totalMnyHight);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			count++;
		}
	}
	
	// A4宽
	public void printGroup(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String titlename, List<ColumnCellAttr> listattr, String[] columns,
			int[] widths, Integer pagecount) throws DocumentException, IOException {
		List<String> pklist = new ArrayList<String>();
		String[] idsArray = null;
		// String realPath = getRequest().getServletContext().getRealPath("/");
		// // 获取真实路径
		BaseFont bf = BaseFont.createFont("/data1/webApp/font/SIMKAI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// C:/windows/font/simfang.ttf
		BaseFont bf_Bold = BaseFont.createFont("/data1/webApp/font/SIMKAI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);/// data1/webApp/font/SIMKAI.TTF
		Font titleFonts = new Font(bf_Bold, 20, Font.BOLD);
		Font tableBodyFounts = new Font(bf, 9, Font.NORMAL);
		Font tableHeadFounts = new Font(bf, 9, Font.BOLD);
		float totalAmountHight = 15f;
		float totalMnyHight = 22f;
		// int separatorLine = 1;
		// int pageVchNum = 1;
		Document document = null;
		if (iscross != null && iscross.booleanValue()) {
			Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
			document = new Document(pageSize, 20, 20, 20, 10);
		} else {
			document = new Document(PageSize.A4);
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PdfWriter writer = PdfWriter.getInstance(document, buffer);
		document.open();
		int tableCount = 1;
		ServletOutputStream out = null;
		try {
			/* SuperVO[] bvoArray = zzvos; */
			Map<String, Object> para = new TreeMap<String, Object>();
			para.put("titleFonts", titleFonts);
			para.put("tableHeadFounts", tableHeadFounts);
			para.put("tableBodyFounts", tableBodyFounts);
			para.put("totalAmountHight", totalAmountHight);
			para.put("totalMnyHight", totalMnyHight);
			if (zzvos != null && zzvos.length > 0) { // ---- 不分页打印----
				// 插入头部信息
				Paragraph title = new Paragraph();
				title.add(new Chunk(titlename, titleFonts));
				title.setAlignment(Element.ALIGN_CENTER);
				// title.setLeading(45f); //设置间距
				Paragraph head = new Paragraph();
				Chunk corp = null;
				Chunk cdate = null;
				Chunk bsh = null;
				Chunk pzqj = null;
				if (titlename.equals("加盟商扣款查询")) {
					cdate = new Chunk(getSpace(3) + "日期：" + zzvos[0].getAttributeValue("dbegindate") + getSpace(9), tableHeadFounts);
					head.add(cdate);
				}
				head.setAlignment(Element.ALIGN_LEFT);
				document.add(title);
				document.add(Chunk.NEWLINE);
				document.add(head);
				List<SuperVO> zzvosList = new ArrayList<SuperVO>();
				for (int i = 0; i < zzvos.length; i++) {
					zzvosList.add(zzvos[i]);
					if ((i + 1) % 21 == 0 && i != 0) {
						SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
						PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, listattr, columns, widths, pagecount);
						document.add(table);
						zzvosList.clear();
						if (i < zzvos.length - 1) {
							document.newPage();
							document.add(title); // 输入标题
							document.add(Chunk.NEWLINE);
							document.add(head);
						}
					}
				}
				SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
				PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, listattr, columns, widths, pagecount);
				document.add(table);
			}
			document.close();
			getResponse().setContentType("application/pdf");
			getResponse().setCharacterEncoding("utf-8");
			getResponse().setContentLength(buffer.size());
			out = getResponse().getOutputStream();
			buffer.writeTo(out);
		} catch (Exception e) {
			throw new WiseRunException(e);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
			if (document != null) {
				document.close();
			}
		}

	}
	
	private PdfPTable addTableByTzpzBvo(SuperVO[] bvoArray, int startIndex, Map<String, Object> para, List<ColumnCellAttr> listattr, String[] columns,
			int[] withs, Integer pagecount) throws DocumentException {
		PdfPTable table = new PdfPTable(columns.length);
		if (columns.length != listattr.size()) {
			table.setHeaderRows(2);
		} else {
			table.setHeaderRows(1);
		}
		table.setSpacingBefore(5);
		table.setWidthPercentage(100);
		table.setWidths(withs);
		// 表头
		addTabHead((Font) para.get("tableHeadFounts"), table, (float) para.get("totalMnyHight"), listattr);
		int line = 0;
		for (int j = startIndex; j < bvoArray.length; j++, line++) { // 表体
			SuperVO bvo = bvoArray[j];
			addTableMny((Font) para.get("tableBodyFounts"), table, bvo, (float) para.get("totalMnyHight"), columns);
		}
		return table;
	}
	
	private void addTableMny(Font fonts, PdfPTable table, SuperVO bvo, float totalMnyHight, String[] columns) {
		try {
			PdfPCell cell;
			for (String key : columns) {
				if (bvo.getClass().getDeclaredField(key).getType() == DZFDouble.class) {
					cell = new PdfPCell(
							new Phrase(
									String.format("%,.2f",
											bvo.getAttributeValue(key) == null ? 0.00 : ((DZFDouble) bvo.getAttributeValue(key)).doubleValue()),
									fonts));
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					table.addCell(cell);
				} else if (bvo.getClass().getDeclaredField(key).getType() == DZFDate.class) {
					if (bvo.getAttributeValue(key) == null) {
						cell = new PdfPCell(new Phrase("", fonts));
					} else {
						cell = new PdfPCell(new Phrase(((DZFDate) bvo.getAttributeValue(key)).toString(), fonts));
					}
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);// getDeclaredField可获取所有定义变量，而getField只能获取public的属性变量
				} else if (bvo.getClass().getDeclaredField(key).getType() == Integer.class) {
					if (bvo.getAttributeValue(key) != null) {
						cell = new PdfPCell(new Phrase((bvo.getAttributeValue(key)).toString(), fonts));
					} else {
						cell = new PdfPCell(new Phrase("", fonts));
					}
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);
				} else if (bvo.getClass().getDeclaredField(key).getType() == DZFBoolean.class) {
					if (bvo.getAttributeValue(key) != null) {
						if (bvo.getAttributeValue(key).toString().equals("Y")) {// 通过判断值为Y或N来输出“是”“否”
							cell = new PdfPCell(new Phrase("是", fonts));
						} else {
							cell = new PdfPCell(new Phrase("否", fonts));
						}

					} else {
						cell = new PdfPCell(new Phrase("否", fonts));
					}
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);
				} else {
					cell = new PdfPCell(new Phrase((String) bvo.getAttributeValue(key), fonts));
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);
				}
			}
		} catch (NoSuchFieldException e) {
			throw new WiseRunException(e);
		} catch (SecurityException e) {
			throw new WiseRunException(e);
		}
	}
	
}
