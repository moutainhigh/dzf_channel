package com.dzf.service.pushappmsg.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ArrayListProcessor;
import com.dzf.model.app.JPMessageBean;
import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.message.MsgAdminVO;
import com.dzf.model.pub.INodeConstant;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.ISysConstants;
import com.dzf.pub.MsgtypeEnum;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.service.pushappmsg.IPushAppMessage;
import com.dzf.service.smsnotice.ISysMessageJPush;

@Service("pushappmessageimpl")
public class PushAppMessageImpl implements IPushAppMessage {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ISysMessageJPush sysMessagePush;

	@SuppressWarnings("unchecked")
	@Override
	public void saveConAuditMsg(ContractConfrimVO contvo, Integer sendtype, String loginpk, String userid)
			throws DZFWarpException {
		StringBuffer msg = new StringBuffer();
		CorpVO corpvo = CorpCache.getInstance().get(null, contvo.getPk_corpk());
		msg.append("客户");
		if (corpvo != null) {
			msg.append(corpvo.getUnitname());
		}
		msg.append("的合同");
		msg.append(contvo.getVcontcode());
		if (sendtype.equals(1)) {
			msg.append("已审批通过，请知悉。");
		} else if (sendtype.equals(2)) {
			msg.append("被驳回，请及时修改。");
		}
		String pk_corp = contvo.getPk_corp();
		String pk_corpk = contvo.getPk_corpk();
		String node = INodeConstant.CHANNEL_HTSH;
		String dealman = "";
		String sql = " SELECT submittid FROM ynt_contract WHERE nvl(dr,0) = 0 AND pk_contract = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(contvo.getPk_contract());
		ArrayList<Object> result = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm,
				new ArrayListProcessor());
		if (result != null && !result.isEmpty()) {
			for (int i = 0; i < result.size(); i++) {
				Object[] obj = (Object[]) result.get(i);
				dealman = String.valueOf(obj[0]);
			}
		}
		String sendman = userid;
		String pk_bill = contvo.getPk_contract();
		MsgtypeEnum type = null;
		if (sendtype.equals(1)) {
			type = MsgtypeEnum.MSG_TYPE_SPXX;
		} else if (sendtype.equals(2)) {
			type = MsgtypeEnum.MSG_TYPE_BHXX;
		}
		MsgAdminVO convertVO = convertVO(pk_corp, pk_corpk, type, dealman, sendman, null, msg.toString(), pk_bill,
				node);
		//存储消息数据
		singleObjectBO.saveObject(loginpk, convertVO);
		//激光推送消息
//		sendWyAppMsg(dealman, msg.toString());
	}

	/**
	 * 向小薇无忧APP推送消息
	 * 
	 * @param cuserid
	 * @param vcontent
	 */
	private void sendWyAppMsg(String cuserid, String vcontent) {
		JPMessageBean messageBean = new JPMessageBean();
		String[] userids = new String[] { cuserid };
		messageBean.setUserids(userids);
		messageBean.setMessage(vcontent);
		sysMessagePush.sendAdminMessage(messageBean);
	}

	/**
	 * 构建消息VO
	 * 
	 * @param pk_corp
	 *            会计公司主键
	 * @param pk_corpk
	 *            客户主键
	 * @param type
	 *            消息类型
	 * @param dealman
	 *            消息接收人
	 * @param sendman
	 *            消息发送人
	 * @param vtitle
	 *            消息标题
	 * @param msg
	 *            消息内容
	 * @param pk_bill
	 *            来源主键
	 * @param node
	 *            节点编码
	 * @return
	 */
	private MsgAdminVO convertVO(String pk_corp, String pk_corpk, MsgtypeEnum type, String dealman, String sendman,
			String vtitle, String msg, String pk_bill, String node) {
		MsgAdminVO msgvo = new MsgAdminVO();
		msgvo.setPk_corp(pk_corp);// 会计公司主键
		msgvo.setPk_corpk(pk_corpk);// 客户主键
		msgvo.setMsgtype(type.getValue());// 消息类型
		msgvo.setMsgtypename(type.getName());// 消息类型名称
		msgvo.setCuserid(dealman);// 消息接收人
		msgvo.setSendman(sendman);// 消息发送人
		msgvo.setVtitle(null);// 消息标题
		msgvo.setVcontent(msg);// 消息内容
		msgvo.setPk_bill(pk_bill);// 来源主键： 加盟商合同主键
		msgvo.setNodecode(node);// 节点编码
		msgvo.setSys_send(ISysConstants.DZF_CHANNEL);// 发送系统
		msgvo.setVsenddate(new DZFDateTime().toString());// 发送时间
		msgvo.setIsread(DZFBoolean.FALSE);// 是否已读
		msgvo.setDr(0);
		return msgvo;
	}

}
