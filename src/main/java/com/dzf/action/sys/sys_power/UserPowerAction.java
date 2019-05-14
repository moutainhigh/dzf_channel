package com.dzf.action.sys.sys_power;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.sys_power.URoleVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.service.channel.sys_power.IUserRoleService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.sys.sys_power.IUserService;

@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "userPower")
public class UserPowerAction extends BaseAction<URoleVO> {

	private static final long serialVersionUID = -3194454292538205899L;

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IUserRoleService userRoleServiceImpl;
	
	@Autowired
	private IPubService pubser;
	
	@Autowired
	private IUserService userServiceImpl;

	/**
	 * 
	 */
	public void queryUserRole() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			String pk_corp = getLogincorppk();
			String pk = null;
			if (data != null) {
				pk = data.getCuserid();
			}
			List<URoleVO> list = userRoleServiceImpl.queryUserRoleVO(pk, pk_corp);
			if (list != null && list.size() > 0) {
				writeJson(list);
				return;
			} else {
				grid.setTotal(0L);
				grid.setSuccess(false);
				grid.setMsg("查询失败!");
				grid.setRows(null);
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败!");
		}
		writeJson(grid);
	}

	public void saveUserRoleVO() {
		Grid grid = new Grid();
		grid.setSuccess(false);
		grid.setMsg("数据为空，保存失败！");
		try {
			UserVO uservo = getLoginUserInfo();
			if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
				throw new BusinessException("登陆用户错误");
			} else if (uservo == null) {
				throw new BusinessException("登陆用户错误");
			}
			pubser.checkFunnode(uservo, IFunNode.CHANNEL_21);
			String pk_corp = getLogincorppk();
			URoleVO[] vos = null;
			if (data != null) {
				String userid = data.getCuserid();
				String[] roleids = data.getCkaccarr();
				if (!StringUtil.isEmpty(userid)) {
					vos = buildRoleVOs(roleids, userid);
					userRoleServiceImpl.saveUserRoleVO(vos, userid, pk_corp);
					grid.setSuccess(true);
					grid.setMsg("保存成功！");
					UserVO uvo = userServiceImpl.queryUserJmVOByID(userid);
					StringBuffer msg = new StringBuffer("分配权限");
					if (uvo != null) {
						msg.append(":").append(uvo.getUser_code()).append(" ").append(uvo.getUser_name());
					}
					writeLogRecord(LogRecordEnum.OPE_CHANNEL_QXFP.getValue(), msg.toString(), ISysConstants.SYS_3);
				}
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "保存失败!");
		}
		writeJson(grid);
	}

	private URoleVO[] buildRoleVOs(String[] roleids, String cuserid) {
		if (StringUtil.isEmpty(cuserid) || roleids == null || roleids.length == 0)
			return null;
		List<URoleVO> va = new ArrayList<URoleVO>();
		for (String roleid : roleids) {
			if (!StringUtil.isEmpty(roleid) && roleid.length() == 24) {
				URoleVO vo = new URoleVO();
				vo.setPk_corp(getLogincorppk());
				vo.setPk_role(roleid);
				vo.setCuserid(cuserid);
				va.add(vo);
			}
		}
		return va.toArray(new URoleVO[0]);
	}
}