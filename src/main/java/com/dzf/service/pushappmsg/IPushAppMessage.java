package com.dzf.service.pushappmsg;

import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.pub.DZFWarpException;

/**
 * 给app发送消息
 * @author zy
 *
 */
public interface IPushAppMessage {
	
	/**
	 * 加盟商合同审批/驳回发送消息
	 * @param contvo  合同vo
	 * @param sendtype  1:审核通过；2：驳回
	 * @param loginpk 登陆公司主键
	 * @param userid 操作人
	 * @throws DZFWarpException
	 */
	void saveConAuditMsg(ContractConfrimVO contvo, Integer sendtype, String loginpk, String userid) throws DZFWarpException;
}
