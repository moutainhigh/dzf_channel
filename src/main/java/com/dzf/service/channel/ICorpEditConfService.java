package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.channel.CorpNameEVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface ICorpEditConfService {
	
	/**
	 * 查询行数
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO paramvo, UserVO uservo) throws DZFWarpException;
	
	/**
	 * 查询
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpNameEVO> query(QryParamVO paramvo, UserVO uservo) throws DZFWarpException;
	
	/**
	 * 审核/拒绝审核
	 * @param paramvo
	 * @param uservo
	 * @param opertype
	 * @param vreason
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpNameEVO updateAudit(CorpNameEVO paramvo, UserVO uservo, int opertype, String vreason) throws DZFWarpException;
	
	/**
	 * 通过主键查询修改信息
	 * @param pk_corpnameedit
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpNameEVO queryByID(String pk_corpnameedit) throws DZFWarpException;

}
