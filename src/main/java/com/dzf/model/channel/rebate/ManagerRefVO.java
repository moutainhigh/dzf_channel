package com.dzf.model.channel.rebate;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;

/**
 * 渠道经理参照vo
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ManagerRefVO extends SuperVO {
	
	private static final long serialVersionUID = 938377569554734037L;

	@FieldAlias("uid")
	private String cuserid;
	
	@FieldAlias("ucode")
	private String usercode;
	
	@FieldAlias("uname")
	private String username;
	
	public String getCuserid() {
		return cuserid;
	}

	public String getUsercode() {
		return usercode;
	}

	public String getUsername() {
		return username;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public void setUsername(String username) {
		this.username = username;
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
