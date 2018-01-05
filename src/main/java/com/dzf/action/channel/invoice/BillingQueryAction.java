package com.dzf.action.channel.invoice;

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
import com.dzf.action.channel.expfield.BillingExcelField;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
import com.dzf.pub.excel.Excelexport2003;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.JSONConvtoJAVA;
import com.dzf.service.channel.invoice.IBillingQueryService;

/**
 * 渠道商--发票管理--加盟商发票查询
 * 
 * @author dzf
 *
 */
@ParentPackage("basePackage")
@Namespace("/invoice")
@Action(value = "billingquery")
public class BillingQueryAction extends BaseAction<ChInvoiceVO> {
	
	private static final long serialVersionUID = -3251202665044730598L;
	
	private Logger log = Logger.getLogger(getClass());
	
	@Autowired
	private IBillingQueryService billingQueryServiceImpl;
	
	/**
	 * 查询
	 */
	public void query(){
		Grid grid = new Grid();
		try {
		    BillingInvoiceVO paramvo = new BillingInvoiceVO();
			paramvo = (BillingInvoiceVO)DzfTypeUtils.cast(getRequest(), paramvo);
			paramvo.setBdate(new DZFDate().toString());
			List<BillingInvoiceVO> rows = billingQueryServiceImpl.query(paramvo);
//			QueryDeCodeUtils.decKeyUtils(new String[]{"corpname"}, rows, 2);
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
			grid.setRows(rows);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 生成开票申请
	 */
	public void insertBilling(){
        Json json = new Json();
        try{
            String invoices = getRequest().getParameter("invoices");
            if(!StringUtil.isEmpty(invoices)){
                invoices = invoices.replace("}{", "},{");
                invoices = "[" + invoices + "]";
                JSONArray array = (JSONArray)JSON.parseArray(invoices);
                Map<String,String> map = FieldMapping.getFieldMapping(new BillingInvoiceVO());
                BillingInvoiceVO[] vos = DzfTypeUtils.cast(array,map, BillingInvoiceVO[].class,JSONConvtoJAVA.getParserConfig());
                int len = 0;
                int length = vos.length;
                StringBuffer msg = new StringBuffer();
                if(vos != null && length > 0){
                    for(BillingInvoiceVO cvo : vos){
                        try{
                            billingQueryServiceImpl.insertBilling(cvo);
                            len++;
                        } catch (BusinessException e) {
                            msg.append("加盟商：").append(cvo.getCorpname()).append(",失败原因：").append(e.getMessage()).append("<br>");
                        } catch (Exception e) {
                            log.error("开票失败",e);    
                        }
                    }
                }
                json.setMsg("成功生成"+len+"张单据，失败"+(length - len) +"张单据<br>  "+msg.toString());
                json.setSuccess(true);
            }else{
                json.setSuccess(false);
                json.setRows(0);
                json.setMsg("请选择数据");
            }
        } catch (Exception e){
            printErrorLog(json, log, e, "生成失败");
        }
        writeJson(json);
    }
	
	/**
	 * 导出
	 */
	public void onExport(){
        String strlist =getRequest().getParameter("strlist");
        String bdate = getRequest().getParameter("bdate");
        if(StringUtil.isEmpty(strlist)){
            throw new BusinessException("导出数据不能为空!");
        }   
        JSONArray exparray = (JSONArray) JSON.parseArray(strlist);
        Map<String, String> mapping = FieldMapping.getFieldMapping(new BillingInvoiceVO());
        BillingInvoiceVO[] expVOs = DzfTypeUtils.cast(exparray, mapping,BillingInvoiceVO[].class, JSONConvtoJAVA.getParserConfig());
        ArrayList<BillingInvoiceVO> explist = new ArrayList<BillingInvoiceVO>();
        for(BillingInvoiceVO vo : expVOs){
//            vo.setDebittotalmny(vo.getDebittotalmny().setScale(2, DZFDouble.ROUND_HALF_UP));
//            vo.setBilltotalmny(vo.getBilltotalmny().setScale(2, DZFDouble.ROUND_HALF_UP));
//            vo.setNoticketmny(vo.getNoticketmny().setScale(2, DZFDouble.ROUND_HALF_UP));
            explist.add(vo);
        }
        HttpServletResponse response = getResponse();
        Excelexport2003<BillingInvoiceVO> ex = new Excelexport2003<BillingInvoiceVO>();
        BillingExcelField fields = new BillingExcelField();
        fields.setVos(explist.toArray(new BillingInvoiceVO[0]));;
        fields.setQj(bdate);
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
