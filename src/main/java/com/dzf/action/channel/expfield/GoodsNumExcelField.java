package com.dzf.action.channel.expfield;

import java.util.Date;

import com.dzf.model.channel.stock.GoodsNumVO;
import com.dzf.pub.Fieldelement;
import com.dzf.pub.IExceport;
import com.dzf.pub.lang.DZFDate;

/**
 * 商品数量明细表导出配置
 *
 */
public class GoodsNumExcelField implements IExceport<GoodsNumVO> {
	
	private GoodsNumVO[] vos = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private String qj = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("vname", "分类",false,0,false),
			new Fieldelement("vgoodscode", "商品编码",false,0,false),
			new Fieldelement("vgoodsname", "商品名称",false,0,false),
			new Fieldelement("invspec", "规格",false,0,false),
			new Fieldelement("invtype", "型号",false,0,false),
			new Fieldelement("goodsunit", "单位",false,0,false),
			new Fieldelement("goodsprice", "售价",true,2,false),
			new Fieldelement("istockinnum", "累计入库数量",false,0,false),
			new Fieldelement("ilocknum", "订单购买数量",false,0,false),
			new Fieldelement("ioutnum", "累计出库数量",false,0,false),
			new Fieldelement("noutnum", "待出库数量",false,0,false),
			new Fieldelement("nsendnum", "待发货数量",false,0,false),
			new Fieldelement("istocknum","实际库存数量",false,0,false),
			new Fieldelement("ibuynum","可购买数量",false,0,false),
	};

	public void setVos(GoodsNumVO[] vos) {
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
	public GoodsNumVO[] getData() {
		return vos;
	}

	@Override
	public String getExcelport2003Name() {
		return "商品数量明细表" + now + ".xls";
	}

	@Override
	public String getExcelport2007Name() {
		return "商品数量明细表" + now + ".xlsx";
	}

	@Override
	public String getExceportHeadName() {
		return "商品数量明细表";
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
		return "商品数量明细表";
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{false,true,false};
	}

}
