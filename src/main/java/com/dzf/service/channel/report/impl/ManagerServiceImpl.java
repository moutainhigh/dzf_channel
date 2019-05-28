package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IManagerService;

@Service("mana_manager")
public class ManagerServiceImpl implements IManagerService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	/**
	 * 排序
	 * @param retList
	 */
	protected void sortList(List<ManagerVO> retList){
		Collections.sort(retList, new Comparator<ManagerVO>() {
			@Override
			public int compare(ManagerVO o1, ManagerVO o2) {
				int sort=0;
				if(o1.getAreacode()!=null && o2.getAreacode()!=null){
					if(o1.getAreacode().equals(o2.getAreacode())){
						sort=o1.getVprovince().compareTo(o2.getVprovince());
					}else{
						sort=o1.getAreacode().compareTo(o2.getAreacode());
					}
				}else if(o1.getAreacode()==null && o2.getAreacode()!=null){
					sort=1;
				}else if(o2.getAreacode()==null && o1.getAreacode()!=null){
					sort=-1;
				}else{
					sort=o1.getVprovince().compareTo(o2.getVprovince());
				}
				return sort;
			}
		});
	}
	
	/**
	 * 设置默认值
	 * @param managerVO
	 */
	protected void setDefult(ManagerVO managerVO) {
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
 	}
	
	/**
	 * 查询报表 基本数据
	 * @param qvo
	 * @param map
	 * @param pk_corps
	 * @return
	 */
	protected ArrayList<ManagerVO> queryCommon(ManagerVO qvo,Map<String, ManagerVO> map,ArrayList<String> pk_corps) {
		if(pk_corps!=null && pk_corps.size()>0){
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
			
			spm.addParam(qvo.getDbegindate());
			spm.addParam(qvo.getDenddate());
			spm.addParam(qvo.getDbegindate());
			spm.addParam(qvo.getDenddate());
			spm.addParam(qvo.getDbegindate());
			spm.addParam(qvo.getDenddate());
			
			buf=new StringBuffer();//扣款金额(预付款,返点款)
			buf.append("  select yt.pk_corp,");
			buf.append("  sum(decode((sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
			buf.append("  sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(t.ndeductmny,0)))+");
			buf.append("  sum(decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
			buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(t.nsubdeductmny,0)))as ndeductmny,");
			
			buf.append("  sum(decode((sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
			buf.append("  sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(t.ndedrebamny,0)))+");
			buf.append("  sum(decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
			buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(t.nsubdedrebamny,0)))as ndedrebamny");
			
			buf.append("  from cn_contract t INNER JOIN ynt_contract yt ON t.pk_contract = yt.pk_contract ");
			buf.append("  where nvl(yt.isncust,'N')='N' and nvl(t.dr,0) = 0 and nvl(yt.dr,0) = 0 and (yt.vstatus=1 or yt.vstatus=9 or yt.vstatus=10) and ");
			buf.append(SqlUtil.buildSqlForIn("yt.pk_corp ",pks));
			buf.append("  group by yt.pk_corp");
			List<ManagerVO> list3 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), spm, new BeanListProcessor(ManagerVO.class));
			
			buf=new StringBuffer();//预存款余额 返点余额
			buf.append(" select (nvl(npaymny,0)-nvl(nusedmny,0)) as outmny,pk_corp,ipaytype anum from cn_balance where nvl(dr,0) = 0 and (ipaytype=2 or ipaytype=3) and ");
			buf.append(SqlUtil.buildSqlForIn("pk_corp ",pks));
			List<ManagerVO> list4 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), null, new BeanListProcessor(ManagerVO.class));
			
			buf=new StringBuffer();//小规模及一般纳税人的数量
			buf.append(" select count(pk_corp) anum,chargedeptname corpname, fathercorp pk_corp from bd_corp  ");
			buf.append(" where nvl(dr,0)=0 and nvl(isaccountcorp,'N')='N' and chargedeptname is not null and ");
			buf.append(SqlUtil.buildSqlForIn("fathercorp ",pks));
			buf.append(" group by fathercorp,chargedeptname  ");
			List<ManagerVO> list5 =(List<ManagerVO>)singleObjectBO.executeQuery(buf.toString(), null, new BeanListProcessor(ManagerVO.class));
			
			spm.addParam(qvo.getDbegindate());
			spm.addParam(qvo.getDenddate());
			spm.addParam(qvo.getDbegindate());
			spm.addParam(qvo.getDenddate());
			spm.addParam(qvo.getDbegindate());
			spm.addParam(qvo.getDenddate());
			
			//地区提单量,地区合同代账费,（计算客单价）
			List<ManagerVO> list6 =(List<ManagerVO>)singleObjectBO.executeQuery(getSql(pks,2).toString(), spm, new BeanListProcessor(ManagerVO.class));
			//提单量,合同代账费,
//			spm.addParam(qvo.getDbegindate());
			List<ManagerVO> list7 =(List<ManagerVO>)singleObjectBO.executeQuery(getSql(pks,1).toString(), spm, new BeanListProcessor(ManagerVO.class));
			
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
		     if(list3!=null&&list3.size()>0){//扣款金额(预付款,返点款)
		    	 for (ManagerVO managerVO : list3) {
						ManagerVO vo = map.get(managerVO.getPk_corp());
						vo.setNdeductmny(managerVO.getNdeductmny());
						vo.setNdedrebamny(managerVO.getNdedrebamny());
						map.put(managerVO.getPk_corp(),vo);
					}
		     }
		     if(list4!=null&&list4.size()>0){//预存款余额,返点余额
		    	 for (ManagerVO managerVO : list4) {
					ManagerVO vo = map.get(managerVO.getPk_corp());
					if(managerVO.getAnum()==2){
						vo.setOutmny(managerVO.getOutmny());
					}else{
						vo.setRetmny(managerVO.getOutmny());
					}
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
		     if(list6!=null && list6.size()>0){//客单价
		    	 for (ManagerVO managerVO : list6) {
		    		 promap.put(managerVO.getVprovince(), managerVO);
				}
		    	Iterator<Entry<String, ManagerVO>> iterator = map.entrySet().iterator();
		    	while (iterator.hasNext()){
		    		Entry<String, ManagerVO> entry = iterator.next();
		    		ManagerVO mapvo =(ManagerVO)entry.getValue();
		    		if(promap.containsKey(mapvo.getVprovince())){
		    			ManagerVO managerVO = promap.get(mapvo.getVprovince());
			    		mapvo.setUnitprice(CommonUtil.getDZFDouble(managerVO.getRntotalmny()).div(CommonUtil.getDZFDouble(managerVO.getRnum())));
		    		}
		    	}
		     }
			if(list7!=null&&list7.size()>0){//提单量, 合同代账费
				 for (ManagerVO managerVO : list7) {
						ManagerVO vo = map.get(managerVO.getPk_corp());
						if(managerVO.getIsxq()!=null && managerVO.getIsxq().booleanValue()){//续签
							vo.setRnum(managerVO.getRnum());
							vo.setRntotalmny(managerVO.getRntotalmny());
						}else{//新增
							vo.setAnum(managerVO.getRnum());
							vo.setAntotalmny(managerVO.getRntotalmny());
						}
						map.put(managerVO.getPk_corp(),vo);
					}
			}
		}
		Collection<ManagerVO> manas = map.values();
		ArrayList<ManagerVO> list= new ArrayList<ManagerVO>(manas);
		return list;
	}
	
	/**
	 * 查询语句（提单量，合同代账费）
	 * @param buf
	 */
	protected StringBuffer getSql(String[] pks,Integer type) {
		StringBuffer buf=new StringBuffer();
		if(type==2){
			buf.append("  select sum(w.rnum) as rnum,");
			buf.append("  sum(w.rntotalmny) as rntotalmny,w.vprovince as vprovince from ( ");
			buf.append("  select c.pk_corp,b.vprovince, ");
		}else{
			buf.append("  select c.pk_corp,nvl(yt.isxq,'N') isxq,");
		}
		buf.append("  sum(decode((sign(to_date(c.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(c.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,1))");
		buf.append("  +sum(decode(c.vstatus,10," );
		buf.append("  decode((sign(to_date(substr(c.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(c.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,-1),");
		buf.append("  decode((sign(to_date(substr(c.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(c.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,0)))");
		buf.append("  -sum (case when (yt.patchstatus=2 or yt.patchstatus=5) then (");
		buf.append("  decode((sign(to_date(c.deductdata, 'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(c.deductdata, 'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd'))),1,0,1)");
		buf.append("   )else 0 end ");
		
		buf.append("  )as rnum,");
		
		buf.append("  sum(decode((sign(to_date(c.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(c.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(yt.nchangetotalmny,0)-nvl(yt.nbookmny,0)))+");
		buf.append("  sum(decode(c.vstatus,10," );
		buf.append("  decode((sign(to_date(substr(c.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(c.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(c.nsubtotalmny,0)+nvl(yt.nbookmny,0)),");
		buf.append("  decode((sign(to_date(substr(c.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
		buf.append("  sign(to_date(substr(c.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(c.nsubtotalmny,0))");
		if(type==2){
			buf.append("  ))as rntotalmny from cn_contract c");
			buf.append("  INNER join ynt_contract yt on c.pk_contract=yt.pk_contract");
			buf.append("  left join bd_corp b on c.pk_corp=b.pk_corp");
		}else{
			buf.append("  ))as rntotalmny from cn_contract c");
			buf.append("  INNER join ynt_contract yt on c.pk_contract=yt.pk_contract");
		}
		buf.append("  where nvl(yt.isncust,'N')='N' and nvl(yt.dr,0) = 0 and nvl(c.dr,0) = 0 and (yt.vstatus=1 or yt.vstatus=9 or yt.vstatus=10) ");
		if(type==2){
			buf.append("  and b.vprovince is not null ");
			buf.append("  group by c.pk_corp,b.vprovince  order by b.vprovince)w group by w.vprovince");
		}else{
			buf.append(" and  ");
			buf.append(SqlUtil.buildSqlForIn("c.pk_corp ",pks));
			buf.append(" group by c.pk_corp,yt.isxq ");
		}
		return buf;
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
