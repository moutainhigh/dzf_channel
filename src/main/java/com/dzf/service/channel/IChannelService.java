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
	public List<CorpVO> querySmall(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 查询渠道商(目前逻辑：过滤掉演示的)
	 * @param paramvo
	 * @return
	 */    
	public List<CorpVO> queryChannel(ChInvoiceVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询渠道商（为多选参照使用）
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpVO> qryMultiChannel(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 查询当前登录用户角色
	 * @param uservo
	 * @return
	 */
	public Integer queryQtype(UserVO uservo) throws DZFWarpException;

}
