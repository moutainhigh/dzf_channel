package com.dzf.action.channel.report;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.channel.expfield.ContractConQueryExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.demp.contract.ContractDocVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.InOutUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.report.IContractConfirmQueryService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.spring.SpringUtils;

/**
 * 合同查询
 */
@ParentPackage("basePackage")
@Namespace("/contract")
@Action(value = "contractconfquery")
public class ContractConfirmQueryAction extends BaseAction<ContractConfrimVO> {

	private static final long serialVersionUID = 8503727157432036048L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IContractConfirmQueryService contractconfquer;

	@Autowired
	private IPubService pubser;

	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			paramvo.setCuserid(getLoginUserid());
			StringBuffer qsql = new StringBuffer();// 附加查询条件
			// 1、渠道经理查询条件
			if (!StringUtil.isEmpty(paramvo.getVmanager())) {
				String sql = contractconfquer.getQrySql(paramvo.getVmanager(), IStatusConstant.IQUDAO);
				if (!StringUtil.isEmpty(sql)) {
					qsql.append(sql);
				}
			}
			int total = 0;
			// 2、根据登录人和选择区域进行过滤
			String condition = pubser.makeCondition(paramvo.getCuserid(), paramvo.getAreaname(),
					IStatusConstant.IQUDAO);
			if (condition != null) {
				if (!condition.equals("alldata")) {
					qsql.append(condition);
				}
				if(qsql != null && qsql.length() > 0){
					paramvo.setVqrysql(qsql.toString());
				}
				total = contractconfquer.queryTotalRow(paramvo);
			}
			grid.setTotal((long) (total));
			if (total > 0) {
				List<ContractConfrimVO> list = contractconfquer.query(paramvo);
				grid.setRows(list);
			} else {
				grid.setRows(new ArrayList<ContractConfrimVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询加盟商合同详情信息
	 */
	public void queryInfoById() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
					throw new BusinessException("登陆用户错误");
				} else if (uservo == null) {
					throw new BusinessException("登陆用户错误");
				}
				ContractConfrimVO retvo = contractconfquer.queryInfoById(data);
				json.setSuccess(true);
				json.setRows(retvo);
			} catch (Exception e) {
				printErrorLog(json, log, e, "操作失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("操作数据为空");
		}
		writeJson(json);
	}
	
	/**
	 * 获取附件列表
	 */
	public void getAttaches() {
		Json json = new Json();
		json.setSuccess(false);
		try {
			ContractDocVO paramvo = new ContractDocVO();
			paramvo = (ContractDocVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if (StringUtil.isEmpty(paramvo.getPk_corp())) {
				paramvo.setPk_corp(getLogincorppk());
			}
			List<ContractDocVO>  resvos = contractconfquer.getAttatches(paramvo);
			json.setRows(resvos);
			json.setSuccess(true);
			json.setMsg("获取附件成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "获取附件失败");
		}
		writeJson(json);
	}

	/**
	 * 获取附件显示图片
	 */
	public void getAttachImage() {
		InputStream is = null;
		OutputStream os = null;
		try {
			ContractDocVO paramvo = new ContractDocVO();
			paramvo = (ContractDocVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if (StringUtil.isEmpty(paramvo.getPk_corp())) {
				paramvo.setPk_corp(getLogincorppk());
			}
			List<ContractDocVO> docVOS = contractconfquer.getAttatches(paramvo);
			if(docVOS.size()<1){
				throw new BusinessException("没有图片");
			}
			ContractDocVO resvo = docVOS.get(0);
			String fpath = resvo.getVfilepath();
			if (!StringUtil.isEmpty(fpath)) {
				if(resvo.getIstoretype()==1){
					File afile = new File(fpath);
					if (afile.exists()) {
						String path = getRequest().getSession().getServletContext().getRealPath("/");
						String typeiconpath = path + "images" + File.separator + "typeicon" + File.separator;
						if (fpath.toLowerCase().lastIndexOf(".pdf") > 0) {
						} else if (fpath.toLowerCase().lastIndexOf(".doc") > 0) {
							typeiconpath += "word.jpg";
							afile = new File(typeiconpath);
						} else if (fpath.toLowerCase().lastIndexOf(".xls") > 0) {
							typeiconpath += "excel.jpg";
							afile = new File(typeiconpath);
						} else if (fpath.toLowerCase().lastIndexOf(".ppt") > 0) {
							typeiconpath += "powerpoint.jpg";
							afile = new File(typeiconpath);
						} else if (fpath.toLowerCase().lastIndexOf(".zip") > 0 || fpath.toLowerCase().lastIndexOf(".rar") > 0) {
							typeiconpath += "zip.jpg";
							afile = new File(typeiconpath);
						} else {

						}
						os = getResponse().getOutputStream();
						is = new FileInputStream(afile);
						IOUtils.copy(is, os);
					}
				}else if(resvo.getIstoretype()==2){
					byte[] bytes = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).downFile(fpath);
					os = getResponse().getOutputStream();
					os.write(bytes);
					os.flush();
				}
			}
		} catch (Exception e) {
		    log.error(e);
		} finally {
		    InOutUtils.close(is, "合同查询查看附件关闭输入流");
		    InOutUtils.close(os, "合同查询查看附件关闭输出流");
		}
	}


	/**
	 * 导出
	 */
	public void onExport() {
		String strlist = getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new ContractConfrimVO());
		ContractConfrimVO[] expVOs = DzfTypeUtils.cast(exparray, mapping, ContractConfrimVO[].class,
				JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<ContractConfrimVO> ex = new Excelexport2003<ContractConfrimVO>();
		ContractConQueryExcelField fields = new ContractConQueryExcelField();
		fields.setVos(expVOs);
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String filename = fields.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_46.getValue(), "导出合同查询成功", ISysConstants.SYS_3);
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
		    InOutUtils.close(toClient, "合同查询导出关闭输出流");
		    InOutUtils.close(servletOutputStream, "合同查询导出关闭输入流");
		}
	}
	
	/**
	 * 导出全部
	 */
	public void onExportAll() {
		QryParamVO pamvo = new QryParamVO();
		pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
		pamvo.setCuserid(getLoginUserid());
		StringBuffer qsql = new StringBuffer();// 附加查询条件
		// 1、渠道经理查询条件
		if (!StringUtil.isEmpty(pamvo.getVmanager())) {
			String sql = contractconfquer.getQrySql(pamvo.getVmanager(), 1);
			if (!StringUtil.isEmpty(sql)) {
				qsql.append(sql);
			}
		}
		// 3、根据登录人和选择区域进行过滤
		String condition = pubser.makeCondition(pamvo.getCuserid(), pamvo.getAreaname(),
				IStatusConstant.IQUDAO);
		if (condition != null) {
			if (!condition.equals("alldata")) {
				qsql.append(condition);
			}
			pamvo.setVqrysql(qsql.toString());
		}
		pamvo.setRows(1000000);
		List<ContractConfrimVO> list = contractconfquer.query(pamvo);
		ContractConfrimVO[] expVOs = null;
		if(list != null && list.size() > 0){
			expVOs = list.toArray(new ContractConfrimVO[0]);
		}else{
			throw new BusinessException("查询数据为空");
		}
		
		
		String qj = getRequest().getParameter("qj");
		
		HttpServletResponse response = getResponse();
		Excelexport2003<ContractConfrimVO> ex = new Excelexport2003<ContractConfrimVO>();
		ContractConQueryExcelField fields = new ContractConQueryExcelField();
		fields.setVos(expVOs);
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String filename = fields.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_27.getValue(), "导出合同查询成功", ISysConstants.SYS_3);
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
		    InOutUtils.close(toClient, "合同查询导出全部关闭输出流");
		    InOutUtils.close(servletOutputStream, "合同查询导出全部关闭输入流");
		}
	}
	
}
