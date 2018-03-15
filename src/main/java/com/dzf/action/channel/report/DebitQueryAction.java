package com.dzf.action.channel.report;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.ExportExcel;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.report.IDebitQueryService;
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
			columnames.add("加盟商编码");
			columnkeys.add("corpcode");
			columnames.add("加盟商名称");
			columnkeys.add("corpname");
			columnames.add("加盟日期");
			columnkeys.add("chndate");
			for (int i = 3 ; i< len; i ++) {
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
			for(int i = 0 ; i< len-5; i ++){
				columnames.add("预付款");
				columnkeys.add(str[i]+"1");
				columnames.add("返点");
				columnkeys.add(str[i]+"2");
			}
			Map<String,String> bodymapping=FieldMapping.getFieldMapping(new DebitQueryVO());
			DebitQueryVO[] bodyvos =DzfTypeUtils.cast(array,bodymapping, DebitQueryVO[].class, JSONConvtoJAVA.getParserConfig());
			setIscross(DZFBoolean.TRUE);//是否横向
			LinkedList<ColumnCellAttr> columnlist = new LinkedList<>();
			int[] size=new int[columnkeys.size()];
 			for(int i=0;i<columnames.size();i++){
				ColumnCellAttr attr = new ColumnCellAttr();
				attr.setColumname(columnames.get(i));
				if(i<3){
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
		String columns =getRequest().getParameter("columns");
		columns = columns.substring(0, columns.length()-1);
		String[] split = columns.split(",");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new DebitQueryVO());
		DebitQueryVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,DebitQueryVO[].class, JSONConvtoJAVA.getParserConfig());
//		for (DebitQueryVO debitQueryVO : expVOs) {
//			if(debitQueryVO.getNdeductmny() != null ){
//				debitQueryVO.setNdeductmny(debitQueryVO.getNdeductmny().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getOutmny() != null ){
//				debitQueryVO.setOutmny(debitQueryVO.getOutmny().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getOne() != null){
//				debitQueryVO.setOne(debitQueryVO.getOne().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getTwo() != null){
//				debitQueryVO.setTwo(debitQueryVO.getTwo().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getThree() != null){
//				debitQueryVO.setThree(debitQueryVO.getThree().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getFour() != null){
//				debitQueryVO.setFour(debitQueryVO.getFour().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getFive() != null){
//				debitQueryVO.setFive(debitQueryVO.getFive().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getSix() != null){
//				debitQueryVO.setSix(debitQueryVO.getSix().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getSeven() != null){
//				debitQueryVO.setSeven(debitQueryVO.getSeven().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getEight() != null){
//				debitQueryVO.setEight(debitQueryVO.getEight().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getNine() != null){
//				debitQueryVO.setNine(debitQueryVO.getNine().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getTen() != null){
//				debitQueryVO.setTen(debitQueryVO.getTen().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getEleven() != null){
//				debitQueryVO.setEleven(debitQueryVO.getEleven().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getTwelve() != null){
//				debitQueryVO.setTwelve(debitQueryVO.getTwelve().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getThirteen() != null){
//				debitQueryVO.setThirteen(debitQueryVO.getThirteen().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getFourteen() != null){
//				debitQueryVO.setFourteen(debitQueryVO.getFourteen().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//			if(debitQueryVO.getFifteen() != null){
//				debitQueryVO.setFifteen(debitQueryVO.getFifteen().setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//		}
		HttpServletResponse response = getResponse();
		ExportExcel<DebitQueryVO> ex = new ExportExcel<DebitQueryVO>();
		Map<String, String> map = getExpFieldMap(split);
		String[] enFields = new String[map.size()];
		String[] cnFields = new String[map.size()];
		 //填充普通字段数组
		int count = 0;
		for (Entry<String, String> entry : map.entrySet()) {
			enFields[count] = entry.getKey();
			cnFields[count] = entry.getValue();
			count++;
		}
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename="+ new String(date+".xls"));
			servletOutputStream = response.getOutputStream();
			 toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			byte[] length = ex.exportExcel("加盟商扣款查询",cnFields,enFields ,Arrays.asList(expVOs), toClient);
			String srt2=new String(length,"UTF-8");
			response.addHeader("Content-Length", srt2);
		} catch (Exception e) {
			log.error("导出失败",e);
		}  finally {
			if(toClient != null){
				try {
					toClient.flush();
					toClient.close();
				} catch (IOException e) {
					log.error("导出失败",e);
				}
			}
			if(servletOutputStream != null){
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error("导出失败",e);
				}
			}
		}
	}
	
	/**
	 * 获取导出列
	 * @return
	 */
	private Map<String, String> getExpFieldMap(String[] split){
		String[] str={"one","two","three","four","five","six","seven",
			        "eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"};
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("corpcode", "加盟商编码");
		map.put("corpname", "加盟商名称");
		map.put("chndate", "加盟日期");
		map.put("outmny", "预付款余额");
		map.put("ndeductmny", "扣款合计");
		for(int i=0;i<split.length;i++){
			map.put(str[i], split[i]);
		}
		return map;
	}

}
