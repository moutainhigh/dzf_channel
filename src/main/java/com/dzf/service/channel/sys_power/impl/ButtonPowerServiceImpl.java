package com.dzf.service.channel.sys_power.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.sys_power.ButtonVO;
import com.dzf.model.channel.sys_power.RoleButtonVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.service.channel.sys_power.IButtonPowerService;

@Service("powerButton")
public class ButtonPowerServiceImpl implements IButtonPowerService {

    @Autowired
    private SingleObjectBO singleObjectBO;
    
	@Override
	public List<ButtonVO> queryHead() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append(" select sr.pk_role, sr.role_name, sr.role_code \n");
		sql.append("   from sm_role sr \n");
		sql.append("  where sr.roletype = 8 \n");
		sql.append("    and sr.role_code != 'jms01' \n");
		sql.append("    and nvl(sr.dr, 0) = 0 \n");
		List<ButtonVO> list = (List<ButtonVO>) singleObjectBO.executeQuery(sql.toString(),null, new BeanListProcessor(ButtonVO.class));
		return list;
	}

    @Override
    public List<ButtonVO> query() throws DZFWarpException {
    	StringBuffer sql = new StringBuffer();
		sql.append(" select but.but_name, \n");
		sql.append("        but.js_id, \n");
		sql.append("        but.js_method, \n");
		sql.append("        but.pk_button, \n");
		sql.append("        fun.fun_name, \n");
		sql.append("        fun.pk_funnode, \n");
		sql.append("        r.role_code \n");
		sql.append("   from sm_button but \n");
		sql.append("   left join sm_funnode fun on but.pk_funnode = fun.pk_funnode \n");
		sql.append("   left join sm_role_button rbu on but.pk_button = rbu.pk_button \n");
		sql.append("   left join sm_role  r on rbu.pk_role=r.pk_role \n");
		sql.append(" where nvl(but.dr,0)=0 and nvl(but.isseal,'N')='N' \n");
		sql.append(" order by but_code  \n");
		List<ButtonVO> list = (List<ButtonVO>) singleObjectBO.executeQuery(sql.toString(),null, new BeanListProcessor(ButtonVO.class));
		ButtonVO getvo=new ButtonVO();
		List<ButtonVO> retlist =new ArrayList<>();
		for (ButtonVO buttonVO : list) {
			if(retlist==null || retlist.size()==0 || !retlist.contains(buttonVO)){
				buttonVO.setAttributeValue(buttonVO.getRole_code(), "Y");
				retlist.add(buttonVO);
			}else{
				getvo= retlist.get(retlist.indexOf(buttonVO));
				getvo.setAttributeValue(buttonVO.getRole_code(), "Y");
			}
		}
        return retlist;
    }

	@Override
	public void save(RoleButtonVO[] vos) throws DZFWarpException {
		singleObjectBO.executeUpdate("update sm_role_button set dr=1 ", null);
		singleObjectBO.insertVOArr("000001", vos);
//		SQLParameter spm = new SQLParameter();
//		for (RoleButtonVO roleButtonVO : vos) {
//			spm = new SQLParameter();
//			spm.addParam(roleButtonVO.getPk_role());
//			spm.addParam(roleButtonVO.getPk_button());
//			spm.addParam(roleButtonVO.getPk_funnode());
//			singleObjectBO.executeUpdate("insert into sm_role_button (pk_role,pk_button,pk_funnode) values(?,?,?)  ", spm);
//		}
	}
	
}