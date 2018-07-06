package com.dzf.service.channel.refund.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.channel.refund.RefundBillVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.util.SafeCompute;
import com.dzf.service.channel.refund.IRefundBillService;
import com.dzf.service.pub.IPubService;

@Service("refundbillser")
public class RefundBillServiceImpl implements IRefundBillService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IPubService pubser;

	@Override
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQryCondition(paramvo);
		return multBodyObjectBO.queryDataTotal(RefundBillVO.class,sqpvo.getSql(), sqpvo.getSpm());
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RefundBillVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQryCondition(paramvo);
		List<RefundBillVO> list = (List<RefundBillVO>) multBodyObjectBO.queryDataPage(RefundBillVO.class, 
					sqpvo.getSql(), sqpvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
		if(list != null && list.size() > 0){
			setListShowName(list);
		}
		return list;
		
	}
	
	/**
	 * 设置多条数据的显示名称
	 * @param datavo
	 * @throws DZFWarpException
	 */
	private void setListShowName(List<RefundBillVO> list) throws DZFWarpException {
		CorpVO corpvo = null;
		UserVO uservo = null;
		Map<Integer, String> areamap = pubser.getAreaMap(null, 3);
		for(RefundBillVO bvo : list){
			corpvo = CorpCache.getInstance().get(null, bvo.getPk_corp());
			if(corpvo != null){
				if(areamap != null && !areamap.isEmpty()){
					bvo.setAreaname(areamap.get(corpvo.getVprovince()));//大区
				}
				bvo.setVprovname(corpvo.getCitycounty());
				bvo.setCorpcode(corpvo.getInnercode());//加盟商编码
				bvo.setCorpname(corpvo.getUnitname());//加盟商名称
			}
			uservo = UserCache.getInstance().get(bvo.getCoperatorid(), null);
			if(uservo != null){
				bvo.setVoperator(uservo.getUser_name());
			}
			
		}
	}
	
	/**
	 * 获取查询语句及条件
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQryCondition(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n") ;
		sql.append("  FROM cn_refund f  \n") ; 
		sql.append(" WHERE nvl(f.dr, 0) = 0  \n") ; 
		if(paramvo.getBegdate() != null){
			sql.append("   AND f.drefunddate >= ?  \n") ; 
			spm.addParam(paramvo.getBegdate());
		}
		if(paramvo.getEnddate() != null){
			sql.append("   AND f.drefunddate <= ?  \n") ; 
			spm.addParam(paramvo.getEnddate());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
			sql.append("   AND f.pk_corp = ?  \n") ; 
			spm.addParam(paramvo.getPk_corp());
		}
		if(!StringUtil.isEmpty(paramvo.getVbillcode())){
			sql.append("   AND f.vbillcode = ?  \n") ; 
			spm.addParam(paramvo.getVbillcode());
		}
		if(paramvo.getVdeductstatus() != null && paramvo.getVdeductstatus() != -1){
			sql.append("   AND f.istatus = ? \n");
			spm.addBlobParam(paramvo.getVdeductstatus());
		}
		sql.append("   ORDER BY f.updatets DESC \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public RefundBillVO save(RefundBillVO datavo, String logincorp) throws DZFWarpException {
		if(StringUtil.isEmpty(datavo.getVbillcode())){
			String vbillcode = pubser.queryCode("cn_refund");
			datavo.setVbillcode(vbillcode);
		}
		checkCodeOnly(datavo);//返点单单号唯一性校验
		datavo = (RefundBillVO) singleObjectBO.saveObject(logincorp, datavo);
		setShowName(datavo);
		return datavo;
	}
	
	/**
	 * 状态校验
	 * @param datavo
	 * @throws DZFWarpException
	 */
	private void checkState(RefundBillVO datavo) throws DZFWarpException {
		
	}
	
	/**
	 * 退款单号唯一性校验
	 * @param vo
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public void checkCodeOnly(RefundBillVO vo) throws DZFWarpException {
		SQLParameter spm = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("select vbillcode from cn_refund where nvl(dr,0) = 0 ");
		sql.append(" and vbillcode = ? ");
		spm.addParam(vo.getVbillcode());
		if (!StringUtil.isEmpty(vo.getPk_refund())) {
			sql.append(" and pk_refund != ? ");
			spm.addParam(vo.getPk_refund());
		}
		List<RebateVO> list = (List<RebateVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RebateVO.class));
		if (list != null && list.size() > 0) {
			throw new BusinessException("退款单号："+vo.getVbillcode()+"已经在系统中存在");
		}
	}
	
	/**
	 * 设置单条数据的显示名称
	 * @param datavo
	 * @throws DZFWarpException
	 */
	private void setShowName(RefundBillVO datavo) throws DZFWarpException {
		CorpVO corpvo = CorpCache.getInstance().get(null, datavo.getPk_corp());
		if(corpvo != null){
			String areaname = pubser.getAreaName(corpvo.getVprovince(), 1);
			datavo.setAreaname(areaname);//大区
			datavo.setVprovname(corpvo.getCitycounty());
			datavo.setCorpcode(corpvo.getInnercode());//加盟商编码
			datavo.setCorpname(corpvo.getUnitname());//加盟商名称
		}
		UserVO uservo = UserCache.getInstance().get(datavo.getCoperatorid(), null);
		if(uservo != null){
			datavo.setVoperator(uservo.getUser_name());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public RefundBillVO queryRefundMny(RefundBillVO paramvo) throws DZFWarpException {
		RefundBillVO retvo = new RefundBillVO();
		// 1、计算付款余额表余额
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT b.nusedmny, b.npaymny, b.ipaytype  \n");
		sql.append("  FROM cn_balance b  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND b.pk_corp = ? \n");
		spm.addParam(paramvo.getPk_corp());
		sql.append("   AND b.ipaytype in (1, 2) \n");
		List<ChnBalanceVO> blist = (List<ChnBalanceVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnBalanceVO.class));
		if(blist != null && blist.size() > 0){
			for(ChnBalanceVO bvo : blist){
				if(bvo.getIpaytype() != null && bvo.getIpaytype() == 1){//保证金
					retvo.setNrefbzjmny(SafeCompute.sub(bvo.getNpaymny(), bvo.getNusedmny()));
				}else if(bvo.getIpaytype() != null && bvo.getIpaytype() == 2){//预付款
					retvo.setNrefyfkmny(SafeCompute.sub(bvo.getNpaymny(), bvo.getNusedmny()));
				}
			}
		}
		// 2、计算退款单未确认单据的退款金额
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("SELECT SUM(d.nrefbzjmny) AS nrefbzjmny, SUM(d.nrefyfkmny) AS nrefyfkmny  \n") ;
		sql.append("  FROM cn_refund d  \n") ; 
		sql.append(" WHERE nvl(d.dr, 0) = 0  \n") ; 
		sql.append("   AND d.pk_corp = ?  \n") ; 
		spm.addParam(paramvo.getPk_corp());
		sql.append("   AND d.istatus = ? \n");
		spm.addParam(IStatusConstant.IREFUNDSTATUS_0);//待确认
		if(!StringUtil.isEmpty(paramvo.getPk_refund())){
			sql.append("   AND d.pk_refund != ? \n");
			spm.addParam(paramvo.getPk_refund());
		}
		List<RefundBillVO> flist = (List<RefundBillVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RefundBillVO.class));
		if(flist != null && flist.size() > 0){
			RefundBillVO fvo = flist.get(0);
			retvo.setNrefbzjmny(SafeCompute.sub(retvo.getNrefbzjmny(), fvo.getNrefbzjmny()));
			retvo.setNrefyfkmny(SafeCompute.sub(retvo.getNrefyfkmny(), fvo.getNrefyfkmny()));
		}
		return retvo;
	}

	@Override
	public RefundBillVO checkBeforeSave(RefundBillVO datavo) throws DZFWarpException {
		StringBuffer errmsg = new StringBuffer();
		RefundBillVO refvo = queryRefundMny(datavo);
		if(datavo.getNrefbzjmny().compareTo(refvo.getNrefbzjmny()) > 0){
			errmsg.append("保证金退款金额大于该加盟商的期末余额，退款后余额可能为负值，");
		}
		if(datavo.getNrefyfkmny().compareTo(refvo.getNrefyfkmny()) > 0){
			errmsg.append("预付款退款金额大于该加盟商的期末余额，退款后余额可能为负值，");
		}
		if(errmsg != null && errmsg.length() > 0){
			errmsg.append("确认保存？");
			datavo.setVerrmsg(errmsg.toString());
		}
		return datavo;
	}

}
