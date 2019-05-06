package com.dzf.model.packagedef;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;

/**
 * 服务套餐定义VO
 * @author 
 *
 */
@SuppressWarnings("rawtypes")
public class PackageQryVO extends SuperVO {

	private static final long serialVersionUID = 8799494318014396487L;
    
    @FieldAlias("begdate")
    private DZFDate dbegindate;//发布日期
    
    @FieldAlias("enddate")
    private DZFDate denddate;//发布日期
    
    @FieldAlias("taxtype")
    public String vtaxpayertype;// 纳税人资格
    
    @FieldAlias("vstatus")
    private Integer vstatus;//状态  1：未发布；2：已发布；3：已下架；4:已保存+已发布
    
    @FieldAlias("cylnum")
    private Integer icashcycle; // 收费周期数
    
    @FieldAlias("contcycle")
    private Integer icontcycle;//合同周期
    
    private Integer itype;//服务套餐类型  1为常规；2：非常规   12查询全部
    
    private Integer ptype;//是否促销 1促销；2：非促销  12查询全部
	
    @FieldAlias("comptype")
    private Integer icompanytype;// 公司类型 20-个体工商，99-非个体工商； -1全部
    
	public DZFDate getDbegindate() {
        return dbegindate;
    }

    public void setDbegindate(DZFDate dbegindate) {
        this.dbegindate = dbegindate;
    }

    public String getVtaxpayertype() {
		return vtaxpayertype;
	}

	public void setVtaxpayertype(String vtaxpayertype) {
		this.vtaxpayertype = vtaxpayertype;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public Integer getIcompanytype() {
		return icompanytype;
	}

	public void setIcompanytype(Integer icompanytype) {
		this.icompanytype = icompanytype;
	}

	public Integer getPtype() {
		return ptype;
	}

	public void setPtype(Integer ptype) {
		this.ptype = ptype;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
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

	public Integer getItype() {
		return itype;
	}

	public void setItype(Integer itype) {
		this.itype = itype;
	}

	public DZFDate getDenddate() {
        return denddate;
    }

    public void setDenddate(DZFDate denddate) {
        this.denddate = denddate;
    }

    @Override
	public String getPKFieldName() {
		return "";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "";
	}

}
