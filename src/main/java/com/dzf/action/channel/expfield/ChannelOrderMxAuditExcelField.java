package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.channel.dealmanage.GoodsBillMxVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 订单明细表导出配置
 *
 */
public class ChannelOrderMxAuditExcelField implements IExceport<GoodsBillMxVO> {
	
	private GoodsBillMxVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("corpcode", "加盟商编码",false,0,false),
			new Fieldelement("corpname", "加盟商名称",false,0,false),
			new Fieldelement("vbillcode", "订单编码",false,0,false),
			new Fieldelement("ndedsummny", "订单金额",false,0,false),
			new Fieldelement("ndeductmny", "预付款扣款",false,0,false),
			new Fieldelement("ndedrebamny", "返点扣款",false,0,false),
			new Fieldelement("vgoodsname", "商品",false,0,false),
			new Fieldelement("invspec", "规格",false,0,false),
			new Fieldelement("invtype", "型号",false,0,false),
			new Fieldelement("amount", "购买数量",false,0,false),
			new Fieldelement("nprice", "单价",false,0,false),
			new Fieldelement("ntotalmny","金额",false,0,false),
			new Fieldelement("vstatus","订单状态",false,0,false,new String[]{"","待确认","待发货","已发货","已收货"}),
			new Fieldelement("dconfirmtime","确认日期",false,0,false),
			new Fieldelement("dsubmittime","提交日期",false,0,false),
			new Fieldelement("dsendtime","发货日期",false,0,false),
	};

	public void setVos(GoodsBillMxVO[] vos) {
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
	public GoodsBillMxVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "订单明细" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "订单明细" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "订单明细";
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
		return "订单明细";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}

}
