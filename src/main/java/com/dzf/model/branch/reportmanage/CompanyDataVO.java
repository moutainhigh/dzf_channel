package com.dzf.model.branch.reportmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.lang.DZFDouble;

/**
 * 公司数据统计VO
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class CompanyDataVO extends SuperVO {

	private String branchname; // 机构名称

	private String pk_corp; // 公司主键
	
	private String innercode;//公司编码

	private String corpname; // 公司名称
	
	private Integer allcorp;//现有客户
	
	private Integer ybrcorp;//一般人
	
	private Integer xgmcorp;//小规模
	
	private Integer addcorp;//新增客户
	
	private Integer losecorp;//流失客户
	
	private Integer contcorp;//客户合同数
	
	private DZFDouble totalmny;//合同总金额
	
	private DZFDouble ysmny;//已收合同金额
	
	private DZFDouble wsmny;//未收合同金额
	
	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public Integer getAllcorp() {
		return allcorp;
	}

	public void setAllcorp(Integer allcorp) {
		this.allcorp = allcorp;
	}

	public Integer getYbrcorp() {
		return ybrcorp;
	}

	public void setYbrcorp(Integer ybrcorp) {
		this.ybrcorp = ybrcorp;
	}

	public Integer getXgmcorp() {
		return xgmcorp;
	}

	public void setXgmcorp(Integer xgmcorp) {
		this.xgmcorp = xgmcorp;
	}

	public Integer getAddcorp() {
		return addcorp;
	}

	public void setAddcorp(Integer addcorp) {
		this.addcorp = addcorp;
	}

	public Integer getLosecorp() {
		return losecorp;
	}

	public void setLosecorp(Integer losecorp) {
		this.losecorp = losecorp;
	}

	public Integer getContcorp() {
		return contcorp;
	}

	public void setContcorp(Integer contcorp) {
		this.contcorp = contcorp;
	}

	public DZFDouble getTotalmny() {
		return totalmny;
	}

	public void setTotalmny(DZFDouble totalmny) {
		this.totalmny = totalmny;
	}

	public DZFDouble getYsmny() {
		return ysmny;
	}

	public void setYsmny(DZFDouble ysmny) {
		this.ysmny = ysmny;
	}

	public DZFDouble getWsmny() {
		return wsmny;
	}

	public void setWsmny(DZFDouble wsmny) {
		this.wsmny = wsmny;
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
