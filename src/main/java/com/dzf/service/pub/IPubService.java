package com.dzf.service.pub;

import java.util.HashMap;
import java.util.Map;

import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.pub.DZFWarpException;

public interface IPubService {
	
	/**
	 * 区域查询
	 * @param parenter_id
	 * @return
	 * @throws DZFWarpException
	 */
	public HashMap<Integer, String> queryAreaMap(String parenter_id) throws DZFWarpException;

	/**
	 * 查询大区信息
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String,ChnAreaVO> queryLargeArea() throws DZFWarpException;
	
	/**
	 * 获取单号，年月日+4位流水号
	 * @param tablename
	 * @return
	 * @throws DZFWarpException
	 */
	public String queryCode(String tablename) throws DZFWarpException;
}
