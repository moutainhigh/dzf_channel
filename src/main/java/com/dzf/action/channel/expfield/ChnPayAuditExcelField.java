package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 付款单审批导出配置
 *
 */
public class ChnPayAuditExcelField implements IExceport<ChnPayBillVO> {
	
	private ChnPayBillVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("corpname", "加盟商",false,0,false),
			new Fieldelement("doperatedate", "付款时间",false,0,false),
			new Fieldelement("vbillcode", "单据号",false,0,false),
			new Fieldelement("ipaytype", "付款类型",false,0,false,new String[]{"","保证金","预付款"}),
			new Fieldelement("npaymny", "付款金额",true,2,false),
			new Fieldelement("ipaymode", "支付方式",false,0,false,new String[]{"","银行转账","支付宝","微信","其他"}),
			new Fieldelement("vbankname", "付款银行",false,0,false),
			new Fieldelement("vbankcode", "付款账号",false,0,false),
			new Fieldelement("vstatus", "单据状态",false,0,false,new String[]{"","待提交","待确认","已确认","已驳回","待确认"}),
			new Fieldelement("vmemo", "备注",false,0,false),
			new Fieldelement("dapprovetime", "收款审批时间",false,0,false),
			new Fieldelement("vreason","驳回说明",false,0,false),
	};

	public void setVos(ChnPayBillVO[] vos) {
		this.vos = vos;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	@Override
	public String getCorpName() {
		return corpname;
	}

	@Override
	public String getCreateSheetDate() {
		return now;
	}

	@Override
	public String getCreateor() {
		return creator;
	}

	@Override
	public ChnPayBillVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "付款单审批" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "付款单审批" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "付款单审批";
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

	@Override
	public String getQj() {
		return qj;
	}

	@Override
	public String getSheetName() {
		return "付款单审批";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}

}
