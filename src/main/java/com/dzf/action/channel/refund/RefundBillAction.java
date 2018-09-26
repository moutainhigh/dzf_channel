package com.dzf.action.channel.refund;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.dzf.action.channel.expfield.RefundExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.refund.RefundBillVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.refund.IRefundBillService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 付款单
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/refund")
@Action(value = "refundbill")
public class RefundBillAction extends BaseAction<RefundBillVO> {

	private static final long serialVersionUID = -7404412877485478067L;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IRefundBillService refundser;
	
	@Autowired
	private IPubService pubser;
	
	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
		    QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), new QryParamVO());
			int total = refundser.queryTotalRow(paramvo);
			grid.setTotal((long)(total));
			if(total > 0){
				List<RefundBillVO> list = refundser.query(paramvo);
				grid.setRows(list);
			}else{
				grid.setRows(new ArrayList<RefundBillVO>());
			}
			grid.setSuccess(true);
			grid.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "操作失败");
		}
		writeJson(grid);
	}

	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				String opertype = "";
				if(StringUtil.isEmpty(data.getPk_refund())){
					opertype = "isAdd";
				}else{
					opertype = "isEdit";
				}
				if (CommonUtil.getDZFDouble(data.getNrefbzjmny()).compareTo(DZFDouble.ZERO_DBL) == 0
						&& CommonUtil.getDZFDouble(data.getNrefyfkmny()).compareTo(DZFDouble.ZERO_DBL) == 0) {
					throw new BusinessException("保证金退款与预付款退款不能同时都为0");
				}
				setDefaultValue(data);
				data = refundser.save(data, getLogincorppk());
				if(data != null){
					if("isAdd".equals(opertype)){
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_40.getValue(), "新增退款单：单据号："+data.getVbillcode(), ISysConstants.SYS_3);
					}else if("isEdit".equals(opertype)){
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_40.getValue(), "修改退款单：单据号："+data.getVbillcode(), ISysConstants.SYS_3);
					}
				}
				json.setRows(data);
				json.setSuccess(true);	
				json.setMsg("保存成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "保存失败");
			}
		}else {
			json.setSuccess(false);
			json.setMsg("保存失败");
		}
		writeJson(json);
	}
	
	/**
	 * 保存前设置默认值
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(RefundBillVO data) throws DZFWarpException{
		if(StringUtil.isEmpty(data.getPk_refund())){
			data.setFathercorp(getLogincorppk());
			data.setCoperatorid(getLoginUserid());
			data.setDoperatedate(new DZFDate());
			data.setTs(new DZFDateTime());
			data.setDr(0);
			data.setIstatus(IStatusConstant.IREFUNDSTATUS_0);//待确认
			if(!StringUtil.isEmpty(data.getVbillcode())){
				data.setVbillcode(data.getVbillcode().trim());
			}
		}
	}
	
	/**
	 * 查询返点相关金额
	 */
	public void queryRefundMny() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				RefundBillVO refvo = refundser.queryRefundMny(data, -1);
				json.setSuccess(true);
				json.setRows(refvo);
				json.setMsg("操作成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "操作失败");
			}
		} else {
			json.setSuccess(false);
			json.setMsg("操作数据为空");
		}
		writeJson(json);
	}
	
	/**
	 * 保存前校验
	 */
	public void checkBeforeSave() {
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			String data = getRequest().getParameter("data");
			JSON headjs = (JSON) JSON.parse(data);
			Map<String, String> refmaping = FieldMapping.getFieldMapping(new RefundBillVO());
			RefundBillVO billvo = DzfTypeUtils.cast(headjs, refmaping, RefundBillVO.class, JSONConvtoJAVA.getParserConfig());
			
			String type = getRequest().getParameter("checktype");
			Integer checktype = Integer.parseInt(type);
			
			RefundBillVO refvo = refundser.checkBeforeSave(billvo, checktype);
			json.setSuccess(true);
			json.setRows(refvo);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 删除
	 */
	public void delete(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			
			String data = getRequest().getParameter("data");
			if (StringUtil.isEmpty(data)) {
				throw new BusinessException("数据不能为空");
			}
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> custmaping = FieldMapping.getFieldMapping(new RefundBillVO());
			RefundBillVO[] refVOs = DzfTypeUtils.cast(arrayJson, custmaping, RefundBillVO[].class,
					JSONConvtoJAVA.getParserConfig());
			
			int rignum = 0;
			int errnum = 0;
			List<RefundBillVO> rightlist = new ArrayList<RefundBillVO>();
			StringBuffer errmsg = new StringBuffer();
			for(RefundBillVO refvo : refVOs){
				try {
					refvo = refundser.delete(refvo);
					rignum++;
					rightlist.add(refvo);
				} catch (Exception e) {
					errnum++;
					errmsg.append(e.getMessage()).append("<br>");
				}
//				if(!StringUtil.isEmpty(refvo.getVerrmsg() )){
//					errnum++;
//					errmsg.append(refvo.getVerrmsg()).append("<br>");
//				}else{
//					rignum++;
//					rightlist.add(refvo);
//				}
			}
			json.setSuccess(true);
			if(rignum > 0 && rignum == refVOs.length){
				json.setRows(Arrays.asList(refVOs));
				json.setMsg("成功"+rignum+"条");
			}else if(errnum > 0){
				json.setMsg("成功"+rignum+"条，失败"+errnum+"条，失败原因："	+ errmsg.toString());
				json.setStatus(-1);
				if(rignum > 0){
					json.setRows(rightlist);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 操作
	 */
	public void operat(){
		Json json = new Json();
		try {
			UserVO uservo = getLoginUserInfo();
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_40);
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			
			String data = getRequest().getParameter("data");
			if (StringUtil.isEmpty(data)) {
				throw new BusinessException("数据不能为空");
			}
			data = data.replace("}{", "},{");
			data = "[" + data + "]";
			JSONArray arrayJson = (JSONArray) JSON.parseArray(data);
			Map<String, String> custmaping = FieldMapping.getFieldMapping(new RefundBillVO());
			RefundBillVO[] refVOs = DzfTypeUtils.cast(arrayJson, custmaping, RefundBillVO[].class,
					JSONConvtoJAVA.getParserConfig());
			
			String type = getRequest().getParameter("opertype");
			int opertype = Integer.parseInt(type);//1、确认；2、取消确认；
			
			int rignum = 0;
			int errnum = 0;
			List<RefundBillVO> rightlist = new ArrayList<RefundBillVO>();
			StringBuffer errmsg = new StringBuffer();
			for(RefundBillVO refvo : refVOs){
				try {
					refvo = refundser.updateOperat(refvo, opertype, getLoginUserid());
					rignum++;
					rightlist.add(refvo);
				} catch (Exception e) {
					errnum++;
					errmsg.append(e.getMessage()).append("<br>");
				}
//				if(!StringUtil.isEmpty(refvo.getVerrmsg())){
//					errnum++;
//					errmsg.append(refvo.getVerrmsg()).append("<br>");
//				}else{
//					rignum++;
//					rightlist.add(refvo);
//				}
			}
			json.setSuccess(true);
			if(rignum > 0 && rignum == refVOs.length){
				json.setRows(Arrays.asList(refVOs));
				json.setMsg("成功"+rignum+"条");
			}else if(errnum > 0){
				json.setMsg("成功"+rignum+"条，失败"+errnum+"条，失败原因："	+ errmsg.toString());
				json.setStatus(-1);
				if(rignum > 0){
					json.setRows(rightlist);
				}
			}
			if(rignum > 0){
				if(opertype == IStatusConstant.IREFOPERATYPE_1){//退款确认
					if(rignum == 1){
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_40.getValue(), "确认退款单：单据号："+
								rightlist.get(0).getVbillcode(), ISysConstants.SYS_3);
					}
				}else if(opertype == IStatusConstant.IREFOPERATYPE_2){//取消确认
					if(rignum == 1){
						writeLogRecord(LogRecordEnum.OPE_CHANNEL_40.getValue(), "取消确认退款单：单据号："+
								rightlist.get(0).getVbillcode(), ISysConstants.SYS_3);
					}
				}
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	/**
	 * 导出
	 */
	public void onExport(){
        String strlist =getRequest().getParameter("strlist");
        String qj = getRequest().getParameter("qj");
        if(StringUtil.isEmpty(strlist)){
            throw new BusinessException("导出数据不能为空");
        }   
        JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
        Map<String, String> mapping = FieldMapping.getFieldMapping(new RefundBillVO());
        RefundBillVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,RefundBillVO[].class, JSONConvtoJAVA.getParserConfig());
        ArrayList<RefundBillVO> explist = new ArrayList<RefundBillVO>();
        for(RefundBillVO vo : expVOs){
            explist.add(vo);
        }
        HttpServletResponse response = getResponse();
        Excelexport2003<RefundBillVO> ex = new Excelexport2003<RefundBillVO>();
        RefundExcelField fields = new RefundExcelField();
        fields.setVos(explist.toArray(new RefundBillVO[0]));;
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
        } catch (Exception e) {
            log.error("导出失败",e);
        }  finally {
            if(toClient != null){
                try {
                    toClient.flush();
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
