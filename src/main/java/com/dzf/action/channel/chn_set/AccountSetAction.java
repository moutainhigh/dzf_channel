package com.dzf.action.channel.chn_set;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sale.AccountSetVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.chn_set.IAccountSetService;
import com.dzf.service.pub.IPubService;

/**
 * 账务设置
 * 
 *
 */
@SuppressWarnings("serial")
@ParentPackage("basePackage")
@Namespace("/chn_set")
@Action(value = "account")
public class AccountSetAction extends BaseAction<AccountSetVO> {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IPubService pubService;
	
	@Autowired
	private IAccountSetService accountSet;

	public void queryCorpk() {
		Grid grid = new Grid();
		try {
			String pk_corp = getRequest().getParameter("corpid");
			String corpkname = getRequest().getParameter("corpkname");
			List<AccountSetVO> queryCorpk = accountSet.queryCorpk(pk_corp, corpkname);
			grid.setRows(queryCorpk);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	public void save() {
		Json json = new Json();
		try {
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_73);
			if(StringUtil.isEmpty(data.getPk_accountset())){
				data.setCoperatorid(uservo.getCuserid());
				accountSet.save(data);
			}else{
				accountSet.saveEdit(data);
			}
			json.setSuccess(true);
			json.setMsg("保存成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("保存失败");
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	public void updateStatus() {
		Json json = new Json();
		try {
			if (data == null) {
				throw new BusinessException("数据信息不能为空");
			}
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_73);
			accountSet.updateStatus(data);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}
	
	public void delete() {
		Json json = new Json();
		try {
			String ids = getRequest().getParameter("ids");
			if(StringUtil.isEmpty(ids)){
				throw new BusinessException("数据信息不能为空");
			}
			UserVO uservo = getLoginUserInfo();
			if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
				throw new BusinessException("登陆用户错误");
			}else if(uservo == null){
				throw new BusinessException("登陆用户错误");
			}
			pubService.checkFunnode(uservo, IFunNode.CHANNEL_73);
			String[] split = ids.split(",");
			accountSet.delete(split);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}

	/**
	 * 查询
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			paramvo.setUser_name(getLoginUserid());
			if (StringUtil.isEmpty(paramvo.getPk_corp())) {
				paramvo.setPk_corp(getLogincorppk());
			}
			paramvo.setBegdate(new DZFDate());
			// List<PersonStatisVO> list = personStatis.query(paramvo);
			grid.setTotal(Long.valueOf(0));
			// grid.setRows(list);
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

}
