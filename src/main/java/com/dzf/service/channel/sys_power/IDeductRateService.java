package com.dzf.service.channel.sys_power;

import java.util.List;
import java.util.Map;

import com.dzf.model.channel.sys_power.DeductRateLogVO;
import com.dzf.model.channel.sys_power.DeductRateVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IDeductRateService {

	/**
	 * 查询数据行数
	 * 
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO pamvo) throws DZFWarpException;

	/**
	 * 查询数据
	 * 
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductRateVO> query(QryParamVO pamvo) throws DZFWarpException;

	/**
	 * 查询所有数据（根据加盟商名称或编码过滤）
	 * 
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductRateVO> queryAllData(QryParamVO pamvo) throws DZFWarpException;

	/**
	 * 保存或更新导入数据
	 * @param ratevo
	 * @param map
	 * @param fathercorp
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public DeductRateVO saveImport(DeductRateVO ratevo, Map<String, String> map, UserVO uservo) throws DZFWarpException;

	/**
	 * 查询加盟商信息
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> queryCorpMap() throws DZFWarpException;

	/**
	 * 通过字段及其值查询扣款率
	 * 
	 * @param field
	 * @param value
	 * @return
	 * @throws DZFWarpException
	 */
	public DeductRateVO queryDeductByField(String field, String value) throws DZFWarpException;
	
	/**
	 * 保存
	 * @param ratevo
	 * @param fathercorp
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public DeductRateVO save(DeductRateVO ratevo, String fathercorp, String cuserid) throws DZFWarpException;
	
	/**
	 * 查询变更记录
	 * @param fathercorp
	 * @param pk_deductrate
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductRateLogVO> queryLog(String fathercorp, String pk_deductrate) throws DZFWarpException;

}
