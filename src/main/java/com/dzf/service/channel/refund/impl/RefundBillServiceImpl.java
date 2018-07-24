package com.dzf.service.channel.refund.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.channel.refund.RefundBillVO;
import com.dzf.model.pub.CommonUtil;
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
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
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
		return multBodyObjectBO.queryDataTotal(RefundBillVO.class, sqpvo.getSql(), sqpvo.getSpm());
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
		Map<Integer, String> areamap = pubser.getAreaMap(null, 1);
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
			spm.addParam(paramvo.getVdeductstatus());
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
		if(StringUtil.isEmpty(datavo.getPk_refund())){//新增
			checkCodeOnly(datavo);//返点单单号唯一性校验
			datavo = (RefundBillVO) singleObjectBO.saveObject(logincorp, datavo);
		}else{
			String errmsg = checkState(datavo, 1);
			if(!StringUtil.isEmpty(errmsg)){
				throw new BusinessException(errmsg);
			}
			datavo.setUpdatets(new DZFDateTime());
			updateData(datavo);
		}
		setShowName(datavo);
		return datavo;
	}
	
	/**
	 * 更新数据
	 * @param datavo
	 * @throws DZFWarpException
	 */
	private void updateData(RefundBillVO datavo) throws DZFWarpException {
	    String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(datavo.getTableName(), datavo.getPk_refund(),uuid, 60);
			String[] str = new String[]{"updatets", "drefunddate", "nrefyfkmny", "nrefbzjmny", "vmemo"};
			singleObjectBO.update(datavo, str);
		} finally {
			LockUtil.getInstance().unLock_Key(datavo.getTableName(), datavo.getPk_refund(), uuid);
		}
	}
	
	/**
	 * 状态校验
	 * @param datavo
	 * @param type  1：修改保存；2：删除保存；3：确认；4：取消确认；
	 * @return
	 * @throws DZFWarpException
	 */
	private String checkState(RefundBillVO datavo, Integer type) throws DZFWarpException {
		RefundBillVO oldvo = (RefundBillVO) singleObjectBO.queryByPrimaryKey(RefundBillVO.class, datavo.getPk_refund());
		if(oldvo == null || (oldvo != null && oldvo.getDr() != null && oldvo.getDr() == 1)){
			return "退款单"+oldvo.getVbillcode()+"已经被删除；";
		}
		if(datavo.getUpdatets().compareTo(oldvo.getUpdatets()) != 0){
			return "退款单"+oldvo.getVbillcode()+"发生变化，请刷新界面后，再次尝试；";
		}
		if(type == 1 || type == 2){
			if(datavo.getIstatus() == IStatusConstant.IREFUNDSTATUS_1){//已确认
				return "退款单"+oldvo.getVbillcode()+"状态为已确认";
			}
		}else if(type == 3){
			if(datavo.getIstatus() == IStatusConstant.IREFUNDSTATUS_1){//已确认
				return "退款单"+oldvo.getVbillcode()+"状态为已确认";
			}
		}else if(type == 4){
			if(datavo.getIstatus() == IStatusConstant.IREFUNDSTATUS_0){//待确认
				return "退款单"+oldvo.getVbillcode()+"状态为待确认";
			}
		}
		return null;
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

	/**
	 * type  -1：新增时查询；1：保存前校验；2：确认前校验；
	 */
	@Override
	public RefundBillVO queryRefundMny(RefundBillVO paramvo, Integer type) throws DZFWarpException {
		RefundBillVO retvo = new RefundBillVO();
		if(type != null && (type == -1 || type == 1)){
			// 1、计算付款余额表余额
			RefundBillVO balvo = qryReBanMny(paramvo);
			if(balvo != null){
				retvo.setNrefbzjmny(CommonUtil.getDZFDouble(balvo.getNrefbzjmny()));
				retvo.setNrefyfkmny(CommonUtil.getDZFDouble(balvo.getNrefyfkmny()));
			}
			// 2、计算退款单未确认单据的退款金额
			RefundBillVO refvo = qryRefundMny(paramvo);
			if(refvo != null){
				retvo.setNrefbzjmny(SafeCompute.sub(retvo.getNrefbzjmny(), refvo.getNrefbzjmny()));
				retvo.setNrefyfkmny(SafeCompute.sub(retvo.getNrefyfkmny(), refvo.getNrefyfkmny()));
			}
		}else if(type != null && type == 2){
			// 1、计算付款余额表余额
			RefundBillVO balvo = qryReBanMny(paramvo);
			if(balvo != null){
				retvo.setNrefbzjmny(CommonUtil.getDZFDouble(balvo.getNrefbzjmny()));
				retvo.setNrefyfkmny(CommonUtil.getDZFDouble(balvo.getNrefyfkmny()));
			}
		}
		return retvo;
	}

	/**
	 * 查询余额表可退款金额
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private RefundBillVO qryReBanMny(RefundBillVO paramvo) throws DZFWarpException {
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
			RefundBillVO retvo = new RefundBillVO();
			for(ChnBalanceVO bvo : blist){
				if(bvo.getIpaytype() != null && bvo.getIpaytype() == 1){//保证金
					retvo.setNrefbzjmny(SafeCompute.sub(bvo.getNpaymny(), bvo.getNusedmny()));
				}else if(bvo.getIpaytype() != null && bvo.getIpaytype() == 2){//预付款
					retvo.setNrefyfkmny(SafeCompute.sub(bvo.getNpaymny(), bvo.getNusedmny()));
				}
			}
			return retvo;
		}
		return null;
	}
	
	/**
	 * 查询保存态退款金额
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private RefundBillVO qryRefundMny(RefundBillVO paramvo) throws DZFWarpException {
		// 2、计算退款单未确认单据的退款金额
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
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
			return flist.get(0);
		}
		return null;
	}
	
	/**
	 * 1：保存前校验；2：确认前校验；
	 */
	@Override
	public RefundBillVO checkBeforeSave(RefundBillVO datavo, Integer checktype) throws DZFWarpException {
		StringBuffer errmsg = new StringBuffer();
		RefundBillVO refvo = queryRefundMny(datavo, checktype);
		if(datavo.getNrefbzjmny().compareTo(refvo.getNrefbzjmny()) > 0){
			errmsg.append("保证金退款金额大于该加盟商的期末余额，退款后余额可能为负值，");
		}
		if(datavo.getNrefyfkmny().compareTo(refvo.getNrefyfkmny()) > 0){
			errmsg.append("预付款退款金额大于该加盟商的期末余额，退款后余额可能为负值，");
		}
		if(errmsg != null && errmsg.length() > 0){
			if(checktype != null && checktype == 1){
				errmsg.append("确认保存？");
			}else if(checktype != null && checktype == 1){
				errmsg.append("确定确认？");
			}
			datavo.setVerrmsg(errmsg.toString());
		}
		return datavo;
	}

	@Override
	public RefundBillVO delete(RefundBillVO datavo) throws DZFWarpException {
		String errmsg = checkState(datavo, 2);
		if(!StringUtil.isEmpty(errmsg)){
			datavo.setVerrmsg(errmsg);
		}else{
		    String uuid = UUID.randomUUID().toString();
			try {
				String delsql = "delete from cn_refund where pk_refund = ? ";
				SQLParameter spm = new SQLParameter();
				spm.addParam(datavo.getPk_refund());
				singleObjectBO.executeUpdate(delsql, spm);
			} finally {
				LockUtil.getInstance().unLock_Key(datavo.getTableName(), datavo.getPk_refund(), uuid);
			}
		}
		return datavo;
	}

	@Override
	public RefundBillVO updateOperat(RefundBillVO datavo, Integer opertype, String cuserid) throws DZFWarpException {
		String errmsg = "";
		if(opertype == IStatusConstant.IREFOPERATYPE_1){//退款确认
			errmsg = checkState(datavo, 3);
		}else if(opertype == IStatusConstant.IREFOPERATYPE_2){//取消确认
			errmsg = checkState(datavo, 4);
		}
		if(!StringUtil.isEmpty(errmsg)){
			datavo.setVerrmsg(errmsg);
			return datavo;
		}
		return updateBalance(datavo, opertype, cuserid);
	}
	
	/**
	 * 更新余额表并生成退款明细
	 * @param datavo
	 * @param opertype  1：退款确认；2：取消确认；
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private RefundBillVO updateBalance(RefundBillVO datavo, Integer opertype, String cuserid) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT b.* \n");
		sql.append("  FROM cn_balance b  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND b.pk_corp = ? \n");
		spm.addParam(datavo.getPk_corp());
		sql.append("   AND b.ipaytype in (1, 2) \n");
		List<ChnBalanceVO> blist = (List<ChnBalanceVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnBalanceVO.class));
		Map<Integer, ChnBalanceVO> bmap = new HashMap<Integer, ChnBalanceVO>();
		if(blist != null && blist.size() > 0){
			for(ChnBalanceVO bvo : blist){
				bmap.put(bvo.getIpaytype(), bvo);
			}
		}
		
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(datavo.getTableName(), datavo.getPk_refund(), uuid, 60);
			if(opertype == IStatusConstant.IREFOPERATYPE_1){//退款确认
				datavo.setIstatus(IStatusConstant.IREFUNDSTATUS_1);//已确认
				datavo.setVconfirmid(cuserid);
				datavo.setDconfirmdate(new DZFDate());
				updateConfBalance(bmap, datavo, cuserid);
			}else if(opertype == IStatusConstant.IREFOPERATYPE_2){//取消确认
				datavo.setIstatus(IStatusConstant.IREFUNDSTATUS_0);//待确认
				datavo.setVconfirmid(null);
				datavo.setDconfirmdate(null);
				updateUnConfBalance(bmap, datavo, cuserid);
			}
			datavo.setUpdatets(new DZFDateTime());
			String[] str = new String[]{"istatus","vconfirmid","dconfirmdate","updatets"};
			singleObjectBO.update(datavo, str);
		} finally {
			LockUtil.getInstance().unLock_Key(datavo.getTableName(), datavo.getPk_refund(), uuid);
		}
		return datavo;
	}
	
	/**
	 * 确认-更新余额
	 * @param balvo
	 * @param refvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updateConfBalance(Map<Integer, ChnBalanceVO> bmap, RefundBillVO refvo, String cuserid) throws DZFWarpException {
		ChnBalanceVO balvo = null;
		if(CommonUtil.getDZFDouble(refvo.getNrefbzjmny()).compareTo(DZFDouble.ZERO_DBL) > 0){//保证金退款
			balvo =  bmap.get(IStatusConstant.IPAYTYPE_1);//保证金
			if(balvo != null){
				//更新余额表：
				balvo.setNpaymny(SafeCompute.sub(balvo.getNpaymny(), refvo.getNrefbzjmny()));
				singleObjectBO.update(balvo, new String[]{"npaymny"});
				//更新明细表：
				ChnDetailVO detvo = new ChnDetailVO();
				detvo.setPk_corp(refvo.getPk_corp());
				detvo.setNpaymny(SafeCompute.sub(DZFDouble.ZERO_DBL, refvo.getNrefbzjmny()));
				detvo.setIpaytype(IStatusConstant.IPAYTYPE_1);//保证金
				detvo.setPk_bill(refvo.getPk_refund());//退款单主键
				detvo.setVmemo("退款：保证金");
				detvo.setCoperatorid(cuserid);
				detvo.setDoperatedate(new DZFDate());
				detvo.setDr(0);
				detvo.setIopertype(IStatusConstant.IDETAILTYPE_4);//退款单退款
				singleObjectBO.saveObject("000001", detvo);
			}else{
				String unitname = "";
				CorpVO corpvo = CorpCache.getInstance().get(null, refvo.getPk_corp());
				if(corpvo != null){
					unitname = corpvo.getUnitname();
				}
				throw new BusinessException("客户："+unitname+"余额表-保证金查询错误；");
			}
		}
		if(CommonUtil.getDZFDouble(refvo.getNrefyfkmny()).compareTo(DZFDouble.ZERO_DBL) > 0){//预付款退款
			balvo =  bmap.get(IStatusConstant.IPAYTYPE_2);//预付款
			if(balvo != null){
				//更新余额表：
				balvo.setNpaymny(SafeCompute.sub(balvo.getNpaymny(), refvo.getNrefyfkmny()));
				singleObjectBO.update(balvo, new String[]{"npaymny"});
				//更新明细表：
				ChnDetailVO detvo = new ChnDetailVO();
				detvo.setPk_corp(refvo.getPk_corp());
				detvo.setNpaymny(SafeCompute.sub(DZFDouble.ZERO_DBL, refvo.getNrefyfkmny()));
				detvo.setIpaytype(IStatusConstant.IPAYTYPE_2);//预付款
				detvo.setPk_bill(refvo.getPk_refund());//退款单主键
				detvo.setVmemo("退款：预付款");
				detvo.setCoperatorid(cuserid);
				detvo.setDoperatedate(new DZFDate());
				detvo.setDr(0);
				detvo.setIopertype(IStatusConstant.IDETAILTYPE_4);//退款单退款
				singleObjectBO.saveObject("000001", detvo);
			}else{
				String unitname = "";
				CorpVO corpvo = CorpCache.getInstance().get(null, refvo.getPk_corp());
				if(corpvo != null){
					unitname = corpvo.getUnitname();
				}
				throw new BusinessException("客户："+unitname+"余额表-预付款查询错误；");
			}
		}
	}

	
	/**
	 * 取消确认-更新余额
	 * @param balvo
	 * @param refvo
	 * @throws DZFWarpException
	 */
	private void updateUnConfBalance(Map<Integer, ChnBalanceVO> bmap, RefundBillVO refvo, String cuserid) throws DZFWarpException {
		ChnBalanceVO balvo = null;
		if(CommonUtil.getDZFDouble(refvo.getNrefbzjmny()).compareTo(DZFDouble.ZERO_DBL) > 0){//保证金退款
			balvo =  bmap.get(IStatusConstant.IPAYTYPE_1);//保证金
			if(balvo != null){
				//更新余额表：
				balvo.setNpaymny(SafeCompute.add(balvo.getNpaymny(), refvo.getNrefbzjmny()));
				singleObjectBO.update(balvo, new String[]{"npaymny"});
				//删除保证金退款明细：
				StringBuffer sql = new StringBuffer();
				SQLParameter spm = new SQLParameter();
				sql.append("DELETE FROM cn_detail  \n") ;
				sql.append(" WHERE pk_corp = ?  \n") ; 
				sql.append("   AND ipaytype = ?  \n") ; 
				sql.append("   AND pk_bill = ?  \n") ; 
				sql.append("   AND iopertype = ? \n");
				spm.addParam(refvo.getPk_corp());
				spm.addParam(IStatusConstant.IPAYTYPE_1);//保证金
				spm.addParam(refvo.getPk_refund());//退款单主键
				spm.addParam(IStatusConstant.IDETAILTYPE_4);//退款单退款
				singleObjectBO.executeUpdate(sql.toString(), spm);
			}else{
				String unitname = "";
				CorpVO corpvo = CorpCache.getInstance().get(null, refvo.getPk_corp());
				if(corpvo != null){
					unitname = corpvo.getUnitname();
				}
				throw new BusinessException("客户："+unitname+"余额表-保证金查询错误；");
			}
		}
		if(CommonUtil.getDZFDouble(refvo.getNrefyfkmny()).compareTo(DZFDouble.ZERO_DBL) > 0){//预付款退款
			balvo =  bmap.get(IStatusConstant.IPAYTYPE_2);//预付款
			if(balvo != null){
				//更新余额表：
				balvo.setNpaymny(SafeCompute.add(balvo.getNpaymny(), refvo.getNrefyfkmny()));
				singleObjectBO.update(balvo, new String[]{"npaymny"});
				//删除预付款退款明细：
				StringBuffer sql = new StringBuffer();
				SQLParameter spm = new SQLParameter();
				sql.append("DELETE FROM cn_detail  \n") ;
				sql.append(" WHERE pk_corp = ?  \n") ; 
				sql.append("   AND ipaytype = ?  \n") ; 
				sql.append("   AND pk_bill = ?  \n") ; 
				sql.append("   AND iopertype = ? \n");
				spm.addParam(refvo.getPk_corp());
				spm.addParam(IStatusConstant.IPAYTYPE_2);//预付款
				spm.addParam(refvo.getPk_refund());//退款单主键
				spm.addParam(IStatusConstant.IDETAILTYPE_4);//退款单退款
				singleObjectBO.executeUpdate(sql.toString(), spm);
			}else{
				String unitname = "";
				CorpVO corpvo = CorpCache.getInstance().get(null, refvo.getPk_corp());
				if(corpvo != null){
					unitname = corpvo.getUnitname();
				}
				throw new BusinessException("客户："+unitname+"余额表-预付款查询错误；");
			}
		}
	}
	
}
