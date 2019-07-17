package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IChannelStatisService;
import com.dzf.service.pub.IPubService;

@Service("statisChannel")
public class ChannelStatisServiceImpl implements IChannelStatisService{

	@Autowired
	private SingleObjectBO singleObjectBO;
	
    @Autowired
    private IPubService pubService;

	@SuppressWarnings("unchecked")
	@Override
	public List<ManagerVO> query(ManagerVO vo) throws DZFWarpException{
		Integer level = pubService.getDataLevel(vo.getCuserid());
		if (level == null) {
			return new ArrayList<ManagerVO>();
		} else if (level == 1) {
			vo.setCuserid(null);
		}
		SQLParameter spm = new SQLParameter();
//		spm.addParam(vo.getDbegindate());
//		spm.addParam(vo.getDenddate());
//		spm.addParam(vo.getDbegindate());
//		spm.addParam(vo.getDenddate());
//		spm.addParam(vo.getDbegindate());
//		spm.addParam(vo.getDenddate());
//		spm.addParam(vo.getDbegindate());
//		spm.addParam(vo.getDenddate());
		
		StringBuffer buf = new StringBuffer();
//		buf.append("select sum( ");
//		buf.append("       case sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd')) * ");
//		buf.append("            sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd')) ");
//		buf.append("         when 1 then  0 ");
//		buf.append("         else nvl(t.ndeductmny,0) end + ");
//		buf.append("       case sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))* ");
//		buf.append("            sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd')) ");
//		buf.append("         when 1 then 0");
//		buf.append("         else nvl(t.nsubdeductmny,0)  end) as ndeductmny,");
//		
//		buf.append("	 sum( ");
//		buf.append("       case sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd')) * ");
//		buf.append("            sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd')) ");
//		buf.append("         when 1 then 0 ");
//		buf.append("         else nvl(t.ndedrebamny,0) end + ");
//		buf.append("       case sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))* ");
//		buf.append("            sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd')) ");
//		buf.append("         when 1 then 0 ");
//		buf.append("         else nvl(t.nsubdedrebamny,0) end) as ndedrebamny,");
		buf.append("SELECT SUM(CASE  ") ;
		buf.append("             WHEN t.deductdata >= ? AND t.deductdata <= ? THEN  ") ; 
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		buf.append("              nvl(t.ndeductmny, 0)  ") ; 
		buf.append("             ELSE  ") ; 
		buf.append("              0  ") ; 
		buf.append("           END) + SUM(CASE  ") ; 
		buf.append("                        WHEN substr(t.dchangetime, 1, 10) >= ? AND  ") ; 
		buf.append("                             substr(t.dchangetime, 1, 10) <= ? THEN  ") ; 
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		buf.append("                         nvl(t.nsubdeductmny, 0)  ") ; 
		buf.append("                        ELSE  ") ; 
		buf.append("                         0  ") ; 
		buf.append("                      END) AS ndeductmny,  ") ; 
		buf.append("       SUM(CASE  ") ; 
		buf.append("             WHEN t.deductdata >= ? AND t.deductdata <= ? THEN  ") ; 
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		buf.append("              nvl(t.ndedrebamny, 0)  ") ; 
		buf.append("             ELSE  ") ; 
		buf.append("              0  ") ; 
		buf.append("           END) + SUM(CASE  ") ; 
		buf.append("                        WHEN substr(t.dchangetime, 1, 10) >= ? AND  ") ; 
		buf.append("                             substr(t.dchangetime, 1, 10) <= ? THEN  ") ; 
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		buf.append("                         nvl(t.nsubdedrebamny, 0)  ") ; 
		buf.append("                        ELSE  ") ; 
		buf.append("                         0  ") ; 
		buf.append("                      END) AS ndedrebamny,  ") ; 
		buf.append("      yt.pk_corp, ");
		buf.append("      account.innercode AS vcontcode,  ") ; 
		buf.append("      account.unitname AS corpname,  ") ;
		buf.append("      yt.vchannelid AS userid, ");
		buf.append("      u.user_name AS username ");
		
		buf.append("  FROM cn_contract t ");
		buf.append(" INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");
		buf.append("  LEFT JOIN sm_user u ON yt.vchannelid = u.cuserid    ") ; 
		buf.append("  LEFT JOIN bd_account account ON account.pk_corp = t.pk_corp    ") ; 
		buf.append(" WHERE nvl(yt.isncust, 'N') = 'N' ");
		buf.append("   AND nvl(t.dr, 0) = 0 ");
		buf.append("   AND nvl(yt.dr, 0) = 0 ");
		buf.append("   AND yt.vstatus IN ( 1, 9, 10) ");
		buf.append("   AND ((t.deductdata >= ? AND t.deductdata <= ?) OR  ") ; 
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		buf.append("        (substr(t.dchangetime, 1, 10) >= ? AND  ") ; 
		buf.append("        substr(t.dchangetime, 1, 10) <= ? ))  ") ; 
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
//		buf.append("   and yt.vchannelid is not null ");
		if(!StringUtil.isEmpty(vo.getUserid())){
			buf.append("   and yt.vchannelid = ? ");
			spm.addParam(vo.getUserid());
		}
		if(!StringUtil.isEmpty(vo.getCuserid())){
			buf.append("   and yt.vchannelid = ? ");
			spm.addParam(vo.getCuserid());
		}
		if(!StringUtil.isEmpty(vo.getPk_corp())){
		    String[] strs = vo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    buf.append(" AND yt.pk_corp in (").append(inSql).append(")");
		}
		String where = QueryUtil.getWhereSql();
		buf.append(" AND ").append(where);
		buf.append(" group by yt.pk_corp,  ");
		buf.append("          yt.vchannelid, ");
		buf.append("          account.innercode, ");
		buf.append("          account.unitname, ");
		buf.append("          u.user_name ");

		buf.append(" order by yt.vchannelid");
		List<ManagerVO> list = (List<ManagerVO>) singleObjectBO.executeQuery(buf.toString(), spm,
				new BeanListProcessor(ManagerVO.class));
		if(list != null && list.size() > 0){
			return getReturnData(list);
		}
//		CorpVO cvo ;
//		String userName;
//		for (ManagerVO managerVO : list) {
//			cvo = CorpCache.getInstance().get(null, managerVO.getPk_corp());
//			if(cvo!=null){
//				managerVO.setCorpname(cvo.getUnitname());
//				managerVO.setVcontcode(cvo.getInnercode());
//			}else{
//				managerVO.setVcontcode(null);
//			}
//			userName = managerVO.getUsername();
//			if(!StringUtil.isEmpty(userName)){
//				managerVO.setUsername(CodeUtils1.deCode(userName));
//			}
//		}
		return list;
	}
	
	/**
	 * 获取包含合计行数据
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<ManagerVO> getReturnData(List<ManagerVO> list) throws DZFWarpException {
		List<ManagerVO> rlist = new ArrayList<ManagerVO>();
		String userid = "";
		int i = 0;
		ManagerVO countvo = null;
		for(ManagerVO mvo : list){
			if(i == 0){
				userid = mvo.getUserid();
				countvo = new ManagerVO();
				countvo.setUsername("合计");
			}else if(i != list.size() - 1){
				if(!userid.equals(mvo.getUserid())){
					rlist.add(countvo);
					userid = mvo.getUserid();
					countvo = new ManagerVO();
					countvo.setUsername("合计");
				}
			}
			QueryDeCodeUtils.decKeyUtil(new String[]{"username", "corpname"}, mvo, 1);
			countvo.setNdeductmny(SafeCompute.add(countvo.getNdeductmny(), mvo.getNdeductmny()));
			countvo.setNdedrebamny(SafeCompute.add(countvo.getNdedrebamny(), mvo.getNdedrebamny()));
			rlist.add(mvo);
			if(i == list.size() - 1){
				rlist.add(countvo);
			}
			i++;
		}
		return rlist;
	}
	
	@Override
	public List<ManagerVO> queryDetail(ManagerVO qvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sp.addParam(qvo.getDbegindate());
		sp.addParam(qvo.getDenddate());
		sp.addParam(qvo.getPk_corp());//补提单合同，数量为0
		sql.append(" select (case yt.patchstatus when 2 then 0 when 5 then  0 else 1 end) as anum,yt.vcontcode, ");
		sql.append(" t.pk_confrim as pk_corp ,t.deductdata as denddate, ");
		sql.append(" nvl(yt.nchangetotalmny,0)-nvl(yt.nbookmny,0) as antotalmny, " );   
		sql.append(" nvl(t.ndeductmny,0) as ndeductmny,nvl(t.ndedrebamny,0) as ndedrebamny from cn_contract t" );
		sql.append(" INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");
		sql.append(" where nvl(yt.isncust,'N')='N' and nvl(t.dr,0) = 0 and nvl(yt.dr,0) = 0 and (yt.vstatus=1 or yt.vstatus=9 or yt.vstatus=10) and " );
		sql.append(" t.deductdata>=? and t.deductdata<=? and yt.pk_corp=? " );
		if(!StringUtil.isEmpty(qvo.getCuserid())){
			sql.append(" and yt.vchannelid=? ");
			sp.addParam(qvo.getCuserid());
		}else{
			sql.append(" and yt.vchannelid is null");
		}
		List<ManagerVO> qryYSH =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
	    sql = new StringBuffer();
	    sql.append(" select 0 as anum,t.pk_confrim as pk_corp,substr(t.dchangetime,0,10)as denddate,yt.vcontcode, ");
		sql.append(" nvl(t.nsubtotalmny,0) as antotalmny,nvl(t.nsubdeductmny,0) as ndeductmny , " );   
		sql.append(" nvl(t.nsubdedrebamny,0) as ndedrebamny from cn_contract  t" );
		sql.append(" INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");  
		sql.append(" where nvl(yt.isncust,'N')='N' and nvl(t.dr,0) = 0 and nvl(yt.dr,0)=0 and yt.vstatus=9  and" );
		sql.append(" substr(t.dchangetime,0,10)>=? and substr(t.dchangetime,0,10)<=? and yt.pk_corp=?" );
		if(!StringUtil.isEmpty(qvo.getCuserid())){
			sql.append(" and yt.vchannelid=? ");
		}else{
			sql.append(" and yt.vchannelid is  null");
		}
		List<ManagerVO> qryYZZ =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
		sql = new StringBuffer();
	    sql.append(" select -1 as anum,t.pk_confrim as pk_corp,substr(t.dchangetime,0,10)as denddate,yt.vcontcode, ");
		sql.append(" nvl(t.nsubtotalmny,0)+nvl(yt.nbookmny,0) as antotalmny,nvl(t.nsubdeductmny,0) as ndeductmny , " );   
		sql.append(" nvl(t.nsubdedrebamny,0) as ndedrebamny from cn_contract t" );
		sql.append(" INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");
		sql.append(" where nvl(yt.isncust,'N')='N' and nvl(t.dr,0) = 0 and nvl(yt.dr,0) = 0 and yt.vstatus=10 and" );
		sql.append(" substr(t.dchangetime,0,10)>=? and substr(t.dchangetime,0,10)<=? and yt.pk_corp=?" );
		if(!StringUtil.isEmpty(qvo.getCuserid())){
			sql.append(" and yt.vchannelid=? ");
		}else{
			sql.append(" and yt.vchannelid is  null");
		}
		List<ManagerVO> qryYZF =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
			
		ArrayList<ManagerVO> vos=new ArrayList<>();
		if(qryYSH!=null && qryYSH.size()>0){
			vos.addAll(qryYSH);
		}
		if(qryYZZ!=null && qryYZZ.size()>0){
			vos.addAll(qryYZZ);
		}
		if(qryYZF!=null && qryYZF.size()>0){
			vos.addAll(qryYZF);
		}
		Collections.sort(vos, new Comparator<ManagerVO>() {
			@Override
			public int compare(ManagerVO o1, ManagerVO o2) {
				return o1.getDenddate().compareTo(o2.getDenddate());
			}
		});
		return vos;
	}
	
}
