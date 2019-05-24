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
import com.dzf.model.pub.IStatusConstant;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IFranchiseeManService;
import com.dzf.service.pub.IPubService;

@Service("manfranchisee")
public class FranchiseeManServiceImpl implements IFranchiseeManService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
    @Autowired
    private IPubService pubService;
    
	private String wheresql = QueryUtil.getWhereSql();
	
	private int qrytype = IStatusConstant.IQUDAO;
	
	@Override
	public List<ManagerVO> query(ManagerVO qvo) throws DZFWarpException {
		ArrayList<ManagerVO> retList = new ArrayList<>();
		Integer level = pubService.getDataLevel(qvo.getUserid());
		
		ArrayList<String> pk_corps = new ArrayList<>();
		Map<String, ManagerVO> manaMap = new HashMap<>();
		if(level!=null && level==1){
			manaMap = qryAllCorp(qvo,pk_corps);
		}
		if(pk_corps!=null && pk_corps.size()!=0){
			retList = queryCommon(qvo,manaMap,pk_corps);
			sortList(retList);
		}
		return setInsertList(retList);
	}
	
	/**
	 * 排序
	 * @param retList
	 */
	private void sortList(List<ManagerVO> retList){
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
	
 	private Map<String, ManagerVO> qryAllCorp(ManagerVO qvo,ArrayList<String> pk_corps) {
 		Map<String, ManagerVO> map = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select y.region_name vprovname, ");
		sql.append("       account.pk_corp, ");
		sql.append("       account.innercode, ");
		sql.append("       account.unitname corpname, ");
		sql.append("       account.vprovince ");
		sql.append("  from bd_account account ");
		sql.append("  left join ynt_area y on account.vprovince = y.region_id ");
		sql.append("                      and y.parenter_id = 1 ");
		sql.append("                      and nvl(y.dr, 0) = 0 ");
		sql.append(" where ").append(wheresql);
		sql.append("   and account.vprovince is not null "); 
		if (qvo.getVprovince() != null && qvo.getVprovince() != -1) {
			sql.append(" and account.vprovince=? ");// 省市
			sp.addParam(qvo.getVprovince());
		}
		if(!StringUtil.isEmpty(qvo.getCuserid())){
			String[] corps = pubService.getManagerCorp(qvo.getCuserid(), qrytype);
			if(corps != null && corps.length > 0){
				String where = SqlUtil.buildSqlForIn(" account.pk_corp", corps);
				sql.append(" AND ").append(where);
			}else{
				sql.append(" AND account.pk_corp is null \n") ; 
			}
		}
		List<ManagerVO> list =(List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ManagerVO.class));
		Map<Integer, String> areaMap = pubService.getAreaMap(qvo.getAreaname(), 1);
		String areaName ;
		String corpName;
		for (ManagerVO managerVO : list) {
			if(areaMap.containsKey(managerVO.getVprovince())){
				corpName = CodeUtils1.deCode(managerVO.getCorpname());
				if(StringUtil.isEmpty(qvo.getCorpname()) || managerVO.getCorpname().indexOf(qvo.getCorpname()) != -1){
					areaName = areaMap.get(managerVO.getVprovince());
					managerVO.setAreaname(areaName);
					managerVO.setCorpname(corpName);
					setDefult(managerVO);
					pk_corps.add(managerVO.getPk_corp());
					map.put(managerVO.getPk_corp(), managerVO);
				}
			}
		}
		return map;
 	}
	 	
	 	
 	private void setDefult(ManagerVO managerVO) {
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
	
	private ArrayList<ManagerVO> queryCommon(ManagerVO qvo,Map<String, ManagerVO> map,ArrayList<String> pk_corps) {
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
		     if(list6!=null&&list6.size()>0){//客单价
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
	 * 设置默认值
	 * @param qvo
	 * @param vos
	 * @param map
	 * @param pk_corps
	 */
	
	
	/**
	 * 查询语句（提单量，合同代账费）
	 * @param buf
	 */
	private StringBuffer getSql(String[] pks,Integer type) {
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
	
	public List<ManagerVO>  setInsertList(List<ManagerVO> list) throws DZFWarpException {
		List<ManagerVO> retlist=new ArrayList<ManagerVO>();
		if(list!=null && list.size()!=0){
			
		    Integer xgmNum = 0;	
		    Integer ybrNum = 0;	
		    Integer rnum = 0;	
		    Integer anum = 0;	
		    DZFDouble bondmny = DZFDouble.ZERO_DBL;	
		    DZFDouble predeposit = DZFDouble.ZERO_DBL;	
		    DZFDouble rntlmny = DZFDouble.ZERO_DBL;	
		    DZFDouble antlmny = DZFDouble.ZERO_DBL;
		    DZFDouble ndemny = DZFDouble.ZERO_DBL;	
		    DZFDouble nderebmny = DZFDouble.ZERO_DBL;	
		    DZFDouble outmny = DZFDouble.ZERO_DBL;	
		    DZFDouble retmny = DZFDouble.ZERO_DBL;
		    
		    Integer xgmNum1 = 0;	
		    Integer ybrNum1 = 0;	
		    Integer rnum1 = 0;	
		    Integer anum1 = 0;	
		    DZFDouble bondmny1 = DZFDouble.ZERO_DBL;	
		    DZFDouble predeposit1 = DZFDouble.ZERO_DBL;	
		    DZFDouble rntlmny1 = DZFDouble.ZERO_DBL;	
		    DZFDouble antlmny1 = DZFDouble.ZERO_DBL;
		    DZFDouble ndemny1 = DZFDouble.ZERO_DBL;	
		    DZFDouble nderebmny1 = DZFDouble.ZERO_DBL;	
		    DZFDouble outmny1 = DZFDouble.ZERO_DBL;	
		    DZFDouble retmny1 = DZFDouble.ZERO_DBL;
			
		    String provName = null;//缓存上一次
		    String areaName = null;//缓存上一次
		    String areaNow = null;
		    ManagerVO inserVO;
			for (ManagerVO managerVO : list) {
				if(provName!=null && !provName.equals(managerVO.getVprovname())){
					inserVO = new ManagerVO();
					inserVO.setVprovname(provName+"小计");
					inserVO.setXgmNum(xgmNum);
					inserVO.setYbrNum(ybrNum);
					inserVO.setRnum(rnum);
					inserVO.setAnum(anum);
					inserVO.setBondmny(bondmny);
					inserVO.setPredeposit(predeposit);
					inserVO.setRntotalmny(rntlmny);
					inserVO.setAntotalmny(antlmny);
					inserVO.setNdeductmny(ndemny);
					inserVO.setNdedrebamny(nderebmny);
					inserVO.setOutmny(outmny);
					inserVO.setRetmny(CommonUtil.getDZFDouble(retmny));
					retlist.add(inserVO);
					
					
					xgmNum1 += xgmNum;
					ybrNum1 += ybrNum;
					rnum1 += rnum;
					anum1 += anum;
					bondmny1 = bondmny1.add(bondmny);
					predeposit1 =  predeposit1.add(predeposit);
					rntlmny1 = rntlmny1.add(rntlmny);
					antlmny1 =antlmny1.add(antlmny);
					ndemny1 = ndemny1.add(ndemny);
					nderebmny1 = nderebmny1.add(nderebmny);
					outmny1 = outmny1.add(outmny);
					retmny1 =retmny1.add(CommonUtil.getDZFDouble(retmny));
					
					xgmNum = managerVO.getXgmNum();
					ybrNum = managerVO.getYbrNum();
					rnum =  managerVO.getRnum();
					anum = managerVO.getAnum();
					bondmny = CommonUtil.getDZFDouble(managerVO.getBondmny());	
					predeposit = CommonUtil.getDZFDouble(managerVO.getPredeposit());	
					rntlmny = CommonUtil.getDZFDouble(managerVO.getRntotalmny());	
					antlmny = CommonUtil.getDZFDouble(managerVO.getAntotalmny());
					ndemny = CommonUtil.getDZFDouble(managerVO.getNdeductmny());	
					nderebmny = CommonUtil.getDZFDouble(managerVO.getNdedrebamny());	
					outmny = CommonUtil.getDZFDouble(managerVO.getOutmny());	
					retmny = CommonUtil.getDZFDouble(managerVO.getRetmny());
				}else{
					xgmNum += managerVO.getXgmNum();
					ybrNum +=  managerVO.getYbrNum();
					rnum +=  managerVO.getRnum();
					anum +=  managerVO.getAnum();
					
					bondmny = bondmny.add(CommonUtil.getDZFDouble(managerVO.getBondmny()));
					predeposit =  predeposit.add(CommonUtil.getDZFDouble(managerVO.getPredeposit()));
					rntlmny =  rntlmny.add(CommonUtil.getDZFDouble(managerVO.getRntotalmny()));
					antlmny = antlmny.add(CommonUtil.getDZFDouble(managerVO.getAntotalmny()));
					ndemny =  ndemny.add(CommonUtil.getDZFDouble(managerVO.getNdeductmny()));
					nderebmny =  nderebmny.add(CommonUtil.getDZFDouble(managerVO.getNdedrebamny()));
					outmny =  outmny.add(CommonUtil.getDZFDouble(managerVO.getOutmny()));
					retmny = retmny.add(CommonUtil.getDZFDouble(managerVO.getRetmny()));
				}
				provName = managerVO.getVprovname();
				
				areaNow = StringUtil.isEmpty(managerVO.getAreaname()) ? "无大区" : managerVO.getAreaname();
				if(areaName!=null && !areaName.equals(areaNow)){
					inserVO = new ManagerVO();
					inserVO.setAreaname(areaNow+"合计");
					inserVO.setXgmNum(xgmNum1);
					inserVO.setYbrNum(ybrNum1);
					inserVO.setRnum(rnum1);
					inserVO.setAnum(anum1);
					inserVO.setBondmny(bondmny1);
					inserVO.setPredeposit(predeposit1);
					inserVO.setRntotalmny(rntlmny1);
					inserVO.setAntotalmny(antlmny1);
					inserVO.setNdeductmny(ndemny1);
					inserVO.setNdedrebamny(nderebmny1);
					inserVO.setOutmny(outmny1);
					inserVO.setRetmny(retmny1);
					retlist.add(inserVO);
					
					xgmNum1 = 0;
					ybrNum1 = 0;
					rnum1 = 0;
					anum1 =0;
					bondmny1 =   DZFDouble.ZERO_DBL;
					predeposit1 =  DZFDouble.ZERO_DBL;
					rntlmny1 =  DZFDouble.ZERO_DBL;
					antlmny1 =  DZFDouble.ZERO_DBL;
					ndemny1 =  DZFDouble.ZERO_DBL;
					nderebmny1 =   DZFDouble.ZERO_DBL;
					outmny1 =   DZFDouble.ZERO_DBL;
					retmny1 =  DZFDouble.ZERO_DBL;
				}
				areaName = managerVO.getAreaname();
				retlist.add(managerVO);
			}
			
			inserVO = new ManagerVO();
			inserVO.setVprovname(provName+"小计");
			inserVO.setXgmNum(xgmNum);
			inserVO.setYbrNum(ybrNum);
			inserVO.setRnum(rnum);
			inserVO.setAnum(anum);
			inserVO.setBondmny(bondmny);
			inserVO.setPredeposit(predeposit);
			inserVO.setRntotalmny(rntlmny);
			inserVO.setAntotalmny(antlmny);
			inserVO.setNdeductmny(ndemny);
			inserVO.setNdedrebamny(nderebmny);
			inserVO.setOutmny(outmny);
			inserVO.setRetmny(CommonUtil.getDZFDouble(retmny));
			retlist.add(inserVO);
			
			inserVO = new ManagerVO();
			inserVO.setAreaname(areaName+"合计");
			inserVO.setXgmNum(xgmNum1+xgmNum);
			inserVO.setYbrNum(ybrNum1+ybrNum);
			inserVO.setRnum(rnum1+rnum);
			inserVO.setAnum(anum1+anum);
			inserVO.setBondmny(bondmny1.add(bondmny));
			inserVO.setPredeposit(predeposit1.add(predeposit));
			inserVO.setRntotalmny(rntlmny1.add(rntlmny));
			inserVO.setAntotalmny(antlmny1.add(antlmny));
			inserVO.setNdeductmny(ndemny1.add(ndemny));
			inserVO.setNdedrebamny(nderebmny1.add(nderebmny));
			inserVO.setOutmny(outmny1.add(outmny));
			inserVO.setRetmny(retmny1.add(retmny));
			retlist.add(inserVO);
			
		}
		return retlist;
	}
	
}
