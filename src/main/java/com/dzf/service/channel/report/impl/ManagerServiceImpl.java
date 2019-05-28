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
import com.dzf.service.channel.report.IManagerService;

@Service("mana_manager")
public class ManagerServiceImpl implements IManagerService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
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
		List<ManagerVO> qryYSH =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
	    sql = new StringBuffer();
	    sql.append(" select 0 as anum,t.pk_confrim as pk_corp,substr(t.dchangetime,0,10)as denddate,yt.vcontcode, ");
		sql.append(" nvl(t.nsubtotalmny,0) as antotalmny,nvl(t.nsubdeductmny,0) as ndeductmny , " );   
		sql.append(" nvl(t.nsubdedrebamny,0) as ndedrebamny from cn_contract  t" );
		sql.append(" INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");  
		sql.append(" where nvl(yt.isncust,'N')='N' and nvl(t.dr,0) = 0 and nvl(yt.dr,0)=0 and yt.vstatus=9  and" );
		sql.append(" substr(t.dchangetime,0,10)>=? and substr(t.dchangetime,0,10)<=? and yt.pk_corp=?" );
		List<ManagerVO> qryYZZ =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
		sql = new StringBuffer();
	    sql.append(" select -1 as anum,t.pk_confrim as pk_corp,substr(t.dchangetime,0,10)as denddate,yt.vcontcode, ");
		sql.append(" nvl(t.nsubtotalmny,0)+nvl(yt.nbookmny,0) as antotalmny,nvl(t.nsubdeductmny,0) as ndeductmny , " );   
		sql.append(" nvl(t.nsubdedrebamny,0) as ndedrebamny from cn_contract t" );
		sql.append(" INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");
		sql.append(" where nvl(yt.isncust,'N')='N' and nvl(t.dr,0) = 0 and nvl(yt.dr,0) = 0 and yt.vstatus=10 and" );
		sql.append(" substr(t.dchangetime,0,10)>=? and substr(t.dchangetime,0,10)<=? and yt.pk_corp=?" );
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
	
	@Override
	public List<ManagerVO> queryWDetail(ManagerVO qvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sp.addParam(qvo.getVprovince());
		sp.addParam(qvo.getDbegindate());
		sp.addParam(qvo.getDenddate());
		sp.addParam(qvo.getPk_corp());//补提单合同，数量为0
		sql.append(" select (case yt.patchstatus when 2 then 0 when 5 then  0 else 1 end) as anum,yt.vcontcode, ");
		sql.append(" yt.pk_contract as pk_corp,substr(yt.dsubmitime,0,10) as denddate, ");
		sql.append(" substr(yt.dsubmitime,0,10) as dbegindate,vconfreason as vprovname, ");
		sql.append(" nvl(yt.ntotalmny,0)-nvl(yt.nbookmny,0) as antotalmny " );   
		sql.append(" from ynt_contract yt " );
		sql.append(" where nvl(yt.isncust,'N')='N' and nvl(yt.dr,0) = 0 and yt.vstatus=? and " );
		sql.append(" substr(yt.dsubmitime,0,10)>=? and substr(yt.dsubmitime,0,10)<=? and yt.pk_corp=? " );
		sql.append(" order by yt.dsubmitime " );
		List<ManagerVO> vos =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		return vos;
	}
	
}
