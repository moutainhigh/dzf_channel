package com.dzf.service.channel.contract.impl;

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
import com.dzf.model.channel.contract.ApplyAuditVO;
import com.dzf.model.channel.contract.ChangeApplyVO;
import com.dzf.model.channel.contract.RejectHistoryHVO;
import com.dzf.model.channel.contract.RejectHistoryVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.contract.IContractAuditService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("contractauditser")
public class ContractAuditServiceImpl implements IContractAuditService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IPubService pubser;
	@Autowired
	private IUserService userServiceImpl;

	@Override
	public Integer queryTotalNum(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(pamvo, uservo);
		return multBodyObjectBO.queryDataTotal(ChangeApplyVO.class, qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChangeApplyVO> query(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySql(pamvo, uservo);
		List<ChangeApplyVO> list = (List<ChangeApplyVO>) multBodyObjectBO.queryDataPage(ChangeApplyVO.class,
				qryvo.getSql(), qryvo.getSpm(), pamvo.getPage(), pamvo.getRows(), "y.applytime");
		if(list != null && list.size() > 0){
			setShowName(list);
		}
		return list;
	}
	
	/**
	 * 设置参照显示名称
	 * @param list
	 * @throws DZFWarpException
	 */
	private void setShowName(List<ChangeApplyVO> list) throws DZFWarpException {
		Map<String,String> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);//渠道经理
		CorpVO corpvo = null;
		UserVO uservo = null;
		HashMap<String, UserVO> map = userServiceImpl.queryUserMap(IDefaultValue.DefaultGroup, true);
		for(ChangeApplyVO vo : list){
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
			if(corpvo != null){
				vo.setCorpkname(corpvo.getUnitname());
			}
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			if(corpvo != null){
				vo.setCorpname(corpvo.getUnitname());
				vo.setVarea(corpvo.getCitycounty());// 地区
			}
			if (marmap != null && !marmap.isEmpty()) {
				String manager = marmap.get(vo.getPk_corp());
				if (!StringUtil.isEmpty(manager)) {
					uservo = map.get(manager);
					if (uservo != null) {
						vo.setVmanagername(uservo.getUser_name());// 渠道经理
					}
				}
			}
		}
	}
	
	/**
	 * 获取查询条件
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySql(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT  \n") ;
		sql.append("       y.pk_changeapply,  \n") ;
		sql.append("       y.applytime,  \n") ;
		sql.append("       y.ichangetype,  \n") ;
		sql.append("       y.vchangeraeson,  \n") ;
		sql.append("       y.iapplystatus,  \n") ; 
		sql.append("       y.updatets,  \n") ; 
		sql.append("       y.pk_confrim,  \n");
		sql.append("       y.pk_contract,  \n") ;
		sql.append("       t.pk_corp,  \n") ; 
		sql.append("       t.pk_corpk,  \n") ; 
		sql.append("       t.vcontcode,  \n") ; 
		sql.append("       t.vbeginperiod,  \n") ; 
		sql.append("       t.vendperiod,  \n") ; 
		sql.append("       t.chargedeptname,  \n") ; 
		sql.append("       t.nmservicemny,  \n") ; 
		sql.append("       t.ireceivcycle,  \n") ; 
		sql.append("       t.icontractcycle,  \n") ; 
		sql.append("       nvl(t.ntotalmny,0) - nvl(t.nbookmny,0) AS naccountmny,  \n") ; 
		sql.append("       t.nbookmny  \n") ; 
		sql.append("  FROM cn_changeapply y  \n") ; 
		sql.append("  LEFT JOIN ynt_contract t ON y.pk_contract = t.pk_contract  \n") ; 
		//数据过滤：本人审核过的申请，都可以看到
		if(pamvo.getIreceivcycle() == null || (pamvo.getIreceivcycle() != null && pamvo.getIreceivcycle() != 1)){
			sql.append("  LEFT JOIN cn_applyaudit a ON a.pk_changeapply = y.pk_changeapply  \n") ; 
		}
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(y.dr, 0) = 0  \n");
		if(pamvo.getIreceivcycle() == null || (pamvo.getIreceivcycle() != null && pamvo.getIreceivcycle() != 1)){
			sql.append("   AND nvl(a.dr, 0) = 0  \n");
		}
		if(!StringUtil.isEmpty(pamvo.getVbeginperiod())){
			sql.append(" AND  SUBSTR(applytime, 0, 10) >= ? \n");
			spm.addParam(pamvo.getVbeginperiod());
		}
		if(!StringUtil.isEmpty(pamvo.getVendperiod())){
			sql.append(" AND  SUBSTR(applytime, 0, 10) <= ? \n");
			spm.addParam(pamvo.getVendperiod());
		}
		
		if(!StringUtil.isEmpty(pamvo.getVfilepath())){//申请类型
			String[] strs = pamvo.getVfilepath().split(",");
		    String where = SqlUtil.buildSqlForIn("y.ichangetype", strs);
		    sql.append(" AND ").append(where);
		}
		if(pamvo.getIapplystatus() != null && pamvo.getIapplystatus() != -1){
			sql.append(" AND y.iapplystatus = ? \n");
			spm.addParam(pamvo.getIapplystatus());
		}
		if(!StringUtil.isEmpty(pamvo.getChargedeptname())){
			if("1".equals(pamvo.getChargedeptname())){
				sql.append(" AND t.chargedeptname = '小规模纳税人' \n");
			}else if("2".equals(pamvo.getChargedeptname())){
				sql.append(" AND t.chargedeptname = '一般纳税人' \n");
			}
		}
		if (!StringUtil.isEmpty(pamvo.getVmanagername())) {//渠道经理
			String where = getQrySql(pamvo.getVmanagername(), IStatusConstant.IQUDAO);
			if (!StringUtil.isEmpty(where)) {
				sql.append(where);
			}
		}
		if(!StringUtil.isEmpty(pamvo.getPk_corp())){//加盟商
			String[] strs = pamvo.getPk_corp().split(",");
		    String where = SqlUtil.buildSqlForIn("t.pk_corp", strs);
		    sql.append(" AND ").append(where);
		}
		if(!StringUtil.isEmpty(pamvo.getPk_corpk())){//客户
			String[] strs = pamvo.getPk_corpk().split(",");
		    String where = SqlUtil.buildSqlForIn("t.pk_corpk", strs);
		    sql.append(" AND ").append(where);
		}
		
		//待审核数据过滤
		if(pamvo.getIreceivcycle() != null && pamvo.getIreceivcycle() == 1){
			sql.append("   AND ((y.vchannelid = ? AND y.iapplystatus = 1) OR  \n") ; 
			sql.append("        (y.vareaer = ? AND y.iapplystatus = 2) OR  \n") ; 
			sql.append("        (y.vdirector = ? AND y.iapplystatus = 3))    \n");
			spm.addParam(uservo.getCuserid());
			spm.addParam(uservo.getCuserid());
			spm.addParam(uservo.getCuserid());
		}else{
			//数据权限过滤
			sql.append(" AND ( ( ( y.vchannelid = ? OR y.vareaer = ? OR y.vdirector = ? ) \n");
			spm.addParam(uservo.getCuserid());
			spm.addParam(uservo.getCuserid());
			spm.addParam(uservo.getCuserid());
			sql.append(" AND y.iapplystatus IN (1, 2, 3) ) \n");
			sql.append(" OR a.coperatorid = ? ) \n");
			spm.addParam(uservo.getCuserid());
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 获取数据过滤条件
	 * @param cuserid
	 * @param qrytype  1：渠道经理；2：培训师；3：渠道运营；
	 * @return
	 * @throws DZFWarpException
	 */
	private String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		String[] corps = pubser.getManagerCorp(cuserid, qrytype);
		if(corps != null && corps.length > 0){
			String where = SqlUtil.buildSqlForIn(" t.pk_corp", corps);
			sql.append(" AND ").append(where);
		}else{
			sql.append(" AND t.pk_corp is null \n") ; 
		}
		return sql.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ComboBoxVO> queryAuditer(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		ChangeApplyVO oldvo = (ChangeApplyVO) singleObjectBO.queryByPrimaryKey(ChangeApplyVO.class,
				pamvo.getPk_changeapply());
		if (oldvo == null || (oldvo != null && oldvo.getDr() != null && oldvo.getDr() == 1)) {
			throw new BusinessException("待审核数据错误");
		}
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		// 申请状态 1：渠道待审（未处理）；2： 区总待审（处理中）；3：总经理待审（处理中）；4：运营待审（处理中）；5：已处理；6：已拒绝；
		if (oldvo.getIapplystatus() != null && oldvo.getIapplystatus() == 1) {
			sql.append("SELECT DISTINCT a.userid AS id,us.user_name as name \n");
			sql.append("  FROM cn_chnarea a  \n");
			sql.append("  LEFT JOIN cn_chnarea_b b ON a.pk_chnarea = b.pk_chnarea  \n");
			sql.append(" left join sm_user us on us.cuserid = a.userid");
			sql.append(" WHERE nvl(a.dr, 0) = 0  \n");
			sql.append("   AND nvl(b.dr, 0) = 0  \n");
			sql.append("   AND b.type = 1 \n");//渠道经理
			sql.append("   AND b.userid = ?  \n");
			spm.addParam(uservo.getCuserid());
		} else if (oldvo.getIapplystatus() != null && oldvo.getIapplystatus() == 2) {
			sql.append("SELECT vdeptuserid AS id,us.user_name as name \n");
			sql.append("  FROM cn_leaderset ld \n");
			sql.append(" left join sm_user us on us.cuserid = ld.vdeptuserid");
			sql.append(" WHERE nvl(dr, 0) = 0  \n");
		} else if(oldvo.getIapplystatus() != null && oldvo.getIapplystatus() == 3){
			return null;
		}
		List<ComboBoxVO> list = (List<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ComboBoxVO.class));
		if(list != null && list.size() > 0){
			QueryDeCodeUtils.decKeyUtils(new String[]{"name"}, list, 1);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ChangeApplyVO queryById(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySqlById(pamvo, uservo);
		List<ChangeApplyVO> list = (List<ChangeApplyVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChangeApplyVO.class));
		if (list != null && list.size() > 0) {
			ChangeApplyVO vo = list.get(0);
			CorpVO corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
			if(corpvo != null){
				vo.setCorpkname(corpvo.getUnitname());
			}
			//申请状态  1：渠道待审（未处理）；2： 区总待审（处理中）；3：总经理待审（处理中）；4：运营待审（处理中）；5：已处理；6：已拒绝；
			if(pamvo.getIopertype() != null && pamvo.getIopertype() == -1){//-1：审核查询；1：明细查询；
				if(vo.getIapplystatus() != null && vo.getIapplystatus() == 1){
					if(!uservo.getCuserid().equals(vo.getVchannelid())){
						throw new BusinessException("当前操作人员没有待审批任务");
					}
				}else if(vo.getIapplystatus() != null && vo.getIapplystatus() == 2){
					if(!uservo.getCuserid().equals(vo.getVareaer())){
						throw new BusinessException("当前操作人员没有待审批任务");
					}
				}else if(vo.getIapplystatus() != null && vo.getIapplystatus() == 3){
					if(!uservo.getCuserid().equals(vo.getVdirector())){
						throw new BusinessException("当前操作人员没有待审批任务");
					}
				}else{
					throw new BusinessException("当前操作人员没有待审批任务");
				}
			}
			//2、查询审批历史
			if(pamvo.getIopertype() != null && pamvo.getIopertype() == 1){
				ApplyAuditVO[] child =  queryAuditHistory(pamvo, uservo);
				vo.setChildren(child);
			}
			//3、查询驳回历史
			if(!StringUtil.isEmpty(vo.getVconfreason())){
				RejectHistoryHVO[] rejeVOs = qryRejectHistory(vo.getPk_contract());
				if(rejeVOs != null && rejeVOs.length > 0){
					vo.setBodys(rejeVOs);
				}
			}
			return vo;
		}else{
			throw new BusinessException("该数据已经发生变化，请重新查询后，再次尝试");
		}
	}
	
	/**
	 * 查询驳回历史
	 * @param pk_contract
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private RejectHistoryHVO[] qryRejectHistory(String pk_contract) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT h.*, r.user_name AS coperator \n");
		sql.append("  FROM cn_rejecthistory_h h  \n");
		sql.append("  LEFT JOIN sm_user r ON h.coperatorid = r.cuserid  \n");
		sql.append(" WHERE nvl(h.dr, 0) = 0  \n");
		sql.append("   AND nvl(r.dr, 0) = 0  \n");
		sql.append("   AND h.pk_contract = ?  \n");
		spm.addParam(pk_contract);
		sql.append(" ORDER BY h.ts DESC \n");
		List<RejectHistoryHVO> list = (List<RejectHistoryHVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RejectHistoryHVO.class));
		if(list != null && list.size() > 0){
			QueryDeCodeUtils.decKeyUtils(new String[]{"coperator"}, list, 1);
			return list.toArray(new RejectHistoryHVO[0]);
		}
		return null;
	}
	
	/**
	 * 查询审批历史
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private ApplyAuditVO[] queryAuditHistory(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n");
		sql.append("  FROM cn_applyaudit  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_changeapply = ?  \n");
		sql.append(" ORDER BY ts ASC \n");
		spm.addParam(pamvo.getPk_changeapply());
		List<ApplyAuditVO> list = (List<ApplyAuditVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ApplyAuditVO.class));
		if(list != null){
			return list.toArray(new ApplyAuditVO[0]);
		}
		return null;
	}
	
	/**
	 * 获取查询条件
	 * 
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlById(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT y.pk_changeapply,  \n");
		sql.append("       y.pk_contract,  \n");
		sql.append("       y.pk_confrim,  \n");
		sql.append("       y.applytime,  \n");
		sql.append("       t.vcontcode,  \n");
		sql.append("       y.pk_corp,  \n");
		sql.append("       y.pk_corpk,  \n");
		sql.append("       t.chargedeptname,  \n");
		sql.append("       t.ntotalmny,  \n");
		sql.append("       t.nmservicemny,  \n");
		sql.append("       t.nbookmny,  \n");
		sql.append("       t.vbeginperiod,  \n");
		sql.append("       t.vendperiod,  \n");
		sql.append("       t.vcontcode,  \n");
		sql.append("       t.dsigndate,  \n"); // 签订日期
		sql.append("       t.icontractcycle,  \n"); // 合同周期
		sql.append("       t.ireceivcycle,  \n"); // 收款周期
		sql.append("       SUBSTR(t.dsubmitime, 0, 10) AS dsubmidate, \n");// 提单日期
		sql.append("       n.ndedsummny,  \n");
		sql.append("       y.ichangetype,  \n");
		sql.append("       y.vchangeraeson,  \n");
		sql.append("       y.vchangememo,  \n");
		sql.append("       y.vstopperiod,  \n");
		sql.append("       y.updatets,  \n");
		
		sql.append("       y.docName,  \n");
		sql.append("       y.vfilepath,  \n");
		sql.append("       y.docTemp,  \n");
		sql.append("       y.vfilepath,  \n");

		sql.append("       y.vbchangeperiod,  \n");
		sql.append("       y.vechangeperiod,  \n");
		sql.append("       y.nchangetotalmny,  \n");
		sql.append("       y.vconfreason,  \n");
		
		sql.append("       y.vchannelid,  \n");
		sql.append("       y.vareaer,  \n");
		sql.append("       y.vdirector,  \n");
		sql.append("       y.ichangetype,  \n");

		sql.append("       y.iapplystatus  \n");
		sql.append("  FROM cn_changeapply y  \n");
		sql.append("  LEFT JOIN ynt_contract t ON y.pk_contract = t.pk_contract  \n");
		sql.append("  LEFT JOIN cn_contract n ON y.pk_contract = n.pk_contract  \n");
		sql.append(" WHERE nvl(y.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(n.dr, 0) = 0  \n");
		sql.append(" AND y.pk_changeapply = ? \n");
		spm.addParam(pamvo.getPk_changeapply());
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public void updateChange(ChangeApplyVO datavo, UserVO uservo) throws DZFWarpException {
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey("ynt_contract", datavo.getPk_contract(), uuid, 120);
			//1、更新前校验
			checkBeforeChange(datavo);
			
			//2、删除审批历史
			if(datavo.getIapplystatus() != null && datavo.getIapplystatus() == 1){
				deleteAuditHistory(datavo);
			}
			//3、记录审批历史
			saveAuditHistory(datavo, uservo);
			
			//4、更新合同相关状态
			updateContData(datavo, uservo);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key("ynt_contract", datavo.getPk_contract(), uuid);
		}
	}
	
	/**
	 * 保存审批历史
	 * @param datavo
	 * @param uservo
	 */
	private void saveAuditHistory(ChangeApplyVO datavo, UserVO uservo) throws DZFWarpException{
		ApplyAuditVO avo = new ApplyAuditVO();
		avo.setPk_changeapply(datavo.getPk_changeapply());
		avo.setPk_confrim(datavo.getPk_confrim());
		avo.setPk_contract(datavo.getPk_contract());
		avo.setPk_corp(datavo.getPk_corp());
		avo.setPk_corpk(datavo.getPk_corpk());
		
		if(datavo.getIchangetype() != null && (datavo.getIchangetype() == 1 || datavo.getIchangetype() == 2)){
			avo.setIopertype(2);// 操作类型1：审核；2：变更；
		}else if(datavo.getIchangetype() != null && datavo.getIchangetype() == 3){
			avo.setIopertype(1);// 操作类型1：审核；2：变更；
		}
		
		Integer opertype = datavo.getIopertype();
		if(opertype == 1){
			avo.setIapplystatus(datavo.getIapplystatus());
			if(datavo.getIapplystatus() == 1){
				avo.setVmemo("渠道已审");
			}else if(datavo.getIapplystatus() == 2){
				avo.setVmemo("区总已审");
			}else if(datavo.getIapplystatus() == 3){
				avo.setVmemo("总经理已审");
			}
		}else if(opertype == 2){
			avo.setIapplystatus(6);
			if(datavo.getIapplystatus() == 1){
				avo.setVmemo("渠道驳回");
			}else if(datavo.getIapplystatus() == 2){
				avo.setVmemo("区总驳回");
			}else if(datavo.getIapplystatus() == 3){
				avo.setVmemo("总经理驳回");
			}
		}
		
		avo.setVreason(datavo.getVconfreason());
		avo.setCoperatorid(uservo.getCuserid());
		avo.setDoperatedate(new DZFDate());
		singleObjectBO.saveObject(IDefaultValue.DefaultGroup, avo);
	}
	
	/**
	 * 删除审批历史
	 * @param datavo
	 * @throws DZFWarpException
	 */
	private void deleteAuditHistory(ChangeApplyVO datavo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("UPDATE cn_applyaudit  \n") ;
		sql.append("   SET dr = 1  \n") ; 
		sql.append(" WHERE nvl(dr, 0) = 0  \n") ; 
		sql.append("   AND pk_changeapply = ?  \n");
		spm.addParam(datavo.getPk_changeapply());
		singleObjectBO.executeUpdate(sql.toString(), spm);
	}
	
	/**
	 * 操作前校验
	 * @param data
	 * @throws DZFWarpException
	 */
	private void checkBeforeChange(ChangeApplyVO data) throws DZFWarpException {
		if(data.getIopertype() != null && data.getIopertype() == 1){
			//1、合同终止审核或作废审核
			if(data.getIchangetype() != null && (data.getIchangetype() == 1 || data.getIchangetype() == 2)){
				if(data.getIapplystatus() != null && (data.getIapplystatus() == 1 || data.getIapplystatus() == 2)){
					if(StringUtil.isEmpty(data.getVauditer())){
						throw new BusinessException("下一审核人不能为空");
					}
				}
			//2、非常规套餐审核
			}else if(data.getIchangetype() != null && data.getIchangetype() == 3){
				if(data.getIapplystatus() != null && data.getIapplystatus() == 1 ){
					if(StringUtil.isEmpty(data.getVauditer())){
						throw new BusinessException("下一审核人不能为空");
					}
				}
			}
		}
		ChangeApplyVO oldvo = (ChangeApplyVO) singleObjectBO.queryByPrimaryKey(ChangeApplyVO.class,
				data.getPk_changeapply());
		if (oldvo == null || (oldvo != null && oldvo.getDr() != null && oldvo.getDr() == 1)) {
			throw new BusinessException("合同数据错误");
		}
		if(oldvo.getUpdatets().compareTo(data.getUpdatets()) != 0){
			throw new BusinessException("申请数据发生变化，请重新查询后，再次尝试");
		}
	}
	
	/**
	 * 更新合同相关状态
	 * @param data
	 * @param uservo
	 * @throws DZFWarpException
	 */
	private void updateContData(ChangeApplyVO data, UserVO uservo) throws DZFWarpException {
		Integer opertype = data.getIopertype();
		// 1、更新原合同-申请状态
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("UPDATE ynt_contract  \n");
		if (opertype == 1) {
			//1、合同终止审核或作废审核
			if(data.getIchangetype() != null && (data.getIchangetype() == 1 || data.getIchangetype() == 2)){
				if(data.getIapplystatus() != null && data.getIapplystatus() == 1){//渠道经理审核
					sql.append("   SET iversion = 2  \n");
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 2){//区总审核
					sql.append("   SET iversion = 3  \n");
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 3){//总经理审核
					sql.append("   SET iversion = 4  \n");
				}
			//2、非常规套餐审核
			}else if(data.getIchangetype() != null && data.getIchangetype() == 3 ){
				if(data.getIapplystatus() != null && data.getIapplystatus() == 1){//渠道经理审核
					sql.append("   SET iversion = 2  \n");
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 2){//区总审核
					sql.append("   SET iversion = 4  \n");
				}
			}
		} else if (opertype == 2) {
			//非常规套餐审核
			if(data.getIchangetype() != null && data.getIchangetype() == 3 ){
				sql.append("   SET iversion = 6,  \n");
				sql.append("       vstatus = 7,  \n");
				sql.append("       vdeductstatus = 7  \n");
			}else{
				sql.append("   SET iversion = 6  \n");
			}
		}
		sql.append(" , tstamp = ? \n");
		spm.addParam(new DZFDateTime());
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_corp = ?  \n");
		spm.addParam(data.getPk_corp());
		sql.append("   AND pk_contract = ?  \n");
		spm.addParam(data.getPk_contract());
		int res = singleObjectBO.executeUpdate(sql.toString(), spm);
		if (res != 1) {
			throw new BusinessException("原合同申请状态更新失败");
		}
		//2、更新历史合同-申请状态 只有合同终止审核或作废审核，才更新历史合同-申请状态
		if(data.getIchangetype() != null && (data.getIchangetype() == 1 || data.getIchangetype() == 2)){
			sql = new StringBuffer();
			spm = new SQLParameter();
			sql.append("UPDATE cn_contract  \n");
			if (opertype == 1) {
				if(data.getIapplystatus() != null && data.getIapplystatus() == 1){//渠道经理审核
					sql.append("   SET iapplystatus = 2  \n");
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 2){//区总审核
					sql.append("   SET iapplystatus = 3  \n");
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 3){//总经理审核
					sql.append("   SET iapplystatus = 4  \n");
				}
			} else if (opertype == 2) {
				sql.append("   SET iapplystatus = 6  \n");
			}
			sql.append(" , tstamp = ? \n");
			spm.addParam(new DZFDateTime());
			sql.append(" WHERE nvl(dr, 0) = 0  \n");
			sql.append("   AND pk_corp = ?  \n");
			spm.addParam(data.getPk_corp());
			sql.append("   AND pk_confrim = ?  \n");
			spm.addParam(data.getPk_confrim());
			res = singleObjectBO.executeUpdate(sql.toString(), spm);
			if (res != 1) {
				throw new BusinessException("历史合同申请状态更新失败");
			}
		}
		//3、更新变更合同申请/非常规套餐申请-申请状态
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("UPDATE cn_changeapply  \n");
		if (opertype == 1) {
			//1、合同终止审核或作废审核
			if(data.getIchangetype() != null && (data.getIchangetype() == 1 || data.getIchangetype() == 2)){
				if(data.getIapplystatus() != null && data.getIapplystatus() == 1){//渠道经理审核
					sql.append("   SET iapplystatus = 2,  \n");
					sql.append("       vareaer = ?  \n");
					spm.addParam(data.getVauditer());
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 2){//区总审核
					sql.append("   SET iapplystatus = 3,  \n");
					sql.append("       vdirector = ?  \n");
					spm.addParam(data.getVauditer());
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 3){//总经理审核
					sql.append("   SET iapplystatus = 4  \n");
				}
			//2、非常规套餐审核
			}else if(data.getIchangetype() != null && data.getIchangetype() == 3){
				if(data.getIapplystatus() != null && data.getIapplystatus() == 1){//渠道经理审核
					sql.append("   SET iapplystatus = 2,  \n");
					sql.append("       vareaer = ?  \n");
					spm.addParam(data.getVauditer());
					//清空驳回历史(驳回历史待渠道运营审核之后清空)
//					deleteRejeHistory(data, uservo);
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 2){//区总审核
					sql.append("   SET iapplystatus = 4  \n");
				}
			}
		} else if (opertype == 2) {
			sql.append("   SET iapplystatus = 6,  \n");
			sql.append("       vconfreason = ?, \n");
			spm.addParam(data.getVconfreason());
			sql.append("       vchannelid = null, \n");
			sql.append("       vareaer = null, \n");
			sql.append("       vdirector = null \n");
			if(data.getIchangetype() != null && data.getIchangetype() == 3){
				//添加驳回历史
				saveRejectHistory(data, uservo);
			}
		}
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_corp = ?  \n");
		spm.addParam(data.getPk_corp());
		sql.append("   AND pk_changeapply = ?  \n");
		spm.addParam(data.getPk_changeapply());
		res = singleObjectBO.executeUpdate(sql.toString(), spm);
		if (res != 1) {
			throw new BusinessException("合同变更申请-申请状态更新失败");
		}
	}
	
	/**
	 * 保存驳回原因
	 * @param paramvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void saveRejectHistory(ChangeApplyVO data, UserVO uservo) throws DZFWarpException {
		//存储驳回原因历史及明细：
		RejectHistoryHVO hvo = new RejectHistoryHVO();
		hvo.setPk_contract(data.getPk_contract());
		hvo.setPk_corp(data.getPk_corp());
		hvo.setVreason(data.getVconfreason());
		hvo.setDr(0);
		hvo.setCoperatorid(uservo.getCuserid());
		hvo.setDoperatedate(new DZFDate());
		
		RejectHistoryVO bvo = new RejectHistoryVO();
		bvo.setPk_contract(data.getPk_contract());
		bvo.setPk_corp(data.getPk_corp());
		bvo.setVreason(data.getVconfreason());//驳回原因
//		bvo.setVsuggest("");//修改建议
		bvo.setDr(0);
		bvo.setCoperatorid(uservo.getCuserid());
		bvo.setDoperatedate(new DZFDate());
		
		hvo.setChildren(new RejectHistoryVO[]{bvo});
		singleObjectBO.saveObject(IDefaultValue.DefaultGroup, hvo);
	}
	
	/**
	 * 删除驳回历史
	 * @param data
	 * @param uservo
	 * @throws DZFWarpException
	 */
	private void deleteRejeHistory(ChangeApplyVO data, UserVO uservo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("DELETE FROM cn_rejecthistory  \n") ;
		sql.append(" WHERE nvl(dr, 0) = 0  \n") ; 
		sql.append("   AND pk_contract = ?  \n") ; 
		sql.append("   AND pk_corp = ?  \n");
		spm.addParam(data.getPk_contract());
		spm.addParam(data.getPk_corp());
		singleObjectBO.executeUpdate(sql.toString(), spm);
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("DELETE FROM cn_rejecthistory_h  \n") ;
		sql.append(" WHERE nvl(dr, 0) = 0  \n") ; 
		sql.append("   AND pk_contract = ?  \n") ; 
		sql.append("   AND pk_corp = ?  \n");
		spm.addParam(data.getPk_contract());
		spm.addParam(data.getPk_corp());
		singleObjectBO.executeUpdate(sql.toString(), spm);
	}

}
