package com.dzf.service.channel.sys_power;

import java.util.List;

import com.dzf.model.channel.sys_power.DataPowerVO;
import com.dzf.model.channel.sys_power.URoleVO;
import com.dzf.pub.DZFWarpException;

public interface IDataPowerService {

    public DataPowerVO queryByID(String roleid) throws DZFWarpException;
    
    public List<URoleVO> queryRoles() throws DZFWarpException;

    public void save(DataPowerVO vo) throws DZFWarpException;

}