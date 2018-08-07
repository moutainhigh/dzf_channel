package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 计量单位
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class MeasVO extends SuperVO {

	private static final long serialVersionUID = -2820498567767781639L;
	
	@FieldAlias("mid")
	private String pk_measdoc;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//公司
	
	@FieldAlias("mcode")
	private String vmeascode;//编码
	
	@FieldAlias("mname")
	private String vmeasname;//名称
	
	@FieldAlias("memo")
	private String vmemo;//备注
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("operdate")
	private DZFDate doperatedate;//录入日期
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间

	public String getPk_measdoc() {
		return pk_measdoc;
	}

	public void setPk_measdoc(String pk_measdoc) {
		this.pk_measdoc = pk_measdoc;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVmeascode() {
		return vmeascode;
	}

	public void setVmeascode(String vmeascode) {
		this.vmeascode = vmeascode;
	}

	public String getVmeasname() {
		return vmeasname;
	}

	public void setVmeasname(String vmeasname) {
		this.vmeasname = vmeasname;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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
		return "pk_measdoc";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_measdoc";
	}

}
