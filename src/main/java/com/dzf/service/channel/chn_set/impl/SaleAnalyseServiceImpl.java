package com.dzf.service.channel.chn_set.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.sale.SaleAnalyseVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.service.channel.chn_set.ISaleAnalyseService;

@Service("sale_analyse")
public class SaleAnalyseServiceImpl implements ISaleAnalyseService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<SaleAnalyseVO> query(SaleAnalyseVO qvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select account.pk_corp,b.vprovname,b.vprovince,c.areaname");
		sql.append("   from bd_account account");
		sql.append("   left join (select distinct pk_chnarea, vprovname, vprovince");
		sql.append("                from cn_chnarea_b");
		sql.append("               where type = 1");
		sql.append("                 and nvl(dr, 0) = 0) b on account.vprovince = b.vprovince");
		sql.append("   left join cn_chnarea c on b.pk_chnarea = c.pk_chnarea");
		sql.append("                         and c.type = 1");
		sql.append("  where nvl(account.dr, 0) = 0");
		sql.append("    and nvl(c.dr, 0) = 0");
		sql.append("    and nvl(account.isaccountcorp, 'N') = 'Y'");
		sql.append("    and nvl(account.ischannel, 'N') = 'Y'");
		sql.append("    and nvl(account.isseal, 'N') = 'N'");
		sql.append("    and " +QueryUtil.getWhereSql());
		if(!StringUtil.isEmpty(qvo.getAreaname())){
			sql.append(" and c.areaname=? " );   //大区
			sp.addParam(qvo.getAreaname());
		}
		if(qvo.getVprovince() != null && qvo.getVprovince() != -1){
			sql.append(" and account.vprovince=? ");//省市
			sp.addParam(qvo.getVprovince());
		}
		List<SaleAnalyseVO> list =(List<SaleAnalyseVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(SaleAnalyseVO.class));
		
		Map<String,SaleAnalyseVO> visitmap = qryVisitNum(qvo);//1、查询拜访数和拜访客户数
		Map<String,Integer> signmap = qrySignNum(qvo);//2、查询签约客户数
		Map<String,SaleAnalyseVO> nummap = qryContNum(qvo);//3、查询合同数
		Map<String,DZFDouble> chnmap = qryChnContMny(qvo);//4、查询加盟商合同金额
		Map<String,DZFDouble> incmap = qryIncContMny(qvo);//5、查询增值服务合同金额
		
		List<SaleAnalyseVO> retlist = new ArrayList<SaleAnalyseVO>();
		
		SaleAnalyseVO visitvo = null;
		Integer signvo = null;
		SaleAnalyseVO numvo = null;
		DZFDouble chnvo = null;
		DZFDouble incvo = null;
		
		CorpVO cvo = null;
		for (SaleAnalyseVO salevo : list) {
			String pk_corp = salevo.getPk_corp();
			cvo = CorpCache.getInstance().get(null,pk_corp);
			if(cvo!=null){
				salevo.setCorpname(cvo.getUnitname());
				if(!StringUtil.isEmpty(qvo.getCorpname())){
					if(salevo.getCorpname().indexOf(qvo.getCorpname()) == -1){
						continue;
					}
				}
			}
			if(visitmap != null && !visitmap.isEmpty()){
				visitvo=visitmap.get(pk_corp);
				if(visitvo!=null){
					salevo.setIvisitnum(visitvo.getIvisitnum());
					salevo.setIviscustnum(visitvo.getIviscustnum());
				}
			}
			if(signmap != null && !signmap.isEmpty()){
				signvo=signmap.get(pk_corp);
				if(signvo!=null){
					salevo.setIsignnum(signmap.get(pk_corp));
				}
			}
			if(nummap != null && !nummap.isEmpty()){
				numvo = nummap.get(pk_corp);
				if(numvo!=null){
					salevo.setIagentnum(numvo.getIagentnum());
					salevo.setIincrenum(numvo.getIincrenum());
				}
			}
			if(chnmap != null && !chnmap.isEmpty()){
				chnvo= chnmap.get(pk_corp);
				if(chnvo!=null){
					salevo.setContractmny(chnvo);
				}
			}
			if (incmap != null && !incmap.isEmpty()) {
				if (salevo.getContractmny() != null) {
					salevo.setContractmny(SafeCompute.add(salevo.getContractmny(), incmap.get(pk_corp)));
				} else {
					salevo.setContractmny(incmap.get(pk_corp));
				}
			}
			if(salevo.getIsignnum() != null && salevo.getContractmny() != null){
				salevo.setPricemny(salevo.getContractmny().div(new DZFDouble(salevo.getIsignnum())));
			}
			if(visitvo!=null || signvo!=null|| numvo!=null|| chnvo!=null|| incvo!=null){
				retlist.add(salevo);
			}
		}
		return retlist;
	}
	
	/**
	 * 查询拜访数和拜访客户数
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,SaleAnalyseVO> qryVisitNum(SaleAnalyseVO qvo) throws DZFWarpException {
		Map<String,SaleAnalyseVO> nummap = new HashMap<String,SaleAnalyseVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,sum(t.iv1) ivisitnum,sum(t.iv2)iviscustnum from (   ");
		sql.append("SELECT count(y.pk_flowhistory) iv1,   ");
		sql.append("  count(distinct y.pk_customno) iv2,   ");
		sql.append("  y.pk_corp   ");
		sql.append("  FROM ynt_porflwhistory y   ");
		sql.append("  LEFT JOIN ynt_potcus s ON y.pk_customno = s.pk_customno   ");
		sql.append(" WHERE nvl(y.dr, 0) = 0 AND nvl(s.dr, 0) = 0 AND s.ibusitype = 1   ");
		if(qvo.getDbegindate() != null){
			sql.append("   AND y.realfollowdate >= ?   ");
			spm.addParam(qvo.getDbegindate());
		}
		if(qvo.getDenddate() != null){
			sql.append("   AND y.realfollowdate <= ?   ");
			spm.addParam(qvo.getDenddate());
		}
		sql.append("  GROUP BY y.pk_corp,y.followuser )t group by t.pk_corp   ");
		List<SaleAnalyseVO> list = (List<SaleAnalyseVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(SaleAnalyseVO.class));
		if(list != null && list.size() > 0){
			for(SaleAnalyseVO vo : list){
				nummap.put(vo.getPk_corp(), vo);
			}
		}
		return nummap;
	}
	
	/**
	 * 查询签约客户数
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Integer> qrySignNum(SaleAnalyseVO qvo) throws DZFWarpException {
		Map<String,Integer> signmap = new HashMap<String,Integer>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" SELECT count(t.pk_customno) isignnum,t.pk_corp   ") ;
		sql.append(" FROM ynt_potcus t   ") ; 
		sql.append("WHERE nvl(t.ibusitype, 0) = 1   ") ; 
		sql.append("  AND t.irecestatus IN (2, 4)   ") ; 
		if(qvo.getDbegindate() != null){
			sql.append("   AND t.dsigndate >= ?   ");
			spm.addParam(qvo.getDbegindate());
		}
		if(qvo.getDenddate() != null){
			sql.append("   AND t.dsigndate <= ?   ");
			spm.addParam(qvo.getDenddate());
		}
		sql.append("  GROUP BY t.pk_corp   ");
		List<SaleAnalyseVO> list = (List<SaleAnalyseVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(SaleAnalyseVO.class));
		if(list != null && list.size() > 0){
			for(SaleAnalyseVO vo : list){
				signmap.put(vo.getPk_corp(), vo.getIsignnum());
			}
		}
		return signmap;
	}

	/**
	 * 查询合同数
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,SaleAnalyseVO> qryContNum(SaleAnalyseVO qvo) throws DZFWarpException {
		Map<String,SaleAnalyseVO> contmap = new HashMap<String,SaleAnalyseVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT count(pk_signinfo) inum,icontractype,pk_corp    ") ;
		sql.append("  FROM ynt_signinfo   ") ; 
		sql.append(" WHERE nvl(dr, 0) = 0   ") ; 
		sql.append("  AND irecestatus = 4   ") ; 
		if(qvo.getDbegindate() != null){
			sql.append("   AND dsigndate >= ?   ");
			spm.addParam(qvo.getDbegindate());
		}
		if(qvo.getDenddate() != null){
			sql.append("   AND dsigndate <= ?   ");
			spm.addParam(qvo.getDenddate());
		}
		sql.append(" GROUP BY pk_corp, icontractype   ");
		List<SaleAnalyseVO> list = (List<SaleAnalyseVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(SaleAnalyseVO.class));
		if(list != null && list.size() > 0){
			SaleAnalyseVO oldvo = null;
			for(SaleAnalyseVO vo : list){
				if(!contmap.containsKey(vo.getPk_corp())){
					if(vo.getIcontractype() == 1){
						vo.setIagentnum(vo.getInum());
					}else if(vo.getIcontractype() == 2){
						vo.setIincrenum(vo.getInum());
					}
					contmap.put(vo.getPk_corp(), vo);
				}else{
					oldvo = contmap.get(vo.getPk_corp());
					if(vo.getIcontractype() == 1){
						oldvo.setIagentnum(vo.getInum());
					}else if(vo.getIcontractype() == 2){
						oldvo.setIincrenum(vo.getInum());
					}
					contmap.put(vo.getPk_corp(), oldvo);
				}
			}
		}
		return contmap;
	}
	
	/**
	 * 查询加盟商合同金额
	 * @param qvo
	 * @param userid
	 * @param deptcodelist
	 * @param userlist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,DZFDouble> qryChnContMny(SaleAnalyseVO qvo) throws DZFWarpException {
		Map<String,DZFDouble> chnmap = new HashMap<String,DZFDouble>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT cn.pk_corp,   ") ;
		sql.append("  sum(decode((sign(to_date(cn.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		sql.append("  sign(to_date(cn.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(ct.nchangetotalmny,0)))+");
		sql.append("  sum(decode((sign(to_date(substr(cn.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		sql.append("  sign(to_date(substr(cn.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(cn.nsubtotalmny,0)))as contractmny");
		sql.append("  FROM cn_contract cn   ") ;
		sql.append("  INNER JOIN ynt_contract ct ON cn.pk_contract = ct.pk_contract   ");
		sql.append("  LEFT JOIN ynt_potcus s ON cn.pk_corpk = s.pk_corpk   ") ; 
		sql.append(" WHERE nvl(cn.dr, 0) = 0   ") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0   ") ; 
		sql.append("   AND nvl(s.dr, 0) = 0   ") ; 
		sql.append("   AND nvl(s.ibusitype, 0) = 1   ") ; 
		sql.append("   AND s.irecestatus IN (2, 4)  ") ; 
		sql.append("   AND (cn.vstatus=1 or cn.vstatus=9 or cn.vstatus=10)   ") ; 
		sql.append(" GROUP BY cn.pk_corp");
		spm.addParam(qvo.getDbegindate());
		spm.addParam(qvo.getDenddate());
		spm.addParam(qvo.getDbegindate());
		spm.addParam(qvo.getDenddate());
		List<SaleAnalyseVO> list = (List<SaleAnalyseVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(SaleAnalyseVO.class));
		if(list != null && list.size() > 0){
			for(SaleAnalyseVO vo : list){
				chnmap.put(vo.getPk_corp(), vo.getContractmny());
			}
		}
		return chnmap;
	}
	
	/**
	 * 查询增值服务合同金额
	 * @param qvo
	 * @param userid
	 * @param deptcodelist
	 * @param userlist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,DZFDouble> qryIncContMny(SaleAnalyseVO qvo) throws DZFWarpException {
		Map<String,DZFDouble> incmap = new HashMap<String,DZFDouble>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT sum(t.ntotalmny) contractmny,t.pk_corp   ") ;
		sql.append("  FROM ynt_contract t   ") ; 
		sql.append("  LEFT JOIN ynt_potcus s ON t.pk_corpk = s.pk_corpk   ") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0   ") ; 
		sql.append("   AND nvl(s.dr, 0) = 0   ") ; 
		sql.append("   AND nvl(s.ibusitype, 0) = 1   ") ; 
		sql.append("   AND s.irecestatus IN (2, 4)  ") ; 
		sql.append("   AND t.icosttype=1 AND t.isflag = 'Y'   ") ; 
		sql.append("   AND t.vstatus = 1 AND nvl(t.icontracttype,1) = 1   ") ; 
		if(qvo.getDbegindate() != null){
			sql.append("   AND t.dbegindate >= ?   ");
			spm.addParam(qvo.getDbegindate());
		}
		if(qvo.getDenddate() != null){
			sql.append("   AND t.dbegindate <= ?   ");
			spm.addParam(qvo.getDenddate());
		}
		sql.append(" GROUP BY t.pk_corp");
		List<SaleAnalyseVO> list = (List<SaleAnalyseVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(SaleAnalyseVO.class));
		if(list != null && list.size() > 0){
			for(SaleAnalyseVO vo : list){
				incmap.put(vo.getPk_corp(), vo.getContractmny());
			}
		}
		return incmap;
	}
	
}
