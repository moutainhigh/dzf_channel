package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.payment.ChnBalanceRepVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 付款余额查询excel导出配置
 * @author gejw
 *
 */
public class ChnPayBalExcelField implements IExceport<ChnBalanceRepVO>{
	
	private ChnBalanceRepVO[] vos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("areaname", "大区",false,0,false),
			new Fieldelement("innercode", "加盟商编码",false,0,false),
			new Fieldelement("corpname", "加盟商名称",false,0,false),
//			new Fieldelement("num", "合同数",false,0,false),
			new Fieldelement("icustnum", "存量合同数",false,0,false),
			new Fieldelement("izeronum", "0扣款(非存量)合同数",false,0,false),
			new Fieldelement("idednum", "非存量合同数",false,0,false),
			new Fieldelement("naccountmny", "合同代账费",true,2,false),
			new Fieldelement("nbookmny","账本费",true,2,false),
			new Fieldelement("ideductpropor", "扣款比率(%)",false,0,false),
			new Fieldelement("vpaytypename", "付款类型",false,0,false),
			new Fieldelement("initbalance", "期初余额",true,2,false),
			new Fieldelement("npaymny", "本期付款金额",true,2,false),
			new Fieldelement("ncondedmny", "合同扣款",true,2,false),
			new Fieldelement("nbuymny", "商品购买",true,2,false),
			new Fieldelement("nusedmny","本期已用金额",true,2,false),
			new Fieldelement("nbalance", "期末余额",true,2,false),
	};

	@Override
	public String getExcelport2007Name() {
		return "付款余额查询" + now + ".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "付款余额查询" + now + ".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "付款余额查询";
	}

	@Override
	public String getSheetName() {
		return "付款余额查询";
	}

	@Override
	public ChnBalanceRepVO[] getData() {
		return vos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

    public void setVos(ChnBalanceRepVO[] vos) {
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