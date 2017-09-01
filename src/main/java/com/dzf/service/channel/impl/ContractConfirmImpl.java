package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.channel.ContractConfrimVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.IContractConfirm;

@Service("contractconfser")
public class ContractConfirmImpl implements IContractConfirm {
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Override
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo);
		return multBodyObjectBO.queryDataTotal(ChnPayBillVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContractConfrimVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo);
		List<ContractConfrimVO> list = (List<ContractConfrimVO>) multBodyObjectBO.queryDataPage(ContractConfrimVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
		if(list != null && list.size() > 0){
			CorpVO corpvo = null;
			for(ContractConfrimVO vo : list){
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(corpvo != null){
					vo.setCorpname(corpvo.getUnitname());
				}
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
				if(corpvo != null){
					vo.setCorpkname(corpvo.getUnitname());
					vo.setChargedeptname(corpvo.getChargedeptname());
				}
			}
		}
		return list;
	}
	
	/**
	 * 获取周期数
	 * @param begindate
	 * @param enddate
	 * @return
	 */
	private Integer getCyclenum(DZFDate begindate, DZFDate enddate){
		int result = enddate.getMonth() - begindate.getMonth();  
        int month = (enddate.getYear() - begindate.getYear()) * 12;  
        System.out.println(Math.abs(month + result)); 
		return 0;
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
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append("   AND con.vdeductstatus = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		sql.append(" order by con.ts desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

}
