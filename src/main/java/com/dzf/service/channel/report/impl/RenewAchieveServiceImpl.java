package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.ICustNumMoneyService;
import com.dzf.service.channel.report.IRenewAchieveService;

@Service("renewachieveser")
public class RenewAchieveServiceImpl extends DataCommonRepImpl implements IRenewAchieveService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ICustNumMoneyService custServ;

	@Override
	public List<CustNumMoneyRepVO> queryRenew(QryParamVO paramvo) throws DZFWarpException {
		
		HashMap<String, DataVO> map = queryCorps(paramvo, CustNumMoneyRepVO.class,2);
		List<String> corplist = null;
		if (map != null && !map.isEmpty()) {
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}
		if (corplist != null && corplist.size() > 0) {
			// 1、查询客户总数量、合同总金额
			Map<String, Integer> custmap = custServ.queryCustNum(paramvo, corplist, null);
			Map<String, DZFDouble> conmap = custServ.queryContMny(paramvo, corplist, null);
			// 2、查询续费客户数量、合同金额
			paramvo.setQrytype(1);// 扣款客户数
			Map<String, Integer> kcustmap = custServ.queryCustNum(paramvo, corplist, 3);
			paramvo.setQrytype(2);// 作废客户数
			Map<String, Integer> tcustmap = custServ.queryCustNum(paramvo, corplist, 3);
			paramvo.setQrytype(null);
			Map<String, DZFDouble> nconmap = custServ.queryContMny(paramvo, corplist, 3);

			// 4、查询续签客户数
			Map<String, CustNumMoneyRepVO> xqmap = queryXqNum(paramvo, corplist);

			return getReturnData(corplist, map, custmap, conmap, kcustmap, tcustmap, nconmap, xqmap);
		}
		return null;
	}
	
	/**
	 * 获取返回数据
	 * @param corplist
	 * @param map
	 * @param custmap
	 * @param conmap
	 * @param kcustmap
	 * @param tcustmap
	 * @param nconmap
	 * @param xqmap
	 * @return
	 * @throws DZFWarpException
	 */
	private List<CustNumMoneyRepVO> getReturnData(List<String> corplist, HashMap<String, DataVO> map,
			Map<String, Integer> custmap, Map<String, DZFDouble> conmap, Map<String, Integer> kcustmap,
			Map<String, Integer> tcustmap, Map<String, DZFDouble> nconmap, Map<String, CustNumMoneyRepVO> xqmap)
			throws DZFWarpException {
		List<CustNumMoneyRepVO> retlist = new ArrayList<CustNumMoneyRepVO>();
		CorpVO corpvo = null;
		CustNumMoneyRepVO retvo = null;
		Integer counum = null;
		CustNumMoneyRepVO xqvo = null;
		Integer resnum = null;
		for (String pk_corp : corplist) {
			retvo = (CustNumMoneyRepVO) map.get(pk_corp);
			corpvo = CorpCache.getInstance().get(null, pk_corp);
			if (corpvo != null) {
				retvo.setCorpname(corpvo.getUnitname());
				retvo.setVprovname(corpvo.getCitycounty());
				retvo.setDrelievedate(corpvo.getDrelievedate());
			}

			// 1 、客户数量、合同金额：
			if (custmap != null && !custmap.isEmpty()) {
				retvo.setIstockcusttaxpay(custmap.get(pk_corp + "一般纳税人"));
				retvo.setIstockcustsmall(custmap.get(pk_corp + "小规模纳税人"));
			}
			if (conmap != null && !conmap.isEmpty()) {
				retvo.setIstockconttaxpay(conmap.get(pk_corp + "一般纳税人"));
				retvo.setIstockcontsmall(conmap.get(pk_corp + "小规模纳税人"));
			}

			// 2、 续费客户数量、合同金额赋值：
			if (kcustmap != null && !kcustmap.isEmpty()) {
				retvo.setIrenewcusttaxpay(kcustmap.get(pk_corp + "一般纳税人"));
				retvo.setIrenewcustsmall(kcustmap.get(pk_corp + "小规模纳税人"));
			}
			if (tcustmap != null && !tcustmap.isEmpty()) {
				counum = tcustmap.get(pk_corp + "一般纳税人");
				resnum = ToolsUtil.subInteger(retvo.getIrenewcusttaxpay(), counum);
				if(resnum != 0){
					retvo.setIrenewcusttaxpay(resnum);
				}else{
					retvo.setIrenewcusttaxpay(null);
				}

				counum = tcustmap.get(pk_corp + "小规模纳税人");
				resnum = ToolsUtil.subInteger(retvo.getIrenewcustsmall(), counum);
				if(resnum != 0){
					retvo.setIrenewcustsmall(resnum);
				}else{
					retvo.setIrenewcustsmall(null);
				}
			}
			if (nconmap != null && !nconmap.isEmpty()) {
				retvo.setIrenewconttaxpay(nconmap.get(pk_corp + "一般纳税人"));
				retvo.setIrenewcontsmall(nconmap.get(pk_corp + "小规模纳税人"));
			}
			// 5、续签客户数
			if (xqmap != null && !xqmap.isEmpty()) {
				xqvo = xqmap.get(pk_corp);
				if (xqvo != null) {
					retvo.setIyrenewnum(xqvo.getIyrenewnum());// 应续签客户数
					retvo.setIrenewnum(xqvo.getIrenewnum());// 已续签客户数
				}
			}
			retlist.add(retvo);
		}
		return retlist;
	}

	/**
	 * 查询续签客户数
	 * 
	 * @param paramvo
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, CustNumMoneyRepVO> queryXqNum(QryParamVO paramvo, List<String> corplist)
			throws DZFWarpException {
		Map<String, CustNumMoneyRepVO> xqmap = new HashMap<String, CustNumMoneyRepVO>();
		// 1、应续签客户数
		List<CustNumMoneyRepVO> shouldlist = queryShouldNum(paramvo, corplist);
		if (shouldlist != null && shouldlist.size() > 0) {
			for (CustNumMoneyRepVO repvo : shouldlist) {
				xqmap.put(repvo.getPk_corp(), repvo);
			}
		}
		// 2、已续签客户数
		List<CustNumMoneyRepVO> alreadylist = queryAlreadyNum(paramvo, corplist);
		if (alreadylist != null && alreadylist.size() > 0) {
			CustNumMoneyRepVO numvo = null;
			for (CustNumMoneyRepVO repvo : alreadylist) {
				if (xqmap.containsKey(repvo.getPk_corp())) {
					numvo = xqmap.get(repvo.getPk_corp());
					numvo.setIrenewnum(repvo.getIrenewnum());
					xqmap.put(repvo.getPk_corp(), numvo);
				} else {
					numvo = new CustNumMoneyRepVO();
					numvo.setIrenewnum(repvo.getIrenewnum());
					xqmap.put(repvo.getPk_corp(), numvo);
				}
			}
		}
		return xqmap;
	}

	/**
	 * 查询应续签客户数 结束月份在查询月 、非存量客户、非补提单的已审核或已终止合同的客户数
	 * 
	 * @param paramvo
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustNumMoneyRepVO> queryShouldNum(QryParamVO paramvo, List<String> corplist) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,    ");
		sql.append("       COUNT(t.pk_corpk) AS iyrenewnum   ");
		sql.append("  FROM ynt_contract t    ");
		sql.append(" WHERE nvl(t.dr, 0) = 0    ");
		sql.append("   AND t.icontracttype = 2    ");
		sql.append("   AND t.icosttype = 0    ");
		sql.append("   AND nvl(t.isncust, 'N') = 'N'    ");
		sql.append("   AND nvl(t.patchstatus, 0) != 2   ");
		sql.append("   AND nvl(t.patchstatus, 0) != 5   ");
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			sql.append("   AND t.vendperiod = ?    ");
			spm.addParam(paramvo.getPeriod());
		}else{
			if(paramvo.getBegdate() != null){
				sql.append("   AND t.denddate >= ?    ");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append("   AND t.denddate <= ?    ");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append("   AND t.vstatus IN (1, 9)    ");
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ").append(where);
		}
		sql.append(" GROUP BY t.pk_corp    ");
		return (List<CustNumMoneyRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustNumMoneyRepVO.class));
	}

	/**
	 * 查询已续签客户数 结束月份在查询月 、非存量客户、非补提单的已审核或已终止合同，且客户在查询月之后有符合此条件的合同的客户
	 * 
	 * @param paramvo
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustNumMoneyRepVO> queryAlreadyNum(QryParamVO paramvo, List<String> corplist) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,    ");
		sql.append("       COUNT(t.pk_corpk) AS irenewnum   ");
		sql.append("  FROM ynt_contract t    ");
		sql.append(" WHERE nvl(t.dr, 0) = 0    ");
		sql.append("   AND t.icontracttype = 2    ");
		sql.append("   AND t.icosttype = 0    ");
		sql.append("   AND nvl(t.isncust, 'N') = 'N'    ");
		sql.append("   AND nvl(t.patchstatus, 0) != 2   ");
		sql.append("   AND nvl(t.patchstatus, 0) != 5   ");
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			sql.append("   AND t.vendperiod = ?    ");
			spm.addParam(paramvo.getPeriod());
		}else{
			if(paramvo.getBegdate() != null){
				sql.append("   AND t.denddate >= ?    ");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append("   AND t.denddate <= ?    ");
				spm.addParam(paramvo.getEnddate());
			}
		}
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ").append(where);
		}
		sql.append("   AND t.vstatus IN (1, 9)    ");
		sql.append("   AND t.pk_corpk IN (SELECT DISTINCT t.pk_corpk    ");
		sql.append("                       FROM ynt_contract t    ");
		sql.append("                      WHERE nvl(t.dr, 0) = 0    ");
		sql.append("                        AND t.icontracttype = 2    ");
		sql.append("                        AND t.icosttype = 0    ");
		sql.append("                        AND nvl(t.isncust, 'N') = 'N'    ");
		sql.append("   						AND nvl(t.patchstatus, 0) != 2   ");
		sql.append("  						AND nvl(t.patchstatus, 0) != 5   ");
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			sql.append("                    AND t.vendperiod > ?    ");
			spm.addParam(paramvo.getPeriod());
		}else{
			sql.append("                    AND t.denddate > ?    ");
			spm.addParam(paramvo.getEnddate());
		}
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ").append(where);
		}
		sql.append("                        AND t.vstatus IN (1, 9))    ");
		sql.append(" GROUP BY t.pk_corp    ");
		return (List<CustNumMoneyRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustNumMoneyRepVO.class));
	}

}
