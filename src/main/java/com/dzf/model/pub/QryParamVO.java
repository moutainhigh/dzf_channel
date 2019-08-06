package com.dzf.model.pub;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;

/**
 * 公用查询条件VO
 * @author 宗岩
 *
 */
@SuppressWarnings("rawtypes")
public class QryParamVO extends SuperVO {

	private static final long serialVersionUID = -8140085829123699985L;

	@FieldAlias("id")
	private String pk_bill;// 主键

	@FieldAlias("cpid")
	private String pk_corp;// 会计机构
	
	@FieldAlias("cpcode")
	private String corpcode;// 会计公司编码
	
	@FieldAlias("cpname")
	private String corpname;// 会计公司名称

	@FieldAlias("cpkid")
	private String pk_corpk;// 客户
	
	@FieldAlias("cpkcode")
	private String corpkcode;// 客户编码
	
	@FieldAlias("cpkname")
	private String corpkname;// 客户名称

	@FieldAlias("uid")
	private String cuserid;// 用户ID
	
	@FieldAlias("ucode")
	private String user_code;// 用户编码

	@FieldAlias("uname")
	private String user_name;// 用户名称
	
	@FieldAlias("qtype")
	private Integer qrytype;//查询类型
	
	@FieldAlias("stype")
	private Integer seletype;//选择类型
	
	@FieldAlias("begdate")
	private DZFDate begdate;// 开始日期

	@FieldAlias("enddate")
	private DZFDate enddate;// 结束日期
	
	@FieldAlias("period")
	private String period;// 期间
	
	@FieldAlias("ipmode")
	private Integer ipaymode;// 1:银行转账；2:支付宝；3：微信
	
	@FieldAlias("iptype")
	private Integer ipaytype;// 1:加盟费；2：预付款
	
	@FieldAlias("bperiod")
	private String beginperiod;// 开始期间

	@FieldAlias("eperiod")
	private String endperiod;// 结束期间
	
	@FieldAlias("stdate")
	private String startdate;// 开始期间

	@FieldAlias("ovdate")
	private String overdate;// 结束期间
	
	@FieldAlias("isncust")
	private DZFBoolean isncust;// 是否存量客户
	   
	@FieldAlias("destatus")
	private Integer vdeductstatus;//最新合同状态：      5:待审批： 1：审核通过； 7：已驳回；8：服务到期；；
	
	private String[] corps;//渠道商ids
	
	private Integer corptype;
	
	private Integer comptype;
	
	@FieldAlias("year")
	private String vyear;//所属季度-年
	
	@FieldAlias("season")
	private Integer iseason;//所属季度-季度
	
	@FieldAlias("aname")
    private String areaname;//大区名称
	
	@FieldAlias("ovince")
	private Integer vprovince;// 地区
	
	@FieldAlias("qrysql")
	private String vqrysql;//查询语句
	
	@FieldAlias("mid")
	private String vmanager;//渠道经理id
	
	@FieldAlias("vcode")
	private String vbillcode;//单据编码
	
	@FieldAlias("contractid")
	private String pk_contract; // 合同主键
	
	@FieldAlias("oid")
	private String voperater;//渠道运营id
	
	public String getVoperater() {
		return voperater;
	}

	public void setVoperater(String voperater) {
		this.voperater = voperater;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public String getVmanager() {
		return vmanager;
	}

	public void setVmanager(String vmanager) {
		this.vmanager = vmanager;
	}

	public String getVqrysql() {
		return vqrysql;
	}

	public void setVqrysql(String vqrysql) {
		this.vqrysql = vqrysql;
	} 

	public String getPk_contract() {
		return pk_contract;
	}

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

	public String getVyear() {
		return vyear;
	}

	public Integer getIseason() {
		return iseason;
	}

	public void setVyear(String vyear) {
		this.vyear = vyear;
	}

	public Integer getSeletype() {
		return seletype;
	}

	public void setSeletype(Integer seletype) {
		this.seletype = seletype;
	}

	public void setIseason(Integer iseason) {
		this.iseason = iseason;
	}

	public String getAreaname() {
		return areaname;
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

	public Integer getCorptype() {
        return corptype;
    }

    public void setCorptype(Integer corptype) {
        this.corptype = corptype;
    }

    public String getBeginperiod() {
		return beginperiod;
	}

	public String getEndperiod() {
		return endperiod;
	}

	public String[] getCorps() {
		return corps;
	}

	public void setCorps(String[] corps) {
		this.corps = corps;
	}

	public void setBeginperiod(String beginperiod) {
		this.beginperiod = beginperiod;
	}

	public void setEndperiod(String endperiod) {
		this.endperiod = endperiod;
	}

	public Integer getIpaymode() {
        return ipaymode;
    }

    public void setIpaymode(Integer ipaymode) {
        this.ipaymode = ipaymode;
    }

    public Integer getIpaytype() {
        return ipaytype;
    }

    public void setIpaytype(Integer ipaytype) {
        this.ipaytype = ipaytype;
    }

    public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public DZFBoolean getIsncust() {
		return isncust;
	}

	public void setIsncust(DZFBoolean isncust) {
		this.isncust = isncust;
	}

	public Integer getVdeductstatus() {
		return vdeductstatus;
	}

	public void setVdeductstatus(Integer vdeductstatus) {
		this.vdeductstatus = vdeductstatus;
	}

	public DZFDate getBegdate() {
		return begdate;
	}

	public void setBegdate(DZFDate begdate) {
		this.begdate = begdate;
	}

	public DZFDate getEnddate() {
		return enddate;
	}

	public void setEnddate(DZFDate enddate) {
		this.enddate = enddate;
	}

	public String getPk_bill() {
		return pk_bill;
	}

	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
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

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getCorpkcode() {
		return corpkcode;
	}

	public void setCorpkcode(String corpkcode) {
		this.corpkcode = corpkcode;
	}

	public String getCorpkname() {
		return corpkname;
	}

	public void setCorpkname(String corpkname) {
		this.corpkname = corpkname;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getUser_code() {
		return user_code;
	}

	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Integer getQrytype() {
		return qrytype;
	}

	public void setQrytype(Integer qrytype) {
		this.qrytype = qrytype;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getOverdate() {
		return overdate;
	}

	public void setOverdate(String overdate) {
		this.overdate = overdate;
	}
	
	public Integer getComptype() {
		return comptype;
	}

	public void setComptype(Integer comptype) {
		this.comptype = comptype;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}	

}
