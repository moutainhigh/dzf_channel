package com.dzf.service.channel.matmanage.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

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
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.SuperVO;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.matmanage.IMatApplyService;
import com.dzf.service.pub.IPubService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.sun.org.apache.bcel.internal.generic.INEG;
import com.sun.org.apache.xpath.internal.operations.And;
import com.sun.org.apache.xpath.internal.operations.Bool;

import oracle.net.aso.s;

@Service("matapply")
public class MatApplyServiceImpl implements IMatApplyService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private IPubService pubser;

	@Override
	public int queryTotalRow(MatOrderVO qvo,String stype) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo,stype);
		return multBodyObjectBO.queryDataTotal(MatOrderVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatOrderVO> query(MatOrderVO qvo, UserVO uservo,String stype) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo,stype);
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
					mvo.setVmanagerid(manager);
				}
			}
			String[] updates= {"vmanagerid"};
			singleObjectBO.update(mvo, updates);
		}
		QueryDeCodeUtils.decKeyUtils(new String[] { "unitname", "corpname" }, list, 1);
		return list;
	}

	private QrySqlSpmVO getQrySqlSpm(MatOrderVO pamvo, String stype) {
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
		sql.append("                bi.corpname,  \n") ; 
		/*sql.append("                bi.,  \n") ; 
		sql.append("                bi.,  \n") ; 
		sql.append("                bi.,  \n") ; 
		sql.append("                bi.,  \n") ; 
		sql.append("                bi.,  \n") ; */
		sql.append("                bi.ts,  \n") ; 
		sql.append("                b.vname,  \n") ; 
		sql.append("                b.vunit,  \n") ; 
		sql.append("                nvl(b.outnum, 0) outnum,  \n") ; 
		sql.append("                nvl(b.applynum, 0) applynum,  \n") ; 
		sql.append("                log.vname logname,  \n") ; 
		sql.append("                cb.vprovname proname,  \n") ; 
		sql.append("                cb.vprovince,  \n") ; 
		sql.append("                c.areaname \n") ; 
		//sql.append("                co.unitname corpname  \n") ; 
		sql.append("  from cn_materielbill bi  \n") ; 
		sql.append("  left join cn_materielbill_b b on bi.pk_materielbill = b.pk_materielbill  \n") ; 
		sql.append("  left join cn_logistics log on log.pk_logistics = bi.pk_logistics  \n") ; 
		sql.append("  left join cn_chnarea_b cb on cb.vprovince = bi.vprovince  \n") ; 
		sql.append("  left join cn_chnarea c on c.pk_chnarea = cb.pk_chnarea  \n") ; 
		//sql.append("  left join bd_account co on co.pk_corp = cb.pk_corp  \n") ; 
		sql.append("  where nvl(bi.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(b.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(log.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(cb.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(c.dr, 0) = 0  \n") ; 
		//sql.append("   and nvl(co.dr, 0) = 0  \n") ; 
		//sql.append("   and cb.pk_corp is not null  \n") ;
		
		if (!StringUtil.isEmpty(pamvo.getCorpname())) {
			sql.append(" AND  bi.corpname like ? ");
			spm.addParam("%" + pamvo.getCorpname() + "%");
		}
		if (!StringUtil.isEmpty(pamvo.getVmanagerid())) {
			sql.append(" AND  bi.vmanagerid = ? ");
			spm.addParam(pamvo.getVmanagerid());
		}
		if(stype!=null && "1".equals(stype)){//物料处理界面
			sql.append("   AND bi.vstatus in (2,3) \n");
		}
		else if (pamvo.getVstatus() != null && pamvo.getVstatus() != 0) {
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
		sql.append(" order by bi.ts desc");
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
			setCitycountry(mvo);
			String[] str = new String[] { "vreceiver", "phone" };
			QueryDeCodeUtils.decKeyUtil(str, mvo, 1);
			return mvo;
		}
		return null;
	}

	@SuppressWarnings("unused")
	@Override
	public String saveApply(MatOrderVO vo, UserVO uservo,
			MatOrderBVO[] bvos,String type,String stype) {
		
		String message = "";
		if(!StringUtil.isEmpty(vo.getPk_materielbill())){
			MatOrderVO mvo=queryById(vo.getPk_materielbill());
			if(mvo.getVstatus()!=null && mvo.getVstatus()==4){//已驳回的修改保存
				save(vo, uservo, bvos, type);
				return null;
			}
		}
		if("1".equals(type)){//发货保存
			save(vo, uservo, bvos, type);
			return null;
		}
		if("1".equals(stype)){//提示后保存
			save(vo, uservo, bvos, type);
			return null;
		}else{
			
			 //获取上个季度时间
			 try {
				String lastQuarter = getLastQuarter(vo.getDedubegdate(), vo.getDuduenddate());
				String[] quarter = lastQuarter.split(",");
				vo.setDedubegdate(quarter[0]);
				vo.setDuduenddate(quarter[1]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			 //校验
			 message = checkIsInfo(vo,bvos,message);
			if(message.isEmpty()){
				save(vo, uservo, bvos, type);
			}
		}
		
		return message;
  }
	
	
	private String checkIsInfo(MatOrderVO vo,MatOrderBVO[] bvos,String message){
		for (MatOrderBVO mbvo :bvos) {
			MaterielFileVO mvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class,mbvo.getPk_materiel());
			if(mvo!=null && mvo.getIsappl()!=null){
				if(mvo.getIsappl() == 1){//勾选了申请条件
					Integer passNum = null;
					StringBuffer sql = new StringBuffer();
					SQLParameter spm=new SQLParameter();
					spm.addParam(vo.getFathercorp());
					
					sql.append("  SELECT  COUNT  (CASE WHEN \n");
					sql.append("       c.vdeductstatus = 1 AND c.vdeductstatus = 9 \n");
					sql.append("       THEN 1 ELSE NULL \n");
					sql.append("       END) num1, \n");
					sql.append("       COUNT (CASE WHEN c.vdeductstatus = 10 \n");
					sql.append("       THEN 1 ELSE NULL \n");
					sql.append("       END) num2 \n");
					sql.append("       FROM cn_contract c \n");
					sql.append("       where nvl(c.dr,0) = 0  \n");
					sql.append("        and c.pk_corp = ? \n");
					
					if (!StringUtil.isEmptyWithTrim(vo.getDedubegdate())) {
						sql.append(" and c.deductdata >= ? ");
						spm.addParam(vo.getDedubegdate());
					}
					if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
						sql.append(" and c.deductdata <= ? ");
						spm.addParam(vo.getDeduenddate());
					}
					MatOrderVO mmvo = (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MatOrderVO.class));
				    
					if(mmvo!=null && mmvo.getNum1()!=null 
							 && mmvo.getNum2()!=null){
						 passNum = mmvo.getNum1()-mmvo.getNum2();
					}
					
					StringBuffer csql = new StringBuffer();
					SQLParameter cspm=new SQLParameter();
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
						sql.append(" and l.deliverdate >= ? ");
						spm.addParam(vo.getDedubegdate());
					}
					if (!StringUtil.isEmptyWithTrim(vo.getDeduenddate())) {
						sql.append(" and l.deliverdate <= ? ");
						spm.addParam(vo.getEnddate());
					}
					List<MatOrderVO> mvoList = (List<MatOrderVO>) singleObjectBO.executeQuery(csql.toString(), cspm, new BeanListProcessor(MatOrderVO.class));
					
					Integer sumapply = 0;//上季度申请数量
					Integer sumout = 0;//上季度实发数量
					Integer sumsucc = 0;//上季度申请通过数量
					for (MatOrderVO ovo : mvoList) {
						if(ovo.getApplynum()==null){
							ovo.setApplynum(0);
						}
						if(ovo.getOutnum()==null){
							ovo.setOutnum(0);
						}
						if(ovo.getSuccnum()==null){
							ovo.setSuccnum(0);
						}
						sumapply = sumapply + ovo.getApplynum();
						sumout = sumout + ovo.getOutnum();
						sumsucc = sumsucc + ovo.getSuccnum();
					}
					sumout= (int) (0.7*sumout);
					if(sumapply == 0){//上季度没有申请
						//可以申请保存
						//save(vo, uservo, bvos, type);
					}else{
						if(passNum >= sumout){
							//可以申请保存
							//save(vo, uservo, bvos, type);
						}else{
							//提示再申请保存
							mbvo.setSumapply(sumapply);
							mbvo.setSumsucc(sumsucc);
							message = message + "该加盟商"+mbvo.getVname()+
									"上季度申请数"+mbvo.getSumapply()+
									"提单审核通过数"+mbvo.getSumsucc()+
									"不符合该物料的申请条件，望知悉";

						}
						
					}
				}else{
					//不需要校验
					//save(vo, uservo, bvos, type);
				}
			}
		}
		return message;
	}
	
     @SuppressWarnings("unused")
	private void saveEdit(MatOrderVO data,MatOrderBVO[] bvos,String type,UserVO uservo) {
    	if(type==null){
    		checkIsOperOrder(data.getVstatus(),"只有待审批或已驳回状态的申请单支持修改！");
    	}
		String uuid = UUID.randomUUID().toString();
		String msg = "";
		String mmsg = "";
		Integer sumnum = 0;
		try{
			boolean lockKey = LockUtil.getInstance().addLockKey(data.getTableName(), data.getPk_materielbill(), uuid, 60);
			String message;
			if (!lockKey) {
				message = "合同编号：" + data.getVcontcode() + ",其他用户正在操作此数据;<br>";
				throw new BusinessException(message);
			}
			
			MatOrderVO checkData = checkData(data.getPk_materielbill(), data.getUpdatets());
			if(type!=null && "1".equals(type)){//发货
				if(bvos!=null && bvos.length>0){
					for (MatOrderBVO mbvo : bvos) {
						if(mbvo.getOutnum()==null){
							mbvo.setOutnum(0);
						}
						msg = checkIsApply(mbvo.getPk_materiel(),mbvo.getOutnum());
						mmsg = mmsg + msg;
						sumnum = sumnum + mbvo.getOutnum();
					}
					if(!StringUtil.isEmpty(mmsg)){
						throw new BusinessException(mmsg);
					}
					if(sumnum == 0){
						throw new BusinessException("总实发数量不可为0");
					}
				}
				String sql = "select pk_logistics,vname logname from cn_logistics "
		    			+ "where pk_logistics = ? ";
		    	SQLParameter spm=new SQLParameter();
		    	spm.addParam(data.getLogname());
		    	MatOrderVO mvo = (MatOrderVO) singleObjectBO.executeQuery(sql, spm, new BeanProcessor(MatOrderVO.class));
		    	if(mvo!=null){
		    		data.setPk_logistics(mvo.getPk_logistics());
		    		data.setLogname(mvo.getLogname());
		    	}
		    	
				data.setVstatus(3);
				data.setDeliverid(uservo.getCoperatorid());
		    	String[] supdates ={"vstatus","pk_logistics","fastcode","fastcost",
						"deliverid","deliverdate" };
		    	
		    	singleObjectBO.update(data, supdates);		
		    }else{
		    	//1.修改主订单
		    	if(data.getVstatus()==4){//已驳回的修改
		    		data.setVstatus(1);
		    		String[] updates = {"vcontcode", "fathercorp", "corpname",
							"vprovince","vcity","varea",
							"vaddress","vreceiver","phone","vmemo",
							"applydate","vstatus"};
		    		singleObjectBO.update(data, updates);
		    	}else{
		    		String[] updates = {"vcontcode", "fathercorp", "corpname",
							"vprovince","vcity","varea",
							"vaddress","vreceiver","phone","vmemo",
							"applydate"};
				    singleObjectBO.update(data, updates);
		    	}
				
		    }
		
		    //2.修改子订单
		    String sql="delete from cn_materielbill_b where pk_materielbill = ? ";
		    SQLParameter spm=new SQLParameter();
		    spm.addParam(data.getPk_materielbill());
		    singleObjectBO.executeUpdate(sql.toString(),spm);
		    
		    List<MatOrderBVO> bvolist = Arrays.asList(bvos);
		    if(bvolist!=null && bvolist.size()>0){
		    	  for (MatOrderBVO bvo : bvolist) {
				    	bvo.setPk_materielbill(data.getPk_materielbill());
				    	singleObjectBO.insertVO("000001", bvo);
				    	
				    	if(type!=null && "1".equals(type)){//发货
				    		//修改物料档案发货数量
					    	if(!StringUtil.isEmpty(bvo.getPk_materiel())){
					    		MaterielFileVO mfvo = (MaterielFileVO) singleObjectBO.queryByPrimaryKey(MaterielFileVO.class, bvo.getPk_materiel());
						    	if(mfvo!=null && mfvo.getOutnum()!=null){
						    		mfvo.setOutnum(mfvo.getOutnum()+bvo.getOutnum());
						    	}
						    	String[] updates = {"outnum"};
						    	singleObjectBO.update(mfvo, updates);
					    	}
				    	}
				    
					}
		    }
		}catch (Exception e) {
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
      * @param vo
      * @param uservo
      * @param bvos
      * @param type
      */
     private void save(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos,String type){
    	 
    	 if (StringUtil.isEmpty(vo.getPk_materielbill())) {// 新增保存
 			setDefaultValue(vo, uservo);
 			// 1.新增到订单主表VO
 			vo.setPk_corp("000001");
 			if(isInteger(vo.getPname())  &&
 					isInteger(vo.getCityname())  &&
 					isInteger(vo.getCountryname())){
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
 				bvo.setSuccnum(0);//审核通过数 默认为0
 				singleObjectBO.insertVO("000001", bvo);
 			}
 		} else {
 			// 编辑保存
 			MatOrderVO mvo=queryById(vo.getPk_materielbill());
 			if(mvo.getUpdatets()!=null){
 				vo.setUpdatets(mvo.getUpdatets());
 				vo.setVstatus(mvo.getVstatus());
 			}
 			 saveEdit(vo,bvos,type,uservo);
 		}
     }
      
     /**
 	 * 检查是否是最新数据
 	 * @param pk_materiel
 	 * @param updatets
 	 * @return
 	 */
 	private MatOrderVO checkData(String pk_materielbill, DZFDateTime updatets) {
 		MatOrderVO vo = (MatOrderVO) singleObjectBO.queryByPrimaryKey(MatOrderVO.class, pk_materielbill);
 		if (!updatets.equals(vo.getUpdatets())) {
 			throw new BusinessException("合同编号：" + vo.getVcontcode() + ",数据已发生变化;<br>");
 		}
 		return vo;
 	}
 	
 	/**
 	 * 判断是否能修改
 	 * @param istatus
 	 * @param msg
 	 * @throws DZFWarpException
 	 */
 	private void checkIsOperOrder(Integer istatus, String msg) throws DZFWarpException{
		if (istatus == null || istatus == 2 || istatus == 3) {
			throw new BusinessException(msg);
		} 
	}
 	
 	/**
 	 * 判断是否能发货
 	 * @param id
 	 * @param flag 
 	 * @param applynum
 	 */
 	private String checkIsApply(String id,Integer outnum){
 		String message = " ";
 		StringBuffer sql = new StringBuffer();
 		SQLParameter spm = new SQLParameter();
 		spm.addParam(id);
 		sql.append("select nvl(intnum - outnum,0) stocknum, \n");
 		sql.append("   nvl(intnum,0),nvl(outnum,0), \n");
 		sql.append("   vname \n");
 		sql.append("   from cn_materiel \n");
 		sql.append("   where nvl(dr,0) = 0 \n");
 		sql.append("   and pk_materiel = ? \n");
 		MaterielFileVO mmvo = (MaterielFileVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MaterielFileVO.class));
 	    if(mmvo!=null && mmvo.getStocknum()!=null){
 	    	if(outnum>0 && mmvo.getStocknum()==0){//库存为0
 	    		message = mmvo.getVname()+"已无货"+"<br/>";
 	    		return message;
 	    	}
 	    	if(mmvo.getStocknum()<outnum){//库存不足
 	    		message = mmvo.getVname()+"可发货数量为"+mmvo.getStocknum()+"<br/>";
 	    		return message;
 	    	}
 	    	
 	    }
 	    return "";
 	    
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
		data.setPk_corp("000001");
		data.setVstatus(data.VSTATUS_1);// 合同状态：默认为待审核
	}

	@Override
	public MatOrderVO queryDataById(String id,UserVO uservo,String type,String stype) {
		String message = "";
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(id);
		sql.append(  "select  m.pk_materielbill, \n ");
		sql.append(  "   m.vcontcode, \n");
		sql.append(  "   m.vstatus,m.vreason, \n");
		sql.append(  "   m.fathercorp,m.corpname, \n");
		sql.append(  "   m.vprovince,m.vcity,m.varea,m.citycounty, \n ");
		sql.append(  "   m.vaddress,m.vreceiver,m.phone,m.vmemo, \n ");
		sql.append(  "   m.coperatorid,m.applydate, \n");
		sql.append(  "   m.pk_logistics, \n");
		sql.append(  "   m.fastcode,m.fastcost, \n");
		sql.append(  "   m.deliverid,m.deliverdate, \n");
		sql.append(  "   lg.vname logname \n");
		
		sql.append("     from cn_materielbill m \n");
		sql.append("     left join cn_logistics lg on \n");
		sql.append("     lg.pk_logistics = m.pk_logistics \n");
		sql.append("     where nvl(m.dr,0) = 0 and nvl(lg.dr,0) = 0 \n");
		sql.append("     and m.pk_materielbill = ? \n");
		
		MatOrderVO vo= (MatOrderVO) singleObjectBO.executeQuery(sql.toString(), spm, new BeanProcessor(MatOrderVO.class));
		if(vo!=null){
				if("1".equals(type)){//发货
					if(vo.getDeliverid()!=null){
						uservo = UserCache.getInstance().get(vo.getDeliverid(), null);
						vo.setDename(uservo.getUser_name());
					}else{
						uservo = UserCache.getInstance().get(uservo.getCuserid(), null);
						vo.setDename(uservo.getUser_name());
					}
					uservo = UserCache.getInstance().get(vo.getCoperatorid(), null);
					vo.setApplyname(uservo.getUser_name());
				}else{//申请
					if (vo.getCoperatorid() != null) {
						uservo = UserCache.getInstance().get(vo.getCoperatorid(), null);
						vo.setApplyname(uservo.getUser_name());
					}
				}
			StringBuffer ssql = new StringBuffer();
			SQLParameter sspm = new SQLParameter();
			sspm.addParam(id);
			ssql.append("  select b.pk_materielbill_b, \n");
			ssql.append("    b.vname,b.vunit,b.applynum,b.outnum, \n");
			ssql.append("    l.pk_materiel \n");
			ssql.append("    from cn_materielbill_b b \n");
			ssql.append("    left join cn_materiel l on \n");
			ssql.append("    b.pk_materiel = l.pk_materiel \n");
			ssql.append("    where nvl(b.dr,0) = 0 \n");
			ssql.append("    and b.pk_materielbill = ? \n");
			List<MatOrderBVO> bvolist = (List<MatOrderBVO>) singleObjectBO.executeQuery(ssql.toString(), sspm, new BeanListProcessor(MatOrderBVO.class));
			MatOrderBVO[] b = new MatOrderBVO[bvolist.size()];
			MatOrderBVO[] bvos = (MatOrderBVO[]) bvolist.toArray(b);
			if(bvolist!=null && bvolist.size()>0){
				vo.setChildren(bvos);
			}
			
			setCitycountry(vo);
			
			if("1".equals(stype)){
				//点击审核校验
				message = checkIsInfo(vo, bvos, message);
				if(!message.isEmpty()){
					vo.setMessage(message);
				}
			}
			return vo;
		}
		return null;
	}
	

	private void setCitycountry(MatOrderVO vo){
		if (vo.getCitycounty() != null) {
			String[] citycountry = vo.getCitycounty().split("-");
			if (citycountry.length == 3) {
				vo.setPname(citycountry[0]);
				vo.setCityname(citycountry[1]);
				vo.setCountryname(citycountry[2]);
			} else if (citycountry.length == 2) {
				String str = "";
				if (citycountry[1] != null) {
					str = citycountry[1].substring(citycountry[1].length() - 1);
				}
				if ("区".equals(str)) {
					vo.setCityname("市辖区");
				} else if ("县".equals(str)) {
					vo.setCityname("县");
				} else if ("市".equals(str)) {
					vo.setCityname("市");
				}
				vo.setPname(citycountry[0]);
				vo.setCountryname(citycountry[1]);
			}

		}
	}

	private MatOrderVO queryById(String pk_materielbill) {
		
		StringBuffer str=new StringBuffer();
		SQLParameter spm=new SQLParameter();
		spm.addParam(pk_materielbill);
		str.append("  select pk_materielbill, \n");
		str.append("     vcontcode,vstatus,updatets \n");
		str.append("     from cn_materielbill  \n");
		str.append("     where nvl(dr,0) = 0 and \n");
		str.append("     pk_materielbill = ? \n");
		MatOrderVO vo = (MatOrderVO) singleObjectBO.executeQuery(str.toString(), spm, new BeanProcessor(MatOrderVO.class));
		
		if(vo!=null){
			return vo;
		}
		return null;
	}

	@Override
	public void delete(MatOrderVO qryvo) {
		
		MatOrderVO mvo=queryById(qryvo.getPk_materielbill());
		if(mvo.getUpdatets()!=null){
			qryvo.setUpdatets(mvo.getUpdatets());
			qryvo.setStatus(mvo.getStatus());
		}
		
		checkIsOperOrder(qryvo.getStatus(), "只有待审批或已驳回状态的申请单支持删除！");
		
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(qryvo.getTableName(), qryvo.getPk_materielbill(), uuid, 60);
			//1.删除主订单
			SQLParameter spm = new SQLParameter();
	    	spm.addParam(qryvo.getPk_materielbill());
			String sql = " DELETE FROM cn_materielbill WHERE pk_materielbill = ? ";
			singleObjectBO.executeUpdate(sql, spm);
			//2.删除子订单
			sql = "DELETE FROM cn_materielbill_b WHERE pk_materielbill = ?";
			singleObjectBO.executeUpdate(sql, spm);
		}catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(qryvo.getTableName(), qryvo.getPk_materielbill(), uuid);
		}
		
	}
	
	/**
	 * 获取上个季度时间
	 * 将中国标准时间转为2019-04-12日期类型
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws ParseException
	 */
	private String getLastQuarter(String startdate,String enddate) throws ParseException{
		startdate = startdate.replace("GMT", "").replaceAll("\\(.*\\)", "");
		enddate = enddate.replace("GMT", "").replaceAll("\\(.*\\)", "");
		//将字符串转化为date类型
		SimpleDateFormat format =  new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z",Locale.ENGLISH);
		Date sdate = format.parse(startdate);
		Date edate = format.parse(enddate);
		String start = new SimpleDateFormat("yyyy-MM-dd").format(sdate);
		String end = new SimpleDateFormat("yyyy-MM-dd").format(edate);
		return start+","+end;
	}
	
	/**
	 * 判断是否是数字
	 * @param str
	 * @return
	 */
	private static boolean isInteger(String str) {  
	        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
	        return pattern.matcher(str).matches();  
	}
	
	


}
