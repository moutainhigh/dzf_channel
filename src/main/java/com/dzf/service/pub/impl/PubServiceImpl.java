package com.dzf.service.pub.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.sys.sys_power.AccountVO;
import com.dzf.model.sys.sys_power.SysFunNodeVO;
import com.dzf.model.sys.sys_power.UserRoleVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.model.sys.sys_set.YntArea;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.IGlobalConstants;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.AreaCache;
import com.dzf.pub.constant.IRoleCode;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.ISysFunnodeService;
import com.dzf.service.sys.sys_power.IUserService;
import com.dzf.service.sys.sys_set.IAreaSearch;

@Service("pubservice")
public class PubServiceImpl implements IPubService {

	@Autowired
	private IAreaSearch areaService;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ISysFunnodeService sysFunnodeService = null;

	@Autowired
	private IUserService userser;

	private String filtersql = QueryUtil.getWhereSql();

	@Override
	public HashMap<Integer, String> queryAreaMap(String parenter_id) throws DZFWarpException {
		HashMap<Integer, String> areamap = new HashMap<Integer, String>();
		YntArea arvo = AreaCache.getInstance().get(areaService);
		YntArea[] vos = (YntArea[]) arvo.getChildren();
		YntArea[] areaVOs = getAreas(vos, parenter_id);
		for (YntArea vo : areaVOs) {
			if (!StringUtil.isEmpty(vo.getRegion_id())) {
				areamap.put(Integer.parseInt(vo.getRegion_id()), vo.getRegion_name());
			}
		}
		return areamap;
	}

	/**
	 * 通过id，获取对应下级地区数组
	 * 
	 * @param vos
	 * @param parenter_id
	 * @return
	 */
	private YntArea[] getAreas(YntArea[] vos, String parenter_id) {
		if (vos != null && vos.length > 0) {
			if (vos[0].getRegion_id().equals(parenter_id)) {
				return vos[0].getChildren();
			} else {
				YntArea[] vos_1 = vos[0].getChildren();
				if (vos_1 != null && vos_1.length > 0) {
					for (YntArea vo_1 : vos_1) {
						if (vo_1.getRegion_id().equals(parenter_id)) {
							return vo_1.getChildren();
						} else {
							YntArea[] vos_2 = vo_1.getChildren();
							if (vos_2 != null && vos_2.length > 0) {
								for (YntArea vo_2 : vos_2) {
									if (vo_2.getRegion_id().equals(parenter_id)) {
										return vo_2.getChildren();
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ChnAreaVO> queryLargeArea() throws DZFWarpException {
		Map<String, ChnAreaVO> lareamap = new HashMap<String, ChnAreaVO>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT b.vprovince AS coperatorid, a.areaname, a.areacode, a.userid   ");
		sql.append("  FROM cn_chnarea a   ");
		sql.append("  LEFT JOIN cn_chnarea_b b ON a.pk_chnarea = b.pk_chnarea   ");
		sql.append(" WHERE nvl(a.dr, 0) = 0   ");
		sql.append("   AND nvl(b.dr, 0) = 0   ");
		List<ChnAreaVO> areaVOs = (List<ChnAreaVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(ChnAreaVO.class));
		if (areaVOs != null && areaVOs.size() > 0) {
			for (ChnAreaVO vo : areaVOs) {
				if (!lareamap.containsKey(vo.getCoperatorid())) {
					lareamap.put(vo.getCoperatorid(), vo);
				}
			}
		}
		return lareamap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, List<String>> getProviceCorp() throws DZFWarpException {
		Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT account.pk_corp, account.vprovince   ");
		sql.append("  FROM bd_account account   ");
		sql.append("   Where nvl(account.ischannel, 'N') = 'Y'   ");
		sql.append("   AND nvl(account.isaccountcorp, 'N') = 'Y'   ");
		sql.append("   AND nvl(account.dr,0) = 0   ");
		List<AccountVO> list = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(AccountVO.class));
		if (list != null && list.size() > 0) {
			List<String> newlist = null;
			List<String> oldlist = null;
			for (AccountVO avo : list) {
				if (!map.containsKey(avo.getVprovince())) {
					newlist = new ArrayList<String>();
					newlist.add(avo.getPk_corp());
					map.put(avo.getVprovince(), newlist);
				} else {
					oldlist = map.get(avo.getVprovince());
					oldlist.add(avo.getPk_corp());
					map.put(avo.getVprovince(), oldlist);
				}
			}
		}
		return map;
	}

	/**
	 * 获取渠道经理所负责客户
	 * 
	 * @param userids
	 *            以‘,’隔开
	 * @param qrytype
	 *            1：渠道经理；2：培训师；3：渠道运营；
	 * 
	 */
	@Override
	public String[] getManagerCorp(String userids, Integer qrytype) throws DZFWarpException {
		if (StringUtil.isEmpty(userids)) {
			throw new BusinessException("区域经理信息不能为空");
		}
		List<String> retlist = new ArrayList<String>();
		List<String> onlylist = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" nvl(dr,0) = 0 AND nvl(type,0) = ? ");
		spm.addParam(qrytype);
		String[] users = userids.split(",");
		String where = SqlUtil.buildSqlForIn("userid", users);
		if (!StringUtil.isEmpty(where)) {
			sql.append(" AND ").append(where);
		}
		ChnAreaBVO[] bVOs = (ChnAreaBVO[]) singleObjectBO.queryByCondition(ChnAreaBVO.class, sql.toString(), spm);
		if (bVOs != null && bVOs.length > 0) {
			Map<Integer, List<String>> map = getProviceCorp();
			List<String> corplist = null;
			for (ChnAreaBVO bvo : bVOs) {
				if (bvo.getIsCharge() != null && bvo.getIsCharge().booleanValue()) {
					if (!onlylist.contains(bvo.getUserid() + "" + bvo.getVprovince())) {
						if (map != null && !map.isEmpty()) {
							corplist = map.get(bvo.getVprovince());
							if (corplist != null && corplist.size() > 0) {
								for (String corpk : corplist) {
									if (!StringUtil.isEmpty(corpk)) {
										if (!retlist.contains(corpk)) {
											retlist.add(corpk);
										}
									}
								}
								onlylist.add(bvo.getUserid() + "" + bvo.getVprovince());
							}
						}
					}
				} else {
					if (!StringUtil.isEmpty(bvo.getPk_corp())) {
						if (!retlist.contains(bvo.getPk_corp())) {
							retlist.add(bvo.getPk_corp());
						}
					}
				}
				if (!StringUtil.isEmpty(bvo.getPk_corp())) {
					if (!retlist.contains(bvo.getPk_corp())) {
						retlist.add(bvo.getPk_corp());
					}
				}
			}
		}
		if (retlist != null && retlist.size() > 0) {
			return retlist.toArray(new String[0]);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getManagerName(String pk_corp) throws DZFWarpException {
		String qsql = " nvl(dr,0) = 0 AND pk_corp = ? AND nvl(type,0) = 1 ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_corp);
		ChnAreaBVO[] bVOs = (ChnAreaBVO[]) singleObjectBO.queryByCondition(ChnAreaBVO.class, qsql, spm);
		if (bVOs != null && bVOs.length > 0) {
			String userid = bVOs[0].getUserid();
			if (!StringUtil.isEmpty(userid)) {
				UserVO uservo = userser.queryUserJmVOByID(userid);
				if (uservo != null) {
					return uservo.getUser_name();
				}
			}
		} else {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT *   ");
			sql.append(" FROM cn_chnarea_b   ");
			sql.append(" WHERE vprovince = (SELECT vprovince   ");
			sql.append(" FROM bd_account   ");
			sql.append(" WHERE nvl(dr, 0) = 0   ");
			sql.append(" AND nvl(ischannel, 'N') = 'Y'   ");
			sql.append(" AND nvl(isaccountcorp, 'N') = 'Y'   ");
			sql.append(" AND pk_corp = ? )   ");
			sql.append(" AND nvl(isCharge, 'N') = 'Y'   ");
			sql.append(" AND nvl(type,0) = 1   ");
			sql.append(" AND nvl(dr,0) = 0   ");
			spm = new SQLParameter();
			spm.addParam(pk_corp);
			List<ChnAreaBVO> blist = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
					new BeanListProcessor(ChnAreaBVO.class));
			if (blist != null && blist.size() > 0) {
				String userid = blist.get(0).getUserid();
				if (!StringUtil.isEmpty(userid)) {
					UserVO uservo = userser.queryUserJmVOByID(userid);
					if (uservo != null) {
						return uservo.getUser_name();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取加盟商直接对应的渠道经理/渠道运营(非省/市负责人) 1：渠道区域；2：培训区域；3：运营区域；
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, UserVO> getManagerMap(Integer qrytype) throws DZFWarpException {
		Map<String, UserVO> map = new HashMap<String, UserVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT account.pk_corp, b.userid cuserid,u.user_name   ");
		sql.append("  FROM bd_account account    ");
		sql.append("  LEFT JOIN cn_chnarea_b b ON account.pk_corp = b.pk_corp    ");
		sql.append("  LEFT JOIN sm_user u ON b.userid = u.cuserid    ");
		sql.append("   Where nvl(b.dr, 0) = 0    ");
		sql.append("   AND nvl(b.type, 0) = ?   ");
		spm.addParam(qrytype);
		List<UserVO> list = (List<UserVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(UserVO.class));
		if (list != null && list.size() > 0) {
			for (UserVO vo : list) {
				if (!StringUtil.isEmpty(vo.getCuserid())) {
					vo.setUser_name(CodeUtils1.deCode(vo.getUser_name()));
					map.put(vo.getPk_corp(), vo);
				}
			}
		}
		return map;
	}

	/**
	 * 查询省所对应的渠道经理(省/市负责人)
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, String> qryProvMap() throws DZFWarpException {
		Map<Integer, String> promap = new HashMap<Integer, String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT b.vprovince, b.userid    ");
		sql.append("  FROM cn_chnarea_b b    ");
		sql.append(" WHERE nvl(b.dr, 0) = 0    ");
		sql.append("   AND nvl(b.type, 0) = 1    ");
		sql.append("   AND nvl(b.ischarge, 'N') = 'Y'   ");
		List<ChnAreaBVO> list = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(ChnAreaBVO.class));
		if (list != null && list.size() > 0) {
			for (ChnAreaBVO bvo : list) {
				promap.put(bvo.getVprovince(), bvo.getUserid());
			}
		}
		return promap;
	}

	/**
	 * type： 1：渠道区域；2：培训区域；3：运营区域；
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, String> getAreaMap(String areaname, Integer type) throws DZFWarpException {
		Map<Integer, String> promap = new HashMap<Integer, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select a.areaname vprovname, b.vprovince    ");
		sql.append("  from cn_chnarea_b b    ");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea    ");
		sql.append(" where nvl(b.dr, 0) = 0    ");
		sql.append("   and nvl(a.dr, 0) = 0    ");
		sql.append("   and b.type = ?   ");
		spm.addParam(type);
		if (!StringUtil.isEmpty(areaname)) {
			sql.append(" and a.areaname = ?");
			spm.addParam(areaname);
		}
		List<ChnAreaBVO> list = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnAreaBVO.class));
		if (list != null && list.size() > 0) {
			for (ChnAreaBVO bvo : list) {
				promap.put(bvo.getVprovince(), bvo.getVprovname());
			}
		}
		return promap;
	}

	@Override
	public Map<Integer, ChnAreaVO> getChnMap(String areaname, Integer type) throws DZFWarpException {
		Map<Integer, ChnAreaVO> chnMap = new HashMap<Integer, ChnAreaVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(
				"select a.areaname,a.areacode,a.userid,u.user_name username,b.vprovince dr,b.vprovname vprovnames   ");
		sql.append("  from cn_chnarea_b b    ");
		sql.append("  LEFT JOIN cn_chnarea a on b.pk_chnarea = a.pk_chnarea    ");
		sql.append("  LEFT JOIN sm_user u ON a.userid = u.cuserid    ");
		sql.append(" where nvl(b.dr, 0) = 0    ");
		sql.append("   and nvl(a.dr, 0) = 0    ");
		sql.append("   and b.type = ?   ");
		spm.addParam(type);
		if (!StringUtil.isEmpty(areaname)) {
			sql.append(" and a.areaname = ?");
			spm.addParam(areaname);
		}
		List<ChnAreaVO> list = (List<ChnAreaVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnAreaVO.class));
		if (list != null && list.size() > 0) {
			for (ChnAreaVO vo : list) {
				vo.setUsername(CodeUtils1.deCode(vo.getUsername()));
				chnMap.put(vo.getDr(), vo);
			}
		}
		return chnMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAreaName(Integer vprovince, Integer type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select a.areaname   ");
		sql.append("  from cn_chnarea_b b    ");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea    ");
		sql.append(" where nvl(b.dr, 0) = 0    ");
		sql.append("   and nvl(a.dr, 0) = 0    ");
		sql.append("   and b.type = ?   ");
		spm.addParam(type);
		sql.append("   and b.vprovince = ?   ");
		spm.addParam(vprovince);
		List<ChnAreaVO> list = (List<ChnAreaVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnAreaVO.class));
		if (list != null && list.size() > 0) {
			return list.get(0).getAreaname();
		}
		return null;
	}

	@Override
	public String makeCondition(String cuserid, String areaname, int type) throws DZFWarpException {
		Integer dataLevel = getDataLevel(cuserid);
		String retstr = null;
		if (dataLevel != null) {
			StringBuffer sql = new StringBuffer();
			if ((dataLevel == 1 && !StringUtil.isEmpty(areaname)) || (dataLevel == 2)) {
				List<String> qryProvince = qryProvince(cuserid, dataLevel, areaname, type);
				if (qryProvince != null && qryProvince.size() > 0) {
					sql.append(" and account.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryProvince.toArray(new String[qryProvince.size()])));
					sql.append(" )");
					retstr = sql.toString();
				}
			} else if (dataLevel == 3) {
				List<String> qryCorpIds = qryCorpIds(cuserid, areaname, type);
				List<String> qryProvince = qryProvince(cuserid, dataLevel, areaname, type);
				if (qryProvince != null && qryProvince.size() > 0 && qryCorpIds != null && qryCorpIds.size() > 0) {
					sql.append(" and (account.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryProvince.toArray(new String[qryProvince.size()])));
					sql.append(" ) or ");
					sql.append("  account.pk_corp  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryCorpIds.toArray(new String[qryCorpIds.size()])));
					sql.append(" ))");
					retstr = sql.toString();
				} else if (qryProvince != null && qryProvince.size() > 0) {
					sql.append(" and account.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryProvince.toArray(new String[qryProvince.size()])));
					sql.append(" )");
					retstr = sql.toString();
				} else if (qryCorpIds != null && qryCorpIds.size() > 0) {
					sql.append(" and account.pk_corp  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryCorpIds.toArray(new String[qryCorpIds.size()])));
					sql.append(" )");
					retstr = sql.toString();
				}
			} else {
				retstr = "alldata";
			}
		}
		return retstr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getDataLevel(String cuserid) throws DZFWarpException {
		Integer reint = null;
		StringBuffer buf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		buf.append(" select to_char(cn.idatalevel) ct  ");
		buf.append("   from sm_role sr  ");
		buf.append("  right join sm_user_role ur on sr.pk_role = ur.pk_role   ");
		buf.append("  right join cn_datapower cn on sr.pk_role=cn.pk_role     ");
		buf.append("  where sr.roletype =?   ");
		buf.append("    and sr.pk_corp = ?   ");
		buf.append("    and nvl(sr.dr, 0) = 0  ");
		buf.append("    and nvl(sr.seal, 'N') = 'N'  ");
		buf.append("     and nvl(ur.dr, 0) = 0  ");
		buf.append("     and ur.cuserid = ?  ");
		sp.addParam(7);
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(cuserid);
		List<String> list = (List<String>) singleObjectBO.executeQuery(buf.toString(), sp,
				new ColumnListProcessor("ct"));
		if (list != null && list.size() > 0) {
			if (list.contains("1")) {
				reint = 1;
			} else if (list.contains("2")) {
				reint = 2;
			} else {
				reint = 3;
			}
		}
		return reint;
	}

	/**
	 * 以大区的所包含的省市，或渠道负责人的所在省市（省市维度）
	 * 
	 * @param cuserid
	 * @param flg
	 * @param areaname
	 * @param type(1:渠道；2：培训；3：运营)
	 * @return
	 */
	private List<String> qryProvince(String cuserid, Integer dataLevel, String areaname, int type) {
		StringBuffer buf = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append("select distinct to_char(b.vprovince)");
		buf.append("  from cn_chnarea_b b");
		buf.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea");
		buf.append(" where nvl(b.dr, 0) = 0");
		buf.append("   and nvl(a.dr, 0) = 0");
		buf.append("   and b.type =? ");
		spm.addParam(type);
		if (dataLevel != 1) {
			buf.append("   and (a.userid = ? ");
			buf.append("    or (b.ischarge = 'Y' and b.userid = ?))");
			spm.addParam(cuserid);
			spm.addParam(cuserid);
		}
		if (!StringUtil.isEmpty(areaname)) {
			buf.append("   and a.areaname = ? ");
			spm.addParam(areaname);
		}
		List<String> list = (List<String>) singleObjectBO.executeQuery(buf.toString(), spm, new ColumnListProcessor());
		return list;
	}

	@Override
	public List<String> qryCorpIds(String cuserid, String areaname, int type) {
		StringBuffer buf = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append("select distinct b.pk_corp");
		buf.append("  from cn_chnarea_b b");
		buf.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea");
		buf.append(" where nvl(b.dr, 0) = 0");
		buf.append("   and nvl(a.dr, 0) = 0");
		buf.append("   and b.type = ? and b.ischarge = 'N' ");
		buf.append("   and b.userid = ? ");
		spm.addParam(type);
		spm.addParam(cuserid);
		if (!StringUtil.isEmpty(areaname)) {
			buf.append("   and a.areaname = ? ");
			spm.addParam(areaname);
		}
		List<String> list = (List<String>) singleObjectBO.executeQuery(buf.toString(), spm, new ColumnListProcessor());
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> qryPros(String cuserid, Integer type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("  select distinct to_char(vprovince) vprovince   ");
		sql.append("   from cn_chnarea_b b  ");
		sql.append("  where nvl(b.dr, 0) = 0  ");
		sql.append("    and b.type = ?  ");
		sql.append("    and nvl(b.ischarge, 'N') = 'Y'  ");
		sql.append("    and b.userid = ?   ");
		sp.addParam(type);
		sp.addParam(cuserid);
		List<String> vos = (List<String>) singleObjectBO.executeQuery(sql.toString(), sp,
				new ColumnListProcessor("vprovince"));
		return vos;
	}

	@Override
	public void checkFunnode(UserVO uvo, String funnode) throws DZFWarpException {
		List<SysFunNodeVO> list = sysFunnodeService.querySysnodeByUser(uvo, IGlobalConstants.DZF_CHANNEL);
		if (list != null && list.size() > 0) {
			ArrayList<String> lnode = new ArrayList<>();
			for (SysFunNodeVO svo : list) {
				lnode.add(svo.getFun_code());
			}
			if (!lnode.contains(funnode)) {
				throw new BusinessException("无操作节点权限，请联系管理员。");
			}
		} else {
			throw new BusinessException("无操作节点权限，请联系管理员。");
		}
	}

	@Override
	public void checkButton(UserVO uvo, String funnode, String btncode) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" select b.userids,s.fun_name from cn_button b ");
		sql.append(" left join sm_funnode s on b.pk_funnode=s.pk_funnode ");
		sql.append(" where nvl(b.dr,0) = 0 and nvl(s.dr,0) = 0 and nvl(b.ispreset,'N')='N' ");
		sql.append(" and b.btn_code = ? and s.fun_code = ? ");
		spm.addParam(btncode);
		spm.addParam(funnode);
		Object executeQuery = singleObjectBO.executeQuery(sql.toString(), spm, new ColumnProcessor());
		if (executeQuery != null) {
			String userids = executeQuery.toString();
			if (!userids.contains(uvo.getCuserid())) {
				throw new BusinessException("对不起，您没有导出权限，若需要导出权限，请联系您的领导，谢谢！");
			}
		} else {
			throw new BusinessException("对不起，您没有导出权限，若需要导出权限，请联系您的领导，谢谢！");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> queryRoleCode(String cuserid) throws DZFWarpException {
		List<String> list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT l.role_code  role_code   ");
		sql.append("  FROM sm_role l    ");
		sql.append("  LEFT JOIN sm_user_role ul ON l.pk_role = ul.pk_role    ");
		sql.append("  LEFT JOIN sm_user r ON ul.cuserid = r.cuserid    ");
		sql.append(" WHERE nvl(l.dr, 0) = 0    ");
		sql.append("   AND nvl(ul.dr, 0) = 0    ");
		sql.append("   AND nvl(r.dr, 0) = 0    ");
		sql.append("   AND l.roletype = 7    ");
		sql.append("   AND r.cuserid = ?   ");
		spm.addParam(cuserid);
		List<UserRoleVO> ret = (List<UserRoleVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(UserRoleVO.class));
		if (ret != null && ret.size() > 0) {
			for (UserRoleVO rvo : ret) {
				list.add(rvo.getRole_code());
			}
		}
		return list;
	}

	@Override
	public String getPowerSql(String cuserid, Integer datatype) throws DZFWarpException {
		Integer datalevel = getDataLevel(cuserid);
		StringBuffer sql = new StringBuffer();
		if (datalevel != null) {
			if (datalevel == 1) {
				sql.append("alldata");
			} else if (datalevel == 2) {
				List<String> qryProvince = queryProvince(cuserid, datalevel, datatype);
				if (qryProvince != null && qryProvince.size() > 0) {
					sql.append(" AND account.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryProvince.toArray(new String[0])));
					sql.append(" )");
				}
			} else if (datalevel == 3) {
				List<String> corlist = queryCorpIds(cuserid, datatype);
				List<String> prolist = queryProvince(cuserid, datalevel, datatype);
				if (prolist != null && prolist.size() > 0 && corlist != null && corlist.size() > 0) {
					sql.append(" AND (account.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(prolist.toArray(new String[0])));
					sql.append(" ) OR ");
					sql.append("  account.pk_corp  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(corlist.toArray(new String[0])));
					sql.append(" ))");
				} else if (prolist != null && prolist.size() > 0) {
					sql.append(" AND account.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(prolist.toArray(new String[0])));
					sql.append(" )");
				} else if (corlist != null && corlist.size() > 0) {
					sql.append(" AND account.pk_corp  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(corlist.toArray(new String[0])));
					sql.append(" )");
				}
			}
		}
		if (sql != null && sql.length() > 0) {
			return sql.toString();
		}
		return null;
	}

	/**
	 * 查询用户负责的省份
	 * 
	 * @param cuserid
	 * @param datalevel
	 * @param datatype
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<String> queryProvince(String cuserid, Integer datalevel, Integer datatype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select distinct to_char(b.vprovince)   ");
		sql.append("  from cn_chnarea_b b");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea");
		sql.append(" where nvl(b.dr, 0) = 0");
		sql.append("   and nvl(a.dr, 0) = 0");
		sql.append("   and b.type = ? ");
		spm.addParam(datatype);
		if (datalevel != 1) {
			sql.append("   and (a.userid = ? ");
			sql.append("    or (b.ischarge = 'Y' and b.userid = ?))");
			spm.addParam(cuserid);
			spm.addParam(cuserid);
		}
		return (List<String>) singleObjectBO.executeQuery(sql.toString(), spm, new ColumnListProcessor());
	}

	/**
	 * 查询用户负责的客户
	 * 
	 * @param cuserid
	 * @param datatype
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<String> queryCorpIds(String cuserid, Integer datatype) throws DZFWarpException {
		StringBuffer buf = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append("select distinct b.pk_corp");
		buf.append("  from cn_chnarea_b b");
		buf.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea");
		buf.append(" where nvl(b.dr, 0) = 0");
		buf.append("   and nvl(a.dr, 0) = 0");
		buf.append("   and b.ischarge = 'N' ");
		buf.append("   and b.type = ?  ");
		spm.addParam(datatype);
		buf.append("   and b.userid = ? ");
		spm.addParam(cuserid);
		return (List<String>) singleObjectBO.executeQuery(buf.toString(), spm, new ColumnListProcessor());
	}

	@Override
	public Integer getAreaPower(String cuserid, Integer type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		// 1、查询是否为总经理
		sql.append("SELECT vdeptuserid    ");
		sql.append("  FROM cn_leaderset    ");
		sql.append(" WHERE nvl(dr, 0) = 0    ");
		sql.append("   AND vdeptuserid = ?    ");
		spm.addParam(cuserid);
		boolean flag = singleObjectBO.isExists(IDefaultValue.DefaultGroup, sql.toString(), spm);
		if (flag) {
			return 1;
		}
		// 2、查询是否为区总
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("SELECT userid    ");
		sql.append("  FROM cn_chnarea    ");
		sql.append(" WHERE nvl(dr, 0) = 0    ");
		sql.append("   AND userid = ?    ");
		spm.addParam(cuserid);
		sql.append("   AND type = ?    ");
		spm.addParam(type);
		flag = singleObjectBO.isExists(IDefaultValue.DefaultGroup, sql.toString(), spm);
		if (flag) {
			return 2;
		}
		// 3、查询是否为渠道经理
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("SELECT userid    ");
		sql.append("  FROM cn_chnarea_b    ");
		sql.append(" WHERE nvl(dr, 0) = 0    ");
		sql.append("   AND userid = ?    ");
		spm.addParam(cuserid);
		sql.append("   AND type = ?    ");
		spm.addParam(type);
		flag = singleObjectBO.isExists(IDefaultValue.DefaultGroup, sql.toString(), spm);
		if (flag) {
			return 3;
		}
		return -1;
	}
   
	@SuppressWarnings("unchecked")
	@Override
	public Integer queryRoleType(String cuserid) throws DZFWarpException {
		//节点角色分类：
		//1：分配“渠道经理、大区总”角色，为渠道人员
		//2：分配“培训师、运营培训经理”角色，为培训人员
		//3：渠道类角色和培训类角色都没有分配，则按培训人员查询大区和省（市）下拉数据
		Integer type = -1;
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT distinct rl.role_code  ") ;
		sql.append("  FROM sm_user_role ul  ") ; 
		sql.append("  LEFT JOIN sm_role rl ON ul.pk_role = rl.pk_role  ") ; 
		sql.append(" WHERE nvl(ul.dr, 0) = 0  ") ; 
		sql.append("   AND nvl(rl.dr, 0) = 0  ") ; 
		sql.append("   AND ul.cuserid = ? ");
		spm.addParam(cuserid);
		List<String> codelist = (List<String>) singleObjectBO.executeQuery(sql.toString(), spm,
				new ColumnListProcessor());
		if(codelist != null && codelist.size() > 0){
			if(codelist.contains(IRoleCode.CORPQDJL) || codelist.contains(IRoleCode.CORPDQZ)){
				return IStatusConstant.IQUDAO;
			}else if(codelist.contains(IRoleCode.CORPPXJL) || codelist.contains(IRoleCode.CORPPXS)){
				return IStatusConstant.IPEIXUN;
			}else{
				return IStatusConstant.IPEIXUN;
			}
		}
		return type;
	}

}
