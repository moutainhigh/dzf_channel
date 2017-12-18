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
	public List<DebitQueryVO> query(DebitQueryVO vo) throws DZFWarpException {
		int length= vo.getDbegindate().length();
		List<DebitQueryVO> headers = queryHeader(vo);
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append(" select a.pk_corp,a.innercode as corpcode,a.unitname as corpname,a.begindate as chndate ,");
		sql.append(" sum(contract.ndeductmny)as ndeductmny,(balance.npaymny-balance.nusedmny) as outmny");
		sql.append(" from bd_account a");
        sql.append(" left join cn_balance balance on a.pk_corp = balance.pk_corp and balance.ipaytype = 2");
        sql.append(" left join cn_contract contract on a.pk_corp = contract.pk_corp and contract.vdeductstatus=2 ");
        sql.append(" where a.ischannel = 'Y'and nvl(a.dr,0)=0 and nvl(balance.dr, 0) = 0 and nvl(contract.dr, 0) = 0 ");
        if( null != vo.getCorps() && vo.getCorps().length > 0){
            String corpIdS = SqlUtil.buildSqlConditionForIn(vo.getCorps());
            sql.append(" and a.pk_corp  in (" + corpIdS + ")");
        }
        sql.append(" and substr(contract.deductdata,0,"+length+")>=? and substr(contract.deductdata,0,"+length+")<=?");
        sql.append(" group by a.pk_corp,a.innercode ,a.unitname,a.begindate,balance.npaymny,balance.nusedmny");
        sp.addParam(vo.getDbegindate());
        sp.addParam(vo.getDenddate());
        sql.append(" order by a.innercode ");
		List<DebitQueryVO> list =(List<DebitQueryVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(DebitQueryVO.class));
		HashMap<String, List<DebitQueryVO>> map = queryDetail(vo);
		if(list != null && list.size() > 0){
			 String[] str={"one","two","three","four","five","six","seven",
				        "eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"};
		    for(DebitQueryVO bvo : list){
		    	List<DebitQueryVO> vos = map.get(bvo.getPk_corp());
		    	HashMap<String, DebitQueryVO> map1=new HashMap<>();
		        for (DebitQueryVO debitQueryVO : vos) {
					map1.put(debitQueryVO.getHead(), debitQueryVO);
				}
		        for(int i=0;i<headers.size();i++){
		        	if(map1.containsKey(headers.get(i).getHead())){
		        		bvo.setAttributeValue(str[i], map1.get(headers.get(i).getHead()).getNdeductmny());
		        	}
		        }
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
        sql.append(" select a.pk_corp,sum(a.ndeductmny)as ndeductmny ,substr(a.deductdata,0,"+length+") as head");
        sql.append(" from cn_contract a where a.vdeductstatus=2 and nvl(a.dr,0)=0 ");
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
