package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.IChannelService;
import com.dzf.service.pub.IPubService;

@Service("channelServ")
public class ChannelServiceImpl implements IChannelService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IPubService pubservice;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CorpVO> querySmall(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select pk_corp,unitname,innercode ");
		sql.append(" from bd_account ba ");
		sql.append(" where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y' ");
		sql.append("  and nvl(ischannel,'N') = 'Y'  ");
		String smallCondition = getSmallCondition(paramvo);
		if(StringUtil.isEmpty(smallCondition)){
			sql.append(" 1 != 1 ");
		}else{
			sql.append(smallCondition);
		}
		sql.append(" order by innercode ");
		
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(CorpVO.class));
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, list, 1);
			List<CorpVO> rList = new ArrayList<>();
			if (!StringUtil.isEmpty(paramvo.getCorpcode())) {
				for (CorpVO cvo : list) {
					if (cvo.getUnitname().contains(paramvo.getCorpcode()) || cvo.getInnercode().contains(paramvo.getCorpcode())) {
						rList.add(cvo);
					}
				}
				return rList;
			}
		}
		return list;
	}
	
	private String getSmallCondition(QryParamVO paramvo){
		List<String> qryProvince = pubservice.qryPros(paramvo.getCuserid(), paramvo.getQrytype());
		List<String> qryCorpIds = pubservice.qryCorpIds(paramvo.getCuserid(), null, paramvo.getQrytype());
		StringBuffer sql = new StringBuffer();
		String retstr = null;
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
		return retstr;
	}
	
}
