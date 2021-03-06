package com.dzf.service.channel.rebate;

import java.util.List;

import com.dzf.model.channel.rebate.ManagerRefVO;
import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IRebateInputService {
	
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
	
	/**
	 * 通过加盟商主键、所属年、所属季度查询返点相关金额
	 * @param data
	 * @return
	 * @throws DZFWarpException
	 */
	public RebateVO queryDebateMny(RebateVO data) throws DZFWarpException;
	
	/**
	 * 提交
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	public RebateVO[] saveCommit(RebateVO[] bateVOs) throws DZFWarpException;
	
	/**
	 * 数据时间校验
	 * @param data
	 * @return
	 * @throws DZFWarpException
	 */
	public String checkData(RebateVO data) throws DZFWarpException;
	
	/**
	 * 通过主键查询返点单信息
	 * @param data
	 * @param opertype 1：修改；2：查看详情；
	 * @return
	 * @throws DZFWarpException
	 */
	public RebateVO queryById(RebateVO data, Integer opertype) throws DZFWarpException;
	
	/**
	 * 获取渠道经理\渠道运营查询条件
	 * @param cuserid
	 * @param qrytype  1：渠道经理；2：培训师；3：渠道运营；
	 * @return
	 * @throws DZFWarpException
	 */
	public String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException; 
}
