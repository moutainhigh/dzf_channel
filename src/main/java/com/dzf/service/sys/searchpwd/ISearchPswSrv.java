package com.dzf.service.sys.searchpwd;

import com.dzf.model.sys.sys_power.UserVO;

public interface ISearchPswSrv {

	/**
	 * param:ucode用户编码
	 * 返回用户电话号码
	*/
	public UserVO getPhoByUcode(String ucode);
	
	/**
	 * param:UserVO
	 * 保存重置的电话号码
	*/
	public UserVO savePsw(UserVO uvo);
	
	/**
	 * param:UserVO
	 * 校验用户编码是否存在
	*/
	public UserVO UCodeIsExist(String user_code);
}
