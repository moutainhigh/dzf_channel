package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.IChnPayConfService;

@Service("chnpayconfser")
public class ChnPayConfServiceImpl implements IChnPayConfService {
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo);
		return multBodyObjectBO.queryDataTotal(ChnPayBillVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnPayBillVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo);
		List<ChnPayBillVO> list = (List<ChnPayBillVO>) multBodyObjectBO.queryDataPage(ChnPayBillVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
		if(list != null && list.size() > 0){
//			List<ChnPayBillVO> retlist = new ArrayList<ChnPayBillVO>();
			CorpVO accvo = null;
			UserVO uservo = null;
			for(ChnPayBillVO vo : list){
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(accvo != null){
					vo.setCorpname(accvo.getUnitname());
					//此代码为界面 加盟商名称 快速过滤，暂时注释
//					if(!StringUtil.isEmpty(paramvo.getCorpname())){
//						if(vo.getCorpname().indexOf(paramvo.getCorpname()) != -1){
//							retlist.add(vo);
//						}
//					}
				}
				uservo = UserCache.getInstance().get(vo.getVconfirmid(), null);
				if(uservo != null){
					vo.setVconfirmname(uservo.getUser_name());
				}
			}
//			if(!StringUtil.isEmpty(paramvo.getCorpname())){
//				return retlist;
//			}
		}
		return list;
	}
	
	/**
	 * 获取查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.* FROM cn_paybill t \n");
		sql.append("  WHERE nvl(t.dr,0) = 0 \n");
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){//查询状态
			sql.append(" AND t.vstatus = ? \n");
			spm.addParam(paramvo.getQrytype());
		}else{
			sql.append(" AND t.vstatus in ( ?, ?) \n");
			spm.addParam(IStatusConstant.IPAYSTATUS_3);
			spm.addParam(IStatusConstant.IPAYSTATUS_5);
		}
		if(paramvo.getIpaytype() != null && paramvo.getIpaytype() != -1){
		    sql.append(" AND t.ipaytype = ? \n");
            spm.addParam(paramvo.getIpaytype());
		}
		if(paramvo.getIpaymode() != null && paramvo.getIpaymode() != -1){
            sql.append(" AND t.ipaymode = ? \n");
            spm.addParam(paramvo.getIpaymode());
        }
		if(paramvo.getBegdate() != null && paramvo.getEnddate() != null){
		    sql.append(" AND (t.dpaydate >= ? AND t.dpaydate <= ? )\n");
            spm.addParam(paramvo.getBegdate());
            spm.addParam(paramvo.getEnddate());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
		    String[] strs = paramvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND t.pk_corp in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(paramvo.getPk_bill())){
			sql.append(" AND t.pk_paybill = ? \n");
            spm.addParam(paramvo.getPk_bill());
		}
		sql.append(" order by t.dpaydate desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	@Override
	public ChnPayBillVO updateOperate(ChnPayBillVO billvo, Integer opertype, String cuserid, String vreason)
			throws DZFWarpException {
		checkBillStatus(billvo);
		return updateData(billvo, opertype, cuserid, vreason);
	}

	/**
	 * 校验数据状态
	 * @param billVOs
	 */
	@Override
	public void checkBillStatus(ChnPayBillVO billvo) throws DZFWarpException {
		ChnPayBillVO oldvo = (ChnPayBillVO) singleObjectBO.queryByPrimaryKey(ChnPayBillVO.class, billvo.getPk_paybill());
		if(oldvo != null){
			if(oldvo.getDr() != null && oldvo.getDr() == 1){
				throw new BusinessException("单据号"+billvo.getVbillcode()+"数据错误");
			}else{
				if(oldvo.getTstamp().compareTo(billvo.getTstamp()) != 0){
					throw new BusinessException("单据号"+billvo.getVbillcode()+"发生变化");
				}
			}
		}else{
			billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"数据错误");
			throw new BusinessException("单据号"+billvo.getVbillcode()+"数据错误");
		}
	}
	
	/**
	 * 收款确认、确认驳回、取消确认
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 */
	private ChnPayBillVO updateData(ChnPayBillVO billvo, Integer opertype, String cuserid,String vreason){
		if (StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())) {
			throw new BusinessException("单据号"+billvo.getVbillcode()+"数据错误");
		}
		String uuid = UUID.randomUUID().toString();
		try {
			checkData(billvo);
			if(opertype == IStatusConstant.ICHNOPRATETYPE_3){//收款确认
				billvo = updateConfrimData(billvo, opertype, cuserid);
			}else if(opertype == IStatusConstant.ICHNOPRATETYPE_4){//确认驳回
				billvo = updateRejectData(billvo, vreason, cuserid);
			}else if(opertype == IStatusConstant.ICHNOPRATETYPE_5){//取消确认
				billvo = updateCancelData(billvo, opertype, cuserid);
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
		}
		return billvo;
	}
	
	/**
	 * 操作数据前校验
	 * @param billvo
	 * @throws DZFWarpException
	 */
	private void checkData(ChnPayBillVO billvo) throws DZFWarpException {
		ChnPayBillVO oldvo = (ChnPayBillVO) singleObjectBO.queryByPrimaryKey(ChnPayBillVO.class, billvo.getPk_paybill());
		if(oldvo == null || (oldvo != null && oldvo.getDr() != null && oldvo.getDr() == 1)){
			throw new BusinessException("界面数据发生变化，请刷新后再次尝试");
		}
		if(billvo.getTstamp().compareTo(oldvo.getTstamp()) != 0){
			throw new BusinessException("界面数据发生变化，请刷新后再次尝试");
		}
	}
	
	/**
	 * 收款确认
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateConfrimData(ChnPayBillVO billvo, Integer opertype, String cuserid) throws DZFWarpException {
//		if(StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())){
//			throw new BusinessException("单据号"+billvo.getVbillcode()+"数据错误");
//		}
//		String uuid = UUID.randomUUID().toString();
//		try {
//			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 120);
			if(billvo.getVstatus() != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_5){
				throw new BusinessException("单据号"+billvo.getVbillcode()+"状态不为【待确认】");
			}
			ChnDetailVO detvo = new ChnDetailVO();
			detvo.setPk_corp(billvo.getPk_corp());
			detvo.setNpaymny(billvo.getNpaymny());
			detvo.setIpaytype(billvo.getIpaytype());
			if(detvo.getIpaytype() != null){
				switch (detvo.getIpaytype()){
				case 1:
					detvo.setVmemo("保证金");
					break;
				case 2:
					detvo.setVmemo("预付款");
					break;
				}
			}
			detvo.setPk_bill(billvo.getPk_paybill());
			detvo.setCoperatorid(cuserid);
			detvo.setDoperatedate(billvo.getDpaydate());
			detvo.setDr(0);
			detvo.setIopertype(IStatusConstant.IDETAILTYPE_1);
			ChnBalanceVO balvo = null;
			String sql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype = ? ";
			SQLParameter spm = new SQLParameter();
			spm.addParam(billvo.getPk_corp());
			spm.addParam(billvo.getIpaytype());
			ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, sql, spm);
			if(balVOs != null && balVOs.length > 0){
				String uid = UUID.randomUUID().toString();
				try {
					LockUtil.getInstance().tryLockKey("cn_balance",
							billvo.getPk_corp() + "" + billvo.getIpaytype(), uid, 120);
					StringBuffer usql = new StringBuffer();
					spm = new SQLParameter();
					usql.append("UPDATE cn_balance l  \n");
					usql.append("   SET l.npaymny = nvl(l.npaymny,0) + ?  \n");
					spm.addParam(billvo.getNpaymny());
					usql.append(" WHERE nvl(l.dr,0) = 0 AND l.ipaytype = ?  \n");
					spm.addParam(billvo.getIpaytype());
					usql.append("   AND l.pk_corp = ?  \n");
					spm.addParam(billvo.getPk_corp());
					int res = singleObjectBO.executeUpdate(usql.toString(), spm);
					if(res != 1){
						throw new BusinessException("余额表金额更新错误");
					}
				} catch (Exception e) {
					if (e instanceof BusinessException)
						throw new BusinessException(e.getMessage());
					else
						throw new WiseRunException(e);
				} finally {
					LockUtil.getInstance().unLock_Key("cn_balance",
							billvo.getPk_corp() + "" + billvo.getIpaytype(), uid);
				}
				singleObjectBO.saveObject("000001", detvo);
			}else{
				balvo = new ChnBalanceVO();
				balvo.setPk_corp(billvo.getPk_corp());
				balvo.setNpaymny(billvo.getNpaymny());
				balvo.setIpaytype(billvo.getIpaytype());
				balvo.setVmemo(billvo.getVmemo());
				balvo.setCoperatorid(cuserid);
				balvo.setDoperatedate(new DZFDate());
				balvo.setDr(0);
				singleObjectBO.saveObject("000001", balvo);
				singleObjectBO.saveObject("000001", detvo);
			}
			List<String> upstr = new ArrayList<String>();
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_3);//付款单状态
			billvo.setVconfirmid(cuserid);//确认人
			billvo.setDconfirmtime(new DZFDateTime());//确认时间
			billvo.setTstamp(new DZFDateTime());//操作时间戳
			upstr.add("vstatus");
			upstr.add("vconfirmid");
			upstr.add("dconfirmtime");
			upstr.add("tstamp");
			if(billvo.getIrejectype() != null && billvo.getIrejectype() == 2){//确认驳回
				billvo.setIrejectype(null);//清空驳回类型
				billvo.setVreason(null);//清空驳回原因
				upstr.add("irejectype");
				upstr.add("vreason");
			}
			singleObjectBO.update(billvo, upstr.toArray(new String[0]));
//		} catch (Exception e) {
//			if (e instanceof BusinessException)
//				throw new BusinessException(e.getMessage());
//			else
//				throw new WiseRunException(e);
//		} finally {
//			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
//		}
		UserVO uservo = UserCache.getInstance().get(billvo.getVconfirmid(), null);
		if(uservo != null){
			billvo.setVconfirmname(uservo.getUser_name());
		}
		return billvo;
	}
	
	
	/**
	 * 取消确认
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateCancelData(ChnPayBillVO billvo, Integer opertype, String cuserid)
			throws DZFWarpException {
//		if (StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())) {
//			throw new BusinessException("单据号" + billvo.getVbillcode() + "数据错误");
//		}
//		String uuid = UUID.randomUUID().toString();
//		try {
//			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(), uuid, 120);
			if (billvo.getVstatus() != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_3) {
				throw new BusinessException("单据号" + billvo.getVbillcode() + "状态不为【已确认】");
			}
			ChnBalanceVO balvo = null;
			String sql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype = ? ";
			SQLParameter spm = new SQLParameter();
			spm.addParam(billvo.getPk_corp());
			spm.addParam(billvo.getIpaytype());
			ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, sql, spm);
			if (balVOs != null && balVOs.length > 0) {
				balvo = balVOs[0];
				DZFDouble balance = SafeCompute.sub(balvo.getNpaymny(), balvo.getNusedmny());
				if (balance.compareTo(billvo.getNpaymny()) < 0) {
					throw new BusinessException("单据号" + billvo.getVbillcode() + "余额不足");
				} else {
					String uiid = UUID.randomUUID().toString();
					String uid = UUID.randomUUID().toString();
					try {
						LockUtil.getInstance().tryLockKey("cn_balance", billvo.getPk_corp() + "" + billvo.getIpaytype(),
								uiid, 120);
						LockUtil.getInstance().tryLockKey("cn_detail", billvo.getPk_paybill(), uid, 120);
						// 更新余额表：
						StringBuffer usql = new StringBuffer();
						spm = new SQLParameter();
						usql.append("UPDATE cn_balance l  \n");
						usql.append("   SET l.npaymny = nvl(l.npaymny,0) - ?  \n");
						spm.addParam(billvo.getNpaymny());
						usql.append(" WHERE nvl(l.dr,0) = 0 AND l.ipaytype = ?  \n");
						spm.addParam(billvo.getIpaytype());
						usql.append("   AND l.pk_corp = ?  \n");
						spm.addParam(billvo.getPk_corp());
						int res = singleObjectBO.executeUpdate(usql.toString(), spm);
						if (res == 1) {
							sql = " delete from cn_detail where nvl(dr,0) = 0 and pk_bill = ? and pk_corp = ? ";
							spm = new SQLParameter();
							spm.addParam(billvo.getPk_paybill());
							spm.addParam(billvo.getPk_corp());
							int rest = singleObjectBO.executeUpdate(sql, spm);
							if(rest != 1){
								String unitname = "";
								CorpVO corpvo = CorpCache.getInstance().get(null, billvo.getPk_corp());
								if (corpvo != null) {
									unitname = corpvo.getUnitname();
								}
								throw new BusinessException("客户：" + unitname + "余额明细表付款金额更新错误；");
							}
						} else {
							String unitname = "";
							CorpVO corpvo = CorpCache.getInstance().get(null, billvo.getPk_corp());
							if (corpvo != null) {
								unitname = corpvo.getUnitname();
							}
							throw new BusinessException("客户：" + unitname + "余额表付款金额更新错误；");
						}
					} catch (Exception e) {
						if (e instanceof BusinessException)
							throw new BusinessException(e.getMessage());
						else
							throw new WiseRunException(e);
					} finally {
						LockUtil.getInstance().unLock_Key("cn_balance", billvo.getPk_corp() + "" + billvo.getIpaytype(),
								uiid);
						LockUtil.getInstance().unLock_Key("cn_detail", billvo.getPk_paybill(), uid);
					}
				}
			}
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_5);// 付款单状态 待确认
			billvo.setVconfirmid(cuserid);// 取消确认人
			billvo.setDconfirmtime(new DZFDateTime());// 取消确认时间
			billvo.setTstamp(new DZFDateTime());// 操作时间
			singleObjectBO.update(billvo, new String[] { "vstatus", "vconfirmid", "dconfirmtime", "tstamp" });
//		} catch (Exception e) {
//			if (e instanceof BusinessException)
//				throw new BusinessException(e.getMessage());
//			else
//				throw new WiseRunException(e);
//		} finally {
//			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(), uuid);
//		}
		return billvo;
	}

	/**
	 * 确认驳回
	 * @param billvo
	 * @param vreason
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateRejectData(ChnPayBillVO billvo, String vreason, String cuserid)
			throws DZFWarpException {
//		if (StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())) {
//			throw new BusinessException("单据号"+billvo.getVbillcode()+"数据错误");
//		}
//		String uuid = UUID.randomUUID().toString();
//		try {
//			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 60);
			if(billvo.getVstatus() != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_5){
				throw new BusinessException("单据号"+billvo.getVbillcode()+"状态不为【待确认】");
			}
			billvo.setIrejectype(2);//驳回类型：确认驳回
			billvo.setVconfirmid(cuserid);//确认-驳回人
			billvo.setDconfirmtime(new DZFDateTime());//确认驳回时间
			billvo.setVreason(vreason);
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_4);//付款单状态  已驳回
			billvo.setTstamp(new DZFDateTime());//操作时间
			String[] str = new String[]{"irejectype","vconfirmid","dconfirmtime","vreason","vstatus","tstamp"}; 
			singleObjectBO.update(billvo, str);
//		} catch (Exception e) {
//			if (e instanceof BusinessException)
//				throw new BusinessException(e.getMessage());
//			else
//				throw new WiseRunException(e);
//		} finally {
//			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
//		}
		UserVO uservo = UserCache.getInstance().get(billvo.getVconfirmid(), null);
		if(uservo != null){
			billvo.setVconfirmname(uservo.getUser_name());
		}
		return billvo;
	}
	
	@Override
	public ChnPayBillVO queryByID(String cid) throws DZFWarpException {
		return  (ChnPayBillVO)singleObjectBO.queryVOByID(cid, ChnPayBillVO.class);
	}

}
