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
import com.dzf.model.branch.reportmanage.QueryContractVO;
import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.branch.setup.UserBranchVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.branch.reportmanage.IBranchExpireContractService;

@Service("contractexp")
public class BranchExpireContractServiceImpl implements IBranchExpireContractService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<QueryContractVO> query(QueryContractVO qvo, UserVO uservo) throws DZFWarpException {
		List<String> setids = new ArrayList<>();
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo, uservo, setids);
		if (StringUtil.isEmpty(sqpvo.getSql())) {
			return null;
		}
		List<QueryContractVO> list = (List<QueryContractVO>) singleObjectBO.executeQuery(sqpvo.getSql(), sqpvo.getSpm(),
				new BeanListProcessor(QueryContractVO.class));

		String[] split = qvo.getQjq().toString().split("-");
		Integer year = new Integer(Integer.parseInt(split[0]));
		Integer month = new Integer(Integer.parseInt(split[1]));
		Integer nextmonth = month + 1;
		Integer nextyear = year + 1;
		if (month < 12) {
			if (nextmonth.toString().length() > 1) {
				qvo.setNextqjq(year + "-" + nextmonth);
			} else {
				qvo.setNextqjq(year + "-" + "0" + nextmonth);
			}
		} else {
			qvo.setNextqjq(nextyear + "-01");
		}

		List<String> corplist = new ArrayList<String>();
		Boolean b = false;
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, list, 1);

			for (int i = 0; i < list.size(); i++) {

				if (list.get(i).getCorpids() != null && list.get(i).getCorpids().length() > 0) {
					corplist = Arrays.asList(list.get(i).getCorpids().split(","));
					String condition = SqlUtil.buildSqlForIn("con.pk_corpk",
							corplist.toArray(new String[corplist.size()]));
					String setcondition = SqlUtil.buildSqlForIn("bc.pk_branchset",
							setids.toArray(new String[setids.size()]));

					
					if ((!StringUtils.isEmpty(list.get(i).getUnitname())
							&& list.get(i).getUnitname().indexOf(qvo.getUnitname()) >= 0)) {
						b = true;
						//Integer count = 0;
						StringBuffer sql = new StringBuffer();
						//StringBuffer ssql = new StringBuffer();
						StringBuffer qsql = new StringBuffer();
						StringBuffer querysql = new StringBuffer();
						SQLParameter queryspm = new SQLParameter();
						SQLParameter qspm = new SQLParameter();
						queryspm.addParam(qvo.getQjq());
						queryspm.addParam(list.get(i).getPk_corp());
						qspm.addParam(list.get(i).getPk_corp());
						qspm.addParam(qvo.getNextqjq());

						sql.append("  select  \n");
						sql.append("     nvl(sum(case when corp.isseal = 'Y' and substr(con.denddate, 1, 7) = ? then 1 else 0 end),0) losscorpnum \n");
						//querysql.append("     nvl(sum(distinct case when substr(con.dbegindate, 1, 7) >= ? then 1 else 0 end),0) signednum,  \n");
						//sql.append("     con.pk_corp \n");
						querysql.append("     from ynt_contract con left join bd_corp corp on  \n");
						querysql.append("     con.pk_corpk  = corp.pk_corp \n");
						querysql.append("     left join br_branchcorp bc on \n");
						querysql.append("     con.pk_corp = bc.pk_corp \n");
						querysql.append("     where nvl(con.dr,0) = 0 \n");
						querysql.append("     and nvl(corp.dr,0) = 0 \n");
						querysql.append("     and nvl(bc.dr,0) = 0 \n");
						querysql.append("     and con.isflag = 'Y' \n");
						querysql.append("     and corp.isaccountcorp = 'N'   \n");
						querysql.append("     and con.icosttype = 0  \n");
						querysql.append("     and con.vstatus in (1,3,4) \n");
						querysql.append("     and con.pk_corp = ? \n");
						querysql.append("     and " + condition + "\n");
						querysql.append("     and " + setcondition + "\n");
						//ssql.append("     group by con.pk_corp \n");
						//ssql.append("     order by con.pk_corp \n");

						QueryContractVO cvo = (QueryContractVO) singleObjectBO.executeQuery(sql.toString()+querysql.toString(),
								queryspm, new BeanProcessor(QueryContractVO.class));
						
						qsql.append("  select  \n");
						qsql.append("    nvl(count(distinct con.pk_corpk),0) signednum  \n");
						
						QueryContractVO qcvo = (QueryContractVO) singleObjectBO.executeQuery(qsql.toString()+querysql.toString()+
								" and substr(con.dbegindate, 1, 7) >= ?", qspm, new BeanProcessor(QueryContractVO.class));
						
                        if(cvo!=null && qcvo!=null){
                        	if(list.get(i).getExpirenum()<qcvo.getSignednum()){
                        		list.get(i).setSignednum(list.get(i).getExpirenum());// 已续签合同数
                        	}else{
                        		list.get(i).setSignednum(qcvo.getSignednum());
                        	}
                        	list.get(i).setUnsignednum(list.get(i).getExpirenum() - list.get(i).getSignednum());// 未续签合同数
                        	list.get(i).setLosscorpnum(cvo.getLosscorpnum());//流失客户数
                        }
					} else {
						list.remove(i);
					}
				}
				if (!b) {
					return new ArrayList<QueryContractVO>();
				}
			}
			
		}
		return list;
	}

	/**
	 * 获取查询条件
	 * 
	 * @param qvo
	 * @param uservo
	 * @param setids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private QrySqlSpmVO getQrySqlSpm(QueryContractVO qvo, UserVO uservo, List<String> setids) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(qvo.getQjq());

		String usql = "select pk_branchset from br_user_branch ub" + " where nvl(dr,0) = 0 " + " and cuserid = ? ";
		SQLParameter uspm = new SQLParameter();
		uspm.addParam(uservo.getCuserid());

		StringBuffer condition = new StringBuffer();

		List<UserBranchVO> mvolist = (List<UserBranchVO>) singleObjectBO.executeQuery(usql, uspm,
				new BeanListProcessor(UserBranchVO.class));
		if (mvolist != null && mvolist.size() > 0) {
			for (UserBranchVO mvo : mvolist) {
				setids.add(mvo.getPk_branchset());
			}
		} else {
			return qryvo;
		}
		if (setids != null && setids.size() > 0) {
			condition.append(SqlUtil.buildSqlForIn("bc.pk_branchset", setids.toArray(new String[setids.size()])));
		}

		sql.append("select  \n");
		//sql.append("    nvl(sum(case when corp.isseal = 'Y' then 1 else 0 end),0) losscorpnum,  \n");
		sql.append("	wmsys.wm_concat(distinct con.pk_corpk) corpids, \n");
		sql.append("	con.pk_corp,corp.unitname,corp.innercode, \n");
		sql.append("	nvl(count(distinct con.pk_corpk),0) expirenum \n");
		sql.append(" 	from ynt_contract con left join bd_corp corp on \n");
		sql.append("	con.pk_corp  = corp.pk_corp   \n");
		sql.append("    left join br_branchcorp bc on \n");
		sql.append("    con.pk_corp = bc.pk_corp \n");
		sql.append("	where nvl(con.dr,0) = 0   \n");
		sql.append("	and nvl(corp.dr,0) = 0   \n");
		sql.append("	and nvl(bc.dr,0) = 0   \n");
		sql.append("	and con.isflag = 'Y'  \n");
		sql.append("	and corp.isaccountcorp = 'Y'   \n");
		sql.append(" 	and substr(con.denddate, 1, 7) = ?  \n");
		sql.append(" 	and con.icosttype = 0 \n");
		sql.append(" 	and con.vstatus in (1,3,4) \n");
		if (!StringUtil.isEmpty(condition.toString())) {
			sql.append("  and " + condition.toString());
		}

		if (!StringUtil.isEmpty(qvo.getInnercode())) {
			sql.append(" AND corp.innercode like ? ");
			spm.addParam("%" + qvo.getInnercode() + "%");
		}
		
		if (!StringUtil.isEmpty(qvo.getPk_branchset())) {
			sql.append(" AND bc.pk_branchset = ? ");
			spm.addParam(qvo.getPk_branchset());
		}

		sql.append("	group by con.pk_corp,corp.unitname,corp.innercode \n");
		sql.append("	order by con.pk_corp \n");

		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<BranchInstSetupVO> queryComboBox(UserVO uservo) {
		
		StringBuffer qsql = new StringBuffer();
		SQLParameter qspm = new SQLParameter();
		qspm.addParam(uservo.getCuserid());
		qsql.append("  select \n");
		qsql.append("    wmsys.wm_concat(pk_branchset) pk_branchset \n");
		qsql.append("    from  br_user_branch  \n");
		qsql.append("    where nvl(dr,0) = 0 and \n");
		qsql.append("    cuserid = ? \n");
		BranchInstSetupVO vo= (BranchInstSetupVO) singleObjectBO.executeQuery(qsql.toString(), qspm, new BeanProcessor(BranchInstSetupVO.class));
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pk_branchset,vname  \n");
		sql.append("  FROM br_branchset  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		if(vo.getPk_branchset()!=null){
			List<String> asList = Arrays.asList(vo.getPk_branchset().split(","));
			String condition = SqlUtil.buildSqlForIn("pk_branchset",
					asList.toArray(new String[asList.size()]));
			sql.append("  and "+condition +"\n");
			sql.append(" order by ts desc \n");
			return (List<BranchInstSetupVO>) singleObjectBO.executeQuery(sql.toString(), null,
					new BeanListProcessor(BranchInstSetupVO.class));
		}else{
			return null;
		}
		
		
	}
}
