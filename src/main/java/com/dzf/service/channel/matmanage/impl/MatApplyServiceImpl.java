package com.dzf.service.channel.matmanage.impl;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatApplyService;
import com.dzf.service.channel.matmanage.IMatCommonService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("matapply")
public class MatApplyServiceImpl implements IMatApplyService {

	private Logger logger = Logger.getLogger(this.getClass());

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private IPubService pubser;

	@Autowired
	private IUserService userser;
	
	@Autowired
	private IMatCommonService matcomm;

	@Override
	public int queryTotalRow(QryParamVO qvo, MatOrderVO parm) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo, parm, null, null);
		return multBodyObjectBO.queryDataTotal(MatOrderVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> query(QryParamVO qvo, MatOrderVO pamvo, UserVO uservo)
			throws DZFWarpException {
		String vpro = "";
		String vcorp = "";
		// 添加数据权限
		List<ChnAreaBVO> list = matcomm.queryPro(uservo, "3",vpro,vcorp);

		String pro = list!=null && list.size()>0 ? list.get(0).getVprovname() : null; 
		String corp = list!=null && list.size()>0 ? list.get(0).getCorpname() : null; 
		
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo, pamvo,pro,corp);
		List<MatOrderVO> mlist = (List<MatOrderVO>) singleObjectBO.executeQuery(sqpvo.getSql(), sqpvo.getSpm(),
				new BeanListProcessor(MatOrderVO.class));
		//HashMap<String, UserVO> map = userser.queryUserMap(uservo.getPk_corp(), true);
		Map<String, UserVO> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		if (mlist != null && mlist.size() > 0) {
			for (MatOrderVO mvo : mlist) {
				uservo = marmap.get(mvo.getFathercorp());
				if (uservo != null) {
					mvo.setVmanagername(uservo.getUser_name());// 渠道经理
				}
			}
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname", "corpname","applyname" }, mlist, 1);
		}

		if (StringUtil.isEmpty(pro) && StringUtil.isEmpty( corp)) {// 没有数据可以查看
			mlist = null;
		}
		return mlist;
	}

	private QrySqlSpmVO getQrySqlSpm(QryParamVO qvo, MatOrderVO pamvo, String vpro, String vcorp)
			throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT distinct bi.pk_materielbill,    ") ;
		sql.append("                bi.vcontcode,    ") ; 
		sql.append("                bi.vaddress,    ") ; 
		sql.append("                bi.vreceiver,    ") ; 
		sql.append("                bi.phone,    ") ; 
		sql.append("                bi.fastcode,    ") ; 
		sql.append("                nvl(bi.fastcost,0) fastcost,    ") ; 
		sql.append("                bi.deliverdate,    ") ; 
		sql.append("                bi.vmemo,    ") ; 
		sql.append("                bi.vreason,    ") ; 
		sql.append("                bi.vstatus,    ") ; 
		sql.append("                bi.doperatedate,    ") ; 
		//sql.append("                bi.coperatorid,    ") ; 
		sql.append("                su1.user_name applyname,    ") ; 
		sql.append("                bi.applydate,    ") ; 
		sql.append("                bi.fathercorp,    ") ; 
		sql.append("                bi.corpname,    ") ; 
		sql.append("                bi.vmanagerid,    ") ; 
		sql.append("                bi.ts,    ") ; 
		sql.append("                b.vname,    ") ; 
		sql.append("                b.vunit,    ") ; 
		sql.append("                nvl(b.outnum, 0) outnum,    ") ; 
		sql.append("                nvl(b.applynum, 0) applynum,    ") ; 
		sql.append("                log.vname logname,    ") ; 
		sql.append("                ba.vprovname proname,    ") ; 
		sql.append("                ba.vprovince,    ") ;
		sql.append("                c.areaname   ") ; 
		sql.append("  from cn_materielbill bi    ") ; 
		sql.append("  left join cn_materielbill_b b on bi.pk_materielbill = b.pk_materielbill    ") ; 
		sql.append("  left join cn_logistics log on log.pk_logistics = bi.pk_logistics    ") ; 
		sql.append("  left join cn_chnarea_b ba on ba.vprovince = bi.vprovince    ") ; 
		sql.append("  left join cn_chnarea c on c.pk_chnarea = ba.pk_chnarea    ") ; 
		sql.append("  left join sm_user su1 on su1.cuserid = bi.coperatorid   ") ; 
		sql.append("  where nvl(bi.dr, 0) = 0    ") ; 
		sql.append("   and nvl(b.dr, 0) = 0    ") ; 
		sql.append("   and nvl(log.dr, 0) = 0    ") ; 
		sql.append("   and nvl(ba.dr, 0) = 0 and ba.type = 1   ") ; 
		sql.append("   and nvl(c.dr, 0) = 0 and c.type = 1   ") ;
		sql.append("   and b.applynum >=0   ") ;
		
		if (!StringUtil.isEmpty(pamvo.getCorpname())) {
			sql.append(" AND  bi.corpname like ? ");
			spm.addParam("%" + pamvo.getCorpname() + "%");
		}
		
		if (pamvo.getVstatus() != null && pamvo.getVstatus() != 0) {
			sql.append("   AND bi.vstatus = ?   ");
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
		if(!StringUtil.isEmpty(vpro) && !StringUtil.isEmpty(vcorp)){
			sql.append(" and (ba.vprovince in "+vpro);
			sql.append(" or ba.pk_corp in "+vcorp+")");
		}
		if(!StringUtil.isEmpty(vpro) && StringUtil.isEmpty(vcorp)){
			sql.append(" and ba.vprovince in "+vpro);
		}
		if(!StringUtil.isEmpty(vcorp) && StringUtil.isEmpty(vpro)){
			sql.append(" and ba.pk_corp in "+vcorp);
		}
		
		if(!StringUtil.isEmpty(qvo.getVqrysql())){
			sql.append(qvo.getVqrysql());
		}
		
		sql.append(" order by bi.ts desc");
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
		

	}

	@Override
	public MatOrderVO showDataByCorp(String corpid) throws DZFWarpException {

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(corpid);
		sql.append("  select b.citycounty,b.vprovince,b.vcity,b.varea,   ");
		sql.append("     b.legalbodycode vreceiver,b.phone1 phone   ");
		sql.append("     from bd_account b   ");
		sql.append("     where nvl(b.dr,0) = 0   ");
		sql.append("     and vprovince is not null   ");
		sql.append("     and b.pk_corp = ?   ");

		MatOrderVO mvo = (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanProcessor(MatOrderVO.class));
		if (mvo != null) {
			matcomm.setCitycountry(mvo);
			String[] str = new String[] { "vreceiver", "phone" };
			QueryDeCodeUtils.decKeyUtil(str, mvo, 1);
			return mvo;
		}
		return null;
	}

	@Override
	public String saveApply(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos, String stype, String kind)
			throws DZFWarpException {

		String message = "";
		if (!StringUtil.isEmpty(vo.getPk_materielbill())) {
			MatOrderVO mvo = matcomm.queryById(vo.getPk_materielbill());
			if (mvo.getVstatus() != null && mvo.getVstatus() == 4) {// 已驳回的修改保存
				save(vo, uservo, bvos);
				return null;
			}
		}
		if (StringUtil.isEmpty(kind)) {// 不需要校验（修改保存，详情保存等）
			save(vo, uservo, bvos);
			return null;
		}
		
		if (stype != null && "1".equals(stype)) {// 提示后保存
			save(vo, uservo, bvos);
			return null;
		} else {

			// 获取上个季度时间
			try {
				if (vo.getDedubegdate() != null && vo.getDuduenddate() != null) {
					String lastQuarter = matcomm.getLastQuarter(vo.getDedubegdate(), vo.getDuduenddate());
					String[] quarter = lastQuarter.split(",");
					vo.setDedubegdate(quarter[0]);
					vo.setDuduenddate(quarter[1]);
				}
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
			}

			// 校验
			message = matcomm.checkIsInfo(vo, bvos, message);
			if (message.isEmpty()) {
				save(vo, uservo, bvos);
			}
		}

		return message;
	}


	@Override
	public void editSave(MatOrderVO data) {

		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid,
					60);
			if (!lockKey) {
				throw new BusinessException("合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>");
			}
			
			matcomm.checkData(data.getPk_materielbill(), data.getUpdatets());

			data.setCitycounty(data.getPname() + "-" + data.getCityname() + "-" + data.getCountryname());
			String[] updates = { "vprovince", "vcity", "varea", "citycounty", "vaddress", "vreceiver", "phone",
					"fastcost", "pk_logistics", "fastcode" };
			singleObjectBO.update(data, updates);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielbill(), uuid);
		}
	}

	private void saveEdit(MatOrderVO data, MatOrderBVO[] bvos, UserVO uservo) throws DZFWarpException {
		
		matcomm.checkIsOperOrder(data.getVstatus(), "只有待审批或已驳回状态的申请单支持修改！");
		String uuid = UUID.randomUUID().toString();
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid,
					60);
			if (!lockKey) {
				throw new BusinessException("合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>");
			}
			matcomm.checkData(data.getPk_materielbill(), data.getUpdatets());
			
				// 1.修改主订单
				if (data.getVstatus() == 4) {// 已驳回的修改
					data.setVstatus(1);
					String[] updates = { "vcontcode", "fathercorp", "corpname", "vprovince", "vcity", "varea",
							"vaddress", "vreceiver", "phone", "vmemo", "applydate", "vstatus" };
					singleObjectBO.update(data, updates);
				} else {
					String[] updates = { "vcontcode", "fathercorp", "corpname", "vprovince", "vcity", "varea",
							"vaddress", "vreceiver", "phone", "vmemo", "applydate" };
					singleObjectBO.update(data, updates);
				}

			// 2.修改子订单

			// 删除原有子订单
			SQLParameter spm = new SQLParameter();
			spm.addParam(data.getPk_materielbill());
			String sql = "delete from cn_materielbill_b where pk_materielbill = ? ";
			singleObjectBO.executeUpdate(sql, spm);

			// 增加修改后的子订单
			List<MatOrderBVO> bvolist = Arrays.asList(bvos);
			if (bvolist != null && bvolist.size() > 0) {
				for (MatOrderBVO bvo : bvolist) {
					bvo.setPk_materielbill(data.getPk_materielbill());
					singleObjectBO.insertVO("000001", bvo);

				}
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(data.getTableName(), data.getPk_materielbill(), uuid);
		}
	}

	/**
	 * 保存
	 * 
	 * @param vo
	 * @param uservo
	 * @param bvos
	 * @param type
	 */
	private void save(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos) throws DZFWarpException {

		if (StringUtil.isEmpty(vo.getPk_materielbill())) {// 新增保存
			matcomm.setDefaultValue(vo, uservo);
			// 1.新增到订单主表VO
			vo.setPk_corp("000001");
			if (matcomm.isInteger(vo.getPname()) && matcomm.isInteger(vo.getCityname()) && matcomm.isInteger(vo.getCountryname())) {
				vo.setVprovince(Integer.parseInt(vo.getPname()));
				vo.setVcity(Integer.parseInt(vo.getCityname()));
				vo.setVarea(Integer.parseInt(vo.getCountryname()));
			}
			if ("市辖区".equals(vo.getCityname()) || "市".equals(vo.getCityname()) || "县".equals(vo.getCityname())) {
				vo.setCitycounty(vo.getPname() + "-" + vo.getCountryname());
			} else {
				vo.setCitycounty(vo.getPname() + "-" + vo.getCityname() + "-" + vo.getCountryname());
			}
			vo = (MatOrderVO) singleObjectBO.insertVO("000001", vo);
			// 2.新增到订单子表VO
			for (MatOrderBVO bvo : bvos) {
				bvo.setPk_materielbill(vo.getPk_materielbill());
				bvo.setSuccnum(0);// 审核通过数 默认为0
				singleObjectBO.insertVO("000001", bvo);
			}
		} else {
			// 编辑保存
			MatOrderVO mvo = matcomm.queryById(vo.getPk_materielbill());
			vo.setVstatus(mvo.getVstatus());
			saveEdit(vo, bvos, uservo);
		}
	}



	@SuppressWarnings("unchecked")
	@Override
	public MatOrderVO queryDataById(MatOrderVO mvo, String id, UserVO uservo)
			throws DZFWarpException {

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(id);
		sql.append("select  m.pk_materielbill,    ");
		sql.append("   m.vcontcode,   ");
		sql.append("   m.vstatus,m.vreason,   ");
		sql.append("   m.fathercorp,m.corpname,   ");
		sql.append("   m.vprovince,m.vcity,m.varea,m.citycounty,    ");
		sql.append("   m.vaddress,m.vreceiver,m.phone,m.vmemo,    ");
		sql.append("   m.coperatorid,m.applydate,   ");
		sql.append("   m.pk_logistics,   ");
		sql.append("   m.fastcode,m.fastcost,   ");
		sql.append("   m.deliverid,m.deliverdate,   ");
		sql.append("   m.auditerid,m.auditdate,   ");
		sql.append("   m.updatets,   ");
		sql.append("   lg.vname logname   ");

		sql.append("     from cn_materielbill m   ");
		sql.append("     left join cn_logistics lg on   ");
		sql.append("     lg.pk_logistics = m.pk_logistics   ");
		sql.append("     where nvl(m.dr,0) = 0 and nvl(lg.dr,0) = 0   ");
		sql.append("     and m.pk_materielbill = ?   ");

		MatOrderVO vo = (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanProcessor(MatOrderVO.class));
		if (vo != null) {
			
			if (vo.getCoperatorid() != null) {
				uservo = userser.queryUserJmVOByID(vo.getCoperatorid());
				if (uservo != null) {
					vo.setApplyname(uservo.getUser_name());// 申请人
				}
			}
			if (vo.getAuditerid() != null) {
				uservo = userser.queryUserJmVOByID(vo.getAuditerid());
				if (uservo != null) {
					vo.setAudname(uservo.getUser_name());// 审核人
				}
			}

			StringBuffer ssql = new StringBuffer();
			SQLParameter sspm = new SQLParameter();
			sspm.addParam(id);
			ssql.append("  select b.pk_materielbill_b,   ");
			ssql.append("    b.vname,b.vunit,b.applynum,b.outnum,nvl(l.intnum,0)-nvl(l.outnum,0) enapplynum,   ");
			ssql.append("    l.pk_materiel   ");
			ssql.append("    from cn_materielbill_b b   ");
			ssql.append("    left join cn_materiel l on   ");
			ssql.append("    b.pk_materiel = l.pk_materiel   ");
			ssql.append("    where nvl(b.dr,0) = 0   ");
			ssql.append("    and b.pk_materielbill = ?   ");
			List<MatOrderBVO> bvolist = (List<MatOrderBVO>) singleObjectBO.executeQuery(ssql.toString(), sspm,
					new BeanListProcessor(MatOrderBVO.class));
			MatOrderBVO[] b = new MatOrderBVO[bvolist.size()];
			MatOrderBVO[] bvos = (MatOrderBVO[]) bvolist.toArray(b);
			if (bvolist != null && bvolist.size() > 0) {
				vo.setChildren(bvos);
			}

			matcomm.setCitycountry(vo);

			if (!StringUtil.isEmpty(mvo.getDedubegdate()) && !StringUtil.isEmpty(mvo.getDuduenddate())) {
				// 获取上个季度时间
				try {
					String lastQuarter = matcomm.getLastQuarter(mvo.getDedubegdate(), mvo.getDuduenddate());
					String[] quarter = lastQuarter.split(",");
					vo.setDedubegdate(quarter[0]);
					vo.setDuduenddate(quarter[1]);
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
				}
			}

			
			return vo;
		}
		return null;
	}

	

	
	@Override
	public void delete(MatOrderVO qryvo) throws DZFWarpException {

		MatOrderVO mvo = matcomm.queryById(qryvo.getPk_materielbill());
		if (mvo.getUpdatets() != null) {
			qryvo.setUpdatets(mvo.getUpdatets());
			qryvo.setStatus(mvo.getStatus());
		}

		matcomm.checkIsOperOrder(qryvo.getStatus(), "只有待审批或已驳回状态的申请单支持删除！");

		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(qryvo.getTableName(), qryvo.getPk_materielbill(), uuid, 60);
			// 1.删除主订单
			SQLParameter spm = new SQLParameter();
			spm.addParam(qryvo.getPk_materielbill());
			String sql = " DELETE FROM cn_materielbill WHERE pk_materielbill = ? ";
			singleObjectBO.executeUpdate(sql, spm);
			// 2.删除子订单
			sql = "DELETE FROM cn_materielbill_b WHERE pk_materielbill = ?";
			singleObjectBO.executeUpdate(sql, spm);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(qryvo.getTableName(), qryvo.getPk_materielbill(), uuid);
		}

	}

	

	/**
	 * 查询直接负责的加盟商
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CorpVO> queryChannel(UserVO uservo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(uservo.getCuserid());
		sql.append("    select distinct   ");
		// sql.append(" b.vprovince,b.isCharge,   ");
		sql.append("       c.pk_corp,c.innercode,c.unitname   ");
		sql.append("       from cn_chnarea a   ");
		sql.append("       left join cn_chnarea_b b on   ");
		sql.append("       a.pk_chnarea = b.pk_chnarea   ");
		sql.append("       left join bd_account c on   ");
		sql.append("       c.vprovince = b.vprovince   ");
		sql.append("       where nvl(a.dr,0) = 0   ");
		sql.append("       and nvl(b.dr,0) = 0    ");
		sql.append("       and nvl(c.dr,0) = 0    ");
		sql.append("       and a.type =1 and b.type = 1   ");
		sql.append("       and b.userid = ?   ");

		List<CorpVO> corpList = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CorpVO.class));
		if (corpList != null && corpList.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, corpList, 1);
			return corpList;
		}
		return null;
	}

	@Override
	public MatOrderVO queryUserData(UserVO uservo) {

		uservo = userser.queryUserJmVOByID(uservo.getCuserid());
		MatOrderVO mvo = new MatOrderVO();
		if (uservo != null) {
			mvo.setApplyname(uservo.getUser_name());
			QueryDeCodeUtils.decKeyUtil(new String[] { "applyname" }, mvo, 1);
		}
		return mvo;
	}

	
}
