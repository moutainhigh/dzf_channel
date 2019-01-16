package com.dzf.action.channel.invoice;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.channel.expfield.BillingExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.model.channel.payment.ChnDetailRepVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.balance.IRecPayDetailService;
import com.dzf.service.channel.invoice.IBillingQueryService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.pub.report.ExportUtil;
import com.dzf.service.pub.report.PrintUtil;
import com.itextpdf.text.DocumentException;

/**
 * 渠道商--发票管理--加盟商发票查询
 * 
 * @author dzf
 *
 */
@ParentPackage("basePackage")
@Namespace("/invoice")
@Action(value = "billingquery")
public class BillingQueryAction extends BaseAction<ChInvoiceVO> {

	private static final long serialVersionUID = -3251202665044730598L;

	private Logger log = Logger.getLogger(getClass());

	@Autowired
	private IBillingQueryService billingQueryServiceImpl;
	@Autowired
	private IRecPayDetailService recPayDetailService;

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			BillingInvoiceVO paramvo = new BillingInvoiceVO();
			paramvo = (BillingInvoiceVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if (paramvo.getBdate() == null) {
				paramvo.setBdate(new DZFDate().toString());
			}
			paramvo.setCuserid(getLoginUserid());
			List<BillingInvoiceVO> rows = billingQueryServiceImpl.query(paramvo);
			// QueryDeCodeUtils.decKeyUtils(new String[]{"corpname"}, rows, 2);
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
			grid.setRows(rows);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_JMSKPCX.getValue(), "加盟商开票查询成功", ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 生成开票申请
	 */
	public void insertBilling() {
		Json json = new Json();
		try {
			String invoices = getRequest().getParameter("invoices");
			if (!StringUtil.isEmpty(invoices)) {
				invoices = invoices.replace("}{", "},{");
				invoices = "[" + invoices + "]";
				JSONArray array = (JSONArray) JSON.parseArray(invoices);
				Map<String, String> map = FieldMapping.getFieldMapping(new BillingInvoiceVO());
				BillingInvoiceVO[] vos = DzfTypeUtils.cast(array, map, BillingInvoiceVO[].class,
						JSONConvtoJAVA.getParserConfig());
				int len = 0;
				int length = vos == null ? 0 : vos.length;
				StringBuffer msg = new StringBuffer();
				if (vos != null && length > 0) {
					for (BillingInvoiceVO cvo : vos) {
						try {
							billingQueryServiceImpl.insertBilling(cvo);
							len++;
						} catch (BusinessException e) {
							msg.append("加盟商：").append(cvo.getCorpname()).append(",失败原因：").append(e.getMessage())
									.append("<br>");
						} catch (Exception e) {
							log.error("开票失败", e);
						}
					}
				}
				json.setMsg("成功生成" + len + "张单据，失败" + (length - len) + "张单据<br>  " + msg.toString());
				json.setSuccess(true);
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_JMSKPCX.getValue(), "开票成功" + len + "张", ISysConstants.SYS_3);
			} else {
				json.setSuccess(false);
				json.setRows(0);
				json.setMsg("请选择数据");
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "生成失败");
		}
		writeJson(json);
	}

	/**
	 * 导出
	 */
	public void onExport() {
		String strlist = getRequest().getParameter("strlist");
		String bdate = getRequest().getParameter("bdate");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new BillingInvoiceVO());
		BillingInvoiceVO[] expVOs = DzfTypeUtils.cast(exparray, mapping, BillingInvoiceVO[].class,
				JSONConvtoJAVA.getParserConfig());
		ArrayList<BillingInvoiceVO> explist = new ArrayList<BillingInvoiceVO>();
		for (BillingInvoiceVO vo : expVOs) {
			explist.add(vo);
		}
		HttpServletResponse response = getResponse();
		Excelexport2003<BillingInvoiceVO> ex = new Excelexport2003<BillingInvoiceVO>();
		BillingExcelField fields = new BillingExcelField();
		fields.setVos(explist.toArray(new BillingInvoiceVO[0]));
		;
		fields.setQj(bdate);
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
		writeLogRecord(LogRecordEnum.OPE_CHANNEL_JMSKPCX.getValue(), "导出加盟商开票查询表", ISysConstants.SYS_3);
	}

	/**
	 * 扣款明细
	 * 
	 * @author gejw
	 * @time 上午10:15:41
	 */
	public void queryRecDetail() {
		Json grid = new Json();
		try {
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if (pamvo.getBegdate() == null) {
				pamvo.setBegdate(new DZFDate());
			}
			List<ChnDetailRepVO> list = recPayDetailService.queryRecDetail(pamvo);
			if (list != null && list.size() > 0) {
				ChnDetailRepVO[] vos = getPagedVOs(list.toArray(new ChnDetailRepVO[0]), pamvo.getPage(),
						pamvo.getRows());
				grid.setRows(vos);
				grid.setTotal((long) list.size());
			} else {
				grid.setTotal(0L);
				grid.setRows(new ArrayList<ChnDetailRepVO>());
			}
			grid.setMsg("查询成功！");
			grid.setSuccess(true);

			writeLogRecord(LogRecordEnum.OPE_CHANNEL_JMSKPCX.getValue(), "扣款明细查询成功", ISysConstants.SYS_3);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	private ChnDetailRepVO[] getPagedVOs(ChnDetailRepVO[] cvos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= cvos.length) {// 防止endIndex数组越界
			endIndex = cvos.length;
		}
		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
		return cvos;
	}

	public void onRecPrint() {
		try {
			String strlist = getRequest().getParameter("strlist");
			String columns = getRequest().getParameter("columns");
			String qrydate = getRequest().getParameter("qrydate");
			String corpnm = getRequest().getParameter("corpnm");

			JSONArray array = (JSONArray) JSON.parseArray(strlist);
			JSONArray headlist = (JSONArray) JSON.parseArray(columns);
			List<String> heads = new ArrayList<String>();
			List<String> fieldslist = new ArrayList<String>();
			ArrayList<String> listData = new ArrayList<>();
			listData.add("查询：" + qrydate);
			listData.add("加盟商：" + corpnm);

			Map<String, String> name = null;
			int[] widths = new int[] {};
			if (strlist == null) {
				return;
			}
			for (int i = 0; i < headlist.size(); i++) {
				name = (Map<String, String>) headlist.get(i);
				heads.add(name.get("title"));
				fieldslist.add(name.get("field"));
				widths = ArrayUtils.addAll(widths, new int[] { 3 });
			}
			// 字符类型字段(取界面元素id)
			List<String> list = new ArrayList<String>();
			list.add("ddate");
			list.add("memo");
			list.add("propor");
			String[] fields = (String[]) fieldslist.toArray(new String[fieldslist.size()]);
			PrintUtil<BillingQueryAction> util = new PrintUtil<BillingQueryAction>();
			util.setIscross(DZFBoolean.TRUE);
			util.printMultiColumn(array, "扣款明细", heads, fields, widths, 20, list, listData);

		} catch (DocumentException e) {
			throw new WiseRunException(e);
		} catch (IOException e) {
			throw new WiseRunException(e);
		}
	}

	public void onRecExport() {
		String strlist = getRequest().getParameter("strlist");
		String columns = getRequest().getParameter("columns");

		String qrydate = getRequest().getParameter("qrydate");
		String corpnm = getRequest().getParameter("corpnm");
		String ptypenm = getRequest().getParameter("ptypenm");

		JSONArray array = (JSONArray) JSON.parseArray(strlist);
		JSONArray headlist = (JSONArray) JSON.parseArray(columns);
		List<String> heads = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		Map<String, String> pmap = new HashMap<String, String>();
		pmap.put("查询", "查询：" + qrydate);
		pmap.put("加盟商", "加盟商：" + corpnm);
		// 字符类型字段(取界面元素id)
		List<String> stringlist = new ArrayList<String>();
		stringlist.add("ddate");
		stringlist.add("memo");
		stringlist.add("propor");
		Map<String, String> name = null;
		if (strlist == null) {
			return;
		}
		for (int i = 0; i < headlist.size(); i++) {
			name = (Map<String, String>) headlist.get(i);
			heads.add(name.get("title"));
			fieldslist.add(name.get("field"));
		}
		ExportUtil<BillingQueryAction> ex = new ExportUtil<BillingQueryAction>();
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
			byte[] length = ex.exportExcel("扣款明细", heads, fieldslist, array, toClient, "", stringlist, pmap);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
		} catch (IOException e) {
			log.error(e);
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
