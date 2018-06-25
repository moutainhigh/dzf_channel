package com.dzf.service.channel.report.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.service.pub.IPubService;

@Service("datacommonser")
public class DataCommonRepImpl {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
    @Autowired
    private IPubService pubService;
	
	protected  HashMap<String, DataVO> queryCorps(QryParamVO paramvo,Class cla) throws DZFWarpException, Exception, IllegalAccessException {
		Integer level = pubService.getDataLevel(paramvo.getUser_name());
		
		HashMap<String, DataVO> map = new HashMap<>();
		if(level==null){
			
		}else if(level<=2){
			map = qryBoth(paramvo,level, cla);//2大区+3渠道总
		}else if(level==3){
			map = qryChannel(paramvo, cla);//1省 
		}
		return map;
	}
	
	/**
	 * 查询省市数据分析
	 * @param qvo
	 * @return
	 */
	private HashMap<String, DataVO>  qryChannel(QryParamVO qvo,Class cla) {
		HashMap<String, DataVO> map = new HashMap<>();
		List<DataVO> qryCharge= qryCharge(qvo, cla);		 //查询  是  省/市负责人相关的数据
		for (DataVO dataVO : qryCharge) {
			map.put(dataVO.getPk_corp(), dataVO);
		}
		List<DataVO> qryNotCharge = qryNotCharge(qvo, cla);//查询  非  省/市负责人相关的数据
		for (DataVO dataVO : qryNotCharge) {
			map.put(dataVO.getPk_corp(), dataVO);
		}
		return map;
	}
		
		
	/**
	 * 查询渠道总数据+区域总经理
	 * @param qvo
	 * @param type
	 * @return
	 */
	private HashMap<String, DataVO>  qryBoth(QryParamVO qvo,Integer level,Class cla) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("  select a.areaname,a.userid,");
		sql.append("  	   y.region_name vprovname,");
		sql.append("       p.pk_corp, p.innercode, p.vprovince,");
		sql.append("       b.userid cuserid,b.pk_corp corpname");//, b.vprovince
		sql.append("  from bd_account p");
		sql.append("  left join ynt_area y on p.vprovince=y.region_id and y.parenter_id = 1 and nvl(y.dr, 0) = 0 ");
		sql.append("  left join cn_chnarea_b b on p.vprovince = b.vprovince and b.type = 2 and nvl(b.dr, 0) = 0");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea and a.type = 2 and nvl(a.dr, 0) = 0");
		sql.append(" where nvl(p.dr, 0) = 0");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'Y'"); 
		sql.append("   and nvl(p.ischannel,'N')='Y'"); 
		sql.append("   and nvl(p.isseal,'N')='N'"); 
		sql.append("   and p.vprovince is not null "); 
		sql.append("   AND p.pk_corp NOT IN  \n");
		sql.append("       (SELECT f.pk_corp  \n");
		sql.append("          FROM ynt_franchisee f  \n");
		sql.append("         WHERE nvl(dr, 0) = 0  \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
	    if(level==2){// 区域总经理
	    	sql.append(" and a.userid=? ");
	    	sp.addParam(qvo.getUser_name());
	    }
		if (!StringUtil.isEmpty(qvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			sp.addParam(qvo.getAreaname());
		}
		if (qvo.getVprovince() != null && qvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			sp.addParam(qvo.getVprovince());
		}
		if (!StringUtil.isEmpty(qvo.getCuserid())) {
			sql.append(" and b.userid=? ");// 渠道经理
			sp.addParam(qvo.getCuserid());
		}
		List<DataVO> list =(List<DataVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(cla));
		HashMap<String, DataVO> map = new HashMap<String, DataVO>();
		if(list!=null && list.size()>0){
			for (DataVO dataVO : list) {
				if(dataVO.getCorpname()==null || !(dataVO.getPk_corp().equals(dataVO.getCorpname()))){
					dataVO.setCuserid(null);
				}
				if(!map.containsKey(dataVO.getPk_corp())){
					map.put(dataVO.getPk_corp(), dataVO);
				}else if(!StringUtil.isEmpty(dataVO.getCuserid())){
					map.put(dataVO.getPk_corp(), dataVO);
				}
			}
		
		}
	    return map;
	}
	
	private List<DataVO> qryCharge(QryParamVO qvo,Class cla) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid ,b.vprovname,b.vprovince,p.innercode,");
		sql.append(" (case when b.pk_corp is null then null else b.userid end) cuserid ");
		sql.append(" from bd_account p right join cn_chnarea_b b on  p.vprovince=b.vprovince  " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 and b.type=2" );
	    sql.append(" and nvl(p.ischannel,'N')='Y' and nvl(p.isaccountcorp,'N') = 'Y' and b.userid=?" );
	    sql.append(" and nvl(b.ischarge,'N')='Y' " );
		sql.append("   AND p.pk_corp NOT IN  \n");
		sql.append("       (SELECT f.pk_corp  \n");
		sql.append("          FROM ynt_franchisee f  \n");
		sql.append("         WHERE nvl(dr, 0) = 0  \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
    	sp.addParam(qvo.getUser_name());
	    List<DataVO> list =(List<DataVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(cla));
	    return list;
	}

	private List<DataVO> qryNotCharge(QryParamVO qvo,Class cla) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid,b.userid cuserid,b.vprovname,b.vprovince,p.innercode");
		sql.append(" from bd_account p right join cn_chnarea_b b on  p.pk_corp=b.pk_corp " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 and b.type=2" );
	    sql.append(" and nvl(p.ischannel,'N')='Y' and nvl(p.isaccountcorp,'N') = 'Y' and b.userid=?" );
	    sql.append(" and nvl(b.ischarge,'N')='N' " );
		sql.append("   AND p.pk_corp NOT IN  \n");
		sql.append("       (SELECT f.pk_corp  \n");
		sql.append("          FROM ynt_franchisee f  \n");
		sql.append("         WHERE nvl(dr, 0) = 0  \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
	    sp.addParam(qvo.getUser_name());
	    List<DataVO> vos =(List<DataVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(cla));
		return vos;
	}

}