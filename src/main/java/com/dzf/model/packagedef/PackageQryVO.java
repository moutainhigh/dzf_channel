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
	
	public DZFDate getDbegindate() {
        return dbegindate;
    }

    public void setDbegindate(DZFDate dbegindate) {
        this.dbegindate = dbegindate;
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
