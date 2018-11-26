package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.channel.report.PersonStatisVO;
import com.dzf.model.channel.report.UserDetailVO;
import com.dzf.model.jms.basicset.JMUserRoleVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IPersonStatis;

@Service("statisPerson")
public class PersonStatisServiceImpl extends DataCommonRepImpl implements IPersonStatis {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	private static String[] str={"jms03","jms04","jms10"};

	@Override
	public List<PersonStatisVO> query(QryParamVO paramvo) throws DZFWarpException, IllegalAccessException, Exception {
		List<PersonStatisVO> retlist = new ArrayList<PersonStatisVO>();
		HashMap<String, DataVO> map = queryCorps(paramvo,PersonStatisVO.class);
		List<String> corplist = null;
		if(map!=null && !map.isEmpty()){
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}
		if (corplist != null && corplist.size() > 0) {
			HashMap<String, PersonStatisVO> queryEmployNum = queryEmployNum(corplist);
			HashMap<String, Integer> queryUserNum = queryUserNum(corplist);
			List<PersonStatisVO> list = queryPersonStatis(corplist);
			PersonStatisVO setVO=new PersonStatisVO();
			PersonStatisVO getvo=new PersonStatisVO();
			DataVO data=null;
			CorpVO corpvo = null;
			UserVO uservo = null;
			String pk_corp =null;
			
			HashMap<String,Integer> Tmap = new HashMap<String,Integer>();

			for (PersonStatisVO personStatisVO : list) {
				if(retlist==null ||retlist.size()==0 || !retlist.contains(personStatisVO)){
					pk_corp=personStatisVO.getPk_corp();
					data =map.get(pk_corp);
					setVO = (PersonStatisVO)data;
					
					corpvo = CorpCache.getInstance().get(null, pk_corp);
					if (corpvo != null) {
						setVO.setCorpname(corpvo.getUnitname());
						setVO.setVprovname(corpvo.getCitycounty());
					}
					uservo = UserCache.getInstance().get(setVO.getUserid(), pk_corp);
					if (uservo != null) {
						setVO.setUsername(uservo.getUser_name());
					}
					uservo = UserCache.getInstance().get(setVO.getCuserid(), pk_corp);
					if (uservo != null) {
						setVO.setCusername(uservo.getUser_name());
					}
					
					getvo= queryEmployNum.get(pk_corp);
					if(getvo!=null){
						setVO.setLtotal(getvo.getLtotal());
						setVO.setLznum(getvo.getLznum());
					}
					setVO.setTotal(queryUserNum.get(pk_corp));
					
					setVO.setAttributeValue(personStatisVO.getAreaname(),1);
					setZhanBi(setVO,Tmap,personStatisVO);
					retlist.add(setVO);
				}else{
					getvo= retlist.get(retlist.indexOf(personStatisVO));
					Integer value = (Integer)getvo.getAttributeValue(personStatisVO.getAreaname());
					if(value!=null){
						getvo.setAttributeValue(personStatisVO.getAreaname(),value+1);
					}else{
						getvo.setAttributeValue(personStatisVO.getAreaname(), 1);
					}
					setZhanBi(getvo,Tmap,personStatisVO);
				}
			}
		}
		return retlist;
	}
	
	
	private List<PersonStatisVO> queryPersonStatis(List<String> corplist) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select sur.cuserid userid, sr.role_code areaname, su.pk_corp, ba.innercode");
		sql.append("          from sm_user_role sur");
		sql.append("         inner join sm_role sr on sr.pk_role = sur.pk_role");
		sql.append("                              and sr.roletype = 8");
		sql.append("         inner join sm_user su on sur.cuserid = su.cuserid");
		sql.append("                              and nvl(su.locked_tag,'N')= 'N'");
		sql.append("          left join bd_account ba on ba.pk_corp = su.pk_corp where");
		sql.append(SqlUtil.buildSqlForIn("su.pk_corp", corplist.toArray(new String[corplist.size()])));
		sql.append("         	and nvl(sur.dr,0)=0 and nvl(sr.dr,0)=0 and nvl(su.dr,0)=0  ");
		sql.append("         group by sur.cuserid, sr.role_code, su.pk_corp, ba.innercode");
		sql.append(" order by ba.innercode");
		List<PersonStatisVO> list=(List<PersonStatisVO>)singleObjectBO.executeQuery(sql.toString(),null, new BeanListProcessor(PersonStatisVO.class));
		return list;
	}
	
	/**
	 * 计算团队占比
	 * @param setVO
	 * @param Tmap
	 * @param cuserid
	 */
	private void setZhanBi(PersonStatisVO setVO,HashMap<String,Integer> Tmap,PersonStatisVO vo) {
		String key=null;
		if(Arrays.asList(str).contains(vo.getAreaname())){
			key=setVO.getPk_corp()+"xs"+vo.getUserid();
			if(Tmap==null ||Tmap.isEmpty()){
				Tmap.put(key,1);
				setVO.setXnum(1);
			}else if(!Tmap.containsKey(key)){
				Tmap.put(key,1);
				setVO.setXnum((setVO.getXnum()==null?0:setVO.getXnum())+1);
			}
		}else if(!"jms01".equals(vo.getAreaname())){
			key=setVO.getPk_corp()+"kj"+vo.getUserid();
			if(Tmap==null ||Tmap.isEmpty()){
				Tmap.put(key,1);
				setVO.setKnum(1);
			}else if(!Tmap.containsKey(key)){
				Tmap.put(key,1);
				setVO.setKnum((setVO.getKnum()==null?0:setVO.getKnum())+1);
			}
		}
		if(setVO.getXnum()!=null && setVO.getXnum()>0){
			setVO.setXtotal(new DZFDouble(setVO.getXnum()).div(setVO.getTotal()).multiply(100));
		}
		if(setVO.getKnum()!=null && setVO.getKnum()>0){
			setVO.setKtotal(new DZFDouble(setVO.getKnum()).div(setVO.getTotal()).multiply(100));
		}
	}

	/**
	 * 查询有权限的加盟商的用户数目
	 * @param corplist
	 * @return
	 */
	private HashMap<String, Integer> queryUserNum(List<String> corplist) {
		HashMap<String, Integer> map=new HashMap<>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(cuserid) total,pk_corp from sm_user su where nvl(su.dr,0)=0 and nvl(su.locked_tag,'N')= 'N' and ");
		sql.append(SqlUtil.buildSqlForIn("pk_corp", corplist.toArray(new String[corplist.size()])));
		sql.append(" group by pk_corp ");
		List<PersonStatisVO> list=(List<PersonStatisVO>)singleObjectBO.executeQuery(sql.toString(),null, new BeanListProcessor(PersonStatisVO.class));
		for (PersonStatisVO personStatisVO : list) {
			map.put(personStatisVO.getPk_corp(), personStatisVO.getTotal());
		} 
		return map;
	}
	
	/**
	 * 查询员工离职数和离职率
	 * @param corplist
	 * @return
	 */
	private HashMap<String, PersonStatisVO> queryEmployNum(List<String> corplist) {
		HashMap<String, PersonStatisVO> map=new HashMap<>();
		StringBuffer sql = new StringBuffer();
		sql.append("select a.total total, nvl(b.total, 0) lznum, a.pk_corp pk_corp");
		sql.append("  from (select count(pk_employee) total, pk_corp");
		sql.append("          from ynt_employee");
		sql.append("         where nvl(dr, 0) = 0");
		sql.append("         group by pk_corp) a");
		sql.append("  left join");
		sql.append(" (select count(pk_employee) total, pk_corp");
		sql.append("    from ynt_employee");
		sql.append("   where istatus = 2");
		sql.append("     and nvl(dr, 0) = 0");
		sql.append("   group by pk_corp) b on a.pk_corp = b.pk_corp");
		sql.append("   where ");
		sql.append(SqlUtil.buildSqlForIn("a.pk_corp", corplist.toArray(new String[corplist.size()])));
		List<PersonStatisVO> list=(List<PersonStatisVO>)singleObjectBO.executeQuery(sql.toString(),null, new BeanListProcessor(PersonStatisVO.class));
		DZFDouble lzTmp = DZFDouble.ZERO_DBL;
		for (PersonStatisVO personStatisVO : list) {
			if(personStatisVO.getLznum()!=null){
				lzTmp=new DZFDouble(personStatisVO.getLznum());
			}
			personStatisVO.setLtotal(lzTmp.div(personStatisVO.getTotal()).multiply(100d));
			map.put(personStatisVO.getPk_corp(), personStatisVO);
		} 
		return map;
	}


    @Override
    public List<UserDetailVO> queryUserDetail(QryParamVO paramvo) throws DZFWarpException {
        StringBuffer str = new StringBuffer();
        SQLParameter params = new SQLParameter();
        str.append("select distinct dept.deptname,us.user_code, us.user_name,us.cuserid as userid ");
        str.append("  from sm_user us");
        str.append("  left join ynt_department dept on dept.pk_department = us.pk_department");
        str.append("  left join sm_userole userr on userr.cuserid = us.cuserid");
        str.append("  left join sm_role sr on sr.pk_role = userr.pk_role  ");
        str.append("  where nvl(us.dr, 0) = 0 and nvl(sr.dr, 0) = 0 and nvl(dept.dr, 0) = 0");
        str.append("  and nvl(us.locked_tag,'N') = 'N' and us.pk_corp = ?");
        str.append("  group by dept.deptname, us.user_code, us.user_name,us.cuserid order by dept.deptname");
        params.addParam(paramvo.getPk_corp());
        List<UserDetailVO> list = (List<UserDetailVO>) singleObjectBO.executeQuery(str.toString(), params, new BeanListProcessor(UserDetailVO.class));
        
        HashMap<String, Integer> map = queryUserCorps(paramvo.getPk_corp());
        HashMap<String, String> mapUR = queryUserRoles(paramvo.getPk_corp());
        HashMap<String, Integer> mapC = queryUserCorpsNum(paramvo.getPk_corp());
        if(list != null && list.size() > 0 && map != null){
            for(UserDetailVO uvo : list){
                uvo.setCorpnum(CommonUtil.getInteger(map.get(uvo.getUserid())));
                uvo.setRolename(mapUR.get(uvo.getUserid()));
                uvo.setCorpnum1(mapC.get(uvo.getUserid() + "_小规模纳税人"));
                uvo.setCorpnum2(mapC.get(uvo.getUserid() + "_一般纳税人"));
            }
        }
        return list;
    }
    
    /**
     * 查询用户负责客户数
     * @author gejw
     * @time 下午3:04:57
     * @param pk_corp
     * @return
     */
    private HashMap<String, Integer> queryUserCorps(String pk_corp){
        StringBuffer sql = new StringBuffer();
        sql.append(" select uc.cuserid as userid,count(uc.pk_corpk) as corpnum from sm_user_corp uc ");
        sql.append(" join bd_corp corp on corp.pk_corp = uc.pk_corpk");
        sql.append(" where uc.pk_corp = ? and uc.pk_corpk != ? and nvl(uc.dr,0) = 0 ");
        sql.append(" and nvl(corp.isseal,'N') = 'N' and nvl(corp.isaccountcorp,'N') = 'N'");
        sql.append(" group by uc.cuserid ");
        SQLParameter parameter = new SQLParameter();
        parameter.addParam(pk_corp);
        parameter.addParam(pk_corp);
        ArrayList<UserDetailVO> list = (ArrayList<UserDetailVO>) singleObjectBO.executeQuery(sql.toString(), parameter, new BeanListProcessor(UserDetailVO.class));
        HashMap<String, Integer> map = new HashMap<>();
        if(list != null && list.size() > 0){
            for(UserDetailVO vo : list){
                if(!StringUtil.isEmpty(vo.getUserid())){
                    map.put(vo.getUserid(), vo.getCorpnum());
                }
            }
        }
        return map;
    }
    /**
     * 查询用户负责的客户，区分小规模和一般纳税人
     * @author gejw
     * @time 上午10:54:59
     * @param pk_corp
     * @return
     */
    private HashMap<String, Integer> queryUserCorpsNum(String pk_corp){
        StringBuffer sql = new StringBuffer();
        sql.append(" select uc.cuserid as userid,corp.chargedeptname,");
        sql.append(" decode(corp.chargedeptname,'小规模纳税人',count(uc.pk_corpk),count(uc.pk_corpk)) as corpnum1");
        sql.append(" from sm_user_corp uc ");
        sql.append(" join bd_corp corp on uc.pk_corpk = corp.pk_corp ");
        sql.append(" where uc.pk_corp = ? and uc.pk_corpk != ? ");
        sql.append(" and nvl(corp.isseal,'N') = 'N' and nvl(corp.isaccountcorp,'N') = 'N'");
        sql.append(" and corp.chargedeptname is not null and nvl(uc.dr,0) = 0  ");
        sql.append(" group by uc.cuserid,corp.chargedeptname ");
        SQLParameter parameter = new SQLParameter();
        parameter.addParam(pk_corp);
        parameter.addParam(pk_corp);
        ArrayList<UserDetailVO> list = (ArrayList<UserDetailVO>) singleObjectBO.executeQuery(sql.toString(), parameter, new BeanListProcessor(UserDetailVO.class));
        HashMap<String, Integer> map = new HashMap<>();
        if(list != null && list.size() > 0){
            for(UserDetailVO vo : list){
                if(!StringUtil.isEmpty(vo.getUserid())){
                    map.put(vo.getUserid()+"_"+vo.getChargedeptname(), vo.getCorpnum1());
                }
            }
        }
        return map;
    }
    
    private HashMap<String, String> queryUserRoles(String pk_corp){
        StringBuffer sql = new StringBuffer();
        sql.append("select distinct us.cuserid,sr.role_name from sm_user us");
        sql.append(" join sm_userole ur on ur.cuserid = us.cuserid");
        sql.append(" join sm_role sr on ur.pk_role = sr.pk_role");
        sql.append(" where nvl(us.dr,0) = 0 and nvl(us.locked_tag,'N') = 'N' ");
        sql.append(" and nvl(ur.dr,0) = 0 and us.pk_corp = ?");
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        ArrayList<JMUserRoleVO> vos = (ArrayList<JMUserRoleVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(JMUserRoleVO.class));
        HashMap<String, String> map = new HashMap<>();
        if(vos != null && vos.size() > 0){
            String value = null;
            for(JMUserRoleVO vo : vos){
                String cuserid = vo.getCuserid();
                if(!map.containsKey(cuserid)){
                    map.put(cuserid, vo.getRole_name());
                }else{
                    value = map.get(cuserid);
                    map.remove(cuserid);
                    map.put(cuserid, value+","+vo.getRole_name());
                }
            }
        }
        return map;
    }

}
