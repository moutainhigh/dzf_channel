package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 返点单导出配置
 * @author zy
 *
 */
public class RebateInputExcelField implements IExceport<RebateVO> {
	
	private RebateVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("doperatedate", "录入日期",false,0,false),
			new Fieldelement("vbillcode", "返点单号",false,0,false),
			new Fieldelement("areaname", "大区",false,0,false),
			new Fieldelement("vprovname", "地区",false,0,false),
			new Fieldelement("vmanagername", "渠道经理",false,0,false),
			new Fieldelement("voperater", "渠道运营",false,0,false),
			new Fieldelement("corpcode", "加盟商编码",false,0,false),
			new Fieldelement("corpname", "加盟商名称",false,0,false),
			new Fieldelement("vperiod", "返点所属期间",false,0,false),
			new Fieldelement("icontractnum", "合同数量",false,0,false),
			new Fieldelement("ndebitmny", "扣款金额",true,2,false),
			new Fieldelement("nbasemny", "返点基数",true,2,false),
			new Fieldelement("nrebatemny","返点金额",true,2,false),
			//状态   0：待提交；1：待确认；2：待审批；3：审批通过；4：已驳回；
			new Fieldelement("istatus", "状态",false,0,false,new String[]{"待提交","待确认","待审批","审批通过","已驳回"}),
			new Fieldelement("vmemo", "说明",false,0,false),
	};

	public void setVos(RebateVO[] vos) {
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
	public RebateVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "返点单" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "返点单" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "返点单";
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
		return "返点单";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}

}
