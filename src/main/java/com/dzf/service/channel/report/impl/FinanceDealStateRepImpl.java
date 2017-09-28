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
import com.dzf.model.channel.report.FinanceDealStateRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.ICustNumMoneyRep;
import com.dzf.service.channel.report.IFinanceDealStateRep;

@Service("financedealstaterepser")
public class FinanceDealStateRepImpl implements IFinanceDealStateRep{
	
	@Autowired
	private ICustNumMoneyRep custServ;
	
    @Autowired
    private SingleObjectBO singleObjectBO;

	@Override
	public List<FinanceDealStateRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<FinanceDealStateRepVO> retlist = new ArrayList<FinanceDealStateRepVO>();
		List<String> corplist = new ArrayList<String>();
		List<String> countcorplist = new ArrayList<String>();
		List<CustCountVO> custlist = custServ.queryCustNum(paramvo, 1);
		Map<String, CustNumMoneyRepVO> custmap = custServ.countCustNumByType(custlist, 1, corplist, countcorplist);
		CustNumMoneyRepVO custnumvo = null;
		FinanceDealStateRepVO retvo = null;
		Map<String, Map<String, CustCountVO>> retmap = queryVoucher(countcorplist);
		Map<String, CustCountVO> voumap = null;
		CustCountVO countvo = null;
		if(corplist != null && corplist.size() > 0){
			retvo = new FinanceDealStateRepVO();
			CorpVO corpvo = null;
			for(String pk_corp : corplist){
				retvo.setPk_corp(pk_corp);
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if(corpvo != null){
					retvo.setVcorpname(corpvo.getUnitname());
					retvo.setVprovince(corpvo.getCitycounty());
				}
				custnumvo = custmap.get(pk_corp);
				if(custnumvo != null){
					retvo.setIcustsmall(custnumvo.getIstockcustsmall());
					retvo.setIcusttaxpay(custnumvo.getIstockcusttaxpay());
				}
				retvo.setIcustratesmall(getCustRate(retvo.getIcustsmall(), retvo.getIcusttaxpay()));
				retvo.setIcustratetaxpay(getCustRate(retvo.getIcusttaxpay(), retvo.getIcustsmall()));
				voumap =  retmap.get(pk_corp);
				if(voumap != null){
					countvo = voumap.get("小规模纳税人");
					if(countvo != null){
						retvo.setIvouchernummall(countvo.getNum());
					}
					countvo = voumap.get("一般纳税人");
					if(countvo != null){
						retvo.setIvouchernumtaxpay(countvo.getNum());
					}
				}
				
				retlist.add(retvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 客户占比计算
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		DZFDouble num = num4.add(num3);
		return num3.div(num).multiply(100);
	}

	/**
	 * 加盟商凭证计算
	 * @param countcorplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, CustCountVO>> queryVoucher(List<String> countcorplist) throws DZFWarpException {
		Map<String, Map<String, CustCountVO>> retmap = new HashMap<String, Map<String, CustCountVO>>();
		Map<String, CustCountVO> voumap = null;
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.fathercorp as pk_corp, p.chargedeptname, count(DISTINCT h.pk_tzpz_h) as num \n");
		sql.append("  FROM ynt_tzpz_h h \n");
		sql.append("  LEFT JOIN bd_corp p ON h.pk_corp = p.pk_corp \n");
		sql.append(" WHERE nvl(h.dr, 0) = 0 \n");
		sql.append("   AND nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");
		String where = SqlUtil.buildSqlForIn("p.fathercorp", countcorplist.toArray(new String[0]));
		sql.append(" AND ").append(where);
		sql.append(" GROUP BY (p.chargedeptname, p.fathercorp)");
		List<CustCountVO> vouchlist = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if(vouchlist != null && vouchlist.size() > 0){
			for(CustCountVO vo : vouchlist){
				if(!StringUtil.isEmpty(vo.getPk_corp())){
					if(!retmap.containsKey(vo.getPk_corp())){
						voumap = new HashMap<String, CustCountVO>();
						if(!StringUtil.isEmpty(vo.getChargedeptname())){
							voumap.put(vo.getChargedeptname(), vo);
						}
						retmap.put(vo.getPk_corp(), voumap);
					}else{
						voumap = retmap.get(vo.getPk_corp());
						if(!StringUtil.isEmpty(vo.getChargedeptname())){
							voumap.put(vo.getChargedeptname(), vo);
						}
					}
				}
			}
		}
		return retmap;
	}
	
}
