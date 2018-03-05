package com.dzf.action.channel.rebate;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.service.channel.rebate.IRebateAuditService;

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
	
	@Autowired
	private IRebateAuditService auditser;
	
	/**
	 * 审批/驳回
	 */
	public void updateAudit() {
		Json json = new Json();
		if (data != null) {
			try {
				UserVO uservo = getLoginUserInfo();
				if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
					throw new BusinessException("登陆用户错误");
				}else if(uservo == null){
					throw new BusinessException("登陆用户错误");
				}
				String opertype = getRequest().getParameter("opertype");
				Integer iopertype = null;
				if(opertype != null){
					iopertype = Integer.parseInt(opertype);
				}
				data.setVapproveid(getLoginUserid());
				data = auditser.updateAudit(data, getLogincorppk(), iopertype);
				json.setRows(data);
				json.setSuccess(true);	
				json.setMsg("审批成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "审批失败");
			}
		}else {
			json.setSuccess(false);
			json.setMsg("审批失败");
		}
		writeJson(json);
	}
}
