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
	public List<PersonStatisVO> query(QryParamVO paramvo, UserVO uservo) throws DZFWarpException;

	public List<UserDetailVO> queryUserDetail(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 新增销售团队人数
	 * @param marketvo
	 */
	public void save(MarketTeamVO marketvo) throws DZFWarpException;

	/**
	 * 编辑回显
	 * @param id
	 * @return
	 */
	public MarketTeamVO queryDataById(String id) throws DZFWarpException;

}
