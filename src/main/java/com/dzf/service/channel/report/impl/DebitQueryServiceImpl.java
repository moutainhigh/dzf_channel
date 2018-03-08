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
		int length= paramvo.getDbegindate().length();
		List<DebitQueryVO> headers = queryHeader(paramvo);
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select a.pk_corp,a.innercode as corpcode,a.unitname as corpname,a.djoindate as chndate ,");
		sql.append(" sum(nvl(contract.ndeductmny,0) - nvl(contract.nretdedmny,0)) as ndeductmny,");
		sql.append(" sum(nvl(contract.ndedrebamny,0) - nvl(contract.nretrebmny,0)) as ndedrebamny,");
		sql.append(" balance.outymny, balance.outfmny");
		sql.append(" from bd_account a");
        sql.append(" left join (select pk_corp,sum(decode(ipaytype,2,nvl(npaymny,0) - nvl(nusedmny,0),0)) as outymny,");
        sql.append("   			 sum(decode(ipaytype,3,nvl(npaymny,0) - nvl(nusedmny,0),0)) as outfmny");
        sql.append("   			from cn_balance where ipaytype!=1 and nvl(dr,0)=0 group by pk_corp) balance ");
        sql.append(" 	on a.pk_corp = balance.pk_corp");
        sql.append(" left join cn_contract contract on a.pk_corp = contract.pk_corp and (contract.vdeductstatus = 1 or contract.vdeductstatus = 9) ");
        sql.append(" where a.ischannel = 'Y'and nvl(a.dr,0)=0  and nvl(contract.dr, 0) = 0 ");
        if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
            sql.append(" and a.pk_corp  in (" + corpIdS + ")");
        }
        sql.append(" and substr(contract.deductdata,0,"+length+")>=? and substr(contract.deductdata,0,"+length+")<=?");
        sql.append(" group by a.pk_corp,a.innercode ,a.unitname,a.djoindate,balance.outymny,balance.outfmny");
        sp.addParam(paramvo.getDbegindate());
        sp.addParam(paramvo.getDenddate());
        sql.append(" order by a.innercode ");
		List<DebitQueryVO> list =(List<DebitQueryVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(DebitQueryVO.class));
		HashMap<String, List<DebitQueryVO>> map = queryDetail(paramvo);
		if(list != null && list.size() > 0){
			QueryDeCodeUtils.decKeyUtils(new String[]{"corpname"}, list, 2);
			String[] str={"one","two","three","four","five","six","seven",
				        "eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"};
			List<DebitQueryVO> retlist = new ArrayList<DebitQueryVO>();
		    for(DebitQueryVO bvo : list){
		    	List<DebitQueryVO> vos = map.get(bvo.getPk_corp());
		    	HashMap<String, DebitQueryVO> map1=new HashMap<>();
		        for (DebitQueryVO debitQueryVO : vos) {
					map1.put(debitQueryVO.getHead(), debitQueryVO);
				}
		        for(int i=0;i<headers.size();i++){
		        	if(map1.containsKey(headers.get(i).getHead())){
		        		bvo.setAttributeValue(str[i]+"1", map1.get(headers.get(i).getHead()).getNdeductmny());
		        		bvo.setAttributeValue(str[i]+"2", map1.get(headers.get(i).getHead()).getNdedrebamny());
		        	}
		        }
		        if(!StringUtil.isEmpty(paramvo.getCorpname())){
		        	if(bvo.getCorpcode().indexOf(paramvo.getCorpname()) != -1 
		        			|| bvo.getCorpname().indexOf(paramvo.getCorpname()) != -1){
		        		retlist.add(bvo);
		        	}
		        }
		    }
		    if(!StringUtil.isEmpty(paramvo.getCorpname())){
		    	return retlist;
		    }
		}
		return list;
	}
	
	/**
	 * 查询期间，加盟商扣款已确认金额
	 * @param vo
	 */
	private HashMap<String,List<DebitQueryVO>> queryDetail(DebitQueryVO vo){
		int length= vo.getDbegindate().length();
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append(" select sum(nvl(a.ndeductmny,0) - nvl(a.nretdedmny,0)) as ndeductmny, ");
        sql.append(" sum(nvl(a.ndedrebamny,0) - nvl(a.nretrebmny,0)) as ndedrebamny, ");
        sql.append(" a.pk_corp,substr(a.deductdata,0,"+length+") as head");
        sql.append(" from cn_contract a where (a.vdeductstatus = 1 or a.vdeductstatus = 9) and nvl(a.dr,0)=0 ");
        sql.append(" and substr(a.deductdata,0,"+length+")>=? and substr(a.deductdata,0,"+length+")<=?  ");
        sp.addParam(vo.getDbegindate());
        sp.addParam(vo.getDenddate());
        if( null != vo.getCorps() && vo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(vo.getCorps());
            sql.append(" and a.pk_corp  in (" + corpIdS + ")");
        }
        sql.append(" group by a.pk_corp, substr(a.deductdata,0,"+length+")");
        List<DebitQueryVO> list = (List<DebitQueryVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(DebitQueryVO.class));
        HashMap<String, List<DebitQueryVO>> map = new HashMap<>();
        List<DebitQueryVO> alist;
        if(list != null && list.size() > 0){
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
