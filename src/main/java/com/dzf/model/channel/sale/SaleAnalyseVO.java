package com.dzf.model.channel.sale;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 销售数据分析
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class SaleAnalyseVO extends SuperVO {
	
    @FieldAlias("corpid")
    private String pk_corp; // 渠道商主键
    
    @FieldAlias("corpnm")
    private String corpname; // 渠道商名称
    
    @FieldAlias("aname")
    private String areaname;//大区名称
	
	@FieldAlias("ovince")
	private Integer vprovince;// 地区
	    
	@FieldAlias("provname")
	private String vprovname;// 地区名称

	@FieldAlias("bdate")
	private String dbegindate; // 开始日期

	@FieldAlias("edate")
	private String denddate; // 结束日期
	
	
	@FieldAlias("visnum")
	private Integer ivisitnum;//拜访数
	
	@FieldAlias("viscustnum")
	private Integer iviscustnum;//拜访客户数
	
	@FieldAlias("signum")
	private Integer isignnum;//签约客户数
	
	@FieldAlias("agentnum")
	private Integer iagentnum;//代账合同数
	
	@FieldAlias("increnum")
	private Integer iincrenum;//增值合同数
	
	@FieldAlias("contmny")
	private DZFDouble contractmny;//合同金额
	
	@FieldAlias("pricemny")
	private DZFDouble pricemny;//客单价
	
	@FieldAlias("contype")
	private Integer icontractype;//合同类型  1：代理记账合同；2：增值服务合同；
	
	@FieldAlias("num")
	private Integer inum;//合同数
	
	private String innercode;//排序用的编码

	@Override
	public String getPKFieldName() {
		return null;
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

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public String getDbegindate() {
		return dbegindate;
	}

	public void setDbegindate(String dbegindate) {
		this.dbegindate = dbegindate;
	}

	public String getDenddate() {
		return denddate;
	}

	public void setDenddate(String denddate) {
		this.denddate = denddate;
	}

	public Integer getIvisitnum() {
		return ivisitnum;
	}

	public void setIvisitnum(Integer ivisitnum) {
		this.ivisitnum = ivisitnum;
	}

	public Integer getIviscustnum() {
		return iviscustnum;
	}

	public void setIviscustnum(Integer iviscustnum) {
		this.iviscustnum = iviscustnum;
	}

	public Integer getIsignnum() {
		return isignnum;
	}

	public void setIsignnum(Integer isignnum) {
		this.isignnum = isignnum;
	}

	public Integer getIagentnum() {
		return iagentnum;
	}

	public void setIagentnum(Integer iagentnum) {
		this.iagentnum = iagentnum;
	}

	public Integer getIincrenum() {
		return iincrenum;
	}

	public void setIincrenum(Integer iincrenum) {
		this.iincrenum = iincrenum;
	}

	public DZFDouble getContractmny() {
		return contractmny;
	}

	public void setContractmny(DZFDouble contractmny) {
		this.contractmny = contractmny;
	}

	public DZFDouble getPricemny() {
		return pricemny;
	}

	public void setPricemny(DZFDouble pricemny) {
		this.pricemny = pricemny;
	}

	public Integer getIcontractype() {
		return icontractype;
	}

	public void setIcontractype(Integer icontractype) {
		this.icontractype = icontractype;
	}

	public Integer getInum() {
		return inum;
	}

	public void setInum(Integer inum) {
		this.inum = inum;
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
