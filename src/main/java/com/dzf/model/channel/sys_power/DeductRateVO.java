package com.dzf.model.channel.sys_power;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 扣款率设置VO
 * 
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class DeductRateVO extends SuperVO {

	private static final long serialVersionUID = 3026577364581579635L;

	@FieldAlias("rateid")
	private String pk_deductrate;

	@FieldAlias("corpid")
	private String pk_corp; // 加盟商主键
	
	@FieldAlias("fcorpid")
	private String fathercorp;// 登录公司主键

	@FieldAlias("nrate")
	private Integer inewrate;// 新增扣款率

	@FieldAlias("rnrate")
	private Integer irenewrate;// 续费扣款率
	
	@FieldAlias("coptid")
	private String coperatorid;// 创建人

	@FieldAlias("ddate")
	private DZFDateTime doperatedate;// 创建日期

	@FieldAlias("lmpsnid")
	private String lastmodifypsnid; // 最后修改人

	@FieldAlias("lsdate")
	private DZFDateTime lastmodifydate; // 最后修改时间

	@FieldAlias("dr")
	private Integer dr;// 删除标记

	@FieldAlias("ts")
	private DZFDateTime ts;// 时间戳
	
	@FieldAlias("cpcode")
	private String corpcode;// 加盟商编码（不存库，取客户缓存）

	@FieldAlias("cpname")
	private String corpname;// 加盟商名称（不存库，取客户缓存）
	
	@FieldAlias("lmpsn")
	private String lastmodifypsn; // 最后修改人名称（不存库，取用户缓存）
	
	@FieldAlias("chtype")
	private Integer channeltype;// 加盟商类型（不存库，仅作判断使用） 1：普通加盟商；2：金牌加盟商；

	public Integer getChanneltype() {
		return channeltype;
	}

	public void setChanneltype(Integer channeltype) {
		this.channeltype = channeltype;
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

	public String getLastmodifypsnid() {
		return lastmodifypsnid;
	}

	public void setLastmodifypsnid(String lastmodifypsnid) {
		this.lastmodifypsnid = lastmodifypsnid;
	}

	public DZFDateTime getLastmodifydate() {
		return lastmodifydate;
	}

	public void setLastmodifydate(DZFDateTime lastmodifydate) {
		this.lastmodifydate = lastmodifydate;
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

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getLastmodifypsn() {
		return lastmodifypsn;
	}

	public void setLastmodifypsn(String lastmodifypsn) {
		this.lastmodifypsn = lastmodifypsn;
	}

	@Override
	public String getPKFieldName() {
		return "pk_deductrate";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_deductrate";
	}

}
