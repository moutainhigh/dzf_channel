package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.model.sys.sys_power.UserVO;

public interface IMatFileService {

	Boolean queryMatName(String name);

	void saveMatFile(MaterielFileVO data, UserVO uservo);

	int queryTotalRow(MaterielFileVO pamvo);

	List<MaterielFileVO> query(MaterielFileVO pamvo, UserVO uservo);

	void updateStatus(MaterielFileVO mvo, Integer type);

	List<MaterielFileVO> querySsealById(String ids);

	MaterielFileVO queryDataById(String id);

	List<MaterielStockInVO> queryIsRk(String ids);

	void deleteWl(MaterielFileVO mvo);

	List<MaterielFileVO> queryMatFile(MaterielFileVO pamvo, UserVO uservo);

	
}
