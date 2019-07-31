package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

/**
 * 数据运营管理下报表的数据权限过滤
 *
 */
@Service("datacommonser")
public class DataCommonRepImpl {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IPubService pubService;

	@Autowired
	private IUserService userser;

	private String filtersql = QueryUtil.getWhereSql();

	protected HashMap<String, DataVO> queryCorps(QryParamVO paramvo, Class<?> cla, int type) throws DZFWarpException {
		Integer level = pubService.getDataLevel(paramvo.getUser_name());
		HashMap<String, DataVO> map = new HashMap<>();
		if (level == null) {

		} else if (level <= 2) {
			HashMap<String, UserVO> userMap = userser.queryUserMap("000001", true);
			map = qryBoth(paramvo, level, cla, userMap, type);// 2大区+3渠道总
		} else if (level == 3) {
			HashMap<String, UserVO> userMap = userser.queryUserMap("000001", true);
			map = qryChannel(paramvo, cla, userMap, type);// 1省
		}
		return map;
	}

	/**
	 * 查询省市数据分析
	 * 
	 * @param qvo
	 * @param type
	 * @return
	 */
	private HashMap<String, DataVO> qryChannel(QryParamVO qvo, Class cla, HashMap<String, UserVO> userMap,
			int type) {
		// 1、查询 是 省/市负责人相关的数据
		List<DataVO> datalist = qryCharge(qvo, cla, type); 
		String condition = null;
		if (datalist == null || datalist.size() == 0) {
			datalist = new ArrayList<>();
		} else {
			List<String> pros = pubService.qryPros(qvo.getUser_name(), 2);
			if (pros != null && pros.size() > 0) {
				condition = SqlUtil.buildSqlForIn("b.vprovince", pros.toArray(new String[pros.size()]));
				qvo.setVqrysql(condition);
			}
		}
		// 2、查询 非  省/市负责人相关的数据
		List<DataVO> nplist = qryNotCharge(qvo, cla, type);
		if (nplist != null && nplist.size() > 0) {
			datalist.addAll(nplist);
		}
		HashMap<String, DataVO> map = new HashMap<>();
		UserVO uservo;
		if (datalist != null && datalist.size() > 0) {
			for (DataVO datavo : datalist) {
				if (datavo.getCorpname() != null && !(datavo.getPk_corp().equals(datavo.getCorpname()))) {
					datavo.setCuserid(null);
				}
				if (!map.containsKey(datavo.getPk_corp())) {
					map.put(datavo.getPk_corp(), datavo);
				} else if (!StringUtil.isEmpty(datavo.getCuserid())) {
					map.put(datavo.getPk_corp(), datavo);
				}
				uservo = userMap.get(datavo.getUserid());
				if (uservo != null) {
					datavo.setUsername(uservo.getUser_name());
				}
				uservo = userMap.get(datavo.getCuserid());
				if (uservo != null) {
					datavo.setCusername(uservo.getUser_name());
				}
			}
		}
		return map;
	}

	/**
	 * 查询渠道总数据+区域总经理
	 * 
	 * @param qvo
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, DataVO> qryBoth(QryParamVO qvo, Integer level, Class cla,
			HashMap<String, UserVO> userMap, int type) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select a.areaname,    ");
		sql.append("       a.userid,    ");
		sql.append("       y.region_name vprovname,    ");
		sql.append("       account.pk_corp,    ");
		sql.append("       account.innercode,    ");
		sql.append("       account.drelievedate,    ");// 解约日期
		sql.append("       account.djoindate chndate,   ");
		sql.append("       account.vprovince,    ");
		sql.append("       b.ischarge,    ");
		sql.append("       b.userid cuserid,    ");
		sql.append("       b.pk_corp corpname    ");
		sql.append("  from bd_account account    ");
		sql.append("  left join ynt_area y on account.vprovince = y.region_id    ");
		sql.append("                      and y.parenter_id = 1    ");
		sql.append("                      and nvl(y.dr, 0) = 0    ");
		sql.append("  left join cn_chnarea_b b on account.vprovince = b.vprovince    ");
		sql.append("                          and b.type = ?    ");
		sql.append("                          and nvl(b.dr, 0) = 0    ");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea    ");
		sql.append("                        and a.type = ?    ");
		sql.append("                        and nvl(a.dr, 0) = 0    ");
		sql.append(" where ").append(filtersql);
		sql.append("   and account.vprovince is not null   ");
		spm.addParam(type);
		spm.addParam(type);
		if (qvo.getSeletype() != null && qvo.getSeletype() != 0) {// 不包含已解约加盟商
			sql.append(" and (account.drelievedate is null or account.drelievedate >? )");
			spm.addParam(new DZFDate().toString());
		}
		if (qvo.getCorps() != null && qvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(qvo.getCorps());
			sql.append(" and account.pk_corp  in (" + corpIdS + ")");
		}
		sql.append("   AND account.pk_corp NOT IN    ");
		sql.append("       (SELECT f.pk_corp    ");
		sql.append("          FROM ynt_franchisee f    ");
		sql.append("         WHERE nvl(dr, 0) = 0    ");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y')   ");
		if (level == 2) {// 区域总经理
			sql.append(" and a.userid=? ");
			spm.addParam(qvo.getUser_name());
		}
		if (!StringUtil.isEmpty(qvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			spm.addParam(qvo.getAreaname());
		}
		if (qvo.getVprovince() != null && qvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			spm.addParam(qvo.getVprovince());
		}
		Boolean isQuery = true;
		if (!StringUtil.isEmpty(qvo.getCuserid())) {
			isQuery = false;
			String condition = null;
			List<String> qryPros = pubService.qryPros(qvo.getCuserid(), type);
			if (qryPros != null && qryPros.size() > 0) {
				condition = SqlUtil.buildSqlForIn("b.vprovince", qryPros.toArray(new String[qryPros.size()]));
				sql.append(" and (" + condition + " or b.userid=? ) ");
			} else {
				sql.append(" and b.userid=? ");// 渠道经理
			}
			spm.addParam(qvo.getCuserid());
		}
		List<DataVO> list = (List<DataVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(cla));
		HashMap<String, DataVO> map = new HashMap<String, DataVO>();
		if (list != null && list.size() > 0) {
			Boolean isPut = true;
			UserVO uservo;
			for (DataVO datavo : list) {
				if (datavo.getCorpname() == null || !(datavo.getPk_corp().equals(datavo.getCorpname()))) {
					datavo.setCuserid(null);
				}
				if (!isQuery && (datavo.getIsCharge().booleanValue() || !StringUtil.isEmpty(datavo.getCuserid()))) {
					isPut = true;
				} else if (!isQuery) {
					isPut = false;
				}
				if (!map.containsKey(datavo.getPk_corp()) && isPut) {
					map.put(datavo.getPk_corp(), datavo);
				} else if (!StringUtil.isEmpty(datavo.getCuserid()) && isPut) {
					map.put(datavo.getPk_corp(), datavo);
				}

				uservo = userMap.get(datavo.getUserid());
				if (uservo != null) {
					datavo.setUsername(uservo.getUser_name());
				}
				uservo = userMap.get(datavo.getCuserid());
				if (uservo != null) {
					datavo.setCusername(uservo.getUser_name());
				}
			}
		}
		return map;
	}

	/**
	 * 查询 是 省/市负责人相关的数据
	 * 
	 * @param qvo
	 * @param cla
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DataVO> qryCharge(QryParamVO qvo, Class<?> cla, int type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select account.pk_corp,    ");
		sql.append("       a.areaname,    ");
		sql.append("       a.userid,    ");
		sql.append("       b.vprovname,    ");
		sql.append("       b.vprovince,    ");
		sql.append("       account.innercode,    ");
		sql.append("       account.drelievedate,    ");// 解约日期
		sql.append("       account.djoindate chndate,   ");
		sql.append("       b.pk_corp corpname,    ");
		sql.append("       (case    ");
		sql.append("         when b.pk_corp is null then    ");
		sql.append("          null    ");
		sql.append("         when b.pk_corp != account.pk_corp then    ");
		sql.append("          null    ");
		sql.append("         else    ");
		sql.append("          b.userid    ");
		sql.append("       end) cuserid    ");
		sql.append("  from bd_account account    ");
		sql.append(" right join cn_chnarea_b b on account.vprovince = b.vprovince    ");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea    ");
		sql.append(" where ").append(filtersql);
		sql.append("   and nvl(b.dr, 0) = 0    ");
		sql.append("   and nvl(a.dr, 0) = 0    ");
		sql.append("   and b.type = ?    ");
		sql.append("   and b.userid = ?    ");
		sql.append("   and nvl(b.ischarge, 'N') = 'Y'   ");
		spm.addParam(type);
		spm.addParam(qvo.getUser_name());
		if (qvo.getSeletype() != null && qvo.getSeletype() != 0) {// 不包含已解约加盟商
			sql.append(" and (account.drelievedate is null or account.drelievedate >? )");
			spm.addParam(new DZFDate());
		}
		if (qvo.getCorps() != null && qvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(qvo.getCorps());
			sql.append(" and account.pk_corp  in (" + corpIdS + ")");
		}
		sql.append("   AND account.pk_corp NOT IN    ");
		sql.append("       (SELECT f.pk_corp    ");
		sql.append("          FROM ynt_franchisee f    ");
		sql.append("         WHERE nvl(dr, 0) = 0    ");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y')   ");
		if (!StringUtil.isEmpty(qvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			spm.addParam(qvo.getAreaname());
		}
		if (qvo.getVprovince() != null && qvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			spm.addParam(qvo.getVprovince());
		}
		return (List<DataVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(cla));
	}

	/**
	 * 查询 非 省/市负责人相关的数据
	 * 
	 * @param qvo
	 * @param cla
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DataVO> qryNotCharge(QryParamVO qvo, Class<?> cla, int type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select account.pk_corp,    ");
		sql.append("       a.areaname,    ");
		sql.append("       a.userid,    ");
		sql.append("       b.userid cuserid,    ");
		sql.append("       b.vprovname,    ");
		sql.append("       account.vprovince,    ");
		sql.append("       account.innercode,    ");
		sql.append("       account.drelievedate,    ");// 解约日期
		sql.append("       account.djoindate chndate,   ");
		sql.append("       b.pk_corp corpname    ");
		sql.append("  from bd_account account    ");
		sql.append(" right join cn_chnarea_b b on account.pk_corp = b.pk_corp    ");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea    ");
		sql.append(" where ").append(filtersql);
		sql.append("   and nvl(b.dr, 0) = 0    ");
		sql.append("   and nvl(a.dr, 0) = 0    ");
		sql.append("   and b.type = ?    ");
		spm.addParam(type);
		if (!StringUtil.isEmpty(qvo.getVqrysql())) {
			sql.append(" and (" + qvo.getVqrysql() + " or b.userid=? ) ");
		} else {
			sql.append(" and b.userid=? ");
		}
		spm.addParam(qvo.getUser_name());
		if (qvo.getSeletype() != null && qvo.getSeletype() != 0) {// 不包含已解约加盟商
			sql.append(" and (account.drelievedate is null or account.drelievedate >? )");
			spm.addParam(new DZFDate());
		}
		if (qvo.getCorps() != null && qvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(qvo.getCorps());
			sql.append(" and account.pk_corp  in (" + corpIdS + ")");
		}
		sql.append(" and nvl(b.ischarge,'N')='N' ");
		sql.append("   AND account.pk_corp NOT IN    ");
		sql.append("       (SELECT f.pk_corp    ");
		sql.append("          FROM ynt_franchisee f    ");
		sql.append("         WHERE nvl(dr, 0) = 0    ");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y')   ");
		if (!StringUtil.isEmpty(qvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			spm.addParam(qvo.getAreaname());
		}
		if (qvo.getVprovince() != null && qvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			spm.addParam(qvo.getVprovince());
		}
		return (List<DataVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(cla));
	}

}
