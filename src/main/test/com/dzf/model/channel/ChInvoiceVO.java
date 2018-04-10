package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
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
	private Integer invstatus;//发票状态  0：待提交 、1：待开票、2：已开票、3：开票失败
	
	private String invtime;//开票日期
	
	private String invperson;//开票人主键
	
	private String iperson;//开票人
	
	private Integer dr;
	
	private DZFDateTime ts;
	
	private String bdate;//查询开始时间
	
	private String edate;//查询结束时间
	
	@FieldAlias("paytype")
	private Integer ipaytype;//付款类型  0：预付款   1：加盟费
	
	@FieldAlias("tprice")
	private String nticketmny;//可开票金额
	
	private String vmome;//备注
	
	private String tempprice;//(用于计算的临时金钱)
	
	private String corpcode;//公司编码
	
	private String[] corps;//渠道商ids
	
	private String[] pk_invoices;//发票ids
	
	private Integer invcorp;//1:加盟商发起；2:大账房发起
	
	private String msg;
	
	private String invcode;//发票号
	
	private String errcode;//错误编号
	
	private String reqserialno;//发票请求流水号
	
	private String qrcodepath;//二维码url
	
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
