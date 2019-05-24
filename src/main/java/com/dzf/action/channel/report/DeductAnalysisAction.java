package com.dzf.action.channel.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.dzf.model.channel.report.ReportDataGrid;
import com.dzf.model.channel.report.ReportDatagridColumn;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.SafeCompute;
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
		ReportDataGrid grid = new ReportDataGrid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			if(paramvo == null){
				paramvo = new QryParamVO();
			}
			DZFDateTime btime = new DZFDateTime();
			Object[] objs = analyser.queryColumn(paramvo);
			if(objs != null && objs.length > 0){
				grid.setHbcolumns((List<ReportDatagridColumn>) objs[0]);
				grid.setColumns((List<ReportDatagridColumn>) objs[1]);
			}
			DZFDateTime b1time = new DZFDateTime();
			List<DeductAnalysisVO> list = queryData(paramvo);
			DZFDateTime b2time = new DZFDateTime();
			if(list != null && list.size() > 0){
				List<String> nslist = getNoSumList();
				HashMap<String, Object> smap = new HashMap<String, Object>();
				String str = "[";
				HashMap<String, Object> map = null;
				Integer num = 0;
				Integer addnum = 0;
				DZFDouble mny = DZFDouble.ZERO_DBL;
				DZFDouble addmny = DZFDouble.ZERO_DBL;
				for (DeductAnalysisVO dvo : list) {
					map = dvo.getHash();
					if(map != null && !map.isEmpty()){
						str = toJson(str, map);
						for(String key : map.keySet()){
							if(!nslist.contains(key)){
								if(!smap.containsKey(key)){
									if(key.indexOf("mny") != -1){
										mny = (DZFDouble) map.get(key);
										smap.put(key, mny.setScale(2, DZFDouble.ROUND_HALF_UP));
									}else{
										smap.put(key, map.get(key));
									}
								}else{
									if(key.indexOf("mny") != -1){
										mny = (DZFDouble) smap.get(key);
										addmny = CommonUtil.getDZFDouble(map.get(key));
										mny = SafeCompute.add(mny, addmny);
										smap.put(key, mny.setScale(2, DZFDouble.ROUND_HALF_UP));
									}else{
										num = (Integer) smap.get(key);
										addnum = CommonUtil.getInteger(map.get(key));
										num = num + addnum;
										smap.put(key, num);
									}
								}
							}
						}
					}
				}
				str = str + "]";
				grid.setRowdata(str);
				if(smap != null && !smap.isEmpty()){
					String sumstr =  "[";
					sumstr = toJson(sumstr, smap);
					sumstr = sumstr + "]";
					grid.setSumdata(sumstr);
				}
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_29.getValue(), "扣款统计表查询成功", ISysConstants.SYS_3);
			}else{
				grid.setRows(new ArrayList<DeductAnalysisVO>());
			}
			DZFDateTime etime = new DZFDateTime();
			log.info("开始时间："+btime);
			log.info("获取列结束时间："+b1time);
			log.info("获取数据结束时间："+b2time);
			log.info("结束时间："+etime);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
//	/**
//	 * 查询金额排序数据
//	 */
//	public void queryMnyOrder() {
//		Grid grid = new Grid();
//		try {
//			QryParamVO paramvo = new QryParamVO();
//			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
//			List<DeductAnalysisVO> vos = analyser.queryMnyOrder(paramvo);
//			grid.setRows(vos);
//			grid.setSuccess(true);
//			grid.setMsg("查询成功");
//		} catch (Exception e) {
//			printErrorLog(grid, log, e, "查询失败");
//		}
//		writeJson(grid);
//	}
	
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
		exptitlist.add("渠道经理");
		exptitlist.add("渠道运营");
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
		hbhtitlist.add("渠道经理");
		hbhtitlist.add("渠道运营");
		hbhtitlist.add("加盟商编码");
		hbhtitlist.add("加盟商");
		hbhtitlist.add("存量客户");
		hbhtitlist.add("存量合同数");
		hbhtitlist.add("0扣款(非存量)合同数");
		hbhtitlist.add("非存量合同数");
		hbhtitlist.add("总扣款");
		//6、合并行字段下标
		Integer[] hbhindexs = new Integer[]{0,1,2,3,4,5,6,7,8};
		//7、字符集合
		List<String> strslist = new ArrayList<String>();
		strslist.add("mid");
		strslist.add("oid");
		strslist.add("corpcode");
		strslist.add("corpname");
		strslist.add("stocknum");
		strslist.add("custnum");
		strslist.add("zeronum");
		strslist.add("dednum");
		//8、金额集合
		List<String> mnylist = new ArrayList<String>();
		mnylist.add("summny");
		
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
		int j = 9;
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
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName
					+ new String(date+".xls"));
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
	
//	/**
//	 * 查询列及数据
//	 */
//	@SuppressWarnings("unchecked")
//	public void queryDataCol() {
//		ReportDataGrid grid = new ReportDataGrid();
//		try {
//			QryParamVO paramvo = new QryParamVO();
//			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
//			if(paramvo == null){
//				paramvo = new QryParamVO();
//			}
//			Object[] objs = analyser.queryColumn(paramvo);
//			if(objs != null && objs.length > 0){
//				grid.setHbcolumns((List<ReportDatagridColumn>) objs[0]);
//				grid.setColumns((List<ReportDatagridColumn>) objs[1]);
//			}
//			DZFDateTime btime = new DZFDateTime();
//			List<DeductAnalysisVO> list = queryData(paramvo);
//			if(list != null && list.size() > 0){
//				List<String> nslist = getNoSumList();
//				HashMap<String, Object> smap = new HashMap<String, Object>();
//				String str = "[";
//				HashMap<String, Object> map = null;
//				Integer num = 0;
//				Integer addnum = 0;
//				DZFDouble mny = DZFDouble.ZERO_DBL;
//				DZFDouble addmny = DZFDouble.ZERO_DBL;
//				for (DeductAnalysisVO dvo : list) {
//					map = dvo.getHash();
//					if(map != null && !map.isEmpty()){
//						str = toJson(str, map);
//						for(String key : map.keySet()){
//							if(!nslist.contains(key)){
//								if(!smap.containsKey(key)){
//									if(key.indexOf("mny") != -1){
//										mny = (DZFDouble) map.get(key);
//										smap.put(key, mny.setScale(2, DZFDouble.ROUND_HALF_UP));
//									}else{
//										smap.put(key, map.get(key));
//									}
//								}else{
//									if(key.indexOf("mny") != -1){
//										mny = (DZFDouble) smap.get(key);
//										addmny = CommonUtil.getDZFDouble(map.get(key));
//										mny = SafeCompute.add(mny, addmny);
//										smap.put(key, mny.setScale(2, DZFDouble.ROUND_HALF_UP));
//									}else{
//										num = (Integer) smap.get(key);
//										addnum = CommonUtil.getInteger(map.get(key));
//										num = num + addnum;
//										smap.put(key, num);
//									}
//								}
//							}
//						}
//					}
//				}
//				str = str + "]";
//				grid.setRowdata(str);
//				if(smap != null && !smap.isEmpty()){
//					String sumstr =  "[";
//					sumstr = toJson(sumstr, smap);
//					sumstr = sumstr + "]";
//					grid.setSumdata(sumstr);
//				}
//				writeLogRecord(LogRecordEnum.OPE_CHANNEL_29.getValue(), "扣款统计表查询成功", ISysConstants.SYS_3);
//			}else{
//				grid.setRows(new ArrayList<DeductAnalysisVO>());
//			}
//			DZFDateTime etime = new DZFDateTime();
//			log.info("开始时间："+btime);
//			log.info("结束时间："+etime);
//			grid.setSuccess(true);
//			grid.setMsg("查询成功");
//		} catch (Exception e) {
//			printErrorLog(grid, log, e, "查询失败");
//		}
//		writeJson(grid);
//	}
	
	/**
	 * 获取不需要汇总列编码
	 * @return
	 * @throws DZFWarpException
	 */
	private List<String> getNoSumList() throws DZFWarpException {
		List<String> nlist = new ArrayList<String>();
		nlist.add("corpid");
		nlist.add("corpcode");
		nlist.add("corpname");
		nlist.add("mid");
		nlist.add("oid");
		return nlist;
	}
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<DeductAnalysisVO> queryData(QryParamVO paramvo) throws DZFWarpException{
		StringBuffer qsql = new StringBuffer();// 附加查询条件
		// 1、渠道经理查询条件
		if (!StringUtil.isEmpty(paramvo.getVmanager())) {
			String sql = analyser.getQrySql(paramvo.getVmanager(), IStatusConstant.IQUDAO);
			if (!StringUtil.isEmpty(sql)) {
				qsql.append(sql);
			}
		}
		// 2、渠道运营查询条件
		if (!StringUtil.isEmpty(paramvo.getVoperater())) {
			String sql = analyser.getQrySql(paramvo.getVoperater(), IStatusConstant.IYUNYING);
			if (!StringUtil.isEmpty(sql)) {
				qsql.append(sql);
			}
		}
		if(qsql != null && qsql.length() > 0){
			paramvo.setVqrysql(qsql.toString());
		}
		
		return analyser.queryData(paramvo);
	}
	
	/**
	 * 序列化成Json字符串
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private static String toJson(String str, Map<String, Object> map) {
		str = str + "{";
		for (String key : map.keySet()) {
			if(map.get(key)!=null){
				str = str + "\"" + key + "\"" + ":\"" + map.get(key).toString().replaceAll("\n", "") + "\",";
			}else{
				str = str + "\"" + key + "\"" + ":\"" + map.get(key) + "\",";
			}
		}
		str = str + "},";
		return str;
	}
	
}
