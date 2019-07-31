package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 商品管理VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class GoodsVO extends SuperVO {

	private static final long serialVersionUID = 235910824819383384L;
	
	@FieldAlias("gid")
	private String pk_goods;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//公司
	
	@FieldAlias("gcode")
	private String vgoodscode;//商品编码

	@FieldAlias("gname")
	private String vgoodsname;//商品名称
	
	@FieldAlias("price")
	private DZFDouble nprice;//单价
	
	@FieldAlias("mname")
	private String vmeasname;//单位
	
	@FieldAlias("measid")
	private String pk_measdoc;//单位ID
	
	@FieldAlias("note")
	private String vnote;//商品说明
	
	@FieldAlias("status")
	private Integer vstatus;//状态  1：已保存；2：已发布；3：已下架；
	
	@FieldAlias("pubdate")
	private DZFDate dpublishdate;//发布日期
	
	@FieldAlias("dofdate")
	private DZFDate doffdate;//下架日期
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("opername")
	private String vopername;//录入人姓名
	
	@FieldAlias("operdate")
	private DZFDate doperatedate;//录入日期
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	@FieldAlias("gtype")
	private String pk_goodstype;//商品类型
	
	@FieldAlias("taxcode")
	private String vtaxclasscode;//税收分类编码
	
	//&&&&&&&&&&&&仅作查询或展示使用begin&&&&&&&&&&&&&&&&
	
	@FieldAlias("staname")
	private String vstaname;//状态名称
	
	@FieldAlias("gtypenm")
	private String vgoodstypename;//商品类型名称

	@FieldAlias("fpath")
	private String vfilepath;// 文件存储路径　
	
	@FieldAlias("isin")
	private DZFBoolean isstockin;//是否已经入库（仅作查询使用）

	private SuperVO[] bodys;
	
	//&&&&&&&&&&&&仅作查询或展示使用end&&&&&&&&&&&&&&&&
	
	public String getVtaxclasscode() {
		return vtaxclasscode;
	}

	public void setVtaxclasscode(String vtaxclasscode) {
		this.vtaxclasscode = vtaxclasscode;
	}

	public DZFBoolean getIsstockin() {
		return isstockin;
	}

	public void setIsstockin(DZFBoolean isstockin) {
		this.isstockin = isstockin;
	}

	public String getVgoodstypename() {
		return vgoodstypename;
	}

	public void setVgoodstypename(String vgoodstypename) {
		this.vgoodstypename = vgoodstypename;
	}

	public String getPk_goodstype() {
		return pk_goodstype;
	}

	public String getVfilepath() {
		return vfilepath;
	}

	public void setVfilepath(String vfilepath) {
		this.vfilepath = vfilepath;
	}

	public SuperVO[] getBodys() {
		return bodys;
	}

	public void setBodys(SuperVO[] bodys) {
		this.bodys = bodys;
	}

	public void setPk_goodstype(String pk_goodstype) {
		this.pk_goodstype = pk_goodstype;
	}

	public String getVstaname() {
		return vstaname;
	}

	public void setVstaname(String vstaname) {
		this.vstaname = vstaname;
	}

	public String getVopername() {
		return vopername;
	}

	public void setVopername(String vopername) {
		this.vopername = vopername;
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

	public String getVgoodscode() {
		return vgoodscode;
	}

	public void setVgoodscode(String vgoodscode) {
		this.vgoodscode = vgoodscode;
	}

	public String getVgoodsname() {
		return vgoodsname;
	}

	public void setVgoodsname(String vgoodsname) {
		this.vgoodsname = vgoodsname;
	}

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public String getVmeasname() {
		return vmeasname;
	}

	public void setVmeasname(String vmeasname) {
		this.vmeasname = vmeasname;
	}

	public String getPk_measdoc() {
		return pk_measdoc;
	}

	public void setPk_measdoc(String pk_measdoc) {
		this.pk_measdoc = pk_measdoc;
	}

	public String getVnote() {
		return vnote;
	}

	public void setVnote(String vnote) {
		this.vnote = vnote;
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
		return "pk_goods";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_goods";
	}

}
