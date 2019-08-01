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
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IRegionalManService;
import com.dzf.service.pub.IPubService;

@Service("manregional")
public class RegionalManServiceImpl extends ManCommonServiceImpl implements IRegionalManService {

	@Autowired
	private SingleObjectBO singleObjectBO = null;

	@Autowired
	private IPubService pubService;

	private int qrytype = IStatusConstant.IQUDAO;

	private String wheresql = QueryUtil.getWhereSql();

	@Override
	public List<ManagerVO> query(ManagerVO qvo) throws DZFWarpException {
		ArrayList<ManagerVO> retList = new ArrayList<>();
		Integer level = pubService.getDataLevel(qvo.getUserid());

		ArrayList<String> pk_corps = new ArrayList<>();
		Map<String, ManagerVO> manaMap = new HashMap<>();
		if (level != null && level <= 2) {
			manaMap = qryReginalCorp(qvo, pk_corps);
		}
		if (pk_corps != null && pk_corps.size() != 0) {
			retList = queryCommon(qvo, manaMap, pk_corps);
			sortList(retList);
		}
		return retList;
	}

	private Map<String, ManagerVO> qryReginalCorp(ManagerVO qvo, ArrayList<String> pk_corps) {
		Map<String, ManagerVO> map = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select ");
		sql.append(" account.pk_corp, ");
		sql.append(" account.innercode, ");
		sql.append(" account.unitname corpname, ");
		sql.append(" account.drelievedate, ");//解约日期
		sql.append(" account.vprovince, ");
		sql.append(" y.region_name vprovname");
		sql.append("  from bd_account account ");
		sql.append("  left join ynt_area y on account.vprovince = y.region_id ");
		sql.append("                      and y.parenter_id = 1 ");
		sql.append("                      and nvl(y.dr, 0) = 0 ");
		sql.append(" where ").append(wheresql);
		sql.append(" 	and exists (select distinct cb.vprovince ");
		sql.append("          from cn_chnarea ca ");
		sql.append("          left join cn_chnarea_b cb on ca.pk_chnarea = cb.pk_chnarea ");
		sql.append("         where nvl(ca.dr, 0) = 0 ");
		sql.append("           and nvl(cb.dr, 0) = 0 ");
		sql.append("           and ca.type = ? ");
		sql.append("           and ca.userid = ? ");
		sql.append("           and cb.vprovince = account.vprovince) ");
		sp.addParam(qrytype);
		sp.addParam(qvo.getUserid());
		// if (!StringUtil.isEmpty(qvo.getAreaname())) {
		// sql.append(" and a.areaname=? "); // 大区
		// sp.addParam(qvo.getAreaname());
		// }
		if(qvo.getChantype() !=null && qvo.getChantype()>0){
			sql.append(" and account.drelievedate ");
			if(qvo.getChantype()==1){
				sql.append(" is null ");
			}else{
				sql.append(" is not null ");
			}
		}
		if (qvo.getVprovince() != null && qvo.getVprovince() != -1) {
			sql.append(" and account.vprovince=? ");// 省市
			sp.addParam(qvo.getVprovince());
		}
		if (!StringUtil.isEmpty(qvo.getCuserid())) {
			String[] corps = pubService.getManagerCorp(qvo.getCuserid(), qrytype);
			if (corps != null && corps.length > 0) {
				String where = SqlUtil.buildSqlForIn(" account.pk_corp", corps);
				sql.append(" AND ").append(where);
			} else {
				sql.append(" AND account.pk_corp is null   ");
			}
		}
		List<ManagerVO> list = (List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(ManagerVO.class));
		if(list!=null && list.size()>0){
			Map<String, UserVO> opermap = pubService.getManagerMap(qrytype);// 渠道运营
			Map<Integer, ChnAreaVO> chnmap = pubService.getChnMap(qvo.getAreaname(), qrytype);// 渠道运营
			// ChnAreaVO areaVO ;
			String corpName;
			UserVO userVO;
			for (ManagerVO managerVO : list) {
				if (chnmap.containsKey(managerVO.getVprovince())) {
					corpName = CodeUtils1.deCode(managerVO.getCorpname());
					if (StringUtil.isEmpty(qvo.getCorpname()) || corpName.indexOf(qvo.getCorpname()) != -1) {
						// areaVO = chnmap.get(managerVO.getVprovince());
						// managerVO.setAreaname(areaVO.getAreaname());
						// managerVO.setUsername(areaVO.getUsername());

						managerVO.setCorpname(corpName);
						userVO = opermap.get(managerVO.getPk_corp());
						if (userVO != null) {
							managerVO.setCusername(userVO.getUser_name());
						}
						setDefult(managerVO);
						pk_corps.add(managerVO.getPk_corp());
						map.put(managerVO.getPk_corp(), managerVO);
					}
				}
			}
		}
		return map;
	}

}
