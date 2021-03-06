package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
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
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.IChnPayAuditService;
import com.dzf.service.channel.IChnPayConfService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("payauditser")
public class ChnPayAuditServiceImpl implements IChnPayAuditService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IChnPayConfService payconfSer;
	
	@Autowired
	private IPubService pubser;
	@Autowired
	private IUserService userServiceImpl;

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
		Map<String, UserVO> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		if(list != null && list.size() > 0){
			CorpVO accvo = null;
			UserVO uservo = null;
			Map<Integer, String> areamap = pubser.getAreaMap(paramvo.getAreaname(), 3);//渠道运营区域设置
			QueryDeCodeUtils.decKeyUtils(new String[]{"vapprovename"}, list, 1);
			for(ChnPayBillVO vo : list){
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				uservo = marmap.get(vo.getPk_corp());
				if(accvo != null){
					vo.setCorpname(accvo.getUnitname());
				}
				if (uservo != null) {
					vo.setVmanagername(uservo.getUser_name());// 渠道经理
				}
				if(areamap != null && !areamap.isEmpty()){
					String area = areamap.get(vo.getVprovince());
					if(!StringUtil.isEmpty(area)){
						vo.setAreaname(area);//渠道运营区域
					}
				}
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
		sql.append("SELECT t.*,account.vprovince,us.user_name as vapprovename FROM cn_paybill t   ");
		sql.append("  LEFT JOIN bd_account account ON t.pk_corp = account.pk_corp   ") ;
		sql.append(" left join sm_user us on us.cuserid = t.vapproveid");
		sql.append(" WHERE nvl(t.dr,0) = 0   ");
		sql.append("   AND nvl(account.dr, 0) = 0    ") ; 
		sql.append("   AND account.ischannel = 'Y'   ");
		sql.append("   AND account.isaccountcorp = 'Y'   ");
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){//查询状态
			sql.append(" AND t.vstatus = ?   ");
			spm.addParam(paramvo.getQrytype());
		}else{
			sql.append(" AND t.vstatus in ( ?, ?, ?, ?)   ");
			spm.addParam(IStatusConstant.IPAYSTATUS_2);
			spm.addParam(IStatusConstant.IPAYSTATUS_3);
			spm.addParam(IStatusConstant.IPAYSTATUS_4);
			spm.addParam(IStatusConstant.IPAYSTATUS_5);
		}
		if(paramvo.getIpaytype() != null && paramvo.getIpaytype() != -1){
		    sql.append(" AND t.ipaytype = ?   ");
            spm.addParam(paramvo.getIpaytype());
		}
		if(paramvo.getIpaymode() != null && paramvo.getIpaymode() != -1){
            sql.append(" AND t.ipaymode = ?   ");
            spm.addParam(paramvo.getIpaymode());
        }
		if(paramvo.getBegdate() != null && paramvo.getEnddate() != null){
		    sql.append(" AND (t.dpaydate >= ? AND t.dpaydate <= ? )  ");
            spm.addParam(paramvo.getBegdate());
            spm.addParam(paramvo.getEnddate());
		}
		//加盟商过滤
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
		    String[] strs = paramvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND t.pk_corp in (").append(inSql).append(")");
		}else{
			if(paramvo.getSeletype() != null && paramvo.getSeletype() != -1){
				sql.append(" AND account.channeltype = ?   ");
				spm.addParam(paramvo.getSeletype());
			}else{
				sql.append(" AND account.channeltype != 9   ");
			}
		}
		if(!StringUtil.isEmpty(paramvo.getPk_bill())){
			sql.append(" AND t.pk_paybill = ?   ");
            spm.addParam(paramvo.getPk_bill());
		}
		if(!StringUtil.isEmpty(paramvo.getVqrysql())){
			sql.append(paramvo.getVqrysql());
		}
		sql.append(" order by t.dpaydate desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public ChnPayBillVO updateOperate(ChnPayBillVO billvo, Integer opertype, String cuserid, String vreason)
			throws DZFWarpException {
		payconfSer.checkBillStatus(billvo);
		return updateData(billvo, opertype, cuserid, vreason);
	}
	
	/**
	 * 审批驳回、取消审批、
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 */
	private ChnPayBillVO updateData(ChnPayBillVO billvo, Integer opertype, String cuserid,String vreason){
		if(opertype == IStatusConstant.ICHNOPRATETYPE_10){//收款审批
			return updateAuditData(billvo, opertype, cuserid);
		}else if(opertype == IStatusConstant.ICHNOPRATETYPE_9){//审批驳回
			return updateRejectData(billvo, vreason, cuserid);
		}else if(opertype == IStatusConstant.ICHNOPRATETYPE_2){//取消审批
			return updateReturnData(billvo, opertype, cuserid);
		} 
		return billvo;
	}
	
	/**
	 * 收款审批
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateAuditData(ChnPayBillVO billvo, Integer opertype, String cuserid) throws DZFWarpException{
		if(StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())){
			throw new BusinessException("单据号"+billvo.getVbillcode()+"数据错误");
		}
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 120);
			if(billvo.getVstatus()  != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_2){
				throw new BusinessException("单据号"+billvo.getVbillcode()+"状态不为【待审批】");
			}
			List<String> upstr = new ArrayList<String>();
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_5);//付款单状态 待确认
			upstr.add("vstatus");
			if(billvo.getIrejectype() != null && billvo.getIrejectype() == 1){//审批驳回
				billvo.setIrejectype(null);//驳回类型
				billvo.setVreason(null);//驳回原因
				upstr.add("irejectype");
				upstr.add("vreason");
			}
			billvo.setVapproveid(cuserid);//审批人
			billvo.setDapprovedate(new DZFDate());//审批日期
			billvo.setDapprovetime(new DZFDateTime());//审批时间
			billvo.setTstamp(new DZFDateTime());//操作时间
			upstr.add("vapproveid");
			upstr.add("dapprovedate");
			upstr.add("dapprovetime");
			upstr.add("tstamp");
			singleObjectBO.update(billvo, upstr.toArray(new String[0]));
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
		}
		UserVO uservo = userServiceImpl.queryUserJmVOByID(billvo.getVapproveid());
		if(uservo != null){
			billvo.setVapprovename(uservo.getUser_name());
		}
		return billvo;
	}
	
	/**
	 * 审批驳回
	 * @param billvo
	 * @param vreason
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	private ChnPayBillVO updateRejectData(ChnPayBillVO billvo, String vreason, String cuserid)
			throws DZFWarpException {
		if (StringUtil.isEmpty(billvo.getTableName()) || StringUtil.isEmpty(billvo.getPk_paybill())) {
			throw new BusinessException("单据号"+billvo.getVbillcode()+"数据错误");
		}
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 60);
			if(billvo.getVstatus()  != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_2){
				throw new BusinessException("单据号"+billvo.getVbillcode()+"状态不为【待审批】");
			}
			billvo.setIrejectype(1);//驳回类型：审批驳回
			billvo.setVapproveid(cuserid);//审批-驳回人
			billvo.setDapprovedate(new DZFDate());//审批-驳回日期
			billvo.setDapprovetime(new DZFDateTime());//审批-驳回时间
			billvo.setVreason(vreason);
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_4);//付款单状态  已驳回
			billvo.setTstamp(new DZFDateTime());//操作时间
			String[] str = new String[]{"irejectype","vapproveid","dapprovedate","dapprovetime",
					"vreason","vstatus","tstamp"};
			singleObjectBO.update(billvo, str);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(billvo.getTableName(), billvo.getPk_paybill(),uuid);
		}
		UserVO uservo = userServiceImpl.queryUserJmVOByID(billvo.getVapproveid());
		if(uservo != null){
			billvo.setVapprovename(uservo.getUser_name());
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
			throw new BusinessException("单据号"+billvo.getVbillcode()+"数据错误");
		}
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(billvo.getTableName(), billvo.getPk_paybill(),uuid, 120);
			if(billvo.getVstatus()  != null && billvo.getVstatus() != IStatusConstant.IPAYSTATUS_5){
				throw new BusinessException("单据号"+billvo.getVbillcode()+"状态不为【待确认】");
			}
			billvo.setVstatus(IStatusConstant.IPAYSTATUS_2);//付款单状态 待审批
			billvo.setVapproveid(null);//审批人
			billvo.setDapprovedate(null);//审批日期
			billvo.setDapprovetime(null);//审批时间
			billvo.setTstamp(new DZFDateTime());//操作时间
			singleObjectBO.update(billvo, new String[]{"vstatus","vapproveid", "dapprovedate", "dapprovetime","tstamp"});
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
	
}
