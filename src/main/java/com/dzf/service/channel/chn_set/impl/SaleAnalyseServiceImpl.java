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
		sql.append(" select ba.pk_corp,b.vprovname,b.vprovince,c.areaname");
		sql.append("   from bd_account ba");
		sql.append("   left join (select distinct pk_chnarea, vprovname, vprovince");
		sql.append("                from cn_chnarea_b");
		sql.append("               where type = 1");
		sql.append("                 and nvl(dr, 0) = 0) b on ba.vprovince = b.vprovince");
		sql.append("   left join cn_chnarea c on b.pk_chnarea = c.pk_chnarea");
		sql.append("                         and c.type = 1");
		sql.append("  where nvl(ba.dr, 0) = 0");
		sql.append("    and nvl(c.dr, 0) = 0");
		sql.append("    and nvl(ba.isaccountcorp, 'N') = 'Y'");
		sql.append("    and nvl(ba.ischannel, 'N') = 'Y'");
		sql.append("    and nvl(ba.isseal, 'N') = 'N'");
		if(!StringUtil.isEmpty(qvo.getAreaname())){
			sql.append(" and c.areaname=? " );   //大区
			sp.addParam(qvo.getAreaname());
		}
		if(qvo.getVprovince() != null && qvo.getVprovince() != -1){
			sql.append(" and ba.vprovince=? ");//省市
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
		SaleAnalyseVO numvo = null;
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
				salevo.setIsignnum(signmap.get(pk_corp));
			}
			if(nummap != null && !nummap.isEmpty()){
				numvo = nummap.get(pk_corp);
				if(numvo!=null){
					salevo.setIagentnum(numvo.getIagentnum());
					salevo.setIincrenum(numvo.getIincrenum());
				}
			}
			if(chnmap != null && !chnmap.isEmpty()){
				salevo.setContractmny(chnmap.get(pk_corp));
			}
			if(incmap != null && !incmap.isEmpty()){
				if(salevo.getContractmny()!=null){
					salevo.setContractmny(salevo.getContractmny().add(chnmap.get(pk_corp)));
				}else{
					salevo.setContractmny(chnmap.get(pk_corp));
				}
			}
			if(salevo.getIsignnum() != null && salevo.getContractmny() != null){
				salevo.setPricemny(salevo.getContractmny().div(new DZFDouble(salevo.getIsignnum())));
			}
			retlist.add(salevo);
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
		sql.append("SELECT t.pk_corp,sum(t.iv1) ivisitnum,sum(t.iv2)iviscustnum from ( \n");
		sql.append("SELECT count(y.pk_flowhistory) iv1, \n");
		sql.append("  count(distinct y.pk_customno) iv2, \n");
		sql.append("  y.pk_corp \n");
		sql.append("  FROM ynt_porflwhistory y \n");
		sql.append("  LEFT JOIN ynt_potcus s ON y.pk_customno = s.pk_customno \n");
		sql.append(" WHERE nvl(y.dr, 0) = 0 AND nvl(s.dr, 0) = 0 AND s.ibusitype = 1 \n");
		if(qvo.getDbegindate() != null){
			sql.append("   AND y.realfollowdate >= ? \n");
			spm.addParam(qvo.getDbegindate());
		}
		if(qvo.getDenddate() != null){
			sql.append("   AND y.realfollowdate <= ? \n");
			spm.addParam(qvo.getDenddate());
		}
		sql.append("  GROUP BY y.pk_corp,y.followuser )t group by t.pk_corp \n");
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
		sql.append(" SELECT count(t.pk_customno) isignnum,t.pk_corp \n") ;
		sql.append(" FROM ynt_potcus t \n") ; 
		sql.append("WHERE nvl(t.ibusitype, 0) = 1 \n") ; 
		sql.append("  AND t.irecestatus IN (2, 4) \n") ; 
		if(qvo.getDbegindate() != null){
			sql.append("   AND t.dsigndate >= ? \n");
			spm.addParam(qvo.getDbegindate());
		}
		if(qvo.getDenddate() != null){
			sql.append("   AND t.dsigndate <= ? \n");
			spm.addParam(qvo.getDenddate());
		}
		sql.append("  GROUP BY t.pk_corp \n");
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
		sql.append("SELECT count(pk_signinfo) inum,icontractype,pk_corp  \n") ;
		sql.append("  FROM ynt_signinfo \n") ; 
		sql.append(" WHERE nvl(dr, 0) = 0 \n") ; 
		sql.append("  AND irecestatus = 4 \n") ; 
		if(qvo.getDbegindate() != null){
			sql.append("   AND dsigndate >= ? \n");
			spm.addParam(qvo.getDbegindate());
		}
		if(qvo.getDenddate() != null){
			sql.append("   AND dsigndate <= ? \n");
			spm.addParam(qvo.getDenddate());
		}
		sql.append(" GROUP BY pk_corp, icontractype \n");
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
		sql.append("SELECT cn.pk_corp, \n") ;
		sql.append("  sum(decode((sign(to_date(cn.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		sql.append("  sign(to_date(cn.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(ct.ntotalmny,0)))+");
		sql.append("  sum(decode((sign(to_date(substr(cn.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		sql.append("  sign(to_date(substr(cn.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(cn.nsubtotalmny,0)))as contractmny");
		sql.append("  FROM cn_contract cn \n") ;
		sql.append("  INNER JOIN ynt_contract ct ON cn.pk_contract = ct.pk_contract \n");
		sql.append("  LEFT JOIN ynt_potcus s ON cn.pk_corpk = s.pk_corpk \n") ; 
		sql.append(" WHERE nvl(cn.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(s.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(s.ibusitype, 0) = 1 \n") ; 
		sql.append("   AND s.irecestatus IN (2, 4)\n") ; 
		sql.append("   AND (cn.vdeductstatus=1 or cn.vdeductstatus=9 or cn.vdeductstatus=10) \n") ; 
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
		sql.append("SELECT sum(t.ntotalmny) contractmny,t.pk_corp \n") ;
		sql.append("  FROM ynt_contract t \n") ; 
		sql.append("  LEFT JOIN ynt_potcus s ON t.pk_corpk = s.pk_corpk \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(s.dr, 0) = 0 \n") ; 
		sql.append("   AND nvl(s.ibusitype, 0) = 1 \n") ; 
		sql.append("   AND s.irecestatus IN (2, 4)\n") ; 
		sql.append("   AND t.icosttype=1 AND t.isflag = 'Y' \n") ; 
		sql.append("   AND t.vstatus = 1 AND nvl(t.icontracttype,1) = 1 \n") ; 
		if(qvo.getDbegindate() != null){
			sql.append("   AND t.dbegindate >= ? \n");
			spm.addParam(qvo.getDbegindate());
		}
		if(qvo.getDenddate() != null){
			sql.append("   AND t.dbegindate <= ? \n");
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
