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
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.CustManageRepVO;
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.model.sys.sys_set.BDTradeVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.ICustManageRep;
import com.dzf.service.channel.report.ICustNumMoneyRep;

@Service("custmanagerepser")
public class CustManageRepImpl implements ICustManageRep {
	
    @Autowired
    private SingleObjectBO singleObjectBO;
    
	@Autowired
	private ICustNumMoneyRep custServ;

	@Override
	public List<CustManageRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<CustManageRepVO> retlist = new ArrayList<CustManageRepVO>();
		List<String> corplist = new ArrayList<String>();
		List<String> countcorplist = new ArrayList<String>();
		HashMap<String, CustManageRepVO> map = queryCorps(paramvo, corplist);
		
		List<CustCountVO> custlist = custServ.queryCustNum(paramvo, 1,corplist);//查询客户数量
		//计算客户分类信息
		Map<String, CustNumMoneyRepVO> custmap = custServ.countCustNumByType(custlist, 1, corplist, countcorplist);
		CustNumMoneyRepVO custnumvo = null;
		CustManageRepVO retvo = null;
		List<String> codelist = qryIndustryCode(paramvo);//排行前五行业主键
		List<CustCountVO> custnumlist = qryIndustryNum(paramvo);
		Map<String, CustCountVO> industmap = qryIndustryMap(custnumlist, codelist);
		codelist.add("others");
		String[] industrys = new String[]{"小规模纳税人","一般纳税人"};
		CustCountVO industryvo = null;
		DZFDouble rate = DZFDouble.ZERO_DBL;
		Integer countnum = null;
		if(corplist != null && corplist.size() > 0){
			CorpVO corpvo = null;
			UserVO uservo = null;
			String key = "";
			for(String pk_corp : corplist){
				retvo = map.get(pk_corp);
				retvo.setPk_corp(pk_corp);
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if(corpvo != null){
					retvo.setVcorpname(corpvo.getUnitname());
					retvo.setVprovince(corpvo.getCitycounty());
				}
				uservo=UserCache.getInstance().get(retvo.getUserid(), pk_corp);
				if(uservo!=null){
					retvo.setUsername(uservo.getUser_name());
				}
				uservo=UserCache.getInstance().get(retvo.getCuserid(), pk_corp);
				if(uservo!=null){
					retvo.setCusername(uservo.getUser_name());
				}
				custnumvo = custmap.get(pk_corp);
				if(custnumvo != null){
					retvo.setIcustsmall(custnumvo.getIstockcustsmall());
					retvo.setIcusttaxpay(custnumvo.getIstockcusttaxpay());
				}
				//获取各个行业的值
				Integer custsum = 0;
				if(codelist != null && codelist.size() > 0){
					for(int i = 0; i < codelist.size(); i++){
						for(int j = 0; j <industrys.length; j++){
							key = pk_corp + "" + codelist.get(i) + "" + industrys[j];
							industryvo = industmap.get(key);
							custsum = 0;
							custsum = addInteger(retvo.getIcustsmall(),retvo.getIcusttaxpay());
							if(industryvo != null){
								if(j == 0){
									retvo.setAttributeValue("icustsmall"+(i+1), industryvo.getNum());
									countnum = CommonUtil.getInteger(retvo.getAttributeValue("icustsmall"+(i+1)));
									rate = getCustRate(countnum, custsum);
//									rate = getCustRate(countnum, retvo.getIcustsmall());
									retvo.setAttributeValue("icustratesmall"+(i+1), rate);
								}else if(j == 1){
									retvo.setAttributeValue("icusttaxpay"+(i+1), industryvo.getNum());
									countnum = CommonUtil.getInteger(retvo.getAttributeValue("icusttaxpay"+(i+1)));
									rate = getCustRate(countnum, custsum);
//									rate = getCustRate(countnum, retvo.getIcusttaxpay());
									retvo.setAttributeValue("icustratetaxpay"+(i+1), rate);
								}
							}
						}
					}
				}
				if(retvo.getIcustsmall() == null && retvo.getIcusttaxpay() == null){
					continue;
				}
				retlist.add(retvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 占比计算
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		return num3.div(num4).multiply(100);
	}
	
	/**
	 * 整数相加
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private Integer addInteger(Integer num1, Integer num2) throws DZFWarpException {
		num1 = num1 == null ? 0 : num1;
		num2 = num2 == null ? 0 : num2;
		return num1 + num2;
	}
	
	/**
	 * 行业分类汇总计算
	 * @param custnumlist
	 * @param pklist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, CustCountVO> qryIndustryMap(List<CustCountVO> custnumlist,	List<String> codelist) throws DZFWarpException {
		Map<String, CustCountVO> retmap = new HashMap<String, CustCountVO>();
		if(custnumlist != null && custnumlist.size() > 0){
			String key = "";
			CustCountVO retvo = null;
			for(CustCountVO vo : custnumlist){
				if(StringUtil.isEmpty(vo.getPk_corp()) || StringUtil.isEmpty(vo.getIndustrycode()) || StringUtil.isEmpty(vo.getChargedeptname())){
					continue;
				}
				if(codelist.contains(vo.getIndustrycode())){
					key = vo.getPk_corp()+""+vo.getIndustrycode()+""+vo.getChargedeptname();
				}else{
					key = vo.getPk_corp()+"others"+vo.getChargedeptname();
				}
				retvo = new CustCountVO();
				if(!retmap.containsKey(key)){
					retvo.setKey(key);
					retvo.setNum(vo.getNum());
					retmap.put(key, retvo);
				}else{
					retvo = retmap.get(key);
					retvo.setNum(CommonUtil.addInteger(retvo.getNum(), vo.getNum()));
				}
			}
		}
		return retmap;
	}
	
	/**
	 * 行业按照会计公司主键、行业（大类编码）、纳税人资格进行分类汇总
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> qryIndustryNum(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT fathercorp as pk_corp, \n") ;
		sql.append("       chargedeptname, \n") ; 
		sql.append("       industrycode,\n") ; 
		sql.append("       count(pk_corp) as num \n") ; 
		sql.append("  FROM (SELECT p.fathercorp,\n") ; 
		sql.append("               p.pk_corp,\n") ; 
		sql.append("               p.chargedeptname,\n") ; 
		sql.append("               (case \n") ; 
		sql.append("                 when instr(trade.tradecode, 'Z') > 0 then \n") ; 
		sql.append("                  trade.tradecode \n") ; 
		sql.append("                 else \n") ; 
		sql.append("                  substr(trade.tradecode, 0, 2) \n") ; 
		sql.append("               end) industrycode \n") ; 
		sql.append("          FROM bd_corp p \n") ; 
		sql.append("          LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n") ; 
		sql.append("          LEFT JOIN ynt_bd_trade trade ON p.industry = trade.pk_trade \n") ; 
		sql.append("         WHERE nvl(p.dr, 0) = 0 \n") ; 
		sql.append("           AND nvl(t.dr, 0) = 0 \n") ; 
		sql.append("           AND nvl(p.isseal, 'N') = 'N'\n") ; 
		sql.append("           AND nvl(p.ishasaccount, 'N') = 'Y'\n") ; //已建账
		sql.append("           AND nvl(t.ischannel, 'N') = 'Y'\n") ; 
		sql.append("           AND p.fathercorp NOT IN \n") ; 
		sql.append("               (SELECT f.pk_corp \n") ; 
		sql.append("                  FROM ynt_franchisee f \n") ; 
		sql.append("                 WHERE nvl(dr, 0) = 0 \n") ; 
		sql.append("                   AND nvl(f.isreport, 'N') = 'Y')\n") ; 
		sql.append("           AND p.chargedeptname IS NOT NULL \n") ;
		sql.append("           AND p.Industry IS NOT NULL )\n") ;
		sql.append(" WHERE industrycode IS NOT NULL \n") ; 
		sql.append("   AND chargedeptname IS NOT NULL \n") ; 
		sql.append(" GROUP BY fathercorp, chargedeptname, industrycode \n") ; 
		sql.append(" ORDER BY num DESC");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}
	
	/**
	 * 排行前五行业主键
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<String> qryIndustryCode(QryParamVO paramvo) throws DZFWarpException {
		List<String> retlist = new ArrayList<String>();
		List<CustCountVO> countlist = queryIndustry(paramvo);
		if(countlist != null && countlist.size() > 0){
			for(CustCountVO vo : countlist){
				if(!StringUtil.isEmpty(vo.getIndustrycode())){
					retlist.add(vo.getIndustrycode());
					if(retlist != null && retlist.size() == 5){
						break;
					}
				}
			}
		}
		return retlist;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<CustCountVO> queryIndustry(QryParamVO paramvo) throws DZFWarpException {
		List<CustCountVO> retlist = new ArrayList<CustCountVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT industrycode, count(pk_corp) as num \n") ;
		sql.append("  FROM (SELECT p.pk_corp,\n") ; 
		sql.append("               (case \n") ; 
		sql.append("                 when instr(trade.tradecode, 'Z') > 0  then \n") ; 
		sql.append("                  trade.tradecode \n") ; 
		sql.append("                 else \n") ; 
		sql.append("                  substr(trade.tradecode, 0, 2)  \n") ; 
		sql.append("               end) industrycode \n") ; 
		sql.append("          FROM bd_corp p \n") ; 
		sql.append("          LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n") ; 
		sql.append("          LEFT JOIN ynt_bd_trade trade ON p.industry = trade.pk_trade \n") ; 
		sql.append("         WHERE nvl(p.dr, 0) = 0 \n") ; 
		sql.append("           AND nvl(t.dr, 0) = 0 \n") ; 
		sql.append("           AND nvl(p.isseal, 'N') = 'N'\n") ; 
		sql.append("           AND nvl(p.ishasaccount, 'N') = 'Y'\n") ;
		sql.append("           AND nvl(t.ischannel, 'N') = 'Y'\n") ; 
		sql.append("           AND p.fathercorp NOT IN \n") ; 
		sql.append("               (SELECT f.pk_corp \n") ; 
		sql.append("                  FROM ynt_franchisee f \n") ; 
		sql.append("                 WHERE nvl(dr, 0) = 0 \n") ; 
		sql.append("                   AND nvl(f.isreport, 'N') = 'Y')\n") ; 
		sql.append("           AND p.chargedeptname IS NOT NULL \n") ;
		sql.append("           AND p.Industry IS NOT NULL )\n") ;
		sql.append(" WHERE industrycode IS NOT NULL \n") ; 
		sql.append(" GROUP BY industrycode \n") ; 
		sql.append(" ORDER BY num DESC \n");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if(list != null && list.size() > 0){
			Map<String,String> trademap = queryTrade();
			for(CustCountVO vo : list){
				if(!StringUtil.isEmpty(vo.getIndustrycode())){
					vo.setIndustryname(trademap.get(vo.getIndustrycode()));
					retlist.add(vo);
					if(retlist != null && retlist.size() == 5 && list.size() > 5){
						CustCountVO countvo = new CustCountVO();
						countvo.setIndustryname("其他类");
						retlist.add(countvo);
						break;
					}
				}
			}
		}
		return retlist;
	}
	
	/**
	 * 查询行业信息
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,String> queryTrade() throws DZFWarpException {
		Map<String,String> map = new HashMap<String,String>();
		String sql = " nvl(dr,0) = 0 ";
		BDTradeVO[] tradeVOs = (BDTradeVO[]) singleObjectBO.queryByCondition(BDTradeVO.class, sql.toString(), null);
		if(tradeVOs != null && tradeVOs.length > 0){
			for(BDTradeVO vo : tradeVOs){
				if(!StringUtil.isEmpty(vo.getTradecode()) ){
					if(vo.getTradecode().indexOf("Z") != -1){
						map.put(vo.getTradecode(), vo.getTradename());
					}else{
						if(vo.getTradecode().length() == 2){
							map.put(vo.getTradecode(), vo.getTradename());
						}
					}
				}
			}
		}
		return map;
	}
	
	public HashMap<String,CustManageRepVO> queryCorps(QryParamVO paramvo, List<String> corplist) throws DZFWarpException {
		Boolean flg=checkIsLeader(paramvo);
		HashMap<String,CustManageRepVO> map=new HashMap<>();
		List<String> vprovinces=new ArrayList<>();
		List<CustManageRepVO> qryCharge=qryCharge(paramvo,flg);
		for (CustManageRepVO custManageRepVO : qryCharge) {
			Boolean flag=false;//判断查询框的培训经理的过滤
			if(StringUtil.isEmpty(paramvo.getCuserid()) || paramvo.getCuserid().equals(custManageRepVO.getCuserid())){
				flag=true;
			}
			if(!map.containsKey(custManageRepVO.getPk_corp())&& flag){
				map.put(custManageRepVO.getPk_corp(), custManageRepVO);
				corplist.add(custManageRepVO.getPk_corp());
			}
			if(!vprovinces.contains(custManageRepVO.getVprovince())){
				vprovinces.add(custManageRepVO.getVprovince());
			}
		}
		List<CustManageRepVO> qryNotCharge=qryNotCharge(paramvo,flg,vprovinces);
		for (CustManageRepVO custManageRepVO : qryNotCharge) {
			Boolean flag=false;//判断查询框的培训经理的过滤
			if(StringUtil.isEmpty(paramvo.getCuserid()) || paramvo.getCuserid().equals(custManageRepVO.getCuserid())){
				flag=true;
			}
			if(!map.containsKey(custManageRepVO.getPk_corp())&& flag){
				map.put(custManageRepVO.getPk_corp(), custManageRepVO);
				corplist.add(custManageRepVO.getPk_corp());
			}else{
				if(!StringUtil.isEmpty(custManageRepVO.getCuserid())){
					map.put(custManageRepVO.getPk_corp(),custManageRepVO);
				}
			}
		}
		return map;
	}
	
	//查询 培训负责人
	private List<CustManageRepVO> qryCharge(QryParamVO paramvo,Boolean flg) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid,b.vprovname,b.vprovince,p.innercode,");
		sql.append(" (case when b.pk_corp is null then null when b.pk_corp!=p.pk_corp then null else b.userid end) cuserid ");
		sql.append(" from bd_corp p right join cn_chnarea_b b on  p.vprovince=b.vprovince  " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 " );
	    sql.append(" and nvl(p.ischannel,'N')='Y' and nvl(p.isaccountcorp,'N') = 'Y' and p.fathercorp = ? and b.type=2 " );
	    sql.append(" and nvl(b.ischarge,'N')='Y' " );
	    sp.addParam(IDefaultValue.DefaultGroup);
	    if(!flg){
			sql.append("  and (a.userid=? or b.userid=? )");
			sp.addParam(paramvo.getUser_name());
			sp.addParam(paramvo.getUser_name());
		}
		if (!StringUtil.isEmpty(paramvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			sp.addParam(paramvo.getAreaname());
		}
		if (paramvo.getVprovince() != null && paramvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			sp.addParam(paramvo.getVprovince());
		}
		if (!StringUtil.isEmpty(paramvo.getCuserid())) {
			sql.append(" and b.userid=? ");// 渠道经理
			sp.addParam(paramvo.getCuserid());
		}
	    List<CustManageRepVO> list =(List<CustManageRepVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(CustManageRepVO.class));
	    return list;
	}
	
	private List<CustManageRepVO> qryNotCharge(QryParamVO paramvo,Boolean flg,List<String> vprovinces) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid,b.userid cuserid,b.vprovname,b.vprovince,p.innercode ");
		sql.append(" from bd_corp p right join cn_chnarea_b b on  p.pk_corp=b.pk_corp " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 and b.type=2 " );
	    sql.append(" and nvl(p.isaccountcorp,'N') = 'Y' and p.fathercorp = ? and nvl(b.ischarge,'N')='N'" );
	    sp.addParam(IDefaultValue.DefaultGroup);
	    if(!flg){
			if(vprovinces!=null && vprovinces.size()>0){
				sql.append("  and ((a.userid=? or b.userid=? ) or ");
				sql.append(SqlUtil.buildSqlForIn("b.vprovince",vprovinces.toArray(new String[vprovinces.size()])));
				sql.append(" )");
			}else{
				sql.append("  and (a.userid=? or b.userid=? )");
			}
			sp.addParam(paramvo.getUser_name());
			sp.addParam(paramvo.getUser_name());
		}
		if (!StringUtil.isEmpty(paramvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			sp.addParam(paramvo.getAreaname());
		}
		if (paramvo.getVprovince() != null && paramvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			sp.addParam(paramvo.getVprovince());
		}
		if (!StringUtil.isEmpty(paramvo.getCuserid())) {
			sql.append(" and b.userid=? ");// 渠道经理
			sp.addParam(paramvo.getCuserid());
		}
	    List<CustManageRepVO> vos =(List<CustManageRepVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(CustManageRepVO.class));
		return vos;
	}
	
	private boolean checkIsLeader(QryParamVO paramvo) {
		String sql="select vdeptuserid corpcode,vcomuserid corpname,vgroupuserid pk_corpk from cn_leaderset where nvl(dr,0)=0";
		List<QryParamVO> list =(List<QryParamVO>)singleObjectBO.executeQuery(sql, null, new BeanListProcessor(QryParamVO.class));
		if(list!=null&&list.size()>0){
			QryParamVO vo=list.get(0);
			if(paramvo.getUser_name().equals(vo.getCorpcode())||paramvo.getUser_name().equals(vo.getCorpname())||paramvo.getUser_name().equals(vo.getPk_corpk())){
				return true;
			}
		}
		return false;
	}

}
