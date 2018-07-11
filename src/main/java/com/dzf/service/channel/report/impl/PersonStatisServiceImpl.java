package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.channel.report.PersonStatisVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IPersonStatis;

@Service("statisPerson")
public class PersonStatisServiceImpl extends DataCommonRepImpl implements IPersonStatis {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
//	private static String[] str1={"jms02","jms06","jms07","jms08","jms09","jms05","jms11"};
	
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
		if(Arrays.asList(str).contains(areaname)){
			DZFDouble xtotal = tmp == null ? DZFDouble.ZERO_DBL : new DZFDouble(tmp);
			setVO.setXtotal(xtotal.div(setVO.getTotal()).multiply(100));
		}else{
			DZFDouble ktotal = tmp == null ? DZFDouble.ZERO_DBL : new DZFDouble(tmp);
			setVO.setKtotal(ktotal.div(setVO.getTotal()).multiply(100));
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

}
