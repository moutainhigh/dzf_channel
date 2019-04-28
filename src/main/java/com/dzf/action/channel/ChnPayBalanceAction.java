package com.dzf.action.channel;

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
import com.dzf.action.channel.expfield.ChnPayBalExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.dao.jdbc.framework.util.InOutUtil;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.payment.ChnBalanceRepVO;
import com.dzf.model.channel.payment.ChnDetailRepVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.IChnPayBalanceService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.pub.report.ExportUtil;
import com.dzf.service.pub.report.PrintUtil;
import com.itextpdf.text.DocumentException;

/**
 * 付款余额
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/chnpay")
@Action(value = "chnpaybalance")
public class ChnPayBalanceAction extends BaseAction<ChnBalanceVO> {

	private static final long serialVersionUID = -750029782321770916L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IChnPayBalanceService paybalanSer;

	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			if (paramvo != null) {
				paramvo.setCuserid(getLoginUserid());
			}
			List<ChnBalanceRepVO> clist = paybalanSer.query(paramvo);
			int page = paramvo == null ? 1 : paramvo.getPage();
			int rows = paramvo == null ? 10000 : paramvo.getRows();
			int len = clist == null ? 0 : clist.size();
			if (len > 0) {
				grid.setTotal((long) (len));
				ChnBalanceRepVO[] balVOs = clist.toArray(new ChnBalanceRepVO[0]);
				balVOs = (ChnBalanceRepVO[]) QueryUtil.getPagedVOs(balVOs, page, rows);
				grid.setRows(Arrays.asList(balVOs));
				grid.setSuccess(true);
				grid.setMsg("操作成功");
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_3.getValue(), "付款余额表查询", ISysConstants.SYS_3);
			} else {
				grid.setTotal(Long.valueOf(0));
				grid.setRows(new ArrayList<ChnBalanceRepVO>());
				grid.setMsg("操作结果为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 明细查询方法
	 */
	public void queryDetail() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			List<ChnDetailRepVO> clist = paybalanSer.queryDetail(paramvo);
			grid.setRows(clist);
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
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
		Map<String, String> mapping = FieldMapping.getFieldMapping(new ChnBalanceRepVO());
		ChnBalanceRepVO[] expVOs = DzfTypeUtils.cast(exparray, mapping, ChnBalanceRepVO[].class,
				JSONConvtoJAVA.getParserConfig());
		ArrayList<ChnBalanceRepVO> explist = new ArrayList<ChnBalanceRepVO>();
		for (ChnBalanceRepVO vo : expVOs) {
			explist.add(vo);
		}
		HttpServletResponse response = getResponse();
		Excelexport2003<ChnBalanceRepVO> ex = new Excelexport2003<ChnBalanceRepVO>();
		ChnPayBalExcelField fields = new ChnPayBalExcelField();
		fields.setVos(explist.toArray(new ChnBalanceRepVO[0]));
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
		} catch (Exception e) {
			log.error("导出失败", e);
		} finally {
		    InOutUtil.close(toClient, "ChnPayBalanceAction关闭流");
			InOutUtil.close(servletOutputStream, "ChnPayBalanceAction关闭流");
		}
	}

	/**
	 * 打印
	 */
	@SuppressWarnings("unchecked")
	public void print() {
		try {
			String strlist = getRequest().getParameter("strlist");
			if (strlist == null) {
				throw new BusinessException("打印数据不能为空!");
			}
			String columns = getRequest().getParameter("columns");
			JSONArray array = (JSONArray) JSON.parseArray(strlist);
			JSONArray headlist = (JSONArray) JSON.parseArray(columns);
			List<String> heads = new ArrayList<String>();
			List<String> fieldslist = new ArrayList<String>();

			Map<String, String> name = null;
			int[] widths = new int[] {};
			int len = headlist.size();
			for (int i = 0; i < len; i++) {
				name = (Map<String, String>) headlist.get(i);
				heads.add(name.get("title"));
				fieldslist.add(name.get("field"));
				widths = ArrayUtils.addAll(widths, new int[] { 3 });
			}
			String[] fields = (String[]) fieldslist.toArray(new String[fieldslist.size()]);

			// 字符类型字段(取界面元素id)
			List<String> list = new ArrayList<String>();
			list.add("aname");
			list.add("incode");
			list.add("corpnm");
			list.add("custnum");
			list.add("zeronum");
			list.add("dednum");
			list.add("ptypenm");
			list.add("propor");
			list.add("mname");
			PrintUtil<ChnPayBalanceAction> util = new PrintUtil<ChnPayBalanceAction>();
			util.setIscross(DZFBoolean.TRUE);
			util.printMultiColumn(array, "付款单余额", heads, fields, widths, 20, list, null);
		} catch (Exception e) {
			log.error("打印失败", e);
		}
	}

	/**
	 * 明细打印
	 */
	public void onDetPrint() {
		try {
			String strlist = getRequest().getParameter("strlist");
			String columns = getRequest().getParameter("columns");
			String qrydate = getRequest().getParameter("qrydate");
			String corpnm = getRequest().getParameter("corpnm");
			String ptypenm = getRequest().getParameter("ptypenm");
			String mname = getRequest().getParameter("mname");
			if(StringUtil.isEmpty(mname)){
				mname = "";
			}

			JSONArray array = (JSONArray) JSON.parseArray(strlist);
			JSONArray headlist = (JSONArray) JSON.parseArray(columns);
			List<String> heads = new ArrayList<String>();
			List<String> fieldslist = new ArrayList<String>();
			ArrayList<String> listData = new ArrayList<>();
			listData.add("查询：" + qrydate);
			listData.add("加盟商：" + corpnm);
			listData.add("付款类型：" + ptypenm);
			listData.add("渠道经理：" + mname);

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
			list.add("corpknm");
			list.add("oldname");
			list.add("vccode");
			String[] fields = (String[]) fieldslist.toArray(new String[fieldslist.size()]);
			PrintUtil<ChnPayBalanceAction> util = new PrintUtil<ChnPayBalanceAction>();
			util.setIscross(DZFBoolean.TRUE);
			util.printMultiColumn(array, "付款单余额明细", heads, fields, widths, 20, list, listData);

		} catch (DocumentException e) {
			throw new WiseRunException(e);
		} catch (IOException e) {
			throw new WiseRunException(e);
		}
	}

	/**
	 * 明细导出
	 */
	public void onDetExport() {
		String strlist = getRequest().getParameter("strlist");
		String columns = getRequest().getParameter("columns");

		String qrydate = getRequest().getParameter("qrydate");
		String corpnm = getRequest().getParameter("corpnm");
		String ptypenm = getRequest().getParameter("ptypenm");
		
		String mname = getRequest().getParameter("mname");
		if(StringUtil.isEmpty(mname)){
			mname = "";
		}

		JSONArray array = (JSONArray) JSON.parseArray(strlist);
		JSONArray headlist = (JSONArray) JSON.parseArray(columns);
		List<String> heads = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		Map<String, String> pmap = new HashMap<String, String>();
		pmap.put("查询", "查询：" + qrydate);
		pmap.put("加盟商", "加盟商：" + corpnm);
		pmap.put("付款类型", "付款类型：" + ptypenm);
		pmap.put("渠道经理", "渠道经理：" + mname);
		// 字符类型字段(取界面元素id)
		List<String> stringlist = new ArrayList<String>();
		stringlist.add("ddate");
		stringlist.add("memo");
		stringlist.add("propor");
		stringlist.add("corpknm");
		stringlist.add("oldname");
		stringlist.add("vccode");
		Map<String, String> name = null;
		if (strlist == null) {
			return;
		}
		for (int i = 0; i < headlist.size(); i++) {
			name = (Map<String, String>) headlist.get(i);
			heads.add(name.get("title"));
			fieldslist.add(name.get("field"));
		}
		ExportUtil<ChnPayBalanceAction> ex = new ExportUtil<ChnPayBalanceAction>();
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
			byte[] length = ex.exportExcel("付款单余额明细", heads, fieldslist, array, toClient, "", stringlist, pmap);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
		} catch (IOException e) {
			log.error(e);
		} finally {
		    InOutUtil.close(toClient, "ChnPayBalanceAction明细导出关闭流");
		    InOutUtil.close(servletOutputStream, "ChnPayBalanceAction明细导出关闭流");
		}
	}

}
