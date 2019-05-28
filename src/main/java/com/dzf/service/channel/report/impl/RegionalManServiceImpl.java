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
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IRegionalManService;
import com.dzf.service.pub.IPubService;

@Service("manregional")
public class RegionalManServiceImpl extends ManagerServiceImpl implements IRegionalManService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
    @Autowired
    private IPubService pubService;
    
	private int qrytype = IStatusConstant.IQUDAO;
    
	private String wheresql = QueryUtil.getWhereSql();
	
	@Override
	public List<ManagerVO> query(ManagerVO qvo) throws DZFWarpException {
		ArrayList<ManagerVO> retList = new ArrayList<>();
		Integer level = pubService.getDataLevel(qvo.getUserid());
		
		ArrayList<String> pk_corps = new ArrayList<>();
		Map<String, ManagerVO> manaMap = new HashMap<>();
		if(level!=null && level<=2){
//			manaMap = qryChannel(qvo,pk_corps);
//			list = qryBoth(qvo,type,queryUserMap);//2大区 
		}
		if(pk_corps!=null && pk_corps.size()!=0){
			retList = queryCommon(qvo,manaMap,pk_corps);
			sortList(retList);
		}
		return retList;
	}
	
	/**
	 * 查询渠道总数据+区域总经理
	 * @param qvo
	 * @param type
	 * @return
	 */
	private List<ManagerVO> qryBoth(ManagerVO qvo,Integer type,HashMap<String, UserVO> queryUserMap ) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("  select a.areaname,a.areacode,a.userid,");
		sql.append("  	   y.region_name vprovname,");
		sql.append("       account.pk_corp, account.innercode, account.vprovince,b.ischarge isxq,");
		sql.append("       b.userid cuserid,b.pk_corp corpname");//, b.vprovince
		sql.append("  from bd_account account");
		sql.append("  left join ynt_area y on account.vprovince=y.region_id and y.parenter_id = 1 and nvl(y.dr, 0) = 0 ");
		sql.append("  left join cn_chnarea_b b on account.vprovince = b.vprovince and b.type = 1 and nvl(b.dr, 0) = 0");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea and a.type = 1 and nvl(a.dr, 0) = 0");
		sql.append(" where ").append(wheresql);
//		sql.append("   and nvl(p.isseal,'N')='N'"); 
//		sql.append("   and (p.sealeddate is null or p.sealeddate > ? ) "); 
		sql.append("   and account.vprovince is not null "); 
//		sp.addParam(new DZFDate());
	    if(type==2){// 区域总经理
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
		Boolean isQuery=true;
		if (!StringUtil.isEmpty(qvo.getCuserid())) {
			isQuery = false;
			String condition = null;
			List<String> qryPros = pubService.qryPros(qvo.getCuserid(), 1);
			if (qryPros != null && qryPros.size() > 0) {
				condition = SqlUtil.buildSqlForIn("b.vprovince", qryPros.toArray(new String[qryPros.size()]));
				sql.append(" and (" + condition + " or b.userid=? ) ");
			} else {
				sql.append(" and b.userid=? ");// 渠道经理
			}
			sp.addParam(qvo.getCuserid());
		}
		List<ManagerVO> list =(List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(ManagerVO.class));
		HashMap<String, ManagerVO> map = new HashMap<String, ManagerVO>();
		UserVO uservo;
		if(list!=null && list.size()>0){
			Boolean isPut=true;
			for (ManagerVO managerVO : list) {
				if(managerVO.getCorpname()==null || !(managerVO.getPk_corp().equals(managerVO.getCorpname()))){
					managerVO.setCuserid(null);
				}
				if(!isQuery &&(managerVO.getIsxq().booleanValue() || !StringUtil.isEmpty(managerVO.getCuserid()))){
					isPut=true;
				}else if(!isQuery){
					isPut=false;
				}
				if(!map.containsKey(managerVO.getPk_corp())&& isPut){
					map.put(managerVO.getPk_corp(), managerVO);
				}else if(!StringUtil.isEmpty(managerVO.getCuserid())&& isPut){
					map.put(managerVO.getPk_corp(), managerVO);
				}
				uservo = queryUserMap.get(managerVO.getUserid());
				if (uservo != null) {
					managerVO.setUsername(uservo.getUser_name());
				}
				uservo = queryUserMap.get(managerVO.getCuserid());
				if (uservo != null) {
					managerVO.setCusername(uservo.getUser_name());
				}
			}
			Collection<ManagerVO> manas = map.values();
			list= new ArrayList<ManagerVO>(manas);
		}
	    return list;
	}
	
	private void setDefult(ManagerVO qvo, List<ManagerVO> vos, Map<String, ManagerVO> map, ArrayList<String> pk_corps) {
		CorpVO cvo = null;
		UserVO uvo = null;
		for (ManagerVO managerVO : vos) {
			cvo = CorpCache.getInstance().get(null, managerVO.getPk_corp());
			if(cvo!=null){
				managerVO.setCorpname(cvo.getUnitname());
			}else{
				managerVO.setCorpname(null);
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
				if(!StringUtil.isEmpty(managerVO.getCorpname()) && managerVO.getCorpname().indexOf(qvo.getCorpname()) != -1){
					pk_corps.add(managerVO.getPk_corp());
					map.put(managerVO.getPk_corp(), managerVO);
				}
			}else{
				pk_corps.add(managerVO.getPk_corp());
				map.put(managerVO.getPk_corp(), managerVO);
			}
		}
	}	
	
}
