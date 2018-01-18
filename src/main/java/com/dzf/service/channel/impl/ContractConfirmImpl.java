package com.dzf.service.channel.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ArrayListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.channel.ContractConfrimVO;
import com.dzf.model.channel.PackageDefVO;
import com.dzf.model.demp.contract.ContractDocVO;
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
import com.dzf.pub.image.ImageCommonPath;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.IContractConfirm;

@Service("contractconfser")
public class ContractConfirmImpl implements IContractConfirm {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<ContractConfrimVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<String> pklist = new ArrayList<String>();
		List<ContractConfrimVO> retlist = new ArrayList<ContractConfrimVO>();
		if((paramvo.getVdeductstatus() != null && (paramvo.getVdeductstatus() == 1 || 
				paramvo.getVdeductstatus() == 9 || paramvo.getVdeductstatus() == 10 
				|| paramvo.getVdeductstatus() == -2 )) || 
				!StringUtil.isEmpty(paramvo.getPk_bill())){//查询审核通过数据 （1、合同状态为已审核；2、或别的界面<付款单余额>联查到此界面）
			qryContractConData(paramvo, pklist, retlist);
		}else if(paramvo.getVdeductstatus() != null && paramvo.getVdeductstatus() == 5){//查询待审核合同数据（合同状态为待审核）
			qryContractData(paramvo, pklist, retlist);
		}else{
			//1、已审核合同数据
			qryContractConData(paramvo, pklist, retlist);
			//2、待审核合同数据
			qryContractData(paramvo, pklist, retlist);
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
	 * 查询已审核合同数据
	 * @param paramvo
	 * @param pklist
	 * @param retlist
	 */
	private void qryContractConData(QryParamVO paramvo, List<String> pklist, List<ContractConfrimVO> retlist) throws DZFWarpException {
		QrySqlSpmVO confqryvo = getConfrimQry(paramvo);
		ContractConfrimVO[] confVOs = (ContractConfrimVO[]) singleObjectBO.queryByCondition(ContractConfrimVO.class,
				confqryvo.getSql(), confqryvo.getSpm());
		if(confVOs != null && confVOs.length > 0){
			UserVO uservo = null;
			CorpVO corpvo = null;
			for(ContractConfrimVO confvo : confVOs){
				if(confvo.getDenddate().compareTo(new DZFDate()) < 0){
					confvo.setVdeductstatus(-2);//服务到期
				}
				uservo = UserCache.getInstance().get(confvo.getVoperator(), null);
				if(uservo != null){
					confvo.setVopername(uservo.getUser_name());
				}
				corpvo = CorpCache.getInstance().get(null, confvo.getPk_corp());
				if (corpvo != null) {
					confvo.setVarea(corpvo.getCitycounty());
				}
				retlist.add(confvo);
				pklist.add(confvo.getPk_contract()+""+confvo.getVcontcode());
			}
		}
	}
	
	/**
	 * 查询待审核合同数据
	 * @param paramvo
	 * @param pklist
	 * @param retlist
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void qryContractData(QryParamVO paramvo, List<String> pklist, List<ContractConfrimVO> retlist) throws DZFWarpException {
		QrySqlSpmVO contqryvo = getContractQry(paramvo);
		List<ContractConfrimVO> conflist = (List<ContractConfrimVO>) singleObjectBO.executeQuery(contqryvo.getSql(),
				contqryvo.getSpm(), new BeanListProcessor(ContractConfrimVO.class));
		if (conflist != null && conflist.size() > 0) {
			CorpVO corpvo = null;
			Map<String, String> packmap = queryPackageMap();
			Integer cyclenum = 0;
			for (ContractConfrimVO vo : conflist) {
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if (corpvo != null) {
					vo.setCorpname(corpvo.getUnitname());
					vo.setVarea(corpvo.getCitycounty());
				}
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
				if (corpvo != null) {
					vo.setCorpkname(corpvo.getUnitname());
				}
				//从套餐取纳税人性质
				if(!StringUtil.isEmpty(vo.getPk_packagedef())){
					if(packmap != null && !packmap.isEmpty()){
						vo.setChargedeptname(packmap.get(vo.getPk_packagedef()));
					}
				}
				cyclenum = 0;
				if(vo.getIcyclenum() != null && vo.getIcyclenum() != 0){
					cyclenum = vo.getIcyclenum() * (Integer.parseInt(vo.getVchargecycle()));
					vo.setVchargecycle(String.valueOf(cyclenum));//收款周期
				}else{
					vo.setVchargecycle(null);//收款周期
				}
				if(!pklist.contains(vo.getPk_contract()+""+vo.getVcontcode())){
					if(!StringUtil.isEmpty(paramvo.getCorpname())){
						if(vo.getCorpname().indexOf(paramvo.getCorpname()) != -1){
							retlist.add(vo);
						}
					}else{
						retlist.add(vo);
					}
				}
			}
		}
	}
	
	/**
	 * 查询套餐属性
	 * @return
	 */
	private Map<String, String> queryPackageMap(){
		Map<String, String> map = new HashMap<String, String>();
		String sql = " nvl(dr,0) = 0 ";
		PackageDefVO[] packVOs = (PackageDefVO[]) singleObjectBO.queryByCondition(PackageDefVO.class, sql, null);
		if(packVOs != null && packVOs.length > 0){
			for(PackageDefVO vo : packVOs){
				map.put(vo.getPk_packagedef(), vo.getVtaxpayertype());
			}
		}
		return map;
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
		if(paramvo.getVdeductstatus() != null && paramvo.getVdeductstatus() != -1){
			if(paramvo.getVdeductstatus() != null && paramvo.getVdeductstatus() == -2){
				sql.append(" AND vdeductstatus in (1, 9, 10) \n") ;
				sql.append(" AND vendperiod < ? ");
				DZFDate date = new DZFDate();
				spm.addParam(date.getYear()+"-"+date.getStrMonth());
			}else{
				sql.append(" AND vdeductstatus = ? \n") ;
				spm.addParam(paramvo.getVdeductstatus());
			}
		}else{
			sql.append(" AND vdeductstatus in (1, 9, 10) \n") ;
		}
		if(paramvo.getBegdate() != null){
			sql.append("   AND dsubmitime >= ? \n") ; 
			spm.addParam(paramvo.getBegdate() + " 00:00:00");
		}
		if(paramvo.getEnddate() != null){
			sql.append("   AND dsubmitime <= ? \n") ; 
			spm.addParam(paramvo.getEnddate() + " 23:59:59");
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
		    String[] strs = paramvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND pk_corp in (").append(inSql).append(")");
		}
		if(paramvo.getIsncust() != null){
			sql.append(" AND nvl(isncust,'N') =? \n") ; 
			spm.addParam(paramvo.getIsncust());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corpk())){
			sql.append(" AND pk_corpk=? \n") ; 
			spm.addParam(paramvo.getPk_corpk());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_bill())){
			sql.append("   AND pk_confrim = ? \n") ; //合同主键
			spm.addParam(paramvo.getPk_bill());
		}
		if(!StringUtil.isEmpty(paramvo.getCorpname())){
			sql.append(" AND corpname like ? \n") ; 
			spm.addParam("%"+paramvo.getCorpname()+"%");
		}
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){
			sql.append(" AND nvl(patchstatus, 0) != 2 \n") ;
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){
			sql.append(" AND nvl(patchstatus, 0) = 2 \n") ;
		}
		sql.append(" order by dsubmitime desc");
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
//		sql.append("   AND con.vdeductstatus = 1 \n");//加盟商合同状态 = 待审核
		sql.append("   AND con.icontracttype = 2 \n");//合同类型 = 加盟商合同
//		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){//标签查询 1：待审核
//			if(paramvo.getQrytype() != 0 && paramvo.getQrytype() == 1){
//				sql.append("   AND vdeductstatus = 5 \n");
//			}else if(paramvo.getQrytype() != 0 && paramvo.getQrytype() == 2){//标签查询 2：存量待审
//				sql.append("   AND vdeductstatus = 5 AND nvl(isncust,'N') = 'Y' \n");
//			}
//		}else{
//	
//		}
		sql.append("   AND con.vdeductstatus = 5 \n");//加盟商合同状态 = 待审核
		if(paramvo.getBegdate() != null){
			sql.append("   AND con.dsubmitime >= ? \n") ; 
			spm.addParam(paramvo.getBegdate() + " 00:00:00");
		}
		if(paramvo.getEnddate() != null){
			sql.append("   AND con.dsubmitime <= ? \n") ; 
			spm.addParam(paramvo.getEnddate() + " 23:59:59");
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
		    String[] strs = paramvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND con.pk_corp in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corpk())){
			sql.append(" AND con.pk_corpk=? \n") ; 
			spm.addParam(paramvo.getPk_corpk());
		}
		if(paramvo.getIsncust()!=null){
			sql.append(" AND nvl(isncust,'N') =? \n") ; 
			spm.addParam(paramvo.getIsncust());
		}
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){
			sql.append(" AND nvl(patchstatus, 0) != 2 \n") ;
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){
			sql.append(" AND nvl(patchstatus, 0) = 2 \n") ;
		}
		sql.append(" order by con.dsubmitime desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
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
			ContractConfrimVO vo = conflist.get(0);
			CorpVO corpvo = null;
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			if (corpvo != null) {
				vo.setCorpname(corpvo.getUnitname());
			}
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
			if (corpvo != null) {
				vo.setCorpkname(corpvo.getUnitname());
			}
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			if(corpvo != null){
				vo.setVarea(corpvo.getCitycounty());
			}
			Map<String, String> packmap = queryPackageMap();
			//从套餐取纳税人性质
			if(!StringUtil.isEmpty(vo.getPk_packagedef())){
				if(packmap != null && !packmap.isEmpty()){
					vo.setChargedeptname(packmap.get(vo.getPk_packagedef()));
				}
			}
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
			if(packvo.getIspromotion() != null && packvo.getIspromotion().booleanValue()){
				Integer publishnum = packvo.getIpublishnum() == null ? 0 : packvo.getIpublishnum();
				Integer usenum = packvo.getIusenum() == null ? 0 : packvo.getIusenum();
				int num = publishnum - usenum;
				String vmome = packvo.getVmemo() == null ? "" :packvo.getVmemo();
				retvo.setVsalespromot("促销活动： "+ vmome + "    剩余名额" + num + "个");
			}
		}
		return retvo;
	}

	@Override
	public ContractConfrimVO updateDeductData(ContractConfrimVO paramvo, Integer opertype, String cuserid) throws DZFWarpException {
		try {
			LockUtil.getInstance().tryLockKey(paramvo.getTableName(), paramvo.getPk_contract(), 120);
			String errmsg = "";
			if(IStatusConstant.IDEDUCTYPE_1 == opertype){//扣款
				//审核前校验
				errmsg = CheckBeforeAudit(paramvo);
				if(!StringUtil.isEmpty(errmsg)){
					throw new BusinessException(errmsg);
				}
				//1、更新合同加盟合同状态、驳回原因
				updateContract(paramvo, opertype);
				//3、生成合同审核数据
				paramvo = saveContConfrim(paramvo, cuserid);
				//扣款比例如果为0，则不回写余额
				if(paramvo.getIdeductpropor() != 0){
					//2、回写付款余额
					updateBalanceMny(paramvo, cuserid);
				}
				//4、回写套餐促销活动名额
				updateSerPackage(paramvo);
				//5、回写客户纳税人性质
				Map<String, String> packmap = queryPackageMap();
				updateCorp(paramvo, packmap);
			}else if(IStatusConstant.IDEDUCTYPE_2 == opertype){//驳回
				errmsg = checkBeforeReject(paramvo);
				if(!StringUtil.isEmpty(errmsg)){
					throw new BusinessException(errmsg);
				}
				updateContract(paramvo, opertype);
				paramvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_7);//已驳回
				setNullValue(paramvo);
			}
		} finally {
			LockUtil.getInstance().unLock_Key(paramvo.getTableName(), paramvo.getPk_contract());
		}
		
		return paramvo;
	}
	
	/**
	 * 更新我的客户“纳税人性质”
	 * @param paramvo
	 * @param packmap
	 * @throws DZFWarpException
	 */
	private void updateCorp(ContractConfrimVO confvo, Map<String, String> packmap) throws DZFWarpException{
		//当为补单或客户档案的纳税人性质为空时，才更新客户档案纳税人性质
		CorpVO corpvo = CorpCache.getInstance().get(null, confvo.getPk_corpk());
		if(confvo.getPatchstatus() != null && confvo.getPatchstatus() == 2){//补单合同
			if(corpvo != null){
				corpvo.setChargedeptname(confvo.getChargedeptname());
				singleObjectBO.update(corpvo, new String[]{"chargedeptname"});
			}
		}else{
			if(corpvo != null && StringUtil.isEmpty(corpvo.getChargedeptname())){
				//从套餐取纳税人性质
				if(!StringUtil.isEmpty(confvo.getPk_packagedef())){
					if(packmap != null && !packmap.isEmpty()){
						if(!StringUtil.isEmpty(packmap.get(confvo.getPk_packagedef()))){
							corpvo.setChargedeptname(packmap.get(confvo.getPk_packagedef()));
							singleObjectBO.update(corpvo, new String[]{"chargedeptname"});
						}
					}
				}
			}
		}
	}
	
	/**
	 * 置空值
	 * @param paramvo
	 */
	private void setNullValue(ContractConfrimVO paramvo) throws DZFWarpException{
		paramvo.setDeductdata(null);
		paramvo.setIdeductpropor(null);
		paramvo.setNdeductmny(null);
		paramvo.setVoperator(null);
		paramvo.setVopername(null);
	}

	/**
	 * 更新套餐使用个数
	 * @param paramvo
	 */
	private void updateSerPackage(ContractConfrimVO paramvo) throws DZFWarpException{
		if(paramvo.getPatchstatus() != null && paramvo.getPatchstatus() != 2){
			PackageDefVO packvo = (PackageDefVO) singleObjectBO.queryByPrimaryKey(PackageDefVO.class, paramvo.getPk_packagedef());
			try {
				if(packvo != null){
					LockUtil.getInstance().tryLockKey(packvo.getTableName(), packvo.getPk_packagedef(), 60);
					Integer num = packvo.getIusenum() == null ? 0 : packvo.getIusenum();
					Integer pulishnum = packvo.getIpublishnum() == null ? 0 : packvo.getIpublishnum();
					if(packvo.getIspromotion() != null && packvo.getIspromotion().booleanValue()){
						if(num.compareTo(pulishnum) == 0){
							throw new BusinessException("套餐发布个数已经用完");
						}
					}
					packvo.setIusenum(num + 1);
					singleObjectBO.update(packvo, new String[]{"iusenum"});
				}
			} finally {
				LockUtil.getInstance().unLock_Key(packvo.getTableName(), packvo.getPk_packagedef());
			}
		}
	}
	
	/**
	 * 保存合同审核信息
	 * @param paramvo
	 * @return
	 */
	private ContractConfrimVO saveContConfrim(ContractConfrimVO paramvo, String cuserid) throws DZFWarpException{
		paramvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_1);
		paramvo.setTstamp(new DZFDateTime());
		paramvo.setDeductdata(new DZFDate());
		paramvo.setDeductime(new DZFDateTime());
		paramvo.setCoperatorid(cuserid);
		paramvo.setDr(0);
		UserVO uservo = UserCache.getInstance().get(paramvo.getVoperator(), null);
		if(uservo != null){
			paramvo.setVopername(uservo.getUser_name());
		}
		return (ContractConfrimVO) singleObjectBO.saveObject("000001", paramvo);
	}
	/**
	 * 更新合同信息
	 * @param contvo
	 * @param paramvo
	 * @throws DZFWarpException
	 */
	private void updateContract(ContractConfrimVO paramvo, Integer opertype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" UPDATE ynt_contract set vdeductstatus = ? , vconfreason = ? ,");
		sql.append(" tstamp = ? WHERE nvl(dr,0) = 0 AND pk_corp = ? AND pk_contract = ? ");
		if(IStatusConstant.IDEDUCTYPE_1 == opertype){//扣款
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		}else if(IStatusConstant.IDEDUCTYPE_2 == opertype){//驳回
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_7);
		}
		if(StringUtil.isEmpty(paramvo.getVconfreason())){
			spm.addParam("");
		}else{
			spm.addParam(paramvo.getVconfreason());
		}
		spm.addParam(new DZFDateTime());
		spm.addParam(paramvo.getPk_corp());
		spm.addParam(paramvo.getPk_contract());
		singleObjectBO.executeUpdate(sql.toString(), spm);
	}
	
	/**
	 * 合同审核前状态校验
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private String checkBeforeReject(ContractConfrimVO confrimvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT tstamp,vdeductstatus \n") ;
		sql.append("  FROM ynt_contract t \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n") ; 
		sql.append("   AND t.pk_contract = ? ");
		spm.addParam(confrimvo.getPk_contract());
		ArrayList<Object> result = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm, new ArrayListProcessor());
		if(result != null && result.size() > 0){
			Object[] obj = (Object[]) result.get(0); 
			DZFDateTime tstamp = new DZFDateTime(String.valueOf(obj[0]));
			if(tstamp != null && tstamp.compareTo(confrimvo.getTstamp()) != 0){
				return "合同号："+confrimvo.getVcontcode()+"数据发生变化，请刷新界面后，再次尝试";
			}
			if(IStatusConstant.IDEDUCTSTATUS_5 != Integer.parseInt(String.valueOf(obj[1]))){
				return "合同状态不为待审核";
			}
		}else{
			return "合同数据错误";
		}
		return "";
	}
	
	/**
	 * 更新余额信息
	 * @param paramvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updateBalanceMny(ContractConfrimVO paramvo, String cuserid) throws DZFWarpException {
		ChnBalanceVO balvo = null;
		String yesql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype = ? ";
		SQLParameter yespm = new SQLParameter();
		yespm.addParam(paramvo.getPk_corp());
		yespm.addParam(IStatusConstant.IPAYTYPE_2);
		ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, yesql, yespm);
		if(balVOs != null && balVOs.length > 0){
			balvo = balVOs[0];
			DZFDouble balance = SafeCompute.sub(balvo.getNpaymny(), balvo.getNusedmny());
			if(balance.compareTo(paramvo.getNdeductmny()) < 0){
				throw new BusinessException("预付款余额不足");
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
			detvo.setNtotalmny(paramvo.getNtotalmny());//合同金额
			detvo.setIdeductpropor(paramvo.getIdeductpropor());//扣款比例
			singleObjectBO.saveObject("000001", detvo);
			
		}else{
			throw new BusinessException("预付款余额不足");
		}
	}

	@Override
	public List<ContractConfrimVO> bathconfrim(ContractConfrimVO[] confrimVOs, ContractConfrimVO paramvo,
			Integer opertype, String cuserid )
			throws DZFWarpException {
		List<ContractConfrimVO> retlist = new ArrayList<ContractConfrimVO>();
		ContractConfrimVO retvo = null;
		Map<String, String> packmap = queryPackageMap();
		for (ContractConfrimVO vo : confrimVOs) {
			retvo = updateBathDeductData(vo, paramvo, opertype, cuserid, packmap);
			retlist.add(retvo);
		}
		return retlist;
	}
	
	/**
	 * 批量审核-审核单个数据
	 * @param confrimvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ContractConfrimVO updateBathDeductData(ContractConfrimVO confrimvo, ContractConfrimVO paramvo,
			Integer opertype, String cuserid, Map<String, String> packmap) throws DZFWarpException {
		try {
			LockUtil.getInstance().tryLockKey(confrimvo.getTableName(), confrimvo.getPk_contract(), 120);
			String errmsg = "";
			if(IStatusConstant.IDEDUCTYPE_1 == opertype){//扣款
				if(paramvo != null){
					countDedMny(confrimvo, paramvo);
				}else{
					throw new BusinessException("审核信息获取错误");
				}
				errmsg = CheckBeforeAudit(confrimvo);
				if(!StringUtil.isEmpty(errmsg)){
					confrimvo.setVerrmsg(errmsg);
					setNullValue(confrimvo);
					return confrimvo;
				}
				//1、更新合同加盟合同状态、驳回原因
				updateContract(confrimvo, opertype);
				//3、生成合同审核数据
				confrimvo = saveContConfrim(confrimvo, cuserid);
				//扣款比例如果为0，则不回写余额
				if(paramvo.getIdeductpropor() != 0){
					//2、回写付款余额
					updateBalanceMny(confrimvo, cuserid);
				}
				//4、回写套餐促销活动名额
				updateSerPackage(confrimvo);
				//5、回写客户纳税人性质
				updateCorp(paramvo, packmap);
			}else if(IStatusConstant.IDEDUCTYPE_2 == opertype){//驳回
				errmsg = checkBeforeReject(confrimvo);
				if(!StringUtil.isEmpty(errmsg)){
					confrimvo.setVerrmsg(errmsg);
					setNullValue(confrimvo);
					return confrimvo;
				}
				confrimvo.setVconfreason(paramvo.getVconfreason());
				updateContract(confrimvo, opertype);
				confrimvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_7);//已驳回
				setNullValue(confrimvo);
			}
		} finally {
			LockUtil.getInstance().unLock_Key(confrimvo.getTableName(), confrimvo.getPk_contract());
		}

		return confrimvo;
	}
	
	@SuppressWarnings("unchecked")
	private String CheckBeforeAudit(ContractConfrimVO confrimvo) throws DZFWarpException{
		// 1、数据状态校验
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT tstamp,vdeductstatus \n");
		sql.append("  FROM ynt_contract t \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND t.pk_contract = ? ");
		spm.addParam(confrimvo.getPk_contract());
		ArrayList<Object> result = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm,
				new ArrayListProcessor());
		if (result != null && result.size() > 0) {
			Object[] obj = (Object[]) result.get(0);
			DZFDateTime tstamp = new DZFDateTime(String.valueOf(obj[0]));
			if (tstamp != null && tstamp.compareTo(confrimvo.getTstamp()) != 0) {
				return "合同号：" + confrimvo.getVcontcode() + "数据发生变化，请刷新界面后，再次尝试";
			}
			if (IStatusConstant.IDEDUCTSTATUS_5 != Integer.parseInt(String.valueOf(obj[1]))) {
				return "合同状态不为待审核";
			}
		} else {
			return "合同数据错误";
		}

		// 2、预付款余额校验（扣款比例不为0的情况进行校验）
		if(confrimvo.getIdeductpropor() != 0){
			ChnBalanceVO balvo = null;
			String yesql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype = ? ";
			SQLParameter yespm = new SQLParameter();
			yespm.addParam(confrimvo.getPk_corp());
			yespm.addParam(IStatusConstant.IPAYTYPE_2);
			ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, yesql, yespm);
			if (balVOs != null && balVOs.length > 0) {
				balvo = balVOs[0];
				DZFDouble balance = SafeCompute.sub(balvo.getNpaymny(), balvo.getNusedmny());
				if (balance.compareTo(confrimvo.getNdeductmny()) < 0) {
					return "预付款余额不足";
				}
			} else {
				return "预付款余额不足";
			}
		}

		// 3、套餐发布个数校验
		PackageDefVO packvo = (PackageDefVO) singleObjectBO.queryByPrimaryKey(PackageDefVO.class,
				confrimvo.getPk_packagedef());
		if(packvo.getIspromotion() != null && packvo.getIspromotion().booleanValue()){
			Integer num = packvo.getIusenum() == null ? 0 : packvo.getIusenum();
			Integer pulishnum = packvo.getIpublishnum() == null ? 0 : packvo.getIpublishnum();
			if (num.compareTo(pulishnum) == 0) {
				return "套餐发布个数已经用完";
			}
		}

		return "";
	}
	
	/**
	 * 计算扣款金额
	 * @param confrimvo
	 * @param paramvo
	 */
	private void countDedMny(ContractConfrimVO confrimvo, ContractConfrimVO paramvo){
		confrimvo.setIdeductpropor(paramvo.getIdeductpropor());//扣款比例
		confrimvo.setVoperator(paramvo.getVoperator());//经办人
		confrimvo.setVconfreason(paramvo.getVconfreason());//驳回原因
		DZFDouble countmny = SafeCompute.sub(confrimvo.getNtotalmny(), confrimvo.getNbookmny());
		DZFDouble ndeductmny = countmny.multiply(confrimvo.getIdeductpropor()).div(100);
		confrimvo.setNdeductmny(ndeductmny);
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
		condition.append(" nvl(dr,0) = 0 and pk_corp = ?");
		SQLParameter params = new SQLParameter();
		params.addParam(qvo.getPk_corp());
		if (!StringUtil.isEmpty(qvo.getPk_contract())) {
			condition.append(" and  pk_contract = ? ");
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

	@Override
	public ContractConfrimVO saveChange(ContractConfrimVO paramvo, String cuserid, File[] files, String[] filenames)
			throws DZFWarpException {
		checkBeforeChange(paramvo);
		if (paramvo.getIchangetype() == IStatusConstant.ICONCHANGETYPE_1) {
			paramvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_9);
			paramvo.setIchangetype(IStatusConstant.ICONCHANGETYPE_1);
			paramvo.setVchangeraeson("C端客户终止，变更合同");
		} else if (paramvo.getIchangetype() == IStatusConstant.ICONCHANGETYPE_2) {
			paramvo.setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_10);
			paramvo.setIchangetype(IStatusConstant.ICONCHANGETYPE_2);
			paramvo.setVchangeraeson("提重了，合同作废");
		}
		paramvo.setPatchstatus(3);//加盟合同类型（null正常合同；1被补提交的合同；2补提交的合同；3变更合同）
		paramvo.setVchanger(cuserid);
		paramvo.setDchangetime(new DZFDateTime());
		// 1、更新合同历史数据
		singleObjectBO.update(paramvo, new String[] { "vdeductstatus", "ichangetype", "vchangeraeson", "vchangememo",
				"vstopperiod", "nreturnmny", "nchangetotalmny", "nchangededutmny","vchanger","dchangetime","patchstatus" });
		// 2、更新原合同数据
		String sql = " update ynt_contract set vdeductstatus = ?, patchstatus = 3 where nvl(dr,0) = 0 and pk_corp = ? and pk_contract = ? ";
		SQLParameter spm = new SQLParameter();
		if (paramvo.getIchangetype() == IStatusConstant.ICONCHANGETYPE_1) {
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		}else if(paramvo.getIchangetype() == IStatusConstant.ICONCHANGETYPE_2){
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		spm.addParam(paramvo.getPk_corp());
		spm.addParam(paramvo.getPk_contract());
		singleObjectBO.executeUpdate(sql, spm);
		// 3、上传变更合同附件
		saveContDocVO(paramvo, files, filenames, cuserid);
		//4、更新合同变更后余额及余额明细表
		String vmemo = "";
		if (paramvo.getIchangetype() == IStatusConstant.ICONCHANGETYPE_1) {
			vmemo = "合同变更：C端客户终止，变更合同";
		}else if(paramvo.getIchangetype() == IStatusConstant.ICONCHANGETYPE_2){
			vmemo = "合同变更：提重了，合同作废";
		}
		updateChangeBalMny(paramvo, cuserid, vmemo);
		CorpVO corpvo = CorpCache.getInstance().get(null, paramvo.getPk_corpk());
		if(corpvo != null){
			paramvo.setCorpkname(corpvo.getUnitname());
		}
		return paramvo;
	}
	
	/**
	 * 合同变更前校验
	 * @param paramvo
	 * @throws DZFWarpException
	 */
	private void checkBeforeChange(ContractConfrimVO paramvo) throws DZFWarpException {
		ContractConfrimVO oldvo = (ContractConfrimVO) singleObjectBO.queryByPrimaryKey(ContractConfrimVO.class,
				paramvo.getPk_confrim());
		if(oldvo == null){
			throw new BusinessException("变更合同信息错误");
		}
		if(oldvo.getVdeductstatus() != null && oldvo.getVdeductstatus() != IStatusConstant.IDEDUCTSTATUS_1){
			throw new BusinessException("合同状态不为审核通过");
		}
		if(oldvo.getPatchstatus() != null && oldvo.getPatchstatus() == 1){
			throw new BusinessException("该合同已被补提单，不允许变更");
		}
		if(oldvo.getPatchstatus() != null && oldvo.getPatchstatus() == 2){
			throw new BusinessException("该合同为补提单，不允许变更");
		}
		if(oldvo.getPatchstatus() != null && oldvo.getPatchstatus() == 3){
			throw new BusinessException("该合同已变更，不允许再次变更");
		}
	}
	
	/**
	 * 上传变更合同附件
	 * @param vo
	 * @param files
	 * @param filenames
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void saveContDocVO(ContractConfrimVO vo, File[] files, String[] filenames, String cuserid)
			throws DZFWarpException {
		CorpVO corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
		if (corpvo == null) {
			throw new BusinessException("会计公司信息错误");
		}
		if (files != null && files.length > 0) {
			String uploadPath = ImageCommonPath.getContractFilePath(corpvo.getInnercode(), vo.getPk_contract(), null);
			ArrayList<ContractDocVO> list = new ArrayList<>();
			ContractDocVO docvo = null;
			for (int i = 0; i < files.length; i++) {
				String fname = System.nanoTime() + filenames[i].substring(filenames[i].indexOf("."));
				String filepath = uploadPath + File.separator + fname;
				docvo = new ContractDocVO();
				docvo.setPk_corp(vo.getPk_corp());
				docvo.setPk_contract(vo.getPk_contract());
				docvo.setCoperatorid(cuserid);
				docvo.setDoperatedate(new DZFDate());
				docvo.setDocOwner(cuserid);
				docvo.setDocTime(new DZFDateTime());
				docvo.setDocName(filenames[i]);
				docvo.setDocTemp(fname);
				docvo.setVfilepath(filepath);
				docvo.setDr(0);
				docvo.setIdoctype(3);
				list.add(docvo);
				InputStream is = null;
				OutputStream os = null;
				try {
					File ff = new File(filepath);
					if (!ff.getParentFile().exists()) {
						ff.getParentFile().mkdirs();
					}
					is = new FileInputStream(files[i]);
					os = new FileOutputStream(filepath);
					IOUtils.copy(is, os);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
						}
					}
					if (os != null) {
						try {
							os.close();
						} catch (IOException e) {
						}
					}
				}
			}
			singleObjectBO.insertVOArr(vo.getPk_corp(), list.toArray(new ContractDocVO[0]));
		}
	}
	
	/**
	 * 更新合同变更后余额信息
	 * @param paramvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	private void updateChangeBalMny(ContractConfrimVO paramvo, String cuserid, String vmemo) throws DZFWarpException {
		ChnBalanceVO balvo = null;
		String yesql = " nvl(dr,0) = 0 and pk_corp = ? and ipaytype = ? ";
		SQLParameter yespm = new SQLParameter();
		yespm.addParam(paramvo.getPk_corp());
		yespm.addParam(IStatusConstant.IPAYTYPE_2);
		ChnBalanceVO[] balVOs = (ChnBalanceVO[]) singleObjectBO.queryByCondition(ChnBalanceVO.class, yesql, yespm);
		if(balVOs != null && balVOs.length > 0){
			balvo = balVOs[0];
			//余额增加退回扣款
			balvo.setNusedmny(SafeCompute.sub(balvo.getNusedmny(), paramvo.getNreturnmny()));
			singleObjectBO.update(balvo, new String[]{"nusedmny"});
			
			ChnDetailVO detvo = new ChnDetailVO();
			detvo.setPk_corp(paramvo.getPk_corp());
			//退回扣款 显示负值
			detvo.setNusedmny(SafeCompute.multiply(paramvo.getNreturnmny(), new DZFDouble(-1)));
			detvo.setIpaytype(IStatusConstant.IPAYTYPE_2);
			detvo.setPk_bill(paramvo.getPk_confrim());
			detvo.setVmemo(vmemo);
			detvo.setCoperatorid(cuserid);
			detvo.setDoperatedate(new DZFDate());
			detvo.setDr(0);
			detvo.setIopertype(IStatusConstant.IDETAILTYPE_2);
			detvo.setNtotalmny(paramvo.getNchangetotalmny());//合同金额
			detvo.setIdeductpropor(paramvo.getIdeductpropor());//扣款比例
			singleObjectBO.saveObject("000001", detvo);
			
		}else{
			throw new BusinessException("预付款余额不足");
		}
	}
}
