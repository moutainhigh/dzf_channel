package com.dzf.service.channel.contract.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.contract.ChangeApplyVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.contract.IContractAuditService;
import com.dzf.service.pub.IPubService;

@Service("contractauditser")
public class ContractAuditServiceImpl implements IContractAuditService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IPubService pubser;

	@Override
	public Integer queryTotalNum(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(pamvo, uservo);
		return multBodyObjectBO.queryDataTotal(ChangeApplyVO.class, qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChangeApplyVO> query(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(pamvo, uservo);
		List<ChangeApplyVO> list = (List<ChangeApplyVO>) multBodyObjectBO.queryDataPage(ChangeApplyVO.class,
				qryvo.getSql(), qryvo.getSpm(), pamvo.getPage(), pamvo.getRows(), "y.applytime");
		if(list != null && list.size() > 0){
			setRefShowName(list);
		}
		return list;
	}
	
	/**
	 * 设置参照显示名称
	 * @param list
	 * @throws DZFWarpException
	 */
	private void setRefShowName(List<ChangeApplyVO> list) throws DZFWarpException {
		Map<String,String> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);//渠道经理
		CorpVO corpvo = null;
		UserVO uservo = null;
		for(ChangeApplyVO vo : list){
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
			if(corpvo != null){
				vo.setCorpkname(corpvo.getUnitname());
			}
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			if(corpvo != null){
				vo.setCorpname(corpvo.getUnitname());
				vo.setVarea(corpvo.getCitycounty());// 地区
			}
			if (marmap != null && !marmap.isEmpty()) {
				String manager = marmap.get(vo.getPk_corp());
				if (!StringUtil.isEmpty(manager)) {
					uservo = UserCache.getInstance().get(manager, null);
					if (uservo != null) {
						vo.setVmanagername(uservo.getUser_name());// 渠道经理
					}
				}
			}
		}
	}
	
	/**
	 * 获取查询条件
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySql(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT  \n") ;
		sql.append("       y.applytime,  \n") ;
		sql.append("       y.ichangetype,  \n") ;
		sql.append("       y.vchangeraeson,  \n") ;
		sql.append("       t.pk_corp,  \n") ; 
		sql.append("       t.pk_corpk,  \n") ; 
		sql.append("       y.iapplystatus,  \n") ; 
		sql.append("       t.vcontcode,  \n") ; 
		sql.append("       t.vbeginperiod,  \n") ; 
		sql.append("       t.vendperiod,  \n") ; 
		sql.append("       t.chargedeptname,  \n") ; 
		sql.append("       t.nmservicemny,  \n") ; 
		sql.append("       t.ireceivcycle,  \n") ; 
		sql.append("       t.icontractcycle,  \n") ; 
		sql.append("       nvl(t.ntotalmny,0) - nvl(t.nbookmny,0) AS naccountmny,  \n") ; 
		sql.append("       t.nbookmny  \n") ; 
		sql.append("  FROM cn_changeapply y  \n") ; 
		sql.append("  LEFT JOIN ynt_contract t ON y.pk_contract = t.pk_contract  \n") ; 
		if(pamvo.getIreceivcycle() == null || (pamvo.getIreceivcycle() != null && pamvo.getIreceivcycle() != -1)){
			sql.append("  LEFT JOIN cn_applyaudit a ON a.pk_changeapply = y.pk_changeapply  \n") ; 
		}
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(y.dr, 0) = 0  \n");
		if (!StringUtil.isEmpty(pamvo.getVmanagername())) {//渠道经理
			String where = getQrySql(pamvo.getVmanagername(), IStatusConstant.IQUDAO);
			if (!StringUtil.isEmpty(where)) {
				sql.append(where);
			}
		}
		if(!StringUtil.isEmpty(pamvo.getVfilepath())){//申请类型
			String[] strs = pamvo.getVfilepath().split(",");
		    String where = SqlUtil.buildSqlForIn("y.ichangetype", strs);
		    sql.append(" AND ").append(where);
		}
		if(pamvo.getIapplystatus() != null && pamvo.getIapplystatus() != -1){
			sql.append(" AND y.iapplystatus = ? \n");
			spm.addParam(pamvo.getIapplystatus());
		}
		if(!StringUtil.isEmpty(pamvo.getChargedeptname())){
			if("1".equals(pamvo.getChargedeptname())){
				sql.append(" AND t.chargedeptname = '小规模纳税人' \n");
			}else if("2".equals(pamvo.getChargedeptname())){
				sql.append(" AND t.chargedeptname = '一般纳税人' \n");
			}
		}
		if(!StringUtil.isEmpty(pamvo.getPk_corp())){//加盟商
			String[] strs = pamvo.getPk_corp().split(",");
		    String where = SqlUtil.buildSqlForIn("t.pk_corp", strs);
		    sql.append(" AND ").append(where);
		}
		if(!StringUtil.isEmpty(pamvo.getPk_corpk())){//客户
			String[] strs = pamvo.getPk_corpk().split(",");
		    String where = SqlUtil.buildSqlForIn("t.pk_corpk", strs);
		    sql.append(" AND ").append(where);
		}

		//数据权限过滤
		sql.append(" AND ( ( ( y.vchannelid = ? OR y.vareaer = ? OR y.vdirector = ? ) \n");
		spm.addParam(uservo.getCuserid());
		spm.addParam(uservo.getCuserid());
		spm.addParam(uservo.getCuserid());
		sql.append(" AND y.iapplystatus IN (1, 2, 3) ) \n");
		sql.append(" OR a.coperatorid = ? ) \n");
		spm.addParam(uservo.getCuserid());
		
		//待审核数据过滤
		if(pamvo.getIreceivcycle() != null && pamvo.getIreceivcycle() != -1){
			sql.append(" AND ( y.vchannelid = ? OR y.vareaer = ? OR y.vdirector = ? ) \n");
			spm.addParam(uservo.getCuserid());
			spm.addParam(uservo.getCuserid());
			spm.addParam(uservo.getCuserid());
			sql.append(" AND y.iapplystatus IN (1, 2, 3) \n");
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 获取数据过滤条件
	 * @param cuserid
	 * @param qrytype  1：渠道经理；2：培训师；3：渠道运营；
	 * @return
	 * @throws DZFWarpException
	 */
	private String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		String[] corps = pubser.getManagerCorp(cuserid, qrytype);
		if(corps != null && corps.length > 0){
			String where = SqlUtil.buildSqlForIn(" t.pk_corp", corps);
			sql.append(" AND ").append(where);
		}else{
			sql.append(" AND t.pk_corp is null \n") ; 
		}
		return sql.toString();
	}

}
