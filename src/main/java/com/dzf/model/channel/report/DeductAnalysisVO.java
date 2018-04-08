package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 加盟商扣款分析
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class DeductAnalysisVO extends SuperVO {

	private static final long serialVersionUID = -7715601659614330398L;
	
	@FieldAlias("corpid")
	private String pk_corp;//加盟商主键
	
	@FieldAlias("corpcode")
    private String corpcode;//加盟商编码
	
	@FieldAlias("corpname")
	private String corpname;//加盟商名称
	
	@FieldAlias("corpnum")
	private Integer icorpnums;//扣款户数
	
	@FieldAlias("dedmny")
	private DZFDouble ndeducmny;//扣款金额
	
	@FieldAlias("sumnum")
	private Integer icorpnums_sum;//扣款总户数
	
	@FieldAlias("summny")
	private DZFDouble ndeductmny_sum;//扣款总总额
	
	@FieldAlias("retnum")
	private Integer iretnum;//退回合同数
	
	@FieldAlias("retmny")
	private DZFDouble nretmny;//退回金额
	
	@FieldAlias("stocknum")
	private Integer istocknum;//存量客户数

	public Integer getIretnum() {
		return iretnum;
	}

	public void setIretnum(Integer iretnum) {
		this.iretnum = iretnum;
	}

	public DZFDouble getNretmny() {
		return nretmny;
	}

	public void setNretmny(DZFDouble nretmny) {
		this.nretmny = nretmny;
	}

	public Integer getIstocknum() {
		return istocknum;
	}

	public void setIstocknum(Integer istocknum) {
		this.istocknum = istocknum;
	}

	public Integer getIcorpnums() {
		return icorpnums;
	}

	public void setIcorpnums(Integer icorpnums) {
		this.icorpnums = icorpnums;
	}

	public Integer getIcorpnums_sum() {
		return icorpnums_sum;
	}

	public void setIcorpnums_sum(Integer icorpnums_sum) {
		this.icorpnums_sum = icorpnums_sum;
	}

	public DZFDouble getNdeductmny_sum() {
		return ndeductmny_sum;
	}

	public void setNdeductmny_sum(DZFDouble ndeductmny_sum) {
		this.ndeductmny_sum = ndeductmny_sum;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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

	public DZFDouble getNdeducmny() {
		return ndeducmny;
	}

	public void setNdeducmny(DZFDouble ndeducmny) {
		this.ndeducmny = ndeducmny;
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
