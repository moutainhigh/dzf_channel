package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.BalanceRepVO;
import com.dzf.model.channel.report.ContQryVO;
import com.dzf.model.channel.report.MonthBusimngVO;
import com.dzf.model.channel.report.WeekBusimngVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.AccountVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IIndexRep;
import com.dzf.service.pub.IPubService;

@Service("indexrepimpl")
public class IndexRepImpl implements IIndexRep {
	
    @Autowired
    private SingleObjectBO singleObjectBO;
    
    @Autowired
    private IPubService pubser;
    
    private String filtersql = QueryUtil.getWhereSql();

	@Override
	public WeekBusimngVO queryBusiByWeek(QryParamVO paramvo) throws DZFWarpException {
		Map<String,DZFDate> weekmap = ToolsUtil.getWeekDate(new Date());
		if(weekmap != null){
			paramvo.setBegdate(weekmap.get("begin"));
			paramvo.setEnddate(weekmap.get("end"));
		}
		WeekBusimngVO budivo = new WeekBusimngVO();
		Integer datatype = getDataType(paramvo.getCuserid());
		String addsql = "";
		if(datatype != null){
			if(datatype == -1){
				return budivo;
			}else if(datatype == 0){
				addsql = "alldata";
			}else{
				addsql = pubser.getPowerSql(paramvo.getCuserid(), datatype);
			}
		}else{
			return budivo;
		}
		if(StringUtil.isEmpty(addsql)){
			return budivo;
		}
		paramvo.setQrytype(IStatusConstant.IINDEXQRYTYPE_1);
		budivo.setItswkfranchisee(qryFranchiseeNum(paramvo, addsql));//本周新增加盟商
		BalanceRepVO balvo = qryFranchiseeFee(paramvo, addsql);
		if(balvo != null){
			budivo.setNtswkinitialfee(balvo.getNbzjmny());//本周收到保证金
			budivo.setNtswkcharge(balvo.getNyfkmny());//本周收到预付款
		}
		budivo.setItswkcustomer(qryCustNum(paramvo, addsql));//本周新增客户
		Map<String, DZFDouble> contmap = qryContractMny(paramvo, addsql);
		if(contmap != null){
			budivo.setNtswkcontamount(contmap.get("ntotalmny"));//本周新增合同金额
			budivo.setNtswkamount(contmap.get("ndeductmny"));//本周扣款金额
		}
		return budivo;
	}
	
	/**
	 * 查询新增加盟商数量 : 1、本周； 2、本月；3、现有加盟商
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer qryFranchiseeNum(QryParamVO paramvo, String addsql) throws DZFWarpException {
		DZFDate date = new DZFDate();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" SELECT account.pk_corp FROM bd_account account \n");
		sql.append("  WHERE nvl(account.dr, 0) = 0 \n");
		sql.append("    AND nvl(account.ischannel, 'N') = 'Y' \n");
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("  AND account.begindate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append("  AND account.begindate <= ? ");
				spm.addParam(paramvo.getEnddate());
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(account.begindate,1,7) = ? \n") ; //初始化日期  = 当前月份
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append("  AND account.begindate <= ? ");
			spm.addParam(date);
		}
		sql.append(" AND nvl(account.isseal,'N') = 'N' \n");
		sql.append(" AND (account.drelievedate is null OR account.drelievedate > ? )" );
		spm.addParam(new DZFDate());
		if(!"alldata".equals(addsql)){
			sql.append(addsql);
		}
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		List<AccountVO> list = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AccountVO.class));
		if (list != null && list.size() > 0) {
			return list.size();
		}
		return 0;
	}

	/**
	 * 查询保证金、预付款金额：1、本周；2、本月；本年；
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private BalanceRepVO qryFranchiseeFee(QryParamVO paramvo, String addsql) throws DZFWarpException {
		DZFDate date = new DZFDate();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT  nvl(sum(nvl(CASE WHEN l.ipaytype = 1 AND l.iopertype = 1 THEN nvl(l.npaymny,0) ELSE 0 END,0) )  \n") ;
		sql.append("      + sum(nvl(CASE WHEN l.ipaytype = 1 AND l.iopertype = 4 THEN nvl(l.npaymny,0) ELSE 0 END,0) ),0) AS nbzjmny,  \n") ; 
		sql.append("        nvl(sum(nvl(CASE WHEN l.ipaytype = 2 AND l.iopertype = 1 THEN nvl(l.npaymny,0) ELSE 0 END,0) )  \n") ; 
		sql.append("      + sum(nvl(CASE WHEN l.ipaytype = 2 AND l.iopertype = 4 THEN nvl(l.npaymny,0) ELSE 0 END,0) ),0) AS nyfkmny  \n") ; 
		sql.append("  FROM cn_detail l  \n") ; 
		sql.append("  LEFT JOIN bd_account account ON l.pk_corp = account.pk_corp  \n") ; 
		sql.append(" WHERE nvl(l.dr, 0) = 0  \n") ; 
		sql.append(" AND nvl(account.dr,0) = 0  \n") ; 
		sql.append(" AND l.ipaytype IN (1, 2)  \n") ; 
		sql.append(" AND l.iopertype IN (1,4) \n");
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("   AND l.doperatedate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append("   and l.doperatedate <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(l.doperatedate,1,7) = ? \n") ; 
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(l.doperatedate,1,4) = ? \n") ; 
			spm.addParam(date.getYear());
		}
		if(!"alldata".equals(addsql)){
			sql.append(addsql);
		}
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		List<BalanceRepVO> list = (List<BalanceRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(BalanceRepVO.class));
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 查询新增客户数量：1、本周；2、本月；3、现有；
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Integer qryCustNum(QryParamVO paramvo, String addsql) throws DZFWarpException {
		DZFDate date = new DZFDate();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.pk_corp \n");
		sql.append("  FROM bd_corp p \n");
		sql.append("  LEFT JOIN bd_account account ON p.fathercorp = account.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(account.dr, 0) = 0 \n");
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y' \n");
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("   AND p.createdate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append("   AND p.createdate <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(p.createdate,1,7) = ? \n") ; 
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(p.createdate,1,4) <= ? \n") ; 
			spm.addParam(date.getYear());
		}
		sql.append(" AND nvl(account.isseal,'N') = 'N' \n");//已封存的加盟商数据不统计，加盟商的已封存客户统计
		sql.append(" AND (account.drelievedate is null OR account.drelievedate > ? )" );
		spm.addParam(new DZFDate());
		if(!"alldata".equals(addsql)){
			sql.append(addsql);
		}
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CorpVO.class));
		if(list != null && list.size() > 0){
			return list.size();
		}
		return 0;
	}
	
	/**
	 * 查询合同金额、合同扣款金额：1、本周；2、本月；3、本年；
	 * @param paramvo
	 * @return
	 */
	private Map<String, DZFDouble> qryContractMny(QryParamVO paramvo, String addsql) throws DZFWarpException {
		Map<String, DZFDouble> map = new HashMap<String, DZFDouble>();
		map.put("ntotalmny", DZFDouble.ZERO_DBL);
		map.put("ndeductmny", DZFDouble.ZERO_DBL);
		List<ContQryVO> kklist = qryPositiveData(paramvo, addsql);
		if(kklist != null && kklist.size() > 0){
			map.put("ntotalmny", SafeCompute.add(map.get("ntotalmny"), kklist.get(0).getNaccountmny()));
			map.put("ndeductmny", SafeCompute.add(map.get("ndeductmny"), kklist.get(0).getNdedsummny()));
		}
		List<ContQryVO> tklist = qryNegativeData(paramvo, addsql);
		if(tklist != null && tklist.size() > 0){
			map.put("ntotalmny", SafeCompute.add(map.get("ntotalmny"), tklist.get(0).getNaccountmny()));
			map.put("ndeductmny", SafeCompute.add(map.get("ndeductmny"), tklist.get(0).getNdedsummny()));
		}
		return map;
	}
	
	/**
	 * 查询扣款数据
	 * @param paramvo
	 * @param addsql
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<ContQryVO> qryPositiveData(QryParamVO paramvo, String addsql) throws DZFWarpException {
		DZFDate date = new DZFDate();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT SUM(nvl(cn.ndedsummny, 0)) AS ndedsummny,  \n");
		sql.append("       SUM(nvl(ct.nchangetotalmny, 0) - nvl(ct.nbookmny, 0)) AS naccountmny  \n");
		sql.append("  FROM cn_contract cn  \n");
		sql.append("  INNER JOIN ynt_contract ct ON cn.pk_contract = ct.pk_contract \n");
		sql.append("  LEFT JOIN bd_account account ON cn.pk_corp = account.pk_corp  \n");
		sql.append(" WHERE nvl(cn.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.isncust, 'N') = 'N'  \n");
		sql.append("   AND cn.vdeductstatus in (?, ?, ?)  \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("   AND cn.deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append("   AND cn.deductdata <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(cn.deductdata,1,7) = ? \n") ; 
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(cn.deductdata,1,4) = ? \n") ; 
			spm.addParam(date.getYear());
		}
		if (!"alldata".equals(addsql)) {
			sql.append(addsql);
		}
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		return (List<ContQryVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ContQryVO.class));
	}
	
	/**
	 * 查询退款数据
	 * @param paramvo
	 * @param addsql
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<ContQryVO> qryNegativeData(QryParamVO paramvo, String addsql) throws DZFWarpException {
		DZFDate date = new DZFDate();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT SUM(nvl(cn.nsubdedsummny, 0)) AS ndedsummny,  \n");
		sql.append("       SUM(CASE cn.vstatus  \n") ; 
		sql.append("             WHEN 9 THEN  \n") ; 
		sql.append("              nvl(cn.nsubtotalmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              nvl(cn.nsubtotalmny, 0) + nvl(ct.nbookmny, 0)  \n") ; 
		sql.append("           END) AS naccountmny  \n") ; 
		sql.append("  FROM cn_contract cn  \n");
		sql.append("  INNER JOIN ynt_contract ct ON cn.pk_contract = ct.pk_contract \n");
		sql.append("  LEFT JOIN bd_account account ON cn.pk_corp = account.pk_corp  \n");
		sql.append(" WHERE nvl(cn.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.isncust, 'N') = 'N'  \n");
		sql.append("   AND cn.vdeductstatus in (?, ?)  \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("   AND cn.deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append("   AND cn.deductdata <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(cn.deductdata,1,7) = ? \n") ; 
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(cn.deductdata,1,4) = ? \n") ; 
			spm.addParam(date.getYear());
		}
		if (!"alldata".equals(addsql)) {
			sql.append(addsql);
		}
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		return (List<ContQryVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ContQryVO.class));
	}

	@Override
	public MonthBusimngVO queryBusiByMonth(QryParamVO paramvo) throws DZFWarpException {
		MonthBusimngVO busivo = new MonthBusimngVO();
		Integer datatype = getDataType(paramvo.getCuserid());
		String addsql = "";
		if(datatype != null){
			if(datatype == -1){
				return busivo;
			}else if(datatype == 0){
				addsql = "alldata";
			}else{
				addsql = pubser.getPowerSql(paramvo.getCuserid(), datatype);
			}
		}else{
			return busivo;
		}
		if(StringUtil.isEmpty(addsql)){
			return busivo;
		}
		paramvo.setQrytype(IStatusConstant.IINDEXQRYTYPE_3);
		//现有加盟商、现有客户数、本年累计已收保证金、本年累计收到预付款、本年累计扣款金额
		busivo.setIfranchisee(qryFranchiseeNum(paramvo, addsql));//现有加盟商
		busivo.setIcustomer(qryCustNum(paramvo, addsql));//现有客户数
		BalanceRepVO ybalvo =  qryFranchiseeFee(paramvo, addsql);
		if(ybalvo != null){
			busivo.setNtsyrinitialfee(ybalvo.getNbzjmny());//本年累计已收保证金
			busivo.setNtsyrcharge(ybalvo.getNyfkmny());//本年累计收到预付款
		}
		Map<String, DZFDouble> yearconmap = qryContractMny(paramvo, addsql);
		if(yearconmap != null){
			busivo.setNtsyramount(yearconmap.get("ndeductmny"));//本年累计扣款金额
		}
		//本月新增加盟商、本月新增客户数、本月收到保证金、本月收到预付款、本月扣款金额、本月新增合同金额
		paramvo.setQrytype(IStatusConstant.IINDEXQRYTYPE_2);
		busivo.setItsmhfranchisee(qryFranchiseeNum(paramvo, addsql));//本月新增加盟商
		busivo.setItsmhcustomer(qryCustNum(paramvo, addsql));//本月新增客户数
		BalanceRepVO mbalvo =  qryFranchiseeFee(paramvo, addsql);
		if(mbalvo != null){
			busivo.setNtsmhinitialfee(mbalvo.getNbzjmny());//本月收到保证金
			busivo.setNtsmhcharge(mbalvo.getNyfkmny());//本月收到预付款
		}
		Map<String, DZFDouble> monthconmap = qryContractMny(paramvo, addsql);
		if(monthconmap != null){
			busivo.setNtsmhcontamount(monthconmap.get("ntotalmny"));//本月扣款金额
			busivo.setNtsmhamount(monthconmap.get("ndeductmny"));//本月新增合同金额
		}
		return busivo;
	}
	
	/**
	 * 获取查询的数据类型
	 * @param cuserid
	 * @return -1:无权查询数据；0：所有数据；1：渠道区域；2：培训区域；3：运营区域；
	 * @throws DZFWarpException
	 */
	private Integer getDataType(String cuserid) throws DZFWarpException {
		List<String> codelist = pubser.queryRoleCode(cuserid);
		if(codelist != null && codelist.size() > 0){
			if(codelist.size() == 1){
				 Map<String,Integer> typemap = getRoleType();
				 if(typemap != null && !typemap.isEmpty()){
					 return typemap.get(codelist.get(0));
				 }
				 return -1;//所分配的单个角色，不在角色对应的权限中，不予查询
			}else{
				List<String> bigroles = getBigPowerRole();
				//分配多个角色，如果有最大数据权限角色，则查询所有数据；剩余情况，不予查询；
				for(String code : codelist){
					if(bigroles.contains(code)){
						return 0;
					}
				}
				return -1;
			}
		}
		return -1;//没分配角色，不予查询
	}
	
	/**
	 * 拥有最大数据的角色
	 * @return
	 * @throws DZFWarpException
	 */
	private List<String> getBigPowerRole() throws DZFWarpException {
		List<String> list = new ArrayList<String>();
		list.add("channel");
		list.add("corpzxgl");
		list.add("corpzjl");
		list.add("qdsqr");
		list.add("gszjl");
		list.add("cwb-kp");
		list.add("cwb-zj");
		return list;
	}
	
	/**
	 * 获取角色对应的权限表
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,Integer> getRoleType() throws DZFWarpException{
		Map<String,Integer> map = new HashMap<String,Integer>();
		// 0：所有数据；1：渠道区域；2：培训区域；3：运营区域；
		map.put("channel", 0);//加盟商管理
		map.put("corpzxgl", 0);//直销管理
		map.put("corppxjl", 2);//运营培训经理-->培训区域设置
		map.put("corppxs", 2);//培训师-->培训区域设置
		map.put("corpzjl", 0);//在线会计部总经理
		map.put("corpdqz", 1);//大区总-->渠道区域设置
		map.put("corpqdjl", 1);//渠道经理-->渠道区域设置
		map.put("corpqdyy", 3);//渠道运营-->运营区域设置
		map.put("qdsqr", 0);//财务部-财务核算
		map.put("gszjl", 0);//公司总经理
		map.put("corpqdyyjl", 3);//渠道运营经理-->运营区域设置
		map.put("cwb-kp", 0);//财务部-财务开票
		map.put("cwb-zj", 0);//财务部-财务总监
		map.put("zjb-gl", 0);//总经办-管理
		return map;
	}
}
