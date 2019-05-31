package com.dzf.service.channel.matmanage.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.StringUtil;
import com.dzf.service.channel.matmanage.IMatNumService;

@Service("nummat")
public class MatNumServiceImpl implements IMatNumService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Override
	public int queryTotalRow(MaterielFileVO parmvo) {
		 QrySqlSpmVO sqpvo = getQrySqlSpm(parmvo);
	     return multBodyObjectBO.queryDataTotal(MaterielFileVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	
	private QrySqlSpmVO getQrySqlSpm(MaterielFileVO qvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		
		sql.append("  select \n");
		sql.append("   m1.vcode,m1.vname,m1.vunit, \n");
		sql.append("   nvl(sum(b.applynum ),0) waitnum, \n");
		sql.append("   nvl(m1.intnum,0) intnum ,nvl(m1.outnum,0) outnum, \n");
		sql.append("   (nvl(m1.intnum,0)-nvl(m1.outnum,0)) enapplynum \n");
		sql.append("   from cn_materiel m1  \n");
		sql.append("   left join cn_materielbill_b b on \n");
		sql.append("   m1.vname = b.vname  \n");
		sql.append("   left join cn_materielbill bl on \n");
		sql.append("   bl.pk_materielbill = b.pk_materielbill \n");
		sql.append("   where nvl(b.dr,0) =0 and \n");
		sql.append("   nvl(bl.dr,0) =0 and \n");
		sql.append("   nvl(m1.dr,0) =0 and \n");
		sql.append("   bl.vstatus = 2 \n");
		if(!StringUtil.isEmpty(qvo.getVname())){
			sql.append(" and m1.vname like ? \n");
			spm.addParam("%"+qvo.getVname()+"%");
		}
		sql.append("   group by m1.vcode,m1.vname,m1.vunit, \n");
		sql.append("   m1.intnum,m1.outnum,(m1.intnum-m1.outnum) \n");
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<MaterielFileVO> query(MaterielFileVO qvo, UserVO uservo) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
		List<MaterielFileVO> onelist = (List<MaterielFileVO>) singleObjectBO.executeQuery(sqpvo.getSql(),sqpvo.getSpm(), new BeanListProcessor(MaterielFileVO.class));
		List<MaterielFileVO> newList = new ArrayList<MaterielFileVO>();
		StringBuffer codes = new StringBuffer();
		if(onelist!=null && onelist.size()>0){
			for (MaterielFileVO vo : onelist) {
				codes.append(","+"'"+vo.getVcode()+"'");
			}
			codes = codes.deleteCharAt(0);
        	newList.addAll(onelist);
        }
		
		SQLParameter spm = new SQLParameter();
		String sql = "select vcode,vname,vunit,0 waitnum,"+
        "nvl(intnum,0) intnum,nvl(outnum,0) outnum,"+
        "(nvl(intnum,0) - nvl(outnum,0)) enapplynum"+
        " from cn_materiel"+
        " where nvl(dr,0) =0 ";
        if(!StringUtil.isEmpty(codes.toString())){
        	sql = sql + "and vcode not in ("+codes+")";
        }
		if(!StringUtil.isEmpty(qvo.getVname())){
			sql = sql +" and vname like ? \n";
			spm.addParam("%"+qvo.getVname()+"%");
		}
		List<MaterielFileVO> twolist = (List<MaterielFileVO>) singleObjectBO.executeQuery(sql, spm, new BeanListProcessor(MaterielFileVO.class));
		if(twolist!=null && twolist.size()>0){
			newList.addAll(twolist);
		}
		return newList;
	}

}
