package com.dzf.action.channel.manager;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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
import com.dzf.model.channel.manager.ManagerVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.ExportExcel;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.manager.IManagerService;
import com.dzf.service.pub.report.PrintUtil;

/**
 * 加盟商管理
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/mana")
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
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new ManagerVO());
		ManagerVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,ManagerVO[].class, JSONConvtoJAVA.getParserConfig());
		ArrayList<ManagerVO> explist = new ArrayList<ManagerVO>();
		for(ManagerVO vo : expVOs){
			explist.add(vo);
		}
		HttpServletResponse response = getResponse();
		ExportExcel<ManagerVO> ex = new ExportExcel<ManagerVO>();
		Map<String, String> map = getExpFieldMap(type);
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
			byte[] length=null;
			if(type==1){
				length = ex.exportExcel("渠道经理表",cnFields,enFields ,explist, toClient);
			}else if (type==2){
				length = ex.exportExcel("区域总经理表",cnFields,enFields ,explist, toClient);
			}else{
				length = ex.exportExcel("加盟商总经理表",cnFields,enFields ,explist, toClient);
			}
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
	private Map<String, String> getExpFieldMap(Integer type){
		Map<String, String> map = new LinkedHashMap<String, String>();
		if(type==3){
			map.put("areaname", "大区");
			map.put("username", "区总");
		}
		if(type==3||type==2){
			map.put("vprovname", "省（市）");
			map.put("cusername", "渠道经理");
		}
		map.put("corpname", "加盟商");
		map.put("bondmny", "保证金");
		map.put("predeposit", "预存款");
		map.put("num", "提单量");
		map.put("ntotalmny", "合同总金额");
		map.put("ndeductmny", "扣款金额");
		map.put("outmny", "预存款余额");
		return map;
	}

}
