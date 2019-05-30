package com.dzf.service.branch.setup.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.branch.setup.BranchUserVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserRoleVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.branch.setup.IBranchUserService;
import com.dzf.service.sys.sys_power.IUserService;


@Service("userBranch")
public class BranchUserServiceImpl implements IBranchUserService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	private String UTYPE = "8";//分支机构，用户
	
	private String loginCorp = IGlobalConstants.DefaultGroup;
	
	@Autowired
	private IUserService userService;
	
	@Override
	public List<UserVO> query(QryParamVO qvo) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter param = new SQLParameter();
        sql.append("select u.cuserid, ");
        sql.append("       u.user_code, ");
        sql.append("       u.user_name, ");
        sql.append("       u.disable_time, ");
        sql.append("       u.able_time, ");
        sql.append("       u.user_note, ");
        sql.append("       bs.vname as pk_depts, ");
        sql.append("       wm_concat(r.role_name) rolenames ");
        sql.append("  from sm_user u ");
        sql.append("  left join br_branchset bs on u.pk_department = bs.pk_branchset ");
        sql.append("  left join sm_user_role ur on u.cuserid = ur.cuserid ");
        sql.append("  left join sm_role r on ur.pk_role = r.pk_role ");
        sql.append(" where nvl(u.dr, 0) = 0 ");
        sql.append("   and nvl(bs.dr, 0) = 0 ");
        sql.append("   and nvl(ur.dr, 0) = 0 ");
        sql.append("   and nvl(r.dr, 0) = 0 ");
        sql.append("   and u.xsstyle = ? ");
        sql.append("   and exists (select ub.pk_branchset br_user_branch ub ");
        sql.append("         where u.pk_department = ub.pk_branchset ");
        sql.append("           and ub.cuserid = ?) ");
        sql.append(" group by u.cuserid, ");
        sql.append("          u.user_code, ");
        sql.append("          u.user_name, ");
        sql.append("          u.disable_time, ");
        sql.append("          u.able_time, ");
        sql.append("          u.user_note, ");
        sql.append("          bs.vname ");
    	param.addParam(UTYPE);
    	List<UserVO> list =(List<UserVO>) singleObjectBO.executeQuery(sql.toString(), param, new BeanListProcessor(UserVO.class));
		return list;
	}
	
	@Override
	public  void save(UserVO uservo) throws DZFWarpException{
		String[] roles = uservo.getRoleids().split(",");
//		uservo.get
		//1、保存用户，相关信息
		uservo.setPk_creatcorp(loginCorp);// 创建用户的公司
		uservo.setPk_corp(loginCorp);
		uservo.setXsstyle(UTYPE);
		QueryDeCodeUtils.decKeyUtil(new String[] { "user_name" },uservo, 0);
		userService.save(uservo);
		//2、保存用户角色中间表
		UserRoleVO[] urvos = new UserRoleVO[roles.length];
		UserRoleVO urvo;
		int i=0;
		for (String roleid : roles) {
			urvo = new UserRoleVO();
			urvo.setPk_corp(loginCorp);
			urvo.setCuserid(uservo.getCuserid());
			urvo.setPk_role(roleid);
			urvos[i] = urvo;
			i++;
		}
		singleObjectBO.insertVOArr(loginCorp, urvos);
	}
	
	
	@Override
	public void saveEdit(UserVO uservo) throws DZFWarpException{
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(uservo.getTableName(), uservo.getCuserid(),uuid, 60);
			UserVO oldVO = checkData(uservo);
			if (!loginCorp.equals(oldVO.getPk_corp())) {
				throw new BusinessException("修改的用户不属于当前登陆公司，不允许修改。");
			}
			QueryDeCodeUtils.decKeyUtil(new String[] { "user_name" },uservo, 0);
			userService.update(uservo);
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(uservo.getTableName(), uservo.getCuserid(),uuid);
		}	
	}
	
	@Override
	public BranchUserVO queryByID(String loginUserid,String qryId) throws DZFWarpException{
		BranchUserVO retvo = new BranchUserVO();
		UserVO uservo;
		if(StringUtil.isEmpty(qryId)){
			
			uservo = (UserVO)singleObjectBO.queryByPrimaryKey(UserVO.class, qryId);
			retvo.setUservo(uservo);
		}
		List<ComboBoxVO> roles = getBranchRoles(loginUserid);
		retvo.setRoles(roles);
		List<ComboBoxVO> branchs = getBranchs(loginUserid);
		retvo.setBranchs(branchs);
		return retvo;
	}
	
	@Override
	public void updateLock(UserVO vo) throws DZFWarpException{
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(vo.getTableName(), vo.getCuserid(),uuid, 60);
			checkData(vo);
			singleObjectBO.update(vo, new String[]{"istatus"});
		}catch (Exception e) {
		    if (e instanceof BusinessException)
		        throw new BusinessException(e.getMessage());
		    else
		        throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getCuserid(),uuid);
		}	
	}
	
	/**
	 * 获取分支机构，所有角色
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private List<ComboBoxVO> getBranchRoles(String cuserid) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        sql.append("select r.pk_role id,r.role_name name ");
        sql.append("  from sm_role r ");
        sql.append(" where r.roletype = 10 ");
        sql.append("   and nvl(r.dr, 0) = 0 ");
        return (List<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), null, new BeanListProcessor(ComboBoxVO.class));
    }
	
	private List<ComboBoxVO> getBranchs(String cuserid) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter param = new SQLParameter();
    	sql.append(" select bs.pk_branchset id, bs.vname name");
    	sql.append("   from br_user_branch ub ");
    	sql.append("   left join br_branchset bs on ub.pk_branchset = bs.pk_branchset ");
    	sql.append("  where ub.cuserid = ? ");
    	sql.append("    and nvl(bs.dr, 0) = 0 ");
    	sql.append("    and nvl(ub.dr, 0) = 0 ");
    	param.addParam(cuserid);
        return (List<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), param, new BeanListProcessor(ComboBoxVO.class));
    }
	
	
	/**
	 * 是否是最新数据
	 * @param qryvo
	 */
	private UserVO checkData(UserVO qvo) throws DZFWarpException{
		UserVO vo =(UserVO) singleObjectBO.queryByPrimaryKey(UserVO.class, qvo.getCuserid());
		if(vo==null){
			throw new BusinessException("该行数据已被其它用户删除!");
		}
		if(!vo.getUpdatets().equals(qvo.getUpdatets())){
			throw new BusinessException("该行数据已发生变化,请取消操作刷新再试!");
		}
		return vo;
	}

}
