package com.dzf.service.channel.matmanage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.matmanage.IMatCommonService;
import com.dzf.service.channel.matmanage.IMatHandleService;
import com.dzf.service.pub.IPubService;
import com.dzf.service.sys.sys_power.IUserService;


@Service("mathandle")
public class MatHandleServiceImpl implements IMatHandleService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IPubService pubser;

	@Autowired
	private IUserService userser;
	
	@Autowired
	private IMatCommonService matcomm;
	

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
		
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo,pamvo,"1");
		List<MatOrderVO> list = (List<MatOrderVO>)singleObjectBO.executeQuery(sqpvo.getSql(),
				sqpvo.getSpm(),new BeanListProcessor(MatOrderVO.class));
		HashMap<String, UserVO> map = userser.queryUserMap(uservo.getPk_corp(), true);
		Map<String, UserVO> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		if(list!=null && list.size()>0){
			for (MatOrderVO mvo : list) {
				if (mvo.getCoperatorid() != null) {
					uservo = map.get(mvo.getCoperatorid());
					if(uservo!=null){
						mvo.setApplyname(uservo.getUser_name());
					}
				}
				uservo = marmap.get(mvo.getFathercorp());
				if (uservo != null) {
					mvo.setVmanagername(uservo.getUser_name());// 渠道经理
				}
			}
			QueryDeCodeUtils.decKeyUtils(new String[] { "unitname", "corpname" }, list, 1);
		}
		return list;
	}

	private QrySqlSpmVO getQrySqlSpm(QryParamVO qvo,MatOrderVO pamvo,
			String stype)  throws DZFWarpException {
		
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
		sql.append("                bi.coperatorid,  \n") ; 
		sql.append("                bi.applydate,  \n") ; 
		sql.append("                bi.fathercorp,  \n") ; 
		sql.append("                bi.corpname,  \n") ; 
		sql.append("                bi.vmanagerid,  \n") ; 
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
		if (!StringUtil.isEmpty(pamvo.getVmanagerid())) {
			sql.append(" AND  bi.vmanagerid = ? ");
			spm.addParam(pamvo.getVmanagerid());
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


}
