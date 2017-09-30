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
import com.dzf.model.channel.ContractConfrimVO;
import com.dzf.model.channel.report.WeekBusimngVO;
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
	public WeekBusimngVO queryThisWeek(QryParamVO paramvo) throws DZFWarpException {
		Map<String,DZFDate> weekmap = ToolsUtil.getWeekDate(new Date());
		if(weekmap != null){
			paramvo.setBegdate(weekmap.get("begin"));
			paramvo.setEnddate(weekmap.get("end"));
		}
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
	 * 查询本周新增加盟商数量
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer qryFranchiseeNum(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" SELECT t.pk_corp FROM bd_account t");
		sql.append("  WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("    AND nvl(t.ischannel, 'N') = 'Y' \n");
		if (paramvo.getBegdate() != null) {
			sql.append("  AND t.begindate >= ? \n");
			spm.addParam(paramvo.getBegdate());
		}
		if (paramvo.getEnddate() != null) {
			sql.append("  AND t.begindate <= ? ");
			spm.addParam(paramvo.getEnddate());
		}
		List<AccountVO> list = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AccountVO.class));
		if (list != null && list.size() > 0) {
			return list.size();
		}
		return 0;
	}

	/**
	 * 查询本周加盟费、预付款金额
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, DZFDouble> qryFranchiseeFee(QryParamVO paramvo) throws DZFWarpException {
		Map<Integer, DZFDouble> map = new HashMap<Integer,DZFDouble>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pay.ipaytype, sum(nvl(pay.npaymny,0)) as npaymny \n");
		sql.append("  FROM cn_paybill pay \n");
		sql.append(" WHERE nvl(pay.dr, 0) = 0 \n");
		if (paramvo.getBegdate() != null) {
			sql.append("   AND pay.dconfirmtime >= ? \n");
			spm.addParam(paramvo.getBegdate() + " 00:00:00");
		}
		if (paramvo.getEnddate() != null) {
			sql.append("   and pay.dconfirmtime <= ? \n");
			spm.addParam(paramvo.getEnddate() + " 23:59:59");
		}
		sql.append("   AND pay.ipaytype = 3 ");
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
	 * 查询本周新增客户数量
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Integer qryCustNum(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.pk_corp \n");
		sql.append("  FROM bd_corp p \n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y' \n");
		if (paramvo.getBegdate() != null) {
			sql.append("   AND p.createdate >= ? \n");
			spm.addParam(paramvo.getBegdate());
		}
		if (paramvo.getEnddate() != null) {
			sql.append("   AND p.createdate <= ? \n");
			spm.addParam(paramvo.getBegdate());
		}
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CorpVO.class));
		if(list != null && list.size() > 0){
			return list.size();
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, DZFDouble> qryContractMny(QryParamVO paramvo) {
		Map<String, DZFDouble> map = new HashMap<String, DZFDouble>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT sum(nvl(t.ntotalmny, 0)) as ntotalmny, \n");
		sql.append("       sum(nvl(t.ndeductmny, 0)) as ndeductmny \n");
		sql.append("  FROM cn_contract t \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND t.vdeductstatus = 2 \n");
		List<ContractConfrimVO> list = (List<ContractConfrimVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ContractConfrimVO.class));
		if(list != null && list.size() > 0){
			map.put("ntotalmny", list.get(0).getNtotalmny());
			map.put("ndeductmny", list.get(0).getNdeductmny());
		}
		return map;
	}
}
