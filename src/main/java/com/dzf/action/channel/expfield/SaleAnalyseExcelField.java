package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.sale.SaleAnalyseVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 销售数据分析excel导出配置
 * @author gejw
 *
 */
public class SaleAnalyseExcelField implements IExceport<SaleAnalyseVO>{
	
	private SaleAnalyseVO[] vos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("areaname", "大区",false,0,false),
			new Fieldelement("vprovname", "省（市）",false,0,false),
			new Fieldelement("corpname", "加盟商",false,0,false),
			new Fieldelement("ivisitnum", "拜访数",false,0,false),
			new Fieldelement("iviscustnum", "拜访客户数",false,0,false),
			new Fieldelement("isignnum", "签约客户数",false,0,false),
			new Fieldelement("iagentnum", "代账合同数",false,0,false),
			new Fieldelement("iincrenum", "增值合同数",false,0,false),
			new Fieldelement("contractmny", "合同金额",true,2,false),
			new Fieldelement("pricemny", "客单价",true,2,false),
	};
	
	@Override
	public String getExcelport2007Name() {
		return "销售数据分析" + now + ".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "销售数据分析" + now + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "销售数据分析";
	}

	@Override
	public String getSheetName() {
		return "销售数据分析";
	}

	@Override
	public SaleAnalyseVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

    public void setVos(SaleAnalyseVO[] vos) {
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