package com.dzf.service.branch.reportmanage.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.branch.reportmanage.CorpDataVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.branch.reportmanage.ICorpDataService;
import com.dzf.service.branch.reportmanage.ISaleCorpDataService;

@Service("salecorpdataser")
public class SaleCorpDataServiceImpl implements ISaleCorpDataService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private ICorpDataService corpser;

	@SuppressWarnings("unchecked")
	@Override
	public List<ComboBoxVO> queryAccount(String cuserid) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT p.pk_corp AS id, p.unitname AS name  \n");
		sql.append("  FROM br_branchcorp p  \n");
		sql.append("  LEFT JOIN sm_user r ON r.pk_department = p.pk_branchset  \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(r.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isseal, 'N') = 'N'  \n");
		sql.append("   AND r.cuserid = ?  \n");
		spm.addParam(cuserid);
		List<ComboBoxVO> list = (List<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ComboBoxVO.class));
		if (list != null && list.size() > 0) {
			List<ComboBoxVO> retlist = new ArrayList<ComboBoxVO>();
			ComboBoxVO bvo = new ComboBoxVO();
			bvo.setId("pk_all");
			bvo.setName("全部");
			retlist.add(bvo);
			retlist.addAll(list);
			return retlist;
		}
		return list;
	}

	@Override
	public Integer queryTotal(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getTotalSqlSpm(pamvo, true);
		return multBodyObjectBO.getDataTotal(qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpDataVO> query(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getTotalSqlSpm(pamvo, false);
		List<CorpDataVO> list = (List<CorpDataVO>) multBodyObjectBO.queryDataPage(CorpDataVO.class, qryvo.getSql(),
				qryvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if (list != null && list.size() > 0) {
			List<String> pklist = new ArrayList<String>();
			for (CorpDataVO dvo : list) {
				pklist.add(dvo.getPk_corp());
			}
			String[] corpks = null;
			if (pklist != null && pklist.size() > 0) {
				corpks = pklist.toArray(new String[0]);
			}
			DZFDate date = new DZFDate();
			String period = date.getYear() + date.getStrMonth();
			period = ToolsUtil.getPreNumsMonth(period, 1);
			return corpser.getReturnData(pamvo, corpks, period);
		}
		return null;
	}
	
	/**
	 * 获取总客户数量
	 * 
	 * @param pamvo
	 * @param isqrynum
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getTotalSqlSpm(QryParamVO pamvo, boolean isqrynum) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if (isqrynum) {
			sql.append("SELECT COUNT(DISTINCT c.pk_corp)  \n");
		} else {
			sql.append("SELECT DISTINCT c.pk_corp, c.unitname  \n");
		}
		sql.append("  FROM bd_corp c  \n");
		sql.append("  LEFT JOIN br_branchcorp p ON c.fathercorp = p.pk_corp  \n");
		sql.append("  LEFT JOIN sm_user r ON r.pk_department = p.pk_branchset  \n");
		sql.append(" WHERE nvl(c.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(r.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isseal, 'N') = 'N'  \n");
		sql.append("   AND nvl(c.isseal, 'N') = 'N'  \n");
		sql.append("   AND nvl(c.isaccountcorp, 'N') = 'N'  \n");
		sql.append("   AND c.isformal = 'Y'  \n");
		sql.append("   AND r.cuserid = ?  \n");
		spm.addParam(pamvo.getCuserid());
		if (pamvo.getBegdate() != null) {
			sql.append(" AND c.createdate >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND c.createdate <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		if (!StringUtil.isEmpty(pamvo.getPk_corp()) && !"pk_all".equals(pamvo.getPk_corp())) {
			sql.append(" AND p.pk_corp = ? \n");
			spm.addParam(pamvo.getPk_corp());
		}
		if (!StringUtil.isEmpty(pamvo.getCorpkcode())) {
			sql.append(" AND c.innercode like ? \n");
			spm.addParam("%" + pamvo.getCorpkcode() + "%");
		}
		// 销售人员
		if (!StringUtil.isEmpty(pamvo.getUser_name())) {
			sql.append(" AND p.foreignname = ? \n");
			spm.addParam(pamvo.getUser_name());
		}
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

}
