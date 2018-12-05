package com.dzf.action.channel.report;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.model.channel.report.DebitQueryVO;
import com.dzf.model.pub.ColumnCellAttr;
import com.dzf.model.pub.Grid;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.report.IDebitQueryService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.pub.report.ExportExcel;
import com.dzf.service.pub.report.PrintUtil;
import com.itextpdf.text.DocumentException;

/**
 * 加盟商扣款查询
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "debitquery")
public class DebitQueryAction extends PrintUtil<DebitQueryVO>{

	@Autowired
	private IDebitQueryService debitquery;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 查询datagrid的表头数据
	 */
	public void queryHeader() {
		Grid grid = new Grid();
		try {
			DebitQueryVO qvo = new DebitQueryVO();
			qvo = (DebitQueryVO) DzfTypeUtils.cast(getRequest(), qvo);
			List<DebitQueryVO> vos = debitquery.queryHeader(qvo);
			grid.setRows(vos);
			grid.setSuccess(true);
			grid.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询主表数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			DebitQueryVO qvo = new DebitQueryVO();
			qvo = (DebitQueryVO) DzfTypeUtils.cast(getRequest(), qvo);
			List<DebitQueryVO> rows = debitquery.query(qvo);
//			QueryDeCodeUtils.decKeyUtils(new String[]{"corpname"}, rows, 2);
			grid.setRows(rows);
			grid.setSuccess(true);
			grid.setMsg("查询成功!");
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_23.getValue(), "加盟商扣款查询成功", ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	
	/**
	 * 打印
	 */
	@SuppressWarnings("unchecked")
	public void print() {
		try {
			String strlist = getRequest().getParameter("strlist");
			String qj = getRequest().getParameter("qj");
			JSONArray array = (JSONArray) JSON.parseArray(strlist);
			String columns =getRequest().getParameter("columns");
			JSONArray headlist = (JSONArray) JSON.parseArray(columns);
			List<String> heads = new ArrayList<String>();
			List<String> columnames = new ArrayList<String>();
			List<String> columnkeys = new ArrayList<String>();
			int len=headlist.size();
			Map<String, String> name = null;
			columnames.add("大区");
			columnkeys.add("areaname");
			columnames.add("区总");
			columnkeys.add("username");
			columnames.add("省（市）");
			columnkeys.add("vprovname");
			columnames.add("渠道经理");
			columnkeys.add("cusername");
			columnames.add("加盟商编码");
			columnkeys.add("corpcode");
			columnames.add("加盟商名称");
			columnkeys.add("corpname");
			columnames.add("加盟商类型");
			columnkeys.add("channeltype");
			columnames.add("加盟日期");
			columnkeys.add("chndate");
			for (int i = 8 ; i< len; i ++) {
				 name=(Map<String, String>) headlist.get(i);
				 columnames.add(name.get("title"));
			}
			columnames.add("预付款");
			columnkeys.add("outymny");
			columnames.add("返点");
			columnkeys.add("outfmny");
			columnames.add("预付款");
			columnkeys.add("ndeductmny");
			columnames.add("返点");
			columnkeys.add("ndedrebamny");
			String[] str={"one","two","three","four","five","six","seven",
			        "eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"};
			for(int i = 0 ; i< len-10; i ++){
				columnames.add("预付款");
				columnkeys.add(str[i]+"1");
				columnames.add("返点");
				columnkeys.add(str[i]+"2");
			}
			Map<String,String> bodymapping=FieldMapping.getFieldMapping(new DebitQueryVO());
			DebitQueryVO[] bodyvos =DzfTypeUtils.cast(array,bodymapping, DebitQueryVO[].class, JSONConvtoJAVA.getParserConfig());
			for (DebitQueryVO debitQueryVO : bodyvos) {
				if(!StringUtil.isEmpty(debitQueryVO.getChanneltype())){
					if(debitQueryVO.getChanneltype().equals("1")){
						debitQueryVO.setChanneltype("普通加盟商");
					}else{
						debitQueryVO.setChanneltype("金牌加盟商");
					}
				}
			}
			setIscross(DZFBoolean.TRUE);//是否横向
			LinkedList<ColumnCellAttr> columnlist = new LinkedList<>();
			int[] size=new int[columnkeys.size()];
 			for(int i=0;i<columnames.size();i++){
				ColumnCellAttr attr = new ColumnCellAttr();
				attr.setColumname(columnames.get(i));
				if(i<8){
					attr.setRowspan(2);
					size[i]=3;
				}else if(i<len){
					attr.setColspan(2);
					size[i]=1;
				}else if(i<columnkeys.size()){
					size[i]=1;
				}
				columnlist.add(attr);
			}
 			bodyvos[0].setDbegindate(qj);
			printGroup(new HashMap<String, List<SuperVO>>(),bodyvos, "加盟商扣款查询", columnlist, columnkeys.toArray(new String[columnkeys.size()]),size,70);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_23.getValue(), "加盟商扣款查询表打印", ISysConstants.SYS_3);
		} catch (DocumentException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	
	/**
	 * Excel导出方法
	 */
	public void exportExcel(){
		String strlist =getRequest().getParameter("strlist");
		String columns =getRequest().getParameter("columns");//最底下一行
		String dateColumns =getRequest().getParameter("dateColumns");//最上面一行
		JSONArray array = (JSONArray) JSON.parseArray(strlist);
		JSONArray headlist = (JSONArray) JSON.parseArray(columns);
		JSONArray headlist1 = (JSONArray) JSON.parseArray(dateColumns);
		List<String> heads = new ArrayList<String>();
		List<String> heads1 = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		Map<String, String> name = null;
		if(strlist==null){
			return;
		}
		for (int i = 0 ; i< headlist1.size(); i ++) {
			 name=(Map<String, String>) headlist1.get(i);
			 if(i<8){
				 fieldslist.add(name.get("field"));
				 heads.add(name.get("title"));
			 }else{
				 heads1.add(name.get("title"));
			 }
		}
		for (int i = 0 ; i< headlist.size(); i ++) {
			 name=(Map<String, String>) headlist.get(i);
			 heads.add(name.get("title"));
			 fieldslist.add(name.get("field"));
		}
		ExportExcel<DebitQueryVO> ex =new ExportExcel();
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			HttpServletResponse response = getResponse();
			response.reset();
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(date+".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			List<String> fieldlist = new ArrayList<String>();
			fieldlist.add("aname");
			fieldlist.add("uname");
			fieldlist.add("provname");
			fieldlist.add("cuname");
			fieldlist.add("ccode");
			fieldlist.add("cname");
			fieldlist.add("chtype");
			fieldlist.add("chndate");
			byte[] length = ex.exportDebitExcel("加盟商扣款查询",heads,heads1,fieldslist ,array,toClient,"",fieldlist);
			String srt2=new String(length,"UTF-8");
			response.addHeader("Content-Length", srt2);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_23.getValue(), "加盟商扣款查询表导出", ISysConstants.SYS_3);
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if(toClient != null){
				try {
					toClient.flush();
					toClient.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
			if(servletOutputStream != null){
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}
}
