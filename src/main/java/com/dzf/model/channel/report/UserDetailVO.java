package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;

/**
 * 
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class UserDetailVO extends SuperVO {
	
    private String deptname;//部门名称
	
	@FieldAlias("uid")
	public String userid; // 用户主键
    
    @FieldAlias("uname")
    public String user_name; // 用户名称
    
    private String rolename;//角色名称
    
    private Integer corpnum;//客户数

	public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public Integer getCorpnum() {
        return corpnum;
    }

    public void setCorpnum(Integer corpnum) {
        this.corpnum = corpnum;
    }

    public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}


	public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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
