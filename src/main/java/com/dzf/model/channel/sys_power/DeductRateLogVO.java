package com.dzf.model.channel.sys_power;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 扣款率设置日志VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class DeductRateLogVO extends SuperVO {

	private static final long serialVersionUID = 8921599723075491395L;
	
	@FieldAlias("dtlogid")
	private String pk_deductratelog;//日志主键
	
	@FieldAlias("rateid")
	private String pk_deductrate;//主表主键

	@FieldAlias("corpid")
	private String pk_corp; // 加盟商主键
	
	@FieldAlias("fcorpid")
	private String fathercorp;// 登录公司主键

	@FieldAlias("nrate")
	private Integer inewrate;// 新增扣款率

	@FieldAlias("rnrate")
	private Integer irenewrate;// 续费扣款率
	
	@FieldAlias("coptid")
	private String coperatorid;// 操作人

	@FieldAlias("ddate")
	private DZFDateTime doperatedate;// 操作时间

	@FieldAlias("dr")
	private Integer dr;// 删除标记

	@FieldAlias("ts")
	private DZFDateTime ts;// 时间戳
	
	@FieldAlias("copter")
	private String coperator;// 操作人姓名
	
	public String getPk_deductratelog() {
		return pk_deductratelog;
	}

	public void setPk_deductratelog(String pk_deductratelog) {
		this.pk_deductratelog = pk_deductratelog;
	}

	public String getPk_deductrate() {
		return pk_deductrate;
	}

	public void setPk_deductrate(String pk_deductrate) {
		this.pk_deductrate = pk_deductrate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public Integer getInewrate() {
		return inewrate;
	}

	public void setInewrate(Integer inewrate) {
		this.inewrate = inewrate;
	}

	public Integer getIrenewrate() {
		return irenewrate;
	}

	public void setIrenewrate(Integer irenewrate) {
		this.irenewrate = irenewrate;
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

	public String getCoperator() {
		return coperator;
	}

	public void setCoperator(String coperator) {
		this.coperator = coperator;
	}

	@Override
	public String getPKFieldName() {
		return "pk_deductratelog";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_deductrate";
	}

	@Override
	public String getTableName() {
		return "cn_deductratelog";
	}

}
