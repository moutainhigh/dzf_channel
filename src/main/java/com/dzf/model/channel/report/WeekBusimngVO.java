package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 周业务量汇总VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class WeekBusimngVO extends SuperVO {

	private static final long serialVersionUID = 379300388669043501L;
	
	@FieldAlias("tswkfranc")
	private Integer itswkfranchisee;//本周新增加盟商
	
	@FieldAlias("tswkinitfee")
	private DZFDouble ntswkinitialfee;//本周收到加盟费
	
	@FieldAlias("tswkcust")
	private Integer itswkcustomer;//本周新增客户
	
	@FieldAlias("tswkcont")
	private DZFDouble ntswkcontamount;//本周新增合同金额
	
	@FieldAlias("tswkcharge")
	private DZFDouble ntswkcharge;//本周收到预付款
	
	@FieldAlias("tswkamount")
	private DZFDouble ntswkamount;//本周扣款金额

	public Integer getItswkfranchisee() {
		return itswkfranchisee;
	}

	public void setItswkfranchisee(Integer itswkfranchisee) {
		this.itswkfranchisee = itswkfranchisee;
	}

	public DZFDouble getNtswkinitialfee() {
		return ntswkinitialfee;
	}

	public void setNtswkinitialfee(DZFDouble ntswkinitialfee) {
		this.ntswkinitialfee = ntswkinitialfee;
	}

	public Integer getItswkcustomer() {
		return itswkcustomer;
	}

	public void setItswkcustomer(Integer itswkcustomer) {
		this.itswkcustomer = itswkcustomer;
	}

	public DZFDouble getNtswkcontamount() {
		return ntswkcontamount;
	}

	public void setNtswkcontamount(DZFDouble ntswkcontamount) {
		this.ntswkcontamount = ntswkcontamount;
	}

	public DZFDouble getNtswkcharge() {
		return ntswkcharge;
	}

	public void setNtswkcharge(DZFDouble ntswkcharge) {
		this.ntswkcharge = ntswkcharge;
	}

	public DZFDouble getNtswkamount() {
		return ntswkamount;
	}

	public void setNtswkamount(DZFDouble ntswkamount) {
		this.ntswkamount = ntswkamount;
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
