package com.dzf.service.pub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dzf.model.channel.sale.ChnAreaVO;
import com.dzf.model.sys.sys_power.UserVO;
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
//	public String getManagerName(String pk_corp) throws DZFWarpException;
	
	/**
	 * 获取加盟会计公司所对应的渠道经理
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> getManagerMap() throws DZFWarpException; 
	
	/**
	 * 获取 省(直辖市)对应的区域名称<省(直辖市)，区域名称> 
	 * @param areaname 区域名称
	 * @param type 数据类型
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<Integer, String> getAreaMap(String areaname,Integer type) throws DZFWarpException;
	
	/**
	 * 通过省(直辖市)获取对应的区域名称
	 * @param vprovince
	 * @param type
	 * @return
	 * @throws DZFWarpException
	 */
	public String getAreaName(Integer vprovince, Integer type) throws DZFWarpException;
	
	/**
	 * 指定用户id,构造    获取其本身负责的加盟商客户  sql表达式
	 * @param cuserid
	 * @param areaname
	 * @return
	 * @throws DZFWarpException
	 */
	public String makeCondition(String cuserid,String areaname) throws DZFWarpException;
	
	/**
	 * 获取当前登陆人，对应角色、最大数据权限
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer getDataLevel(String cuserid) throws DZFWarpException;
	
	
	/**
	 * 获取省市负责人为cuserid的所有的省份
	 * @param cuserid
	 * @param type
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> qryPros(String cuserid,Integer type) throws DZFWarpException;
	
	/**
     * 在线业务系统功能操作是校验是否有节点权限，防止攻击
     * @author gejw
     * @time 下午2:22:25
     * @param uvo
     * @throws DZFWarpException
     */
    public void checkFunnode(UserVO uvo,String funnode) throws DZFWarpException;
    
    /**
     * 检验当前登陆人是否有导出权限
     * @param uvo
     * @param funnode
     * @param butname
     * @throws DZFWarpException
     */
    public void checkButton(UserVO uvo,String funnode,String btncode) throws DZFWarpException;
}
