package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;

public interface IChannelService {
	
	/**
	 * 查询加盟商（渠道级别）
	 */
	List<CorpVO> querySmall(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 查询渠道商
	 * @param paramvo
	 * @return
	 */
	List<CorpVO> queryChannel(ChInvoiceVO paramvo);

}
