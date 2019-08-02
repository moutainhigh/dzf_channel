package com.dzf.action.channel.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
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
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.report.MarketTeamVO;
import com.dzf.model.channel.report.PersonStatisVO;
import com.dzf.model.channel.report.UserDetailVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.channel.report.IPersonStatis;
import com.dzf.service.pub.report.ExportExcel;

/**
 * 加盟商人员统计
 * 
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "personStatis")
public class PersonStatisAction extends BaseAction<PersonStatisVO> {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IPersonStatis personStatis;

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			paramvo.setUser_name(getLoginUserid());
			if (StringUtil.isEmpty(paramvo.getPk_corp())) {
				paramvo.setPk_corp(getLogincorppk());
			}
			UserVO uservo = getLoginUserInfo();
			paramvo.setBegdate(new DZFDate());
			List<PersonStatisVO> list = personStatis.query(paramvo,uservo);
			grid.setTotal(Long.valueOf(0));
			grid.setRows(list);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	public void queryUserDetail() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			List<UserDetailVO> list = personStatis.queryUserDetail(paramvo);
			QueryDeCodeUtils.decKeyUtils(new String[] { "user_name" }, list, 1);
			grid.setRows(list);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	
	
	/**
	 * 编辑销售团队人数
	 */
	public void save() {
		Json json = new Json();
		try {
			MarketTeamVO marketvo = new MarketTeamVO();
			marketvo = (MarketTeamVO) DzfTypeUtils.cast(getRequest(), marketvo);
			UserVO uservo = getLoginUserInfo();
			marketvo.setDoperatetime(new DZFDateTime());
			marketvo.setCoperatorid(uservo.getCuserid());
			personStatis.save(marketvo);
			json.setMsg("保存成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("保存失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}

	/**
	 * 编辑回显
	 */
	public void queryById() {
		Json json = new Json();
		try {
			MarketTeamVO vo = new MarketTeamVO();
			String id = getRequest().getParameter("id");
			if (!StringUtil.isEmpty(id)) {
				vo = personStatis.queryDataById(id);
			}
			json.setRows(vo);
			json.setMsg("查询成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("查询失败");
			json.setSuccess(false);
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);

	}

	/**
	 * 导出
	 */
	public void exportAuditExcel() {
		String strlist = getRequest().getParameter("strlist");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		String columns = getRequest().getParameter("columns");
		String cols = getRequest().getParameter("cols");
		String coltwo = getRequest().getParameter("coltwo");
		String frozencols = getRequest().getParameter("frozencols");
		JSONArray columnslist = (JSONArray) JSON.parseArray(columns);
		JSONArray coltwolist = (JSONArray) JSON.parseArray(coltwo);
		JSONArray frozenlist = (JSONArray) JSON.parseArray(frozencols);
		JSONArray colslist = (JSONArray) JSON.parseArray(cols);
		List<String> heads = new ArrayList<String>();
		List<String> heads1 = new ArrayList<String>();
		List<String> heads2 = new ArrayList<String>();
		List<String> heads3 = new ArrayList<String>();
		List<String> heads4 = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		Map<String, String> name = null;
		List<String> fieldlist = new ArrayList<String>();
		
		int num = 9;
		heads.add("大区");
		heads.add("区总");
		heads.add("省份");
		heads.add("加盟商名称");
		heads.add("加盟商编码");
		
		Integer[] headindexs = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 18, 19, 24, 25, 26 };
		List<Integer> head1indexs = new ArrayList<Integer>();
		head1indexs.add(9);
		head1indexs.add(20);
		for (int i = 2; i < frozenlist.size(); i++) {
			fieldlist.add(frozenlist.getString(i));
			fieldslist.add(frozenlist.getString(i));
		}
		for (int i = 0; i < colslist.size(); i++) {
			fieldlist.add(colslist.getString(i));
			if(i<=3){
				fieldslist.add(colslist.getString(i));
			}
		}
		
		for (int i = 0; i < columnslist.size(); i++) {
			name = (Map<String, String>) columnslist.get(i);
			if(i==4||i==8){
				heads1.add(name.get("title"));
			}else{
				heads.add(name.get("title"));
			}
		}
		
		heads2.addAll(heads);
		for (int i = 0; i < coltwolist.size(); i++) {
			name = (Map<String, String>) coltwolist.get(i);
			if(i>=8){
				heads4.add(name.get("title"));
			}else{
				heads3.add(name.get("title"));
			}
		}
		heads2.addAll(9,heads3);
		heads2.addAll(20,heads4);
		
		ExportExcel<StockOutInMVO> ex = new ExportExcel<StockOutInMVO>();
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			HttpServletResponse response = getResponse();
			response.reset();
			String fileName = null;
			String userAgent = getRequest().getHeader("user-agent");
			String date = DateUtils.getDate(new Date());
			if (!StringUtil.isEmpty(userAgent) && (userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0
					|| userAgent.indexOf("Safari") >= 0)) {
				fileName = new String(("加盟商人员统计表").getBytes(), "ISO8859-1");
			} else {
				fileName = URLEncoder.encode("加盟商人员统计表", "UTF8"); // 其他浏览器
			}
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName +new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.exportPersonStatisExcel("加盟商人员统计表", heads,headindexs, heads1,head1indexs,heads2, fieldslist, exparray, toClient, "",
					fieldlist, num);
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
