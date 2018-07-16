package com.dzf.action.channel.chn_set;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import com.dzf.action.channel.expfield.SaleAnalyseExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.SaleAnalyseVO;
import com.dzf.model.pub.Grid;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.chn_set.ISaleAnalyseService;
import com.dzf.service.pub.LogRecordEnum;

/**
 * 销售数据分析
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/chn_set")
@Action(value = "saleAnalyse")
public class SaleAnalyseAction extends  BaseAction<SaleAnalyseVO> {

	@Autowired
	private ISaleAnalyseService saleAnalyse;

	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * 查询主表数据
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			SaleAnalyseVO qvo = new SaleAnalyseVO();
			qvo = (SaleAnalyseVO) DzfTypeUtils.cast(getRequest(), qvo);
			List<SaleAnalyseVO> rows = saleAnalyse.query(qvo);
			grid.setRows(rows);
			grid.setSuccess(true);
			grid.setMsg("查询成功!");
			if(rows!=null && rows.size()>0){
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_24.getValue(), "销售数据分析查询成功", ISysConstants.SYS_3);
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * Excel导出方法
	 */
	public void exportExcel(){
		String strlist =getRequest().getParameter("strlist");
		String qj = getRequest().getParameter("qj");
		if(StringUtil.isEmpty(strlist)){
			throw new BusinessException("导出数据不能为空!");
		}	
		JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
		Map<String, String> mapping = FieldMapping.getFieldMapping(new SaleAnalyseVO());
		SaleAnalyseVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,SaleAnalyseVO[].class, JSONConvtoJAVA.getParserConfig());
		for (SaleAnalyseVO saleAnalyseVO : expVOs) {
			if(saleAnalyseVO.getPricemny()!=null){
				saleAnalyseVO.setPricemny(saleAnalyseVO.getPricemny().setScale(2, DZFDouble.ROUND_HALF_UP));
			}
			if(saleAnalyseVO.getContractmny()!=null){
				saleAnalyseVO.setContractmny(saleAnalyseVO.getContractmny().setScale(2, DZFDouble.ROUND_HALF_UP));
			}
		}
		HttpServletResponse response = getResponse();
		Excelexport2003<SaleAnalyseVO> ex = new Excelexport2003<>();
		SaleAnalyseExcelField fields = new SaleAnalyseExcelField();
		fields.setVos(expVOs);
		fields.setQj(qj);
		ServletOutputStream servletOutputStream = null;
		OutputStream toClient = null;
		try {
			response.reset();
			// 设置response的Header
			String date = DateUtils.getDate(new Date());
			response.addHeader("Content-Disposition", "attachment;filename="+ new String(date+".xls"));
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
