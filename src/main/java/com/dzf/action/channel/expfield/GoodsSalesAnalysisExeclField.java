package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.report.GoodsSalesAnalysisVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

public class GoodsSalesAnalysisExeclField implements IExceport<GoodsSalesAnalysisVO>{

	private GoodsSalesAnalysisVO[] vos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("corpname", "加盟商",false,0,false),
			new Fieldelement("vgoodsname", "商品",false,0,false),
			new Fieldelement("invspec", "规格",false,0,false),
			new Fieldelement("invtype", "型号",false,0,false),
			new Fieldelement("amount", "数量",false,0,false),
			new Fieldelement("ncost", "成本",true,2,false),
			new Fieldelement("ntotalcost", "成本合计",true,2,false),
			new Fieldelement("nprice", "售价",true,2,false),
			new Fieldelement("ndeductmny", "预付款",true,2,false),
			new Fieldelement("ndedrebamny", "返点",true,2,false),
			new Fieldelement("ndedsummny", "合计",true,2,false),
	};

	@Override
	public String getExcelport2007Name() {
		return "商品销售分析" + now + ".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "商品销售分析" + now + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "商品销售分析";
	}

	@Override
	public String getSheetName() {
		return "商品销售分析";
	}

	@Override
	public GoodsSalesAnalysisVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

    public void setVos(GoodsSalesAnalysisVO[] vos) {
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
