package com.dzf.model.channel.report;

import java.util.HashMap;

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
	
	private HashMap<String, Object> hash = new HashMap<String, Object>();
	
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
	private DZFDouble ndeductmny_sum;//扣款总额
	
	@FieldAlias("retnum")
	private Integer iretnum;//退回合同数
	
	@FieldAlias("retmny")
	private DZFDouble nretmny;//退回金额
	
	@FieldAlias("stocknum")
	private Integer istocknum;//存量客户数
	
    @FieldAlias("custnum")
    private Integer icustnum;//存量合同数
    
    @FieldAlias("zeronum")
    private Integer izeronum;//0扣款(非存量)合同数
    
    @FieldAlias("dednum")
    private Integer idednum;//非存量合同数
    
	@FieldAlias("mid")
	private String vmanager;//渠道经理
	
	@FieldAlias("oid")
	private String voperater;//渠道运营

	public HashMap<String, Object> getHash() {
		return hash;
	}

	public void setHash(HashMap<String, Object> hash) {
		this.hash = hash;
	}

	public String getVmanager() {
		return vmanager;
	}

	public void setVmanager(String vmanager) {
		this.vmanager = vmanager;
	}

	public String getVoperater() {
		return voperater;
	}

	public void setVoperater(String voperater) {
		this.voperater = voperater;
	}

	public Integer getIcustnum() {
		return icustnum;
	}

	public void setIcustnum(Integer icustnum) {
		this.icustnum = icustnum;
	}

	public Integer getIzeronum() {
		return izeronum;
	}

	public void setIzeronum(Integer izeronum) {
		this.izeronum = izeronum;
	}

	public Integer getIdednum() {
		return idednum;
	}

	public void setIdednum(Integer idednum) {
		this.idednum = idednum;
	}

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
