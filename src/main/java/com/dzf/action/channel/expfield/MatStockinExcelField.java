package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 物料入库单导出配置
 *
 */
public class MatStockinExcelField implements IExceport<MaterielStockInVO> {
	
	private MaterielStockInVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("vbillcode", "单据编码",false,0,false),
			new Fieldelement("vname", "物料名称",false,0,false),
			new Fieldelement("vunit", "单位",false,0,false),
			new Fieldelement("ncost", "成本价",true,2,false),
			new Fieldelement("nnum", "数量",false,0,false),
			new Fieldelement("ntotalmny", "金额",true,2,false),
			new Fieldelement("vmemo", "备注",false,0,false),
			new Fieldelement("stockdate", "入库日期",false,0,false),
			new Fieldelement("coperatorid", "录入人",false,0,false),
			new Fieldelement("doperatetime", "录入时间",false,0,false),
	};

	public void setVos(MaterielStockInVO[] vos) {
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
	public MaterielStockInVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "物料入库单" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "物料入库单" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "物料入库单";
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
		return "物料入库单";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}


}
