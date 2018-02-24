package com.dzf.service.channel.rebate;

import java.util.List;

import com.dzf.model.channel.rebate.ManagerRefVO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IRebateInptService {
	
	/**
	 * 查询
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<RebateVO> query(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 返点单录入保存
	 * @param data
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public RebateVO save(RebateVO data, String pk_corp) throws DZFWarpException;
	
	/**
	 * 查询渠道经理参照数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ManagerRefVO> queryManagerRef(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 删除
	 * @param data
	 * @throws DZFWarpException
	 */
	public void delete(RebateVO data) throws DZFWarpException;
}
