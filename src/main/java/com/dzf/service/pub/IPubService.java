package com.dzf.service.pub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IPubService {
	
	/**
	 * 根据用户获取负责的加盟商 sql<用于查询方法同一过滤>
	 * 
	 * @param cuserid
	 * @param areaname
	 * @param type(1:渠道；2：培训；3：运营)
	 * @return
	 * @throws DZFWarpException
	 */
	public String makeCondition(String cuserid, String areaname, int type) throws DZFWarpException;
	
	/**
	 * 获取用户获取所负责的加盟商主键<用于查询条件中，某一查询条件(渠道经理或运营经理)的过滤>
	 * 
	 * @param userids
	 * @param qrytype
	 *            1：渠道经理；2：培训师；3：渠道运营；
	 * @return
	 * @throws DZFWarpException
	 */
	public String[] getManagerCorp(String userids, Integer qrytype) throws DZFWarpException;

	/**
	 * 地区查询
	 * 
	 * @param parenter_id
	 * @return <地区编码,地区名称>
	 * @throws DZFWarpException
	 */
	public HashMap<Integer, String> queryAreaMap(String parenter_id) throws DZFWarpException;

	/**
	 * 查询大区信息
	 * 
	 * @return <区域编码,(区域名称，区域编码，大区经理)>
	 * @throws DZFWarpException
	 */
	public Map<String, ChnAreaVO> queryLargeArea() throws DZFWarpException;

	/**
	 * 获取各个省（直辖市）对应的加盟商
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<Integer, List<String>> getProviceCorp() throws DZFWarpException;

	/**
	 * 获取加盟商直接对应的 渠道经理/渠道运营(非省/市负责人)
	 * 
	 * @param pk_corp
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, UserVO> getManagerMap(Integer qrytype) throws DZFWarpException;

	/**
	 * 获取 省(直辖市)对应的区域名称<省(直辖市)，区域名称>
	 * 
	 * @param areaname
	 *            区域名称
	 * @param type
	 *            数据类型
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<Integer, String> getAreaMap(String areaname, Integer type) throws DZFWarpException;

	/**
	 * 获取 省(直辖市)-》大区信息（名称、编码、大区负责人） map
	 * 
	 * @param areaname
	 * @param type
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<Integer, ChnAreaVO> getChnMap(String areaname, Integer type) throws DZFWarpException;

	/**
	 * 通过省(直辖市)获取对应的区域名称
	 * 
	 * @param vprovince
	 * @param type
	 * @return
	 * @throws DZFWarpException
	 */
	public String getAreaName(Integer vprovince, Integer type) throws DZFWarpException;

	/**
	 * 获取当前登陆人，对应角色、最大数据权限
	 * 
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer getDataLevel(String cuserid) throws DZFWarpException;

	/**
	 * 获取省市负责人为cuserid的所有的省份
	 * 
	 * @param cuserid
	 * @param type
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> qryPros(String cuserid, Integer type) throws DZFWarpException;

	/**
	 * 以渠道经理所选的加盟商（加盟商客户维度）
	 * 
	 * @param cuserid
	 * @param areaname
	 * @param type(1:渠道；2：培训；3：运营)
	 * @return
	 */
	public List<String> qryCorpIds(String cuserid, String areaname, int type) throws DZFWarpException;

	/**
	 * 在线业务系统功能操作是校验是否有节点权限，防止攻击
	 * 
	 * @author gejw
	 * @time 下午2:22:25
	 * @param uvo
	 * @throws DZFWarpException
	 */
	public void checkFunnode(UserVO uvo, String funnode) throws DZFWarpException;

	/**
	 * 检验当前登陆人是否有导出权限
	 * 
	 * @param uvo
	 * @param funnode
	 * @param butname
	 * @throws DZFWarpException
	 */
	public void checkButton(UserVO uvo, String funnode, String btncode) throws DZFWarpException;

	/**
	 * 查询用户分配的角色编码
	 * 
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> queryRoleCode(String cuserid) throws DZFWarpException;

	/**
	 * 获取角色查询条件
	 * 
	 * @param cuserid
	 * @param datatype
	 * @return
	 * @throws DZFWarpException
	 */
	public String getPowerSql(String cuserid, Integer datatype) throws DZFWarpException;

	/**
	 * 查询加盟商对应的渠道经理 (如果有直接对应对的渠道经理，则取；否则，取省(直辖市)负责人)
	 * 
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String getManagerName(String pk_corp) throws DZFWarpException;

	/**
	 * 查询当前登录人的角色权限
	 * 
	 * @param cuserid
	 * @return 1：总经理；2：区总；3：渠道经理；-1：无权限；
	 * @param type(1:渠道；2：培训；3：运营)
	 * @throws DZFWarpException
	 */
	public Integer getAreaPower(String cuserid, Integer type) throws DZFWarpException;

	/**
	 * 查询当前登录人角色 1：渠道 2：培训
	 * 
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public int queryRole(String cuserid) throws DZFWarpException;
}
