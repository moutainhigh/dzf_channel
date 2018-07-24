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
import com.dzf.model.channel.report.DeductAnalysisVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.channel.report.IDeductAnalysis;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.pub.report.ExportExcel;

/**
 * 加盟商扣款分析
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "deductanalysis")
public class DeductAnalysisAction extends BaseAction<DeductAnalysisVO>{

	private static final long serialVersionUID = -1632075355173038501L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IDeductAnalysis analyser;

	/**
	 * 查询金额数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			List<DeductAnalysisVO> list = analyser.query(paramvo);
			if(list != null && list.size() > 0){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_29.getValue(), "扣款统计表查询成功", ISysConstants.SYS_3);
			}
			grid.setRows(list);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询金额排序数据
	 */
	public void queryMnyOrder() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			List<DeductAnalysisVO> vos = analyser.queryMnyOrder(paramvo);
			grid.setRows(vos);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 导出excel
	 */
	@SuppressWarnings("unchecked")
	public void export() {
		// 获取需要导出数据
		String strlist = getRequest().getParameter("strlist");
		if (StringUtil.isEmpty(strlist)) {
			return;
		}
		JSONArray array = (JSONArray) JSON.parseArray(strlist);
		
		String hblcols = getRequest().getParameter("hblcols");
		JSONArray hblcolsarray = (JSONArray) JSON.parseArray(hblcols);//合并列信息
		
		String hbhcols = getRequest().getParameter("hbhcols");
		JSONArray hbhcolsarray = (JSONArray) JSON.parseArray(hbhcols);//冻结列编码
		
		String cols = getRequest().getParameter("cols");
		JSONArray colsarray = (JSONArray) JSON.parseArray(cols);//除冻结列之外，导出字段编码
		
		//1、导出字段名称
		List<String> exptitlist = new ArrayList<String>();
		exptitlist.add("加盟商编码");
		exptitlist.add("加盟商");
		exptitlist.add("存量客户");
		exptitlist.add("存量合同数");
		exptitlist.add("0扣款(非存量)合同数");
		exptitlist.add("非存量合同数");
		exptitlist.add("总扣款");
		
		//2、导出字段编码
		List<String> expfieidlist = new ArrayList<String>();
		//3、合并列字段名称
		List<String> hbltitlist = new ArrayList<String>();
		//4、合并列字段下标
		List<Integer> hblindexlist = new ArrayList<Integer>();
		//5、合并行字段名称
		List<String> hbhtitlist = new ArrayList<String>();
		hbhtitlist.add("加盟商编码");
		hbhtitlist.add("加盟商");
		hbhtitlist.add("存量客户");
		hbhtitlist.add("存量合同数");
		hbhtitlist.add("0扣款(非存量)合同数");
		hbhtitlist.add("非存量合同数");
		hbhtitlist.add("总扣款");
		//6、合并行字段下标
		Integer[] hbhindexs = new Integer[]{0,1,2,3,4,5,6};
		//7、字符集合
		List<String> strslist = new ArrayList<String>();
		strslist.add("corpcode");
		strslist.add("corpname");
		strslist.add("stocknum");
		strslist.add("custnum");
		strslist.add("zeronum");
		strslist.add("dednum");
		//8、金额集合
		List<String> mnylist = new ArrayList<String>();
		mnylist.add("mny");
		
		Map<String, String> field = null;
		for(int i = 1; i < hbhcolsarray.size(); i++){
			expfieidlist.add(hbhcolsarray.getString(i));
		}
		
		for(int i = 0; i < colsarray.size(); i++){
			
			expfieidlist.add(String.valueOf(colsarray.get(i)));
			if(i % 2 == 0){
				strslist.add(String.valueOf(colsarray.get(i)));
				exptitlist.add("合同数");
			}else{
				mnylist.add(String.valueOf(colsarray.get(i)));
				exptitlist.add("金额");
			}
		}
		int j = 7;
		for(int i = 0; i < hblcolsarray.size(); i++){
			field = (Map<String, String>) hblcolsarray.get(i);
			hbltitlist.add(String.valueOf(field.get("title")));
			hblindexlist.add(i+j);
			j++;
		}
		
		
		ExportExcel<DeductAnalysisVO> ex = new ExportExcel<DeductAnalysisVO>();
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			HttpServletResponse response = getResponse();
			response.reset();
			String date = DateUtils.getDate(new Date());
			String fileName = null;
			String userAgent = getRequest().getHeader("user-agent");  
            if (!StringUtil.isEmpty(userAgent) && ( userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0   
                    || userAgent.indexOf("Safari") >= 0 ) ) {  
                fileName= new String(("扣款统计表").getBytes(), "ISO8859-1");  
            } else {  
                fileName=URLEncoder.encode("扣款统计表","UTF8"); //其他浏览器  
            }  
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName//new String("扣款统计表".getBytes("UTF-8"),"ISO8859-1")
					+ new String(date+".xls"));
//			response.addHeader("Content-Disposition", "attachment;filename=" + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.expDeductExcel("扣款统计表", exptitlist, expfieidlist, hbltitlist, hblindexlist, hbhtitlist,
					hbhindexs, array, toClient, "",strslist,mnylist);
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
