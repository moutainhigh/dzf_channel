package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.PersonStatisVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IPersonStatis {
	/**
	 * 查询
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<PersonStatisVO> query(QryParamVO paramvo) throws DZFWarpException, IllegalAccessException, Exception;

}
