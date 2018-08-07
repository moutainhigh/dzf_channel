package com.dzf.action.channel.report;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
import com.dzf.action.channel.ChnPayBalanceAction;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IButtonName;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.channel.report.IManagerService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.pub.report.ExportExcel;
import com.dzf.service.pub.report.ExportUtil;
import com.dzf.service.pub.report.PrintUtil;
import com.itextpdf.text.DocumentException;

/**
 * 加盟商管理
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "manager")
public class ManagerAction extends PrintUtil<ManagerVO>{

	@Autowired
	private IManagerService manager;
	
	@Autowired
	private IPubService pubService;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 查询主表数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			ManagerVO qvo = new ManagerVO();
			Integer type = Integer.parseInt(getRequest().getParameter("type"));
			qvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setUserid(getLoginUserid());
			List<ManagerVO> vos = manager.query(qvo,type);
			if(vos==null||vos.size()==0){
				grid.setRows(new ArrayList<ChnAreaVO>());
				grid.setMsg("查询数据为空!");
			}else{
				grid.setRows(vos);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
				if(type==3){
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_QDZSJFX.getValue(), "渠道总数据分析查询成功", ISysConstants.SYS_3);
				}else if(type==2){
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_DQSJFX.getValue(), "大区总数据分析查询成功", ISysConstants.SYS_3);
				}else{
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_SSJFX.getValue(), "省数据分析查询成功", ISysConstants.SYS_3);
				}
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 明细查询方法
	 */
	public void queryDetail() {
		Grid grid = new Grid();
		try {
			ManagerVO paramvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), new ManagerVO());
			List<ManagerVO> clist = manager.queryDetail(paramvo);
			grid.setRows(clist);
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}
	
	/**
	 * Excel导出方法  1渠道；2区域；3总
	 */
	public void exportExcel(){
		Integer type = Integer.parseInt(getRequest().getParameter("type"));
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		String columns =getRequest().getParameter("columns");
		JSONArray headlist = (JSONArray) JSON.parseArray(columns);
		List<String> heads = new ArrayList<String>();
		List<String> heads1 = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		Map<String, String> name = null;
		List<String> fieldlist = new ArrayList<String>();
		String funnode=null;
		int num=8;
		String title="省数据分析";
		switch (type) {
		case 1:
			funnode= IFunNode.CHANNEL_16;
			break;
		case 2:
			title="大区数据分析";
			funnode= IFunNode.CHANNEL_17;
			break;
		default:
			num=10;
			title="渠道总数据分析";
			fieldlist.add("aname");
			fieldlist.add("uname");
			funnode= IFunNode.CHANNEL_18;
			break;
		}
		pubService.checkButton(getLoginUserInfo(), funnode,IButtonName.BTN_EXPORT);
		fieldlist.add("provname");
		fieldlist.add("cuname");
		fieldlist.add("corpnm");
		fieldlist.add("xgmNum");
		fieldlist.add("ybrNum");
		fieldlist.add("anum");
		fieldlist.add("rnum");
		for (int i = 0 ; i< headlist.size(); i ++) {
			 name=(Map<String, String>) headlist.get(i);
			 if(i==num){
				 fieldslist.add("rnum");
				 fieldslist.add("anum");
				 heads.add("续费");
				 heads.add("新增");
			 }else if(i==num+1){
				 fieldslist.add("rntlmny");
				 fieldslist.add("antlmny");
				 heads.add("续费");
				 heads.add("新增");
			 }else{
				 heads.add(name.get("title"));
				 fieldslist.add(name.get("field"));
			 }
		}
		heads1.add("提单量");
		heads1.add("合同代账费");
		ExportExcel<ManagerVO> ex =new ExportExcel();
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			HttpServletResponse response = getResponse();
			response.reset();
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(date+".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.exportManExcel(title,heads,heads1,fieldslist ,exparray,toClient,"",fieldlist,num);
			String srt2=new String(length,"UTF-8");
			response.addHeader("Content-Length", srt2);
			if(type==3){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_QDZSJFX.getValue(), "导出渠道总数据分析表", ISysConstants.SYS_3);
			}else if(type==2){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_DQSJFX.getValue(), "导出大区总数据分析表", ISysConstants.SYS_3);
			}/*else{
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_SSJFX.getValue(), "导出省数据分析表", ISysConstants.SYS_3);
			}*/
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if(toClient != null){
				try {
					toClient.flush();
					toClient.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
			if(servletOutputStream != null){
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}
	
	/**
	 * 明细打印
	 */
	public void onDetPrint() {
		try{
			String strlist =getRequest().getParameter("strlist");
			String columns =getRequest().getParameter("columns");
			String qrydate =getRequest().getParameter("qrydate");
			String corpnm =getRequest().getParameter("corpnm");
			JSONArray array = (JSONArray) JSON.parseArray(strlist);
			JSONArray headlist = (JSONArray) JSON.parseArray(columns);
			List<String> heads = new ArrayList<String>();
			List<String> fieldslist = new ArrayList<String>();
			ArrayList<String> listData = new ArrayList<>();
            listData.add("查询："+qrydate);
            listData.add("加盟商："+corpnm);
			Map<String, String> name = null;
			int[] widths =new  int[]{};
			if(strlist==null){
				return;
			}
			for (int i = 0 ; i< headlist.size(); i ++) {
				 name=(Map<String, String>) headlist.get(i);
				 heads.add(name.get("title"));
				 fieldslist.add(name.get("field"));
				 widths =ArrayUtils.addAll(widths, new int[] {3}); 
			}
			//字符类型字段(取界面元素id)
			List<String> list = new ArrayList<String>();
			list.add("edate");
			list.add("num");
			String[] fields= (String[]) fieldslist.toArray(new String[fieldslist.size()]);
			PrintUtil<ManagerVO> util = new PrintUtil<ManagerVO>();
			util.setIscross(DZFBoolean.TRUE);
			util.printMultiColumn(array, "合同金额明细", heads, fields, widths, 20, list, listData);
		}catch(DocumentException e){
			throw new WiseRunException(e);
		}catch(IOException e){
			throw new WiseRunException(e);
		}
	}
	
	/**
	 * 明细导出
	 */
	public void onDetExport(){
		String strlist =getRequest().getParameter("strlist");
		String columns =getRequest().getParameter("columns");
		String qrydate =getRequest().getParameter("qrydate");
		String corpnm =getRequest().getParameter("corpnm");
		JSONArray array = (JSONArray) JSON.parseArray(strlist);
		JSONArray headlist = (JSONArray) JSON.parseArray(columns);
		List<String> heads = new ArrayList<String>();
		List<String> fieldslist = new ArrayList<String>();
		Map<String, String> pmap = new HashMap<String, String>();
		pmap.put("查询", "查询："+qrydate);
        pmap.put("加盟商", "加盟商：" + corpnm);
		//字符类型字段(取界面元素id)
		List<String> stringlist = new ArrayList<String>();
		stringlist.add("edate");
		Map<String, String> name = null;
		if(strlist == null){
			return;
		}
		for (int i = 0 ; i< headlist.size(); i ++) {
			 name=(Map<String, String>) headlist.get(i);
			 heads.add(name.get("title"));
			 fieldslist.add(name.get("field"));
		}
		ExportUtil<ChnPayBalanceAction> ex =new ExportUtil<ChnPayBalanceAction>();
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			HttpServletResponse response = getResponse();
			response.reset();
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename="
					+ new String(date+".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.exportExcel("合同金额明细",heads,fieldslist ,array, toClient,"",stringlist,pmap);
			String srt2=new String(length,"UTF-8");
			response.addHeader("Content-Length", srt2);
		} catch (IOException e) {
			log.error(e);
		} finally {
			if(toClient != null){
				try {
					toClient.flush();
					toClient.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
			if(servletOutputStream != null){
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
