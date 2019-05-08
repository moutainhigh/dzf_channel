package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 服务套餐定义VO
 * @author 
 *
 */
@SuppressWarnings("rawtypes")
public class PackageDefVO extends SuperVO {

	private static final long serialVersionUID = 8799494318014396487L;
	
    @FieldAlias("pid")
    private String pk_packagedef; // 主键
	
    @FieldAlias("corpid")
    private String pk_corp; //
    
    private String pk_product;
    
    @FieldAlias("typemin")
    private String pk_busitype;// 业务小类
    
    @FieldAlias("typename")
    private String vbusitypename;// 业务小类名称
    
    @FieldAlias("typecode")
    private String vbusitypecode;//业务小类编码
   
    @FieldAlias("taxtype")
    public String vtaxpayertype;// 纳税人资格
    
    @FieldAlias("nmsmny")
    private DZFDouble nmonthmny; // 月服务费
    
    @FieldAlias("cylnum")
    private Integer icashcycle; // 收费周期数
    
    @FieldAlias("contcycle")
    private Integer icontcycle;//合同周期
    
    @FieldAlias("pubnum")
    private Integer ipublishnum;//发布个数
    
    private Integer iusenum;//已抢个数
    
    @FieldAlias("dpubdate")
    private DZFDate dpublishdate;//发布日期
    
    @FieldAlias("offdate")
    private DZFDate doffdate;//下架日期
    
    @FieldAlias("vstatus")
    private Integer vstatus;//状态  1：未发布；2：已发布；3：已下架；
    
    @FieldAlias("ispro")
    private DZFBoolean ispromotion;//是否促销
    
    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳
    
    @FieldAlias("memo")
    private String vmemo; // 备注
    
    private String coperatorid;
    
    private DZFDate doperatedate;
    
    private String coperatorname;
    
    @FieldAlias("comptype")
    private Integer icompanytype;// 公司类型 20-个体工商户，99-非个体户；
    
    private Integer itype;//服务套餐类型 null或者0为常规；1：非常规
    
    private Integer sortnum;//排序
    
    private String corpids;//适用加盟商id总合
    
    private String cityids;//适用地区id总合
    
    @FieldAlias("corpnms")
    private String corpnames;
    
    @FieldAlias("citynms")
    private String citynames;

	public Integer getIcompanytype() {
        return icompanytype;
    }

    public void setIcompanytype(Integer icompanytype) {
        this.icompanytype = icompanytype;
    }

    public String getVbusitypecode() {
        return vbusitypecode;
    }

    public void setVbusitypecode(String vbusitypecode) {
        this.vbusitypecode = vbusitypecode;
    }

    public String getCoperatorname() {
        return coperatorname;
    }

	public String getCorpids() {
		return corpids;
	}

	public void setCorpids(String corpids) {
		this.corpids = corpids;
	}

	public String getCityids() {
		return cityids;
	}

	public void setCityids(String cityids) {
		this.cityids = cityids;
	}

	public String getCorpnames() {
		return corpnames;
	}

	public void setCorpnames(String corpnames) {
		this.corpnames = corpnames;
	}

	public String getCitynames() {
		return citynames;
	}

	public void setCitynames(String citynames) {
		this.citynames = citynames;
	}

	public Integer getItype() {
		return itype;
	}

	public void setItype(Integer itype) {
		this.itype = itype;
	}

	public Integer getSortnum() {
		return sortnum;
	}

	public void setSortnum(Integer sortnum) {
		this.sortnum = sortnum;
	}

	public void setCoperatorname(String coperatorname) {
        this.coperatorname = coperatorname;
    }

    public Integer getIusenum() {
        return iusenum;
    }

    public void setIusenum(Integer iusenum) {
        this.iusenum = iusenum;
    }

    public String getPk_packagedef() {
        return pk_packagedef;
    }

    public void setPk_packagedef(String pk_packagedef) {
        this.pk_packagedef = pk_packagedef;
    }

    public String getPk_product() {
        return pk_product;
    }

    public void setPk_product(String pk_product) {
        this.pk_product = pk_product;
    }

    public String getPk_busitype() {
        return pk_busitype;
    }

    public void setPk_busitype(String pk_busitype) {
        this.pk_busitype = pk_busitype;
    }

    public String getVtaxpayertype() {
        return vtaxpayertype;
    }

    public void setVtaxpayertype(String vtaxpayertype) {
        this.vtaxpayertype = vtaxpayertype;
    }

    public DZFDouble getNmonthmny() {
        return nmonthmny;
    }

    public void setNmonthmny(DZFDouble nmonthmny) {
        this.nmonthmny = nmonthmny;
    }

    public Integer getIcashcycle() {
        return icashcycle;
    }

    public void setIcashcycle(Integer icashcycle) {
        this.icashcycle = icashcycle;
    }

    public Integer getIcontcycle() {
        return icontcycle;
    }

    public void setIcontcycle(Integer icontcycle) {
        this.icontcycle = icontcycle;
    }

    public Integer getIpublishnum() {
        return ipublishnum;
    }

    public void setIpublishnum(Integer ipublishnum) {
        this.ipublishnum = ipublishnum;
    }

    public DZFDate getDpublishdate() {
        return dpublishdate;
    }

    public void setDpublishdate(DZFDate dpublishdate) {
        this.dpublishdate = dpublishdate;
    }

    public DZFDate getDoffdate() {
        return doffdate;
    }

    public void setDoffdate(DZFDate doffdate) {
        this.doffdate = doffdate;
    }

    public Integer getVstatus() {
        return vstatus;
    }

    public void setVstatus(Integer vstatus) {
        this.vstatus = vstatus;
    }

    public DZFBoolean getIspromotion() {
        return ispromotion;
    }

    public void setIspromotion(DZFBoolean ispromotion) {
        this.ispromotion = ispromotion;
    }

    public String getVmemo() {
        return vmemo;
    }

    public void setVmemo(String vmemo) {
        this.vmemo = vmemo;
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


	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}


	public String getVbusitypename() {
		return vbusitypename;
	}

	public void setVbusitypename(String vbusitypename) {
		this.vbusitypename = vbusitypename;
	}

	@Override
	public String getPKFieldName() {
		return "pk_packagedef";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_packagedef";
	}

}
