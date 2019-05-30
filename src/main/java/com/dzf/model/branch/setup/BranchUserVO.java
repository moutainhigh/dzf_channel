package com.dzf.model.branch.setup;

import java.util.List;

import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.SuperVO;

/**
 * 机构设置主表VO
 */
@SuppressWarnings("rawtypes")
public class BranchUserVO extends SuperVO {

	private UserVO uservo;
	
	private List<ComboBoxVO> roles;
	
	private List<ComboBoxVO> branchs;
	
	

	public UserVO getUservo() {
		return uservo;
	}

	public void setUservo(UserVO uservo) {
		this.uservo = uservo;
	}

	public List<ComboBoxVO> getRoles() {
		return roles;
	}

	public void setRoles(List<ComboBoxVO> roles) {
		this.roles = roles;
	}

	public List<ComboBoxVO> getBranchs() {
		return branchs;
	}

	public void setBranchs(List<ComboBoxVO> branchs) {
		this.branchs = branchs;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

}
