package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.DebitQueryVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IDebitQueryService;

@Service("debitquery_ser")
public class DebitQueryServiceImpl implements IDebitQueryService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
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
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select a.pk_corp,a.innercode as corpcode,a.unitname as corpname,a.djoindate as chndate,a.channeltype,");
		sql.append(" balance.outymny, balance.outfmny");
		sql.append(" from bd_account a");
        sql.append(" left join (select pk_corp,sum(decode(ipaytype,2,nvl(npaymny,0) - nvl(nusedmny,0),0)) as outymny,");
        sql.append("   			 sum(decode(ipaytype,3,nvl(npaymny,0) - nvl(nusedmny,0),0)) as outfmny");
        sql.append("   			from cn_balance where ipaytype!=1 and nvl(dr,0)=0 group by pk_corp) balance ");
        sql.append(" 	on a.pk_corp = balance.pk_corp");
        sql.append(" left join cn_contract contract on a.pk_corp = contract.pk_corp ");
        sql.append(" and (contract.vdeductstatus = 1 or contract.vdeductstatus = 9 or contract.vdeductstatus = 10) ");//
        sql.append(" where nvl(contract.dr, 0) = 0 ");
        if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
            sql.append(" and a.pk_corp  in (" + corpIdS + ")");
        }
        sql.append(" group by a.pk_corp,a.innercode ,a.unitname,a.djoindate,a.channeltype,balance.outymny,balance.outfmny");
        sql.append(" order by a.innercode ");
		List<DebitQueryVO> list =(List<DebitQueryVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(DebitQueryVO.class));
		HashMap<String, List<DebitQueryVO>> map = queryDetail(paramvo,length);
		List<DebitQueryVO> shlist = new ArrayList<DebitQueryVO>();
		if(list != null && list.size() > 0){
			QueryDeCodeUtils.decKeyUtils(new String[]{"corpname"}, list, 2);
			String[] str={"one","two","three","four","five","six","seven",
				        "eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"};
			List<DebitQueryVO> retlist = new ArrayList<DebitQueryVO>();
		    for(DebitQueryVO bvo : list){
		    	List<DebitQueryVO> vos = map.get(bvo.getPk_corp());
		    	if(vos==null){
		    		continue;
		    	}
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
        sql.append(" from cn_contract a where (a.vdeductstatus = 1 or a.vdeductstatus = 9 or a.vdeductstatus = 10) and nvl(a.dr,0)=0 ");
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
        sql.append(" from cn_contract a where (a.vdeductstatus = 9 or a.vdeductstatus = 10) and nvl(a.dr,0)=0 ");
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
}
