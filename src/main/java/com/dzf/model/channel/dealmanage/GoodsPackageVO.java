package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 商品套餐
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class GoodsPackageVO extends SuperVO {
	
	private static final long serialVersionUID = 1L;

	@FieldAlias("pid")
	private String pk_goodspackage;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//登录公司
	
	@FieldAlias("gid")
	private String pk_goods;//商品主键
	
	@FieldAlias("specid")
	private String pk_goodsspec;//规格型号主键  
	
	@FieldAlias("spec")
	private String invspec;//规格
	
	@FieldAlias("type")
	private String invtype;//型号
	
	@FieldAlias("mname")
	private String vmeasname;//单位
	
	@FieldAlias("num")
	private Integer nnum;//数量
	
	@FieldAlias("ispackage")
	private DZFBoolean isbuypackage;//只买套餐
	
	@FieldAlias("status")
	private Integer vstatus;//状态  1：已保存；2：已发布；3：已下架；
	
	@FieldAlias("pubdate")
    private DZFDate dpublishdate;//发布日期
    
    @FieldAlias("offdate")
    private DZFDate doffdate;//下架日期
	
    @FieldAlias("operid")
    private String coperatorid;//录入人
    
    @FieldAlias("operdate")
    private DZFDate doperatedate;//录入日期
    
	private Integer dr;
	
    private DZFDateTime ts;
    
	@FieldAlias("price")
	private DZFDouble nprice;//单价（仅做展示）
	
	@FieldAlias("gname")
	private String vgoodsname;//商品名称（仅做展示）
	
	public String getVgoodsname() {
		return vgoodsname;
	}

	public void setVgoodsname(String vgoodsname) {
		this.vgoodsname = vgoodsname;
	}

	public String getPk_goodspackage() {
		return pk_goodspackage;
	}

	public void setPk_goodspackage(String pk_goodspackage) {
		this.pk_goodspackage = pk_goodspackage;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getPk_goodsspec() {
		return pk_goodsspec;
	}

	public void setPk_goodsspec(String pk_goodsspec) {
		this.pk_goodsspec = pk_goodsspec;
	}

	public String getInvspec() {
		return invspec;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getVmeasname() {
		return vmeasname;
	}

	public void setVmeasname(String vmeasname) {
		this.vmeasname = vmeasname;
	}

	public Integer getNnum() {
		return nnum;
	}

	public void setNnum(Integer nnum) {
		this.nnum = nnum;
	}

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public DZFBoolean getIsbuypackage() {
		return isbuypackage;
	}

	public void setIsbuypackage(DZFBoolean isbuypackage) {
		this.isbuypackage = isbuypackage;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public DZFDate getDpublishdate() {
		return dpublishdate;
	}

	public void setDpublishdate(DZFDate dpublishdate) {
		this.dpublishdate = dpublishdate;
	}

	public DZFDate getDoffdate() {
		return doffdate;
	}

	public void setDoffdate(DZFDate doffdate) {
		this.doffdate = doffdate;
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
		return "pk_goodspackage";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_goodspackage";
	}

}
