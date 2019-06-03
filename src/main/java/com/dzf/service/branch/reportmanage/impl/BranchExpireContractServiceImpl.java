package com.dzf.service.branch.reportmanage.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
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
		 List<QueryContractVO> list = (List<QueryContractVO>) singleObjectBO.executeQuery(sqpvo.getSql(), sqpvo.getSpm(), new BeanListProcessor(QueryContractVO.class));
	      
    		String[] split = qvo.getQjq().toString().split("-");
    		Integer year = new Integer(Integer.parseInt(split[0]));
    		Integer month = new Integer(Integer.parseInt(split[1]));
    		Integer nextmonth = month+1;
    		Integer nextyear = year+1;
    		if(month<12){
    			if(nextmonth.toString().length()>1){
        			qvo.setNextqjq(year+"-"+nextmonth);
        		}else{
        			qvo.setNextqjq(year+"-"+"0"+nextmonth);
        		}
    		}else{
    			qvo.setNextqjq(nextyear+"-01");
    		}
    		
            
    		List<String> corplist = new ArrayList<String>();
    		Boolean b = false;
	        if(list!=null && list.size()>0){
	        	QueryDeCodeUtils.decKeyUtils(new String[]{"unitname"}, list, 1);
	        	
	        	for (int i = 0; i < list.size(); i++) {
				
	    	    	if(list.get(i).getCorpids()!=null && list.get(i).getCorpids().length()>0){
	    	    		corplist = Arrays.asList(list.get(i).getCorpids().split(","));
	    	    	}
	    	    	
	    	    	if ((!StringUtils.isEmpty(list.get(i).getUnitname()) && list.get(i).getUnitname().indexOf(qvo.getUnitname()) >= 0)){
	    	    		b = true;
	    	    		Integer count = 0;
		    	    	for (String id : corplist) {
		    	    		StringBuffer querysql = new StringBuffer();
			    			SQLParameter queryspm = new SQLParameter();
			    			queryspm.addParam(qvo.getNextqjq());
			    			queryspm.addParam(list.get(i).getPk_corp());
			    			queryspm.addParam(id);
			    			querysql.append("  select sign(count(1)) kcount \n");
			    			querysql.append("    from ynt_contract \n");
			    			querysql.append("    where nvl(dr,0) = 0 \n");
			    			querysql.append("    and substr(dbegindate,1,7) >= ? \n");
			    			querysql.append("    and pk_corp = ? \n");
			    			querysql.append("    and pk_corpk = ? \n");
			    			QueryContractVO cvo = (QueryContractVO) singleObjectBO.executeQuery(querysql.toString(), queryspm, new BeanProcessor(QueryContractVO.class));
			    			count = count + cvo.getKcount();//一个公司下所有客户的合同数
		    	    	}
		    	    	
		    	    	list.get(i).setSignednum(count);//已续签合同数
		    	    	list.get(i).setUnsignednum(list.get(i).getExpirenum()-list.get(i).getSignednum());//未续签合同数
	    	    	}else{
	    	    		list.remove(i);
	    	    	}
	        	}
	    	    if(!b){
	    	    	return new ArrayList<QueryContractVO>();
	    	    }
	    	   
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
		sql.append("select  \n");
		sql.append("    nvl(sum(case when corp.isseal = 'Y' then 1 else 0 end),0) losscorpnum,  \n");
	    sql.append("	wmsys.wm_concat(distinct con.pk_corpk) corpids, \n");
	    sql.append("	corp.pk_corp,corp.unitname,corp.unitcode, \n");
	    sql.append("	count(distinct con.pk_corpk) expirenum \n");
	    sql.append(" 	from ynt_contract con left join bd_corp corp on \n");  
	    sql.append("	con.pk_corp  = corp.pk_corp   \n");
	    sql.append("	where nvl(con.dr,0) = 0   \n");
	    sql.append("	and nvl(corp.dr,0) = 0   \n");
	    sql.append("	and con.isflag = 'Y'  \n");
	    sql.append("	and corp.isaccountcorp = 'Y'   \n");
	    sql.append(" 	and substr(con.denddate, 1, 7) = ?  \n");
	    sql.append(" 	and con.icosttype = 0  \n");
	  
		if (!StringUtil.isEmpty(qvo.getUnitcode())) {
			sql.append(" AND corp.unitcode like ? ");
			spm.addParam("%" + qvo.getUnitcode() + "%");
		}
		
	    sql.append("	group by corp.pk_corp,corp.unitname,corp.unitcode \n");
        sql.append("	order by corp.pk_corp \n");
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
}
