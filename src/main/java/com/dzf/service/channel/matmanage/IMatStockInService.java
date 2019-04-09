package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.model.sys.sys_power.UserVO;

public interface IMatStockInService {

	List<MaterielFileVO> queryComboBox();

	void saveStockIn(MaterielStockInVO data, UserVO uservo);

	int queryTotalRow(MaterielStockInVO pamvo);

	List<MaterielStockInVO> query(MaterielStockInVO pamvo, UserVO uservo);

	MaterielStockInVO queryDataById(String id);

	void delete(MaterielStockInVO pamvo);

}
