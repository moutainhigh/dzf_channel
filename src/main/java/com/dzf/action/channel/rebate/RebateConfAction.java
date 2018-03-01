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
import com.dzf.service.channel.rebate.IRebateConfService;

/**
 * 返点单确认
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/rebate")
@Action(value = "rebateconf")
public class RebateConfAction extends BaseAction<RebateVO> {

	private static final long serialVersionUID = 5761681623723616215L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IRebateConfService confser;
	
	/**
	 * 确认/驳回/取消确认
	 */
	public void updateConf() {
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
				data.setVconfirmid(getLoginUserid());
				data = confser.updateConf(data, getLogincorppk(), iopertype);
				json.setRows(data);
				json.setSuccess(true);	
				json.setMsg("保存成功");
			} catch (Exception e) {
				printErrorLog(json, log, e, "保存失败");
			}
		}else {
			json.setSuccess(false);
			json.setMsg("保存失败");
		}
		writeJson(json);
	}
}
