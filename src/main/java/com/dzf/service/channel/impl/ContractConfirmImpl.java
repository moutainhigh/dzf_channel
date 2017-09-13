package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.DAOException;
import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ArrayListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.channel.ContractConfrimVO;
import com.dzf.model.demp.contract.ContractDocVO;
import com.dzf.model.demp.contract.ContractVO;
import com.dzf.model.packagedef.PackageDefVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_set.ResponAreaVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.service.channel.IContractConfirm;

@Service("contractconfser")
public class ContractConfirmImpl implements IContractConfirm {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractConfrimVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<String> pklist = new ArrayList<String>();
		List<ContractConfrimVO> retlist = new ArrayList<ContractConfrimVO>();
		//1、已确认数据
		QrySqlSpmVO confqryvo = getConfrimQry(paramvo);
		ContractConfrimVO[] confVOs = (ContractConfrimVO[]) singleObjectBO.queryByCondition(ContractConfrimVO.class,
				confqryvo.getSql(), confqryvo.getSpm());
		if(confVOs != null && confVOs.length > 0){
			for(ContractConfrimVO confvo : confVOs){
				retlist.add(confvo);
				pklist.add(confvo.getPk_contract()+""+confvo.getVcontcode());
			}
		}
		
		//2、合同数据
		QrySqlSpmVO contqryvo = getContractQry(paramvo);
		List<ContractConfrimVO> conflist = (List<ContractConfrimVO>) singleObjectBO.executeQuery(contqryvo.getSql(),
				contqryvo.getSpm(), new BeanListProcessor(ContractConfrimVO.class));
		
		if (conflist != null && conflist.size() > 0) {
			CorpVO corpvo = null;
//			int icyclenum = 0;
			Map<Integer, String> areamap = queryAreaMap();
			String aeraname = "";
			Integer cyclenum = 0;
			for (ContractConfrimVO vo : conflist) {
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if (corpvo != null) {
					vo.setCorpname(corpvo.getUnitname());
				}
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
				if (corpvo != null) {
					aeraname = "";
					vo.setCorpkname(corpvo.getUnitname());
					vo.setChargedeptname(corpvo.getChargedeptname());
					if(!StringUtil.isEmpty(areamap.get(corpvo.getVprovince()))){
						aeraname = areamap.get(corpvo.getVprovince())+""+areamap.get(corpvo.getVcity());
					}
					vo.setVarea(aeraname);
				}
//				icyclenum = ToolsUtil.getCyclenum(vo.getDbegindate(), vo.getDenddate());
//				vo.setIcontractcycle(icyclenum);;//合同周期
				cyclenum = 0;
				if(vo.getIcyclenum() != null && vo.getIcyclenum() != 0){
					cyclenum = vo.getIcyclenum() * (Integer.parseInt(vo.getVchargecycle()));
					vo.setVchargecycle(String.valueOf(cyclenum));//收款周期
				}else{
					vo.setVchargecycle(null);//收款周期
				}
				if(!pklist.contains(vo.getPk_contract()+""+vo.getVcontcode())){
					retlist.add(vo);
				}
			}
		}
		//3、按照时间戳排序
		Collections.sort(retlist, new Comparator<ContractConfrimVO>() {
			@Override
			public int compare(ContractConfrimVO o1, ContractConfrimVO o2) {
				return -o1.getTs().compareTo(o2.getTs());
			}
		});
		return retlist;
	}
	
	/**
	 * 查询区域的值
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, String> queryAreaMap() throws DZFWarpException {
		Map<Integer, String> areamap = new HashMap<Integer, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select region_id, region_name\n");
		sql.append("  from ynt_area\n");
		sql.append(" where nvl(dr, 0) = 0\n");
		sql.append("   order by region_id asc ");
		ArrayList<ResponAreaVO> list = (ArrayList<ResponAreaVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(ResponAreaVO.class));
		if(list != null && list.size() > 0){
			for(ResponAreaVO vo: list){
				areamap.put(vo.getRegion_id(), vo.getRegion_name());
			}
		}
		return areamap;
	}
	
	/**
	 * 获取确认数据查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getConfrimQry(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" nvl(dr,0) = 0 ");
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			if(paramvo.getQrytype() != 0){
				sql.append("   AND vdeductstatus = ? \n");
				spm.addParam(paramvo.getQrytype());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append("   AND dbegindate >= ? \n") ; 
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append("   AND dbegindate <= ? \n") ; 
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by ts desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 获取合同数据查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getContractQry(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT  con.*,busi.vbusitypename as vbusitypename \n") ;
		sql.append("  FROM ynt_contract con \n") ; 
		sql.append("  LEFT JOIN bd_account acc ON con.pk_corp = acc.pk_corp \n") ; 
		sql.append("  LEFT JOIN ynt_busitype busi ON con.busitypemin = busi.pk_busitype");
		sql.append(" WHERE nvl(con.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(acc.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(con.isflag, 'N') = 'Y' \n") ; 
		sql.append("   AND nvl(acc.ischannel,'N') = 'Y' \n");
		sql.append("   AND nvl(con.icosttype, 0) = 0 \n") ; 
		sql.append("   AND con.vdeductstatus = 1 \n");//加盟商合同状态 = 待审核
		sql.append("   AND con.icontracttype = 2 \n");//合同类型 = 加盟商合同
//		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
//			if(paramvo.getQrytype() != 0){
//				sql.append("   AND con.vdeductstatus = ? \n");
//				spm.addParam(paramvo.getQrytype());
//			}
//		}else{
//			if(paramvo.getBegdate() != null){
//				sql.append("   AND con.dbegindate >= ? \n") ; 
//				spm.addParam(paramvo.getBegdate());
//			}
//			if(paramvo.getEnddate() != null){
//				sql.append("   AND con.dbegindate <= ? \n") ; 
//				spm.addParam(paramvo.getEnddate());
//			}
//		}
		if(paramvo.getBegdate() != null){
			sql.append("   AND con.dbegindate >= ? \n") ; 
			spm.addParam(paramvo.getBegdate());
		}
		if(paramvo.getEnddate() != null){
			sql.append("   AND con.dbegindate <= ? \n") ; 
			spm.addParam(paramvo.getEnddate());
		}
		sql.append(" order by con.ts desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

//	@Override
//	public ContractConfrimVO updateConfStatus(ContractConfrimVO[] confrimVOs, Integer status) throws DZFWarpException {
//		//1-确认成功；2-确认失败；3-取消确认；
//		checkConfStatus(confrimVOs[0], status);
//		ContractConfrimVO retvo = null;
//		StringBuffer sql = new StringBuffer();
//		SQLParameter spm = new SQLParameter();
//		sql.append(" UPDATE ynt_contract SET vdeductstatus = ?, vconfreason = ? WHERE nvl(dr,0) = 0 AND pk_contract = ? ");
//		if(IStatusConstant.ICONTRACTCONFRIM_1 == status){
//			confrimVOs[0].setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_2);
//			confrimVOs[0].setTstamp(new DZFDateTime());
//			retvo = (ContractConfrimVO) singleObjectBO.saveObject("000001", confrimVOs[0]);
//			spm.addParam(IStatusConstant.IDEDUCTSTATUS_2);//待扣款
//			spm.addParam(confrimVOs[0].getVconfreason());
//		}else if(IStatusConstant.ICONTRACTCONFRIM_2 == status){
//			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);//待确认
//			spm.addParam(confrimVOs[0].getVconfreason());
//			retvo = queryContractById(confrimVOs[0].getPk_contract());
//		}else if(IStatusConstant.ICONTRACTCONFRIM_3 == status){
//			String delsql = " UPDATE cn_contract SET dr = 1 WHERE nvl(dr,0) = 0 AND pk_confrim = ? ";
//			SQLParameter delspm = new SQLParameter();
//			delspm.addParam(confrimVOs[0].getPk_confrim());
//			singleObjectBO.executeUpdate(delsql, delspm);
//			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);//待确认
//			spm.addParam("");
//			retvo = queryContractById(confrimVOs[0].getPk_contract());
//			retvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_1);
//		}
//		spm.addParam(confrimVOs[0].getPk_contract());
//		singleObjectBO.executeUpdate(sql.toString(), spm);
//		return retvo;
//	}
	
	/**
	 * 合同确认成功、合同确认失败、取消确认状态校验
	 * @param confrimvo
	 * @param status
	 */
	private void checkConfStatus(ContractConfrimVO confrimvo, Integer status){
		if(IStatusConstant.ICONTRACTCONFRIM_1 == status || IStatusConstant.ICONTRACTCONFRIM_2 == status ){
			if(IStatusConstant.IDEDUCTSTATUS_2 == confrimvo.getVdeductstatus()){
				throw new BusinessException("合同状态为待扣款");
			}else if(IStatusConstant.IDEDUCTSTATUS_3 == confrimvo.getVdeductstatus()){
				throw new BusinessException("合同状态为已扣款");
			}
			checkContState(confrimvo);
		}else if( IStatusConstant.ICONTRACTCONFRIM_3 == status){
			if(IStatusConstant.IDEDUCTSTATUS_1 == confrimvo.getVdeductstatus()){
				throw new BusinessException("合同状态为待确认");
			}else if(IStatusConstant.IDEDUCTSTATUS_3 == confrimvo.getVdeductstatus()){
				throw new BusinessException("合同状态为已扣款");
			}
			checkConfState(confrimvo);
		}
	}
	
	/**
	 * 合同确认前状态校验
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void checkContState(ContractConfrimVO confrimvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT tstamp \n") ;
		sql.append("  FROM ynt_contract t \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n") ; 
		sql.append("   AND t.pk_contract = ? ");
		spm.addParam(confrimvo.getPk_contract());
		ArrayList<Object> result = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm, new ArrayListProcessor());
		if(result != null && result.size() > 0){
			Object[] obj = (Object[]) result.get(0); 
			DZFDateTime tstamp = new DZFDateTime(String.valueOf(obj[0]));
			if(tstamp != null && tstamp.compareTo(confrimvo.getTstamp()) != 0){
				throw new BusinessException("合同号："+confrimvo.getVcontcode()+"数据发生变化，请刷新界面后，再次尝试");
			}
		}
	}
	
	/**
	 * 合同取消确认前状态校验
	 * @param confrimVOs
	 * @throws DZFWarpException
	 */
	private void checkConfState(ContractConfrimVO confrimvo) throws DZFWarpException {
		ContractConfrimVO oldvo = (ContractConfrimVO) singleObjectBO.queryByPrimaryKey(ContractConfrimVO.class, 
				confrimvo.getPk_confrim());
		if(oldvo != null){
			if(oldvo.getTstamp().compareTo(confrimvo.getTstamp()) != 0){
				throw new BusinessException("合同号："+confrimvo.getVcontcode()+"数据发生变化，请刷新界面后，再次尝试");
			}
		}
	}
	
	/**
	 * 通过主键查询合同数据
	 * @param pk_contract
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private ContractConfrimVO queryContractById(String pk_contract) throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT  con.*,busi.vbusitypename as vbusitypename \n") ;
		sql.append("  FROM ynt_contract con \n") ; 
		sql.append("  LEFT JOIN bd_account acc ON con.pk_corp = acc.pk_corp \n") ; 
		sql.append("  LEFT JOIN ynt_busitype busi ON con.busitypemin = busi.pk_busitype");
		sql.append(" WHERE nvl(con.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(acc.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(con.isflag, 'N') = 'Y' \n") ; 
		sql.append("   AND nvl(acc.ischannel,'N') = 'Y' \n");
		sql.append("   AND nvl(con.icosttype, 0) = 0 \n") ; 
		sql.append("   AND con.vdeductstatus is not null \n");
		sql.append("   AND con.pk_contract = ? \n");
		spm.addParam(pk_contract);
		List<ContractConfrimVO> conflist = (List<ContractConfrimVO>) singleObjectBO.executeQuery(sql.toString(),
				spm, new BeanListProcessor(ContractConfrimVO.class));
		if (conflist != null && conflist.size() > 0) {
			Map<Integer, String> areamap = queryAreaMap();
			ContractConfrimVO vo = conflist.get(0);
			CorpVO corpvo = null;
//			int icyclenum = 0;
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			if (corpvo != null) {
				vo.setCorpname(corpvo.getUnitname());
			}
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
			if (corpvo != null) {
				vo.setCorpkname(corpvo.getUnitname());
				vo.setChargedeptname(corpvo.getChargedeptname());
				String aeraname = "";
				if(!StringUtil.isEmpty(areamap.get(corpvo.getVprovince()))){
					aeraname = areamap.get(corpvo.getVprovince())+""+areamap.get(corpvo.getVcity());
				}
				vo.setVarea(aeraname);
			}
//			icyclenum = ToolsUtil.getCyclenum(vo.getDbegindate(), vo.getDenddate());
//			vo.setIcontractcycle(icyclenum);//合同周期
			Integer cyclenum = 0;
			if(vo.getIcyclenum() != null && vo.getIcyclenum() != 0){
				cyclenum = vo.getIcyclenum() * (Integer.parseInt(vo.getVchargecycle()));
				vo.setVchargecycle(String.valueOf(cyclenum));//收款周期
			}else{
				vo.setVchargecycle(null);//收款周期
			}
			return vo;
		}
		return null;
	}

	@Override
	public ContractConfrimVO queryDebitData(ContractConfrimVO paramvo) throws DZFWarpException {
		ContractConfrimVO retvo = queryContractById(paramvo.getPk_contract());
		String sql = " nvl(dr,0) = 0 AND pk_corp = ? AND ipaytype = 2";//只查询预付款
		SQLParameter spm = new SQLParameter();
		spm.addParam(paramvo.getPk_corp());
		ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, sql, spm);
		if(balVOs != null && balVOs.length > 0){
			retvo.setNbalance(SafeCompute.sub(balVOs[0].getNpaymny(), balVOs[0].getNusedmny()));
		}
		PackageDefVO packvo = (PackageDefVO) singleObjectBO.queryByPrimaryKey(PackageDefVO.class, paramvo.getPk_packagedef());
		if(packvo != null){
			if(!StringUtil.isEmptyWithTrim(packvo.getVmemo())){
				int num = packvo.getIpublishnum() - packvo.getIusenum();
				retvo.setVsalespromot(packvo.getVmemo() + " 剩余名额" + num + "个");
			}
		}
		return retvo;
	}

	@Override
	public ContractConfrimVO updateDeductData(ContractConfrimVO paramvo, Integer opertype, String cuserid) throws DZFWarpException {
		String errmsg = "";
		if(IStatusConstant.IDEDUCTYPE_1 == opertype){//扣款
			//1、更新合同加盟合同状态、驳回原因
			errmsg = updateContract(paramvo);
			if(StringUtil.isEmpty(errmsg)){
				throw new BusinessException(errmsg);
			}
			//2、回写付款余额
			errmsg = updateBalanceMny(paramvo, cuserid);
			if(StringUtil.isEmpty(errmsg)){
				throw new BusinessException(errmsg);
			}
			//3、生成合同审核数据
			paramvo = saveContConfrim(paramvo);
			//4、回写套餐促销活动名额
			updateSerPackage(paramvo);
		}else if(IStatusConstant.IDEDUCTYPE_2 == opertype){//驳回
			errmsg = updateContract(paramvo);
			if(StringUtil.isEmpty(errmsg)){
				throw new BusinessException(errmsg);
			}
			paramvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_3);//已驳回
			paramvo.setVconfreason(paramvo.getVconfreason());//驳回原因
		}
		
		return paramvo;
	}

	/**
	 * 更新套餐使用个数
	 * @param paramvo
	 */
	private void updateSerPackage(ContractConfrimVO paramvo){
		PackageDefVO packvo = (PackageDefVO) singleObjectBO.queryByPrimaryKey(PackageDefVO.class, paramvo.getPk_packagedef());
		try {
			if(packvo != null){
				LockUtil.getInstance().tryLockKey(packvo.getTableName(), packvo.getPk_packagedef(), 60);
				int num = packvo.getIusenum();
				packvo.setIusenum(num + 1);
				singleObjectBO.update(packvo, new String[]{"iusenum"});
			}
		} finally {
			LockUtil.getInstance().unLock_Key(packvo.getTableName(), packvo.getPk_packagedef());
		}
	}
	
	/**
	 * 保存合同审核信息
	 * @param paramvo
	 * @return
	 */
	private ContractConfrimVO saveContConfrim(ContractConfrimVO paramvo){
		paramvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_2);
		paramvo.setTstamp(new DZFDateTime());
		return (ContractConfrimVO) singleObjectBO.saveObject("000001", paramvo);
	}
	/**
	 * 更新合同信息
	 * @param contvo
	 * @param paramvo
	 * @throws DZFWarpException
	 */
	private String updateContract(ContractConfrimVO paramvo) throws DZFWarpException {
		String msg = "";
		ContractVO contvo = (ContractVO) singleObjectBO.queryByPrimaryKey(ContractVO.class, paramvo.getPk_contract());
		if(contvo != null){
			if(paramvo.getTstamp().compareTo(contvo.getTstamp()) != 0){
				return "合同数据发生变化，请刷新数据后，再次尝试";
			}
			if(IStatusConstant.IDEDUCTSTATUS_1 != contvo.getVdeductstatus()){
				return "合同状态不为待审核";
			}
			contvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_2);//已审核
			contvo.setVconfreason(paramvo.getVconfreason());//驳回原因
			contvo.setTstamp(new DZFDateTime());
			singleObjectBO.update(contvo, new String[]{"vdeductstatus","vconfreason","tstamp"});
		}else{
			return "合同数据错误";
		}
		return msg;
	}
	
	/**
	 * 更新余额信息
	 * @param paramvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private String updateBalanceMny(ContractConfrimVO paramvo, String cuserid) throws DZFWarpException {
		ChnBalanceVO balvo = null;
		String yesql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype = ? ";
		SQLParameter yespm = new SQLParameter();
		yespm.addParam(paramvo.getPk_corp());
		yespm.addParam(IStatusConstant.IPAYTYPE_2);
		ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, yesql, yespm);
		if(balVOs != null && balVOs.length > 0){
			balvo = balVOs[0];
			if(balvo.getNpaymny().compareTo(paramvo.getNdeductmny()) < 0){
//				throw new BusinessException("预付款余额不足");
				return "预付款余额不足";
			}
			balvo.setNusedmny(SafeCompute.add(balvo.getNusedmny(), paramvo.getNdeductmny()));
			singleObjectBO.update(balvo, new String[]{"nusedmny"});
			
			ChnDetailVO detvo = new ChnDetailVO();
			detvo.setPk_corp(paramvo.getPk_corp());
			detvo.setNusedmny(paramvo.getNdeductmny());
			detvo.setIpaytype(IStatusConstant.IPAYTYPE_2);
			detvo.setPk_bill(paramvo.getPk_confrim());
			detvo.setVmemo(paramvo.getCorpkname()+"、"+paramvo.getVcontcode());
			detvo.setCoperatorid(cuserid);
			detvo.setDoperatedate(new DZFDate());
			detvo.setDr(0);
			detvo.setIopertype(IStatusConstant.IDETAILTYPE_2);
			singleObjectBO.saveObject("000001", detvo);
			
		}else{
			return "预付款余额不足";
		}
		return "";
	}

	@Override
	public ContractDocVO[] getAttatches(ContractDocVO qvo) throws DZFWarpException {
		if (qvo == null) {
			return null;
		}
		if (StringUtil.isEmpty(qvo.getPk_contract()) && StringUtil.isEmpty(qvo.getPk_contract_doc())) {
			throw new BusinessException("获取附件参数有误");
		}
		StringBuffer condition = new StringBuffer();
		condition.append(" nvl(dr,0)=0 and pk_corp=?");
		SQLParameter params = new SQLParameter();
		params.addParam(qvo.getPk_corp());
		if (!StringUtil.isEmpty(qvo.getPk_contract())) {
			condition.append(" and  pk_contract=? ");
			params.addParam(qvo.getPk_contract());
		}
		if (!StringUtil.isEmpty(qvo.getPk_contract_doc())) {
			condition.append(" and pk_contract_doc = ? ");
			params.addParam(qvo.getPk_contract_doc());
		}
		String orderBy = " ts desc ";
		@SuppressWarnings("unchecked")
		List<ContractDocVO> resvos = (List<ContractDocVO>) singleObjectBO.retrieveByClause(ContractDocVO.class,
				condition.toString(), orderBy, params);
		if (resvos != null) {
			return resvos.toArray(new ContractDocVO[0]);
		}
		return null;
	}
}
