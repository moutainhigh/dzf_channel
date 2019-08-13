package com.dzf.service.channel.report.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.report.RenewAchieveVO;
import com.dzf.model.channel.report.RenewCountVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IRenewAchieveService;
import com.dzf.service.pub.IPubService;

@Service("renewachieveser")
public class RenewAchieveServiceImpl implements IRenewAchieveService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IPubService pubService;
	
	@Override
	public Integer queryTotal(QrySqlSpmVO qryvo) throws DZFWarpException {
		return multBodyObjectBO.getDataTotal(qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RenewAchieveVO> query(QryParamVO pamvo, UserVO uservo, QrySqlSpmVO qryvo) throws DZFWarpException {
		List<String> list = (List<String>) multBodyObjectBO.queryDataPage("pk_corp", qryvo.getSql(), qryvo.getSpm(),
				pamvo.getPage(), pamvo.getRows(), "");
		if (list != null && list.size() > 0) {
			String[] pk_corps = list.toArray(new String[0]);
			QrySqlSpmVO daryvo = getQrySql(pk_corps, pamvo);
			List<RenewAchieveVO> dliat = (List<RenewAchieveVO>) singleObjectBO.executeQuery(daryvo.getSql(),
					daryvo.getSpm(), new BeanListProcessor(RenewAchieveVO.class));
			if(dliat != null && dliat.size() > 0){
				setContData(dliat, pk_corps, pamvo);
			}
			return dliat;
		}
		return null;
	}
	
	/**
	 * 设置相关显示名称和合同相关金额
	 * @param dliat
	 * @param pk_corps
	 * @throws DZFWarpException
	 */
	private void setContData(List<RenewAchieveVO> dliat, String[] pk_corps, QryParamVO pamvo) throws DZFWarpException {
		List<RenewCountVO> ctlist = queryContInfo(pk_corps, pamvo);
		//获取加盟商信息，并对相关字段进行解密操作
		Map<String, RenewAchieveVO> dmap = getDataMap(dliat);
		if (ctlist != null && ctlist.size() > 0) {
			List<String> periods = ToolsUtil.getPeriodsBp(pamvo.getBeginperiod(), pamvo.getEndperiod());
			RenewAchieveVO rvo = null;
			Integer index = null;
			DZFDouble rate = null;
			for (RenewCountVO tvo : ctlist) {
				rvo = dmap.get(tvo.getPk_corp());
				index = periods.indexOf(tvo.getVperiod());
				if (index != null) {
					rvo.setAttributeValue("iexpirenum" + index, tvo.getIexpirenum());
					rvo.setAttributeValue("irenewnum" + index, tvo.getIrenewnum());
					rvo.setAttributeValue("renewmny" + index, tvo.getRenewmny());
					rate = CommonUtil.getDZFDouble(tvo.getIrenewnum()).div(CommonUtil.getDZFDouble(tvo.getIexpirenum()))
							.multiply(100);
					rate.setScale(2, DZFDouble.ROUND_HALF_UP);
					rvo.setAttributeValue("renewrate" + index, rate);
				}
			}
		}
	}
	
	/**
	 * 获取加盟商数据集合
	 * @param dliat
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, RenewAchieveVO> getDataMap(List<RenewAchieveVO> dliat) throws DZFWarpException {
		Map<String, RenewAchieveVO> dmap = new HashMap<String, RenewAchieveVO>();
		String[] str = new String[]{"username", "corpname", "cusername"};
		for(RenewAchieveVO dvo : dliat){
			QueryDeCodeUtils.decKeyUtil(str, dvo, 1);
			dvo.setInaccnum(ToolsUtil.subInteger(dvo.getIcorpnum(), dvo.getIaccnum()));
			dmap.put(dvo.getPk_corp(), dvo);
		}
		return dmap;
	}
	
	/**
	 * 查询合同相关信息
	 * @param pk_corps
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<RenewCountVO> queryContInfo(String[] pk_corps, QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getContSql(pk_corps, pamvo);
		return (List<RenewCountVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(RenewCountVO.class));
	}
	
	/**
	 * 获取合同数据查询条件
	 * @param pk_corps
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getContSql(String[] pk_corps, QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ct.pk_corp,  ") ;
		sql.append("       COUNT(ct.pk_contract) AS iexpirenum,  ") ; 
		sql.append("       ct.vendperiod AS vperiod,  ") ; 
		sql.append("       SUM(CASE  ") ; 
		sql.append("             WHEN xq.pk_contract IS NOT NULL AND  ") ; 
		sql.append("                  nvl(xq.patchstatus, 0) NOT IN (1, 4) THEN  ") ; 
		sql.append("              1  ") ; 
		sql.append("             ELSE  ") ; 
		sql.append("              0  ") ; 
		sql.append("           END) AS irenewnum,  ") ; 
		sql.append("       SUM(xq.ntotalmny) AS renewmny  ") ; 
		sql.append("  FROM ynt_contract ct  ") ; 
		sql.append("  LEFT JOIN ynt_contract xq ON ct.pk_contract = xq.pk_xuqian  ") ; 
		sql.append("                           AND xq.vstatus IN (1, 9)  ") ; 
		sql.append(" WHERE nvl(ct.dr, 0) = 0  ") ; 
		sql.append("   AND ct.icontracttype = 2  ") ; 
		sql.append("   AND ct.icosttype = 0  ") ; 
		sql.append("   AND ct.vstatus IN (1, 9)  ") ; 
		sql.append("   AND nvl(ct.patchstatus, 0) NOT IN (1, 4)  ") ; 
		sql.append("   AND ct.vendperiod >= ?  ") ; 
		spm.addParam(pamvo.getBeginperiod());
		sql.append("   AND ct.vendperiod <= ?  ") ; 
		spm.addParam(pamvo.getEndperiod());
		String where = SqlUtil.buildSqlForIn("ct.pk_corp", pk_corps);
		sql.append(" AND ").append(where);
		if(pamvo.getIsncust() != null){
			//客户类型过滤
			sql.append("   AND EXISTS (SELECT p.pk_corp  ") ; 
			sql.append("          FROM bd_corp p  ") ; 
			sql.append("         WHERE nvl(p.dr, 0) = 0  ") ; 
//			sql.append("           AND nvl(p.isseal, 'N') = 'N'  ") ; 
			sql.append("           AND nvl(p.isaccountcorp, 'N') = 'N'  ") ; 
			sql.append("   AND nvl(p.isncust,'N') = ?   ");
			spm.addParam(pamvo.getIsncust());
			String filter = SqlUtil.buildSqlForIn("p.fathercorp", pk_corps);
			sql.append("   AND ").append(filter);
			sql.append("   AND ct.pk_corpk = p.pk_corp)  ") ; 
		}
		sql.append(" GROUP BY ct.pk_corp, ct.vendperiod  ");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	@Override
	public QrySqlSpmVO getCorpQrySql(QryParamVO pamvo, UserVO uservo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		Integer roletype = pubService.queryRoleType(uservo.getCuserid());
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT COUNT(account.pk_corp) ") ;//account.pk_corp
		sql.append("  FROM bd_account account  ") ; 
		sql.append(" WHERE nvl(account.dr, 0) = 0  ") ; 
		sql.append("   AND account.fathercorp = ?  ");
		spm.addParam(IDefaultValue.DefaultGroup);
		//1、过滤演示加盟商：
		String filter = QueryUtil.getWhereSql();
		sql.append(" AND ").append(filter);
		//2、通过登陆人和大区过滤加盟商：
		String addsql = pubService.makeCondition(uservo.getCuserid(), pamvo.getAreaname(), roletype);
		if(!StringUtil.isEmpty(addsql)){
			if(!"alldata".equals(addsql)){
				sql.append(addsql);
			}
		}else{
			return null;
		}
		//3、 省(市)：
		if (pamvo.getVprovince() != null && pamvo.getVprovince() != -1) {
			sql.append(" AND account.vprovince = ? ");
			spm.addParam(pamvo.getVprovince());
		}
		//4、会计运营经理：
		if(!StringUtil.isEmpty(pamvo.getCuserid())){
			String[] pk_corps = pubService.getManagerCorp(pamvo.getCuserid(), IStatusConstant.IPEIXUN);
			if(pk_corps != null && pk_corps.length > 0){
				String where = SqlUtil.buildSqlForIn(" account.pk_corp ", pk_corps);
				sql.append(" AND ").append(where);
			}
		}
		//5、加盟商：
		if (pamvo.getCorps() != null && pamvo.getCorps().length > 0) {
			String where = SqlUtil.buildSqlForIn(" account.pk_corp ", pamvo.getCorps());
			sql.append(" AND ").append(where);
		}
		//6、是否查询已解约加盟商
		if (pamvo.getSeletype() != null && pamvo.getSeletype() != 0) {// 不包含已解约加盟商
			sql.append(" AND (account.drelievedate IS NULL OR account.drelievedate > ? ) ");
			spm.addParam(new DZFDate());
		}
		sql.append(" AND NOT EXISTS ");
		sql.append("  (SELECT f.pk_corp  ");
		sql.append("     FROM ynt_franchisee f    ");
		sql.append("    WHERE nvl(dr, 0) = 0    ");
		sql.append("      AND nvl(f.isreport, 'N') = 'Y' ");
		sql.append("      AND account.pk_corp = f.pk_corp)  ");
		sql.append(" ORDER BY account.innercode ");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 获取数据查询条件
	 * @param pk_corps
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySql(String[] pk_corps, QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT account.pk_corp,  ") ;
		sql.append("       ar.areaname,  ");
		sql.append("       ar.username,  ");
		sql.append("       a.region_name AS vprovname,  ") ; 
		sql.append("       account.innercode,  ") ; 
		sql.append("       account.unitname AS corpname,  ") ; 
		sql.append("       account.djoindate chndate,  ") ; 
		sql.append("       account.drelievedate,  ") ; 
		sql.append("       r.user_name AS cusername,  ") ; 
		sql.append("       COUNT(p.pk_corp) AS icorpnum,  ") ; //会计运营经理
		sql.append("       SUM(CASE  ") ; 
		sql.append("             WHEN p.begindate IS NOT NULL THEN  ") ; 
		sql.append("              1  ") ; 
		sql.append("             ELSE  ") ; 
		sql.append("              0  ") ; 
		sql.append("           END) AS iaccnum  ") ; 
		sql.append("  FROM bd_account account  ") ; 
		sql.append("  LEFT JOIN bd_corp p ON p.fathercorp = account.pk_corp  ") ; 
		sql.append("  LEFT JOIN ynt_area a on account.vprovince = a.region_id  ") ; 
		sql.append("                      AND a.parenter_id = 1  ") ; 
		sql.append("                      AND nvl(a.dr, 0) = 0  ") ; 
		sql.append("  LEFT JOIN cn_chnarea_b b on account.pk_corp = b.pk_corp  ") ; 
		sql.append("                          and b.type = ?  ") ; 
		spm.addParam(IStatusConstant.IPEIXUN);
		sql.append("                          and nvl(b.dr, 0) = 0  ") ; 
		sql.append("  LEFT JOIN sm_user r ON b.userid = r.cuserid  ") ; 
		appendSql(sql, spm);
		sql.append(" WHERE nvl(p.dr, 0) = 0  ") ; 
		sql.append("   AND nvl(account.dr, 0) = 0  ") ; 
//		sql.append("   AND nvl(p.isseal, 'N') = 'N'  ") ; //非封存
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'  ") ; 
		sql.append("   AND account.fathercorp = '000001'  ") ; 
		String where = SqlUtil.buildSqlForIn("account.pk_corp", pk_corps);
		sql.append(" AND ").append(where);
		if(pamvo.getIsncust() != null){
			sql.append("   AND nvl(p.isncust,'N') = ?   ");
			spm.addParam(pamvo.getIsncust());
		}
		sql.append(" GROUP BY account.pk_corp,  ") ; 
		sql.append("          a.region_name,  ") ; 
		sql.append("          account.innercode,  ") ; 
		sql.append("          account.unitname,  ") ; 
		sql.append("          account.djoindate,  ") ; 
		sql.append("          account.drelievedate,  ") ; 
		sql.append("          r.user_name, ");
		sql.append("          ar.areaname, ");
		sql.append("          ar.username ");
		sql.append(" ORDER BY account.innercode ");
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 追加大区、区总查询语句
	 * @param sql
	 * @param spm
	 * @param pk_corps
	 * @throws DZFWarpException
	 */
	private void appendSql(StringBuffer sql, SQLParameter spm) throws DZFWarpException {
		sql.append("  LEFT JOIN  ( ");
		sql.append("SELECT DISTINCT a.areaname, r.user_name AS username, b.vprovince  ") ;
		sql.append("  FROM cn_chnarea a  ") ; 
		sql.append(" INNER JOIN cn_chnarea_b b ON a.pk_chnarea = b.pk_chnarea  ") ; 
		sql.append(" INNER JOIN sm_user r ON a.userid = r.cuserid  ") ; 
		sql.append(" WHERE nvl(a.dr, 0) = 0  ") ; 
		sql.append("   AND nvl(b.dr, 0) = 0  ") ; 
		sql.append("   AND nvl(r.dr, 0) = 0  ") ; 
		sql.append("   AND b.type = ?  ");
		spm.addParam(IStatusConstant.IPEIXUN);
		sql.append("  ) ar  ON account.vprovince = ar.vprovince ");
	}

}
