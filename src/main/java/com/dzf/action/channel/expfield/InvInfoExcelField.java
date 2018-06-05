package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.piaotong.invinfo.InvInfoResBVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 发票管理--电票余量  excel导出配置
 * @author gejw
 *
 */
public class InvInfoExcelField implements IExceport<InvInfoResBVO>{
	
	private InvInfoResBVO[] vos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("extensionNum", "分机号",false,0,false),
			new Fieldelement("invoiceKindCode", "发票种类",false,0,false),
			new Fieldelement("invoiceCode", "发票代码",false,0,false),
			new Fieldelement("invoiceStartNo", "发票起始号码",false,0,false),
			new Fieldelement("invoiceEndNo", "发票终止号码",false,0,false),
			new Fieldelement("invoiceSurplusNum", "剩余份数",false,0,false),
			new Fieldelement("invoiceBuyTime", "购买日期",false,0,false),
	};
	
	@Override
	public String getExcelport2007Name() {
		return "电子票余量查询" + now + ".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "电子票余量查询" + now + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "电子票余量查询";
	}

	@Override
	public String getSheetName() {
		return "电子票余量查询";
	}

	@Override
	public InvInfoResBVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

    public void setVos(InvInfoResBVO[] vos) {
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