package com.dzf.action.channel.dealmanage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
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
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.channel.dealmanage.IStockOutInService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.pub.report.ExportExcel;

@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/dealmanage")
@Action(value = "stockoutin")
public class StockOutInAction extends BaseAction<StockOutInMVO>{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IStockOutInService stockoutin;

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			StockOutInMVO qvo = new StockOutInMVO();
			qvo = (StockOutInMVO) DzfTypeUtils.cast(getRequest(), qvo);
			List<StockOutInMVO> list = stockoutin.query(qvo);
			grid.setRows(list);
			grid.setTotal((long)list.size());
			grid.setMsg("查询成功!");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 出入库明细表导出
	 */
	public void exportAuditExcel(){
		String strlist = getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		String columns = getRequest().getParameter("columns");
		JSONArray headlist = (JSONArray) JSON.parseArray(columns);
		List<String> heads = new ArrayList<String>();
		List<String> heads1 = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		Map<String, String> name = null;
		List<String> fieldlist = new ArrayList<String>();
		int num = 7;
		fieldlist.add("gcode");
		fieldlist.add("gname");
		fieldlist.add("spec");
		fieldlist.add("type");
		fieldlist.add("contime");
		fieldlist.add("itype");
		fieldlist.add("vcode");
		fieldlist.add("numin");
		fieldlist.add("pricein");
		fieldlist.add("moneyin");
		fieldlist.add("numout");
		fieldlist.add("priceout");
		fieldlist.add("moneyout");
		fieldlist.add("numb");
		fieldlist.add("priceb");
		fieldlist.add("moneyb");
		for (int i = 0; i < headlist.size(); i++) {
			name = (Map<String, String>) headlist.get(i);
			if (i >= num) {
				heads.add("数量");
				heads.add("单价");
				heads.add("金额");
				heads1.add(name.get("title"));
			} else {
				heads.add(name.get("title"));
				fieldslist.add(name.get("field"));
			}
		}
		
		ExportExcel<StockOutInMVO> ex = new ExportExcel<StockOutInMVO>();
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			HttpServletResponse response = getResponse();
			response.reset();
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.exportOutInExcel("出入库明细表", qj,heads, heads1, fieldslist, exparray, toClient, "", fieldlist,
					num);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_7.getValue(), "导出出入库明细表", ISysConstants.SYS_3);
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if (toClient != null) {
				try {
					toClient.flush();
					toClient.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
			if (servletOutputStream != null) {
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}
	
	
	
	/**
	 * 查询商品下拉
	 */
	public void queryComboBox() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			List<GoodsBoxVO> list = stockoutin.queryComboBox();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<GoodsBoxVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
}
