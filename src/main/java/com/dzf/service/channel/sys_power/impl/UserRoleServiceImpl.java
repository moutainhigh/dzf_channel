package com.dzf.service.channel.sys_power.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.sys_power.URoleVO;
import com.dzf.model.sys.sys_power.UserRoleVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.service.channel.sys_power.IUserRoleService;

@Service("userRoleServiceImpl")
public class UserRoleServiceImpl implements IUserRoleService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    // 只标准末级的状态即可
    private void setCheckStatus(List<URoleVO> list) {
        if (list != null && list.size() > 0) {
            for (URoleVO vo : list) {
                if (!StringUtil.isEmpty(vo.getPk_user_role())) {
                    vo.setChecked(Boolean.TRUE);
                } else {
                    vo.setChecked(Boolean.FALSE);
                }
            }

        }
    }

    @Override
    public List<URoleVO> queryUserRoleVO(String pk, String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(pk) || StringUtil.isEmpty(pk_corp))
            return null;
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk);
        StringBuffer sf = new StringBuffer();
        sf.append(" select sr.pk_role,sr.role_name,ur.pk_user_role from sm_role sr ");
        sf.append(" left join sm_user_role ur on sr.pk_role = ur.pk_role and ur.cuserid = ? and nvl(ur.dr,0) = 0");
        sf.append(" where sr.roletype = ? and sr.pk_corp = ? and nvl(sr.dr,0) = 0 and nvl(sr.seal,'N') = 'N'");
        sp.addParam(7);
        sp.addParam(pk_corp);
        List<URoleVO> list = (List<URoleVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(URoleVO.class));
        setCheckStatus(list);
        return list;
    }

    @Override
    public void saveUserRoleVO(URoleVO[] vos, String userid, String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(userid) || StringUtil.isEmpty(pk_corp))
            return;
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(userid);
        // 删除
        UserRoleVO[] vros = (UserRoleVO[]) singleObjectBO.queryByCondition(UserRoleVO.class,
                " pk_corp = ? and  cuserid = ? and nvl(dr,0) = 0 ", sp);
        singleObjectBO.deleteVOArray(vros);
//        String sql = "delete from sm_user_role where pk_corp = ? and  cuserid = ? and nvl(dr,0) = 0 ";
//        singleObjectBO.executeUpdate(sql, sp);
        // 新增
        if (vos != null && vos.length > 0) {
            ArrayList<UserRoleVO> list = new ArrayList<>();
            UserRoleVO urvo = null;
            for(URoleVO vo : vos){
                urvo = new UserRoleVO();
                urvo.setPk_corp(vo.getPk_corp());
                urvo.setPk_role(vo.getPk_role());
                urvo.setCuserid(vo.getCuserid());
                list.add(urvo);
            }
            singleObjectBO.insertVOArr(pk_corp, list.toArray(new UserRoleVO[0]));
        }
    }
}