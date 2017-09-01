package com.dzf.model.pub;

import com.dzf.dao.jdbc.framework.SQLParameter;

public class QrySqlSpmVO {

	private String sql;
	
	private SQLParameter spm;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public SQLParameter getSpm() {
		return spm;
	}

	public void setSpm(SQLParameter spm) {
		this.spm = spm;
	}
}
