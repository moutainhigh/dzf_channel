package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 入库单主表VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class StockInVO extends SuperVO {

	private static final long serialVersionUID = -5473996542501201808L;
	
	@FieldAlias("stid")
	private String pk_stockin;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//登录公司
	
	@FieldAlias("vcode")
	private String vbillcode;//入库单号
	
	@FieldAlias("totalmny")
	private DZFDouble ntotalmny;//总金额
	
	@FieldAlias("stdate")
	private DZFDate dstockdate;//入库日期
	
	@FieldAlias("status")
	private Integer vstatus;//状态  1：待确认；2：已确认；
	
	@FieldAlias("confid")
	private String vconfirmid;//确认人
	
	@FieldAlias("conftime")
	private DZFDateTime dconfirmtime;//确认时间
	
	@FieldAlias("coperid")
	private String coperatorid;//录入人
	
	@FieldAlias("opertime")
	private DZFDateTime doperatedate;//录入日期
	
	@FieldAlias("memo")
	private String vmemo;//备注
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	@FieldAlias("opername")
	private String coperatorname;//录入人姓名

	public String getCoperatorname() {
		return coperatorname;
	}

	public void setCoperatorname(String coperatorname) {
		this.coperatorname = coperatorname;
	}

	public String getPk_stockin() {
		return pk_stockin;
	}

	public void setPk_stockin(String pk_stockin) {
		this.pk_stockin = pk_stockin;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public DZFDate getDstockdate() {
		return dstockdate;
	}

	public void setDstockdate(DZFDate dstockdate) {
		this.dstockdate = dstockdate;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public String getVconfirmid() {
		return vconfirmid;
	}

	public void setVconfirmid(String vconfirmid) {
		this.vconfirmid = vconfirmid;
	}

	public DZFDateTime getDconfirmtime() {
		return dconfirmtime;
	}

	public void setDconfirmtime(DZFDateTime dconfirmtime) {
		this.dconfirmtime = dconfirmtime;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDateTime getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDateTime doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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
		return "pk_stockin";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_stockin";
	}

}
