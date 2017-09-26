//package com.dzf.action.channel;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.ServletOutputStream;
//
//import org.apache.log4j.Logger;
//import org.apache.struts2.convention.annotation.Action;
//import org.apache.struts2.convention.annotation.Namespace;
//import org.apache.struts2.convention.annotation.ParentPackage;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.dzf.action.pub.BaseAction;
//import com.dzf.model.pub.Grid;
//import com.dzf.model.pub.Json;
//import com.dzf.model.pub.QueryParamVO;
//import com.dzf.model.sys.sys_power.CorpDocVO;
//import com.dzf.model.sys.sys_power.CorpVO;
//import com.dzf.pub.BusinessException;
//import com.dzf.pub.DzfTypeUtils;
//import com.dzf.pub.QueryDeCodeUtils;
//import com.dzf.pub.Field.FieldMapping;
//import com.dzf.pub.util.JSONConvtoJAVA;
//import com.dzf.service.channel.IChannelCustomerApprove;
//
///**
// * 渠道商客户审批
// * 
// * @author lbj
// *
// */
//@ParentPackage("basePackage")
//@Namespace("/sys")
//@Action(value = "sys_channel_approve")
//public class ChannelCustomerApproveAction extends BaseAction<CorpVO> {
//	private Logger log = Logger.getLogger(getClass());
//	@Autowired
//	private IChannelCustomerApprove sys_channel_approveserv;
//
//	public void doApprove() {
//		Json json = new Json();
//		try {
//			String jsonStr = getRequest().getParameter("corps");
//
//			JSONArray array = (JSONArray) JSON.parseArray(jsonStr);
//
//			Map<String, String> mapping = FieldMapping
//					.getFieldMapping(new CorpVO());
//
//			CorpVO[] corps = DzfTypeUtils.cast(array, mapping, CorpVO[].class,
//					JSONConvtoJAVA.getParserConfig());
//			List<CorpVO> doCorps = new ArrayList<CorpVO>();
//			for (CorpVO corpVO : corps) {
//				// 已建账已审批的记录不可以重复审批
//				if (corpVO.getApprove_status() != null
//						&& corpVO.getApprove_status() == 1
//						&& corpVO.getIshasaccount() != null
//						&& corpVO.getIshasaccount().booleanValue()) {
//					continue;
//				}
//				doCorps.add(corpVO);
//			}
//			sys_channel_approveserv.processApprove(doCorps.toArray(new CorpVO[0]), getLoginUserid());
//			json.setSuccess(true);
//			int successNum = doCorps.size();
//			int errorNum = corps.length - successNum;
//			String msg = "成功" + successNum +  "条";
//			if (errorNum > 0) {
//				msg += "，失败" + errorNum + "条";
//			}
//			json.setMsg(msg);
//		} catch (Exception e) {
//			printErrorLog(json, this.log, e, "审批失败");
//		}
//		writeJson(json);
//	}
//
//	public void abandonApprove() {
//		Json json = new Json();
//		try {
//			String jsonStr = getRequest().getParameter("corps");
//			JSONArray array = (JSONArray) JSON.parse(jsonStr);
//			Map<String, String> mapping = FieldMapping
//					.getFieldMapping(new CorpVO());
//
//			CorpVO[] corps = DzfTypeUtils.cast(array, mapping, CorpVO[].class,
//					JSONConvtoJAVA.getParserConfig());
//			List<CorpVO> doCorps = new ArrayList<CorpVO>();
//			for (CorpVO corpVO : corps) {
//				// 已建账已审批的记录不可以弃审
//				if (corpVO.getApprove_status() != null
//						&& corpVO.getApprove_status() == 1
//						&& corpVO.getIshasaccount() != null
//						&& corpVO.getIshasaccount().booleanValue()) {
//					continue;
//				}
//				doCorps.add(corpVO);
//			}
//			sys_channel_approveserv.processAbandonApprove(doCorps.toArray(new CorpVO[0]));
//			json.setSuccess(true);
//			int successNum = doCorps.size();
//			int errorNum = corps.length - successNum;
//			String msg = "成功" + successNum +  "条";
//			if (errorNum > 0) {
//				msg += "，失败" + errorNum + "条";
//			}
//			json.setMsg(msg);
//		} catch (Exception e) {
//			printErrorLog(json, this.log, e, "弃审失败");
//		}
//		writeJson(json);
//	}
//
//	public void queryChannelBusiness() {
//
//		Json json = new Json();
//		try {
//			String logincorp = getLoginCorpInfo().getPk_corp();
//
//			String location = getRequest().getParameter("location");
//			List<CorpVO> corpvos = sys_channel_approveserv
//					.queryChannelBusiness(logincorp, location);
//			CorpVO[] vos = (CorpVO[]) corpvos
//					.toArray(new CorpVO[corpvos.size()]);
//			String[] columnkeys = { "unitname" };
//			QueryDeCodeUtils.decKeyUtils(columnkeys, vos, 1);
//			json.setRows(corpvos);
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//		} catch (Exception e) {
//			printErrorLog(json, this.log, e, "查询失败");
//		}
//		writeJson(json);
//
//	}
//
//	public void queryChannelCustomer() {
//
//		Grid grid = new Grid();
//		try {
//			String account_corp = getRequest().getParameter("pk_account");
//			String bdate = getRequest().getParameter("begindate");
//			String edate = getRequest().getParameter("enddate");
//			String status = getRequest().getParameter("status");
//			List<CorpVO> corpvos = sys_channel_approveserv
//					.queryChannelCustomer(account_corp, bdate, edate, status);
//			CorpVO[] vos = (CorpVO[]) corpvos
//					.toArray(new CorpVO[corpvos.size()]);
//			String[] columnkeys = { "foreignname", "unitname", "legalbodycode",
//					"vcorporatephone", "approve_user_name" };
//			QueryDeCodeUtils.decKeyUtils(columnkeys, vos, 1);
//			
//			QueryParamVO paramvo = getQueryParamVO();
//			int page = paramvo == null ? 1: paramvo.getPage();
//			int rows = paramvo == null? 1000 : paramvo.getRows();
//			long total = (long) vos.length;
//			vos = getPagedVOs(vos, page, rows);
//			grid.setRows(Arrays.asList(vos));
//			grid.setTotal(total);
//			grid.setSuccess(true);
//			grid.setMsg("查询成功");
//		} catch (Exception e) {
//			printErrorLog(grid, this.log, e, "查询失败");
//		}
//		writeJson(grid);
//	}
//
//	
//	// 将查询后的结果分页
//	private CorpVO[] getPagedVOs(CorpVO[] cvos, int page, int rows) {
//		int beginIndex = rows * (page - 1);
//		int endIndex = rows * page;
//		if (endIndex >= cvos.length) {
//			// 防止endIndex数组越界
//			endIndex = cvos.length;
//		}
//		cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
//		return cvos;
//	}
//	
//	public QueryParamVO getQueryParamVO() {
//		QueryParamVO paramvo = new QueryParamVO();
//		paramvo = (QueryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
//		return paramvo;
//	}
//	
//	public void downFile() {
//		FileInputStream fileis = null;
//		Json json = new Json();
//		try {
//			String pk_doc = getRequest().getParameter("pk_doc");
//			CorpDocVO docvo = sys_channel_approveserv.queryCorpDocByID(pk_doc);
//			fileis = new FileInputStream(docvo.getVfilepath());
//			getResponse().setContentType("application/octet-stream");
//
//			String formattedName = URLEncoder.encode(docvo.getDocName(),
//					"UTF-8");
//			getResponse().addHeader(
//					"Content-Disposition",
//					"attachment;filename="
//							+ new String(docvo.getDocName().getBytes("UTF-8"),
//									"ISO8859-1") + ";filename*=UTF-8''"
//							+ formattedName);
//			ServletOutputStream out = getResponse().getOutputStream();
//			byte[] buf = new byte[4096];
//			int bytesRead = 0;
//			while ((bytesRead = fileis.read(buf)) != -1) {
//				out.write(buf, 0, bytesRead);
//			}
//			out.flush();
//		} catch (Exception e) {
//			if ((e instanceof BusinessException)) {
//				json.setMsg(e.getMessage());
//			} else {
//				json.setMsg("文件下载失败");
//			}
//			this.log.error("文件下载失败", e);
//			json.setSuccess(false);
//			if (fileis != null) {
//				try {
//					fileis.close();
//				} catch (IOException e1) {
//					this.log.error("关闭流失败", e1);
//				}
//			}
//		} finally {
//			if (fileis != null) {
//				try {
//					fileis.close();
//				} catch (IOException e) {
//					this.log.error("关闭流失败", e);
//				}
//			}
//		}
//	}
//}
