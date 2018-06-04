package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import com.dzf.model.pub.IStatusConstant;
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
		Map<String, ChnBalanceRepVO> initmap = qryDataMap(paramvo, pklist, 1);// 查询期初余额
		Map<String, ChnBalanceRepVO> datamap = qryDataMap(paramvo, pklist, 2);// 查询明细金额
		if(paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2)){
			qryNoDeductConData(paramvo, pklist);
		}
		HashMap<String, ChnBalanceRepVO> map = new HashMap<String, ChnBalanceRepVO>();
		if (pklist != null && pklist.size() > 0) {
			if (paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2)) {
				map = queryConList(paramvo);
			}
			ChnBalanceRepVO repvo = null;
			ChnBalanceRepVO vo = null;
			String pk_corp = "";
			CorpVO accvo = null;
			String ipaytype = null;
			Integer paytype = null;
			for (String pk : pklist) {
				repvo = new ChnBalanceRepVO();
				ipaytype = pk.substring(pk.indexOf(",") + 1);
				pk_corp = pk.substring(0, pk.indexOf(","));
				repvo.setPk_corp(pk_corp);
				accvo = CorpCache.getInstance().get(null, pk_corp);
				if (accvo != null) {
					repvo.setCorpname(accvo.getUnitname());
					repvo.setInnercode(accvo.getInnercode());
				}
				if (!StringUtil.isEmpty(paramvo.getCorpname())) {
					if (repvo.getInnercode().indexOf(paramvo.getCorpname()) == -1
							&& repvo.getCorpname().indexOf(paramvo.getCorpname()) == -1) {
						continue;
					}
				}
				paytype = Integer.parseInt(ipaytype);
				repvo.setIpaytype(paytype);
				if (paytype == 1) {
					repvo.setVpaytypename("保证金");
				} else if (paytype == 2) {
					repvo.setVpaytypename("预付款");
				} else if (paytype == 3) {
					repvo.setVpaytypename("返点");
				}
				if (map != null && !map.isEmpty() && paytype == 2 && map.containsKey(repvo.getPk_corp())) {
					ChnBalanceRepVO balanvo = map.get(repvo.getPk_corp());
					repvo.setNum(balanvo.getNum());
					repvo.setNaccountmny(balanvo.getNaccountmny());
					repvo.setNbookmny(balanvo.getNbookmny());
				}
				if (initmap != null && !initmap.isEmpty()) {
					vo = initmap.get(pk);
					if (vo != null) {
						if (paytype == 1) {
							repvo.setInitbalance(vo.getBail());
						} else if (paytype == 2) {
							repvo.setInitbalance(vo.getCharge());
						} else if (paytype == 3) {
							repvo.setInitbalance(vo.getRebate());
						}
					}
				}
				if (datamap != null && !datamap.isEmpty()) {
					vo = datamap.get(pk);
					if (vo != null) {
						if (paytype == 1) {
							repvo.setNpaymny(vo.getBail());
						} else if (paytype == 2) {
							repvo.setNpaymny(vo.getNpaymny());
							repvo.setNusedmny(vo.getNusedmny());
							repvo.setIdeductpropor(vo.getIdeductpropor());
						} else if (paytype == 3) {
							repvo.setNpaymny(vo.getNpaymny());
							repvo.setNusedmny(vo.getNusedmny());
							repvo.setIdeductpropor(vo.getIdeductpropor());
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
	 * 查询零扣款合同的加盟商信息(既没有保证金，也没有预付款)
	 * @param paramvo
	 * @param pklist
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void qryNoDeductConData(QryParamVO paramvo,List<String> pklist) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT t.pk_corp  \n") ;
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(t.ndedsummny, 0) = 0  \n") ; 
		sql.append("   AND t.vstatus = 1  \n") ; 
		sql.append("   AND t.pk_confrim NOT IN (SELECT l.pk_bill  \n") ; 
		sql.append("                              FROM cn_detail l  \n") ; 
		sql.append("                             WHERE nvl(l.dr, 0) = 0  \n") ; 
		sql.append("                               AND l.ipaytype = 2) \n");
		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
	        sql.append(" and  t.pk_corp  in (" + corpIdS + ")");
	    }
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(t.deductdata,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(t.deductdata,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND deductdata <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnBalanceRepVO.class));
		if(list != null && list.size() > 0){
			String pk = "";
			for(ChnBalanceRepVO repvo : list){
				pk = repvo.getPk_corp() + ",2";
				if(!pklist.contains(pk)){
					pklist.add(pk);
				}
			}
		}
	}
	
	/**
	 * 查询付款单余额列表上的合同相关数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String,ChnBalanceRepVO> queryConList(QryParamVO paramvo) throws DZFWarpException {
		SQLParameter spm=new SQLParameter();
		StringBuffer buf=new StringBuffer();
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			DZFDate start=new DZFDate(paramvo.getBeginperiod()+"-01");
			DZFDate end=new DZFDate(paramvo.getEndperiod()+"-01");
			Calendar cal =Calendar.getInstance();
			cal.setTime(new Date(end.getMillis()));
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DATE, -1);
			end=new DZFDate(cal.getTime());
			for(int i=0;i<3*3+1;i++){
				spm.addParam(start);
				spm.addParam(end);
			}
		}else{
			for(int i=0;i<3*3+1;i++){
				spm.addParam(paramvo.getBegdate());
				spm.addParam(paramvo.getEnddate());
			}
		}
		
		buf=new StringBuffer();//提单量,合同代账费,账本费
		buf.append("  select t.pk_corp,");
		buf.append("  sum(decode((sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,1))+");
		buf.append("  sum(decode(t.vdeductstatus,10," );
		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,-1),");
		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,0)))");
		buf.append("  -sum (decode(ct.patchstatus,2,decode((sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd'))*");
		buf.append("   sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd'))),1,0,1),0)");
		buf.append("  )as num,");
		
		buf.append("  sum(decode((sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(ct.ntotalmny,0)-nvl(ct.nbookmny,0)))+");
		buf.append("  sum(decode(t.vdeductstatus,10," );
		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(t.nsubtotalmny,0)+nvl(ct.nbookmny,0)),");
		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(t.nsubtotalmny,0))");
		buf.append("  ))as naccountmny,");
		
		buf.append("  sum(decode((sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(ct.nbookmny,0)))+");
		buf.append("  sum(decode(t.vdeductstatus,10," );
		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,-nvl(ct.nbookmny,0)),");
		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,0)");
		buf.append("  ))as nbookmny");
		
		buf.append("  from cn_contract t INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract ");
		buf.append("  where nvl(t.dr,0) = 0 and nvl(ct.dr,0) = 0 and (t.vdeductstatus=1 or t.vdeductstatus=9 or t.vdeductstatus=10) ");
		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
	        buf.append(" and  t.pk_corp  in (" + corpIdS + ")");
	    }
		buf.append("  group by t.pk_corp");
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(buf.toString(), spm,
				new BeanListProcessor(ChnBalanceRepVO.class));
		HashMap<String, ChnBalanceRepVO> map = new HashMap<>();
		for (ChnBalanceRepVO chnBalanceRepVO : list) {
			map.put(chnBalanceRepVO.getPk_corp(), chnBalanceRepVO);
		}
		return map;
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
	
	/**
	 * 查询金额
	 * @param paramvo
	 * @param pklist  
	 * @param qrytype  1：查询期初余额；2：查询明细金额；
	 * @return
	 * @throws DZFWarpException
	 */
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
		sql.append("       SUM(decode(ipaytype, 1, nvl(npaymny,0), 0)) AS bail, \n") ; 
		sql.append("       SUM(decode(ipaytype, 2, nvl(npaymny,0), 0)) - \n") ; 
		sql.append("       SUM(decode(ipaytype, 2, nvl(nusedmny,0), 0)) AS charge, \n") ; 
		sql.append("       SUM(decode(ipaytype, 3, nvl(npaymny,0), 0)) - \n") ; 
		sql.append("       SUM(decode(ipaytype, 3, nvl(nusedmny,0), 0)) AS rebate \n") ; 
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
		sql.append("       SUM(decode(ipaytype, 1, nvl(npaymny,0), 0)) AS bail, \n") ; 
		sql.append("       SUM(decode(ipaytype, 2, nvl(npaymny,0) ,3 , nvl(npaymny,0),0)) AS npaymny, \n") ; 
		sql.append("       SUM(decode(ipaytype, 2, nvl(nusedmny,0),3 , nvl(nusedmny,0),0)) AS nusedmny, \n") ; 
		sql.append("       MIN(CASE ideductpropor WHEN 0 THEN NULL ELSE ideductpropor END) AS ideductpropor \n") ; 
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
		Map<String, ChnBalanceRepVO> initmap = qryDataMap(paramvo, pklist, 1);
		DZFDouble coutbal = DZFDouble.ZERO_DBL;
		if (initmap != null && !initmap.isEmpty()) {
			ChnBalanceRepVO repvo = initmap.get(paramvo.getPk_corp() + "," + paramvo.getQrytype());
			if (repvo != null) {
				ChnDetailVO initvo = new ChnDetailVO();
				if (!StringUtil.isEmpty(paramvo.getPeriod())) {
					initvo.setDoperatedate(new DZFDate(paramvo.getBeginperiod() + "-01"));
				} else {
					initvo.setDoperatedate(paramvo.getBegdate());
				}
				initvo.setPk_corp(paramvo.getPk_corp());
				CorpVO accvo = CorpCache.getInstance().get(null, paramvo.getPk_corp());
				if (accvo != null) {
					initvo.setCorpname(accvo.getUnitname());
				}
				initvo.setVmemo("期初余额");
				if (paramvo.getQrytype() == 1) {
					initvo.setNbalance(repvo.getBail());
					coutbal = repvo.getBail();
					initvo.setVpaytypename("保证金");
				} else if (paramvo.getQrytype() == 2) {
					initvo.setNbalance(repvo.getCharge());
					coutbal = repvo.getCharge();
					initvo.setVpaytypename("预付款");
				} else if (paramvo.getQrytype() == 3) {
					initvo.setNbalance(repvo.getRebate());
					coutbal = repvo.getRebate();
					initvo.setVpaytypename("返点");
				}
				retlist.add(initvo);
			}
		}

		QrySqlSpmVO sqpvo = getDetailQry(paramvo);
		List<ChnDetailVO> list = (List<ChnDetailVO>) singleObjectBO.executeQuery(sqpvo.getSql(), sqpvo.getSpm(),
				new BeanListProcessor(ChnDetailVO.class));
		if (paramvo.getQrytype() == 2) {
			list = qryNoDeduction(list, paramvo);// 查询扣费为0的数据
		}
		if (list != null && list.size() > 0) {
			List<ChnDetailVO> oederlist = getOrderList(list);
			CorpVO accvo = null;
			DZFDouble balance = DZFDouble.ZERO_DBL;
			HashMap<String, ChnDetailVO> map = new HashMap<>();
			if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {
				map = queryConDetail(paramvo);
			}
			for (ChnDetailVO vo : oederlist) {
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if (accvo != null) {
					vo.setCorpname(accvo.getUnitname());
				}
				if (vo.getIpaytype() != null) {
					switch (vo.getIpaytype()) {
					case 1:
						vo.setVpaytypename("保证金");
						break;
					case 2:
						vo.setVpaytypename("预付款");
						break;
					case 3:
						vo.setVpaytypename("返点");
						break;
					}
				}
				if (map != null && !map.isEmpty() && vo.getNusedmny() != null) {
					String key = "1" + vo.getPk_bill();
					if (vo.getNusedmny().equals(DZFDouble.ZERO_DBL) && vo.getDr() == -1) {
						key = "-1" + vo.getPk_bill();
					}
					if (vo.getNusedmny().toDouble() < 0) {
						key = "-1" + vo.getPk_bill();
					}
					if (map.containsKey(key)) {
						ChnDetailVO chnDetailVO = map.get(key);
						vo.setNaccountmny(chnDetailVO.getNaccountmny());
						vo.setNbookmny(chnDetailVO.getNbookmny());
					}
				}
				balance = SafeCompute.sub(vo.getNpaymny(), vo.getNusedmny());
				vo.setNbalance(SafeCompute.add(coutbal, balance));
				coutbal = vo.getNbalance();
				retlist.add(vo);
			}
			// return oederlist;
		}
		return retlist;
	}
	
	/**
	 * 查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @return
	 */
	private List<ChnDetailVO> qryNoDeduction(List<ChnDetailVO> list, QryParamVO paramvo) {
		StringBuffer ids=new StringBuffer();
		for (ChnDetailVO chnDetailVO : list) {
			ids.append("'"+chnDetailVO.getPk_bill()+"',");
		}
		List<ChnDetailVO> vos1 = qryByDeduct(list, paramvo, ids);
		CorpVO corpvo= null;
		StringBuffer vmemo=null;
		for (ChnDetailVO chnDetailVO : vos1) {
			corpvo = CorpCache.getInstance().get(null, chnDetailVO.getPk_corp());
			vmemo=new StringBuffer();
			if(!StringUtil.isEmpty(chnDetailVO.getVmemo()) &&chnDetailVO.getVmemo().contains("存量客户")&& corpvo!=null){
				vmemo.append("存量客户:").append(corpvo.getUnitname()).append("、").append(chnDetailVO.getVmemo().substring(5));
			}else if(corpvo!=null){
				vmemo.append(corpvo.getUnitname()).append(chnDetailVO.getVmemo());
			}
			chnDetailVO.setVmemo(vmemo.toString());
			chnDetailVO.setDr(1);
			chnDetailVO.setNusedmny(DZFDouble.ZERO_DBL);
			list.add(chnDetailVO);
		}
		List<ChnDetailVO> vos2 = qryByChange(list, paramvo, ids);
		for (ChnDetailVO chnDetailVO : vos2) {
			corpvo = CorpCache.getInstance().get(null, chnDetailVO.getPk_corp());
			vmemo=new StringBuffer();
			if(chnDetailVO.getDr() != null && chnDetailVO.getDr() == 9){
				vmemo.append("合同终止：");
			}else{
				vmemo.append("合同作废：");
			}
			if(corpvo!=null){
				vmemo.append(corpvo.getUnitname()).append("、").append(chnDetailVO.getVmemo());
			}
			chnDetailVO.setVmemo(vmemo.toString());
			chnDetailVO.setNusedmny(DZFDouble.ZERO_DBL);
			chnDetailVO.setDr(-1);
			list.add(chnDetailVO);
		}
		return list;
	}
	
	/**
	 * 根据变更日期，查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ChnDetailVO> qryByChange(List<ChnDetailVO> list, QryParamVO paramvo, StringBuffer ids) {
		StringBuffer sql;
		SQLParameter spm;
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append(" select t.pk_confrim as pk_bill,substr(t.dchangetime,1,10) as doperatedate,t.pk_corpk as pk_corp," );   
		sql.append(" t.vstatus as dr,2 as ipaytype,2 as iopertype,ct.vcontcode as vmemo from cn_contract t" ); 
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr,0) = 0 and nvl(ct.dr,0) = 0 and t.pk_corp=? and t.ideductpropor=0 and (t.vstatus=10 or t.vstatus=9) \n");
		spm.addParam(paramvo.getPk_corp());
		if(ids != null && ids.length() > 0){
			sql.append(" and t.pk_confrim not in (");
			sql.append(ids.toString().substring(0,ids.toString().length()-1));
			sql.append(" )");
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(t.dchangetime,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(t.dchangetime,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND substr(t.dchangetime,1,10) >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND substr(t.dchangetime,1,10) <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by t.dchangetime \n");
		return (List<ChnDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnDetailVO.class));
	}
	
	/**
	 * 根据扣款日期，查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ChnDetailVO> qryByDeduct(List<ChnDetailVO> list, QryParamVO paramvo, StringBuffer ids) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" select t.pk_confrim as pk_bill,t.deductdata as doperatedate,t.pk_corpk as pk_corp," );   
		sql.append(" 2 as ipaytype,2 as iopertype," );  
		sql.append(" CONCAT(decode(nvl(ct.isncust,'N'),'Y','存量客户:',''),ct.vcontcode) as vmemo from cn_contract t" );   
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr,0) = 0 and nvl(ct.dr,0) = 0 and t.pk_corp=? and t.ideductpropor=0  \n");
		sql.append(" and (t.vstatus=1 or t.vstatus=9 or t.vstatus=10) \n");
		spm.addParam(paramvo.getPk_corp());
		if(ids != null && ids.length() > 0){
			sql.append(" and t.pk_confrim not in (");
			sql.append(ids.toString().substring(0,ids.toString().length()-1));
			sql.append(" )");
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(t.deductdata,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(t.deductdata,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND t.deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND t.deductdata <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by t.deductdata \n");
		return (List<ChnDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnDetailVO.class));
	}
	/**
	 * 查询付款单余额明细的合同相关数据
	 * @param paramvo  
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String,ChnDetailVO> queryConDetail(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			DZFDate start=new DZFDate(paramvo.getBeginperiod()+"-01");
			DZFDate end=new DZFDate(paramvo.getEndperiod()+"-01");
			Calendar cal =Calendar.getInstance();
			cal.setTime(new Date(end.getMillis()));
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DATE, -1);
			end=new DZFDate(cal.getTime());
			sp.addParam(start);
			sp.addParam(end);
		}else{
			sp.addParam(paramvo.getBegdate());
			sp.addParam(paramvo.getEnddate());
		}
		sp.addParam(paramvo.getPk_corp());
		sql.append(" select t.pk_confrim as pk_bill ,t.deductdata as doperatedate, ");
		sql.append(" nvl(ct.ntotalmny,0)-nvl(ct.nbookmny,0) as naccountmny,nvl(ct.nbookmny,0) as nbookmny from cn_contract t" ); 
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" where nvl(t.dr,0) = 0 and nvl(ct.dr,0) = 0 and (t.vdeductstatus=1 or t.vdeductstatus=9 or t.vdeductstatus=10) and " );
		sql.append(" t.deductdata>=? and t.deductdata<=? and t.pk_corp=? " );
		List<ChnDetailVO> qryYSH =(List<ChnDetailVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ChnDetailVO.class));
		
	    sql = new StringBuffer();
	    sql.append(" select t.pk_confrim as pk_bill,substr(t.dchangetime,0,10)as doperatedate, ");
		sql.append(" nvl(t.nsubtotalmny,0) as naccountmny,0 as nbookmny  from cn_contract t " ); 
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" where nvl(t.dr,0) = 0 and nvl(ct.dr,0) = 0 and t.vdeductstatus=9  and" );
		sql.append(" substr(t.dchangetime,0,10)>=? and substr(t.dchangetime,0,10)<=? and t.pk_corp=?" );
		List<ChnDetailVO> qryYZZ =(List<ChnDetailVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ChnDetailVO.class));
		
		sql = new StringBuffer();
	    sql.append(" select t.pk_confrim as pk_bill,substr(t.dchangetime,0,10)as doperatedate, ");
		sql.append(" nvl(t.nsubtotalmny,0)+nvl(ct.nbookmny,0) as naccountmny,-nvl(ct.nbookmny,0) as nbookmny from cn_contract t " );   
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" where nvl(t.dr,0) = 0  and nvl(ct.dr,0) = 0  and t.vdeductstatus=10 and" );
		sql.append(" substr(t.dchangetime,0,10)>=? and substr(t.dchangetime,0,10)<=? and t.pk_corp=?" );
		List<ChnDetailVO> qryYZF =(List<ChnDetailVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ChnDetailVO.class));	
		HashMap<String,ChnDetailVO> map=new HashMap<>();
		for (ChnDetailVO chnDetailVO : qryYSH) {
			String key="1"+chnDetailVO.getPk_bill();
			map.put(key, chnDetailVO);
		}
		for (ChnDetailVO chnDetailVO : qryYZZ) {
			String key="-1"+chnDetailVO.getPk_bill();
			map.put(key, chnDetailVO);
		}
		for (ChnDetailVO chnDetailVO : qryYZF) {
			String key="-1"+chnDetailVO.getPk_bill();
			map.put(key, chnDetailVO);
		}
		return map;
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
				map.put(vo.getDoperatedate(), oldlist);
			}
		}
		Collections.sort(keylist, new Comparator<DZFDate>(){

			@Override
			public int compare(DZFDate o1, DZFDate o2) {
				return o1.compareTo(o2);
			}
			
		});
		for(DZFDate key : keylist){
			newlist = map.get(key);
			Collections.sort(newlist,new Comparator<ChnDetailVO>() {

				@Override
				public int compare(ChnDetailVO o1, ChnDetailVO o2) {
					if(o1.getIpaytype() == IStatusConstant.IPAYTYPE_3){
						return -o1.getIopertype().compareTo(o2.getIopertype());
					}else{
						return o1.getIopertype().compareTo(o2.getIopertype());
					}
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
