package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
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
			List<ChnPayBillVO> retlist = new ArrayList<ChnPayBillVO>();
			CorpVO accvo = null;
			for(ChnPayBillVO vo : list){
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(accvo != null){
					vo.setCorpname(accvo.getUnitname());
					if(!StringUtil.isEmpty(paramvo.getCorpname())){
						if(vo.getCorpname().indexOf(paramvo.getCorpname()) != -1){
							retlist.add(vo);
						}
					}
				}
			}
			if(!StringUtil.isEmpty(paramvo.getCorpname())){
				return retlist;
			}
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
		sql.append("SELECT * FROM cn_paybill WHERE nvl(dr,0) = 0 \n");
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND vstatus = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		if(paramvo.getIpaytype() != null && paramvo.getIpaytype() != -1){
		    sql.append(" AND ipaytype = ? \n");
            spm.addParam(paramvo.getIpaytype());
		}
		if(paramvo.getIpaymode() != null && paramvo.getIpaymode() != -1){
            sql.append(" AND ipaymode = ? \n");
            spm.addParam(paramvo.getIpaymode());
        }
		if(paramvo.getBegdate() != null && paramvo.getEnddate() != null){
		    sql.append(" AND (dpaydate >= ? and dpaydate <= ? )\n");
            spm.addParam(paramvo.getBegdate());
            spm.addParam(paramvo.getEnddate());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
		    String[] strs = paramvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND pk_corp in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(paramvo.getPk_bill())){
			sql.append(" AND pk_paybill = ? \n");
            spm.addParam(paramvo.getPk_bill());
		}
		sql.append(" AND vstatus != 1");
		sql.append(" order by dpaydate desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	@Override
	public ChnPayBillVO updateOperate(ChnPayBillVO billvo, Integer opertype, String cuserid, String vreason)
			throws DZFWarpException {
		billvo = checkBillStatus(billvo);
		if(!StringUtil.isEmpty(billvo.getVerrmsg())){
			return billvo;
		}
		return updateData(billvo, opertype, cuserid, vreason);
	}

	/**
	 * 校验数据状态
	 * @param billVOs
	 */
	private ChnPayBillVO checkBillStatus(ChnPayBillVO billvo) throws DZFWarpException {
		ChnPayBillVO oldvo = (ChnPayBillVO) singleObjectBO.queryByPrimaryKey(ChnPayBillVO.class, billvo.getPk_paybill());
		if(oldvo != null){
			if(oldvo.getDr() != null && oldvo.getDr() == 1){
				billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"数据错误");
			}else{
				if(oldvo.getTstamp().compareTo(billvo.getTstamp()) != 0){
					billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"发生变化");
				}
			}
		}else{
			billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"数据错误");
		}
		return billvo;
	}
	
	/**
	 * 收款确认、取消确认、确认驳回、审批驳回、取消审批
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 */
	private ChnPayBillVO updateData(ChnPayBillVO billvo, Integer opertype, String cuserid,String vreason){
		if(opertype == IStatusConstant.ICHNOPRATETYPE_3){//收款确认
			return updateConfrimData(billvo, opertype, cuserid);
		}else if(opertype == IStatusConstant.ICHNOPRATETYPE_5){//取消确认
			return updateCancelData(billvo, opertype, cuserid);
		}else if(opertype == IStatusConstant.ICHNOPRATETYPE_4 || opertype == IStatusConstant.ICHNOPRATETYPE_9){//确认驳回、审批驳回
			return updateRejectData(billvo, opertype,vreason, cuserid);
		}else if(opertype == IStatusConstant.ICHNOPRATETYPE_2){//取消审批
			return updateReturnData(billvo, opertype, cuserid);
		}
		return billvo;
	}
	
	/**
	 * 取消审批
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateReturnData(ChnPayBillVO billvo, Integer opertype, String cuserid)throws DZFWarpException{
		if(StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())){
			billvo.setVerrmsg("数据错误");
			return billvo;
		}
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 120);
			if(billvo.getVstatus() == IStatusConstant.IPAYSTATUS_5){
				billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"状态不为【待确认】");
				return billvo;
			}
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_2);//付款单状态 待审批
			billvo.setVconfirmid(cuserid);//取消审批人
			billvo.setDconfirmtime(new DZFDateTime());//取消审批时间
			billvo.setTstamp(new DZFDateTime());//操作时间
			singleObjectBO.update(billvo, new String[]{"vstatus","vconfirmid", "dconfirmtime", "tstamp"});
		} finally {
			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
		}
		return billvo;
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
		if(StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())){
			billvo.setVerrmsg("数据错误");
			return billvo;
		}
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 120);
			if(billvo.getVstatus() != IStatusConstant.IPAYSTATUS_5){
				billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"状态不为【待确认】");
				return billvo;
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
				balvo = balVOs[0];
				balvo.setNpaymny(SafeCompute.add(balvo.getNpaymny(), billvo.getNpaymny()));
				singleObjectBO.update(balvo, new String[]{"npaymny"});
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
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_3);//付款单状态
			billvo.setVconfirmid(cuserid);//确认人
			billvo.setDconfirmtime(new DZFDateTime());//确认时间
			billvo.setTstamp(new DZFDateTime());//操作时间戳
			billvo.setVreason(null);//清空驳回原因
			billvo.setIrejectype(null);//清空驳回类型
			singleObjectBO.update(billvo, new String[]{"vstatus","vconfirmid", "dconfirmtime", "tstamp"});
		} finally {
			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
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
	private ChnPayBillVO updateCancelData(ChnPayBillVO billvo, Integer opertype, String cuserid)throws DZFWarpException{
		if(StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())){
			billvo.setVerrmsg("数据错误");
			return billvo;
		}
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 120);
			if(billvo.getVstatus() == IStatusConstant.IPAYSTATUS_3){
				billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"状态不为【已确认】");
				return billvo;
			}
			ChnBalanceVO balvo = null;
			String sql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype = ? ";
			SQLParameter spm = new SQLParameter();
			spm.addParam(billvo.getPk_corp());
			spm.addParam(billvo.getIpaytype());
			ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, sql, spm);
			if(balVOs != null && balVOs.length > 0){
				balvo = balVOs[0];
				DZFDouble balance = SafeCompute.sub(balvo.getNpaymny(), balvo.getNusedmny());
				if(balance.compareTo(billvo.getNpaymny()) < 0){
					billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"余额不足");
					return billvo;
				}else{
					DZFDouble npaymny = SafeCompute.sub(balvo.getNpaymny(), billvo.getNpaymny());
					balvo.setNpaymny(npaymny);
					if(npaymny.compareTo(DZFDouble.ZERO_DBL) == 0){
						balvo.setDr(1);
						singleObjectBO.update(balvo, new String[]{"npaymny","dr"});
					}else{
						singleObjectBO.update(balvo, new String[]{"npaymny"});
					}
				}
				sql = " update cn_detail set dr = 1 where pk_bill = ? ";
				spm = new SQLParameter();
				spm.addParam(billvo.getPk_paybill());
				singleObjectBO.executeUpdate(sql, spm);
			}
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_5);//付款单状态 待确认
			billvo.setVconfirmid(cuserid);//取消确认人
			billvo.setDconfirmtime(new DZFDateTime());//取消确认时间
			billvo.setTstamp(new DZFDateTime());//操作时间
			singleObjectBO.update(billvo, new String[]{"vstatus","vconfirmid", "dconfirmtime", "tstamp"});
		} finally {
			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
		}
		return billvo;
	}

	/**
	 * 驳回
	 * @param billvo
	 * @param opertype  4：收款驳回；9：审批驳回；
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateRejectData(ChnPayBillVO billvo, Integer opertype, String vreason, String cuserid)
			throws DZFWarpException {
		if (StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())) {
			billvo.setVerrmsg("数据错误");
			return billvo;
		}
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 60);
//			if(billvo.getVstatus() == IStatusConstant.ICHNOPRATETYPE_3){
//				billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"状态已为【已确认】");
//				return billvo;
//			}else if(billvo.getVstatus() == IStatusConstant.ICHNOPRATETYPE_4){
//				billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"状态已为【已驳回】");
//				return billvo;
//			}
			List<String> upstr = new ArrayList<String>();
			if(opertype == IStatusConstant.ICHNOPRATETYPE_4){
				if(billvo.getVstatus() != IStatusConstant.IPAYSTATUS_5){
					billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"状态不为【待确认】");
					return billvo;
				}
				billvo.setIrejectype(2);//驳回类型：确认驳回
				billvo.setVconfirmid(cuserid);//确认-驳回人
				billvo.setDconfirmtime(new DZFDateTime());//确认驳回时间
				upstr.add("vconfirmid");
				upstr.add("dconfirmtime");
			}else if(opertype == IStatusConstant.ICHNOPRATETYPE_9){
				if(billvo.getVstatus() != IStatusConstant.IPAYSTATUS_2){
					billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"状态不为【待审批】");
					return billvo;
				}
				billvo.setIrejectype(1);//驳回类型：审批驳回
				billvo.setVauditid(cuserid);//确认-驳回人
				billvo.setDaudittime(new DZFDateTime());//确认-驳回时间
				upstr.add("vauditid");
				upstr.add("daudittime");
			}
			upstr.add("irejectype");
			billvo.setVreason(vreason);
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_4);//付款单状态
			billvo.setTstamp(new DZFDateTime());//操作时间
			upstr.add("vreason");
			upstr.add("vstatus");
			upstr.add("tstamp");
			singleObjectBO.update(billvo, upstr.toArray(new String[0]));
		} finally {
			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
		}
		return billvo;
	}
	
	
	/**
	 * 校验数据状态
	 * @param billVOs
	 */
	private void checkStatus(ChnPayBillVO[] billVOs) throws DZFWarpException {
		Map<String,ChnPayBillVO> billmap = new HashMap<String,ChnPayBillVO>();
		List<String> pklist = new ArrayList<String>();
		for(ChnPayBillVO vo : billVOs){
			billmap.put(vo.getPk_paybill(), vo);
			pklist.add(vo.getPk_paybill());
		}
		ChnPayBillVO[] oldVOs = null;
		if(pklist != null && pklist.size() > 0){
			StringBuffer sql = new StringBuffer();
			sql.append(" nvl(dr,0) =0 ");
			String where = SqlUtil.buildSqlForIn("pk_paybill", pklist.toArray(new String[0]));
			sql.append(" and ").append(where);
			oldVOs = (ChnPayBillVO[]) singleObjectBO.queryByCondition(ChnPayBillVO.class, sql.toString(), null);
			if(oldVOs != null && oldVOs.length > 0){
				ChnPayBillVO billvo = null;
				for(ChnPayBillVO oldvo : oldVOs){
					billvo = billmap.get(oldvo.getPk_paybill());
					if(oldvo.getTstamp().compareTo(billvo.getTstamp()) != 0){
						billvo.setVerrmsg("单据号"+billvo.getVbillcode()+"发生变化");
					}
				}
			}
		}
	}

	@Override
	public ChnPayBillVO queryByID(String cid) throws DZFWarpException {
		return  (ChnPayBillVO)singleObjectBO.queryVOByID(cid, ChnPayBillVO.class);
	}

}
