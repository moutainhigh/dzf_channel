package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 合同审核excel导出配置
 * @author zy
 *
 */
public class ContractConExcelField implements IExceport<ContractConfrimVO>{
	
	private ContractConfrimVO[] vos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("isncust", "存量客户",false,0,false),
			new Fieldelement("dsubmitime", "提交时间",false,0,false),
			new Fieldelement("varea", "地区",false,0,false),
			new Fieldelement("vmanagername", "渠道经理",false,0,false),
			new Fieldelement("corpname", "加盟商",false,0,false),
			new Fieldelement("corpkname", "客户名称",false,0,false),
			new Fieldelement("deductdata", "扣费日期",false,0,false),
			new Fieldelement("ideductpropor", "扣款比例(%)",false,0,false),
			new Fieldelement("ndeductmny", "预付款扣款",true,2,false),
			new Fieldelement("ndedrebamny", "返点扣款",true,2,false),
			new Fieldelement("vdeductstatus", "合同状态",false,0,false,
					new String[]{"待提交","审核通过","","","","待审批","","已驳回","服务到期","已终止","已作废"}),
			new Fieldelement("vconfreason", "驳回原因",false,0,false),
			new Fieldelement("naccountmny", "合同代账费",true,2,false),
			new Fieldelement("nbookmny", "账本费",true,2,false),
			new Fieldelement("isnconfirm", "未确定期限",false,0,false),
			new Fieldelement("dbegindate", "开始日期",false,0,false),
			new Fieldelement("denddate", "结束日期",false,0,false),
			new Fieldelement("ireceivcycle", "收款周期(月)",false,0,false),
			new Fieldelement("icontractcycle", "合同周期(月)",false,0,false),
			new Fieldelement("chargedeptname", "纳税人资格",false,0,false),
			new Fieldelement("nmservicemny", "月服务费",true,2,false),
			new Fieldelement("vopername", "经办人",false,0,false),
	};

	@Override
	public String getExcelport2007Name() {
		return "合同审核" + now + ".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "合同审核" + now + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "合同审核";
	}

	@Override
	public String getSheetName() {
		return "合同审核";
	}

	@Override
	public ContractConfrimVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

    public void setVos(ContractConfrimVO[] vos) {
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