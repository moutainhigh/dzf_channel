package com.dzf.model.channel.report;

import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 续费统计
 * @author zy
 *
 */
public class RenewAchieveVO extends DataVO {

	private static final long serialVersionUID = 1L;
	
	@FieldAlias("corpnum")
	private Integer icorpnum;//总客户数
	
	@FieldAlias("accnum")
	private Integer iaccnum;//建账数
	
	@FieldAlias("naccnum")
	private Integer inaccnum;//未建账数
	
	@FieldAlias("expire0")
	private Integer iexpirenum0;//到期数
	
	@FieldAlias("num0")
	private Integer irenewnum0;//续费数
	
	@FieldAlias("rate0")
	private DZFDouble renewrate0;//续费率
	
	@FieldAlias("mny0")
	private DZFDouble renewmny0;//续费额
	
	@FieldAlias("expire1")
	private Integer iexpirenum1;//到期数
	
	@FieldAlias("num1")
	private Integer irenewnum1;//续费数
	
	@FieldAlias("rate1")
	private DZFDouble renewrate1;//续费率
	
	@FieldAlias("mny1")
	private DZFDouble renewmny1;//续费额
	
	@FieldAlias("expire2")
	private Integer iexpirenum2;//到期数
	
	@FieldAlias("num2")
	private Integer irenewnum2;//续费数
	
	@FieldAlias("rate2")
	private DZFDouble renewrate2;//续费率
	
	@FieldAlias("mny2")
	private DZFDouble renewmny2;//续费额
	
	@FieldAlias("expire3")
	private Integer iexpirenum3;//到期数
	
	@FieldAlias("num3")
	private Integer irenewnum3;//续费数
	
	@FieldAlias("rate3")
	private DZFDouble renewrate3;//续费率
	
	@FieldAlias("mny3")
	private DZFDouble renewmny3;//续费额
	
	@FieldAlias("expire4")
	private Integer iexpirenum4;//到期数
	
	@FieldAlias("num4")
	private Integer irenewnum4;//续费数
	
	@FieldAlias("rate4")
	private DZFDouble renewrate4;//续费率
	
	@FieldAlias("mny4")
	private DZFDouble renewmny4;//续费额
	
	@FieldAlias("expire5")
	private Integer iexpirenum5;//到期数
	
	@FieldAlias("num5")
	private Integer irenewnum5;//续费数
	
	@FieldAlias("rate5")
	private DZFDouble renewrate5;//续费率
	
	@FieldAlias("mny5")
	private DZFDouble renewmny5;//续费额

	public Integer getIcorpnum() {
		return icorpnum;
	}

	public void setIcorpnum(Integer icorpnum) {
		this.icorpnum = icorpnum;
	}

	public Integer getIaccnum() {
		return iaccnum;
	}

	public void setIaccnum(Integer iaccnum) {
		this.iaccnum = iaccnum;
	}

	public Integer getInaccnum() {
		return inaccnum;
	}

	public void setInaccnum(Integer inaccnum) {
		this.inaccnum = inaccnum;
	}

	public Integer getIexpirenum0() {
		return iexpirenum0;
	}

	public void setIexpirenum0(Integer iexpirenum0) {
		this.iexpirenum0 = iexpirenum0;
	}

	public Integer getIrenewnum0() {
		return irenewnum0;
	}

	public void setIrenewnum0(Integer irenewnum0) {
		this.irenewnum0 = irenewnum0;
	}

	public DZFDouble getRenewrate0() {
		return renewrate0;
	}

	public void setRenewrate0(DZFDouble renewrate0) {
		this.renewrate0 = renewrate0;
	}

	public DZFDouble getRenewmny0() {
		return renewmny0;
	}

	public void setRenewmny0(DZFDouble renewmny0) {
		this.renewmny0 = renewmny0;
	}

	public Integer getIexpirenum1() {
		return iexpirenum1;
	}

	public void setIexpirenum1(Integer iexpirenum1) {
		this.iexpirenum1 = iexpirenum1;
	}

	public Integer getIrenewnum1() {
		return irenewnum1;
	}

	public void setIrenewnum1(Integer irenewnum1) {
		this.irenewnum1 = irenewnum1;
	}

	public DZFDouble getRenewrate1() {
		return renewrate1;
	}

	public void setRenewrate1(DZFDouble renewrate1) {
		this.renewrate1 = renewrate1;
	}

	public DZFDouble getRenewmny1() {
		return renewmny1;
	}

	public void setRenewmny1(DZFDouble renewmny1) {
		this.renewmny1 = renewmny1;
	}

	public Integer getIexpirenum2() {
		return iexpirenum2;
	}

	public void setIexpirenum2(Integer iexpirenum2) {
		this.iexpirenum2 = iexpirenum2;
	}

	public Integer getIrenewnum2() {
		return irenewnum2;
	}

	public void setIrenewnum2(Integer irenewnum2) {
		this.irenewnum2 = irenewnum2;
	}

	public DZFDouble getRenewrate2() {
		return renewrate2;
	}

	public void setRenewrate2(DZFDouble renewrate2) {
		this.renewrate2 = renewrate2;
	}

	public DZFDouble getRenewmny2() {
		return renewmny2;
	}

	public void setRenewmny2(DZFDouble renewmny2) {
		this.renewmny2 = renewmny2;
	}

	public Integer getIexpirenum3() {
		return iexpirenum3;
	}

	public void setIexpirenum3(Integer iexpirenum3) {
		this.iexpirenum3 = iexpirenum3;
	}

	public Integer getIrenewnum3() {
		return irenewnum3;
	}

	public void setIrenewnum3(Integer irenewnum3) {
		this.irenewnum3 = irenewnum3;
	}

	public DZFDouble getRenewrate3() {
		return renewrate3;
	}

	public void setRenewrate3(DZFDouble renewrate3) {
		this.renewrate3 = renewrate3;
	}

	public DZFDouble getRenewmny3() {
		return renewmny3;
	}

	public void setRenewmny3(DZFDouble renewmny3) {
		this.renewmny3 = renewmny3;
	}

	public Integer getIexpirenum4() {
		return iexpirenum4;
	}

	public void setIexpirenum4(Integer iexpirenum4) {
		this.iexpirenum4 = iexpirenum4;
	}

	public Integer getIrenewnum4() {
		return irenewnum4;
	}

	public void setIrenewnum4(Integer irenewnum4) {
		this.irenewnum4 = irenewnum4;
	}

	public DZFDouble getRenewrate4() {
		return renewrate4;
	}

	public void setRenewrate4(DZFDouble renewrate4) {
		this.renewrate4 = renewrate4;
	}

	public DZFDouble getRenewmny4() {
		return renewmny4;
	}

	public void setRenewmny4(DZFDouble renewmny4) {
		this.renewmny4 = renewmny4;
	}

	public Integer getIexpirenum5() {
		return iexpirenum5;
	}

	public void setIexpirenum5(Integer iexpirenum5) {
		this.iexpirenum5 = iexpirenum5;
	}

	public Integer getIrenewnum5() {
		return irenewnum5;
	}

	public void setIrenewnum5(Integer irenewnum5) {
		this.irenewnum5 = irenewnum5;
	}

	public DZFDouble getRenewrate5() {
		return renewrate5;
	}

	public void setRenewrate5(DZFDouble renewrate5) {
		this.renewrate5 = renewrate5;
	}

	public DZFDouble getRenewmny5() {
		return renewmny5;
	}

	public void setRenewmny5(DZFDouble renewmny5) {
		this.renewmny5 = renewmny5;
	}
	
}
