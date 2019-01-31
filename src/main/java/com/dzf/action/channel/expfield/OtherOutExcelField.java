package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 其他出库单导出配置
 *
 */
public class OtherOutExcelField implements IExceport<StockOutVO> {
	
	private StockOutVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("vbillcode", "单据编码",false,0,false),
			new Fieldelement("vmemo", "事项",false,0,false),
			new Fieldelement("getdate", "领取日期",false,0,false),
			//状态   0：待确认；1：已确认；
			new Fieldelement("vstatus", "单据状态",false,0,false,new String[]{"待确认","已确认"}),
			new Fieldelement("voperator", "录入人",false,0,false),
			new Fieldelement("doperatedate", "录入时间",false,0,false),
			new Fieldelement("dconfirmtime", "确认时间",false,0,false),
	};

	public void setVos(StockOutVO[] vos) {
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
	public StockOutVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "其他出库单" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "其他出库单" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "其他出库单";
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
		return "其他出库单";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}

}
