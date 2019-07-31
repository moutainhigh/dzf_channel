package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.channel.ChInvoiceVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IChannelService {
	
	/**
	 * 查询加盟商（渠道级别）
	 */
	List<CorpVO> querySmall(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 查询渠道商(目前逻辑：过滤掉演示的)
	 * @param paramvo
	 * @return
	 */    
	List<CorpVO> queryChannel(ChInvoiceVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询渠道商（为多选参照使用）
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	List<CorpVO> qryMultiChannel(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 查询当前登录用户角色
	 * @param uservo
	 * @return
	 */
	int queryQtype(UserVO uservo);

}
