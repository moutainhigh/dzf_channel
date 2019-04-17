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
import com.dzf.model.channel.report.LogisticRepVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.channel.report.ILogisticRepService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.report.ExportExcel;

/**
 * 快递统计表
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "logistic")
public class LogisticRepAction extends BaseAction<LogisticRepVO>{

	@Autowired
	private ILogisticRepService logistic;
	
	@Autowired
	private IPubService pubService;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 查询商品数据
	 */
	public void queryGoods() {
		Grid grid = new Grid();
		try {
			QryParamVO qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			UserVO uservo = getLoginUserInfo();
			List<LogisticRepVO> retlist = new ArrayList<>();
			String powSql = pubService.makeCondition(uservo.getCuserid(), qvo.getAreaname(), 3);
			qvo.setPage(1);
			qvo.setRows(10000);
			if (powSql != null && !powSql.equals("alldata")) {
				qvo.setVqrysql(powSql);
				retlist = logistic.queryGoods(qvo);
			} else if (powSql != null) {
				retlist = logistic.queryGoods(qvo);
			}
			grid.setRows(retlist);
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询物料数据
	 */
	public void queryMateriel() {
		Grid grid = new Grid();
		try {
			QryParamVO qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			UserVO uservo = getLoginUserInfo();
			List<LogisticRepVO> retlist = new ArrayList<>();
			String powSql = pubService.makeCondition(uservo.getCuserid(), qvo.getAreaname(), 3);
			qvo.setPage(1);
			qvo.setRows(10000);
			if (powSql != null && !powSql.equals("alldata")) {
				qvo.setVqrysql(powSql);
				retlist = logistic.queryMateriel(qvo);
			} else if (powSql != null) {
				retlist = logistic.queryMateriel(qvo);
			}
			grid.setRows(retlist);
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询商品表头
	 */
	public void qryGoodsHead() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			List<ComboBoxVO> list = logistic.qryGoodsHead();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<ComboBoxVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询物料表头
	 */
	public void qryMaterHead() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			List<ComboBoxVO> list = logistic.qryMaterHead();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<ComboBoxVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	public void exportLogistic(){
		String reportName = "快递统计表 ";
		
		// 获取需要导出数据
		String strlist = getRequest().getParameter("strlist");
		if (StringUtil.isEmpty(strlist)) {
			return;
		}
		JSONArray array = (JSONArray) JSON.parseArray(strlist);

		// 第一行单元格元素
		String head0 = getRequest().getParameter("head0");
		JSONArray head0array = (JSONArray) JSON.parseArray(head0);// title+field
		
		// 第二行单元格元素
		String head1 = getRequest().getParameter("head1");
		JSONArray head1array = (JSONArray) JSON.parseArray(head1);// title+field

		// 导出字段编码
		String cols = getRequest().getParameter("cols");
		JSONArray colsarray = (JSONArray) JSON.parseArray(cols);// 字段编码

		// 1、导出字段名称
		List<String> exptitlist = new ArrayList<String>();

		// 2、导出字段编码
		List<String> expfieidlist = new ArrayList<String>();

		// 3、合并列名称
		List<String> hbltitlist = new ArrayList<String>();

		// 4、合并列字段下标
		List<Integer> hblindexlist = new ArrayList<Integer>();

		// 5、合并行字段名称
		List<String> hbhtitlist = new ArrayList<String>();

		// 7、字符集合
		List<String> strslist = new ArrayList<String>();

		// 8、金额集合
		List<String> mnylist = new ArrayList<String>();
		mnylist.add("fcost");

		Map<String, String> field = null;
		
		List<Integer> hbhindexs = new ArrayList<Integer>();

		int start = 0 ;
		int colspan = 0;
		int w = 0;
		int hblindex = 0;
		for (int i = 0; i < head0array.size(); i++) {
			field = (Map<String, String>) head0array.get(i);
			if ("2".equals(String.valueOf(field.get("rowspan")))) {//合并行 列
				exptitlist.add(String.valueOf(field.get("title")));
				expfieidlist.add(String.valueOf(field.get("field")));
				strslist.add(String.valueOf(field.get("field")));
				
				hbhtitlist.add(field.get("title"));
				if(w!=3){
					hbhindexs.add(i);
				}else{
					hbhindexs.add(i-3+head1array.size());
				}
				w++;
			}else{
				hbltitlist.add(String.valueOf(field.get("title")));
				if(hblindex==0){
					hblindex = i+colspan;
				}else{
					hblindex += colspan;
				}
				hblindexlist.add(hblindex);
				colspan = Integer.valueOf(String.valueOf(field.get("colspan")));
				for(int j = start; j < start+colspan; j++){
					field = (Map<String, String>) head1array.get(j);
					exptitlist.add(String.valueOf(field.get("title")));
					expfieidlist.add(String.valueOf(field.get("field")));
					if(!"fcost".equals(String.valueOf(field.get("field")))){
						strslist.add(String.valueOf(field.get("field")));
					}
				}
				start += colspan;
			}
		}
		ExportExcel<LogisticRepVO> ex = new ExportExcel<LogisticRepVO>();
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			HttpServletResponse response = getResponse();
			response.reset();
			String date = DateUtils.getDate(new Date());
			String fileName = null;
			String userAgent = getRequest().getHeader("user-agent");
			if (!StringUtil.isEmpty(userAgent) && (userAgent.indexOf("Firefox") >= 0 || userAgent.indexOf("Chrome") >= 0
					|| userAgent.indexOf("Safari") >= 0)) {
				fileName = new String((reportName).getBytes(), "ISO8859-1");
			} else {
				fileName = URLEncoder.encode(reportName, "UTF8"); // 其他浏览器
			}
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.exportLogistic(reportName, exptitlist, expfieidlist, hbltitlist, hblindexlist, hbhtitlist,
					hbhindexs, array, toClient, "", strslist, mnylist);
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
