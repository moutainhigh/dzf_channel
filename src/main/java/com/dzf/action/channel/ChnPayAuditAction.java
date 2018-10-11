package com.dzf.action.channel;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import com.dzf.action.channel.expfield.ChnPayAuditExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.IChnPayAuditService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 付款单审批
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/chnpay")
@Action(value = "chnpayaudit")
public class ChnPayAuditAction extends BaseAction<ChnPayBillVO> {

	private static final long serialVersionUID = -1475772734628751196L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IChnPayAuditService payauditser;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询方法
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			QryParamVO paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			paramvo.setCuserid(getLoginUserid());
			int total = 0;
			//列表查询，根据登录人和选择区域进行过滤
			String condition = pubser.makeCondition(paramvo.getCuserid(), paramvo.getAreaname(),IStatusConstant.IYUNYING);
			if (condition != null ) {
				if(!condition.equals("alldata")){
					paramvo.setVqrysql(condition);
				}
				total = payauditser.queryTotalRow(paramvo);
			}
			grid.setTotal((long)(total));
			if(total > 0){
				List<ChnPayBillVO> clist = payauditser.query(paramvo);
				grid.setRows(clist);
			}else{
				grid.setRows(new ArrayList<ChnPayBillVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 收款确认、确认驳回、取消确认
	 */
	public void operate() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_37);
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			String data = getRequest().getParameter("data"); // 操作数据
			if(StringUtil.isEmpty(data)){
				throw new BusinessException("数据不能为空");
			}
			String type = getRequest().getParameter("type"); // 操作类型
			String vreason = getRequest().getParameter("vreason"); // 驳回原因
			Integer opertype = Integer.valueOf(type);
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> headmaping = FieldMapping.getFieldMapping(new ChnPayBillVO());
			ChnPayBillVO[] billVOs = DzfTypeUtils.cast(arrayJson, headmaping, ChnPayBillVO[].class,
					JSONConvtoJAVA.getParserConfig()); 
			int rignum = 0;
			int errnum = 0;
			StringBuffer errmsg = new StringBuffer();
			List<ChnPayBillVO> rightlist = new ArrayList<ChnPayBillVO>();
			for(ChnPayBillVO billvo : billVOs){
				try {
					if(billvo == null){
						throw new BusinessException("付款单-获取操作数据为空");
					}
					billvo = payauditser.updateOperate(billvo, opertype, getLoginUserid(), vreason);
					rignum ++;
					rightlist.add(billvo);
				} catch (Exception e) {
					errnum ++;
					errmsg.append(e.getMessage()).append("<br>");
				}
//				if(!StringUtil.isEmpty(billvo.getVerrmsg())){
//					errnum ++;
//					errmsg.append(billvo.getVerrmsg()).append("<br>");
//				}else{
//					rignum ++;
//					rightlist.add(billvo);
//				}
			}
			json.setSuccess(true);
			if(rignum > 0 && rignum == billVOs.length){
				json.setRows(rightlist);
				json.setMsg("成功"+rignum+"条");
			}else if(errnum > 0){
				json.setMsg("成功"+rignum+"条，失败"+errnum+"条，失败原因："	+ errmsg.toString());
				json.setStatus(-1);
				if(rignum > 0){
					json.setRows(rightlist);
				}
			}
			if(rignum > 0){
				if(opertype == IStatusConstant.ICHNOPRATETYPE_10){//收款审批
					if(rignum == 1){
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_37.getValue(), "审核付款单：单据号："
								+rightlist.get(0).getVbillcode(), ISysConstants.SYS_3);
					}else{
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_37.getValue(), "审核付款单"+rignum+"个", ISysConstants.SYS_3);
					}
				}else if(opertype == IStatusConstant.ICHNOPRATETYPE_9){//审批驳回
					if(rignum == 1){
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_37.getValue(), "驳回付款单：单据号："
								+rightlist.get(0).getVbillcode(), ISysConstants.SYS_3);
					}else{
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_37.getValue(), "驳回付款单"+rignum+"个", ISysConstants.SYS_3);
					}
				}else if(opertype == IStatusConstant.ICHNOPRATETYPE_2){//取消审批
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_37.getValue(), "取消审批付款单", ISysConstants.SYS_3);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 付款单审批导出
	 */
	public void exportAuditExcel(){
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new ChnPayBillVO());
		ChnPayBillVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,ChnPayBillVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<ChnPayBillVO> ex = new Excelexport2003<>();
		ChnPayAuditExcelField fields = new ChnPayAuditExcelField();
		fields.setVos(expVOs);
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String filename = fields.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
	        response.addHeader("Content-Disposition", "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			servletOutputStream = response.getOutputStream();
			toClient = new BufferedOutputStream(servletOutputStream);
			response.setContentType("applicationnd.ms-excel;charset=gb2312");
			ex.exportExcel(fields, toClient);
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_37.getValue(), "导出付款单", ISysConstants.SYS_3);
		} catch (Exception e) {
			log.error("导出失败",e);
		}  finally {
			if(toClient != null){
				try {
					toClient.close();
				} catch (IOException e) {
					log.error("导出失败",e);
				}
			}
			if(servletOutputStream != null){
				try {
					servletOutputStream.flush();
					servletOutputStream.close();
				} catch (IOException e) {
					log.error("导出失败",e);
				}
			}
		}
	}
}
