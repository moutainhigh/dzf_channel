package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.sys.sys_power.UserVO;

public interface IMatNumService {

	/**
	 * 查询数据条数
	 * @param pamvo
	 * @return
	 */
	int queryTotalRow(MaterielFileVO pamvo);

	/**
	 * 列表查询
	 * @param pamvo
	 * @param uservo
	 * @return
	 */
	List<MaterielFileVO> query(MaterielFileVO pamvo, UserVO uservo);

}
