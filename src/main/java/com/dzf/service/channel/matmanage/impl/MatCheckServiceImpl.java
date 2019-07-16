package com.dzf.service.channel.matmanage.impl;

import java.text.ParseException;
import java.util.ArrayList;
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
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatCheckService;
import com.dzf.service.channel.matmanage.IMatCommonService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;

@Service("matcheck")
public class MatCheckServiceImpl implements IMatCheckService {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IUserService userser;
	
	@Autowired
	private IPubService pubser;
	
	@Autowired
	private IMatCommonService matcomm;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ChnAreaBVO> queryComboBox(UserVO uservo)   throws DZFWarpException{
		
		StringBuffer corpsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		corpsql.append(" SELECT distinct  b.userid \n");
		corpsql.append(" FROM cn_chnarea_b b \n");
		corpsql.append(" left join cn_chnarea a on \n");
		corpsql.append(" a.pk_chnarea = b.pk_chnarea \n");
		corpsql.append("  where nvl(a.dr,0)= 0 and \n");
		corpsql.append("  nvl(b.dr,0)= 0 and \n");
		corpsql.append("  a.userid = ? \n");
		sp.addParam(uservo.getCuserid());
		List<ChnAreaBVO> bvolist = (List<ChnAreaBVO>) singleObjectBO.executeQuery(corpsql.toString(), sp, new BeanListProcessor(ChnAreaBVO.class));
		if(bvolist!=null && bvolist.size()>0){
			for (ChnAreaBVO bvo : bvolist) {
				  uservo = userser.queryUserJmVOByID(bvo.getUserid());
				if(uservo != null){
					bvo.setUsername(uservo.getUser_name());
				}
			}
			return bvolist;
		}
		
		return null;
	}

	@Override
	public void updateStatusById(MatOrderVO data,UserVO uservo,MatOrderBVO[] bvos)   throws DZFWarpException{
		
		String uuid = UUID.randomUUID().toString();
        try {
        	boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid, 60);
			if (!lockKey) {
				throw new BusinessException("合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>");
			}
			
			checkData(data.getPk_materielbill(), data.getUpdatets());
			//修改申请通过数
			String[] updatets = {"succnum"};
			if(bvos!=null && bvos.length>0){
				for (MatOrderBVO bvo : bvos) {
					if(data.getVstatus()==2){//审核通过
						if(bvo.getSuccnum()==null){
							bvo.setSuccnum(0);
						}
						bvo.setSuccnum(bvo.getSuccnum()+bvo.getApplynum());
					}
					singleObjectBO.update(bvo, updatets);
				}
			}
			
			data.setAuditerid(uservo.getCuserid());
			data.setAuditdate(new DZFDate());
			String[] updates ={ "vstatus","vreason","auditerid","auditdate"};
			singleObjectBO.update(data, updates);
			
			if(data.getVstatus()==1){//反审核后
				data.setAuditdate(null);//清除之前的审核人信息
				data.setAuditerid(null);
				String[] nupdatets ={ "auditerid","auditdate"};
				singleObjectBO.update(data, nupdatets);
				
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

	
	@Override
	public MatOrderVO queryById(String pk_materielbill)  throws DZFWarpException {
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_materielbill);
		sql.append("  select vcontcode,updatets,pk_materielbill \n");
		sql.append("       from cn_materielbill \n");
		sql.append("     where nvl(dr,0) = 0 \n");
		sql.append("     and pk_materielbill = ? \n");
		
		MatOrderVO vo=(MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MatOrderVO.class));
		return vo;
	}
	
	 /**
 	 * 检查是否是最新数据
 	 * @param pk_materiel
 	 * @param updatets
 	 * @return
 	 */
 	private MatOrderVO checkData(String pk_materielbill, DZFDateTime updatets)  throws DZFWarpException {
 		MatOrderVO vo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, pk_materielbill);
 		if (!updatets.equals(vo.getUpdatets())) {
 			throw new BusinessException("合同编号：" + vo.getVcontcode() + ",数据已发生变化;<br>");
 		}
 		return vo;
 	}

	@Override
	public MatOrderVO queryUserData(UserVO uservo, String mid) {
		
	    MatOrderVO mvo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, mid);
	    uservo = userser.queryUserJmVOByID(uservo.getCuserid());
		mvo.setAudname(uservo.getUser_name());//审核人
		uservo = userser.queryUserJmVOByID(mvo.getCoperatorid());
		mvo.setApplyname(uservo.getUser_name());//申请人
		QueryDeCodeUtils.decKeyUtil(new String[] { "applyname","audname" }, mvo, 1);
		return mvo;
	}
	
	@Override
	public int queryTotalRow(QryParamVO qvo,MatOrderVO parm) throws DZFWarpException{
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo,parm,null,null);
		return multBodyObjectBO.queryDataTotal(MatOrderVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> query(QryParamVO qvo,MatOrderVO pamvo, UserVO uservo)  throws DZFWarpException {
		String vpro = "";
		String vcorp = "";
		List<MatOrderVO> retlist = new ArrayList<MatOrderVO>();
		// 添加数据权限
		List<ChnAreaBVO> list = matcomm.queryPro(uservo, "2",vpro,vcorp);
		String pro = list!=null && list.size()>0 ? list.get(0).getVprovname() : null; 
		String corp = list!=null && list.size()>0 ? list.get(0).getCorpname() : null; 
		
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo, pamvo, pro, corp);
		List<MatOrderVO> mlist = (List<MatOrderVO>)singleObjectBO.executeQuery(sqpvo.getSql(),
				sqpvo.getSpm(),new BeanListProcessor(MatOrderVO.class));
		//HashMap<String, UserVO> map = userser.queryUserMap(uservo.getPk_corp(), true);
		Map<String, UserVO> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		if(mlist!=null && mlist.size()>0){
			for (MatOrderVO mvo : mlist) {
				uservo = marmap.get(mvo.getFathercorp());
				if(uservo != null ){
					mvo.setVmanagername(uservo.getUser_name());// 渠道经理
					if(!StringUtil.isEmpty(pamvo.getVmanagerid()) 
							&& uservo.getCuserid().equals(pamvo.getVmanagerid())){
						retlist.add(mvo);
					}
				}
			}
			if(StringUtil.isEmpty(pamvo.getVmanagerid())){
				retlist.addAll(mlist);
			}
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname", "corpname","applyname" }, mlist, 1);
		}
		if (StringUtil.isEmpty(pro) && StringUtil.isEmpty( corp)) {// 没有数据可以查看
			retlist = null;
		}
		return retlist;
	}

	private QrySqlSpmVO getQrySqlSpm(QryParamVO qvo,MatOrderVO pamvo,
			 String vpro, String vcorp)  throws DZFWarpException {
		
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT distinct bi.pk_materielbill,  \n") ;
		sql.append("                bi.vcontcode,  \n") ; 
		sql.append("                bi.vaddress,  \n") ; 
		sql.append("                bi.vreceiver,  \n") ; 
		sql.append("                bi.phone,  \n") ; 
		sql.append("                bi.fastcode,  \n") ; 
		sql.append("                nvl(bi.fastcost,0) fastcost,  \n") ; 
		sql.append("                bi.deliverdate,  \n") ; 
		sql.append("                bi.vmemo,  \n") ; 
		sql.append("                bi.vreason,  \n") ; 
		sql.append("                bi.vstatus,  \n") ; 
		sql.append("                bi.doperatedate,  \n") ; 
		//sql.append("                bi.coperatorid,  \n") ; 
		sql.append("                su1.user_name applyname,  \n") ; 
		sql.append("                bi.applydate,  \n") ; 
		sql.append("                bi.fathercorp,  \n") ; 
		sql.append("                bi.corpname,  \n") ; 
		//sql.append("                bi.vmanagerid,  \n") ; 
		//sql.append("                su.user_name vmanagername,  \n") ; 
		sql.append("                bi.ts,  \n") ; 
		sql.append("                b.vname,  \n") ; 
		sql.append("                b.vunit,  \n") ; 
		sql.append("                nvl(b.outnum, 0) outnum,  \n") ; 
		sql.append("                nvl(b.applynum, 0) applynum,  \n") ; 
		sql.append("                log.vname logname,  \n") ; 
		sql.append("                ba.vprovname proname,  \n") ; 
		sql.append("                ba.vprovince,  \n") ;
		sql.append("                c.areaname \n") ; 
		sql.append("  from cn_materielbill bi  \n") ; 
		sql.append("  left join cn_materielbill_b b on bi.pk_materielbill = b.pk_materielbill  \n") ; 
		sql.append("  left join cn_logistics log on log.pk_logistics = bi.pk_logistics  \n") ; 
		sql.append("  left join cn_chnarea_b ba on ba.vprovince = bi.vprovince  \n") ; 
		sql.append("  left join cn_chnarea c on c.pk_chnarea = ba.pk_chnarea  \n") ; 
		//sql.append("  left join sm_user su on su.cuserid = bi.vmanagerid \n") ; 
		sql.append("  left join sm_user su1 on su1.cuserid = bi.coperatorid \n") ; 
		sql.append("  where nvl(bi.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(b.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(log.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(ba.dr, 0) = 0 and ba.type = 1 \n") ; 
		sql.append("   and nvl(c.dr, 0) = 0 and c.type = 1 \n") ;
		sql.append("   and b.applynum >=0 \n") ;
		
		if (!StringUtil.isEmpty(pamvo.getCorpname())) {
			sql.append(" AND  bi.corpname like ? ");
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

	@SuppressWarnings("unchecked")
	@Override
	public MatOrderVO queryDataById(MatOrderVO mvo, String id, UserVO uservo,String stype)
			throws DZFWarpException {
		String message = "";

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(id);
		sql.append("select  m.pk_materielbill, \n ");
		sql.append("   m.vcontcode, \n");
		sql.append("   m.vstatus,m.vreason, \n");
		sql.append("   m.fathercorp,m.corpname, \n");
		sql.append("   m.vprovince,m.vcity,m.varea,m.citycounty, \n ");
		sql.append("   m.vaddress,m.vreceiver,m.phone,m.vmemo, \n ");
		sql.append("   m.coperatorid,m.applydate, \n");
		sql.append("   m.pk_logistics, \n");
		sql.append("   m.fastcode,m.fastcost, \n");
		sql.append("   m.deliverid,m.deliverdate, \n");
		sql.append("   m.auditerid,m.auditdate, \n");
		sql.append("   lg.vname logname \n");

		sql.append("     from cn_materielbill m \n");
		sql.append("     left join cn_logistics lg on \n");
		sql.append("     lg.pk_logistics = m.pk_logistics \n");
		sql.append("     where nvl(m.dr,0) = 0 and nvl(lg.dr,0) = 0 \n");
		sql.append("     and m.pk_materielbill = ? \n");

		MatOrderVO vo = (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanProcessor(MatOrderVO.class));
		if (vo != null) {
				if (vo.getDeliverid() != null) {
					uservo = userser.queryUserJmVOByID(vo.getDeliverid());
					if (uservo != null) {
						vo.setDename(uservo.getUser_name());
					}
				} else {
					uservo = userser.queryUserJmVOByID(uservo.getCuserid());
					if (uservo != null) {
						vo.setDename(uservo.getUser_name());// 发货人
					}
				}
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
			ssql.append("  select b.pk_materielbill_b, \n");
			ssql.append("    b.vname,b.vunit,b.applynum,b.outnum,nvl(l.intnum,0)-nvl(l.outnum,0) enapplynum, \n");
			ssql.append("    l.pk_materiel \n");
			ssql.append("    from cn_materielbill_b b \n");
			ssql.append("    left join cn_materiel l on \n");
			ssql.append("    b.pk_materiel = l.pk_materiel \n");
			ssql.append("    where nvl(b.dr,0) = 0 \n");
			ssql.append("    and b.pk_materielbill = ? \n");
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

			if (stype != null && "1".equals(stype)) {
				// 点击审核校验
				message = matcomm.checkIsInfo(vo, bvos, message);
				if (!message.isEmpty()) {
					vo.setMessage(message);
				}
			}
			return vo;
		}
		return null;
	}

	@Override
	public String queryLastReason(String id) {
		MatOrderVO vo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, id);
		return vo.getVreason();
	}
	
}

	