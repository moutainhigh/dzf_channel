package com.dzf.action.channel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.spring.SpringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.channel.expfield.ContractConExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.demp.contract.ContractDocVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.InOutUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.IContractConfirm;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 合同确认
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/contract")
@Action(value = "contractconf")
public class ContractConfirmAction extends BaseAction<ContractConfrimVO> {

	private static final long serialVersionUID = 8503727157432036048L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IContractConfirm contractconfser;

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
			StringBuffer qsql = new StringBuffer();// 附加查询条件
			// 1、渠道经理查询条件
			if (!StringUtil.isEmpty(paramvo.getVmanager())) {
				String sql = contractconfser.getQrySql(paramvo.getVmanager(), IStatusConstant.IQUDAO);
				if (!StringUtil.isEmpty(sql)) {
					qsql.append(sql);
				}
			}
			// 2、渠道运营查询条件
			if (!StringUtil.isEmpty(paramvo.getVoperater())) {
				String sql = contractconfser.getQrySql(paramvo.getVoperater(), IStatusConstant.IYUNYING);
				if (!StringUtil.isEmpty(sql)) {
					qsql.append(sql);
				}
			}
			paramvo.setCuserid(getLoginUserid());
			int total = 0;
			// 3、根据登录人和选择区域进行过滤
			String condition = pubser.makeCondition(paramvo.getCuserid(), paramvo.getAreaname(),
					IStatusConstant.IYUNYING);
			if (condition != null) {
				if (!condition.equals("alldata")) {
					qsql.append(condition);
				}
				if(qsql != null && qsql.length() > 0){
					paramvo.setVqrysql(qsql.toString());
				}
				total = contractconfser.queryTotalRow(paramvo);
			}
			grid.setTotal((long) (total));
			if (total > 0) {
				List<ContractConfrimVO> list = contractconfser.query(paramvo);
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
	 * 查询待扣款数据
	 */
	public void queryDeductData() {
		Json json = new Json();
		try {
			ContractConfrimVO qryvo = (ContractConfrimVO) DzfTypeUtils.cast(getRequest(), new ContractConfrimVO());
			ContractConfrimVO retvo = contractconfser.queryDebitData(qryvo);
			json.setRows(retvo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}

	/**
	 * 审核
	 */
	public void updateDeductData() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_4);
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String type = getRequest().getParameter("opertype");
			Integer opertype = Integer.parseInt(type);// 操作类型

			String head = getRequest().getParameter("head");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new ContractConfrimVO());
			ContractConfrimVO datavo = new ContractConfrimVO();
			datavo = DzfTypeUtils.cast(headjs, headmaping, ContractConfrimVO.class, JSONConvtoJAVA.getParserConfig());

			if (IStatusConstant.IDEDUCTYPE_2 == opertype) {// 驳回
				if (StringUtil.isEmpty(datavo.getVconfreasonid())) {
					throw new BusinessException("驳回原因不能为空");
				}
			}

			if (datavo == null) {
				throw new BusinessException("单个审核-获取审核数据为空");
			}
			datavo = contractconfser.updateAuditData(datavo, null, opertype, getLoginUserid(), getLogincorppk(),
					"single");
			json.setRows(datavo);
			if (datavo != null) {
				if (IStatusConstant.IDEDUCTYPE_1 == opertype) {// 审核
					String corpcode = "";
					CorpVO corpvo = CorpCache.getInstance().get(null, datavo.getPk_corpk());
					if (corpvo != null) {
						corpcode = corpvo.getInnercode();
					}
					if (datavo.getPatchstatus() != null && (IStatusConstant.ICONTRACTTYPE_2 == datavo.getPatchstatus()
							|| IStatusConstant.ICONTRACTTYPE_5 == datavo.getPatchstatus())) {
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(),
								"纳税人变更：客户编码[" + corpcode + "] 合同编码[" + datavo.getVcontcode() + "]",
								ISysConstants.SYS_3);
					} else {
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "审核合同：合同编码：" + datavo.getVcontcode(),
								ISysConstants.SYS_3);
					}
				} else if (IStatusConstant.IDEDUCTYPE_2 == opertype) {// 驳回
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "驳回合同：合同编码：" + datavo.getVcontcode(),
							ISysConstants.SYS_3);
				}
			}
			json.setMsg("操作成功");
			json.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}

	/**
	 * 批量审核
	 */
	public void bathconfrim() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_4);
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String contract = getRequest().getParameter("contract"); // 审核数据
			if (StringUtil.isEmpty(contract)) {
				throw new BusinessException("数据不能为空");
			}
			String type = getRequest().getParameter("opertype");
			int opertype = Integer.parseInt(type);// 1、扣款；2、驳回；

			String head = getRequest().getParameter("head");
			JSON headjs = (JSON) JSON.parse(head);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new ContractConfrimVO());
			ContractConfrimVO paramvo = DzfTypeUtils.cast(headjs, headmaping, ContractConfrimVO.class,
					JSONConvtoJAVA.getParserConfig());

			contract = contract.replace("}{", "},{");
			contract = "[" + contract + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(contract);
			Map<String, String> contmaping = FieldMapping.getFieldMapping(new ContractConfrimVO());
			ContractConfrimVO[] confrimVOs = DzfTypeUtils.cast(arrayJson, contmaping, ContractConfrimVO[].class,
					JSONConvtoJAVA.getParserConfig());

			if (IStatusConstant.IDEDUCTYPE_2 == opertype) {// 驳回
				if (StringUtil.isEmpty(paramvo.getVconfreasonid())) {
					throw new BusinessException("驳回原因不能为空");
				}
			}

			int rignum = 0;
			int errnum = 0;
			List<ContractConfrimVO> rightlist = new ArrayList<ContractConfrimVO>();
			StringBuffer errmsg = new StringBuffer();
			if (confrimVOs != null && confrimVOs.length > 0) {
				for (ContractConfrimVO datavo : confrimVOs) {
					if (datavo == null) {
						log.info("批量审核-获取审核数据为空");
					} else {
						try {
							if (IStatusConstant.IDEDUCTYPE_1 == opertype && datavo.getPatchstatus() != null
									&& IStatusConstant.ICONTRACTTYPE_5 == datavo.getPatchstatus()) {
								throw new BusinessException("一般人转小规模合同，不允许批量审核");
							}
							datavo = contractconfser.updateAuditData(datavo, paramvo, opertype, getLoginUserid(),
									getLogincorppk(), "batch");
							rignum++;
							rightlist.add(datavo);
						} catch (Exception e) {
							errnum++;
							errmsg.append(e.getMessage()).append("<br>");
						}
					}
				}
			}
			json.setSuccess(true);
			if (rignum > 0 && rignum == confrimVOs.length) {
				json.setRows(Arrays.asList(confrimVOs));
				json.setMsg("成功" + rignum + "条");
			} else if (errnum > 0) {
				json.setMsg("成功" + rignum + "条，失败" + errnum + "条，失败原因：" + errmsg.toString());
				json.setStatus(-1);
				if (rignum > 0) {
					json.setRows(rightlist);
				}
			}
			if (rignum > 0) {
				if (opertype == 1) {// 审核
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "审核合同：" + rignum + "个", ISysConstants.SYS_3);
				} else if (opertype == 2) {
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "驳回合同：" + rignum + "个", ISysConstants.SYS_3);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
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
			List<ContractDocVO>  resvos = contractconfser.getAttatches(paramvo);
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
			List<ContractDocVO> docVOS = contractconfser.getAttatches(paramvo);
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
							/*
							 * typeiconpath += "pdf.jpg"; afile = new
							 * File(typeiconpath);
							 */
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
		    InOutUtils.close(is, "合同确认查看附件关闭输入流");
		    InOutUtils.close(os, "合同确认查看附件关闭输出流");
		}
	}

	/**
	 * 变更保存
	 */
	public void saveChange() {
		Json json = new Json();
		if (data != null) {
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_4);
			File[] files = ((MultiPartRequestWrapper) getRequest()).getFiles("imageFile");
			String[] filenames = ((MultiPartRequestWrapper) getRequest()).getFileNames("imageFile");
			try {
				if(data.getIapplystatus() != null && data.getIapplystatus() != 4){
					if (files == null || files.length == 0) {
						throw new BusinessException("变更附件不能为空");
					}
				}
				ContractConfrimVO retvo = contractconfser.saveChange(data, getLoginUserid(), files, filenames);
				if (retvo != null) {
					if (data.getIchangetype() == IStatusConstant.ICONCHANGETYPE_1) {// 终止
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "终止合同：合同编码：" + retvo.getVcontcode(),
								ISysConstants.SYS_3);
					} else if (data.getIchangetype() == IStatusConstant.ICONCHANGETYPE_2) {// 作废
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "作废合同：合同编码：" + retvo.getVcontcode(),
								ISysConstants.SYS_3);
					}
				}
				json.setRows(retvo);
				json.setSuccess(true);
				json.setMsg("变更成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "变更失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("变更失败");
		}
		writeJson(json);
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
				ContractConfrimVO retvo = contractconfser.queryInfoById(data);
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
		ContractConExcelField fields = new ContractConExcelField();
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
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "导出合同列表", ISysConstants.SYS_3);
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
		    InOutUtils.close(toClient, "合同确认导出关闭输出流");
		    InOutUtils.close(servletOutputStream, "合同确认导出关闭输入流");
		}
	}
	
	/**
	 * 导出全部
	 */
	public void onExportAll() {
		QryParamVO pamvo = new QryParamVO();
		pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
		StringBuffer qsql = new StringBuffer();// 附加查询条件
		// 1、渠道经理查询条件
		if (!StringUtil.isEmpty(pamvo.getVmanager())) {
			String sql = contractconfser.getQrySql(pamvo.getVmanager(), 1);
			if (!StringUtil.isEmpty(sql)) {
				qsql.append(sql);
			}
		}
		// 2、渠道运营查询条件
		if (!StringUtil.isEmpty(pamvo.getVoperater())) {
			String sql = contractconfser.getQrySql(pamvo.getVoperater(), 3);
			if (!StringUtil.isEmpty(sql)) {
				qsql.append(sql);
			}
		}
		pamvo.setCuserid(getLoginUserid());
		// 3、根据登录人和选择区域进行过滤
		String condition = pubser.makeCondition(pamvo.getCuserid(), pamvo.getAreaname(),
				IStatusConstant.IYUNYING);
		if (condition != null) {
			if (!condition.equals("alldata")) {
				qsql.append(condition);
			}
			pamvo.setVqrysql(qsql.toString());
		}
		pamvo.setRows(1000000);
		List<ContractConfrimVO> list = contractconfser.query(pamvo);
		ContractConfrimVO[] expVOs = null;
		if(list != null && list.size() > 0){
			expVOs = list.toArray(new ContractConfrimVO[0]);
		}else{
			throw new BusinessException("查询数据为空");
		}
		
		
		String qj = getRequest().getParameter("qj");
		
		HttpServletResponse response = getResponse();
		Excelexport2003<ContractConfrimVO> ex = new Excelexport2003<ContractConfrimVO>();
		ContractConExcelField fields = new ContractConExcelField();
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
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_4.getValue(), "导出查询合同列表", ISysConstants.SYS_3);
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
		    InOutUtils.close(toClient, "合同确认导出全部关闭输出流");
		    InOutUtils.close(servletOutputStream, "合同确认导出全部关闭输入流");
		}
	}
	
	/**
	 * 查询待变更数据信息
	 */
	public void queryChangeById() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
					throw new BusinessException("登陆用户错误");
				} else if (uservo == null) {
					throw new BusinessException("登陆用户错误");
				}
				ContractConfrimVO retvo = contractconfser.queryChangeById(data);
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

}
