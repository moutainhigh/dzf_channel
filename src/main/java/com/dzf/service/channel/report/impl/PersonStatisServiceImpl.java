package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnListProcessor;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.channel.report.MarketTeamVO;
import com.dzf.model.channel.report.PersonStatisVO;
import com.dzf.model.channel.report.UserDetailVO;
import com.dzf.model.jms.basicset.JMUserRoleVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IPersonStatis;
import com.dzf.service.pub.IPubService;

@Service("statisPerson")
public class PersonStatisServiceImpl extends DataCommonRepImpl implements IPersonStatis {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IPubService pubser;
	
	@Override
	public List<PersonStatisVO> query(QryParamVO paramvo,UserVO user) throws DZFWarpException, IllegalAccessException, Exception {
		List<PersonStatisVO> retlist = new ArrayList<PersonStatisVO>();
		int type = queryRole(user.getCuserid());
		HashMap<String, DataVO> map = queryCorps(paramvo,PersonStatisVO.class,type);
		List<String> corplist = null;
		if(map!=null && !map.isEmpty()){
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}
		if (corplist != null && corplist.size() > 0) {
			
			HashMap<String, Integer> queryEmployNum = queryEmployNum(corplist);
			HashMap<String, Integer> queryCustNum = queryCustNum(corplist);
			List<PersonStatisVO> list = queryPersonStatis(corplist,user,type);
			PersonStatisVO setVO=new PersonStatisVO();
			PersonStatisVO getvo=new PersonStatisVO();
			DataVO data=null;
			CorpVO corpvo = null;
			String pk_corp =null;
			
			HashMap<String,Integer> Tmap = new HashMap<String,Integer>();

			for (PersonStatisVO personStatisVO : list) {
				if(retlist==null ||retlist.size()==0 || !retlist.contains(personStatisVO)){
					pk_corp=personStatisVO.getPk_corp();
					data =map.get(pk_corp);
					setVO = (PersonStatisVO)data;
					
					setVO.setJms03(personStatisVO.getManagernum());
					setVO.setJms04(personStatisVO.getDepartnum());
					setVO.setJms10(personStatisVO.getSellnum());
					setVO.setPk_marketeam(personStatisVO.getPk_marketeam());
					if(setVO.getXnum()==null){
						setVO.setXnum(0);
						if(personStatisVO.getManagernum()!=null){
							setVO.setXnum(setVO.getXnum()+personStatisVO.getManagernum());
						}
						if(personStatisVO.getDepartnum()!=null){
							setVO.setXnum(setVO.getXnum()+personStatisVO.getDepartnum());
						}
						if(personStatisVO.getSellnum()!=null){
							setVO.setXnum(setVO.getXnum()+personStatisVO.getSellnum());
						}
					}
					corpvo = CorpCache.getInstance().get(null, pk_corp);
					if (corpvo != null) {
						setVO.setCorpname(corpvo.getUnitname());
						setVO.setVprovname(corpvo.getCitycounty());
					}
					setVO.setCustnum(queryCustNum.get(pk_corp));
					
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
			
			for (PersonStatisVO pervo : retlist) {
				Integer lznum = queryEmployNum.get(pervo.getPk_corp())==null?0:queryEmployNum.get(pervo.getPk_corp());
				pervo.setLznum(lznum);
				if(pervo.getXnum()==null && pervo.getKnum()!=null){
					pervo.setXtotal(null);
					pervo.setTotal(pervo.getJms01()+pervo.getKnum());
					pervo.setKtotal(new DZFDouble(pervo.getKnum()).div(pervo.getTotal()).multiply(100));
				}
				if(pervo.getKnum()==null && pervo.getXnum()!=null){
					pervo.setKtotal(null);
					pervo.setTotal(pervo.getJms01()+pervo.getXnum());
					pervo.setXtotal(new DZFDouble(pervo.getXnum()).div(pervo.getTotal()).multiply(100));
				}
				if(pervo.getKnum()==null && pervo.getXnum()==null){
					pervo.setTotal(pervo.getJms01());
					pervo.setKtotal(null);
					pervo.setXtotal(null);
				}
				if(pervo.getKnum()!=null && pervo.getXnum()!=null){
					pervo.setTotal(pervo.getKnum()+pervo.getJms01()+pervo.getXnum());
					pervo.setKtotal(new DZFDouble(pervo.getKnum()).div(pervo.getTotal()).multiply(100));
					pervo.setXtotal(new DZFDouble(pervo.getXnum()).div(pervo.getTotal()).multiply(100));
				}
				pervo.setLtotal(new DZFDouble(pervo.getLznum()).div(pervo.getTotal()+pervo.getLznum()).multiply(100));
			}
			
		}
		return retlist;
	}
	
	
	private int queryRole(String cuserid) {
		
		int type = 0;
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(cuserid);
		sql.append("  select distinct sr.role_name   ");
		sql.append("    from sm_user_role ur   ");
		sql.append("    left join sm_role sr on ur.pk_role = sr.pk_role   ");
		sql.append("    where nvl(ur.dr,0)=0   ");
		sql.append("    and nvl(sr.dr,0)=0   ");
		sql.append("    and ur.cuserid = ?   ");
		List<String> rolename = (List<String>) singleObjectBO.executeQuery(sql.toString(), spm, new ColumnListProcessor());
		for (String string : rolename) {
			if("渠道经理".equals(string) || "大区总".equals(string)){
				type = IStatusConstant.IQUDAO;
				break;
			}else if("培训师".equals(string) || "运营培训经理".equals(string)){
				type = IStatusConstant.IPEIXUN;
				break;
			}
		}
		return type;
	}


	private List<PersonStatisVO> queryPersonStatis(List<String> corplist, UserVO uservo,int type) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select ma.pk_marketeam,ma.managernum,ma.departnum,ma.sellnum,sur.cuserid userid, sr.role_code areaname, su.pk_corp, account.innercode,account.drelievedate");
		sql.append("          from sm_user_role sur");
		sql.append("         inner join sm_role sr on sr.pk_role = sur.pk_role");
		sql.append("                              and sr.roletype = 8");
		sql.append("         inner join sm_user su on sur.cuserid = su.cuserid");
		sql.append("                              and nvl(su.locked_tag,'N')= 'N'");
		sql.append("          left join bd_account account on account.pk_corp = su.pk_corp ");
		sql.append("          left join cn_marketteam ma on ma.pk_corp = su.pk_corp where");
		sql.append(SqlUtil.buildSqlForIn("su.pk_corp", corplist.toArray(new String[corplist.size()])));
		sql.append("         	and nvl(sur.dr,0)=0 and nvl(sr.dr,0)=0 and nvl(su.dr,0)=0 ");
		sql.append("         	and sr.role_code not in('jms03','jms04','jms10') ");
		
		String condition = pubser.makeCondition(uservo.getCuserid(), null,type);
		if (!condition.equals("alldata")) {
			sql.append(condition);
		}
		sql.append("         group by sur.cuserid, sr.role_code, su.pk_corp, account.innercode,account.drelievedate,");
		sql.append("         ma.pk_marketeam,ma.managernum,ma.departnum,ma.sellnum");
		sql.append(" order by account.innercode");
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
		
		if(!"jms01".equals(vo.getAreaname())){
			key=setVO.getPk_corp()+"kj"+vo.getUserid();
			if(!Tmap.containsKey(key)){
				Tmap.put(key,1);
				setVO.setKnum((setVO.getKnum()==null?1:(setVO.getKnum())+1));
			}
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
		sql.append(" select count(cuserid) total,pk_corp from sm_user where nvl(dr,0)=0 and nvl(locked_tag,'N')= 'N' and ");
		sql.append(SqlUtil.buildSqlForIn("pk_corp", corplist.toArray(new String[corplist.size()])));
		sql.append(" group by pk_corp ");
		List<PersonStatisVO> list=(List<PersonStatisVO>)singleObjectBO.executeQuery(sql.toString(),null, new BeanListProcessor(PersonStatisVO.class));
		for (PersonStatisVO personStatisVO : list) {
			map.put(personStatisVO.getPk_corp(), personStatisVO.getTotal());
		} 
		return map;
	}
	
	/**
	 * 查询加盟商的总客户数（不包含流失的）
	 * @param corplist
	 * @return
	 */
	private HashMap<String, Integer> queryCustNum(List<String> corplist) {
		HashMap<String, Integer> map=new HashMap<>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(pk_corp) total,fathercorp pk_corp from bd_corp  where nvl(dr,0)=0 and nvl(isseal,'N')='N' and ");
		sql.append(SqlUtil.buildSqlForIn("fathercorp", corplist.toArray(new String[corplist.size()])));
		sql.append(" group by fathercorp ");
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
	private HashMap<String, Integer> queryEmployNum(List<String> corplist) {
		HashMap<String, Integer> map=new HashMap<>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(u.pk_employee) lznum, u.pk_corp");
		sql.append(" 	from sm_user u left join ynt_employee e");
		sql.append("   	on  e.pk_employee = u.pk_employee ");
		sql.append("   where e.istatus = 2");
		sql.append("     and nvl(e.dr, 0) = 0");
		sql.append("     and nvl(u.dr, 0) = 0 and ");
		sql.append(SqlUtil.buildSqlForIn("u.pk_corp", corplist.toArray(new String[corplist.size()])));
		sql.append("    group by u.pk_corp ");
		List<PersonStatisVO> list=(List<PersonStatisVO>)singleObjectBO.executeQuery(sql.toString(),null, new BeanListProcessor(PersonStatisVO.class));
		for (PersonStatisVO personStatisVO : list) {
			map.put(personStatisVO.getPk_corp(), personStatisVO.getLznum());
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


	@Override
	public void save(MarketTeamVO marketvo) {
		if(StringUtil.isEmpty(marketvo.getPk_marketeam())){
			singleObjectBO.insertVO("000001", marketvo);
		}else{
			String uuid = UUID.randomUUID().toString();
			try{
				boolean lockKey = LockUtil.getInstance().addLockKey(marketvo.getTableName(), marketvo.getPk_marketeam(), uuid, 60);
				if (!lockKey) {
					throw new BusinessException("加盟商编码：" + marketvo.getInnercode() + ",其他用户正在操作此数据;<br>");
				}
				checkData(marketvo.getPk_marketeam(), marketvo.getUpdatets());
				singleObjectBO.update(marketvo, new String[] { "managernum","departnum","sellnum"});
			}catch (Exception e) {
				if (e instanceof BusinessException)
					throw new BusinessException(e.getMessage());
				else
					throw new WiseRunException(e);
			} finally {
				LockUtil.getInstance().unLock_Key(marketvo.getTableName(), marketvo.getPk_marketeam(), uuid);
			}

			singleObjectBO.update(marketvo);
		}
	}


	/**
	 * 检验是否是最新数据
	 */
	private void checkData(String pk_marketeam, DZFDateTime updatets) {
		MarketTeamVO vo = (MarketTeamVO) singleObjectBO.queryByPrimaryKey(MarketTeamVO.class, pk_marketeam);
		if (!updatets.equals(vo.getUpdatets())) {
			throw new BusinessException("加盟商编码：" + vo.getInnercode() + ",数据已发生变化;<br>");
		}
	}


	@Override
	public MarketTeamVO queryDataById(String id) {
		return (MarketTeamVO) singleObjectBO.queryByPrimaryKey(MarketTeamVO.class, id);
	}


	@Override
	public int queryQtype(UserVO uservo) {
		return queryRole(uservo.getCuserid());
	}


}
