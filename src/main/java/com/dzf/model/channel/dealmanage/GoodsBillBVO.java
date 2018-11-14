package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 商品购买订单明细
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class GoodsBillBVO extends SuperVO {
	
	@FieldAlias("billid")
	private String pk_goodsbill;//主表主键
	
	@FieldAlias("billid_b")
	private String pk_goodsbill_b;//主键
	
	@FieldAlias("gid")
	private String pk_goods;//商品主键
	
	@FieldAlias("corpid")
    private String pk_corp; // 所属公司
	
	@FieldAlias("gname")
	private String vgoodsname; // 商品名称
 
	@FieldAlias("mname")
	private String vmeasname; // 单位
 
	@FieldAlias("price")
	private String nprice; // 单价
	
	private Integer amount; // 数量
	
	private Integer deamount;//发货数量（目前逻辑：有值就是该订单该商品发货，无值就是该订单该商品没有发货）
	
	@FieldAlias("totalmny")
	private String ntotalmny; //合计
	
    @FieldAlias("opid")
    private String coperatorid; // 制单人

    @FieldAlias("opdate")
    private DZFDate doperatedate; // 制单日期
    
    private Integer dr; // 删除标记

    private DZFDateTime ts; // 时间戳
    
    //仅作数据展示begin
    
	@FieldAlias("doc_id")
	private String pk_goodsdoc; // 商品主键
	
	@FieldAlias("note")
	private String vnote;//商品说明
	
	@FieldAlias("fpath")
	private String vfilepath;// 文件存储路径　
	
	//仅作数据展示end
	
	public String getVfilepath() {
		return vfilepath;
	}

	public void setVfilepath(String vfilepath) {
		this.vfilepath = vfilepath;
	}

	public String getVnote() {
		return vnote;
	}

	public void setVnote(String vnote) {
		this.vnote = vnote;
	}

	public String getPk_goodsdoc() {
		return pk_goodsdoc;
	}

	public Integer getDeamount() {
		return deamount;
	}

	public void setDeamount(Integer deamount) {
		this.deamount = deamount;
	}

	public void setPk_goodsdoc(String pk_goodsdoc) {
		this.pk_goodsdoc = pk_goodsdoc;
	}

	public String getPk_goodsbill() {
		return pk_goodsbill;
	}

	public void setPk_goodsbill(String pk_goodsbill) {
		this.pk_goodsbill = pk_goodsbill;
	}

	public String getPk_goodsbill_b() {
		return pk_goodsbill_b;
	}

	public void setPk_goodsbill_b(String pk_goodsbill_b) {
		this.pk_goodsbill_b = pk_goodsbill_b;
	}

	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVgoodsname() {
		return vgoodsname;
	}

	public void setVgoodsname(String vgoodsname) {
		this.vgoodsname = vgoodsname;
	}

	public String getVmeasname() {
		return vmeasname;
	}

	public void setVmeasname(String vmeasname) {
		this.vmeasname = vmeasname;
	}

	public String getNprice() {
		return nprice;
	}

	public void setNprice(String nprice) {
		this.nprice = nprice;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(String ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "pk_goodsbill_b";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_goodsbill_b";
	}
	
}

