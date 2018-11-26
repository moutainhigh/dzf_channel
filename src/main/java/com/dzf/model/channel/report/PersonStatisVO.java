package com.dzf.model.channel.report;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.dzf.pub.lang.DZFDouble;

@SuppressWarnings({ "rawtypes", "serial" })
public class PersonStatisVO extends DataVO{
	
	private Integer jms01;//机构负责人
	
	private Integer jms02;//会计经理
	private Integer jms06;//主管会计
	private Integer jms07;//主办会计
	private Integer jms08;//记账会计
	private Integer jms09;//财税支持
	private Integer jms05;//外勤主管
	private Integer jms11;//外勤
	
	private Integer jms03;//销售经理
	private Integer jms04;//销售主管
	private Integer jms10;//销售
	
	private Integer lznum;//离职数
	private DZFDouble ltotal;//流失率 
	
	private DZFDouble ktotal;//会计人员
	private DZFDouble xtotal;//销售人员
	private Integer total;//总用户
	
	private Integer knum;//会计人员用户总数
	private Integer xnum;//销售人员用户总数
	
	public Integer getLznum() {
		return lznum;
	}
	public void setLznum(Integer lznum) {
		this.lznum = lznum;
	}
	public DZFDouble getLtotal() {
		return ltotal;
	}
	public void setLtotal(DZFDouble ltotal) {
		this.ltotal = ltotal;
	}

	public Integer getJms01() {
		return jms01;
	}
	public void setJms01(Integer jms01) {
		this.jms01 = jms01;
	}
	public Integer getKnum() {
		return knum;
	}
	public void setKnum(Integer knum) {
		this.knum = knum;
	}
	public Integer getXnum() {
		return xnum;
	}
	public void setXnum(Integer xnum) {
		this.xnum = xnum;
	}
	public Integer getJms02() {
		return jms02;
	}
	public void setJms02(Integer jms02) {
		this.jms02 = jms02;
	}
	public Integer getJms03() {
		return jms03;
	}
	public void setJms03(Integer jms03) {
		this.jms03 = jms03;
	}
	public Integer getJms04() {
		return jms04;
	}
	public void setJms04(Integer jms04) {
		this.jms04 = jms04;
	}
	public Integer getJms05() {
		return jms05;
	}
	public void setJms05(Integer jms05) {
		this.jms05 = jms05;
	}
	public Integer getJms06() {
		return jms06;
	}
	public void setJms06(Integer jms06) {
		this.jms06 = jms06;
	}
	public Integer getJms07() {
		return jms07;
	}
	public void setJms07(Integer jms07) {
		this.jms07 = jms07;
	}
	public Integer getJms08() {
		return jms08;
	}
	public void setJms08(Integer jms08) {
		this.jms08 = jms08;
	}
	public Integer getJms09() {
		return jms09;
	}
	public void setJms09(Integer jms09) {
		this.jms09 = jms09;
	}
	public Integer getJms10() {
		return jms10;
	}
	public void setJms10(Integer jms10) {
		this.jms10 = jms10;
	}
	public Integer getJms11() {
		return jms11;
	}
	public void setJms11(Integer jms11) {
		this.jms11 = jms11;
	}
	public DZFDouble getKtotal() {
		return ktotal;
	}
	public void setKtotal(DZFDouble ktotal) {
		this.ktotal = ktotal;
	}
	public DZFDouble getXtotal() {
		return xtotal;
	}
	public void setXtotal(DZFDouble xtotal) {
		this.xtotal = xtotal;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	
	@Override  
	public boolean equals(Object o) {  
        if (o == this) return true;  
        if (!(o instanceof PersonStatisVO)) {  
            return false;  
        }  
        PersonStatisVO vo = (PersonStatisVO) o;  
        return new EqualsBuilder().append(pk_corp, vo.pk_corp).isEquals();  
	 }  
	  
    @Override  
    public int hashCode() {  
        return new HashCodeBuilder(17, 37).append(pk_corp).toHashCode();  
    } 
	
}
