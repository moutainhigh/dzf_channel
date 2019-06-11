package com.dzf.action.branch.reportmanage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.dzf.model.branch.reportmanage.CompanyDataVO;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.branch.reportmanage.ISalerDataService;
import com.dzf.service.pub.report.ExportExcel;
import com.dzf.service.pub.report.PrintUtil;

/**
 * 销售业绩统计
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/branch")
@Action(value = "salerData")
public class SalerDataAction extends PrintUtil<CompanyDataVO> {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ISalerDataService saler;
	
	/**
	 * 查询
	 */
	public void query() {
		Json grid = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			
			QryParamVO qvo = new QryParamVO();
			qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setCuserid(uservo.getCuserid());
			qvo.setUser_name(uservo.getUser_name());
			
			int page = qvo == null ? 1 : qvo.getPage();
			int rows = qvo == null ? 100000 : qvo.getRows();
			List<CompanyDataVO> list = saler.query(qvo);
			int len = list == null ? 0 : list.size();
			grid.setStatus(1);
			if (list != null && list.size() > 0) {
				if(qvo.getIpaytype()!=null && qvo.getIpaytype()==2){
					grid.setStatus(2);
				}
				list = getPagedVOs(list, page, rows);
				grid.setRows(list);
				grid.setTotal((long) (len));
				grid.setMsg("查询成功!");
			} else {
				grid.setRows(new ArrayList<>());
				grid.setTotal(0L);
				grid.setMsg("查询数据为空!");
			}
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	// 将查询后的结果分页
	private ArrayList<CompanyDataVO> getPagedVOs(List<CompanyDataVO> list, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= list.size()) {// 防止endIndex数组越界
			endIndex = list.size();
		}
		CompanyDataVO[] cvos = Arrays.copyOfRange(list.toArray(new CompanyDataVO[list.size()]), beginIndex, endIndex);
		ArrayList<CompanyDataVO> vos = new ArrayList<>();
		Collections.addAll(vos, cvos);
		return vos;
	}
	
	public void exportExcel(){
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
		List<String> fieldlist = new ArrayList<String>();//字符串
		int num = 2;
		fieldlist.add("branchname");
		fieldlist.add("allcorp");
		fieldlist.add("ybrcorp");
		fieldlist.add("xgmcorp");
		fieldlist.add("addcorp");
		fieldlist.add("losecorp");
		fieldlist.add("contcorp");
		for (int i = 0; i < headlist.size()-1; i++) {
			name = (Map<String, String>) headlist.get(i);
			if (i == num) {
				heads.add("一般人");
				heads.add("小规模");
				heads1.add(name.get("title"));
			} else {
				heads.add(name.get("title"));
				fieldslist.add(name.get("field"));
			}
		}
		heads1.add("本期");
		heads.add("客户新增数");
		heads.add("客户流失数");
		heads.add("客户合同数");
		heads.add("合同总金额");
		heads.add("合同已收金额");
		heads.add("合同未收金额");
		
		fieldslist.add("ybrcorp");
		fieldslist.add("xgmcorp");
		fieldslist.add("addcorp");
		fieldslist.add("losecorp");
		fieldslist.add("contcorp");
		fieldslist.add("totalmny");
		fieldslist.add("ysmny");
		fieldslist.add("wsmny");
		ExportExcel<CompanyDataVO> ex = new ExportExcel<CompanyDataVO>();
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
			byte[] length = ex.exportCompanyDataExcel("销售业绩统计", heads, heads1, fieldslist, exparray, toClient, "", fieldlist,
					num);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
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
