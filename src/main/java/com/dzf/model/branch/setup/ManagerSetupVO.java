package com.dzf.model.branch.setup;

import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.Field.FieldAlias;

/**
 * 分部管理 - 管理员账号设置
 * @author zy
 *
 */
public class ManagerSetupVO extends UserVO {

	private static final long serialVersionUID = 1L;
    
	@FieldAlias("braname")
	private String vbraname;// 机构机构名称

	public String getVbraname() {
		return vbraname;
	}

	public void setVbraname(String vbraname) {
		this.vbraname = vbraname;
	}
	
}
