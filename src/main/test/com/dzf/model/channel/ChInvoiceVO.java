package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

@SuppressWarnings("rawtypes")
public class ChInvoiceVO extends SuperVO{
	
	private static final long serialVersionUID = -6057771983096487432L;

	@FieldAlias("id")
	private String pk_invoice;
	
	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("nature")
	private Integer invnature;//发票性质  0：公司 1：个人
	
	@FieldAlias("cname")
	private String corpname;//单位名称
	
	private String taxnum;//税号
	
	@FieldAlias("iprice")
	private DZFDouble invprice;//开票金额
	
	@FieldAlias("itype")
	private Integer invtype;//发票类型  0: 专用发票、 1:普通发票 、2: 电子普通发票

	@FieldAlias("caddr")
	private String corpaddr;//公司地址
	
	@FieldAlias("phone")
	private String invphone;//开票电话
	
	@FieldAlias("bname")
	private String bankname;//开户行
	
	@FieldAlias("bcode")
	private String bankcode;//开户账户
	
	private String email;//邮箱
	
	private String apptime;//申请时间
	
	@FieldAlias("istatus")
	private Integer invstatus;//发票状态  0：待提交 ；1：待开票；2：已开票；3：开票失败；9：已换票；
	
	private String invtime;//开票日期
	
	private String invperson;//开票人主键
	
	private String iperson;//开票人（不存库）
	
	private Integer dr;
	
	private DZFDateTime ts;
	
	@FieldAlias("paytype")
	private Integer ipaytype;//付款类型  0：预付款   1：加盟费
	
	@FieldAlias("tprice")
	private String nticketmny;//可开票金额
	
	private String vmome;//备注
	
	private Integer invcorp;//1:加盟商发起；2:大账房发起
	
	private String msg;
	
	private String invcode;//发票号
	
	private String errcode;//错误编号
	
	private String reqserialno;//发票请求流水号
	
	private String qrcodepath;//二维码url
	
	@FieldAlias("runame")
	private String rusername;//收票人
	
	private Integer billway;//开票方式：1-电子开票；2-纸质票
	
	@FieldAlias("dcdate")
	private DZFDate dchangedate;//换票日期
	
	@FieldAlias("vcmemo")
	private String vchangememo;//换票说明
	
	@FieldAlias("isourtype")
	private Integer isourcetype;//发票来源类型  1：合同扣款开票； 2：商品扣款开票；
	
	@FieldAlias("sourceid")
	private String pk_source;//来源主键（商品订单主键）
	
	@FieldAlias("billcode")
	private String vbillcode;//订单编号
	
	@FieldAlias("ndesummny")
	private DZFDouble ndedsummny;// 扣款总金额（订单金额）

	@FieldAlias("nderebmny")
	private DZFDouble ndedrebamny;// 扣款金额-返点款（返点金额）
	
	@FieldAlias("ndemny")
	private DZFDouble ndeductmny;// 扣款金额-预付款（预付款金额）
	
	@FieldAlias("datatype")
	private Integer idatatype;//数据类型（1：商品扣款全扣预付款；2：商品扣款扣预付款和返点）
	
	/******以下字段不存库*********/
    private String bdate;//查询开始时间
    
    private String edate;//查询结束时间
    
	private String tempprice;//(用于计算的临时金钱)
	   
	private String corpcode;//公司编码
	    
	private String[] corps;//渠道商ids
	    
	private String[] pk_invoices;//发票ids
	
	private Integer qrytype;//1:申请日期；2：开票日期
	
	@FieldAlias("aname")
    private String areaname;//大区名称

    @FieldAlias("provname")
	public String vprovname;//省市名称

	@FieldAlias("ovince")
	public Integer vprovince;// 省市
    
    public Integer getIdatatype() {
		return idatatype;
	}

	public void setIdatatype(Integer idatatype) {
		this.idatatype = idatatype;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public DZFDouble getNdedsummny() {
		return ndedsummny;
	}

	public void setNdedsummny(DZFDouble ndedsummny) {
		this.ndedsummny = ndedsummny;
	}

	public DZFDouble getNdedrebamny() {
		return ndedrebamny;
	}

	public void setNdedrebamny(DZFDouble ndedrebamny) {
		this.ndedrebamny = ndedrebamny;
	}

	public DZFDouble getNdeductmny() {
		return ndeductmny;
	}

	public void setNdeductmny(DZFDouble ndeductmny) {
		this.ndeductmny = ndeductmny;
	}

	public String getPk_source() {
		return pk_source;
	}

	public void setPk_source(String pk_source) {
		this.pk_source = pk_source;
	}

	public Integer getIsourcetype() {
		return isourcetype;
	}

	public void setIsourcetype(Integer isourcetype) {
		this.isourcetype = isourcetype;
	}

	public DZFDate getDchangedate() {
        return dchangedate;
    }

    public void setDchangedate(DZFDate dchangedate) {
        this.dchangedate = dchangedate;
    }

    public String getVchangememo() {
        return vchangememo;
    }

    public void setVchangememo(String vchangememo) {
        this.vchangememo = vchangememo;
    }

    public Integer getQrytype() {
        return qrytype;
    }

    public void setQrytype(Integer qrytype) {
        this.qrytype = qrytype;
    }

    public Integer getBillway() {
        return billway;
    }

    public void setBillway(Integer billway) {
        this.billway = billway;
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

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public String getRusername() {
        return rusername;
    }

    public void setRusername(String rusername) {
        this.rusername = rusername;
    }

    public String getInvcode() {
        return invcode;
    }

    public void setInvcode(String invcode) {
        this.invcode = invcode;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getReqserialno() {
        return reqserialno;
    }

    public void setReqserialno(String reqserialno) {
        this.reqserialno = reqserialno;
    }

    public String getQrcodepath() {
        return qrcodepath;
    }

    public void setQrcodepath(String qrcodepath) {
        this.qrcodepath = qrcodepath;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getInvcorp() {
        return invcorp;
    }

    public void setInvcorp(Integer invcorp) {
        this.invcorp = invcorp;
    }

    public String[] getPk_invoices() {
		return pk_invoices;
	}

	public void setPk_invoices(String[] pk_invoices) {
		this.pk_invoices = pk_invoices;
	}

	public String[] getCorps() {
		return corps;
	}

	public void setCorps(String[] corps) {
		this.corps = corps;
	}

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}

	public String getIperson() {
		return iperson;
	}

	public void setIperson(String iperson) {
		this.iperson = iperson;
	}

	public String getTempprice() {
        return tempprice;
    }

    public void setTempprice(String tempprice) {
        this.tempprice = tempprice;
    }

    public String getVmome() {
		return vmome;
	}

	public void setVmome(String vmome) {
		this.vmome = vmome;
	}
	
	public Integer getIpaytype() {
		return ipaytype;
	}

	public void setIpaytype(Integer ipaytype) {
		this.ipaytype = ipaytype;
	}

	public String getNticketmny() {
		return nticketmny;
	}

	public void setNticketmny(String nticketmny) {
		this.nticketmny = nticketmny;
	}

	public String getBdate() {
		return bdate;
	}

	public void setBdate(String bdate) {
		this.bdate = bdate;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}

	public String getPk_invoice() {
		return pk_invoice;
	}

	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getInvnature() {
		return invnature;
	}

	public void setInvnature(Integer invnature) {
		this.invnature = invnature;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getTaxnum() {
		return taxnum;
	}

	public void setTaxnum(String taxnum) {
		this.taxnum = taxnum;
	}

	public DZFDouble getInvprice() {
		return invprice;
	}

	public void setInvprice(DZFDouble invprice) {
		this.invprice = invprice;
	}

	public Integer getInvtype() {
		return invtype;
	}

	public void setInvtype(Integer invtype) {
		this.invtype = invtype;
	}

	public String getCorpaddr() {
		return corpaddr;
	}

	public void setCorpaddr(String corpaddr) {
		this.corpaddr = corpaddr;
	}

	public String getInvphone() {
		return invphone;
	}

	public void setInvphone(String invphone) {
		this.invphone = invphone;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getApptime() {
		return apptime;
	}

	public void setApptime(String apptime) {
		this.apptime = apptime;
	}

	public Integer getInvstatus() {
		return invstatus;
	}

	public void setInvstatus(Integer invstatus) {
		this.invstatus = invstatus;
	}

	public String getInvtime() {
		return invtime;
	}

	public void setInvtime(String invtime) {
		this.invtime = invtime;
	}

	public String getInvperson() {
		return invperson;
	}

	public void setInvperson(String invperson) {
		this.invperson = invperson;
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
		return "pk_invoice";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_invoice";
	}
	
}
