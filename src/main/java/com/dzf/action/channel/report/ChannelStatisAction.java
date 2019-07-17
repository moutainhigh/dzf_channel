package com.dzf.action.channel.report;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.channel.report.IChannelStatisService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.report.ExportExcel;

/**
 * 渠道业绩统计
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "channelStatis")
public class ChannelStatisAction extends BaseAction<ManagerVO> {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IChannelStatisService chnStatis;

	@Autowired
	private IPubService pubser;

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_56);
			ManagerVO qvo = new ManagerVO();
			qvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setCuserid(uservo.getCuserid());
			List<ManagerVO> list = chnStatis.query(qvo);
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<ManagerVO>());
				grid.setMsg("查询数据为空!");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
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
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_56);
			ManagerVO paramvo = (ManagerVO) DzfTypeUtils.cast(getRequest(), new ManagerVO());
			List<ManagerVO> clist = chnStatis.queryDetail(paramvo);
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
	public void exportAuditExcel() {
		String strlist = getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if (StringUtil.isEmpty(strlist)) {
			throw new BusinessException("导出数据不能为空!");
		}
		JSONArray array = (JSONArray) JSON.parseArray(strlist);

		// 1、导出字段名称
		List<String> conameList = new ArrayList<String>();
		conameList.add("渠道经理");
		conameList.add("加盟商编码");
		conameList.add("加盟商名称");
		conameList.add("预付款扣款");
		conameList.add("返点扣款");

		// 2、导出字段编码
		List<String> codeList = new ArrayList<String>();
		codeList.add("uname");
		codeList.add("vccode");
		codeList.add("corpnm");
		codeList.add("ndemny");
		codeList.add("nderebmny");

		// 3、金额集合
		List<String> mnylist = new ArrayList<String>();
		mnylist.add("ndemny");
		mnylist.add("nderebmny");

		// 4、字符集合
		List<String> strslist = new ArrayList<String>();
		strslist.add("uname");
		strslist.add("vccode");
		strslist.add("corpnm");

		ExportExcel<ManagerVO> ex = new ExportExcel<ManagerVO>();
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
				fileName = new String(("渠道业绩统计表").getBytes(), "ISO8859-1");
			} else {
				fileName = URLEncoder.encode("渠道业绩统计表", "UTF8"); // 其他浏览器
			}
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.expChannelStatis("渠道业绩统计表", conameList, codeList, array, toClient, mnylist, strslist);
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
