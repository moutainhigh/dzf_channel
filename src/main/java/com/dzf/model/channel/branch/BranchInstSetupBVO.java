package com.dzf.model.channel.branch;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

import lombok.Data;

/**
 * 机构设置子表VO
 */
@SuppressWarnings("rawtypes")
@Data
public class BranchInstSetupBVO extends SuperVO {
	
	 
	@FieldAlias("pk_bcorp")
	private String pk_branchcorp;
	
	@FieldAlias("pk_bset")
	private String pk_branchset;//主表主键

	@FieldAlias("corpid")
	private String pk_corp;// 公司id

	@FieldAlias("name")
	private String vname;// 企业识别码

	@FieldAlias("lman")
	private String linkman; //联系人
	
	private String phone;//联系方式
	
	private String isseal; //是否封存 
	
	@FieldAlias("memo")
	private String vmemo; //备注
	
	@FieldAlias("opid")
	private String coperatorid; // 录入人

	@FieldAlias("opdate")
	private DZFDate doperatedate; // 录入日期

	private Integer dr; // 删除标记

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
