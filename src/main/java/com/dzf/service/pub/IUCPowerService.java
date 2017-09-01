package com.dzf.service.pub;

import java.util.List;
import java.util.Set;

import com.dzf.dao.bs.DAOException;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

/**
 * 权限相关接口
 * 
 * @author dzf
 *
 */
public interface IUCPowerService {
	/**
	 * 查询有权限的会计公司与客户
	 * @param user
	 * @return
	 * @throws DAOException
	 */
	public List<CorpVO> queryPowerCorp(UserVO user) throws DZFWarpException;

	/**
	 * 查询有权限的会计公司与客户
	 * @param user
	 * @return
	 * @throws DAOException
	 */
	public Set<String> queryPowerCorpSet(UserVO user) throws DZFWarpException;
	
	/**
	 * 查询会计公司及所有分支机构
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public List<CorpVO> querySelfAndChildsCorp(String pk_corp) throws DZFWarpException;
	
	/**
	 * 查询会计公司及所有分支机构
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public Set<String> querySelfAndChildsCorpSet(String pk_corp) throws DZFWarpException;
	
	/**
	 * 查询用户所属公司
	 * @param user
	 * @return
	 * @throws BusinessException
	 */
	public Set<String> queryUserCorpVO(UserVO user) throws DZFWarpException;
	
	/**
	 * 获取用户所属公司信息
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpVO> queryUserCorp(UserVO uservo) throws DZFWarpException;
	
	/**
	 * 查询有权限的会计公司
	 * @param user
	 * @return
	 * @throws DAOException
	 */
	public List<CorpVO> queryPowerAccount(String pk_corp,UserVO uvo) throws DZFWarpException;

	/**
	 * 查询有权限的会计公司
	 * @param user
	 * @return
	 * @throws DAOException
	 */
	public Set<String> queryPowerAccountSet(String pk_corp,UserVO uvo) throws DZFWarpException;
}
