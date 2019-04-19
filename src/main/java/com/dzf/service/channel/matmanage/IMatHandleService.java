package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.pub.DZFWarpException;

public interface IMatHandleService {

	/**
	 * 查询快递公司下拉
	 * @return
	 */
	List<MatOrderVO> queryComboBox() throws DZFWarpException;

}
