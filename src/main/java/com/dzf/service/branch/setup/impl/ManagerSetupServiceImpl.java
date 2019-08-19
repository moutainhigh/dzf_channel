package com.dzf.service.branch.setup.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.branch.setup.ManagerSetupVO;
import com.dzf.model.branch.setup.UserBranchVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserRoleVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.framework.rsa.Encode;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.branch.setup.IManagerSetupService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("managersetupser")
public class ManagerSetupServiceImpl implements IManagerSetupService {

	// 统计管理岗-角色编码
	private static final String BRTJGL = "brtjgl";

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IUserService userService;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Override
	public Integer queryTotalRow(ManagerSetupVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(pamvo, false);
		return multBodyObjectBO.queryDataTotal(ManagerSetupVO.class, qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ManagerSetupVO> query(ManagerSetupVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(pamvo, false);
		List<ManagerSetupVO> list = (List<ManagerSetupVO>) multBodyObjectBO.queryDataPage(ManagerSetupVO.class,
				qryvo.getSql(), qryvo.getSpm(), pamvo.getPage(), pamvo.getRows(), "u.ts");
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "user_name" }, list, 1);
			setShowName(list);
		}

		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ManagerSetupVO> queryAll(ManagerSetupVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(pamvo, false);
		List<ManagerSetupVO> retlist = new ArrayList<ManagerSetupVO>();
		List<ManagerSetupVO> list = (List<ManagerSetupVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ManagerSetupVO.class));
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "user_name" }, list, 1);
			for (ManagerSetupVO mvo : list) {
				if (!StringUtil.isEmpty(mvo.getUser_code()) && !StringUtil.isEmpty(mvo.getUser_name())
						&& (mvo.getUser_code().indexOf(pamvo.getUser_code()) != -1
								|| mvo.getUser_name().indexOf(pamvo.getUser_code()) != -1)) {
					retlist.add(mvo);
				}
			}
			if(retlist != null && retlist.size() > 0){
				setShowName(list);
			}
		}
		return retlist;
	}

	/**
	 * 设置显示名称
	 * 
	 * @param list
	 * @throws DZFWarpException
	 */
	private void setShowName(List<ManagerSetupVO> list) throws DZFWarpException {
		Map<String, StringBuffer> nmap = qryBanchName(null);
		StringBuffer vbraname = null;
		for (ManagerSetupVO mvo : list) {
			if (nmap != null && !nmap.isEmpty()) {
				vbraname = nmap.get(mvo.getCuserid());
				if (vbraname != null && vbraname.length() > 0) {
					mvo.setVbraname(vbraname.toString());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ManagerSetupVO queryById(ManagerSetupVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(pamvo, false);
		List<ManagerSetupVO> list = (List<ManagerSetupVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ManagerSetupVO.class));
		if(list != null && list.size() > 0){
			ManagerSetupVO mvo = list.get(0);
			QueryDeCodeUtils.decKeyUtil(new String[] { "user_name"}, mvo, 1);
			mvo.setUser_password(new Encode().decode(mvo.getUser_password()));
			String ids = qryBanchId(mvo.getCuserid());
			mvo.setVbraname(ids);
			return mvo;
		}
		return null;
	}
	
	/**
	 * 查询用户分配的机构的Id
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private String qryBanchId(String cuserid) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT h.cuserid, h.pk_branchset    ");
		sql.append("  FROM br_user_branch h    ");
		sql.append(" WHERE nvl(h.dr, 0) = 0    ");
		if(!StringUtil.isEmpty(cuserid)){
			sql.append(" AND h.cuserid = ?   ");
			spm.addParam(cuserid);
		}
		List<UserBranchVO> list = (List<UserBranchVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(UserBranchVO.class));
		if (list != null && list.size() > 0) {
			StringBuffer ids = new StringBuffer();
			for (UserBranchVO bvo : list) {
				ids.append(bvo.getPk_branchset()).append(";");
			}
			String retid = ids.toString();
			return retid.substring(0, retid.length() - 1);
		}
		return null;
	}

	/**
	 * 查询用户分配的机构的名称
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, StringBuffer> qryBanchName(String cuserid) throws DZFWarpException {
		Map<String, StringBuffer> nmap = new HashMap<String, StringBuffer>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT h.cuserid AS coperatorid, t.vname    ");
		sql.append("  FROM br_user_branch h    ");
		sql.append("  LEFT JOIN br_branchset t ON h.pk_branchset = t.pk_branchset    ");
		sql.append(" WHERE nvl(h.dr, 0) = 0    ");
		sql.append("   AND nvl(t.dr, 0) = 0    ");
		if(!StringUtil.isEmpty(cuserid)){
			sql.append(" AND h.cuserid = ?   ");
			spm.addParam(cuserid);
		}
		List<BranchInstSetupVO> list = (List<BranchInstSetupVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(BranchInstSetupVO.class));
		if (list != null && list.size() > 0) {
			StringBuffer name = null;
			for (BranchInstSetupVO bvo : list) {
				if (!nmap.containsKey(bvo.getCoperatorid())) {
					name = new StringBuffer();
					name.append(bvo.getVname());
					nmap.put(bvo.getCoperatorid(), name);
				} else {
					name = nmap.get(bvo.getCoperatorid());
					name.append(";").append(bvo.getVname());
					nmap.put(bvo.getCoperatorid(), name);
				}
			}
		}
		return nmap;
	}

	/**
	 * 获取查询条件
	 * 
	 * @param pamvo
	 * @param isqrynum
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySql(ManagerSetupVO pamvo, boolean isqrynum) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if (isqrynum) {
			sql.append("SELECT COUNT(u.cuserid)    ");
		} else {
			sql.append("SELECT u.cuserid,    ");
			sql.append("       u.user_code,    ");
			sql.append("       u.user_name,    ");
			sql.append("       u.able_time,    ");
			sql.append("       u.disable_time,    ");
			sql.append("       nvl(u.locked_tag,'N') AS locked_tag,    ");
			sql.append("       u.user_note,    ");
			sql.append("       u.ismanager,   ");
			sql.append("       u.pk_creatcorp,   ");
			sql.append("       u.pk_corp,   ");
			sql.append("       u.user_password   ");
		}
		sql.append("  FROM sm_user u    ");
		sql.append(" WHERE nvl(u.dr, 0) = 0     ");
		sql.append("   AND u.xsstyle = 8   ");// 分部管理用户
		sql.append("   AND u.ismanager = 'Y'   ");
		sql.append("   AND u.pk_corp = ?    ");
		spm.addParam(pamvo.getPk_corp());
		// 是否锁定
		if (pamvo.getLocked_tag() != null && pamvo.getLocked_tag().booleanValue()) {
			sql.append(" and u.locked_tag = 'Y'   ");
		} else if(pamvo.getLocked_tag() != null && !pamvo.getLocked_tag().booleanValue()){
			sql.append(" and nvl(u.locked_tag,'N') = 'N'   ");
		}
		if(!StringUtil.isEmpty(pamvo.getCuserid())){
			sql.append(" and u.cuserid = ?   ");
			spm.addParam(pamvo.getCuserid());
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public void save(ManagerSetupVO upvo, String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(upvo.getCuserid())){
			saveManagerSet(upvo, pk_corp);
		}else{
			updateManagerSet(upvo, pk_corp);
		}
	}
	
	/**
	 * 更新管理员
	 * @param upvo
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private void updateManagerSet(ManagerSetupVO upvo, String pk_corp) throws DZFWarpException {
		checkBeforeUpdate(upvo, pk_corp);
		//1、更新用户
		userService.update(upvo);
		//2、重新设置用户机构
		String sql = " DELETE FROM br_user_branch WHERE cuserid = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(upvo.getCuserid());
		singleObjectBO.executeUpdate(sql, spm);
		saveBranchSet(upvo, upvo, pk_corp);
	}
	
	/**
	 * 管理员更新前校验
	 * @throws DZFWarpException
	 */
	private void checkBeforeUpdate(ManagerSetupVO upvo, String pk_corp) throws DZFWarpException {
		UserVO user = userService.queryUserById(upvo.getCuserid());
		if (user == null) {
			throw new BusinessException("该数据不存在或已删除！");
		}
		if (!pk_corp.equals(user.getPk_corp())) {
			throw new BusinessException("修改的用户不属于当前登陆公司，不允许修改。");
		}
	}
	
	/**
	 * 保存管理员
	 * @param upvo
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private void saveManagerSet(ManagerSetupVO upvo, String pk_corp) throws DZFWarpException {
		// 1、保存用户
		UserVO uvo = userService.save(upvo);
		// 2、给用户赋默认角色
		String pk_role = getRoleId();
		if (StringUtil.isEmpty(pk_role)) {
			throw new BusinessException("请先设置统计管理岗角色");
		}
		UserRoleVO urvo = new UserRoleVO();
		urvo.setCuserid(uvo.getCuserid());
		urvo.setPk_corp(pk_corp);
		urvo.setPk_role(pk_role);
		singleObjectBO.saveObject(pk_corp, urvo);
		// 3、用户机构设置
		saveBranchSet(upvo, uvo, pk_corp);
	}
	
	/**
	 * 用户机构设置
	 * @param upvo
	 * @param uvo
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private void saveBranchSet(ManagerSetupVO upvo, UserVO uvo, String pk_corp) throws DZFWarpException {
		if (!StringUtil.isEmpty(upvo.getVbraname())) {
			String[] brs = upvo.getVbraname().split(",");
			List<UserBranchVO> mslist = new ArrayList<UserBranchVO>();
			UserBranchVO ubvo = null;
			for (String br : brs) {
				ubvo = new UserBranchVO();
				ubvo.setPk_corp(pk_corp);
				ubvo.setPk_branchset(br.trim());
				ubvo.setCuserid(uvo.getCuserid());
				ubvo.setCoperatorid(uvo.getCuserid());
				ubvo.setDoperatedate(new DZFDate());
				ubvo.setDr(0);
				mslist.add(ubvo);
			}
			singleObjectBO.insertVOArr(pk_corp, mslist.toArray(new UserBranchVO[0]));
		} else {
			throw new BusinessException("请选择机构");
		}
	}

	/**
	 * 获取统计管理岗角色主键
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	private String getRoleId() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_role    ");
		sql.append("  FROM sm_role    ");
		sql.append(" WHERE nvl(dr, 0) = 0    ");
		sql.append("   AND role_code = ?     ");
		spm.addParam(BRTJGL);
		return (String) singleObjectBO.executeQuery(sql.toString(), spm, new ColumnProcessor("pk_role"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BranchInstSetupVO> queryBranch() throws DZFWarpException {
		String sql = "SELECT pk_branchset, vname FROM br_branchset WHERE nvl(dr, 0) = 0 ORDER BY ts DESC";
		return (List<BranchInstSetupVO>) singleObjectBO.executeQuery(sql, null,
				new BeanListProcessor(BranchInstSetupVO.class));
	}

}
