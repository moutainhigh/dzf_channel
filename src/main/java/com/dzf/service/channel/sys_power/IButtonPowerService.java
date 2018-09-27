package com.dzf.service.channel.sys_power;

import java.util.List;

import com.dzf.model.channel.sys_power.ButtonVO;
import com.dzf.model.channel.sys_power.RoleButtonVO;
import com.dzf.pub.DZFWarpException;

public interface IButtonPowerService {
	
	public List<ButtonVO> queryHead() throws DZFWarpException;
	
	public List<ButtonVO> query() throws DZFWarpException;
	
	public void save(RoleButtonVO[] vos) throws DZFWarpException;
}