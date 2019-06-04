package com.dzf.service.branch.reportmanage.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.branch.reportmanage.CompanyDataVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.branch.reportmanage.ICompanyDataService;

@Service("datacompany")
public class CompanyDataServiceImpl implements ICompanyDataService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public List<CompanyDataVO> query(QryParamVO qvo) throws DZFWarpException {
    	if(qvo.getBegdate()==null || qvo.getEnddate()==null){
    		throw new BusinessException("请选择日期");
    	}
		List<CompanyDataVO> retList = new ArrayList<>();
		List<String> pk_corps = new ArrayList<>();
		Map<String, CompanyDataVO> qryAllCorp = qryAllCorp(qvo,pk_corps);
    	
		if(pk_corps!=null && pk_corps.size()!=0){
			retList = queryCommon(qvo,qryAllCorp,pk_corps);
		}
        return retList;
    }
    
    /**
     * 查询所有，有权限客户
     * @param qvo
     * @param pk_corps
     * @return
     */
 	private Map<String, CompanyDataVO> qryAllCorp(QryParamVO qvo,List<String> pk_corps) {
 		Map<String, CompanyDataVO> map = new LinkedHashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select bs.vname branchname, ac.pk_corp, ac.innercode, ac.unitname corpname ");
		sql.append("  from br_branchcorp bc ");
		sql.append("  left join br_branchset bs on bc.pk_branchset = bs.pk_branchset ");
		sql.append("  left join bd_account ac on bc.pk_corp = ac.pk_corp ");
		sql.append(" where nvl(bc.dr, 0) = 0 ");
		sql.append("   and nvl(bs.dr, 0) = 0 ");
		sql.append("   and nvl(ac.dr, 0) = 0 ");
		sql.append("   and nvl(bc.isseal, 'N') = 'N' ");
		if(!StringUtil.isEmpty(qvo.getPk_bill())){
			sql.append(" and  bs.pk_branchset = ? ");
			sp.addParam(qvo.getPk_bill());
		}
		sql.append("   and exists (select ub.pk_branchset ");
		sql.append("          from br_user_branch ub ");
		sql.append("         where nvl(ub.dr, 0) = 0 ");
		sql.append("           and ub.cuserid = ? ");
		sql.append("           and ub.pk_branchset = bs.pk_branchset) ");
		sp.addParam(qvo.getCuserid());
		
		List<CompanyDataVO> list =(List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(CompanyDataVO.class));
		if(list!=null && list.size()>0){
			String corpName;
			for (CompanyDataVO companyDataVO : list) {
				corpName = CodeUtils1.deCode(companyDataVO.getCorpname());
				if(StringUtil.isEmpty(qvo.getCorpname()) || corpName.indexOf(qvo.getCorpname()) != -1){
					companyDataVO.setCorpname(corpName);
					pk_corps.add(companyDataVO.getPk_corp());
					map.put(companyDataVO.getPk_corp(), companyDataVO);
				}
			}
		}
		return map;
 	}

	/**
	 * 查询报表 基本数据
	 * @param qvo
	 * @param map
	 * @param pk_corps
	 * @return
	 */
	private List<CompanyDataVO> queryCommon(QryParamVO qvo, Map<String, CompanyDataVO> map, List<String> pk_corps) {
		CompanyDataVO getVO;
		String[] pks = pk_corps.toArray(new String[pk_corps.size()]);

		StringBuffer sql = new StringBuffer();// 现有总客户数
		sql.append("select count(p.pk_corp) allcorp, p.fathercorp pk_corp ");
		sql.append("  from bd_corp p ");
		sql.append(" where nvl(p.dr, 0) = 0 ");
		sql.append("   and nvl(p.isseal, 'N') = 'N' ");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
		sql.append("   and nvl(p.isformal, 'N') = 'Y' ");
		sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
		sql.append(" group by p.fathercorp ");
		List<CompanyDataVO> allCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(CompanyDataVO.class));
		if (allCorps != null && allCorps.size() > 0) {
			for (CompanyDataVO companyDataVO : allCorps) {
				getVO = map.get(companyDataVO.getPk_corp());
				getVO.setAllcorp(companyDataVO.getAllcorp());
			}
		}

		sql = new StringBuffer();// 建账客户数
		sql.append("select count(p.pk_corp) ybrcorp, p.fathercorp pk_corp, p.chargedeptname corpname ");
		sql.append("  from bd_corp p ");
		sql.append(" where nvl(p.dr, 0) = 0 ");
		sql.append("   and nvl(p.isseal, 'N') = 'N' ");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
		sql.append("   and p.begindate is not null ");
		sql.append("   and p.chargedeptname is not null ");
		sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
		sql.append(" group by p.fathercorp, p.chargedeptname ");
		List<CompanyDataVO> jzCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(CompanyDataVO.class));

		if (jzCorps != null && jzCorps.size() > 0) {
			for (CompanyDataVO companyDataVO : jzCorps) {
				getVO = map.get(companyDataVO.getPk_corp());
				if("小规模纳税人".equals(companyDataVO.getCorpname())){
					getVO.setXgmcorp(companyDataVO.getYbrcorp());
				}else if("一般纳税人".equals(companyDataVO.getCorpname())){
					getVO.setYbrcorp(companyDataVO.getYbrcorp());
				}
			}
		}
		
		sql = new StringBuffer();// 新增客户数
		SQLParameter spm = new SQLParameter();
		spm.addParam(qvo.getBegdate());
		spm.addParam(qvo.getEnddate());
		
		sql.append("select count(p.pk_corp) addcorp, p.fathercorp pk_corp ");
		sql.append("  from bd_corp p ");
		sql.append(" where nvl(p.dr,0) = 0 ");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
		sql.append("   and substr(createdate, 0, 7) >= ? ");
		sql.append("   and substr(createdate, 0, 7) <= ? ");
		sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
		sql.append(" group by p.fathercorp ");
		List<CompanyDataVO> addCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CompanyDataVO.class));
		if (addCorps != null && addCorps.size() > 0) {
			for (CompanyDataVO companyDataVO : addCorps) {
				getVO = map.get(companyDataVO.getPk_corp());
				getVO.setAddcorp(companyDataVO.getAddcorp());
			}
		}
		
		sql = new StringBuffer();// 流失客户数
		sql.append("select count(p.pk_corp) losecorp, p.fathercorp pk_corp ");
		sql.append("  from bd_corp p ");
		sql.append(" where nvl(p.dr,0) = 0 ");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'N' ");
		sql.append("   and nvl(p.isseal, 'N') = 'Y' ");
		sql.append("   and substr(p.sealeddate, 0, 7) >= ? ");
		sql.append("   and substr(p.sealeddate, 0, 7) <= ? ");
		sql.append(" and ").append(SqlUtil.buildSqlForIn("p.fathercorp ", pks));
		sql.append(" group by p.fathercorp ");
		List<CompanyDataVO> loseCorps = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CompanyDataVO.class));
		if (loseCorps != null && loseCorps.size() > 0) {
			for (CompanyDataVO companyDataVO : loseCorps) {
				getVO = map.get(companyDataVO.getPk_corp());
				 getVO.setLosecorp(companyDataVO.getLosecorp());
			}
		}
		
		sql = new StringBuffer();// 合同
		sql.append("select count(t.pk_contract) contcorp, ");
		sql.append("       sum(t.ntotalmny) totalmny, ");
		sql.append("       sum(w.nunyhys) ysmny, ");
		sql.append("       sum(w.wsreceivemny) wsmny, ");
		sql.append("       t.pk_corp ");
		sql.append("  from ynt_contract t ");
		sql.append("  left join (select distinct b.pk_contract, ");
		sql.append("                             sum(nvl(b.nunyhys, 0)) nunyhys, ");
		sql.append("                             sum(nvl(b.wsreceivemny, 0)) wsreceivemny ");
		sql.append("               from ynt_contract_b b ");
		sql.append("              where nvl(b.dr, 0) = 0 ");
		sql.append("              group by b.pk_contract) w on t.pk_contract = w.pk_contract ");
		sql.append(" where nvl(t.dr, 0) = 0 ");
		sql.append("   and nvl(t.isflag, 'N') = 'Y' ");
		sql.append("   and t.doperatedate >= ? ");
		sql.append("   and t.doperatedate <= ? ");
		sql.append(" and ").append(SqlUtil.buildSqlForIn("t.pk_corp ", pks));
		sql.append(" group by pk_corp ");

		List<CompanyDataVO> contracts = (List<CompanyDataVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CompanyDataVO.class));
		if (contracts != null && contracts.size() > 0) {
			for (CompanyDataVO companyDataVO : contracts) {
				getVO = map.get(companyDataVO.getPk_corp());
				getVO.setContcorp(companyDataVO.getContcorp());
				getVO.setTotalmny(companyDataVO.getTotalmny());
				getVO.setYsmny(companyDataVO.getYsmny());
				getVO.setWsmny(companyDataVO.getWsmny());
			}
		}
		return new ArrayList<CompanyDataVO>(map.values());
	}

}
