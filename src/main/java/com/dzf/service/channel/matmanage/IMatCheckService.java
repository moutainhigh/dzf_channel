package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.sys.sys_power.UserVO;

public interface IMatCheckService {

	List<ChnAreaBVO> queryComboBox(UserVO uservo);

	void updateStatusById(MatOrderVO vo, UserVO uservo);

	MatOrderVO queryById(String pk_materielbill);

}
