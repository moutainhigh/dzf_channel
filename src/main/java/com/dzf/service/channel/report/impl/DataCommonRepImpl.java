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
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.pub.IPubService;

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

	protected HashMap<String, DataVO> queryCorps(QryParamVO paramvo, Class cla) throws DZFWarpException {
		Integer level = pubService.getDataLevel(paramvo.getUser_name());

		HashMap<String, DataVO> map = new HashMap<>();
		if (level == null) {

		} else if (level <= 2) {
			map = qryBoth(paramvo, level, cla);// 2大区+3渠道总
		} else if (level == 3) {
			map = qryChannel(paramvo, cla);// 1省
		}
		return map;
	}

	/**
	 * 查询省市数据分析
	 * 
	 * @param qvo
	 * @return
	 */
	private HashMap<String, DataVO> qryChannel(QryParamVO qvo, Class cla) {
		List<DataVO> qryCharge = qryCharge(qvo, cla); // 查询 是 省/市负责人相关的数据
		String condition = null;
		if (qryCharge == null || qryCharge.size() == 0) {
			qryCharge = new ArrayList<>();
		} else {
			List<String> pros = pubService.qryPros(qvo.getUser_name(), 2);
			if (pros != null && pros.size() > 0) {
				condition = SqlUtil.buildSqlForIn("b.vprovince", pros.toArray(new String[pros.size()]));
				qvo.setVqrysql(condition);
			}
		}
		List<DataVO> qryNotCharge = qryNotCharge(qvo, cla);// 查询 非 省/市负责人相关的数据
		if (qryNotCharge != null && qryNotCharge.size() > 0) {
			qryCharge.addAll(qryNotCharge);
		}
		HashMap<String, DataVO> map = new HashMap<>();
		if (qryCharge != null && qryCharge.size() > 0) {
			for (DataVO dataVO : qryCharge) {
				if (dataVO.getCorpname() != null && !(dataVO.getPk_corp().equals(dataVO.getCorpname()))) {
					dataVO.setCuserid(null);
				}
				if (!map.containsKey(dataVO.getPk_corp())) {
					map.put(dataVO.getPk_corp(), dataVO);
				} else if (!StringUtil.isEmpty(dataVO.getCuserid())) {
					map.put(dataVO.getPk_corp(), dataVO);
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
	private HashMap<String, DataVO> qryBoth(QryParamVO qvo, Integer level, Class cla) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select a.areaname,  \n");
		sql.append("       a.userid,  \n");
		sql.append("       y.region_name vprovname,  \n");
		sql.append("       p.pk_corp,  \n");
		sql.append("       p.innercode,  \n");
		sql.append("       p.vprovince,  \n");
		sql.append("       b.ischarge,  \n");
		sql.append("       b.userid cuserid,  \n");
		sql.append("       b.pk_corp corpname  \n");
		sql.append("  \n");
		sql.append("  from bd_account p  \n");
		sql.append("  left join ynt_area y on p.vprovince = y.region_id  \n");
		sql.append("                      and y.parenter_id = 1  \n");
		sql.append("                      and nvl(y.dr, 0) = 0  \n");
		sql.append("  left join cn_chnarea_b b on p.vprovince = b.vprovince  \n");
		sql.append("                          and b.type = 2  \n");
		sql.append("                          and nvl(b.dr, 0) = 0  \n");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea  \n");
		sql.append("                        and a.type = 2  \n");
		sql.append("                        and nvl(a.dr, 0) = 0  \n");
		sql.append(" where nvl(p.dr, 0) = 0  \n");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'Y'  \n");
		sql.append("   and nvl(p.ischannel, 'N') = 'Y'  \n");
		sql.append("   and nvl(p.isseal, 'N') = 'N'  \n");
		sql.append("   and p.vprovince is not null \n");
		if (qvo.getSeletype() != null && qvo.getSeletype() != 0) {// 不包含已解约加盟商
			sql.append(" and (p.drelievedate is null or p.drelievedate >? )");
			spm.addParam(new DZFDate().toString());
		}
		if (qvo.getCorps() != null && qvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(qvo.getCorps());
			sql.append(" and p.pk_corp  in (" + corpIdS + ")");
		}
		sql.append("   AND p.pk_corp NOT IN  \n");
		sql.append("       (SELECT f.pk_corp  \n");
		sql.append("          FROM ynt_franchisee f  \n");
		sql.append("         WHERE nvl(dr, 0) = 0  \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
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
			List<String> qryPros = pubService.qryPros(qvo.getCuserid(), 2);
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
			for (DataVO dataVO : list) {
				if (dataVO.getCorpname() == null || !(dataVO.getPk_corp().equals(dataVO.getCorpname()))) {
					dataVO.setCuserid(null);
				}
				if (!isQuery && (dataVO.getIsCharge().booleanValue() || !StringUtil.isEmpty(dataVO.getCuserid()))) {
					isPut = true;
				} else if (!isQuery) {
					isPut = false;
				}
				if (!map.containsKey(dataVO.getPk_corp()) && isPut) {
					map.put(dataVO.getPk_corp(), dataVO);
				} else if (!StringUtil.isEmpty(dataVO.getCuserid()) && isPut) {
					map.put(dataVO.getPk_corp(), dataVO);
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
	private List<DataVO> qryCharge(QryParamVO qvo, Class cla) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select p.pk_corp,  \n");
		sql.append("       a.areaname,  \n");
		sql.append("       a.userid,  \n");
		sql.append("       b.vprovname,  \n");
		sql.append("       b.vprovince,  \n");
		sql.append("       p.innercode,  \n");
		sql.append("       b.pk_corp corpname,  \n");
		sql.append("       (case  \n");
		sql.append("         when b.pk_corp is null then  \n");
		sql.append("          null  \n");
		sql.append("         when b.pk_corp != p.pk_corp then  \n");
		sql.append("          null  \n");
		sql.append("         else  \n");
		sql.append("          b.userid  \n");
		sql.append("       end) cuserid  \n");
		sql.append("  from bd_account p  \n");
		sql.append(" right join cn_chnarea_b b on p.vprovince = b.vprovince  \n");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea  \n");
		sql.append(" where nvl(b.dr, 0) = 0  \n");
		sql.append("   and nvl(p.dr, 0) = 0  \n");
		sql.append("   and nvl(a.dr, 0) = 0  \n");
		sql.append("   and b.type = 2  \n");
		sql.append("   and nvl(p.ischannel, 'N') = 'Y'  \n");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'Y'  \n");
		sql.append("   and b.userid = ?  \n");
		sql.append("   and nvl(b.ischarge, 'N') = 'Y' \n");
		spm.addParam(qvo.getUser_name());
		if (qvo.getSeletype() != null && qvo.getSeletype() != 0) {// 不包含已解约加盟商
			sql.append(" and (p.drelievedate is null or p.drelievedate >? )");
			spm.addParam(new DZFDate());
		}
		if (qvo.getCorps() != null && qvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(qvo.getCorps());
			sql.append(" and p.pk_corp  in (" + corpIdS + ")");
		}
		sql.append("   AND p.pk_corp NOT IN  \n");
		sql.append("       (SELECT f.pk_corp  \n");
		sql.append("          FROM ynt_franchisee f  \n");
		sql.append("         WHERE nvl(dr, 0) = 0  \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		if (!StringUtil.isEmpty(qvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			spm.addParam(qvo.getAreaname());
		}
		if (qvo.getVprovince() != null && qvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			spm.addParam(qvo.getVprovince());
		}
		List<DataVO> list = (List<DataVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(cla));
		return list;
	}

	/**
	 * 查询 非 省/市负责人相关的数据
	 * 
	 * @param qvo
	 * @param cla
	 * @return
	 */
	private List<DataVO> qryNotCharge(QryParamVO qvo, Class cla) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select p.pk_corp,  \n");
		sql.append("       a.areaname,  \n");
		sql.append("       a.userid,  \n");
		sql.append("       b.userid cuserid,  \n");
		sql.append("       b.vprovname,  \n");
		sql.append("       b.vprovince,  \n");
		sql.append("       p.innercode,  \n");
		sql.append("       b.pk_corp corpname  \n");
		sql.append("  from bd_account p  \n");
		sql.append(" right join cn_chnarea_b b on p.pk_corp = b.pk_corp  \n");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea  \n");
		sql.append(" where nvl(b.dr, 0) = 0  \n");
		sql.append("   and nvl(p.dr, 0) = 0  \n");
		sql.append("   and nvl(a.dr, 0) = 0  \n");
		sql.append("   and b.type = 2  \n");
		sql.append("   and nvl(p.ischannel, 'N') = 'Y'  \n");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'Y' \n");
		if (!StringUtil.isEmpty(qvo.getVqrysql())) {
			sql.append(" and (" + qvo.getVqrysql() + " or b.userid=? ) ");
		} else {
			sql.append(" and b.userid=? ");
		}
		spm.addParam(qvo.getUser_name());
		if (qvo.getSeletype() != null && qvo.getSeletype() != 0) {// 不包含已解约加盟商
			sql.append(" and (p.drelievedate is null or p.drelievedate >? )");
			spm.addParam(new DZFDate());
		}
		if (qvo.getCorps() != null && qvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(qvo.getCorps());
			sql.append(" and p.pk_corp  in (" + corpIdS + ")");
		}
		sql.append(" and nvl(b.ischarge,'N')='N' ");
		sql.append("   AND p.pk_corp NOT IN  \n");
		sql.append("       (SELECT f.pk_corp  \n");
		sql.append("          FROM ynt_franchisee f  \n");
		sql.append("         WHERE nvl(dr, 0) = 0  \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		if (!StringUtil.isEmpty(qvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			spm.addParam(qvo.getAreaname());
		}
		if (qvo.getVprovince() != null && qvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			spm.addParam(qvo.getVprovince());
		}
		List<DataVO> vos = (List<DataVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(cla));
		return vos;
	}

}
