package com.dzf.service.channel.report.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.channel.report.MonthBusimngVO;
import com.dzf.model.channel.report.WeekBusimngVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.AccountVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IIndexRep;

@Service("indexrepimpl")
public class IndexRepImpl implements IIndexRep {
	
    @Autowired
    private SingleObjectBO singleObjectBO;

	@Override
	public WeekBusimngVO queryBusiByWeek(QryParamVO paramvo) throws DZFWarpException {
		Map<String,DZFDate> weekmap = ToolsUtil.getWeekDate(new Date());
		if(weekmap != null){
			paramvo.setBegdate(weekmap.get("begin"));
			paramvo.setEnddate(weekmap.get("end"));
		}
		paramvo.setQrytype(IStatusConstant.IINDEXQRYTYPE_1);
		WeekBusimngVO budivo = new WeekBusimngVO();
		budivo.setItswkfranchisee(qryFranchiseeNum(paramvo));//本周新增加盟商
		Map<Integer, DZFDouble> chisfeemap = qryFranchiseeFee(paramvo);
		if(chisfeemap != null){
			budivo.setNtswkinitialfee(chisfeemap.get(1));//本周收到加盟费
			budivo.setNtswkcharge(chisfeemap.get(2));//本周收到预付款
		}
		budivo.setItswkcustomer(qryCustNum(paramvo));//本周新增客户
		Map<String, DZFDouble> contmap = qryContractMny(paramvo);
		if(contmap != null){
			budivo.setNtswkcontamount(contmap.get("ntotalmny"));
			budivo.setNtswkamount(contmap.get("ndeductmny"));
		}
		return budivo;
	}
	
	/**
	 * 查询新增加盟商数量 : 1、本周； 2、本月；3、现有加盟商
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer qryFranchiseeNum(QryParamVO paramvo) throws DZFWarpException {
		DZFDate date = new DZFDate();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" SELECT t.pk_corp FROM bd_account t");
		sql.append("  WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("    AND nvl(t.ischannel, 'N') = 'Y' \n");
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("  AND t.begindate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append("  AND t.begindate <= ? ");
				spm.addParam(paramvo.getEnddate());
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(t.begindate,1,7) = ? \n") ; //初始化日期  = 当前月份
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append("  AND t.begindate <= ? ");
			spm.addParam(date);
		}
		List<AccountVO> list = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AccountVO.class));
		if (list != null && list.size() > 0) {
			return list.size();
		}
		return 0;
	}

	/**
	 * 查询加盟费、预付款金额：1、本周；2、本月；本年；
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, DZFDouble> qryFranchiseeFee(QryParamVO paramvo) throws DZFWarpException {
		DZFDate date = new DZFDate();
		Map<Integer, DZFDouble> map = new HashMap<Integer,DZFDouble>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pay.ipaytype, sum(nvl(pay.npaymny,0)) as npaymny \n");
		sql.append("  FROM cn_paybill pay \n");
		sql.append(" WHERE nvl(pay.dr, 0) = 0 \n");
		sql.append("   AND pay.ipaytype = 3 ");
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("   AND pay.dconfirmtime >= ? \n");
				spm.addParam(paramvo.getBegdate() + " 00:00:00");
			}
			if (paramvo.getEnddate() != null) {
				sql.append("   and pay.dconfirmtime <= ? \n");
				spm.addParam(paramvo.getEnddate() + " 23:59:59");
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(pay.dconfirmtime,1,7) = ? \n") ; 
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(pay.dconfirmtime,1,4) = ? \n") ; 
			spm.addParam(date.getYear());
		}
		sql.append(" GROUP BY pay.ipaytype ");
		List<ChnPayBillVO> list = (List<ChnPayBillVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnPayBillVO.class));
		if(list != null && list.size() > 0){
			for(ChnPayBillVO vo: list){
				map.put(vo.getIpaytype(), vo.getNpaymny());
			}
		}
		return map;
	}
	
	/**
	 * 查询新增客户数量：1、本周；2、本月；3、现有；
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Integer qryCustNum(QryParamVO paramvo) throws DZFWarpException {
		DZFDate date = new DZFDate();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.pk_corp \n");
		sql.append("  FROM bd_corp p \n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y' \n");
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("   AND p.createdate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append("   AND p.createdate <= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(p.createdate,1,7) = ? \n") ; 
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(p.createdate,1,4) <= ? \n") ; 
			spm.addParam(date.getYear());
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
	@SuppressWarnings("unchecked")
	private Map<String, DZFDouble> qryContractMny(QryParamVO paramvo) {
		DZFDate date = new DZFDate();
		Map<String, DZFDouble> map = new HashMap<String, DZFDouble>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT sum(nvl(t.ntotalmny, 0)) as ntotalmny, \n");
		sql.append("       sum(nvl(t.ndeductmny, 0)) as ndeductmny \n");
		sql.append("  FROM cn_contract t \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND t.vdeductstatus = 2 \n");
		if(IStatusConstant.IINDEXQRYTYPE_1 == paramvo.getQrytype()){
			if (paramvo.getBegdate() != null) {
				sql.append("   AND t.deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append("   AND t.deductdata <= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
		}else if(IStatusConstant.IINDEXQRYTYPE_2 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(t.deductdata,1,7) = ? \n") ; 
			spm.addParam(date.getYear()+"-"+date.getStrMonth());
		}else if(IStatusConstant.IINDEXQRYTYPE_3 == paramvo.getQrytype()){
			sql.append(" AND SUBSTR(t.deductdata,1,4) = ? \n") ; 
			spm.addParam(date.getYear());
		}
		List<ContractConfrimVO> list = (List<ContractConfrimVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ContractConfrimVO.class));
		if(list != null && list.size() > 0){
			map.put("ntotalmny", list.get(0).getNtotalmny());
			map.put("ndeductmny", list.get(0).getNdeductmny());
		}
		return map;
	}

	@Override
	public MonthBusimngVO queryBusiByMonth(QryParamVO paramvo) throws DZFWarpException {
		MonthBusimngVO busivo = new MonthBusimngVO();
		paramvo.setQrytype(IStatusConstant.IINDEXQRYTYPE_3);
		//现有加盟商、现有客户数、本年累计已收加盟费、本年累计收到预付款、本年累计扣款金额
		busivo.setIfranchisee(qryFranchiseeNum(paramvo));//现有加盟商
		busivo.setIcustomer(qryCustNum(paramvo));//现有客户数
		Map<Integer, DZFDouble> yearfeemap = qryFranchiseeFee(paramvo);
		if(yearfeemap != null){
			busivo.setNtsyrinitialfee(yearfeemap.get(1));//本年累计已收加盟费
			busivo.setNtsyrcharge(yearfeemap.get(2));//本年累计收到预付款
		}
		Map<String, DZFDouble> yearconmap = qryContractMny(paramvo);
		if(yearconmap != null){
			busivo.setNtsyramount(yearconmap.get("ndeductmny"));//本年累计扣款金额
		}
		//本月新增加盟商、本月新增客户数、本月收到加盟费、本月收到预付款、本月扣款金额、本月新增合同金额
		paramvo.setQrytype(IStatusConstant.IINDEXQRYTYPE_2);
		busivo.setItsmhfranchisee(qryFranchiseeNum(paramvo));//本月新增加盟商
		busivo.setItsmhcustomer(qryCustNum(paramvo));//本月新增客户数
		Map<Integer, DZFDouble> monthfeemap = qryFranchiseeFee(paramvo);
		if(monthfeemap != null){
			busivo.setNtsmhinitialfee(yearfeemap.get(1));//本月收到加盟费
			busivo.setNtsmhcharge(yearfeemap.get(2));//本月收到预付款
		}
		Map<String, DZFDouble> monthconmap = qryContractMny(paramvo);
		if(monthconmap != null){
			busivo.setNtsmhcontamount(monthconmap.get("ntotalmny"));//本月扣款金额
			busivo.setNtsmhamount(monthconmap.get("ndeductmny"));//本月新增合同金额
		}
		return busivo;
	}
}
