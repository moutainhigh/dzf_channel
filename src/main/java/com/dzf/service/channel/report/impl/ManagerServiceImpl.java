package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.CommonUtil;
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
	
	@Override
	public List<ManagerVO> query(ManagerVO qvo,Integer type) throws DZFWarpException {
		ArrayList<ManagerVO> list=new ArrayList<ManagerVO>();
		if(type==3){//渠道经理
			if(!checkIsLeader(qvo)){
				return null;
			}
		}
		List<String> vprovinces=new ArrayList<>();
		List<ManagerVO> qryCharge= qryCharge(qvo,type);			//查询  是  省/市负责人相关的数据
		LinkedHashMap<String, ManagerVO> map=new LinkedHashMap<>();
		for (ManagerVO managerVO : qryCharge) {
			Boolean flg=false;//判断查询框的渠道经理的过滤
			if(StringUtil.isEmpty(qvo.getCuserid()) || qvo.getCuserid().equals(managerVO.getCuserid())){
				flg=true;
			}
			if(!map.containsKey(managerVO.getPk_corp()) && flg){
				map.put(managerVO.getPk_corp(), managerVO);
			}
			if(!vprovinces.contains(String.valueOf(managerVO.getVprovince()))){
				vprovinces.add(managerVO.getVprovince().toString());
			}else{
				if(!StringUtil.isEmpty(managerVO.getCuserid())){
					map.put(managerVO.getPk_corp(),managerVO);
				}
			}
		}
		List<ManagerVO> qryNotCharge = qryNotCharge(qvo,type,vprovinces);//查询  非  省/市负责人相关的数据
		for (ManagerVO managerVO : qryNotCharge) {
			Boolean flg=false;//判断查询框的渠道经理的过滤
			if(StringUtil.isEmpty(qvo.getCuserid()) || qvo.getCuserid().equals(managerVO.getCuserid())){
				flg=true;
			}
			if(!map.containsKey(managerVO.getPk_corp()) && flg){
				map.put(managerVO.getPk_corp(), managerVO);
			}else{
				if(!StringUtil.isEmpty(managerVO.getCuserid())){
					map.put(managerVO.getPk_corp(),managerVO);
				}
			}
		}
		if(!map.isEmpty()){
			list=new ArrayList<>(map.values());
			Collections.sort(list, new Comparator<ManagerVO>() {
				@Override
				public int compare(ManagerVO o1, ManagerVO o2) {
					return o1.getVprovince().compareTo(o2.getVprovince());
				}
			});
		}
		if(list!=null && list.size()>0){
			list=queryCommon(qvo,list);
		}
		return list;
	}
	
	private List<ManagerVO> qryCharge(ManagerVO qvo,Integer type) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid ,b.vprovname,b.vprovince,p.innercode,");
		sql.append(" (case when b.pk_corp is null then null when b.pk_corp!=p.pk_corp then null else b.userid end) cuserid ");
		sql.append(" from bd_corp p right join cn_chnarea_b b on  p.vprovince=b.vprovince  " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 and b.type=1" );
	    sql.append(" and nvl(p.ischannel,'N')='Y' and nvl(p.isaccountcorp,'N') = 'Y' and p.fathercorp = ? " );
	    sql.append(" and nvl(b.ischarge,'N')='Y' " );
	    sp.addParam(IDefaultValue.DefaultGroup);
	    if(type==1){
	    	sql.append("  and b.userid=? ");
	    	sp.addParam(qvo.getUserid());
	    }
	    if (type == 2) {// 区域总经理
			sql.append(" and a.userid=? ");
			sp.addParam(qvo.getUserid());
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
	    List<ManagerVO> list =(List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ManagerVO.class));
	    return list;
	}

	private List<ManagerVO> qryNotCharge(ManagerVO qvo,Integer type,List<String> vprovinces) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid,b.userid cuserid,b.vprovname,b.vprovince,p.innercode");
		sql.append(" from bd_corp p right join cn_chnarea_b b on  p.pk_corp=b.pk_corp " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 and b.type=1" );
	    sql.append(" and nvl(p.ischannel,'N')='Y' and nvl(p.isaccountcorp,'N') = 'Y' and p.fathercorp = ? " );
	    sql.append(" and nvl(b.ischarge,'N')='N' " );
	    sp.addParam(IDefaultValue.DefaultGroup);
	    if(type==1){
	    	if(vprovinces!=null && vprovinces.size()>0){
				sql.append("  and (b.userid=? or ");
				sql.append(SqlUtil.buildSqlForIn("b.vprovince",vprovinces.toArray(new String[vprovinces.size()])));
				sql.append(" )");
			}else{
				sql.append("  and b.userid=? ");
			}
	    	sp.addParam(qvo.getUserid());
	    }
	    if (type == 2) {// 区域总经理
	    	if(vprovinces!=null && vprovinces.size()>0){
				sql.append("  and (a.userid=? or ");
				sql.append(SqlUtil.buildSqlForIn("b.vprovince",vprovinces.toArray(new String[vprovinces.size()])));
				sql.append(" )");
			}else{
				sql.append(" and a.userid=? ");
			}
			sp.addParam(qvo.getUserid());
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
	    List<ManagerVO> vos =(List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ManagerVO.class));
		return vos;
	}

	private ArrayList<ManagerVO> queryCommon(ManagerVO qvo,List<ManagerVO> vos) {
		LinkedHashMap<String, ManagerVO> map = new LinkedHashMap<String, ManagerVO>();
		ArrayList<String> pk_corps =new ArrayList<>();
		setDefult(qvo, vos, map, pk_corps);//设置默认值
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
			
			buf=new StringBuffer();//预存款余额
			buf.append(" select (nvl(npaymny,0)-nvl(nusedmny,0)) as outmny,pk_corp from cn_balance where nvl(dr,0) = 0 and ipaytype=2 and ");
			buf.append(SqlUtil.buildSqlForIn("pk_corp ",pks));
			List<ManagerVO> list4 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), null, new BeanListProcessor(ManagerVO.class));
			
			buf=new StringBuffer();//小规模及一般纳税人的数量
			buf.append(" select count(pk_corp) anum,chargedeptname corpname, fathercorp pk_corp from bd_corp  ");
			buf.append(" where nvl(dr,0)=0 and nvl(isseal,'N')='N' and nvl(isaccountcorp,'N')='N' and chargedeptname is not null and ");
			buf.append(SqlUtil.buildSqlForIn("fathercorp ",pks));
			buf.append(" group by fathercorp,chargedeptname  ");
			List<ManagerVO> list5 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), null, new BeanListProcessor(ManagerVO.class));
			
			for(int i=0;i<6;i++){//地区提单量,地区合同代账费,（计算客单价）
				spm.addParam(qvo.getDbegindate());
				spm.addParam(qvo.getDenddate());
			}
			List<ManagerVO> list6 =(List<ManagerVO>)singleObjectBO.executeQuery(getSql(pks,3).toString(), spm, new BeanListProcessor(ManagerVO.class));
			
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
		     
		     if(list4!=null&&list4.size()>0){//预存款余额
		    	 for (ManagerVO managerVO : list4) {
					ManagerVO vo = map.get(managerVO.getPk_corp());
					vo.setOutmny(managerVO.getOutmny());
					map.put(managerVO.getPk_corp(),vo);
				}
		     }
		     if(list5!=null&&list5.size()>0){//小规模及一般纳税人的数量
		    	 for (ManagerVO managerVO : list5) {
					ManagerVO vo = map.get(managerVO.getPk_corp());
					if("小规模纳税人".equals(managerVO.getCorpname())){
						vo.setXgmNum(managerVO.getAnum());
					}else{
						vo.setYbrNum(managerVO.getAnum());
					}
					map.put(managerVO.getPk_corp(),vo);
				}
		     }
		     HashMap<Integer, ManagerVO> promap=new HashMap<Integer, ManagerVO>();
		     if(list6!=null&&list6.size()>0){//客单价
		    	 for (ManagerVO managerVO : list6) {
		    		 promap.put(managerVO.getVprovince(), managerVO);
				}
		    	Iterator<Entry<String, ManagerVO>> iterator = map.entrySet().iterator();
		    	while (iterator.hasNext()){
		    		Entry<String, ManagerVO> entry = iterator.next();
		    		ManagerVO mapvo =(ManagerVO)entry.getValue();
		    		ManagerVO managerVO = promap.get(mapvo.getVprovince());
		    		if(managerVO != null){
		    			mapvo.setUnitprice(CommonUtil.getDZFDouble(managerVO.getRntotalmny()).div(CommonUtil.getDZFDouble(managerVO.getRnum())));
		    		}
		    	}
		     }
		     
			List<ManagerVO> list7 =qryBoth(qvo,pks,1);
			if(list7!=null&&list7.size()>0){//新增提单量, 新增合同代账费
				 for (ManagerVO managerVO : list7) {
						ManagerVO vo = map.get(managerVO.getPk_corp());
						vo.setAnum(vo.getAnum()+managerVO.getAnum());
						vo.setAntotalmny(vo.getAntotalmny().add(managerVO.getAntotalmny()));
						vo.setNdeductmny(vo.getNdeductmny().add(managerVO.getNdeductmny()));
						vo.setNdedrebamny(vo.getNdedrebamny().add(managerVO.getNdedrebamny()));
						map.put(managerVO.getPk_corp(),vo);
					}
			}
			List<ManagerVO> list8 =qryBoth(qvo,pks,2);
			if(list8!=null&&list8.size()>0){//续费提单量, 续费合同代账费
				 for (ManagerVO managerVO : list8) {
						ManagerVO vo = map.get(managerVO.getPk_corp());
						vo.setRnum(vo.getRnum()+managerVO.getAnum());
						vo.setRntotalmny(vo.getRntotalmny().add(managerVO.getAntotalmny()));
						vo.setNdeductmny(vo.getNdeductmny().add(managerVO.getNdeductmny()));
						vo.setNdedrebamny(vo.getNdedrebamny().add(managerVO.getNdedrebamny()));
						map.put(managerVO.getPk_corp(),vo);
					}
			}
		}
		Collection<ManagerVO> manas = map.values();
		ArrayList<ManagerVO> list= new ArrayList<ManagerVO>(manas);
		return list;
	}
	
	/**
	 * 设置默认值
	 * @param qvo
	 * @param vos
	 * @param map
	 * @param pk_corps
	 */
	private void setDefult(ManagerVO qvo, List<ManagerVO> vos, Map<String, ManagerVO> map, ArrayList<String> pk_corps) {
		CorpVO cvo = null;
		UserVO uvo = null;
		for (ManagerVO managerVO : vos) {
			cvo = CorpCache.getInstance().get(null, managerVO.getPk_corp());
			if(cvo!=null){
				managerVO.setCorpname(cvo.getUnitname());
			}
			uvo = UserCache.getInstance().get(managerVO.getUserid(), null);
			if(uvo!=null){
				managerVO.setUsername(uvo.getUser_name());
			}
			if(!StringUtil.isEmpty(managerVO.getCuserid())){
				uvo = UserCache.getInstance().get(managerVO.getCuserid(), null);
				if(uvo!=null){
					managerVO.setCusername(uvo.getUser_name());
				}
			}
			managerVO.setNdeductmny(DZFDouble.ZERO_DBL);
			managerVO.setNdedrebamny(DZFDouble.ZERO_DBL);
			managerVO.setXgmNum(0);
			managerVO.setYbrNum(0);
			managerVO.setAnum(0);
			managerVO.setRnum(0);
			managerVO.setBondmny(DZFDouble.ZERO_DBL);
			managerVO.setAntotalmny(DZFDouble.ZERO_DBL);
			managerVO.setRntotalmny(DZFDouble.ZERO_DBL);
			managerVO.setOutmny(DZFDouble.ZERO_DBL);
			managerVO.setPredeposit(DZFDouble.ZERO_DBL);
			managerVO.setUnitprice(DZFDouble.ZERO_DBL);
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
	}
	
	/**
	 * 查询语句（提单量，合同代账费）
	 * @param buf
	 */
	private StringBuffer getSql(String[] pks,Integer type) {
		StringBuffer buf=new StringBuffer();
//		if(type==3){
			buf.append("  select wm_concat(w.pk_corp) as pk_corp,sum(w.rnum) as rnum,");
			buf.append("  sum(w.rntotalmny) as rntotalmny,w.vprovince as vprovince from ( ");
			buf.append("  select c.pk_corp,b.vprovince, ");
//		}else{
//			buf.append("  select c.pk_corp,");
//		}
		buf.append("  sum(decode((sign(to_date(deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,1))");
		buf.append("  +sum(decode(vstatus,10," );
		buf.append("  decode((sign(to_date(substr(dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,-1),");
		buf.append("  decode((sign(to_date(substr(dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,0)))");
		buf.append("  -sum (decode(patchstatus,2,decode((sign(to_date(deductdata, 'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd'))*");
		buf.append("   sign(to_date(deductdata, 'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd'))),1,0,1),0)");
//		if(type==1){
//			buf.append("  )as anum,");
//		}else{
			buf.append("  )as rnum,");
//		}
		
		buf.append("  sum(decode((sign(to_date(deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(ntotalmny,0)-nvl(nbookmny,0)))+");
		buf.append("  sum(decode(vstatus,10," );
		buf.append("  decode((sign(to_date(substr(dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(nsubtotalmny,0)+nvl(nbookmny,0)),");
		buf.append("  decode((sign(to_date(substr(dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(nsubtotalmny,0))");
//		if(type==1){
//			buf.append("  ))as antotalmny from cn_contract c");
//		}else if(type==2){
//			buf.append("  ))as rntotalmny from cn_contract c");
//		}else{
			buf.append("  ))as rntotalmny from cn_contract c");
			buf.append("  left join bd_corp b on c.pk_corp=b.pk_corp");
//		}
		buf.append("  where nvl(c.isncust,'N')='N' and nvl(c.dr,0) = 0 and (c.vstatus=1 or c.vstatus=9 or c.vstatus=10) ");
//		if(type==3){
			buf.append("  and b.vprovince is not null ");
			buf.append("  group by c.pk_corp,b.vprovince  order by b.vprovince)w group by w.vprovince");
//		}else{
//			buf.append(" and CONCAT(c.pk_corpk,c.pk_corp) ");
//			if(type==1){
//				buf.append(" not ");
//			}
//			buf.append("  in(select CONCAT(pk_corpk,pk_corp) from cn_contract where nvl(dr,0)=0 and vstatus in(1,9)  and substr(deductdata,1,10)<?)");
//			buf.append("  and  ");
//			buf.append(SqlUtil.buildSqlForIn("c.pk_corp ",pks));
//			buf.append("  group by c.pk_corp");
//		}
		return buf;
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
	
	@Override
	public List<ManagerVO> queryDetail(ManagerVO qvo) throws DZFWarpException {//补提单的合同
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sp.addParam(qvo.getDbegindate());
		sp.addParam(qvo.getDenddate());
		sp.addParam(qvo.getPk_corp());//补提单合同，数量为0
		sql.append(" select decode(patchstatus,2,0,1) as anum,pk_confrim as pk_corp ,deductdata as denddate, ");
		sql.append(" nvl(ntotalmny,0)-nvl(nbookmny,0) as antotalmny, " );   
		sql.append(" nvl(ndeductmny,0) as ndeductmny,nvl(ndedrebamny,0) as ndedrebamny from cn_contract " );   
		sql.append(" where nvl(isncust,'N')='N' and nvl(dr,0) = 0 and (vstatus=1 or vstatus=9 or vstatus=10) and " );
		sql.append(" deductdata>=? and deductdata<=? and pk_corp=? " );
		List<ManagerVO> qryYSH =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
	    sql = new StringBuffer();
	    sql.append(" select 0 as anum,pk_confrim as pk_corp,substr(dchangetime,0,10)as denddate, ");
		sql.append(" nvl(nsubtotalmny,0) as antotalmny,nvl(nsubdeductmny,0) as ndeductmny , " );   
		sql.append(" nvl(nsubdedrebamny,0) as ndedrebamny from cn_contract " );   
		sql.append(" where nvl(isncust,'N')='N' and nvl(dr,0) = 0 and vstatus=9  and" );
		sql.append(" substr(dchangetime,0,10)>=? and substr(dchangetime,0,10)<=? and pk_corp=?" );
		List<ManagerVO> qryYZZ =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
		sql = new StringBuffer();
	    sql.append(" select -1 as anum,pk_confrim as pk_corp,substr(dchangetime,0,10)as denddate, ");
		sql.append(" nvl(nsubtotalmny,0)+nvl(nbookmny,0) as antotalmny,nvl(nsubdeductmny,0) as ndeductmny , " );   
		sql.append(" nvl(nsubdedrebamny,0) as ndedrebamny from cn_contract " );   
		sql.append(" where nvl(isncust,'N')='N' and nvl(dr,0) = 0  and vstatus=10 and" );
		sql.append(" substr(dchangetime,0,10)>=? and substr(dchangetime,0,10)<=? and pk_corp=?" );
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
	
	/**
	 * 1、type=1列表新增(提单量+代账费)2、type=2:列表续费；
	 * 
	 */
	private List<ManagerVO> qryBoth(ManagerVO qvo,String[] pks,Integer type) throws DZFWarpException {
		StringBuffer sql = null;
		SQLParameter sp=new SQLParameter();
		sp.addParam(qvo.getDbegindate());
		sp.addParam(qvo.getDenddate());
		
		sql = new StringBuffer();//非补提单的合同
		sql.append(" select 1 as anum,c.pk_corp, ");//补提单合同，数量为0
		sql.append(" nvl(c.ntotalmny,0)-nvl(c.nbookmny,0) as antotalmny, " );   
		sql.append(" nvl(c.ndeductmny,0) as ndeductmny,nvl(c.ndedrebamny,0) as ndedrebamny from cn_contract c" );   
		sql.append(" where nvl(c.isncust,'N')='N' and nvl(c.dr,0) = 0 and (c.vstatus=1 or c.vstatus=9 or c.vstatus=10) and " );
		sql.append(" c.deductdata>=? and c.deductdata<=? and nvl(c.patchstatus,0)!=2 and " );
		sql.append(SqlUtil.buildSqlForIn("c.pk_corp ",pks));
		sql.append(" and c.pk_corpk ");
		if(type==1){
			sql.append(" not ");
		}
		sql.append(" in (select pk_corpk from cn_contract where nvl(dr,0)=0 and vstatus in(1,9) and deductdata<c.deductdata)");
		List<ManagerVO> qryNBT =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
		sql = new StringBuffer();//补提单的合同(补提单合同不能作废，变更)
		sql.append(" select 0 as anum,c.pk_corp, ");
		sql.append(" nvl(c.ntotalmny,0)-nvl(c.nbookmny,0) as antotalmny, " );   
		sql.append(" nvl(c.ndeductmny,0) as ndeductmny,nvl(c.ndedrebamny,0) as ndedrebamny from cn_contract c,cn_contract t" );   
		sql.append(" where nvl(c.isncust,'N')='N' and nvl(c.dr,0) = 0 and c.vstatus=1  and " );
		sql.append(" c.deductdata>=? and c.deductdata<=? and nvl(c.patchstatus,0)=2 and c.pk_source=t.pk_contract and" );
		sql.append(SqlUtil.buildSqlForIn("c.pk_corp ",pks));
		sql.append(" and c.pk_corpk ");
		if(type==1){
			sql.append(" not ");
		}
		sql.append(" in (select pk_corpk from cn_contract where nvl(dr,0)=0 and vstatus in(1,9) and deductdata<t.deductdata)");
		List<ManagerVO> qryYBT =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
	    sql = new StringBuffer();//变更的情况
	    sql.append(" select 0 as anum,c.pk_corp, ");
		sql.append(" nvl(c.nsubtotalmny,0) as antotalmny,nvl(c.nsubdeductmny,0) as ndeductmny , " );   
		sql.append(" nvl(c.nsubdedrebamny,0) as ndedrebamny from cn_contract c" );   
		sql.append(" where nvl(c.isncust,'N')='N' and nvl(c.dr,0) = 0 and c.vstatus=9  and" );
		sql.append(" substr(c.dchangetime,0,10)>=? and substr(c.dchangetime,0,10)<=? and " );
		sql.append(SqlUtil.buildSqlForIn("c.pk_corp ",pks));
		sql.append(" and c.pk_corpk ");
		if(type==1){
			sql.append(" not ");
		}
		sql.append(" in(select pk_corpk from cn_contract where nvl(dr,0)=0 and vstatus in(1,9) and deductdata<c.deductdata)");
		List<ManagerVO> qryYBG =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
		
		sql = new StringBuffer();//作废的情况
	    sql.append(" select -1 as anum,c.pk_corp, ");
		sql.append(" nvl(c.nsubtotalmny,0)+nvl(c.nbookmny,0) as antotalmny,nvl(c.nsubdeductmny,0) as ndeductmny , " );   
		sql.append(" nvl(c.nsubdedrebamny,0) as ndedrebamny from cn_contract c" );   
		sql.append(" where nvl(c.isncust,'N')='N' and nvl(c.dr,0) = 0  and c.vstatus=10 and" );
		sql.append(" substr(c.dchangetime,0,10)>=? and substr(c.dchangetime,0,10)<=? and ");
		sql.append(SqlUtil.buildSqlForIn("c.pk_corp ",pks));
		sql.append(" and c.pk_corpk ");
		if(type==1){
			sql.append(" not ");
		}
		sql.append(" in (select pk_corpk from cn_contract where nvl(dr,0)=0 and vstatus in(1,9) and deductdata<c.deductdata)");
		List<ManagerVO> qryYZF =(List<ManagerVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ManagerVO.class));
			
		ArrayList<ManagerVO> vos=new ArrayList<>();
		if(qryNBT!=null && qryNBT.size()>0){
			vos.addAll(qryNBT);
		}
		if(qryYBT!=null && qryYBT.size()>0){
			vos.addAll(qryYBT);
		}
		if(qryYBG!=null && qryYBG.size()>0){
			vos.addAll(qryYBG);
		}
		if(qryYZF!=null && qryYZF.size()>0){
			vos.addAll(qryYZF);
		}
		return vos;
	}
	
}
