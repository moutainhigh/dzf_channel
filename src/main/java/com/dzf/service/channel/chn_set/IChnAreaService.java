package com.dzf.service.channel.chn_set;

import java.util.ArrayList;

import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.pub.DZFWarpException;

public interface IChnAreaService {
	
	/**
	 * 保存（新增和修改）主子表
	 * @param vo
	 * @throws DZFWarpException
	 */
	public ChnAreaVO save(ChnAreaVO vo) throws DZFWarpException;
	
	/**
	 * 删除 主子表
	 * @param pk
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public void delete(String pk,String pk_corp) throws DZFWarpException;
	
	/**
	 * 查询 一主一从
	 * @param pk
	 * @param user
	 * @return
	 * @throws DZFWarpException	
	 */
	public ChnAreaVO queryByPrimaryKey(String pk) throws DZFWarpException;
	
	
	/**
	 * 列表查询
	 * @param vo
	 * @param user
	 * @return
	 * @throws DZFWarpException
	 */
	public ChnAreaVO[] query(ChnAreaVO vo) throws DZFWarpException;
	
	/**
	 * 查询加盟商总经理
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String queryManager(String pk_corp) throws DZFWarpException;
	
	/**
	 * 查询没有使用过的省市
	 * @return
	 * @throws DZFWarpException
	 */
	public ArrayList queryComboxArea(String pk_area,String type) throws DZFWarpException;
	
}
