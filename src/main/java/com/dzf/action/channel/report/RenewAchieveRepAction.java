package com.dzf.action.channel.report;

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
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.channel.report.RenewAchieveVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IButtonName;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.channel.report.IRenewAchieveService;
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
public class RenewAchieveRepAction extends PrintUtil<RenewAchieveVO> {

	private static final long serialVersionUID = 7071363023455589093L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IPubService pubSer;
	
	@Autowired
	private IRenewAchieveService renewser;

	/**
	 * 查询
	 */
	public void queryRenew() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubSer.checkFunnode(uservo, IFunNode.CHANNEL_47);
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if(pamvo == null){
				pamvo = new QryParamVO();
			}
			
			QrySqlSpmVO qryvo = renewser.getCorpQrySql(pamvo, uservo);
			Integer total = null;
			if(qryvo == null){
				total = 0;
			}else{
				total = renewser.queryTotal(qryvo);
			}
			if (total > 0) {
				String sql = qryvo.getSql();
				StringBuffer qsql = new StringBuffer();
				qsql.append(" SELECT account.pk_corp FROM ");
				int index = sql.indexOf("FROM");
				qsql.append(sql.substring(index + 4));
				qryvo.setSql(qsql.toString());
				List<RenewAchieveVO> list = renewser.query(pamvo, uservo, qryvo);
				grid.setTotal((long) total);
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_7.getValue(), "业绩续费统计查询成功", ISysConstants.SYS_3);
			} else {
				grid.setTotal(Long.valueOf(0));
				grid.setRows(new ArrayList<RenewAchieveVO>());
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
		UserVO uservo = getLoginUserInfo();
		pubSer.checkFunnode(uservo, IFunNode.CHANNEL_47);
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误");
		} else if (uservo == null) {
			throw new BusinessException("登陆用户错误");
		}
		
		pubSer.checkButton(getLoginUserInfo(), IFunNode.CHANNEL_47, IButtonName.BTN_EXPORT);
		String strlist = getRequest().getParameter("strlist");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		
		String columns = getRequest().getParameter("columns");
		JSONArray headlist = (JSONArray) JSON.parseArray(columns);
		
		String djcols = getRequest().getParameter("djcols");
		JSONArray djlist = (JSONArray) JSON.parseArray(djcols);
		
		List<String> heads = new ArrayList<String>();
		List<String> heads1 = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		List<String> strcodes = new ArrayList<String>();
		
		heads.add("大区");
		heads.add("区总");
		heads.add("省份");
		heads.add("加盟商编码");
		heads.add("加盟商名称");
		
		int num = 5;
		Map<String, String> name = null;
		String code = "";
		for(int i = 0; i < djlist.size(); i++){
			code = djlist.getString(i);
			fieldslist.add(code);
			strcodes.add(code);
		}
		
		int index = 0;
		String title = "";
		for (int i = 0; i < headlist.size(); i++) {
			name = (Map<String, String>) headlist.get(i);
			if (i >= 5) {
				heads.add("到期数");
				heads.add("续费数");
				heads.add("续费率(%)");
				heads.add("续费额");
				heads1.add(name.get("title"));
				
				fieldslist.add("expire"+index);
				fieldslist.add("num"+index);
				fieldslist.add("rate"+index);
				fieldslist.add("mny"+index);
				
				strcodes.add("expire"+index);
				strcodes.add("num"+index);
				index ++;
			}else {
				title = name.get("title");
				if(!StringUtil.isEmpty(title) && title.indexOf("<br>") != -1){
					title = "会计运营经理";
				}
				heads.add(title);
				fieldslist.add(name.get("field"));
				strcodes.add(name.get("field"));
			}
		}
		num = 10;
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
			byte[] length = ex.exportYjtjExcel("业绩续费统计", heads, heads1, fieldslist, exparray, toClient, "", strcodes,
					num, "renew");
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
