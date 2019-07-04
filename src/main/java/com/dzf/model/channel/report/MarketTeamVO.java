package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 销售团队VO
 * @author admin
 *
 */
@SuppressWarnings("rawtypes")
public class MarketTeamVO extends SuperVO {

	@FieldAlias("marketid")
	private String pk_marketeam;
	
	@FieldAlias("corpid")
	private String pk_corp;
	
	@FieldAlias("mnum")
	private Integer managernum;//销售经理人数
	
	@FieldAlias("dnum")
	private Integer departnum;//销售主管人数
	
	@FieldAlias("snum")
	private Integer sellnum;//销售人数
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("opertime")
	private DZFDateTime doperatetime;//录入时间
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	/**不存库***/
	
	@FieldAlias("code")
	private String innercode;//加盟商编码
	
	/**不存库***/
	
	public String getPk_marketeam() {
		return pk_marketeam;
	}

	public void setPk_marketeam(String pk_marketeam) {
		this.pk_marketeam = pk_marketeam;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getManagernum() {
		return managernum;
	}

	public void setManagernum(Integer managernum) {
		this.managernum = managernum;
	}

	public Integer getDepartnum() {
		return departnum;
	}

	public void setDepartnum(Integer departnum) {
		this.departnum = departnum;
	}

	public Integer getSellnum() {
		return sellnum;
	}

	public void setSellnum(Integer sellnum) {
		this.sellnum = sellnum;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDateTime getDoperatetime() {
		return doperatetime;
	}

	public void setDoperatetime(DZFDateTime doperatetime) {
		this.doperatetime = doperatetime;
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

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_marketeam";
	}

	@Override
	public String getTableName() {
		return "cn_marketteam";
	}

}
