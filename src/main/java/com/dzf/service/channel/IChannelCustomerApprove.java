package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.sys.sys_power.CorpDocVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;

public interface IChannelCustomerApprove {
	public void processApprove(CorpVO[] corps, String user) throws DZFWarpException;

	public void processAbandonApprove(CorpVO[] corps) throws DZFWarpException;

	/**
	 * 查询渠道商
	 * 
	 * @param logincorp
	 *            登录公司
	 * @param location
	 *            地区
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpVO> queryChannelBusiness(String logincorp, String location)
			throws DZFWarpException;

	/**
	 * 查询渠道商客户
	 * 
	 * @param account_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpVO> queryChannelCustomer(String account_corp, String bdate,
			String edate, String status) throws DZFWarpException;
	/**
	 * 查询附件
	 * @param pk_doc
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpDocVO queryCorpDocByID(String pk_doc) throws DZFWarpException;
}
