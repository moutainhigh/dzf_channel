package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.channel.contract.RejectHistoryHVO;
import com.dzf.model.demp.contract.ContractDocVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IContractConfirmQueryService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("contractconqueryser")
public class ContractConfirmQueryServiceImpl implements IContractConfirmQueryService{
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IPubService pubser;

	@Autowired
	private IUserService userSer;

	@Override
	public int queryTotalRow(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQryCondition(paramvo, "listqry", true);
		return multBodyObjectBO.getDataTotal(sqpvo.getSql(), sqpvo.getSpm());
	}

	/**
	 * 获取查询条件
	 * @param paramvo
	 * @param qrytype   listqry:列表查询；info：详情查询；
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQryCondition(QryParamVO paramvo, String qrytype, boolean isqrynum) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if(isqrynum){
			sql.append("SELECT count(t.pk_contract)    ") ;
		}else{
			sql.append("SELECT t.pk_contract,    ") ;
			sql.append("       t.pk_corp,    ") ; 
			sql.append("       t.pk_corpk,    ") ; 
			sql.append("       t.vdeductstatus,    ") ; 
			sql.append("       t.vstatus,    ") ; 
			sql.append("       t.iversion AS iapplystatus,    ") ; 
			sql.append("       CASE    ") ; 
			sql.append("       WHEN  t.vstatus = 5 OR t.vstatus = 7 THEN    ") ; 
			sql.append("             t.tstamp    ") ; 
			sql.append("       ELSE    ") ; 
			sql.append("             cn.tstamp    ") ; 
			sql.append("       END AS checkts,    ") ;//原合同、历史合同时间戳
			if("listqry".equals(qrytype)){
				sql.append("       account.vprovince,    ") ; //省份
			}
			sql.append("       account.citycounty AS varea,    ");
			sql.append("       account.unitname AS corpname,    ");
			sql.append("       account.drelievedate AS drelievedate,    ");
			sql.append("       t.vcontcode,    ") ; 
			sql.append("       t.pk_packagedef,    ") ; 
			sql.append("       nvl(t.isncust,'N') AS isncust,    ") ; // 是否存量客户
			sql.append("       nvl(t.isnconfirm,'N') AS isnconfirm,    ") ; // 未确定服务期限
			sql.append("       t.chargedeptname,    ") ; 
			sql.append("       t.busitypemin,    ") ;//业务小类
			sql.append("       bs.vbusitypename,    ") ; 
			sql.append("       t.ntotalmny,    ") ; 
			sql.append("       t.nbookmny,    ") ; 
			sql.append("       t.nmservicemny,    ") ; 
			sql.append("       t.icontractcycle,    ") ; 
			sql.append("       t.ireceivcycle,    ") ; 
			sql.append("       t.dsubmitime,    ") ; 
			sql.append("       t.vbeginperiod,    ") ; 
			sql.append("       t.vendperiod,    ") ; 
			sql.append("       t.dbegindate,    ") ; 
			sql.append("       t.denddate,    ") ; 
			sql.append("       t.vchangeperiod,    ") ; 
			//加盟商合同类型：null正常合同；1：被2补提交的原合同；2：小规模转一般人的合同；3：变更合同;4：被5补提交的原合同;5:一般人转小规模的合同
			sql.append("       t.patchstatus,    ") ; 
			sql.append("       t.vconfreason,    ") ; 
			sql.append("       t.dsigndate,    ") ; 
			sql.append("       t.vadviser,    ") ; 
			sql.append("       t.pk_source,    ") ; //来源合同主键
			sql.append("       t.ichargetype,    ") ;//扣费类型    1或null：新增扣费； 2：续费扣款；
			sql.append("       t.icompanytype,    ") ;// 公司类型 20-个体工商户，99-非个体户；
			sql.append("       CASE nvl(t.icompanytype,99) WHEN 20 THEN '个体工商户' WHEN 99 THEN '非个体户' END AS vcomptypename,    ");
			sql.append("       cn.pk_confrim,    ") ; 
			sql.append("       cn.tstamp,    ") ; 
			sql.append("       cn.deductdata,    ") ; 
			sql.append("       cn.deductime,    ") ; 
			sql.append("       cn.voperator,    ") ; 
			sql.append("       cn.ndedsummny,    ") ; 
			//sql.append("       cn.ndeductmny,    ") ; 
			//sql.append("       cn.ndedrebamny,    ") ; 
			sql.append("       cn.ideductpropor,    ") ;
			sql.append("       cn.ichangetype,    ") ; 
			sql.append("       cn.vchangeraeson,    ") ; 
			sql.append("       cn.vchangememo,    ") ; 
			sql.append("       cn.vstopperiod,    ") ; 
			sql.append("       cn.nreturnmny,    ") ; 
			sql.append("       cn.nretdedmny,    ") ; 
			sql.append("       cn.nretrebmny,    ") ; 
			sql.append("       t.nchangetotalmny,    ") ; //原合同金额(原合同)/变更后合同金额(历史合同-终止、作废)
			sql.append("       cn.nchangesummny,    ") ; 
			sql.append("       cn.nchangededutmny,    ") ; 
			sql.append("       cn.nchangerebatmny,    ") ; 
			sql.append("       cn.vchanger,    ") ; 
			sql.append("       cn.dchangetime,    ") ; 
			sql.append("       substr(dchangetime, 0, 10) AS dchangedate,    ") ; 
			sql.append("       cn.nsubtotalmny,    ") ; 
			sql.append("       cn.nsubdedsummny,    ") ; 
			sql.append("       cn.nsubdeductmny,    ") ; 
			sql.append("       cn.nsubdedrebamny    ") ; 
		}
		sql.append("  FROM ynt_contract t    ") ; 
		sql.append("  LEFT JOIN cn_contract cn ON t.pk_contract = cn.pk_contract    ") ; 
		sql.append("  LEFT JOIN ynt_busitype bs ON t.busitypemin = bs.pk_busitype    ") ; 
		sql.append(" INNER JOIN bd_account account ON t.pk_corp = account.pk_corp   ") ;
		sql.append(" WHERE nvl(t.dr, 0) = 0    ") ; 
		sql.append("   AND nvl(cn.dr, 0) = 0    ") ; 
		sql.append("   AND nvl(bs.dr, 0) = 0    ") ; 
		sql.append("   AND nvl(account.dr, 0) = 0    ") ; 
		sql.append("   AND account.ischannel = 'Y'   ");
		sql.append("   AND account.isaccountcorp = 'Y'   ");
		sql.append("   AND t.isflag = 'Y'    ") ; 
		sql.append("   AND nvl(t.icosttype, 0) = 0    ") ; 
		sql.append("   AND t.icontracttype = 2    ") ; //加盟商合同
		
		//合同状态过滤：1、未提交的合同不显示；2、待审核的合同，申请状态为待运营处理（过滤非常规套餐）；
		sql.append("   AND ((t.vstatus != 0 AND t.vstatus != 5) OR    ") ; 
		sql.append("        (t.vstatus = 5 AND nvl(t.iversion, 4) >= 4))    ") ; 
		if(paramvo.getVdeductstatus() != null && paramvo.getVdeductstatus() != -1){
			if(paramvo.getVdeductstatus() != null && paramvo.getVdeductstatus() == IStatusConstant.IDEDUCTSTATUS_8){
				sql.append(" AND t.vendperiod < ? ");
				DZFDate date = new DZFDate();
				spm.addParam(date.getYear()+"-"+date.getStrMonth());
			}else{
				sql.append(" AND t.vdeductstatus = ?   ") ;
				spm.addParam(paramvo.getVdeductstatus());
			}
		}
		if(paramvo.getBegdate() != null){
			sql.append("   AND t.dsubmitime >= ?   ") ; 
			spm.addParam(paramvo.getBegdate() + " 00:00:00");
		}
		if(paramvo.getEnddate() != null){
			sql.append("   AND t.dsubmitime <= ?   ") ; 
			spm.addParam(paramvo.getEnddate() + " 23:59:59");
		}
		if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
			sql.append("   AND cn.deductdata >= ?   ") ; 
			spm.addParam(paramvo.getBeginperiod());
		}
		if(!StringUtil.isEmpty(paramvo.getEndperiod())){
			sql.append("   AND cn.deductdata <= ?   ") ; 
			spm.addParam(paramvo.getEndperiod());
		}
		if(!StringUtil.isEmpty(paramvo.getStartdate())){//到期月份
			sql.append("   AND t.vendperiod >= ?   ") ; 
			spm.addParam(paramvo.getStartdate());
		}
		if(!StringUtil.isEmpty(paramvo.getOverdate())){
			sql.append("   AND t.vendperiod <= ?   ") ; 
			spm.addParam(paramvo.getOverdate());
		}
		if(paramvo.getIsncust() != null){
			sql.append(" AND nvl(t.isncust,'N') = ?   ") ; 
			spm.addParam(paramvo.getIsncust());
		}
		if("info".equals(qrytype) /*|| "audit".equals(qrytype) */){
			if(!StringUtil.isEmpty(paramvo.getPk_bill())){
				sql.append("   AND t.pk_contract = ?   ") ; //原合同合同主键
				spm.addParam(paramvo.getPk_bill());
			}
		}else{
			if(!StringUtil.isEmpty(paramvo.getPk_bill())){
				sql.append("   AND cn.pk_confrim = ?   ") ; //历史合同主键
				spm.addParam(paramvo.getPk_bill());
			}
		}
		if(!StringUtil.isEmpty(paramvo.getPk_contract())){
			sql.append("   AND t.pk_contract = ?   ") ;//原合同合同主键
			spm.addParam(paramvo.getPk_contract());
		}
		sql.append(" AND account.channeltype != 9   ");
		//qrytype 1：正常提单；2：纳税人变更；3：待变更；
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			if(paramvo.getQrytype() == 1){
				sql.append(" AND nvl(t.patchstatus, 0) != 2 AND nvl(t.patchstatus, 0) != 5   ") ;
			}else if(paramvo.getQrytype() == 2){
				sql.append(" AND ( nvl(t.patchstatus, 0) = 2 OR nvl(t.patchstatus, 0) = 5)   ") ;
			}else if(paramvo.getQrytype() == 3){
				sql.append(" AND cn.pk_confrim IN (   ");
				sql.append("SELECT pk_confrim    ") ;
				sql.append("  FROM cn_changeapply    ") ; 
				sql.append(" WHERE nvl(dr, 0) = 0    ") ; 
				sql.append("   AND ichangetype IN (1, 2)    ") ; 
				sql.append("   AND iapplystatus = 4    ");
				sql.append(" )   ");
			}
		}
		if(paramvo.getCorptype() != null){
			if(paramvo.getCorptype() == 1){
			    sql.append(" AND t.chargedeptname = ?   ") ; 
	            spm.addParam("小规模纳税人");
			}else if(paramvo.getCorptype() == 2){
			    sql.append(" AND t.chargedeptname = ?   ") ; 
	            spm.addParam("一般纳税人");
			}
		}
		if(paramvo.getComptype() != null && !"-1".equals(paramvo.getComptype().toString())){
			sql.append(" AND nvl(t.icompanytype,99) = ? ") ; 
			spm.addParam(paramvo.getComptype());//套餐类型
		}
		//加盟商主键 如果不为空，则按照加盟商主键查询所有数据；
		//如果加盟商主键为空，则判断是否为演示加盟商快速查询，如果是，则只查询演示加盟商；否则只查询正式加盟商；
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
		    String[] strs = paramvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND t.pk_corp in (").append(inSql).append(")");
		}/*else{
			//公司类型过滤：9：演示加盟商
			if(paramvo.getIpaymode() != null && paramvo.getIpaymode() != -1){
				sql.append(" AND account.channeltype = ?   ");
				spm.addParam(paramvo.getIpaymode());
			}else{
				sql.append(" AND account.channeltype != 9   ");
			}
		}*/
		//客户主键
		if(!StringUtil.isEmpty(paramvo.getPk_corpk())){
			sql.append(" AND t.pk_corpk = ?   ") ; 
			spm.addParam(paramvo.getPk_corpk());
		}
		if(!StringUtil.isEmpty(paramvo.getVqrysql())){
			sql.append(paramvo.getVqrysql());
		}
		if(paramvo.getSeletype() != null && paramvo.getSeletype() != -1){
			sql.append(" AND cn.ideductpropor = ?   ") ; 
			spm.addParam(paramvo.getSeletype());
		}
		sql.append(" ORDER BY t.dsubmitime DESC   ");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractConfrimVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQryCondition(paramvo, "listqry", false);
		List<ContractConfrimVO> list = (List<ContractConfrimVO>) multBodyObjectBO.queryDataPage(ContractConfrimVO.class,
				sqpvo.getSql(), sqpvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
		if (list != null && list.size() > 0) {
			setShowData(list, "listqry", paramvo.getAreaname());
		}
		return list;
	}
	
	
	/**
	 * 设置显示内容
	 * 
	 * @param list
	 * @param showtype
	 *            1、listqry：列表查询；2、audit：审核查询；3、info：明细查询；
	 * @throws DZFWarpException
	 */
	private void setShowData(List<ContractConfrimVO> list, String showtype, String areaname) throws DZFWarpException {
		Map<String, UserVO> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		Map<String, UserVO> opermap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		Map<Integer, String> areaMap = null;// 大区集合
		if ("listqry".equals(showtype)) {
			areaMap = pubser.getAreaMap(areaname, IStatusConstant.IQUDAO);
		}
		HashMap<String, UserVO> map = userSer.queryUserMap(IDefaultValue.DefaultGroup, true);
		for (ContractConfrimVO confvo : list) {
			confvo.setTstamp(confvo.getCheckts());// 校验时间戳，5:待审批；7：已驳回；取原合同，剩余情况取历史合同
			if ("listqry".equals(showtype)) {
				setListQryData(confvo, areaMap);
			} else if ("info".equals(showtype)) {// 明细查询
				setDetailQryData(confvo);
			} 
			QueryDeCodeUtils.decKeyUtil(new String[] { "corpname" }, confvo, 1);
			setShowData(map, confvo, marmap, opermap);
		}
	}
	
	/**
	 * 列表查询返回值设置
	 * @param confvo
	 * @param areaMap
	 * @throws DZFWarpException
	 */
	private void setListQryData(ContractConfrimVO confvo, Map<Integer, String> areaMap) throws DZFWarpException {
		if (confvo.getVstatus() != null && confvo.getVstatus() == IStatusConstant.IDEDUCTSTATUS_1) {// 已审核
			// 合同代账费 = 合同总金额 - 合同账本费
			confvo.setNaccountmny(SafeCompute.sub(confvo.getNtotalmny(), confvo.getNbookmny()));
			// 预付款扣款
			confvo.setNdeductmny(CommonUtil.getDZFDouble(confvo.getNdeductmny()));
			// 返点扣款
			confvo.setNdedrebamny(CommonUtil.getDZFDouble(confvo.getNdedrebamny()));
			// 变更后合同金额
			confvo.setNchangetotalmny(null);
		} else if (confvo.getVstatus() != null && confvo.getVstatus() == IStatusConstant.IDEDUCTSTATUS_9) {// 已终止
			// 扣款总金额 = 变更后扣款总金额
			confvo.setNdedsummny(confvo.getNchangesummny());
			// 预付款扣款 = 变更后预付款扣款
			confvo.setNdeductmny(CommonUtil.getDZFDouble(confvo.getNchangededutmny()));
			// 返点扣款 = 变更后返点扣款
			confvo.setNdedrebamny(CommonUtil.getDZFDouble(confvo.getNchangerebatmny()));
			// 合同代账费 = 变更后合同总金额 - 合同账本费
			confvo.setNaccountmny(SafeCompute.sub(confvo.getNtotalmny(), confvo.getNbookmny()));
		} else if (confvo.getVstatus() != null && confvo.getVstatus() == IStatusConstant.IDEDUCTSTATUS_10) {// 已作废
			// 合同总金额
			confvo.setNtotalmny(DZFDouble.ZERO_DBL);
			// 扣款总金额
			confvo.setNdedsummny(DZFDouble.ZERO_DBL);
			// 预付款扣款
			confvo.setNdeductmny(DZFDouble.ZERO_DBL);
			// 返点扣款
			confvo.setNdedrebamny(DZFDouble.ZERO_DBL);
			// 合同账本费
			confvo.setNbookmny(DZFDouble.ZERO_DBL);
			// 合同代账费
			confvo.setNaccountmny(DZFDouble.ZERO_DBL);
		} else {// 待提交、已驳回
				// 合同代账费 = 合同总金额 - 合同账本费
			confvo.setNaccountmny(SafeCompute.sub(confvo.getNtotalmny(), confvo.getNbookmny()));
			// 变更后合同金额
			confvo.setNchangetotalmny(null);
		}
		if (areaMap != null && !areaMap.isEmpty()) {
			String area = areaMap.get(confvo.getVprovince());
			if (!StringUtil.isEmpty(area)) {
				confvo.setAreaname(area);// 大区
			}
		}
	}
	
	/**
	 * 详情查询返回值设置
	 * @param confvo
	 * @throws DZFWarpException
	 */
	private void setDetailQryData(ContractConfrimVO confvo) throws DZFWarpException{
		DZFDouble totalmny = DZFDouble.ZERO_DBL;
		DZFDouble changetotalmny = DZFDouble.ZERO_DBL;
		if(confvo.getVstatus() != null && confvo.getVstatus() == IStatusConstant.IDEDUCTSTATUS_9){//已终止
			//合同总金额 = (原合同)变更前合同金额
			totalmny = confvo.getNchangetotalmny();
			//变更后合同金额 = (原合同)现金额
			changetotalmny = confvo.getNtotalmny();
			confvo.setNtotalmny(totalmny);
			confvo.setNchangetotalmny(changetotalmny);
		}else if(confvo.getVstatus() != null && confvo.getVstatus() == IStatusConstant.IDEDUCTSTATUS_10){//已作废
			//合同总金额 = (原合同)变更前合同金额
			totalmny = confvo.getNchangetotalmny();
			confvo.setNtotalmny(totalmny);
			confvo.setNchangetotalmny(DZFDouble.ZERO_DBL);
		}
		String statusname = "";
		//0：待提交；5:待审批： 1：审核通过； 7：已驳回；8：服务到期；9：已终止；10：已作废；
		switch(confvo.getVdeductstatus()){
		case 0:
			statusname = "待提交";
			break;
		case 5:
			statusname = "待审核";
			break;
		case 1:
			statusname = "已审核";
			break;
		case 7:
			statusname = "已驳回";
			break;
		case 8:
			statusname = "服务到期";
			break;
		case 9:
			statusname = "已终止";
			break;
		case 10:
			statusname = "已作废";
			break;
		}
		confvo.setVstatusname(statusname);
	}
	
	
	/**
	 * 查询设置返回值
	 * 
	 * @param confvo
	 * @param marmap
	 * @throws DZFWarpException
	 */
	private void setShowData(HashMap<String, UserVO> map, ContractConfrimVO confvo, Map<String, UserVO> marmap,
			Map<String, UserVO> opermap) throws DZFWarpException {
		UserVO uservo = map.get(confvo.getVoperator());
		if (uservo != null) {
			confvo.setVopername(uservo.getUser_name());// 经办人（审核人）姓名
		}
		CorpVO corpvo = CorpCache.getInstance().get(null, confvo.getPk_corpk());
		if (corpvo != null) {
			confvo.setCorpkname(corpvo.getUnitname());// 客户名称
		}
		if (marmap != null && !marmap.isEmpty()) {
			uservo = marmap.get(confvo.getPk_corp());
			if (uservo != null) {
				confvo.setVmanagername(uservo.getUser_name());// 渠道经理
			}
		}
		if (opermap != null && !opermap.isEmpty()) {
			uservo = opermap.get(confvo.getPk_corp());
			if (uservo != null) {
				confvo.setVoperater(uservo.getUser_name());// 渠道经理
			}
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public ContractConfrimVO queryInfoById(ContractConfrimVO paramvo) throws DZFWarpException {
		ContractConfrimVO confvo = null;
		QryParamVO pavo = new QryParamVO();
		pavo.setPk_bill(paramvo.getPk_contract());
		pavo.setPk_corp(paramvo.getPk_corp());
		QrySqlSpmVO qryvo =  getQryCondition(pavo, "info", false);
		List<ContractConfrimVO> list = (List<ContractConfrimVO>) singleObjectBO.executeQuery(qryvo.getSql(),
				qryvo.getSpm(), new BeanListProcessor(ContractConfrimVO.class));
		if (list != null && list.size() > 0) {
			setShowData(list, "info", null);
			confvo = list.get(0);
		}
		if(confvo != null && !StringUtil.isEmpty(confvo.getVconfreason())){
			RejectHistoryHVO[] rejeVOs = qryRejectHistory(confvo.getPk_contract());
			if(rejeVOs != null && rejeVOs.length > 0){
				confvo.setChildren(rejeVOs);
			}
		}
		return confvo;
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
		sql.append("SELECT h.*, r.user_name AS coperator   ");
		sql.append("  FROM cn_rejecthistory_h h    ");
		sql.append("  LEFT JOIN sm_user r ON h.coperatorid = r.cuserid    ");
		sql.append(" WHERE nvl(h.dr, 0) = 0    ");
		sql.append("   AND nvl(r.dr, 0) = 0    ");
		sql.append("   AND h.pk_contract = ?    ");
		spm.addParam(pk_contract);
		sql.append(" ORDER BY h.ts DESC   ");
		List<RejectHistoryHVO> list = (List<RejectHistoryHVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(RejectHistoryHVO.class));
		if(list != null && list.size() > 0){
			QueryDeCodeUtils.decKeyUtils(new String[]{"coperator"}, list, 1);
			return list.toArray(new RejectHistoryHVO[0]);
		}
		return null;
	}

	/**
	 * 获取查询条件
	 * @param cuserid
	 * @param qrytype  1：渠道经理；2：培训师；3：渠道运营；
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		String[] corps = pubser.getManagerCorp(cuserid, qrytype);
		if(corps != null && corps.length > 0){
			String where = SqlUtil.buildSqlForIn(" t.pk_corp", corps);
			sql.append(" AND ").append(where);
		}else{
			sql.append(" AND t.pk_corp is null   ") ; 
		}
		return sql.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ContractDocVO> getAttatches(ContractDocVO qvo) throws DZFWarpException {
		if (StringUtil.isEmpty(qvo.getPk_contract()) && StringUtil.isEmpty(qvo.getPk_contract_doc())) {
			throw new BusinessException("获取附件参数有误");
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" nvl(dr,0) = 0 and pk_corp = ?");
		SQLParameter spm = new SQLParameter();
		spm.addParam(qvo.getPk_corp());
		if (!StringUtil.isEmpty(qvo.getPk_contract())) {
			sql.append(" and  pk_contract = ? ");
			spm.addParam(qvo.getPk_contract());
		}
		if (!StringUtil.isEmpty(qvo.getPk_contract_doc())) {
			sql.append(" and pk_contract_doc = ? ");
			spm.addParam(qvo.getPk_contract_doc());
		}
		String orderBy = " ts asc ";
		List<ContractDocVO> list = (List<ContractDocVO>) singleObjectBO.retrieveByClause(ContractDocVO.class,
				sql.toString(), orderBy, spm);
		if (list != null && list.size() > 0) {
			return list;
		}
		return new ArrayList<>();
	}


}
