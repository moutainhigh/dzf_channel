package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
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
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.IChnPayConfService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("chnpayconfser")
public class ChnPayConfServiceImpl implements IChnPayConfService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IUserService userServiceImpl;

	@Override
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(paramvo);
		return multBodyObjectBO.queryDataTotal(ChnPayBillVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnPayBillVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(paramvo);
		List<ChnPayBillVO> list = (List<ChnPayBillVO>) multBodyObjectBO.queryDataPage(ChnPayBillVO.class,
				sqpvo.getSql(), sqpvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
		if (list != null && list.size() > 0) {
			CorpVO accvo = null;
			QueryDeCodeUtils.decKeyUtils(new String[] { "vconfirmname" }, list, 1);
			for (ChnPayBillVO vo : list) {
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if (accvo != null) {
					vo.setCorpname(accvo.getUnitname());
				}
			}
		}
		return list;
	}

	/**
	 * 获取查询条件
	 * 
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO paramvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_paybill, \n");
		sql.append("       t.pk_corp, \n");
		sql.append("       t.vbillcode, \n");
		sql.append("       t.dpaydate, \n");
		sql.append("       t.vhandleid, \n");
		sql.append("       t.vconfirmid, \n");
		sql.append("       t.dconfirmtime, \n");
		sql.append("       t.vmemo, \n");
		sql.append("       t.coperatorid, \n");
		sql.append("       t.doperatedate, \n");
		sql.append("       t.npaymny, \n");
		sql.append("       t.ipaymode, \n");
		sql.append("       t.ipaytype, \n");
		sql.append("       t.vstatus, \n");
		sql.append("       t.tstamp, \n");
		sql.append("       t.docName, \n");
		sql.append("       t.docOwner, \n");
		sql.append("       t.docTime, \n");
		sql.append("       t.vfilepath, \n");
		sql.append("       t.vbankname, \n");
		sql.append("       t.vbankcode, \n");
		sql.append("       t.vreason, \n");
		sql.append("       t.submitime, \n");
		sql.append("       t.submitid, \n");
		sql.append("       t.systype, \n");
		sql.append("       t.vapproveid, \n");
		sql.append("       t.dapprovedate, \n");
		sql.append("       t.dapprovetime, \n");
		sql.append("       t.irejectype, \n");
		sql.append("       t.ichargetype, \n");
		sql.append("       us.user_name as vconfirmname  \n");
		sql.append("  FROM cn_paybill t\n");
		sql.append("  LEFT JOIN sm_user us ON us.cuserid = t.vconfirmid");
		sql.append("  LEFT JOIN bd_account account ON t.pk_corp = account.pk_corp \n");
		sql.append(" WHERE nvl(t.dr,0) = 0 \n");
		sql.append("   AND nvl(account.dr, 0) = 0  \n");
		sql.append("   AND account.ischannel = 'Y' \n");
		sql.append("   AND account.isaccountcorp = 'Y' \n");
		if (paramvo.getQrytype() != null && paramvo.getQrytype() != -1) {// 查询状态
			sql.append(" AND t.vstatus = ? \n");
			spm.addParam(paramvo.getQrytype());
		} else {
			sql.append(" AND t.vstatus in ( ?, ?) \n");
			spm.addParam(IStatusConstant.IPAYSTATUS_3);
			spm.addParam(IStatusConstant.IPAYSTATUS_5);
		}
		if (paramvo.getIpaytype() != null && paramvo.getIpaytype() != -1) {
			sql.append(" AND t.ipaytype = ? \n");
			spm.addParam(paramvo.getIpaytype());
		}
		if (paramvo.getIpaymode() != null && paramvo.getIpaymode() != -1) {
			sql.append(" AND t.ipaymode = ? \n");
			spm.addParam(paramvo.getIpaymode());
		}
		if (paramvo.getSeletype() != null && paramvo.getSeletype() != -1) {
			sql.append(" AND t.ichargetype = ? \n");
			spm.addParam(paramvo.getSeletype());
		}
		if (paramvo.getBegdate() != null && paramvo.getEnddate() != null) {
			sql.append(" AND (t.dpaydate >= ? AND t.dpaydate <= ? )\n");
			spm.addParam(paramvo.getBegdate());
			spm.addParam(paramvo.getEnddate());
		}
		//加盟商过滤：
		if (!StringUtil.isEmpty(paramvo.getPk_corp())) {
			String[] strs = paramvo.getPk_corp().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND t.pk_corp in (").append(inSql).append(")");
		}else{
			if (paramvo.getCorptype() != null && paramvo.getCorptype() != -1) {
				sql.append(" AND account.channeltype = ? \n");
				spm.addParam(paramvo.getCorptype());
			} else {
				sql.append(" AND account.channeltype != 9 \n");
			}
		}
		if (!StringUtil.isEmpty(paramvo.getPk_bill())) {
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
	 * 
	 * @param billVOs
	 */
	@Override
	public void checkBillStatus(ChnPayBillVO billvo) throws DZFWarpException {
		ChnPayBillVO oldvo = (ChnPayBillVO) singleObjectBO.queryByPrimaryKey(ChnPayBillVO.class,
				billvo.getPk_paybill());
		if (oldvo != null) {
			if (oldvo.getDr() != null && oldvo.getDr() == 1) {
				throw new BusinessException("单据号" + billvo.getVbillcode() + "数据错误");
			} else {
				if (oldvo.getTstamp().compareTo(billvo.getTstamp()) != 0) {
					throw new BusinessException("单据号" + billvo.getVbillcode() + "发生变化");
				}
			}
		} else {
			billvo.setVerrmsg("单据号" + billvo.getVbillcode() + "数据错误");
			throw new BusinessException("单据号" + billvo.getVbillcode() + "数据错误");
		}
	}

	/**
	 * 收款确认、确认驳回、取消确认
	 * 
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 */
	private ChnPayBillVO updateData(ChnPayBillVO billvo, Integer opertype, String cuserid, String vreason) {
		if (StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())) {
			throw new BusinessException("单据号" + billvo.getVbillcode() + "数据错误");
		}
		String uuid = UUID.randomUUID().toString();
		try {
			checkData(billvo);
			if (opertype == IStatusConstant.ICHNOPRATETYPE_3) {// 收款确认
				billvo = updateConfrimData(billvo, opertype, cuserid);
			} else if (opertype == IStatusConstant.ICHNOPRATETYPE_4) {// 确认驳回
				billvo = updateRejectData(billvo, vreason, cuserid);
			} else if (opertype == IStatusConstant.ICHNOPRATETYPE_5) {// 取消确认
				billvo = updateCancelData(billvo, opertype, cuserid);
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(), uuid);
		}
		return billvo;
	}

	/**
	 * 操作数据前校验
	 * 
	 * @param billvo
	 * @throws DZFWarpException
	 */
	private void checkData(ChnPayBillVO billvo) throws DZFWarpException {
		ChnPayBillVO oldvo = (ChnPayBillVO) singleObjectBO.queryByPrimaryKey(ChnPayBillVO.class,
				billvo.getPk_paybill());
		if (oldvo == null || (oldvo != null && oldvo.getDr() != null && oldvo.getDr() == 1)) {
			throw new BusinessException("界面数据发生变化，请刷新后再次尝试");
		}
		if (billvo.getTstamp().compareTo(oldvo.getTstamp()) != 0) {
			throw new BusinessException("界面数据发生变化，请刷新后再次尝试");
		}
	}

	/**
	 * 收款确认
	 * 
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateConfrimData(ChnPayBillVO billvo, Integer opertype, String cuserid)
			throws DZFWarpException {
		if (billvo.getVstatus() != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_5) {
			throw new BusinessException("单据号" + billvo.getVbillcode() + "状态不为【待确认】");
		}
		//更新余额及明细表
		updateBalanceDet(billvo, cuserid);
		//更新付款单信息
		updatePayBill(billvo, cuserid);
		return billvo;
	}
	
	/**
	 * 更新付款单信息
	 * @param billvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updatePayBill(ChnPayBillVO billvo, String cuserid) throws DZFWarpException {
		List<String> upstr = new ArrayList<String>();
		billvo.setVstatus(IStatusConstant.IPAYSTATUS_3);// 付款单状态
		billvo.setVconfirmid(cuserid);// 确认人
		billvo.setDconfirmtime(new DZFDateTime());// 确认时间
		billvo.setTstamp(new DZFDateTime());// 操作时间戳
		upstr.add("vstatus");
		upstr.add("vconfirmid");
		upstr.add("dconfirmtime");
		upstr.add("tstamp");
		upstr.add("ichargetype");
		if (billvo.getIrejectype() != null && billvo.getIrejectype() == 2) {// 确认驳回
			billvo.setIrejectype(null);// 清空驳回类型
			billvo.setVreason(null);// 清空驳回原因
			upstr.add("irejectype");
			upstr.add("vreason");
		}
		setChargeType(billvo);
		singleObjectBO.update(billvo, upstr.toArray(new String[0]));
		UserVO uservo = userServiceImpl.queryUserJmVOByID(billvo.getVconfirmid());
		if (uservo != null) {
			billvo.setVconfirmname(uservo.getUser_name());
		}
	}
	
	/**
	 * 更新付款余额及明细表
	 * @param billvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updateBalanceDet(ChnPayBillVO billvo, String cuserid) throws DZFWarpException {
		ChnDetailVO detvo = new ChnDetailVO();
		detvo.setPk_corp(billvo.getPk_corp());
		detvo.setNpaymny(billvo.getNpaymny());
		detvo.setIpaytype(billvo.getIpaytype());
		if (detvo.getIpaytype() != null) {
			switch (detvo.getIpaytype()) {
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
		if (balVOs != null && balVOs.length > 0) {
			String uid = UUID.randomUUID().toString();
			try {
				LockUtil.getInstance().tryLockKey("cn_balance", billvo.getPk_corp() + "" + billvo.getIpaytype(), uid,
						120);
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
				if (res != 1) {
					throw new BusinessException("余额表金额更新错误");
				}
			} catch (Exception e) {
				if (e instanceof BusinessException)
					throw new BusinessException(e.getMessage());
				else
					throw new WiseRunException(e);
			} finally {
				LockUtil.getInstance().unLock_Key("cn_balance", billvo.getPk_corp() + "" + billvo.getIpaytype(), uid);
			}
			singleObjectBO.saveObject("000001", detvo);
		} else {
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
	}

	/**
	 * 设置付款类型为预付款，是首次充值，还是后续充值
	 * 
	 * @param billvo
	 */
	private void setChargeType(ChnPayBillVO billvo) throws DZFWarpException {
		if (billvo.getIpaytype() == 2) {
			billvo.setIchargetype(1);
			SQLParameter sp = new SQLParameter();
			sp.addParam(billvo.getPk_corp());
			sp.addParam(IStatusConstant.IPAYTYPE_2);
			sp.addParam(IStatusConstant.IPAYSTATUS_3);
			StringBuffer sql = new StringBuffer();
			sql.append(" select 1 from cn_paybill ");
			sql.append(" where nvl(dr, 0) = 0 and pk_corp = ? ");
			sql.append("  and ipaytype=? and vstatus=?");
			boolean b = singleObjectBO.isExists(billvo.getPk_corp(), sql.toString(), sp);
			if (b) {
				billvo.setIchargetype(2);
			}
		}
	}

	/**
	 * 判断是否可以取消，该预付款付款单
	 * 
	 * @param billvo
	 */
	@SuppressWarnings("unchecked")
	private void checkIsCancel(ChnPayBillVO billvo) throws DZFWarpException {
		if (billvo.getIpaytype() == 2) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(billvo.getPk_corp());
			sp.addParam(IStatusConstant.IPAYTYPE_2);
			sp.addParam(IStatusConstant.IPAYSTATUS_3);
			sp.addParam(billvo.getDconfirmtime());
			sp.addParam(billvo.getPk_paybill());
			StringBuffer sql = new StringBuffer();
			sql.append(" select vbillcode from cn_paybill ");
			sql.append(" where nvl(dr, 0) = 0 and pk_corp = ? ");
			sql.append("  and ipaytype=? and vstatus=? ");
			sql.append("  and dconfirmtime>? and pk_paybill!=? ");
			List<ChnPayBillVO> vos = (List<ChnPayBillVO>) singleObjectBO.executeQuery(sql.toString(), sp,
					new BeanListProcessor(ChnPayBillVO.class));
			if (vos != null && vos.size() > 0) {
				throw new BusinessException("请先取消 " + vos.get(0).getVbillcode() + "付款单");
			}
		}
	}

	/**
	 * 取消确认
	 * 
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateCancelData(ChnPayBillVO billvo, Integer opertype, String cuserid)
			throws DZFWarpException {
		if (billvo.getVstatus() != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_3) {
			throw new BusinessException("单据号" + billvo.getVbillcode() + "状态不为【已确认】");
		}
		checkIsCancel(billvo);
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
						if (rest != 1) {
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
		billvo.setVconfirmid(null);// 取消确认人
		billvo.setDconfirmtime(null);// 取消确认时间
		billvo.setIchargetype(null);// 付款单充值类型
		billvo.setTstamp(new DZFDateTime());// 操作时间
		singleObjectBO.update(billvo,
				new String[] { "vstatus", "vconfirmid", "dconfirmtime", "tstamp", "ichargetype" });
		return billvo;
	}

	/**
	 * 确认驳回
	 * 
	 * @param billvo
	 * @param vreason
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateRejectData(ChnPayBillVO billvo, String vreason, String cuserid) throws DZFWarpException {
		if (billvo.getVstatus() != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_5) {
			throw new BusinessException("单据号" + billvo.getVbillcode() + "状态不为【待确认】");
		}
		billvo.setIrejectype(2);// 驳回类型：确认驳回
		billvo.setVconfirmid(cuserid);// 确认-驳回人
		billvo.setDconfirmtime(new DZFDateTime());// 确认驳回时间
		billvo.setVreason(vreason);
		billvo.setVstatus(IStatusConstant.IPAYSTATUS_4);// 付款单状态 已驳回
		billvo.setTstamp(new DZFDateTime());// 操作时间
		String[] str = new String[] { "irejectype", "vconfirmid", "dconfirmtime", "vreason", "vstatus", "tstamp" };
		singleObjectBO.update(billvo, str);
		UserVO uservo = userServiceImpl.queryUserJmVOByID(billvo.getVconfirmid());
		if (uservo != null) {
			billvo.setVconfirmname(uservo.getUser_name());
		}
		return billvo;
	}

	@Override
	public ChnPayBillVO queryByID(String cid) throws DZFWarpException {
		return (ChnPayBillVO) singleObjectBO.queryVOByID(cid, ChnPayBillVO.class);
	}

}
