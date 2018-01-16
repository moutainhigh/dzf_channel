package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.channel.payment.ChnBalanceRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.IChnPayBalanceService;

@Service("chnpaybalanceser")
public class ChnPayBalanceServiceImpl implements IChnPayBalanceService{
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public List<ChnBalanceRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<ChnBalanceRepVO> retlist = new ArrayList<ChnBalanceRepVO>();
		List<String> pklist = new ArrayList<String>();
		Map<String,ChnBalanceRepVO> initmap = qryDataMap(paramvo, pklist, 1);
		Map<String,ChnBalanceRepVO> datamap = qryDataMap(paramvo, pklist, 2);
		if(pklist != null && pklist.size() > 0){
			ChnBalanceRepVO repvo = null;
			ChnBalanceRepVO vo = null;
			String pk_corp = "";
			CorpVO accvo = null;
			String ipaytype = null;
			Integer paytype = null;
			for(String pk : pklist){
				repvo = new ChnBalanceRepVO();
				ipaytype = pk.substring(pk.indexOf(",")+1);  
				pk_corp = pk.substring(0, pk.indexOf(","));
				repvo.setPk_corp(pk_corp);
				accvo = CorpCache.getInstance().get(null, pk_corp);
				if(accvo != null){
					repvo.setCorpname(accvo.getUnitname());
					repvo.setInnercode(accvo.getInnercode());
				}
				if (!StringUtil.isEmpty(paramvo.getCorpname())) {
					if (repvo.getInnercode().indexOf(paramvo.getCorpname())== -1
							&& repvo.getCorpname().indexOf(paramvo.getCorpname()) == -1) {
						continue;
					}
				}
				paytype = Integer.parseInt(ipaytype);
				repvo.setIpaytype(paytype);
				if(paytype == 1){
					repvo.setVpaytypename("保证金");
				}else if(paytype == 2){
					repvo.setVpaytypename("预付款");
				}
				if(initmap != null && !initmap.isEmpty()){
					vo = initmap.get(pk);
					if(vo != null){
						if(paytype == 1){
							repvo.setInitbalance(vo.getBail());
						}else if(paytype == 2){
							repvo.setInitbalance(vo.getCharge());
						}
					}
				}
				if(datamap != null && !datamap.isEmpty()){
					vo = datamap.get(pk);
					if(vo != null){
						if(paytype == 1){
							repvo.setNpaymny(vo.getBail());
						}else if(paytype == 2){
							repvo.setNpaymny(vo.getNpaymny());
							repvo.setNusedmny(vo.getNusedmny());
						}
					}
				}
				countBalance(repvo);
				retlist.add(repvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 计算余额
	 * @param repvo
	 */
	private void countBalance(ChnBalanceRepVO repvo){
		DZFDouble paymny = SafeCompute.add(repvo.getInitbalance(), repvo.getNpaymny());
		DZFDouble balance = SafeCompute.sub(paymny, repvo.getNusedmny());
		repvo.setNbalance(balance);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,ChnBalanceRepVO> qryDataMap(QryParamVO paramvo, List<String> pklist, Integer qrytype) throws DZFWarpException {
		Map<String,ChnBalanceRepVO> map = new HashMap<String,ChnBalanceRepVO>();
		QrySqlSpmVO qryvo = null;
		if(qrytype == 1){
			qryvo = getInitQryParam(paramvo);
		}else if(qrytype == 2){
			qryvo = getQryParam(paramvo);
		}
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnBalanceRepVO.class));
		if(list != null && list.size() > 0){
			String pk = "";
			for(ChnBalanceRepVO repvo : list){
				pk = repvo.getPk_corp() + "," + repvo.getIpaytype();
				if(!pklist.contains(pk)){
					pklist.add(pk);
				}
				map.put(pk, repvo);
			}
		}
		return map;
	}

	/**
	 * 获取期初数据查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getInitQryParam(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_corp, \n") ;
		sql.append("       ipaytype, \n") ; 
		sql.append("       SUM(decode(ipaytype, 1, npaymny, 0)) AS bail, \n") ; 
		sql.append("       SUM(decode(ipaytype, 2, npaymny, 0)) - \n") ; 
		sql.append("       SUM(decode(ipaytype, 2, nusedmny, 0)) AS charge \n") ; 
		sql.append("  FROM cn_detail \n") ; 
		sql.append(" WHERE nvl(dr, 0) = 0 \n") ; 
		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
	        sql.append(" and pk_corp  in (" + corpIdS + ")");
	    }
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(doperatedate,1,7) < ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND doperatedate < ? \n");
				spm.addParam(paramvo.getBegdate());
			}
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
			sql.append(" AND pk_corp = ? \n");
			spm.addParam(paramvo.getPk_corp());
		}
		sql.append(" GROUP BY pk_corp, ipaytype \n");
		sql.append(" ORDER BY pk_corp \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 获取流水数据查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getQryParam(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_corp, \n") ;
		sql.append("       ipaytype, \n") ; 
		sql.append("       SUM(decode(ipaytype, 1, npaymny, 0)) AS bail, \n") ; 
		sql.append("       SUM(decode(ipaytype, 2, npaymny, 0)) AS npaymny, \n") ; 
		sql.append("       SUM(decode(ipaytype, 2, nusedmny, 0)) AS nusedmny \n") ; 
		sql.append("  FROM cn_detail \n") ; 
		sql.append(" WHERE nvl(dr, 0) = 0 \n") ; 
		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
	        sql.append(" and  pk_corp  in (" + corpIdS + ")");
	    }
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(doperatedate,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(doperatedate,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND doperatedate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND doperatedate <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" GROUP BY pk_corp, ipaytype \n");
		sql.append(" ORDER BY pk_corp \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnDetailVO> queryDetail(QryParamVO paramvo) throws DZFWarpException {
		List<ChnDetailVO> retlist = new ArrayList<ChnDetailVO>();
		List<String> pklist = new ArrayList<String>();
		Map<String,ChnBalanceRepVO> initmap = qryDataMap(paramvo, pklist, 1);
		DZFDouble coutbal = DZFDouble.ZERO_DBL;
		if(initmap != null && !initmap.isEmpty()){
			ChnBalanceRepVO repvo = initmap.get(paramvo.getPk_corp() + "," + paramvo.getQrytype());
			if(repvo != null){
				ChnDetailVO initvo = new ChnDetailVO();
				if(!StringUtil.isEmpty(paramvo.getPeriod())){
					initvo.setDoperatedate(new DZFDate(paramvo.getBeginperiod()+"-01"));
				}else{
					initvo.setDoperatedate(paramvo.getBegdate());
				}
				initvo.setPk_corp(paramvo.getPk_corp());
				CorpVO accvo = CorpCache.getInstance().get(null, paramvo.getPk_corp());
				if(accvo != null){
					initvo.setCorpname(accvo.getUnitname());
				}
				initvo.setVmemo("期初余额");
				if(paramvo.getQrytype() == 1){
					initvo.setNbalance(repvo.getBail());
					coutbal = repvo.getBail();
					initvo.setVpaytypename("保证金");
				}else if(paramvo.getQrytype() == 2){
					initvo.setNbalance(repvo.getCharge());
					coutbal = repvo.getCharge();
					initvo.setVpaytypename("预付款");
				}
				retlist.add(initvo);
			}
		}
		
		QrySqlSpmVO sqpvo =  getDetailQry(paramvo);
		List<ChnDetailVO> list = (List<ChnDetailVO>) singleObjectBO.executeQuery(sqpvo.getSql(), 
				sqpvo.getSpm(), new BeanListProcessor(ChnDetailVO.class));
		if(list != null && list.size() > 0){
			List<ChnDetailVO> oederlist = getOrderList(list);
			CorpVO accvo = null;
			DZFDouble balance = DZFDouble.ZERO_DBL;
			for(ChnDetailVO vo : oederlist){
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(accvo != null){
					vo.setCorpname(accvo.getUnitname());
				}
				if(vo.getIpaytype() != null){
					switch (vo.getIpaytype()){
					case 1:
						vo.setVpaytypename("保证金");
						break;
					case 2:
						vo.setVpaytypename("预付款");
						break;
					}
				}
				balance = SafeCompute.sub(vo.getNpaymny(), vo.getNusedmny());
				vo.setNbalance(SafeCompute.add(coutbal, balance));
				coutbal = vo.getNbalance();
				retlist.add(vo);
			}
//			return oederlist;
		}
		return retlist;
	}
	
	/**
	 * 同一天的数据，按照收款在前，付款在后排列
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<ChnDetailVO> getOrderList(List<ChnDetailVO> list) throws DZFWarpException{
		List<ChnDetailVO> relist = new ArrayList<ChnDetailVO> ();
		Map<DZFDate,List<ChnDetailVO>> map = new HashMap<DZFDate,List<ChnDetailVO>>();
		List<ChnDetailVO> newlist = null;
		List<ChnDetailVO> oldlist = null;
		List<DZFDate> keylist = new ArrayList<DZFDate>();
		for(ChnDetailVO vo : list){
			if(!map.containsKey(vo.getDoperatedate())){
				newlist = new ArrayList<ChnDetailVO>();
				newlist.add(vo);
				map.put(vo.getDoperatedate(), newlist);
				keylist.add(vo.getDoperatedate());
			}else{
				oldlist = map.get(vo.getDoperatedate());
				oldlist.add(vo);
			}
		}
		for(DZFDate key : keylist){
			newlist = map.get(key);
			Collections.sort(newlist,new Comparator<ChnDetailVO>() {

				@Override
				public int compare(ChnDetailVO o1, ChnDetailVO o2) {
					return o1.getIopertype().compareTo(o2.getIopertype());
				}
				
			});
			for(ChnDetailVO vo : newlist){
				relist.add(vo);
			}
		}
		return relist;
	}
	
	/**
	 * 获取明细查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getDetailQry(QryParamVO paramvo) throws DZFWarpException{
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT * FROM cn_detail WHERE nvl(dr,0) = 0 and pk_corp=? \n");
		spm.addParam(paramvo.getPk_corp());
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(doperatedate,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(doperatedate,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND doperatedate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND doperatedate <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by ts asc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
}
