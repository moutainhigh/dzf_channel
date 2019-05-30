package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.branch.reportmanage.QueryContractVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 到期合同统计表导出配置
 *
 */
public class ExpireContractExcelField implements IExceport<QueryContractVO> {
	
	private QueryContractVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("ucode", "公司编码",false,0,false),
			new Fieldelement("uname", "公司名称",false,0,false),
			new Fieldelement("enum", "到期合同数",false,0,false),
			new Fieldelement("snum", "已签合同数",false,0,false),
			new Fieldelement("usnum", "未签合同数",false,0,false),
			new Fieldelement("locorpnum", "流失客户数",false,0,false),
	};

	public void setVos(QueryContractVO[] vos) {
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
	public QueryContractVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "到期合同统计表" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "到期合同统计表" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "到期合同统计表";
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
		return "到期合同统计表";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}


}
