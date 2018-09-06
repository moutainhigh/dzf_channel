package com.dzf.service.pub.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ArrayListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.channel.sale.ChnAreaVO;
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
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.ISysFunnodeService;
import com.dzf.service.sys.sys_set.IAreaSearch;

@Service("pubservice")
public class PubServiceImpl implements IPubService {

	@Autowired
	private IAreaSearch areaService;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ISysFunnodeService sysFunnodeService = null;

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
		sql.append("SELECT b.vprovince AS coperatorid, a.areaname, a.areacode, a.userid \n");
		sql.append("  FROM cn_chnarea a \n");
		sql.append("  LEFT JOIN cn_chnarea_b b ON a.pk_chnarea = b.pk_chnarea \n");
		sql.append(" WHERE nvl(a.dr, 0) = 0 \n");
		sql.append("   AND nvl(b.dr, 0) = 0 \n");
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
		sql.append("SELECT t.pk_corp, t.vprovince \n");
		sql.append("  FROM bd_account t \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y' \n");
		sql.append("   AND nvl(t.isaccountcorp, 'N') = 'Y' \n");
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
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public String[] getManagerCorp(String userids) throws DZFWarpException {
		if (StringUtil.isEmpty(userids)) {
			throw new BusinessException("区域经理信息不能为空");
		}
		List<String> retlist = new ArrayList<String>();
		List<String> onlylist = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		sql.append(" nvl(dr,0) = 0 AND nvl(type,0) = 1 ");
		String[] users = userids.split(",");
		String where = SqlUtil.buildSqlForIn("userid", users);
		if (!StringUtil.isEmpty(where)) {
			sql.append(" AND ").append(where);
		}
		ChnAreaBVO[] bVOs = (ChnAreaBVO[]) singleObjectBO.queryByCondition(ChnAreaBVO.class, sql.toString(), null);
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

	// @SuppressWarnings("unchecked")
	// @Override
	// public String getManagerName(String pk_corp) throws DZFWarpException {
	// String qsql = " nvl(dr,0) = 0 AND pk_corp = ? AND nvl(type,0) = 1 ";
	// SQLParameter spm = new SQLParameter();
	// spm.addParam(pk_corp);
	// ChnAreaBVO[] bVOs = (ChnAreaBVO[])
	// singleObjectBO.queryByCondition(ChnAreaBVO.class, qsql, spm);
	// if (bVOs != null && bVOs.length > 0) {
	// String userid = bVOs[0].getUserid();
	// if (!StringUtil.isEmpty(userid)) {
	// UserVO uservo = UserCache.getInstance().get(userid, null);
	// if (uservo != null) {
	// return uservo.getUser_name();
	// }
	// }
	// } else {
	// StringBuffer sql = new StringBuffer();
	// sql.append("SELECT * \n");
	// sql.append(" FROM cn_chnarea_b \n");
	// sql.append(" WHERE vprovince = (SELECT vprovince \n");
	// sql.append(" FROM bd_account \n");
	// sql.append(" WHERE nvl(dr, 0) = 0 \n");
	// sql.append(" AND nvl(ischannel, 'N') = 'Y' \n");
	// sql.append(" AND nvl(isaccountcorp, 'N') = 'Y' \n");
	// sql.append(" AND pk_corp = ? ) \n");
	// sql.append(" AND nvl(isCharge, 'N') = 'Y' \n");
	// sql.append(" AND nvl(type,0) = 1 \n");
	// sql.append(" AND nvl(dr,0) = 0 \n");
	// spm = new SQLParameter();
	// spm.addParam(pk_corp);
	// List<ChnAreaBVO> blist = (List<ChnAreaBVO>)
	// singleObjectBO.executeQuery(sql.toString(), spm,
	// new BeanListProcessor(ChnAreaBVO.class));
	// if(blist != null && blist.size() > 0){
	// String userid = blist.get(0).getUserid();
	// if (!StringUtil.isEmpty(userid)) {
	// UserVO uservo = UserCache.getInstance().get(userid, null);
	// if (uservo != null) {
	// return uservo.getUser_name();
	// }
	// }
	// }
	// }
	// return null;
	// }

	/**
	 * 获取加盟商对应的渠道经理
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getManagerMap() throws DZFWarpException {
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.pk_corp, t.vprovince, b.userid  \n");
		sql.append("  FROM bd_account t  \n");
		sql.append("  LEFT JOIN cn_chnarea_b b ON t.pk_corp = b.pk_corp  \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(b.type, 0) = 1 \n");
		List<ChnAreaBVO> list = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(ChnAreaBVO.class));
		if (list != null && list.size() > 0) {
			// Map<Integer, String> promap = qryProvMap();
			// String userid = "";
			for (ChnAreaBVO bvo : list) {
				if (!StringUtil.isEmpty(bvo.getUserid())) {
					map.put(bvo.getPk_corp(), bvo.getUserid());
				} /*
					 * else{ if(promap != null && !promap.isEmpty()){ userid =
					 * promap.get(bvo.getVprovince());
					 * if(!StringUtil.isEmpty(userid)){
					 * map.put(bvo.getPk_corp(), userid); } } }
					 */
			}
		}
		return map;
	}

	/**
	 * 查询省所对应的渠道经理
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, String> qryProvMap() throws DZFWarpException {
		Map<Integer, String> promap = new HashMap<Integer, String>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT b.vprovince, b.userid  \n");
		sql.append("  FROM cn_chnarea_b b  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(b.type, 0) = 1  \n");
		sql.append("   AND nvl(b.ischarge, 'N') = 'Y' \n");
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
		sql.append("select a.areaname vprovname, b.vprovince  \n");
		sql.append("  from cn_chnarea_b b  \n");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea  \n");
		sql.append(" where nvl(b.dr, 0) = 0  \n");
		sql.append("   and nvl(a.dr, 0) = 0  \n");
		sql.append("   and b.type = ? \n");
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

	@SuppressWarnings("unchecked")
	@Override
	public String getAreaName(Integer vprovince, Integer type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select a.areaname \n");
		sql.append("  from cn_chnarea_b b  \n");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea  \n");
		sql.append(" where nvl(b.dr, 0) = 0  \n");
		sql.append("   and nvl(a.dr, 0) = 0  \n");
		sql.append("   and b.type = ? \n");
		spm.addParam(type);
		sql.append("   and b.vprovince = ? \n");
		spm.addParam(vprovince);
		List<ChnAreaVO> list = (List<ChnAreaVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnAreaVO.class));
		if (list != null && list.size() > 0) {
			return list.get(0).getAreaname();
		}
		return null;
	}

	@Override
	public String makeCondition(String cuserid, String areaname) throws DZFWarpException {
		Integer dataLevel = getDataLevel(cuserid);
		String retstr = null;
		if (dataLevel != null) {
			StringBuffer sql = new StringBuffer();
			if ((dataLevel == 1 && !StringUtil.isEmpty(areaname)) || (dataLevel == 2)) {
				List<String> qryProvince = qryProvince(cuserid, dataLevel, areaname);
				if (qryProvince != null && qryProvince.size() > 0) {
					sql.append(" and ba.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryProvince.toArray(new String[qryProvince.size()])));
					sql.append(" )");
					retstr = sql.toString();
				}
			} else if (dataLevel == 3) {
				List<String> qryCorpIds = qryCorpIds(cuserid, areaname);
				List<String> qryProvince = qryProvince(cuserid, dataLevel, areaname);
				if (qryProvince != null && qryProvince.size() > 0 && qryCorpIds != null && qryCorpIds.size() > 0) {
					sql.append(" and (ba.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryProvince.toArray(new String[qryProvince.size()])));
					sql.append(" ) or ");
					sql.append("  ba.pk_corp  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryCorpIds.toArray(new String[qryCorpIds.size()])));
					sql.append(" ))");
					retstr = sql.toString();
				} else if (qryProvince != null && qryProvince.size() > 0) {
					sql.append(" and ba.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryProvince.toArray(new String[qryProvince.size()])));
					sql.append(" )");
					retstr = sql.toString();
				} else if (qryCorpIds != null && qryCorpIds.size() > 0) {
					sql.append(" and ba.pk_corp  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryCorpIds.toArray(new String[qryCorpIds.size()])));
					sql.append(" )");
					retstr = sql.toString();
				}
			} else {
				retstr = "flg";
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
		buf.append(" select to_char(cn.idatalevel) ct\n");
		buf.append("   from sm_role sr\n");
		buf.append("  right join sm_user_role ur on sr.pk_role = ur.pk_role \n");
		buf.append("  right join cn_datapower cn on sr.pk_role=cn.pk_role   \n");
		buf.append("  where sr.roletype =? \n");
		buf.append("    and sr.pk_corp = ? \n");
		buf.append("    and nvl(sr.dr, 0) = 0\n");
		buf.append("    and nvl(sr.seal, 'N') = 'N'\n");
		buf.append("     and nvl(ur.dr, 0) = 0\n");
		buf.append("     and ur.cuserid = ?\n");
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
	 * @return
	 */
	private List<String> qryProvince(String cuserid, Integer dataLevel, String areaname) {
		StringBuffer buf = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append("select distinct to_char(b.vprovince)");
		buf.append("  from cn_chnarea_b b");
		buf.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea");
		buf.append(" where nvl(b.dr, 0) = 0");
		buf.append("   and nvl(a.dr, 0) = 0");
		buf.append("   and b.type = 3 ");
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

	/**
	 * 以渠道经理所选的加盟商（加盟商客户维度）
	 * 
	 * @param cuserid
	 * @param flg
	 * @param areaname
	 * @return
	 */
	private List<String> qryCorpIds(String cuserid, String areaname) {
		StringBuffer buf = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		buf.append("select distinct b.pk_corp");
		buf.append("  from cn_chnarea_b b");
		buf.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea");
		buf.append(" where nvl(b.dr, 0) = 0");
		buf.append("   and nvl(a.dr, 0) = 0");
		buf.append("   and b.type = 3 and b.ischarge = 'N' ");
		buf.append("   and b.userid = ? ");
		spm.addParam(cuserid);
		if (!StringUtil.isEmpty(areaname)) {
			buf.append("   and a.areaname = ? ");
			spm.addParam(areaname);
		}
		List<String> list = (List<String>) singleObjectBO.executeQuery(buf.toString(), spm, new ColumnListProcessor());
		return list;
	}

	@Override
	public List<String> qryPros(String cuserid, Integer type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("  select distinct to_char(vprovince) vprovince \n");
		sql.append("   from cn_chnarea_b b\n");
		sql.append("  where nvl(b.dr, 0) = 0\n");
		sql.append("    and b.type = ?\n");
		sql.append("    and nvl(b.ischarge, 'N') = 'Y'\n");
		sql.append("    and b.userid = ? \n");
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
	public void checkButton(UserVO uvo,String funnode,String btncode) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter spm = new SQLParameter();
        sql.append(" select b.userids,s.fun_name from cn_button b ");
        sql.append(" left join sm_funnode s on b.pk_funnode=s.pk_funnode ");
        sql.append(" where nvl(b.dr,0) = 0 and nvl(s.dr,0) = 0 and nvl(b.ispreset,'N')='N' ");
        sql.append(" and b.btn_code = ? and s.fun_code = ? ");
        spm.addParam(btncode);
        spm.addParam(funnode);
        Object executeQuery = singleObjectBO.executeQuery(sql.toString(),spm, new ColumnProcessor());
        if(executeQuery!=null){
             String userids =executeQuery.toString();
             if(!userids.contains(uvo.getCuserid())){
                 throw new BusinessException("对不起，您没有导出权限，若需要导出权限，请联系您的领导，谢谢！");
             }
        }else{
            throw new BusinessException("对不起，您没有导出权限，若需要导出权限，请联系您的领导，谢谢！");
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<String> queryRoleCode(String cuserid) throws DZFWarpException {
		List<String> list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT l.role_code  role_code \n");
		sql.append("  FROM sm_role l  \n");
		sql.append("  LEFT JOIN sm_user_role ul ON l.pk_role = ul.pk_role  \n");
		sql.append("  LEFT JOIN sm_user r ON ul.cuserid = r.cuserid  \n");
		sql.append(" WHERE nvl(l.dr, 0) = 0  \n");
		sql.append("   AND nvl(ul.dr, 0) = 0  \n");
		sql.append("   AND nvl(r.dr, 0) = 0  \n");
		sql.append("   AND l.roletype = 7  \n");
		sql.append("   AND r.cuserid = ? \n");
		spm.addParam(cuserid);
		List<UserRoleVO> ret = (List<UserRoleVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(UserRoleVO.class));
		if(ret != null && ret.size() > 0){
			for(UserRoleVO rvo : ret){
				list.add(rvo.getRole_code());
			}
		}
		return list;
	}

	@Override
	public String getPowerSql(String cuserid, Integer datatype) throws DZFWarpException {
		Integer datalevel = getDataLevel(cuserid);
		StringBuffer sql = new StringBuffer();
		if(datalevel != null){
			if(datalevel == 1){
				sql.append("alldata");
			}else if (datalevel == 2) {
				List<String> qryProvince = queryProvince(cuserid, datalevel, datatype);
				if (qryProvince != null && qryProvince.size() > 0) {
					sql.append(" AND t.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(qryProvince.toArray(new String[0])));
					sql.append(" )");
				}
			} else if (datalevel == 3) {
				List<String> corlist = queryCorpIds(cuserid, datatype);
				List<String> prolist = queryProvince(cuserid, datalevel, datatype);
				if (prolist != null && prolist.size() > 0 && corlist != null && corlist.size() > 0) {
					sql.append(" AND (t.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(prolist.toArray(new String[0])));
					sql.append(" ) OR ");
					sql.append("  t.pk_corp  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(corlist.toArray(new String[0])));
					sql.append(" ))");
				} else if (prolist != null && prolist.size() > 0) {
					sql.append(" AND t.vprovince  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(prolist.toArray(new String[0])));
					sql.append(" )");
				} else if (corlist != null && corlist.size() > 0) {
					sql.append(" AND t.pk_corp  in (");
					sql.append(SqlUtil.buildSqlConditionForIn(corlist.toArray(new String[0])));
					sql.append(" )");
				}
			}
		}
		if(sql != null && sql.length() > 0){
			return sql.toString();
		}
		return "";
	}
	
	/**
	 * 查询用户负责的省份
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
		sql.append("select distinct to_char(b.vprovince) \n");
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
	
}
