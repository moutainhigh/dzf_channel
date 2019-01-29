package com.dzf.action.channel.invoice;

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
import com.dzf.action.channel.expfield.InvInfoExcelField;
import com.dzf.action.channel.expfield.InvManageExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.piaotong.invinfo.InvInfoResBVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.InvManagerService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 渠道商--发票管理
 * 
 * @author shiyan
 *
 */
@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "sys_inv_manager")
public class InvManagerAction extends BaseAction<ChInvoiceVO> {

	private static final long serialVersionUID = -3251202665044730598L;

	private Logger log = Logger.getLogger(getClass());

	@Autowired
	private InvManagerService invManagerService;

	@Autowired
	private IPubService pubser;

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			ChInvoiceVO paramvo = new ChInvoiceVO();
			paramvo = (ChInvoiceVO) DzfTypeUtils.cast(getRequest(), paramvo);
			int total = 0;
			String condition = pubser.makeCondition(getLoginUserid(), paramvo.getAreaname(),IStatusConstant.IYUNYING);
			if (condition != null) {
				if (!condition.equals("alldata")) {
					paramvo.setVprovname(condition);
				}
				total = invManagerService.queryTotalRow(paramvo);
			}
			if (total > 0) {
				List<ChInvoiceVO> rows = invManagerService.query(paramvo);
				grid.setRows(rows);
			} else {
				grid.setRows(new ArrayList<ChInvoiceVO>());
			}
			grid.setTotal((long) total);
			grid.setMsg("查询成功");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 查询渠道商
	 */
	public void queryChannel() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			ChInvoiceVO paramvo = new ChInvoiceVO();
			paramvo = (ChInvoiceVO) DzfTypeUtils.cast(getRequest(), paramvo);
			int page = paramvo == null ? 1 : paramvo.getPage();
			int rows = paramvo == null ? 100000 : paramvo.getRows();
			if(paramvo != null){
				paramvo.setEmail(getLoginUserid());
			}
			List<CorpVO> list = invManagerService.queryChannel(paramvo);
			if (list != null && list.size() > 0) {
				CorpVO[] corpvos = getPagedVOs(list.toArray(new CorpVO[0]), page, rows);
				grid.setRows(Arrays.asList(corpvos));
				grid.setTotal((long) (list.size()));
			} else {
				grid.setRows(list);
				grid.setTotal(0L);
			}
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
			// grid.setRows(rows);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	private CorpVO[] getPagedVOs(CorpVO[] cvos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= cvos.length) {// 防止endIndex数组越界
			endIndex = cvos.length;
		}
		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
		return cvos;
	}

	/**
	 * 开票
	 */
	public void onBilling() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_39);
			String uid = getLoginUserid();
			String pk_invoices = getRequest().getParameter("pk_invoices");
			String invtime = getRequest().getParameter("invtime");
			String[] pkArry = pkinvoicesToArray(pk_invoices);
			List<ChInvoiceVO> listError = invManagerService.onBilling(pkArry, uid, invtime);
			int errorNum = listError == null ? 0 : listError.size();
			int success = pkArry.length - errorNum;
			StringBuffer msg = new StringBuffer();
			msg.append("成功").append(success).append("条");
			if (listError != null && errorNum > 0) {
				msg.append("，失败").append(errorNum).append("条<br>");
				for (ChInvoiceVO vo : listError) {
					msg.append("加盟商：").append(vo.getCorpname()).append(",失败原因：").append(vo.getMsg()).append("<br>");
				}
			}
			json.setMsg(msg.toString());
			json.setSuccess(true);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_FPGL.getValue(), "成功开具纸质发票" + success+"张",ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(json, log, e, "开票失败");
		}
		writeJson(json);
	}

	/**
	 * 电子票
	 * 
	 * @author gejw
	 * @date 2018年6月5日
	 * @time 下午3:43:27
	 */
	public void onAutoBill() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_39);
			ChInvoiceVO paramvo = new ChInvoiceVO();
			paramvo = (ChInvoiceVO) DzfTypeUtils.cast(getRequest(), paramvo);
			String pk_invoices = getRequest().getParameter("ids");
			String[] pkArry = pkinvoicesToArray(pk_invoices);
			List<ChInvoiceVO> listError = invManagerService.onAutoBill(pkArry, getLoginUserInfo());
			int errorNum = listError == null ? 0 : listError.size();
			int success = pkArry.length - errorNum;
			StringBuffer msg = new StringBuffer();
			msg.append("成功").append(success).append("条");
			if (listError != null && errorNum > 0) {
				msg.append("，失败").append(errorNum).append("条<br>");
				for (ChInvoiceVO vo : listError) {
					msg.append("加盟商：").append(vo.getCorpname()).append(",失败原因：").append(vo.getMsg()).append("<br>");
				}
			}
			json.setMsg(msg.toString());
			json.setSuccess(true);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_FPGL.getValue(), "成功开具电子发票" + success+"张",ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(json, log, e, "开票失败");
		}
		writeJson(json);
	}

	/**
	 * 查询票通电子发票库存信息
	 * 
	 * @author gejw
	 * @time 上午11:00:43
	 */
	public void queryInvInfo() {

		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			InvInfoResBVO[] vos = invManagerService.queryInvRepertoryInfo();
			json.setMsg("电子票余量查询成功");
			json.setSuccess(true);
			json.setRows(vos);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_FPGL.getValue(), "查询电票余量", ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(json, log, e, "电子票余量查询失败");
		}
		writeJson(json);

	}

	private String[] pkinvoicesToArray(String pk_invoices) {
		JSONArray array = JSON.parseArray(pk_invoices);
		if (array != null && array.size() > 0) {
			Object[] list = array.toArray();
			String[] pkinvoices = new String[list.length];
			for (int i = 0; i < list.length; i++) {
				pkinvoices[i] = list[i].toString();
			}
			return pkinvoices;
		}
		return null;
	}

	public void save() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_39);
			invManagerService.save(data);
			json.setSuccess(true);
			json.setMsg("保存成功!");
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_FPGL.getValue(), "发票申请修改：" + data.getCorpname(),
					ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}

	public void delete() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_39);
			String invoices = getRequest().getParameter("invoices");
			if (!StringUtil.isEmpty(invoices)) {
				invoices = invoices.replace("}{", "},{");
				invoices = "[" + invoices + "]";
				JSONArray array = (JSONArray) JSON.parseArray(invoices);
				Map<String, String> map = FieldMapping.getFieldMapping(new ChInvoiceVO());
				ChInvoiceVO[] vos = DzfTypeUtils.cast(array, map, ChInvoiceVO[].class,
						JSONConvtoJAVA.getParserConfig());
				int len = 0;
				int length = vos == null ? 0 : vos.length;
				if (vos != null && length > 0) {
					for (ChInvoiceVO cvo : vos) {
						try {
							invManagerService.delete(cvo);
							len++;
						} catch (BusinessException e) {
							// msg.append("合同号：").append(cvo.getVcontcode()).append(",失败原因：").append(e.getMessage()).append("<br>");
						} catch (Exception e) {
							log.error("删除失败", e);
						}
					}
					StringBuffer str = new StringBuffer();
					str.append(getLoginUserInfo().getUser_name());
					str.append("删除发票").append(vos.length).append("张");
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_FPGL.getValue(), str.toString(), ISysConstants.SYS_3);
				}
				json.setMsg("成功删除" + len + "张单据，失败" + (length - len) + "张单据  ");
				json.setSuccess(true);
			} else {
				json.setSuccess(false);
				json.setRows(0);
				json.setMsg("请选择删除数据");
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "删除失败");
		}
		writeJson(json);
	}

	/**
	 * 获取可开票金额
	 */
	public void queryTotalPrice() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String pk_corp = getRequest().getParameter("corpid");
			String ipaytype = getRequest().getParameter("ipaytype");
			String invprice = StringUtil.isEmpty(getRequest().getParameter("invprice")) ? ""
					: getRequest().getParameter("invprice");
			ChInvoiceVO vo = invManagerService.queryTotalPrice(pk_corp, Integer.valueOf(ipaytype), invprice);
			json.setSuccess(true);
			json.setMsg("获取可开票金额成功!");
			json.setRows(vo);
		} catch (Exception e) {
			printErrorLog(json, log, e, "获取可开票金额失败");
		}
		writeJson(json);
	}

	public void onChange() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.CHANNEL_39);
			String pk_invoice = getRequest().getParameter("id");
			String dcdate = getRequest().getParameter("dcdate");
			if (StringUtil.isEmpty(dcdate)) {
				throw new BusinessException("换票日期不能为空。");
			}
			String vcmemo = getRequest().getParameter("vcmemo");
			ChInvoiceVO cvo = new ChInvoiceVO();
			cvo.setPk_invoice(pk_invoice);
			cvo.setDchangedate(new DZFDate(dcdate));
			cvo.setVchangememo(vcmemo);
			cvo.setPk_corp(getLogincorppk());
			invManagerService.onChange(cvo);
			json.setMsg("换票成功");
			json.setSuccess(true);

		} catch (Exception e) {
			printErrorLog(json, log, e, "开票失败");
		}
		writeJson(json);
	}

	public void onExport() {
		String strlist = getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new ChInvoiceVO());
		ChInvoiceVO[] expVOs = DzfTypeUtils.cast(exparray, mapping, ChInvoiceVO[].class,
				JSONConvtoJAVA.getParserConfig());
		ArrayList<ChInvoiceVO> explist = new ArrayList<ChInvoiceVO>();
		for (ChInvoiceVO vo : expVOs) {
			explist.add(vo);
		}
		HttpServletResponse response = getResponse();

		Excelexport2003<ChInvoiceVO> ex = new Excelexport2003<ChInvoiceVO>();
		InvManageExcelField fields = new InvManageExcelField();
		fields.setVos(explist.toArray(new ChInvoiceVO[0]));
		;
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
			if (toClient != null) {
				try {
					toClient.flush();
					toClient.close();
				} catch (IOException e) {
					log.error("导出失败", e);
				}
			}
			if (servletOutputStream != null) {
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error("导出失败", e);
				}
			}
		}
		writeLogRecord(LogRecordEnum.OPE_CHANNEL_FPGL.getValue(), "导出发票申请表", ISysConstants.SYS_3);
	}

	/**
	 * 导出电票余量
	 * 
	 * @author gejw
	 * @date 2018年6月5日
	 * @time 下午2:41:27
	 */
	public void onExportInvInfo() {
		String strlist = getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new InvInfoResBVO());
		InvInfoResBVO[] expVOs = DzfTypeUtils.cast(exparray, mapping, InvInfoResBVO[].class,
				JSONConvtoJAVA.getParserConfig());
		ArrayList<InvInfoResBVO> explist = new ArrayList<InvInfoResBVO>();
		for (InvInfoResBVO vo : expVOs) {
			explist.add(vo);
		}
		HttpServletResponse response = getResponse();

		Excelexport2003<InvInfoResBVO> ex = new Excelexport2003<InvInfoResBVO>();
		InvInfoExcelField fields = new InvInfoExcelField();
		fields.setVos(explist.toArray(new InvInfoResBVO[0]));
		;
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
			if (toClient != null) {
				try {
					toClient.flush();
					toClient.close();
				} catch (IOException e) {
					log.error("导出失败", e);
				}
			}
			if (servletOutputStream != null) {
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error("导出失败", e);
				}
			}
		}
	}
}
