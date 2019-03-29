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
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.ICustNumMoneyRep;

@Service("custnummoneyrepser")
public class CustNumMoneyRepImpl extends DataCommonRepImpl implements ICustNumMoneyRep {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<CustNumMoneyRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<CustNumMoneyRepVO> retlist = new ArrayList<CustNumMoneyRepVO>();
		HashMap<String, DataVO> map = queryCorps(paramvo, CustNumMoneyRepVO.class);
		List<String> corplist = null;
		if (map != null && !map.isEmpty()) {
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}
		if (corplist != null && corplist.size() > 0) {
			// 1、查询客户数量、合同金额
			Map<String, Integer> custmap = queryCustNum(paramvo, corplist, null);
			Map<String, DZFDouble> conmap = queryContMny(paramvo, corplist, null);
			// 2、查询新增客户数量、合同金额
			Map<String, Integer> ncustmap = queryCustNum(paramvo, corplist, 1);
			Map<String, DZFDouble> nconmap = queryContMny(paramvo, corplist, 1);
			// 3、查询上一个月新增客户数量、合同金额
			Map<String, Integer> lncustmap = queryCustNum(paramvo, corplist, 2);
			Map<String, DZFDouble> lnconmap = queryContMny(paramvo, corplist, 2);

			CorpVO corpvo = null;
			UserVO uservo = null;
			CustCountVO custvo = null;
			CustNumMoneyRepVO retvo = null;
			
			//4、查询合同提单量
			Map<String, Integer> cmap = queryContNum(paramvo, corplist);

			for (String pk_corp : corplist) {
				retvo = (CustNumMoneyRepVO) map.get(pk_corp);
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if (corpvo != null) {
					retvo.setCorpname(corpvo.getUnitname());
					retvo.setVprovname(corpvo.getCitycounty());
				}
				uservo = UserCache.getInstance().get(retvo.getUserid(), pk_corp);
				if (uservo != null) {
					retvo.setUsername(uservo.getUser_name());
				}
				uservo = UserCache.getInstance().get(retvo.getCuserid(), pk_corp);
				if (uservo != null) {
					retvo.setCusername(uservo.getUser_name());
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
				// 2 、新增客户数量、合同金额：
				if (ncustmap != null && !ncustmap.isEmpty()) {
					retvo.setInewcusttaxpay(ncustmap.get(pk_corp + "一般纳税人"));
					retvo.setInewcustsmall(ncustmap.get(pk_corp + "小规模纳税人"));
				}
				if (nconmap != null && !nconmap.isEmpty()) {
					retvo.setInewconttaxpay(nconmap.get(pk_corp + "一般纳税人"));
					retvo.setInewcontsmall(nconmap.get(pk_corp + "小规模纳税人"));
				}

				// 3、 上月新增客户数量、合同金额：
				if (lncustmap != null && !lncustmap.isEmpty()) {
					retvo.setIlastnewcusttaxpay(lncustmap.get(pk_corp + "一般纳税人"));
					retvo.setIlastnewcustsmall(lncustmap.get(pk_corp + "小规模纳税人"));
				}
				if (lnconmap != null && !lnconmap.isEmpty()) {
					retvo.setIlastnewconttaxpay(lnconmap.get(pk_corp + "一般纳税人"));
					retvo.setIlastnewcontsmall(lnconmap.get(pk_corp + "小规模纳税人"));
				}
				// 4、新增客户、合同增长率
				retvo.setInewcustratesmall(getCustRate(retvo.getInewcustsmall(), retvo.getIlastnewcustsmall()));
				retvo.setInewcustratetaxpay(getCustRate(retvo.getInewcusttaxpay(), retvo.getIlastnewcusttaxpay()));
				retvo.setInewcontratesmall(getContRate(retvo.getInewcontsmall(), retvo.getIlastnewcontsmall()));
				retvo.setInewcontratetaxpay(getContRate(retvo.getInewconttaxpay(), retvo.getIlastnewconttaxpay()));
				// 5、合同数量
				if(cmap != null && !cmap.isEmpty()){
					retvo.setIcontnum(cmap.get(pk_corp));
				}
				
				retlist.add(retvo);
			}
		}
		return retlist;
	}

	@Override
	public List<CustNumMoneyRepVO> queryRenew(QryParamVO paramvo) throws DZFWarpException {
		List<CustNumMoneyRepVO> retlist = new ArrayList<CustNumMoneyRepVO>();
		HashMap<String, DataVO> map = queryCorps(paramvo, CustNumMoneyRepVO.class);
		List<String> corplist = null;
		if (map != null && !map.isEmpty()) {
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}
		if (corplist != null && corplist.size() > 0) {
			// 1、查询客户数量、合同金额
			Map<String, Integer> custmap = queryCustNum(paramvo, corplist, null);
			Map<String, DZFDouble> conmap = queryContMny(paramvo, corplist, null);
			// 2、查询续费客户数量、合同金额
			paramvo.setQrytype(1);//扣款客户数
			Map<String, Integer> kcustmap = queryCustNum(paramvo, corplist, 3);
			paramvo.setQrytype(2);//作废客户数
			Map<String, Integer> tcustmap = queryCustNum(paramvo, corplist, 3);
			paramvo.setQrytype(null);
			
			Map<String, DZFDouble> nconmap = queryContMny(paramvo, corplist, 3);
			// 3、查询上一个月续费客户数量、合同金额
			paramvo.setQrytype(1);//扣款客户数
			Map<String, Integer> lkcustmap = queryCustNum(paramvo, corplist, 4);
			paramvo.setQrytype(2);//作废客户数
			Map<String, Integer> ltcustmap = queryCustNum(paramvo, corplist, 4);
			paramvo.setQrytype(null);
			Map<String, DZFDouble> lnconmap = queryContMny(paramvo, corplist, 4);

			CorpVO corpvo = null;
			UserVO uservo = null;
			CustNumMoneyRepVO retvo = null;
			
			Integer counum = null;
			
			// 4、查询续签客户数
			Map<String, CustNumMoneyRepVO> xqmap = queryXqNum(paramvo, corplist);
			CustNumMoneyRepVO xqvo = null;

			for (String pk_corp : corplist) {
				retvo = (CustNumMoneyRepVO) map.get(pk_corp);
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if (corpvo != null) {
					retvo.setCorpname(corpvo.getUnitname());
					retvo.setVprovname(corpvo.getCitycounty());
				}
				uservo = UserCache.getInstance().get(retvo.getUserid(), pk_corp);
				if (uservo != null) {
					retvo.setUsername(uservo.getUser_name());
				}
				uservo = UserCache.getInstance().get(retvo.getCuserid(), pk_corp);
				if (uservo != null) {
					retvo.setCusername(uservo.getUser_name());
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
				if(tcustmap != null && !tcustmap.isEmpty()){
					counum = tcustmap.get(pk_corp + "一般纳税人");
					retvo.setIrenewcusttaxpay(ToolsUtil.subInteger(retvo.getIrenewcusttaxpay(), counum));
					
					counum = tcustmap.get(pk_corp + "小规模纳税人");
					retvo.setIrenewcustsmall(ToolsUtil.subInteger(retvo.getIrenewcustsmall(), counum));
				}
				if (nconmap != null && !nconmap.isEmpty()) {
					retvo.setIrenewconttaxpay(nconmap.get(pk_corp + "一般纳税人"));
					retvo.setIrenewcontsmall(nconmap.get(pk_corp + "小规模纳税人"));
				}

				// 3 、上月续费客户数量、合同金额赋值：
				if (lkcustmap != null && !lkcustmap.isEmpty()) {
					retvo.setIlastrenewcusttaxpay(lkcustmap.get(pk_corp + "一般纳税人"));
					retvo.setIlastrenewcustsmall(lkcustmap.get(pk_corp + "小规模纳税人"));
				}
				if(ltcustmap != null && !ltcustmap.isEmpty()){
					counum = ltcustmap.get(pk_corp + "一般纳税人");
					retvo.setIlastrenewcusttaxpay(ToolsUtil.subInteger(retvo.getIlastrenewcusttaxpay(), counum));
					
					counum = ltcustmap.get(pk_corp + "小规模纳税人");
					retvo.setIlastrenewcustsmall(ToolsUtil.subInteger(retvo.getIlastrenewcustsmall(), counum));
				}
				if (lnconmap != null && !lnconmap.isEmpty()) {
					retvo.setIlastrenewconttaxpay(lnconmap.get(pk_corp + "一般纳税人"));
					retvo.setIlastrenewcontsmall(lnconmap.get(pk_corp + "小规模纳税人"));
				}
				// 4、续费客户、合同增长率
				retvo.setIrenewcustratesmall(getCustRate(retvo.getIrenewcustsmall(), retvo.getIlastrenewcustsmall()));
				retvo.setIrenewcustratetaxpay(
						getCustRate(retvo.getIrenewcusttaxpay(), retvo.getIlastrenewcusttaxpay()));
				retvo.setIrenewcontratesmall(getContRate(retvo.getIrenewcontsmall(), retvo.getIlastrenewcontsmall()));
				retvo.setIrenewcontratetaxpay(
						getContRate(retvo.getIrenewconttaxpay(), retvo.getIlastrenewconttaxpay()));
				
				// 5、续签客户数
				if(xqmap != null && !xqmap.isEmpty()){
					xqvo = xqmap.get(pk_corp);
					if(xqvo != null){
						retvo.setIyrenewnum(xqvo.getIyrenewnum());//应续签客户数
						retvo.setIrenewnum(xqvo.getIrenewnum());//已续签客户数
					}
				}
				
				retlist.add(retvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 查询续签客户数
	 * @param paramvo
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, CustNumMoneyRepVO> queryXqNum(QryParamVO paramvo, List<String> corplist) throws DZFWarpException {
		Map<String, CustNumMoneyRepVO> xqmap = new HashMap<String, CustNumMoneyRepVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  \n");
		sql.append("       COUNT(CASE  \n");
		sql.append("               WHEN nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n");
		sql.append("                1  \n");
		sql.append("               ELSE  \n");
		sql.append("                0  \n");
		sql.append("             END) AS iyrenewnum,  \n");
		sql.append("       COUNT(CASE  \n");
		sql.append("               WHEN nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND  \n");
		sql.append("                    nvl(ct.isxq, 'N') = 'Y' THEN  \n");
		sql.append("                1  \n");
		sql.append("               ELSE  \n");
		sql.append("                0  \n");
		sql.append("             END) AS irenewnum  \n");
		sql.append("  FROM cn_contract t  \n");
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND ct.icontracttype = 2  \n");
		sql.append("   AND ct.icosttype = 0  \n");
		sql.append("   AND nvl(ct.isncust, 'N') = 'N'  \n");
		sql.append("   AND ct.vendperiod = ?  \n");
		spm.addParam(paramvo.getPeriod());
		sql.append("   AND t.vdeductstatus = ?  \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		sql.append(" GROUP BY t.pk_corp  \n");
		List<CustNumMoneyRepVO> list = (List<CustNumMoneyRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustNumMoneyRepVO.class));
		if(list != null && list.size() > 0){
			for(CustNumMoneyRepVO repvo : list){
				xqmap.put(repvo.getPk_corp(), repvo);
			}
		}
		return xqmap;
	}

	/**
	 * 查询客户数量
	 * 
	 * @param paramvo
	 *            查询参数
	 * @param corplist
	 *            过滤后加盟商主键
	 * @param qrytype
	 *            查询类型 空：所有数据； 1：新增客户；2：新增客户（上月）；3：续费客户；4：续费客户（上月）；
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> queryCustNum(QryParamVO paramvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		Map<String, Integer> nummap = new HashMap<String, Integer>();
		SQLParameter spm = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT p.fathercorp AS pk_corp,\n");
		sql.append("       NVL(p.chargedeptname, '小规模纳税人') AS chargedeptname,\n");
		sql.append("       COUNT(p.pk_corp) AS num  \n");
		sql.append("  FROM bd_corp p  \n");
		sql.append("  LEFT JOIN bd_account acc ON p.fathercorp = acc.pk_corp  \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(acc.dr, 0) = 0  \n");
		sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'\n");
		sql.append("   AND nvl(p.isncust, 'N') = 'N' \n");//非存量客户
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");//非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");//非分支机构
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ");
			sql.append(where);
		}
		if (qrytype != null && qrytype == 1) {// 新增客户
			sql.append(" AND SUBSTR(p.createdate, 1, 7) = ? \n");
			spm.addParam(paramvo.getPeriod());
		} else if (qrytype != null && qrytype == 2) {// 新增客户（上月）
			sql.append(" AND SUBSTR(p.createdate, 1, 7) = ? \n");
			String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
			spm.addParam(preperiod);
		} else if (qrytype != null && (qrytype == 3 || qrytype == 4)) {
			sql.append(" AND p.pk_corp IN ( \n");
			sql.append("SELECT t.pk_corpk \n");
			sql.append("  FROM cn_contract t  \n");
			sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n");
			sql.append("  LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp  \n");
			sql.append("  LEFT JOIN bd_corp p ON acc.pk_corp = p.fathercorp  \n");
			sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
			sql.append("   AND nvl(ct.dr, 0) = 0  \n");
			sql.append("   AND nvl(acc.dr, 0) = 0  \n");
			sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'\n");
			sql.append("   AND nvl(p.dr, 0) = 0  \n");
			sql.append("   AND nvl(p.isncust, 'N') = 'N' \n");//非存量客户
			sql.append("   AND nvl(p.isseal,'N') = 'N' \n");//非封存客户
			sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");//非分支机构
			if (corplist != null && corplist.size() > 0) {
				String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
				sql.append(" AND ");
				sql.append(where);
			}
			sql.append(" AND nvl(ct.isxq,'N') = 'Y' ");
			if (qrytype != null && qrytype == 3) {
				if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){//查询扣款客户
					sql.append(" AND SUBSTR(t.deductdata, 1, 7) = ? ");
					spm.addParam(paramvo.getPeriod());
					sql.append("   AND t.vdeductstatus in (?, ?, ? )  \n");
					spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
					spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
					spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
				}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//查询退款客户
					sql.append(" AND SUBSTR(t.dchangetime, 1, 7) = ? ");
					spm.addParam(paramvo.getPeriod());
					sql.append("   AND t.vdeductstatus = ?  \n");
					spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
				}
			} else if (qrytype != null && qrytype == 4) {
				String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
				if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){//查询扣款客户
					sql.append(" AND SUBSTR(t.deductdata, 1, 7) = ? ");
					spm.addParam(preperiod);
					sql.append("   AND t.vdeductstatus in (?, ?, ? )  \n");
					spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
					spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
					spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
				}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//查询退款客户
					sql.append(" AND SUBSTR(t.dchangetime, 1, 7) = ? ");
					spm.addParam(preperiod);
					sql.append("   AND t.vdeductstatus = ?  \n");
					spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
				}
			}
			sql.append(" AND nvl(ct.patchstatus,-1) not in (2, 5) \n");
			sql.append(" ) ");
		}
		sql.append(" GROUP BY p.fathercorp, NVL(p.chargedeptname, '小规模纳税人') \n");

		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			String key = "";
			for (CustCountVO vo : list) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				nummap.put(key, vo.getNum());
			}
		}
		return nummap;
	}

	/**
	 * 查询合同金额
	 * 
	 * @param qrytype
	 * @param corplist
	 * @param qrytype
	 *            查询类型 空：所有数据； 1：新增客户；2：新增客户（上月）；3：续费客户；4：续费客户（上月）；
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, DZFDouble> queryContMny(QryParamVO paramvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		Map<String, DZFDouble> mnymap = new HashMap<String, DZFDouble>();
		// 1、扣款信息：
		List<CustCountVO> plist = qryPositiveData(paramvo, corplist, qrytype);
		String key = "";
		if (plist != null && plist.size() > 0) {
			for (CustCountVO vo : plist) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				mnymap.put(key, vo.getSummny());
			}

		}
		// 2、退款信息：
		List<CustCountVO> nlist = qryNegativeData(paramvo, corplist, qrytype);
		if (nlist != null && nlist.size() > 0) {
			for (CustCountVO vo : nlist) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				if (!mnymap.containsKey(key)) {
					mnymap.put(key, vo.getSummny());
				} else {
					DZFDouble mny = SafeCompute.add(mnymap.get(key), vo.getSummny());
					mnymap.put(key, mny);
				}
			}
		}
		return mnymap;
	}

	/**
	 * 查询扣款数据
	 * 
	 * @param paramvo
	 * @param corplist
	 * @param qrytype
	 *            查询类型 空：所有数据； 1：新增客户；2：新增客户（上月）；3：续费客户；4：续费客户（上月）；
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> qryPositiveData(QryParamVO paramvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp, \n");
		sql.append("       NVL(p.chargedeptname, '小规模纳税人') AS chargedeptname,\n");
		sql.append("       SUM(nvl(ct.nchangetotalmny, 0)) AS summny  \n");
		sql.append("  FROM cn_contract t  \n");
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n");
		sql.append("  LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp  \n");
		sql.append("  LEFT JOIN bd_corp p ON t.pk_corpk = p.pk_corp  \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(acc.dr, 0) = 0  \n");
		sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'\n");
		sql.append("   AND nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isncust, 'N') = 'N' \n");//非存量客户
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");//非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");//非分支机构
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ");
			sql.append(where);
		}
		if (qrytype != null && (qrytype == 1 || qrytype == 2)) {
//			sql.append(" AND t.pk_corpk IN ( ");
//			sql.append("SELECT p.pk_corp \n");
//			sql.append("  FROM bd_corp p  \n");
//			sql.append("  LEFT JOIN bd_account acc ON p.fathercorp = acc.pk_corp  \n");
//			sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
//			sql.append("   AND nvl(acc.dr, 0) = 0  \n");
//			sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'\n");
//			sql.append("   AND nvl(p.isncust, 'N') = 'N'  \n");
//			if (corplist != null && corplist.size() > 0) {
//				String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
//				sql.append(" AND ");
//				sql.append(where);
//			}
//			sql.append(" AND SUBSTR(p.createdate, 1, 7) = ? \n");
//			if (qrytype != null && qrytype == 1) {
//				spm.addParam(paramvo.getPeriod());
//			} else if (qrytype != null && qrytype == 2) {
//				String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
//				spm.addParam(preperiod);
//			}
//			sql.append(" ) ");
			sql.append(" AND nvl(ct.isxq,'N') = 'N' ");
			sql.append(" AND SUBSTR(t.deductdata, 1, 7) = ? ");
			if (qrytype != null && qrytype == 1) {
				spm.addParam(paramvo.getPeriod());
			} else if (qrytype != null && qrytype == 2) {
				String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
				spm.addParam(preperiod);
			}
		} else if (qrytype != null && (qrytype == 3 || qrytype == 4)) {
			sql.append(" AND nvl(ct.isxq,'N') = 'Y' ");
			sql.append(" AND SUBSTR(t.deductdata, 1, 7) = ? ");
			if (qrytype != null && qrytype == 3) {
				spm.addParam(paramvo.getPeriod());
			} else if (qrytype != null && qrytype == 4) {
				String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
				spm.addParam(preperiod);
			}
		}
		sql.append("   AND t.vdeductstatus in (?, ?, ?)  \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		sql.append(" GROUP BY t.pk_corp, NVL(p.chargedeptname, '小规模纳税人') \n");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}

	/**
	 * 查询退款数据
	 * 
	 * @param paramvo
	 * @param corplist
	 * @param qrytype
	 *            查询类型 空：所有数据； 1：新增客户；2：新增客户（上月）；3：续费客户；4：续费客户（上月）；
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> qryNegativeData(QryParamVO paramvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,\n");
		sql.append("       NVL(p.chargedeptname, '小规模纳税人') AS chargedeptname,\n");
		sql.append("       SUM(nvl(t.nsubtotalmny, 0)) AS summny");
//		sql.append("       SUM(CASE t.vstatus  \n");
//		sql.append("             WHEN 9 THEN  \n");
//		sql.append("              nvl(t.nsubtotalmny, 0)\n");
//		sql.append("             ELSE  \n");
//		sql.append("              nvl(t.nsubtotalmny, 0) \n");
//		sql.append("           END) AS summny  \n");
		sql.append("  FROM cn_contract t  \n");
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n");
		sql.append("  LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp  \n");
		sql.append("  LEFT JOIN bd_corp p ON t.pk_corpk = p.pk_corp   \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(acc.dr, 0) = 0  \n");
		sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'\n");
		sql.append("   AND nvl(p.isncust, 'N') = 'N' \n");//非存量客户
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");//非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");//非分支机构
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ");
			sql.append(where);
		}
		if (qrytype != null && (qrytype == 1 || qrytype == 2)) {
//			sql.append(" AND t.pk_corpk IN ( ");
//			sql.append("SELECT p.pk_corp \n");
//			sql.append("  FROM bd_corp p  \n");
//			sql.append("  LEFT JOIN bd_account acc ON p.fathercorp = acc.pk_corp  \n");
//			sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
//			sql.append("   AND nvl(acc.dr, 0) = 0  \n");
//			sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'\n");
//			sql.append("   AND nvl(p.isncust, 'N') = 'N'  \n");
//			if (corplist != null && corplist.size() > 0) {
//				String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
//				sql.append(" AND ");
//				sql.append(where);
//			}
//			sql.append(" ) ");
			sql.append(" AND nvl(ct.isxq,'N') = 'N' ");
			sql.append(" AND SUBSTR(t.dchangetime, 1, 7) = ? ");
			if (qrytype != null && qrytype == 1) {
				spm.addParam(paramvo.getPeriod());
			} else if (qrytype != null && qrytype == 2) {
				String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
				spm.addParam(preperiod);
			}
		} else if (qrytype != null && (qrytype == 3 || qrytype == 4)) {
			sql.append(" AND nvl(ct.isxq,'N') = 'Y' ");
			sql.append(" AND SUBSTR(t.dchangetime, 1, 7) = ? ");
			if (qrytype != null && qrytype == 3) {
				spm.addParam(paramvo.getPeriod());
			} else if (qrytype != null && qrytype == 4) {
				String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
				spm.addParam(preperiod);
			}
		}
		sql.append("   AND t.vdeductstatus in (?, ?)  \n");
		sql.append(" GROUP BY t.pk_corp, NVL(p.chargedeptname, '小规模纳税人') \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}

	/**
	 * 查询非存量客户数量、合同金额
	 * 
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, CustCountVO> queryStockNumMny(QryParamVO paramvo, List<String> corplist)
			throws DZFWarpException {
		Map<String, CustCountVO> stockmap = new HashMap<String, CustCountVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_corp, chargedeptname, COUNT(pk_corpk) AS num, SUM(ntotalmny) AS summny \n");
		sql.append("  FROM (SELECT NVL(ct.chargedeptname, '小规模纳税人') AS chargedeptname, \n");
		sql.append("               t.pk_corp AS pk_corp, \n");
		sql.append("               t.pk_corpk AS pk_corpk, \n");
		sql.append(" 			CASE t.vdeductstatus WHEN 1 THEN ct.ntotalmny");
		sql.append("                WHEN 9 THEN t.nchangetotalmny END AS ntotalmny \n");
		sql.append("          FROM cn_contract t \n");
		sql.append("         INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append("          LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp \n");
		sql.append("          LEFT JOIN bd_corp p ON t.pk_corpk = p.pk_corp   \n");
		sql.append("         WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("           AND nvl(ct.dr, 0) = 0 \n");
		sql.append("           AND nvl(ct.isncust,'N')='N' \n");// 不统计存量客户
		sql.append("   AND nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isncust, 'N') = 'N' \n");//非存量客户
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");//非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");//非分支机构
		if (corplist != null && corplist.size() > 0) {
			String condition = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[corplist.size()]));
			sql.append(" and ");
			sql.append(condition);
		}
		sql.append("           AND nvl(ct.patchstatus, -1) != 2 \n");// 补单合同不统计
		sql.append("           AND nvl(acc.dr, 0) = 0 \n");
		sql.append("           AND nvl(acc.ischannel, 'N') = 'Y'\n");
		sql.append("           AND (ct.vbeginperiod = ? OR ct.vendperiod = ? OR \n");
		spm.addParam(paramvo.getPeriod());
		spm.addParam(paramvo.getPeriod());
		sql.append("                (ct.vbeginperiod < ? AND ct.vendperiod > ? )) \n");
		spm.addParam(paramvo.getPeriod());
		spm.addParam(paramvo.getPeriod());
		// 合同状态 = 已审核 或 已终止
		sql.append("           AND t.vdeductstatus in ( 1 , 9) \n");
		sql.append("   AND t.pk_corp NOT IN \n");
		sql.append("       (SELECT f.pk_corp \n");
		sql.append("          FROM ynt_franchisee f \n");
		sql.append("         WHERE nvl(dr, 0) = 0 \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y')) cu \n");
		sql.append(" GROUP BY pk_corp, chargedeptname");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			String key = "";
			for (CustCountVO vo : list) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				stockmap.put(key, vo);
			}
		}
		return stockmap;
	}

	/**
	 * 查询新增（续费）客户数量、合同金额
	 * 
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
		sql.append("  FROM (SELECT NVL(ct.chargedeptname, '小规模纳税人') AS chargedeptname,\n");
		sql.append("               t.pk_corp AS pk_corp,\n");
		sql.append("			CASE t.vdeductstatus WHEN 1 THEN ct.ntotalmny");
		sql.append("                WHEN 9 THEN t.nchangetotalmny END AS ntotalmny, \n");
		sql.append("               t.pk_corpk AS pk_corpk\n");
		sql.append("          FROM cn_contract t\n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append("          LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp \n");
		sql.append("          LEFT JOIN bd_corp p ON t.pk_corpk = p.pk_corp   \n");
		sql.append("         WHERE nvl(t.dr, 0) = 0\n");
		sql.append("           AND nvl(ct.dr, 0) = 0\n");
		sql.append("   AND nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isncust, 'N') = 'N' \n");//非存量客户
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");//非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");//非分支机构
		if (corplist != null && corplist.size() > 0) {
			String condition = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[corplist.size()]));
			sql.append(" and ");
			sql.append(condition);
		}
		sql.append("           AND nvl(ct.patchstatus, -1) != 2 \n");// 补单合同不统计
		sql.append("           AND nvl(acc.dr, 0) = 0\n");
		sql.append("           AND nvl(acc.ischannel, 'N') = 'Y' \n");
		sql.append("           AND nvl(ct.isncust,'N')='N' \n");// 不统计存量客户
		sql.append("   AND t.pk_corp NOT IN \n");
		sql.append("       (SELECT f.pk_corp \n");
		sql.append("          FROM ynt_franchisee f \n");
		sql.append("         WHERE nvl(dr, 0) = 0 \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		sql.append("           AND SUBSTR(ct.dsigndate, 1, 7) = ? \n");
		spm.addParam(paramvo.getPeriod());
		// 合同状态 = 已审核 或已终止
		sql.append("           AND t.vdeductstatus in ( 1 , 9) \n");
		if (qrytype == 1) {// 新增客户
			sql.append("           AND t.pk_corpk NOT IN \n");
		} else if (qrytype == 2) {// 续费客户
			sql.append("           AND t.pk_corpk IN \n");
		}
		sql.append("               (SELECT t.pk_corpk AS pk_corpk\n");
		sql.append("                  FROM cn_contract t\n");
		sql.append("                  LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp\n");
		sql.append("                 WHERE nvl(t.dr, 0) = 0\n");
		sql.append("                   AND nvl(acc.dr, 0) = 0\n");
		sql.append("                   AND nvl(acc.ischannel, 'N') = 'Y'\n");
		sql.append("   AND t.pk_corp NOT IN \n");
		sql.append("       (SELECT f.pk_corp \n");
		sql.append("          FROM ynt_franchisee f \n");
		sql.append("         WHERE nvl(dr, 0) = 0 \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		sql.append("                   AND SUBSTR(ct.dsigndate, 1, 7) < ? \n");
		spm.addParam(paramvo.getPeriod());
		// 合同状态 = 已审核 或已终止
		sql.append("                   AND t.vdeductstatus in ( 1 , 9))) cu\n");//
		sql.append(" GROUP BY pk_corp, chargedeptname");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			String key = "";
			for (CustCountVO vo : list) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				map.put(key, vo);
			}
		}
		return map;
	}

	/**
	 * 整数类型增长率计算方法
	 * 
	 * @param num1
	 *            本月数据
	 * 
	 * @param num2
	 *            上月数据
	 * 
	 * @return
	 */
	@Override
	public DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		if (DZFDouble.ZERO_DBL.compareTo(num3) == 0) {
			return DZFDouble.ZERO_DBL;
		}
		if (DZFDouble.ZERO_DBL.compareTo(num4) == 0) {
			return DZFDouble.ZERO_DBL;
		}
		DZFDouble num = num3.sub(num4);
		return num.div(num2).multiply(100);
	}

	/**
	 * DZFDouble类型增长率计算方法
	 * 
	 * @param num1
	 *            本月数据
	 * @param num2
	 *            上月数据
	 * @return
	 */
	private DZFDouble getContRate(DZFDouble num1, DZFDouble num2) throws DZFWarpException {
		num1 = num1 == null ? DZFDouble.ZERO_DBL : num1;
		num2 = num2 == null ? DZFDouble.ZERO_DBL : num2;
		if (DZFDouble.ZERO_DBL.compareTo(num1) == 0) {
			return DZFDouble.ZERO_DBL;
		}
		if (DZFDouble.ZERO_DBL.compareTo(num2) == 0) {
			return DZFDouble.ZERO_DBL;
		}
		DZFDouble num = num1.sub(num2);
		return num.div(num2).multiply(100);
	}
	
	/**
	 * 查询合同提单量
	 * @param paramvo
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> queryContNum(QryParamVO paramvo, List<String> corplist) throws DZFWarpException {
		Map<String, Integer> cmap = new HashMap<String, Integer>();

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp, \n");
		// 合同数量去掉补提单合同数
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.patchstatus,0) != 2 AND nvl(ct.patchstatus,0) != 5   \n");
		sql.append("                  AND SUBSTR(t.deductdata, 1, 7) = ? THEN  \n");
		spm.addParam(paramvo.getPeriod());
		sql.append("              1  \n");
		sql.append("             WHEN nvl(ct.patchstatus,0) != 2 AND nvl(ct.patchstatus,0) != 5 \n");
		sql.append("                  AND t.vdeductstatus = 10 AND SUBSTR(t.dchangetime, 1, 7) = ? THEN  \n");
		spm.addParam(paramvo.getPeriod());
		sql.append("              -1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS num  \n");
		sql.append("  FROM cn_contract t \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(ct.dr, 0) = 0 \n");
		sql.append("   AND nvl(ct.isncust, 'N') = 'N' \n");
		sql.append("   AND t.vdeductstatus in (?, ?, ?) \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);

		if (corplist != null && corplist.size() > 0) {
			String condition = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[corplist.size()]));
			sql.append(" and ");
			sql.append(condition);
		}

		sql.append("   AND ( SUBSTR(t.deductdata, 1, 7) = ? OR  \n");
		sql.append("         SUBSTR(t.dchangetime, 1, 7) = ? )  \n");
		spm.addParam(paramvo.getPeriod());
		spm.addParam(paramvo.getPeriod());

		sql.append("   GROUP BY t.pk_corp \n");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			for (CustCountVO cvo : list) {
				cmap.put(cvo.getPk_corp(), cvo.getNum());
			}
		}
		return cmap;
	}

}
