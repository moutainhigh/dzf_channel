package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 续费统计合同相关信息
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class RenewCountVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
	@FieldAlias("corpid")
	private String pk_corp;//加盟商主键
	
	@FieldAlias("period")
	private String vperiod;//期间
	
	@FieldAlias("expire")
	private Integer iexpirenum;//到期数
	
	@FieldAlias("num")
	private Integer irenewnum;//续费数
	
	@FieldAlias("rate")
	private DZFDouble renewrate;//续费率
	
	@FieldAlias("mny")
	private DZFDouble renewmny;//续费额

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public Integer getIexpirenum() {
		return iexpirenum;
	}

	public void setIexpirenum(Integer iexpirenum) {
		this.iexpirenum = iexpirenum;
	}

	public Integer getIrenewnum() {
		return irenewnum;
	}

	public void setIrenewnum(Integer irenewnum) {
		this.irenewnum = irenewnum;
	}

	public DZFDouble getRenewrate() {
		return renewrate;
	}

	public void setRenewrate(DZFDouble renewrate) {
		this.renewrate = renewrate;
	}

	public DZFDouble getRenewmny() {
		return renewmny;
	}

	public void setRenewmny(DZFDouble renewmny) {
		this.renewmny = renewmny;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
