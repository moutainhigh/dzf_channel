package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.service.channel.IChnPayBalanceService;

@Service("chnpaybalanceser")
public class ChnPayBalanceServiceImpl implements IChnPayBalanceService{

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo);
		return multBodyObjectBO.queryDataTotal(ChnBalanceVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnBalanceVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(paramvo);
		List<ChnBalanceVO> list = (List<ChnBalanceVO>) multBodyObjectBO.queryDataPage(ChnBalanceVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
		if(list != null && list.size() > 0){
			CorpVO accvo = null;
			for(ChnBalanceVO vo : list){
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(accvo != null){
					vo.setCorpname(accvo.getUnitname());
				}
				vo.setNbalance(SafeCompute.sub(vo.getNpaymny(), vo.getNusedmny()));
			}
		}
		return list;
	}

	/**
	 * 获取查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT * FROM cn_balance WHERE nvl(dr,0) = 0 \n");
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		sql.append(" order by ts desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnDetailVO> queryDetail(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getDetailQry(paramvo);
		List<ChnDetailVO> list = (List<ChnDetailVO>) singleObjectBO.executeQuery(sqpvo.getSql(), 
				sqpvo.getSpm(), new BeanListProcessor(ChnDetailVO.class));
		if(list != null && list.size() > 0){
			List<ChnDetailVO> oederlist = getOrderList(list);
			CorpVO accvo = null;
			DZFDouble coutbal = DZFDouble.ZERO_DBL;
			DZFDouble balance = DZFDouble.ZERO_DBL;
			for(ChnDetailVO vo : oederlist){
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(accvo != null){
					vo.setCorpname(accvo.getUnitname());
				}
				if(vo.getIpaytype() != null){
					switch (vo.getIpaytype()){
					case 1:
						vo.setVpaytypename("加盟费");
						break;
					case 2:
						vo.setVpaytypename("预付款");
						break;
					}
				}
				balance = SafeCompute.sub(vo.getNpaymny(), vo.getNusedmny());
				vo.setNbalance(SafeCompute.add(coutbal, balance));
				coutbal = vo.getNbalance();
			}
			return oederlist;
		}
		return list;
	}
	
	/**
	 * 同一天的数据，按照收款在前，付款在后排列
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<ChnDetailVO> getOrderList(List<ChnDetailVO> list) throws DZFWarpException{
		List<ChnDetailVO> relist = new ArrayList<ChnDetailVO> ();
		Map<DZFDate,List<ChnDetailVO>> map = new HashMap<DZFDate,List<ChnDetailVO>>();
		List<ChnDetailVO> newlist = null;
		List<ChnDetailVO> oldlist = null;
		List<DZFDate> keylist = new ArrayList<DZFDate>();
		for(ChnDetailVO vo : list){
			if(!map.containsKey(vo.getDoperatedate())){
				newlist = new ArrayList<ChnDetailVO>();
				newlist.add(vo);
				map.put(vo.getDoperatedate(), newlist);
				keylist.add(vo.getDoperatedate());
			}else{
				oldlist = map.get(vo.getDoperatedate());
				oldlist.add(vo);
			}
		}
		for(DZFDate key : keylist){
			newlist = map.get(key);
			Collections.sort(newlist,new Comparator<ChnDetailVO>() {

				@Override
				public int compare(ChnDetailVO o1, ChnDetailVO o2) {
					return o1.getIopertype().compareTo(o2.getIopertype());
				}
				
			});
			for(ChnDetailVO vo : newlist){
				relist.add(vo);
			}
		}
		return relist;
	}
	
	/**
	 * 获取明细查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getDetailQry(QryParamVO paramvo) throws DZFWarpException{
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT * FROM cn_detail WHERE nvl(dr,0) = 0 \n");
		sql.append(" AND ipaytype = ? \n");
		spm.addParam(paramvo.getQrytype());
		sql.append(" AND pk_corp = ? \n");
		spm.addParam(paramvo.getPk_corp());
		sql.append(" order by ts asc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
}
