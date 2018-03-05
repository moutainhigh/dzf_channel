package com.dzf.action.channel.invoice;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import com.dzf.action.channel.expfield.InvManageExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.InvManagerService;

/**
 * 渠道商--发票管理
 * 
 * @author shiyan
 *
 */
@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "sys_inv_manager")
public class InvManagerAction extends BaseAction<ChInvoiceVO> {
	
	private static final long serialVersionUID = -3251202665044730598L;
	
	private Logger log = Logger.getLogger(getClass());
	
	@Autowired
	private InvManagerService invManagerService;
	
	/**
	 * 查询
	 */
	public void query(){
		Grid grid = new Grid();
		try {
			ChInvoiceVO paramvo = new ChInvoiceVO();
			paramvo = (ChInvoiceVO)DzfTypeUtils.cast(getRequest(), paramvo);
//			paramvo.setPage(getPage());
//			paramvo.setRows(getRows());
			int total = invManagerService.queryTotalRow(paramvo);
			if(total > 0){
				List<ChInvoiceVO> rows = invManagerService.query(paramvo);
				grid.setRows(rows);
			}else{
				grid.setRows(new ArrayList<ChInvoiceVO>());
			}
			grid.setTotal((long)total);
			grid.setMsg("查询成功");
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 查询渠道商
	 */
	public void queryChannel(){
		Grid grid = new Grid();
		try {
			ChInvoiceVO paramvo = new ChInvoiceVO();
			paramvo = (ChInvoiceVO)DzfTypeUtils.cast(getRequest(), paramvo);
//			paramvo.setPage(getPage());
//			paramvo.setRows(getRows());
			int page = paramvo == null ? 1: paramvo.getPage();
            int rows = paramvo ==null ? 100000: paramvo.getRows();
			List<CorpVO> list = invManagerService.queryChannel(paramvo);
			if(list != null && list.size() > 0){
			    CorpVO[] corpvos = getPagedVOs(list.toArray(new CorpVO[0]),page,rows);
			    grid.setRows(Arrays.asList(corpvos));
			    grid.setTotal((long)(list.size()));
			}else{
			    grid.setRows(list);
			    grid.setTotal(0L);
			}
            grid.setMsg("查询成功！");
            grid.setSuccess(true);
//			grid.setRows(rows);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	private CorpVO[] getPagedVOs(CorpVO[] cvos,int page,int rows){
        int beginIndex = rows * (page-1);
        int endIndex = rows*page;
        if(endIndex>=cvos.length){//防止endIndex数组越界
            endIndex=cvos.length;
        }
        cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
        return cvos;
    } 
	
	/**
	 * 开票
	 */
	public void onBilling(){
		Json json = new Json();
		try{
			ChInvoiceVO paramvo = new ChInvoiceVO();
			paramvo = (ChInvoiceVO)DzfTypeUtils.cast(getRequest(), paramvo);
			String uid = getLoginUserid();
			String pk_invoices = getRequest().getParameter("pk_invoices");
			String[] pkArry = pkinvoicesToArray(pk_invoices);
			List<ChInvoiceVO> listError = invManagerService.onBilling(pkArry,uid);
			int errorNum = listError == null ? 0 :listError.size();
			int success = pkArry.length - errorNum;
			StringBuffer msg = new StringBuffer();
			msg.append("成功").append(success).append("条");
			if (listError != null && errorNum > 0) {
			    msg.append("，失败").append(errorNum).append("条<br>");
			    for(ChInvoiceVO vo : listError){
			        msg.append("加盟商：").append(vo.getCorpname()).append(",失败原因：").append(vo.getMsg()).append("<br>");
			    }
			}
			json.setMsg(msg.toString());
			json.setSuccess(true);
		} catch (Exception e){
			printErrorLog(json, log, e, "开票失败");
		}
		writeJson(json);
	}
	
	private String[] pkinvoicesToArray(String pk_invoices){
		JSONArray array = JSON.parseArray(pk_invoices);
		if (array != null && array.size() > 0) {
			Object[] list = array.toArray();
			String[] pkinvoices = new String[list.length];
			for (int i = 0; i < list.length; i++) {
				pkinvoices[i] = list[i].toString();
			}
			return pkinvoices;
		}
		return null;
	}
	
	public void save(){
        Json json = new Json();
        try {
            invManagerService.save(data);
            json.setSuccess(true);
            json.setMsg("保存成功!");
        } catch (Exception e) {
            printErrorLog(json, log, e, "保存失败");
        }
        writeJson(json);
    }
	
	public void delete(){
        Json json = new Json();
        try{
            String invoices = getRequest().getParameter("invoices");
            if(!StringUtil.isEmpty(invoices)){
                invoices = invoices.replace("}{", "},{");
                invoices = "[" + invoices + "]";
                JSONArray array = (JSONArray)JSON.parseArray(invoices);
                Map<String,String> map = FieldMapping.getFieldMapping(new ChInvoiceVO());
                ChInvoiceVO[] vos = DzfTypeUtils.cast(array,map, ChInvoiceVO[].class,JSONConvtoJAVA.getParserConfig());
                int len = 0;
                int length = vos== null ? 0 :vos.length;
                if(vos != null && length > 0){
                    for(ChInvoiceVO cvo : vos){
                        try{
                            invManagerService.delete(cvo);
                            len++;
                        } catch (BusinessException e) {
//                            msg.append("合同号：").append(cvo.getVcontcode()).append(",失败原因：").append(e.getMessage()).append("<br>");
                        } catch (Exception e) {
                            log.error("审批失败",e);    
                        }
                    }
                }
                json.setMsg("成功删除"+len+"张单据，失败"+(length - len) +"张单据  ");
                json.setSuccess(true);
            }else{
                json.setSuccess(false);
                json.setRows(0);
                json.setMsg("请选择审批数据");
            }
        } catch (Exception e){
            printErrorLog(json, log, e, "删除失败");
        }
        writeJson(json);
    }
	
	/**
	 * 获取可开票金额
	 */
	public void queryTotalPrice(){
	    Json json = new Json();
	    try {
	        String pk_corp = getRequest().getParameter("corpid");
	        String ipaytype = getRequest().getParameter("ipaytype");
	        String invprice = StringUtil.isEmpty(getRequest().getParameter("invprice")) ? "" : getRequest().getParameter("invprice");
	        ChInvoiceVO vo = invManagerService.queryTotalPrice(pk_corp, Integer.valueOf(ipaytype), invprice);
	        json.setSuccess(true);
	        json.setMsg("获取可开票金额成功!");
	        json.setRows(vo);
	    }catch (Exception e) {
	        printErrorLog(json, log, e, "获取可开票金额失败");
	    }
	    writeJson(json);
	}
	
	public void onExport(){
        String strlist =getRequest().getParameter("strlist");
        String qj = getRequest().getParameter("qj");
        if(StringUtil.isEmpty(strlist)){
            throw new BusinessException("导出数据不能为空!");
        }   
        JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
        Map<String, String> mapping = FieldMapping.getFieldMapping(new ChInvoiceVO());
        ChInvoiceVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,ChInvoiceVO[].class, JSONConvtoJAVA.getParserConfig());
        ArrayList<ChInvoiceVO> explist = new ArrayList<ChInvoiceVO>();
        for(ChInvoiceVO vo : expVOs){
            explist.add(vo);
        }
        HttpServletResponse response = getResponse();
//        ExportArrayExcel<ChInvoiceVO> ex = new ExportArrayExcel<ChInvoiceVO>();
        
        Excelexport2003<ChInvoiceVO> ex = new Excelexport2003<ChInvoiceVO>();
        InvManageExcelField fields = new InvManageExcelField();
        fields.setVos(explist.toArray(new ChInvoiceVO[0]));;
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
//            byte[] length = ex.exportExcel("发票管理",cnFields,enFields ,explist, toClient,arrayList, arrayMap);
//            String srt2=new String(length,"UTF-8");
//            response.addHeader("Content-Length", srt2);
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
