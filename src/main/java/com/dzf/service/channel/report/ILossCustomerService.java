package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.LossCustomerVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface ILossCustomerService {

	/**
	 * 查询数据
	 * @param qvo
	 * @param userVO 
	 * @return
	 * @throws DZFWarpException
	 */
	List<LossCustomerVO> query(QryParamVO qvo, UserVO userVO) throws DZFWarpException;

}
