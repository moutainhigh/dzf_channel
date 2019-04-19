package com.dzf.action.channel.matmanage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.service.channel.matmanage.IMatHandleService;

/**
 * 物料档案
 */
@ParentPackage("basePackage")
@Namespace("/matmanage")
@Action(value = "mathandle")
public class MatHandleAction extends BaseAction<MatOrderVO> {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private IMatHandleService mathandle;
	
	/**
	 * 查询快递公司下拉
	 */
	public void queryComboBox() {
		Grid grid = new Grid();
		try {
			UserVO uservo = getLoginUserInfo();
			checkUser(uservo);
			List<MatOrderVO> list = mathandle.queryComboBox();
			if (list == null || list.size() == 0) {
				grid.setRows(new ArrayList<MatOrderVO>());
				grid.setSuccess(true);
				grid.setMsg("查询数据为空");
			} else {
				grid.setRows(list);
				grid.setSuccess(true);
				grid.setMsg("查询成功");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 登录用户校验
	 * @throws DZFWarpException
	 */
	private void checkUser(UserVO uservo) throws DZFWarpException {
		if(uservo != null && !"000001".equals(uservo.getPk_corp()) ){
			throw new BusinessException("登陆用户错误！");
		}else if(uservo == null){
			throw new BusinessException("请先登录！");
		}
	}
}
