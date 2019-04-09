package com.dzf.service.channel.matmanage.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.matmanage.IMatApplyService;
import com.dzf.service.pub.IPubService;

@Service("matapply")
public class MatApplyServiceImpl implements IMatApplyService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private IPubService pubser;

	@Override
	public int queryTotalRow(MatOrderVO qvo) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
		return multBodyObjectBO.queryDataTotal(MatOrderVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> query(MatOrderVO qvo, UserVO uservo) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
		List<MatOrderVO> list = (List<MatOrderVO>) multBodyObjectBO.queryDataPage(MatOrderVO.class, sqpvo.getSql(),
				sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
		Map<String, String> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		for (MatOrderVO mvo : list) {
			if (mvo.getCoperatorid() != null) {
				uservo = UserCache.getInstance().get(mvo.getCoperatorid(), null);
				mvo.setApplyname(uservo.getUser_name());
			}
			String manager = marmap.get(mvo.getFathercorp());
			if (!StringUtil.isEmpty(manager)) {
				uservo = UserCache.getInstance().get(manager, null);
				if (uservo != null) {
					mvo.setVmanagername(uservo.getUser_name());// 渠道经理
				}
			}
		}
		QueryDeCodeUtils.decKeyUtils(new String[] { "unitname", "corpname" }, list, 1);
		return list;
	}

	private QrySqlSpmVO getQrySqlSpm(MatOrderVO pamvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT distinct bi.pk_materielbill,  \n") ;
		sql.append("                bi.vcontcode,  \n") ; 
		sql.append("                bi.vaddress,  \n") ; 
		sql.append("                bi.vreceiver,  \n") ; 
		sql.append("                bi.phone,  \n") ; 
		sql.append("                bi.fastcode,  \n") ; 
		sql.append("                bi.fastcost,  \n") ; 
		sql.append("                bi.deliverdate,  \n") ; 
		sql.append("                bi.vmemo,  \n") ; 
		sql.append("                bi.vreason,  \n") ; 
		sql.append("                bi.vstatus,  \n") ; 
		sql.append("                bi.doperatedate,  \n") ; 
		sql.append("                bi.coperatorid,  \n") ; 
		sql.append("                bi.applydate,  \n") ; 
		sql.append("                bi.fathercorp,  \n") ; 
		sql.append("                b.vname,  \n") ; 
		sql.append("                b.vunit,  \n") ; 
		sql.append("                nvl(b.outnum, 0) outnum,  \n") ; 
		sql.append("                nvl(b.applynum, 0) applynum,  \n") ; 
		sql.append("                log.vname logname,  \n") ; 
		sql.append("                cb.vprovname proname,  \n") ; 
		sql.append("                cb.vprovince,  \n") ; 
		sql.append("                c.areaname,  \n") ; 
		sql.append("                co.unitname corpname  \n") ; 
		sql.append("  from cn_materielbill bi  \n") ; 
		sql.append("  left join cn_materielbill_b b on bi.pk_materielbill = b.pk_materielbill  \n") ; 
		sql.append("  left join cn_logistics log on log.pk_logistics = bi.pk_logistics  \n") ; 
		sql.append("  left join cn_chnarea_b cb on cb.vprovince = bi.vprovince  \n") ; 
		sql.append("  left join cn_chnarea c on c.pk_chnarea = cb.pk_chnarea  \n") ; 
		sql.append("  left join bd_account co on co.pk_corp = cb.pk_corp  \n") ; 
		sql.append(" where nvl(bi.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(b.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(log.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(cb.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(c.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(co.dr, 0) = 0  \n") ; 
		sql.append("   and cb.pk_corp is not null  \n") ;
		if (!StringUtil.isEmpty(pamvo.getCorpname())) {
			sql.append(" AND co.unitname like ? ");
			spm.addParam("%" + pamvo.getCorpname() + "%");
		}
		if (pamvo.getVstatus() != null && pamvo.getVstatus() != 0) {
			sql.append("   AND bi.vstatus = ? \n");
			spm.addParam(pamvo.getVstatus());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getBegindate())) {
			sql.append(" and bi.doperatedate >= ? ");
			spm.addParam(pamvo.getBegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getEnddate())) {
			sql.append(" and bi.doperatedate <= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getApplybegindate())) {
			sql.append(" and bi.applydate>= ? ");
			spm.addParam(pamvo.getApplybegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getApplyenddate())) {
			sql.append(" and bi.applydate <= ? ");
			spm.addParam(pamvo.getApplyenddate());
		}
		sql.append(" order by bi.doperatedate desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderBVO> queryNumber(MatOrderVO pamvo) {

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select DISTINCT b.vname,  \n") ;
		sql.append("       b.vunit  \n") ; 
//		sql.append("       nvl(b.applynum, 0) applynum,  \n") ; 
//		sql.append("       nvl(b.outnum, 0) outnum  \n") ; 
		sql.append("  from cn_materielbill_b b  \n") ; 
		sql.append("  left join cn_materielbill c on c.pk_materielbill = b.pk_materielbill  \n") ; 
		sql.append("  left join cn_materiel m on m.pk_materiel = b.pk_materiel  \n") ; 
		sql.append("  left join bd_account co on co.pk_corp = c.pk_corp  \n") ; 
		sql.append(" where nvl(b.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(c.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(m.dr, 0) = 0  \n") ; 
		sql.append("   and m.isseal = 1  \n") ; 

		if (!StringUtil.isEmptyWithTrim(pamvo.getBegindate())) {
			sql.append(" and c.doperatedate >= ? ");
			spm.addParam(pamvo.getBegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getEnddate())) {
			sql.append(" and c.doperatedate <= ? ");
			spm.addParam(pamvo.getEnddate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getApplybegindate())) {
			sql.append(" and c.applydate>= ? ");
			spm.addParam(pamvo.getApplybegindate());
		}
		if (!StringUtil.isEmptyWithTrim(pamvo.getApplyenddate())) {
			sql.append(" and c.applydate <= ? ");
			spm.addParam(pamvo.getApplyenddate());
		}
		if (!StringUtil.isEmpty(pamvo.getCorpname())) {
			sql.append(" AND co.unitname like ? ");
			spm.addParam("%" + pamvo.getCorpname() + "%");
		}
		if (pamvo.getVstatus() != null && pamvo.getVstatus() != 0) {
			sql.append("   AND c.vstatus = ? \n");
			spm.addParam(pamvo.getVstatus());
		}

		List<MatOrderBVO> bvoList = (List<MatOrderBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(MatOrderBVO.class));
		if (bvoList != null && bvoList.size() > 0) {
			return bvoList;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaterielFileVO> queryMatFile() {
		StringBuffer sql = new StringBuffer();
		sql.append("select vname, vunit  \n") ;
		sql.append("  from cn_materiel  \n") ; 
		sql.append(" where nvl(dr, 0) = 0  \n") ; 
		sql.append("   and isseal = 1  \n");

		List<MaterielFileVO> bvoList = (List<MaterielFileVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(MaterielFileVO.class));
		if (bvoList != null && bvoList.size() > 0) {
			return bvoList;
		}
		return null;
	}

	@Override
	public List<MatOrderVO> queryAllProvince() {
		StringBuffer sql = new StringBuffer();
		sql.append("  select region_id vprovince,region_name pname,parenter_id parid \n");
		sql.append("     from ynt_area \n");
		sql.append("     where nvl(dr,0) = 0  \n");
		sql.append("     and parenter_id = 1 \n");

		List<MatOrderVO> plist = (List<MatOrderVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(MatOrderVO.class));
		if (plist != null && plist.size() > 0) {
			return plist;
		}
		return null;
	}

	@Override
	public List<MatOrderVO> queryCityByProId(Integer pid) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(pid);
		sql.append("  select region_id vcity,region_name cityname,parenter_id parid \n");
		sql.append("     from ynt_area \n");
		sql.append("     where nvl(dr,0) = 0  \n");
		sql.append("     and parenter_id = ? \n");

		List<MatOrderVO> clist = (List<MatOrderVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(MatOrderVO.class));
		if (clist != null && clist.size() > 0) {
			return clist;
		}
		return null;
	}

	@Override
	public List<MatOrderVO> queryAreaByCid(Integer cid) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(cid);
		sql.append("  select region_id varea,region_name countryname,parenter_id parid \n");
		sql.append("     from ynt_area \n");
		sql.append("     where nvl(dr,0) = 0  \n");
		sql.append("     and parenter_id = ? \n");

		List<MatOrderVO> clist = (List<MatOrderVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(MatOrderVO.class));
		if (clist != null && clist.size() > 0) {
			return clist;
		}
		return null;
	}

	@Override
	public MatOrderVO showDataByCorp(String corpid) {

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(corpid);
		sql.append("  select b.citycounty,b.vprovince,b.vcity,b.varea, \n");
		sql.append("     b.legalbodycode vreceiver,b.phone1 phone \n");
		sql.append("     from bd_account b \n");
		sql.append("     where nvl(b.dr,0) = 0 \n");
		sql.append("     and vprovince is not null \n");
		sql.append("     and b.pk_corp = ? \n");

		MatOrderVO mvo = (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanProcessor(MatOrderVO.class));
		if (mvo != null) {
			if (mvo.getCitycounty() != null) {
				String[] citycountry = mvo.getCitycounty().split("-");
				if (citycountry.length == 3) {
					mvo.setPname(citycountry[0]);
					mvo.setCityname(citycountry[1]);
					mvo.setCountryname(citycountry[2]);
				} else if (citycountry.length == 2) {
					String str = "";
					if (citycountry[1] != null) {
						str = citycountry[1].substring(citycountry[1].length() - 1);
					}
					if ("区".equals(str)) {
						mvo.setCityname("市辖区");
					} else if ("县".equals(str)) {
						mvo.setCityname("县");
					} else if ("市".equals(str)) {
						mvo.setCityname("市");
					}
					mvo.setPname(citycountry[0]);
					mvo.setCountryname(citycountry[1]);
				}

			}
			String[] str = new String[] { "vreceiver", "phone" };
			QueryDeCodeUtils.decKeyUtil(str, mvo, 1);
			return mvo;
		}
		return null;
	}

	@Override
	public void saveApply(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos) {
		if (StringUtil.isEmpty(vo.getPk_materielbill())) {// 新增保存
			setDefaultValue(vo, uservo);
			// 1.新增到订单主表VO
			vo.setPk_corp("000001");
			if ("市辖区".equals(vo.getCityname()) || "市".equals(vo.getCityname()) || "县".equals(vo.getCityname())) {
				vo.setCitycounty(vo.getPname() + "-" + vo.getCountryname());
			} else {
				vo.setCitycounty(vo.getPname() + "-" + vo.getCityname() + "-" + vo.getCountryname());
			}
			vo = (MatOrderVO) singleObjectBO.insertVO("000001", vo);
			// 2.新增到订单子表VO
			for (MatOrderBVO bvo : bvos) {
				bvo.setPk_materielbill(vo.getPk_materielbill());
				singleObjectBO.insertVO("000001", bvo);
			}
		} else {
			// 编辑保存
			// saveEdit();
		}
	}

	/**
	 * 设置默认值
	 * 
	 * @param data
	 * @throws DZFWarpException
	 */
	private void setDefaultValue(MatOrderVO data, UserVO uservo) throws DZFWarpException {
		data.setCoperatorid(uservo.getCuserid());
		data.setDoperatedate(new DZFDate());
		data.setDoperatedate(new DZFDate());
		data.setPk_corp("000001");
		data.setVstatus(data.VSTATUS_1);// 合同状态：默认为待审核
	}

}
