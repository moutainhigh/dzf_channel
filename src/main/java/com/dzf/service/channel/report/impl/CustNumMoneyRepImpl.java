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
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.ICustNumMoneyRep;

@Service("custnummoneyrepser")
public class CustNumMoneyRepImpl implements ICustNumMoneyRep {

    @Autowired
    private SingleObjectBO singleObjectBO;
    
	@SuppressWarnings("unchecked")
	@Override
	public List<CustNumMoneyRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.fathercorp as pk_corp, \n");
		sql.append("	p.chargedeptname as chargedeptname, count(p.pk_corp) as num \n");
		sql.append("  FROM bd_corp p\n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp\n");
		sql.append(" WHERE nvl(p.dr, 0) = 0\n");
		sql.append("   AND nvl(t.dr, 0) = 0\n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'\n");
		sql.append(" GROUP BY (p.fathercorp, p.chargedeptname)");
		List<CustCountVO> stocklist = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		List<CustNumMoneyRepVO> retlist = null;
		List<String> corplist = new ArrayList<String>();
		if(stocklist != null && stocklist.size() > 0){
			retlist = queryStockCustList(stocklist, corplist);
			queryStockContList(retlist, corplist);
		}
		return retlist;
	}
	
	/**
	 * 查询存量客户信息
	 * @param stocklist
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	private List<CustNumMoneyRepVO> queryStockCustList(List<CustCountVO> stocklist, List<String> corplist)
			throws DZFWarpException {
		List<CustNumMoneyRepVO> retlist = new ArrayList<CustNumMoneyRepVO>();
		Map<String, CustNumMoneyRepVO> map = new HashMap<String, CustNumMoneyRepVO>();
		CustNumMoneyRepVO countvo = null;
		for (CustCountVO vo : stocklist) {
			if (!map.containsKey(vo.getPk_corp())) {
				countvo = new CustNumMoneyRepVO();
				countvo.setPk_corp(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					countvo.setIstockcustsmall(vo.getNum());
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					countvo.setIstockcusttaxpay(vo.getNum());
				}
				map.put(vo.getPk_corp(), countvo);
			} else {
				countvo = map.get(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					countvo.setIstockcustsmall(ToolsUtil.addInteger(countvo.getIstockcustsmall(), vo.getNum()));
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					countvo.setIstockcusttaxpay(ToolsUtil.addInteger(countvo.getIstockcusttaxpay(), vo.getNum()));
				}
			}
		}
		if (map != null && !map.isEmpty()) {
			CorpVO corpvo = null;
			CustNumMoneyRepVO retvo = null;
			for (String key : map.keySet()) {
				retvo = map.get(key);
				corpvo = CorpCache.getInstance().get(null, key);
				if(corpvo != null){
					retvo.setVcorpname(corpvo.getUnitname());
					retvo.setVprovince(corpvo.getCitycounty());
				}
				retlist.add(retvo);
				corplist.add(key);
			}
		}
		return retlist;
	}
	
	/**
	 * 查询存量客户合同信息
	 * @param stocklist
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void queryStockContList(List<CustNumMoneyRepVO> retlist, List<String> corplist)
			throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp as pk_corp, t.chargedeptname as chargedeptname, sum(t.ntotalmny) as sum \n") ;
		sql.append("  FROM cn_contract t\n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0\n") ; 
		String where = SqlUtil.buildSqlForIn("pk_corp", corplist.toArray(new String[0]));
		sql.append(" AND ").append(where);
		sql.append(" GROUP BY (t.pk_corp, t.chargedeptname)");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if(list != null && list.size() > 0){
			Map<String,CustNumMoneyRepVO> map = new HashMap<String,CustNumMoneyRepVO>();
			CustNumMoneyRepVO custnumvo = null;
			for(CustCountVO vo : list){
				if(!map.containsKey(vo.getPk_corp())){
					custnumvo = new CustNumMoneyRepVO();
					custnumvo.setPk_corp(vo.getPk_corp());
					if("小规模纳税人".equals(vo.getChargedeptname())){
						custnumvo.setIstockcontsmall(vo.getSum());
					}else if("一般纳税人".equals(vo.getChargedeptname())){
						custnumvo.setIstockconttaxpay(vo.getSum());
					}
					map.put(vo.getPk_corp(), custnumvo);
				}else{
					custnumvo = map.get(vo.getPk_corp());
					if("小规模纳税人".equals(vo.getChargedeptname())){
						custnumvo.setIstockcontsmall(SafeCompute.add(custnumvo.getIstockcontsmall(), vo.getSum()));
					}else if("一般纳税人".equals(vo.getChargedeptname())){
						custnumvo.setIstockconttaxpay(SafeCompute.add(custnumvo.getIstockconttaxpay(), vo.getSum()));
					}
				}
			}
			CustNumMoneyRepVO countvo = null;
			for(CustNumMoneyRepVO vo : retlist){
				countvo = map.get(vo.getPk_corp());
				vo.setIstockcontsmall(countvo.getIstockcontsmall());
				vo.setIstockconttaxpay(countvo.getIstockconttaxpay());
			}
		}
	}

}
