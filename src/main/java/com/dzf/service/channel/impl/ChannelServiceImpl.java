package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.QueryUtil;
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
		sql.append(" from bd_account account ");
		sql.append(" where nvl(dr,0) = 0 and nvl(isaccountcorp,'N') = 'Y' ");
		sql.append("  and nvl(ischannel,'N') = 'Y'  ");
		sql.append("  and "+ QueryUtil.getWhereSql());
		String smallCondition = getSmallCondition(paramvo);
		if(StringUtil.isEmpty(smallCondition)){
			sql.append(" and 1 != 1 ");
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
		return retstr;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CorpVO> queryChannel(ChInvoiceVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select pk_corp, unitname, innercode, vprovince    ") ;
		sql.append("  from bd_account account    ") ; 
		sql.append(" where nvl(account.dr, 0) = 0    ") ; 
		sql.append("   and nvl(account.isaccountcorp, 'N') = 'Y'    ") ; 
		sql.append("   and nvl(account.ischannel, 'N') = 'Y'    ");
		if (vo.getDr() != null && vo.getDr() >= 0) {// 给区域划分（省市过滤）用的
			sql.append(" and account.vprovince=? ");
			sp.addParam(vo.getDr());
			if (!StringUtil.isEmpty(vo.getVmome())) {
				String[] split = vo.getVmome().split(",");
				sql.append(" and account.pk_corp not in (");
				sql.append(SqlUtil.buildSqlConditionForIn(split));
				sql.append(" )");
			}
		} else if (vo.getDr() != null && vo.getDr() < 0 && vo.getDr() != -1) {// 增加权限的加盟商参照 -2（渠道） -3（培训） -4（运营）
			String condition = pubservice.getPowerSql(vo.getEmail(), vo.getDr()==-5 ? 2 :-vo.getDr()-1);
			if (condition != null && !condition.equals("alldata")) {
				sql.append(condition);
			} else if (condition == null) {
				return null;
			}
			if (vo.getDr() == -5) {// 数据运营管理，4个报表
				sql.append(" and account.pk_corp not in (");
				sql.append("       (SELECT f.pk_corp    ");
				sql.append("          FROM ynt_franchisee f    ");
				sql.append("         WHERE nvl(dr, 0) = 0    ");
				sql.append("           AND nvl(f.isreport, 'N') = 'Y')   ");
				sql.append(" )");
			}
		}
		sql.append(" order by innercode ");
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(CorpVO.class));
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, list, 1);
			List<CorpVO> rList = new ArrayList<>();
			if (!StringUtil.isEmpty(vo.getCorpcode())) {
				for (CorpVO cvo : list) {
					if (cvo.getUnitname().contains(vo.getCorpcode()) || cvo.getInnercode().contains(vo.getCorpcode())) {
						rList.add(cvo);
					}
				}
				return rList;
			}
		}
		return list;
	}
	
	@Override
	public List<CorpVO> qryMultiChannel(QryParamVO qvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select pk_corp,unitname,innercode,vprovince ");
		sql.append(" from bd_account account ");
		sql.append("  where nvl(dr,0) = 0  ");
		sql.append("   and nvl(isaccountcorp,'N') = 'Y'  ");
		sql.append("   and nvl(ischannel,'N') = 'Y' ");
		if(qvo.getQrytype()== 1){//过滤掉演示加盟商
			sql.append(" and ").append(QueryUtil.getWhereSql());
		}
		sql.append(" and ").append(QueryUtil.getWhereSql());
		if (qvo.getVprovince()!=-1) {// 给区域划分（省市过滤）用的
			sql.append(" and vprovince=? ");
			sp.addParam(qvo.getVprovince());
			if (!StringUtil.isEmpty(qvo.getPk_corp())) {
				String[] split = qvo.getPk_corp().split(",");
				sql.append(" and pk_corp not in (");
				sql.append(SqlUtil.buildSqlConditionForIn(split));
				sql.append(" )");
			}
		}
		sql.append(" order by innercode ");
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(CorpVO.class));
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, list, 1);
			List<CorpVO> rList = new ArrayList<>();
			if (!StringUtil.isEmpty(qvo.getCorpcode())) {
				for (CorpVO cvo : list) {
					if (cvo.getUnitname().contains(qvo.getCorpcode()) || cvo.getInnercode().contains(qvo.getCorpcode())) {
						rList.add(cvo);
					}
				}
				return rList;
			}
		}
		return list;
	}
	
}
