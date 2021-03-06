package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDouble;

@SuppressWarnings("rawtypes")
public class CustCountVO extends SuperVO {

	private static final long serialVersionUID = 7233443153082029621L;
	
	@FieldAlias("pid")
	private String pk_corp;//会计公司主键
	
	private String chargedeptname;//公司性质
	
	private String industry;//行业主键
	
	private String industryname;//行业名称
	
	private String industrycode;//行业编码
	
	private String key;//主键
	
	private Integer num;

	private DZFDouble summny;
	
    @FieldAlias("isncust")
    private DZFBoolean isncust;// 是否存量客户（Y是存量客户）
	
	public DZFBoolean getIsncust() {
		return isncust;
	}

	public void setIsncust(DZFBoolean isncust) {
		this.isncust = isncust;
	}

	public String getIndustrycode() {
		return industrycode;
	}

	public void setIndustrycode(String industrycode) {
		this.industrycode = industrycode;
	}

	public DZFDouble getSummny() {
		return summny;
	}

	public void setSummny(DZFDouble summny) {
		this.summny = summny;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIndustry() {
		return industry;
	}

	public String getIndustryname() {
		return industryname;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public void setIndustryname(String industryname) {
		this.industryname = industryname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public Integer getNum() {
		return num;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public void setNum(Integer num) {
		this.num = num;
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
