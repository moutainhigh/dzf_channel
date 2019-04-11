package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 物料档案表导出配置
 *
 */
public class MatFileExcelField implements IExceport<MaterielFileVO> {
	
	private MaterielFileVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("vcode", "物料编码",false,0,false),
			new Fieldelement("vname", "物料名称",false,0,false),
			new Fieldelement("vunit", "单位",false,0,false),
			new Fieldelement("isappl", "申请条件",false,0,false,new String[]{"","上季度合同审核通过数≥上季度申请数量的 70%"}),
			// 0-全部  1-启用  2-封存
			new Fieldelement("isseal", "状态",false,0,false,new String[]{"","启用","封存"}),
			new Fieldelement("applyname", "录入人",false,0,false),
			new Fieldelement("doperatetime", "录入时间",false,0,false),
	};

	public void setVos(MaterielFileVO[] vos) {
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
	public MaterielFileVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "物料档案表" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "物料档案表" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "物料档案表";
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
		return "物料档案表";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}


}
