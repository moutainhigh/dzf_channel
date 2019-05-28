package com.dzf.model.channel.branch;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

import lombok.Data;

/**
 * 机构设置主表VO
 */
@SuppressWarnings("rawtypes")
@Data
public class BranchInstSetupVO extends SuperVO {

	@FieldAlias("pk_bset")
	private String pk_branchset;

	@FieldAlias("corpid")
	private String pk_corp;// 公司id

	@FieldAlias("name")
	private String vname;// 机构名称

	@FieldAlias("oper")
	private String coperatorid; // 录入人

	@FieldAlias("opdate")
	private DZFDate doperatedate; // 录入日期

	@FieldAlias("dr")
	private Integer dr; // 删除标记

	@FieldAlias("ts")
	private DZFDateTime ts; // 时间戳
	
	

	@Override
	public String getPKFieldName() {
		return "pk_branchset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "br_branchset";
	}

}
