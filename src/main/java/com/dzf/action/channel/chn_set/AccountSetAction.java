package com.dzf.action.channel.chn_set;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.dzf.action.channel.expfield.AccountSetExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.AccountSetVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.chn_set.IAccountSetService;
import com.dzf.service.pub.IPubService;

/**
 * 账务设置
 * 
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/chn_set")
@Action(value = "account")
public class AccountSetAction extends BaseAction<AccountSetVO> {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IPubService pubService;
	
	@Autowired
	private IAccountSetService accountSet;
	
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			int page = paramvo == null ? 1 : paramvo.getPage();
			int rows = paramvo == null ? 100000 : paramvo.getRows();
			paramvo.setUser_name(getLoginUserid());
			List<AccountSetVO> list = accountSet.query(paramvo);
			int len = list == null ? 0 : list.size();
			if (list != null && list.size() > 0) {
				list = getPagedVOs(list, page, rows);
				grid.setRows(list);
				grid.setTotal((long) (len));
				grid.setMsg("查询成功!");
			} else {
				grid.setRows(list);
				grid.setTotal(0L);
				grid.setMsg("查询数据为空!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	public void queryCorpk() {
		Grid grid = new Grid();
		try {
			String pk_corp = getRequest().getParameter("corpid");
			String corpkname = getRequest().getParameter("corpkname");
			List<AccountSetVO> queryCorpk = accountSet.queryCorpk(pk_corp, corpkname);
			grid.setRows(queryCorpk);
			grid.setSuccess(true);
			grid.setTotal((long)queryCorpk.size());
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	public void save() {
		Json json = new Json();
		try {
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			DZFDate stopdate = new DZFDate(data.getStopperiod());
			int days = DateUtils.getDaysDiff(stopdate.getMillis(),new DZFDate().getMillis());
			if(!StringUtil.isEmpty(data.getStopperiod()) && days>15){
				throw new BusinessException("停用时间需在15天之内！");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_73);
			if(StringUtil.isEmpty(data.getPk_accountset())){
				data.setCoperatorid(uservo.getCuserid());
				accountSet.save(data);
			}else{
				accountSet.saveEdit(data);
			}
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	public void updateStatus() {
		Json json = new Json();
		try {
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_73);
			accountSet.updateStatus(data);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	public void delete() {
		Json json = new Json();
		try {
			String ids = getRequest().getParameter("ids");
			if(StringUtil.isEmpty(ids)){
				throw new BusinessException("数据信息不能为空");
			}
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_73);
			String[] split = ids.split(",");
			accountSet.delete(split);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 导出
	 */
	public void exportExcel(){
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new AccountSetVO());
		AccountSetVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,AccountSetVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<AccountSetVO> ex = new Excelexport2003<>();
		AccountSetExcelField fields = new AccountSetExcelField();
		fields.setVos(expVOs);
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String filename = fields.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
	        response.addHeader("Content-Disposition", "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
		} catch (Exception e) {
			log.error("导出失败",e);
		}  finally {
			if(toClient != null){
				try {
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

	private ArrayList<AccountSetVO> getPagedVOs(List<AccountSetVO> list, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= list.size()) {// 防止endIndex数组越界
			endIndex = list.size();
		}
		AccountSetVO[] cvos = Arrays.copyOfRange(list.toArray(new AccountSetVO[list.size()]), beginIndex, endIndex);
		ArrayList<AccountSetVO> vos = new ArrayList<>();
		Collections.addAll(vos, cvos);
		return vos;
	}
}
