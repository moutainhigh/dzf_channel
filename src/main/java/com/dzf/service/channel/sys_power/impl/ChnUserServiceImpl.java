package com.dzf.service.channel.sys_power.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IRoleConstants;
import com.dzf.service.channel.sys_power.IChnUserService;

@Service("chnUserService")
public class ChnUserServiceImpl implements IChnUserService {
    
    @Autowired
    private SingleObjectBO singleObjectBO;
    
    private static final String UTYPE = "6";// 加盟商系统登录用户

    @Override
    public List<ComboBoxVO> queryCombobox(String loginCorpID) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp=new SQLParameter();
        sql.append("select u.cuserid as id,u.user_name as name from sm_user u");
        sql.append(" where u.pk_corp=? and nvl(u.dr,0) = 0 ");
        sql.append(" and nvl(u.locked_tag,'N')= 'N' ");
        sp.addParam(loginCorpID);
        sql.append(" and u.xsstyle = ?");
        sp.addParam(UTYPE);
        sql.append(" order by user_code asc");
        List<ComboBoxVO> listVo = (ArrayList<ComboBoxVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ComboBoxVO.class));
        List<ComboBoxVO> list = queryJituanUser(loginCorpID);
        listVo.addAll(list);
        return listVo;
    }

    /**
     * 查询集团创建的用户，需要登录加盟商系统
     * @author gejw
     * @time 上午10:22:45
     * @param loginCorpID
     * @return
     */
    private List<ComboBoxVO> queryJituanUser(String loginCorpID){
        StringBuffer sql = new StringBuffer();
        SQLParameter sp=new SQLParameter();
        sql.append(" select distinct us.cuserid as id,us.user_name as name from sm_user us");
        sql.append(" join sm_user_role ur on us.cuserid = ur.cuserid");
        sql.append(" join sm_role sr on ur.pk_role = sr.pk_role");
        sql.append(" where sr.roletype = ? and us.pk_corp = ? and us.xsstyle is null");
        sql.append(" and nvl(us.dr,0) = 0 and nvl(ur.dr,0) = 0 ");
        sql.append(" and nvl(us.locked_tag,'N')= 'N' ");
        sp.addParam(IRoleConstants.ROLE_7);
        sp.addParam(loginCorpID);
        sql.append(" order by user_code asc");
        List<ComboBoxVO> listVo = (ArrayList<ComboBoxVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(ComboBoxVO.class));
        return listVo;
    
    }
}
