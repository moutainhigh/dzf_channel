package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.invoice.BillingInvoiceVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 加盟商开票查询excel导出配置
 * @author gejw
 *
 */
public class BillingExcelField implements IExceport<BillingInvoiceVO>{
	
	private BillingInvoiceVO[] vos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("corpcode", "加盟商编码",false,0,false),
			new Fieldelement("corpname", "加盟商名称",false,0,false),
			new Fieldelement("debittotalmny", "累计扣款金额",true,2,false),
			new Fieldelement("billtotalmny","累计开票金额",true,2,false),
			new Fieldelement("noticketmny", "未开票金额",true,2,false),
	};
	

	@Override
	public String getExcelport2007Name() {
		return "加盟商开票查询" + now + ".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "加盟商开票查询" + now + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "加盟商开票查询";
	}

	@Override
	public String getSheetName() {
		return "加盟商开票查询";
	}

	@Override
	public BillingInvoiceVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

    public void setVos(BillingInvoiceVO[] vos) {
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