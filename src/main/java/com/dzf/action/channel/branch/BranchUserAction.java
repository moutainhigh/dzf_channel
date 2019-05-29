package com.dzf.action.channel.branch;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.SysPowerConditVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.constant.IFunNode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.sys_power.IChnUserService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.sys.sys_power.IUserService;

/**
 * 分支结构 用户管理
 * 
 * @author
 *
 */
@ParentPackage("basePackage")
@Namespace("/branch")
@Action(value = "/user")
public class BranchUserAction extends BaseAction<UserVO> {

	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());

	private static final String UTYPE = "6";// 加盟商系统登录用户

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IChnUserService chnUserService;

	@Autowired
	private IPubService pubser;

	public void save() {
		Json json = new Json();
		try {
			if (data != null) {
				UserVO uservo = getLoginUserInfo();
				checkUser();
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_14);
				String loginCorp = getLoginCorpInfo().getPk_corp();
				data.setPk_creatcorp(loginCorp);// 创建用户的公司
				data.setPk_corp(loginCorp);
				data.setXsstyle(UTYPE);
				if (data.getPk_corp() == null)
					throw new BusinessException("所属公司不能为空");
				UserVO[] datas = (UserVO[]) QueryDeCodeUtils
						.decKeyUtils(new String[] { "user_name" }, new UserVO[] { data }, 0);
				userService.save(datas[0]);
				UserVO[] decdatas = (UserVO[]) QueryDeCodeUtils
						.decKeyUtils(new String[] { "user_name" }, new UserVO[] { datas[0] }, 1);
				json.setSuccess(true);
				json.setRows(decdatas[0]);
				json.setMsg("保存成功");
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_YHGL.getValue(),
						"新增用户：" + data.getUser_code() + " " + data.getUser_name(), ISysConstants.SYS_3);
			} else {
				json.setSuccess(false);
				json.setMsg("保存失败:数据为空。");
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "保存失败");
		}
		writeJson(json);
	}

	public void update() {
		Json json = new Json();
		try {
			if (data != null) {
				UserVO uservo = getLoginUserInfo();
				checkUser();
				pubser.checkFunnode(uservo, IFunNode.CHANNEL_14);
				String loginCorp = getLoginCorpInfo().getPk_corp();
				UserVO user = userService.queryUserById(data.getCuserid());
				if (user == null) {
					throw new BusinessException("该数据不存在或已删除！");
				}
				if (!loginCorp.equals(user.getPk_corp())) {
					throw new BusinessException("修改的用户不属于当前登陆公司，不允许修改。");
				}
				UserVO[] datas = (UserVO[]) QueryDeCodeUtils
						.decKeyUtils(new String[] { "user_name"}, new UserVO[] { data }, 0);
				userService.update(datas[0]);
				UserVO[] decdatas = (UserVO[]) QueryDeCodeUtils
						.decKeyUtils(new String[] { "user_name"}, new UserVO[] { datas[0] }, 1);
				json.setSuccess(true);
				json.setRows(decdatas[0]);
				json.setMsg("更新成功");
				writeLogRecord(LogRecordEnum.OPE_CHANNEL_YHGL.getValue(),
						"修改用户：" + data.getUser_code() + " " + data.getUser_name(), ISysConstants.SYS_3);
			} else {
				json.setSuccess(false);
				json.setMsg("更新失败:数据为空。");
			}
		} catch (Exception e) {
			printErrorLog(json, log, e, "更新失败");
		}
		writeJson(json);
	}

	public void query() throws Exception {
		Grid grid = new Grid();
		String loginCorp = IGlobalConstants.DefaultGroup;
		try {
			checkUser();
			SysPowerConditVO qryVO = new SysPowerConditVO();
			String ilock = getRequest().getParameter("ilock");
			String invalid = getRequest().getParameter("invalid");// 失效
			qryVO.setCrtcorp_id(loginCorp);
			qryVO.setUtype(UTYPE);
			qryVO.setIlock(ilock);
			List<UserVO> list = userService.query(loginCorp, qryVO);
			List<UserVO> alist = new ArrayList<>();
			if (invalid != null && invalid.equals("N")) {// 查询没有失效的用户
				for (UserVO userVO : list) {
					if (userVO.getDisable_time() == null || new DZFDate().before(userVO.getDisable_time())) {
						alist.add(userVO);
					}
				}
			} else {
				alist = list;
			}
			UserVO[] array = alist.toArray(new UserVO[0]);
			array = (UserVO[]) QueryDeCodeUtils.decKeyUtils(new String[] { "user_name" }, array, 1);
			if (list != null && list.size() > 0) {
				grid.setSuccess(true);
				grid.setMsg("查询成功");
				grid.setTotal((long) alist.size());
				grid.setRows(alist);
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
	
	public void queryCombobox() throws Exception {
        Grid grid = new Grid();
        String loginCorp = IGlobalConstants.DefaultGroup;
        try {
            checkUser();
            List<ComboBoxVO> list = chnUserService.queryCombobox(loginCorp);
            ComboBoxVO[] array = list.toArray(new ComboBoxVO[0]);
            array = (ComboBoxVO[]) QueryDeCodeUtils.decKeyUtils(new String[] { "name" }, array, 1);
            if (list != null && list.size() > 0) {
                grid.setSuccess(true);
                grid.setMsg("查询成功");
                grid.setTotal((long) list.size());
                grid.setRows(list);
            } else {
                grid.setRows(new ArrayList<ComboBoxVO>());
                grid.setSuccess(true);
                grid.setMsg("查询数据为空");
            }
        } catch (Exception e) {
            printErrorLog(grid, log, e, "查询失败");
        }
        writeJson(grid);
	}
	
	private void checkUser() throws Exception{
	    UserVO uservo = getLoginUserInfo();
        if (uservo != null && !"000001".equals(uservo.getPk_corp())) {
            throw new BusinessException("登陆用户错误");
        } else if (uservo == null) {
            throw new BusinessException("登陆用户错误");
        }
	}
}
