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
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IChannelStatisService;
import com.dzf.service.pub.IPubService;

@Service("statisChannel")
public class ChannelStatisServiceImpl implements IChannelStatisService{

	@Autowired
	private SingleObjectBO singleObjectBO;
	
    @Autowired
    private IPubService pubService;

	@Override
	public List<ManagerVO> query(ManagerVO vo) throws DZFWarpException{
		Integer level = pubService.getDataLevel(vo.getCuserid());
		if(level==null){
			return new ArrayList<ManagerVO>();
		}else if(level==1){
			vo.setCuserid(null);
		}
		SQLParameter spm=new SQLParameter();
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		spm.addParam(vo.getDbegindate());
		spm.addParam(vo.getDenddate());
		
		StringBuffer buf = new StringBuffer();
		buf.append("select sum( ");
		buf.append("       case sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd')) * ");
		buf.append("            sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd')) ");
		buf.append("         when 1 then  0 ");
		buf.append("         else nvl(t.ndeductmny,0) end + ");
		buf.append("       case sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))* ");
		buf.append("            sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd')) ");
		buf.append("         when 1 then 0");
		buf.append("         else nvl(t.nsubdeductmny,0)  end) as ndeductmny,");
		
		buf.append("	 sum( ");
		buf.append("       case sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd')) * ");
		buf.append("            sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd')) ");
		buf.append("         when 1 then 0 ");
		buf.append("         else nvl(t.ndedrebamny,0) end + ");
		buf.append("       case sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))* ");
		buf.append("            sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd')) ");
		buf.append("         when 1 then 0 ");
		buf.append("         else nvl(t.nsubdedrebamny,0) end) as ndedrebamny,");
		buf.append("     yt.pk_corp,yt.vchannelid as userid,u.user_name username");
		buf.append("  from cn_contract t ");
		buf.append(" INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");
		buf.append(" LEFT JOIN sm_user u ON yt.vchannelid = u.cuserid  \n") ; 
		buf.append(" where nvl(yt.isncust, 'N') = 'N' ");
		buf.append("   and nvl(t.dr, 0) = 0 ");
		buf.append("   and nvl(yt.dr, 0) = 0 ");
		buf.append("   and (yt.vstatus = 1 or yt.vstatus = 9 or yt.vstatus = 10) ");
//		buf.append("   and yt.vchannelid is not null ");
		if(!StringUtil.isEmpty(vo.getUserid())){
			buf.append("   and yt.vchannelid=? ");
			spm.addParam(vo.getUserid());
		}
		if(!StringUtil.isEmpty(vo.getCuserid())){
			buf.append("   and yt.vchannelid=? ");
			spm.addParam(vo.getCuserid());
		}
		if(!StringUtil.isEmpty(vo.getPk_corp())){
		    String[] strs = vo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    buf.append(" AND yt.pk_corp in (").append(inSql).append(")");
		}
		buf.append(" group by yt.pk_corp, yt.vchannelid ");
		buf.append(" order by yt.vchannelid");
		List<ManagerVO> list=(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(),spm, new BeanListProcessor(ManagerVO.class));
		CorpVO cvo ;
		String userName;
		for (ManagerVO managerVO : list) {
			cvo = CorpCache.getInstance().get(null, managerVO.getPk_corp());
			if(cvo!=null){
				managerVO.setCorpname(cvo.getUnitname());
				managerVO.setVcontcode(cvo.getInnercode());
			}else{
				managerVO.setVcontcode(null);
			}
			userName = managerVO.getUsername();
			if(!StringUtil.isEmpty(userName)){
				managerVO.setUsername(CodeUtils1.deCode(userName));
			}
		}
		return list;
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
