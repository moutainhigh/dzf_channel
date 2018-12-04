package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
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
		buf.append("     yt.pk_corp,yt.vchannelid as userid");
		buf.append("  from cn_contract t ");
		buf.append(" INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");
		buf.append(" where nvl(yt.isncust, 'N') = 'N' ");
		buf.append("   and nvl(t.dr, 0) = 0 ");
		buf.append("   and nvl(yt.dr, 0) = 0 ");
		buf.append("   and (yt.vstatus = 1 or yt.vstatus = 9 or yt.vstatus = 10) ");
		buf.append("   and yt.vchannelid is not null ");
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
		CorpVO cvo = null;
		UserVO uvo = null;
		for (ManagerVO managerVO : list) {
			cvo = CorpCache.getInstance().get(null, managerVO.getPk_corp());
			if(cvo!=null){
				managerVO.setCorpname(cvo.getUnitname());
				managerVO.setPk_corp(cvo.getInnercode());
			}else{
				managerVO.setPk_corp(null);
			}
			uvo = UserCache.getInstance().get(managerVO.getUserid(), null);
			if(uvo!=null){
				managerVO.setUsername(uvo.getUser_name());
			}
		}
		return list;
	}
	
}
