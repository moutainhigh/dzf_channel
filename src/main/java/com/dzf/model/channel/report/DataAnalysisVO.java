package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;

/**
 * 加盟商数据分析
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class DataAnalysisVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
	@FieldAlias("corpid")
	private String pk_corp;//加盟商主键
	
	@FieldAlias("corpname")
	private String corpname;//加盟商名称
	
	@FieldAlias("aname")
    public String areaname;//大区名称
	
	@FieldAlias("provname")
	public String vprovname;// 省市名称
	
    @FieldAlias("jdate")
    private DZFDate djoindate;//加盟日期
    
    @FieldAlias("dreldate")
    private DZFDate drelievedate;//解约日期
    
    @FieldAlias("smcnum")
    private Integer ismcustnum;//小规模客户数
    
    @FieldAlias("gecnum")
    private Integer igecustnum;//一般人客户数
    
    @FieldAlias("smsnum")
    private Integer ismstocknum;//小规模存量客户数
    
    @FieldAlias("gesnum")
    private Integer igestocknum;//一般人存量客户数
    
    @FieldAlias("smnsnum")
    private Integer ismnstocknum;//小规模非存量客户数
    
    @FieldAlias("gensnum")
    private Integer igenstocknum;//一般人非存量客户数
    
    @FieldAlias("sconnum")
    private Integer istockconnum;//存量合同
    
    @FieldAlias("zconnum")
    private Integer izeroconnum;//0扣款(非存量)合同
    
    @FieldAlias("nsconnum")
    private Integer instockconnum;//非存量合同
    
    @FieldAlias("ntotmny")
	private DZFDouble ntotalmny; // 合同代账费
    
	@FieldAlias("naccmny")
	private DZFDouble naccountmny; // 合同代账费
	
	@FieldAlias("nbmny")
	private DZFDouble nbookmny; // 账本费
	
	@FieldAlias("ndpmny")
	private DZFDouble ndepositmny;//保证金
	
	@FieldAlias("npmny")
	private DZFDouble npaymentmny;//预付款
	
	@FieldAlias("nrmny")
	private DZFDouble nrebatemny;//返点
	
	@FieldAlias("ndtmny")
	private DZFDouble ndeductmny;//合同扣款
	
	@FieldAlias("ngbmny")
	private DZFDouble ngoodsbuymny;//商品购买
	
	@FieldAlias("ssumnum")
	private Integer isumcustnum;//总用户数
	
	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public DZFDate getDjoindate() {
		return djoindate;
	}

	public void setDjoindate(DZFDate djoindate) {
		this.djoindate = djoindate;
	}

	public DZFDate getDrelievedate() {
		return drelievedate;
	}

	public void setDrelievedate(DZFDate drelievedate) {
		this.drelievedate = drelievedate;
	}

	public Integer getIsmcustnum() {
		return ismcustnum;
	}

	public void setIsmcustnum(Integer ismcustnum) {
		this.ismcustnum = ismcustnum;
	}

	public Integer getIgecustnum() {
		return igecustnum;
	}

	public void setIgecustnum(Integer igecustnum) {
		this.igecustnum = igecustnum;
	}

	public Integer getIsmstocknum() {
		return ismstocknum;
	}

	public void setIsmstocknum(Integer ismstocknum) {
		this.ismstocknum = ismstocknum;
	}

	public Integer getIgestocknum() {
		return igestocknum;
	}

	public void setIgestocknum(Integer igestocknum) {
		this.igestocknum = igestocknum;
	}

	public Integer getIsmnstocknum() {
		return ismnstocknum;
	}

	public void setIsmnstocknum(Integer ismnstocknum) {
		this.ismnstocknum = ismnstocknum;
	}

	public Integer getIgenstocknum() {
		return igenstocknum;
	}

	public void setIgenstocknum(Integer igenstocknum) {
		this.igenstocknum = igenstocknum;
	}

	public Integer getIstockconnum() {
		return istockconnum;
	}

	public void setIstockconnum(Integer istockconnum) {
		this.istockconnum = istockconnum;
	}

	public Integer getIzeroconnum() {
		return izeroconnum;
	}

	public void setIzeroconnum(Integer izeroconnum) {
		this.izeroconnum = izeroconnum;
	}

	public Integer getInstockconnum() {
		return instockconnum;
	}

	public void setInstockconnum(Integer instockconnum) {
		this.instockconnum = instockconnum;
	}

	public DZFDouble getNaccountmny() {
		return naccountmny;
	}

	public void setNaccountmny(DZFDouble naccountmny) {
		this.naccountmny = naccountmny;
	}

	public DZFDouble getNbookmny() {
		return nbookmny;
	}

	public void setNbookmny(DZFDouble nbookmny) {
		this.nbookmny = nbookmny;
	}

	public DZFDouble getNdepositmny() {
		return ndepositmny;
	}

	public void setNdepositmny(DZFDouble ndepositmny) {
		this.ndepositmny = ndepositmny;
	}

	public DZFDouble getNpaymentmny() {
		return npaymentmny;
	}

	public void setNpaymentmny(DZFDouble npaymentmny) {
		this.npaymentmny = npaymentmny;
	}

	public DZFDouble getNrebatemny() {
		return nrebatemny;
	}

	public void setNrebatemny(DZFDouble nrebatemny) {
		this.nrebatemny = nrebatemny;
	}

	public DZFDouble getNdeductmny() {
		return ndeductmny;
	}

	public void setNdeductmny(DZFDouble ndeductmny) {
		this.ndeductmny = ndeductmny;
	}

	public DZFDouble getNgoodsbuymny() {
		return ngoodsbuymny;
	}

	public void setNgoodsbuymny(DZFDouble ngoodsbuymny) {
		this.ngoodsbuymny = ngoodsbuymny;
	}

	public Integer getIsumcustnum() {
		return isumcustnum;
	}

	public void setIsumcustnum(Integer isumcustnum) {
		this.isumcustnum = isumcustnum;
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
