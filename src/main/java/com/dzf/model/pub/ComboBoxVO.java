package com.dzf.model.pub;

import com.dzf.pub.SuperVO;

@SuppressWarnings({ "rawtypes", "serial" })
public class ComboBoxVO extends SuperVO{

	private String id;
	
	private String name;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
