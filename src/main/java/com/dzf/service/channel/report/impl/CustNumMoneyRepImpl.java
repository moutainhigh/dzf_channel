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
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.ICustNumMoneyRep;

@Service("custnummoneyrepser")
public class CustNumMoneyRepImpl implements ICustNumMoneyRep {

    @Autowired
    private SingleObjectBO singleObjectBO;
    
	@Override
	public List<CustNumMoneyRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<CustNumMoneyRepVO> retlist = new ArrayList<CustNumMoneyRepVO>();
		List<String> corplist = new ArrayList<String>();
		List<String> countcorplist = new ArrayList<String>();
		CustNumMoneyRepVO retvo = null;
		CustNumMoneyRepVO countvo = null;
		//1、查询存量客户数量
		List<CustCountVO> stocklist = queryCustNum(paramvo,1);
		//2、计算存量客户分类：小规模、一般纳税人
		Map<String, CustNumMoneyRepVO> stockmap = countCustNumByType(stocklist, 1, corplist, countcorplist);
		//3、查询存量客户的合同数量
		List<CustCountVO> stockcontlist = queryContNum(paramvo, countcorplist, 1);
		//4、计算存量客户合同分类：小规模、一般纳税人
		Map<String, CustNumMoneyRepVO> stockcontmap = countContNumByType(stockcontlist, 1);
		
		//5、查询新增客户数量
		List<CustCountVO> newlist = queryCustNum(paramvo,2);
		//6、计算新增客户分类：小规模、一般纳税人
		Map<String, CustNumMoneyRepVO> newmap = countCustNumByType(newlist, 2, corplist, countcorplist);
		//7、计算新增客户的合同数量
		List<CustCountVO> newcontlist = queryContNum(paramvo, countcorplist, 2);
		//8、计算新增客户分类：小规模、一般纳税人
		Map<String, CustNumMoneyRepVO> newcontmap = countContNumByType(newcontlist, 2);
		
		
		if(corplist != null && corplist.size() > 0){
			CorpVO corpvo = null;
			for(String pk_corp : corplist){
				retvo = new CustNumMoneyRepVO();
				retvo.setPk_corp(pk_corp);
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if(corpvo != null){
					retvo.setVcorpname(corpvo.getUnitname());
					retvo.setVprovince(corpvo.getCitycounty());
				}
				//2.1存量客户数量
				if(stockmap != null && !stockmap.isEmpty()){
					countvo = stockmap.get(pk_corp);
					if(countvo != null){
						retvo.setIstockcustsmall(countvo.getIstockcustsmall());
						retvo.setIstockcusttaxpay(countvo.getIstockcusttaxpay());
					}
				}
				//2.2存量客户合同数量
				if(stockcontmap != null && !stockcontmap.isEmpty()){
					countvo = stockcontmap.get(pk_corp);
					if(countvo != null){
						retvo.setIstockcontsmall(countvo.getIstockcontsmall());
						retvo.setIstockconttaxpay(countvo.getIstockconttaxpay());
					}
				}
				//2.3新增客户数量
				if(newmap != null && !newmap.isEmpty()){
					countvo = newmap.get(pk_corp);
					if(countvo != null){
						retvo.setInewcustsmall(countvo.getInewcustsmall());
						retvo.setInewcusttaxpay(countvo.getInewcusttaxpay());
					}
				}
				//2.4新增客户合同数量
				if(newcontmap != null && !newcontmap.isEmpty()){
					countvo = newcontmap.get(pk_corp);
					if(countvo != null){
						retvo.setInewcontsmall(countvo.getInewcontsmall());
						retvo.setInewconttaxpay(countvo.getInewconttaxpay());
					}
				}
				//2.5续费客户数量
				
				//2.6续费客户合同数量
				
				//2.7新增客户增长率
				retvo.setInewcustratesmall(getCustRate(retvo.getInewcustsmall(), retvo.getIstockcustsmall()));
				retvo.setInewcustratetaxpay(getCustRate(retvo.getInewcusttaxpay(), retvo.getIstockcusttaxpay()));
				
				//2.8新增合同增长率
				retvo.setInewcontratesmall(getContRate(retvo.getInewcontsmall(), retvo.getIstockcontsmall()));
				retvo.setInewcontratetaxpay(getContRate(retvo.getInewconttaxpay(), retvo.getIstockconttaxpay()));
				
				retlist.add(retvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 整数类型增长率计算方法
	 * @param num1
	 * @param num2
	 * @return
	 */
	@Override
	public DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		DZFDouble num = num4.sub(num3);
		return num3.div(num).multiply(100);
	}
	
	/**
	 * DZFDouble类型增长率计算方法
	 * @param num1
	 * @param num2
	 * @return
	 */
	private DZFDouble getContRate(DZFDouble num1, DZFDouble num2) throws DZFWarpException {
		num1 = num1 == null ? DZFDouble.ZERO_DBL : num1;
		num2 = num2 == null ? DZFDouble.ZERO_DBL : num2;
		DZFDouble num = num2.sub(num1);
		return num1.div(num).multiply(100);
	}
	
	/**
	 * 查询客户数量
	 * @param paramvo
	 * @param qrytype   1:存量客户查询；2：新增客户查询；
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CustCountVO> queryCustNum(QryParamVO paramvo, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.fathercorp as pk_corp, \n");
		sql.append("	p.chargedeptname as chargedeptname, count(p.pk_corp) as num \n");
		sql.append("  FROM bd_corp p \n");
		sql.append("  LEFT JOIN ynt_franchisee t ON p.fathercorp = t.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.isreport, 'N') = 'Y' \n");//授权会计公司
//		sql.append("   AND nvl(t.ischannel, 'N') = 'Y' \n");
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");
		if(qrytype == 1){
			sql.append("   AND p.createdate <= ? \n");
			spm.addParam(paramvo.getBegdate());
		}else if(qrytype == 2){
			sql.append("   AND p.createdate <= ? \n");
			spm.addParam(paramvo.getBegdate());
			sql.append("   AND p.createdate >= ? \n");
			DZFDate date = new DZFDate();
			spm.addParam(date.getYear() + "-" + date.getStrMonth() + "-01");
		}
		sql.append(" GROUP BY (p.fathercorp, p.chargedeptname)");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}
	
	/**
	 * 计算客户分类信息
	 * @param stocklist
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public Map<String, CustNumMoneyRepVO> countCustNumByType(List<CustCountVO> custlist, Integer qrytype,
			List<String> corplist, List<String> countcorplist) throws DZFWarpException {
		Map<String, CustNumMoneyRepVO> custmap = new HashMap<String, CustNumMoneyRepVO>();
		CustNumMoneyRepVO countvo = null;
		countcorplist.clear();
		for (CustCountVO vo : custlist) {
			if(!corplist.contains(vo.getPk_corp())){
				corplist.add(vo.getPk_corp());
			}
			if(!countcorplist.contains(vo.getPk_corp())){
				countcorplist.add(vo.getPk_corp());
			}
			if (!custmap.containsKey(vo.getPk_corp())) {
				countvo = new CustNumMoneyRepVO();
				countvo.setPk_corp(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						countvo.setIstockcustsmall(vo.getNum());
					} else if (qrytype == 2) {
						countvo.setInewcustsmall(vo.getNum());
					}
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						countvo.setIstockcusttaxpay(vo.getNum());
					} else if (qrytype == 2) {
						countvo.setInewcusttaxpay(vo.getNum());
					}
				}
				custmap.put(vo.getPk_corp(), countvo);
			} else {
				countvo = custmap.get(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						countvo.setIstockcustsmall(ToolsUtil.addInteger(countvo.getIstockcustsmall(), vo.getNum()));
					} else if (qrytype == 2) {
						countvo.setInewcustsmall(ToolsUtil.addInteger(countvo.getIstockcustsmall(), vo.getNum()));
					}
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						countvo.setIstockcusttaxpay(ToolsUtil.addInteger(countvo.getIstockcusttaxpay(), vo.getNum()));
					} else if (qrytype == 2) {
						countvo.setInewcusttaxpay(ToolsUtil.addInteger(countvo.getIstockcusttaxpay(), vo.getNum()));
					}
				}
			}
		}
		return custmap;
	}
	
	/**
	 * 查询客户合同信息
	 * @param paramvo
	 * @param corplist
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> queryContNum(QryParamVO paramvo, List<String> countcorplist, Integer qrytype)throws DZFWarpException {
		if(countcorplist == null || countcorplist.size() == 0){
			return new ArrayList<CustCountVO>();
		}
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp as pk_corp, \n") ;
		sql.append("       t.chargedeptname as chargedeptname, \n") ; 
		sql.append("       sum(t.ntotalmny) as sum \n") ; 
		sql.append("  FROM cn_contract t \n") ; 
		sql.append("  LEFT JOIN bd_corp p ON t.pk_corpk = p.pk_corp \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(p.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(p.isseal, 'N') = 'N' \n") ; 
		String where = SqlUtil.buildSqlForIn("t.pk_corp", countcorplist.toArray(new String[0]));
		sql.append(" AND ").append(where);
		if(qrytype == 1){
			sql.append("   AND p.createdate <= ? \n");
			spm.addParam(paramvo.getBegdate());
		}else if(qrytype == 2){
			sql.append("   AND p.createdate <= ? \n");
			spm.addParam(paramvo.getBegdate());
			sql.append("   AND p.createdate >= ? \n");
			DZFDate date = new DZFDate();
			spm.addParam(date.getYear() + "-" + date.getStrMonth() + "-01");
		}
		sql.append(" GROUP BY (t.pk_corp, t.chargedeptname)");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,	new BeanListProcessor(CustCountVO.class));
	}
	
	/**
	 * 计算客户合同分类信息
	 * @param contlist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, CustNumMoneyRepVO> countContNumByType(List<CustCountVO> contlist, Integer qrytype)
			throws DZFWarpException {
		Map<String, CustNumMoneyRepVO> map = new HashMap<String, CustNumMoneyRepVO>();
		CustNumMoneyRepVO custnumvo = null;
		for (CustCountVO vo : contlist) {
			if (!map.containsKey(vo.getPk_corp())) {
				custnumvo = new CustNumMoneyRepVO();
				custnumvo.setPk_corp(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						custnumvo.setIstockcontsmall(vo.getSum());
					} else if (qrytype == 2) {
						custnumvo.setInewcontsmall(vo.getSum());
					}
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						custnumvo.setIstockconttaxpay(vo.getSum());
					} else if (qrytype == 2) {
						custnumvo.setInewconttaxpay(vo.getSum());
					}
				}
				map.put(vo.getPk_corp(), custnumvo);
			} else {
				custnumvo = map.get(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						custnumvo.setIstockcontsmall(SafeCompute.add(custnumvo.getIstockcontsmall(), vo.getSum()));
					} else if (qrytype == 2) {
						custnumvo.setInewcontsmall(SafeCompute.add(custnumvo.getIstockcontsmall(), vo.getSum()));
					}
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						custnumvo.setIstockconttaxpay(SafeCompute.add(custnumvo.getIstockconttaxpay(), vo.getSum()));
					} else if (qrytype == 2) {
						custnumvo.setInewconttaxpay(SafeCompute.add(custnumvo.getIstockconttaxpay(), vo.getSum()));
					}
				}
			}
		}
		return map;
	}

}
