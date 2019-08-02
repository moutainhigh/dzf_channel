package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.RenewAchieveVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IRenewAchieveService {
	
	/**
	 * 查询数据总条数
	 * @param qryvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotal(QrySqlSpmVO qryvo) throws  DZFWarpException;

	/**
	 * 查询数据
	 * @param pamvo
	 * @param uservo
	 * @param qryvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<RenewAchieveVO> query(QryParamVO pamvo, UserVO uservo, QrySqlSpmVO qryvo) throws  DZFWarpException;
	
	/**
	 * 拼装加盟商数量查询语句
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public QrySqlSpmVO getCorpQrySql(QryParamVO pamvo, UserVO uservo) throws DZFWarpException;
	
}
