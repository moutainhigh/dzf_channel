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
import com.dzf.model.channel.CorpNameEVO;
import com.dzf.model.channel.payment.ChnBalanceRepVO;
import com.dzf.model.channel.payment.ChnDetailRepVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.IChnPayBalanceService;
import com.dzf.service.pub.IPubService;

@Service("chnpaybalanceser")
public class ChnPayBalanceServiceImpl implements IChnPayBalanceService{
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
    @Autowired
    private IPubService pubService;
    
    private String filtersql = QueryUtil.getWhereSql();
    
	@Override
	public List<ChnBalanceRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<ChnBalanceRepVO> retlist = new ArrayList<ChnBalanceRepVO>();
		List<String> pklist = new ArrayList<String>();
		StringBuffer qrysql = new StringBuffer();//附加查询条件
		//1、渠道经理查询条件
		if(!StringUtil.isEmpty(paramvo.getVmanager())){
			String[] corps = pubService.getManagerCorp(paramvo.getVmanager(), 1);
			if(corps != null && corps.length > 0){
				String where = SqlUtil.buildSqlForIn(" account.pk_corp ", corps);
				qrysql.append(" AND ").append(where);
			}else{
				return null;
			}
		}
		//2、根据当前登陆人和选择的运营大区，获取有权限查询的客户
		String areaname = paramvo.getAreaname();
		String areaqry = pubService.makeCondition(paramvo.getCuserid(), areaname,IStatusConstant.IYUNYING);
		if (areaqry == null) {
			return null;
		} else if (areaqry.equals("alldata")) {
			areaqry = null;
		}
		if(!StringUtil.isEmpty(areaqry)){
			qrysql.append(areaqry);
		}
		if(qrysql != null && qrysql.length() > 0){
			paramvo.setAreaname(qrysql.toString());
		}
		Map<String, ChnBalanceRepVO> initmap = qryDataMap(paramvo, pklist, 1);// 查询期初余额
		Map<String, ChnBalanceRepVO> datamap = qryDataMap(paramvo, pklist, 2);// 查询明细金额
		if (paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2)) {// 全部查询、预付款查询
			// 0扣款按照预付款扣款统计
			qryNoDeductConData(paramvo, pklist);
		}
		
		if (pklist != null && pklist.size() > 0) {
			setReturnValue(paramvo, initmap, datamap, pklist, retlist, areaname);
		}
		return retlist;
	}
	
	/**
	 * 设置返回值
	 * @param paramvo
	 * @param initmap
	 * @param datamap
	 * @param pklist
	 * @param retlist
	 * @param areaname
	 * @throws DZFWarpException
	 */
	private void setReturnValue(QryParamVO paramvo, Map<String, ChnBalanceRepVO> initmap,
			Map<String, ChnBalanceRepVO> datamap, List<String> pklist, List<ChnBalanceRepVO> retlist, String areaname)
			throws DZFWarpException {
		Map<String, ChnBalanceRepVO> posimap = null;// 扣款合同信息
		Map<String, ChnBalanceRepVO> negamap = null;// 退款合同信息
		ChnBalanceRepVO contvo = null;
		// 全部查询、预付款查询、返点查询
		if (paramvo.getQrytype() != null
				&& (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2 || paramvo.getQrytype() == 3)) {
			posimap = qryPositiveData(paramvo, pklist);
			negamap = qryNegativeData(paramvo, pklist);
		}
		ChnBalanceRepVO repvo = null;
		ChnBalanceRepVO vo = null;
		String pk_corp = "";
		CorpVO accvo = null;
		String ipaytype = null;
		Integer paytype = null;
		Map<Integer, String> areaMap = pubService.getAreaMap(areaname, 3);// 运营区域
		Map<String, UserVO> marmap = pubService.getManagerMap(1);// 渠道经理
		UserVO uservo = null;
		for (String pk : pklist) {
			repvo = new ChnBalanceRepVO();
			ipaytype = pk.substring(pk.indexOf(",") + 1);
			pk_corp = pk.substring(0, pk.indexOf(","));
			repvo.setPk_corp(pk_corp);
			accvo = CorpCache.getInstance().get(null, pk_corp);
			if (accvo != null) {
				repvo.setCorpname(accvo.getUnitname());
				repvo.setInnercode(accvo.getInnercode());
				repvo.setDrelievedate(accvo.getDrelievedate());
				if (areaMap != null && !areaMap.isEmpty()) {
					repvo.setAreaname(areaMap.get(accvo.getVprovince()));
				}
			}
			if (!StringUtil.isEmpty(paramvo.getCorpname())) {
				if (repvo.getInnercode().indexOf(paramvo.getCorpname()) == -1
						&& repvo.getCorpname().indexOf(paramvo.getCorpname()) == -1) {
					continue;
				}
			}
			paytype = Integer.parseInt(ipaytype);

			setShowName(repvo, paytype, marmap, uservo, pk_corp);

			// 设置合同相关数量及金额
			setContValue(paramvo, repvo, paytype, posimap, negamap, contvo);

			// 设置期初及使用金额
			setInitAndUsedMny(initmap, datamap, vo, paytype, repvo, pk);

			// 计算余额
			countBalance(repvo);

			retlist.add(repvo);
		}
	}
	
	/**
	 * 设置显示名称
	 * @param repvo
	 * @param paytype
	 * @param marmap
	 * @param uservo
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private void setShowName(ChnBalanceRepVO repvo, Integer paytype, Map<String, UserVO> marmap, UserVO uservo,
			String pk_corp) throws DZFWarpException {
		repvo.setIpaytype(paytype);
		if (paytype == 1) {
			repvo.setVpaytypename("保证金");
		} else if (paytype == 2) {
			repvo.setVpaytypename("预付款");
		} else if (paytype == 3) {
			repvo.setVpaytypename("返点");
		}
		if (marmap != null && !marmap.isEmpty()) {
			uservo = marmap.get(pk_corp);
			if (uservo != null) {
				repvo.setVmanagername(uservo.getUser_name());// 渠道经理
			}
		}
	}
	
	/**
	 * 设置期初及使用金额
	 * @param initmap
	 * @param datamap
	 * @param vo
	 * @param paytype
	 * @param repvo
	 * @param pk
	 * @throws DZFWarpException
	 */
	private void setInitAndUsedMny(Map<String, ChnBalanceRepVO> initmap, Map<String, ChnBalanceRepVO> datamap,
			ChnBalanceRepVO vo, Integer paytype, ChnBalanceRepVO repvo, String pk) throws DZFWarpException {
		// 期初余额
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

		// 使用金额
		if (datamap != null && !datamap.isEmpty()) {
			vo = datamap.get(pk);
			if (vo != null) {
				if (paytype == 1) {
					repvo.setNpaymny(vo.getBail());
				} else if (paytype == 2) {
					repvo.setNpaymny(vo.getNpaymny());
					repvo.setNusedmny(vo.getNusedmny());
					repvo.setIdeductpropor(vo.getIdeductpropor());
					repvo.setNcondedmny(vo.getNyfhtmny());
					repvo.setNbuymny(vo.getNyfspmny());
				} else if (paytype == 3) {
					repvo.setNpaymny(vo.getNpaymny());
					repvo.setNusedmny(vo.getNusedmny());
					repvo.setIdeductpropor(vo.getIdeductpropor());
					repvo.setNcondedmny(vo.getNfdhtmny());
					repvo.setNbuymny(vo.getNfdspmny());
				}
			}
		}
	}
	
	/**
	 * 设置合同相关数量及金额
	 * @param paramvo
	 * @param repvo
	 * @param paytype
	 * @param posimap
	 * @param negamap
	 * @param contvo
	 * @throws DZFWarpException
	 */
	private void setContValue(QryParamVO paramvo, ChnBalanceRepVO repvo, Integer paytype,
			Map<String, ChnBalanceRepVO> posimap, Map<String, ChnBalanceRepVO> negamap, ChnBalanceRepVO contvo)
			throws DZFWarpException {
		// 全部查询、预付款查询时，存量合同数、0扣款(非存量)合同数、非存量合同数、合同代账费、账本费显示在预付款上
		if (paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2)) {
			if (paytype == 2) {
				if (posimap != null && !posimap.isEmpty()) {
					contvo = posimap.get(repvo.getPk_corp());
					if (contvo != null) {
						repvo.setIcustnum(contvo.getIcustnum());
						repvo.setIzeronum(contvo.getIzeronum());
						repvo.setIdednum(contvo.getIdednum());
						repvo.setNaccountmny(contvo.getNaccountmny());
						repvo.setNbookmny(contvo.getNbookmny());
					}
				}
				if (negamap != null && !negamap.isEmpty()) {
					contvo = negamap.get(repvo.getPk_corp());
					if (contvo != null) {
						repvo.setIcustnum(ToolsUtil.addInteger(repvo.getIcustnum(), contvo.getIcustnum()));
						repvo.setIzeronum(ToolsUtil.addInteger(repvo.getIzeronum(), contvo.getIzeronum()));
						repvo.setIdednum(ToolsUtil.addInteger(repvo.getIdednum(), contvo.getIdednum()));
						repvo.setNaccountmny(SafeCompute.add(repvo.getNaccountmny(), contvo.getNaccountmny()));
						repvo.setNbookmny(SafeCompute.add(repvo.getNbookmny(), contvo.getNbookmny()));
					}
				}
				repvo.setNum(CommonUtil.getInteger(repvo.getIcustnum()) + CommonUtil.getInteger(repvo.getIzeronum())
						+ CommonUtil.getInteger(repvo.getIdednum()));
			}
			// 返点查询时，存量合同数、0扣款(非存量)合同数、非存量合同数、合同代账费、账本费显示在返点上
		} else if (paramvo.getQrytype() != null && (paramvo.getQrytype() == 3)) {
			if (paytype == 3) {
				if (posimap != null && !posimap.isEmpty()) {
					contvo = posimap.get(repvo.getPk_corp());
					if (contvo != null) {
						repvo.setIcustnum(contvo.getIcustnum());
						repvo.setIzeronum(contvo.getIzeronum());
						repvo.setIdednum(contvo.getIdednum());
						repvo.setNaccountmny(contvo.getNaccountmny());
						repvo.setNbookmny(contvo.getNbookmny());
					}
				}
				if (negamap != null && !negamap.isEmpty()) {
					contvo = negamap.get(repvo.getPk_corp());
					if (contvo != null) {
						repvo.setIcustnum(ToolsUtil.addInteger(repvo.getIcustnum(), contvo.getIcustnum()));
						repvo.setIzeronum(ToolsUtil.addInteger(repvo.getIzeronum(), contvo.getIzeronum()));
						repvo.setIdednum(ToolsUtil.addInteger(repvo.getIdednum(), contvo.getIdednum()));
						repvo.setNaccountmny(SafeCompute.add(repvo.getNaccountmny(), contvo.getNaccountmny()));
						repvo.setNbookmny(SafeCompute.add(repvo.getNbookmny(), contvo.getNbookmny()));
					}
				}
				repvo.setNum(CommonUtil.getInteger(repvo.getIcustnum()) + CommonUtil.getInteger(repvo.getIzeronum())
						+ CommonUtil.getInteger(repvo.getIdednum()));
			}
		}
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
		sql.append("SELECT DISTINCT t.pk_corp \n") ;
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" LEFT JOIN bd_account account on t.pk_corp=account.pk_corp ");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(t.ndedsummny, 0) = 0  \n") ; 
		sql.append("   AND t.vstatus = 1  \n") ; 
//		String[] billids = getbillids();
//		if(billids != null && billids.length > 0){
//			String where = ToolsUtil.buildSqlForNotIn("t.pk_confrim", billids);
//			sql.append(" AND ").append(where);
//		}
		sql.append(" and not exists ( SELECT pk_bill FROM cn_detail det \n");
		sql.append("  WHERE det.pk_bill = t.pk_confrim and nvl(dr, 0) = 0 AND ipaytype = 2) \n");
		if(paramvo.getCorps() != null && paramvo.getCorps().length > 0){
			String where = SqlUtil.buildSqlForIn("t.pk_corp", paramvo.getCorps());
			sql.append(" AND ").append(where);
	    }
		
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());//根据当前登陆人和选择的运营大区，获取有权限查询的客户
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
		if(StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
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
	 * 查询扣款合同信息
	 * @param paramvo
	 * @param pklist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, ChnBalanceRepVO> qryPositiveData(QryParamVO paramvo, List<String> pklist) throws DZFWarpException {
		Map<String, ChnBalanceRepVO> posimap = new HashMap<String, ChnBalanceRepVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  \n");
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2   \n");
		sql.append("                  AND nvl(ct.patchstatus, 0) != 5 THEN \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS icustnum,  \n");//存量合同数：不包含小规模转一般人和一般人转小规模
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) = 0   \n");
		sql.append("                  AND nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS izeronum,  \n");//0扣款(非存量)合同数：不包含小规模转一般人和一般人转小规模
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) != 0   \n");
		sql.append("                  AND nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS idednum,  \n");//非存量合同数：不包含小规模转一般人和一般人转小规模
		sql.append("       SUM(nvl(ct.nchangetotalmny, 0) - nvl(ct.nbookmny, 0)) AS naccountmny,  \n");
		sql.append("       SUM(nvl(ct.nbookmny, 0)) AS nbookmny  \n");
		sql.append("  FROM cn_contract t  \n");
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n");
		sql.append(" LEFT JOIN bd_account account on t.pk_corp=account.pk_corp ");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.dr, 0) = 0  \n");
		if (paramvo.getCorps() != null && paramvo.getCorps().length > 0) {
			String where = SqlUtil.buildSqlForIn("t.pk_corp", paramvo.getCorps());
			sql.append(" AND ").append(where);
		}
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());//根据当前登陆人和选择的运营大区，获取有权限查询的客户
		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {//预付款扣款
			//正常和作废扣款：1、预付款扣款金额不为0；2、扣款总金额为0；
			//变更扣款：1、状态为变更，且变更后预付款扣款金额不为0；
			sql.append(" AND ( (  ( nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 )  AND t.vstatus IN (?, ?) ) \n");
			sql.append(" OR  t.vstatus = ?  )\n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//返点扣款
			//正常和作废扣款：1、返点扣款金额不为0；
			//变更扣款：1、状态为变更，返点扣款金额不为0；
			sql.append(" AND ( nvl(t.ndedrebamny,0) != 0 AND t.vstatus IN (?, ?, ?) )  \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		}else{
			sql.append("   AND t.vstatus IN (?, ?, ?) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		if (!StringUtil.isEmpty(paramvo.getPeriod())) {
			if (!StringUtil.isEmpty(paramvo.getBeginperiod())) {
				sql.append(" AND substr(t.deductdata,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if (!StringUtil.isEmpty(paramvo.getEndperiod())) {
				sql.append(" AND substr(t.deductdata,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		} else {
			if (paramvo.getBegdate() != null) {
				sql.append(" AND t.deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append(" AND t.deductdata <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" GROUP BY t.pk_corp \n");
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnBalanceRepVO.class));
		if(list != null && list.size() > 0){
			for(ChnBalanceRepVO repvo : list){
				posimap.put(repvo.getPk_corp(), repvo);
			}
		}
		return posimap;
	}
	
	/**
	 * 查询退款合同信息
	 * @param paramvo
	 * @param pklist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, ChnBalanceRepVO> qryNegativeData(QryParamVO paramvo, List<String> pklist) throws DZFWarpException {
		Map<String, ChnBalanceRepVO> negamap = new HashMap<String, ChnBalanceRepVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  \n") ;
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2   \n") ; 
		sql.append("             	  AND nvl(ct.patchstatus, 0) != 5 AND t.vstatus = 10 THEN  \n") ; 
		sql.append("              -1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS icustnum,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) = 0 AND nvl(ct.patchstatus, 0) != 2 \n") ; 
		sql.append("                   AND nvl(ct.patchstatus, 0) != 5 AND t.vstatus = 10 THEN  \n") ; 
		sql.append("              -1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS izeronum,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) != 0 AND nvl(ct.patchstatus, 0) != 2  \n") ; 
		sql.append("                  AND nvl(ct.patchstatus, 0) != 5 AND t.vstatus = 10 THEN  \n") ; 
		sql.append("              -1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS idednum,  \n") ; 
		sql.append("       SUM(CASE t.vstatus  \n") ; 
		sql.append("             WHEN 9 THEN  \n") ; 
		sql.append("              nvl(t.nsubtotalmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              nvl(t.nsubtotalmny, 0) + nvl(ct.nbookmny, 0)  \n") ; 
		sql.append("           END) AS naccountmny,  \n") ; 
		sql.append("       -abs(SUM(CASE  \n") ; 
		sql.append("                  WHEN t.vstatus = 10 THEN  \n") ; 
		sql.append("                   nvl(ct.nbookmny, 0)  \n") ; 
		sql.append("                  ELSE  \n") ; 
		sql.append("                   0  \n") ; 
		sql.append("                END)) AS nbookmny  \n") ; 
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n") ; 
		sql.append("  LEFT JOIN bd_account account on t.pk_corp=account.pk_corp ");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.dr, 0) = 0  \n") ; 
		if (paramvo.getCorps() != null && paramvo.getCorps().length > 0) {
			String where = SqlUtil.buildSqlForIn("t.pk_corp", paramvo.getCorps());
			sql.append(" AND ").append(where);
		}
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());//根据当前登陆人和选择的运营大区，获取有权限查询的客户
		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {//预付款扣款
			sql.append(" AND (  t.vstatus = ?  \n");
			sql.append("  OR ( (nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 ) AND t.vstatus = ? ) ) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//返点扣款
			sql.append(" AND ( nvl(t.ndedrebamny,0) != 0 AND t.vstatus in ( ?, ? ) )  \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}else{
			sql.append("   AND t.vstatus IN (?, ?) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		if (!StringUtil.isEmpty(paramvo.getPeriod())) {
			if (!StringUtil.isEmpty(paramvo.getBeginperiod())) {
				sql.append(" AND substr(t.dchangetime,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if (!StringUtil.isEmpty(paramvo.getEndperiod())) {
				sql.append(" AND substr(t.dchangetime,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		} else {
			if (paramvo.getBegdate() != null) {
				sql.append(" AND substr(t.dchangetime,1,10) >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append(" AND substr(t.dchangetime,1,10) <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" GROUP BY t.pk_corp \n");
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnBalanceRepVO.class));
		if(list != null && list.size() > 0){
			for(ChnBalanceRepVO repvo : list){
				negamap.put(repvo.getPk_corp(), repvo);
			}
		}
		return negamap;
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
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
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
		sql.append("SELECT a.pk_corp, \n") ;
		sql.append("       a.ipaytype, \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 1, nvl(a.npaymny,0), 0)) AS bail, \n") ; //保证金
		sql.append("       SUM(decode(a.ipaytype, 2, nvl(a.npaymny,0), 0)) - \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 2, nvl(a.nusedmny,0), 0)) AS charge, \n") ; //预付款
		sql.append("       SUM(decode(a.ipaytype, 3, nvl(a.npaymny,0), 0)) - \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 3, nvl(a.nusedmny,0), 0)) AS rebate \n") ; //返点
		sql.append("  FROM cn_detail a \n") ; 
		sql.append("  LEFT JOIN bd_account account on a.pk_corp = account.pk_corp ");
		sql.append(" WHERE nvl(a.dr, 0) = 0 \n") ; 
		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
	        sql.append(" and a.pk_corp in (" + corpIdS + ")");
	    }
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());//根据当前登陆人和选择的运营大区，获取有权限查询的客户
		}
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND a.ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}else{
			sql.append(" AND a.ipaytype in (2, 3) \n");
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(a.doperatedate,1,7) < ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND a.doperatedate < ? \n");
				spm.addParam(paramvo.getBegdate());
			}
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
			sql.append(" AND a.pk_corp = ? \n");
			spm.addParam(paramvo.getPk_corp());
		}
		if(StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		sql.append(" GROUP BY a.pk_corp, a.ipaytype \n");
		sql.append(" ORDER BY a.pk_corp \n");
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
		sql.append("SELECT a.pk_corp, \n") ;
		sql.append("       a.ipaytype, \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 1, nvl(a.npaymny,0), 0)) AS bail, \n") ; 
		sql.append("       SUM(nvl(a.npaymny,0)) AS npaymny, \n") ; 
		sql.append("       SUM(nvl(a.nusedmny,0)) AS nusedmny, \n") ; 
		sql.append("       SUM(CASE WHEN a.ipaytype = 2 AND a.iopertype = 2  THEN nvl(a.nusedmny, 0) ELSE 0 END) AS nyfhtmny, \n");
		sql.append("       SUM(CASE WHEN a.ipaytype = 2 AND a.iopertype = 5  THEN nvl(a.nusedmny, 0) ELSE 0 END) AS nyfspmny, \n");
		sql.append("       SUM(CASE WHEN a.ipaytype = 3 AND a.iopertype = 2  THEN nvl(a.nusedmny, 0) ELSE 0 END) AS nfdhtmny, \n");
		sql.append("       SUM(CASE WHEN a.ipaytype = 3 AND a.iopertype = 5  THEN nvl(a.nusedmny, 0) ELSE 0 END) AS nfdspmny, \n");
		sql.append("       MIN(CASE a.ideductpropor WHEN 0 THEN NULL ELSE a.ideductpropor END) AS ideductpropor \n") ; 
		sql.append("  FROM cn_detail a \n") ; 
		sql.append("  LEFT JOIN bd_account account on a.pk_corp=account.pk_corp ");
		sql.append(" WHERE nvl(a.dr, 0) = 0 \n") ; 
		if(paramvo.getCorps() != null  && paramvo.getCorps().length > 0){
	        String where = SqlUtil.buildSqlForIn("a.pk_corp", paramvo.getCorps());
	        sql.append(" AND ").append(where);
	    }
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());//根据当前登陆人和选择的运营大区，获取有权限查询的客户
		}
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND a.ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}else{
			sql.append(" AND a.ipaytype in (2, 3) \n");
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(a.doperatedate,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(a.doperatedate,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND a.doperatedate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND a.doperatedate <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		if(StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		sql.append(" GROUP BY a.pk_corp,a.ipaytype \n");
		sql.append(" ORDER BY a.pk_corp \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnDetailRepVO> queryDetail(QryParamVO paramvo) throws DZFWarpException {
		List<ChnDetailRepVO> retlist = new ArrayList<ChnDetailRepVO>();
		//1、查询明细期初数据
		DZFDouble coutbal = getDetInitData(paramvo, retlist);

		//2、查询明细数据
		QrySqlSpmVO sqpvo = getDetailQry(paramvo);
		List<ChnDetailRepVO> list = (List<ChnDetailRepVO>) singleObjectBO.executeQuery(sqpvo.getSql(), sqpvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
		
		//3、查询零扣款数据
		if (paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2)) {
			//查询全部或查询预付款时，把零扣款的数据查询出来
			List<ChnDetailRepVO> zerolist = qryZeroDeduction(sqpvo,list, paramvo);// 查询扣费为0的数据
			if(zerolist != null && zerolist.size() > 0){
				list.addAll(zerolist);
			}
		}
		if (list != null && list.size() > 0) {
			List<ChnDetailRepVO> orderlist = getOrderList(list);
			CorpVO corpvo = null;
			DZFDouble balance = DZFDouble.ZERO_DBL;
			Map<String, ChnDetailRepVO> contmap = null;// 合同信息
			// 查询全部（显示在预付款上）、预付款、返点，查询合同明细
			if (paramvo.getQrytype() != null
					&& (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2 || paramvo.getQrytype() == 3)) {
				contmap = queryConDetail(paramvo);
			}
			
			//获取客户最后一次变更的原客户名称
			Map<String, String> nmap = getOldName(orderlist.get(0).getPk_corp());
			
			for (ChnDetailRepVO repvo : orderlist) {
				corpvo = CorpCache.getInstance().get(null, repvo.getPk_corp());
				if (corpvo != null) {
					repvo.setCorpname(corpvo.getUnitname());
				}
				corpvo = CorpCache.getInstance().get(null, repvo.getPk_corpk());
				if(corpvo != null){
					repvo.setCorpkname(corpvo.getUnitname());
				}
				if(nmap != null && !nmap.isEmpty()){
					repvo.setVoldname(nmap.get(repvo.getPk_corpk()));
				}
				//设置类型名称：
				setTypeName(repvo);
				//设置合同相关金额、合同扣款相关的备注
				setContData(paramvo, contmap, repvo);
				balance = SafeCompute.sub(repvo.getNpaymny(), repvo.getNusedmny());
				repvo.setNbalance(SafeCompute.add(coutbal, balance));
				coutbal = repvo.getNbalance();
				retlist.add(repvo);
			}
		}
		if(retlist != null && retlist.size() > 0){
			//设置渠道经理显示
			ChnDetailRepVO firstvo = retlist.get(0);
			String manager = pubService.getManagerName(firstvo.getPk_corp());
			firstvo.setVmanagername(manager);
		}
		return retlist;
	}
	
	/**
	 * 获取客户最后一次变更的原客户名称
	 * @param fathercorp
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> getOldName(String fathercorp) throws DZFWarpException {
		Map<String, String> nmap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ed.pk_corp, ed.voldname  \n");
		sql.append("  FROM (SELECT ROW_NUMBER() OVER(PARTITION BY pk_corpnameedit ORDER BY ts DESC) rn,  \n");
		sql.append("               e.pk_corp,  \n");
		sql.append("               e.voldname  \n");
		sql.append("          FROM cn_corpnameedit e  \n");
		sql.append("         WHERE nvl(e.dr, 0) = 0  \n");
		sql.append("           AND e.istatus = 2  \n");
		sql.append("           AND e.fathercorp = ? ) ed  \n");
		spm.addParam(fathercorp);
		sql.append(" where ed.rn = 1  \n");
		List<CorpNameEVO> list = (List<CorpNameEVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CorpNameEVO.class));
		if(list != null && list.size() > 0){
			QueryDeCodeUtils.decKeyUtils(new String[]{"voldname"}, list.toArray(new CorpNameEVO[0]), 1);
			for(CorpNameEVO evo : list){
				nmap.put(evo.getPk_corp(), evo.getVoldname());
			}
		}
		return nmap;
	}
	
	/**
	 * 设置类型名称
	 * @param repvo
	 * @throws DZFWarpException
	 */
	private void setTypeName(ChnDetailRepVO repvo) throws DZFWarpException {
		if (repvo.getIpaytype() != null) {
			switch (repvo.getIpaytype()) {
			case 1:
				repvo.setVpaytypename("保证金");
				break;
			case 2:
				repvo.setVpaytypename("预付款");
				break;
			case 3:
				repvo.setVpaytypename("返点");
				break;
			}
		}
	}
	
	/**
	 * 设置合同相关金额、合同扣款相关的备注
	 * @param paramvo
	 * @param contmap
	 * @param repvo
	 * @throws DZFWarpException
	 */
	private void setContData(QryParamVO paramvo, Map<String, ChnDetailRepVO> contmap, 
			ChnDetailRepVO repvo) throws DZFWarpException{
		if (contmap != null && !contmap.isEmpty()) {
			String key = "1" + repvo.getPk_bill();
			//加盟商合同类型（null正常合同；1：被2补提交的原合同；2：小规模转一般人的合同；3：变更合同;4：被5补提交的原合同;5:一般人转小规模的合同）
			//零扣款的合同：0扣款：patchstatus值为1；0退款：patchstatus值为3；
			//正常扣款的合同：1：被2补提交的原合同；2：小规模转一般人的合同；4：被5补提交的原合同;5:一般人转小规模的合同
			//           特殊情况：正常扣款后，变更的合同，patchstatus值都为3，但是扣款金额一正一负
			if(CommonUtil.getDZFDouble(repvo.getNusedmny()).compareTo(DZFDouble.ZERO_DBL) <= 0
					&& repvo.getPatchstatus() != null && repvo.getPatchstatus() == 3){//变更合同
				key = "-1" + repvo.getPk_bill();
			}
			if (contmap.containsKey(key)) {
				ChnDetailRepVO contvo = contmap.get(key);
				if (paramvo.getQrytype() != null && paramvo.getQrytype() == -1) {// 查询全部时，展示预付款和返点的合同相关金额
					if (repvo.getIpaytype() == 3 && repvo.getIopertype() == 2) {// 返点扣款或返点退款
						if (contvo.getIdeductype() != null && contvo.getIdeductype() == 3) {// 全部返点扣款
							repvo.setNaccountmny(contvo.getNaccountmny());
							repvo.setNbookmny(contvo.getNbookmny());
						}
					} else {
						repvo.setNaccountmny(contvo.getNaccountmny());
						repvo.setNbookmny(contvo.getNbookmny());
					}
				} else {
					repvo.setNaccountmny(contvo.getNaccountmny());
					repvo.setNbookmny(contvo.getNbookmny());
				}
				//设置合同扣款相关的备注
				setShowMemo(repvo);
			}
		}
	}
	
	/**
	 * 设置合同扣款相关的备注
	 * @param repvo
	 * @throws DZFWarpException
	 */
	private void setShowMemo(ChnDetailRepVO repvo) throws DZFWarpException {
		if(CommonUtil.getDZFDouble(repvo.getNusedmny()).compareTo(DZFDouble.ZERO_DBL) != 0){
			//零扣款的数据的备注已经在查询时组装好，不需要重新处理
			//加盟商合同类型（null正常合同；1：被2补提交的原合同；2：小规模转一般人的合同；3：变更合同;4：被5补提交的原合同;5:一般人转小规模的合同）
			//1、正常扣款数据，需要区分存量客户和非存量客户：value = null,1,4
			if (repvo.getPatchstatus() == null || (repvo.getPatchstatus() != null
					&& (repvo.getPatchstatus() == 1 || repvo.getPatchstatus() == 4))) {
				if (repvo.getIsncust() != null && repvo.getIsncust().booleanValue()) {
					repvo.setVmemo("存量客户");
				} else {
					repvo.setVmemo("");
				}
			}else if(repvo.getPatchstatus() != null && repvo.getPatchstatus() == 3){
				//2、正常扣款后，变更的合同，按照扣款金额区分扣款或退款，按照合同状态区分终止或作废：value = 3
				if(CommonUtil.getDZFDouble(repvo.getNusedmny()).compareTo(DZFDouble.ZERO_DBL) > 0){
					if(repvo.getIsncust() != null && repvo.getIsncust().booleanValue()){
						repvo.setVmemo("存量客户");
					}else{
						repvo.setVmemo("");
					}
				}else if(CommonUtil.getDZFDouble(repvo.getNusedmny()).compareTo(DZFDouble.ZERO_DBL) < 0){
					if(repvo.getVstatus() == IStatusConstant.IDEDUCTSTATUS_9){
						repvo.setVmemo("合同终止");
					}else if(repvo.getVstatus() == IStatusConstant.IDEDUCTSTATUS_10){
						repvo.setVmemo("合同作废");
					}
				}
				
			} else if (repvo.getPatchstatus() != null
					&& (repvo.getPatchstatus() == 2 || repvo.getPatchstatus() == 5)) {
				//3、纳税人变更合同：
				if(repvo.getPatchstatus() == 2){
					if (repvo.getIsncust() != null && repvo.getIsncust().booleanValue()) {
						repvo.setVmemo("存量客户：小规模转一般人");
					} else {
						repvo.setVmemo("小规模转一般人");
					}
				}else if(repvo.getPatchstatus() == 5){
					if (repvo.getIsncust() != null && repvo.getIsncust().booleanValue()) {
						repvo.setVmemo("存量客户：一般人转小规模");
					} else {
						repvo.setVmemo("一般人转小规模");
					}
				}
			}
		}
	}
	
	/**
	 * 获取明细的期初数据
	 * @param paramvo
	 * @param retlist
	 * @param pklist
	 * @param coutbal
	 * @throws DZFWarpException
	 */
	private DZFDouble getDetInitData(QryParamVO paramvo, List<ChnDetailRepVO> retlist) 
			throws DZFWarpException {
		DZFDouble coutbal = DZFDouble.ZERO_DBL;
		List<String> pklist = new ArrayList<String>();
		Map<String, ChnBalanceRepVO> initmap = qryDataMap(paramvo, pklist, 1);
		if (initmap != null && !initmap.isEmpty()) {
			if (paramvo.getQrytype() != null && paramvo.getQrytype() != -1) {// 分类查询
				ChnBalanceRepVO repvo = initmap.get(paramvo.getPk_corp() + "," + paramvo.getQrytype());
				if (repvo != null) {
					ChnDetailRepVO initvo = new ChnDetailRepVO();
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
			} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == -1) {// 全部查询
				ChnDetailRepVO initvo = new ChnDetailRepVO();
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
				ChnBalanceRepVO repvo = initmap.get(paramvo.getPk_corp() + "," + 2);// 预付款期初余额
				if (repvo != null) {
					initvo.setNbalance(repvo.getCharge());
					coutbal = repvo.getCharge();
				}
				repvo = initmap.get(paramvo.getPk_corp() + "," + 3);// 返点期初余额
				if (repvo != null) {
					initvo.setNbalance(SafeCompute.add(initvo.getNbalance(), repvo.getRebate()));
					coutbal = SafeCompute.add(coutbal, repvo.getRebate());
				}
				initvo.setVpaytypename("预付款+返点");
				retlist.add(initvo);
			}
		}
		return coutbal;
	}
	
	/**
	 * 查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @return
	 */
	private List<ChnDetailRepVO> qryZeroDeduction(QrySqlSpmVO sqpvo,List<ChnDetailRepVO> list, QryParamVO paramvo) {
		List<ChnDetailRepVO> zerolist = new ArrayList<ChnDetailRepVO>();
		List<String> pklist = new ArrayList<String>();
		for (ChnDetailRepVO repvo : list) {
			pklist.add(repvo.getPk_bill());
		}
		String addwhere = "";
		if(pklist != null && pklist.size() > 0){
			StringBuffer asql = new StringBuffer();
			String where = ToolsUtil.buildSqlForNotIn("t.pk_confrim", pklist.toArray(new String[0]));
			asql.append(" AND ").append(where);
			addwhere = asql.toString();
		}
		//1、查询零扣款数据：
		List<ChnDetailRepVO> dlist = qryZeroDeduct(list, paramvo, addwhere);
		if(dlist != null && dlist.size() > 0){
			zerolist.addAll(dlist);
		}
		//2、查询零退款数据：
		List<ChnDetailRepVO> clist = qryZeroChange(list, paramvo, addwhere);
		if(clist != null && clist.size() > 0){
			zerolist.addAll(clist);
		}
		return zerolist;
	}
	
	/**
	 * 根据变更日期，查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ChnDetailRepVO> qryZeroChange(List<ChnDetailRepVO> list, QryParamVO paramvo, String addwhere) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_confrim AS pk_bill,  \n") ;
		sql.append("       substr(t.dchangetime, 1, 10) AS doperatedate,  \n") ; 
		sql.append("       t.pk_corpk AS pk_corp,  \n") ; 
		sql.append("       t.vstatus AS dr,  \n") ; 
		sql.append("       2 AS ipaytype,  \n") ; 
		sql.append("       2 AS iopertype,  \n") ; 
		sql.append("       ct.vcontcode,  \n") ; 
		sql.append("       ct.pk_corpk,  \n") ; 
		sql.append("       0.00 AS nusedmny,  \n") ; 
		sql.append("       3 AS patchstatus,  \n") ;
		sql.append("       decode(nvl(ct.vstatus, -1), 9, '合同终止', '合同作废') as vmemo  \n") ; 
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0  \n") ; 
		sql.append("   AND t.pk_corp = ?  \n") ; 
		sql.append("   AND t.ideductpropor = 0  \n") ; 
		sql.append("   AND t.vstatus IN (?, ?)");
		spm.addParam(paramvo.getPk_corp());
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if(!StringUtil.isEmpty(addwhere)){
			sql.append(addwhere);
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
		return (List<ChnDetailRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnDetailRepVO.class));
	}
	
	/**
	 * 根据扣款日期，查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ChnDetailRepVO> qryZeroDeduct(List<ChnDetailRepVO> list, QryParamVO paramvo, String addwhere) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_confrim AS pk_bill,  \n") ;
		sql.append("       t.deductdata AS doperatedate,  \n") ; 
		sql.append("       t.pk_corpk AS pk_corp,  \n") ; 
		sql.append("       2 AS ipaytype,  \n") ; 
		sql.append("       2 AS iopertype,  \n") ; 
		sql.append("       ct.vcontcode,  \n") ; 
		sql.append("       ct.pk_corpk,  \n") ; 
		sql.append("       0.00 AS nusedmny,  \n") ; 
		sql.append("       1 AS patchstatus,  \n") ; 
		sql.append("       CASE  \n") ; 
		sql.append("         WHEN nvl(ct.patchstatus, -1) = 2 THEN  \n") ; 
		sql.append("          CONCAT(decode(nvl(ct.isncust, 'N'), 'Y', '存量客户：', ''), '小规模转一般人')  \n") ; 
		sql.append("         WHEN nvl(ct.patchstatus, -1) = 5 THEN  \n") ; 
		sql.append("          CONCAT(decode(nvl(ct.isncust, 'N'), 'Y', '存量客户：', ''), '一般人转小规模')  \n") ; 
		sql.append("         ELSE  \n") ; 
		sql.append("          decode(nvl(ct.isncust, 'N'), 'Y', '存量客户', '')  \n") ; 
		sql.append("       END as vmemo  \n") ;
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0  \n") ; 
		sql.append("   AND t.pk_corp = ?  \n") ; 
		sql.append("   AND t.ideductpropor = 0  \n") ; 
		sql.append("   AND t.vstatus IN (?, ? , ?) \n");
		spm.addParam(paramvo.getPk_corp());
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if(!StringUtil.isEmpty(addwhere)){
			sql.append(addwhere);
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
		return (List<ChnDetailRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnDetailRepVO.class));
	}
	/**
	 * 查询付款单余额明细的合同相关数据
	 * @param paramvo  
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String,ChnDetailRepVO> queryConDetail(QryParamVO paramvo) throws DZFWarpException {
		HashMap<String, ChnDetailRepVO> map = new HashMap<String, ChnDetailRepVO>();
		DZFDate begdate = null;
		DZFDate enddate = null;
		//如果为期间查询，则把期间转换为开始日期（开始期间+'-01'），结束日期（结束期间下一个月减去1天），即开始期间月初日期、结束期间月末日期
		if (!StringUtil.isEmpty(paramvo.getPeriod())) {
			begdate = new DZFDate(paramvo.getBeginperiod() + "-01");
			enddate = new DZFDate(paramvo.getEndperiod() + "-01");
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(enddate.getMillis()));
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DATE, -1);
			enddate = new DZFDate(cal.getTime());
		} else {
			begdate = paramvo.getBegdate();
			enddate = paramvo.getEnddate();
		}
		//1、查询扣款合同信息：
		QrySqlSpmVO qryvo = getQrySql(1, begdate, enddate, paramvo);
		List<ChnDetailRepVO> kklist = (List<ChnDetailRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
		for (ChnDetailRepVO repvo : kklist) {
			String key = "1" + repvo.getPk_bill();
			map.put(key, repvo);
		}
		
		//2、查询变更合同信息：
		qryvo = getQrySql(2, begdate, enddate, paramvo);
		List<ChnDetailRepVO> bglist = (List<ChnDetailRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
		for (ChnDetailRepVO repvo : bglist) {
			String key = "-1" + repvo.getPk_bill();
			map.put(key, repvo);
		}
		
		//3、查询作废合同信息：
		qryvo = getQrySql(3, begdate, enddate, paramvo);
		List<ChnDetailRepVO> zflist = (List<ChnDetailRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
		for (ChnDetailRepVO repvo : zflist) {
			String key = "-1" + repvo.getPk_bill();
			map.put(key, repvo);
		}
		return map;
	}
	
	/**
	 * 获取查询语句
	 * @param qrytype  1：扣款；2：变更；3：作废；
	 * @param begdate
	 * @param enddate
	 * @param paramvo
	 * @return
	 */
	
	private QrySqlSpmVO getQrySql(int qrytype, DZFDate begdate, DZFDate enddate, QryParamVO paramvo) throws DZFWarpException{
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select t.pk_confrim as pk_bill,  \n") ;
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){//查询全部时，区分该合同是否为全部返点扣款
			sql.append(" CASE WHEN nvl(t.ndeductmny,0) = 0 AND nvl(t.ndedrebamny,0) != 0  \n");
			sql.append("  THEN 3 ELSE 0 END AS ideductype, \n");
		}
		if(qrytype == 1){
			sql.append("       t.deductdata as doperatedate,  \n") ; 
			sql.append("       nvl(ct.nchangetotalmny, 0) - nvl(ct.nbookmny, 0) as naccountmny,  \n") ; 
			sql.append("       nvl(ct.nbookmny, 0) as nbookmny  \n") ; 
		}else if(qrytype == 2){
			sql.append("       substr(t.dchangetime, 0, 10) as doperatedate,  \n") ; 
			sql.append("       nvl(t.nsubtotalmny, 0) as naccountmny,  \n") ; 
			sql.append("       0 as nbookmny  \n") ; 
		}else if(qrytype == 3){
			sql.append("       substr(t.dchangetime, 0, 10) as doperatedate,  \n") ; 
			sql.append("       nvl(t.nsubtotalmny, 0)  + nvl(ct.nbookmny, 0) as naccountmny,  \n") ; 
			sql.append("       -abs(nvl(ct.nbookmny, 0)) as nbookmny  \n") ;
		}
		sql.append("  from cn_contract t  \n") ; 
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n") ; 
		sql.append(" where nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(ct.dr, 0) = 0  \n") ; 
		if(qrytype == 1){
			sql.append("   AND t.vstatus IN (?, ?, ?) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}else if(qrytype == 2){
			sql.append("   AND t.vstatus = ? \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		}else if(qrytype == 3){
			sql.append("   AND t.vstatus = ? \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		if(qrytype == 1){
			sql.append("   and t.deductdata >= ?  \n") ; 
			sql.append("   and t.deductdata <= ?  \n") ; 
		}else if(qrytype == 2 || qrytype == 3){
			sql.append("   and substr(t.dchangetime, 0, 10) >= ?  \n") ; 
			sql.append("   and substr(t.dchangetime, 0, 10) <= ?  \n") ; 
		}
		spm.addParam(begdate);
		spm.addParam(enddate);
		sql.append("   and t.pk_corp = ? \n");
		spm.addParam(paramvo.getPk_corp());
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//预付款
			sql.append(" AND ( nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 ) ");
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//返点
			sql.append(" AND nvl(t.ndedrebamny,0) != 0 ");
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**	
	 * 同一天的数据，按照收款在前，付款在后排列
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<ChnDetailRepVO> getOrderList(List<ChnDetailRepVO> list) throws DZFWarpException{
		List<ChnDetailRepVO> relist = new ArrayList<ChnDetailRepVO> ();
		Map<DZFDate,List<ChnDetailRepVO>> map = new HashMap<DZFDate,List<ChnDetailRepVO>>();
		List<ChnDetailRepVO> newlist = null;
		List<ChnDetailRepVO> oldlist = null;
		List<DZFDate> keylist = new ArrayList<DZFDate>();
		for(ChnDetailRepVO vo : list){
			if(!map.containsKey(vo.getDoperatedate())){
				newlist = new ArrayList<ChnDetailRepVO>();
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
			Collections.sort(newlist,new Comparator<ChnDetailRepVO>() {
				@Override
				public int compare(ChnDetailRepVO o1, ChnDetailRepVO o2) {
					if(o1.getIpaytype() == IStatusConstant.IPAYTYPE_3){
						return -o1.getIopertype().compareTo(o2.getIopertype());
					}else{
						return o1.getIopertype().compareTo(o2.getIopertype());
					}
				}
			});
			for(ChnDetailRepVO vo : newlist){
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
		sql.append("SELECT l.*, \n") ;
		sql.append("       ct.patchstatus, \n");
		sql.append("       ct.vcontcode, \n");
		sql.append("       ct.pk_corpk, \n");
		sql.append("       ct.vstatus, \n");
		sql.append("       ct.isncust \n");
		sql.append("  FROM cn_detail l  \n") ; 
		sql.append("  LEFT JOIN cn_contract t ON l.pk_bill = t.pk_confrim  \n") ; 
		sql.append("  LEFT JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n") ; 
		sql.append(" WHERE nvl(l.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0  \n") ; 
		sql.append("   AND l.pk_corp = ? \n");
		spm.addParam(paramvo.getPk_corp());
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND l.ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){
			sql.append(" AND l.ipaytype in (2, 3) \n");
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(l.doperatedate,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(l.doperatedate,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND l.doperatedate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND l.doperatedate <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by l.ts asc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
}
