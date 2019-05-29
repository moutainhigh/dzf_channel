package com.dzf.service.branch.setup;

import java.util.List;

import com.dzf.model.channel.payment.ChnDetailRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

/**
 * 付款、扣款明细接口
 * @author gejw
 * @time 2018年8月6日 下午2:15:15
 *
 */
public interface IRecPayDetailService {
	
	/**
	 * 查询扣款明细数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ChnDetailRepVO> queryRecDetail(QryParamVO paramvo) throws DZFWarpException;
	
}
