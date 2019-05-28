package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IFranchiseeManService;
import com.dzf.service.pub.IPubService;

@Service("manfranchisee")
public class FranchiseeManServiceImpl extends ManCommonServiceImpl implements IFranchiseeManService {
	
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
	
 	private Map<String, ManagerVO> qryAllCorp(ManagerVO qvo,ArrayList<String> pk_corps) {
 		Map<String, ManagerVO> map = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select y.region_name vprovname, ");
		sql.append("       account.vprovince, ");
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
		if(list!=null && list.size()>0){
			Map<String, UserVO> opermap = pubService.getManagerMap(qrytype);// 渠道运营
			Map<Integer, ChnAreaVO> chnmap = pubService.getChnMap(qvo.getAreaname(), qrytype);// 渠道运营
			ChnAreaVO areaVO ;
			String corpName;
			UserVO userVO;
			for (ManagerVO managerVO : list) {
				if(chnmap.containsKey(managerVO.getVprovince())){
					corpName = CodeUtils1.deCode(managerVO.getCorpname());
					if(StringUtil.isEmpty(qvo.getCorpname()) || corpName.indexOf(qvo.getCorpname()) != -1){
						areaVO = chnmap.get(managerVO.getVprovince());
						managerVO.setAreaname(areaVO.getAreaname());
						managerVO.setUsername(areaVO.getUsername());
						
						managerVO.setCorpname(corpName);
						userVO = opermap.get(managerVO.getPk_corp());
						if (userVO != null) {
							managerVO.setCusername(userVO.getUser_name());
						}
						setDefult(managerVO);
						pk_corps.add(managerVO.getPk_corp());
						map.put(managerVO.getPk_corp(), managerVO);
					}
				}
			}
		}
		return map;
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
					inserVO.setAreaname(areaName+"合计");
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
				areaName = areaNow;
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
