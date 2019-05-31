package com.dzf.action.branch.setup;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.branch.setup.ManagerSetupVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.DzfTypeUtils;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.util.QueryUtil;
import com.dzf.service.branch.setup.IManagerSetupService;
import com.dzf.service.pub.IPubService;

/**
 * 管理员账号设置
 * 
 * @author zy
 *
 */
@ParentPackage("basePackage")
@Namespace("/branch")
@Action(value = "managersetup")
public class ManagerSetupAction extends BaseAction<ManagerSetupVO> {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());

	private static final String UTYPE = "8";// 分部管理系统登录用户

	@Autowired
	private IManagerSetupService mansetupser;
	
	@Autowired
	private IPubService pubser;

	/**
	 * 查询
	 * 
	 * @throws Exception
	 */
	public void query() {
		Grid grid = new Grid();
		try {
			checkUser();
			ManagerSetupVO pamvo = getPamVO();
			if (StringUtil.isEmpty(pamvo.getUser_code())) {
				long total = mansetupser.queryTotalRow(pamvo);
				if (total > 0) {
					List<ManagerSetupVO> list = mansetupser.query(pamvo);
					grid.setRows(list);
				}
				grid.setTotal(total);
			} else {
				List<ManagerSetupVO> list = mansetupser.queryAll(pamvo);
				if (list != null && list.size() > 0) {
					ManagerSetupVO[] mVOs = list.toArray(new ManagerSetupVO[0]);
					mVOs = (ManagerSetupVO[]) QueryUtil.getPagedVOs(mVOs, pamvo.getPage(), pamvo.getRows());
					grid.setRows(Arrays.asList(mVOs));
				}
				grid.setTotal((long) list.size());
			}
			grid.setSuccess(true);
			grid.setMsg("查询成功");
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}

	/**
	 * 获取查询条件
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	private ManagerSetupVO getPamVO() throws DZFWarpException {
		ManagerSetupVO pamvo = new ManagerSetupVO();
		pamvo = (ManagerSetupVO) DzfTypeUtils.cast(getRequest(), new ManagerSetupVO());
		if (StringUtil.isEmptyWithTrim(pamvo.getPk_corp())) {
			pamvo.setPk_corp(getLogincorppk());
		}
		return pamvo;
	}

	/**
	 * 保存
	 */
	public void save() {
		Json json = new Json();
		try {
			if (data != null) {
				UserVO uservo = getLoginUserInfo();
				checkUser();
				pubser.checkFunnode(uservo, IFunNode.BRANCH_03);
				if (StringUtil.isEmpty(data.getCuserid())) {
					setDefaultValue(data);
				}
				String[] str = new String[] { "user_name" };
				QueryDeCodeUtils.decKeyUtil(str, data, 0);
				mansetupser.save(data, getLogincorppk());

				QueryDeCodeUtils.decKeyUtil(str, data, 1);
				json.setSuccess(true);
				json.setRows(data);
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

	/**
	 * 设置默认值
	 * 
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(ManagerSetupVO data) throws DZFWarpException {
		String pk_corp = getLogincorppk();
		data.setPk_creatcorp(pk_corp);// 创建用户的公司
		data.setPk_corp(pk_corp);
		data.setXsstyle(UTYPE);
		data.setIsmanager(DZFBoolean.TRUE);
		if (data.getPk_corp() == null) {
			throw new BusinessException("所属公司不能为空");
		}
	}

	/**
	 * 查询分部机构
	 */
	public void queryBranch() {
		Grid grid = new Grid();
		try {
			checkUser();
			List<BranchInstSetupVO> list = mansetupser.queryBranch();
			grid.setRows(list);
			grid.setSuccess(true);
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	/**
	 * 通过主键查询数据
	 */
	public void queryById() {
		Json json = new Json();
		try {
			ManagerSetupVO pamvo = getPamVO();
			checkUser();
			ManagerSetupVO mvo = mansetupser.queryById(pamvo);
			json.setRows(mvo);
			json.setSuccess(true);
			json.setMsg("操作成功");
		} catch (Exception e) {
			printErrorLog(json, log, e, "操作失败");
		}
		writeJson(json);
	}

	/**
	 * 登录用户验证
	 * 
	 * @throws Exception
	 */
	private void checkUser() throws Exception {
		UserVO uservo = getLoginUserInfo();
		if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
			throw new BusinessException("登陆用户错误");
		} else if (uservo == null) {
			throw new BusinessException("登陆用户错误");
		}
	}

}
