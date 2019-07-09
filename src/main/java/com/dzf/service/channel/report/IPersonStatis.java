package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.MarketTeamVO;
import com.dzf.model.channel.report.PersonStatisVO;
import com.dzf.model.channel.report.UserDetailVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IPersonStatis {
	/**
	 * 查询
	 * @param paramvo
	 * @param uservo 
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<PersonStatisVO> query(QryParamVO paramvo, UserVO uservo) throws DZFWarpException, IllegalAccessException, Exception;

	public List<UserDetailVO> queryUserDetail(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 新增销售团队人数
	 * @param marketvo
	 */
	public void save(MarketTeamVO marketvo);

	/**
	 * 编辑回显
	 * @param id
	 * @return
	 */
	public MarketTeamVO queryDataById(String id);

	/**
	 * 查询当前登录用户角色
	 * @param uservo
	 * @return
	 */
	public int queryQtype(UserVO uservo);


}
