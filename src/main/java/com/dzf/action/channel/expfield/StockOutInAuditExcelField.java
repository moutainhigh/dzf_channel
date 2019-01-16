package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 出入库明细表导出配置
 *
 */
public class StockOutInAuditExcelField implements IExceport<StockOutInMVO> {
	
	private StockOutInMVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("vbillcode", "单据编码",false,0,false),
			new Fieldelement("vgoodscode", "商品编码",false,0,false),
			new Fieldelement("vgoodsname", "商品",false,0,false),
			new Fieldelement("invspec", "规格",false,0,false),
			new Fieldelement("invtype", "型号",false,0,false),
			new Fieldelement("vitype", "类型",false,0,false,new String[]{"","入库","出库"}),
			new Fieldelement("nprice", "成本价",true,2,false),
			new Fieldelement("nnum", "数量",false,0,false),
			new Fieldelement("sprice", "售价",true,2,false),
			new Fieldelement("totalmny", "金额",true,2,false),
			new Fieldelement("vconfirmname", "操作人",false,0,false),
			new Fieldelement("dconfirmtime","入库（出库）时间",false,0,false),
	};

	public void setVos(StockOutInMVO[] vos) {
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
	public StockOutInMVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "出入库明细表" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "出入库明细表" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "出入库明细表";
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
		return "出入库明细表";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}

}
