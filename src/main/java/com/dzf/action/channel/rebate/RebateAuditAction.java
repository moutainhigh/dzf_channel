package com.dzf.action.channel.rebate;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.rebate.RebateVO;

/**
 * 返点单审批
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/rebate")
@Action(value = "rebateaudit")
public class RebateAuditAction extends BaseAction<RebateVO> {

	private static final long serialVersionUID = -4800266979617985267L;

	private Logger log = Logger.getLogger(this.getClass());
	
}
