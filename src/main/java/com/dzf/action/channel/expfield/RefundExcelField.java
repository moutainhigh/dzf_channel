package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.refund.RefundBillVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 退款单导出配置
 * @author zy
 *
 */
public class RefundExcelField implements IExceport<RefundBillVO> {
	
	private RefundBillVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("areaname", "大区",false,0,false),
			new Fieldelement("vprovname", "地区",false,0,false),
			new Fieldelement("corpcode", "加盟商编码",false,0,false),
			new Fieldelement("corpname", "加盟商名称",false,0,false),
			new Fieldelement("vbillcode", "退款单号",false,0,false),
			new Fieldelement("nrefyfkmny", "预付款退款",true,2,false),
			new Fieldelement("nrefbzjmny", "保证金退款",true,2,false),
			new Fieldelement("drefunddate", "退款日期",false,0,false),
			//状态   0：待确认；1：已确认；
			new Fieldelement("istatus", "单据状态",false,0,false,new String[]{"待确认","已确认"}),
			new Fieldelement("vmemo", "备注",false,0,false),
			new Fieldelement("dconfirmdate", "确认日期",false,0,false),
			new Fieldelement("voperator", "录入人",false,0,false),
			new Fieldelement("doperatedate", "录入日期",false,0,false),
	};

	public void setVos(RefundBillVO[] vos) {
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
	public RefundBillVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "退款单" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "退款单" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "退款单";
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
		return "退款单";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}

}
