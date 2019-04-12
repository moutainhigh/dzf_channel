package com.dzf.service.channel.contract.impl;

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
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.IStatusConstant;
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
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.contract.IContractAuditService;
import com.dzf.service.pub.IPubService;

@Service("contractauditser")
public class ContractAuditServiceImpl implements IContractAuditService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IPubService pubser;

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
					uservo = UserCache.getInstance().get(manager, null);
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
		sql.append("       t.pk_corp,  \n") ; 
		sql.append("       t.pk_corpk,  \n") ; 
		sql.append("       y.iapplystatus,  \n") ; 
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
		if(pamvo.getIreceivcycle() == null || (pamvo.getIreceivcycle() != null && pamvo.getIreceivcycle() != -1)){
			sql.append("  LEFT JOIN cn_applyaudit a ON a.pk_changeapply = y.pk_changeapply  \n") ; 
		}
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(y.dr, 0) = 0  \n");
		if (!StringUtil.isEmpty(pamvo.getVmanagername())) {//渠道经理
			String where = getQrySql(pamvo.getVmanagername(), IStatusConstant.IQUDAO);
			if (!StringUtil.isEmpty(where)) {
				sql.append(where);
			}
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

		//数据权限过滤
		sql.append(" AND ( ( ( y.vchannelid = ? OR y.vareaer = ? OR y.vdirector = ? ) \n");
		spm.addParam(uservo.getCuserid());
		spm.addParam(uservo.getCuserid());
		spm.addParam(uservo.getCuserid());
		sql.append(" AND y.iapplystatus IN (1, 2, 3) ) \n");
		sql.append(" OR a.coperatorid = ? ) \n");
		spm.addParam(uservo.getCuserid());
		
		//待审核数据过滤
		if(pamvo.getIreceivcycle() != null && pamvo.getIreceivcycle() != -1){
			sql.append(" AND ( y.vchannelid = ? OR y.vareaer = ? OR y.vdirector = ? ) \n");
			spm.addParam(uservo.getCuserid());
			spm.addParam(uservo.getCuserid());
			spm.addParam(uservo.getCuserid());
			sql.append(" AND y.iapplystatus IN (1, 2, 3) \n");
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
			sql.append("SELECT DISTINCT a.userid AS id \n");
			sql.append("  FROM cn_chnarea a  \n");
			sql.append("  LEFT JOIN cn_chnarea_b b ON a.pk_chnarea = b.pk_chnarea  \n");
			sql.append(" WHERE nvl(a.dr, 0) = 0  \n");
			sql.append("   AND nvl(b.dr, 0) = 0  \n");
			sql.append("   AND b.userid = ?  \n");
			spm.addParam(uservo.getCuserid());
		} else if (oldvo.getIapplystatus() != null && oldvo.getIapplystatus() == 2) {
			sql.append("SELECT vdeptuserid AS id \n");
			sql.append("  FROM cn_leaderset  \n");
			sql.append(" WHERE nvl(dr, 0) = 0  \n");
		} else if(oldvo.getIapplystatus() != null && oldvo.getIapplystatus() == 3){
			return null;
		}
		List<ComboBoxVO> list = (List<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ComboBoxVO.class));
		if(list != null && list.size() > 0){
			UserVO uvo = null;
			for(ComboBoxVO bvo : list){
				uvo = UserCache.getInstance().get(bvo.getId(), null);
				if(uvo != null){
					bvo.setName(uvo.getUser_name());
				}
			}
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
			}
			return vo;
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

		sql.append("       y.vbchangeperiod,  \n");
		sql.append("       y.vechangeperiod,  \n");
		sql.append("       y.nchangetotalmny,  \n");
		sql.append("       y.vfilepath,  \n");
		
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
			LockUtil.getInstance().tryLockKey(datavo.getTableName(), datavo.getPk_changeapply(), uuid, 120);
			//1、更新前校验
			checkBeforeChange(datavo);
			//2、更新合同相关状态
			updateContData(datavo);
			//3、删除审批历史
			if(datavo.getIapplystatus() != null && datavo.getIapplystatus() == 1){
				deleteAuditHistory(datavo);
			}
			//4、记录审批历史
			saveAuditHistory(datavo, uservo);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(datavo.getTableName(), datavo.getPk_changeapply(), uuid);
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
		avo.setPk_contract(datavo.getPk_contract());
		avo.setPk_corp(datavo.getPk_corp());
		avo.setPk_corpk(datavo.getPk_corpk());
		avo.setIapplystatus(datavo.getIapplystatus());
		avo.setVreason(datavo.getVconfreason());
		avo.setCoperatorid(uservo.getCuserid());
		avo.setDoperatedate(new DZFDate());
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
	 * @throws DZFWarpException
	 */
	private void updateContData(ChangeApplyVO data) throws DZFWarpException {
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
			sql.append("   SET iversion = 6  \n");
		}
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_corp = ?  \n");
		spm.addParam(data.getPk_corp());
		sql.append("   AND pk_contract = ?  \n");
		spm.addParam(data.getPk_contract());
		int res = singleObjectBO.executeUpdate(sql.toString(), spm);
		if (res != 1) {
			throw new BusinessException("原合同申请状态更新失败");
		}
		//只有合同终止审核或作废审核，才更新历史合同-申请状态
		if(data.getIchangetype() != null && (data.getIchangetype() == 1 || data.getIchangetype() == 2)){
			// 2、更新历史合同-申请状态
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
		//3、更新变更合同审核-申请状态
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append("UPDATE cn_changeapply  \n");
		if (opertype == 1) {
			//1、合同终止审核或作废审核
			if(data.getIchangetype() != null && (data.getIchangetype() == 1 || data.getIchangetype() == 2)){
				if(data.getIapplystatus() != null && data.getIapplystatus() == 1){//渠道经理审核
					sql.append("   SET iapplystatus = 2,  \n");
					sql.append("       vareaer = ?  \n");
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 2){//区总审核
					sql.append("   SET iapplystatus = 3,  \n");
					sql.append("       vdirector = ?  \n");
				}else if(data.getIapplystatus() != null && data.getIapplystatus() == 3){//总经理审核
					sql.append("   SET iapplystatus = 4  \n");
				}
				spm.addParam(data.getVauditer());
			//2、非常规套餐审核
			}else if(data.getIchangetype() != null && data.getIchangetype() == 3){
				if(data.getIapplystatus() != null && data.getIapplystatus() == 1){//渠道经理审核
					sql.append("   SET iapplystatus = 2,  \n");
					sql.append("       vareaer = ?  \n");
					spm.addParam(data.getVauditer());
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

}
