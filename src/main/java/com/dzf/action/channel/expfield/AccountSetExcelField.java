package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.sale.AccountSetVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 账务设置导出配置
 *
 */
public class AccountSetExcelField implements IExceport<AccountSetVO> {
	
	private AccountSetVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("areaname", "大区",false,0,false),
			new Fieldelement("vprovname", "省市",false,0,false),
			new Fieldelement("cusername", "会计运营经理",false,0,false),
			new Fieldelement("corpname", "加盟商",false,0,false),
			new Fieldelement("corpkname", "客户名称",false,0,false),
			new Fieldelement("vcontcode", "合同编码",false,0,false),
			new Fieldelement("vbeginperiod", "开始日期",false,0,false),
			new Fieldelement("vendperiod", "结束日期",false,0,false),
			new Fieldelement("vchangeperiod", "调整后",false,0,false),
			new Fieldelement("idiff", "差异月份",false,0,false),
			new Fieldelement("istatus", "状态",false,0,false,new String[]{"启用","停用"}),
			new Fieldelement("coperatname", "录入人",false,0,false),
			new Fieldelement("doperatedate", "录入时间",false,0,false),
	};

	public void setVos(AccountSetVO[] vos) {
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
	public AccountSetVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "账务设置" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "账务设置" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "账务设置";
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
		return "账务设置";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}

}
