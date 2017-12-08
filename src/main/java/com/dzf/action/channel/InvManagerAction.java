package com.dzf.action.channel;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.Field.FieldMapping;
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
			paramvo.setPage(getPage());
			paramvo.setRows(getRows());
			List<ChInvoiceVO> rows = invManagerService.query(paramvo);
			grid.setTotal((long)invManagerService.queryTotalRow(paramvo));
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
			grid.setRows(rows);
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
			paramvo.setPage(getPage());
			paramvo.setRows(getRows());
			List<CorpVO> rows = invManagerService.queryChannel(paramvo);
			grid.setTotal((long)invManagerService.queryChTotalRow(paramvo));
			grid.setMsg("查询成功！");
			grid.setSuccess(true);
			grid.setRows(rows);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
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
			int success = invManagerService.onBilling(pkArry,uid);
			int erroNum = pkArry.length - success;
			String msg = "成功" + success +  "条";
			if (erroNum > 0) {
				msg += "，失败" + erroNum  + "条";
			}
			json.setMsg(msg);
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
                int length = vos.length;
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


}
