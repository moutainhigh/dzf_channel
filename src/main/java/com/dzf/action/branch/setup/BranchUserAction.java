package com.dzf.action.branch.setup;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.branch.setup.BranchUserVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.service.branch.setup.IBranchUserService;
import com.dzf.service.pub.IPubService;

/**
 * 分支结构 用户管理
 * 
 * @author
 *
 */
@ParentPackage("basePackage")
@Namespace("/branch")
@Action(value = "/brachuser")
public class BranchUserAction extends BaseAction<UserVO> {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private IBranchUserService branchUser;

	@Autowired
	private IPubService pubser;

	public void save() {
		Json json = new Json();
		try {
			if (data != null) {
				checkUser();
				if(StringUtil.isEmpty(data.getPk_department())){
					throw new BusinessException("请选择所属机构");
				}
				if(StringUtil.isEmpty(data.getRoleids())){
					throw new BusinessException("请勾选角色");
				}
				branchUser.save(data);
				json.setSuccess(true);
				json.setMsg("保存成功");
			} else {
				json.setSuccess(false);
				json.setMsg("保存失败:数据为空。");
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	public void saveEdit() {
		Json json = new Json();
		try {
			if (data != null || StringUtil.isEmpty(data.getCuserid())) {
				checkUser();
				if(StringUtil.isEmpty(data.getPk_department())){
					throw new BusinessException("请选择所属机构");
				}
				if(StringUtil.isEmpty(data.getRoleids())){
					throw new BusinessException("请勾选角色");
				}
				branchUser.saveEdit(data);
				json.setSuccess(true);
				json.setMsg("保存成功");
			} else {
				json.setSuccess(false);
				json.setMsg("保存失败:数据为空。");
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}
	
	public void updateLock() {
		Json json = new Json();
		try {
			checkUser();
			branchUser.updateLock(data);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}


	public void query() throws Exception {
		Grid grid = new Grid();
		String loginCorp = IGlobalConstants.DefaultGroup;
		try {
			UserVO checkUser = checkUser();
			QryParamVO paramvo = new QryParamVO();
			paramvo = (QryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
			paramvo.setCuserid(checkUser.getCuserid());
			List<UserVO> list = branchUser.query(paramvo);
			if (list != null && list.size() > 0) {
				grid.setSuccess(true);
				grid.setMsg("查询成功");
				grid.setTotal((long) list.size());
				grid.setRows(list);
			} else {
				grid.setRows(new ArrayList<UserVO>());
				grid.setSuccess(false);
				grid.setMsg("查询数据为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
    /**
     * 查询用户
     */
	public void queryByID() {
		Json json = new Json();
		try {
			String qryId = getRequest().getParameter("qryId");
			BranchUserVO retvo = branchUser.queryByID(getLogin_userid(),qryId);
			json.setSuccess(true);
			json.setRows(retvo);
			json.setMsg("查询成功!");
		} catch (Exception e) {
			printErrorLog(json, log, e, "查询失败");
		}
		writeJson(json);
	}
	
	private UserVO checkUser() throws Exception{
	    UserVO uservo = getLoginUserInfo();
        if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
            throw new BusinessException("登陆用户错误");
        } else if (uservo == null) {
            throw new BusinessException("登陆用户错误");
        }
        pubser.checkFunnode(uservo, IFunNode.BRANCH_04);
        return uservo;
	}
}
