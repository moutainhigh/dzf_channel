package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.channel.report.LossCustomerVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.ILossCustomerService;

@Service("losscustservice")
public class LossCustomerServiceImpl extends DataCommonRepImpl implements ILossCustomerService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public  List<LossCustomerVO> query(QryParamVO pamvo) throws DZFWarpException {

		// 过滤出符合条件的加盟商信息
		HashMap<String, DataVO> map = queryCorps(pamvo, LossCustomerVO.class, 2);
		List<String> corplist = null;
		if (map != null && !map.isEmpty()) {
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}
		//String[] clist = QueryUtil.getPagedPKs(corplist.toArray(new String[corplist.size()]),pamvo.getPage(),pamvo.getRows());
		List<LossCustomerVO> list = queryLossCust(pamvo,corplist,2);
		
		//List<LossCustomerVO> retlist = new ArrayList<LossCustomerVO>();
		if (corplist != null && corplist.size() > 0) {
			CorpVO corpvo = null;
			//LossCustomerVO retcorp = null;
			LossCustomerVO showvo = null;
			for (LossCustomerVO retcorp : list) {
				//System.out.println("pk_corp: "+pk_corp);
				if(retcorp!=null){
					corpvo = CorpCache.getInstance().get(null, retcorp.getPk_corp());
					if(corpvo!=null){
						retcorp.setCorpname(corpvo.getUnitname());//加盟商名称
						retcorp.setVprovname(corpvo.getCitycounty());//省市
					}
					showvo = (LossCustomerVO) map.get(retcorp.getPk_corp());
					if (showvo != null) {
						retcorp.setAreaname(showvo.getAreaname());// 大区
						retcorp.setCusername(showvo.getCusername());// 会计运营
					}
					if(!StringUtil.isEmpty(retcorp.getChargedeptname())){
						if("小规模纳税人".equals(retcorp.getChargedeptname())){
							retcorp.setChargedeptname("小规模");
						}else if("一般纳税人".equals(retcorp.getChargedeptname())){
							retcorp.setChargedeptname("一般人");
						}
					}
				}
			}
			return list;
		}else{
			return null;
		}
		
	}

	private List<LossCustomerVO> queryLossCust(QryParamVO pamvo, List<String> corplist, int type) throws DZFWarpException{
		
		//Map<String , LossCustomerVO> retMap = new HashMap<String, LossCustomerVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("    select \n");
		sql.append("      co.fathercorp pk_corp,co.pk_corp pk_corpk,co.unitname corpkname,co.chargedeptname, nvl(co.sealeddate,'1970-01-01') stopdatetime, \n");
		sql.append("      co.def17 stopreason,us.user_name stopname, \n");
		sql.append("      substr(co.begindate,1,7) jzdate, \n");
		sql.append("      case when jz.isqjsyjz ='Y' and jz.period is not null then jz.period||'已关账' end isClose \n");
		sql.append("      from bd_corp co \n");
		sql.append("      left join sm_user us on us.cuserid = co.approve_user \n");
		sql.append("      left join ynt_qmcl jz on jz.pk_corp = co.pk_corp \n");
		sql.append("      where nvl(co.dr, 0) = 0 \n");
		sql.append("       and nvl(us.dr, 0) = 0 \n");
		sql.append("       and nvl(jz.dr, 0) = 0 \n");
		sql.append("       and co.isseal = 'Y' \n");
		sql.append("       and nvl(co.isaccountcorp,'N')= 'N' \n");
		//sql.append("       and co.sealeddate is not null \n");
		if (!StringUtil.isEmpty(pamvo.getBeginperiod())) {
			sql.append("  and substr(co.sealeddate, 1, 7) >= ? \n"); //开始期间
			spm.addParam(pamvo.getBeginperiod());
		}
		if (!StringUtil.isEmpty(pamvo.getEndperiod())) {
			sql.append("  and substr(co.sealeddate, 1, 7) <= ? \n"); //结束期间
			spm.addParam(pamvo.getEndperiod());
		}
		if (!StringUtil.isEmpty(pamvo.getVbillcode())) {
			sql.append(" and co.def17 like ? "); //停用原因
			spm.addParam("%"+pamvo.getVbillcode()+"%");
		}
		if (corplist != null && corplist.size() > 0) {
			sql.append(" and "+SqlUtil.buildSqlForIn("co.fathercorp", corplist.toArray(new String[corplist.size()])));
		}
		int qrytype = pamvo.getQrytype() == null ? 0 : pamvo.getQrytype();
		if(qrytype == 1 ){
			sql.append(" and nvl(co.approve_status,1) < 2 ");
		}else if(qrytype == 2 ){
			sql.append(" and co.approve_status = 2 ");
        }
		sql.append("   order by co.fathercorp desc,co.sealeddate desc \n");
		List<LossCustomerVO> list = (List<LossCustomerVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(LossCustomerVO.class));
		
		if(list!=null && list.size()>0){
			QueryDeCodeUtils.decKeyUtils(new String[] { "corpkname","stopname"}, list, 1);
		}
		
		return list;
	}

}
