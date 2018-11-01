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
		List<String> countcorplist = new ArrayList<String>();
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
					setVO.setAttributeValue(personStatisVO.getAreaname(), personStatisVO.getTotal());
					setVO.setTotal(queryUserNum.get(pk_corp));
					setZhanBi(setVO,personStatisVO.getTotal(),personStatisVO.getAreaname());
					retlist.add(setVO);
				}else{
					getvo= retlist.get(retlist.indexOf(personStatisVO));
					setZhanBi(setVO,personStatisVO.getTotal(),personStatisVO.getAreaname());
					getvo.setAttributeValue(personStatisVO.getAreaname(), personStatisVO.getTotal());
				}
			}
		}
		return retlist;
	}
	
	
	private List<PersonStatisVO> queryPersonStatis(List<String> corplist) {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(w.cuserid) total, w.role_code areaname, w.pk_corp, w.innercode");
		sql.append("  from (select sur.cuserid, sr.role_code, su.pk_corp, ba.innercode");
		sql.append("          from sm_user_role sur");
		sql.append("         inner join sm_role sr on sr.pk_role = sur.pk_role");
		sql.append("                              and sr.roletype = 8");
		sql.append("         inner join sm_user su on sur.cuserid = su.cuserid");
		sql.append("                              and nvl(su.locked_tag,'N')= 'N'");
		sql.append("          left join bd_account ba on ba.pk_corp = su.pk_corp where");
		sql.append(SqlUtil.buildSqlForIn("su.pk_corp", corplist.toArray(new String[corplist.size()])));
		sql.append("         	and nvl(sur.dr,0)=0 and nvl(sr.dr,0)=0   ");
		sql.append("         group by sur.cuserid, sr.role_code, su.pk_corp, ba.innercode) w");
		sql.append(" group by w.role_code, w.pk_corp, w.innercode");
		sql.append(" order by w.innercode");
		List<PersonStatisVO> list=(List<PersonStatisVO>)singleObjectBO.executeQuery(sql.toString(),null, new BeanListProcessor(PersonStatisVO.class));
		return list;
	}
	
	/**
	 * 设置团队占比
	 * @param setVO
	 * @param tmp
	 * @param areaname
	 */
	private void setZhanBi(PersonStatisVO setVO,Integer tmp, String areaname) {
		DZFDouble tem_total=null;
		if(Arrays.asList(str).contains(areaname)){
			tem_total=setVO.getXtotal()==null? DZFDouble.ZERO_DBL : setVO.getXtotal();
			DZFDouble xtotal = tmp == null ? DZFDouble.ZERO_DBL : new DZFDouble(tmp);
			setVO.setXtotal(tem_total.add(xtotal.div(setVO.getTotal()).multiply(100)));
		}else if(!"jms01".equals(areaname)){
			tem_total=setVO.getKtotal()==null? DZFDouble.ZERO_DBL : setVO.getKtotal();
			DZFDouble ktotal = tmp == null ? DZFDouble.ZERO_DBL : new DZFDouble(tmp);
			setVO.setKtotal(tem_total.add(ktotal.div(setVO.getTotal()).multiply(100)));
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
		DZFDouble lzTmp=new DZFDouble().ZERO_DBL;
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
//        str.append("SELECT deptname, user_code, user_name, ");
//        str.append(" LISTAGG(to_char(role_name), ';') WITHIN GROUP(ORDER BY role_name) AS rolename, sum(corpnum) as corpnum");
//        str.append(" FROM ");
//        str.append(" (select distinct dept.deptname, us.user_code, us.user_name, sr.role_name, count(uc.pk_corp) as corpnum");
//        str.append("  from sm_user us");
//        str.append("  left join ynt_department dept on dept.pk_department = us.pk_department");
//        str.append("  left join sm_userole userr on userr.cuserid = us.cuserid");
//        str.append("  left join sm_role sr on sr.pk_role = userr.pk_role");
//        str.append("  left join sm_user_corp uc on uc.cuserid = us.cuserid  ");
//        str.append("  where nvl(us.dr, 0) = 0 and nvl(sr.dr, 0) = 0 and nvl(dept.dr, 0) = 0");
//        str.append("  and nvl(us.locked_tag,'N') = 'N' and us.pk_corp = ?");
//        str.append("  group by dept.deptname, us.user_code, us.user_name, sr.role_name)");
//        str.append(" group by deptname, user_code, user_name");
//        str.append(" order by deptname");
        
        str.append("select distinct dept.deptname,us.user_code, us.user_name,us.cuserid as userid,");
        str.append(" LISTAGG(to_char(sr.role_name), ';') WITHIN GROUP(ORDER BY role_name) AS rolename");
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
        if(list != null && list.size() > 0 && map != null){
            for(UserDetailVO uvo : list){
                uvo.setCorpnum(CommonUtil.getInteger(map.get(uvo.getUserid())));
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
        sql.append(" select cuserid as userid,count(pk_corpk) as corpnum from sm_user_corp where pk_corp = ? and pk_corpk != ? and nvl(dr,0) = 0 group by cuserid ");
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

}
