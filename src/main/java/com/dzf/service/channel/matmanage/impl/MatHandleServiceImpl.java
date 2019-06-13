package com.dzf.service.channel.matmanage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.jm.CodeUtils1;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.matmanage.IMatHandleService;


@Service("mathandle")
public class MatHandleServiceImpl implements IMatHandleService {

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


	@Override
	public MatOrderVO[] saveImoprt(MatOrderVO[] vos) {
		
		String sql = "select vname from cn_materiel where nvl(dr,0) = 0 ";
		List<String> nameList = (List<String>) singleObjectBO.executeQuery(sql, null, new ColumnProcessor("vname"));
		
		Map<String,Integer> map = new HashMap<String,Integer>(); 
		Set<String> nameSet = map.keySet();
		for (MatOrderVO mvo : vos) {
			MatOrderBVO[] bvo = (MatOrderBVO[]) mvo.getChildren();
			mvo.setChildren(null);
			singleObjectBO.insertVO("000001", mvo);
			for (MatOrderBVO mbvo : bvo) {
				if(nameList.contains(mbvo.getVname())){
					mbvo.setPk_materielbill(mvo.getPk_materielbill());
					singleObjectBO.insertVO("000001", mbvo);
				     //导入的物料在物料档案表中加上物料发货数量
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

}
