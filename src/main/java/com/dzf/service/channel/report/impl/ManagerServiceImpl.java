package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IManagerService;

@Service("mana_manager")
public class ManagerServiceImpl implements IManagerService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO = null;

	@SuppressWarnings("unchecked")
	@Override
	public List<ManagerVO> query(ManagerVO qvo,Integer type) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid,b.userid cuserid,b.vprovname,b.vprovince ");
		sql.append(" from bd_corp p right join cn_chnarea_b b on  p.vprovince=b.vprovince  " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 " );
	    sql.append(" and nvl(p.ischannel,'N')='Y' and nvl(p.isaccountcorp,'N') = 'Y' and p.fathercorp = ?" );
	    sp.addParam(IDefaultValue.DefaultGroup);
		if(type==1){//渠道经理
			sql.append(" and b.userid=? ");
			sp.addParam(qvo.getUserid());
		}else if(type==2){//区域总经理
			sql.append(" and a.userid=? ");
			sp.addParam(qvo.getUserid());
		}else{
			if(!checkIsLeader(qvo)){
				return null;
			}
		}
		if(!StringUtil.isEmpty(qvo.getAreaname())){
			sql.append(" and a.areaname=? " );   //大区
			sp.addParam(qvo.getAreaname());
		}
		if(qvo.getVprovince() != null && qvo.getVprovince() != -1){
			sql.append(" and b.vprovince=? ");//省市
			sp.addParam(qvo.getVprovince());
		}
		if(!StringUtil.isEmpty(qvo.getCuserid())){
			sql.append(" and b.userid=? ");//渠道经理
			sp.addParam(qvo.getCuserid());
		}
		sql.append(" order by p.innercode " );   
		List<ManagerVO> vos =(List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ManagerVO.class));
		CorpVO cvo = null;
		UserVO uvo = null;
		Map<String, ManagerVO> map = new HashMap<String, ManagerVO>();
		ArrayList<String> pk_corps =new ArrayList<>();
		for (ManagerVO managerVO : vos) {
			cvo = CorpCache.getInstance().get(null, managerVO.getPk_corp());
			if(cvo!=null){
				managerVO.setCorpname(cvo.getUnitname());
			}
			uvo = UserCache.getInstance().get(managerVO.getUserid(), null);
			if(uvo!=null){
				managerVO.setUsername(uvo.getUser_name());
			}
			uvo = UserCache.getInstance().get(managerVO.getCuserid(), null);
			if(uvo!=null){
				managerVO.setCusername(uvo.getUser_name());
			}
			managerVO.setNdeductmny(DZFDouble.ZERO_DBL);
			managerVO.setNum(0);
			managerVO.setBondmny(DZFDouble.ZERO_DBL);
			managerVO.setNtotalmny(DZFDouble.ZERO_DBL);
			managerVO.setOutmny(DZFDouble.ZERO_DBL);
			managerVO.setPredeposit(DZFDouble.ZERO_DBL);
			if(!StringUtil.isEmpty(qvo.getCorpname())){
				if(cvo.getUnitname().indexOf(qvo.getCorpname())>=0){
					pk_corps.add(managerVO.getPk_corp());
					map.put(managerVO.getPk_corp(), managerVO);
				}
			}else{
				pk_corps.add(managerVO.getPk_corp());
				map.put(managerVO.getPk_corp(), managerVO);
			}
		}
		if(pk_corps!=null&&pk_corps.size()>0){
			String[] pks=pk_corps.toArray(new String[0]);
			StringBuffer buf=new StringBuffer();//保证金
			buf.append(" select npaymny as bondmny,pk_corp from cn_balance where nvl(dr,0) = 0 and ipaytype=1 and ");
			buf.append(SqlUtil.buildSqlForIn("pk_corp ",pks));
			List<ManagerVO> list1 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), null, new BeanListProcessor(ManagerVO.class));
			
			buf=new StringBuffer();//预存款
			buf.append("  select  sum(d.npaymny) as predeposit,d.pk_corp from cn_detail d left join cn_paybill p on d.pk_bill=p.pk_paybill where p.vstatus=3  and");
			buf.append(SqlUtil.buildSqlForIn("d.pk_corp ",pks));
			buf.append("  and  p.dpaydate>=? and  p.dpaydate<=?  and nvl(d.dr,0)=0 and nvl(p.dr,0)=0 and p.ipaytype=2");
			buf.append("  group by d.pk_corp");
			SQLParameter spm=new SQLParameter();
			spm.addParam(qvo.getDbegindate());
			spm.addParam(qvo.getDenddate());
			List<ManagerVO> list2 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ManagerVO.class));
			
			buf=new StringBuffer();//提单量v 合同总金额
			buf.append("  select count(1) as num,a.pk_corp,sum(a.ntotalmny)-sum(a.nbookmny)as ntotalmny from cn_contract a where ");
			buf.append("  substr(a.dsubmitime,0,10)>=? and substr(a.dsubmitime,0,10)<=? and nvl(a.isncust,'N')='N' and nvl(a.dr,0) = 0 and a.vdeductstatus=1 and ");
			buf.append(SqlUtil.buildSqlForIn("a.pk_corp ",pks));
			buf.append("  group by a.pk_corp");
			List<ManagerVO> list3 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ManagerVO.class));
			
			buf=new StringBuffer();//扣款金额
			buf.append("  select sum(ndeductmny) as ndeductmny ,pk_corp from cn_contract a where ");
			buf.append("  substr(a.dsubmitime,0,10)>=? and substr(a.dsubmitime,0,10)<=? and nvl(a.isncust,'N')='N' and nvl(a.dr,0) = 0 and a.vdeductstatus=1 and ");
			buf.append(SqlUtil.buildSqlForIn("pk_corp ",pks));
			buf.append("  group by a.pk_corp");
			List<ManagerVO> list4 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ManagerVO.class));
			
		    buf=new StringBuffer();//预存款余额
			buf.append(" select (npaymny-nusedmny) as outmny,pk_corp from cn_balance where nvl(dr,0) = 0 and ipaytype=2 and ");
			buf.append(SqlUtil.buildSqlForIn("pk_corp ",pks));
			List<ManagerVO> list5 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), null, new BeanListProcessor(ManagerVO.class));
			
		     if(list1!=null&&list1.size()>0){//保证金
		    	 for (ManagerVO managerVO : list1) {
					ManagerVO vo = map.get(managerVO.getPk_corp());
					vo.setBondmny(managerVO.getBondmny());
					map.put(managerVO.getPk_corp(),vo);
				}
		     }
		     if(list2!=null&&list2.size()>0){//预存款
		    	 for (ManagerVO managerVO : list2) {
						ManagerVO vo = map.get(managerVO.getPk_corp());
						vo.setPredeposit(managerVO.getPredeposit());
						vo.setOutmny(managerVO.getPredeposit()); //预存款余额金额s
						map.put(managerVO.getPk_corp(),vo);
					}
		     } 
		     if(list3!=null&&list3.size()>0){//提单量 合同总金额
		    	 for (ManagerVO managerVO : list3) {
						ManagerVO vo = map.get(managerVO.getPk_corp());
						vo.setNum(managerVO.getNum());
						vo.setNtotalmny(managerVO.getNtotalmny());
						map.put(managerVO.getPk_corp(),vo);
					}
		     }		  
		     if(list4!=null&&list4.size()>0){//扣款金额
		    	 for (ManagerVO managerVO : list4) {
						ManagerVO vo = map.get(managerVO.getPk_corp());
						vo.setNdeductmny(managerVO.getNdeductmny());
						map.put(managerVO.getPk_corp(),vo);
					}
		     }
		     if(list5!=null&&list5.size()>0){//预存款余额
		    	 for (ManagerVO managerVO : list5) {
					ManagerVO vo = map.get(managerVO.getPk_corp());
					vo.setOutmny(managerVO.getOutmny());
					map.put(managerVO.getPk_corp(),vo);
				}
		     }
		}
		 Collection<ManagerVO> manas = map.values();
		 ArrayList<ManagerVO> list= new ArrayList<ManagerVO>(manas);
		return list;
	}
	
	private boolean checkIsLeader(ManagerVO qvo) {
		String sql="select vdeptuserid corpname,vcomuserid username,vgroupuserid cusername from cn_leaderset where nvl(dr,0)=0";
		List<ManagerVO> list =(List<ManagerVO>)singleObjectBO.executeQuery(sql, null, new BeanListProcessor(ManagerVO.class));
		if(list!=null&&list.size()>0){
			ManagerVO vo=list.get(0);
			if(qvo.getUserid().equals(vo.getCusername())||qvo.getUserid().equals(vo.getCorpname())||qvo.getUserid().equals(vo.getUsername())){
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ComboBoxVO> queryManager(Integer type,String cuserid) throws DZFWarpException {
		StringBuffer buf=null;
		List<ComboBoxVO> list=null;
		UserVO uvo=null;
		if(type==3){//查询全部
		    buf=new StringBuffer();
			buf.append(" select  distinct b.userid as id from cn_chnarea_b b where nvl(b.dr,0)=0 ");
			list =(List<ComboBoxVO>)singleObjectBO.executeQuery(buf.toString(), null, new BeanListProcessor(ComboBoxVO.class));
		}else{
			buf=new StringBuffer();
			buf.append(" select distinct b.userid as id from cn_chnarea a right  join cn_chnarea_b b on a.pk_chnarea=b.pk_chnarea ");
			buf.append(" where nvl(b.dr,0)=0 and nvl(a.dr,0)=0 and a.userid=? ");
			SQLParameter spm=new SQLParameter();
			spm.addParam(cuserid);
			list =(List<ComboBoxVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ComboBoxVO.class));
		}
		for (ComboBoxVO comboBoxVO : list) {
			uvo = UserCache.getInstance().get(comboBoxVO.getId(), null);
			if(uvo!=null){
				comboBoxVO.setName(uvo.getUser_name());
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ComboBoxVO> queryArea() throws DZFWarpException {
		List<ComboBoxVO> list=null;
		StringBuffer buf=new StringBuffer();
		buf.append(" select areaname as name,areacode as id from cn_chnarea where nvl(dr,0)=0 ");
		list =(List<ComboBoxVO>)singleObjectBO.executeQuery(buf.toString(), null, new BeanListProcessor(ComboBoxVO.class));
		return list;
	}
	
}
