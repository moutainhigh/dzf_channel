package com.dzf.action.channel.report;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.channel.ChnPayBalanceAction;
import com.dzf.action.channel.expfield.ManagerExcelField;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.report.IManagerService;
import com.dzf.service.pub.report.ExportUtil;
import com.dzf.service.pub.report.PrintUtil;
import com.itextpdf.text.DocumentException;

/**
 * 加盟商管理
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "manager")
public class ManagerAction extends PrintUtil<ManagerVO>{

	@Autowired
	private IManagerService manager;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 查询主表数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			ManagerVO qvo = new ManagerVO();
			Integer type = Integer.parseInt(getRequest().getParameter("type"));
			qvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setUserid(getLoginUserid());
			List<ManagerVO> vos = manager.query(qvo,type);
			if(vos==null||vos.size()==0){
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setMsg("查询数据为空!");
			}else{
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 明细查询方法
	 */
	public void queryDetail() {
		Grid grid = new Grid();
		try {
			ManagerVO paramvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), new ManagerVO());
			List<ManagerVO> clist = manager.queryDetail(paramvo);
			grid.setRows(clist);
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询渠道经理（下拉选项）  2为区域总经理 3为加盟商总经理
	 */
	public void queryManager() {
		Grid grid = new Grid();
		try {
			Integer type = Integer.parseInt(getRequest().getParameter("type"));
			List<ComboBoxVO> vos = manager.queryManager(type,getLoginUserid());
			if(vos==null||vos.size()==0){
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setMsg("查询数据为空!");
			}else{
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询大区（下拉选项）
	 */
	public void queryArea() {
		Grid grid = new Grid();
		try {
			List<ComboBoxVO> vos = manager.queryArea();
			if(vos==null||vos.size()==0){
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setMsg("查询数据为空!");
			}else{
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * Excel导出方法  1渠道；2区域；3总
	 */
	public void exportExcel(){
		Integer type = Integer.parseInt(getRequest().getParameter("type"));
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new ManagerVO());
		ManagerVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,ManagerVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<ManagerVO> ex = new Excelexport2003<>();
		ManagerExcelField fields = new ManagerExcelField(type);
		fields.setVos(expVOs);
		fields.setQj(qj);
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
			ex.exportExcel(fields, toClient);
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
	 * 明细打印
	 */
	public void onDetPrint() {
		try{
			String strlist =getRequest().getParameter("strlist");
			String columns =getRequest().getParameter("columns");
			String qrydate =getRequest().getParameter("qrydate");
			String corpnm =getRequest().getParameter("corpnm");
			JSONArray array = (JSONArray) JSON.parseArray(strlist);
			JSONArray headlist = (JSONArray) JSON.parseArray(columns);
			List<String> heads = new ArrayList<String>();
			List<String> fieldslist = new ArrayList<String>();
			Map<String, String> pmap = new HashMap<String, String>();
			pmap.put("qrydate", qrydate);
			pmap.put("corpnm", corpnm);
			Map<String, String> name = null;
			int[] widths =new  int[]{};
			if(strlist==null){
				return;
			}
			for (int i = 0 ; i< headlist.size(); i ++) {
				 name=(Map<String, String>) headlist.get(i);
				 heads.add(name.get("title"));
				 fieldslist.add(name.get("field"));
				 widths =ArrayUtils.addAll(widths, new int[] {3}); 
			}
			//字符类型字段(取界面元素id)
			List<String> list = new ArrayList<String>();
			list.add("edate");
			list.add("num");
			String[] fields= (String[]) fieldslist.toArray(new String[fieldslist.size()]);
			PrintUtil<ManagerVO> util = new PrintUtil<ManagerVO>();
			util.setIscross(DZFBoolean.TRUE);
			util.printMultiColumn(array, "合同金额明细", heads, fields, widths, 20, list, pmap);
		}catch(DocumentException e){
			throw new WiseRunException(e);
		}catch(IOException e){
			throw new WiseRunException(e);
		}
	}
	
	/**
	 * 明细导出
	 */
	public void onDetExport(){
		String strlist =getRequest().getParameter("strlist");
		String columns =getRequest().getParameter("columns");
		String qrydate =getRequest().getParameter("qrydate");
		String corpnm =getRequest().getParameter("corpnm");
		JSONArray array = (JSONArray) JSON.parseArray(strlist);
		JSONArray headlist = (JSONArray) JSON.parseArray(columns);
		List<String> heads = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		Map<String, String> pmap = new HashMap<String, String>();
		pmap.put("查询", qrydate);
		pmap.put("加盟商", corpnm);
		//字符类型字段(取界面元素id)
		List<String> stringlist = new ArrayList<String>();
		stringlist.add("edate");
		Map<String, String> name = null;
		if(strlist == null){
			return;
		}
		for (int i = 0 ; i< headlist.size(); i ++) {
			 name=(Map<String, String>) headlist.get(i);
			 heads.add(name.get("title"));
			 fieldslist.add(name.get("field"));
		}
		ExportUtil<ChnPayBalanceAction> ex =new ExportUtil<ChnPayBalanceAction>();
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
			byte[] length = ex.exportExcel("合同金额明细",heads,fieldslist ,array, toClient,"",stringlist,pmap);
			String srt2=new String(length,"UTF-8");
			response.addHeader("Content-Length", srt2);
		} catch (IOException e) {
			log.error(e);
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
