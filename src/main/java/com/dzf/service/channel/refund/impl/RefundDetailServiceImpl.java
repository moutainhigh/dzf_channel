package com.dzf.service.channel.refund.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.refund.RefundDetailVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.refund.IRefundDetailService;
import com.dzf.service.pub.IPubService;

@Service("refunddetailser")
public class RefundDetailServiceImpl implements IRefundDetailService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IPubService pubser;

	@Override
	public List<RefundDetailVO> query(QryParamVO pamvo) throws DZFWarpException {
		//1、根据登录人和选择大区进行过滤
		StringBuffer addWhere = new StringBuffer();
		String areafilter = pubser.makeCondition(pamvo.getCuserid(), pamvo.getAreaname(),IStatusConstant.IQUDAO);
		if (!StringUtil.isEmpty(areafilter)) {
			if(!areafilter.equals("alldata")){//当返回为alldata时，为最大查询权限
				addWhere.append(areafilter);
			}
		}else{//当返回值为空时，说明没有查询权限
			return null;
		}
		
		//2、如果渠道经理查询条件不为空，则根据区域经理进行过滤
		if(!StringUtil.isEmpty(pamvo.getVmanager())){
			String[] corps = pubser.getManagerCorp(pamvo.getVmanager(), 1);
			if(corps != null && corps.length > 0){
				String where = SqlUtil.buildSqlForIn(" t.pk_corp", corps);
				addWhere.append(" AND ").append(where);
			}else{
				return null;
			}
		}
		
		return getSumList(pamvo, addWhere.toString());
	}
	
	/**
	 * 获取汇总信息
	 * @param pamvo
	 * @param addWhere
	 * @return
	 * @throws DZFWarpException
	 */
	private List<RefundDetailVO> getSumList(QryParamVO pamvo, String addWhere) throws DZFWarpException {
		List<RefundDetailVO> retlist = new ArrayList<RefundDetailVO>();
		Map<String, RefundDetailVO> retmap = getRetMap(pamvo, addWhere.toString());
		if(retmap != null && !retmap.isEmpty()){
			RefundDetailVO refvo = null;
			CorpVO corpvo = null;
			UserVO uservo = null;
			Map<Integer, String> areaMap = pubser.getAreaMap(pamvo.getAreaname(), 1);//渠道区域大区集合
			Map<String,UserVO> marmap = pubser.getManagerMap(1);//渠道经理
			String manager = "";
			for(String key : retmap.keySet()){
				refvo = retmap.get(key);
				corpvo = CorpCache.getInstance().get(null, key);
				if(corpvo != null){
					refvo.setCorpcode(corpvo.getInnercode());
					refvo.setCorpname(corpvo.getUnitname());
					refvo.setVarea(corpvo.getCitycounty());//地区
				}
				if(areaMap != null && !areaMap.isEmpty()){
					String area = areaMap.get(refvo.getVprovince());
					if(!StringUtil.isEmpty(area)){
						refvo.setAreaname(area);//大区
					}
				}
				if(marmap != null && !marmap.isEmpty()){
					uservo = marmap.get(refvo.getPk_corp());
					if (uservo != null) {
						refvo.setVmanagername(uservo.getUser_name());//渠道经理
					}
				}
				retlist.add(refvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 获取退款数据集合
	 * @param pamvo
	 * @param addWhere
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, RefundDetailVO> getRetMap(QryParamVO pamvo, String addWhere) throws DZFWarpException {
		Map<String, RefundDetailVO> retmap = new HashMap<String, RefundDetailVO>();
		// 1、合同变更（终止、作废）退款数据
		List<RefundDetailVO> tklist = queryContRefund(pamvo, addWhere);
		countData(tklist, retmap);
		// 2、一般人转为小规模的退款数据
		List<RefundDetailVO> kklist = queryContDeduct(pamvo, addWhere);
		countData(kklist, retmap);
		// 3、退款单退款数据
		List<RefundDetailVO> reflist = queryRefund(pamvo, addWhere);
		countData(reflist, retmap);
		return retmap;
	}
	
	/**
	 * 合同变更（终止、作废）的退款数据
	 * @param pamvo
	 * @param addWhere
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<RefundDetailVO> queryContRefund(QryParamVO pamvo, String addWhere) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ct.pk_corp, nvl(SUM(t.nretdedmny),0) AS nreturnmny, account.vprovince  \n");
		sql.append("  FROM ynt_contract ct  \n");
		sql.append("  LEFT JOIN cn_contract t ON ct.pk_contract = t.pk_contract  \n");
		sql.append("  LEFT JOIN bd_account account ON ct.pk_corp = account.pk_corp  \n");
		sql.append(" WHERE nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y'  \n");
		sql.append("   AND ct.patchstatus = 3 \n");
		sql.append("   AND ct.vstatus IN (?, ?) \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if (!StringUtil.isEmpty(addWhere)) {
			sql.append(addWhere);
		}
		if (!StringUtil.isEmpty(pamvo.getPk_corp())) {
			sql.append("   AND ct.pk_corp = ? \n");
			spm.addParam(pamvo.getPk_corp());
		}
		if (pamvo.getBegdate() != null) {
			sql.append(" AND substr(t.dchangetime,1,10) >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND substr(t.dchangetime,1,10) <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" GROUP BY ct.pk_corp, account.vprovince \n");
		return (List<RefundDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RefundDetailVO.class));
	}
	
	/**
	 * 一般人转为小规模的退款
	 * @param pamvo
	 * @param addWhere
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<RefundDetailVO> queryContDeduct(QryParamVO pamvo, String addWhere) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ct.pk_corp, nvl(abs(SUM(t.ndeductmny)),0) AS nreturnmny, account.vprovince  \n") ;
		sql.append("  FROM ynt_contract ct  \n") ; 
		sql.append("  LEFT JOIN cn_contract t ON ct.pk_contract = t.pk_contract  \n") ; 
		sql.append("  LEFT JOIN bd_account account ON ct.pk_corp = account.pk_corp  \n") ; 
		sql.append(" WHERE nvl(ct.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y'  \n") ; 
		sql.append("   AND ct.patchstatus = 5  \n") ; 
		sql.append("   AND ct.vstatus = ?  \n") ; 
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		if (!StringUtil.isEmpty(addWhere)) {
			sql.append(addWhere);
		}
		if (!StringUtil.isEmpty(pamvo.getPk_corp())) {
			sql.append("   AND ct.pk_corp = ? \n");
			spm.addParam(pamvo.getPk_corp());
		}
		if (pamvo.getBegdate() != null) {
			sql.append(" AND t.deductdata >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND t.deductdata <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" GROUP BY ct.pk_corp, account.vprovince \n");
		return (List<RefundDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RefundDetailVO.class));
	}
	
	/**
	 * 查询退款单退款
	 * @param pamvo
	 * @param addWhere
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<RefundDetailVO> queryRefund(QryParamVO pamvo, String addWhere) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp, nvl(SUM(t.nrefyfkmny),0) AS nreturnmny, account.vprovince  \n") ; 
		sql.append("  FROM cn_refund t  \n") ; 
		sql.append("  LEFT JOIN bd_account account ON t.pk_corp = account.pk_corp  \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y'  \n") ; 
		sql.append("   AND t.istatus = 1  \n") ; 
		if (!StringUtil.isEmpty(addWhere)) {
			sql.append(addWhere);
		}
		if (!StringUtil.isEmpty(pamvo.getPk_corp())) {
			sql.append("   AND t.pk_corp = ? \n");
			spm.addParam(pamvo.getPk_corp());
		}
		if (pamvo.getBegdate() != null) {
			sql.append(" AND t.dconfirmdate >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND t.dconfirmdate <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" GROUP BY t.pk_corp, account.vprovince \n");
		return (List<RefundDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RefundDetailVO.class));
	}

	/**
	 * 相关数据金额汇总
	 * @param list
	 * @throws DZFWarpException
	 */
	private void countData(List<RefundDetailVO> list, Map<String,RefundDetailVO> retmap) throws DZFWarpException {
		if (list != null && list.size() > 0) {
			DZFDouble mny = DZFDouble.ZERO_DBL;
			for (RefundDetailVO detvo : list) {
				if(!retmap.containsKey(detvo.getPk_corp())){
					retmap.put(detvo.getPk_corp(), detvo);
				}else{
					mny = retmap.get(detvo.getPk_corp()).getNreturnmny();
					mny = SafeCompute.add(mny, detvo.getNreturnmny());
					detvo.setNreturnmny(mny);
					retmap.put(detvo.getPk_corp(), detvo);
				}
			}
		}
	}

	@Override
	public List<RefundDetailVO> queryDetail(QryParamVO pamvo) throws DZFWarpException {
		List<RefundDetailVO> detlist = new ArrayList<RefundDetailVO>();
		//1、合同变更退款明细：
		List<RefundDetailVO> tklist = queryContRefundDet(pamvo);
		if(tklist != null && tklist.size() > 0){
			detlist.addAll(tklist);
		}
		//2、合同小规模转一般人退款明细：
		List<RefundDetailVO> kklist = queryContDeductDet(pamvo);
		if(kklist != null && kklist.size() > 0){
			detlist.addAll(kklist);
		}
		//3、退款明细
		List<RefundDetailVO> reflist = queryRefundDet(pamvo);
		if(reflist != null && reflist.size() > 0){
			detlist.addAll(reflist);
		}
		if(detlist != null && detlist.size() > 0){
			CorpVO corpvo = null;
			for(RefundDetailVO dvo : detlist){
				corpvo = CorpCache.getInstance().get(null, dvo.getPk_corpk());
				if(corpvo != null){
					dvo.setCorpkcode(corpvo.getInnercode());
					dvo.setCorpkname(corpvo.getUnitname());
				}
				corpvo = CorpCache.getInstance().get(null, dvo.getPk_corp());
				if(corpvo != null){
					dvo.setCorpname(corpvo.getUnitname());
				}
			}
		}
		return detlist;
	}
	
	/**
	 * 合同变更（终止、作废）的退款明细数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<RefundDetailVO> queryContRefundDet(QryParamVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ct.pk_corp, \n");
		sql.append("       ct.pk_corpk, \n");
		sql.append("       substr(t.dchangetime,1,10) AS drefunddate, \n");
		sql.append("       nvl(t.nretdedmny,0) AS nreturnmny, \n");
		sql.append("       ct.vcontcode AS vbillcode, \n");
		sql.append("       decode(nvl(ct.vstatus, -1), 9, '合同终止', '合同作废') AS vmemo  \n") ; 
		sql.append("  FROM ynt_contract ct  \n");
		sql.append("  LEFT JOIN cn_contract t ON ct.pk_contract = t.pk_contract  \n");
		sql.append("  LEFT JOIN bd_account account ON ct.pk_corp = account.pk_corp  \n");
		sql.append(" WHERE nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y'  \n");
		sql.append("   AND ct.patchstatus = 3 \n");
		sql.append("   AND ct.vstatus IN (?, ?) \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		sql.append("   AND ct.pk_corp = ? \n");
		spm.addParam(pamvo.getPk_corp());
		if (pamvo.getBegdate() != null) {
			sql.append(" AND substr(t.dchangetime,1,10) >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND substr(t.dchangetime,1,10) <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		return (List<RefundDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RefundDetailVO.class));
	}
	
	/**
	 * 一般人转为小规模的退款明细
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<RefundDetailVO> queryContDeductDet(QryParamVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ct.pk_corp, \n");
		sql.append("       ct.pk_corpk, \n");
		sql.append("       deductdata AS drefunddate, \n");
		sql.append("       nvl(abs(t.ndeductmny),0) AS nreturnmny, \n");
		sql.append("       ct.vcontcode AS vbillcode, \n");
		sql.append("       '一般人转为小规模' AS vmemo  \n") ; 
		sql.append("  FROM ynt_contract ct  \n") ; 
		sql.append("  LEFT JOIN cn_contract t ON ct.pk_contract = t.pk_contract  \n") ; 
		sql.append("  LEFT JOIN bd_account account ON ct.pk_corp = account.pk_corp  \n") ; 
		sql.append(" WHERE nvl(ct.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y'  \n") ; 
		sql.append("   AND ct.patchstatus = 5  \n") ; 
		sql.append("   AND ct.vstatus = ?  \n") ; 
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		sql.append("   AND ct.pk_corp = ? \n");
		spm.addParam(pamvo.getPk_corp());
		if (pamvo.getBegdate() != null) {
			sql.append(" AND t.deductdata >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND t.deductdata <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		return (List<RefundDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RefundDetailVO.class));
	}
	
	/**
	 * 查询退款单退款明细
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<RefundDetailVO> queryRefundDet(QryParamVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp, \n") ; 
		sql.append("       nvl(t.nrefyfkmny,0) AS nreturnmny, \n") ; 
		sql.append("       t.dconfirmdate AS drefunddate, \n") ;
		sql.append("       t.vbillcode, \n") ;
		sql.append("       '退款单' AS vmemo  \n") ;
		sql.append("  FROM cn_refund t  \n") ; 
		sql.append("  LEFT JOIN bd_account account ON t.pk_corp = account.pk_corp  \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y'  \n") ; 
		sql.append("   AND t.istatus = 1  \n") ; 
		sql.append("   AND t.pk_corp = ? \n");
		spm.addParam(pamvo.getPk_corp());
		if (pamvo.getBegdate() != null) {
			sql.append(" AND t.dconfirmdate >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND t.dconfirmdate <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		return (List<RefundDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RefundDetailVO.class));
	}
}