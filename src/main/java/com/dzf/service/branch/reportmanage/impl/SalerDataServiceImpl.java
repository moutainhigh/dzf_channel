package com.dzf.service.branch.reportmanage.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnListProcessor;
import com.dzf.model.branch.reportmanage.CompanyDataVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.branch.reportmanage.ISalerDataService;

@Service("datasaler")
public class SalerDataServiceImpl implements ISalerDataService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public List<CompanyDataVO> query(QryParamVO qvo) throws DZFWarpException {
    	if(qvo.getBegdate()==null || qvo.getEnddate()==null){
    		throw new BusinessException("请选择日期");
    	}
		List<CompanyDataVO> retList = new ArrayList<>();
		Integer qryLevel = qryLevel(qvo.getCuserid());
		if(qryLevel>0){
	    	List<String> qryCorpIds = qryCorpIds(qvo);
			if(qryCorpIds!=null && qryCorpIds.size()!=0){
				qvo.setIpaytype(qryLevel);
				retList = queryCommon(qvo,qryCorpIds);
			}
		}
        return retList;
    }
    
    private List<CompanyDataVO> queryCommon(QryParamVO qvo, List<String> qryCorpIds) {
    	List<CompanyDataVO> retList = new ArrayList<>();
		String[] pks = qryCorpIds.toArray(new String[qryCorpIds.size()]);
		LinkedHashMap<String,CompanyDataVO> map = new LinkedHashMap<>();
		
		StringBuffer sql = new StringBuffer();// 现有总客户数
		SQLParameter sp = new SQLParameter();
		sql.append("select count(p.pk_corp) allcorp,nvl(p.foreignname,'null') branchname");
		sql.append("  from bd_corp p ");
		sql.append(" where nvl(p.dr, 0) = 0 ");
		sql.append("   and nvl(p.isformal, 'N') = 'Y' ");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
		sql.append("   and nvl(p.isseal, 'N') = 'N' ");
		if(qvo.getIpaytype()==1){
			sql.append(" and p.foreignname = ? ");
			sp.addParam(qvo.getUser_name());
		}
		if(!StringUtil.isEmpty(qvo.getCorpkcode())){
			sql.append(" and p.foreignname like ? ");
			sp.addParam("%"+qvo.getCorpkcode()+"%");
		}
		sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
		sql.append(" group by  p.foreignname ");
		List<CompanyDataVO> allCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(CompanyDataVO.class));
		if (allCorps != null && allCorps.size() > 0) {
			for (CompanyDataVO companyDataVO : allCorps) {
				map.put(companyDataVO.getBranchname(), companyDataVO);
			}
		}
		if(!map.isEmpty()){
			CompanyDataVO getVO;
			
			sql = new StringBuffer();// 未建账客户数
			sql.append("select count(p.pk_corp) wjzcorp,nvl(p.foreignname,'null') branchname");
			sql.append("  from bd_corp p ");
			sql.append(" where nvl(p.dr, 0) = 0 ");
			sql.append("   and nvl(p.isformal, 'N') = 'Y' ");
			sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
			sql.append("   and nvl(p.isseal, 'N') = 'N' ");
			sql.append("   and p.begindate is null ");
//			sql.append("   and p.chargedeptname is not null ");
			sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
			sql.append(" group by p.foreignname ");
			List<CompanyDataVO> wjzCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), null,
					new BeanListProcessor(CompanyDataVO.class));
			if (wjzCorps != null && wjzCorps.size() > 0) {
				for (CompanyDataVO companyDataVO : wjzCorps) {
					getVO = map.get(companyDataVO.getBranchname());
					getVO.setWjzcorp(companyDataVO.getWjzcorp());
				}
			}
			
			sql = new StringBuffer();// 建账客户数
			sql.append("select count(p.pk_corp) ybrcorp, nvl(p.foreignname,'null') branchname, p.chargedeptname corpname ");
			sql.append("  from bd_corp p ");
			sql.append(" where nvl(p.dr, 0) = 0 ");
			sql.append("   and nvl(p.isformal, 'N') = 'Y' ");
			sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
			sql.append("   and nvl(p.isseal, 'N') = 'N' ");
			sql.append("   and p.begindate is not null ");
			sql.append("   and p.chargedeptname is not null ");
			sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
			sql.append(" group by p.foreignname, p.chargedeptname ");
			List<CompanyDataVO> jzCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), null,
					new BeanListProcessor(CompanyDataVO.class));

			if (jzCorps != null && jzCorps.size() > 0) {
				for (CompanyDataVO companyDataVO : jzCorps) {
					getVO = map.get(companyDataVO.getBranchname());
					if(getVO == null){
						continue;
					}else if("小规模纳税人".equals(companyDataVO.getCorpname())){
						getVO.setXgmcorp(companyDataVO.getYbrcorp());
					}else if("一般纳税人".equals(companyDataVO.getCorpname())){
						getVO.setYbrcorp(companyDataVO.getYbrcorp());
					}
				}
			}
			
			sql = new StringBuffer();// 新增客户数
			sp = new SQLParameter();
			sp.addParam(qvo.getBegdate());
			sp.addParam(qvo.getEnddate());
			
			sql.append("select count(p.pk_corp) addcorp, nvl(p.foreignname,'null') branchname ");
			sql.append("  from bd_corp p ");
			sql.append(" where nvl(p.dr,0) = 0 ");
			sql.append("   and nvl(p.isformal, 'N') = 'Y' ");
			sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
			sql.append("   and createdate >= ? ");
			sql.append("   and createdate <= ? ");
			sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
			sql.append(" group by p.foreignname ");
			List<CompanyDataVO> addCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), sp,
					new BeanListProcessor(CompanyDataVO.class));
			if (addCorps != null && addCorps.size() > 0) {
				for (CompanyDataVO companyDataVO : addCorps) {
					getVO = map.get(companyDataVO.getBranchname());
					if(getVO == null){
						continue;
					}else{
						getVO.setAddcorp(companyDataVO.getAddcorp());
					}
				}
			}
			
			sql = new StringBuffer();// 流失客户数
			sql.append("select count(p.pk_corp) losecorp, nvl(p.foreignname,'null') branchname ");
			sql.append("  from bd_corp p ");
			sql.append(" where nvl(p.dr,0) = 0 ");
			sql.append("   and nvl(p.isformal, 'N') = 'Y' ");
			sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
			sql.append("   and nvl(p.isseal, 'N') = 'Y' ");
			sql.append("   and p.sealeddate >= ? ");
			sql.append("   and p.sealeddate <= ? ");
			sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
			sql.append(" group by p.foreignname ");
			List<CompanyDataVO> loseCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), sp,
					new BeanListProcessor(CompanyDataVO.class));
			if (loseCorps != null && loseCorps.size() > 0) {
				for (CompanyDataVO companyDataVO : loseCorps) {
					getVO = map.get(companyDataVO.getBranchname());
					if(getVO == null){
						continue;
					}else{
						getVO.setLosecorp(companyDataVO.getLosecorp());
					}
				}
			}
			
			sql = new StringBuffer();// 合同
			sql.append("select count(t.pk_contract) contcorp, ");
			sql.append("       sum(t.ntotalmny) totalmny, ");
			sql.append("       sum(w.ysreceivemny) ysmny, ");
			sql.append("       sum(w.wsreceivemny) wsmny, ");
			sql.append("       nvl(p.foreignname,'null') branchname ");
			sql.append("  from ynt_contract t ");
			sql.append("  left join (select distinct b.pk_contract, ");
			sql.append("                             sum(nvl(b.ysreceivemny, 0)) ysreceivemny, ");
			sql.append("                             sum(nvl(b.wsreceivemny, 0)) wsreceivemny ");
			sql.append("               from ynt_contract_b b ");
			sql.append("              where nvl(b.dr, 0) = 0 ");
			sql.append("              group by b.pk_contract) w on t.pk_contract = w.pk_contract ");
			sql.append("  left join bd_corp p on t.pk_corpk=p.pk_corp ");
			sql.append(" where nvl(t.dr, 0) = 0 ");
			sql.append("   and nvl(t.isflag, 'N') = 'Y' ");
			sql.append("   and nvl(t.icosttype, 0) = 0 ");
			sql.append("   and nvl(p.dr, 0) = 0 ");
			sql.append("   and nvl(p.isformal, 'N') = 'Y' ");
			sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
			sql.append("   and p.createdate >= ? ");
			sql.append("   and p.createdate <= ? ");
			sql.append(" and ").append(SqlUtil.buildSqlForIn("t.pk_corp ", pks));
			sql.append(" group by p.foreignname  ");

			List<CompanyDataVO> contracts = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), sp,
					new BeanListProcessor(CompanyDataVO.class));
			if (contracts != null && contracts.size() > 0) {
				for (CompanyDataVO companyDataVO : contracts) {
					getVO = map.get(companyDataVO.getBranchname());
					if(getVO == null){
						continue;
					}else{
						getVO.setContcorp(companyDataVO.getContcorp());
						getVO.setTotalmny(companyDataVO.getTotalmny());
						getVO.setYsmny(companyDataVO.getYsmny());
						getVO.setWsmny(companyDataVO.getWsmny());
					}
				}
			}
			retList = new ArrayList<CompanyDataVO>(map.values());
		}
		return retList;
	}
    
    private Integer qryLevel(String cuserid){
    	Integer level = 0;
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select r.role_name ");
		sql.append("  from sm_user_role ur ");
		sql.append("  left join sm_user u on ur.cuserid = u.cuserid ");
		sql.append("  left join sm_role r on ur.pk_role = r.pk_role ");
		sql.append(" where ur.cuserid = ? and nvl(ur.dr,0)=0 and nvl(u.dr,0)=0 ");
		sp.addParam(cuserid);
		List<String> names = (List<String>)singleObjectBO.executeQuery(sql.toString(), sp,new ColumnListProcessor("role_name"));
		if(names!=null && names.size()>0){
			if(names.contains("统计管理岗")){
				level = 2;
			}else if(names.contains("销售岗")){
				level = 1;
			}
		}
		return level;
    }

	/**
     * 查询有权限的代账公司id
     * @param qvo
     * @return
     */
    private List<String> qryCorpIds(QryParamVO qvo){
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select ac.pk_corp");
		sql.append("  from br_branchcorp bc ");
		sql.append("  left join br_branchset bs on bc.pk_branchset = bs.pk_branchset ");
		sql.append("  left join bd_account ac on bc.pk_corp = ac.pk_corp ");
		sql.append(" where nvl(bc.dr, 0) = 0 ");
		sql.append("   and nvl(bs.dr, 0) = 0 ");
		sql.append("   and nvl(ac.dr, 0) = 0 ");
		sql.append("   and nvl(bc.isseal, 'N') = 'N' ");
		sql.append("   and exists (select ub.pk_branchset ");
		sql.append("          from br_user_branch ub ");
		sql.append("         where nvl(ub.dr, 0) = 0 ");
		sql.append("           and ub.cuserid = ? ");
		sql.append("           and ub.pk_branchset = bs.pk_branchset) ");
		sp.addParam(qvo.getCuserid());
    	return (List<String>)singleObjectBO.executeQuery(sql.toString(), sp,new ColumnListProcessor("pk_corp"));
    }

}
