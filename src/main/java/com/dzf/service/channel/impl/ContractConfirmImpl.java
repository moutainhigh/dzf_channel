package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ArrayListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.ContractConfrimVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.IContractConfirm;

@Service("contractconfser")
public class ContractConfirmImpl implements IContractConfirm {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

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
			int icyclenum = 0;
			for (ContractConfrimVO vo : conflist) {
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if (corpvo != null) {
					vo.setCorpname(corpvo.getUnitname());
				}
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corpk());
				if (corpvo != null) {
					vo.setCorpkname(corpvo.getUnitname());
					vo.setChargedeptname(corpvo.getChargedeptname());
				}
				icyclenum = ToolsUtil.getCyclenum(vo.getDbegindate(), vo.getDenddate());
				vo.setIcyclenum(icyclenum);
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
		sql.append("   AND con.vdeductstatus is not null \n");
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			if(paramvo.getQrytype() != 0){
				sql.append("   AND con.vdeductstatus = ? \n");
				spm.addParam(paramvo.getQrytype());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append("   AND con.dbegindate >= ? \n") ; 
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append("   AND con.dbegindate <= ? \n") ; 
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by con.ts desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public void updateConfStatus(ContractConfrimVO[] confrimVOs, Integer status) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" UPDATE ynt_contract SET vdeductstatus = ? WHERE nvl(dr,0) = 0 AND pk_contract = ? ");
		if(IStatusConstant.ICONTRACTCONFRIM_1 == status){
			checkConfrState(confrimVOs);//数据状态校验
			confrimVOs[0].setVdeductstatus(IStatusConstant.IDEDUCTSTATUS_2);
			confrimVOs[0].setTstamp(new DZFDateTime());
			singleObjectBO.saveObject("000001", confrimVOs[0]);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_2);//待扣款
		}else if(IStatusConstant.ICONTRACTCONFRIM_2 == status){
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);//待确认
		}else if(IStatusConstant.ICONTRACTCONFRIM_3 == status){
			String delsql = " UPDATE chn_contract SET dr = 1 WHERE nvl(dr,0) = 0 AND pk_confrim = ? ";
			SQLParameter delspm = new SQLParameter();
			delspm.addParam(confrimVOs[0].getPk_confrim());
			singleObjectBO.executeUpdate(delsql, delspm);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);//待确认
		}
		spm.addParam(confrimVOs[0].getPk_contract());
		singleObjectBO.executeUpdate(sql.toString(), spm);
	}
	
	/**
	 * 合同确认前状态校验
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void checkConfrState(ContractConfrimVO[] confrimVOs) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT tstamp \n") ;
		sql.append("  FROM ynt_contract t \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n") ; 
		sql.append("   AND t.pk_contract = ? ");
		spm.addParam(confrimVOs[0].getPk_contract());
		ArrayList<Object> result = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm, new ArrayListProcessor());
		if(result != null && result.size() > 0){
			Object[] obj = (Object[]) result.get(0); 
			DZFDateTime tstamp = new DZFDateTime(String.valueOf(obj[0]));
			if(tstamp != null && tstamp.compareTo(confrimVOs[0].getTstamp()) != 0){
				throw new BusinessException("合同号："+confrimVOs[0].getVcontcode()+"数据发生变化，请刷新界面后，再次尝试");
			}
		}
	}

}
