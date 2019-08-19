package com.dzf.action.branch.reportmanage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
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
import com.dzf.action.pub.BaseAction;
import com.dzf.model.branch.reportmanage.CorpDataVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.InOutUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.branch.reportmanage.ISaleCorpDataService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.report.ExportExcel;

/**
 * 销售客户明细
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/branch")
@Action(value = "salecorpdataact")
public class SaleCorpDataAction extends BaseAction<CorpDataVO> {
	
	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ISaleCorpDataService salecorpser;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询
	 */
	public void query(){
		Grid grid = new Grid();
		try {
			checkUser();
			pubser.checkFunnode(getLoginUserInfo(), IFunNode.BRANCH_10);
			QryParamVO pamvo = new QryParamVO();
			pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
			if(pamvo == null){
				pamvo = new QryParamVO();
			}
			if(StringUtil.isEmpty(pamvo.getCuserid())){
				pamvo.setCuserid(getLoginUserid());
			}
			UserVO uservo = getLoginUserInfo();
			if(uservo != null){
				pamvo.setUser_name(uservo.getUser_name());
			}
			if (StringUtil.isEmpty(pamvo.getCorpkname()) && "全部".equals(pamvo.getVmanager())
					&& "全部".equals(pamvo.getVbillcode()) && StringUtil.isEmpty(pamvo.getVoperater())) {
				long total = salecorpser.queryTotal(pamvo);
				if (total > 0) {
					List<CorpDataVO> list = salecorpser.query(pamvo);
					grid.setRows(list);
				} else {
					grid.setRows(new ArrayList<CorpDataVO>());
				}
				grid.setTotal(total);
			}else{
				List<CorpDataVO> list = salecorpser.queryAll(pamvo);
				if(list != null && list.size() > 0){
					CorpDataVO[] cVOs = list.toArray(new CorpDataVO[0]);
					cVOs = (CorpDataVO[]) QueryUtil.getPagedVOs(cVOs, pamvo.getPage(), pamvo.getRows());
					grid.setRows(Arrays.asList(cVOs));
					grid.setTotal((long) list.size());
				}else{
					grid.setRows(new ArrayList<CorpDataVO>());
					grid.setTotal((long) 0);
				}
			}
			grid.setMsg("查询成功");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询会计公司下拉
	 */
	public void queryAccount() {
		Grid grid = new Grid();
		try {
			checkUser();
			List<ComboBoxVO> list = salecorpser.queryAccount(getLoginUserInfo());
			grid.setRows(list);
			grid.setMsg("查询成功");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 获取导出查询数据
	 * @return
	 * @throws DZFWarpException
	 */
	private List<CorpDataVO> getExportQryData() throws DZFWarpException {
		pubser.checkFunnode(getLoginUserInfo(), IFunNode.BRANCH_10);
		QryParamVO pamvo = new QryParamVO();
		pamvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), pamvo);
		if(pamvo == null){
			pamvo = new QryParamVO();
		}
		if(StringUtil.isEmpty(pamvo.getCuserid())){
			pamvo.setCuserid(getLoginUserid());
		}
		UserVO uservo = getLoginUserInfo();
		if(uservo != null){
			pamvo.setUser_name(uservo.getUser_name());
		}
		
		List<CorpDataVO> list = null;
		if (StringUtil.isEmpty(pamvo.getCorpkname()) && "全部".equals(pamvo.getVmanager())
				&& "全部".equals(pamvo.getVbillcode()) && StringUtil.isEmpty(pamvo.getVoperater())) {
			pamvo.setRows(100000);
			list = salecorpser.query(pamvo);
		}else{
			list = salecorpser.queryAll(pamvo);
		}
		return list;
	}
	
	/**
	 * 导出
	 */
	public void onExport() {
		String strlist = getRequest().getParameter("strlist");
		CorpDataVO[] expVOs = null;
		if (!StringUtil.isEmpty(strlist)) {
			JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
			Map<String, String> mapping = FieldMapping.getFieldMapping(new CorpDataVO());
			expVOs = DzfTypeUtils.cast(exparray, mapping, CorpDataVO[].class, JSONConvtoJAVA.getParserConfig());
		} else {
			List<CorpDataVO> list = getExportQryData();
			if(list != null && list.size() > 0){
				expVOs = list.toArray(new CorpDataVO[0]);
			}
		}

		// 1、导出字段名称
		List<String> exptitlist = new ArrayList<String>();
		exptitlist.add("公司名称");
		exptitlist.add("客户编码");
		exptitlist.add("客户名称");
		exptitlist.add("省市");
		exptitlist.add("联系人");
		exptitlist.add("手机号");
		exptitlist.add("纳税人");
		exptitlist.add("建账日期");
		exptitlist.add("记账状态");
		exptitlist.add("报税状态");
		exptitlist.add("开始日期");
		exptitlist.add("结束日期");
		exptitlist.add("总金额");
		exptitlist.add("实收金额");
		exptitlist.add("服务余额/月");
		exptitlist.add("录入日期");

		// 2、导出字段编码
		List<String> expfieidlist = new ArrayList<String>();
		expfieidlist.add("corpname");
		expfieidlist.add("unitcode");
		expfieidlist.add("unitname");
		expfieidlist.add("citycounty");
		expfieidlist.add("linkman2");
		expfieidlist.add("phone2");
		expfieidlist.add("chargedeptname");
		expfieidlist.add("begindate");
		expfieidlist.add("vjzstatues");
		expfieidlist.add("vbsstatues");
		expfieidlist.add("dbegindate");
		expfieidlist.add("denddate");
		expfieidlist.add("ntotalmny");
		expfieidlist.add("nreceivemny");
		expfieidlist.add("isurplusmonth");
		expfieidlist.add("createdate");
		
		
		// 3、合并列字段名称
		List<String> hbltitlist = new ArrayList<String>();
		
		// 4、合并列字段下标
		List<Integer> hblindexlist = new ArrayList<Integer>();
		hbltitlist.add(String.valueOf("合同信息"));
		hblindexlist.add(10);
		// 5、合并行字段名称
		List<String> hbhtitlist = new ArrayList<String>();
		hbhtitlist.add("公司名称");
		hbhtitlist.add("客户编码");
		hbhtitlist.add("客户名称");
		hbhtitlist.add("省市");
		hbhtitlist.add("联系人");
		hbhtitlist.add("手机号");
		hbhtitlist.add("纳税人");
		hbhtitlist.add("建账日期");
		hbhtitlist.add("记账状态");
		hbhtitlist.add("报税状态");
		hbhtitlist.add("录入日期");
		// 6、合并行字段下标
		Integer[] hbhindexs = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 15 };
		// 7、字符集合
		List<String> strslist = new ArrayList<String>();
		strslist.add("corpname");
		strslist.add("unitcode");
		strslist.add("unitname");
		strslist.add("citycounty");
		strslist.add("linkman2");
		strslist.add("phone2");
		strslist.add("chargedeptname");
		strslist.add("begindate");
		strslist.add("vjzstatues");
		strslist.add("vbsstatues");
		strslist.add("dbegindate");
		strslist.add("denddate");
		strslist.add("isurplusmonth");
		strslist.add("pcountname");
		strslist.add("foreignname");
		strslist.add("createdate");
		// 8、金额集合
		List<String> mnylist = new ArrayList<String>();
		mnylist.add("ntotalmny");
		mnylist.add("nreceivemny");

		ExportExcel<CorpDataVO> ex = new ExportExcel<CorpDataVO>();
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
				fileName = new String(("销售客户明细").getBytes(), "ISO8859-1");
			} else {
				fileName = URLEncoder.encode("销售客户明细", "UTF8"); // 其他浏览器
			}
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName + new String(date + ".xls"));
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = ex.expCorpDataExcel("销售客户明细", exptitlist, expfieidlist, hbltitlist, hblindexlist, hbhtitlist,
					hbhindexs, expVOs, toClient, "", strslist, mnylist, null);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
		} catch (IOException e) {
			log.error(e);
		} finally {
		    InOutUtils.close(toClient, "销售客户明细导出全部关闭输出流");
		    InOutUtils.close(servletOutputStream, "销售客户明细导出全部关闭输入流");
		}
	}
	
	/**
	 * 登录用户验证
	 * 
	 * @throws Exception
	 */
	private void checkUser() throws Exception {
		UserVO uservo = getLoginUserInfo();
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误");
		} else if (uservo == null) {
			throw new BusinessException("登陆用户错误");
		}
	}
}
