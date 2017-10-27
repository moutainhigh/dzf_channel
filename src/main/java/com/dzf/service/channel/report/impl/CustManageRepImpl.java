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
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_set.BDTradeVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
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
		List<String> pklist = qryIndustryPk(paramvo);//排行前五行业主键
		List<CustCountVO> custnumlist = qryIndustryNum(paramvo);
		Map<String, CustCountVO> industmap = qryIndustryMap(custnumlist, pklist);
		String[] industrys = new String[]{"小规模纳税人","一般纳税人"};
		CustCountVO industryvo = null;
		DZFDouble rate = DZFDouble.ZERO_DBL;
		Integer countnum = null;
		if(corplist != null && corplist.size() > 0){
			CorpVO corpvo = null;
			String key = "";
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
				//获取各个行业的值
				Integer custsum = 0;
				if(pklist != null && pklist.size() > 0){
					for(int i = 0; i < pklist.size(); i++){
						for(int j = 0; j <industrys.length; j++){
							key = pk_corp + "" + pklist.get(i) + "" + industrys[j];
							industryvo = industmap.get(key);
							custsum = 0;
							custsum = retvo.getIcustsmall() + retvo.getIcusttaxpay();
							if(industryvo != null){
								if(j == 0){
									retvo.setAttributeValue("icustsmall"+(i+1), industryvo.getNum());
									countnum = CommonUtil.getInteger(retvo.getAttributeValue("icustsmall"+(i+1)));
									rate = getCustRate(countnum, custsum);
//									rate = getCustRate(countnum, retvo.getIcustsmall());
									retvo.setAttributeValue("icustratesmall"+(i+1), rate);
								}else if(j == 1){
									retvo.setAttributeValue("icusttaxpay"+(i+1), industryvo.getNum());
									countnum = CommonUtil.getInteger(retvo.getAttributeValue("icusttaxpay"+(i+1)));
									rate = getCustRate(countnum, custsum);
//									rate = getCustRate(countnum, retvo.getIcusttaxpay());
									retvo.setAttributeValue("icustratetaxpay"+(i+1), rate);
								}
							}
						}
					}
				}
				retlist.add(retvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 占比计算
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		return num3.div(num4).multiply(100);
	}
	
	/**
	 * 行业分类汇总计算
	 * @param custnumlist
	 * @param pklist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, CustCountVO> qryIndustryMap(List<CustCountVO> custnumlist,	List<String> pklist) throws DZFWarpException {
		Map<String, CustCountVO> retmap = new HashMap<String, CustCountVO>();
		if(custnumlist != null && custnumlist.size() > 0){
			String key = "";
			CustCountVO retvo = null;
			for(CustCountVO vo : custnumlist){
				if(StringUtil.isEmpty(vo.getPk_corp()) || StringUtil.isEmpty(vo.getIndustry()) || StringUtil.isEmpty(vo.getChargedeptname())){
					continue;
				}
				if(pklist.contains(vo.getIndustry())){
					key = vo.getPk_corp()+""+vo.getIndustry()+""+vo.getChargedeptname();
				}else{
					key = vo.getPk_corp()+"others"+vo.getChargedeptname();
				}
				retvo = new CustCountVO();
				if(!retmap.containsKey(key)){
					retvo.setKey(key);
					retvo.setNum(vo.getNum());
					retmap.put(key, retvo);
				}else{
					retvo = retmap.get(key);
					retvo.setNum(CommonUtil.addInteger(retvo.getNum(), vo.getNum()));
				}
			}
		}
		return retmap;
	}
	
	/**
	 * 行业按照会计工资主键、行业、纳税人资格进行分类汇总
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> qryIndustryNum(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.fathercorp as pk_corp, p.industry, p.chargedeptname, count(p.pk_corp) as num \n")  ;
		sql.append("  FROM bd_corp p \n")  ; 
		sql.append("  LEFT JOIN ynt_franchisee f ON p.fathercorp = f.pk_corp \n")  ; 
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n")  ; 
		sql.append("   AND nvl(f.dr, 0) = 0 \n")  ; 
		sql.append("   AND nvl(f.isreport, 'N') = 'Y' \n")  ; 
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");
		sql.append(" GROUP BY (p.fathercorp, p.chargedeptname, p.industry) \n")  ; 
		sql.append(" ORDER BY num DESC \n");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}
	
	/**
	 * 排行前五行业主键
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<String> qryIndustryPk(QryParamVO paramvo) throws DZFWarpException {
		List<String> retlist = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.industry, count(p.pk_corp) as num \n");
		sql.append("  FROM bd_corp p \n");
		sql.append("  LEFT JOIN ynt_franchisee f ON p.fathercorp = f.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(f.dr, 0) = 0 \n");
		sql.append("   AND nvl(f.isreport, 'N') = 'Y' \n");
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");
		sql.append(" GROUP BY p.industry \n");
		sql.append(" ORDER BY num DESC \n");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if(list != null && list.size() > 0){
			for(CustCountVO vo : list){
				if(!StringUtil.isEmpty(vo.getIndustry())){
					retlist.add(vo.getIndustry());
					if(retlist != null && retlist.size() == 5){
						retlist.add("others");
						break;
					}
				}
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
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");
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
						CustCountVO countvo = new CustCountVO();
						countvo.setIndustryname("其他类占比");
						retlist.add(countvo);
						break;
					}
				}
			}
		}
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
