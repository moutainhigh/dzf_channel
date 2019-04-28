package com.dzf.action.channel.dealmanage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;

import com.dzf.action.pub.BaseAction;
import com.dzf.dao.jdbc.framework.util.InOutUtil;
import com.dzf.model.channel.stock.StockOutBVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.ColumnCellAttr;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDouble;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 出库单明细打印
 * 
 */
@SuppressWarnings("serial")
public class StockOutPrint<T> extends BaseAction<T> {

	private static final Logger log = Logger.getLogger(StockOutPrint.class);


	public String getSpace(int num) {
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sf.append(" ");
		}
		return sf.toString();
	}

	public void printFileTrans(StockOutVO headvo) throws DZFWarpException, DocumentException, IOException {
        float marginLeft = 47f; // 定义边距
        float marginTop = 36f;
        // float originalTop = marginTop;
        BaseFont bf = BaseFont.createFont(IGlobalConstants.FONTPATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFonts = new Font(bf, 18, Font.NORMAL);
        Font tableHeadFonts = new Font(bf, 13, Font.NORMAL);
        Document document = new Document(PageSize.A4, marginLeft, 20, marginTop, 0);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        document.open();
        PdfContentByte canvas = writer.getDirectContent();
        // int tableCount = 1;

        try {
			if (headvo != null) {
				String content = "";
				Paragraph title = new Paragraph();
				title.setAlignment(Element.ALIGN_CENTER);
				title.add(new Chunk("出库单", titleFonts));
				Phrase billcode = new Phrase();
				billcode.add(new Chunk("编码：", tableHeadFonts));
				content = headvo.getVbillcode();
				billcode.add(new Chunk(content, tableHeadFonts));
				Phrase corpname = new Phrase();
				corpname.add(new Chunk("  加盟商：", tableHeadFonts));
				corpname.add(new Chunk(headvo.getCorpname(), tableHeadFonts));
				Phrase stocktime = new Phrase();
				stocktime.add(new Chunk("  出库时间：", tableHeadFonts));
				content = headvo.getDconfirmtime() == null ? "" : headvo.getDconfirmtime().toString();
				stocktime.add(new Chunk(content, tableHeadFonts));
				
				document.add(Chunk.NEWLINE);
				document.add(title);
				document.add(Chunk.NEWLINE);
				document.add(billcode);
				document.add(corpname);
				document.add(stocktime);
				
				StockOutBVO[] children =(StockOutBVO[]) headvo.getChildren();
				document.add(getTable(children));
				
				Phrase person = new Phrase();
				person.add(new Chunk("收件人：", tableHeadFonts));
				content = headvo.getVreceivername() == null ? "" : headvo.getVreceivername().toString();
				person.add(new Chunk( content, tableHeadFonts));
				Phrase phone = new Phrase();
				phone.add(new Chunk("      联系方式：", tableHeadFonts));
				content = headvo.getPhone() == null ? "" : headvo.getPhone().toString();
				phone.add(new Chunk(content, tableHeadFonts));
				Phrase address = new Phrase();
				address.add(new Chunk("地址：", tableHeadFonts));
				content = headvo.getVreceiveaddress() == null ? "" : headvo.getVreceiveaddress().toString();
				address.add(new Chunk(content, tableHeadFonts));
				document.add(Chunk.NEWLINE);
				document.add(person);
				document.add(phone);
				document.add(Chunk.NEWLINE);
				document.add(address);
			}
        } catch (Exception e) {
            log.error("打印出错", e);
            document.add(new Chunk("打印失败", titleFonts));
        } finally {
            document.close();
        }
        ServletOutputStream out = null;
        try {
            getResponse().setContentType("application/pdf");
            getResponse().setContentLength(buffer.size());
            out = getResponse().getOutputStream();
            buffer.writeTo(out);
        } catch (Exception exp) {
            throw new WiseRunException(exp);
        } finally {
            InOutUtil.close(out, "出库单明细打印关闭输入流");
            InOutUtil.close(buffer, "出库单明细打印关闭输出流");
        }

    }

	private PdfPTable getTable(StockOutBVO[] grandson) throws DocumentException, IOException {
		PdfPTable table = new PdfPTable(6);
		table.setHeaderRows(1);
		table.setSpacingBefore(10);
		table.setWidthPercentage(100);
		try {
			table.setWidths(new int[]{3,3,3,3,3,3});
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		List<ColumnCellAttr> listattr = new ArrayList<ColumnCellAttr>();
		ColumnCellAttr attr = new ColumnCellAttr();
		attr.setColumname("订单号");
		listattr.add(attr);
		attr = new ColumnCellAttr();
		attr.setColumname("商品名称");
		listattr.add(attr);
		attr = new ColumnCellAttr();
		attr.setColumname("规格");
		listattr.add(attr);
		attr = new ColumnCellAttr();
		attr.setColumname("型号");
		listattr.add(attr);
		attr = new ColumnCellAttr();
		attr.setColumname("数量");
		listattr.add(attr);
		attr = new ColumnCellAttr();
		attr.setColumname("金额");
		listattr.add(attr);
		
		BaseFont bf = BaseFont.createFont("/data1/webApp/font/SIMKAI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// C:/windows/font/simfang.ttf
		Font tableHeadFounts = new Font(bf, 9, Font.BOLD);
		Font tableBodyFounts = new Font(bf,9, Font.NORMAL);
		float totalMnyHight = 22f;
		addTabHead(tableHeadFounts,table,totalMnyHight,listattr);
		
		try {
			PdfPCell cell = null;
			for (StockOutBVO stockOutBVO : grandson) {
				cell = new PdfPCell(new Phrase(stockOutBVO.getVbillcode(), tableBodyFounts));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setFixedHeight(totalMnyHight);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(stockOutBVO.getVgoodsname(), tableBodyFounts));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setFixedHeight(totalMnyHight);
				table.addCell(cell);
				
				if(StringUtil.isEmpty(stockOutBVO.getInvspec())){
					cell = new PdfPCell(new Phrase("", tableBodyFounts));
				}else{
					cell = new PdfPCell(new Phrase(stockOutBVO.getInvspec(), tableBodyFounts));
				}
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setFixedHeight(totalMnyHight);
				table.addCell(cell);
				
				if(StringUtil.isEmpty(stockOutBVO.getInvtype())){
					cell = new PdfPCell(new Phrase("", tableBodyFounts));
				}else{
					cell = new PdfPCell(new Phrase(stockOutBVO.getInvtype(), tableBodyFounts));
				}
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setFixedHeight(totalMnyHight);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(String.valueOf(stockOutBVO.getNnum()), tableBodyFounts));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setFixedHeight(totalMnyHight);
				table.addCell(cell);
				
				DZFDouble doublevalue =new DZFDouble(stockOutBVO.getNmny());
				doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
				cell = new PdfPCell(new Phrase(doublevalue.toString(), tableBodyFounts));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setFixedHeight(totalMnyHight);
				table.addCell(cell);
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
}
