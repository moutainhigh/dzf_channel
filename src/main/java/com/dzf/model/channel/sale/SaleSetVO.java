package com.dzf.model.channel.sale;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 销售设置VO
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class SaleSetVO extends SuperVO {

    @FieldAlias("id")
    private String pk_saleset; // 销售设置主键
	
    @FieldAlias("corpid")
    private String pk_corp; // 渠道商主键
    
    @FieldAlias("corpnm")
    private String corpname; // 渠道商名称
    
    @FieldAlias("isfirecovery")
    private DZFBoolean isfirecovery;//是否回收 first
    
    @FieldAlias("finum")
    private Integer firstnum;//领取后，多少个自然日回收
    
    @FieldAlias("isserecovery")
    private DZFBoolean isserecovery;//是否回收 second
    
    @FieldAlias("senum")
    private Integer secondnum;//销售持有30个自然日，未拜访几次，回收
    
    @FieldAlias("isthrecovery")
    private DZFBoolean isthrecovery;//是否回收 third
    
    @FieldAlias("thnum")
    private Integer thirdnum;//多少个自然日，为签约
    
    @FieldAlias("isreceive")
    private DZFBoolean isreceive;//是否领取
    
    @FieldAlias("recnum")
    private Integer receivenum;//领取上线，未签约客户
    
    @FieldAlias("relnum")
    private Integer releasenum;//延期规则：释放日期增加多少个自然日
    
    @FieldAlias("ficrla")
    private String classifyfir; // 客户分类1 first
    
    @FieldAlias("seccla")
    private String classifysec; // 客户分类2 second
    
    @FieldAlias("thicla")
    private String classifythi; // 客户分类3 third
    
    @FieldAlias("foucla")
    private String classifyfou; // 客户分类4 fourch
    
    @FieldAlias("fifcla")
    private String classifyfif; // 客户分类5 fifth
    
    @FieldAlias("lmpsnid")
    private String lastmodifypsnid; // 最后修改人

    @FieldAlias("lmpsn")
    private String lastmodifypsn; // 最后修改人名称

    @FieldAlias("lsdate")
    private DZFDateTime lastmodifydate; // 最后修改日期
    
	@FieldAlias("coptid")
	private String coperatorid;// 创建人
	
	@FieldAlias("ddate")
	private DZFDate doperatedate;// 创建日期
	
	@FieldAlias("dr")
	private Integer dr;// 删除标记
	
	@FieldAlias("ts")
	private DZFDateTime ts;// 时间戳
    

	public String getPk_saleset() {
		return pk_saleset;
	}

	public void setPk_saleset(String pk_saleset) {
		this.pk_saleset = pk_saleset;
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

	public DZFBoolean getIsfirecovery() {
		return isfirecovery;
	}

	public void setIsfirecovery(DZFBoolean isfirecovery) {
		this.isfirecovery = isfirecovery;
	}

	public Integer getFirstnum() {
		return firstnum;
	}

	public void setFirstnum(Integer firstnum) {
		this.firstnum = firstnum;
	}

	public DZFBoolean getIsserecovery() {
		return isserecovery;
	}

	public void setIsserecovery(DZFBoolean isserecovery) {
		this.isserecovery = isserecovery;
	}

	public Integer getSecondnum() {
		return secondnum;
	}

	public void setSecondnum(Integer secondnum) {
		this.secondnum = secondnum;
	}

	public DZFBoolean getIsthrecovery() {
		return isthrecovery;
	}

	public void setIsthrecovery(DZFBoolean isthrecovery) {
		this.isthrecovery = isthrecovery;
	}

	public Integer getThirdnum() {
		return thirdnum;
	}

	public void setThirdnum(Integer thirdnum) {
		this.thirdnum = thirdnum;
	}

	public DZFBoolean getIsreceive() {
		return isreceive;
	}

	public void setIsreceive(DZFBoolean isreceive) {
		this.isreceive = isreceive;
	}

	public Integer getReceivenum() {
		return receivenum;
	}

	public void setReceivenum(Integer receivenum) {
		this.receivenum = receivenum;
	}

	public Integer getReleasenum() {
		return releasenum;
	}

	public void setReleasenum(Integer releasenum) {
		this.releasenum = releasenum;
	}

	public String getLastmodifypsnid() {
		return lastmodifypsnid;
	}

	public String getClassifyfir() {
		return classifyfir;
	}

	public void setClassifyfir(String classifyfir) {
		this.classifyfir = classifyfir;
	}

	public String getClassifysec() {
		return classifysec;
	}

	public void setClassifysec(String classifysec) {
		this.classifysec = classifysec;
	}

	public String getClassifythi() {
		return classifythi;
	}

	public void setClassifythi(String classifythi) {
		this.classifythi = classifythi;
	}

	public String getClassifyfou() {
		return classifyfou;
	}

	public void setClassifyfou(String classifyfou) {
		this.classifyfou = classifyfou;
	}

	public String getClassifyfif() {
		return classifyfif;
	}

	public void setClassifyfif(String classifyfif) {
		this.classifyfif = classifyfif;
	}

	public void setLastmodifypsnid(String lastmodifypsnid) {
		this.lastmodifypsnid = lastmodifypsnid;
	}

	public String getLastmodifypsn() {
		return lastmodifypsn;
	}

	public void setLastmodifypsn(String lastmodifypsn) {
		this.lastmodifypsn = lastmodifypsn;
	}

	public DZFDateTime getLastmodifydate() {
		return lastmodifydate;
	}

	public void setLastmodifydate(DZFDateTime lastmodifydate) {
		this.lastmodifydate = lastmodifydate;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "pk_saleset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_saleset";
	}

}
