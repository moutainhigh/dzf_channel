package com.dzf.service.channel.chn_set;

import java.util.List;

import com.dzf.model.channel.sale.AccountSetVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IAccountSetService {
	
	/**
	 * 查询
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<AccountSetVO> query(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询客户
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<AccountSetVO> queryCorpk(String pk_corp,String corpkname) throws DZFWarpException;
	
	/**
	 * 保存
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void save(AccountSetVO vo) throws DZFWarpException;
	
	/**
	 * 修改
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void saveEdit(AccountSetVO vo) throws DZFWarpException;
	
	/**
	 * 启用禁用
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void updateStatus(AccountSetVO vo) throws DZFWarpException;
	
	/**
	 * 删除
	 * @param ids
	 * @throws DZFWarpException
	 */
	public void delete(String[] ids) throws DZFWarpException;

}
