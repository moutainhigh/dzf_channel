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
import com.dzf.model.channel.report.CustManageRepVO;
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_set.BDTradeVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.service.channel.report.ICustManageRep;
import com.dzf.service.channel.report.ICustNumMoneyRep;

@Service("custmanagerepser")
public class CustManageRepImpl implements ICustManageRep {
	
    @Autowired
    private SingleObjectBO singleObjectBO;
    
	@Autowired
	private ICustNumMoneyRep custServ;

	@Override
	public List<CustManageRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<CustManageRepVO> retlist = new ArrayList<CustManageRepVO>();
		List<String> corplist = new ArrayList<String>();
		List<String> countcorplist = new ArrayList<String>();
		List<CustCountVO> custlist = custServ.queryCustNum(paramvo, 1);
		Map<String, CustNumMoneyRepVO> custmap = custServ.countCustNumByType(custlist, 1, corplist, countcorplist);
		CustNumMoneyRepVO custnumvo = null;
		CustManageRepVO retvo = null;
		if(corplist != null && corplist.size() > 0){
			CorpVO corpvo = null;
			for(String pk_corp : corplist){
				retvo = new CustManageRepVO();
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
				retlist.add(retvo);
			}
		}
		return retlist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustCountVO> queryIndustry(QryParamVO paramvo) throws DZFWarpException {
		List<CustCountVO> retlist = new ArrayList<CustCountVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.industry, count(p.pk_corp) as num \n");
		sql.append("  FROM bd_corp p \n");
		sql.append("  LEFT JOIN ynt_franchisee f ON p.fathercorp = f.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(f.dr, 0) = 0 \n");
		sql.append("   AND nvl(f.isreport, 'N') = 'Y' \n");
		sql.append(" GROUP BY p.industry \n");
		sql.append(" ORDER BY num DESC \n");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if(list != null && list.size() > 0){
			Map<String,String> trademap = queryTrade();
			for(CustCountVO vo : list){
				if(!StringUtil.isEmpty(vo.getIndustry())){
					vo.setIndustryname(trademap.get(vo.getIndustry()));
					retlist.add(vo);
					if(retlist != null && retlist.size() == 5){
						break;
					}
				}
			}
		}
		CustCountVO countvo = new CustCountVO();
		countvo.setIndustryname("其他类占比");
		retlist.add(countvo);
		return retlist;
	}
	
	/**
	 * 查询行业信息
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,String> queryTrade() throws DZFWarpException {
		Map<String,String> map = new HashMap<String,String>();
		String sql = " nvl(dr,0) = 0 ";
		BDTradeVO[] tradeVOs = (BDTradeVO[]) singleObjectBO.queryByCondition(BDTradeVO.class, sql.toString(), null);
		if(tradeVOs != null && tradeVOs.length > 0){
			for(BDTradeVO vo : tradeVOs){
				map.put(vo.getPk_trade(), vo.getTradename());
			}
		}
		return map;
	}

}
