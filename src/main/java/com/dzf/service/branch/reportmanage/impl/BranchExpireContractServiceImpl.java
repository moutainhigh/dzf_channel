package com.dzf.service.branch.reportmanage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.branch.reportmanage.QueryContractVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.service.branch.reportmanage.IBranchExpireContractService;

@Service("contractexp")
public class BranchExpireContractServiceImpl implements IBranchExpireContractService{

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Override
	public int queryTotalRow(QueryContractVO qvo)  throws DZFWarpException{
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	     return multBodyObjectBO.queryDataTotal(QueryContractVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<QueryContractVO> query(QueryContractVO qvo,UserVO uservo)  throws DZFWarpException{
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	        List<QueryContractVO> list = (List<QueryContractVO>) multBodyObjectBO.queryDataPage(QueryContractVO.class, sqpvo.getSql(),
	                sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
	       if(list!=null && list.size()>0){
		        QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, list, 1);  
	       }
	       return list;
	}
	
	/**
	 * 获取查询条件
	 * @param qvo
	 * @return
	 */
	private QrySqlSpmVO getQrySqlSpm(QueryContractVO qvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(qvo.getQjq());
		spm.addParam(qvo.getQjq());
		spm.addParam(qvo.getQjq());
		spm.addParam(qvo.getQjq());
		sql.append("select \n");
		sql.append("	nvl(sum(case when substr(con.denddate, 1, 7) = ? and con.vstatus = 8 and con.icosttype = 0 then 1 else 0 end),0) expirenum,   \n");
		sql.append("	nvl(sum(case when substr(con.denddate, 1, 7) = ? and con.isxq ='Y' and con.icosttype = 0 then 1 else 0 end),0) signednum,  \n");
		sql.append("	nvl(sum(case when substr(con.denddate, 1, 7) = ? and con.isxq ='N' and con.icosttype = 0 then 1 else 0 end),0) unsignednum,  \n");
		sql.append("	nvl(sum(case when substr(con.denddate, 1, 7) = ? and corp.isseal = 'Y' and con.icosttype = 0 then 1 else 0 end),0) losscorpnum,  \n");
		sql.append("	corp.pk_corp,   \n");
		sql.append("	corp.unitname,corp.unitcode  \n");
		sql.append("	from ynt_contract con left join bd_corp corp on  \n");
		sql.append("	con.pk_corp  = corp.pk_corp  \n");
		sql.append("	where nvl(con.dr,0) = 0 and  \n");
		sql.append("	nvl(corp.dr,0) = 0   \n");
		sql.append("	and corp.isaccountcorp = 'Y'  \n");
		sql.append("	group by con.pk_contract,   \n");
		sql.append("	corp.unitname,corp.unitcode  \n");
		if (!StringUtil.isEmpty(qvo.getUnitname())) {
			sql.append(" AND unitname like ? ");
			spm.addParam("%" + qvo.getUnitname() + "%");
		}
		if (!StringUtil.isEmpty(qvo.getUnitcode())) {
			sql.append(" AND unitcode like ? ");
			spm.addParam("%" + qvo.getUnitcode() + "%");
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
}
