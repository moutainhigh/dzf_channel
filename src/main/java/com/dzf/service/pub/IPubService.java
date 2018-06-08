package com.dzf.service.pub;

import java.util.HashMap;
import java.util.List;
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
	
	/**
	 * 获取各个省（直辖市）对应的加盟商
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<Integer,List<String>> getProviceCorp() throws DZFWarpException;
	
	/**
	 * 获取渠道经理所直接负责客户
	 * @param userids
	 * @return
	 * @throws DZFWarpException
	 */
	public String[] getManagerCorp(String userids) throws DZFWarpException;
	
	/**
	 * 通过加盟商获取渠道经理名称
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String getManagerName(String pk_corp) throws DZFWarpException;
	
	/**
	 * 获取加盟会计公司所对应的渠道经理
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> getManagerMap() throws DZFWarpException; 
	
	/**
	 * 获取  省市---》区域名称 
	 * @param areaname
	 * @param type
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<Integer, String> getAreaMap(String areaname,Integer type) throws DZFWarpException;
	
	/**
	 * 指定用户id,判断是否为3个顶级用户
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public boolean checkIsLeader(String cuserid) throws DZFWarpException;
	
	/**
	 * 指定用户id,构造    获取其本身负责的加盟商客户  sql表达式
	 * @param cuserid
	 * @param areaname
	 * @return
	 * @throws DZFWarpException
	 */
	public String makeCondition(String cuserid,String areaname) throws DZFWarpException;
	
}
