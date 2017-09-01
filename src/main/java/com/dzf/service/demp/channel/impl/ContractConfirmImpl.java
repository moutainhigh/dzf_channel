package com.dzf.service.demp.channel.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.demp.channel.ChnPayBillVO;
import com.dzf.model.demp.channel.ContractConfrimVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.service.demp.channel.IContractConfirm;

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
			CorpVO accvo = null;
			for(ContractConfrimVO vo : list){
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(accvo != null){
					vo.setCorpname(accvo.getUnitname());
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
		sql.append("SELECT * FROM cn_paybill WHERE nvl(dr,0) = 0 \n");
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND vstatus = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		sql.append(" AND vstatus != 1");
		sql.append(" order by dpaydate desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

}
