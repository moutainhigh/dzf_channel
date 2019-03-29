package com.dzf.model.channel.report;

import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 加盟商客户数量、金额统计表
 * @author 宗岩
 *
 */
public class CustNumMoneyRepVO extends DataVO {

	private static final long serialVersionUID = 7914020685684503615L;
	
	@FieldAlias("stockcusts")
	private Integer istockcustsmall;//客户-小规模
	
	@FieldAlias("stockcustt")
	private Integer istockcusttaxpay;//客户-一般纳税人
	
	@FieldAlias("stockconts")
	private DZFDouble istockcontsmall;//客户合同-小规模
	
	@FieldAlias("stockcontt")
	private DZFDouble istockconttaxpay;//客户合同-一般纳税人
	
	@FieldAlias("newcusts")
	private Integer inewcustsmall;//新增客户-小规模
	
	@FieldAlias("newcustt")
	private Integer inewcusttaxpay;//新增客户-一般纳税人

	@FieldAlias("newconts")
	private DZFDouble inewcontsmall;//新增客户合同-小规模
	
	@FieldAlias("newcontt")
	private DZFDouble inewconttaxpay;//新增客户合同-一般纳税人
	
	@FieldAlias("renewcusts")
	private Integer irenewcustsmall;//续费客户-小规模
	
	@FieldAlias("renewcustt")
	private Integer irenewcusttaxpay;//续费客户-一般纳税人

	@FieldAlias("renewconts")
	private DZFDouble irenewcontsmall;//续费客户合同-小规模
	
	@FieldAlias("renewcontt")
	private DZFDouble irenewconttaxpay;//续费客户合同-一般纳税人
	
	@FieldAlias("newcustrates")
	private DZFDouble inewcustratesmall;//新增客户增长率-小规模
	
	@FieldAlias("newcustratet")
	private DZFDouble inewcustratetaxpay;//新增客户增长率-一般纳税人
	
	@FieldAlias("newcontrates")
	private DZFDouble inewcontratesmall;//新增合同增长率-小规模
	
	@FieldAlias("newcontratet")
	private DZFDouble inewcontratetaxpay;//新增合同增长率-一般纳税人
	
	@FieldAlias("renewcustrates")
	private DZFDouble irenewcustratesmall;//续费客户占比-小规模
	
	@FieldAlias("renewcustratet")
	private DZFDouble irenewcustratetaxpay;//续费客户占比-一般纳税人
	
	@FieldAlias("renewcontrates")
	private DZFDouble irenewcontratesmall;//续费合同占比-小规模
	
	@FieldAlias("renewcontratet")
	private DZFDouble irenewcontratetaxpay;//续费合同占比-一般纳税人
	
	//****************************上一月数据*******************************
	
	@FieldAlias("lastnewcusts")
	private Integer ilastnewcustsmall;//新增客户-小规模
	
	@FieldAlias("lastnewcustt")
	private Integer ilastnewcusttaxpay;//新增客户-一般纳税人

	@FieldAlias("lastnewconts")
	private DZFDouble ilastnewcontsmall;//新增客户合同-小规模
	
	@FieldAlias("lastnewcontt")
	private DZFDouble ilastnewconttaxpay;//新增客户合同-一般纳税人
	
	@FieldAlias("lastrenewcusts")
	private Integer ilastrenewcustsmall;//续费客户-小规模
	
	@FieldAlias("lastrenewcustt")
	private Integer ilastrenewcusttaxpay;//续费客户-一般纳税人

	@FieldAlias("lastrenewconts")
	private DZFDouble ilastrenewcontsmall;//续费客户合同-小规模
	
	@FieldAlias("lastrenewcontt")
	private DZFDouble ilastrenewconttaxpay;//续费客户合同-一般纳税人
	
	@FieldAlias("custsum")
	private Integer icustsum;//客户合计
	
	@FieldAlias("contnum")
	private Integer icontnum;//提单数
	
	@FieldAlias("yrenewnum")
	private Integer iyrenewnum;//应续签客户数
	
	@FieldAlias("renewnum")
	private Integer irenewnum;//已续签客户数

	public Integer getIyrenewnum() {
		return iyrenewnum;
	}

	public void setIyrenewnum(Integer iyrenewnum) {
		this.iyrenewnum = iyrenewnum;
	}

	public Integer getIrenewnum() {
		return irenewnum;
	}

	public void setIrenewnum(Integer irenewnum) {
		this.irenewnum = irenewnum;
	}

	public Integer getIcontnum() {
		return icontnum;
	}

	public void setIcontnum(Integer icontnum) {
		this.icontnum = icontnum;
	}

	public Integer getIcustsum() {
		return icustsum;
	}

	public void setIcustsum(Integer icustsum) {
		this.icustsum = icustsum;
	}

	public Integer getIlastnewcustsmall() {
		return ilastnewcustsmall;
	}

	public void setIlastnewcustsmall(Integer ilastnewcustsmall) {
		this.ilastnewcustsmall = ilastnewcustsmall;
	}

	public Integer getIlastnewcusttaxpay() {
		return ilastnewcusttaxpay;
	}

	public void setIlastnewcusttaxpay(Integer ilastnewcusttaxpay) {
		this.ilastnewcusttaxpay = ilastnewcusttaxpay;
	}

	public DZFDouble getIlastnewcontsmall() {
		return ilastnewcontsmall;
	}

	public void setIlastnewcontsmall(DZFDouble ilastnewcontsmall) {
		this.ilastnewcontsmall = ilastnewcontsmall;
	}

	public DZFDouble getIlastnewconttaxpay() {
		return ilastnewconttaxpay;
	}

	public String getCusername() {
		return cusername;
	}

	public void setCusername(String cusername) {
		this.cusername = cusername;
	}

	public void setIlastnewconttaxpay(DZFDouble ilastnewconttaxpay) {
		this.ilastnewconttaxpay = ilastnewconttaxpay;
	}

	public Integer getIlastrenewcustsmall() {
		return ilastrenewcustsmall;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public void setIlastrenewcustsmall(Integer ilastrenewcustsmall) {
		this.ilastrenewcustsmall = ilastrenewcustsmall;
	}

	public Integer getIlastrenewcusttaxpay() {
		return ilastrenewcusttaxpay;
	}

	public void setIlastrenewcusttaxpay(Integer ilastrenewcusttaxpay) {
		this.ilastrenewcusttaxpay = ilastrenewcusttaxpay;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	public DZFDouble getIlastrenewcontsmall() {
		return ilastrenewcontsmall;
	}

	public void setIlastrenewcontsmall(DZFDouble ilastrenewcontsmall) {
		this.ilastrenewcontsmall = ilastrenewcontsmall;
	}

	public DZFDouble getIlastrenewconttaxpay() {
		return ilastrenewconttaxpay;
	}

	public void setIlastrenewconttaxpay(DZFDouble ilastrenewconttaxpay) {
		this.ilastrenewconttaxpay = ilastrenewconttaxpay;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public Integer getIstockcustsmall() {
		return istockcustsmall;
	}

	public Integer getIstockcusttaxpay() {
		return istockcusttaxpay;
	}

	public DZFDouble getIstockcontsmall() {
		return istockcontsmall;
	}

	public DZFDouble getIstockconttaxpay() {
		return istockconttaxpay;
	}

	public Integer getInewcustsmall() {
		return inewcustsmall;
	}

	public Integer getInewcusttaxpay() {
		return inewcusttaxpay;
	}

	public DZFDouble getInewcontsmall() {
		return inewcontsmall;
	}

	public DZFDouble getInewconttaxpay() {
		return inewconttaxpay;
	}

	public Integer getIrenewcustsmall() {
		return irenewcustsmall;
	}

	public Integer getIrenewcusttaxpay() {
		return irenewcusttaxpay;
	}

	public DZFDouble getIrenewcontsmall() {
		return irenewcontsmall;
	}

	public DZFDouble getIrenewconttaxpay() {
		return irenewconttaxpay;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setIstockcustsmall(Integer istockcustsmall) {
		this.istockcustsmall = istockcustsmall;
	}

	public void setIstockcusttaxpay(Integer istockcusttaxpay) {
		this.istockcusttaxpay = istockcusttaxpay;
	}

	public void setIstockcontsmall(DZFDouble istockcontsmall) {
		this.istockcontsmall = istockcontsmall;
	}

	public void setIstockconttaxpay(DZFDouble istockconttaxpay) {
		this.istockconttaxpay = istockconttaxpay;
	}

	public void setInewcustsmall(Integer inewcustsmall) {
		this.inewcustsmall = inewcustsmall;
	}

	public void setInewcusttaxpay(Integer inewcusttaxpay) {
		this.inewcusttaxpay = inewcusttaxpay;
	}

	public void setInewcontsmall(DZFDouble inewcontsmall) {
		this.inewcontsmall = inewcontsmall;
	}

	public void setInewconttaxpay(DZFDouble inewconttaxpay) {
		this.inewconttaxpay = inewconttaxpay;
	}

	public void setIrenewcustsmall(Integer irenewcustsmall) {
		this.irenewcustsmall = irenewcustsmall;
	}

	public void setIrenewcusttaxpay(Integer irenewcusttaxpay) {
		this.irenewcusttaxpay = irenewcusttaxpay;
	}

	public void setIrenewcontsmall(DZFDouble irenewcontsmall) {
		this.irenewcontsmall = irenewcontsmall;
	}

	public void setIrenewconttaxpay(DZFDouble irenewconttaxpay) {
		this.irenewconttaxpay = irenewconttaxpay;
	}

	public DZFDouble getInewcontratesmall() {
		return inewcontratesmall;
	}

	public void setInewcontratesmall(DZFDouble inewcontratesmall) {
		this.inewcontratesmall = inewcontratesmall;
	}

	public DZFDouble getInewcontratetaxpay() {
		return inewcontratetaxpay;
	}

	public void setInewcontratetaxpay(DZFDouble inewcontratetaxpay) {
		this.inewcontratetaxpay = inewcontratetaxpay;
	}

	public DZFDouble getIrenewcontratesmall() {
		return irenewcontratesmall;
	}

	public void setIrenewcontratesmall(DZFDouble irenewcontratesmall) {
		this.irenewcontratesmall = irenewcontratesmall;
	}

	public DZFDouble getIrenewcontratetaxpay() {
		return irenewcontratetaxpay;
	}

	public void setIrenewcontratetaxpay(DZFDouble irenewcontratetaxpay) {
		this.irenewcontratetaxpay = irenewcontratetaxpay;
	}

	public DZFDouble getInewcustratesmall() {
		return inewcustratesmall;
	}

	public DZFDouble getInewcustratetaxpay() {
		return inewcustratetaxpay;
	}

	public DZFDouble getIrenewcustratesmall() {
		return irenewcustratesmall;
	}

	public DZFDouble getIrenewcustratetaxpay() {
		return irenewcustratetaxpay;
	}

	public void setInewcustratesmall(DZFDouble inewcustratesmall) {
		this.inewcustratesmall = inewcustratesmall;
	}

	public void setInewcustratetaxpay(DZFDouble inewcustratetaxpay) {
		this.inewcustratetaxpay = inewcustratetaxpay;
	}

	public void setIrenewcustratesmall(DZFDouble irenewcustratesmall) {
		this.irenewcustratesmall = irenewcustratesmall;
	}

	public void setIrenewcustratetaxpay(DZFDouble irenewcustratetaxpay) {
		this.irenewcustratetaxpay = irenewcustratetaxpay;
	}
	
}
