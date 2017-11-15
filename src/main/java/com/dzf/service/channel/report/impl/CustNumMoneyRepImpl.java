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
		//1.1、查询存量客户数量、合同金额
		Map<String,CustCountVO> stockmap = queryStockNumMny(paramvo, corplist);
		//1.2、查询新增客户数量、合同金额
		Map<String, CustCountVO> newmap = queryNumMnyByType(paramvo, corplist, 1);
		//1.3、查询续费客户数量、合同金额
		Map<String, CustCountVO> renewmap = queryNumMnyByType(paramvo, corplist, 2);
		
		String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
		paramvo.setPeriod(preperiod);
		
		//1.4、查询上一个月新增客户数量、合同金额
		Map<String, CustCountVO> lastnewmap = queryNumMnyByType(paramvo, corplist, 1);
		//1.5、查询上一个月续费客户数量、合同金额
		Map<String, CustCountVO> lastrenewmap = queryNumMnyByType(paramvo, corplist, 2);
		
		if(corplist != null && corplist.size() > 0){
			CorpVO corpvo = null;
			CustCountVO custvo = null;
			CustNumMoneyRepVO retvo = null;
			for(String pk_corp : corplist){
				retvo = new CustNumMoneyRepVO();
				retvo.setPk_corp(pk_corp);
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if(corpvo != null){
					retvo.setVcorpname(corpvo.getUnitname());
					retvo.setVprovince(corpvo.getCitycounty());
				}
				//2.1 存量客户数量、合同金额赋值：
				if(stockmap != null && !stockmap.isEmpty()){
					custvo = stockmap.get(pk_corp+"一般纳税人");
					if(custvo != null){
						retvo.setIstockcusttaxpay(custvo.getNum());//存量客户数量
						retvo.setIstockconttaxpay(custvo.getSummny());//存量客户合同金额
					}
					custvo = stockmap.get(pk_corp+"小规模纳税人");
					if(custvo != null){
						retvo.setIstockcustsmall(custvo.getNum());//存量客户数量
						retvo.setIstockcontsmall(custvo.getSummny());//存量客户合同金额
					}
				}
				//2.2 新增客户数量、合同金额赋值：
				if(newmap != null && !newmap.isEmpty()){
					custvo = newmap.get(pk_corp+"一般纳税人");
					if(custvo != null){
						retvo.setInewcusttaxpay(custvo.getNum());//新增客户数量
						retvo.setInewconttaxpay(custvo.getSummny());//新增客合同金额
					}
					custvo = newmap.get(pk_corp+"小规模纳税人");
					if(custvo != null){
						retvo.setInewcustsmall(custvo.getNum());//新增客户数量
						retvo.setInewcontsmall(custvo.getSummny());//新增客户合同金额
					}
				}
				//2.3续费客户数量、合同金额赋值：
				if(renewmap != null && !renewmap.isEmpty()){
					custvo = renewmap.get(pk_corp+"一般纳税人");
					if(custvo != null){
						retvo.setIrenewcusttaxpay(custvo.getNum());//续费客户数量
						retvo.setIrenewconttaxpay(custvo.getSummny());//续费客合同金额
					}
					custvo = renewmap.get(pk_corp+"小规模纳税人");
					if(custvo != null){
						retvo.setIrenewcustsmall(custvo.getNum());//续费客户数量
						retvo.setIrenewcontsmall(custvo.getSummny());//续费客户合同金额
					}
				}
				
				//3.1 上月新增客户数量、合同金额赋值：
				if(lastnewmap != null && !lastnewmap.isEmpty()){
					custvo = lastnewmap.get(pk_corp+"一般纳税人");
					if(custvo != null){
						retvo.setIlastnewcusttaxpay(custvo.getNum());//新增客户数量
						retvo.setIlastnewconttaxpay(custvo.getSummny());//新增客合同金额
					}
					custvo = lastnewmap.get(pk_corp+"小规模纳税人");
					if(custvo != null){
						retvo.setIlastnewcustsmall(custvo.getNum());//新增客户数量
						retvo.setIlastnewcontsmall(custvo.getSummny());//新增客户合同金额
					}
				}
				//3.2 上月续费客户数量、合同金额赋值：
				if(lastrenewmap != null && !lastrenewmap.isEmpty()){
					custvo = lastrenewmap.get(pk_corp+"一般纳税人");
					if(custvo != null){
						retvo.setIlastrenewcusttaxpay(custvo.getNum());//续费客户数量
						retvo.setIlastrenewconttaxpay(custvo.getSummny());//续费客合同金额
					}
					custvo = lastrenewmap.get(pk_corp+"小规模纳税人");
					if(custvo != null){
						retvo.setIlastrenewcustsmall(custvo.getNum());//续费客户数量
						retvo.setIlastrenewcontsmall(custvo.getSummny());//续费客户合同金额
					}
				}
				
				
				//4.1新增客户、合同增长率
				retvo.setInewcustratesmall(getCustRate(retvo.getInewcustsmall(), retvo.getIlastnewcustsmall()));
				retvo.setInewcustratetaxpay(getCustRate(retvo.getInewcusttaxpay(), retvo.getIlastnewcusttaxpay()));
				retvo.setInewcontratesmall(getContRate(retvo.getInewcontsmall(), retvo.getIlastnewcontsmall()));
				retvo.setInewcontratetaxpay(getContRate(retvo.getInewconttaxpay(), retvo.getIlastnewconttaxpay()));
				
				//4.2续费客户、合同增长率
				retvo.setIrenewcustratesmall(getCustRate(retvo.getIrenewcustsmall(), retvo.getIlastrenewcustsmall()));
				retvo.setIrenewcustratetaxpay(getCustRate(retvo.getIrenewcusttaxpay(), retvo.getIlastrenewcusttaxpay()));
				retvo.setIrenewcontratesmall(getContRate(retvo.getIrenewcontsmall(), retvo.getIlastrenewcontsmall()));
				retvo.setIrenewcontratetaxpay(getContRate(retvo.getIrenewconttaxpay(), retvo.getIlastrenewconttaxpay()));
				
				retlist.add(retvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 查询存量客户数量、合同金额
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,CustCountVO> queryStockNumMny(QryParamVO paramvo, List<String> corplist) throws DZFWarpException {
		Map<String,CustCountVO> stockmap = new HashMap<String,CustCountVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_corp, chargedeptname, COUNT(pk_corpk) AS num, SUM(ntotalmny) AS summny \n") ;
		sql.append("  FROM (SELECT NVL(t.chargedeptname, '小规模纳税人') AS chargedeptname, \n") ; 
		sql.append("               t.pk_corp AS pk_corp, \n") ; 
		sql.append("               t.pk_corpk AS pk_corpk, \n") ; 
		sql.append("               t.ntotalmny AS ntotalmny \n") ; 
		sql.append("          FROM ynt_contract t \n") ; 
		sql.append("          LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp \n") ; 
		sql.append("         WHERE nvl(t.dr, 0) = 0 \n") ; 
		sql.append("           AND nvl(acc.dr, 0) = 0 \n") ;
		sql.append("           AND nvl(acc.ischannel, 'N') = 'Y'\n") ; 
		sql.append("           AND (t.vbeginperiod = ? OR t.vendperiod = ? OR \n") ; 
		spm.addParam(paramvo.getPeriod());
		spm.addParam(paramvo.getPeriod());
		sql.append("                (t.vbeginperiod < ? AND t.vendperiod > ? )) \n") ; 
		spm.addParam(paramvo.getPeriod());
		spm.addParam(paramvo.getPeriod());
		sql.append("           AND nvl(t.icontracttype, 1) = 2 \n") ; 
		sql.append("           AND t.vdeductstatus = 2 \n") ; //合同状态 = 已审核
		sql.append("   AND t.pk_corp NOT IN \n") ; 
		sql.append("       (SELECT f.pk_corp \n") ; 
		sql.append("          FROM ynt_franchisee f \n") ; 
		sql.append("         WHERE nvl(dr, 0) = 0 \n") ; 
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		sql.append("           AND NVL(t.isncust, 'N') = 'N') cu \n") ; 
		sql.append(" GROUP BY pk_corp, chargedeptname");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(CustCountVO.class));
		if(list != null && list.size() > 0){
			String key = "";
			for(CustCountVO vo : list){
				if(!corplist.contains(vo.getPk_corp())){
					corplist.add(vo.getPk_corp());
				}
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				stockmap.put(key, vo);
			}
		}
		return stockmap;
	}
	
	/**
	 * 查询新增（续费）客户数量、合同金额
	 * @param paramvo
	 * @param corplist
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, CustCountVO> queryNumMnyByType(QryParamVO paramvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		Map<String, CustCountVO> map = new HashMap<String, CustCountVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_corp,\n");
		sql.append("       chargedeptname,\n");
		sql.append("       COUNT(pk_corpk) AS num,\n");
		sql.append("       SUM(ntotalmny) AS summny\n");
		sql.append("  FROM (SELECT NVL(t.chargedeptname, '小规模纳税人') AS chargedeptname,\n");
		sql.append("               t.pk_corp AS pk_corp,\n");
		sql.append("               t.ntotalmny AS ntotalmny,\n");
		sql.append("               t.pk_corpk AS pk_corpk\n");
		sql.append("          FROM ynt_contract t\n");
		sql.append("          LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp \n");
		sql.append("         WHERE nvl(t.dr, 0) = 0\n");
		sql.append("           AND nvl(acc.dr, 0) = 0\n");
		sql.append("           AND nvl(acc.ischannel, 'N') = 'Y' \n") ; 
		sql.append("   AND t.pk_corp NOT IN \n") ; 
		sql.append("       (SELECT f.pk_corp \n") ; 
		sql.append("          FROM ynt_franchisee f \n") ; 
		sql.append("         WHERE nvl(dr, 0) = 0 \n") ; 
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		sql.append("           AND SUBSTR(t.dsigndate, 1, 7) = ? \n");
		spm.addParam(paramvo.getPeriod());
		sql.append("           AND nvl(t.icontracttype, 1) = 2 \n");
		sql.append("           AND t.vdeductstatus = 2 \n");//合同状态 = 已审核
		sql.append("           AND NVL(t.isncust, 'N') = 'N'\n");
		if (qrytype == 1) {// 新增客户
			sql.append("           AND t.pk_corpk NOT IN \n");
		} else if (qrytype == 2) {// 续费客户
			sql.append("           AND t.pk_corpk IN \n");
		}
		sql.append("               (SELECT t.pk_corpk AS pk_corpk\n");
		sql.append("                  FROM ynt_contract t\n");
		sql.append("                  LEFT JOIN ynt_franchisee f ON t.pk_corp = f.pk_corp\n");
		sql.append("                 WHERE nvl(t.dr, 0) = 0\n");
		sql.append("                   AND SUBSTR(t.dsigndate, 1, 7) > ? \n");
		spm.addParam(paramvo.getPeriod());
		sql.append("                   AND nvl(t.icontracttype, 1) = 2\n");
		sql.append("                   AND t.vdeductstatus = 2)) cu\n");//合同状态 = 已审核
		sql.append(" GROUP BY pk_corp, chargedeptname");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			String key = "";
			for (CustCountVO vo : list) {
				if (!corplist.contains(vo.getPk_corp())) {
					corplist.add(vo.getPk_corp());
				}
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				map.put(key, vo);
			}
		}
		return map;
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
		sql.append("SELECT p.fathercorp as pk_corp,\n") ;
		sql.append("       p.chargedeptname as chargedeptname,\n") ; 
		sql.append("       count(p.pk_corp) as num \n") ; 
		sql.append("  FROM bd_corp p \n") ; 
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n") ; 
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(t.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'\n") ; //渠道客户
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n") ; //未封存
		sql.append("   AND nvl(p.ishasaccount,'N') = 'Y' \n");//已建账
		sql.append("   AND p.chargedeptname is not null \n");//纳税人性质不能为空
		sql.append("   AND p.fathercorp NOT IN \n") ; 
		sql.append("       (SELECT f.pk_corp \n") ; 
		sql.append("          FROM ynt_franchisee f \n") ; 
		sql.append("         WHERE nvl(dr, 0) = 0 \n") ; 
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		if(qrytype == 1){
			if(paramvo.getBegdate() != null){
				sql.append("   AND p.createdate <= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
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
						countvo.setIstockcustsmall(vo.getNum());//存量客户-小规模
					} else if (qrytype == 2) {
						countvo.setInewcustsmall(vo.getNum());//新增客户-小规模
					}
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						countvo.setIstockcusttaxpay(vo.getNum());//存量客户-一般纳税人
					} else if (qrytype == 2) {
						countvo.setInewcusttaxpay(vo.getNum());//新增客户-一般纳税人
					}
				}
				countvo.setIcustsum(vo.getNum());//客户合计
				custmap.put(vo.getPk_corp(), countvo);
			} else {
				countvo = custmap.get(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						//存量客户-小规模
						countvo.setIstockcustsmall(ToolsUtil.addInteger(countvo.getIstockcustsmall(), vo.getNum()));
					} else if (qrytype == 2) {
						//新增客户-小规模
						countvo.setInewcustsmall(ToolsUtil.addInteger(countvo.getIstockcustsmall(), vo.getNum()));
					}
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						//存量客户-一般纳税人
						countvo.setIstockcusttaxpay(ToolsUtil.addInteger(countvo.getIstockcusttaxpay(), vo.getNum()));
					} else if (qrytype == 2) {
						//新增客户-一般纳税人
						countvo.setInewcusttaxpay(ToolsUtil.addInteger(countvo.getIstockcusttaxpay(), vo.getNum()));
					}
				}
				countvo.setIcustsum(ToolsUtil.addInteger(countvo.getIcustsum(), vo.getNum()));//客户合计
			}
		}
		return custmap;
	}
	
}
