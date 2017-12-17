package com.dzf.service.sys.sys_set.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.action.sys.sys_set.PinyinUtil;
import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.pub.QueryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.service.sys.sys_set.ICorpQueryService;

/**
 * 公司目录
 * 
 * 
 */
@Service("sys_quchncorp")
public class CorpQueryServiceImpl implements ICorpQueryService {
	@Autowired
	private SingleObjectBO singleObjectBO = null;

	@SuppressWarnings("unchecked")
	public CorpVO[] queryCorpRef(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException {
		// 根据查询条件查询公司的信息
		String sql = getRefQuerySql(queryvo, uservo);
		SQLParameter param = getRefParam(queryvo, uservo);
		List<CorpVO> vos = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), param,
				new BeanListProcessor(CorpVO.class));
		QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname" },
				vos, 1);
		String corpname = queryvo.getCorpname();
		String corpcode = queryvo.getCorpcode();
		if (!StringUtil.isEmpty(corpname)) {
			if (vos != null && vos.size() > 0) {
				CorpVO[] cvos = vos.toArray(new CorpVO[0]);
				List<CorpVO> list = new ArrayList<>();
				String enf_name = null;
				String en_name = null;
				String unitname = null;
				for (CorpVO cvo : cvos) {
					unitname = cvo.getUnitname();
					if (!StringUtil.isEmpty(unitname)) {
						en_name = PinyinUtil.getFullSpell(unitname);
						enf_name = PinyinUtil.getFirstSpell(unitname);
					}
//					if (unitname.contains(corpname) || en_name.contains(corpname) || enf_name.contains(corpname)) {
//						list.add(cvo);
//					} else if (!StringUtils.isEmpty(cvo.getInnercode()) && cvo.getInnercode().contains(corpcode)) {
//						list.add(cvo);
//					}
					if ((!StringUtils.isEmpty(unitname)
							&& (unitname.indexOf(corpname) >= 0 || en_name.indexOf(corpname) >= 0
									|| en_name.indexOf(corpname) >= 0))
							|| (!StringUtils.isEmpty(cvo.getInnercode())
									&& cvo.getInnercode().indexOf(corpcode) >= 0)) {
						list.add(cvo);
					}
				}
				return list.toArray(new CorpVO[0]);
			}
		}
		return vos.toArray(new CorpVO[0]);
	}

	/**
	 * 拼装我的客户查询信息
	 * 
	 * @param queryvo
	 * @return
	 */
	private String getRefQuerySql(QueryParamVO queryvo, UserVO uservo) {
		// 根据查询条件查询公司的信息
		StringBuffer corpsql = new StringBuffer();
		corpsql.append("select bd_corp.* ");
		corpsql.append(" from bd_corp bd_corp");
//		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
		corpsql.append(" where nvl(bd_corp.dr,0) =0 ");//
		
		//这段有点拗口，区别在于是否包含委托的客户
//		if(queryvo.getIshasjz() != null && queryvo.getIshasjz().booleanValue()){
//			corpsql.append(" and ( ");
//			if(ismanager.booleanValue()){
//				corpsql.append(" bd_corp.fathercorp = ? or ");
//			}
//			corpsql.append(" bd_corp.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
//			corpsql.append(" ) ");
//		}else{
			corpsql.append(" and bd_corp.fathercorp = ?  ");
//			if (!ismanager.booleanValue()) {
//				corpsql.append("  and bd_corp.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
//			}
//		}
		
		corpsql.append(" and nvl(bd_corp.isaccountcorp,'N') = 'N' ");
		corpsql.append(" and nvl(bd_corp.isseal,'N') = 'N'");
		corpsql.append(" and nvl(bd_corp.isformal,'N') = 'Y'");
		corpsql.append(" order by bd_corp.innercode");
		return corpsql.toString();
	}

	private SQLParameter getRefParam(QueryParamVO queryvo, UserVO uservo) {
		SQLParameter param = new SQLParameter();
//		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
		
//		if(queryvo.getIshasjz() != null && queryvo.getIshasjz().booleanValue()){
//			if(ismanager.booleanValue()){
//				param.addParam(queryvo.getPk_corp());
//			}
//			param.addParam(uservo.getCuserid());
//		}else{
			param.addParam(queryvo.getPk_corp());
//			if (!ismanager.booleanValue()) {
//				param.addParam(uservo.getCuserid());
//			}
//		}
		return param;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CorpVO[] queryCorpTotal(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException {
		// 根据查询条件查询公司的信息
		StringBuffer corpsql = new StringBuffer();
		String sql = getQuerySql(queryvo, uservo);
		SQLParameter param = getQueryParam(queryvo, uservo);
		corpsql.append(sql);
		corpsql.append("  order by a.begindate desc");
		List<CorpVO> vos = (List<CorpVO>) singleObjectBO.executeQuery(corpsql.toString(), param,
				new BeanListProcessor(CorpVO.class));
		QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname",
				"def1", "def2", "def3", "pcountname", "wqcountname" }, vos, 1);
		if (!StringUtil.isEmpty(queryvo.getCorpname())) {
			if (vos != null && vos.size() > 0) {
				CorpVO[] cvos = vos.toArray(new CorpVO[0]);
				List<CorpVO> list = new ArrayList<>();
				for (CorpVO cvo : cvos) {
					if (cvo.getUnitname().contains(queryvo.getCorpname())) {
						list.add(cvo);
					}
				}
				return list.toArray(new CorpVO[0]);
			}
		}
		return vos.toArray(new CorpVO[0]);
	}

	/**
	 * 拼装我的客户查询信息
	 * 
	 * @param queryvo
	 * @return
	 */
	private String getQuerySql(QueryParamVO queryvo, UserVO uservo) {
		// 根据查询条件查询公司的信息
		StringBuffer corpsql = new StringBuffer();
		corpsql.append("select ");
		corpsql.append(" a.begindate        ,");
		corpsql.append(" a.briefintro       ,");
		corpsql.append(" a.busibegindate    ,");
		corpsql.append(" a.busienddate      ,");
		corpsql.append(" a.chargedeptcode   ,");
		corpsql.append(" a.chargedeptname   ,");
		corpsql.append(" a.citycounty       ,");
		corpsql.append(" a.corptype         ,");
		corpsql.append(" a.countryarea      ,");
		corpsql.append(" a.createdate       ,");
		corpsql.append(" a.def10,a.def11,a.def12,a.def13,");
		corpsql.append(" a.def14, a.def15, a.def16 ,a.def17,");
		corpsql.append(" a.def18, a.def19,a.def20, ");
		corpsql.append(" a.def4, a.def5, a.def6, a.def7, a.def8, a.def9,");
		corpsql.append(" a.dr               ,");
		corpsql.append(" a.ecotype          ,");
		corpsql.append(" a.email1, a.email2,a.email3,");
		corpsql.append(" a.enddate          ,");
		corpsql.append(" a.fathercorp       ,");
		corpsql.append(" a.fax1             ,");
		corpsql.append(" a.fax2             ,");
		corpsql.append(" a.holdflag         ,");
		corpsql.append(" a.idnumber         ,");
		corpsql.append(" a.industry         ,");
		corpsql.append(" a.innercode        ,");
		corpsql.append(" a.ishasaccount     ,");
		corpsql.append(" a.isseal           ,");
		corpsql.append(" a.isuseretail      ,");
		corpsql.append(" a.isworkingunit    ,");
		corpsql.append(" a.legalbodycode    ,");
		corpsql.append(" a.linkman1, a.linkman2, a.linkman3,");
		corpsql.append(" a.maxinnercode     ,");
		corpsql.append(" a.memo             ,");
		corpsql.append(" a.ownersharerate   ,");
		corpsql.append(" a.phone1, a.phone2, a.phone3,");
		corpsql.append(" a.pk_corp          ,");
		corpsql.append(" a.pk_corpkind      ,");
		corpsql.append(" a.pk_currency      ,");
		corpsql.append(" a.postaddr         ,");
		corpsql.append(" a.province         ,");
		corpsql.append(" a.regcapital       ,");
		corpsql.append(" a.region           ,");
		corpsql.append(" a.saleaddr         ,");
		corpsql.append(" a.sealeddate       ,");
		corpsql.append(" a.showorder        ,");
		corpsql.append(" a.taxcode          ,");
		corpsql.append(" a.taxpayertype     ,");
		corpsql.append(" a.unitcode         ,");
		corpsql.append(" a.unitdistinction  ,");
		corpsql.append(" a.unitname         ,");
		corpsql.append(" a.unitshortname    ,");
		corpsql.append(" a.url              ,");
		corpsql.append(" a.zipcode          ,");
		corpsql.append(" a.isaccountcorp    ,");
		corpsql.append(" a.isdatacorp       ,");
		corpsql.append(" a.bbuildic         ,");
		corpsql.append(" a.icostforwardstyle,");
		corpsql.append(" a.iscurr           ,");
		corpsql.append(" a.icbegindate      ,");
		corpsql.append(" a.ts               ,");
		corpsql.append(" a.vcorporationid	,");
		corpsql.append(" a.ispersonal		,");
		corpsql.append(" a.vorgcode			,");
		corpsql.append(" a.vbusilicode		,");
		corpsql.append(" a.dlicexpdate		,");
		corpsql.append(" a.vaccountopen		,");
		corpsql.append(" a.vcompcode		,");
		corpsql.append(" a.dcompdate		,");
		corpsql.append(" a.vsoccrecode		,");
		corpsql.append(" a.vbankname		,");
		corpsql.append(" a.vbankcode		,");
		corpsql.append(" a.vbankaddr		,");
		corpsql.append(" a.vbankpos			,");
		corpsql.append(" a.vbusitype		,");
		corpsql.append(" a.vfilecode		,");
		corpsql.append(" a.vprovince		,");
		corpsql.append(" a.vcity			,");
		corpsql.append(" a.varea			,");
		corpsql.append(" a.vcustsource		,");
		corpsql.append(" a.vsourcenote		,");
		corpsql.append(" a.vcustmainbusi	,");
		corpsql.append(" a.vrespaccount		,");
		corpsql.append(" a.vresptel			,");
		corpsql.append(" a.vcustothertel	,");
		corpsql.append(" a.vstatetaxplace	,");
		corpsql.append(" a.vstatetaxaddr	,");
		corpsql.append(" a.vstatetaxper		,");
		corpsql.append(" a.vstatetaxpertel	,");
		corpsql.append(" a.vstatetaxpwd		,");
		corpsql.append(" a.dstatetaxdate	,");
		corpsql.append(" a.vlocaltaxcode	,");
		corpsql.append(" a.vlocaltaxplace	,");
		corpsql.append(" a.vlocaltaxper		,");
		corpsql.append(" a.vlocaltaxpertel	,");
		corpsql.append(" a.vlocaltaxaddr	,");
		corpsql.append(" a.vlocaltaxpwd		,");

		corpsql.append(" a.vcorporatetax	,");
		corpsql.append(" a.vtaxtype			,");
		corpsql.append(" a.vtaxcode			,");
		// 个税密码
		corpsql.append(" a.vpersonalpwd	    ,");
		// UKEY到期日
		corpsql.append(" a.dukeydate	    ,");

		// WJX 申报方式、征收方式、金税盘编号、税收优惠额度
		corpsql.append(" a.taxreporttype	    ,");
		corpsql.append(" a.taxlevytype	    ,");
		corpsql.append(" a.golddiskno	    ,");
		corpsql.append(" a.taxfeeamount	    ,");

		corpsql.append(" a.vgenvohtype		,");
		corpsql.append(" a.vsuperaccount	,");
		corpsql.append(" a.vwqaccount		,");
		corpsql.append(" a.destablishdate	,");
		corpsql.append(" a.isukey			,");
		corpsql.append(" a.pk_source		,");
		corpsql.append(" a.iskwxjr  		,");
		corpsql.append(" a.lastmodifytime   ,");
		corpsql.append(" a.industrycode   ,");// 行业代码
		corpsql.append(" a.taxcontrmachtype   ,");// 税控器具类型

		corpsql.append(" a.isdkfp   ,");// 代开发票
		corpsql.append(" a.isdbbx   ,");// 代办保险
		corpsql.append(" a.isxrq   ,");// 一般纳税人生效日期
		corpsql.append(" a.drdsj   ,");// 认定时间
		corpsql.append(" a.isywskp   ,");// 有无税控盘
		corpsql.append(" a.ifwgs   ,");// 房屋归属
		corpsql.append(" a.isdsbsjg   ,");// 所得税报送机关
		corpsql.append(" a.ikjzc   ,");// 会计政策
		corpsql.append(" a.drzsj   ,");// 认证时间
		corpsql.append(" a.vschlnd   ,");// 首次获利年度
		corpsql.append(" a.vyhzc   ,");// 公司可享受的优惠政策
		corpsql.append(" a.rembday		,"); // 提醒开始日期
		corpsql.append(" a.remeday		,"); // 提醒截止日期
		corpsql.append(" a.isformal        ,");// 是否正式客户
		// corpsql.append(" b.unitname as
		// def1,b.def3,b.def2,b.foreignname,y.accname as ctypename,t.tradename
		// as indusname from bd_corp a ");
		corpsql.append(" b.unitname as def1 ,");
		corpsql.append(" b.def3      		,");
		corpsql.append(" b.def2     		,");
		corpsql.append(" a.foreignname		,");
		corpsql.append(" a.citybuildtax		,");// 撑建税
		corpsql.append(" a.def16		,"); // 登录方式
		corpsql.append(" a.ismaintainedtax		,"); // 是否已维护税率信息
		corpsql.append(" y.accname as ctypename      ,");
		corpsql.append(" t.tradename as indusname    ,");
		corpsql.append(" u.user_name as pcountname   ,");//
		corpsql.append(" us.user_name as wqcountname  ");//
		corpsql.append(" from bd_corp a");

		corpsql.append(" left join bd_account b on a.fathercorp = b.pk_corp");
		corpsql.append(" left join ynt_tdaccschema y on a.corptype = y.pk_trade_accountschema");// 查询科目方案的名称
		corpsql.append(" left join ynt_bd_trade t on a.industry = t.pk_trade");
		corpsql.append(" left join sm_user u on a.vsuperaccount = u.cuserid");// 关联用户表--主管会计
		corpsql.append(" left join sm_user us on a.vwqaccount = us.cuserid");// 关联用户表--外勤会计
		// 添加权限过滤
		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
		corpsql.append(" where nvl(a.dr,0) =0 and a.fathercorp = ?");

		if (queryvo.getIshassh() == null || !queryvo.getIshassh().booleanValue()) {
			corpsql.append(" and nvl(a.isseal,'N') = 'N' ");
		}
		if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
			corpsql.append(" and a.innercode like ? ");
		}
		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			if (queryvo.getBegindate1().compareTo(queryvo.getEnddate()) == 0) {
				corpsql.append(" and a.createdate = ? ");
			} else {
				corpsql.append(" and (a.createdate >= ? and a.createdate <= ? )");
			}
		}
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			if (queryvo.getBcreatedate().compareTo(queryvo.getEcreatedate()) == 0) {
				corpsql.append(" and a.begindate = ? ");
			} else {
				corpsql.append(" and (a.begindate >= ? and a.begindate <= ? )");
			}
		} else if (queryvo.getBcreatedate() != null) {// 建账日期只录入开始日期或结束日期
			corpsql.append(" and a.begindate >= ? ");
		} else if (queryvo.getEcreatedate() != null) {
			corpsql.append(" and a.begindate <= ? ");
		}
		corpsql.append(" and  nvl(a.isaccountcorp,'N') = 'N' ");
		// 添加权限过滤
		if (!ismanager.booleanValue()) {
			corpsql.append(" and a.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
		}

		if (!StringUtil.isEmpty(queryvo.getVprovince())) {
			corpsql.append(" and a.vprovince=? ");
		}
		if (queryvo.getIsmaintainedtax() != null) {
			if (queryvo.getIsmaintainedtax().booleanValue()) {
				corpsql.append(" and a.ismaintainedtax='Y' ");
			} else {
				corpsql.append(" and nvl(a.ismaintainedtax,'N')='N' ");
			}
		}
		if (!StringUtil.isEmpty(queryvo.getKms_id())) {// 公司主键
			corpsql.append(" and a.pk_corp=? ");
		}
		if (queryvo.getIsdkfp() != null) {
			corpsql.append(" and nvl(a.isdkfp,'N')= ? ");
		}
		if (queryvo.getIsdbbx() != null) {
			corpsql.append(" and nvl(a.isdbbx,'N')= ? ");
		}
		if (queryvo.getIsywskp() != null) {
			corpsql.append(" and nvl(a.isywskp,'N')= ? ");
		}
		if (queryvo.getIfwgs() != null && queryvo.getIfwgs() != -1) {
			corpsql.append(" and nvl(a.ifwgs,0)= ? ");
		}
		if (queryvo.getIsformal() != null) {
			corpsql.append(" and nvl(a.isformal,'N') = ?");
		} else {
			corpsql.append(" and nvl(a.isformal,'N') = 'Y'");
		}
		corpsql.append(" order by a.innercode");
		return corpsql.toString();
	}

	/**
	 * 获取我的客户查询参数
	 * 
	 * @param queryvo
	 * @param uservo
	 * @return
	 */
	private SQLParameter getQueryParam(QueryParamVO queryvo, UserVO uservo) {
		SQLParameter param = new SQLParameter();
		param.addParam(queryvo.getPk_corp());

		if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
			param.addParam("%" + queryvo.getCorpcode() + "%");
		}
		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			if (queryvo.getBegindate1().compareTo(queryvo.getEnddate()) == 0) {
				param.addParam(queryvo.getBegindate1());
			} else {
				param.addParam(queryvo.getBegindate1());
				param.addParam(queryvo.getEnddate());
			}
		}
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			if (queryvo.getBcreatedate().compareTo(queryvo.getEcreatedate()) == 0) {
				param.addParam(queryvo.getBcreatedate());
			} else {
				param.addParam(queryvo.getBcreatedate());
				param.addParam(queryvo.getEcreatedate());
			}
		} else if (queryvo.getBcreatedate() != null) {// 建账日期只录入开始日期或结束日期
			param.addParam(queryvo.getBcreatedate());
		} else if (queryvo.getEcreatedate() != null) {
			param.addParam(queryvo.getEcreatedate());
		}
		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
		if (!ismanager.booleanValue()) {
			param.addParam(uservo.getCuserid());
		}
		if (!StringUtil.isEmpty(queryvo.getVprovince())) {
			param.addParam(Integer.parseInt(queryvo.getVprovince()));
		}
		if (!StringUtil.isEmpty(queryvo.getKms_id())) {// 公司主键
			param.addParam(queryvo.getKms_id());
		}
		if (queryvo.getIsdkfp() != null) {
			param.addParam(queryvo.getIsdkfp());
		}
		if (queryvo.getIsdbbx() != null) {
			param.addParam(queryvo.getIsdbbx());
		}
		if (queryvo.getIsywskp() != null) {
			param.addParam(queryvo.getIsywskp());
		}
		if (queryvo.getIsformal() != null) {
			param.addParam(queryvo.getIsformal());
		}
		if (queryvo.getIfwgs() != null && queryvo.getIfwgs() != -1) {
			param.addParam(queryvo.getIfwgs());
		}
		return param;
	}

}
