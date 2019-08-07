package com.dzf.action.channel.report;
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
import com.dzf.action.channel.expfield.LossCustExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.report.LossCustomerVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.channel.report.ILossCustomerService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 加盟商管理
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/report")
@Action(value = "losscust")
public class LossCustomerAction extends BaseAction<LossCustomerVO>{

	@Autowired
	private ILossCustomerService losscust;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 查询数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO qvo = new QryParamVO();
			qvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), qvo);
			qvo.setUser_name(getLoginUserid());
			List<LossCustomerVO> corpvos = losscust.query(qvo);
			
			if(corpvos==null||corpvos.size()==0){
				grid.setRows(new ArrayList<QryParamVO>());
				grid.setMsg("查询数据为空!");
			}else{
				LossCustomerVO[]  cVos = corpvos.toArray(new LossCustomerVO[0]);
				cVos = (LossCustomerVO[]) QueryUtil.getPagedVOs(cVos, qvo.getPage(), qvo.getRows());
				grid.setRows(Arrays.asList(cVos));
				grid.setTotal((long) corpvos.size());
				grid.setSuccess(true);
				grid.setMsg("查询成功!");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 导出
	 */
	public void exportAuditExcel() {
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new LossCustomerVO());
		LossCustomerVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,LossCustomerVO[].class, JSONConvtoJAVA.getParserConfig());
		HttpServletResponse response = getResponse();
		Excelexport2003<LossCustomerVO> ex = new Excelexport2003<>();
	    LossCustExcelField fields = new LossCustExcelField();
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
			writeLogRecord(LogRecordEnum.OPE_CHANNEL_45.getValue(), "导出流失客户成功", ISysConstants.SYS_3);
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
