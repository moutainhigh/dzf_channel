package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.DebitQueryVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IDebitQueryService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("debitquery_ser")
public class DebitQueryServiceImpl implements IDebitQueryService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
    private IUserService userser;
	
	@Override
	public List<DebitQueryVO> queryHeader(DebitQueryVO qvo) throws DZFWarpException {
		String start = qvo.getDbegindate();
		String end = qvo.getDenddate();
		DebitQueryVO vo=null;
		ArrayList<DebitQueryVO> list=new ArrayList<>();
		if(start.length()==7){//2017-09
			qvo.setHead(start);
			list.add(qvo);
			start=start+"-01";
			end=end+"-01";
			int num=1;
			while(!start.equals(end)){
				DZFDate date=new DZFDate(start);
				Calendar cal=Calendar.getInstance();
				cal.setTime(date.toDate());
				cal.add(Calendar.MONTH,1);
				start=new DZFDate(cal.getTime()).toString();
				num++;
				vo=new DebitQueryVO();
				vo.setHead(start.substring(0,7));
				list.add(vo);
			}
			qvo.setNum(num);
		}else{//2017-09-09
			qvo.setHead(start);
			list.add(qvo);
			int num=1;
			while(!start.equals(end)){
				DZFDate date=new DZFDate(start);
				Calendar cal=Calendar.getInstance();
				cal.setTime(date.toDate());
				cal.add(Calendar.DATE,1);
				start=new DZFDate(cal.getTime()).toString();
				num++;
				vo=new DebitQueryVO();
				vo.setHead(start);
				list.add(vo);
			}
			qvo.setNum(num);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DebitQueryVO> query(DebitQueryVO paramvo) throws DZFWarpException {
		List<DebitQueryVO> headers = queryHeader(paramvo);
		int length= paramvo.getDbegindate().length();
		if(length==7){
			paramvo.setDbegindate(paramvo.getDbegindate()+"-01");
			DZFDate date=new DZFDate(paramvo.getDenddate()+"-01");
			Calendar cal=Calendar.getInstance();
			cal.setTime(date.toDate());
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DATE, -1);
			paramvo.setDenddate(new DZFDate(cal.getTime()).toString());
		}
		HashMap<String, List<DebitQueryVO>> map = queryDetail(paramvo,length);
		
		List<DebitQueryVO> shlist = new ArrayList<DebitQueryVO>();
		if(!map.isEmpty()){
		  String[] str={"one","two","three","four","five","six","seven","eight","nine","ten",
			          "eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eighteen","nineteen",
			          "twenty","twenty1","twenty2","twenty3","twenty4","twenty5","twenty6","twenty7","twenty8","twenty9",
			          "thirty","thirty1","thirty2","thirty3","thirty4","thirty5","thirty6","thirty7"};
			List<DebitQueryVO> retlist = new ArrayList<DebitQueryVO>();
			paramvo.setCorps(map.keySet().toArray(new String[0]));
			List<DebitQueryVO> list =qryChannel(paramvo);
			HashMap<String, DebitQueryVO> qrySumMny = qrySumMny(paramvo);
			DebitQueryVO getVO= null;
		    for(DebitQueryVO bvo : list){
		    	bvo.setCorpname(CodeUtils1.deCode(bvo.getCorpname()));
		    	getVO = qrySumMny.get(bvo.getPk_corp());
		    	bvo.setOutfmny(getVO.getOutfmny());
		    	bvo.setOutymny(getVO.getOutymny());
		    	List<DebitQueryVO> vos = map.get(bvo.getPk_corp());
		    	HashMap<String, DebitQueryVO> map1=new HashMap<>();
		        DZFDouble umny=DZFDouble.ZERO_DBL;
		        DZFDouble rmny=DZFDouble.ZERO_DBL;
		        for (DebitQueryVO debitQueryVO : vos) {
		        	umny=umny.add(debitQueryVO.getNdeductmny());
		        	rmny=rmny.add(debitQueryVO.getNdedrebamny());
					map1.put(debitQueryVO.getHead(), debitQueryVO);
				}
		        for(int i=0;i<headers.size();i++){
		        	if(map1.containsKey(headers.get(i).getHead())){
		        		bvo.setAttributeValue(str[i]+"1", map1.get(headers.get(i).getHead()).getNdeductmny());
		        		bvo.setAttributeValue(str[i]+"2", map1.get(headers.get(i).getHead()).getNdedrebamny());
		        	}
		        }
		        bvo.setNdeductmny(umny);
	        	bvo.setNdedrebamny(rmny);
		        if(!StringUtil.isEmpty(paramvo.getCorpname())){
		        	if(bvo.getCorpcode().indexOf(paramvo.getCorpname()) != -1 
		        			|| bvo.getCorpname().indexOf(paramvo.getCorpname()) != -1){
		        		retlist.add(bvo);
		        	}
		        }
		        shlist.add(bvo);
		    }
		    if(!StringUtil.isEmpty(paramvo.getCorpname())){
		    	return retlist;
		    }
		}
		return shlist;
	}
	
	/**
	 * 查询加盟商查询期间内的汇总金额
	 * @param paramvo
	 * @return
	 */
	private HashMap<String,DebitQueryVO> qrySumMny(DebitQueryVO paramvo) {
		HashMap<String,DebitQueryVO> map =new HashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select contract.pk_corp,balance.outymny, balance.outfmny");
		sql.append(" from cn_contract contract ");
        sql.append(" left join (select pk_corp,sum(decode(ipaytype,2,nvl(npaymny,0) - nvl(nusedmny,0),0)) as outymny,");
        sql.append("   			 sum(decode(ipaytype,3,nvl(npaymny,0) - nvl(nusedmny,0),0)) as outfmny");
        sql.append("   			from cn_balance where ipaytype!=1 and nvl(dr,0)=0 group by pk_corp) balance ");
        sql.append(" 	on contract.pk_corp = balance.pk_corp");
        sql.append(" where nvl(contract.dr, 0) = 0 ");
        sql.append(" and (contract.vstatus = 1 or contract.vstatus = 9 or contract.vstatus = 10) ");
        if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
            sql.append(" and contract.pk_corp  in (" + corpIdS + ")");
        }
        sql.append(" group by contract.pk_corp,balance.outymny,balance.outfmny");
		List<DebitQueryVO> list =(List<DebitQueryVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(DebitQueryVO.class));
		for (DebitQueryVO debitQueryVO : list) {
			map.put(debitQueryVO.getPk_corp(),debitQueryVO);
		}
		return map;
	}
	
	/**
	 * 查询期间，加盟商扣款已确认金额
	 * @param vo
	 */
	private HashMap<String,List<DebitQueryVO>> queryDetail(DebitQueryVO vo,int length){
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getDbegindate());
        sp.addParam(vo.getDenddate());
        sql.append(" select sum(nvl(a.ndeductmny,0)) as ndeductmny, ");
        sql.append(" sum(nvl(a.ndedrebamny,0)) as ndedrebamny, ");  
        sql.append(" a.pk_corp,substr(a.deductdata,0,"+length+") as head");
        sql.append(" from cn_contract a where (a.vstatus = 1 or a.vstatus = 9 or a.vstatus = 10) and nvl(a.dr,0)=0 ");
		sql.append(" and substr(a.deductdata,0,10)>=? and substr(a.deductdata,0,10)<=?  ");
	    if( null != vo.getCorps() && vo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(vo.getCorps());
            sql.append(" and a.pk_corp  in (" + corpIdS + ")");
        }
        sql.append(" group by a.pk_corp, substr(a.deductdata,0,"+length+")");
		List<DebitQueryVO> qryNormal =(List<DebitQueryVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(DebitQueryVO.class));
		sql = new StringBuffer();
		sql.append(" select sum(nvl(a.nsubdeductmny,0)) as ndeductmny, ");
        sql.append(" sum(nvl(a.nsubdedrebamny,0)) as ndedrebamny, ");  
        sql.append(" a.pk_corp,substr(a.dchangetime,0,"+length+") as head");
        sql.append(" from cn_contract a where (a.vstatus = 9 or a.vstatus = 10) and nvl(a.dr,0)=0 ");
		sql.append(" and substr(a.dchangetime,0,10)>=? and substr(a.dchangetime,0,10)<=?  ");
	    if( null != vo.getCorps() && vo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(vo.getCorps());
            sql.append(" and a.pk_corp  in (" + corpIdS + ")");
        }
        sql.append(" group by a.pk_corp, substr(a.dchangetime,0,"+length+")");
		List<DebitQueryVO> qryBian =(List<DebitQueryVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(DebitQueryVO.class));
		
		ArrayList<DebitQueryVO> list=new ArrayList<>();
		if(qryNormal!=null && qryNormal.size()>0){
			list.addAll(qryNormal);
		}
		if(qryBian!=null && qryBian.size()>0){
			list.addAll(qryBian);
		}
        HashMap<String, List<DebitQueryVO>> map = new HashMap<>();//放pk_corp
        HashMap<String, DebitQueryVO> map2 = new HashMap<>();//放pk_corp+time
        List<DebitQueryVO> alist=new ArrayList<>();
        if(list != null && list.size() > 0){
            for(DebitQueryVO bvo : list){
            	if(map2.containsKey(bvo.getPk_corp()+bvo.getHead())){
            		DebitQueryVO dvo=map2.get(bvo.getPk_corp()+bvo.getHead());
        			bvo.setNdedrebamny(bvo.getNdedrebamny().add(dvo.getNdedrebamny()));
        			bvo.setNdeductmny(bvo.getNdeductmny().add(dvo.getNdeductmny()));
        			map2.put(bvo.getPk_corp()+bvo.getHead(), bvo);
            	}else{
            		map2.put(bvo.getPk_corp()+bvo.getHead(), bvo);
            	}
            }
            list=new ArrayList<>(map2.values());
            for(DebitQueryVO bvo : list){
            	if(map.containsKey(bvo.getPk_corp())){
            		alist=map.get(bvo.getPk_corp());
            		alist.add(bvo);
            		map.put(bvo.getPk_corp(), alist);
            	}else{
            		alist=new ArrayList<>();
            		alist.add(bvo);
            		map.put(bvo.getPk_corp(), alist);
            	}
            }
        }
        return map;
	}
	
	/**
	 * 查询出，有扣款数据的加盟商————————————(这有机会，能抽取出来公共方法)
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<DebitQueryVO> qryChannel(DebitQueryVO paramvo) throws DZFWarpException {
		List<String> ids =new ArrayList<>();
		List<DebitQueryVO>  list = new ArrayList<>();
		HashMap<String, UserVO> queryUserMap = userser.queryUserMap("000001", true);
		List<DebitQueryVO>  nlist = qryNChannel(paramvo,ids,queryUserMap);
		List<DebitQueryVO>  Ylist = qryYChannel(paramvo,ids,queryUserMap);
		if(nlist != null && nlist.size() > 0){
			list.addAll(nlist);
		}
		if(Ylist != null && Ylist.size() > 0){
			list.addAll(Ylist);
		}
        if(list != null && list.size() > 0){
            Collections.sort(list, new Comparator<DebitQueryVO>(){
                @Override
                public int compare(DebitQueryVO o1, DebitQueryVO o2) {
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
            }
           );
        }
		return list;
	}
	
	/**
	 * 查询 非省市负责人的加盟商
	 */
	private List<DebitQueryVO> qryNChannel(DebitQueryVO paramvo,List<String> ids,HashMap<String, UserVO> queryUserMap ) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select account.pk_corp, \n");
		sql.append("        account.innercode as corpcode, \n");
		sql.append("        account.unitname as corpname, \n");
		sql.append("        account.djoindate as chndate, \n");
		sql.append("        account.channeltype, \n");
		sql.append("        cn.areaname,cn.userid, cb.vprovname, \n");
		sql.append("        cn.areacode,cb.vprovince, \n");
		sql.append("        cb.userid   as cuserid \n");
		sql.append("   from bd_account account \n");
		sql.append("	left join cn_chnarea_b cb on account.pk_corp=cb.pk_corp \n");
		sql.append("    left join cn_chnarea cn on cb.pk_chnarea = cn.pk_chnarea  \n");
		sql.append("      where nvl(cb.dr,0)=0 and nvl(cn.dr,0)=0  \n");
		sql.append("      and cb.type=1 and nvl(cb.ischarge,'N')='N'  \n");
        if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
            sql.append(" and account.pk_corp  in (" + corpIdS + ")");
        }
        if(!StringUtil.isEmpty(paramvo.getCuserid())){
        	sql.append(" and cb.userid=?   \n");
        	sp.addParam(paramvo.getCuserid());
        }
        sql.append(" and "+ QueryUtil.getWhereSql());
		sql.append(" order by cn.areacode ,account.innercode \n");
		List<DebitQueryVO> list =(List<DebitQueryVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(DebitQueryVO.class));
		UserVO uvo = null;
		for (DebitQueryVO debitQueryVO : list) {
			uvo =queryUserMap.get(debitQueryVO.getCuserid());
			if(uvo!=null){
				debitQueryVO.setCusername(uvo.getUser_name());
			}
			uvo = queryUserMap.get(debitQueryVO.getUserid());
			if(uvo!=null){
				debitQueryVO.setUsername(uvo.getUser_name());
			}
			ids.add(debitQueryVO.getPk_corp());
		}
		return list;
	}
	
	/**
	 * 查询 是省市负责人的加盟商(不包含已查询出来的非省市负责人的加盟商)
	 */
	private List<DebitQueryVO> qryYChannel(DebitQueryVO paramvo,List<String> ids,HashMap<String, UserVO> queryUserMap ) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select ba.pk_corp, \n");
		sql.append("        ba.innercode as corpcode, \n");
		sql.append("        ba.unitname as corpname, \n");
		sql.append("        ba.djoindate as chndate, \n");
		sql.append("        ba.channeltype, \n");
		sql.append("        cn.areaname,cn.userid, cb.vprovname, \n");
		sql.append("        cn.areacode,cb.vprovince, \n");
		sql.append("        case when cb.pk_corp=ba.pk_corp then cb.userid else null end as cuserid \n");
		sql.append("   from bd_account ba \n");
		sql.append("   left join cn_chnarea_b cb on ba.vprovince=cb.vprovince \n");
		sql.append("   left join cn_chnarea cn on cb.pk_chnarea = cn.pk_chnarea  \n");
		sql.append("      where nvl(ba.dr,0)=0 and nvl(cb.dr,0)=0 and nvl(cn.dr,0)=0  \n");
		sql.append("      and cb.type=1 and nvl(cb.ischarge,'N')='Y'  \n");
        if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
            sql.append(" and ba.pk_corp  in (" + corpIdS + ")");
        }
        if(ids!=null && ids.size()>0){
        	  String corpIdS = SqlUtil.buildSqlConditionForIn(ids.toArray(new String[ids.size()]));
              sql.append(" and ba.pk_corp not in (" + corpIdS + ")");
        }
        if(!StringUtil.isEmpty(paramvo.getCuserid())){
        	sql.append(" and cb.userid=?   \n");
        	sp.addParam(paramvo.getCuserid());
        }
		sql.append(" order by cn.areacode ,ba.innercode \n");
		List<DebitQueryVO> list =(List<DebitQueryVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(DebitQueryVO.class));
		UserVO uvo = null;
		HashMap<String, DebitQueryVO> map =new HashMap<>();
		for (DebitQueryVO debitQueryVO : list) {
			if(map.isEmpty() || !map.containsKey(debitQueryVO.getPk_corp())){
				map.put(debitQueryVO.getPk_corp(), debitQueryVO);
			}else if(!StringUtil.isEmpty(debitQueryVO.getCuserid())){
				map.put(debitQueryVO.getPk_corp(), debitQueryVO);
			}else{
				continue;
			}
			uvo = queryUserMap.get(debitQueryVO.getCuserid());
			if(uvo!=null){
				debitQueryVO.setCusername(uvo.getUser_name());
			}
			uvo = queryUserMap.get(debitQueryVO.getUserid());
			if(uvo!=null){
				debitQueryVO.setUsername(uvo.getUser_name());
			}
		}
		return new ArrayList<DebitQueryVO>(map.values());
	}
	
}
