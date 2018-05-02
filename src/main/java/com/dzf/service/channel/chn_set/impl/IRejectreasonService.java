package com.dzf.service.channel.chn_set.impl;

import java.util.List;

import com.dzf.model.channel.sale.RejectreasonVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IRejectreasonService {

	/**
	 * 分页-总行数
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO paramvo, UserVO uservo) throws DZFWarpException;
	
	/**
	 * 分页-查询
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<RejectreasonVO> query(QryParamVO paramvo, UserVO uservo) throws DZFWarpException;
	
	/**
	 * 保存
	 * @param data
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public RejectreasonVO save(RejectreasonVO data, String pk_corp) throws DZFWarpException;
	
	/**
	 * 删除
	 * @param data
	 * @throws DZFWarpException
	 */
	public void delete(RejectreasonVO data) throws DZFWarpException;
	
	/**
	 * 通过主键查询数据信息
	 * @param data
	 * @return
	 * @throws DZFWarpException
	 */
	public RejectreasonVO queryById(RejectreasonVO data) throws DZFWarpException;
}
