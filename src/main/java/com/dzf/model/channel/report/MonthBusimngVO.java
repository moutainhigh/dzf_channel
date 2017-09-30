package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 月业务量汇总VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class MonthBusimngVO extends SuperVO {

	private static final long serialVersionUID = 2095918541085024511L;
	
	@FieldAlias("franch")
	private Integer ifranchisee;//现有加盟商
	
	@FieldAlias("tsmhfranch")
	private Integer itsmhfranchisee;//本月新增加盟商
	
	@FieldAlias("cust")
	private Integer icustomer;//现有客户数
	
	@FieldAlias("tsmhcust")
	private Integer itsmhcustomer;//本月新增客户数
	
	@FieldAlias("tsyrinitfee")
	private DZFDouble ntsyrinitialfee;//本年累计已收加盟费
	
	@FieldAlias("tsmhinitfee")
	private DZFDouble ntsmhinitialfee;//本月收到加盟费
	
	@FieldAlias("tsmhcont")
	private DZFDouble ntsmhcontamount;//本月新增合同金额
	
	@FieldAlias("tsyrcharge")
	private DZFDouble ntsyrcharge;//本年累计收到预付款
	
	@FieldAlias("tsmhcharge")
	private DZFDouble ntsmhcharge;//本月收到预付款
	
	@FieldAlias("tsyramount")
	private DZFDouble ntsyramount;//本年累计扣款金额
	
	@FieldAlias("tsmhamount")
	private DZFDouble ntsmhamount;//本月扣款金额

	public Integer getIfranchisee() {
		return ifranchisee;
	}

	public void setIfranchisee(Integer ifranchisee) {
		this.ifranchisee = ifranchisee;
	}

	public Integer getItsmhfranchisee() {
		return itsmhfranchisee;
	}

	public void setItsmhfranchisee(Integer itsmhfranchisee) {
		this.itsmhfranchisee = itsmhfranchisee;
	}

	public Integer getIcustomer() {
		return icustomer;
	}

	public void setIcustomer(Integer icustomer) {
		this.icustomer = icustomer;
	}

	public Integer getItsmhcustomer() {
		return itsmhcustomer;
	}

	public void setItsmhcustomer(Integer itsmhcustomer) {
		this.itsmhcustomer = itsmhcustomer;
	}

	public DZFDouble getNtsyrinitialfee() {
		return ntsyrinitialfee;
	}

	public void setNtsyrinitialfee(DZFDouble ntsyrinitialfee) {
		this.ntsyrinitialfee = ntsyrinitialfee;
	}

	public DZFDouble getNtsmhinitialfee() {
		return ntsmhinitialfee;
	}

	public void setNtsmhinitialfee(DZFDouble ntsmhinitialfee) {
		this.ntsmhinitialfee = ntsmhinitialfee;
	}

	public DZFDouble getNtsmhcontamount() {
		return ntsmhcontamount;
	}

	public void setNtsmhcontamount(DZFDouble ntsmhcontamount) {
		this.ntsmhcontamount = ntsmhcontamount;
	}

	public DZFDouble getNtsyrcharge() {
		return ntsyrcharge;
	}

	public void setNtsyrcharge(DZFDouble ntsyrcharge) {
		this.ntsyrcharge = ntsyrcharge;
	}

	public DZFDouble getNtsmhcharge() {
		return ntsmhcharge;
	}

	public void setNtsmhcharge(DZFDouble ntsmhcharge) {
		this.ntsmhcharge = ntsmhcharge;
	}

	public DZFDouble getNtsyramount() {
		return ntsyramount;
	}

	public void setNtsyramount(DZFDouble ntsyramount) {
		this.ntsyramount = ntsyramount;
	}

	public DZFDouble getNtsmhamount() {
		return ntsmhamount;
	}

	public void setNtsmhamount(DZFDouble ntsmhamount) {
		this.ntsmhamount = ntsmhamount;
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
