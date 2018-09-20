package com.dzf.action.channel.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IButtonName;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.report.ICustNumMoneyRep;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.pub.report.ExportExcel;
import com.dzf.service.pub.report.PrintUtil;

/**
 * 业绩统计-续费统计
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "renewachieverep")
public class RenewAchieveRepAction extends PrintUtil<CustNumMoneyRepVO> {

	private static final long serialVersionUID = 7071363023455589093L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private ICustNumMoneyRep custServ;

	@Autowired
	private IPubService pubService;

	/**
	 * 查询
	 */
	public void queryRenew() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if (paramvo != null) {
				paramvo.setUser_name(getLoginUserid());
			}
			if (paramvo != null && StringUtil.isEmpty(paramvo.getPk_corp())) {
				paramvo.setPk_corp(getLogincorppk());
			}
			List<CustNumMoneyRepVO> list = custServ.queryRenew(paramvo);
			int page = paramvo == null ? 1 : paramvo.getPage();
			int rows = paramvo == null ? 10000 : paramvo.getRows();
			int len = list == null ? 0 : list.size();
			if (len > 0) {
				grid.setTotal((long) (len));
				grid.setRows(Arrays.asList(QueryUtil.getPagedVOs(list.toArray(new CustNumMoneyRepVO[0]), page, rows)));
				grid.setSuccess(true);
				grid.setMsg("查询成功");
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_7.getValue(), "业绩续费统计查询成功", ISysConstants.SYS_3);
			} else {
				grid.setTotal(Long.valueOf(0));
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询结果为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * Excel导出方法
	 */
	public void exportExcel() {
		String strlist = getRequest().getParameter("strlist");
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
		int num = 6;
		pubService.checkButton(getLoginUserInfo(), IFunNode.CHANNEL_47, IButtonName.BTN_EXPORT);
		fieldlist.add("aname");
		fieldlist.add("uname");
		fieldlist.add("provname");
		fieldlist.add("incode");
		fieldlist.add("corpnm");
		fieldlist.add("cuname");
		fieldlist.add("stockcusts");
		fieldlist.add("stockcustt");
		fieldlist.add("renewcusts");
		fieldlist.add("renewcustt");
		for (int i = 0; i < headlist.size(); i++) {
			name = (Map<String, String>) headlist.get(i);
			if (i >= num) {
				heads.add("小规模");
				heads.add("一般纳税人");
				heads1.add(name.get("title"));
			} else {
				heads.add(name.get("title"));
				fieldslist.add(name.get("field"));
			}
		}
		fieldslist.add("stockcusts");
		fieldslist.add("stockcustt");
		fieldslist.add("stockconts");
		fieldslist.add("stockcontt");
		fieldslist.add("renewcusts");
		fieldslist.add("renewcustt");
		fieldslist.add("renewconts");
		fieldslist.add("renewcontt");
		fieldslist.add("renewcustrates");
		fieldslist.add("renewcustratet");
		fieldslist.add("renewcontrates");
		fieldslist.add("renewcontratet");
		ExportExcel<CustNumMoneyRepVO> ex = new ExportExcel<CustNumMoneyRepVO>();
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
			byte[] length = ex.exportYjtjExcel("业绩续费统计", heads, heads1, fieldslist, exparray, toClient, "", fieldlist,
					num);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_7.getValue(), "导出业绩续费统计表", ISysConstants.SYS_3);
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

}
