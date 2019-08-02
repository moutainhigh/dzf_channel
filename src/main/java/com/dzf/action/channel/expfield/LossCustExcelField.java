package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.report.LossCustomerVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 流失客户导出配置
 *
 */
public class LossCustExcelField implements  IExceport<LossCustomerVO> {
	
	private LossCustomerVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("areaname", "大区",false,0,false),
			new Fieldelement("cusername", "会计运营",false,0,false),
			new Fieldelement("vprovname", "省市",false,0,false),
			new Fieldelement("corpname", "加盟商名称",false,0,false),
			new Fieldelement("corpkname", "客户名称",false,0,false),
			new Fieldelement("chargedeptname", "纳税人资格",false,0,false),
			new Fieldelement("jzdate", "建账日期",false,0,false),
			new Fieldelement("isClose", "关账状态",false,0,false),
			new Fieldelement("stopdatetime", "停用时间",false,0,false),
			new Fieldelement("stopname", "停用人",false,0,false),
			new Fieldelement("stopreason", "停用原因",false,0,false),
	};

	public void setVos(LossCustomerVO[] vos) {
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
	public LossCustomerVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "流失客户明细表" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "流失客户明细表" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "流失客户明细表";
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
		return "流失客户明细表";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}


}
