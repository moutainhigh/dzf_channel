package com.dzf.service.channel.matmanage.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.dzf.dao.jdbc.framework.processor.ColumnListProcessor;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatCommonService;
import com.dzf.service.channel.matmanage.IMatHandleService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;


@Service("mathandle")
public class MatHandleServiceImpl implements IMatHandleService {

	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private IPubService pubser;

	@Autowired
	private IUserService userser;
	
	@Autowired
	private IMatCommonService matcomm;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> queryComboBox()   throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT lg.pk_logistics, \n");
		sql.append("    lg.vname logname \n");
		sql.append(" FROM cn_logistics lg \n");
		sql.append("  where nvl(lg.dr,0)= 0 \n");
		List<MatOrderVO> volist = (List<MatOrderVO>) singleObjectBO.executeQuery(sql.toString(), null, new BeanListProcessor(MatOrderVO.class));
		return volist;
	}

	@Override
	public MatOrderVO queryIsExist(String sTmp) throws DZFWarpException {
		String enCode = CodeUtils1.enCode(sTmp);
		SQLParameter spm = new SQLParameter();
		spm.addParam(enCode);
		String sql = "select pk_corp fathercorp,unitname corpname from bd_account where nvl(dr,0) = 0 and unitname = ?";
		MatOrderVO mvo = (MatOrderVO) singleObjectBO.executeQuery(sql, spm, new BeanProcessor(MatOrderVO.class));
	    return mvo;
	}

	@Override
	public MatOrderVO getFullVO(MatOrderVO excelvo, String corpname,
			String managename,String date,String logname,UserVO uservos) {
		//完善主订单
		excelvo.setApplydate(new DZFDate(date));
		excelvo.setDoperatedate(new DZFDate(date));
		excelvo.setAuditdate(new DZFDate(date));
		excelvo.setPk_corp("000001");
		excelvo.setVstatus(3);
		excelvo.setAuditerid(uservos.getCuserid());
		excelvo.setDeliverid(uservos.getCuserid());
		
		
		MatOrderVO vo = queryIsExist(corpname);
		if(vo!=null){
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			spm.addParam(vo.getFathercorp());
			sql.append("   select citycounty,vprovince,vcity,varea \n");
			sql.append("     from bd_account \n");
			sql.append("     where vprovince is not null \n");
			sql.append("     and nvl(dr,0) = 0 \n");
			sql.append("     and pk_corp= ? \n");
			MatOrderVO mvo = (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MatOrderVO.class));
			if(mvo!=null){
				excelvo.setCitycounty(mvo.getCitycounty());
				excelvo.setVprovince(mvo.getVprovince());
				excelvo.setVcity(mvo.getVcity());
				excelvo.setVarea(mvo.getVarea());
			}
			excelvo.setFathercorp(vo.getFathercorp());
			
			//根据渠道经理获取申请人ID
			StringBuffer ssql = new StringBuffer();
			SQLParameter ssspm = new SQLParameter();
			String enCode = CodeUtils1.enCode(managename);
			ssspm.addParam(enCode);
			ssspm.addParam("000001");
			ssql.append("select cuserid coperatorid from sm_user \n");
			ssql.append("    where nvl(dr,0) = 0 and \n");
			ssql.append("    user_name = ? and pk_corp =? \n");
			MatOrderVO mmvo = (MatOrderVO) singleObjectBO.executeQuery(ssql.toString(), ssspm, new BeanProcessor(MatOrderVO.class));
			if(mmvo!=null){
				excelvo.setCoperatorid(mmvo.getCoperatorid());
				//excelvo.setVmanagerid(mmvo.getCoperatorid());
			}
			
			String string = "select pk_logistics from cn_logistics where nvl(dr,0)=0 and vname = ? ";
			SQLParameter spp = new SQLParameter();
			spp.addParam(logname);
			MatOrderVO vvo = (MatOrderVO) singleObjectBO.executeQuery(string, spp, new BeanProcessor(MatOrderVO.class));
			if(vvo!=null){
				excelvo.setPk_logistics(vvo.getPk_logistics());
			}
		}
		
		//完善子订单
		MatOrderBVO[] bvos = (MatOrderBVO[]) excelvo.getChildren();
		for (MatOrderBVO mbvo : bvos) {
			String sql = "select pk_materiel,vunit from cn_materiel where nvl(dr,0)=0 and vname = ? ";
			SQLParameter spm = new SQLParameter();
			spm.addParam(mbvo.getVname());
			MaterielFileVO fvo = (MaterielFileVO) singleObjectBO.executeQuery(sql, spm, new BeanProcessor(MaterielFileVO.class));
			if(fvo!=null){
				mbvo.setPk_materiel(fvo.getPk_materiel());
				mbvo.setVunit(fvo.getVunit());
				mbvo.setApplynum(mbvo.getOutnum());
			}
		}
		
	  return excelvo;
	}


	@SuppressWarnings("unchecked")
	@Override
	public MatOrderVO[] saveImoprt(MatOrderVO[] vos) {
		
		String sql = "select vname from cn_materiel where nvl(dr,0) = 0 ";
		List<String> nameList = (List<String>) singleObjectBO.executeQuery(sql, null, new ColumnListProcessor("vname"));
		
		Map<String,Integer> map = new HashMap<String,Integer>(); 
		for (MatOrderVO mvo : vos) {
			MatOrderBVO[] bvo = (MatOrderBVO[]) mvo.getChildren();
			
			mvo.setChildren(null);
			singleObjectBO.insertVO("000001", mvo);
			for (MatOrderBVO mbvo : bvo) {
				if(nameList.contains(mbvo.getVname())
				   && !StringUtil.isEmpty(mbvo.getOutnum().toString())){
					mbvo.setPk_materielbill(mvo.getPk_materielbill());
					singleObjectBO.insertVO("000001", mbvo);
					
				     //导入的物料在物料档案表中累加物料发货数量
					if(!map.containsKey(mbvo.getVname())){
						map.put(mbvo.getVname(), mbvo.getOutnum());
					}else{
						map.replace(mbvo.getVname(), map.get(mbvo.getVname()),
								map.get(mbvo.getVname()) + mbvo.getOutnum());
					}
					
				}
			}
			
		}
		
		for(String key:map.keySet()){
			StringBuffer ssql = new StringBuffer();
			SQLParameter sspm = new SQLParameter();
			sspm.addParam(map.get(key));
			sspm.addParam(key);
			ssql.append("update cn_materiel ");
			ssql.append("  set outnum = outnum + ? ");
			ssql.append("  where nvl(dr,0) = 0 and vname = ?");
			singleObjectBO.executeUpdate(ssql.toString(), sspm);
		}
		return vos;
	}

	@Override
	public List<String> queryAllMatName() {
		String sql = "select vname from cn_materiel where nvl(dr,0) = 0";
		return (List<String>) singleObjectBO.executeQuery(sql, null, new ColumnListProcessor("vname"));
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> query(QryParamVO qvo,MatOrderVO pamvo, UserVO uservo)  throws DZFWarpException {
		
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo,pamvo);
		List<MatOrderVO> retlist = new ArrayList<MatOrderVO>();
		List<MatOrderVO> list = (List<MatOrderVO>)singleObjectBO.executeQuery(sqpvo.getSql(),
				sqpvo.getSpm(),new BeanListProcessor(MatOrderVO.class));
		//HashMap<String, UserVO> map = userser.queryUserMap(uservo.getPk_corp(), true);
		Map<String, UserVO> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		if(list!=null && list.size()>0){
			for (MatOrderVO mvo : list) {
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
				retlist.addAll(list);
			}
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname", "corpname","applyname" }, list, 1);
		}
		return retlist;
	}

	private QrySqlSpmVO getQrySqlSpm(QryParamVO qvo,MatOrderVO pamvo)  throws DZFWarpException {
		
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
		sql.append("  left join bd_account co on co.pk_corp = bi.fathercorp  \n") ; 
		sql.append("  left join sm_user su1 on su1.cuserid = bi.coperatorid \n") ; 
		sql.append("  where nvl(bi.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(b.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(log.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(ba.dr, 0) = 0 and ba.type = 1 \n") ; 
		sql.append("   and nvl(c.dr, 0) = 0 and c.type = 1 \n") ;
		sql.append("   and b.applynum >=0 \n") ;
		sql.append("   and nvl(co.dr, 0) = 0  \n") ; 
		sql.append("   AND bi.vstatus in (2,3) \n");
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
		
		if(!StringUtil.isEmpty(qvo.getVqrysql())){
			sql.append(qvo.getVqrysql());
		}
		
		sql.append(" order by bi.ts desc");
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;

	}
	
	@Override
	public String saveApply(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos, String stype, String type)
			throws DZFWarpException {

		String message = "";
		if (!StringUtil.isEmpty(vo.getPk_materielbill())) {
			MatOrderVO mvo = matcomm.queryById(vo.getPk_materielbill());
			if (mvo.getVstatus() != null && mvo.getVstatus() == 4) {// 已驳回的修改保存
				save(vo, uservo, bvos);
				return null;
			}
		}
		
		if (type != null && "1".equals(type)) {// 发货保存
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
			message = checkIsInfo(vo, bvos, message);
			if (message.isEmpty()) {
				save(vo, uservo, bvos);
			}
		}

		return message;
	}

	@SuppressWarnings("unchecked")
	private String checkIsInfo(MatOrderVO vo, MatOrderBVO[] bvos, String message) throws DZFWarpException {
		if (bvos != null && bvos.length > 0) {
			for (MatOrderBVO mbvo : bvos) {
				MaterielFileVO mvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class,
						mbvo.getPk_materiel());
				if (mvo != null && mvo.getIsappl() != null) {
					if (mvo.getIsappl() == 1) {// 勾选了申请条件
						Integer passNum = null;
						// 获取上季度提单审核通过数
						passNum = matcomm.queryContNum(vo, vo.getFathercorp());

						StringBuffer csql = new StringBuffer();
						SQLParameter cspm = new SQLParameter();
						cspm.addParam(vo.getFathercorp());
						cspm.addParam(mbvo.getPk_materiel());
						csql.append("  select b.vname,b.applynum,b.outnum,b.succnum \n");
						csql.append("      from cn_materielbill l  \n");
						csql.append("      left join cn_materielbill_b b on  \n");
						csql.append("      l.pk_materielbill = b.pk_materielbill \n");
						csql.append("      where nvl(l.dr,0) = 0 \n");
						csql.append("      and nvl(b.dr,0) = 0 \n");
						csql.append("      and l.fathercorp = ? \n");
						csql.append("      and b.pk_materiel = ? \n");

						if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
							csql.append(" and l.deliverdate >= ? ");
							cspm.addParam(vo.getDedubegdate());
						}
						if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
							csql.append(" and l.deliverdate <= ? ");
							cspm.addParam(vo.getDeduenddate());
						}
						List<MatOrderVO> mvoList = (List<MatOrderVO>) singleObjectBO.executeQuery(csql.toString(), cspm,
								new BeanListProcessor(MatOrderVO.class));

						Integer sumout = 0;// 上季度实发数量
						// Integer sumsucc = 0;//上季度申请通过数量

						if (mvoList != null && mvoList.size() > 0) {
							for (MatOrderVO ovo : mvoList) {
								if (ovo.getOutnum() == null) {
									ovo.setOutnum(0);
								}
								if (ovo.getSuccnum() == null) {
									ovo.setSuccnum(0);
								}
								sumout = sumout + ovo.getOutnum();
								// sumsucc = sumsucc + ovo.getSuccnum();
							}
						}
						Integer ssumout = (int) (0.7 * sumout);
						if (sumout == 0) {// 上季度没有发货
							// 可以申请保存
						} else {
							if (passNum != null && ssumout != null) {
								if (passNum >= ssumout) {
									// 可以申请保存
								} else {
									// 提示再申请保存
									mbvo.setSumapply(sumout);
									// mbvo.setSumsucc(sumsucc);
									message = message + "该加盟商" + mbvo.getVname() + "上季度申请数" + mbvo.getSumapply() + "，"
											+ "提单审核通过数" + passNum + "，" + "不符合该物料的申请条件，望知悉" + "<br/>";

								}
							}

						}
					} else {
						// 不需要校验
					}
				}
			}
		}

		return message;
	}


	/**
	 * 保存
	 * @param vo
	 * @param uservo
	 * @param bvos
	 * @param type
	 */
	private void save(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos) throws DZFWarpException {

			// 编辑保存
			MatOrderVO mvo = matcomm.queryById(vo.getPk_materielbill());
			if (mvo.getUpdatets() != null) {
				vo.setUpdatets(mvo.getUpdatets());
				vo.setVstatus(mvo.getVstatus());
			}
			saveEdit(vo, bvos, uservo);
	}
	
	private void saveEdit(MatOrderVO data, MatOrderBVO[] bvos, UserVO uservo) throws DZFWarpException {
		
		String uuid = UUID.randomUUID().toString();
		String msg = "";
		String mmsg = "";
		Integer sumnum = 0;
		try {
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid,
					60);
			if (!lockKey) {
				throw new BusinessException("合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>");
			}

			matcomm.checkData(data.getPk_materielbill(), data.getUpdatets());
				if (bvos != null && bvos.length > 0) {
					for (MatOrderBVO mbvo : bvos) {
						if (mbvo.getOutnum() == null) {
							mbvo.setOutnum(0);
						}
						msg = matcomm.checkIsApply(mbvo.getPk_materiel(), mbvo.getOutnum());
						mmsg = mmsg + msg;
						sumnum = sumnum + mbvo.getOutnum();
					}
					if (!StringUtil.isEmpty(mmsg)) {
						throw new BusinessException(mmsg);
					}
					if (sumnum == 0) {
						throw new BusinessException("总实发数量不可为0");
					}
				}
				String sql = "select pk_logistics,vname logname from cn_logistics " + "where pk_logistics = ? ";
				SQLParameter spm = new SQLParameter();
				spm.addParam(data.getLogname());
				MatOrderVO mvo = (MatOrderVO) singleObjectBO.executeQuery(sql, spm,
						new BeanProcessor(MatOrderVO.class));
				if (mvo != null) {
					data.setPk_logistics(mvo.getPk_logistics());
					data.setLogname(mvo.getLogname());
				}

				data.setVstatus(3);
				data.setDeliverid(uservo.getCuserid());
				data.setCitycounty(data.getPname() + "-" + data.getCityname() + "-" + data.getCountryname());
				String[] supdates = { "vstatus", "pk_logistics", "fastcode", "fastcost", "deliverid", "deliverdate",
						"vprovince", "vcity", "varea", "citycounty", "vaddress", "vreceiver", "phone" };

				singleObjectBO.update(data, supdates);

			// 2.修改子订单

			// 删除原有子订单
			SQLParameter oldspm = new SQLParameter();
			oldspm.addParam(data.getPk_materielbill());
			String oldsql = "delete from cn_materielbill_b where pk_materielbill = ? ";
			singleObjectBO.executeUpdate(oldsql, oldspm);

			// 增加修改后的子订单
			List<MatOrderBVO> bvolist = Arrays.asList(bvos);
			if (bvolist != null && bvolist.size() > 0) {
				for (MatOrderBVO bvo : bvolist) {
					bvo.setPk_materielbill(data.getPk_materielbill());
					singleObjectBO.insertVO("000001", bvo);

						// 修改物料档案发货数量
						if (!StringUtil.isEmpty(bvo.getPk_materiel())) {
							MaterielFileVO mfvo = (MaterielFileVO) singleObjectBO
									.queryByPrimaryKey(MaterielFileVO.class, bvo.getPk_materiel());
							if (mfvo != null && mfvo.getOutnum() != null) {
								mfvo.setOutnum(mfvo.getOutnum() + bvo.getOutnum());
							}
							String[] updates = { "outnum" };
							singleObjectBO.update(mfvo, updates);
						}

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

	
}
