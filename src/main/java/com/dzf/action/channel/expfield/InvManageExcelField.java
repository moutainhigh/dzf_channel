package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 发票管理excel导出配置
 * @author gejw
 *
 */
public class InvManageExcelField implements IExceport<ChInvoiceVO>{
	
	private ChInvoiceVO[] vos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("corpname", "加盟商",false,0,false),
			new Fieldelement("ipaytype", "付款类型",false,0,false,new String[]{"预付款","加盟费"}),
			new Fieldelement("taxnum", "税号",false,0,false),
			new Fieldelement("invtype", "发票类型",false,0,false,new String[]{"专用发票","普通发票","电子发票"}),
			new Fieldelement("corpaddr", "公司地址",false,0,false),
			new Fieldelement("invphone", "开票电话",false,0,false),
			new Fieldelement("bankname", "开户行",false,0,false),
			new Fieldelement("bankcode", "开户帐号",false,0,false),
			new Fieldelement("email", "邮箱",false,0,false),
			new Fieldelement("apptime", "申请时间",false,0,false),
			new Fieldelement("invtime", "开票日期",false,0,false),
			new Fieldelement("iperson", "经手人",false,0,false),
			new Fieldelement("invstatus", "发票状态",false,0,false,new String[]{"待提交","待开票","已开票"}),
			new Fieldelement("vmome", "备注",false,0,false),
	};
	
	@Override
	public String getExcelport2007Name() {
		return "付款余额查询" + now + ".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "付款余额查询" + now + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "付款余额查询";
	}

	@Override
	public String getSheetName() {
		return "付款余额查询";
	}

	@Override
	public ChInvoiceVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

    public void setVos(ChInvoiceVO[] vos) {
        this.vos = vos;
    }

    @Override
	public String getQj() {
		return qj;
	}

	@Override
	public String getCreateSheetDate() {
		return now;
	}

	@Override
	public String getCreateor() {
		return creator;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String getCorpName() {
		return corpname;
	}
	
	public void setCorpName(String corpname){
		this.corpname = corpname;
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}
	
	
}