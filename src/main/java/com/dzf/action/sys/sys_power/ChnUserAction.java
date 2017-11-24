package com.dzf.action.sys.sys_power;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import com.dzf.action.pub.BaseAction;
import com.dzf.model.pub.Grid;
import com.dzf.model.pub.Json;
import com.dzf.model.sys.sys_power.SysPowerConditVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.service.pub.LogRecordEnum;
import com.dzf.service.sys.sys_power.IUserService;

/**
 * 加盟商用户管理
 * @author 
 *
 */
@ParentPackage("basePackage")
@Namespace("/sys")
@Action(value = "/chnUseract")
public class ChnUserAction extends BaseAction<UserVO>{
	
	private static final long serialVersionUID = 1L;

	private Logger log = Logger.getLogger(this.getClass());
	
	private static final String UTYPE = "6";//加盟商系统登录用户
	
	private IUserService userService = null;
	

	public IUserService getUserService() {
		return userService;
	}
	@Autowired
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
	
	public void save(){
		Json json = new Json();
		if (data != null) {
			try{
				//FieldValidateUtils.Validate(data);
				UserVO vo = ((UserVO)data);
				String loginCorp = getLoginCorpInfo().getPk_corp();
				
				vo.setPk_creatcorp(loginCorp);//创建用户的公司
				vo.setPk_corp(loginCorp);
				if(vo.getPk_corp() == null)
					throw new BusinessException("所属公司不能为空");
				
				data.setXsstyle(UTYPE);
				UserVO[] datas = (UserVO[]) QueryDeCodeUtils.decKeyUtils(new String[]{"user_name","corpnm","crtcorp"}, new UserVO[]{data}, 0);
				userService.save(datas[0]);
				UserVO[] decdatas = (UserVO[]) QueryDeCodeUtils.decKeyUtils(new String[]{"user_name","corpnm","crtcorp"}, new UserVO[]{datas[0]}, 1);
				
				json.setSuccess(true);
				json.setRows(decdatas[0]);
				json.setMsg("保存成功");
			}catch(Exception e){
				printErrorLog(json, log, e, "保存失败");
			}
		}else{
			json.setSuccess(false);
			json.setMsg("保存失败:数据为空。");
		}
		writeJson(json);
	}
	
	public void update(){//更新！
		Json json = new Json();
		if (data != null) {
			try{
				UserVO vo = ((UserVO)data);
				String loginCorp = getLoginCorpInfo().getPk_corp();
				
				UserVO user = UserCache.getInstance().get(vo.getCuserid(), vo.getPk_corp());
				if(user == null){
					throw new BusinessException("该数据不存在或已删除！");
				}
				if(!loginCorp.equals(vo.getPk_corp())){
				    throw new BusinessException("修改的用户不属于当前登陆公司，不允许修改。");
				}
				UserVO[] datas = (UserVO[]) QueryDeCodeUtils.decKeyUtils(new String[]{"user_name","corpnm","crtcorp"}, new UserVO[]{data}, 0);
				userService.update(datas[0]);
				UserVO[] decdatas = (UserVO[]) QueryDeCodeUtils.decKeyUtils(new String[]{"user_name","corpnm","crtcorp"}, new UserVO[]{datas[0]}, 1);	
				json.setSuccess(true);
				json.setRows(decdatas[0]);
				json.setMsg("更新成功");
			}catch(Exception e){
				printErrorLog(json, log, e, "更新失败");
			}
		}else{
			json.setSuccess(false);
			json.setMsg("更新失败:数据为空。");
		}
		writeJson(json);
	}
	
	public void query() throws Exception{
		Grid grid = new Grid();
		String loginCorp = IGlobalConstants.DefaultGroup;//(String) getSession().getAttribute(IGlobalConstants.login_corp);
		try {
			SysPowerConditVO qryVO = new SysPowerConditVO();
			String ilock = getRequest().getParameter("ilock");
			qryVO.setCrtcorp_id(loginCorp);
			qryVO.setUtype(UTYPE);
			qryVO.setIlock(ilock);
			//查询前对用户名加密 
			if(qryVO !=null && qryVO.getUname() != null){
				String uname = qryVO.getUname();
				qryVO.setUname(CodeUtils1.enCode(uname));
			}
			List<UserVO> list = userService.query(loginCorp,qryVO);
			UserVO[] array = list.toArray(new UserVO[0]);
			array = (UserVO[]) QueryDeCodeUtils.decKeyUtils(new String[]{"user_name"}, array,1);
			if(list != null && list.size()>0){
				grid.setSuccess(true);
				grid.setMsg("查询成功");
				grid.setTotal((long)list.size());
				grid.setRows(list);
			}else{
				grid.setRows(new ArrayList<UserVO>());
				grid.setSuccess(false);
				grid.setMsg("查询数据为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, log, e, "查询失败");
		}
		writeJson(grid);
	}
	
	public void check(){
		Json json = new Json();
		json.setSuccess(true);
		if (data != null) {
			try{
				boolean exist = userService.exist((UserVO)data);
				if(exist){
					json.setSuccess(false);
					json.setMsg("已存在");
				}
			}catch(Exception e){
				printErrorLog(json, log, e, "校验失败");
			}
		}
		writeJson(json);
	}
	


	
	public void updateUserVO(DZFBoolean islocked) throws Exception {
		Json json = new Json();
		if (data != null) {
			try{
				UserVO vo = (UserVO)data;
				String loginCorp = getLoginCorpInfo().getPk_corp();
				
				UserVO user = UserCache.getInstance().get(vo.getCuserid(), vo.getPk_corp());
				if(user == null){
					throw new BusinessException("该数据不存在或已删除！");
				}
				
				vo.setLocked_tag(islocked);
				userService.updateLock(vo);
				json.setSuccess(true);
				json.setRows(data);
				if(islocked.booleanValue()){
					json.setMsg("锁定成功");
					if(loginCorp.equals(IDefaultValue.DefaultGroup)){
						writeLogRecord(LogRecordEnum.OPE_JITUAN_QXGL.getValue(),"锁定用户:用户编码"+user.getUser_code(),ISysConstants.SYS_0);
					}else{
						writeLogRecord(LogRecordEnum.OPE_ADMIN_YHGL.getValue(),"锁定用户:用户编码"+user.getUser_code(),ISysConstants.SYS_1);
					}
				}else{
					json.setMsg("解锁成功");
					if(loginCorp.equals(IDefaultValue.DefaultGroup)){
						writeLogRecord(LogRecordEnum.OPE_JITUAN_QXGL.getValue(),"解锁用户:用户编码"+user.getUser_code(),ISysConstants.SYS_0);
					}else{
						writeLogRecord(LogRecordEnum.OPE_ADMIN_YHGL.getValue(),"解锁用户:用户编码"+user.getUser_code(),ISysConstants.SYS_1);
					}
				}
			}catch(Exception e){
				printErrorLog(json, log, e, "操作失败");
			}
		}else{
			json.setSuccess(false);
			if(islocked.booleanValue()){
				json.setMsg("锁定失败:数据为空。");
			}else{
				json.setMsg("解锁失败:数据为空。");
			}
		}
		writeJson(json);
	}
}
