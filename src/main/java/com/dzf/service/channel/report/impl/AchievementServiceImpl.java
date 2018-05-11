package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.AchievementVO;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.AccountVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IAchievementService;

@Service("achievementser")
public class AchievementServiceImpl implements IAchievementService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public AchievementVO queryLine(QryParamVO paramvo) throws DZFWarpException {
		Map<Integer, String> powmap = qryUserPower(paramvo);
		return null;
	}

	@Override
	public AchievementVO queryChart(QryParamVO paramvo) throws DZFWarpException {
		return null;
	}
	
	/**
	 * 查询用户角色权限  
	 * 1、区域总经理；2、区域经理；3、渠道经理或渠道负责人（有可能一个渠道经理是多个地区的负责人，同时是另外多个地区的非负责人）；4、非销售人员；
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, String> qryUserPower(QryParamVO paramvo) throws DZFWarpException {
		Map<Integer, String> pmap = new HashMap<Integer, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		List<String> pklist = new ArrayList<String>();
		// 1、区域总经理；
		sql.append("SELECT t.pk_leaderset  \n");
		sql.append("  FROM cn_leaderset t  \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND (t.vdeptuserid = ? OR t.vcomuserid = ? OR t.vgroupuserid = ? ) \n");
		spm.addParam(paramvo.getCuserid());
		spm.addParam(paramvo.getCuserid());
		spm.addParam(paramvo.getCuserid());
		List<ManagerVO> list = (List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ManagerVO.class));
		if (list != null && list.size() > 0) {
			pmap.put(1, "区域总经理");
			return pmap;
		}
		// 2、区域经理
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("SELECT b.vprovince  \n");
		sql.append("  FROM cn_chnarea a  \n");
		sql.append("  LEFT JOIN cn_chnarea_b b ON a.pk_chnarea = b.pk_chnarea  \n");
		sql.append(" WHERE nvl(a.dr, 0) = 0  \n");
		sql.append("   AND nvl(b.dr, 0) = 0  \n");
		sql.append("   AND a.userid = ? \n");
		List<ChnAreaBVO> plist = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(ChnAreaBVO.class));
		if (plist != null && plist.size() > 0) {
			pklist = new ArrayList<String>();
			for (ChnAreaBVO bvo : plist) {
				if (bvo.getVprovince() != null) {
					pklist.add(String.valueOf(bvo.getVprovince()));
				}
			}
			if (pklist != null && pklist.size() > 0) {
				String prosql = SqlUtil.buildSqlForIn("t.vprovince", pklist.toArray(new String[0]));
				pmap.put(2, prosql);
				return pmap;
			}
		}
		// 3、渠道经理或渠道负责人
		pklist = new ArrayList<String>();
		sql = new StringBuffer();
		spm = new SQLParameter();
		// 3.1、区域负责人
		sql.append("SELECT t.pk_corp  \n");
		sql.append("  FROM cn_chnarea_b b  \n");
		sql.append("  LEFT JOIN bd_account t ON b.vprovince = t.vprovince  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'  \n");
		sql.append("   AND nvl(b.ischarge, 'N') = 'Y'  \n");
		sql.append("   AND b.userid = ? \n");
		spm.addParam(paramvo.getCuserid());
		List<AccountVO> clist = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AccountVO.class));
		if(clist != null && clist.size() > 0){
			for(AccountVO acvo : clist){
				pklist.add(acvo.getPk_corp());
			}
		}
		sql.append("SELECT t.pk_corp  \n") ;
		sql.append("  FROM cn_chnarea_b b  \n") ; 
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(b.ischarge, 'N') = 'Y'  \n") ; 
		sql.append("   AND b.userid = ? \n");
		List<AccountVO> klist = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AccountVO.class));
		if(klist != null && klist.size() > 0){
			for(AccountVO acvo : klist){
				pklist.add(acvo.getPk_corp());
			}
		}
		if(pklist != null && pklist.size() > 0){
			String corpsql = SqlUtil.buildSqlForIn("t.pk_corp", pklist.toArray(new String[0]));
			pmap.put(3, corpsql);
			return pmap;
		}
		pmap.put(4, "非销售人员");
		return pmap;
	}

}
