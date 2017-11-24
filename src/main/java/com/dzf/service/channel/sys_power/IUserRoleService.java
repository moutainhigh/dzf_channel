package com.dzf.service.channel.sys_power;

import java.util.List;

import com.dzf.model.channel.sys_power.URoleVO;
import com.dzf.pub.DZFWarpException;

public interface IUserRoleService {

    public List<URoleVO> queryUserRoleVO(String pk, String pk_corp) throws DZFWarpException;

    public void saveUserRoleVO(URoleVO[] vos, String userid, String pk_corp) throws DZFWarpException;

}